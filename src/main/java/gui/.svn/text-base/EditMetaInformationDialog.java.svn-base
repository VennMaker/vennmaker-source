/**
 * 
 */
package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Projekt;
import files.FileOperations;

/**
 * Dialog um die Meta Informationen eines Projekts (im Moment nur Text) zu
 * bearbeiten und einzusehen.
 * 
 * 
 */
public class EditMetaInformationDialog extends JDialog
{
	private static final long	serialVersionUID	= 1L;

	private JEditorPane			editor;

	public EditMetaInformationDialog(final Projekt pro)
	{
		this.setTitle(Messages.getString("VennMaker.EditMeta")); //$NON-NLS-1$
		this.setModal(true);
		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		// EditorPane
		editor = new JEditorPane();
		final String metaInfos = pro.getMetaInformation();
		editor.setText(metaInfos != null ? metaInfos : "");

		JPanel buttonPanel = createButtonPanel(this, pro);

		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints gbc;
		this.setLayout(layout);

		JScrollPane scrollPane = new JScrollPane(editor);
		scrollPane.setPreferredSize(new Dimension(600, 500));
		editor.setPreferredSize(new Dimension(600, 500));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 10;
		gbc.gridheight = 10;
		gbc.weightx = 10;
		gbc.weighty = 10;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(scrollPane, gbc);
		this.add(scrollPane);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		layout.setConstraints(buttonPanel, gbc);
		this.add(buttonPanel);

		this.setSize(300, 300);
		this.pack();

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	private JPanel createButtonPanel(final JDialog dia, final Projekt pro)
	{
		// ButtonPanel
		JPanel buttonPanel = new JPanel(new GridLayout(0, 2));

		JButton okButton = new JButton(Messages.getString("BackgroundConfig.OK"));

		JButton cancelButton = new JButton(
				Messages.getString("BackgroundConfig.Cancel"));

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String mI = pro.getMetaInformation();

				if (!mI.equals(editor.getText()))
				{
					pro.setMetaInformation(editor.getText());
					VennMaker.getInstance().setChangesUnsaved();
				}
				dia.dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dia.dispose();
			}
		});

		return buttonPanel;
	}
}
