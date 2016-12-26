package gui.configdialog.save;

public class ZoomSaveElement extends SaveElement
{
	private float factor;
	
	public ZoomSaveElement(float factor)
	{
		this.factor = factor;
	}
	
	public float getZoomFactor()
	{
		return this.factor;
	}
}
