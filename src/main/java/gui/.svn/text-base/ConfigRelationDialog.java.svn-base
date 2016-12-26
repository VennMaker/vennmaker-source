/**
 * Diese Klasse realisiert die Suche bzw die Filterfunktion
 */
package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import data.Filterparameter;
import data.Netzwerk;
import files.FileOperations;

/**
 * 
 * 
 */
public class ConfigRelationDialog extends JDialog implements WindowListener
{
	private static final long									serialVersionUID			= 1L;
	
	private JScrollPane											scrollPane;

	public ConfigRelationDialog(boolean visible, JPanel p)
	{
		setModal(true);
		setTitle(Messages.getString("SearchDialog.0")); //$NON-NLS-1$
		setSize(700, 550);
		setLayout(new BorderLayout());
		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		JLabel descriptionArea = new JLabel(Messages.getString("SearchDialog.15")); //$NON-NLS-1$

		add(descriptionArea, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(Messages.getString("SearchDialog.1")); //$NON-NLS-1$
		JButton cancelButton = new JButton(Messages.getString("SearchDialog.2")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				dispose();
			}
		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		add(p, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setSize(580, 314);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		if (visible)
		{
			setVisible(true);
		}
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
	}
}
