package painting;

import java.awt.Dimension;

import javax.swing.JPanel;

public class PaintLayer extends JPanel
{
	private static final long	serialVersionUID	= 1L;

	private byte					alpha					= (byte) 255;

	/**
	 * Controls the quality of this layer, according to:
	 * http://docs.oracle.com/javase/tutorial/2d/advanced/quality.html
	 */
	private int						quality				= 3;

	
	public void setTransparency(byte alpha)
	{
		this.alpha = alpha;
	}

	public byte getTransparency()
	{
		return this.alpha;
	}

	@Override
	public Dimension getPreferredSize(){
		return new Dimension(this.getWidth(), this.getHeight());
	}
}
