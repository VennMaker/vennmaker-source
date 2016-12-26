/**
 * 
 */
package data;

import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.FakeSectorInfo;
import interview.elements.ego.EgoEnhancedFreeAnswerElement;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Speichert alle notwendigen Informationen zum Thema Hintergrundbild,
 * Hintergrundfarbe, Überblendungen, konzentrische Kreise, Sektoren, deren
 * Farbe und Beschriftung.
 * 
 * 
 */
public class BackgroundInfo implements Cloneable, Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID			= 1L;

	@Deprecated
	public Dimension				definedDrawingArea		= null;

	/**
	 * Contains all background network cards
	 */
	private Vector<Integer>		backgroundNetworkcard	= new Vector<Integer>();

	/**
	 * Kapselt die verschiedenen Möglichkeiten, wie das Hintergrundbild
	 * angezeigt wird.
	 * 
	 */
	public static enum BackgroundImageOptions
	{
		FIT_TO_SCREEN(Messages.getString("BackgroundInfo.0")), PRESERVE_AR_MIN(Messages.getString("BackgroundInfo.1")), PRESERVE_AR_MAX( //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("BackgroundInfo.2")), EXACT(Messages.getString("BackgroundInfo.3")); //$NON-NLS-1$ //$NON-NLS-2$

		private String	typename;

		private BackgroundImageOptions(String typename)
		{
			this.typename = typename;
		}

		public String toString()
		{
			return typename;
		}
	}

	/**
	 * Transparenz des Hintergrundbildes (zwischen 0 und 100). 100 = Bild wird
	 * vollständig übermalt. 0 = Bild wird nicht überblendet und normal
	 * angezeigt.
	 */
	private int								transparency;

	/**
	 * Hintergrundbild + evtl. Auswahl des Hintergrundbildes
	 */
	private String							filename;													// orginal

	// Bild

	private String							filenameOfSelection;									// Bild

	// der
	// Auswahl

	private Rectangle						upscaledImgSelection;									// upscaled

	// Selection
	// of
	// the
	// original
	// Image

	/**
	 * Hintergrundfarbe
	 */
	private Color							bgcolor;

	/**
	 * Interpolation des Bildes
	 */
	private BackgroundImageOptions	imgoption;

	/**
	 * Informationen über die Sektoren
	 */
	private List<SectorInfo>			sektoren		= new ArrayList<SectorInfo>();

	/**
	 * Informationen über die Kreise
	 */
	private List<String>					circles		= new ArrayList<String>();

	/**
	 * Was geben die Sektoren an? Wird ggf. in der Legende aufgeführt.
	 */
	private String							sectorLabel;

	/**
	 * Was geben die Kreise an? Wird ggf. in der Legende aufgeführt.
	 */
	private String							circlesLabel;

	/**
	 * Anzahl der konzentrischen Kreise
	 */
	private int								numCircles	= 0;

	/**
	 * Reihenfolge der Kreise
	 */
	private boolean						circlesAsc	= true;

	/**
	 * Anzahl der Sektoren.
	 */
	private int								numSectors	= 0;

	/**
	 * enthält die temporären kreise und sektoren
	 */
	private List<String>					tempCircles	= new ArrayList<String>();

	private List<FakeSectorInfo>		tempSectors	= new ArrayList<FakeSectorInfo>();

	private VennMakerView				view;

	private AttributeType				circleAttribute;

	private AttributeType				sectorAttribute;

	public void setCircleAttribute(AttributeType a)
	{
		circleAttribute = a;
	}

	public AttributeType getCircleAttribute()
	{
		return circleAttribute;
	}

	public void setSectorAttribute(AttributeType a)
	{
		sectorAttribute = a;
		if (sectorAttribute != null)
			this.setNumSectors(sectorAttribute.getPredefinedValues().length);
	}

	public AttributeType getSectorAttribute()
	{
		return sectorAttribute;
	}

	public List<String> getTemporaryCircles()
	{
		return tempCircles;
	}

	public int getTemporaryCirclesSize()
	{
		if (tempCircles != null)
		{
			return tempCircles.size();
		}
		else
		{
			return 0;
		}
	}

	public void addTemporaryCircle(int index, String circle)
	{
		tempCircles.add(index, circle);
	}

	public void setTemporaryCircles(List<String> newTempCircles)
	{
		tempCircles = newTempCircles;
	}

	public void clearTemporaryCircles()
	{
		tempCircles.clear();
	}

	public void clearTemporarySectors()
	{
		tempSectors.clear();
	}

	public List<FakeSectorInfo> getTemporarySectors()
	{
		return tempSectors;
	}

	public int getTemporarySectorsSize()
	{
		if (tempSectors != null)
		{
			return tempSectors.size();
		}
		else
		{
			return 0;
		}
	}

	public void setTemporarySectors(List<FakeSectorInfo> tmpSectors)
	{
		this.tempSectors = tmpSectors;
	}

	/**
	 * @return the numCircles
	 */

	public final int getNumCircles()
	{
		return numCircles;
	}

	public VennMakerView getView()
	{
		return view;
	}

	/**
	 * @param numCircles
	 *           the numCircles to set
	 */
	public final void setNumCircles(int numCircles)
	{
		this.numCircles = numCircles;
	}

	/**
	 * @return the numSectors
	 */
	public final int getNumSectors()
	{
		return numSectors;
	}

	/**
	 * @param numSectors
	 *           the numSectors to set
	 */
	public final void setNumSectors(int numSectors)
	{
		if (this.numSectors != numSectors)
		{
			this.numSectors = numSectors;
			refreshWidths(numSectors);
		}
	}

	/**
	 * guarantees that the circle is filled with sectors
	 * 
	 * @param numSectors
	 */
	public void refreshWidths(int numSectors)
	{
		if (numSectors == 0)
			return;
		for (int i = 0; i < numSectors; i++)
			getSector(i).width = 1.0 / numSectors;
	}

	public void setView(VennMakerView view)
	{
		this.view = view;
	}

	/**
	 * Transparenz aller Sektors (1.0 ist deckend, 0.0 unsichtbar)
	 */
	public double				alpha					= 0.3;

	// /**
	// * Maximale Zahl an Sektoren
	// */
	// public static final int NUMSECTORS = 6;

	/**
	 * Maximale Zahl an Akteurs-Sektoren
	 */
	public static final int	NUMACTORSECTORS	= 6;

	/**
	 * Maximale Zahl an Kreisen
	 */
	public static final int	NUMCIRCLES			= 6;

	/**
	 * Standardwerte
	 */
	private static Color[]	standardColors		= { new Color(255, 32, 32),
			new Color(32, 255, 32), new Color(32, 32, 255),
			new Color(192, 192, 32), new Color(192, 32, 192),
			new Color(32, 192, 192)				};

	/**
	 * Liefert eine Sektorenbeschreibung oder <code>null</code> wenn der Index
	 * falsch ist.
	 * 
	 * @param index
	 *           Ein gültiger Index (&gt;= 0 und &lt; NUMSECTORS)
	 * @return Eine Sektorenbeschreibung oder <code>null</code>
	 */
	public SectorInfo getSector(int index)
	{
		if (index < 0)
			return null;

		// Workaround: Alte Dateien haben keine Liste, daher muss erst eine
		// erzeugt werden.
		if (sektoren == null)
		{
			sektoren = new ArrayList<SectorInfo>();

			// außerdem ist dann meist auch die Transparenz nicht vorhanden (und
			// wäre 0, also nichts sichtbar).
			alpha = 0.3f;
		}

		while (index >= sektoren.size())
		{
			sektoren.add(new SectorInfo());
			// assert (sektoren.size() <= NUMSECTORS) : sektoren.size();
			// Standardwerte... (nicht im Konstruktor, da Index dort nicht
			// bekannt)
			sektoren.get(index).sectorColor = index >= standardColors.length ? Color.gray
					: standardColors[sektoren.size() - 1];
			sektoren.get(index).label = ""; //$NON-NLS-1$
			sektoren.get(index).width = 1.0 / sektoren.size();
			sektoren.get(index).off = 0 + index * 1.0 / sektoren.size();
		}

		return sektoren.get(index);
	}

	/**
	 * Liefert die Liste der konfigurierten Kreise.
	 */
	public List<String> getCircles()
	{
		if (!circlesAsc)
		{
			List<String> cir = new ArrayList<String>();
			for (int i = 0; i < circles.size(); i++)
				cir.add(0, circles.get(i));
			return cir;
		}
		while (getNumCircles() < circles.size())
			circles.remove(circles.size() - 1);
		while (getNumCircles() > circles.size())
			circles.add(""); //$NON-NLS-1$
		return circles;
	}

	public void setCircles(List<String> circles)
	{
		this.circles = circles;
		this.setNumCircles(circles.size());
	}

	/**
	 * Diese Klasse kapselt informationen über die einzelnen Sektoren (farbe,
	 * name, etc.).
	 * 
	 * 
	 */
	public class SectorInfo implements Serializable
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		/**
		 * Die Farbe des Sektors
		 */
		public Color					sectorColor;

		/**
		 * Die Beschriftung des Sektors
		 */
		public String					label;

		/**
		 * Die Breite des Sektors (Prozent eines Kreises 0..1)
		 */
		public double					width;

		/**
		 * Abstand zum Nullpunkt (waagerecht) in Grad
		 */
		public double					off;
	}

	public BackgroundInfo()
	{
		this.filename = null;
		this.bgcolor = Color.white;
		this.imgoption = BackgroundImageOptions.EXACT;
		this.transparency = 0;
		this.backgroundNetworkcard.clear();
	}

	public int getTransparency()
	{
		return this.transparency;
	}

	public void setTransparency(final int t)
	{
		this.transparency = t;
	}

	/**
	 * @return the filename of the Selection of the Background Image if the
	 *         selection is <code>null</code> then the filename of the Original
	 *         Image File - <code>null</code> if there is no Background choosen.
	 */
	public final String getFilename()
	{
		return (this.upscaledImgSelection == null) ? filename
				: filenameOfSelection;
	}

	/**
	 * @return the filname of the original background image - <code>null</code>
	 *         if there is none
	 */
	public final String getFilenameOfOriginalImage()
	{
		return filename;
	}

	/**
	 * @param filename
	 *           the filename to set
	 */
	public final void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	 * @param filenameOfSelection
	 *           the filename of the Image Part which is Selected
	 */
	public final void setFilenameOfSelection(String filenameOfSelection)
	{
		this.filenameOfSelection = filenameOfSelection;
	}

	public final Rectangle getUpscaledImgSelection()
	{
		return upscaledImgSelection;
	}

	public final void setUpscaledImgSelection(Rectangle selection)
	{
		this.upscaledImgSelection = selection;
	}

	/**
	 * @return the bgcolor
	 */
	public final Color getBgcolor()
	{
		return bgcolor;
	}

	/**
	 * @param bgcolor
	 *           the bgcolor to set
	 */
	public final void setBgcolor(Color bgcolor)
	{
		this.bgcolor = bgcolor;
	}

	/**
	 * @return the imgoption
	 */
	public final BackgroundImageOptions getImgoption()
	{
		return imgoption;
	}

	/**
	 * @param imgoption
	 *           the imgoption to set
	 */
	public final void setImgoption(BackgroundImageOptions imgoption)
	{
		this.imgoption = imgoption;
	}

	public BackgroundInfo clone()
	{
		try
		{
			return (BackgroundInfo) super.clone();
		} catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

	public String getSectorLabel()
	{
		return sectorLabel;
	}

	public void setSectorLabel(String sectorLabel)
	{
		this.sectorLabel = sectorLabel;
	}

	public String getCirclesLabel()
	{
		return circlesLabel;
	}

	public void setCirclesLabel(String circlesLabel)
	{
		this.circlesLabel = circlesLabel;
	}

	public static Color[] getStandardColors()
	{
		return standardColors;
	}

	/**
	 * Add a background networkcard
	 * 
	 * @param n
	 *           network
	 */
	public void addBackgroundNetworkcard(Netzwerk n)
	{
		backgroundNetworkcard.add(n.getId());
	}

	public void addBackgroundNetworkcard(Vector<Netzwerk> networks)
	{
		backgroundNetworkcard.clear();
		for (Netzwerk n : networks)
			backgroundNetworkcard.add(n.getId());
	}

	/**
	 * Delete the background network card
	 * 
	 * @param n
	 *           the network
	 */

	public void removeBackgroundNetworkcard(Netzwerk n)
	{
		backgroundNetworkcard.remove(n.getId());
	}

	/**
	 * Get the the background network cards
	 * 
	 * @return Vector<Netzwerk>
	 */

	public Vector<Netzwerk> getBackgroundNetworkcards()
	{
		Projekt project = VennMaker.getInstance().getProject();
		Vector<Netzwerk> networks = new Vector<Netzwerk>();
		if (backgroundNetworkcard != null)
			for (Integer id : backgroundNetworkcard)
			{
				Netzwerk n = project.getNetworkForId(id);
				if (n != null)
					networks.add(n);
			}
		return networks;
	}

	public boolean containBackgroundNetworkcards(Netzwerk n)
	{
		return backgroundNetworkcard.contains(n);
	}

	/**
	 * @return the circlesAsc
	 */
	public boolean isCirclesAsc()
	{
		return circlesAsc;
	}

	/**
	 * @param circlesAsc
	 *           the circlesAsc to set
	 */
	public void setCirclesAsc(boolean circlesAsc)
	{
		this.circlesAsc = circlesAsc;
	}

	private void placePointAtCircleCorner(Point2D p, double circleRadius)
	{
		double x = p.getX();
		double y = p.getY();
		double length = p.distance(0, 0);
		if (length >= 0.1)
		{ // normalize
			x = x / length;
			x = x * circleRadius;
			y = y / length;
			y = y * circleRadius;

			p.setLocation(x, y);
		}

	}

	private void placePointUsingPolar(Point2D p, double segStartDegree,
			double segLength, Random rand, double radius)
	{
		double angleInDegree = segStartDegree + 1
				+ rand.nextInt((int) segLength - 1);
		double angleInRad = Math.toRadians(angleInDegree);

		p.setLocation(radius * Math.cos(angleInRad),
				-radius * Math.sin(angleInRad)); // flipped y; swing coordinates
	}

	
	/**
	 * Calculate akteur position on attributes if network has sectors //
	 * circles
	 */
	public Point2D getRandomPointBySectorAndCircle(Akteur akteur,
			Netzwerk network, Random randomizer)
	{
		double radius = 100;
		double egoSpace = VennMaker.getInstance().getProject()
				.getViewAreaHeight() / 10.0;

		Point2D point = new Point2D.Double(
				(-radius + randomizer.nextInt(2 * (int) radius)),
				(-radius + randomizer.nextInt(2 * (int) radius)));

		if (sectorAttribute != null || circleAttribute != null)
		{

			
			/**
			 * Calculate region of sector (polar position)
			 */
			Object akteurSectorValue = akteur.getAttributeValue(
					this.sectorAttribute, network);
			if (akteurSectorValue != null)
			{
				// find sector for value
				for (int i = 0; i < numSectors; ++i)
				{
					SectorInfo si;
					if ((si = this.sektoren.get(i)).label.equals(akteurSectorValue))
					{ // found label matching value
						double angle = si.off * 360.0; // start angle of the circle
						// segment
						double segAngle = si.width * 360.0; // angle of the segment

						
//						System.out.printf("%s(%s)\t|\t", akteurSectorValue,
//								akteur.getName());
//						System.out.printf("%3.2f;;%3.2f\t|\t", si.off, angle);
//						System.out.printf("%3.2f, %3.2f\t|\t", point.getX(),
//								point.getY());
//						
						
						
						
						placePointUsingPolar(point, angle, segAngle, randomizer, 100);
						double randDistance = randomizer.nextInt((int)(radius - egoSpace));
						placePointAtCircleCorner(point, randDistance);
						
						
//						System.out.printf("%3.2f, %3.2f", point.getX(), point.getY());
//						System.out.println();
					}
				}
			}
			
			/**
			 * Calculate circle distance(randomized in range) for circle value
			 */
			Object akteurCircleValue = akteur.getAttributeValue(
					this.circleAttribute, network);
			if (akteurCircleValue != null)
			{
				
				double minLength = Math.min(VennMaker.getInstance().getProject()
						.getViewAreaHeight()
						* VennMaker.getInstance().getConfig().getViewAreaRatio() / 2.0,
						VennMaker.getInstance().getProject().getViewAreaHeight() / 2.0);
				
				double lastRadius = circlesAsc ? 0.0 : (egoSpace + ((minLength - egoSpace) / numCircles)
						* (numCircles+1));
				for (int i = 0; i < numCircles; ++i)
				{
					int real_index = circlesAsc ? i : numCircles - (i+1);
					double r = egoSpace + ((minLength - egoSpace) / numCircles)
							* (real_index+1);
					
					if ((this.circles.get(i)).equals(akteurCircleValue))
					{ // found label matching value
						double newLength = 0.0;
						if(circlesAsc) { // lastRadius < r
							newLength = lastRadius + randomizer.nextInt((int)r - (int)lastRadius); 
						}
						else { // lastRadius > r
							System.out.println(lastRadius+"-"+r+"="+((int)lastRadius - (int)r));
							newLength = r - randomizer.nextInt((int)lastRadius - (int)r);
						}
							placePointAtCircleCorner(point, newLength);
					}
					lastRadius = r;
				}
			}
		}
		return point;
	}
}
