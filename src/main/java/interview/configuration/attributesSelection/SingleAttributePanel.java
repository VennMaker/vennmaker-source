package interview.configuration.attributesSelection;

import gui.Messages;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.ChooseRelationGroup;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import interview.UpdateListener;
import interview.elements.StandardElement;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import data.AttributeType;

/**
 * Panel to select one AttributeType and one question
 * 
 * 
 * 
 */
public class SingleAttributePanel implements AttributeSelector, ActionListener,
		Serializable
{
	private static final long							serialVersionUID	= 1L;

	protected JComboBox<NormalizedAttributeType>	cbATypes;

	protected JTextField									tfQuestion;

	private List<UpdateListener>						updateListeners;

	private boolean										performRemove;

	protected StandardElement							parent;

	private boolean										freeAnswer;

	private JPanel											componentPanel;

	/*
	 * ask for a new groupname, when adding types?
	 */
	private boolean										askForGetType				= false;

	public SingleAttributePanel(boolean freeAnswer)
	{
		// cbATypes = new AttributeTypeComboBox();
		cbATypes = new JComboBox<NormalizedAttributeType>();
		cbATypes.setEditable(false);
		cbATypes.addActionListener(this);
		cbATypes.setActionCommand("attributeBox");
		tfQuestion = new JTextField();
		updateListeners = new ArrayList<UpdateListener>();
		this.freeAnswer = freeAnswer;
	}

	public SingleAttributePanel(boolean freeAnswer, boolean askForGetType)
	{
		this(freeAnswer);
		this.askForGetType = askForGetType;
	}

	@Override
	public void setParent(StandardElement parent)
	{
		this.parent = parent;
	}

	@Override
	public JPanel getConfigurationPanel()
	{
		JPanel panel = new JPanel();
		JPanel attributePanel = new JPanel(new GridLayout(1, 0));

		GridBagLayout gbLayout = new GridBagLayout();
		panel.setLayout(gbLayout);
		GridBagConstraints g = new GridBagConstraints();
		int x = 0;
		int y = 0;

		if (!hasAttributes())
		{
			String s = Messages.getString("AttributeSelector.NoAttributes")
					+ " "
					+ (freeAnswer ? Messages
							.getString("EditIndividualAttributeTypeDialog.10")
							: Messages
									.getString("EditIndividualAttributeTypeDialog.11"))
					+ "  ";
			JLabel errorLabel = new JLabel("<html>" + s + "</html>");
			errorLabel.setForeground(Color.red);

			g.gridx = x++;
			gbLayout.setConstraints(errorLabel, g);
			panel.add(errorLabel);
		}
		else
		{
			JLabel lblAttribute = new JLabel(
					Messages.getString("CDialogActorSize.Label1")); //$NON-NLS-1$

			JLabel lblQuestion = new JLabel(
					Messages.getString("EditIndividualAttributeTypeDialog.1")); //$NON-NLS-1$

			JButton editButton = new JButton(new ImageIcon(
					"./icons/intern/emblem-system_small.png")); //$NON-NLS-1$
			editButton.addActionListener(this);
			editButton.setActionCommand("edit");
			editButton.setToolTipText(Messages
					.getString("NameGenerator.EditAttribute")); //$NON-NLS-1$	

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
			gbLayout.setConstraints(lblQuestion, g);
			panel.add(lblQuestion);

			x = 0;
			g.gridx = x;
			g.gridy = ++y;
			g.insets = new Insets(0, 0, 0, 0);
			gbLayout.setConstraints(cbATypes, g);
			panel.add(cbATypes);

			g.gridx = ++x;
			g.gridy = y;
			g.weightx = 2;
			g.insets = new Insets(0, 10, 0, 0);
			gbLayout.setConstraints(tfQuestion, g);
			panel.add(tfQuestion);

			g.gridx = ++x;
			g.gridy = y;
			g.weightx = 0;
			g.insets = new Insets(0, 0, 0, 0);
			g.anchor = GridBagConstraints.FIRST_LINE_END;
			g.fill = GridBagConstraints.NONE;
			gbLayout.setConstraints(editButton, g);
			panel.add(editButton);
		}
		final JButton newButton = new JButton(new ImageIcon(
				"./icons/intern/PlusIcon.png")); //$NON-NLS-1$
		newButton.addActionListener(new NewAttributeListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				/* if it's not "ACTOR", then ask for which type to add */
				if (askForGetType){
					ChooseRelationGroup crg = new ChooseRelationGroup(null);
					if (crg.getButton() == JOptionPane.OK_OPTION)
						super.setGetType(crg.getRelationGroup());
					else
						return;
				}
				super.actionPerformed(arg0);
				if (parent != null)
					parent.update(super.getAttributeType());
			}
		});

		newButton
				.setToolTipText(Messages.getString("NameGenerator.AddAttribute")); //$NON-NLS-1$

		g.gridx = ++x;
		// g.insets = new Insets(0, 0, 0, 0);
		gbLayout.setConstraints(newButton, g);
		panel.add(newButton);

		JScrollPane scroll = new JScrollPane(panel);
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.anchor = GridBagConstraints.FIRST_LINE_START;
		g.fill = GridBagConstraints.BOTH;
		g.weightx = 1;
		g.weighty = 1;
		gbLayout.setConstraints(scroll, g);
		attributePanel.add(scroll);

		componentPanel = panel;

		return attributePanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		if (arg0.getActionCommand().equals("attributeBox") && !performRemove)
		{
			if (!hasAttributes())
				return;
			AttributeType a = ((NormalizedAttributeType) cbATypes
					.getSelectedItem()).getAttributeType();
			setSelectedAType(a);
		}
		else if (arg0.getActionCommand().equals("edit"))
		{
			new EditIndividualAttributeTypeDialog()
					.showDialog(((NormalizedAttributeType) cbATypes
							.getSelectedItem()).getAttributeType());

			for (UpdateListener l : updateListeners)
				l.update();
		}
	}

	@Override
	public Map<String, AttributeType> getAttributesAndQuestions()
	{
		if (!hasAttributes())
			return null;
		Map<String, AttributeType> questions = new HashMap<String, AttributeType>();
		questions.put(tfQuestion.getText(), ((NormalizedAttributeType) cbATypes
				.getSelectedItem()).getAttributeType());
		return questions;
	}

	@Override
	public boolean isSingleAttributeSelector()
	{
		return true;
	}

	@Override
	public boolean hasAttributes()
	{
		return cbATypes.getItemCount() > 0;
	}

	@Override
	public void init(List<AttributeType> aTypes)
	{
		if (aTypes == null || aTypes.size() <= 0)
		{
			cbATypes.removeAllItems();
			return;
		}
		int oldAttrId = -1;
		AttributeType aNew = aTypes.get(0);

		if (cbATypes.getSelectedItem() != null)
			oldAttrId = ((NormalizedAttributeType) cbATypes.getSelectedItem())
					.getAttributeType().getId();

		performRemove = true;
		cbATypes.removeAllItems();
		performRemove = false;

		for (AttributeType a : aTypes)
		{
			if (freeAnswer)
			{
				if (a.getPredefinedValues() == null)
				{
					cbATypes.addItem(new NormalizedAttributeType(a));
					cbATypes.setToolTipText(a.toString());
				}
				if (a.getId() == oldAttrId)
				{
					aNew = a;
				}
			}
			else
			{
				if (a.getPredefinedValues() != null
						&& a.getPredefinedValues().length > 0)
				{
					cbATypes.addItem(new NormalizedAttributeType(a));
					cbATypes.setToolTipText(a.toString());
				}
				if (a.getId() == oldAttrId)
				{
					aNew = a;
				}
			}
		}
		setSelectedAType(aNew);
	}

	@Override
	public void addUpdateListener(UpdateListener l)
	{
		if (l == null)
			throw new IllegalArgumentException(
					Messages.getString("InterviewElement.InvalidArgument")); //$NON-NLS-1$

		this.updateListeners.add(l);
	}

	@Override
	public void updatePanel(List<AttributeType> aTypesNew)
	{
		init(aTypesNew);
	}

	public void setSelectedAType(AttributeType a)
	{
		if (a == null)
			return;
		Object[] preVals = a.getPredefinedValues();
		if ((preVals == null && freeAnswer) || (preVals != null && !freeAnswer))
		{
			cbATypes.setSelectedItem(new NormalizedAttributeType(a));
			cbATypes.setToolTipText(a.toString());
			String s = a.getQuestion();
			if (s != null && s.length() > 0)
				tfQuestion.setText(s);
			else
				tfQuestion.setText("");
		}
	}

	@Override
	public void setAttributesAndQuestions(
			Map<String, AttributeType> attributesAndQuestions)
	{
		if (attributesAndQuestions == null || attributesAndQuestions.size() <= 0)
		{
			cbATypes.removeAllItems();
			return;
		}
		for (Map.Entry<String, AttributeType> entry : attributesAndQuestions
				.entrySet())
		{
			for (int i = 0; i < cbATypes.getItemCount(); i++)
				if (((NormalizedAttributeType) cbATypes.getItemAt(i))
						.getAttributeType().getId() == entry.getValue().getId())
				{
					setSelectedAType(entry.getValue());
					tfQuestion.setText(entry.getKey());
				}
		}
	}

	protected JPanel getComponentPanel()
	{
		return this.componentPanel;
	}

	protected AttributeType getSelectedAttributeType()
	{
		if (cbATypes.getSelectedItem() == null)
			return null;

		return ((NormalizedAttributeType) cbATypes.getSelectedItem())
				.getAttributeType();
	}
}

class NormalizedAttributeType
{
	private AttributeType	attributeType;

	public NormalizedAttributeType(AttributeType attributeType)
	{
		this.attributeType = attributeType;
	}

	public AttributeType getAttributeType()
	{
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType)
	{
		this.attributeType = attributeType;
	}

	public String toString()
	{
	if (attributeType.toString().length() > 20)
			return attributeType.toString().substring(0, 15) + "...";
		else
			return attributeType.toString();
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof NormalizedAttributeType))
			return false;

		NormalizedAttributeType type = (NormalizedAttributeType) obj;

		return type.getAttributeType() == this.getAttributeType();
	}

	public int hashCode()
	{
		return attributeType.hashCode();
	}
}
