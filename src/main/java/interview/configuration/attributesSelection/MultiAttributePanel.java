package interview.configuration.attributesSelection;

import gui.Messages;
import gui.configdialog.ConfigDialog;
import gui.configdialog.NewAttributeListener;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import interview.UpdateListener;
import interview.elements.StandardElement;
import interview.panels.other.AttributeCheckBox;
import interview.panels.other.DragableCheckBoxSource;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import data.AttributeType;

/**
 * 
 * Panel which makes it possible to select multiple AttributeTypes and for each
 * one question.
 * 
 * 
 */
public class MultiAttributePanel implements AttributeSelector, Serializable
{
	private static final long				serialVersionUID		= 1L;

	public static String						QUESTION_SEPERATOR	= ".@||@.";

	private AttributeAndQuestionPanel	aqPanel;

	private List<UpdateListener>			updateListeners;

	private StandardElement					parent;

	private Map<String, JCheckBox>		attributeCheckBoxes;

	public MultiAttributePanel()
	{
		this.updateListeners = new ArrayList<UpdateListener>();
	}

	/**
	 * HAS TO BE DONE BEFORE init()
	 */
	@Override
	public void setParent(StandardElement parent)
	{
		this.parent = parent;
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
	public JPanel getConfigurationPanel()
	{
		GridBagLayout gbl = new GridBagLayout();
		JPanel configPanel = new JPanel(gbl);
		GridBagConstraints g = new GridBagConstraints();

		JScrollPane scroll = new JScrollPane(aqPanel);
		scroll.setPreferredSize(new Dimension(250, 80));

		g.gridx = 0;
		g.gridy = 0;
		g.anchor = GridBagConstraints.FIRST_LINE_START;
		g.fill = GridBagConstraints.BOTH;
		g.weightx = 1;
		g.weighty = 1;
		gbl.setConstraints(scroll, g);
		configPanel.add(scroll);

		return configPanel;
	}

	@Override
	public Map<String, AttributeType> getAttributesAndQuestions()
	{
		return aqPanel.getAttributesAndQuestions(false);
	}

	@Override
	public boolean isSingleAttributeSelector()
	{
		return false;
	}

	/**
	 * setParent() has to be done BEFORE this
	 */
	@Override
	public void init(List<AttributeType> aTypes)
	{
		aqPanel = new AttributeAndQuestionPanel(aTypes, updateListeners, parent);
	}

	@Override
	public void updatePanel(List<AttributeType> aTypesNew)
	{
		boolean changed = false;

		List<AttributeType> aTypes = aqPanel.getATypes();

		if (aTypes.size() != aTypesNew.size())
			changed = true;
		else
		{
			for (int i = 0; i < aTypes.size(); i++)
			{
				AttributeType aTypeOld = aTypes.get(i);
				AttributeType aTypeNew = aTypesNew.get(i);

				// Fragen
				String qOld = aTypeOld.getQuestion();
				String qNew = aTypeNew.getQuestion();
				boolean sameQuestion = qOld == null && qNew == null;
				sameQuestion |= (qOld != null && qNew != null)
						&& (qOld.equals(qNew));

				// Label
				String lOld = aTypeOld.getLabel();
				String lNew = aTypeNew.getLabel();
				boolean sameLabel = lOld == null && lNew == null;
				sameQuestion |= (lOld != null && lNew != null)
						&& (lOld.equals(lNew));

				if (!sameQuestion || !sameLabel)
				{
					changed = true;
					break;
				}
			}
		}

		if (changed)
			aqPanel.update(aTypesNew);
	}

	@Override
	public void setAttributesAndQuestions(
			Map<String, AttributeType> attributesAndQuestions)
	{
		aqPanel.setAttributesAndQuestions(attributesAndQuestions);
	}

	public static String normalizeName(String attributeName)
	{
		if (attributeName.length() > 18)
			return attributeName.substring(0, 15) + "...";
		else
			return attributeName;
	}

	@Override
	public boolean hasAttributes()
	{
		return aqPanel.hasAttributes();
	}
}

class AttributeAndQuestionPanel extends JPanel implements
		DragableCheckBoxSource
{
	private static final long							serialVersionUID	= 1L;

	private Map<AttributeCheckBox, JTextField>	questions;

	private final List<UpdateListener>				updateListeners;

	private List<AttributeType>						aTypes;

	private StandardElement								parent;

	private Map<String, JCheckBox>					attributeCheckBoxes;

	public AttributeAndQuestionPanel(List<AttributeType> aTypes,
			final List<UpdateListener> updateListeners, StandardElement parent)
	{
		this.parent = parent;
		this.aTypes = aTypes;
		questions = new HashMap<AttributeCheckBox, JTextField>();
		attributeCheckBoxes = new HashMap<String, JCheckBox>();
		this.updateListeners = updateListeners;
		build();
	}

	public boolean hasAttributes()
	{
		return (aTypes != null) && (aTypes.size() > 0);
	}

	public void update(List<AttributeType> aTypes)
	{
		this.aTypes = aTypes;
		this.removeAll();
		Map<AttributeCheckBox, JTextField> oldQuestions = questions;

		questions = new LinkedHashMap<AttributeCheckBox, JTextField>();
		build();

		// enter the old values
		for (AttributeCheckBox acb : oldQuestions.keySet())
		{
			AttributeType a = acb.getAttributeType();
			for (AttributeCheckBox acbNew : questions.keySet())
			{
				if (acbNew.getAttributeType().getId() == a.getId())
				{
					// Nur wenn die alte Frage leer ist wird die Standardfrage
					// des Attributes gesetzt
					String q = oldQuestions.get(acb).getText();
					if (q == null || q.length() <= 0)
						q = a.getQuestion();
					questions.get(acbNew).setText(q);
					acbNew.setText(MultiAttributePanel.normalizeName(a.getLabel()));
					acbNew.setToolTipText(a.getLabel());
					acbNew.setSelected(acb.isSelected());
					break;
				}
			}
		}

		this.revalidate();
		this.repaint();
	}

	public List<AttributeType> getATypes()
	{
		return aTypes;
	}

	private void build()
	{
		GridBagLayout gbLayout = new GridBagLayout();
		this.setLayout(gbLayout);
		GridBagConstraints g = new GridBagConstraints();

		JButton newButton = new JButton(new ImageIcon(
				"./icons/intern/PlusIcon.png")); //$NON-NLS-1$
		newButton.addActionListener(new NewAttributeListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				super.actionPerformed(arg0);
				if (parent != null)
					parent.update(super.getAttributeType());
			}
		});
		newButton
				.setToolTipText(Messages.getString("NameGenerator.AddAttribute")); //$NON-NLS-1$

