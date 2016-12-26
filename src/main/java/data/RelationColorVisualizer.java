/**
 * 
 */
package data;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * class which saves all colors for the corresponding relationattributes
 * 
 * 
 */
public class RelationColorVisualizer extends Visualizer
{

	public static final Color	DEFAULT_COLOR	= Color.BLACK;

	/* contains the attributes and their corresponding colors */
	private Map<Object, Color>	colors = new HashMap<Object, Color>();

	/**
	 * returns the color for a relationattribute in the given network
	 * 
	 * @param subject
	 *           the relation, which has a specific attribute
	 * @param network
	 *           the network of the relation
	 * @return the color of the relation for the given network and the
	 *         corresponding attribute
	 */
	public Color getColor(AttributeSubject subject, Netzwerk network)
	{
		assert this.colors != null;
				
		Color i = this.colors.get(subject.getAttributeValue(
				this.getAttributeType(), network));

		// Wenn keine Farbe angegeben, dann gib schwarz zurueck
		if (i == null)
			i = DEFAULT_COLOR;

		return i;
	}

	/**
	 * checks, which color is associated with the given attribute
	 * 
	 * @param value
	 *           the attribute
	 * @return the corresponding color for the given attribute
	 */
	public Color getColor(Object value)
	{
		assert this.colors != null;
		if (!this.colors.containsKey(value))
			this.colors.put(value, DEFAULT_COLOR);
		return this.colors.get(value);
	}

	/**
	 * returns all attributes and their corresponding colors
	 * 
	 * @return all attributes and their corresponding colors
	 */
	public Map<Object, Color> getColors()
	{
		return this.colors;
	}

	/**
	 * sets the attributes and their colors
	 * 
	 * @param colors
	 *           a map with the attributes as keys and the colors as data
	 */
	public void setColors(Map<Object, Color> colors)
	{		
		this.colors = colors;
	}

}
