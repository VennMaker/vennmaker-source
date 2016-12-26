package interview.panels.multi;

import gui.Messages;
import gui.VennMaker;
import interview.panels.SpecialPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * panel - multiple actors - one (free) attribute type
 * 
 * enter the value for an attribute for each actor
 * 
 * 
 */
public class FreeAnswerPanel extends SpecialPanel implements FocusListener
{
	private static final long				serialVersionUID	= 1L;

	private List<Akteur>						actors;

	private AttributeType					aType;

	private HashMap<Akteur, JTextField>	answers;

	private Map<Netzwerk, Object>			oldValues;

	public FreeAnswerPanel()
	{
		super();
		answers = new HashMap<Akteur, JTextField>();
		actors = new ArrayList<Akteur>();
		oldValues = new HashMap<Netzwerk, Object>();
		this.addFocusListener(this);
	}

	private void build()
	{
		GridBagLayout gbl = new GridBagLayout();
		this.setLayout(gbl);

		// one label + one textfield per actor
		if (actors != null && actors.size() > 0)
		{
			GridBagConstraints g = null;
			int x = 0;
			int y = 0;

			for (Akteur a : actors)
			{
				Color c = (y % 2 == 0) ? this.getBackground() : Color.lightGray;
				x = 0;

				JPanel actorPanel = buildActorPanel(a, c);
				JPanel answerPanel = buildAnswerPanel(a, c);

				g = new GridBagConstraints();
				g.anchor = GridBagConstraints.CENTER;
				g.gridx = x; // 0
				g.gridy = y;
				g.weighty = 0;
				g.weightx = 0;
				g.insets = new Insets(5, 5, 0, 0);
				gbl.setConstraints(actorPanel, g);
				this.add(actorPanel);

				g = new GridBagConstraints();
				g.gridx = ++x;
				g.gridy = y++;
				g.anchor = GridBagConstraints.CENTER;
				g.fill = GridBagConstraints.HORIZONTAL;
				g.ipadx = 10;
				g.weighty = 0;
				g.weightx = 1;
				g.insets = new Insets(5, 0, 0, 5);
				gbl.setConstraints(answerPanel, g);
				this.add(answerPanel);
			}
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = y;
			g.gridwidth = 2;
			g.fill = GridBagConstraints.BOTH;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.weightx = 1;
			g.weighty = 1;
			JPanel p = new JPanel();
			gbl.setConstraints(p, g);
			this.add(p);
		}
		else
		{
			this.add(new JLabel("<html><h1>"
					+ Messages.getString("InterviewElement.NoMatchingActors") //$NON-NLS-1$
					+ "</h1></html>"));
		}
	}

	private JPanel buildAnswerPanel(Akteur a, Color c)
	{
		Netzwerk net = VennMaker.getInstance().getActualVennMakerView()
				.getNetzwerk();
		Object answer = a.getAttributeValue(aType, net);
		JTextField tf = new JTextField(answer == null ? "" : answer + "");
		answers.put(a, tf);
		GridBagLayout gbLayout = new GridBagLayout();
		JPanel tfp = new JPanel(gbLayout);
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1;
		gc.weighty = 0;
		gc.insets = new Insets(0, 0, 0, 5);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		gbLayout.setConstraints(tf, gc);
		tfp.add(tf);
		tfp.setBackground(c);
		tfp.setPreferredSize(new Dimension(80, 30));

		return tfp;
	}

	private JPanel buildActorPanel(Akteur a, Color c)
	{
		JLabel l = new JLabel(a.getName() + " ", SwingConstants.RIGHT);
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();
		JPanel lp = new JPanel(gbLayout);
		gc.weightx = 0;
		gc.weighty = 0;
		gc.insets = new Insets(0, 5, 0, 10);
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.CENTER;
		gbLayout.setConstraints(l, gc);
		lp.add(l);
		lp.setBackground(c);
		lp.setPreferredSize(new Dimension(100, 30));
		return lp;
	}

	@Override
	public boolean performChanges()
	{
		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();
		for (Akteur a : answers.keySet())
		{
			String value = answers.get(a).getText();
			for (Netzwerk n : networks)
			{
				oldValues.put(n, a.getAttributeValue(aType, n));
				a.setAttributeValue(aType, n, value);
			}
		}
		return true;
	}

	@Override
	public void rebuild()
	{
		answers.clear();
		this.removeAll();
		build();
		this.updateUI();
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
		this.actors.clear();
		for (Akteur act : actors)
		{
			if (!this.actors.contains(act))
				this.actors.add(act);
		}
	}

	public boolean undoChanges()
	{
		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();
		for (Akteur a : answers.keySet())
		{
			for (Netzwerk n : networks)
				a.setAttributeValue(aType, n, oldValues.get(n));
		}
		return true;
	}

	@Override
	public void focusGained(FocusEvent arg0)
	{
		if (actors != null && actors.size() > 0)
		{
			JTextField tf = answers.get(actors.get(0));
			tf.requestFocusInWindow();
		}
	}

	@Override
	public void focusLost(FocusEvent arg0)
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
		/**
		 * do not return immediately, when there's a value missing - color ALL the
		 * empty textfields
		 */
		boolean endsuccessful = true;

		/** defines the textfield, which receives focus, when empty */
		JTextField focusOn = null;

		for (JTextField field : answers.values())
		{
			if (field.getText().equals(""))
			{
				field.setBackground(new Color(0xffbab0));

				if (focusOn == null)
					focusOn = field;

				endsuccessful = false;
			}
			else{
				field.setBackground(Color.white);			
			}
				
		}

		if (!endsuccessful){
			JOptionPane.showMessageDialog(this,
					"You have to fill out all fields to proceed",
					"Missing values", JOptionPane.WARNING_MESSAGE);
			focusOn.requestFocus();
		}

		return endsuccessful;
	}
}
