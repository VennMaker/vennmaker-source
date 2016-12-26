package interview.panels.single;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import data.AttributeType;

/**
 * superclass for each panel which is used to receive an attribute value from
 * initializes the GridBagLayout and provides standard functionality like (getValue ...)
 * 
 * 
 */
public abstract class ValuePanel extends JPanel
{
	static final long	serialVersionUID	= 1L;
	
	AttributeType 		a;
	
	Object 				answer;
	
	GridBagLayout 		gbLayout;
	
	GridBagConstraints 	g;
	
	public ValuePanel(AttributeType a)
	{
		super();
		this.a = a;
		gbLayout = new GridBagLayout();
		this.setLayout(gbLayout);
		g = new GridBagConstraints();
	}
	
	/**
	 * returns the attribute type the panel is used for
	 */
	public AttributeType getAttributeType()
	{
		return a;
	}

	/**
	 * returns the selected / entered value
	 */
	public Object getValue()
	{
		return answer;
	}
	
	/**
	 * use this to set the Value of the panel 
	 * (as for initialization of already answered questions)
	 */
	public abstract void setValue(Object val);
	
	/**
	 * builds the panel (call before displaying)
	 */
	abstract public void build();
}
