package interview.elements.meta;

import gui.Messages;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.elements.StandardElement;
import interview.elements.information.AudioRecorderElementInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import data.Audiorecorder;

/**
 * meta-element to start / stop audio recording while interview
 * 
 * 
 */

public class AudioRecorderElement extends StandardElement implements
		IECategory_MetaElement
{
	private static final long	serialVersionUID	= 1L;

	private JPanel					controllerPanel;

	private JRadioButton			rbStart;

	private JRadioButton			rbStop;

	public AudioRecorderElement()
	{
		super(null, null, null, true);
	}

	@Override
	public JPanel getControllerDialog()
	{
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		controllerPanel = new JPanel(gbl);
		JLabel label = new JLabel(
				"<html><h2>"
						+ (rbStart.isSelected() ? Messages
								.getString("Interview.AudiorecordStart") : Messages
								.getString("Interview.AudiorecordStop"))
						+ "</h2></html>");

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(label, c);
		controllerPanel.add(label);

		return controllerPanel;
	}

	@Override
	public boolean writeData()
	{
		// only if "Next" was pressed
		if (InterviewController.getInstance().wasNextPressed())
		{
			if (rbStart.isSelected())
				Audiorecorder.getInstance().startRecording();
			else
				Audiorecorder.getInstance().stopRecording();
		}
		return true;
	}

	@Override
	public void setData()
	{
	}

	@Override
	public JPanel getConfigurationDialog()
	{
		if (configurationPanel != null)
			return configurationPanel;
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		configurationPanel = new JPanel(gbLayout);

		// RecordPanel
		JLabel recLabel = new JLabel(
				"<html><h1>" + Messages.getString("AudioRecorderElement.ConfigLabel") + "</h1></html>"); //$NON-NLS-1$
		ButtonGroup bg = new ButtonGroup();
		if (rbStart == null)
		{
			rbStart = new JRadioButton(
					"<html><h2>" + Messages.getString("AudioRecorderElement.Start") + "</h2></html>"); //$NON-NLS-1$
			rbStart.setSelected(true);
			rbStop = new JRadioButton(
					"<html><h2>" + Messages.getString("AudioRecorderElement.Stop") + "</h2></html>"); //$NON-NLS-1$
		}
		bg.add(rbStart);
		bg.add(rbStop);

		GridBagLayout gbl = new GridBagLayout();
		JPanel recPanel = new JPanel(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		gbl.setConstraints(recLabel, gbc);
		recPanel.add(recLabel);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbl.setConstraints(rbStart, gbc);
		recPanel.add(rbStart);

		gbc.gridx = 1;
		gbl.setConstraints(rbStop, gbc);
		recPanel.add(rbStop);
		// -------RecordPanel---------------

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(30, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.setConstraints(recPanel, c);
		configurationPanel.add(recPanel);

		return configurationPanel;
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}

	@Override
	public boolean addToTree()
	{
		setElementNameInTree("AudioRecorderElement");
		return true;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("AudioRecorderElement.Description"); //$NON-NLS-1$
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		AudioRecorderElementInformation info = new AudioRecorderElementInformation(
				rbStart.isSelected());
		info.setId(this.getId());
		info.createChildInformation(children);
		info.setElementName(this.getElementNameInTree());

		if (parent != null)
		{
			info.setParentInformation(parent);
		}
		info.setElementClass(this.getClass());

		return info;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (information instanceof AudioRecorderElementInformation)
		{
			AudioRecorderElementInformation info = (AudioRecorderElementInformation) information;
			if (rbStart == null)
			{
				rbStart = new JRadioButton(
						"<html><h2>" + Messages.getString("AudioRecorderElement.Start") + "</h2></html>"); //$NON-NLS-1$
				rbStop = new JRadioButton(
						"<html><h2>" + Messages.getString("AudioRecorderElement.Stop") + "</h2></html>"); //$NON-NLS-1$
			}
			boolean start = info.shouldStartRecording();
			rbStart.setSelected(start);
			rbStop.setSelected(!start);

			this.setId(info.getId());
			this.setElementNameInTree(info.getElementName());
			InterviewLayer.getInstance().createChild(information, this);
		}
	}
}
