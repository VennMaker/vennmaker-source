package interview.elements.relation;

import gui.VennMaker;
import interview.InterviewLayer;
import interview.categories.IECategory_RelationInterpretator;
import interview.elements.InterviewElement;
import interview.panels.multi.RelationRadioAnswerPanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import data.Akteur;

public class RelationGeneratorPairElement extends RelationGeneratorListElement
		implements IECategory_RelationInterpretator
{
	/**
	 * Points at the current <code>Actor</code> asked
	 * for <code>getCurrentActor()</code>
	 */
	private int				currentActorPointer;

	/**
	 * Contains all actors to ask for <code>getCurrentActor()</code>
	 */
	private List<Akteur>	actorsToAsk;
	
	/**
	 * <code>true</code> if <code>EGO</code> should be ignored when asking 
	 * the questions
	 */
	private boolean		ignoreEgo;
	
	/**
	 * <code>true</code> if this generator should start asking questions 
	 * at the last pair of actors.
	 */
	private boolean		startAtEnd;


	public JPanel getControllerDialog()
	{
		
		JPanel p = super.getControllerDialog();
		
		
		if(ignoreEgo)
			initalActorsToAsk.remove(VennMaker.getInstance().getProject().getEgo());
		
		if(filterDirectedActors)
		{
			if(initalActorsToAsk.get(getCurrentActor()) == null)
			{
				List<Akteur> actorsToFilter = fSelector.getFilteredActors();
				
				if(ignoreEgo)
					actorsToFilter.remove(VennMaker.getInstance().getProject().getEgo());
					
				actorsToAsk = filterDirectedActors(actorsToFilter);
				initalActorsToAsk.put(getCurrentActor(), actorsToAsk);
			}
			else
			{
				actorsToAsk = initalActorsToAsk.get(getCurrentActor());
			}
		}
		else
		{
			actorsToAsk = initalActorsToAsk.get(getCurrentActor());
			
			
			List<Akteur> noRelationActors = actorsWithNoRelation.get(getCurrentActor());
			
			for(Akteur akt : actorsToAsk)
			{
				if(noRelationActors != null)
					noRelationActors.remove(akt);
				
				if(actorsWithNoRelation.get(akt) != null)
					actorsWithNoRelation.get(akt).remove(getCurrentActor());
			}
			
			
		}
		
		if (ignoreEgo)
			actorsToAsk.remove(VennMaker.getInstance().getProject().getEgo());
		
		for (Akteur actor : getActors())
			if (getCurrentActor().equals(actor))
				actorsToAsk.remove(actor);
		
		List<Akteur> currentActors = new ArrayList<Akteur>();
		
		if(startAtEnd)
			currentActorPointer = actorsToAsk.size() - 1;
		
		if (currentActorPointer < actorsToAsk.size() && currentActorPointer > -1)
			currentActors.add(actorsToAsk.get(currentActorPointer));
		
		if(specialPanel instanceof RelationRadioAnswerPanel)
			((RelationRadioAnswerPanel)specialPanel).setPairMode(true);
		
		specialPanel.setActors(currentActors);

		specialPanel.rebuild();

		return p;

	}
	

	public InterviewElement getNextElementInList()
	{

		startAtEnd = false;
		
		if (currentActorPointer + 1 < actorsToAsk.size())
		{
			currentActorPointer++;
			filterDirectedActors = true;
			return this;
		}
		else
		{
			currentActorPointer = 0;
			
			if (super.getInternalPointerValue() + 1 < getInternalListSize())
				return super.getNextElementInList();
			
			return InterviewLayer.getInstance().getNextElement();
		}
	}

	public InterviewElement getPreviousElementInList()
	{
		if (currentActorPointer - 1 >= 0)
		{
			currentActorPointer--;
			filterDirectedActors = false;
			startAtEnd = false;
			return this;
		}
		else
		{
			currentActorPointer = 0;
			startAtEnd = true;
			return super.getPreviousElementInList();
		}
	}

	public int getInternalPointerValue()
	{
		
		if (actorsToAsk != null && currentActorPointer == 0
				&& super.getInternalPointerValue() == getInternalListSize() - 1)
			return super.getInternalPointerValue() - 1;
		
		if (actorsToAsk != null && currentActorPointer > 0
				&& currentActorPointer < actorsToAsk.size())
		{
			return currentActorPointer;
		}
		else
		{
			currentActorPointer = 0;
			return super.getInternalPointerValue();
		}
	}
	
	
	public Akteur getCurrentActor()
	{
		if (this.actors.size() > 0)
			return this.actors.get(super.getInternalPointerValue());
		return null;
	}
	
	
	/**
	 * Returns <code>true</code> if <code>EGO</code> should be ignored when asking 
	 * @return <code>true</code> if <code>EGO</code> should be ignored when asking 
	 */
	public boolean isIgnoreEgo()
	{
		return ignoreEgo;
	}
	
	/**
	 * Sets <code>true</code> if <code>EGO</code> should be ignored when asking 
	 * @param ignoreEgo <code>true</code> if <code>EGO</code> should be ignored when asking 
	 */
	public void setIgnoreEgo(boolean ignoreEgo)
	{
		this.ignoreEgo = ignoreEgo;
	}
}
