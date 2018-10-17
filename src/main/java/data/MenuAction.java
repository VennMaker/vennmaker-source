package data;

import javax.swing.JMenuItem;

import events.MenuEvent;
import files.IO;
import gui.AboutTeam;
import gui.ActorTableDialog;
import gui.ChangeFontSize;
import gui.ConfigRelationDialog;
import gui.EditDataProtection;
import gui.EditMetaInformationDialog;
import gui.ExportImageDialog;
import gui.FilterDialog;
import gui.ImportData;
import gui.RelationTableDialog;
import gui.VennMaker;
import gui.ViewStatisticDialog;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.elements.CDialogCircle;
import gui.configdialog.elements.CDialogEditAttributeTypes;
import gui.configdialog.elements.CDialogEditRelationalAttributeTypes;
import gui.configdialog.elements.CDialogInterviewCreator;
import gui.configdialog.elements.CDialogSector;

/**
 * Sammlung von Methoden, die aufgerufen werden, wenn entsprechendes Event
 * ausgeloest wird (z.B. durch Menue oder Toolbar)
 * 
 */
public class MenuAction implements MenuListener
{

	public MenuAction()
	{
		MenuEventList.getInstance().addListener(this);
	}

	@Override
	public void action(MenuEvent e)
	{

		if ("newproject".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.newProjekt();
		}

		if ("opencurrentproject".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.openCurrentProject();
			VennMaker.getInstance().updateRelations();
		}

		if ("openotherproject".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.openOtherProject();
			VennMaker.getInstance().updateRelations();
		}

		if ("saveproject".equals(e.getInfo().getMessage()))
		{
			IO.save();
		}

		if ("saveasproject".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.saveAsProject();
		}

		if ("printproject".equals(e.getInfo().getMessage()))
		{
			VennMaker.getInstance().getActualVennMakerView().printPage();
		}

		if ("notesproject".equals(e.getInfo().getMessage()))
		{
			new EditMetaInformationDialog(VennMaker.getInstance().getProject());
		}

		if ("protectionproject".equals(e.getInfo().getMessage()))
		{
			new EditDataProtection();
		}

		if ("exitproject".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.exitAndSave();
		}

		if ("audioanalyse".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.showAudioRecorder();
		}

		if ("audiorecordanalyse".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.toggleAudioRecorder();
		}

		if ("computeanalyse".equals(e.getInfo().getMessage()))
		{
			ViewStatisticDialog.showStatisticDialog();
		}

		if ("testanalyse".equals(e.getInfo().getMessage()))
		{
			new Tester().createActorsAndRelations();
		}

		if ("drawallactors".equals(e.getInfo().getMessage()))
		{
			new DrawAllActors().draw();
		}
		if ("actortableanalyse".equals(e.getInfo().getMessage()))
		{
			new ActorTableDialog(VennMaker.getInstance().getActualVennMakerView(),
					VennMaker.getInstance().getActualVennMakerView().getNetzwerk());
		}

		if ("relationtableanalyse".equals(e.getInfo().getMessage()))
		{
			new RelationTableDialog(VennMaker.getInstance()
					.getActualVennMakerView());
		}

		if ("interviewanalyse".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.startInterview();
		}

		if ("addnewmap".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.addNewMap();
		}

		if ("clonemap".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.cloneMap();
		}

		if ("importactor".equals(e.getInfo().getMessage()))
		{
			ImportData.showImportDataDialog();
		}

		if ("exportimage".equals(e.getInfo().getMessage()))
		{
			new ExportImageDialog(VennMaker.getInstance().getActualVennMakerView());
		}

		if ("exportdata".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.exportData();
		}

		if ("exportaudio".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.exportAudio();
		}

		if ("editconfig".equals(e.getInfo().getMessage()))
		{
			new ConfigDialog();
		}

		if ("attributesconfig".equals(e.getInfo().getMessage()))
		{
			new ConfigDialog(CDialogEditAttributeTypes.class);
		}

		if ("relationattributesconfig".equals(e.getInfo().getMessage()))
		{
			new ConfigDialog(CDialogEditRelationalAttributeTypes.class);

			/*
			ConfigDialogElement d = new CDialogEditRelationalAttributeTypes();
			new ConfigRelationDialog(true, d.getDialog());
			*/
		}

		if ("editsectors".equals(e.getInfo().getMessage()))
		{
			new ConfigDialog(CDialogSector.class);
		}

		if ("editcircles".equals(e.getInfo().getMessage()))
		{
			new ConfigDialog(CDialogCircle.class);
		}

		if ("editfilter".equals(e.getInfo().getMessage()))
		{
			new FilterDialog(true);
		}

		if ("deactivatefilter".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.deactiveFilter();
			// VennMaker.getInstance().getConfig().setEgoDisabled(false);
		}

		if ("aboutupdate".equals(e.getInfo().getMessage()))
		{
			VennMakerActions.aboutUpdate();
		}
		

		if ("changefontsize".equals(e.getInfo().getMessage()))
		{
			new ChangeFontSize();
		}

		if ("aboutteam".equals(e.getInfo().getMessage()))
		{
			new AboutTeam();
		}

		if ("questionnnaireconfig".equals(e.getInfo().getMessage()))
		{
			new ConfigDialog(CDialogInterviewCreator.class);
		}

		String moduleName;

		for (int q = 0; q < VennMaker.getInstance().getModule().size(); q++)
		{

			moduleName = "module" + q;
			if (moduleName.equals(e.getInfo().getMessage()))
			{
				VennMaker.getInstance().getModule().get(q).getConfigDialog();
			}
		}

	}

}
