package data;

import java.util.Map;
import java.util.Vector;

/**
 * Objects of this class contain the data for the relation dashing, definied in
 * <code>CDialogRelationDashing</code>
 * 
 * 
 * 
 */
public class RelationDashingData
{
	/**
	 * The definied dashing
	 */
	private Map<String, Vector<float[]>>	dashing;

	/**
	 * The values belonging to the dashing
	 */
	private Map<String, Vector<String>>		values;

	/**
	 * Creates a new object of <code>RelationDashingData</code>
	 * 
	 * @param dashing
	 * @param values
	 */
	public RelationDashingData(Map<String, Vector<float[]>> dashing,
			Map<String, Vector<String>> values)
	{
		this.dashing = dashing;
		this.values = values;
	}

	/**
	 * Returns the definied dashing
	 * 
	 * @return the definied dashing
	 */
	public Map<String, Vector<float[]>> getDashing()
	{
		return dashing;
	}

	/**
	 * Sets the definied dashing
	 * 
	 * @param dashing
	 *           the definied dashing
	 */
	public void setDashing(Map<String, Vector<float[]>> dashing)
	{
		this.dashing = dashing;
	}

	/**
	 * Returns the values belonging to the dashing
	 * 
	 * @return the values belonging to the dashing
	 */
	public Map<String, Vector<String>> getValues()
	{
		return values;
	}

	/**
	 * Sets the values belonging to the dashing
	 * 
	 * @param values
	 *           the values belonging to the dashing
	 */
	public void setValues(Map<String, Vector<String>> values)
	{
		this.values = values;
	}

}
