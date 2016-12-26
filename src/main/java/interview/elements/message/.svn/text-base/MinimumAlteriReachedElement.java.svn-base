package interview.elements.message;

import gui.Messages;
import gui.VennMaker;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.elements.StandardElement;
import interview.elements.information.MinimumAlteriInformation;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * Tests, if there are the minimum amount of actors to continue the interview.
 * 
 * While configuring, the user has the possibility to provide a custom infotext
 * an set the needed amount of actors (without Ego).
 * 
 */
public class MinimumAlteriReachedElement extends StandardElement implements
		IECategory_MetaElement
{
	private static final long	serialVersionUID	= 1L;

	private JSpinner				minimumAlteriSpinner;

	private JPanel					configPanel;

	private JTextArea				infoField;
	
	private Boolean skipped = false;

	public MinimumAlteriReachedElement()
	{
		
		super(null, null, null, false);
		this.skipped = false;

	}


	@Override
	public boolean writeData()
	{
		// perform only when reached by "Next"
		if (InterviewController.getInstance().wasNextPressed())
		{
			
			if (this.shouldBeSkipped() == true)
			{
				this.skipped = true;
				return true;
			}
			//JOptionPane.showMessageDialog(null, Messages.getString("MinimumAlteriReachedElement.MinimumNotReached"));
			this.skipped = false;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean shouldBeSkipped()
	{
		int value = Integer.valueOf(this.minimumAlteriSpinner.getValue().toString()).intValue();

		if ( value <= (VennMaker
				.getInstance().getProject().getAkteure().size() ))
		{
			return true;
		}
		return false;
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}

	@Override
	public boolean addToTree()
	{
		setElementNameInTree(Messages
				.getString("MinimumAlteriReachedElement.Name"));

		return true;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("MinimumAlteriReachedElement.Description"); //$NON-NLS-1$
	}

	@Override
	public String toString()
	{
		return getElementNameInTree() + " "
				+ Messages.getString("MinimumAlteriReachedElement.Name");
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		int value = Integer.valueOf(minimumAlteriSpinner.getValue().toString()).intValue();
		MinimumAlteriInformation info = new MinimumAlteriInformation(
				value , infoField.getText());
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
		if (!(information instanceof MinimumAlteriInformation))
			return;

		getConfigurationDialog();
		MinimumAlteriInformation info = (MinimumAlteriInformation) information;
		this.setElementNameInTree(info.getElementName());
		this.minimumAlteriSpinner.setValue(info.getValue());
		this.infoField.setText(info.getInfo());

		InterviewLayer.getInstance().createChild(information, this);
	}

	@Override
	public JPanel getControllerDialog()
	{
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel controllerDialog = new JPanel(gbLayout);

		JLabel captionLabel = new JLabel(
				Messages.getString("MinimumAlteriReachedElement.MinimumNotReached")
						+ "("
						+ (VennMaker.getInstance().getProject().getAkteure().size() )
						+ " / " + this.minimumAlteriSpinner.getValue() + ")");
		Font f = captionLabel.getFont().deriveFont(Font.BOLD);
		f = f.deriveFont(f.getSize2D() + 4);
		captionLabel.setFont(f);
		JTextArea ta = new JTextArea();
		ta.setText(infoField.getText());
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

		return controllerDialog;
	}

	@Override
	public void setData()
	{
		/* only check, when next was pressed */
		if (InterviewController.getInstance().wasNextPressed())
		{
			int value = Integer.valueOf(this.minimumAlteriSpinner.getValue().toString()).intValue();
	
			if (value <= (VennMaker
					.getInstance().getProject().getAkteure().size() - 1))
			{

			InterviewController.getInstance().next();
			}

		}
		/* ignore dialog, when user is going backwards */
		else{
			InterviewController.getInstance().previous();
		}
	}
	

	@Override
	public JPanel getConfigurationDialog()
	{
		infoField = new JTextArea(
				Messages.getString("MinimumAlteriReachedElement.DummyText"));
		infoField.setLineWrap(true);
		infoField.setWrapStyleWord(true);

		/*
		 * limiting the minimum/maximum/stepsize for the given JSpinner
		 * (initialValue, minimumValue, maximumValue, stepSize)
		 */
		SpinnerModel sm = new SpinnerNumberModel(1, 1, 333, 1);
		if (this.minimumAlteriSpinner == null)
		this.minimumAlteriSpinner = new JSpinner(sm);
	
		if (true)
		{
			GridBagLayout gbLayout = new GridBagLayout();

			configPanel = new JPanel(gbLayout);

			JLabel infoLabel = new JLabel(
					Messages.getString("MinimumAlteriReachedElement.CaptionLabel"));

			JLabel spinnerLabel = new JLabel(
					Messages.getString("MinimumAlteriReachedElement.Spinner"));

			JScrollPane scroll = new JScrollPane(infoField);
			GridBagConstraints c = new GridBagConstraints();

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
			gbLayout.setConstraints(spinnerLabel, c);
			configPanel.add(spinnerLabel);

			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = 2;
			c.weighty = 1;
			c.weightx = 10;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.insets = new Insets(30, 10, 0, 0);
			c.fill = GridBagConstraints.HORIZONTAL;
			gbLayout.setConstraints(this.minimumAlteriSpinner, c);
			configPanel.add(this.minimumAlteriSpinner);
		}
		return configPanel;
	}

	@Override
	public boolean validateInput()
	{
		return true;
	}

}
