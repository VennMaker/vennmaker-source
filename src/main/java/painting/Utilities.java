package painting;

import gui.VennMaker;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Vector;

import data.Akteur;
import data.Netzwerk;
import data.Relation;
import data.RelationMonitor;

public class Utilities
{
	public static void drawRelation(Relation r, Graphics2D g)
	{

	}

	public static void drawMovedRelations(Netzwerk n, Graphics2D g)
	{
		for (Relation r : RelationMonitor.getInstance().getMovedRelations(n))
			drawRelation(r, g);
	}

	public static void drawRelations(Netzwerk n, Graphics2D g)
	{
		RelationMonitor.getInstance().update();

		Vector<Akteur> allActors = new Vector<Akteur>();
		allActors.addAll(n.getAkteure());

		if (VennMaker.getInstance().getConfig().isEgoDisabled())
			allActors.remove(n.getEgo());

		/* Paint relations, which are locked and therefore not very visible */
		if (n.getMarkedActors() != null
				&& VennMaker.getInstance().getViewOfNetwork(n).isFilterActive())
		{
			for (Akteur a : n.getMarkedActors())
			{
				allActors.remove(a);
				for (Akteur b : allActors)
				{
					for (Relation r : RelationMonitor.getInstance()
							.getRelationsBetween(a, b, n))
					{
						n.addLockedRelation(r);
						drawRelation(r, g);
					}
				}
			}
		}

		Vector<Akteur> visitActors = new Vector<Akteur>();
		visitActors.addAll(allActors);

		/* loop through all actors without the last one */
		for (int i = 0; i < allActors.size() - 1; i++)
		{
			Akteur a = allActors.get(i);
			visitActors.remove(a);
			for (Akteur b : visitActors)
			{
				for (Relation r : RelationMonitor.getInstance()
						.getRelationsBetween(a, b, n))
				{
					drawRelation(r, g);
				}
			}
		}
	}

	public static void drawActor()
	{

	}

	public static void drawActors()
	{

	}

	public static void drawImage()
	{

	}

	public static void drawIcon()
	{

	}

	public static void drawSector()
	{

	}

	public static void drawSectors(Graphics g)
	{
		//VennMakerView.paintSektoren(VennMaker.getInstance().getProject()
		//		.getCurrentNetzwerk().getHintergrund(), g);
	}

	public static void drawCircle()
	{
		
	}

	public static void drawCircles(Graphics g)
	{
		//VennMakerView.paintCircles(VennMaker.getInstance().getProject()
		//		.getCurrentNetzwerk().getHintergrund(), g);
	}

	/**
	 * Berechnet den Schnittpunkt einer Linie mit einem Objekt. Die Linie soll
	 * dabei vom Punkt <code>start</code> gedacht bis zum Mittelpunkt von
	 * <code>destination</code> erfolgen. Dies ist notwendig bei gerichteten
	 * Kanten, da sonst die Pfeilspitze meist verdeckt ist.
	 * 
	 * @param end
	 *           Das Objekt auf das die Linie zeigen soll.
	 * @param startX
	 *           X-Koordinate: Der Punkt auf der Linie (um richtung zu berechnen)
	 * @param startY
	 *           Y-Koordinate: Der Punkt auf der Linie (um richtung zu berechnen)
	 * @return Der Schnittpunkt der Linie bzw. <code>null</code>, wenn
	 *         <code>destination</code> schon <code>null</code> ist.
	 * @version 0.6
	 */
	private static Point2D getIntersection(final Shape destination,
			final double startX, final double startY)
	{
		if (destination == null)
			return null;
		double midX = destination.getBounds2D().getCenterX();
		double midY = destination.getBounds2D().getCenterY();
		double dirX = startX - midX;
		double dirY = startY - midY;
		double dirL = 1.0 / Math.sqrt(dirX * dirX + dirY * dirY);
		double destR = Math.max(destination.getBounds2D().getWidth(), destination
				.getBounds2D().getHeight()) / 2.0;
		dirX *= (destR * dirL);
		dirY *= (destR * dirL);

		return new Point2D.Double(midX + dirX, midY + dirY);
	}
}
