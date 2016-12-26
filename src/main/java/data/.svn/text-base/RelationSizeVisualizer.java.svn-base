/**
 * 
 */
package data;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 *         relations
 */
public class RelationSizeVisualizer extends Visualizer
{
	
	public static final int DEFAULT_SIZE = 5;
	
	private Map<Object, Integer>	sizes	= new HashMap<Object, Integer>();

	public int getSize(AttributeSubject subject, Netzwerk network)
	{
		assert this.sizes != null;
		int i = 0;

		if (this.sizes.get(subject.getAttributeValue(this.getAttributeType(),
				network)) != null)
			i = this.sizes.get(subject.getAttributeValue(this.getAttributeType(),
					network));
		else
			this.sizes.put(subject, 1);

		/* Wenn keine Groesse angegeben, dann Liniendicke = 5 */
		if (i <= 0)
			i = DEFAULT_SIZE;

		return i;
	}

	/**
	 * returns the size of the given attribute
	 * 
	 * @param value
	 *           the relation in question
	 * @return the corresponding size
	 */
	public int getSize(Object value)
	{
		assert this.sizes != null;
		if (this.sizes.get(value) == null)
			return DEFAULT_SIZE;
		return this.sizes.get(value);
	}

	/**
	 * all saved sizes
	 * 
	 * @return the map which contains the attributes an the corresponding sizes
	 */
	public Map<Object, Integer> getSizes()
	{
		return this.sizes;
	}

	/**
	 * sets all sizes for the different relationattributes
	 * 
	 * @param sizes
	 *           a map containing the relationattributes and the corresponding
	 *           sizes
	 */
	public void setSizes(Map<Object, Integer> sizes)
	{
		this.sizes = sizes;
	}

}