		JLabel lblAttribute = new JLabel(
				Messages.getString("CDialogActorSize.Label1")); //$NON-NLS-1$

		JLabel lblQuestion = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.1")); //$NON-NLS-1$

		int x = 0, y = 0;

		g.gridx = x;
		g.gridy = y;
		g.weightx = 0;
		g.weighty = 1;
		g.fill = GridBagConstraints.NONE;
		g.insets = new Insets(0, 5, 10, 0);
		g.anchor = GridBagConstraints.FIRST_LINE_START;
		gbLayout.setConstraints(lblAttribute, g);
		this.add(lblAttribute);

		g.gridx = ++x;
		g.weightx = 5;
		g.insets = new Insets(0, 0, 10, 0);
		gbLayout.setConstraints(lblQuestion, g);
		this.add(lblQuestion);

		g.insets = new Insets(0, 0, 0, 0);
		y++;
		x = 0;
		boolean first = true;

		for (final AttributeType a : aTypes)
		{

			JTextField tf = new JTextField()
			{
				public String toString()
				{
					return getText();
				}
			};
			int width = ConfigDialog.getInstance().getSize().width;

			width = (int) ((width - width / 2.5) - 200);

			// tf.setPreferredSize(new Dimension(width,20));
			tf.setText(a.getQuestion());
			// tf.setColumns(50);
			AttributeCheckBox acb = new AttributeCheckBox(a, this);
			attributeCheckBoxes.put(a.toString(), acb);
			// first attribute is selected by default
			/*
			 * if(first) { acb.setSelected(true); first=false; }
			 */
			JButton editButton = new JButton(new ImageIcon(
					"./icons/intern/emblem-system_small.png")); //$NON-NLS-1$
			editButton.setToolTipText(Messages
					.getString("NameGenerator.EditAttribute")); //$NON-NLS-1$

			editButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					new EditIndividualAttributeTypeDialog().showDialog(a);

					for (UpdateListener l : updateListeners)
						l.update();
				}
			});

			questions.put(acb, tf);
			g.gridx = x;
			g.gridy = y;
			g.weightx = 0;
			g.weighty = 1;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.fill = GridBagConstraints.NONE;
			g.insets = new Insets(2, 0, 0, 0);
			gbLayout.setConstraints(acb, g);
			this.add(acb);

			g.gridx = ++x;
			g.weightx = 1;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.fill = GridBagConstraints.HORIZONTAL;
			gbLayout.setConstraints(tf, g);

			this.add(tf);

			g.gridx = ++x;
			g.weightx = 0;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.fill = GridBagConstraints.NONE;
			gbLayout.setConstraints(editButton, g);

			this.add(editButton);

			x = 0;
			y++;
		}

		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = y;
		g.weightx = 1;
		g.gridwidth = 1;
		g.fill = GridBagConstraints.NONE;
		g.anchor = GridBagConstraints.WEST;
		gbLayout.setConstraints(newButton, g);
		this.add(newButton);
	}

	public Map<String, AttributeType> getAttributesAndQuestions(
			boolean ignoreSelection)
	{
		Map<String, AttributeType> aAndq = new HashMap<String, AttributeType>();

		int i = 0;
		for (AttributeCheckBox a : questions.keySet())
		{
			if (a.isSelected() || ignoreSelection)
				aAndq.put(i++ + MultiAttributePanel.QUESTION_SEPERATOR
						+ questions.get(a).getText(), a.getAttributeType());
		}

		return aAndq;
	}

	public void setAttributesAndQuestions(
			Map<String, AttributeType> attributesAndQuestions)
	{
		if (attributesAndQuestions == null)
			return;
		for (AttributeCheckBox box : questions.keySet())
		{
			for (Map.Entry<String, AttributeType> entry : attributesAndQuestions
					.entrySet())
			{
				if (entry.getValue().toString()
						.equals(box.getAttributeType().toString()))
				{
					box.setSelected(true);
					String questionValue = entry.getKey();
					String separator = MultiAttributePanel.QUESTION_SEPERATOR;

					String question = questionValue.substring(
							questionValue.indexOf(separator) + separator.length(),
							questionValue.length());

					questions.get(box).setText(question);

					break;
				}
			}
		}
	}

	@Override
	public Map<String, JCheckBox> getAttributeCheckBoxes()
	{
		return this.attributeCheckBoxes;
	}


	@Override
	public void updateCheckBoxes()
	{
		update(aTypes);
	}

	@Override
	public void switchCheckBoxValues(Object sourceObject, Object targetObject)
	{
		
		AttributeType sourceType = (AttributeType) sourceObject;
		AttributeType targetType = (AttributeType) targetObject;

		AttributeCheckBox sourceBox = (AttributeCheckBox) attributeCheckBoxes
				.get(sourceType.toString());
		AttributeCheckBox targetBox = (AttributeCheckBox) attributeCheckBoxes
				.get(targetType.toString());

		for (AttributeType type : aTypes)
		{
			if (type.toString().equals(sourceType.toString()))
			{
				sourceType = type;
				break;
			}
		}

		sourceBox.setAttributeType(targetType);
		targetBox.setAttributeType(sourceType);
		
		attributeCheckBoxes.put(sourceType.toString(), targetBox);
		attributeCheckBoxes.put(targetType.toString(), sourceBox);
		
		String sourceQuestion = questions.get(sourceBox).getText();

		questions.get(sourceBox).setText(questions.get(targetBox).getText());
		questions.get(targetBox).setText(sourceQuestion);

		int sourceIndex = aTypes.indexOf(sourceType);
		int targetIndex = aTypes.indexOf(targetType);

		aTypes.remove(sourceType);
		aTypes.remove(targetType);

		aTypes.add(targetIndex, sourceType);
		aTypes.add(sourceIndex, targetType);

		sourceBox.setText(MultiAttributePanel.normalizeName(sourceBox
				.getAttributeType().getLabel()));
		sourceBox.setToolTipText(sourceBox.getAttributeType().getLabel());

		targetBox.setText(MultiAttributePanel.normalizeName(targetBox
				.getAttributeType().getLabel()));
		targetBox.setToolTipText(targetBox.getAttributeType().getLabel());

		sourceBox.revalidate();
		sourceBox.repaint();

		targetBox.revalidate();
		targetBox.repaint();
		
		sourceBox.setSelected(false);
		targetBox.setSelected(false);
	}
}
