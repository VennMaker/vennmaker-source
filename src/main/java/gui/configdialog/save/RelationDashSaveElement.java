package gui.configdialog.save;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import data.AttributeType;
import data.Netzwerk;


/**
 * Objects of this class contain all necessary information to save
 * an RelationDash element
 * 
 * 
 *
 */
public class RelationDashSaveElement extends SaveElement
{
	private Netzwerk 						net;
	
	private HashMap<String, AttributeType> 		comboSelected;
	
	/**
	 * Backwarts compatiblity for older templates
	 */
	private HashMap<String, Vector<float[]>> 	predefinedDashing;
	
	/**
	 * Backwarts compatiblity for older templates
	 */
	private HashMap<String, Vector<String>>		predefinedValues;
	
	/**
	 * The dashing definied in <code>CDialogRelationDash</code>
	 */
	private Map<String, Map<String, Vector<float[]>>> dashing;
	
	/**
	 * The predefnied values for the given dashing
	 */
	private Map<String, Map<String, Vector<String>>> predefValues;
	
	public RelationDashSaveElement(Netzwerk net,
			HashMap<String, AttributeType> comboSelected,
			Map<String, Map<String, Vector<float[]>>> dashing,
			Map<String, Map<String, Vector<String>>> predefValues)
	{
		this.net = net;
		this.comboSelected = comboSelected;
		this.dashing = dashing;
		this.predefValues = predefValues;
	}
	
	public RelationDashSaveElement(
			Map<String, Map<String, Vector<float[]>>> dashing,
			Map<String, Map<String, Vector<String>>> predefValues, HashMap<String, AttributeType> comboSelected,Netzwerk net)
	{
		this.dashing = dashing;
		this.predefValues = predefValues;
		this.comboSelected = comboSelected;
		this.net = net;
	}

	/**
	 * @return Netzwerk 
	 */
	public Netzwerk getNet()
	{
		return net;
	}
	
	/**
	 * @return Combobox Selection 
	 */
	public HashMap<String, AttributeType> getComboSelected()
	{
		return comboSelected;
	}
	
	/**
	 * @return Predefined Dashing 
	 */
	public HashMap<String, Vector<float[]>> getOldPredefinedDashing()
	{
		return predefinedDashing;
	}
	
	/**
	 * @return Predefined Values 
	 */
	public HashMap<String, Vector<String>> getOldPredefinedValues()
	{
		return predefinedValues;
	}

	public Map<String, Map<String, Vector<float[]>>> getDashing()
	{
		return dashing;
	}

	public void setDashing(Map<String, Map<String, Vector<float[]>>> dashing)
	{
		this.dashing = dashing;
	}

	public Map<String, Map<String, Vector<String>>> getPredefValues()
	{
		return predefValues;
	}

	public void setPredefValues(
			Map<String, Map<String, Vector<String>>> predefValues)
	{
		this.predefValues = predefValues;
	}
}
