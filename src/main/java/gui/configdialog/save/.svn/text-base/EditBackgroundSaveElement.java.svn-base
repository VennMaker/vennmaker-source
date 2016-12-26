package gui.configdialog.save;

import java.awt.Color;
import java.awt.Rectangle;

public class EditBackgroundSaveElement extends SaveElement
{
	private Color backgroundColor;
	private int transparency;
	
	private String imageData;
	private String imageName;
	
	private Rectangle imageSelection;
	
	public EditBackgroundSaveElement(Color backgroundColor, int transparency)
	{
		this.backgroundColor = backgroundColor;
		this.transparency = transparency;
	}
	
	
	public EditBackgroundSaveElement(Color backgroundColor, int transparency, String imageData, String imageName, Rectangle imageSelection)
	{
		this.backgroundColor = backgroundColor;
		this.transparency = transparency;
		this.imageData = imageData;
		this.imageName = imageName;
		this.imageSelection = imageSelection;
	}

	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor()
	{
		return backgroundColor;
	}

	/**
	 * @return the transparency
	 */
	public int getTransparency()
	{
		return transparency;
	}
	
	/**
	 * 
	 * @return image data in base64 encoding
	 */
	public String getImageData()
	{
		return imageData;
	}
	
	/**
	 * 
	 * @return name of the backgroundimage
	 */
	public String getImageName()
	{
		return imageName;
	}

	/**
	 * Returns the image selection made by the user for
	 * the background image
	 * @return the image selection made by the user for
	 * the background image
	 */
	public Rectangle getImageSelection()
	{
		return imageSelection;
	}
}
