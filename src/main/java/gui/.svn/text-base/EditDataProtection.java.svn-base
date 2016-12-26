/**
 * 
 */
package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import data.ActorCryptoElement;
import data.Akteur;
import data.Config;
import data.VennMakerCrypto;

/**
 * 
 * 
 */
public class EditDataProtection extends JDialog
{

	public EditDataProtection()
	{

		super(VennMaker.getInstance(), true);

		this.setPreferredSize(new Dimension(500, 500));
		this.setMinimumSize(new Dimension(500, 500));
		this.setMaximumSize(new Dimension(500, 500));

		this.setTitle(Messages.getString("EditDataProtection.0")); //$NON-NLS-1$

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

		Container contentPane = this.getContentPane();

		final JButton encodeButton = new JButton(Messages
				.getString("EditDataProtection.1")); //$NON-NLS-1$

		if (VennMaker.getInstance().getProject().isEncodeFlag() == true)
			encodeButton.setEnabled(false);

		final JButton decodeButton = new JButton(Messages
				.getString("EditDataProtection.2")); //$NON-NLS-1$

		if (VennMaker.getInstance().getProject().isEncodeFlag() == false)
			decodeButton.setEnabled(false);

		final JButton cancelButton = new JButton(Messages
				.getString("EditDataProtection.3")); //$NON-NLS-1$

		final JPasswordField passwordInput = new JPasswordField();
		final JPasswordField passwordInput2 = new JPasswordField();

		final JLabel info = new JLabel(Messages.getString("EditDataProtection.6"));//$NON-NLS-1$
		final JLabel infoPassword = new JLabel(Messages
				.getString("EditDataProtection.7"));//$NON-NLS-1$

		final JLabel infoPassword2 = new JLabel(Messages.getString("EditDataProtection.10")); //$NON-NLS-1$

		final JTextArea dataprotectioninfo = new JTextArea(Messages
				.getString("EditDataProtection.9"), 10, 5);//$NON-NLS-1$
		
		dataprotectioninfo.setLineWrap(true);
		dataprotectioninfo.setWrapStyleWord(true);
		dataprotectioninfo.setEditable(false);
		dataprotectioninfo.setCursor(null);
		dataprotectioninfo.setOpaque(false);
		dataprotectioninfo.setFocusable(false);
		dataprotectioninfo.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$

		final JScrollPane datainfo = new JScrollPane(dataprotectioninfo); //$NON-NLS-1$
		datainfo.setBorder(null);

		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);

		GridBagConstraints gbc;

		// ----

		int zeile = 0;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 12;
		gbc.gridheight = 5;
		gbc.weightx = 12;
		gbc.weighty = 5;
		gbc.insets = new Insets(10, 10, 10, 1);
		layout.setConstraints(datainfo, gbc);
		contentPane.add(datainfo);

		zeile = zeile + 5;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 12;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		layout.setConstraints(info, gbc);
		contentPane.add(info);

		zeile++;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 12;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 10, 10, 10);
		layout.setConstraints(infoPassword, gbc);
		contentPane.add(infoPassword);

		zeile++;
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 12;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 10, 0, 10);
		layout.setConstraints(passwordInput, gbc);
		contentPane.add(passwordInput);

		if (VennMaker.getInstance().getProject().isEncodeFlag() == false)
		{

			zeile++;

			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 12;
			gbc.gridheight = 1;
			gbc.insets = new Insets(10, 10, 10, 10);
			layout.setConstraints(infoPassword2, gbc);
			contentPane.add(infoPassword2);

			zeile++;
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = zeile;
			gbc.gridwidth = 12;
			gbc.gridheight = 1;
			gbc.insets = new Insets(0, 10, 0, 10);
			layout.setConstraints(passwordInput2, gbc);
			contentPane.add(passwordInput2);

		}
		zeile++;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(encodeButton, gbc);
		contentPane.add(encodeButton);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(10, 0, 10, 10);
		layout.setConstraints(decodeButton, gbc);
		contentPane.add(decodeButton);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(10, 0, 10, 10);
		layout.setConstraints(cancelButton, gbc);
		contentPane.add(cancelButton);

		decodeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				Vector<VennMakerView> views = VennMaker.getInstance().getViews();
				for (VennMakerView v : views)
					v.updateView();

				if (decodeActorName(passwordInput.getPassword()) == true)
				{
					VennMaker.getInstance().getProject().setEncodeFlag(false);
					VennMaker.getInstance().getConfig().setLabelBehaviour(
							Config.LabelBehaviour.UNDER);

					setVisible(false);
					dispose();
					VennMaker.getInstance().refresh();
				}
			}
		});

		encodeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{

				if (Arrays.equals(passwordInput.getPassword(), passwordInput2
						.getPassword()) == true)
				{
					encodeActorName(passwordInput.getPassword());

					Vector<VennMakerView> views = VennMaker.getInstance().getViews();

					for (VennMakerView v : views)
					{
						v.updateView();
					}

					setVisible(false);
					dispose();
					
					VennMaker.getInstance().getProject().setEncodeFlag(true);
					VennMaker.getInstance().refresh();

				}
				else
					JOptionPane.showMessageDialog(null,
							Messages.getString("EditDataProtection.11"), Messages.getString("EditDataProtection.12"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.ERROR_MESSAGE);
			}
		});

		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{

				setVisible(false);
				dispose();

			}
		});

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth()-this.getWidth())/2;
		int yOff = (v.getHeight()-this.getHeight())/2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		this.setVisible(true);

	}

	/**
	 * workaround: soll später auch auf andere attribute anwendbar werden
	 * 
	 * @param password
	 */
	private void encodeActorName(char[] password)
	{
		
		Vector<Akteur> actors = VennMaker.getInstance().getProject().getAkteure();
		List<String> encodedActorNames = VennMakerCrypto.getInstance().encodeActorNames(new ActorCryptoElement(password), actors);
		
		for(int i = 0; i < actors.size(); i++)
			actors.get(i).setName(encodedActorNames.get(i));
	}

	/**
	 * workaround: soll später auch auf andere attribute anwendbar werden
	 * 
	 * @param password
	 */
	private boolean decodeActorName(char[] password)
	{

		Vector<Akteur> actors = VennMaker.getInstance().getProject().getAkteure();
		List<String> decodedActorNames = VennMakerCrypto.getInstance().decodeActorNames(new ActorCryptoElement(password), actors);
		
		if(decodedActorNames.size() == 0)
		{
			JOptionPane
			.showMessageDialog(
					null,
					Messages.getString("EditDataProtection.5"), Messages.getString("EditDataProtection.8"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			
			return false;
		}
		
		for(int i = 0; i < actors.size(); i++)
			actors.get(i).setName(decodedActorNames.get(i));
		
		return true;
	}

}
