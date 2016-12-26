package gui.configdialog.save;



/**
 * Objects of this class contain all necessary information to save
 * an actor label property
 * 
 * 
 *
 */
public class ActorLabelSaveElement extends SaveElement
{
	private int labelBehavior; 
	boolean[] labelArray,tooltipArray;
	
	public ActorLabelSaveElement(int labelBehavior, boolean[] labelArray, boolean[] tooltipArray)
	{
		this.labelBehavior = labelBehavior;
		this.labelArray = labelArray;
		this.tooltipArray = tooltipArray;
	}

	/**
	 * @return the labelBehavior
	 */
	public int getLabelBehavior()
	{
		return labelBehavior;
	}

	/**
	 * @return array containing label selection
	 */
	public boolean[] getSelectedLabel()
	{
		return labelArray;
	}

	/**
	 * @return array containing tooltip selection
	 */
	public boolean[] getSelectedTooltip()
	{
		return tooltipArray;
	}
}
