package gui;

import gui.configdialog.ConfigDialog;
import gui.configdialog.elements.CDialogLegend;
import gui.configdialog.elements.CDialogSector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import data.Akteur;
import data.EventProcessor;
import data.Netzwerk;
import data.Relation;
import data.RelationMonitor;
import events.ChangeDirectionEvent;
import events.ComplexEvent;
import events.DeleteActorEvent;
import events.MoveActorEvent;
import events.RemoveActorEvent;
import events.RemoveRelationEvent;
import events.RenameActorEvent;
import events.SetAttributeEvent;

/**
 * Factory to build up the different menus needed on a VennMakerView.
 * 
 * (for cleaning up the VennMakerViewMouseController)
 * 
 * 
 * 
 */
public class MouseMenuFactory
{
	private VennMakerView	view;

	public MouseMenuFactory(VennMakerView view)
	{
		this.view = view;
	}

	private JMenuItem createHideEgoItem(final boolean dft)
	{
		JMenuItem hideItem = new JMenuItem(
				Messages.getString("VennMaker.Ego_Hide")); //$NON-NLS-1$
		hideItem.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @seejava.awt.event. ActionListener #actionPerformed (java.
			 * awt.event.ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				VennMaker.getInstance().getConfig().setEgoDisabled(!dft);
				VennMaker.getInstance().setChangesUnsaved();
				view.repaint();
				Akteur ego = VennMaker.getInstance().getProject().getEgo();
				Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
						.getNetzwerke();

				if (!dft)
				{
					for (Netzwerk net : networks)
					{
						net.lockActor(ego);
					}
				}
				else
				{
					for (Netzwerk net : networks)
					{
						net.unlockActor(ego);
					}
				}
			}
		});
		return hideItem;
	}

	private JMenuItem createSearchItem()
	{
		JMenuItem showItem = new JMenuItem(
				Messages.getString("VennMakerViewMouseController.1")); //$NON-NLS-1$
		showItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				new ConfigDialog(CDialogSector.class);
			}
		});
		return showItem;
	}

	private JMenuItem createShowLegendItem(final boolean dft)
	{
		JMenuItem showItem = new JMenuItem(
				Messages.getString("VennMaker.Legend_Hide")); //$NON-NLS-1$
		showItem.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @seejava.awt.event. ActionListener #actionPerformed (java.
			 * awt.event.ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				view.drawLegend = dft;
				view.repaint();
			}
		});
		return showItem;
	}

	private JMenuItem createConfigureLegendItem()
	{
		JMenuItem showItem = new JMenuItem(
				Messages.getString("VennMaker.Legend_Config")); //$NON-NLS-1$
		showItem.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @seejava.awt.event. ActionListener #actionPerformed (java.
			 * awt.event.ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				new ConfigDialog(CDialogLegend.class);
			}
		});
		return showItem;
	}

	private JMenu createShowNetworkName()
	{
		JMenu networkNameSubmenu = new JMenu(
				Messages.getString("VennMaker.Show_NetworkName")); //$NON-NLS-1$
		JMenuItem hideNetworkName = new JMenuItem(
				Messages.getString("VennMaker.Hide_NetworkName")); //$NON-NLS-1$
		JMenuItem leftNetworkName = new JMenuItem(
				Messages.getString("VennMaker.left_NetworkName")); //$NON-NLS-1$
		JMenuItem centerNetworkName = new JMenuItem(
				Messages.getString("VennMaker.center_NetworkName")); //$NON-NLS-1$
		JMenuItem rightNetworkName = new JMenuItem(
				Messages.getString("VennMaker.right_NetworkName")); //$NON-NLS-1$

		hideNetworkName.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @see java.awt. event .ActionListener # actionPerformed (
			 * java.awt.event .ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				view.drawNetworkName = false;
				view.repaint();
			}
		});

		leftNetworkName.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @see java.awt. event .ActionListener # actionPerformed (
			 * java.awt.event .ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				view.drawNetworkName = true;
				view.drawNetworkNameOffset = -1;
				view.repaint();
			}
		});

		centerNetworkName.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc )
			 * 
			 * @see java. awt.event . ActionListener # actionPerformed ( java.awt.
			 * event .ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				view.drawNetworkName = true;
				view.drawNetworkNameOffset = 0;
				view.repaint();
			}
		});

		rightNetworkName.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @see java.awt. event .ActionListener # actionPerformed (
			 * java.awt.event .ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				view.drawNetworkName = true;
				view.drawNetworkNameOffset = 1;
				view.repaint();
			}
		});

		networkNameSubmenu.add(hideNetworkName);
		networkNameSubmenu.add(leftNetworkName);
		networkNameSubmenu.add(centerNetworkName);
		networkNameSubmenu.add(rightNetworkName);

		return networkNameSubmenu;
	}

	private JCheckBoxMenuItem createFixEgoItem()
	{
		final JCheckBoxMenuItem fixEgoItem = new JCheckBoxMenuItem(
				Messages.getString("VennMaker.Ego_Fixed"), !VennMaker.getInstance() //$NON-NLS-1$
						.getConfig().isEgoMoveable()); //$NON-NLS-1$
		fixEgoItem.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event .ActionListener #actionPerformed
			 * (java.awt.event. ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				VennMaker.getInstance().getConfig()
						.setEgoMoveable(!fixEgoItem.isSelected());
				VennMaker.getInstance().setChangesUnsaved();
			}
		});
		return fixEgoItem;
	}

	private JCheckBoxMenuItem createFilterItem(final Akteur actor)
	{
		final JCheckBoxMenuItem filterItem = new JCheckBoxMenuItem(
				Messages.getString("VennMakerViewMouseController.2"), //$NON-NLS-1$
				VennMaker.getInstance().getProject().getCurrentNetzwerk()
						.getFilter(actor));
		filterItem.addActionListener(new ActionListener()
		{ /*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event .ActionListener #actionPerformed
			 * (java.awt.event. ActionEvent )
			 */
			public void actionPerformed(ActionEvent e)
			{
				if (VennMaker.getInstance().getProject().getCurrentNetzwerk()
						.getFilter(actor) == false)
					VennMaker.getInstance().getProject().getCurrentNetzwerk()
							.setFilter(actor);
				else
					VennMaker.getInstance().getProject().getCurrentNetzwerk()
							.removeFilter(actor);
				view.repaint();
			}
		});
		return filterItem;
	}

	/**
	 * @param mousePressedActor
	 *           - actor the menu is based on
	 * @return fully build Actor Menu
	 */
	public JPopupMenu getActorMenu(final Akteur mousePressedActor)
	{
		JPopupMenu actorMenu = new JPopupMenu(mousePressedActor.getName());

		final JCheckBoxMenuItem filterActorItem = createFilterItem(mousePressedActor);

		final JMenuItem enlargeItem = new JMenuItem(
				Messages.getString("VennMaker.Enlarge")); //$NON-NLS-1$
		final JMenuItem shrinkItem = new JMenuItem(
				Messages.getString("VennMaker.Shrink")); //$NON-NLS-1$
		if (mousePressedActor == view.getNetzwerk().getEgo()
				&& VennMaker.getInstance().getConfig().isEgoResizable() == false)
		{
			enlargeItem.setEnabled(false);
			shrinkItem.setEnabled(false);
		}

		if (VennMaker.getInstance().getProject().getMainAttributeType("ACTOR") == null)
		{
			shrinkItem.setEnabled(false);
			enlargeItem.setEnabled(false);
		}
		else
		{
			if (VennMaker
					.getInstance()
					.getProject()
					.getMainAttributeType("ACTOR")
					.isFirst(
							mousePressedActor.getAttributeValue(
									VennMaker.getInstance().getProject()
											.getMainAttributeType("ACTOR"),
									view.getNetzwerk())))
				shrinkItem.setEnabled(false);
			if (VennMaker
					.getInstance()
					.getProject()
					.getMainAttributeType("ACTOR")
					.isLast(
							mousePressedActor.getAttributeValue(
									VennMaker.getInstance().getProject()
											.getMainAttributeType("ACTOR"),
									view.getNetzwerk())))
				enlargeItem.setEnabled(false);
		}

		enlargeItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ve)
			{
				mousewheelChangeAttributeValue(mousePressedActor, 1);
			}
		});
		shrinkItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ve)
			{
				mousewheelChangeAttributeValue(mousePressedActor, -1);
			}
		});

		final JMenuItem removeFromNetworkItem = new JMenuItem(
				Messages.getString("VennMaker.Removing")); //$NON-NLS-1$
		removeFromNetworkItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				EventProcessor.getInstance().fireEvent(
						new RemoveActorEvent(mousePressedActor, view.getNetzwerk(),
								mousePressedActor.getLocation(view.getNetzwerk())));
			}
		});

		final JMenuItem deleteItem = new JMenuItem(
				Messages.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		deleteItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Lösche Akteur: Erfolgt durch zusammengesetztes Event:
				// Alle Netzwerke müssen erst informiert werden.
				ComplexEvent ce = new ComplexEvent(Messages
						.getString("VennMaker.Removing_2")); //$NON-NLS-1$
				for (Netzwerk n : mousePressedActor.getNetzwerke())
					ce.addEvent(new RemoveActorEvent(mousePressedActor, n,
							mousePressedActor.getLocation(view.getNetzwerk())));
				ce.addEvent(new DeleteActorEvent(mousePressedActor));
				EventProcessor.getInstance().fireEvent(ce);
			}
		});

		final JMenuItem centerEgoItem = new JMenuItem(
				Messages.getString("VennMakerViewMouseController.3")); //$NON-NLS-1$
		centerEgoItem.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Wir wollen nur Ego zentrieren
				assert (mousePressedActor == view.getNetzwerk().getEgo());

				MoveActorEvent moveActorEvent = new MoveActorEvent(
						mousePressedActor, view.getNetzwerk(), new Point2D.Double(
								-mousePressedActor.getLocation(view.getNetzwerk())
										.getX(), -mousePressedActor.getLocation(
										view.getNetzwerk()).getY()));
				EventProcessor.getInstance().fireEvent(moveActorEvent);
			}
		});

		final JMenuItem addNameItem = new JMenuItem(
				Messages.getString("VennMaker.Rename_Actor")); //$NON-NLS-1$
		addNameItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ve)
			{

				// String result = JOptionPane.showInputDialog(null,
				// Messages
				//									.getString("VennMaker.Rename_Actor") + " " //$NON-NLS-1$ //$NON-NLS-2$
				// + mouseOnActor.getName(), mouseOnActor.getName());

				if (VennMaker.getInstance().getProject().isEncodeFlag())
				{
					new EditDataProtection();

					return;
				}

				Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
				String result = (String) JOptionPane.showInputDialog(
						VennMaker.getInstance(),
						Messages.getString("VennMaker.Rename_Actor"), Messages.getString("VennMaker.Rename_Actor"), JOptionPane.QUESTION_MESSAGE, icon, null, mousePressedActor.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

				/*
				 * String result = (String) TextFieldInput.showInputDialog(null,
				 * Messages.getString("VennMaker.Rename_Actor")+
				 * mouseOnActor.getName(), mouseOnActor.getName()).getText() ;
				 */

				if (result != null)
				{
					// mouseOnActor.setName(result);

					boolean isDup = false;
					for (Akteur a : VennMaker.getInstance().getProject()
							.getAkteure())
					{
						if ((a.getName().equals(result)) && (a != mousePressedActor))
						{
							isDup = true;
							break;
						}
					}
					if (isDup)
						switch (VennMaker.getInstance().getConfig()
								.getDuplicateBehaviour())
						{
							case NOT_ALLOWED:
								JOptionPane.showMessageDialog(null,
										Messages.getString("VennMaker.No_Duplicate")); //$NON-NLS-1$
								return;
							case ALLOWED:
								break;
							case ASK_USER:
								int val = JOptionPane.showConfirmDialog(null,
										Messages.getString("VennMaker.Already_Same"), //$NON-NLS-1$
										Messages.getString("VennMaker.Duplicate_Actor"), //$NON-NLS-1$
										JOptionPane.YES_NO_OPTION);

								if (val == JOptionPane.YES_OPTION)
								{
									break;
								}
								/*
								 * eigentlich ueberfluessige abfrage, aber sicher ist
								 * sicher
								 */
								else if (val == JOptionPane.NO_OPTION)
								{
									return;
								}
							default:
								assert (false);
						}

					// renaming allowed!
					// mouseOnActor.setName(result);
					// ComplexEvent event = new ComplexEvent(Messages
					//										.getString("VennMaker.Rename_Actor")); //$NON-NLS-1$
					RenameActorEvent actorEvent = new RenameActorEvent(
							mousePressedActor, result);
					/*
					 * event.addEvent(new NewActorEvent(mouseOnActor));
					 * event.addEvent(new AddActorEvent(mouseOnActor,
					 * mouseOnActor.getNetzwerke(), this.vmcs .toVMCS(new
					 * Point2D.Double(x2, y2))));
					 */
					EventProcessor.getInstance().fireEvent(actorEvent);
				}
			}
		});

		// -------------

		// final JMenu changeTypeSubMenu = new
		// JMenu("Change Actor Type");
		//
		// // lists all available relationtypes in a submenu
		// int i = 0;
		// for (AkteurTyp akteurTyp :
		// VennMaker.getInstance().getProject()
		// .getAkteurTypen())
		// {
		//
		// if (akteurTyp != mouseOnActor.getTyp())
		// {
		// JMenuItem changeTypeSubMenuItem = new JMenuItem(akteurTyp
		// .getBezeichnung());
		//
		//							changeTypeSubMenuItem.setActionCommand(i + ""); //$NON-NLS-1$
		// changeTypeSubMenuItem
		// .addActionListener(new ActionListener()
		// {
		// public void actionPerformed(ActionEvent e)
		// {
		// ChangeTypeOfActorEvent event = new ChangeTypeOfActorEvent(
		// mouseOnActor, VennMaker.getInstance()
		// .getProject().getAkteurTypen()
		// .get(
		// Integer.parseInt(e
		// .getActionCommand())));
		// EventProcessor.getInstance().fireEvent(event);
		// }
		// });
		//
		// changeTypeSubMenuItem.setName(Messages
		//									.getString("VennMaker.Index") //$NON-NLS-1$
		// + i);
		// changeTypeSubMenu.add(changeTypeSubMenuItem);
		// }
		// i++;
		// }

		final JMenuItem attributeItem = new JMenuItem(
				Messages.getString("VennMakerViewMouseController.0")); //$NON-NLS-1$
		attributeItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ve)
			{
				new EditAttributes(null, true, mousePressedActor, VennMaker
						.getInstance().getProject().getCurrentNetzwerk(), "ACTOR");
			}
		});

		// -------------

		actorMenu.add(filterActorItem);

		if (mousePressedActor == VennMaker.getInstance().getProject().getEgo())
		{
			final JCheckBoxMenuItem fixEgoItem = createFixEgoItem();
			final JMenuItem hideItem = createHideEgoItem(false);
			actorMenu.add(fixEgoItem);
			actorMenu.add(hideItem);
			if (mousePressedActor.getLocation(view.getNetzwerk()).getX() != 0.0
					|| mousePressedActor.getLocation(view.getNetzwerk()).getY() != 0.0)
				actorMenu.add(centerEgoItem);
			actorMenu.add(new JSeparator());
		}

		if (mousePressedActor != VennMaker.getInstance().getProject().getEgo())
		{
			actorMenu.add(removeFromNetworkItem);
			actorMenu.add(deleteItem);
			actorMenu.add(new JSeparator());
		}
		actorMenu.add(enlargeItem);
		actorMenu.add(shrinkItem);
		actorMenu.add(new JSeparator());
		actorMenu.add(addNameItem);
		// actorMenu.add(changeTypeSubMenu);
		actorMenu.add(attributeItem);

		return actorMenu;
	}

	/**
	 * @return fully build "EmptySpace"-Menu
	 */
	public JPopupMenu getEmptySpaceMenu()
	{
		JPopupMenu emptyMenu = new JPopupMenu(
				Messages.getString("Projekt.Netzwerk")); //$NON-NLS-1$
		final JCheckBoxMenuItem fixEgoItem = createFixEgoItem();
		final JMenuItem hideEgoItem = createHideEgoItem(VennMaker.getInstance()
				.getConfig().isEgoDisabled());
		final JMenuItem configLegendItem = createConfigureLegendItem();
		if (VennMaker.getInstance().getConfig().isEgoDisabled())
			hideEgoItem.setText(Messages.getString("VennMaker.Ego_Show")); //$NON-NLS-1$
		final JMenuItem showLegendItem = createShowLegendItem(!view.drawLegend);
		if (!view.drawLegend)
			showLegendItem.setText(Messages.getString("VennMaker.Legend_Show")); //$NON-NLS-1$

		final JMenuItem showSearchItem = createSearchItem();

		final JMenu showNetworkNameSubmenu = createShowNetworkName();

		emptyMenu.add(fixEgoItem);
		emptyMenu.add(hideEgoItem);
		emptyMenu.add(showLegendItem);
		emptyMenu.add(configLegendItem);
		emptyMenu.add(showNetworkNameSubmenu);
		emptyMenu.add(showSearchItem);

		return emptyMenu;
	}

	/**
	 * @param mouseOnRelation
	 *           - relation the menu is based on
	 * @return fully build Relation Menu
	 */
	public JPopupMenu getRelationMenu(final Relation mouseOnRelation)
	{
		JPopupMenu relationsMenu = new JPopupMenu(
				Messages.getString("VennMaker.Relation")); //$NON-NLS-1$
		final JMenuItem reverseRelationDirection = new JMenuItem(
				Messages.getString("VennMaker.Relation_Reverse")); //$NON-NLS-1$
		final JMenuItem deleteRelation = new JMenuItem(
				Messages.getString("VennMaker.Relation_Del")); //$NON-NLS-1$

		if (VennMaker.getInstance().getProject()
				.getIsDirected(mouseOnRelation.getAttributeCollectorValue()))
			reverseRelationDirection.setEnabled(true);
		else
			reverseRelationDirection.setEnabled(false);

		deleteRelation.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final Netzwerk curNetwork = view.getNetzwerk();
				EventProcessor.getInstance().fireEvent(
						new RemoveRelationEvent(RelationMonitor.getInstance()
								.getStartActor(mouseOnRelation), curNetwork,
								mouseOnRelation));
			}
		});

		reverseRelationDirection.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final Netzwerk curNetwork = view.getNetzwerk();
				final Akteur fromActor = RelationMonitor.getInstance()
						.getStartActor(mouseOnRelation);

				EventProcessor.getInstance().fireEvent(
						new ChangeDirectionEvent(fromActor, curNetwork,
								mouseOnRelation));
			}
		});

		final JMenuItem attributeItem = new JMenuItem(
				Messages.getString("VennMakerViewMouseController.0")); //$NON-NLS-1$
		attributeItem.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ve)
			{
				new EditAttributes(null, true, mouseOnRelation, VennMaker
						.getInstance().getProject().getCurrentNetzwerk(),
						mouseOnRelation.getAttributeCollectorValue());
			}
		});

		/* creates the final menu */
		relationsMenu.add(reverseRelationDirection);
		relationsMenu.add(deleteRelation);
		relationsMenu.add(attributeItem);

		return relationsMenu;
	}

	/**
	 * Mousewheel Trigger aendert den Attributwert eines Akteurs und ruft dazu
	 * passende Events auf.
	 * 
	 * @param actor
	 * @param f
	 */
	private void mousewheelChangeAttributeValue(Akteur actor, int f)
	{
		EventProcessor.getInstance().fireEvent(
				new SetAttributeEvent(actor, VennMaker.getInstance().getProject()
						.getMainAttributeType("ACTOR"), view.getNetzwerk(), f));
	}

}
