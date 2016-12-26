package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.Audiorecorder;
import data.MenuEventList;
import data.MenuListener;
import data.MenuObject;
import events.MenuEvent;
import files.FileOperations;
import files.VMPaths;

public class VennMakerMenu implements ActionListener, ItemListener, MenuListener
{

	/**
	 * Constant returned by saveRoutine() if saving was successfull
	 */
	public static final int	SAVED			= 1;

	/**
	 * Constant returned by saveRoutine() if saving was canceld;
	 */
	public static final int	CANCELD		= 0;

	/**
	 * Constant returned by saveRoutine() if saving was unsuccessfull
	 */
	public static final int	SAVE_FAILED	= -1;

	private JMenuItem			audiorecordAnalyse;

	private JMenuItem			activatefilter;
	

	public JMenuBar createVennMakerMenu() throws FileNotFoundException
	{
		ResizableIcon icon;
		JMenuBar menuBar;
		JMenu menu;
		JMenu menuEntry;

		MenuEventList.getInstance().addListener(this); //spaeter wieder entfernen!

		// Create the menu bar.
		menuBar = new JMenuBar();

		// ---- File------------------------------------------------
		
	 	menu = new JMenu(Messages.getString("VennMaker.MenuFile"));
		menuBar.add(menu);
		// -------New--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_DocNew"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem newProject = new JMenuItem(
				Messages.getString("VennMaker.MenuFileNew"), icon); //$NON-NLS-1$

		newProject.setActionCommand("newproject");
		newProject.addActionListener(this);
		menu.add(newProject);

		// -------Open--------

