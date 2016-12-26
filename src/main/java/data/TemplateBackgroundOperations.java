package data;

import events.LoadTemplateEvent;
import files.IO;
import gui.Messages;
import gui.VennMaker;
import gui.WaitDialog;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogLayer;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.SaveElementWrapper;
import gui.configdialog.save.NetworkSaveElement;
import gui.configdialog.save.ProjectSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.save.SaveElementConverter;
import gui.configdialog.settings.DeleteNetworkSetting;
import gui.configdialog.settings.SettingCloneVirtualNetwork;
import gui.configdialog.settings.SettingNewNetwork;
import gui.configdialog.settings.SettingStartNewProject;
import interview.InterviewLayer;

import java.awt.Window;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * An Object of this class loads or saves template files (*.vmt) in background
 * while showing an wait dialog the user
 * 
 * 
 * 
 */
public class TemplateBackgroundOperations extends Thread
{
	private File			templateFile;

	private WaitDialog	waitDialog;

	private boolean		withEvents;

	private XStream		xstream;

	private Window			parent;

	public enum TemplateOperation
	{
		SAVE, LOAD
	};

	public enum OperationStatus
	{
		SAVE_SUCESSFULL, SAVE_FAILED, LOAD_SUCESSFULL, LOAD_FAILED
	};

	TemplateOperation	op;

	/**
	 * Constructs a new object of this class
	 * 
	 * @param templateFile
	 *           file to save or load
	 * @param parent
	 *           parent component to show notification dialogs
	 * @param withEvents
	 *           <code>true</code> if <code>LoadTemplateEvent</code> should be
	 *           fired (used for loading a template an immediately starting the
	 *           <code>InterviewController</code>
	 * @param op
	 *           <code>TemplateOperation</code> for this object.
	 *           <code>SAVE</code> saving a template or <code>LOAD</code> if a
	 *           template should be loaded
	 */
	public TemplateBackgroundOperations(File templateFile, Window parent,
			boolean withEvents, TemplateOperation op)
	{
		this.templateFile = templateFile;
		this.op = op;
		this.withEvents = withEvents;
		initXStream();
		this.parent = parent;

	}

	public void startAction()
	{
		waitDialog = new WaitDialog(this.parent, false);

		start();

		waitDialog.setVisible(true);
	}

