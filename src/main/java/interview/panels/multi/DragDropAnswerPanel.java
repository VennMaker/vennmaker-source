package interview.panels.multi;

import gui.Messages;
import gui.VennMaker;
import interview.panels.SpecialPanel;
import interview.panels.other.ActorTransferHandler;
import interview.panels.other.DraggableActor;
import interview.panels.single.DragDropValueList;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * panel - multiple actors - one (categorical) attribute type
 * 
 * dragging actors into the box for the right attribute value...
 * 
 * 
 */
public class DragDropAnswerPanel extends SpecialPanel
{
	private static final long					serialVersionUID	= 1L;

	private AttributeType						aType;

	private Map<Object, DragDropValueList>	answers;

	private List<Akteur>							actors;
	
	private JList<DraggableActor>				actorsList;
	
	public DragDropAnswerPanel()
	{
		super();
		actors = new ArrayList<Akteur>();
		answers = new HashMap<Object, DragDropValueList>();
	}

	private void build()
	{
		GridBagLayout gbLayout = new GridBagLayout();
		this.setLayout(gbLayout);

		//each actor will be put in the matching list (or in "not defined" list)
		if(actors!=null && actors.size()>0)
		{
			//for each AttributeValue add a JAttributeValueList Element
			Object[] preVals = aType.getPredefinedValues();
			
			DefaultListModel<DraggableActor> dflm = new DefaultListModel<DraggableActor>();
			actorsList = new JList<DraggableActor>(dflm);
			JScrollPane scroll = new JScrollPane(actorsList);
			Netzwerk net = VennMaker.getInstance().getActualVennMakerView().getNetzwerk();
			
			//each possible answer gets a DragDropValueList
			for(Object o : preVals)
			{
				DragDropValueList avList = new DragDropValueList(aType,o);
				answers.put(o,avList);
			}
			
			for(Akteur a : actors)
			{
				Object answer = a.getAttributeValue(aType, net);
				if(answer == null)
					dflm.addElement( new DraggableActor(a) );
				else
					if (answers.get(answer) != null)
					answers.get(answer).addActor(a);
			}
		
			actorsList.setDragEnabled(true);
			actorsList.setDropMode(DropMode.INSERT);
			actorsList.setTransferHandler(new ActorTransferHandler(actorsList,false));

			scroll.setBorder(BorderFactory.createTitledBorder(Messages.getString("InterviewElement.ActorInput")));//$NON-NLS-1$
			
			int x = 0, y = 0;
			
			//question label
			GridBagConstraints g = new GridBagConstraints();

			g.anchor = GridBagConstraints.NORTHWEST;
			g.gridheight = 3;
			g.gridwidth = 1;
			g.gridx = x;
			g.gridy = y;
			g.weightx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.BOTH;
			g.insets = new Insets(10, 5, 10, 5);
			gbLayout.setConstraints(scroll, g);
			this.add(scroll);
			
			y+=g.gridheight;
			x = 0;
			JPanel answerPanel = new JPanel(new GridLayout(0,answers.size()));
			//here the lists will be put on the panel...
			for(Object o : preVals)
			{
				DragDropValueList avList = answers.get(o);
				answerPanel.add(avList);
			}
			
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = y;
			g.gridheight = 3;
			g.gridwidth = 2;
			g.weightx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.BOTH;
			
			gbLayout.setConstraints(answerPanel, g);
			this.add(answerPanel);
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
		for(Object o : answers.keySet())
			answers.get(o).performChanges();
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
	public void setAttributesAndQuestions(Map<String, AttributeType> attributesAndQuestions)
	{
		String question = attributesAndQuestions.keySet().iterator().next();
		this.aType = attributesAndQuestions.get(question);
	}

	@Override
	public void setActors(List<Akteur> actors) {
		this.actors.clear();
		for(Akteur act : actors)
		{
			if(!this.actors.contains(act))
				this.actors.add(act);
		}
	}
	
	public boolean undoChanges()
	{
		for(Object o : answers.keySet())
			answers.get(o).undoChanges();
		return true;
	}

	@Override
	public boolean shouldBeScrollable()
	{
		return false;
	}
	
	public boolean validateInput()
	{
		if(actorsList.getModel().getSize() == 0)
			return true;
		
		JOptionPane.showMessageDialog(this, "All actors have to be assigned to an attribute value");
		
		return false;
	}
}