		menuEntry = new JMenu(Messages.getString("VennMaker.MenuFileOpen"));

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_DocOpen"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem currentProject = new JMenuItem(
				Messages.getString("VennMaker.OpenFromCurrentProject"), icon); //$NON-NLS-1$

		JMenuItem otherProject = new JMenuItem(
				Messages.getString("VennMaker.OpenFromOtherProject"), icon); //$NON-NLS-1$

		String tmp = VMPaths.VENNMAKER_TEMPDIR
				+ "projects" + VMPaths.SEPARATOR + "temp" + VMPaths.SEPARATOR; //$NON-NLS-1$ //$NON-NLS-2$

		if (!(tmp.equals(VMPaths.getCurrentWorkingDirectory())))
		{
			currentProject.setActionCommand("opencurrentproject");
			currentProject.addActionListener(this);

			menuEntry.add(currentProject);
		}

		otherProject.setActionCommand("openotherproject");
		otherProject.addActionListener(this);
		menuEntry.add(otherProject);

		menu.add(menuEntry);

		// -------Save--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_DocSave"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem saveProject = new JMenuItem(
				Messages.getString("VennMaker.MenuFileSave"), icon); //$NON-NLS-1$

		saveProject.setActionCommand("saveproject");
		saveProject.addActionListener(this);
		menu.add(saveProject);

		// -------Save as--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_DocSaveAs"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem saveAsProject = new JMenuItem(
				Messages.getString("VennMaker.MenuFileSaveas"), icon); //$NON-NLS-1$

		saveAsProject.setActionCommand("saveasproject");
		saveAsProject.addActionListener(this);
		menu.add(saveAsProject);
		
		
		// --------Export -----------------------------

		menuEntry = new JMenu(Messages.getString("VennMaker.MenuFileExport"));

		// -------Export Image--------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Export_Drawing"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem exportimage = new JMenuItem(
				Messages.getString("VennMaker.Image"), icon); //$NON-NLS-1$

		exportimage.setActionCommand("exportimage");
		exportimage.addActionListener(this);
		menuEntry.add(exportimage);

		// -------Export Data--------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Export_Spreadsheet"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem exportdata = new JMenuItem(
				Messages.getString("VennMaker.Actor_Data"), icon); //$NON-NLS-1$

		exportdata.setActionCommand("exportdata");
		exportdata.addActionListener(this);
		menuEntry.add(exportdata);

		// -------Export Audio--------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Export_Audio"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem exportaudio = new JMenuItem(
				Messages.getString("VennMaker.Audio_Data"), icon); //$NON-NLS-1$

		exportaudio.setActionCommand("exportaudio");
		exportaudio.addActionListener(this);
		menuEntry.add(exportaudio);

		// -------------------------------------------

		menu.add(menuEntry);


		// -------Print--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_DocPrint"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem printProject = new JMenuItem(
				Messages.getString("VennMaker.MenuFilePrint"), icon); //$NON-NLS-1$

		printProject.setActionCommand("printproject");
		printProject.addActionListener(this);
		menu.add(printProject);


		// -------vennmaker quit--------

		JMenuItem closeProject = new JMenuItem(
				Messages.getString("VennMaker.MenuExit")); //$NON-NLS-1$

		closeProject.setActionCommand("exitproject");
		closeProject.addActionListener(this);
		menu.add(closeProject);
		
		// --------Edit -------------------------------------------------------------------------
		menu = new JMenu(Messages.getString("VennMaker.MenuEdit"));
		menuBar.add(menu);

		
	

		// -------data protection--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("./icons/intern/key.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem protectionProject = new JMenuItem(
				Messages.getString("EditDataProtection.4"), icon); //$NON-NLS-1$

		protectionProject.setActionCommand("protectionproject");
		protectionProject.addActionListener(this);
		menu.add(protectionProject);

		// -------Add network map--------

		menuEntry = new JMenu(Messages.getString("VennMaker.Network_Add"));

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_NewWindow"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem addnewmap = new JMenuItem(
				Messages.getString("VennMaker.Network_AddEmpty"), icon); //$NON-NLS-1$
		addnewmap.setActionCommand("addnewmap");
		addnewmap.addActionListener(this);

		menuEntry.add(addnewmap);

		JMenuItem clonemap = new JMenuItem(
				Messages.getString("VennMaker.Network_Clone"), icon); //$NON-NLS-1$
		clonemap.setActionCommand("clonemap");
		clonemap.addActionListener(this);

		menuEntry.add(clonemap);

		menu.add(menuEntry);

		// -------Import--------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_User-System"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem importactor = new JMenuItem(
				Messages.getString("VennMaker.ImportActor"), icon); //$NON-NLS-1$

		importactor.setActionCommand("importactor");
		importactor.addActionListener(this);
		menu.add(importactor);		
		
		
		// -------Alle vorhandenen Akteure einzeichnen--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_Compute"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem drawAllActors = new JMenuItem(Messages.getString("VennMaker.InputAllActors") ); //$NON-NLS-1$

		drawAllActors.setActionCommand("drawallactors");
		drawAllActors.addActionListener(this);
		
		menu.add(drawAllActors);
		
		
		
		// -------Actor Table--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("icons/intern/table.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem actortableAnalyse = new JMenuItem(
				Messages.getString("VennMaker.17"), icon); //$NON-NLS-1$

		actortableAnalyse.setActionCommand("actortableanalyse");
		actortableAnalyse.addActionListener(this);
		menu.add(actortableAnalyse);

		
		// -------Relation Table-----
		
		JMenuItem relationtableAnalyse = new JMenuItem(
				Messages.getString("VennMaker.22"), icon); //$NON-NLS-1$

		relationtableAnalyse.setActionCommand("relationtableanalyse");
		relationtableAnalyse.addActionListener(this);
		menu.add(relationtableAnalyse);
		
		

		// ------Analyse------------------------------------------------------------

		menu = new JMenu(Messages.getString("VennMaker.MenuAnalyse"));
		menuBar.add(menu);


		// -------Compute--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_Compute"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem computeAnalyse = new JMenuItem(
				Messages.getString("VennMaker.Compute"), icon); //$NON-NLS-1$

		computeAnalyse.setActionCommand("computeanalyse");
		computeAnalyse.addActionListener(this);
		menu.add(computeAnalyse);

		// -------Testnetzwerk--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_Compute"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem testAnalyse = new JMenuItem("Generate Test Network", icon); //$NON-NLS-1$

		testAnalyse.setActionCommand("testanalyse");
		testAnalyse.addActionListener(this);
		
		menu.add(testAnalyse);
	


		// ------------------Config ------------------------------------

		menu = new JMenu(Messages.getString("VennMaker.Config"));
		menuBar.add(menu);

		// -------Attributes --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(
								FileOperations
										.getAbsolutePath("icons/intern/x-office-spreadsheet.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem attributesconfig = new JMenuItem(
				Messages.getString("VennMaker.4"), icon); //$NON-NLS-1$

		attributesconfig.setActionCommand("attributesconfig");
		attributesconfig.addActionListener(this);
		menu.add(attributesconfig);

		// -------Relation Attributes --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_User-System"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem relationattributesconfig = new JMenuItem(
				Messages.getString("VennMaker.Relation_Value"), icon); //$NON-NLS-1$

		relationattributesconfig.setActionCommand("relationattributesconfig");
		relationattributesconfig.addActionListener(this);
		menu.add(relationattributesconfig);
		
		
		
		// -------Config Questionnaire --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_User-System"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem questionnnaireconfig = new JMenuItem(
				Messages.getString("VennMaker.MenuQuestionnaireConfig")); //$NON-NLS-1$

		questionnnaireconfig.setActionCommand("questionnnaireconfig");
		questionnnaireconfig.addActionListener(this);
		menu.add(questionnnaireconfig);

		// ------ Audio ------------------------------------------------

		menu = new JMenu(Messages.getString("VennMaker.23"));
		menuBar.add(menu);

		// -------Audio Play--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("icons/intern/play.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem audioAnalyse = new JMenuItem(
				Messages.getString("VennMaker.Audio"), icon); //$NON-NLS-1$

		audioAnalyse.setActionCommand("audioanalyse");
		audioAnalyse.addActionListener(this);
		menu.add(audioAnalyse);

		// -------Audio Record / Stop--------

		if (Audiorecorder.getInstance().isRunning() == true)
		{

			icon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath("icons/intern/stop.svg")), new Dimension(16, //$NON-NLS-1$
									16));

			audiorecordAnalyse = new JMenuItem(
					(Messages.getString("VennMaker.6")),//$NON-NLS-1$
					icon);
		}
		else
		{
			icon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath("icons/intern/record.svg")), new Dimension(16, //$NON-NLS-1$
									16));

			audiorecordAnalyse = new JMenuItem(
					(Messages.getString("VennMaker.0")),//$NON-NLS-1$
					icon);
		}

		audiorecordAnalyse.setActionCommand("audiorecordanalyse");
		audiorecordAnalyse.addActionListener(this);
		menu.add(audiorecordAnalyse);	

		
		// ------ Interview ------------------------------------------------

		menu = new JMenu(Messages.getString("VennMaker.Interview"));
		menuBar.add(menu);
		
		// -------Interview notes--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_EditMeta"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem notesProject = new JMenuItem(
				Messages.getString("VennMaker.EditMeta"), icon); //$NON-NLS-1$

		notesProject.setActionCommand("notesproject");
		notesProject.addActionListener(this);
		menu.add(notesProject);

		// -------Start Questionnaire--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("icons/intern/interview.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem interviewAnalyse = new JMenuItem(Messages.getString("VennMaker.Interview_Start"), icon); //$NON-NLS-1$

		interviewAnalyse.setActionCommand("interviewanalyse");
		interviewAnalyse.addActionListener(this);
		menu.add(interviewAnalyse);		
		
		// ------ Module ------------------------------------------------

		menu = new JMenu("Module");
		menuBar.add(menu);
		
		// ------ VennMaker Hist Modul (testweise)--------

		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_EditMeta"))), new Dimension(16, 16)); //$NON-NLS-1$

		for(int q=0; q < VennMaker.getInstance().getModule().size(); q++){
		String moduleName = VennMaker.getInstance().getModule().get(q).getModuleName();
		JMenuItem moduleProject = new JMenuItem(moduleName, icon); //$NON-NLS-1$

		moduleProject.setActionCommand("module"+q);
		moduleProject.addActionListener(this);
		menu.add(moduleProject);
		
		}
		
		
		
		
		// ------ Filter ------------------------------------------------

		menu = new JMenu(Messages.getString("VennMaker.21"));
		menuBar.add(menu);

		// -------Edit Filter --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("icons/intern/filter.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem editfilter = new JMenuItem(
				Messages.getString("VennMaker.15"), icon); //$NON-NLS-1$

		editfilter.setActionCommand("editfilter");
		editfilter.addActionListener(this);
		menu.add(editfilter);

		// -------Activate / Deactivate Filter --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("icons/intern/filter_off.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		activatefilter = new JMenuItem(Messages.getString("VennMaker.18"), icon); //$NON-NLS-1$

		activatefilter.setActionCommand("deactivatefilter");
		activatefilter.addActionListener(this);
		menu.add(activatefilter);



		// ------- About -------------------------------------------------

		menu = new JMenu(Messages.getString("VennMaker.MenuAbout"));

		// -------Check for updates --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icons_CheckUpdates"))), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem aboutUpdate = new JMenuItem(
				Messages.getString("VennMaker.Check_Updates"), icon); //$NON-NLS-1$

		aboutUpdate.setActionCommand("aboutupdate");
		aboutUpdate.addActionListener(this);
		menu.add(aboutUpdate);

		// -------Team info --------
		icon = SvgBatikResizableIcon
				.getSvgIcon(
						new FileInputStream(FileOperations
								.getAbsolutePath("icons/intern/VennMaker.svg")), new Dimension(16, 16)); //$NON-NLS-1$

		JMenuItem aboutTeam = new JMenuItem("Team"); //$NON-NLS-1$

		aboutTeam.setActionCommand("aboutteam");
		aboutTeam.addActionListener(this);
		menu.add(aboutTeam);

		menuBar.add(menu);

		return menuBar;

	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		MenuEventList.getInstance().notify(
				new MenuEvent(this, new MenuObject( e.getActionCommand())));
	}

	@Override
	public void action(MenuEvent e)
	{
	
		

		
		//----------------
		if ("audiorecordanalyse".equals(e.getInfo().getMessage()))
		{

			if (Audiorecorder.getInstance().isRunning() == true)
			{
				audiorecordAnalyse.setText(Messages.getString("VennMaker.0")); //$NON-NLS-1$
				try
				{
					audiorecordAnalyse.setIcon(SvgBatikResizableIcon.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath("icons/intern/record.svg")),
							new Dimension(16, 16)));
				} catch (FileNotFoundException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				}
				audiorecordAnalyse.revalidate();
				audiorecordAnalyse.repaint();
			}
			else if (Audiorecorder.getInstance().isRunning() == false)
			{

				audiorecordAnalyse.setText(Messages.getString("VennMaker.6")); //$NON-NLS-1$
				try
				{
					audiorecordAnalyse.setIcon(SvgBatikResizableIcon.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath("icons/intern/stop.svg")),
							new Dimension(16, 16)));
				} catch (FileNotFoundException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				}
				audiorecordAnalyse.revalidate();
				audiorecordAnalyse.repaint();
			}

		}
	}
}
