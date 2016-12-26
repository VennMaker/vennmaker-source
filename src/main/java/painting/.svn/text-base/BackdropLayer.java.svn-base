package painting;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Currently only draws the gray border; if wanted, anything else can be put in
 * the background with this
 * 
 * 
 * 
 */
public class BackdropLayer extends PaintLayer implements PaintLayerInterface
{
	private static final long	serialVersionUID	= 1L;

	public BackdropLayer(){
		this.setBackground(Color.black);
	}
	
	@Override
	public void paintComponent(final Graphics graphics)
	{
		this.setOpaque(true);
		this.setBackground(Color.black);
		super.paintComponent(graphics);
	}
}
