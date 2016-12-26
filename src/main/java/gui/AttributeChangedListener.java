/**
 * 
 */
package gui;

import data.AttributeType;

/**
 * 
 *
 * Diese Listener werden vom ConfigDialogTempCache informiert 
 * wenn sich einzelne Attribute aendern.
 */
public interface AttributeChangedListener
{
	public void attributeChanged(AttributeType oldA, AttributeType newA);
}
