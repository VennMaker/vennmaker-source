package gui.sidemenu;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTaskPane;
import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.Akteur;
import data.EventProcessor;
import data.Netzwerk;
import data.Projekt;
import events.ComplexEvent;
import events.DeleteActorEvent;
import events.RemoveActorEvent;
import files.ImageOperations;
import gui.Messages;
import gui.ResizableImageIcon;
import gui.VennMaker;
import gui.VennMaker.ViewMode;
import gui.utilities.StringUtilities;

@SuppressWarnings("serial")
public class VennMakerSideMenuActorsPane extends SideMenuSubItem
{
	private Map<String, ResizableIcon>	iconCache	= new HashMap<String, ResizableIcon>();

	public VennMakerSideMenuActorsPane()
	{
		setSpecial(true);
		setLayout(new GridLayout(0, 1));

	}

	protected void build()
	{
		removeAll();
		ButtonGroup actorsButtonGroup = new ButtonGroup();

		int i = 0;
		int num = 0;

		Projekt project = VennMaker.getInstance().getProject();
		Vector<Akteur> akteure = project.getAkteure();
		Akteur ego = project.getEgo();
		for (final Akteur akteur : akteure)
		{
			if (!project.getCurrentNetzwerk().getAkteure().contains(akteur)
					&& ego != akteur)
			{
				ResizableIcon finalIcon = null;
				try
				{
					String filename = project.getCurrentNetzwerk()
							.getActorImageVisualizer()
							.getImage(akteur, project.getCurrentNetzwerk());

					if (iconCache.containsKey(filename))
					{
						finalIcon = iconCache.get(filename);
					}
					else
					{
						// check if svg or jpg/png
						String name = filename.toLowerCase();
						if (name.endsWith(".svg"))
						{
							finalIcon = SvgBatikResizableIcon
									.getSvgIcon(new FileInputStream(filename),
											new Dimension(32, 32));
						}
						else
						{
							if (name.equals(""))
								finalIcon = new ResizableImageIcon(new ImageIcon(
										ImageOperations.createActorIcon(null, 32, 1)));
							else
								finalIcon = new ResizableImageIcon(new ImageIcon(
										ImageOperations.loadActorImage(filename, 32, 1)));
						}
						iconCache.put(filename, finalIcon);
					}
				} catch (FileNotFoundException exn)
				{
					// exn.printStackTrace();
				}
				final JToggleButton jrb = new JToggleButton(
						StringUtilities.truncate(akteur.getName(), StringUtilities.MAX_TASK_PANE_LABEL_LENGTH), finalIcon);
				jrb.setHorizontalAlignment(SwingConstants.LEFT);
				actorsButtonGroup.add(jrb);
				jrb.setToolTipText(akteur.getName());
				jrb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						VennMaker v = VennMaker.getInstance();
						v.setCurrentActor(akteur);/* projekt.getAkteure().get(j) */
						v.setActualButton(ViewMode.ALTER_NODES);

						// Akteurtypbuttons deaktivieren
						// akteurTypButtonGroup.clearSelection();
						// FIXME: maybe does damage here
						// Relationtyp-Buttons deaktivieren
						// relationTypButtonGroup.clearSelection();
					}
				});

				/*
				 * mouselistener to remove an actor from the interview
				 */
				jrb.addMouseListener(new MouseAdapter()
				{
					/** create a Popup to delete the current actor from the project */
					private void showDeletePopup(int x, int y)
					{
						JPopupMenu popup = new JPopupMenu();
						popup.setInvoker(jrb);
						JMenuItem deleteActorItem = new JMenuItem(Messages
								.getString("VennMaker.Removing_2")); //$NON-NLS-1$
						popup.add(deleteActorItem);

						deleteActorItem.addActionListener(new ActionListener()
						{

							@Override
							public void actionPerformed(ActionEvent arg0)
							{
								ComplexEvent ce = new ComplexEvent(Messages
										.getString("VennMaker.Removing_2")); //$NON-NLS-1$
								for (Netzwerk n : akteur.getNetzwerke())
									ce.addEvent(new RemoveActorEvent(akteur, n, akteur
											.getLocation(n)));
								ce.addEvent(new DeleteActorEvent(akteur));
								EventProcessor.getInstance().fireEvent(ce);
							}

						});

						popup.setLocation(x, y);
						popup.setVisible(true);
					}

					@Override
					public void mousePressed(MouseEvent e)
					{
						/* show popupwindow when the button is rightclicked */
						if (e.isPopupTrigger())
						{
							showDeletePopup(e.getXOnScreen(), e.getYOnScreen());
						}
					}

					@Override
					/** codeclone for crossplatform compatibity 
					 * Linux: mousePressed
					 * Windows: mouseReleased
					 */
					public void mouseReleased(MouseEvent e)
					{
						/* show popupwindow when the button is rightclicked */
						if (e.isPopupTrigger())
						{
							showDeletePopup(e.getXOnScreen(), e.getYOnScreen());
						}
					}
				});

				add(jrb);
				if (i == 0)
					jrb.doClick();
				num++;
			}
			i++;
		}

		String actortitle = "";
		if (num > 0)
			actortitle = "" + num + " ";
		if (num == 1)
			actortitle += Messages.getString("VennMaker.Available_Actor");
		else
			actortitle += Messages.getString("VennMaker.Available_Actors");

		setTitle(actortitle); //$NON-NLS-1$

		// Alle Akteure gesetzt?
		if (num == 0)
		{
			// Akteurstypen ausklappen
			// actorTypesPane.setCollapsed(false);

			setVisible(false);
		}
		else
		{
			// actorTypesPane.setCollapsed(true);
			setVisible(true);
		}
		setVisible(true);
		
	}

}
