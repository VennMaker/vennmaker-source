package gui;

import java.io.Serializable;

import data.RelationColorData;
import data.RelationDashingData;
import data.RelationSizeData;

/**
 * Every <code>Projekt</code> has an object of this class. Objects of this class
 * show the <code>VennMakerView</code> how to paint the legend
 * 
 * 
 * 
 */
public class PaintLegendPolicy implements Serializable
{

	private static final long	serialVersionUID	= 8549119710710529862L;

	/**
	 * <code>true</code> if the dashing legend should be painted.
	 * <code>false</code> if not
	 */
	private boolean				paintDashing;

	/**
	 * <code>true</code> if the thickness legend should be painted.
	 * <code>false</code> if not
	 */
	private boolean				paintThickness;

	/**
	 * <code>true</code> if the color legend should be painted.
	 * <code>false</code> if not
	 */
	private boolean				paintColor;
	
	
	private RelationDashingData			dashingData;
	
	private RelationColorData 				colorData;
	
	private RelationSizeData				sizeData;

	/**
	 * Constructs a new <code>PaintLegendPolicy</code>
	 * 
	 * @param paintDashing
	 *           <code>true</code> if the dashing legend should be painted,
	 *           <code>false</code> if not
	 * 
	 * @param paintThickness
	 *           <code>true</code> if the thickness legend should be painted,
	 *           <code>false</code> if not
	 * @param paintColor
	 *           <code>true</code> if the color legend should be painted,
	 *           <code>false</code> if not
	 */
	public PaintLegendPolicy(boolean paintDashing, boolean paintThickness,
			boolean paintColor)
	{
		this.paintDashing = paintDashing;
		this.paintThickness = paintThickness;
		this.paintColor = paintColor;
	}

	/**
	 * Returns <code>true</code> if the dashing legend should be painted,
	 * <code>false</code> if not
	 * 
	 * @return <code>true</code> if the dashing legend should be painted,
	 *         <code>false</code> if not
	 */
	public boolean paintDashing()
	{
		return paintDashing;
	}

	/**
	 * Returns <code>true</code> if the thickness legend should be painted,
	 * <code>false</code> if not
	 * 
	 * @return <code>true</code> if the thickness legend should be painted,
	 *         <code>false</code> if not
	 */
	public boolean paintThickness()
	{
		return paintThickness;
	}

	/**
	 * Returns <code>true</code> if the color legend should be painted,
	 * <code>false</code> if not
	 * 
	 * @return
	 */
	public boolean paintColor()
	{
		return paintColor;
	}

	/**
	 * Set <code>true</code> if the dashing legend should be painted,
	 * <code>false</code> if not
	 * 
	 * @param paintDashing
	 *           <code>true</code> if the dashing legend should be painted,
	 *           <code>false</code> if not
	 */
	public void setPaintDashing(boolean paintDashing)
	{
		this.paintDashing = paintDashing;
	}

	/**
	 * Set <code>true</code> if the thickness legend should be painted,
	 * <code>false</code> if not
	 * 
	 * @param paintThickness
	 *           <code>true</code> if the thickness legend should be painted,
	 *           <code>false</code> if not
	 */
	public void setPaintThickness(boolean paintThickness)
	{
		this.paintThickness = paintThickness;
	}

	/**
	 * Set <code>true</code> if the color legend should be painted,
	 * <code>false</code> if not
	 * 
	 * @param paintColor
	 *           <code>true</code> if the color legend should be painted,
	 *           <code>false</code> if not
	 */
	public void setPaintColor(boolean paintColor)
	{
		this.paintColor = paintColor;
	}

	public RelationDashingData getDashingData()
	{
		return dashingData;
	}

	public void setDashingData(RelationDashingData dashingData)
	{
		this.dashingData = dashingData;
	}

	public RelationColorData getColorData()
	{
		return colorData;
	}

	public void setColorData(RelationColorData colorData)
	{
		this.colorData = colorData;
	}

	public RelationSizeData getSizeData()
	{
		return sizeData;
	}

	public void setSizeData(RelationSizeData sizeData)
	{
		this.sizeData = sizeData;
	}
}
