package gui.configdialog.save;

/**
 * This element saves the ratio for VennMaker
 * 
 *
 */
public class RatioSaveElement extends SaveElement
{
	private String ratio;
	
	public RatioSaveElement(String ratio)
	{
		this.ratio = ratio;
	}
	
	/**
	 * Returns the ratio
	 * @return the ratio for VennMaker
	 */
	public String getRatio()
	{
		return this.ratio;
	}
}
