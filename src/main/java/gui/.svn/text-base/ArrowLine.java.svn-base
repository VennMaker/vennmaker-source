/**
 * 
 */
package gui;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Diese Klasse implementiert eine Linie mit einer abschliessenden Pfeilspitze.
 * 
 * 
 * 
 */
public class ArrowLine implements Shape
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Die komplette Linie als Shape.
	 */
	private Path2D					line;

	/**
	 * Erzeugt eine Linie mit Pfeilspitze von (x1,y1) nach (x2,y2).
	 * Dieser Shape kann zwar auch gefüllt werden, allerdings bedeutet dies
	 * nur eine Füllung der Pfeilspitze - der Rest bleibt nicht sichtbar.
	 * 
	 * Daher sollte immer <code>Graphics2D.draw(...)</code> benutzt werden.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public ArrowLine(final double x1, final double y1, final double x2,
			final double y2)
	{
		Path2D arrow = new Path2D.Double();
		arrow.moveTo(-0.5, 1.0);
		arrow.lineTo(0.0, 0.0);
		arrow.lineTo(0.5, 1.0);

		final AffineTransform lArrowTransform = new AffineTransform();
		lArrowTransform.translate(x2, y2);
		lArrowTransform.rotate(-Math.atan2(x1 - x2, y1 - y2));
		lArrowTransform.scale(8.0, 8.0);
		arrow.transform(lArrowTransform);

		Line2D baseLine = new Line2D.Double(x1, y1, x2, y2);

		line = new Path2D.Double();
		line.append(baseLine, false);
		line.append(arrow, false);
	}

	
	/* @see java.awt.Shape#getBounds2D()
	 */
	public Rectangle2D getBounds2D()
	{
		return line.getBounds2D();
	}

	/* @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
	 */
	public PathIterator getPathIterator(AffineTransform at)
	{
		return line.getPathIterator(at);
	}

	
	/* @see java.awt.geom.Path2D#contains(double, double, double, double)
	 */
	public final boolean contains(double x, double y, double w, double h)
	{
		return line.contains(x, y, w, h);
	}

	/* @see java.awt.geom.Path2D#contains(double, double)
	 */
	public final boolean contains(double x, double y)
	{
		return line.contains(x, y);
	}

	/* @see java.awt.geom.Path2D#contains(java.awt.geom.Point2D)
	 */
	public final boolean contains(Point2D p)
	{
		return line.contains(p);
	}

	/* @see java.awt.geom.Path2D#contains(java.awt.geom.Rectangle2D)
	 */
	public final boolean contains(Rectangle2D r)
	{
		return line.contains(r);
	}

	/* @see java.awt.geom.Path2D#getBounds()
	 */
	public final Rectangle getBounds()
	{
		return line.getBounds();
	}

	/* @see java.awt.geom.Path2D#getPathIterator(java.awt.geom.AffineTransform, double)
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness)
	{
		return line.getPathIterator(at, flatness);
	}

	/*
	 *  @see java.awt.geom.Path2D#intersects(double, double, double, double)
	 */
	public final boolean intersects(double x, double y, double w, double h)
	{
		return line.intersects(x, y, w, h);
	}

	/*
	 * @see java.awt.geom.Path2D#intersects(java.awt.geom.Rectangle2D)
	 */
	public final boolean intersects(Rectangle2D r)
	{
		return line.intersects(r);
	}


}
