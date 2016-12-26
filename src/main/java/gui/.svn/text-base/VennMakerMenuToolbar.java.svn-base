package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import data.MenuEventList;
import data.MenuListener;
import data.MenuObject;
import events.MenuEvent;
import files.FileOperations;

public class VennMakerMenuToolbar implements ActionListener, MenuListener
{

	JToolBar										toolBar	= new JToolBar();

	ActionListener								al;
	
	JButton filterbutton; 

	/**
	 * Singleton: Referenz.
	 */
	private static VennMakerMenuToolbar	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Audiorecorder-Instanz in diesem Prozess.
	 */
	public static VennMakerMenuToolbar getInstance()
	{
		if (instance == null)
		{
			instance = new VennMakerMenuToolbar();

		}
		return instance;
	}

	private VennMakerMenuToolbar()
	{

	}

	public JToolBar create()
	{
		this.toolBar.removeAll();

		MenuEventList.getInstance().addListener(this);

		this.addButtons();

		return this.toolBar;

	}

	protected void addButtons()
	{

		JButton button = null;

		// New
		button = makeButton("icons/intern/document-new.png", "newproject",
				Messages.getString("VennMaker.MenuFileNew"),
				Messages.getString("VennMaker.MenuFileNew"));

		toolBar.add(button);

		// Save
		button = makeButton("icons/intern/document-save.png", "saveproject",
				Messages.getString("VennMaker.MenuFileSave"),
				Messages.getString("VennMaker.MenuFileSave"));
		toolBar.add(button);

		// Print
		button = makeButton("icons/intern/printer.png", "printproject",
				Messages.getString("VennMaker.MenuFilePrint"),
				Messages.getString("VennMaker.MenuFilePrint"));
		toolBar.add(button);

		toolBar.addSeparator();

		// Compute
		button = makeButton("icons/intern/compute.png", "computeanalyse",
				Messages.getString("VennMaker.Compute"),
				Messages.getString("VennMaker.Compute"));
		toolBar.add(button);

		// Actor table
		button = makeButton("icons/intern/table.png", "actortableanalyse",
				Messages.getString("VennMaker.17"),
				Messages.getString("VennMaker.17"));
		toolBar.add(button);

		toolBar.addSeparator();

		// Edit Configuration
		button = makeButton("icons/intern/emblem-system.png", "editconfig",
				Messages.getString("VennMaker.MenuToolbarConfigurationTooltip"),
				Messages.getString("VennMaker.MenuToolbarConfiguration"));
		toolBar.add(button);

		
		// Edit Attribute
		button = makeButton("icons/intern/edit_actor.png", "attributesconfig",
				Messages.getString("VennMaker.MenuToolbarAttributesTooltip"),
				Messages.getString("VennMaker.MenuToolbarAttributes"));
		toolBar.add(button);

		// Edit relational Attribute
		button = makeButton("icons/intern/system-users.png",
				"relationattributesconfig",
				Messages.getString("VennMaker.MenuToolbarRelationsTooltip"),
				Messages.getString("VennMaker.MenuToolbarRelations"));
		toolBar.add(button);

		// Edit Sektors
		button = makeButton("icons/intern/sector-button.png", "editsectors",
				Messages.getString("VennMaker.MenuToolbarSectorsTooltip"),
				Messages.getString("VennMaker.MenuToolbarSectors"));
		toolBar.add(button);

		// Edit Circles
		button = makeButton("icons/intern/circle-button.png", "editcircles",
				Messages.getString("VennMaker.MenuToolbarCirclesTooltip"),
				Messages.getString("VennMaker.MenuToolbarCircles"));
		toolBar.add(button);


		toolBar.addSeparator();

		// Edit Filter
		button = makeButton("icons/intern/filter.png", "editfilter",
				Messages.getString("VennMaker.MenuToolbarFilter"),
				Messages.getString("VennMaker.MenuToolbarFilterTooltip"));
		toolBar.add(button);

		// Activate or deactivate filter

		filterbutton = makeButton("icons/intern/filter_off.png", "deactivatefilter",
				Messages.getString("VennMaker.MenuToolbarFilterDeactivate"),
				Messages.getString("VennMaker.MenuToolbarFilterDeactivateTooltip"));
		toolBar.add(filterbutton);
		
	}

	/**
	 * Create button
	 * 
	 * @param imageName
	 * @param actionCommand
	 * @param toolTipText
	 * @param altText
	 * @return JButton
	 */
	protected JButton makeButton(String imageName, String actionCommand,
			String toolTipText, String altText)
	{
		JButton button = new JButton();

		String fileName = FileOperations.getAbsolutePath(imageName);
		
		if (FileOperations.existFile(fileName)){
		ImageIcon icon = new ImageIcon(fileName);

		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

			button.setIcon(icon);
		}
		else
		{ // no image found
			button.setSize(30, 30);
			button.setText(altText);
		}

		return button;
	}

	public void actionPerformed(ActionEvent e)
	{

		MenuEventList.getInstance().notify(
				new MenuEvent(this, new MenuObject(e.getActionCommand())));
	}

	@Override
	public void action(MenuEvent e)
	{

	}
}
