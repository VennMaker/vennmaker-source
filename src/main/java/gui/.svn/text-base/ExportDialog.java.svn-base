/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import data.Netzwerk;
import data.Projekt;
import export.ExportCSV;
import export.ExportData;
import export.ExportPajek;
import export.ExportWriteData;
import files.FileOperations;

/**
 * 
 * 
 *         This dialog shows some options for the csv export
 * 
 */

public class ExportDialog extends JDialog implements ActionListener,
		WindowListener
{
	private static final long	serialVersionUID	= 1L;

	private Projekt				project;

	public ExportDialog(Projekt project)
	{

		this.project = project;

		addWindowListener(this);
		setPreferredSize(new Dimension(620, 300));
		setMinimumSize(new Dimension(620, 300));
		setMaximumSize(new Dimension(620, 300));
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(Messages.getString("CsvExportDialog.0")); //$NON-NLS-1$
		setIconImage(new ImageIcon(
				FileOperations.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$
		setLayout(new BorderLayout());

		JPanel windowPanel = new JPanel(new BorderLayout());

		final JButton changeDirectoryButton = new JButton(
				Messages.getString("CsvExportDialog.1")); //$NON-NLS-1$
		final JButton defaultDirectoryButton = new JButton(
				Messages.getString("CsvExportDialog.2")); //$NON-NLS-1$
		final JButton cancelButton = new JButton(
				Messages.getString("CsvExportDialog.3")); //$NON-NLS-1$
		final JButton exportButton = new JButton(
				Messages.getString("CsvExportDialog.4")); //$NON-NLS-1$

		final JTextField fileField = new JTextField(VennMaker.getInstance().getExportPath(), 20);
		fileField.setEditable(false);

		final JTextField nameField = new JTextField("", 20);

		final JLabel folderName = new JLabel(
				Messages.getString("CsvExportDialog.5")); //$NON-NLS-1$

		final JLabel fileName = new JLabel(
				Messages.getString("CsvExportDialog.6")); //$NON-NLS-1$

		final JRadioButton rb1 = new JRadioButton(
				Messages.getString("CsvExportDialog.7")); //$NON-NLS-1$
		final JRadioButton rb2 = new JRadioButton(
				Messages.getString("CsvExportDialog.8")); //$NON-NLS-1$

		windowPanel.removeAll();

		final Box vbox = Box.createVerticalBox();

		// ----

		final JPanel panel = new JPanel();

		final Box vbox2 = Box.createVerticalBox();

		Box hbox1 = Box.createHorizontalBox();
		hbox1.add(fileName);
		hbox1.add(nameField);
		vbox2.add(hbox1);

		vbox2.add(Box.createVerticalStrut(20));

		Box hbox2 = Box.createHorizontalBox();

		hbox2.add(folderName);
		hbox2.add(fileField);
		hbox2.add(changeDirectoryButton);
		hbox2.add(defaultDirectoryButton);

		vbox2.add(hbox2);

		vbox2.add(Box.createVerticalStrut(20));

		Box hbox3 = Box.createHorizontalBox();

		final JPanel panel1 = new JPanel();

		panel1.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("CsvExportDialog.9"))); //$NON-NLS-1$

		rb1.setSelected(true);

		final ButtonGroup g = new ButtonGroup();
		g.add(rb1);
		g.add(rb2);

		final Box vbox3 = Box.createVerticalBox();

		hbox3.add(rb1);

		vbox3.add(hbox3);

		vbox3.add(Box.createVerticalStrut(20));

		Box hbox4 = Box.createHorizontalBox();

		hbox4.add(rb2);

		vbox3.add(hbox4);

		panel1.add(vbox3);
		vbox2.add(panel1);

		vbox2.add(Box.createVerticalStrut(20));

		/*
		 * Box hbox5 = Box.createHorizontalBox();
		 * 
		 * JRadioButton ne1 = new JRadioButton("current network map");
		 * JRadioButton ne2 = new JRadioButton("all network maps");
		 * 
		 * ne2.setSelected(true);
		 * 
		 * ButtonGroup networkGroup = new ButtonGroup(); networkGroup.add(ne1);
		 * networkGroup.add(ne2);
		 * 
		 * hbox5.add(ne1); hbox5.add(ne2);
		 * 
		 * vbox2.add(hbox5);
		 */
		vbox2.add(Box.createVerticalStrut(20));

		Box hbox6 = Box.createHorizontalBox();

		hbox6.add(exportButton);

		vbox2.add(hbox6);

		Box hbox7 = Box.createHorizontalBox();

		hbox7.add(cancelButton);
		hbox7.add(exportButton);

		vbox2.add(hbox7);

		panel.add(vbox2);

		vbox.add(panel);

		windowPanel.add(vbox);

		windowPanel.validate();
		windowPanel.repaint();

		changeDirectoryButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{

				VennMaker.getInstance().setExportPath(folderDialog(VennMaker.getInstance().getExportPath()));
				fileField.setText(VennMaker.getInstance().getExportPath());
			}

		});

		defaultDirectoryButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				fileField.setText(VennMaker.getInstance().getExportPath());

			}
		});

		exportButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				ExportCSV export = new ExportCSV();

				if (rb1.isSelected())
					export.toCsv(
							fileField.getText() + "/", nameField.getText(), ";", //$NON-NLS-1$ //$NON-NLS-2$
							","); //$NON-NLS-1$
				else if (rb2.isSelected())
					export.toCsv(
							fileField.getText() + "/", nameField.getText(), ",", //$NON-NLS-1$ //$NON-NLS-2$
							";"); //$NON-NLS-1$

				// CreateMovie test= new
				// CreateMovie(VennMaker.getInstance().getViews().firstElement());
				// test.generateSlides("/home/nexus/test/");
				
				int counter = 1;
				for (Netzwerk n: VennMaker.getInstance().getProject().getNetzwerke()){
					ExportData exportNet = new ExportPajek();
					exportNet.setNetwork(n);
					ExportWriteData.writeToFile(fileField.getText() + "/" + counter + "_" + nameField.getText() + ".net", exportNet.toString());
					counter ++;
				}

				dispose();

			}
		});

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				dispose();

			}
		});

		add(windowPanel);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		setVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent
	 * )
	 */
	@Override
	public void windowDeactivated(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent
	 * )
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	private String folderDialog(String currentPath)
	{

		final JFileChooser fc = new JFileChooser();
		File file = null;

		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory(new File(currentPath));

		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile();

		}
		else
		{
			file = new File(currentPath);
		}

		return file.toString();

	}

}
