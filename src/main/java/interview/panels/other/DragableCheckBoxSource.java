package interview.panels.other;

import java.util.Map;

import javax.swing.JCheckBox;

/**
 * Every class that wants dragable <code>JCheckBoxes</code> should implement this interface
 * 
 *
 */
public interface DragableCheckBoxSource
{
	/**
	 * Returns a <code>Map</code> containing all D&D-Checkboxes 
	 * @return a <code>Map</code> containing all D&D-Checkboxes 
	 */
	public Map<String, JCheckBox> getAttributeCheckBoxes();
	
	/**
	 * Switches data between two <code>JCheckBoxes</code>
	 * @param source the data of the <code>JCheckBox</code> that is the source
	 * @param target the data of the <code>JCheckBox</code> that is the target
	 */
	public void switchCheckBoxValues(Object source, Object target);
	
	/**
	 * Callback to update all <code>JCheckBoxes</code>
	 */
	public void updateCheckBoxes();
}
