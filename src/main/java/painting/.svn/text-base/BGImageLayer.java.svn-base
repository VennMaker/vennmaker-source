package painting;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class BGImageLayer extends PaintLayer implements PaintLayerInterface
{
	private static final long	serialVersionUID	= 1L;

	private Color					backgroundColor	= Color.white;

	private BufferedImage		backgroundImage;

	@Override
	public void paintComponent(final Graphics graphics)
	{
		this.setOpaque(true);
		this.setBackground(backgroundColor);
		super.paintComponent(graphics);
	}
}
