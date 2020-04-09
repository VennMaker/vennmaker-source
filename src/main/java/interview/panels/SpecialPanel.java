package interview.panels;

import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import data.Akteur;
import data.AttributeType;

/**
 * panel to display the interview questions and answering methods
 *  
 * 
 */
public abstract class SpecialPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	protected int actorPointer;
	
	/**
	 * use this to set the questions and related attribute types
	 * for panels with only one question/attribute the list has length 1
	 */
	abstract public void setAttributesAndQuestions(Map<String, AttributeType> attributesAndQuestions);
	
	/**
	 * use this to set the actors for the special panel
	 * for panels with only one actor the list has length 1
	 */
	abstract public void setActors(List<Akteur> actors);
	
	/**
	 * should be called out of <code>writeData</code> method from an interview element
	 */
	abstract public boolean performChanges();
	
	/**
	 * should be called out of <code>setData</code> method from an interview element
	 */
	public abstract void rebuild();
	
	public abstract boolean undoChanges();
	
	/**
	 * This Method can be used if it necessary to display more than one actor as
	 * list. (For example MixedAnswerPanel with more than one Actor)
	 * @param actorPointer the pointer delivered by <code>getInternalPointer</code> in <code>InterviewElement</code>
	 */
	public void setActorPointer(int actorPointer)
	{
		this.actorPointer = actorPointer;
	}
	
	/**
	 * Method used by StandardPanel to determin if the kind of special panel
	 * should be placed within a scroll pane or can be directly placed
	 * on the controller dialog
	 * 
	 * @return <code>true</code> if should be placed within a scroll pane <br/>
	 * <code>false</code> false otherwise
	 */
	public abstract boolean shouldBeScrollable();
	
	/**
	 * Validates the input of the user
	 * 
	 * @return <code>true</code> if the input of the user is correct (e.g all
	 *         fields all filled) <code>false</code> if the input is not correct
	 */
	public abstract boolean validateInput();
	
}
