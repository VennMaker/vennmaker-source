package interview.elements.meta;

import gui.Messages;
import gui.VennMaker;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.elements.StandardElement;
import interview.elements.information.CryptActorElementInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import data.ActorCryptoElement;
import data.Akteur;
import data.Config;
import data.CryptoElement;
import data.VennMakerCrypto;

/**
 * With an object of this class, the user can encrypt all actor names with a
 * choosen password
 * 
 * 
 * 
 */
public class CryptActorElement extends StandardElement implements
		IECategory_MetaElement
{

	private JPasswordField	passwordField;
	
	private JPasswordField	confirmPasswordField;

	private JCheckBox			encryptBox;

	private JCheckBox			decryptBox;

	private byte[]				salt;

	private boolean			encrypt;
	
	private JTextArea			dataprotectioninfo;

	public CryptActorElement()
	{
		super(new NameInfoPanel(false), null, null, true);
		nSelector.setName(Messages.getString("CryptActorElement.NameInTree")); //$NON-NLS-1$
		nSelector.setInfo(Messages.getString("CryptActorElement.Description")); //$NON-NLS-1$

	}

	@Override
	public JPanel getControllerDialog()
	{
		passwordField = new JPasswordField();
		confirmPasswordField = new JPasswordField();
		JPanel passwordPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		
		final JTextArea dataprotectioninfo = new JTextArea(this.dataprotectioninfo.getText(), 10, 5);//$NON-NLS-1$
		
		dataprotectioninfo.setLineWrap(true);
		dataprotectioninfo.setWrapStyleWord(true);
		dataprotectioninfo.setEditable(false);
		dataprotectioninfo.setCursor(null);
		dataprotectioninfo.setOpaque(false);
		dataprotectioninfo.setFocusable(false);
		dataprotectioninfo.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		
		JScrollPane textPanel = new JScrollPane(dataprotectioninfo);
		
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10,10,10,10);
		
		passwordPanel.add(textPanel,gbc);
		
		gbc = new GridBagConstraints();
		
		gbc.insets = new Insets(10,10,10,10);
		gbc.gridy = 6;
		gbc.anchor = GridBagConstraints.WEST;
		
		passwordPanel.add(
				new JLabel(Messages.getString("CryptActorElement.Password")), gbc); //$NON-NLS-1$
		
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		passwordPanel.add(passwordField, gbc);
		
		if(encrypt)
		{
			gbc = new GridBagConstraints();
			gbc.insets = new Insets(10,10,10,10);
			gbc.gridy = 7;
			
			passwordPanel.add(new JLabel(Messages.getString("EditDataProtection.10")), gbc);
			
			gbc.gridx = 1;
			gbc.gridwidth = 2;
			gbc.weightx = 0.5;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			
			passwordPanel.add(confirmPasswordField,gbc);
		}
		
		return passwordPanel;

	}

	@Override
	public boolean writeData()
	{
		
		if(encrypt && !Arrays.equals(passwordField.getPassword(), confirmPasswordField.getPassword()))
		{
			JOptionPane.showMessageDialog(null, Messages.getString("EditDataProtection.11"));
			
			return false;
		}

		if ((VennMaker.getInstance().getProject().isEncodeFlag() && encrypt))
		{
			JOptionPane
					.showMessageDialog(
							null,
							Messages.getString("CryptActorElement.AlreadyEncrypted"), Messages.getString("CryptActorElement.Encryption"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$

			return true;
		}
		else if ((!VennMaker.getInstance().getProject().isEncodeFlag() && !encrypt))
		{
			JOptionPane
					.showMessageDialog(
							null,
							Messages.getString("CryptActorElement.AlreadyDecrypted"), Messages.getString("CryptActorElement.Decryption"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$

			return true;
		}

		if (encrypt)
		{
			
			CryptoElement element = new ActorCryptoElement(passwordField.getPassword());
			
			Vector<Akteur> actors = VennMaker.getInstance().getProject().getAkteure();
			
			
			List<String> encodedNames = VennMakerCrypto.getInstance().encodeActorNames(element, actors);
			
			for(int i = 0; i < actors.size(); i++)
				actors.get(i).setName(encodedNames.get(i));

			VennMaker.getInstance().getProject().setEncodeFlag(true);
		}
		else
		{
			Vector<Akteur> actors = VennMaker.getInstance().getProject()
					.getAkteure();
			List<String> decodedActorNames = VennMakerCrypto.getInstance()
					.decodeActorNames(
							new ActorCryptoElement(passwordField.getPassword()),
							actors);
			
			if(decodedActorNames.size() == 0)
				JOptionPane.showMessageDialog(null,
						"Password for Decryption may be wrong", "Password Error",
						JOptionPane.INFORMATION_MESSAGE);
				

			for (int i = 0; i < actors.size(); i++)
				actors.get(i).setName(decodedActorNames.get(i));
			

			VennMaker.getInstance().getProject().setEncodeFlag(false);
			VennMaker.getInstance().getConfig()
					.setLabelBehaviour(Config.LabelBehaviour.UNDER);
		}

		return true;
	}

	@Override
	public void setData()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public JPanel getConfigurationDialog()
	{

		if (this.configurationPanel != null)
			return this.configurationPanel;

		JPanel controllerPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		int zeile = 0;

		controllerPanel.add(
				new JLabel(Messages
						.getString("CryptActorElement.ElementDescription")), //$NON-NLS-1$
				gbc);

		gbc.gridy = ++zeile;
		
		
		
		dataprotectioninfo = new JTextArea(Messages
				.getString("EditDataProtection.9"), 10, 5);//$NON-NLS-1$
		
		dataprotectioninfo.setLineWrap(true);
		dataprotectioninfo.setWrapStyleWord(true);
		
		JScrollPane textPanel = new JScrollPane(dataprotectioninfo);
		
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10,10,10,10);
		
		controllerPanel.add(textPanel,gbc);
		
		gbc = new GridBagConstraints();
		
		gbc.gridy = ++zeile;

		encryptBox = new JCheckBox(
				Messages.getString("CryptActorElement.Encrypt")); //$NON-NLS-1$
		decryptBox = new JCheckBox(
				Messages.getString("CryptActorElement.Decrypt")); //$NON-NLS-1$

		ButtonGroup grp = new ButtonGroup();
		grp.add(decryptBox);
		grp.add(encryptBox);
		
		gbc.insets = new Insets(10,10,10,10);
		gbc.anchor = GridBagConstraints.WEST;
		controllerPanel.add(encryptBox, gbc);
		
		gbc = new GridBagConstraints();
		
		gbc.gridy = zeile;
		gbc.gridx = 4;
		gbc.insets = new Insets(10,10,10,10);
		gbc.anchor = GridBagConstraints.EAST;
		controllerPanel.add(decryptBox, gbc);

		this.configurationPanel = controllerPanel;

		return controllerPanel;
	}

	@Override
	public boolean addToTree()
	{
		setElementNameInTree(Messages.getString("CryptActorElement.NameInTree")); //$NON-NLS-1$

		this.encrypt = encryptBox.isSelected();

		return true;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("CryptActorElement.DescriptionText"); //$NON-NLS-1$
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		CryptActorElementInformation info = new CryptActorElementInformation(
				encrypt);

		info.setId(this.getId());
		info.createChildInformation(children);

		if (parent != null)
		{
			info.setParentInformation(parent); 
		}
		
		info.setInfoText(this.dataprotectioninfo.getText());
		info.setElementClass(this.getClass());

		return info;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof CryptActorElementInformation))
			return;

		CryptActorElementInformation info = (CryptActorElementInformation) information;

		getConfigurationDialog();

		if (info.isEncrypt())
			this.encryptBox.setSelected(true);
		else
			this.decryptBox.setSelected(true);

		encrypt = info.isEncrypt();

		nSelector.setName(Messages.getString("CryptActorElement.ElementName")); //$NON-NLS-1$
		nSelector.setInfo(Messages
				.getString("CryptActorElement.DescriptionText2")); //$NON-NLS-1$

		setElementNameInTree(nSelector.getName());
		
		this.dataprotectioninfo.setText(info.getInfoText());

		InterviewLayer.getInstance().createChild(information, this);
	}

	public BufferedImage getPreview()
	{
		return null;
	}
}
