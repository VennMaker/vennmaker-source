/**
 * 
 */
package gui.configdialog;

import gui.LabelledPie;
import gui.configdialog.elements.CDialogSector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import data.BackgroundInfo;

/**
 * 
 * 
 */
public class SectorPreview extends JPanel
{
	/**
	 * 
	 */
	private static final long				serialVersionUID	= 1L;

	/**
	 * Information about the Sectors & Circles
	 */
	private BackgroundInfo					bginfo;

	private List<FakeSectorInfo>			tempSectors			= new ArrayList<FakeSectorInfo>();

	/**
	 * Die Anfasser, um die Sektoren groesser zu ziehen
	 */
	private List<Point>						anfasser				= new ArrayList<Point>();

	/**
	 * Groesse der Anfasser in px;
	 */
	private static int						TOUCHPOINTSIZE		= 10;

	private static int						RADIUS				= 140;

	private static int						INNERRADIUS			= 15;

	/**
	 * the mouseListener for the Canvas
	 */
	private SectorPreviewMouseListener	mouseController;

	/**
	 * Antialiasing on(true)/off(false)
	 */
	private boolean							highDefRendering	= true;

	private Point								center;

	private int									activeSector		= -1;

	private Point								activeTouchPoint	= new Point();

	private CDialogSector					parentConfig;

	private Graphics2D						g;

	private double								alpha;

	/**
	 * wurden die letzten aenderungen an sektorennamen hier vorgenommen?
	 */
	public boolean								lastchanged			= false;

	/**
	 * Constructor with the needed information about circles and sectors.
	 * enabling the mouselistener
	 * 
	 * @param backgroundinfo
	 *           information about circles and sectors
	 */
	public SectorPreview(BackgroundInfo backgroundinfo,
			CDialogSector parentConfig)
	{
		bginfo = backgroundinfo.clone();
		alpha = bginfo.alpha;
		this.parentConfig = parentConfig;

		this.mouseController = new SectorPreviewMouseListener(this);

		/**
		 * Kopien der Sektoren, um nicht die falschen Werte zu ueberschreiben
		 */
		tempSectors.clear();

		if (bginfo.getNumSectors() > 0)
		{
			for (int i = 0; i < bginfo.getNumSectors(); i++)
			{
				FakeSectorInfo temp = new FakeSectorInfo();
				temp.width = bginfo.getSector(i).width;
				temp.off = bginfo.getSector(i).off;
				temp.sectorColor = bginfo.getSector(i).sectorColor;
				temp.label = bginfo.getSector(i).label;
				tempSectors.add(temp);
			}
		}
		else
		{
			int numSectors = parentConfig.getNumSectors();
			for (int i = 0; i < numSectors; i++)
			{
				FakeSectorInfo tempSInfo = new FakeSectorInfo();
				tempSInfo.width = 1.0 / numSectors;
				tempSInfo.off = 0 + i * 1.0 / numSectors;
				tempSInfo.sectorColor = parentConfig.getSectorColor(i);
				tempSInfo.label = parentConfig.getSectorLabel(i);
				tempSectors.add(tempSInfo);
			}
		}

		// Add mouse interaction
		this.addMouseListener(this.mouseController);
		this.addMouseMotionListener(this.mouseController);
		this.addMouseWheelListener(this.mouseController);

		Dimension d = getSize();
		center = new Point(d.width / 2, d.height / 2);
	}

