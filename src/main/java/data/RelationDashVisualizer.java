/**
 * 
 */
package data;

import java.util.HashMap;
import java.util.Map;

/**
 * saves all dasharrays for the relationattributes
 * 
 * 
 * 
 */
public class RelationDashVisualizer extends Visualizer
{
	
	public static final float[] DEFAULT_DASHING = new float[] { 1.0f, 0.0f };
	
	/**
	 * the map, which contains all relationattributes and their corresponding
	 * dasharrays
	 */
	private Map<Object, float[]>	dashArrays	= new HashMap<Object, float[]>();

	/**
	 * returns the dasharray for the given relation in the given network
	 * 
	 * @param subject
	 *           wich relation
	 * @param network
	 *           in which network
	 * @return the corresponding dasharray
	 */
	public float[] getDashArray(AttributeSubject subject, Netzwerk network)
	{
		assert this.dashArrays != null;

		float[] i = this.dashArrays.get(subject.getAttributeValue(
				this.getAttributeType(), network));

		/* Wenn keine Linienart angegeben, dann normale durchgezogene Linie */
		if (i == null)
			i = DEFAULT_DASHING;

		return i;
	}

	/**
	 * gets a specific dasharray for the given attribute
	 * 
	 * @param value
	 *           the attribute
	 * @return the corresponding dasharray
	 */
	public float[] getDasharray(Object value)
	{
		assert this.dashArrays != null;
		if (this.dashArrays.get(value) == null)
			return DEFAULT_DASHING;
		return this.dashArrays.get(value);
	}

	/**
	 * all dasharrays and their corresponding relationattributes
	 * 
	 * @return a map with the relationattributes as keys and their dashing as
	 *         corresponding data
	 */
	public Map<Object, float[]> getDasharrays()
	{
		return this.dashArrays;
	}

	/**
	 * sets the map for the relationattributes
	 * 
	 * @param dashArrays
	 *           a map with the relationattributes as keys and their
	 *           corresponding dasharrays as data
	 */
	public void setDasharrays(Map<Object, float[]> dashArrays)
	{
		this.dashArrays = dashArrays;
	}

}
