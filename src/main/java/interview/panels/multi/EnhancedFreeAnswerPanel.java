package interview.panels.multi;

import gui.Messages;
import gui.VennMaker;
import interview.panels.SpecialPanel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * panel - one actor - one (free) attribute type
 * 
 * text area with multiple lines to type in a free attribute value
 * 
 * 
 */
public class EnhancedFreeAnswerPanel extends SpecialPanel implements
		FocusListener
{
	private static final long		serialVersionUID	= 1L;

	private Akteur						actor;

	private AttributeType			aType;

	private JTextArea					taAnswer;

	private Map<Netzwerk, Object>	oldValues;

	public EnhancedFreeAnswerPanel()
	{
		super();
		oldValues = new HashMap<Netzwerk, Object>();
		this.addFocusListener(this);
	}

	private void build()
	{
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		if (actor != null)
		{
			taAnswer = new JTextArea();

			taAnswer.setLineWrap(true);
			taAnswer.setWrapStyleWord(true);

			/**
			 * if actor already has an attributevalue for the current attributeType
			 * - update the textfield
			 */
			String text = actor.getAttributeValue(aType, VennMaker.getInstance()
					.getProject().getCurrentNetzwerk()) == null ? "" : actor
					.getAttributeValue(aType,
							VennMaker.getInstance().getProject().getCurrentNetzwerk())
					.toString();

			taAnswer.setText(text);

			JScrollPane scrollAnswer = new JScrollPane(taAnswer);
			JLabel actorLabel = new JLabel("<html><b>" + actor.getName());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1.0;
			c.weighty = 0.1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.CENTER;
			gbl.setConstraints(actorLabel, c);
			this.add(actorLabel);

			c.gridy = 1;
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
			gbl.setConstraints(scrollAnswer, c);
			this.add(scrollAnswer);
		}
		else
		{
			this.add(new JLabel("<html><h1>"
					+ Messages.getString("InterviewElement.NoMatchingActors") //$NON-NLS-1$
					+ "</h1></html>"));
		}
	}

	@Override
	public void setAttributesAndQuestions(
			Map<String, AttributeType> attributesAndQuestions)
	{
		if (attributesAndQuestions == null)
			return;
		String question = attributesAndQuestions.keySet().iterator().next();
		this.aType = attributesAndQuestions.get(question);
	}

	@Override
	public void setActors(List<Akteur> actors)
	{
		if (actors != null)
			this.actor = actors.get(0);
	}

	@Override
	public boolean performChanges()
	{
		String value = taAnswer.getText();
		for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
		{
			oldValues.put(n, actor.getAttributeValue(aType, n));
			actor.setAttributeValue(aType, n, value);
		}
		return true;
	}

	@Override
	public void rebuild()
	{
		taAnswer = null;
		this.removeAll();
		build();
		this.updateUI();
	}

	@Override
	public boolean undoChanges()
	{
		if (actor != null && oldValues != null && oldValues.size() > 0)
		{
			for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
				actor.setAttributeValue(aType, n, oldValues.get(n));
		}
		return true;
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		if (taAnswer != null)
			taAnswer.requestFocusInWindow();
	}

	@Override
	public void focusLost(FocusEvent e)
	{
	}

	@Override
	public boolean shouldBeScrollable()
	{
		return true;
	}

	@Override
	public boolean validateInput()
	{
		if (taAnswer.getText().equals(""))
		{
			JOptionPane.showMessageDialog(this,
					"You have to fill out all fields to proceed", "Missing values",
					JOptionPane.WARNING_MESSAGE);

			/* 0xffbab0 = very light red */
			taAnswer.setBackground(new Color(0xffbab0));
			taAnswer.requestFocus();
			return false;
		}

		return true;
	}
}
