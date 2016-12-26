package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * 
 * Makes it possible to paint a rotated label under a line (line is not be
 * drawn). Depending on Background Color, the Label will be drawn in a filled
 * Rectangle.
 * 
 * 
 * 
 */
public class DrawLineLabel
{
	private Point2D			lineStart;

	private String[]			lines;

	private Color				fg, bg;

	private int					yOffset			= 0;

	private final int			PADDING			= 2;

	private double				lineWidth, lineHeight;

	public static final int	STRAIGHT_MAX	= 30;	// every angle below is seen
																	// as straight up / down
																	// (text and box will be put
																	// in another angle)

	/**
	 * @param lineStart
	 *           - Point where the line starts
	 * @param lineEnd
	 *           - Point where the line ends
	 * @param text
	 *           - text with "\n"-char to separate multiple lines
	 * @param fg
	 *           - Foreground Color (Text)
	 * @param bg
	 *           - Background Color (Box)
	 */
	public DrawLineLabel(Point2D lineStart, Point2D lineEnd, String text,
			Color fg, Color bg)
	{
		this.lineStart = lineStart;
		this.lines = text.split("\n");
		this.bg = bg;
		this.fg = fg;

		this.lineWidth = lineEnd.getX() - lineStart.getX();
		this.lineHeight = lineEnd.getY() - lineStart.getY();
	}

	private Rectangle calculateBox(Graphics2D g2d)
	{
		FontMetrics fm = g2d.getFontMetrics();

		int max_sw = 0; // biggest string width
		int height = PADDING;
		int fontSize = g2d.getFont().getSize();

		for (String line : lines)
		{
			int w = fm.stringWidth(line);
			max_sw = Math.max(w, max_sw);

			height += fontSize + PADDING;
		}
		double boxWidth = max_sw + 2 * PADDING; // box width = padding + maximal
																// string + padding
		double boxHeight = height;

		double boxX = lineStart.getX() + (lineWidth / 2.0); // RAW start point of
																				// box is the center
																				// of the line
		double boxY = lineStart.getY() + (lineHeight / 2.0);

		return new Rectangle((int) Math.round(boxX), (int) Math.round(boxY),
				(int) boxWidth, (int) boxHeight);
	}

	private AffineTransform calculateTransform(Rectangle box)
	{
		/* angle of the line */
		double angle = Math.atan(lineWidth / lineHeight);

		double testangle = Math.abs(Math.toDegrees(angle));
		/*
		 * if angle in degrees is < STRAIGHT_MAX the line is seen as straight
		 * up/down
		 */
		boolean almost0 = (testangle <= STRAIGHT_MAX);

		if (!almost0)
			angle -= angle < 0 ? -Math.PI / 2.0 : +Math.PI / 2.0; // if the line is
																					// not straight
																					// up

		AffineTransform trans = new AffineTransform();
		trans.concatenate(AffineTransform.getTranslateInstance(box.getX(),
				box.getY()));
		trans.concatenate(AffineTransform.getRotateInstance(-angle));
		trans.concatenate(AffineTransform.getTranslateInstance(-box.getCenterX(),
				-box.getCenterY()));

		/* if the line is straight up or down the x of the box has to be corrected */
		if (almost0)
			box.x -= Math.abs(box.width / 2.0) + 2 * PADDING; //

		/* add yOffset and half of box.height to box.y */
		box.y += (box.height / 2.0) + 2 * PADDING + yOffset;

		return trans;
	}

	/**
	 * Method which calculates the Label box without drawing it to return the
	 * correct Dimensions of it.
	 * 
	 * @param g2d
	 *           - Graphics to draw on
	 * @return Rectangle <0,0,labelbox_width, labelbox_height>
	 */
	public Rectangle getLabelDimensions(Graphics2D g2d)
	{
		Rectangle rect = calculateBox(g2d);
		// calculateTransform(rect);

		return new Rectangle(0, 0, (int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Calculates and paints the line label + box and returns the box size to
	 * calculate an y offset if another label should be drawn under this line
	 * 
	 * @param g2d
	 *           - Graphics to draw on
	 * @param yOffset
	 *           - Offset to add to Box/Text Position (needed if multiple labels
	 *           should be drawn under one line)
	 */
	public void draw(Graphics2D g2d, int yOffset)
	{
		this.yOffset = yOffset;
		Rectangle rect = calculateBox(g2d);
		AffineTransform old_trans = g2d.getTransform();
		AffineTransform new_trans = calculateTransform(rect);

		/*
		 * don't reset transformation and only apply the new one. Apply
		 * oldTransformation + newTransformation to respect changes done by
		 * old_trans
		 */
		new_trans.preConcatenate(old_trans);
		g2d.setTransform(new_trans);

//		g2d.setColor(bg);
//		g2d.fill(rect);
		g2d.setColor(this.fg);
		g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN));
		
		int fontSize = g2d.getFont().getSize();
		int y = rect.y + fontSize + PADDING; // y of string (down left of string)
															// = box.y + padding + fontSize
		int x = rect.x + PADDING; // x of string begins PADDING pixels in box

		for (String line : lines)
		{
			g2d.drawString(line, x, y);
			y += fontSize + PADDING;
		}

		g2d.setTransform(old_trans);
	}
}
