package interview.configuration.attributesSelection;

import interview.UpdateListener;
import interview.elements.StandardElement;

import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import data.AttributeType;

/**
 * 
 * Returns all Questions and Attributes entered in the configuration mode of an
 * interview element
 * 
 * 
 */
public interface AttributeSelector 
{
	/**
	 * Initialize an AttributeSelector abject with all AttributeTypes possible for selection
	 */
	public void init(List<AttributeType> aTypes);
	
	/**
	 * Updates the panel (to react on changes) 
	 */
	public void updatePanel(List<AttributeType> aTypesNew);
	
	/**
	 * returns an Map containing for each AttributeType the choosen Question
	 */
	public Map<String,AttributeType> getAttributesAndQuestions();
	
	/**
	 * Sets an Map containing the choosen question for each AttributeType
	 * @param attributesAndQuestions
	 */
	public void setAttributesAndQuestions(Map<String,AttributeType> attributesAndQuestions);
	
	/**
	 * returns the panel for Configuration of the Questions and AttributeTypes
	 */
	public JPanel getConfigurationPanel();
	
	/**
	 * returns true if only Single Attribute should be selected, false elsewise
	 */
	public boolean isSingleAttributeSelector();
	
	/**
	 * adds a listener, which will be called after changes
	 */
	public void addUpdateListener(UpdateListener l);
	
	/**
	 * set the parent element of this panel (for callback on updates)
	 */
	public void setParent(StandardElement parent);
	
	/**
	 * ask before build Dialog to verify that list of attributes is not empty
	 */
	abstract public boolean hasAttributes();
}
