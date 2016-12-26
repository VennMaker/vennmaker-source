/**
 * 
 */
package gui;

/**
 * 
 */

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;

public class ActorPie
{
	private Area	sector;

	private Area	font;

	private double	x, y, r1, r2, rot, off;

	private String	label;

	private int		fontsize;

	/**
	 * Konstruiert einen neuen Sektor mit Namenslabel.
	 * 
	 * @param x
	 *           Die x-position (Zentrum des imaginären Kreises, Sternpunkt)
	 * @param y
	 *           Die y-position (Zentrum des imaginären Kreises, Sternpunkt)
	 * @param r1
	 *           Der innere Radius (bis dorthin wird nichts gezeichnet)
	 * @param r2
	 *           Der äußere Radius (danach wird nichts gezeichnet)
	 * @param rot
	 *           Der Öffnungswinkel (in GRAD)
	 * @param off
	 *           Der Rotationswinkel (in GRAD)
	 * @param label
	 *           Das Label, darf nicht <code>null</code> sein.
	 * @param fontsize
	 *           Die Schriftgröße in Punkten
	 */
	public ActorPie(double x, double y, double r1, double r2,
			double rot, double off, String label, int fontsize)
	{
		this.x = x;
		this.y = y;
		this.r1 = r1;
		this.r2 = r2;
		this.rot = rot;
		this.off = off;
		assert (label != null);
		this.label = label;
		this.fontsize = fontsize;
		createShape();
	}

	/**
	 * Erzegt das graphische Objekt neu (nach Änderungen an Parametern)
	 */
	private void createShape()
	{
		while (off > 360.0)
			off -= 360.0;
		while (off < 0.0)
			off += 360.0;

		Arc2D inner = new Arc2D.Double(x - r1, y - r1, r1 * 2, r1 * 2, 0, rot,
				Arc2D.PIE);
		Arc2D outer = new Arc2D.Double(x - r2, y - r2, r2 * 2, r2 * 2, 0, rot,
				Arc2D.PIE);
		sector = new Area(outer);
		sector.subtract(new Area(inner));
		/*Font f = Font.decode("Arial-Bold-" + fontsize);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		GlyphVector v = f.createGlyphVector(frc, label);
		Shape outline = v.getOutline();

		

		outline = v.getOutline(
				(int) (x + r2 - outline.getBounds2D().getWidth() - 5),
				(int) (y - 2));
		if (off <= 270 && off >= 90.0)
		{
			final AffineTransform rotate = new AffineTransform();
			final Rectangle2D bounds = outline.getBounds();
			rotate.translate(bounds.getCenterX(), bounds.getCenterY());
			rotate.rotate(Math.PI);
			rotate.translate(-bounds.getCenterX(), -bounds.getCenterY());
			outline = rotate.createTransformedShape(outline);
		}
		font = new Area(outline);
		*/
	}

	public void paintComponent(Graphics2D g)
	{

		AffineTransform f = g.getTransform();
		g.translate(x, y);
		g.rotate(((360.0 - off) / 360.0) * 2 * Math.PI);
		g.translate(-x, -y);
		g.fill(sector);
		/*g.setColor(g.getColor().darker().darker());
		g.fill(font);
		*/
		g.setTransform(f);
	}

	/**
	 * @return the sector
	 */
	public final Area getSector()
	{
		return sector;
	}

	/**
	 * @param sector
	 *           the sector to set
	 */
	public final void setSector(Area sector)
	{
		this.sector = sector;
		createShape();
	}

	/**
	 * @return the x
	 */
	public final double getX()
	{
		return x;
	}

	/**
	 * @param x
	 *           the x to set
	 */
	public final void setX(double x)
	{
		this.x = x;
		createShape();
	}

	/**
	 * @return the y
	 */
	public final double getY()
	{
		return y;
	}

	/**
	 * @param y
	 *           the y to set
	 */
	public final void setY(double y)
	{
		this.y = y;
		createShape();
	}

	/**
	 * @return the r1
	 */
	public final double getR1()
	{
		return r1;
	}

	/**
	 * @param r1
	 *           the r1 to set
	 */
	public final void setR1(double r1)
	{
		this.r1 = r1;
		createShape();
	}

	/**
	 * @return the r2
	 */
	public final double getR2()
	{
		return r2;
	}

	/**
	 * @param r2
	 *           the r2 to set
	 */
	public final void setR2(double r2)
	{
		this.r2 = r2;
		createShape();
	}

	/**
	 * @return the rot
	 */
	public final double getRot()
	{
		return rot;
	}

	/**
	 * @param rot
	 *           the rot to set
	 */
	public final void setRot(double rot)
	{
		this.rot = rot;
		createShape();
	}

	/**
	 * @return the off
	 */
	public final double getOff()
	{
		return off;
	}

	/**
	 * @param off
	 *           the off to set
	 */
	public final void setOff(double off)
	{
		this.off = off;
		createShape();
	}

	/**
	 * @return the label
	 */
	public final String getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *           the label to set
	 */
	public final void setLabel(String label)
	{
		this.label = label;
		createShape();
	}
}
