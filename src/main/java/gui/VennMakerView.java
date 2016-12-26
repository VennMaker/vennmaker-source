package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import data.Akteur;
import data.AttributeSubject;
import data.AttributeType;
import data.BackgroundInfo;
import data.Config;
import data.EventPerformedListener;
import data.EventProcessor;
import data.MenuEventList;
import data.MenuListener;
import data.Netzwerk;
import data.Relation;
import data.RelationColorVisualizer;
import data.RelationDashVisualizer;
import data.RelationMonitor;
import data.VennMakerCoordinateSystem;
import events.ActorSelectionEvent;
import events.AddActorEvent;
import events.ComplexEvent;
import events.MenuEvent;
import events.MoveActorEvent;
import events.NewActorEvent;
import events.SetAttributeEvent;
import events.VennMakerEvent;
import files.ImageOperations;

/**
 * 
 * 
 *         The main view class - provides interactive tools for manipulation of
 *         network
 */
public class VennMakerView extends JComponent implements Printable,
		MenuListener
{
	/**
	 * Sollen debug-Ausgaben auf stdout/in der Grafik ausgegeben werden?
	 */
	private static final boolean			debug	= false;

	/**
	 * A dictionary of visible shapes to their model relation representation.
	 * This mainly serves as a cache and is updated after *EVERY* paint
	 * operation. There is no guarantee that the linked <code>Relation</code>
	 * -Object is still available.
	 */
	private final Map<Shape, Relation>	visibleRelations;

	class RelationVector
	{
		public RelationVector(Point end, Relation r, Point start)
		{
			relation = r;
			this.start = start;
			direction = new Point(end.x - start.x, end.y - start.y);

		}

		final protected Point		start;

		final protected Point		direction;

		final protected Relation	relation;
	}

	private final ArrayList<RelationVector>				searchVectors;

	/**
	 * bei einem screenshot der "viel" groeÃŸer ist als die momentane
	 * Zeichenflaeche muessen auch die Bilder der Akteuere in einer viel
	 * groesseren Version geladen werden dafuer ist dieser Faktor (standart 1.0 ,
	 * nur die Methode screenshot aendert diesen Wert kurz fuer das Zeichnen...
	 */
	private double													imageScalingFactor			= 1.0;

	/**
	 * 
	 */
	private static final BasicStroke							STANDARD_STROKE				= new BasicStroke(
																													1.0f);

	/**
	 * 
	 */
	private static final int									BORDER_SAFETY_PADDING		= 25;

	/**
	 * 
	 */
	private static final Color									COLOR_LABEL						= new Color(
																													64,
																													64,
																													64);

	/**
	 * 
	 */
	private static final Color									COLOR_LABEL_BOX				= new Color(
																													224,
																													224,
																													224,
																													200);

	private static final Color									COLOR_LABEL_BOX_EGO			= new Color(
																													180,
																													224,
																													224,
																													200);

	/**
	 * 
	 */
	private static final Color									COLOR_CONCENTRIC_CIRCLES	= new Color(
																													92,
																													92,
																													92);

	/**
	 * 
	 */
	private static final Color									COLOR_SECTOR_LINE				= new Color(
																													150,
																													150,
																													150);

	/**
	 * Hintergrundfarbe des inaktiven Bereiches um die ZeichenflÃ¤che.
	 */
	private static final Color									COLOR_BACKGROUND				= new Color(
																													235,
																													235,
																													235);

	private static final long									serialVersionUID				= 1L;

	/**
	 * Die Breite der Kanten wird gemÃ¤ÃŸ aktueller Skalierung berechnet,
	 * zusÃ¤tzlich wird dieser Korrekturfaktor angewendet, damit die Linien in
	 * den nicht-skalierten Bereichen (Buttons) in etwa mit denen auf der
	 * ZeichenflÃ¤che harmonieren.
	 */
	public static final int										LINE_WIDTH_SCALE				= 6;

	/**
	 * additional space between multiple relations
	 */
	public static final int										RELATION_DISTANCE				= 2;

	/**
	 * A pointer to the base network object that is displayed through this view
	 */
	private final Netzwerk										netzwerk;

	private VennMakerViewMouseController					mouseController;

	private static VennMakerCoordinateSystem				vmcs;

	/**
	 * Zeichnen von Legende?
	 */
	boolean															drawLegend						= true;

	private static HashMap<Netzwerk, VennMakerLegend>	legends							= new HashMap<Netzwerk, VennMakerLegend>();

	/**
	 * Zeichnen des Netzwerknamens?
	 */
	boolean															drawNetworkName				= true;

	int																drawNetworkNameOffset		= -1;													// -1:

	// links,
	// 0:
	// Mitte,
	// 1:
	// rechts

	/**
	 * Aktuell hervorzuhebende Relation
	 */
	public static Relation										currentHoverRelation			= null;

	/**
	 * Zeichnen aller Kommentare?
	 */
	@Deprecated
	static boolean													showComments					= false;

	// Voll aufgelÃ¶stes Hintergrundbild
	private BufferedImage										backgroundImage;

	// Sichtbares Hintergrundbild (Cache)
	private BufferedImage										visibleBackgroundImage;

	static AlphaComposite										alphaComp;

	private boolean												isFilterActive					= false;

	/**
	 * Contains a pointer to the shape that represents an interactively dragged
	 * relation. This is usually the case if an interviewee is creating a new
	 * relation.
	 */
	private Shape													temporaryDrawnLine;

	/**
	 * Contains a pointer to the rectangle representing an interactively dragged
	 * multiple selection.
	 */
	private Rectangle2D.Double									currentMultiSelection;

	/**
	 * Contains the outgoing actor of a temporary drawn line to control different
	 * transparencies
	 */
	private Akteur													relationActor;

	/**
	 * this floatvalue defines how visible relations are, which are not going out
	 * from the same actor as the temporary relationline
	 */
	private static final float									NONACTIVE_RELATION_ALPHA	= 0.2f;

	/**
	 * A pointer to the currently dragged actors during an interactive animation.
	 * If there is no such ongoing animation this pointer contains
	 * 
	 * ITS NOW A LIST BECAUSE OF MULTIPLE SELECTION / MOVEMENT <code>null</code>.
	 */
	private List<Akteur>											temporaryDrawnActors;

	/**
	 * Contains the current movement in a mouse interaction of the moved actor.
	 * This is only valid iff <code>temporaryDrawnActor</code> is not
	 * <code>null</code>.
	 */
	private Point2D												temporaryMovement;

	/**
	 * Speichere die FensterhÃ¶he, mit der ddas VMCS berechnet wurde.
	 */
	private int														lastHeight;

	/**
	 * GrÃ¶ÃŸe der gezeichneten Kreise (um Attribute zu setzen je nachdem wo
	 * Akteur sitzt)
	 */
	private ArrayList<Ellipse2D>								drawnCircles					= new ArrayList<Ellipse2D>();

	/**
	 * An image pool that contains all actor images in full resolution.
	 */
	private Map<String, BufferedImage>						actorsImageCache				= new HashMap<String, BufferedImage>();

	/****************************************************************************
	 * This image pool contains all actor images in current size (omits scaling
	 * if only using <code>actorsImageCache</code>).
	 */
	private Map<Akteur, Image>									actorsIconCache				= new HashMap<Akteur, Image>();

	/**
	 * Erzeugt eine neue ZeichenflÃ¤che fuer das angegebene Netzwerkobjekt.
	 * Aenderungen am Modell fuehren nicht zu einem Neuzeichnen der
	 * Zeichenflaeche. Dies muss explizit durchgefuehrt werden.
	 * 
	 * @param netzwerk
	 *           Das Modell fuer die Zeichenflaeche.
	 */
	public VennMakerView(Netzwerk netzwerk)
	{

		this.netzwerk = netzwerk;
		this.mouseController = new VennMakerViewMouseController(this);
		this.backgroundImage = null;
		this.visibleBackgroundImage = null;
		this.temporaryMovement = new Point2D.Double();
		this.visibleRelations = new HashMap<Shape, Relation>();

		this.searchVectors = new ArrayList<RelationVector>();

		setBackground(Color.WHITE);

		addComponentListener(new ComponentListener()
		{
			public void componentHidden(ComponentEvent e)
			{
			}

			public void componentMoved(ComponentEvent e)
			{
			}

			public void componentResized(ComponentEvent e)
			{
				updateView();
			}

			public void componentShown(ComponentEvent e)
			{
			}
		});

		// Cache muss regelmaessig aktualisiert werden.
		EventProcessor.getInstance().addEventPerformedListener(
				new EventPerformedListener()
				{
					@Override
					public void eventConsumed(VennMakerEvent event)
					{
						if (event instanceof SetAttributeEvent)
						{
							AttributeSubject subject = ((SetAttributeEvent) event)
									.getSubject();
							if (subject instanceof Akteur)
								actorsIconCache.remove(subject);
						}
						if (event instanceof ActorSelectionEvent)
						{
							ActorSelectionEvent ase = (ActorSelectionEvent) event;
							currentMultiSelection = ase.getSelection();
							selectMultipleActors();
						}
					}
				});

		// ----
		MenuEventList.getInstance().addListener(this);

		// ----
		// Add mouse interaction
		this.addMouseListener(this.mouseController);
		this.addMouseMotionListener(this.mouseController);
		this.addMouseWheelListener(this.mouseController);

		// Nach jedem Event neuzeichnen!
		EventProcessor.getInstance().addEventPerformedListener(
				new EventPerformedListener()
				{
					@Override
					public void eventConsumed(VennMakerEvent event)
					{
						repaint();
					}
				});

		BufferedImage img = null;

		if (this.netzwerk.getHintergrund().getFilename() != null)
			img = ImageOperations.loadImage(new File(this.netzwerk
					.getHintergrund().getFilename()));

		// Zentrum erst mal in die Mitte.
		if (VennMakerView.vmcs == null)
			VennMakerView.vmcs = new VennMakerCoordinateSystem(null);

		if (img != null)
			setBackgroundImage(img);
	}

	/**
	 * Invoked when the view's size has changed, updates the vmcs-information
	 * container in order to work properly.
	 */
	private void updateVMCS()
	{
		lastHeight = getHeight();
		// Bestimme Skalierung, so dass die ZeichenflÃ¤che horizontal und
		// vertikal
		// genug Platz im Fenster hat.
		float scale = Math
				.min((float) (getHeight() - BORDER_SAFETY_PADDING)
						/ VennMaker.getInstance().getProject().getViewAreaHeight(),
						(float) (getWidth() - BORDER_SAFETY_PADDING)
								/ (VennMaker.getInstance().getProject()
										.getViewAreaHeight() * VennMaker.getInstance()
										.getConfig().getViewAreaRatio()));
		VennMakerView.vmcs.setScale(scale);
		actorsIconCache.clear();
		VennMakerView.vmcs.setMid(new Point2D.Double((getWidth()) / 2.0,
				(getHeight()) / 2.0));
	}

	/**
	 * Berechnet das VMCS neu und zeichnet dann neu. Wird zum Beispiel vom
	 * ViewConfigDialoag nach moeglichen Aenderungen an Hintergrundbild,
	 * Seitenverhaeltnis etc. aufgerufen.
	 */
	public void updateView()
	{
		updateVMCS();
		inferVisibleBackgroundImage();
		// repaint();
	}

	/**
	 * Die Sektor und Kreis Attribute aller Akteure anpassen falls Sektoren oder
	 * Kreise hinzugefuegt werden
	 */
	public void updateSectorAndCircleAttributes()
	{
		ComplexEvent ce = new ComplexEvent(
				"Change attributes of all Actors through Sektors and Circles");

		AttributeType circleAttribute = netzwerk.getHintergrund()
				.getCircleAttribute();
		AttributeType sectorAttribute = netzwerk.getHintergrund()
				.getSectorAttribute();
		if (circleAttribute != null || sectorAttribute != null)
		{
			for (Akteur a : netzwerk.getAkteure())
			{
				addSectorCircleEvents(ce, a, 0, 0, a.getLocation(netzwerk).getX(),
						a.getLocation(netzwerk).getY());
			}
		}

		EventProcessor.getInstance().fireEvent(ce);
	}

	/**
	 * Sets the current position of an interactively dragged actor. If there is
	 * no such actor this method has no effect. The movement is a relative
	 * movement in java2d space.
	 * 
	 * @param x
	 *           x-coordinate in java2d space
	 * @param y
	 *           y-coordinate in java2d space
	 */
	public void setTemporaryMovement(double x, double y)
	{
		if (this.temporaryDrawnActors != null
				&& this.temporaryDrawnActors.size() > 0)
		{
			// Check whether interactive movement range is not exceeded

			// check if all movements are allowed
			boolean allowed = true;
			for (Akteur a : temporaryDrawnActors)
			{
				Point2D curPosOfActor = VennMakerView.vmcs.toJava2D(a
						.getLocation(this.netzwerk));
				allowed = allowed
						&& isActorAllowed(a, curPosOfActor.getX() + x,
								curPosOfActor.getY() + y);
			}
			if (allowed)
				this.temporaryMovement.setLocation(x, y);
			repaint();
		}
	}

	public ArrayList<Ellipse2D> getDrawnCircles()
	{
		return drawnCircles;
	}

	/**
	 * Updates the view's information such that a relation becomes visible that
	 * is still unconnected with its target.
	 * 
	 * @param start
	 *           The destination of the relation. No relation will be displayed
	 *           if start is <code>null</code>.
	 * @param x
	 *           The current end point (x-coordinate) in java2d space.
	 * @param y
	 *           The current end point (y-coordinate) in java2d space.
	 */
	public void setInteractiveRelation(Akteur start, double x, double y)
	{
		if (start == null)
		{
			this.temporaryDrawnLine = null;
		}
		else
		{
			Point2D startPoint = VennMakerView.vmcs.toJava2D(start
					.getLocation(this.netzwerk));
			if (VennMaker
					.getInstance()
					.getProject()
					.getIsDirected(
							VennMaker.getInstance().getBeziehungsart().getType()))
				this.temporaryDrawnLine = new ArrowLine(startPoint.getX(),
						startPoint.getY(), x, y);

			else
				this.temporaryDrawnLine = new Line2D.Double(startPoint.getX(),
						startPoint.getY(), x, y);
		}

		this.relationActor = start;

		repaint();
	}

	/**
	 * Defines which actor is to be interactively dragged.
	 * 
	 * @param actor
	 *           The dragged actors or <code>null</code> after a interactive move
	 *           operation. In the latter case, an appropriate event is fired
	 *           with the current interaction state.
	 */
	public void setTemporaryActors(List<Akteur> tmpDrawnActors)
	{
		if (tmpDrawnActors == null)
		{

			if (this.temporaryDrawnActors != null)
			{
				ComplexEvent ce = new ComplexEvent(
						Messages.getString("VennMaker.MoveActorToSector")); //$NON-NLS-1$

				double x = temporaryMovement.getX() / VennMakerView.vmcs.getScale();
				double y = temporaryMovement.getY() / VennMakerView.vmcs.getScale();

				for (Akteur a : this.temporaryDrawnActors)
				{

					MoveActorEvent event = new MoveActorEvent(a, netzwerk,
							new Point2D.Double(x, y));

					if (ce != null)
					{
						ce.addEvent(event);

						/*
						 * make sure, that all sector- and circleattributes are
						 * adapted to the new position
						 */
						Point2D p = a.getLocation(netzwerk);
						
						addSectorCircleEvents(ce, a, 0, 0, p.getX() + x, p.getY() + y);
					}

					// Point2D p = a.getLocation(netzwerk);
					// {
					// ce = addSectorCircleEvents(ce, a, 0, 0, p.getX()
					// + x, p.getY() + y);
					// }
				}
				if (ce != null)
					EventProcessor.getInstance().fireEvent(ce);
			}

			if (currentMultiSelection != null)
			{
				currentMultiSelection.x += temporaryMovement.getX();
				currentMultiSelection.y += temporaryMovement.getY();
			}
			// reset movement
			this.temporaryMovement.setLocation(0.0, 0.0);
		}

		this.temporaryDrawnActors = tmpDrawnActors;
	}

	/**
	 * 
	 * @return the actor which is dragged in the current animation, or
	 *         <code>null</code> if there is no animation
	 */
	public List<Akteur> getTemporaryActors()
	{
		return temporaryDrawnActors;
	}

	/**
	 * changes the background image of this view
	 * 
	 * @param newImg
	 *           - the new background image
	 */
	public void setBackgroundImage(BufferedImage newImg)
	{
		this.backgroundImage = newImg;
		this.visibleBackgroundImage = null;
	}

	/**
	 * 
	 */
	protected void inferVisibleBackgroundImage()
	{
		if (this.backgroundImage != null)
		{
			if (!netzwerk.getHintergrund().getImgoption()
					.equals(BackgroundInfo.BackgroundImageOptions.EXACT))
			{
				int curW = this.backgroundImage.getWidth();
				int curH = this.backgroundImage.getHeight();
				double ratioW = ((double) this.getViewArea().getWidth()) / curW;
				double ratioH = ((double) this.getViewArea().getHeight()) / curH;
				if (netzwerk
						.getHintergrund()
						.getImgoption()
						.equals(BackgroundInfo.BackgroundImageOptions.PRESERVE_AR_MIN))
				{
					ratioW = Math.min(ratioW, ratioH);
					ratioH = ratioW;
				}
				else if (netzwerk
						.getHintergrund()
						.getImgoption()
						.equals(BackgroundInfo.BackgroundImageOptions.PRESERVE_AR_MAX))
				{
					ratioW = Math.max(ratioW, ratioH);
					ratioH = ratioW;
				}

				if (this.backgroundImage.getType() == BufferedImage.TYPE_CUSTOM)
					this.backgroundImage = ImageOperations.pngToARGB(
							this.backgroundImage, null);
				// DEFAULT: Fit to screen!
				AffineTransformOp ato = new AffineTransformOp(
						AffineTransform.getScaleInstance(ratioW, ratioH),
						AffineTransformOp.TYPE_BICUBIC);

				// Bugfix Ticket #1008
				// Prevent negative width and height values for image
				if ((curW * ratioW) <= 0 || (curH * ratioH) <= 0)
					return;

				BufferedImage smallImg = new BufferedImage((int) (curW * ratioW),
						(int) (curH * ratioH), this.backgroundImage.getType());
				ato.filter(this.backgroundImage, smallImg);
				this.visibleBackgroundImage = smallImg;
			}
			else
			{
				this.visibleBackgroundImage = this.backgroundImage;
			}

			Graphics2D g = this.visibleBackgroundImage.createGraphics();
			g.setPaint(this.netzwerk.getHintergrund().getBgcolor());
			g.setComposite(AlphaComposite.SrcOver.derive(this.netzwerk
					.getHintergrund().getTransparency() / 100.0f));
			g.fill(new Rectangle2D.Double(0, 0, this.visibleBackgroundImage
					.getWidth(), this.visibleBackgroundImage.getHeight()));

		}
		else
		{
			this.visibleBackgroundImage = null;
		}
	}

	/**
	 * 
	 * @return the drawing area as rectangle
	 */
	public Rectangle2D getViewArea()
	{
		return VennMakerView.vmcs
				.toJava2D(new Rectangle((int) -(VennMaker.getInstance()
						.getProject().getViewAreaHeight() * VennMaker.getInstance()
						.getConfig().getViewAreaRatio()) / 2, -VennMaker
						.getInstance().getProject().getViewAreaHeight() / 2,
						(int) (VennMaker.getInstance().getProject()
								.getViewAreaHeight() * VennMaker.getInstance()
								.getConfig().getViewAreaRatio()), VennMaker
								.getInstance().getProject().getViewAreaHeight()));
	}

	/**
	 * Creates a screenshot of the current view (also containing possibly
	 * invisible areas) and writes a png or jpg file to the given filename.<br>
	 * (screenshot will be scaled)<br>
	 * 
	 * @param filename
	 *           - A valid filename
	 * @param imgType
	 *           - "PNG" or "JPG"
	 * @param width
	 *           - destinated width
	 * @param height
	 *           - destinated height
	 * 
	 * @throws IOException
	 */
	public void screenshot(String filename, String imgType, int width, int height)
			throws IOException
	{
		double faktorX = (double) width / getViewArea().getWidth();
		double faktorY = (double) height / getViewArea().getHeight();

		// Cache leeren, damit Symbole anschliessend mit besserer Qualitaet
		// gezeichnet werden
		for (Akteur a : this.netzwerk.getAkteure())
			actorsIconCache.remove(a);

		// SVG
		if (imgType.equals(Messages.getString("VennMaker.SVG"))) //$NON-NLS-1$
		{

			DOMImplementation domImpl = GenericDOMImplementation
					.getDOMImplementation();

			String svgNS = "http://www.w3.org/2000/svg"; //$NON-NLS-1$
			Document document = domImpl.createDocument(svgNS, "svg", null); //$NON-NLS-1$

			SVGGraphics2D svg = new SVGGraphics2D(document);
			svg.setClip(this.getViewArea());
			imageScalingFactor = Math.max(faktorX, faktorY);
			paintComponent(svg);
			imageScalingFactor = 1.0;

			Writer out = new OutputStreamWriter(new FileOutputStream(filename),
					"UTF-8"); //$NON-NLS-1$
			svg.stream(out, false);
		}
		// Andere
		else
		{
			/*
			 * Workaround: Das Problem besteht darin, dass alte VennMakerdateien
			 * mit vorgegebenem Zeichenbereich beim Export als png Datei nicht mehr
			 * lesbar sind. Die Ursache ist, dass vennmaker die netzwerkkarte
			 * automatisch in der Groesse anpasst.
			 */

			final BufferedImage img = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();

			g.setClip(0, 0, width, height);
			g.translate(-getViewArea().getX() * faktorX, -getViewArea().getY()
					* faktorY);

			((Graphics2D) g).scale(faktorX, faktorY);
			((Graphics2D) g).drawImage(img, 0, 0, null);

			imageScalingFactor = Math.max(faktorX, faktorY);
			this.paintComponent(g);
			imageScalingFactor = 1.0;

			ImageIO.write(img, imgType, new File(filename));
		}
	}

	/**
	 * print the current view in landscape
	 */
	public void printPage()
	{
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		aset.add(OrientationRequested.LANDSCAPE);
		// aset.add(new Copies(2));
		aset.add(new JobName("VennMaker-Job", null)); //$NON-NLS-1$

		/* Create a print job */
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(this);
		/* locate a print service that can handle the request */
		PrintService[] services = PrinterJob.lookupPrintServices();

		if (services.length > 0)
		{

			try
			{
				pj.setPrintService(services[0]);
				pj.pageDialog(aset);
				if (pj.printDialog(aset))
				{
					pj.print(aset);
				}
			} catch (PrinterException pe)
			{
				System.err.println(pe);
			}
		}
	}

	/**
	 * prints a graphic set to a specific page format
	 * 
	 * @param g
	 *           - graphics which should be printed
	 * @param pf
	 *           - format options
	 * @param pageIndex
	 *           - (only 0 supported atm)
	 */
	public int print(Graphics g, PageFormat pf, int pageIndex)
	{

		if (pageIndex == 0)
		{

			int w = (int) getViewArea().getWidth();
			int h = (int) getViewArea().getHeight();

			int pageWidth = (int) pf.getImageableWidth();
			int pageHeight = (int) pf.getImageableHeight();

			final BufferedImage img = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D gd = img.createGraphics();

			gd.setClip(0, 0, w, h);

			gd.translate(-getViewArea().getX(), -getViewArea().getY());

			this.paintComponent(gd);

			((Graphics2D) g).translate(pf.getImageableX(), pf.getImageableY());

			double xScale = (double) (pageWidth) / w;
			double yScale = (double) (pageHeight) / h;

			double scaleFactor = Math.min(xScale, yScale);
			((Graphics2D) g).scale(scaleFactor, scaleFactor);
			((Graphics2D) g).drawImage(img, 0, 0, null);

			return Printable.PAGE_EXISTS;
		}
		else
		{
			return Printable.NO_SUCH_PAGE;
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics graphics)
	{
		// Wenn die Skalierung im VMCS nicht mehr aktuell ist, weil sich die
		// Fensterhoehe geaendert hat, aktualisere das VMCS erst. Das
		// Resize-Event
		// beim VennMaker-Start kommt erst nach dem ersten Zeichnen.
		if (this.getHeight() != lastHeight)
			updateVMCS();

		// Copy graphics context -> Transformations are non-destructive
		Graphics2D g = (Graphics2D) graphics.create();
		Rectangle2D drawnRect = g.getClipBounds();

		// Draw Background
		g.setBackground(netzwerk.getHintergrund().getBgcolor());

		g.clearRect((int) drawnRect.getX(), (int) drawnRect.getY(),
				(int) drawnRect.getWidth(), (int) drawnRect.getHeight());

		// HIER HINTERGRUNDBILD
		if (this.backgroundImage != null || this.visibleBackgroundImage != null)
		{
			// Reload from Cache
			if (this.visibleBackgroundImage == null)
			{
				inferVisibleBackgroundImage();
			}

			g.drawImage(this.visibleBackgroundImage, ((int) this.getViewArea()
					.getWidth() - this.visibleBackgroundImage.getWidth())
					/ 2
					+ (int) this.getViewArea().getX(), ((int) this.getViewArea()
					.getHeight() - this.visibleBackgroundImage.getHeight())
					/ 2
					+ (int) this.getViewArea().getY(), this);
		}

		Area grayArea = new Area(new Rectangle2D.Double(0.0, 0.0,
				this.getWidth(), this.getHeight()));
		grayArea.subtract(new Area(getViewArea()));

		g.setColor(COLOR_BACKGROUND);
		g.fill(grayArea);

		/**
		 * High Quality Rendering
		 */
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_NORMALIZE);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		/**
		 * Konzentrische Kreise und Sektoren
		 */
		this.paintSektoren(netzwerk.getHintergrund(), g);
		this.paintCircles(netzwerk.getHintergrund(), g);

		if (getTemporaryActors() != null)
		{

			// keep old transform
			AffineTransform lastTransform = g.getTransform();
			g.translate(temporaryMovement.getX(), temporaryMovement.getY());
			for (Akteur a : temporaryDrawnActors)
				paintActor(a, g, VennMaker.getInstance().getProject()
						.getCurrentNetzwerk(), 0.35f);

			// reset
			g.setTransform(lastTransform);

			alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
			g.setComposite(alphaComp);
		}

		if ((TestVersion.getEndDate() > 0) //$NON-NLS-1$
				&& (TestVersion.isLogo() == true))
			this.paintLogo(g);

		// Legende einzeichnen
		if (drawLegend)
			getLegend(netzwerk).paintLegend(g);

		// Netzwerkname zeichnen
		if (drawNetworkName)
			this.drawNetworkName(this.netzwerk, g, drawNetworkNameOffset);

		// Draw interactive relation
		if (this.temporaryDrawnLine != null)
		{
			alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			g.setComposite(alphaComp);
			g.setPaint(VennMaker
					.getInstance()
					.getProject()
					.getCurrentNetzwerk()
					.getRelationColorVisualizer(
							VennMaker.getInstance().getBeziehungsart().getType(),
							VennMaker.getInstance().getBeziehungsart())
					.getColor(VennMaker.getInstance().getBeziehungsauspraegung()));

			/*
			 * g.setStroke(new BasicStroke(VennMakerView.vmcs.toJava2D(stroke
			 * .getLineWidth()) / LINE_WIDTH_SCALE, stroke.getEndCap(),
			 * stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(),
			 * stroke .getDashPhase()));
			 */

			g.draw(this.temporaryDrawnLine);

			alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
			g.setComposite(alphaComp);
			g.setStroke(STANDARD_STROKE);
		}

		// Loesche Relationscache
		this.visibleRelations.clear();
		this.searchVectors.clear();
		// long benchmark = new java.util.Date().getTime();
		// Draw the relations
		// Bottleneck
		// paintRelation(g, this.netzwerk, 1f);
		paintRelationPerf(g, this.netzwerk, 1.0f);

		// + (new java.util.Date().getTime() - benchmark));

		for (final Akteur akteur : this.netzwerk.getAkteure())
		{ // Filter aktiviert?
			float a = 1f;
			if (this.netzwerk.getMarkedActors() != null && isFilterActive)
			{
				if (this.netzwerk.getMarkedActors().contains(akteur))
					a = 1f;
				else
				{
					a = 0.1f;
					netzwerk.lockActor(akteur);
				}

			}

			if (!VennMaker.getInstance().getConfig().isEgoDisabled()
					|| akteur != this.netzwerk.getEgo())
			{

				paintActorSector(akteur, g, this.netzwerk, (float) a);
				paintActor(akteur, g, this.netzwerk, a);
				// paintMarkedActor(akteur, g, this.netzwerk);
			}

			// Zeichne alle Kommentare der Akteure
			if (showComments)
			{
				g.setFont(g.getFont().deriveFont(Font.PLAIN));

				// for (final Akteur akteur : this.netzwerk.getAkteure())
				if (!VennMaker.getInstance().getConfig().isEgoDisabled()
						|| akteur != this.netzwerk.getEgo())
					paintActorComment(akteur, g);
			}

		}

		// Transparente Hintergrundnetzwerke zeichnen
		// Relationen
		if (this.netzwerk.getHintergrund().getBackgroundNetworkcards() != null)
			for (final Netzwerk n : this.netzwerk.getHintergrund()
					.getBackgroundNetworkcards())
			{
				if (n != this.netzwerk)
					// for (final Akteur akteur : this.netzwerk.getAkteure()){
					paintRelation(g, n, 1.0f);
				// }

			}

		// Akteure
		/**
		 * Wird wann ausgefuehrt?
		 * 
		 */
		if (this.netzwerk.getHintergrund().getBackgroundNetworkcards() != null)
			for (final Netzwerk n : this.netzwerk.getHintergrund()
					.getBackgroundNetworkcards())
			{
				// if (n != this.netzwerk)
				for (Akteur akteur : n.getAkteure())
					if (!VennMaker.getInstance().getConfig().isEgoDisabled()
							|| akteur != n.getEgo())
					{

						paintActor(akteur, g, n, 0.2f);
					}
			}

		// Multi Selection
		if (currentMultiSelection != null)
		{
			paintSelection(g);
		}

		alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
		g.setComposite(alphaComp);

	}

	private void paintRelationPerf(Graphics2D g, Netzwerk n, float alpha)
	{

		HashMap<Akteur, HashMap<Akteur, Vector<Integer>>> knownRelations = new HashMap<Akteur, HashMap<Akteur, Vector<Integer>>>();
		Map<Akteur, Map<Akteur, Point2D[]>> bottomRelations = new HashMap<Akteur, Map<Akteur, Point2D[]>>();

		List<AttributeType> relationLabels = VennMaker.getInstance().getProject()
				.getDisplayedAtRelation();

		/*
		 * Preset values for render which will not change during iteration
		 */
		RelationMonitor rm = RelationMonitor.getInstance();

		BasicStroke baseStroke = new BasicStroke();
		float a = 1.0f;

		g.setFont(g.getFont().deriveFont(
				Font.PLAIN,
				VennMakerView.vmcs.toJava2D(3)
						* VennMaker.getInstance().getProject().getTextZoom()));

		// RelationMonitor updaten
		rm.update();

		final Akteur[] akteure = new Akteur[2];

		for (final Relation r : rm.getNetworkRelations(n))
		{
			// Get start und end actor
			Akteur x = rm.getStartActor(r);
			Akteur y = r.getAkteur();

			// if currently dragging line
			if (this.relationActor != null)
			{
				// if this is the relation draw higher alpha
				if (rm.getRelationsFromAndToActor(this.relationActor, n)
						.contains(r))
				{
					a = 1.f;
				}
				else
				{
					a = NONACTIVE_RELATION_ALPHA;
				}
			}
			else
			{
				/* Filteractors */
				Vector<Akteur> markedActors = n.getMarkedActors();
				if (markedActors != null && isFilterActive)
				{
					if (markedActors.contains(x) && markedActors.contains(y))
					{
						a = 1.0f;
					}
					else
					{
						a = NONACTIVE_RELATION_ALPHA;
						n.addLockedRelation(r);
					}
				}

				/* relationfilter */
				if (!isFilterActive)
				{
					if (!n.getStatusFilter()
							|| (n.getStatusFilter() && (n.getFilter(x) || (!VennMaker
									.getInstance().getProject()
									.getIsDirected(r.getAttributeCollectorValue()) && n
									.getFilter(y)))))
					{
						a = 1.0f;
					}
					else
					{
						a = NONACTIVE_RELATION_ALPHA;
					}
				}
			}

			// Draw lines from/to ego if ego is not disabled and
			// neither A or B is ego
			if (!(VennMaker.getInstance().getConfig().isEgoDisabled() && (x
					.equals(netzwerk.getEgo()) || y.equals(netzwerk.getEgo()))))
			{
				// Calculate Points in Java Coord-Space
				Point2D px = VennMakerView.vmcs.toJava2D(x
						.getLocation(this.netzwerk));
				Point2D py = VennMakerView.vmcs.toJava2D(y
						.getLocation(this.netzwerk));

				if (py == null || px == null)
					continue;

				double ySize = VennMakerView.vmcs.toJava2D(y.getGroesse(n));
				Point2D endPoint = getIntersection(new Rectangle2D.Double(py.getX()
						- ySize * .5 - 2, py.getY() - ySize * .5 - 2, ySize + 4,
						ySize + 4), px.getX(), px.getY());
				if (endPoint == null)
					continue;

				double endPointX = endPoint.getX(), endPointY = endPoint.getY();

				double xSize = VennMakerView.vmcs.toJava2D(x.getGroesse(n));

				Point2D startPoint = getIntersection(
						new Rectangle2D.Double(px.getX() - xSize * .5, px.getY()
								- xSize * .5, xSize, xSize), endPointX, endPointY);
				if (startPoint == null)
					continue;

				double startPointX = startPoint.getX(), startPointY = startPoint
						.getY();

				if (!x.equals(n.getEgo()) && (x.getId() < y.getId())
						|| (y.equals(n.getEgo())))
				{
					akteure[0] = y;
					akteure[1] = x;
				}
				else
				{
					akteure[0] = x;
					akteure[1] = y;
				}

				// Set Color depending on attribute
				String currentAttributeTypeCollector = r
						.getAttributeCollectorValue();

				/*
				 * Hole MainGenetor fuer AttributeType von Relation r
				 */
				AttributeType next = VennMaker.getInstance().getProject()
						.getMainGeneratorType(currentAttributeTypeCollector);

				RelationColorVisualizer relationColorVisualizer = n
						.getActiveRelationColorVisualizer(currentAttributeTypeCollector);

				Color color = relationColorVisualizer.getColor(r, n);
		
				g.setPaint(color);
				double distance = 0.0;
				// If relation is known
				if (knownRelations.containsKey(akteure[0])
						&& knownRelations.get(akteure[0]).containsKey(akteure[1]))
				{
					int amount = knownRelations.get(akteure[0]).get(akteure[1])
							.size();
					knownRelations
							.get(akteure[0])
							.get(akteure[1])
							.add(n.getRelationSizeVisualizer(
									currentAttributeTypeCollector, next).getSize(r, n));

					double vektorLaenge = Math.sqrt((endPointX - startPointX)
							* (endPointX - startPointX) + (endPointY - startPointY)
							* (endPointY - startPointY));

					double normalizedEndStartX = (startPointX - endPointX)
							/ vektorLaenge;
					double normalizedEndStartY = (endPointY - startPointY)
							/ vektorLaenge;
					if (normalizedEndStartX < 0)
					{
						normalizedEndStartX *= -1;
						normalizedEndStartY *= -1;
					}

					int j = amount;
					if (j > 0)
					{
						distance += (double) ((knownRelations.get(akteure[0])
								.get(akteure[1]).get(j) / 2.0)
								/ LINE_WIDTH_SCALE + RELATION_DISTANCE);
						j -= 2;
						while (j > 0)
						{
							distance += (double) (knownRelations.get(akteure[0]).get(
									akteure[1]).get(j))
									/ LINE_WIDTH_SCALE + RELATION_DISTANCE;
							j -= 2;
						}
						distance += (double) ((knownRelations.get(akteure[0])
								.get(akteure[1]).get(0) / 2.0) / LINE_WIDTH_SCALE);
					}
					double distanceScale = VennMakerView.vmcs.toJava2D(1.0);
					if (amount % 2 == 0)
					{
						distance *= -distanceScale;
					}
					else
					{
						distance *= distanceScale;
					}

					px.setLocation(startPointX + distance * normalizedEndStartY,
							startPointY + distance * normalizedEndStartX);
					py.setLocation(endPointX + distance * normalizedEndStartY,
							endPointY + distance * normalizedEndStartX);
				}
				else
				{
					if (!knownRelations.containsKey(akteure[0]))
						knownRelations.put(akteure[0],
								new HashMap<Akteur, Vector<Integer>>());
					knownRelations.get(akteure[0]).put(akteure[1],
							new Vector<Integer>());
					knownRelations
							.get(akteure[0])
							.get(akteure[1])
							.add(n.getRelationSizeVisualizer(
									currentAttributeTypeCollector, next).getSize(r, n));

					px.setLocation(startPointX, startPointY);
					py.setLocation(endPointX, endPointY);
				}

				Shape line = null;
				if (VennMaker.getInstance().getProject()
						.getIsDirected(r.getAttributeCollectorValue()))
				{
					line = new ArrowLine(px.getX(), px.getY(), py.getX(), py.getY());
				}
				else
				{
					line = new Line2D.Double(px.getX(), px.getY(), py.getX(),
							py.getY());
				}

				float size = n.getActiveRelationSizeVisualizer(
						currentAttributeTypeCollector).getSize(r, n)
						/ (LINE_WIDTH_SCALE / 2);

				// TODO: Increase performance or search for performant setStroke

				RelationDashVisualizer dashViz = n
						.getActiveRelationDashVisualizer(currentAttributeTypeCollector);

				if (currentHoverRelation == r)
				{

					size += 2;
					g.setStroke(new BasicStroke(VennMakerView.vmcs
							.toJava2D((2 + size) / LINE_WIDTH_SCALE), baseStroke
							.getEndCap(), baseStroke.getLineJoin(), baseStroke
							.getMiterLimit(), dashViz.getDashArray(r, n), baseStroke
							.getDashPhase()));
				}
				else
				{
					g.setStroke(new BasicStroke(VennMakerView.vmcs.toJava2D((size)
							/ LINE_WIDTH_SCALE), baseStroke.getEndCap(), baseStroke
							.getLineJoin(), baseStroke.getMiterLimit(), dashViz
							.getDashArray(r, n), baseStroke.getDashPhase()));
				}

				// performance hungrig - alternative finden
				// OctTree search bei VennMakerView.searchRelation
				// Only create if KEY not in map OR is updated
				// is updated includes => hover or deleted
				// this.visibleRelations.put(g.getStroke().createStrokedShape(
				// new Line2D.Double(px.getX(), px.getY(), py.getX(), py.getY())
				// ), r);
				// used for internal searchRelation
				this.searchVectors.add(new RelationVector(new Point((int) (py
						.getX()), (int) (py.getY())), r, new Point((int) px.getX(),
						(int) px.getY())));

				alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a
						* alpha);
				g.setComposite(alphaComp);
				// Draw Line
				g.draw(line);

				if (relationLabels != null && relationLabels.size() > 0)
				{

					Map<Akteur, Point2D[]> bottom0 = bottomRelations.get(akteure[0]);
					if (bottom0 == null)
						bottom0 = new HashMap<Akteur, Point2D[]>();
					Map<Akteur, Point2D[]> bottom1 = bottomRelations.get(akteure[1]);
					if (bottom1 == null)
						bottom1 = new HashMap<Akteur, Point2D[]>();

					Point2D[] startEndPoints = bottom0.get(akteure[1]);
					Point2D start0, end0;

					if (startEndPoints == null)
					{
						start0 = startPoint;
						end0 = endPoint;
					}
					else
					{
						start0 = startEndPoints[0];
						end0 = startEndPoints[1];
					}

					double lineWidth = (endPointX - startPointX);
					double lineHeight = (endPointY - startPointY);

					double ad = Math.abs(Math.toDegrees(Math.atan(lineWidth
							/ lineHeight)));

					boolean almost0 = (ad < DrawLineLabel.STRAIGHT_MAX);

					if ((almost0 && startPointX < start0.getX())
							|| (!almost0 && endPointY > end0.getY()))
					{
						end0 = new Point2D.Double(endPointX, endPointY);
						start0 = new Point2D.Double(startPointX, startPointY);
					}

					bottom0.put(akteure[1], new Point2D[] { start0, end0 });
					bottom1.put(akteure[0], new Point2D[] { start0, end0 });

					bottomRelations.put(akteure[0], bottom0);
					bottomRelations.put(akteure[1], bottom1);
				}

			}

			// TODO:
			if (relationLabels != null && relationLabels.size() > 0)
			{
				for (Akteur actor : n.getAkteure())
				{
					Map<Akteur, Point2D[]> bottom = bottomRelations.get(actor);
					if (bottom != null)
					{
						for (Akteur endAkteur : bottom.keySet())
						{
							Point2D startPoint2d = bottom.get(endAkteur)[0];
							Point2D endPoint2d = bottom.get(endAkteur)[1];

							Vector<Relation> relations = rm.getRelationsBetween(actor,
									endAkteur, n);

							double lineWidth = (endPoint2d.getX() - startPoint2d
									.getX());
							double lineHeight = (endPoint2d.getY() - startPoint2d
									.getY());

							double ad = Math.abs(Math.toDegrees(Math.atan(lineWidth
									/ lineHeight)));
							boolean almost0 = (ad <= DrawLineLabel.STRAIGHT_MAX);
							int yOffset = 0;

							Map<Relation, DrawLineLabel> lineLabels = new HashMap<Relation, DrawLineLabel>();

							for (Relation rel : relations)
							{
								Color c = n.getRelationColorVisualizer(
										rel.getAttributeCollectorValue(),
										rel.getAttributes(n).keySet().iterator().next())
										.getColor(rel, n);

								c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 52);

								Map<AttributeType, Object> attr = rel.getAttributes(n);
								StringBuffer sb = new StringBuffer(28 * attr.size()); // maxShown(25)
																										// +
																										// ...(3)
								for (AttributeType at : attr.keySet())
								{
									if (relationLabels.contains(at))
									{
										Object val = attr.get(at);
										if (val != null)
										{
											String s = val.toString();
											if (s.length() > 25)
											{
												s = s.substring(0, 24) + "...";

											}
											if (!s.equals(""))
											{
												sb.append(at.getLabel() + " : " + s + "\n");
											}
										}
									}
								}
								String text = sb.toString();
								if (text != null && !text.equals(""))
								{
									DrawLineLabel dll = new DrawLineLabel(startPoint2d,
											endPoint2d, text, Color.black, c);
									lineLabels.put(rel, dll);
									Rectangle rect = dll.getLabelDimensions(g);

									if (almost0)
										yOffset -= rect.getHeight() + 2;
								}
							}
							for (Relation rel : lineLabels.keySet())
							{
								DrawLineLabel dll = lineLabels.get(rel);
								Rectangle rect = dll.getLabelDimensions(g);
								dll.draw(g, yOffset);
								yOffset += rect.getHeight() + 2;
							}
						}
					}
				}
			}
		}
		knownRelations.clear();
		bottomRelations.clear();
	}

	private void paintSelection(Graphics2D g)
	{
		Rectangle2D.Double rect = (Double) currentMultiSelection.clone();
		if (rect.width >= 5 && rect.height >= 5)
		{
			Color oldColor = g.getColor();
			Stroke oldStroke = g.getStroke();
			Color c = new Color(255, 0, 0, 110);
			g.setColor(c);
			g.setStroke(new BasicStroke());
			if (temporaryMovement != null)
			{
				rect.x += temporaryMovement.getX();
				rect.y += temporaryMovement.getY();
			}

			g.draw(rect);
			g.drawString("Selection", (int) Math.round(rect.x),
					(int) Math.round(rect.y));
			g.setColor(oldColor);
			g.setStroke(oldStroke);
		}
	}

	private void paintRelation(Graphics2D g, Netzwerk n, float alpha)
	{
		/*
		 * Speichere alle durchlaufenen Relationen, um doppelte Zeichnungen zu
		 * vermeiden (Gesamtanzahl zwischen Akteur A und B ist gespeichert in der
		 * Groesse des Vektors, der die Relationsdicken enthaelt)
		 */
		Map<Akteur, Map<Akteur, Vector<Integer>>> knownRelations = new HashMap<Akteur, Map<Akteur, Vector<Integer>>>();

		/*
		 * Hier werden die Punkte der untersten relation gespeichert um die
		 * Relations-Label von dort an zu zeichnen.
		 */
		Map<Akteur, Map<Akteur, Point2D[]>> bottomRelations = new HashMap<Akteur, Map<Akteur, Point2D[]>>();

		List<AttributeType> relationLabels = VennMaker.getInstance().getProject()
				.getDisplayedAtRelation();

		Composite composite = g.getComposite();
		float a = 1f;

		/*
		 * Zeichne alle Kanten durch Schleifendurchlaeufe, erst ueber Akteure und
		 * dann der Relationen
		 */

		RelationMonitor.getInstance().update();

		Vector<Relation> activeRelationVector;

		if (this.relationActor != null)
			activeRelationVector = RelationMonitor.getInstance()
					.getRelationsFromAndToActor(relationActor, n);
		else
			activeRelationVector = null;

		for (final Relation relation : RelationMonitor.getInstance()
				.getNetworkRelations(n))
		{
			Akteur akteur = RelationMonitor.getInstance().getStartActor(relation);
			if (n.getAkteure().contains(relation.getAkteur()))
			{
				Akteur arrowAkteur = relation.getAkteur();

				/* when a temporary relation is drawn */
				if (this.relationActor != null)
				{
					if (activeRelationVector.contains(relation))
						a = 1f;
					else
						a = NONACTIVE_RELATION_ALPHA;
				}
				else
				{
					if (this.netzwerk.getMarkedActors() != null && isFilterActive)
					{
						if ((this.netzwerk.getMarkedActors().contains(akteur))
								&& (this.netzwerk.getMarkedActors()
										.contains(arrowAkteur)))
							a = 1f;
						else
						{
							a = 0.1f;
							netzwerk.addLockedRelation(relation);
						}
					}
				}

				/* Alter-Ego Kanten nur zeichnen, wenn Ego sichtbar ist */
				if (!((VennMaker.getInstance().getConfig().isEgoDisabled() && ((akteur
						.equals(n.getEgo())) || (arrowAkteur.equals(n.getEgo()))))))
				{

					double xS = VennMakerView.vmcs.toJava2D(akteur.getLocation(n))
							.getX();
					double yS = VennMakerView.vmcs.toJava2D(akteur.getLocation(n))
							.getY();
					/* Andockpunkt berechnen. */
					final Point2D arrowAkteurLocStart = VennMakerView.vmcs
							.toJava2D(akteur.getLocation(n));
					final double arrowAkteurSizeStart = VennMakerView.vmcs
							.toJava2D(akteur.getGroesse(n));

					final Point2D arrowAkteurLoc = VennMakerView.vmcs
							.toJava2D(arrowAkteur.getLocation(n));
					final double arrowAkteurSize = VennMakerView.vmcs
							.toJava2D(arrowAkteur.getGroesse(n));

					/*
					 * Calculate endpoint at second actor; -2 and +4 to create some
					 * extra space, so a possible arrowpoint is always visible
					 */
					Point2D endPoint = getIntersection(new Rectangle2D.Double(
							arrowAkteurLoc.getX() - arrowAkteurSize * .5 - 2,
							arrowAkteurLoc.getY() - arrowAkteurSize * .5 - 2,
							arrowAkteurSize + 4, arrowAkteurSize + 4), xS, yS);
					if (endPoint == null)
						continue;

					double xEnd = endPoint.getX();
					double yEnd = endPoint.getY();

					Point2D startPoint = getIntersection(new Rectangle2D.Double(
							arrowAkteurLocStart.getX() - arrowAkteurSizeStart * .5,
							arrowAkteurLocStart.getY() - arrowAkteurSizeStart * .5,
							arrowAkteurSizeStart, arrowAkteurSizeStart), xEnd, yEnd);
					if (startPoint == null)
						continue;
					double xStart = startPoint.getX();
					double yStart = startPoint.getY();

					float distance = VennMakerView.vmcs.toJava2D(2);

					/*
					 * Parallelverschiebung, falls bereits eine Relation an dieser
					 * Stelle ist. Jeweils um 10 pixel abwechselnd nach unten und
					 * oben.
					 */

					Akteur[] akteure = new Akteur[2];
					if ((!arrowAkteur.equals(n.getEgo()))
							&& (arrowAkteur.getId() < akteur.getId())
							|| (akteur.equals(n.getEgo())))
					{
						akteure[0] = arrowAkteur;
						akteure[1] = akteur;
					}
					else
					{
						akteure[0] = akteur;
						akteure[1] = arrowAkteur;
					}

					/* Suche, ob es eine solche Relation bereits gibt */
					if (knownRelations.containsKey(akteure[0])
							&& knownRelations.get(akteure[0]).containsKey(akteure[1]))
					{
						/*
						 * wenn es bereits Relationen mit diesen Akteuren gibt, dann
						 * fuege Relationsdicke hinzu, ansonsten lege erst neuen
						 * Vektor an
						 */
						int i = knownRelations.get(akteure[0]).get(akteure[1]).size();

						knownRelations
								.get(akteure[0])
								.get(akteure[1])
								.add(n.getRelationSizeVisualizer(
										relation.getAttributeCollectorValue(),
										relation.getAttributes(n).keySet().iterator()
												.next()).getSize(relation, n));

						double vektorlaenge = Math.sqrt((xEnd - xStart)
								* (xEnd - xStart) + (yEnd - yStart) * (yEnd - yStart));
						double xEinheitsvektor = ((-xEnd + xStart) / vektorlaenge);
						double yEinheitsvektor = ((yEnd - yStart) / vektorlaenge);

						/* Einheitsvektor normieren */
						if (xEinheitsvektor < 0)
						{
							xEinheitsvektor = -xEinheitsvektor;
							yEinheitsvektor = -yEinheitsvektor;
						}

						/* Abstand je nach Anzahl der Relationen regulieren */
						int j = i;

						distance = 0;
						if (j > 0)
						{
							/* half of the size of the actual relation */
							distance += (float) ((knownRelations.get(akteure[0])
									.get(akteure[1]).get(j) / 2.0)
									/ LINE_WIDTH_SCALE + RELATION_DISTANCE);
							j -= 2;
							while (j > 0)
							{
								/*
								 * full size of relations beetween actual relation an
								 * first relation
								 */
								distance += (float) (knownRelations.get(akteure[0])
										.get(akteure[1]).get(j))
										/ LINE_WIDTH_SCALE
										+ RELATION_DISTANCE;
								j -= 2;
							}
							/* Half of the relation in the middle */
							distance += (float) (knownRelations.get(akteure[0])
									.get(akteure[1]).get(0) / 2.0)
									/ LINE_WIDTH_SCALE;
						}

						/* adjustment on top or below the relation in the middle ? */
						if (i % 2 == 0)
							distance = distance * -VennMakerView.vmcs.toJava2D(1);
						else
							distance = distance * VennMakerView.vmcs.toJava2D(1);

						yEnd = yEnd + distance * xEinheitsvektor; // Multiplikation
						// mit dem
						// Normalenvektor
						xEnd = xEnd + distance * yEinheitsvektor;
						yStart = yStart + distance * xEinheitsvektor;
						xStart = xStart + distance * yEinheitsvektor;
					}
					/*
					 * No relations between current actors so far: add new vector
					 * with current relationsize as initial value
					 */
					else
					{
						if (!knownRelations.containsKey(akteure[0]))
							knownRelations.put(akteure[0],
									new HashMap<Akteur, Vector<Integer>>());
						knownRelations.get(akteure[0]).put(akteure[1],
								new Vector<Integer>());
						knownRelations
								.get(akteure[0])
								.get(akteure[1])
								.add(n.getRelationSizeVisualizer(
										relation.getAttributeCollectorValue(),
										relation.getAttributes(n).keySet().iterator()
												.next()).getSize(relation, n));
					}

					Shape line = null;
					if (VennMaker.getInstance().getProject()
							.getIsDirected(relation.getAttributeCollectorValue()))
						line = new ArrowLine(xStart, yStart, xEnd, yEnd);
					else
						line = new Line2D.Double(xStart, yStart, xEnd, yEnd);

					if ((n.getStatusFilter() == true)
							&& (n.getFilter(akteur) == false))
						alphaComp = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, 0.05f * a);
					else
						alphaComp = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, alpha * a);

					if ((n.getStatusFilter() == true)
							&& ((n.getFilter(akteure[0]) == true) || (n
									.getFilter(akteure[1]) == true)

							))
					{
						alphaComp = AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, alpha * a);
					}
					g.setComposite(alphaComp);

					// Draw relation.
					String currentAttributeTypeCollector = relation
							.getAttributeCollectorValue();

					g.setPaint(n.getRelationColorVisualizer(
							currentAttributeTypeCollector,
							relation.getAttributes(n).keySet().iterator().next())
							.getColor(relation, n));
					// g.setPaint(relation.getTyp().getColor());
					// BasicStroke stroke = relation.getTyp().getStroke();

					BasicStroke stroke = new BasicStroke();
					stroke = new BasicStroke(
							VennMakerView.vmcs.toJava2D((float) n
									.getRelationSizeVisualizer(
											currentAttributeTypeCollector,
											relation.getAttributes(n).keySet().iterator()
													.next()).getSize(relation, n)
									/ LINE_WIDTH_SCALE), stroke.getEndCap(),
							stroke.getLineJoin(), stroke.getMiterLimit(), n
									.getRelationDashVisualizer(
											currentAttributeTypeCollector,
											relation.getAttributes(n).keySet().iterator()
													.next()).getDashArray(relation, n),
							stroke.getDashPhase());

					if (currentHoverRelation == relation)
					{
						g.setStroke(new BasicStroke(VennMakerView.vmcs
								.toJava2D((2 + stroke.getLineWidth())
										/ LINE_WIDTH_SCALE), stroke.getEndCap(), stroke
								.getLineJoin(), stroke.getMiterLimit(), stroke
								.getDashArray(), stroke.getDashPhase()));
					}
					else
					{
						g.setStroke(new BasicStroke(VennMakerView.vmcs
								.toJava2D(stroke.getLineWidth() / LINE_WIDTH_SCALE),
								stroke.getEndCap(), stroke.getLineJoin(), stroke
										.getMiterLimit(), stroke.getDashArray(), stroke
										.getDashPhase()));
					}

					this.visibleRelations.put(stroke.createStrokedShape(line),
							relation);

					g.draw(line);
					g.setFont(g.getFont()
							.deriveFont(
									Font.PLAIN,
									VennMakerView.vmcs.toJava2D(3)
											* VennMaker.getInstance().getProject()
													.getTextZoom()));

					/**
					 * Find and save the smallest y or biggest x Relation (depending
					 * on if the Relation is straight up/down or not)
					 */
					Map<Akteur, Point2D[]> bottom0 = bottomRelations.get(akteure[0]);
					Map<Akteur, Point2D[]> bottom1 = bottomRelations.get(akteure[1]);

					if (bottom0 == null)
						bottom0 = new HashMap<Akteur, Point2D[]>();
					if (bottom1 == null)
						bottom1 = new HashMap<Akteur, Point2D[]>();

					Point2D[] startEndPoints = bottom0.get(akteure[1]);

					Point2D start0, end0;
					if (startEndPoints == null)
					{
						start0 = startPoint;
						end0 = endPoint;
					}
					else
					{
						start0 = startEndPoints[0];
						end0 = startEndPoints[1];
					}

					// only if labels should be drawn
					if (relationLabels != null && relationLabels.size() > 0)
					{
						// Check if relation is almost straight up / down (then take
						// the most left relation for starting
						// else the most bottom relation
						double lineWidth = (xEnd - xStart);
						double lineHeight = (yEnd - yStart);

						double ad = Math.abs(Math.toDegrees(Math.atan(lineWidth
								/ lineHeight)));
						/*
						 * indicates if the line is straight up or straight down
						 */
						boolean almost0 = (ad <= DrawLineLabel.STRAIGHT_MAX);

						/* if almost0 search for smallest x else search for heighest y */
						if ((almost0 && xStart < start0.getX())
								|| (!almost0 && yEnd > end0.getY()))
						{
							end0 = new Point2D.Double(xEnd, yEnd);
							start0 = new Point2D.Double(xStart, yStart);
						}

						/* update maps with new values */
						bottom0.put(akteure[1], new Point2D[] { start0, end0 });
						bottom1.put(akteure[0], new Point2D[] { start0, end0 });

						bottomRelations.put(akteure[0], bottom0);
						bottomRelations.put(akteure[1], bottom1);
					}
				}
			}
		}

		/**
		 * Now draw the line labels
		 */
		if (relationLabels != null && relationLabels.size() > 0)
		{
			RelationMonitor rm = RelationMonitor.getInstance();
			for (Akteur startAkteur : netzwerk.getAkteure())
			{
				Map<Akteur, Point2D[]> bottom = bottomRelations.get(startAkteur);

				if (bottom != null)
				{
					for (Akteur endAkteur : bottom.keySet())
					{
						Point2D startPoint = bottom.get(endAkteur)[0]; // Start-Punkt
																						// der
																						// "untersten"
																						// Relation
						Point2D endPoint = bottom.get(endAkteur)[1]; // End-Punkt der
																					// "untersten"
																					// Relation

						Vector<Relation> relations = rm.getRelationsBetween(
								startAkteur, endAkteur, netzwerk);

						// Check if relation is almost straight up / down (then take
						// the most left relation for starting
						// else the most bottom relation
						double lineWidth = (endPoint.getX() - startPoint.getX());
						double lineHeight = (endPoint.getY() - startPoint.getY());

						double ad = Math.abs(Math.toDegrees(Math.atan(lineWidth
								/ lineHeight)));
						/* indicates if the line is straight up or straight down */
						boolean almost0 = (ad <= DrawLineLabel.STRAIGHT_MAX);

						int yOffset = 0;

						/*
						 * Map to save the Line Labels first they are used to
						 * calculate the overall height of labels (if line is straight
						 * up/down) second they are used to actually draw the labels
						 */
						Map<Relation, DrawLineLabel> lineLabels = new HashMap<Relation, DrawLineLabel>();

						for (Relation relation : relations)
						{
							Color c = netzwerk
									.getRelationColorVisualizer(
											relation.getAttributeCollectorValue(),
											relation.getAttributes(n).keySet().iterator()
													.next()).getColor(relation, netzwerk);

							/* add transparency to color */
							c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 52);

							Map<AttributeType, Object> attr = relation
									.getAttributes(n);
							StringBuffer sb = new StringBuffer();
							for (AttributeType at : attr.keySet())
							{
								if (relationLabels.contains(at))
								{
									Object val = attr.get(at);
									if (val != null)
									{
										String s = val.toString();
										if (s.length() > 25)
											s = s.substring(0, 24) + "...";

										if (!s.equals(""))
											sb.append(at.getLabel() + " : " + s + "\n");
									}
								}
							}

							String text = sb.toString();
							// falls kein Attribut im Label auftaucht...
							if (text != null && !text.equals(""))
							{
								DrawLineLabel dll = new DrawLineLabel(startPoint,
										endPoint, text, Color.black, c);
								lineLabels.put(relation, dll);
								Rectangle r = dll.getLabelDimensions(g);

								if (almost0)
									yOffset -= r.getHeight() + 2;
							}
						}

						yOffset /= 2.0;

						/* NOW FINALLY DRAW THEM (with calculated offset) */
						for (Relation relation : lineLabels.keySet())
						{
							DrawLineLabel dll = lineLabels.get(relation);
							Rectangle r = dll.getLabelDimensions(g);
							dll.draw(g, yOffset);
							yOffset += r.getHeight() + 2;
						}
					}
				}
			}
		}
		knownRelations.clear();
		bottomRelations.clear();

		g.setComposite(composite);
	}

	/**
	 * Paint given actor on the given graphics context.
	 * 
	 * @param a
	 *           The actor to paint.
	 * @param g
	 *           The graphics context to paint on.
	 * @param n
	 *           The source network
	 * @param alpha
	 *           The transparency
	 */
	private void paintActor(Akteur a, Graphics2D g, Netzwerk n, float alpha)
	{

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				(float) alpha));

		Point2D actorsLocation = VennMakerView.vmcs.toJava2D(a.getLocation(n));
		
		if (actorsLocation == null){
			a.setLocation(n, new Point2D.Double(0.0, 0.0));
			System.out.println("paintActor: actorsLocation is null");
			return;
		}
		
		double groesse = Math.ceil(VennMakerView.vmcs.toJava2D(a.getGroesse(n)));
		
		
		if (g.getClipBounds() == null
				|| g.getClipBounds().intersects(
						actorsLocation.getX() 
						- groesse / 2.0,
						actorsLocation.getY() 
						- groesse / 2.0, groesse, groesse))
		{

			Image actorsIcon = actorsIconCache.get(a);
			// Cache empty, rescaling image from source
			if (actorsIcon == null)
			{
				String filename = n.getActorImageVisualizer().getImage(a, n);
				BufferedImage actorsImage = ImageOperations.loadActorImage(
						filename, groesse, imageScalingFactor);

				if (actorsImage == null)
					actorsImage = getDefaultActorImage((int) groesse);

				actorsIcon = ImageOperations.createActorIcon(actorsImage, groesse,
						imageScalingFactor);

				actorsImageCache.put(filename, actorsImage);
				actorsIconCache.put(a, actorsIcon);
			}

			if (debug)
			{
				Rectangle2D akteurBox = new Rectangle2D.Double(
						VennMakerView.vmcs.xToJava2D(a.getLocation(n).getX())
								- groesse * .5, VennMakerView.vmcs.yToJava2D(a
								.getLocation(n).getY()) - groesse * .5, groesse,
						groesse);
				g.setColor(Color.blue);
				g.draw(akteurBox);
			}

			// Verwendet Java2D
			// Akteur durch Icons darstellen
			if (actorsIcon != null)
			{

				/*
				 * Problem: Geklonte Akteure greifen auf gemeinsamen actorsIcon zu.
				 * Wenn sich nun die Icongroesse aendert, dann wirkt sich diese
				 * aenderung auf alle geklonten (gleiche) Akteure aus, die auf die
				 * gemeinsame Netzwerkkarte abgebildet werden. Dieses Problem tritt
				 * nur auf, wenn geklontes Netzwerk in den Hintergrund gezeichnet
				 * wird. Hier sollte eine bessere Loesung gefunden werden.
				 * 
				 * Daher:
				 */

				if (actorsIcon.getHeight(this) != a.getGroesse(n))
					g.drawImage(actorsIcon,
							(int) (actorsLocation.getX() - groesse / 2.0),
							(int) (actorsLocation.getY() - groesse / 2.0),
							(int) groesse, (int) groesse, this);
				else
					g.drawImage(actorsIcon,
							(int) (actorsLocation.getX() - groesse / 2.0),
							(int) (actorsLocation.getY() - groesse / 2.0), this);
			}

			// Beschriftung der Akteure

			// wenn die verschluessel aktiviert wurde:
			if (VennMaker.getInstance().getProject().isEncodeFlag() == true)
				VennMaker.getInstance().getConfig()
						.setLabelBehaviour(Config.LabelBehaviour.NUMBER);

			g.setFont(g.getFont().deriveFont(
					Font.PLAIN,
					VennMakerView.vmcs.toJava2D(3)
							* VennMaker.getInstance().getProject().getTextZoom()));

			int x = 0;
			int y = 0;
			int width = 0;

			int height = g.getFontMetrics().getAscent() - 4;
			List<AttributeType> displayTypes = VennMaker.getInstance()
					.getProject().getDisplayedAtActor();
			int rectHeight = height;
			if (displayTypes != null)
			{
				for (AttributeType at : displayTypes)
				{
					Object val = a.getAttributeValue(at, netzwerk);

					if (val != null)
					{
						String s = at.getLabel() + ": " + val.toString();
						if (s.length() > 25)
							s = s.substring(0, 24) + "...";

						int w = g.getFontMetrics().stringWidth(s);
						if (w > width)
							width = w;
						rectHeight += height + 4;
					}
				}
			}

			int tmpwidth = 0;
			int tmpheight = 0;

			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case ASIDE:
					tmpwidth = g.getFontMetrics().stringWidth("" + a.getName()); //$NON-NLS-1$
					tmpheight = g.getFontMetrics().getAscent() - 4;

					if (tmpwidth > width)
						width = tmpwidth;
					if (tmpheight > height)
						height = tmpheight;

					x = (int) (actorsLocation.getX() + .7 * groesse);
					y = (int) (actorsLocation.getY() + (height * 0.5));
					break;
				case ASIDE_LEFT:
					tmpwidth = g.getFontMetrics().stringWidth("" + a.getName()); //$NON-NLS-1$
					tmpheight = g.getFontMetrics().getAscent() - 4;

					if (tmpwidth > width)
						width = tmpwidth;
					if (tmpheight > height)
						height = tmpheight;

					x = (int) (actorsLocation.getX() - .7 * groesse - width);
					y = (int) (actorsLocation.getY() + (height * 0.5));
					break;
				case ON_TOP:
					tmpwidth = g.getFontMetrics().stringWidth("" + a.getName()); //$NON-NLS-1$
					tmpheight = g.getFontMetrics().getAscent() - 4;

					if (tmpwidth > width)
						width = tmpwidth;
					if (tmpheight > height)
						height = tmpheight;

					x = (int) (actorsLocation.getX() - (width * .5));
					y = (int) (actorsLocation.getY() + (height * .5));
					break;
				case UNDER:
					tmpwidth = g.getFontMetrics().stringWidth("" + a.getName()); //$NON-NLS-1$
					tmpheight = g.getFontMetrics().getAscent() - 4;

					if (tmpwidth > width)
						width = tmpwidth;
					if (tmpheight > height)
						height = tmpheight;

					x = (int) (actorsLocation.getX() - (width * .5));
					y = (int) (actorsLocation.getY() + (groesse * .6) + height);
					break;
				case NUMBER:
					tmpwidth = g.getFontMetrics().stringWidth("" + a.getId()); //$NON-NLS-1$
					tmpheight = g.getFontMetrics().getAscent() - 4;

					if (tmpwidth > width)
						width = tmpwidth;
					if (tmpheight > height)
						height = tmpheight;

					x = (int) (actorsLocation.getX() - (width * .5));
					y = (int) (actorsLocation.getY() + (groesse * .5) + height);
					break;
				case NO_LABEL:
					x = (int) (actorsLocation.getX() - (width * .5));
					y = (int) (actorsLocation.getY() + (groesse * .5) + height);
					break;

				default:
					break;
			}

			if (((a.getName().length() > 0) || (displayTypes != null))
					&& (VennMaker.getInstance().getConfig().getLabelBehaviour() != VennMaker
							.getInstance().getConfig().getLabelBehaviour().NO_LABEL))
			{
		/*		if (a == n.getEgo())
					g.setColor(COLOR_LABEL_BOX_EGO);
				else
					g.setColor(COLOR_LABEL_BOX);

				g.fillRect(x - (int) VennMakerView.vmcs.toJava2D(2), y - height
						- (int) VennMakerView.vmcs.toJava2D(2), width
						+ (int) VennMakerView.vmcs.toJava2D(4), rectHeight
						+ (int) VennMakerView.vmcs.toJava2D(4));
*/
				g.setColor(COLOR_LABEL);

				String drawText = a.getName();

				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NO_LABEL:

						/*
						 * if (displayTypes != null) { for (AttributeType at :
						 * displayTypes) { Object val = a.getAttributeValue(at,
						 * netzwerk); if (val != null) { String s = val.toString(); if
						 * (s.length() > 25) s = s.substring(0, 24) + "..."; y +=
						 * height + 4; DrawText.create(g, s, x, y, false); } } }
						 */

						break;
					case NUMBER:
						drawText = "" + a.getId();
					default:
						DrawText.create(g, drawText, (int) (x), (int) (y), false);

						if (displayTypes != null)
						{
							for (AttributeType at : displayTypes)
							{
								Object val = a.getAttributeValue(at, netzwerk);
								if (val != null)
								{
									String s = at.getLabel() + ": " + val.toString();
									if (s.length() > 25)
										s = s.substring(0, 24) + "...";
									y += height + 4;
									DrawText.create(g, s, x, y, false);
								}
							}
						}
						break;
				}

			}
		}
	}

	/**
	 * Paint given actor comment on the given graphics context.
	 * 
	 * @param a
	 *           The actor to paint.
	 * @param g
	 *           The graphics context to paint on.
	 */
	private void paintActorComment(Akteur a, Graphics2D g)
	{
		// Point2D actorsLocation = VennMakerView.vmcs.toJava2D(a
		// .getLocation(this.netzwerk));
		//
		// double groesse = Math.ceil(VennMakerView.vmcs.toJava2D(a
		// .getGroesse(this.netzwerk)));
		//
		// g.setFont(g.getFont().deriveFont(
		// Font.ITALIC,
		// VennMakerView.vmcs.toJava2D(3)
		// * VennMaker.getInstance().getProject().getTextZoom()));
		//
		// int x = 0;
		// int y = 0;
		// int width = g.getFontMetrics().stringWidth(a.getName());
		// int height = g.getFontMetrics().getAscent();
		//
		// switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
		// {
		// case ASIDE:
		// x = (int) (actorsLocation.getX() + .7 * groesse);
		// y = (int) (actorsLocation.getY() + (height * 0.5));
		// break;
		// case ON_TOP:
		// x = (int) (actorsLocation.getX() - (width * .5));
		// y = (int) (actorsLocation.getY() + (height * .5));
		// break;
		// case UNDER:
		// x = (int) (actorsLocation.getX() - (width * .5));
		// y = (int) (actorsLocation.getY() + (groesse * .5) + height);
		// break;
		// case NUMBER:
		// x = (int) (actorsLocation.getX() - (width * .5));
		// y = (int) (actorsLocation.getY() + (groesse * .5) + height);
		// break;
		// case NO_LABEL:
		// x = (int) (actorsLocation.getX() - (width * .5));
		// y = (int) (actorsLocation.getY() + (groesse * .5) + height);
		// default:
		// break;
		// }
		// if (a.getKommentar() != null)
		// {
		// DrawText.create(g, a.getKommentar().toString(), (int) (x), (int) (y)
		// + height, true);
		// }
	}

	/**
	 * Visualisiert Akteurs-Attribute mithilfe eines Kuchendiagramms
	 * 
	 * @param a
	 *           : Akteur
	 * @param g
	 *           : Graphics2D
	 * @param n
	 *           : Netzwerk
	 */
	private void paintActorSector(Akteur a, Graphics2D g, Netzwerk n, float alpha)
	{
		Point2D actorsLocation = VennMakerView.vmcs.toJava2D(a.getLocation(n));

		double groesse = VennMakerView.vmcs.toJava2D(a.getGroesse(n));

		Map<AttributeType, Object> typeAndSelection;

		if (n.getActorSectorVisualizer() != null)
		{

			typeAndSelection = new HashMap<AttributeType, Object>(n
					.getActorSectorVisualizer().getAttributeTypesAndSelection());

			double anzahlSektoren = 0;

			// Schauen, wieviele Sektoren sollen fuer diesen Akteur gezeichnet
			// werden sollen
			for (AttributeType o : VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete())
			{
				if (typeAndSelection.containsKey(o))
					if (a.getAttributeValue(o, n) != null)
						if (a.getAttributeValue(o, n).equals(typeAndSelection.get(o)))
						{
							anzahlSektoren++;
						}

			}

			if (anzahlSektoren > 0)
			{
				double allSectorWidth = 1 / anzahlSektoren;
				double totalWidth = 0.0;

				Composite tmp = g.getComposite();
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) (alpha) * (float) (0.5f)));

				double sectorWidth = 1 / anzahlSektoren;

				for (int i = 0; i < anzahlSektoren; i++)
				{
					int q = 0;
					for (AttributeType o : VennMaker.getInstance().getProject()
							.getAttributeTypesDiscrete())
					{
						q++;
						if (typeAndSelection.containsKey(o))
							if (a.getAttributeValue(o, n) != null)
								if (a.getAttributeValue(o, n).equals(
										typeAndSelection.get(o)))
								{

									ActorPie pie = new ActorPie(actorsLocation.getX(),
											actorsLocation.getY(), 0, groesse,
											360.0 * sectorWidth, 360.0 * totalWidth,
											o.getLabel(),
											(int) (VennMakerView.vmcs.toJava2D(2)));

									g.setColor((Color) n.getActorSectorVisualizer()
											.getSectorColor().get(o));

									pie.paintComponent(g);

									totalWidth += allSectorWidth;

								}

					}

				}

				g.setComposite(tmp);
			}
		}

	}

	/**
	 * Draws the concentric circles
	 * 
	 * @param anzahl
	 *           the number of circles
	 * @param graphics
	 *           The graphics context
	 */
	private void paintCircles(BackgroundInfo bginfo, final Graphics graphics)
	{
		drawnCircles.clear();
		if (bginfo.getNumCircles() > 0)
		{
			final Graphics2D g = (Graphics2D) graphics.create();

			double x_mid = 0;
			double y_mid = 0;

			float egoSpace = VennMaker.getInstance().getProject()
					.getViewAreaHeight() / 10;

			g.setColor(COLOR_CONCENTRIC_CIRCLES);
			g.setFont(g.getFont().deriveFont(
					Font.PLAIN,
					VennMakerView.vmcs.toJava2D(4)
							* VennMaker.getInstance().getProject().getTextZoom()));

			double minLength = Math.min(VennMaker.getInstance().getProject()
					.getViewAreaHeight()
					* VennMaker.getInstance().getConfig().getViewAreaRatio() / 2.0,
					VennMaker.getInstance().getProject().getViewAreaHeight() / 2.0);
			minLength = minLength - 1; // Ein wenig Abstand zum Kartenrand

			List<String> circles = bginfo.getCircles();
			int numCircles = bginfo.getNumCircles();
			for (int i = 1; i <= numCircles; ++i)
			{
				double radius = egoSpace + ((minLength - egoSpace) / numCircles)
						* i;
				Ellipse2D e = new Ellipse2D.Double(
						VennMakerView.vmcs.xToJava2D(x_mid - (int) radius),
						VennMakerView.vmcs.yToJava2D(y_mid - (int) radius),
						VennMakerView.vmcs.toJava2D(2 * (int) radius),
						VennMakerView.vmcs.toJava2D(2 * (int) radius));
				g.draw(e);
				drawnCircles.add(e);

				// der 0-te Kreis ist der Kreis direkt um Ego und wird an
				// anderer
				// Stelle nicht mitgezÃ¤hlt
				if (i != 0 && (i - 1) < circles.size()
						&& circles.get(i - 1) != null)
				{
					FontMetrics fm = getFontMetrics(g.getFont());
					g.drawString(
							circles.get(i - 1),
							VennMakerView.vmcs.xToJava2D(x_mid)
									- fm.stringWidth(circles.get(i - 1)) / 2,
							VennMakerView.vmcs.yToJava2D(y_mid + radius - 3));
				}
			}
		}
	}

	private void paintSektoren(BackgroundInfo bginfo, final Graphics graphics)
	{
		if (bginfo.getNumSectors() > 0)
		{
			final Graphics2D g = (Graphics2D) graphics.create();

			double x_mid = 0;
			double y_mid = 0;

			g.setColor(COLOR_SECTOR_LINE);
			double minLength = Math.min(VennMaker.getInstance().getProject()
					.getViewAreaHeight()
					* VennMaker.getInstance().getConfig().getViewAreaRatio() / 2.0,
					VennMaker.getInstance().getProject().getViewAreaHeight() / 2.0);

			minLength = minLength - 1; // Ein wenig Abstand zum Kartenrand

			double totalWidth = 0.0;
			for (int i = 0; i < bginfo.getNumSectors(); ++i)
			{
				LabelledPie sectorPie = new LabelledPie(
						VennMakerView.vmcs.xToJava2D(x_mid),
						VennMakerView.vmcs.yToJava2D(y_mid), 0,
						VennMakerView.vmcs.toJava2D(minLength),
						360.0 * bginfo.getSector(i).width, 360.0 * totalWidth,
						bginfo.getSector(i).label,
						(int) (VennMakerView.vmcs.toJava2D(6) * VennMaker
								.getInstance().getProject().getTextZoom()));
				g.setColor(bginfo.getSector(i).sectorColor);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) bginfo.alpha));
				sectorPie.paintComponent(g);

				// Anfang des Sektors wird durch totalWidth festgelegt
				totalWidth += bginfo.getSector(i).width;
			}

		}
	}

	/**
	 * Paints the vennmaker logo onto the view area.
	 * 
	 * @param graphics
	 *           The view graphics context
	 */
	private void paintLogo(final Graphics graphics)
	{
		int groesse = 45;
		ImageObserver imgObserver = new JPanel();
		BufferedImage image = null;
		BufferedImageTranscoder t = new BufferedImageTranscoder();
		t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, 50f);

		final Graphics2D g = (Graphics2D) graphics.create();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		final int x = (int) (getViewArea().getX()
				+ (getViewArea().getWidth() / 2) - VennMakerView.vmcs
				.toJava2D(groesse / 2));
		final int y = (int) (getViewArea().getY()
				+ (getViewArea().getHeight() / 2) + VennMakerView.vmcs
				.toJava2D(groesse / 2));

		try
		{
			InputStream is = new FileInputStream("icons/intern/VennMaker.svg"); //$NON-NLS-1$
			TranscoderInput ti = new TranscoderInput(is);
			t.transcode(ti, null);
			image = t.getImage();

			Image icon = image.getScaledInstance(
					(int) (VennMakerView.vmcs.toJava2D(groesse)),
					(int) (VennMakerView.vmcs.toJava2D(groesse)) * image.getHeight()
							/ image.getWidth(), BufferedImage.SCALE_SMOOTH);
			g.drawImage(icon, x, y, imgObserver);
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (TranscoderException e)
		{
			e.printStackTrace();
		}
		g.setFont(g.getFont().deriveFont(Font.PLAIN,
				VennMakerView.vmcs.toJava2D(4)));
		g.setColor(Color.black);
		g.setFont(g.getFont().deriveFont(
				Font.PLAIN,
				VennMakerView.vmcs.toJava2D(3)
						* VennMaker.getInstance().getProject().getTextZoom()));
		String text = "www.vennmaker.com"; //$NON-NLS-1$
		g.drawString(text, x + VennMakerView.vmcs.toJava2D(groesse / 2)
				- (g.getFontMetrics().stringWidth(text) / 2), y
				+ VennMakerView.vmcs.toJava2D(25) + g.getFontMetrics().getHeight());

		String text2 = "Version " + VennMaker.VERSION + " (trial version)"; //$NON-NLS-1$ //$NON-NLS-2$
		g.drawString(
				text2,
				x + VennMakerView.vmcs.toJava2D(groesse / 2)
						- (g.getFontMetrics().stringWidth(text2) / 2),
				y + VennMakerView.vmcs.toJava2D(25) + 2
						* (g.getFontMetrics().getHeight()));

	}

	/**
	 * Creates a new actor and places it at position (x2, y2) [java2d-space].
	 * Note that a valid default actor type has to be selected prior to this
	 * operation! If (x2,y2) is outside the valid view area, the behaviour of
	 * this method is undefined!
	 * 
	 * @param x2
	 *           x-coordinate
	 * @param y2
	 *           y-coordinate
	 * 
	 * 
	 */
	public Akteur createNewActorByAttribute(final double x2, final double y2)
	{
		AttributeType type = VennMaker.getInstance().getProject()
				.getMainGeneratorType("ACTOR");
		Object value = VennMaker.getInstance().getMainGeneratorValue();
		assert (value != null);

		Akteur akteur = new Akteur(""); //$NON-NLS-1$
		if (type != null)
			akteur.setAttributeValue(type, VennMaker.getInstance().getProject()
					.getCurrentNetzwerk(), value);

		// Check valid edit area!
		if (isActorAllowed(akteur, x2, y2))
		{
			String result = JOptionPane.showInputDialog(this,
					Messages.getString("VennMaker.Actor_Naming")); //$NON-NLS-1$

			if (result != null)
			{
				akteur.setName(result);

				boolean isDup = false;
				for (Akteur a : VennMaker.getInstance().getProject().getAkteure())
				{
					if (a.getName().equals(akteur.getName()))
					{
						isDup = true;
						break;
					}
				}
				if (isDup)
					switch (VennMaker.getInstance().getConfig()
							.getDuplicateBehaviour())
					{
						case NOT_ALLOWED:
							JOptionPane.showMessageDialog(null,
									Messages.getString("VennMaker.No_Duplicate")); //$NON-NLS-1$
							return null;
						case ALLOWED:
							break;
						case ASK_USER:
							int val = JOptionPane
									.showConfirmDialog(
											null,
											Messages.getString("VennMaker.Already_Same"), Messages //$NON-NLS-1$
													.getString("VennMaker.Duplicate_Actor"), //$NON-NLS-1$
											JOptionPane.YES_NO_OPTION);

							if (val == JOptionPane.YES_OPTION)
							{
								break;
							}
							// eigentlich Ã¼berlÃ¼ssige abfrage, aber sicher ist
							// sicher
							else if (val == JOptionPane.NO_OPTION)
							{
								return null;
							}
						default:
							assert (false);
					}

				Point2D p = VennMakerView.vmcs.toVMCS(new Point2D.Double(x2, y2));
				// Adding allowed!
				ComplexEvent event = new ComplexEvent(
						Messages.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
				event.addEvent(new NewActorEvent(akteur));
				event.addEvent(new AddActorEvent(akteur, netzwerk, p));

				double x_mid = 0;
				double y_mid = 0;

				ComplexEvent dummyEvent = addSectorCircleEvents(event, akteur,
						x_mid, y_mid, p.getX(), p.getY());

				if (dummyEvent != null)
					event = dummyEvent;
				EventProcessor.getInstance().fireEvent(event);
			}
			else
				akteur = null;
		}
		return akteur;
	}

	/**
	 * Returns <code>true</code> if an actor can be placed on postition (x2,y2)
	 * in java2d-coordinate system.
	 * 
	 * @param actor
	 *           the actor that is to be placed
	 * @param x2
	 *           x-coordinate
	 * @param y2
	 *           y-coordinate
	 * @return <code>false</code> if (x2,y2) is outside the editable view area.
	 */
	public boolean isActorAllowed(Akteur actor, double x2, double y2)
	{
		final double aSize = actor.getGroesse(this.netzwerk) / 2.0;
		return (getViewArea().contains(x2 - aSize, y2 - aSize, aSize + aSize,
				aSize + aSize));
	}

	/**
	 * Invoked when an actor is to be added to the view. The actor selection is
	 * already stored in the corresponding instance of this class via
	 * <code>setActualActor</code>. If (x2,y2) is outside the view area the
	 * behavior of this method is undefined.
	 * 
	 * @param x2
	 *           x-coordinate
	 * @param y2
	 *           y-coordinate
	 */
	public Akteur addActor(final double x2, final double y2)
	{
		Akteur a = null;
		// Check valid edit area!
		if (isActorAllowed(VennMaker.getInstance().getCurrentActor(), x2, y2))
		{
			a = VennMaker.getInstance().getCurrentActor();

			AddActorEvent event = new AddActorEvent(a, netzwerk,
					VennMakerView.vmcs.toVMCS(new Point2D.Double(x2, y2)));
			/*
			 * Falls ein Sektoren oder Kreise bestehen wird ein ComplexEvent
			 * ausgefuehrt
			 */

			ComplexEvent ce = new ComplexEvent(
					Messages.getString("VennMaker.CreateActorInSector")); //$NON-NLS-1$
			ce.addEvent(event);

			double x_mid = 0;
			double y_mid = 0;

			Point2D p = VennMakerView.vmcs.toVMCS(new Point2D.Double(x2, y2));
			addSectorCircleEvents(ce, a, x_mid, y_mid, p.getX(), p.getY());

			if (ce != null)
				EventProcessor.getInstance().fireEvent(ce);
			else
				EventProcessor.getInstance().fireEvent(event);

			VennMaker.getInstance().setCurrentActor(null);
		}
		else
		{
			System.err.println(Messages.getString("VennMaker.Actor_Outside")); //$NON-NLS-1$
			a = null;
		}
		return a;
	}

	/**
	 * 
	 * Methode um neu erstellten oder verschobenen Akteuren ggf.
	 * SetAttributeEvents hinzuzufÃ¼gen fÃ¼r die Sektoren oder Kreise in die
	 * ein Akteur geschoben oder in denen er erstellt wird
	 * 
	 * @param ce
	 *           - ComplexEvent
	 * @param a
	 *           - Akteur fÃ¼r den die Events gelten sollen
	 * @param x_mid
	 *           - X Position des Zentrums (beim Verschieben vorrausberechnen
	 *           !!!)
	 * @param y_mid
	 *           - Y Position des Zentrums (beim Verschieben vorrausberechnen
	 *           !!!)
	 * @param actorX
	 *           - X Position des Akteurs (beim Verschieben vorrausberechnen !!!)
	 * @param actorY
	 *           - Y Position des Akteurs (beim Verschieben vorrausberechnen !!!)
	 * @return <code>null</code> falls kein Event hinzugefÃ¼gt. Sonst altes
	 *         erweitertes ComplexEvent
	 */

	private ComplexEvent addSectorCircleEvents(ComplexEvent ce, Akteur a,
			double x_mid, double y_mid, double actorX, double actorY)
	{
		boolean sectorEventAdded = false, circleEventAdded = false;

		if (a == null)
			return null;

		// SEKTOREN DA ?
		AttributeType sectorAttribute = netzwerk.getHintergrund()
				.getSectorAttribute();
		if (sectorAttribute != null)
		{
			int max = sectorAttribute.getPredefinedValues().length;
			int sectorNum = getSector(x_mid, y_mid, actorX, actorY);
			Object oldVal = a.getAttributeValue(sectorAttribute, netzwerk);

			if (sectorNum >= 0 && sectorNum < max)
			{
				Object newVal = netzwerk.getHintergrund().getSector(sectorNum).label;

				if (oldVal == null || !oldVal.equals(newVal))
				{
					ce.addEvent(new SetAttributeEvent(a, sectorAttribute, netzwerk,
							newVal));
					sectorEventAdded = true;
				}
			}
			else if (oldVal != null)
			{
				ce.addEvent(new SetAttributeEvent(a, sectorAttribute, netzwerk,
						null));
				sectorEventAdded = true;
			}
		}

		// KREISE DA ?
		AttributeType circleAttribute = netzwerk.getHintergrund()
				.getCircleAttribute();

		if (circleAttribute != null)
		{
			List<String> circles = netzwerk.getHintergrund().getCircles();

			int max = circles.size();

			int circleNum = getCircle(x_mid, y_mid, actorX, actorY) - 1;

			Object oldVal = a.getAttributeValue(circleAttribute, netzwerk);

			if (circleNum >= 0 && circleNum < max)
			{
				Object newVal = circles.get(circleNum);

				/* nur bei Veraenderung*/
				if (oldVal == null || !oldVal.equals(newVal))
				{
					ce.addEvent(new SetAttributeEvent(a, circleAttribute, netzwerk,
							newVal));
					circleEventAdded = true;
				}
			}
			else if (oldVal != null)
			{
				ce.addEvent(new SetAttributeEvent(a, circleAttribute, netzwerk,
						null));
				circleEventAdded = true;
			}
		}
		return ce;
		/*
		 * if (circleEventAdded || sectorEventAdded) return ce; else return ce;
		 */
	}

	/**
	 * To get the circle index of a specific position, relative to the center of
	 * circles.
	 * 
	 * @param x_m
	 *           - x coordinate of the center (x of EGO)
	 * @param y_m
	 *           - y coordinate of the center (y of EGO)
	 * 
	 * @param x
	 *           - x coordinate of the destinated position
	 * @param y
	 *           - y coordinate of the destinated position
	 * 
	 * @return circle index of the position (x,y)
	 */
	public int getCircle(double x_m, double y_m, double x, double y)
	{
		return netzwerk.getCircle(x_m, y_m, x, y);
	}

	/**
	 * To get the sector index of a specific position, relative to the center of
	 * sectors.
	 * 
	 * @param x_m
	 *           - x coordinate of the center (x of EGO)
	 * @param y_m
	 *           - y coordinate of the center (y of EGO)
	 * 
	 * @param x
	 *           - x coordinate of the destinated position
	 * @param y
	 *           - y coordinate of the destinated position
	 * 
	 * @return sector index of the position (x,y)
	 */
	public int getSector(double x_m, double y_m, double x, double y)
	{
		return netzwerk.getSector(x_m, y_m, x, y);
	}

	/**
	 * Returns the relation object that is available at position (x,y) in java2d
	 * space.
	 * 
	 * @param x
	 *           the x-coordinate
	 * @param y
	 *           the y-coordinate
	 * @return <code>null</code> if no such relation can be found
	 */
	public Relation searchRelation(int x, int y)
	{
		Rectangle2D.Double px1 = new Rectangle2D.Double(x - 5, y - 5, 10, 10);

		for (Shape s : this.visibleRelations.keySet())
		{
			if (s.intersects(px1))
			{
				if (!netzwerk.isLocked(this.visibleRelations.get(s)))
				{
					return this.visibleRelations.get(s);
				}
			}
		}
		return null;
	}

	/**
	 * Calculates the point of intersection and returns the a given relation or
	 * null
	 * 
	 * @param x
	 *           x-position of Circle
	 * @param y
	 *           y-position of Circle
	 * @return first unlocked relation in searchVectors within radius of mouse
	 *         coordinates
	 */
	public Relation searchRelationPerf(int x, int y)
	{
		// size of mouse radius
		final int radius = 3;

		for (final RelationVector v : searchVectors)
		{
			final int kx = v.start.x - x;
			final int ky = v.start.y - y;
			final int c = kx * kx + ky * ky - radius * radius;
			final int a = v.direction.x * v.direction.x + v.direction.y
					* v.direction.y;
			final int b = 2 * v.direction.x * kx + 2 * v.direction.y * ky;
			double p = (double) b / a;
			double p_square = p * p;
			final double q = (double) c / a;

			if (p_square - 4 * q > 0)
			{
				final double t1, t2;

				final double totalDisk = Math.sqrt(p_square / 4 - q);
				final double premitter = -p / 2;
				t1 = premitter + totalDisk;
				t2 = premitter - totalDisk;

				if ((t1 <= 1.0 || t2 <= 1.0) && (t1 >= 0 && t2 >= 0))
				{

					if (!netzwerk.isLocked(v.relation))
					{
						return v.relation;
					}
				}
			}
		}
		return null;

	}

	/**
	 * Returns the actor that is on position (x,y) in java2d space.
	 * 
	 * @param x
	 *           x-cooordinate
	 * @param y
	 *           y-coordinate
	 * @return The actor or <code>null</code> if none could be found.
	 */
	public Akteur searchAkteur(int x, int y)
	{
		Point2D pt = new Point2D.Double(x, y);

		/* reverse the order of all actors, so last added actors show first */
		ArrayList<Akteur> temp = new ArrayList<Akteur>(this.netzwerk.getAkteure());
		Collections.reverse(temp);

		for (final Akteur akteur : temp)
		{
			final float groesse = VennMakerView.vmcs.toJava2D(akteur
					.getGroesse(netzwerk));

			if (akteur.getLocation(this.netzwerk) != null)
			{
				Rectangle2D akteurBox = new Rectangle2D.Double(
						VennMakerView.vmcs.xToJava2D(akteur.getLocation(this.netzwerk)
								.getX()) - groesse * .5,
						VennMakerView.vmcs.yToJava2D(akteur.getLocation(this.netzwerk)
								.getY()) - groesse * .5, groesse, groesse);
	
				if (akteurBox.contains(pt) && !netzwerk.isLocked(akteur))
				{
					return akteur;
				}
			}
		}

		return null;
	}

	/**
	 * @return the netzwerk
	 */
	public final Netzwerk getNetzwerk()
	{
		return netzwerk;
	}

	/**
	 * @return the VennMakerCoordinateSystem
	 */
	public final static VennMakerCoordinateSystem getVmcs()
	{
		return vmcs;
	}

	/**
	 * Methode zur Darstellung einer Legende in der linken unteren Ecke
	 * 
	 * @param graphics
	 * 
	 */
	// private void paintLegend(final Graphics graphics)
	// {
	// double x, y;
	// Graphics2D g = (Graphics2D) graphics.create();
	// g.setPaint(Color.BLACK);
	// g.setFont(g.getFont().deriveFont(
	// Font.PLAIN,
	// VennMakerView.vmcs.toJava2D(3)
	// * VennMaker.getInstance().getProject().getTextZoom()));
	// ImageObserver imgObserver = new JPanel();
	//
	// // Symbol Size Zeichnen
	// // --------------------
	// ArrayList<String> bes = new ArrayList<String>();
	// ArrayList<Integer> sizes = new ArrayList<Integer>();
	// String imgFileName = null;
	// AttributeType at = netzwerk.getActorSizeVisualizer().getAttributeType();
	// for (Akteur a : this.netzwerk.getAkteure())
	// {
	// String actorBes = (String) a.getAttributeValue(at, netzwerk);
	// if (actorBes == null)
	//				actorBes = ""; //$NON-NLS-1$
	//
	// int actorSize = this.netzwerk.getActorSizeVisualizer().getSize(a,
	// this.netzwerk);
	//
	// if (imgFileName == null || imgFileName.equals(""))
	// imgFileName = this.netzwerk.getActorImageVisualizer().getImage(a,
	// this.netzwerk);
	//
	// boolean contains = false;
	//
	// for (String s : bes)
	// {
	// if (s.equals(actorBes))
	// contains = true;
	// }
	//
	// if (!contains)
	// {
	// int pos = -1;
	// boolean found = false;
	// Integer newSize = new Integer(actorSize);
	//
	// for (int u = 0; u < sizes.size(); u++)
	// {
	// if (!found && sizes.get(u).intValue() > actorSize)
	// {
	// found = true;
	// pos = u;
	// }
	// }
	//
	// // wenn alle vorhandenen kleiner sind werden "bez" und "newI" am
	// // jeweiligen Ende eingefuegt
	// if (pos == -1)
	// {
	// sizes.add(newSize);
	// bes.add(actorBes);
	// }
	// else
	// // Sonst werden sie an die richtige Stelle einsortiert
	// {
	// Integer intBefore = newSize;
	// String stringBefore = actorBes;
	// Integer intNow;
	// String stringNow;
	//
	// for (int u = pos; u < sizes.size(); u++)
	// {
	// intNow = sizes.get(u);
	// stringNow = bes.get(u);
	//
	// sizes.set(u, intBefore);
	// bes.set(u, stringBefore);
	//
	// intBefore = intNow;
	// stringBefore = stringNow;
	// }
	//
	// sizes.add(intBefore);
	// bes.add(stringBefore);
	// }
	// }
	// }
	//
	// // Image des Egos laden
	// InputStream is;
	// BufferedImage actorsImage = null;
	// TranscoderInput ti;
	// BufferedImageTranscoder t = new BufferedImageTranscoder();
	// float biggestSize = vmcs.toJava2D(sizes.size() > 0 ? sizes.get(sizes
	// .size() - 1) : 0);
	//
	// float egoGroesse = 30.0f;
	// if (netzwerk.getEgo() != null)
	// {
	// egoGroesse = netzwerk.getEgo().getGroesse(netzwerk);
	// }
	//
	// double groesse = Math.ceil(biggestSize == 0 ? vmcs.toJava2D(egoGroesse)
	// : biggestSize);
	//
	// // t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT,
	// // (float) Math.round(groesse * imageScalingFactor));
	// // try
	// // {
	// // is = new FileInputStream(imgFileName);
	// // ti = new TranscoderInput(is);
	// // t.transcode(ti, null);
	// // actorsImage = t.getImage();
	// // } catch (FileNotFoundException e)
	// // {
	// // } catch (TranscoderException e)
	// // {
	// // e.printStackTrace();
	// // }
	//
	// actorsImage = ImageOperations.loadActorImage(imgFileName, groesse,
	// imageScalingFactor);
	//
	// if (actorsImage == null)
	// actorsImage = getDefaultActorImage((int) groesse);
	//
	// double curSize = 0;
	// double circleOffset = 0;
	// String curBes = null;
	// float textZoom = VennMaker.getInstance().getProject().getTextZoom();
	// int fontSize = g.getFont().getSize();
	// boolean first = true;
	// double yUeber = 0;
	//
	// x = getViewArea().getMinX() + vmcs.toJava2D(2);
	// y = getViewArea().getMaxY() - vmcs.toJava2D(2);
	//
	// // Variablen fuer die "Knick-Linie"
	// double kLineEndX = 0, kLineEndY;
	// /** describes the last endpoint, to measure correct distances */
	// double lastEndY = y - (fontSize * (sizes.size() - 1));
	//
	// for (int i = (sizes.size() - 1); i >= 0; i--)
	// {
	// curSize = vmcs.toJava2D(sizes.get(i));
	// curBes = bes.get(i);
	//
	// circleOffset = (biggestSize - curSize) / 2;
	//
	// if (actorsImage == null)
	// {
	// // Default - Bild
	// actorsImage = new BufferedImage((int) curSize, (int) curSize,
	// BufferedImage.TYPE_4BYTE_ABGR);
	//
	// Graphics2D gi = actorsImage.createGraphics();
	// gi.setColor(Color.gray);
	// gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// gi.setStroke(new BasicStroke(3.0f));
	// gi.draw(new Ellipse2D.Double(2, 2, curSize - 4, curSize - 4));
	// gi.drawLine((int) (curSize / 2), 2, (int) (curSize / 2),
	// (int) curSize - 4);
	// gi.drawLine(2, (int) (curSize / 2), (int) curSize - 4,
	// (int) (curSize / 2));
	//
	// }
	//
	// Image actorsIcon = actorsImage.getScaledInstance(
	// (int) Math.ceil(curSize * imageScalingFactor),
	// (int) Math.ceil(curSize * imageScalingFactor),
	// BufferedImage.SCALE_SMOOTH);
	//
	// if (actorsIcon != null)
	// {
	// /**
	// * switch colors between black and light gray to improve the
	// * traceability of corresponding lines, sizes and captions
	// */
	// if ((i % 2) == 0)
	// g.setPaint(Color.BLACK);
	// else
	// g.setPaint(Color.GRAY);
	//
	// g.drawImage(actorsIcon, (int) Math.round(x + circleOffset),
	// (int) Math.round(y - curSize), (int) curSize, (int) curSize,
	// imgObserver);
	//
	// double startLineX = x + (biggestSize / 2);
	// double lineY = y - curSize;
	// double endLineX = startLineX + (biggestSize / 2)
	// + vmcs.toJava2D(2 * textZoom);
	//
	// kLineEndX = endLineX + vmcs.toJava2D(2 + textZoom);
	// kLineEndY = lastEndY;
	//
	// /** make sure, nothing is overlapping */
	// if (!first)
	// while ((Math.abs(kLineEndY - lastEndY) < fontSize))
	// kLineEndY += vmcs.toJava2D(1.0);
	// else if (kLineEndY > lineY)
	// kLineEndY = lineY;
	//
	// g.setStroke(new BasicStroke(VennMakerView.vmcs.toJava2D(0.3)));
	//
	// // gerade Linie
	// g.drawLine((int) Math.round(startLineX), (int) Math.round(lineY),
	// (int) Math.round(endLineX), (int) Math.round(lineY));
	// // geknickte Linie
	// g.drawLine((int) Math.round(endLineX + 1), (int) Math.round(lineY),
	// (int) Math.round(kLineEndX), (int) Math.round(kLineEndY));
	//
	// /** after lines switch color back to black */
	// g.setPaint(Color.BLACK);
	// // Beschreibung
	// g.drawString(curBes, (int) Math.round(kLineEndX + 5),
	// (int) Math.round(kLineEndY + (fontSize / 3)));
	//
	// // Ueberschrift
	// if (first)
	// {
	// yUeber = ((kLineEndY < lineY ? kLineEndY : lineY) - fontSize);
	// g.drawString(
	//							Messages.getString("VennMakerView.0"), (int) Math.round(x), (int) Math //$NON-NLS-1$
	// .round(yUeber));
	// first = false;
	// }
	//
	// lastEndY = kLineEndY;
	// }
	// }
	//
	// // y = yDERueberschrift + offset
	// y = yUeber - (fontSize + vmcs.toJava2D(4 * textZoom));
	//
	// // TODO
	// // for (AkteurTyp akteurTyp : VennMaker.getInstance().getProject()
	// // .getAkteurTypen())
	// // {
	// // BufferedImage actorsImage = null;
	// //
	// // BufferedImageTranscoder t = new BufferedImageTranscoder();
	// // t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, 50f);
	// //
	// // try
	// // {
	// // InputStream is = new FileInputStream(akteurTyp.getImageFile());
	// // TranscoderInput ti = new TranscoderInput(is);
	// // t.transcode(ti, null);
	// // actorsImage = t.getImage();
	// // } catch (FileNotFoundException e)
	// // {
	// // e.printStackTrace();
	// // } catch (TranscoderException e)
	// // {
	// // e.printStackTrace();
	// // }
	// //
	// // if (actorsImage == null)
	// // {
	// // // Default - Bild
	// // actorsImage = new BufferedImage(32, 32,
	// // BufferedImage.TYPE_3BYTE_BGR);
	// // System.err
	// // .println("[VENNMAKERVIEW] Legend: Using default image for Actortype");
	// // }
	// //
	// // Image actorsIcon = actorsImage.getScaledInstance(
	// // (int) (VennMakerView.vmcs.toJava2D(4) * VennMaker.getInstance()
	// // .getProject().getTextZoom()), (int) (VennMakerView.vmcs
	// // .toJava2D(4) * VennMaker.getInstance().getProject()
	// // .getTextZoom()), BufferedImage.SCALE_SMOOTH);
	// // if (actorsIcon != null)
	// // {
	// // g.drawImage(actorsIcon, (int) (x + VennMakerView.vmcs.toJava2D(2)
	// // * VennMaker.getInstance().getProject().getTextZoom()),
	// // (int) (y - VennMakerView.vmcs.toJava2D(2)
	// // * VennMaker.getInstance().getProject().getTextZoom()),
	// // imgObserver);
	// // }
	// // g.drawString(akteurTyp.getBezeichnung(), (int) x
	// // + VennMakerView.vmcs.toJava2D(10)
	// // * VennMaker.getInstance().getProject().getTextZoom(), (int) y
	// // + VennMakerView.vmcs.toJava2D(1)
	// // * VennMaker.getInstance().getProject().getTextZoom());
	// // y = y - VennMakerView.vmcs.toJava2D(4)
	// // * VennMaker.getInstance().getProject().getTextZoom();
	// // }
	//
	// // Ist die Bedeutung der Kreise hinterlegt?
	// if (VennMaker.getInstance().getProject().getCurrentNetzwerk()
	// .getHintergrund().getCirclesLabel() != null)
	// {
	// // Laden des Symbols fuer die Kreise
	// BufferedImage legendImage = null;
	//
	// try
	// {
	// is = new FileInputStream(
	// FileOperations
	//								.getAbsolutePath("icons/intern/CircleSymbol.svg")); //$NON-NLS-1$
	// ti = new TranscoderInput(is);
	// t.transcode(ti, null);
	// legendImage = t.getImage();
	// } catch (FileNotFoundException e)
	// {
	// gui.ErrorCenter
	// .manageException(
	// e,
	//								Messages.getString("VennMakerView.6"), ErrorCenter.ERROR, true, true); //$NON-NLS-1$
	// } catch (TranscoderException e)
	// {
	// e.printStackTrace();
	// }
	// if (legendImage != null)
	// {
	// Image legendIcon = legendImage.getScaledInstance(
	// (int) (VennMakerView.vmcs.toJava2D(4) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(4) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// BufferedImage.SCALE_SMOOTH);
	// if (legendIcon != null)
	// {
	// g.drawImage(
	// legendIcon,
	// (int) (x + VennMakerView.vmcs.toJava2D(2)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (y - VennMakerView.vmcs.toJava2D(2)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// imgObserver);
	// }
	// }
	// g.drawString(VennMaker.getInstance().getProject().getCurrentNetzwerk()
	// .getHintergrund().getCirclesLabel(), (int) x
	// + VennMakerView.vmcs.toJava2D(10)
	// * VennMaker.getInstance().getProject().getTextZoom(), (int) y
	// + VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom());
	// y = y - VennMakerView.vmcs.toJava2D(6)
	// * VennMaker.getInstance().getProject().getTextZoom();
	// }
	//
	// // Ist die Bedeutung der Sektoren hinterlegt?
	// if (VennMaker.getInstance().getProject().getCurrentNetzwerk()
	// .getHintergrund().getSectorAttribute() != null)
	// {
	// // Laden des Symbols fuer die Sektoren
	// BufferedImage legendImage = null;
	//
	// try
	// {
	// is = new FileInputStream(
	// FileOperations
	//								.getAbsolutePath("icons/intern/SectorsSymbol.svg")); //$NON-NLS-1$
	// ti = new TranscoderInput(is);
	// t.transcode(ti, null);
	// legendImage = t.getImage();
	// } catch (FileNotFoundException e)
	// {
	// gui.ErrorCenter
	// .manageException(
	// e,
	//								Messages.getString("VennMakerView.6"), ErrorCenter.ERROR, true, true); //$NON-NLS-1$
	// } catch (TranscoderException e)
	// {
	// e.printStackTrace();
	// }
	//
	// if (legendImage != null)
	// {
	// Image legendIcon = legendImage.getScaledInstance(
	// (int) (VennMakerView.vmcs.toJava2D(4) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(4) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// BufferedImage.SCALE_SMOOTH);
	// if (legendIcon != null)
	// {
	// g.drawImage(
	// legendIcon,
	// (int) (x + VennMakerView.vmcs.toJava2D(2)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (y - VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// imgObserver);
	//
	// }
	// }
	// /*
	// * Workaround: wenn kein Sectorlabel gesetzt, dann leeren String
	// * zurueckgeben
	// */
	// String sLabel = (VennMaker.getInstance().getProject()
	// .getCurrentNetzwerk().getHintergrund().getSectorLabel() == null ? ""
	// : VennMaker.getInstance().getProject().getCurrentNetzwerk()
	// .getHintergrund().getSectorLabel());
	//
	// g.drawString(sLabel, (int) x + VennMakerView.vmcs.toJava2D(10)
	// * VennMaker.getInstance().getProject().getTextZoom(), (int) y
	// + VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom());
	// y = y - VennMakerView.vmcs.toJava2D(6)
	// * VennMaker.getInstance().getProject().getTextZoom();
	// }
	//
	// // alle verwendeten Relations-Attribute, Actor-Symbol-Attribute und
	// // Akteursektoren
	// // mit Werten raussuchen
	//
	// // Alle dargestellten Relationsgruppen und die davon verwendeten Attribute
	// // raussuchen
	// Vector<String> rGroups = VennMaker.getInstance().getProject()
	// .getAttributeCollectors();
	//
	// List<AttributeType> colorA = new ArrayList<AttributeType>();
	// for (String s : rGroups)
	// colorA.add(netzwerk.getActiveRelationColorVisualizer(s)
	// .getAttributeType());
	//
	// List<AttributeType> dashA = new ArrayList<AttributeType>();
	// for (String s : rGroups)
	// dashA.add(netzwerk.getActiveRelationDashVisualizer(s)
	// .getAttributeType());
	//
	// List<AttributeType> sizeA = new ArrayList<AttributeType>();
	// for (String s : rGroups)
	// sizeA.add(netzwerk.getActiveRelationSizeVisualizer(s)
	// .getAttributeType());
	//
	// boolean visualized = true;
	//
	// Map<AttributeType, List<Object>> relationMap = new HashMap<AttributeType,
	// List<Object>>();
	// Map<AttributeType, List<Object>> symbolMap = new HashMap<AttributeType,
	// List<Object>>();
	// Map<AttributeType, Object> actorSectorMap = new HashMap<AttributeType,
	// Object>();
	// Map<AttributeType, Object> allActorSectorTypes = netzwerk
	// .getActorSectorVisualizer().getAttributeTypesAndSelection();
	//
	// AttributeType symbolAtt = netzwerk.getActorImageVisualizer()
	// .getAttributeType();
	// for (Akteur a : netzwerk.getAkteure())
	// {
	// // Relation
	// for (Relation r : a.getRelations(netzwerk))
	// {
	// Map<AttributeType, Object> map = r.getAttributes(netzwerk);
	// for (AttributeType att : map.keySet())
	// {
	// visualized = false;
	// for (AttributeType ca : colorA)
	// if (att.equals(ca))
	// {
	// visualized = true;
	// break;
	// }
	// if (!visualized)
	// for (AttributeType da : dashA)
	// if (att.equals(da))
	// {
	// visualized = true;
	// break;
	// }
	// if (!visualized)
	// for (AttributeType sa : sizeA)
	// if (att.equals(sa))
	// {
	// visualized = true;
	// break;
	// }
	//
	// if (visualized)
	// {
	// List<Object> oList = relationMap.get(att);
	// Object o = map.get(att);
	//
	// if (oList == null)
	// {
	// oList = new ArrayList<Object>();
	// oList.add(o);
	//
	// relationMap.put(att, oList);
	// }
	// else
	// {
	// if (!oList.contains(o))
	// oList.add(o);
	// }
	// }
	// }
	// }
	// // Actor-Symbol
	// if (symbolAtt != null)
	// {
	// Object o = a.getAttributeValue(symbolAtt, netzwerk);
	// if (o != null)
	// {
	// List<Object> oList = symbolMap.get(symbolAtt);
	// if (oList == null)
	// {
	// oList = new ArrayList<Object>();
	// oList.add(o);
	//
	// symbolMap.put(symbolAtt, oList);
	// }
	// else
	// {
	// if (!oList.contains(o))
	// oList.add(o);
	// }
	// }
	// }
	//
	// // Akteursektor
	// for (Entry<AttributeType, Object> e : a.getAttributes(netzwerk)
	// .entrySet())
	// {
	// if (actorSectorMap.containsKey(e.getKey()))
	// continue;
	// Object displayedValue = allActorSectorTypes.get(e.getKey());
	// Object actorValue = e.getValue();
	// if (displayedValue != null && actorValue != null
	// && displayedValue.equals(actorValue))
	// actorSectorMap.put(e.getKey(), displayedValue);
	// }
	// }
	//
	// // Zeichne Akteurssektoren-Legende
	// if (this.netzwerk.getActorSectorVisualizer() != null)
	// {
	// for (Map.Entry<AttributeType, Object> entry : actorSectorMap
	// .entrySet())
	// {
	// g.setColor((Color) this.netzwerk.getActorSectorVisualizer()
	// .getSectorColor().get(entry.getKey()));
	// g.fillArc((int) (x + VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (y - VennMakerView.vmcs.toJava2D(3)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(6) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(6) * VennMaker
	// .getInstance().getProject().getTextZoom()), -45, 90);
	// g.setColor(Color.WHITE);
	// g.fillArc((int) (x + VennMakerView.vmcs.toJava2D(3)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (y - VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(2) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(2) * VennMaker
	// .getInstance().getProject().getTextZoom()), 0, 360);
	// g.setColor(Color.BLACK);
	// g.drawArc((int) (x + VennMakerView.vmcs.toJava2D(3)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (y - VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(2) * VennMaker
	// .getInstance().getProject().getTextZoom()),
	// (int) (VennMakerView.vmcs.toJava2D(2) * VennMaker
	// .getInstance().getProject().getTextZoom()), 0, 360);
	//
	//				String text = entry.getKey().toString() + ": " + entry.getValue(); //$NON-NLS-1$
	// if (text.length() > 30)
	//					text = text.substring(0, 30) + "..."; //$NON-NLS-1$
	// g.drawString(text, (int) x + VennMakerView.vmcs.toJava2D(10)
	// * VennMaker.getInstance().getProject().getTextZoom(), (int) y
	// + VennMakerView.vmcs.toJava2D(1)
	// * VennMaker.getInstance().getProject().getTextZoom());
	//
	// y = y - VennMakerView.vmcs.toJava2D(6)
	// * VennMaker.getInstance().getProject().getTextZoom();
	// }
	// }
	//
	// // Akteurs-Symbole + Werte zeichnen
	// Image actorsIcon = null;
	// t = new BufferedImageTranscoder();
	// float one = VennMakerView.vmcs.toJava2D(1);
	// FontMetrics fm = getFontMetrics(g.getFont());
	// String s = null;
	// for (AttributeType a : symbolMap.keySet())
	// {
	// x += 2 * one;
	// for (Object o : symbolMap.get(a))
	// {
	// String imageName = netzwerk.getActorImageVisualizer().getImage(o);
	// if (imageName != null && imageName.endsWith(".svg"))
	// {
	// groesse = vmcs.toJava2D(9.0);
	// t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT,
	// (float) Math.round(groesse * imageScalingFactor));
	// try
	// {
	// is = new FileInputStream(imageName);
	// ti = new TranscoderInput(is);
	// t.transcode(ti, null);
	// actorsImage = t.getImage();
	// } catch (FileNotFoundException e)
	// {
	// } catch (TranscoderException e)
	// {
	// e.printStackTrace();
	// }
	//
	// actorsIcon = actorsImage.getScaledInstance(
	// (int) Math.round(groesse * imageScalingFactor),
	// (int) Math.round(groesse * imageScalingFactor),
	// BufferedImage.SCALE_SMOOTH);
	//
	// if (actorsIcon != null)
	// {
	// g.drawImage(actorsIcon, (int) Math.round(x),
	// (int) Math.round(y - groesse), (int) groesse,
	// (int) groesse, imgObserver);
	// s = "" + o;
	// g.drawString(s, (int) x + (float) groesse + (one * 2),
	// (int) (y - ((float) (groesse / 2) + (fontSize / 3)))
	// + (one * 2) * textZoom);
	// y = y - groesse - one;
	// }
	// }
	// }
	// s = a.getLabel();
	// y = y - (one * 3) * textZoom;
	// g.drawString(s, (int) x, (int) y + one * textZoom);
	// y = y - (one * 2);
	//
	// x -= 2 * one;
	// }
	// // Symbol-Attributes
	// if (s != null)
	// {
	// s = Messages.getString("VennMakerView.2");
	// y = y - (one * 5) * textZoom;
	// g.drawString(s, (int) x, (int) y + (one * 2) * textZoom);
	// y = y - (one * 5) * textZoom;
	// }
	//
	// // Relations-Attribute und Werte zeichnen
	// s = null;
	//
	// boolean hasDrawnRelationColor = false;
	// boolean hasDrawnRelationDashing = false;
	// boolean hasDrawnRelationThickness = false;
	//
	// if (VennMaker.getInstance().getProject().getPaintLegendPolicy()
	// .paintColor())
	// {
	//
	// // color
	// for (AttributeType ac : colorA)
	// {
	// HashMap<Color, Shape> colorShapeMap = new HashMap<Color, Shape>();
	// HashMap<Color, String> colorStringMap = new HashMap<Color, String>();
	// HashMap<Color, Point2D.Float> colorStringPointMap = new HashMap<Color,
	// Point2D.Float>();
	//
	// double tmp_y = y;
	//
	// for (AttributeType a : relationMap.keySet())
	// {
	// if (a.equals(ac))
	// {
	// for (Object o : relationMap.get(a))
	// {
	// Color color = netzwerk.getActiveRelationColorVisualizer(
	// a.getType()).getColor(o);
	//
	// s = "" + o;
	// tmp_y = tmp_y - (one * 3) * textZoom;
	//
	// colorStringMap.put(color, s);
	//
	// colorStringPointMap.put(color, new Point2D.Float((int) x
	// + (one * 6), (int) tmp_y + one * textZoom));
	//
	// double stringWidth = fm.stringWidth(s);
	// double xStart = x + (one * 6) + stringWidth + (one * 2);
	// Shape line = new Line2D.Double(xStart, tmp_y, xStart
	// + (one * 10), tmp_y);
	//
	// colorShapeMap.put(color, line);
	//
	// }
	// if (colorShapeMap.size() != 0)
	// {
	// y = tmp_y;
	//
	// boolean combineColorNames = colorShapeMap.size() == 1;
	//
	// for (Color c : colorShapeMap.keySet())
	// {
	// g.setColor(c);
	// g.setStroke(new BasicStroke(VennMakerView.vmcs
	// .toJava2D(1.0f / LINE_WIDTH_SCALE)));
	// g.draw(colorShapeMap.get(c));
	// }
	// g.setColor(Color.black);
	// if (combineColorNames)
	// {
	// Point2D.Float fpoint = colorStringPointMap
	// .get(colorShapeMap.keySet().iterator().next());
	// g.drawString(
	// Messages
	// .getString("VennMakerView.CombinedRelationColorText"),
	// fpoint.x, fpoint.y);
	// }
	// else
	// {
	// for (Color c : colorStringMap.keySet())
	// {
	// Point2D.Float fpoint = colorStringPointMap.get(c);
	// g.drawString(colorStringMap.get(c), fpoint.x,
	// fpoint.y);
	// }
	// }
	//
	// // System.out.println(a.getLabel());
	// s = a.getLabel() + " "
	// + Messages.getString("VennMakerView.RelationColor");
	// y = y - (one * 3) * textZoom;
	// g.drawString(s, (int) x + (one * 2), (int) y + one
	// * textZoom);
	// // y = y - (one * 2);
	//
	// hasDrawnRelationColor = true;
	// }
	//
	// }
	// }
	// }
	// }
	// // dash
	// BasicStroke stroke = new BasicStroke(1.0f);
	// if (VennMaker.getInstance().getProject().getPaintLegendPolicy()
	// .paintDashing())
	// {
	// if (hasDrawnRelationColor)
	// y = y - (one * 2);
	// for (AttributeType ad : dashA)
	// {
	// HashMap<BasicStroke, Shape> strokeShapeMap = new HashMap<BasicStroke,
	// Shape>();
	// HashMap<BasicStroke, String> strokeStringMap = new HashMap<BasicStroke,
	// String>();
	// HashMap<BasicStroke, Point2D.Float> strokeStringPointMap = new
	// HashMap<BasicStroke, Point2D.Float>();
	//
	// double tmp_y = y;
	//
	// for (AttributeType a : relationMap.keySet())
	// {
	// if (a.equals(ad))
	// {
	// for (Object o : relationMap.get(a))
	// {
	// float strokeSize = 1.0f / LINE_WIDTH_SCALE;
	// float[] dashArr = netzwerk
	// .getActiveRelationDashVisualizer(ad.getType())
	// .getDasharray(o);
	//
	// stroke = new BasicStroke(
	// VennMakerView.vmcs.toJava2D(strokeSize),
	// stroke.getEndCap(), stroke.getLineJoin(),
	// stroke.getMiterLimit(), dashArr,
	// stroke.getDashPhase());
	//
	// s = "" + o;
	// tmp_y = tmp_y - (one * 3) * textZoom;
	//
	// strokeStringMap.put(stroke, s);
	// strokeStringPointMap.put(stroke, new Point2D.Float((int) x
	// + (one * 6), (int) tmp_y + one * textZoom));
	//
	// double stringWidth = fm.stringWidth(s);
	// double xStart = x + (one * 6) + stringWidth + (one * 2);
	// Shape line = new Line2D.Double(xStart, tmp_y, xStart
	// + (one * 10), tmp_y);
	//
	// strokeShapeMap.put(stroke, line);
	// }
	// if (strokeShapeMap.size() != 0)
	// {
	// y = tmp_y;
	//
	// boolean combineStrokeNames = strokeShapeMap.size() == 1;
	//
	// for (BasicStroke basicStroke : strokeShapeMap.keySet())
	// {
	// g.setStroke(basicStroke);
	// g.draw(strokeShapeMap.get(basicStroke));
	// Point2D.Float fpoint = strokeStringPointMap
	// .get(basicStroke);
	// if (!combineStrokeNames)
	// g.drawString(strokeStringMap.get(basicStroke),
	// fpoint.x, fpoint.y);
	// }
	// if (combineStrokeNames)
	// {
	// Point2D.Float fpoint = strokeStringPointMap
	// .get(strokeShapeMap.keySet().iterator().next()); // get
	// // first
	// // item
	// // position
	// // and
	// // draw
	// // combinedString
	// // there
	// g.drawString(
	// Messages
	// .getString("VennMakerView.CombinedRelationDashingText"),
	// fpoint.x, fpoint.y);
	// }
	//
	// s = a.getLabel()
	// + " "
	// + Messages
	// .getString("VennMakerView.RelationDashing");
	// y = y - (one * 3) * textZoom;
	// g.drawString(s, (int) x + (one * 2), (int) y + one
	// * textZoom);
	//
	// hasDrawnRelationDashing = true;
	// }
	// }
	// }
	// }
	// }
	//
	// if (VennMaker.getInstance().getProject().getPaintLegendPolicy()
	// .paintThickness())
	// {
	// // Either has drawn dashing legend with already shifted; or has not
	// // drawn but color drawn -> so shift
	// if (hasDrawnRelationDashing
	// || (hasDrawnRelationColor && !hasDrawnRelationDashing))
	// y = y - (one * 2);
	// // size
	// for (AttributeType as : sizeA)
	// {
	// double tmp_y = y;
	//
	// HashMap<Float, Shape> sizeShapeMap = new HashMap<Float, Shape>();
	// HashMap<Float, String> sizeStringMap = new HashMap<Float, String>();
	// HashMap<Float, Point2D.Float> sizeStringPointMap = new HashMap<Float,
	// Point2D.Float>();
	//
	// for (AttributeType a : relationMap.keySet())
	// {
	// if (a.equals(as))
	// {
	// for (Object o : relationMap.get(a))
	// {
	// float strokeSize = VennMakerView.vmcs
	// .toJava2D((float) (netzwerk
	// .getActiveRelationSizeVisualizer(as.getType())
	// .getSize(o))
	// / LINE_WIDTH_SCALE)
	// / LINE_WIDTH_SCALE;
	//
	// s = "" + o;
	// tmp_y = tmp_y - (one * 3) * textZoom;
	// sizeStringMap.put(strokeSize, s);
	// sizeStringPointMap.put(strokeSize, new Point2D.Float(
	// (int) x + (one * 6), (int) tmp_y + one * textZoom));
	//
	// double stringWidth = fm.stringWidth(s);
	// double xStart = x + (one * 6) + stringWidth + (one * 2);
	// Shape line = new Line2D.Double(xStart, tmp_y, xStart
	// + (one * 10), tmp_y);
	//
	// sizeShapeMap.put(strokeSize, line);
	// }
	// if (sizeShapeMap.size() != 0)
	// {
	//
	// y = tmp_y;
	//
	// boolean combineRelationThickness = sizeShapeMap.size() == 1;
	//
	// for (float f : sizeShapeMap.keySet())
	// {
	// g.setStroke(new BasicStroke(f));
	// g.draw(sizeShapeMap.get(f));
	//
	// Point2D.Float fpoint = sizeStringPointMap.get(f);
	// if (!combineRelationThickness)
	// g.drawString(sizeStringMap.get(f), fpoint.x,
	// fpoint.y);
	// }
	// if (combineRelationThickness)
	// {
	// Point2D.Float fpoint = sizeStringPointMap
	// .get(sizeShapeMap.keySet().iterator().next());
	// g.drawString(
	// Messages
	// .getString("VennMakerView.CombinedRelationThicknessText"),
	// fpoint.x, fpoint.y);
	// }
	//
	// g.setStroke(new BasicStroke());
	//
	// s = a.getLabel()
	// + " "
	// + Messages
	// .getString("VennMakerView.RelationThickness");
	// y = y - (one * 3) * textZoom;
	// g.drawString(s, (int) x + (one * 2), (int) y + one
	// * textZoom);
	//
	// hasDrawnRelationThickness = true;
	// }
	// }
	// }
	// }
	// }
	// // Relational Attributes
	// if (s != null
	// && (hasDrawnRelationColor || hasDrawnRelationDashing ||
	// hasDrawnRelationThickness))
	// {
	// s = Messages.getString("VennMakerView.1");
	// y = y - (one * 5) * textZoom;
	// g.drawString(s, (int) x, (int) y + (one * 2) * textZoom);
	// }
	// }

	public static BufferedImage getDefaultActorImage(int groesse)
	{
		// Default - Bild
		BufferedImage actorsImage = new BufferedImage(groesse, groesse,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D gi = actorsImage.createGraphics();
		gi.setColor(Color.gray);
		gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		gi.setStroke(new BasicStroke(3.0f));
		gi.draw(new Ellipse2D.Double(2, 2, groesse - 4, groesse - 4));
		gi.drawLine((int) (groesse / 2), 2, (int) (groesse / 2),
				(int) groesse - 4);
		gi.drawLine(2, (int) (groesse / 2), (int) groesse - 4,
				(int) (groesse / 2));

		return actorsImage;
	}

	/**
	 * Draws the current networkname into the tab
	 * 
	 * @param network
	 *           the current network
	 * @param graphics
	 *           graphics to draw
	 * @param offset
	 *           -1 - left 0 - center 1 - right
	 */
	private void drawNetworkName(Netzwerk network, Graphics graphics, int offset)
	{
		double x, y;
		Graphics2D g = (Graphics2D) graphics.create();
		Font f = g.getFont().deriveFont(Font.PLAIN);
		String bezeichnung = network.getBezeichnung();
		if (bezeichnung == null)
			bezeichnung = "";

		f = f.deriveFont((float) (VennMakerView.vmcs.toJava2D(4) * VennMaker
				.getInstance().getProject().getTextZoom()));

		g.setFont(f);
		g.setColor(Color.BLACK);
		FontMetrics fm = getFontMetrics(f);
		double stringWidth = fm.stringWidth(bezeichnung);
		double paintWidth = getViewArea().getWidth();

		// String so lange verkleinern, bis seine GrÃ¶ÃŸe auf die Viewarea
		// passt...
		while (stringWidth >= (paintWidth - 20))
		{
			bezeichnung = bezeichnung.substring(0, bezeichnung.length() - 4)
					+ "...";
			stringWidth = fm.stringWidth(bezeichnung);
		}

		y = getViewArea().getMinY()
				+ (VennMakerView.vmcs.toJava2D(4)
						* VennMaker.getInstance().getProject().getTextZoom() * 1.25);

		x = (paintWidth - stringWidth) / 2 + offset * (paintWidth - stringWidth)
				/ 2 + getViewArea().getMinX();

		// kleiner Offset vom horizontalen AuÃŸenrand
		x += -1 * offset * 8;

		g.drawString(bezeichnung, (int) x, (int) y);
	}

	private JLabel		tooltipLabel;

	private JWindow	tooltipWindow;

	/**
	 * to popup a tooltip with attribute information on an actor
	 * 
	 * @param mousePosition
	 *           - current cursor position
	 * @param akteur
	 *           - actor under cursor
	 */
	public void tooltip(Point mousePosition, Akteur akteur)
	{

		createTooltip();

		/*
		 * Compute m = new Compute(this.netzwerk, this.netzwerk.getAkteure());
		 * 
		 * int inD = m.inDegree(akteur); int outD = m.outDegree(akteur); int inC =
		 * m.inCloseness(akteur); int outC = m.outCloseness(akteur); double inDstd
		 * = m.inDegreeStd(akteur); double outDstd = m.outDegreeStd(akteur);
		 * double outCstd = m.outClosenessStd(akteur); //double proxPrestige =
		 * m.proximityPrestige(akteur);
		 */
		String s = "<html>" + akteur.getName()+" ("+akteur.getId()+")"; //$NON-NLS-1$
		List<AttributeType> displayList = VennMaker.getInstance().getProject()
				.getDisplayedAtActorTooltip();

		if (displayList != null)
		{
			s += "<small><br>";
			Object o = null;
			for (AttributeType a : displayList)
			{
				o = akteur.getAttributeValue(a, netzwerk);
				if (o != null)
					s += "<br/>" + a.getLabel() + ": " + o;
			}
			s += "</small>";
		}
		// TODO
		// if (akteur.getKommentar() != null)
		//			s += "<br><br>" //$NON-NLS-1$
		//					+ akteur.getKommentar().toString().replace("\n", "<br>"); //$NON-NLS-1$ //$NON-NLS-2$
		//		s += "<br><br>" + akteur.getTyp().getBezeichnung(); //$NON-NLS-1$

		/*
		 * s += "<br><br><hr><small>";
		 * 
		 * s += "In-Degree: " + inD + "<br>"; s += "Out-Degree: " + outD + "<br>";
		 * 
		 * s += "</small><hr><small>";
		 * 
		 * s += "In-Degree (std): " + MessageFormat.format("{0,number,#.###}", new
		 * Object[] { new Double(inDstd) }) + "<br>";
		 * 
		 * s += "Out-Degree (std): " + MessageFormat.format("{0,number,#.###}",
		 * new Object[] { new Double(outDstd) }) + "<br>";
		 * 
		 * s += "</small><hr><small>";
		 * 
		 * s += "In-closeness: " + inC + "<br>"; s += "Out-closeness: " + outC +
		 * "<br>";
		 * 
		 * s += "</small><hr><small>";
		 * 
		 * s += "Out-closeness (std): " + MessageFormat.format("{0,number,#.###}",
		 * new Object[] { new Double(outCstd) }) + "<br>";
		 * 
		 * s += "</small>";
		 */
		tooltipLabel.setText(s);
		tooltipWindow.pack();
		tooltipWindow.setVisible(true);
		SwingUtilities.convertPointToScreen(mousePosition, this);
		tooltipWindow.setLocation(mousePosition.x + 5, mousePosition.y
				- tooltipWindow.getHeight() - 5);
	}

	private void createTooltip()
	{
		if (tooltipLabel == null)
		{
			tooltipLabel = new JLabel(" "); //$NON-NLS-1$
			tooltipLabel.setOpaque(true);
			tooltipLabel.setBackground(UIManager.getColor("ToolTip.background")); //$NON-NLS-1$
			tooltipLabel.setBorder(UIManager.getBorder("ToolTip.border")); //$NON-NLS-1$
			tooltipWindow = new JWindow(new Frame());
			tooltipWindow.getContentPane().add(tooltipLabel);
		}
	}

	/**
	 * to popup a tooltip with attribute information on an relation
	 * 
	 * @param mousePosition
	 *           - current cursor position
	 * @param relation
	 *           - relation under cursor
	 */

	public void tooltip(Point mousePosition, Relation relation)
	{
		createTooltip();

		String s = "<html>" + Messages.getString("VennMaker.4");
		List<AttributeType> tooltipATypes = VennMaker.getInstance().getProject()
				.getDisplayedAtRelationTooltip();
		if (tooltipATypes == null)
			return;

		s += "<small><br>";
		String aVals = "";
		Object o = null;
		Map<AttributeType, Object> attr = relation.getAttributes(netzwerk);
		for (AttributeType a : attr.keySet())
		{
			if (tooltipATypes.contains(a))
			{
				o = attr.get(a);
				if (o != null && !o.toString().equals(""))
					aVals += "<br/>" + a.getLabel() + ": " + o;
			}
		}

		if (aVals.equals(""))
			return;

		s += aVals;
		s += "</small>";

		tooltipLabel.setText(s);
		tooltipWindow.pack();
		tooltipWindow.setVisible(true);
		SwingUtilities.convertPointToScreen(mousePosition, this);
		tooltipWindow.setLocation(mousePosition.x + 5, mousePosition.y
				- tooltipWindow.getHeight() - 5);
	}

	/**
	 * Hides the tooltip window again.<br>
	 * Can be used for relation tooltip and actor tooltip, because only one of
	 * them can be visible anyway.
	 */
	public void hideTooltip()
	{
		tooltipWindow.setVisible(false);
	}

	/**
	 * activates the specified filter and repaints.
	 */
	public void setFilter()
	{
		repaint();
		isFilterActive = true;
	}

	/**
	 * deactivated the specified filter and repaints.
	 */
	public void deactivateFilter()
	{
		isFilterActive = false;
		netzwerk.unlockAllActors();
		netzwerk.unlockAllRelations();
		this.netzwerk.removeAllMarkedActors();

		repaint();
	}

	/**
	 * @return the status of the filter.<br>
	 * <br>
	 *         <code>true</code> - filter active<br>
	 *         <code>false</code> - filter inactive<br>
	 */
	public boolean isFilterActive()
	{
		return isFilterActive;
	}

	public void setInteractiveSelection(Point2D start, Point2D end)
	{
		if (start == null || end == null)
			currentMultiSelection = null;
		else
		{
			double sX = start.getX();
			double sY = start.getY();
			double eX = end.getX();
			double eY = end.getY();
			if (sX > eX)
			{
				double d = sX;
				sX = eX;
				eX = d;
			}

			if (sY > eY)
			{
				double d = sY;
				sY = eY;
				eY = d;
			}

			double w = eX - sX;
			double h = eY - sY;

			currentMultiSelection = new Rectangle2D.Double(sX, sY, w, h);
		}

		repaint();
	}

	/**
	 * multiple selection at the moment (set when mouse is dragged)
	 */
	public Rectangle2D.Double getCurrentMultiSelection()
	{
		return currentMultiSelection;
	}

	private void selectMultipleActors()
	{
		if (currentMultiSelection != null)
		{
			temporaryDrawnActors = new ArrayList<Akteur>();
			for (Akteur a : netzwerk.getAkteure())
			{
				Point2D loc = vmcs.toJava2D(a.getLocation(netzwerk));

				if (loc != null)
				{
					if (currentMultiSelection.contains(loc))
						temporaryDrawnActors.add(a);
				}
			}
			if (temporaryDrawnActors.size() <= 0)
			{
				temporaryDrawnActors = null;
				currentMultiSelection = null;
			}
		}
	}

	@Override
	public void action(MenuEvent e)
	{
		if ("ButtonActorAttributeSelected".equals(e.getInfo().getMessage()))
		{
			Image image = ImageOperations.loadActorImage(
					"icons/intern/cursorActor.svg", 32, 1.0);

			if (image != null)
			{
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Cursor c = toolkit.createCustomCursor(image, new Point(16, 16),
						"img");
				setCursor(c);
			}
		}

		if ("ButtonRelationAttributeSelected".equals(e.getInfo().getMessage()))
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = ImageOperations.loadActorImage(
					"icons/intern/cursorRelation.svg", 32, 1.0);

			if (image != null)
			{
				Cursor c = toolkit.createCustomCursor(image, new Point(16, 16),
						"img");
				setCursor(c);
			}
		}
	}

	public double getImageScalingFactor()
	{
		return this.imageScalingFactor;
	}

	/**
	 * returns the stored legend to the given network - if non exists, the
	 * standardlegend is created
	 */
	public static VennMakerLegend getLegend(Netzwerk n)
	{
		if (legends.get(n) == null)
			legends.put(n, new VennMakerLegend(n));

		return legends.get(n);
	}
}
