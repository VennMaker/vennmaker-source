package interview.panels.multi;

import gui.Messages;
import gui.VennMaker;
import interview.configuration.attributesSelection.MultiAttributePanel;
import interview.panels.SpecialPanel;
import interview.panels.single.CategoricalValuePanel;
import interview.panels.single.FreeValuePanel;
import interview.panels.single.ValuePanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * panel - one actors - multiple (free/categorical) attribute types
 * 
 * select/enter multiple values of attributes for one actor
 * 
 * 
 */
public class MixedAnswerPanel extends SpecialPanel
{
	private static final long													serialVersionUID	= 1L;

	private List<Akteur>															actors;

	private Map<String, AttributeType>										questions;

	private List<ValuePanel>													panelList;

	private Map<Akteur, Map<Netzwerk, Map<AttributeType, Object>>>	oldValues;

	private int																		actorPointer;
	
	public MixedAnswerPanel()
	{
		super();
		this.questions = new HashMap<String,AttributeType>();
		panelList = new ArrayList<ValuePanel>();
		oldValues = new HashMap<Akteur,Map<Netzwerk,Map<AttributeType,Object>>>();
		this.actors = new ArrayList<Akteur>();
	}
	
	private void build()
	{
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gbLayout);
	
		this.panelList.clear();
		
		if(actors.size() > 0 && actorPointer < actors.size())
		{
			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(10, 5, 0, 0);
			c.weightx = 1;
			c.weighty = 0;
			JLabel l = new JLabel("<html><h3>"+actors.get(actorPointer).getName()+"</h3></html>");
	
			gbLayout.setConstraints(l, c);
			this.add(l);
			Netzwerk net = VennMaker.getInstance().getActualVennMakerView().getNetzwerk();
			
			GridBagLayout gbl = new GridBagLayout();
			GridBagConstraints g = new GridBagConstraints();
			JPanel centerPanel = new JPanel(gbl);
			
			int y=0;
			g.gridx = 0;
			g.gridy = y;
			g.weightx = 1;
			g.weighty = 0;
			g.fill = GridBagConstraints.HORIZONTAL;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.insets = new Insets(10, 0, 0, 0);
			
			
			String[] orderedQuestions = new String[questions.size()];
			
			for(String question : questions.keySet())
			{
				int pos = question.indexOf(MultiAttributePanel.QUESTION_SEPERATOR);
				
				int order = Integer.parseInt(question.substring(0, pos));
				
				if(order >= 0)
					orderedQuestions[order] = question;
			}
			
			
			//one label per value
			for(String question : orderedQuestions)
			{
				Color color = (y%2==0)?this.getBackground():Color.lightGray;
				int pos = question.indexOf(MultiAttributePanel.QUESTION_SEPERATOR);
				String real_question = question.substring(pos + MultiAttributePanel.QUESTION_SEPERATOR.length() ,question.length());
				AttributeType a = questions.get(question);
				Object[] preVals = a.getPredefinedValues();

				ValuePanel p = null;
				
				//free Values
				if(preVals != null)
					p = new CategoricalValuePanel(a, real_question,color);
				else //categorical Values
					p = new FreeValuePanel(a, real_question,color);
				
				Object answer = actors.get(actorPointer).getAttributeValue(a,net);
				if(answer != null)
					p.setValue(answer);
				
				panelList.add(p);
				
				gbl.setConstraints(p, g);
				centerPanel.add(p);
				
				g.gridy = ++y;
				
			}
			JScrollPane centerScroll = new JScrollPane(centerPanel);
			centerScroll.setBorder(null);
			c.gridy = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			gbLayout.setConstraints(centerScroll, c);
			this.add(centerScroll);
		}
		else
		{
			this.add(new JLabel("<html><h1>"+ 
					Messages.getString("InterviewElement.NoMatchingActors") 	//$NON-NLS-1$
					+"</h1></html>"));
		}
	}
	
	@Override
	public boolean performChanges()
	{
		if(actors.size() == 0 || actorPointer >= actors.size())
			return false;
		Vector<Netzwerk> networks = VennMaker.getInstance().getProject().getNetzwerke();
		for(ValuePanel vp : panelList)
		{
			AttributeType a = vp.getAttributeType();
			Object selectedValue = vp.getValue();
			Map<AttributeType,Object> oldAttributeValues = new HashMap<AttributeType,Object>();
			Map<Netzwerk,Map<AttributeType,Object>> networkValues = new HashMap<Netzwerk,Map<AttributeType,Object>>();
			
			for(Netzwerk n : networks)
			{
				oldAttributeValues.put(a, actors.get(actorPointer).getAttributeValue(a, n));
				networkValues.put(n, oldAttributeValues);
				oldValues.put(actors.get(actorPointer), networkValues);
				actors.get(actorPointer).setAttributeValue(a, n, selectedValue);
			}
		}
		
		return true;
	}

	@Override
	public void rebuild()
	{
		this.removeAll();
		build();
		this.updateUI();
	}

	@Override
	public void setActors(List<Akteur> actors) 
	{
		this.actors.clear();
		
		for(Akteur act : actors)
			this.actors.add(act);
	}

	@Override
	public void setAttributesAndQuestions(Map<String, AttributeType> attributesAndQuestions)
	{
		this.questions = attributesAndQuestions;
	}
	
	public boolean undoChanges()
	{
		if(actors.size() == 0 || actorPointer >= actors.size())
			return false;
		
		for(Akteur actor : actors)
		{
			Vector<Netzwerk> networks = VennMaker.getInstance().getProject().getNetzwerke();
			for(ValuePanel vp : panelList)
			{
				AttributeType a = vp.getAttributeType();
				for(Netzwerk n : networks)
				{
					Map<Netzwerk,Map<AttributeType,Object>> networkValues = oldValues.get(actor);
					
					if(networkValues == null)
						continue;
					
					Map<AttributeType,Object> attributeValues = networkValues.get(n);
					
					actor.setAttributeValue(a, n, attributeValues.get(a));
				}
			}
		}
		return true;
	}

	@Override
	public void setActorPointer(int actorPointer)
	{
		this.actorPointer = actorPointer;
	}
	
	@Override
	public boolean shouldBeScrollable()
	{
		return true;
	}

	@Override
	public boolean validateInput()
	{
		for(ValuePanel p : panelList)
			if(p.getValue() == null || p.getValue().equals(""))
				return false;
		
		return true;
	}
}

class LinePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		int h = this.getHeight() / 2;
		g2.drawLine(0, h, this.getWidth(), h);
	}
}
