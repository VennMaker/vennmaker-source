/**
 * 
 */
package gui.configdialog.elements;

import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.InterviewNotesSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingInterviewNotes;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Dialog fuer Interview Notizen (View auf "Edit"->"Interview notes")
 * 
 * 
 */
public class CDialogInterviewNotes extends ConfigDialogElement
{
	private static final long	serialVersionUID	= 1L;

	private JEditorPane			editor;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		dialogPanel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		if (editor == null)
		{
			editor = new JEditorPane();
			editor.setText(VennMaker.getInstance().getProject()
					.getMetaInformation());
		}

		JScrollPane editorScroll = new JScrollPane(editor);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 1;
		dialogPanel.add(editorScroll, gbc);
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		return new SettingInterviewNotes(editor.getText());
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof InterviewNotesSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		InterviewNotesSaveElement element = (InterviewNotesSaveElement) setting;

		this.buildPanel();
		editor.setText(element.getNotes());
	}

	@Override
	public SaveElement getSaveElement()
	{
		InterviewNotesSaveElement elem = new InterviewNotesSaveElement(
				editor.getText());
		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}
}
