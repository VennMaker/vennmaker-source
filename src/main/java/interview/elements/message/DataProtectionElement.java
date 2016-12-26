package interview.elements.message;

import gui.Messages;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.elements.InterviewElement;
import interview.elements.information.DataProtectionInformation;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog to display data protection information while interview
 * 
 * 
 */
public class DataProtectionElement extends InterviewElement implements IECategory_MetaElement
{
	private static final long 	serialVersionUID = 1L;
	
	private JTextArea infoArea;
	private JTextField instituteField;
	private JPanel configPanel;
	
	public DataProtectionElement()
	{
			infoArea = new JTextArea(Messages.getString("DataProtectionElement.DummyText"));
			infoArea.setLineWrap(true);
			infoArea.setWrapStyleWord(true);
			this.hasPreview=true;
			instituteField = new JTextField();
	}

	@Override
	public boolean writeData()
	{
		return true;
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}
	
	@Override
	public boolean addToTree()
	{
		setElementNameInTree(Messages.getString("DataProtectionElement.Name"));

		return true;
	}
	
	@Override
	public String getInstructionText()
	{
		return Messages.getString("DataProtectionElement.Description");	//$NON-NLS-1$
	}

	@Override
	public String toString()
	{
		return getElementNameInTree() +" "+Messages.getString("DataProtectionElement.Name");
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{		
		DataProtectionInformation info = new DataProtectionInformation(infoArea.getText(),instituteField.getText());
		info.setId(this.getId());
		info.createChildInformation(children);
		info.setElementName(this.getElementNameInTree());
		if (parent != null)
		{
			info.setParent(parent.getClass());
			info.setParentId(parent.getId());
		}

		info.setElementClass(this.getClass());
		return info;
	}
	
	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof DataProtectionInformation))
			return;
		
		getConfigurationDialog();
		DataProtectionInformation info = (DataProtectionInformation) information;
		this.setElementNameInTree(info.getElementName());

		this.infoArea.setText(info.getInfo());
		this.instituteField.setText(info.getInstitute());
		InterviewLayer.getInstance().createChild(information, this);
	}

	@Override
	public JPanel getControllerDialog()
	{
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel controllerDialog = new JPanel(gbLayout);
		
		JLabel captionLabel = new JLabel(Messages.getString("DataProtectionElement.CaptionLabel"));
		Font f = captionLabel.getFont().deriveFont(Font.BOLD);
		f = f.deriveFont(f.getSize2D()+4);
		captionLabel.setFont(f);
		JTextArea ta = new JTextArea();
		ta.setText(infoArea.getText());
		ta.setEditable(false);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		
		JScrollPane scroll = new JScrollPane(ta);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(10, 10, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		gbLayout.setConstraints(captionLabel, c);
		controllerDialog.add(captionLabel);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 5;
		c.weighty = 5;
		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		gbLayout.setConstraints(scroll, c);
		controllerDialog.add(scroll);
		
		if(instituteField.getText()!=null && !instituteField.getText().equals(""))
		{
			JLabel instituteLabel = new JLabel(instituteField.getText());
			f = instituteLabel.getFont().deriveFont(Font.ITALIC);
			f = f.deriveFont(f.getSize2D()+6);
			instituteLabel.setFont(f);
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 2;
			c.weighty = 0;
			c.weightx = 3;
			c.anchor = GridBagConstraints.SOUTHEAST;
			c.insets = new Insets(20, 0, 20, 10);
			c.fill = GridBagConstraints.NONE;
			gbLayout.setConstraints(instituteLabel, c);
			controllerDialog.add(instituteLabel);
		}
		
		return controllerDialog;
	}

	@Override
	public JPanel getConfigurationDialog()
	{
		if(true)
		{
			GridBagLayout gbLayout = new GridBagLayout();
			GridBagConstraints c = new GridBagConstraints();
			configPanel = new JPanel(gbLayout);
			
			JLabel infoLabel = new JLabel(Messages.getString("DataProtectionElement.CaptionLabel"));
			JLabel instituteLabel = new JLabel(Messages.getString("DataProtectionElement.Institute"));
			
			JScrollPane scroll = new JScrollPane(infoArea);
			
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			gbLayout.setConstraints(infoLabel, c);
			configPanel.add(infoLabel);
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 1;
			c.weighty = 5;
			c.weightx = 6;
			c.gridwidth = 2;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(10, 0, 0, 0);
			c.fill = GridBagConstraints.BOTH;
			gbLayout.setConstraints(scroll, c);
			configPanel.add(scroll);
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 2;
			c.weighty = 1;
			c.weightx = 0;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets = new Insets(30, 0, 0, 0);
			c.fill = GridBagConstraints.NONE;
			gbLayout.setConstraints(instituteLabel, c);
			configPanel.add(instituteLabel);
			
			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 2;
			c.weighty = 1;
			c.weightx = 10;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets = new Insets(30, 10, 0, 0);
			c.fill = GridBagConstraints.HORIZONTAL;
			gbLayout.setConstraints(instituteField, c);
			configPanel.add(instituteField);
		}
		return configPanel;
	}

	@Override
	public void setData()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validateInput()
	{
		return true;
	}
}
