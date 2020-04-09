/**
 * 
 */
package files.oldClasses;

import gui.configdialog.elements.CDialogSector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import data.BackgroundInfo;

/**
 * old class 
 * 
 * only needed for deserialization of old venn Files by xstream.
 * 
 * 
 */
public class SectorCirclePreview extends JPanel
{
	/**
	 * 
	 */
	private static final long						serialVersionUID	= 1L;

	/**
	 * Information about the Sectors & Circles
	 */
	private BackgroundInfo							bginfo;

	private List<FakeSectorInfo>					tempSectors			= new ArrayList<FakeSectorInfo>();

	/**
	 * Die Anfasser, um die Sektoren groesser zu ziehen
	 */
	private List<Point>								anfasser				= new ArrayList<Point>();

	/**
	 * Groesse der Anfasser in px;
	 */
	private final int									TOUCHPOINTSIZE		= 10;

	private final int									RADIUS				= 140;

	private final int									INNERRADIUS			= 15;

	/**
	 * the mouseListener for the Canvas
	 */
	private SectorCirclePreviewMouseListener	mouseController;

	/**
	 * Antialiasing on(true)/off(false)
	 */
	private boolean									highDefRendering	= true;

	private Point										center;

	private int											activeSector		= -1;

	private Point										activeTouchPoint	= new Point();

	private CDialogSector							parentConfig;

	/**
	 * Graphics Objekt kann unter umstaenden nicht wieder deserialisiert werden 
	   Fehlermeldung:
	   com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter$UnknownFieldException: No such field sun.awt.image.BufImgSurfaceData.dirty
	   (siehe: Ticket #1023)
	 */
	@XStreamOmitField
	private Graphics2D								g;

	private double										alpha;
	/**
	 * wurden die letzten aenderungen an sektorennamen hier vorgenommen?
	 */
	public boolean										lastchanged			= false;
	


	/**
	 * Diese Klasse kapselt informationen über die einzelnen Sektoren (farbe,
	 * name, etc.).
	 * 
	 */
	public class FakeSectorInfo implements Serializable
	{
		private static final long	serialVersionUID	= 1L;

		/**
		 * Die Farbe des Sektors
		 */
		public Color	sectorColor;

		/**
		 * Die Beschriftung des Sektors
		 */
		public String	label;

		/**
		 * Die Breite des Sektors (Prozent eines Kreises 0..1)
		 */
		public double	width;

		/**
		 * Abstand zum Nullpunkt (waagerecht) in Grad
		 */
		public double	off;
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