	public void run()
	{
		if (this.op == TemplateOperation.LOAD)
		{
			if (load() == OperationStatus.LOAD_SUCESSFULL && !withEvents)
				JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
						"Template loaded sucessfully");
			else if (!withEvents)
				JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
						"Template failed to load");
		}
		else
		{
			if (save() == OperationStatus.SAVE_SUCESSFULL)
			{
				JOptionPane
						.showMessageDialog(
								ConfigDialog.getInstance(),
								Messages.getString("ConfigSaveListener.Saved"), Messages.getString("ConfigSaveListener.Saved-Title"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			}
			else
			{
				JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
						"Saving the file was not successfull");
			}
		}

	}

	/**
	 * Method for loading a template
	 * 
	 * @return <code>OperationStatus.LOAD_SUCESSFULL</code> if loading was
	 *         sucessfull, otherwise <code>OperationStatus.LOAD__FAILED</code>
	 *         will be returned
	 */
	private OperationStatus load()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{

				@Override
				public void run()
				{
					InterviewLayer.getInstance().reset();
				}
			});

			/*
			 * delete old attribute types, to provide only the ones from the
			 * template
			 */

			VennMaker.getInstance().getProject().getAttributeTypes().clear();

			ConfigDialogTempCache.getInstance().addSetting(
					new SettingStartNewProject());

			try
			{
				FileInputStream fis = new FileInputStream(templateFile);

				SaveElementWrapper wrapper = null;

				try
				{
					wrapper = (SaveElementWrapper) xstream.fromXML(fis);
				} catch (XStreamException xse)
				{
					// waitDialog.dispose();
					waitDialog.setVisible(false);
					return OperationStatus.LOAD_FAILED;
				}

				/* reset active configCache */
				final ConfigDialogTempCache cache = ConfigDialogTempCache
						.getInstance();
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						cache.clearActiveElementsList();	
					}
				});

				List<NetworkSaveElement> networkSaveElements = wrapper
						.getNetworkSaveElements();
				ProjectSaveElement projectSaveElement = wrapper
						.getProjectSaveElement();

				/*
				 * Add all networks to the new created project, which are saved in
				 * the template
				 */

				final Vector<Netzwerk> newNetworks = new Vector<Netzwerk>();
				for (final NetworkSaveElement nse : networkSaveElements)
				{
					newNetworks.add(nse.getNetwork());
					SwingUtilities.invokeAndWait(new Runnable()
					{
						@Override
						public void run()
						{
							ConfigDialogTempCache.getInstance().addSetting(
									new SettingNewNetwork(nse.getNetwork().getBezeichnung()));
						}
					});
				}

				Vector<Netzwerk> oldNetworks = VennMaker.getInstance().getProject()
						.getNetzwerke();

				Netzwerk[] oldNetworksArray = new Netzwerk[oldNetworks.size()];
				oldNetworksArray = oldNetworks.toArray(oldNetworksArray);

				for (final Netzwerk net : oldNetworksArray) {
					SwingUtilities.invokeAndWait(new Runnable()
					{
						@Override
						public void run()
						{
								if (!newNetworks.contains(net))
									ConfigDialogTempCache.getInstance().addSetting(
											new DeleteNetworkSetting(net, false));
							
						}
					});
				}

				List<SaveElement> elements = projectSaveElement
						.getProjectElements();

				/**
				 * Set Attributes first.
				 */
				for (int i = 0; i < elements.size(); i++)
				{
					if (elements.get(i).getElementName()
							.equals("CDialogEditAttributeTypes")
							|| elements.get(i).getElementName()
									.equals("CDialogEditRelationalAttributeTypes"))
					{
						SaveElement elem = elements.get(i);
						elements.remove(elem);

						ConfigDialogElement attributeElement = ConfigDialogLayer
								.getInstance().getActivatedProjectElement(
										elem.getElementName());

						if (attributeElement != null)
							attributeElement.setAttributesFromSetting(elem);
					}
				}

				/* create Networks to work with */
				for (final NetworkSaveElement nse : networkSaveElements)
				{
					try
					{
						SwingUtilities.invokeAndWait(new Runnable()
						{
							@Override
							public void run()
							{
								String bezeichner = nse.getNetwork().getBezeichnung();
								Netzwerk clonedNetwork = nse.getNetwork()
										.cloneVirtualNetwork(bezeichner); //$NON-NLS-1$
								cache.addSetting(new SettingCloneVirtualNetwork(
										clonedNetwork));

								nse.setNetwork(clonedNetwork);
							}
						});
					} catch (InvocationTargetException e)
					{
						e.printStackTrace();
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						ConfigDialog.getInstance().updateTree();		
					}
				});
				

				for (SaveElement elem : projectSaveElement.getProjectElements())
				{
					if (elem == null)
						continue;

					ConfigDialogElement dialogElement = ConfigDialogLayer
							.getInstance().getActivatedProjectElement(
									elem.getElementName());

					if (dialogElement == null)
						continue;

					dialogElement.setAttributesFromSetting(elem);
				}

				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						ConfigDialog.getInstance().updateTree();		
					}
				});

				SaveElementConverter saveElementConverter = new SaveElementConverter();
				for (NetworkSaveElement nse : networkSaveElements)
				{
					Map<String, ConfigDialogElement> activeElements = ConfigDialogLayer
							.getInstance().getAllActiveNetworkElements(
									nse.getNetwork().getBezeichnung());

					for (ConfigDialogElement element : activeElements.values())
						element.setNet(nse.getNetwork());

					for (int i = 0; i < nse.getNetworkSaveElements().size(); i++)
					{
						SaveElement elem = nse.getSaveElementAt(i);

						if (elem == null)
							continue;

						String newName = saveElementConverter.convert(elem
								.getElementName());

						ConfigDialogElement dialogElement = activeElements
								.get(newName);

						if (dialogElement == null)
							continue;

						dialogElement.setAttributesFromSetting(elem);
					}
				}

				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						ConfigDialog.getInstance().updateTree();		
					}
				});

				ConfigDialog.getInstance().setTitle(
						Messages.getString("ConfigDialog.0") + " " //$NON-NLS-1$ //$NON-NLS-2$
								+ templateFile.toString()); //$NON-NLS-1$

				// EventProcessor.getInstance().fireEvent(new LoadTemplateEvent());
			} catch (IOException ioe)
			{
				// waitDialog.dispose();
				waitDialog.setVisible(false);
				ioe.printStackTrace();

				return OperationStatus.LOAD_FAILED;
			}

			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{
					@Override
					public void run()
					{
						// waitDialog.dispose();
						waitDialog.setVisible(false);
					}
				});
			} catch (InvocationTargetException e)
			{
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			if (withEvents)
			{
				VennMaker.getInstance().registerEventListeners();
				ConfigDialogTempCache.getInstance().setAllSettings();
				EventProcessor.getInstance().fireEvent(new LoadTemplateEvent());
			}
		} catch (InvocationTargetException e1)
		{
			e1.printStackTrace();
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}

		return OperationStatus.LOAD_SUCESSFULL;
	}

	/**
	 * Method for saving a template
	 * 
	 * @return <code>OperationStatus.SAVE_SUCESSFULL</code> if saving was
	 *         sucessfull, otherwise <code>OperationStatus.SAVE_FAILED</code>
	 *         will be returned
	 */
	private OperationStatus save()
	{
		Map<Netzwerk, List<Akteur>> networkActors = new HashMap<Netzwerk, List<Akteur>>();

		List<ConfigDialogElement> activeElements = ConfigDialogLayer
				.getInstance().getAllActiviatedElements();

		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();
		Vector<NetworkSaveElement> networkSaveElements = new Vector<NetworkSaveElement>();

		final ProjectSaveElement projectSaveElement = new ProjectSaveElement();

		/* set all current changes before saving template */
		// ConfigDialogTempCache.getInstance().setAllSettings();

		for (Netzwerk n : networks)
		{
			networkSaveElements.add(new NetworkSaveElement(n));
			List<Akteur> actors = new ArrayList<Akteur>(n.getAkteure());

			networkActors.put(n, actors);

			for (Akteur act : actors)
				n.removeAkteur(act);
		}

		for (NetworkSaveElement nse : networkSaveElements)
		{
			for (final ConfigDialogElement elem : activeElements)
			{
				try
				{
					SwingUtilities.invokeAndWait(new Runnable()
					{

						@Override
						public void run()
						{
							elem.refreshBeforeGetDialog();
						}
					});
				} catch (InvocationTargetException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (nse.getNetwork() == elem.getNet())
					nse.addSaveElement(elem.getSaveElement());
			}
		}

		for (final ConfigDialogElement elem : activeElements)
		{
			try
			{
				SwingUtilities.invokeAndWait(new Runnable()
				{

					@Override
					public void run()
					{
						elem.refreshBeforeGetDialog();
						if (elem.getNet() == null)
							projectSaveElement.addProjectElement(elem.getSaveElement());
					}
				});
			} catch (InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		SaveElementWrapper wrapper = new SaveElementWrapper(networkSaveElements,
				projectSaveElement);

		String filename = templateFile.toString();

		if (!filename.endsWith(IO.TEMPLATE_SUFFIX))
			filename += IO.TEMPLATE_SUFFIX;

		try
		{
			FileOutputStream fos = new FileOutputStream(new File(filename));
			PrintWriter fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
					true);
			fw.write(xstream.toXML(wrapper));
			fw.close();
		} catch (IOException ioe)
		{

			ioe.printStackTrace();

			return OperationStatus.SAVE_FAILED;
		}

		for (Netzwerk net : networks)
		{
			List<Akteur> nwActors = networkActors.get(net);

			for (Akteur act : nwActors)
				net.addAkteur(act);
		}

		ConfigDialog.getInstance().setTitle(
				Messages.getString("ConfigDialog.0") + " " //$NON-NLS-1$ //$NON-NLS-2$
						+ templateFile.toString());

		ConfigDialog.getInstance().updateTree();

		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{

				@Override
				public void run()
				{
					// waitDialog.dispose();
					waitDialog.setVisible(false);
				}
			});
		} catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return OperationStatus.SAVE_SUCESSFULL;
	}

	private void initXStream()
	{
		xstream = new XStream();
		xstream = new XStream();
		xstream.alias("netzwerk", Netzwerk.class); //$NON-NLS-1$
		xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
	}
}