	public void paint(Graphics graphics)
	{
		graphics.setColor(this.getBackground());
		Dimension d = getSize();
		graphics.fillRect(0, 0, d.width, d.height);
		center = new Point(d.width / 2, d.height / 2);
		if (tempSectors.size() > 0)
		{
			g = (Graphics2D) graphics.create();

			// High Quality Rendering and Transparency
			if (highDefRendering)
			{
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				/**
				 * Transparenz wird nichtmehr benoetigt, da transparenz durch festen
				 * weisswert simuliert wird
				 * g.setComposite(AlphaComposite.getInstance(AlphaComposite
				 * .SRC_OVER, (float) bginfo.alpha));
				 */
			}

			// Anfang jedes Sektors wird durch totalWidth festgelegt
			double totalWidth = 0.0;

			anfasser.clear();

			for (int i = 0; i < tempSectors.size(); ++i)
			{
				tempSectors.get(i).off = totalWidth;
				LabelledPie sectorPie = new LabelledPie(center.getX(),
						center.getY(), INNERRADIUS, RADIUS,
						360.0 * tempSectors.get(i).width,
						360.0 * tempSectors.get(i).off, tempSectors.get(i).label, 12);
				g.setColor(new Color(new Double(tempSectors.get(i).sectorColor
						.getRed() * alpha + 250 * (1.0 - alpha)).intValue(),
						new Double(tempSectors.get(i).sectorColor.getGreen() * alpha
								+ 250 * (1.0 - alpha)).intValue(), new Double(
								tempSectors.get(i).sectorColor.getBlue() * alpha + 250
										* (1.0 - alpha)).intValue()));
				sectorPie.paintComponent(g);
				totalWidth += tempSectors.get(i).width;

				// "Anfasser" speichern
				if (i < tempSectors.size() - 1)
					anfasser.add(new Point((int) (Math.cos(Math
							.toRadians(360.0 * totalWidth)) * RADIUS + center.getX()),
							(int) (Math.sin(Math.toRadians(360.0 * totalWidth))
									* -RADIUS + center.getY())));
			}

			parentConfig.getNet().getHintergrund()
					.setTemporarySectors(tempSectors);

			for (Point p : anfasser)
			{
				if (!p.equals(activeTouchPoint))
					g.drawRect((int) p.getX() - TOUCHPOINTSIZE / 2, (int) p.getY()
							- TOUCHPOINTSIZE / 2, TOUCHPOINTSIZE, TOUCHPOINTSIZE);
				g.drawOval((int) p.getX() - TOUCHPOINTSIZE / 2, (int) p.getY()
						- TOUCHPOINTSIZE / 2, TOUCHPOINTSIZE, TOUCHPOINTSIZE);
			}

		}

	}

	/**
	 * enable antialiasing (and transparency)
	 */
	public void enableHighDefRendering()
	{
		highDefRendering = true;
		parentConfig.updateSectorInfos();
	}

	/**
	 * disable antialiasing (and transparency)
	 */
	public void disableHighDefRendering()
	{
		highDefRendering = false;
	}

	/**
	 * Enlarge sector i by 5%
	 * 
	 * @param i
	 */
	public void enlargeSector(int i)
	{
		/*
		 * if (i > -1) { tempSectors.get(i).width += 0.05; tempSectors.get(i +
		 * 1).width -= 0.05; repaint(); }
		 */
	}

	/**
	 * Shrink sector i by 5%
	 * 
	 * @param i
	 */
	public void shrinkSector(int i)
	{
		/*
		 * if (i > -1) { tempSectors.get(i).width -= 0.05; tempSectors.get(i +
		 * 1).width += 0.05; repaint(); }
		 */
	}

	/**
	 * get the sector at position x, y
	 * 
	 * @param x
	 *           Position.x
	 * @param y
	 *           Position.y
	 * @return sectorIndex, -1 if non-existent
	 */
	public int getSector(int x, int y)
	{
		x = x - (int) center.getX();
		y = y - (int) center.getY();
		double currentRad = Math.atan2(y, x);
		if (currentRad > 0.0)
			currentRad = 2 * Math.PI - currentRad;
		else
			currentRad = -currentRad;

		double totalWidth = 0.0;
		double distance = Math.sqrt(x * x + y * y);

		if (distance < RADIUS && distance > INNERRADIUS)
			for (int i = 0; i < tempSectors.size(); i++)
			{
				totalWidth += tempSectors.get(i).width * 2 * Math.PI;
				if (totalWidth > currentRad)
					return i;
			}
		return -1;
	}

	/**
	 * sets the active/current sector (for hovering effects etc)
	 * 
	 * @param sector
	 *           sector which will be the new active sector
	 */
	public void setActiveSector(int sector)
	{
		if (sector != activeSector)
			activeSector = sector;
	}

