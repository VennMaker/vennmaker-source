/**
 * 
 */
package files;

import gui.Messages;
import gui.VennMaker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * 
 * 
 */
public class Filelist extends JDialog
{
	/**
	 * Fenster, das die Dateien, die als Vector<String> uebergeben werden,
	 * anzeigt okButton zum uebernehmen der ausgewaehlten datei als "file"
	 * cancelButton zum abbruch
	 * 
	 */

	private static final long	serialVersionUID	= 1L;

	private String					file;

	/**
	 * Konstruktor
	 * 
	 * @param files
	 *           strings, die in der liste angezeigt werden sollen
	 * @param title
	 *           der fenstertitel
	 */
	public Filelist(Vector<String> files, String title)
	{
		super(VennMaker.getInstance(), true);
		setTitle(title);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setModal(true);
		setResizable(false);

		this.addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent arg0)
			{
			}

			public void windowActivated(WindowEvent arg0)
			{
			}

			public void windowClosing(WindowEvent arg0)
			{
			}

			public void windowDeactivated(WindowEvent arg0)
			{

			}

			public void windowDeiconified(WindowEvent arg0)
			{
			}

			public void windowIconified(WindowEvent arg0)
			{
			}

			public void windowOpened(WindowEvent arg0)
			{
			}
		});

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JPanel mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(220, 345));
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		final JList listOfFiles = new JList();
		listOfFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listOfFiles.setListData(files);

		// OKbutton
		JButton okButton = new JButton(Messages.getString("Filelist.Ok"));//$NON-NLS-1$
		this.getRootPane().setDefaultButton(okButton);
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (listOfFiles.getSelectedIndex() > -1)
				{
					file = (String) listOfFiles.getSelectedValue();
					setVisible(false);
				}
				else
				{
					setAlwaysOnTop(false);
					JOptionPane
							.showMessageDialog(
									null,
									Messages.getString("Filelist.NoFileSelected"),//$NON-NLS-1$
									Messages.getString("Filelist.NoSelection"), JOptionPane.ERROR_MESSAGE);//$NON-NLS-1$
					setAlwaysOnTop(true);
				}
			}
		});

		// Cancelbutton
		JButton cancelButton = new JButton(Messages.getString("Filelist.Cancel"));//$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				file = null;
				setVisible(false);
			}
		});

		// groessen festlegen
		listOfFiles.setMaximumSize(new Dimension(215, 265));
		okButton.setMaximumSize(new Dimension(200, 25));
		cancelButton.setMaximumSize(new Dimension(200, 25));

		// alles zentrieren
		listOfFiles.setAlignmentX(Component.CENTER_ALIGNMENT);
		cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		okButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Rahmen um die Listbox
		listOfFiles.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.black), listOfFiles.getBorder()));

		// alles zusammenstellen
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(listOfFiles);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		mainPanel.add(okButton);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		mainPanel.add(cancelButton);
		mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		contentPane.add(mainPanel);

		// anzeigen
		pack();
		int top = (screenSize.height - this.getHeight()) / 2;
		int left = (screenSize.width - this.getWidth()) / 2;
		setLocation(left, top);
		setVisible(true);
	}

	public String getFilename()
	{
		return file;
	}
}
