package interview.configuration.attributesSelection;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.NewAttributeListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import data.AttributeType;

public class SingleAttributeValuePanel extends SingleAttributePanel implements
		AttributeValueSelector
{
	private static final long	serialVersionUID	= 1L;

	private JComboBox				valueBox;

	private Object					attributeValue;

	public SingleAttributeValuePanel(boolean freeAnswer)
	{
		super(freeAnswer);
		valueBox = new JComboBox()
		{
			public String toString()
			{
				return "SelectedItem: " + getSelectedItem().toString();
			}
		};
	}

	public JPanel getConfigurationPanel()
	{

		JPanel panel = new JPanel();
		GridBagLayout attrPanelLayout = new GridBagLayout();
		// JPanel attributePanel = new JPanel(new GridLayout(0,1));
		JPanel attributePanel = new JPanel(attrPanelLayout);
		JLabel lblAttribute = new JLabel(
				Messages.getString("CDialogActorSize.Label1")); //$NON-NLS-1$

		JLabel lblQuestion = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.1")); //$NON-NLS-1$

		JLabel lblAttributeValue = new JLabel(
				Messages.getString("NameGenerator.AttributeValue"));

		JButton editButton = new JButton(new ImageIcon(
				"./icons/intern/emblem-system_small.png")); //$NON-NLS-1$
		editButton.addActionListener(this);
		editButton.setActionCommand("edit");
		editButton.setToolTipText(Messages
				.getString("NameGenerator.EditAttribute")); //$NON-NLS-1$

		JButton newButton = new JButton(new ImageIcon(
				"./icons/intern/PlusIcon.png")); //$NON-NLS-1$
		newButton.addActionListener(new NewAttributeListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				super.actionPerformed(arg0);
				if (parent != null)
					parent.update(super.getAttributeType());

				init(VennMaker.getInstance().getProject()
						.getAttributeTypes("ACTOR"));
			}
		});
		newButton
				.setToolTipText(Messages.getString("NameGenerator.AddAttribute")); //$NON-NLS-1$

		GridBagLayout gbLayout = new GridBagLayout();
		panel.setLayout(gbLayout);
		GridBagConstraints g = new GridBagConstraints();

		int x = 0, y = 0;
		g.gridx = x;
		g.gridy = y;
		g.weightx = 0;
		g.weighty = 1;
		g.fill = GridBagConstraints.NONE;
		g.anchor = GridBagConstraints.NORTHWEST;
		gbLayout.setConstraints(lblAttribute, g);
		panel.add(lblAttribute);

		g.gridx = ++x;
		g.gridy = y;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.insets = new Insets(0, 10, 0, 0);
		gbLayout.setConstraints(lblAttributeValue, g);
		panel.add(lblAttributeValue);

		x = 0;
		g.gridx = x;
		g.gridy = ++y;
		g.weightx = 1;
		g.insets = new Insets(0, 0, 0, 0);
		gbLayout.setConstraints(cbATypes, g);
		panel.add(cbATypes);

		g.gridx = ++x;
		g.gridy = y;
		g.weightx = 1;
		g.insets = new Insets(0, 10, 0, 0);
		gbLayout.setConstraints(valueBox, g);
		panel.add(valueBox);

		g.gridx = ++x;
		g.gridy = y;
		g.weightx = 0;
		g.insets = new Insets(0, 0, 0, 0);
		g.anchor = GridBagConstraints.FIRST_LINE_END;
		g.fill = GridBagConstraints.NONE;
		gbLayout.setConstraints(editButton, g);
		panel.add(editButton);

		g.gridx = ++x;
		g.insets = new Insets(0, 0, 0, 0);
		gbLayout.setConstraints(newButton, g);
		panel.add(newButton);

		g.gridx = 0;
		g.gridy = ++y;
		g.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.setConstraints(lblQuestion, g);
		panel.add(lblQuestion);

		JPanel tfQuestionPanel = new JPanel();
		tfQuestionPanel
				.setLayout(new BoxLayout(tfQuestionPanel, BoxLayout.Y_AXIS));

		JScrollBar scroll = new JScrollBar(JScrollBar.HORIZONTAL);
		scroll.setModel(tfQuestion.getHorizontalVisibility());

		tfQuestionPanel.add(tfQuestion);
		tfQuestionPanel.add(scroll);

		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.anchor = GridBagConstraints.FIRST_LINE_START;
		g.fill = GridBagConstraints.BOTH;
		g.weightx = 1;
		g.weighty = 1;
		attrPanelLayout.setConstraints(panel, g);
		attributePanel.add(panel);
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 1;
		g.gridheight = 1;
		g.anchor = GridBagConstraints.FIRST_LINE_START;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		g.weighty = 1;
		attrPanelLayout.setConstraints(tfQuestionPanel, g);
		attributePanel.add(tfQuestionPanel);

		return attributePanel;

	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		super.actionPerformed(arg0);

		fillValueBox();
	}

	public void init(List<AttributeType> aTypes)
	{
		attributeValue = valueBox.getSelectedItem();

		super.init(aTypes);

		fillValueBox();
	}

	public AttributeType getSelectedAttribute()
	{
		return getSelectedAttributeType();
	}

	public Object getSelectedValue()
	{
		return valueBox.getSelectedItem();
	}

	@Override
	public void setSelectedAttributeFromString(String attributeName)
	{
		for (AttributeType type : VennMaker.getInstance().getProject()
				.getAttributeTypes())
			if (type.toString().equals(attributeName))
				setSelectedAType(type);
	}

	@Override
	public void setSelectedValueFromString(String valueName)
	{
		for (int i = 0; i < valueBox.getItemCount(); i++)
			if (valueBox.getItemAt(i).toString().equals(valueName))
				valueBox.setSelectedIndex(i);
	}

	private void fillValueBox()
	{
		valueBox.removeAllItems();

		if ((getSelectedAttribute() != null)
				&& (getSelectedAttributeType().getPredefinedValues() != null && getSelectedAttributeType()
						.getPredefinedValues().length > 0))
		{
			for (Object obj : getSelectedAttributeType().getPredefinedValues())
			{
				valueBox.addItem(obj);

				if (obj == attributeValue)
					valueBox.setSelectedItem(obj);
			}
		}
		else
		{
			cbATypes.removeItem(new NormalizedAttributeType(
					getSelectedAttributeType()));
			int anzahl = cbATypes.getItemCount();
			if (anzahl >0)
			cbATypes.setSelectedIndex(anzahl);
			
		}
	}

	public void setAttributesAndQuestions(
			Map<String, AttributeType> attributesAndQuestions)
	{
		Object obj = valueBox.getSelectedItem();

		super.setAttributesAndQuestions(attributesAndQuestions);

		valueBox.setSelectedItem(obj);
	}
}