	/**
	 * 
	 * @return the current active sector.
	 */
	public int getActiveSector()
	{
		return activeSector;
	}

	/**
	 * get the touchpoint at x, y
	 * 
	 * @param x
	 *           x-coord
	 * @param y
	 *           y-coord
	 * @return the touchpoint at (x,y), -1 if none
	 */
	public int getTouchPoint(int x, int y)
	{
		int i = 0;
		for (Point touchPoint : anfasser)
		{
			Rectangle2D touchPointBox = new Rectangle2D.Double(touchPoint.getX()
					- TOUCHPOINTSIZE / 2, touchPoint.getY() - TOUCHPOINTSIZE / 2,
					TOUCHPOINTSIZE, TOUCHPOINTSIZE);
			if (touchPointBox.contains(new Point(x, y)))
				return i;
			i++;
		}
		return -1;
	}

	/**
	 * sets the current touchpoint
	 * 
	 * @param i
	 *           index of current touchpoint in array
	 */

	public void setActiveTouchPoint(int i)
	{
		if ((i > -1) && !activeTouchPoint.equals(anfasser.get(i)))
		{
			activeTouchPoint = anfasser.get(i);
			repaint(anfasser.get(i).x - TOUCHPOINTSIZE / 2 - 2, anfasser.get(i).y
					- TOUCHPOINTSIZE / 2 - 2, TOUCHPOINTSIZE + 4, TOUCHPOINTSIZE + 4);
		}

		if ((i == -1) && !activeTouchPoint.equals(new Point()))
		{
			activeTouchPoint = new Point();
			this.paint(g);
		}
	}

	/**
	 * @param touchPointIndex
	 *           mouseOnTouchPoint
	 * @param x
	 * @param y
	 */
	public void moveTouchPoint(int touchPointIndex, int x, int y)
	{
		if (tempSectors.size() > 1)
		{
			double currentRad = Math.atan2(y - center.getY(), x - center.getX());

			if (currentRad > 0.0)
				currentRad = 2 * Math.PI - currentRad;
			else
				currentRad = -currentRad;

			double oldWidth = tempSectors.get(touchPointIndex).width;
			tempSectors.get(touchPointIndex).width = (currentRad / (2 * Math.PI))
					- tempSectors.get(touchPointIndex).off;

			double difference = oldWidth - tempSectors.get(touchPointIndex).width;
			Integer otherSector;
			if (touchPointIndex == tempSectors.size())
				otherSector = 0;
			else
				otherSector = touchPointIndex + 1;

			tempSectors.get(otherSector).width += difference;
			if ((tempSectors.get(otherSector).width >= 0.05)
					&& (tempSectors.get(touchPointIndex).width >= 0.05))
				repaint();
			else
			{
				tempSectors.get(otherSector).width -= difference;
				tempSectors.get(touchPointIndex).width = oldWidth;
			}
		}
	}

	/**
	 * return the sectors with all changes (width, color)
	 * 
	 * @return the sectors with all changes (width, color)
	 */
	public List<FakeSectorInfo> getSectors()
	{
		return tempSectors;
	}

	/**
	 * Sets current number of sectors to paint
	 * 
	 * @param numSectors
	 */
	public void setSectorCount(int numSectors)
	{
		tempSectors.clear();
		for (int i = 0; i < numSectors; i++)
		{
			FakeSectorInfo tempSInfo = new FakeSectorInfo();
			tempSInfo.width = 1.0 / numSectors;
			tempSInfo.off = 0 + i * 1.0 / numSectors;
			tempSInfo.sectorColor = parentConfig.getSectorColor(i);
			tempSInfo.label = parentConfig.getSectorLabel(i);
			tempSectors.add(tempSInfo);
		}
		repaint();
	}

	/**
	 * @param value
	 */
	public void setTransparency(int value)
	{
		alpha = 1.0 - (value / 100.0);
	}

	public CDialogSector getParentConfig()
	{
		return parentConfig;
	}
}
