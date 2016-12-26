package gui.sidemenu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.EventPerformedListener;
import data.EventProcessor;
import events.VennMakerEvent;
import files.FileOperations;
import gui.Messages;
import gui.VennMaker;

/**
 * 
 * 
 * 
 */
public class VennMakerSideMenu implements MouseMotionListener
{

	final private JXTaskPaneContainer				sideMenu;

	final private JXTaskPane							actionPane;

	final private JButton								undoButton;

	final private JButton								redoButton;

	final private VennMakerSideMenuActorTypePane	actorTypePane;

	final private VennMakerSideMenuActorsPane		actorsPane;

	final private VennMakerSideMenuRelationPanes	relationPane;

	final private ArrayList<SideMenuSubItem>		subitems;

	public VennMakerSideMenu()
	{
		subitems = new ArrayList<>();
		sideMenu = new JXTaskPaneContainer();
		sideMenu.setMaximumSize(new Dimension(100, 50));
		sideMenu.setMinimumSize(new Dimension(100, 50));

		sideMenu.setScrollableTracksViewportHeight(false);
		sideMenu.setScrollableTracksViewportWidth(false);
		// -- Create ActionPane
		actionPane = new JXTaskPane();

		undoButton = new JButton();
		undoButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				EventProcessor.getInstance().undoEvent();
			}
		});
		redoButton = new JButton();
		redoButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				EventProcessor.getInstance().redoEvent();
			}
		});

		// -- Create ActorType Pane
		actorTypePane = new VennMakerSideMenuActorTypePane();

		// -- Create Actors Pane
		actorsPane = new VennMakerSideMenuActorsPane();
		// -- Create RelationPanes
		relationPane = new VennMakerSideMenuRelationPanes();

		registerListener();
	}

	private void buildActionPane()
	{
		actionPane.removeAll();

		actionPane.setTitle(Messages.getString("VennMaker.MenuEditAction"));
		actionPane.setLayout(new GridLayout(0, 2));

		addIconsToActionPaneButtons();

		undoButton.setText(Messages.getString("VennMaker.MenuEditUndo"));

		redoButton.setText(Messages.getString("VennMaker.MenuEditRedo"));

		actionPane.add(undoButton);
		actionPane.add(redoButton);

		sideMenu.add(actionPane);
	}

	private void addIconsToActionPaneButtons()
	{
		try
		{
			SvgBatikResizableIcon icon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath(Messages
											.getString("VennMaker.Icons_EditUndo"))), new Dimension(32, //$NON-NLS-1$
									32));

			undoButton.setIcon(icon);

			icon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath(Messages
											.getString("VennMaker.Icons_EditRedo"))), new Dimension(32, 32)); //$NON-NLS-1$

			redoButton.setIcon(icon);

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Completely rebuilds the sidemenu
	 */
	private void buildSideMenu()
	{
		sideMenu.removeAll();

		buildActionPane();
		actorTypePane.build();
		actorsPane.build();
		relationPane.build();

		addOrdered();

		final ArrayList<JXTaskPane> relationSubPanes = relationPane.retrieveAll();
		for (JXTaskPane relationSubPane : relationSubPanes)
		{
			// relationSubPane.addMouseMotionListener(this);
			sideMenu.add(relationSubPane);
		}
	}

	public JXTaskPaneContainer retrieve()
	{

		buildSideMenu();
		update();
		return sideMenu;
	}

	public void update()
	{

		buildSideMenu();

		this.sideMenu.invalidate();
		resetUndoRedoControls();
		sideMenu.repaint();
	}

	public void resetUndoRedoControls()
	{
		undoButton.setEnabled(EventProcessor.getInstance().isUndoable());
		redoButton.setEnabled(EventProcessor.getInstance().isRedoable());
		// Undo - Button sichtbar machen, sobald ein Event eingetroffen ist...
		EventProcessor.getInstance().addEventPerformedListener(
				new EventPerformedListener()
				{
					@Override
					public void eventConsumed(VennMakerEvent event)
					{
						undoButton.setEnabled(EventProcessor.getInstance()
								.isUndoable());
						undoButton.setToolTipText(EventProcessor.getInstance()
								.getCurrentDescription());
					}
				});
		EventProcessor.getInstance().addEventPerformedListener(
				new EventPerformedListener()
				{
					@Override
					public void eventConsumed(VennMakerEvent event)
					{
						if (EventProcessor.getInstance().isRedoable())
						{
							redoButton.setEnabled(true);
							redoButton.setToolTipText(EventProcessor.getInstance()
									.getRedoDescription());
						}
						else
						{
							redoButton.setEnabled(false);
							redoButton.setToolTipText("");//$NON-NLS-1$
						}
					}
				});

	}

	private void registerListener()
	{
		actorsPane.addMouseMotionListener(this);
		subitems.add(actorsPane);

		actorTypePane.addMouseMotionListener(this);
		subitems.add(actorTypePane);
	}

	private void addOrdered()
	{
		for (SideMenuSubItem item : subitems)
		{
			sideMenu.add(item);
		}
	}

	private void moveup(SideMenuSubItem c)
	{
		// find id
		int index = 0;
		for (SideMenuSubItem i : subitems)
		{
			if (i == c)
				break;
			index++;
		}
		if (index > 0 && index < subitems.size())
		{
			SideMenuSubItem movedItem = subitems.remove(index);
			subitems.add(index - 1, movedItem);
			update();
		}
	}

	private void movedown(SideMenuSubItem c)
	{
		int index = 0;
		for (SideMenuSubItem i : subitems)
		{
			if (i == c)
			{
				break;
			}
			index++;
		}
		if (index >= 0 && index < subitems.size() - 1)
		{
			SideMenuSubItem movedItem = subitems.remove(index);
			subitems.add(index+1, movedItem);
			update();
		}
	}

	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		/**
		 * FIXME: recover collapsed status correctly!
		 */
		// System.out.println(e.getSource() + "[ " + e.getY() + " ]");
		/**
		 * e.getY() => + (on down-); - (on upward movement)
		 */
		if (e.getComponent() instanceof SideMenuSubItem)
		{
			
			if (e.getY() < -50)
			{
				moveup((SideMenuSubItem) e.getComponent());
			}
			if (e.getY() > 50)
			{
				movedown((SideMenuSubItem) e.getComponent());
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{

	}
}
