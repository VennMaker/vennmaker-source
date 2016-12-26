package data;

import events.ChangeRelationTypeEvent;
import events.DeleteActorEvent;
import events.DeleteNetworkEvent;
import events.DeleteRelationTypeEvent;
import events.NetworkEvent;
import events.NewActorEvent;
import events.NewNetworkEvent;
import events.NewRelationTypeEvent;
import events.RelationTypeEvent;
import events.VennMakerEvent;
import files.FileOperations;
import files.VMPaths;
import files.VennMakerXStream;
import gui.Messages;
import gui.PaintLegendPolicy;
import gui.VennMaker;
import gui.configdialog.save.InterviewSaveElement;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.elements.InterviewElement;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Vector;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;

import data.AttributeType.Scope;
import data.BackgroundInfo.SectorInfo;
import data.RelationTyp.DirectionType;
import de.module.IModule;
import de.module.IModuleSave;
import de.module.ModuleData;

/**
 * Ein Projekt besteht aus einer Ansammlung von Netzwerken, von denen eins
 * gerade aktiv ist sowie Listen von projektweit definierten Typen für Akteure
 * und Relationen.
 * 
 * Zu jedem Zeitpunkt ist in der Anwendung genau ein Projekt aktiv.
 * 
 * Methoden zum Speichern und öffnen in/aus einer XML-Datei sind vorhanden.
 * 
 */
public class Projekt implements EventLogger
{

	// Versionsinformationen
	private String													version				= VennMaker.VERSION;

	private String													revision				= VennMaker.REVISION;

	/**
	 * Die interne Höhe der virtuellen Zeichenfläche. Bestimmt den Wertebereich
	 * für die Speicherung der Koordinaten, spielt aber bei der Ausgabe keine
	 * Rolle. Die Breite ergibt sich aus dem konfigurierten Seitenverhältnis.
	 * 
	 * Der Wert von 200 ergibt einen Wertebereich für die Positionen von
	 * -100..100.
	 */
	private int														viewAreaHeight		= 200;

	// Muss hier stehen um mitgespeichert zu werden.
	private float													viewAreaRatio		= 1.33f;

	// Hier werden Metainformationen ueber das Projekt
	// (zb. Notizen zur Verwendeten Literatur)
	private String													metaInformation	= "";						//$NON-NLS-1$

	/**
	 * Enthält den aktuellen Filter
	 */

	private String													filter				= null;

	/** enthaelt die aktuellen Filtereinstellungen */
	private HashMap<Integer, Vector<Filterparameter>>	filters;

	/**
	 * Flag, das anzeigt, ob Namen verschluessel wurden (=true) oder nicht
	 * (=false)
	 */

	private boolean												encodeFlag			= false;

	private File													vmpFile;

	private boolean												wasVmpFile;

	private InterviewSaveElement								currentInterviewConfig;

	/**
	 * Enthält die positionen der Filterkomponenten in der jeweiligen ComboBox
	 */
	private ArrayList<Integer>									filterIndex;

	private static String										projectFileName	= "";						//$NON-NLS-1$

	/**
	 * Contains the CryptoElements to save in the project file
	 */
	public List<CryptoElement>									cryptoElementsToSave;

	/**
	 * Lists with AttributeType's to show at Actors/Relation as Tooltip or Labels
	 */
	private List<AttributeType>								displayedAtActor;

	private List<AttributeType>								displayedAtActorTooltip;

	private List<AttributeType>								displayedAtRelation;

	private List<AttributeType>								displayedAtRelationTooltip;

	private int														maxNumberActors	= 45;						// Maximal
																																// erlaubte
																																// Anzahl
																																// an
																																// Akteuren

	private ArrayList<ModuleData> modules = new ArrayList<ModuleData>();
	
	
	/**
	 * Add the module configuratino to the module configuration list
	 * @param module
	 */
	public void addModuleConfig(IModuleSave m){
		
		ModuleData module = new ModuleData();
		
		module.setModuleName(m.getName());
		module.setModuleVersion(m.getVersion());
		module.setModuleData(m.getSaveData());
		
		this.modules.add(module);
	}
	
	/**
	 * Return the module list
	 * @return module list
	 */
	public ArrayList<ModuleData> getModuleConfig(){
		return this.modules;
	}
	
	
	public int getViewAreaHeight()
	{
		return this.viewAreaHeight;
	}

	public void setViewAreaHeight(int viewAreaHeight)
	{
		this.viewAreaHeight = viewAreaHeight;
	}

	/**
	 * Vergrößerungsfaktor für Texte
	 */
	private float	textZoom	= 1f;

	public float getTextZoom()
	{
		return textZoom;
	}

	public void setTextZoom(float textZoom)
	{
		this.textZoom = textZoom;
	}

	public float getViewAreaRatio()
	{
		return this.viewAreaRatio;
	}

	public void setViewAreaRatio(float ratio)
	{
		this.viewAreaRatio = ratio;
	}

	public static String getProjectFileName()
	{
		return projectFileName;
	}

	public static void setProjectFileName(String f)
	{
		projectFileName = f;
	}

	private EventLogger		eventLogger	= new EventLoggerImpl();

	// private static XStream xstream = new XStream();

	private static XStream	xstream		= new VennMakerXStream();

	// FIXME: project loading is completly seperated from different styles of
	// loading
	public static Projekt load(final String fileName)
	{
		Projekt.initXStream();
		try
		{
			final FileInputStream is = new FileInputStream(fileName);

			Projekt p = null;

			try
			{
				p = (Projekt) Projekt.xstream.fromXML(is);
			} catch (Exception xse)
			{
				xse.printStackTrace();
				return null;
			}

			is.close();
			

		//	System.out.println(p.toXml());

			// Hier wird die Aspekt Ratio wieder in die VennMaker Config zurueck
			// geschrieben. Falls Aspekt Ratio noch nicht vorhanden, wirds auf
			// quadratisch (1.0) gesetzt
			if (p.getViewAreaRatio() == 0.0)
				p.setViewAreaRatio(1.0f);
			VennMaker.getInstance().getConfig()
					.setViewAreaRatio(p.getViewAreaRatio());

			setProjectFileName(fileName);

			EventProcessor.getInstance().resetEventListener();
			p.registerEventListeners();

			p.createTempNetworkList();

			VennMakerCrypto.getInstance()
					.setCryptoElements(p.cryptoElementsToSave);

			/**
			 * Sind wir in einem Projekt, das durch ein Interview entstanden ist?
			 * Dann uebernehme die Fragen aus dem Interview in die Konfiguration.
			 */
			if (p.getInterviewConfig() != null)
				VennMaker.getInstance().setConfig(p.getInterviewConfig());

			/**
			 * Abwaertskompatibilitaet: Wertebereich der Koordinaten
			 */
			if (p.getNetzwerke().get(0).getHintergrund().definedDrawingArea != null)
			{
				p.setViewAreaHeight(p.getNetzwerke().get(0).getHintergrund().definedDrawingArea.height);
				p.setTextZoom(2.5f);
			}

			/**
			 * Abwaertskompatibilitaet: Filterfunktion
			 */

			if (p.getNetzwerke().get(0).getStatusFilter() == false)
			{
				p.getNetzwerke().get(0).createFilter();
			}

			/**
			 * Abwaertskompatibilitaet: RelationDirection
			 */
			if (p.getIsDirectedAttributeCollection() == null)
				p.setIsDirectedAttributeCollection(new HashMap<String, Boolean>());

			/**
			 * Abwaertskompatibilitaet: Sektoren; falls keine breite angegeben,
			 * gleichverteilung der sektorengroessen
			 * 
			 * Abwaertskompatiblitaet: Erstellen der Visualizer Hash-Maps falls
			 * keine vorhanden
			 * 
			 * Auslesen der hoechsten ID der Akteure - XStream speichert keine
			 * statics
			 */

			for (Netzwerk n : p.getNetzwerke())
			{
				for (int i = 0; i < n.getHintergrund().getNumSectors(); ++i)
				{
					SectorInfo s = n.getHintergrund().getSector(i);
					if (s.width == 0.0)
					{
						s.width = 1.0 / n.getHintergrund().getNumSectors();
						s.off = 0 + i * 1.0 / n.getHintergrund().getNumSectors();
					}
				}

				for (Akteur actor : n.getAkteure())
				{
					if (actor.getId() >= actor.getGlobalId())
						actor.setGlobalId(actor.getId() + 1);
				}
				n.createNewVisualizers();

			}

			/**
			 * Abwaertskompatibilitaet: Attribute; falls noch keine
			 * Relationsattribute vorhanden, Erzeugung von diesen und den
			 * entsprechenden HashMaps
			 */
			boolean oldFile = false;
			boolean notSoOldButStillOldFile = true;
			for (AttributeType at : p.attributeTypes)
			{

				if ((at.getType() != null) && (!at.getType().equals("ACTOR"))) //$NON-NLS-1$
					notSoOldButStillOldFile = false;
				if (at.getType() == null)
				{
					/**
					 * Types sind noch nicht erstellt also: - types erstellen -
					 * maingenerator und mainattribute setzen (Hashmap) -
					 * Visualisierer erzeugen
					 */
					oldFile = true;
					notSoOldButStillOldFile = false;
					at.setType("ACTOR"); //$NON-NLS-1$
					if (p.getMainAttributeType("ACTOR") == null) //$NON-NLS-1$
					{
						p.mainGeneratorTypeSet = new HashMap<String, AttributeType>();
						p.mainAttributeTypeSet = new HashMap<String, AttributeType>();
						p.setMainGeneratorType("ACTOR", p.mainGeneratorType); //$NON-NLS-1$
						p.setMainAttributeType("ACTOR", p.mainAttributeType); //$NON-NLS-1$
					}
				}
			}

			/**
			 * "ACTOR"-Types gesetzt, aber noch keine Hashmaps bei mainGenerator
			 * und mainAttributeTypeSet
			 */
			if (notSoOldButStillOldFile)
			{
				p.mainGeneratorTypeSet = new HashMap<String, AttributeType>();
				p.mainAttributeTypeSet = new HashMap<String, AttributeType>();

				if (p.mainGeneratorType != null)
					p.setMainGeneratorType("ACTOR", p.mainGeneratorType); //$NON-NLS-1$
				else
					p.setMainGeneratorType("ACTOR", p.mainGeneratorType); //$NON-NLS-1$
				if (p.mainAttributeType != null)
					p.setMainAttributeType("ACTOR", p.mainAttributeType); //$NON-NLS-1$
			}

			if (oldFile || notSoOldButStillOldFile)
			{
				HashMap<Object, Color> colors = new HashMap<Object, Color>();
				HashMap<Object, float[]> dashArray = new HashMap<Object, float[]>();
				HashMap<Object, Integer> sizes = new HashMap<Object, Integer>();
				AttributeType newRelationAttribute = new AttributeType(1);
				Vector<String> preValues = new Vector<String>();
				newRelationAttribute.setType("STANDARDRELATION"); //$NON-NLS-1$
				// TODO: language
				String imported = Messages.getString("AttributeLabel.Imported");

				newRelationAttribute.setDescription(imported); //$NON-NLS-1$
				newRelationAttribute.setLabel(imported); //$NON-NLS-1$

				newRelationAttribute.setScope(Scope.NETWORK);
				// Relationsattribute muessen komplett erstellt werden;
				for (RelationTyp rType : p.getRelationTypen())
				{
					preValues.add(rType.getBezeichnung());
					colors.put(rType.getBezeichnung(), rType.getColor());
					sizes.put(rType.getBezeichnung(), (int) rType.getStroke()
							.getLineWidth());
					dashArray.put(rType.getBezeichnung(), rType.getStroke()
							.getDashArray());
				}
				newRelationAttribute.setPredefinedValues(preValues.toArray());
				p.attributeTypes.add(newRelationAttribute);

				p.setMainGeneratorType("STANDARDRELATION", newRelationAttribute); //$NON-NLS-1$
				p.setMainAttributeType("STANDARDRELATION", newRelationAttribute); //$NON-NLS-1$

				Map<String, AttributeType> newCircleAttributes = new HashMap<String, AttributeType>();
				Map<String, AttributeType> newSectorAttributes = new HashMap<String, AttributeType>();

				/* wird hochgezaehlt falls es mehr als 1 CircleAttribut gibt */
				int circleCounter = 0;
				int sectorCounter = 0;

				for (Netzwerk netzwerk : p.netzwerke)
				{
					netzwerk.createVisualizer();
					netzwerk.resetVisualizers();

					netzwerk.setRelationColorVisualizer("STANDARDRELATION",
							newRelationAttribute, new RelationColorVisualizer());
					netzwerk.setRelationSizeVisualizer("STANDARDRELATION",
							newRelationAttribute, new RelationSizeVisualizer());
					netzwerk.setRelationDashVisualizer("STANDARDRELATION",
							newRelationAttribute, new RelationDashVisualizer());

					netzwerk.getRelationColorVisualizer(
							"STANDARDRELATION", newRelationAttribute) //$NON-NLS-1$
							.setAttributeType(newRelationAttribute);
					netzwerk.getRelationColorVisualizer(
							"STANDARDRELATION", newRelationAttribute) //$NON-NLS-1$
							.setColors(colors);

					netzwerk.getRelationSizeVisualizer(
							"STANDARDRELATION", newRelationAttribute) //$NON-NLS-1$
							.setAttributeType(newRelationAttribute);
					netzwerk.getRelationSizeVisualizer(
							"STANDARDRELATION", newRelationAttribute).setSizes( //$NON-NLS-1$
							sizes);

					netzwerk.getRelationDashVisualizer(
							"STANDARDRELATION", newRelationAttribute) //$NON-NLS-1$
							.setAttributeType(newRelationAttribute);
					netzwerk.getRelationDashVisualizer(
							"STANDARDRELATION", newRelationAttribute) //$NON-NLS-1$
							.setDasharrays(dashArray);

					boolean directionSet = false;
					for (Akteur a : netzwerk.getAkteure())
					{
						for (Relation r : a.getRelations(netzwerk))
						{
							r.setAttributeCollectorValue("STANDARDRELATION"); //$NON-NLS-1$
							r.setAttributeValue(newRelationAttribute, netzwerk, r
									.getTyp().getBezeichnung());
							if (!directionSet)
							{
								p.getIsDirectedAttributeCollection()
										.put("STANDARDRELATION",
												r.getTyp().getDirectionType() == DirectionType.DIRECTED);
								directionSet = true;
							}
						}
					}

					/**
					 * Circle und Sektoren Attribute erzeugen
					 */
					BackgroundInfo bgInfo = netzwerk.getHintergrund();
					AttributeType a = null;

					List<String> circles = bgInfo.getCircles();
					if (bgInfo.getCircleAttribute() == null && circles != null
							&& circles.size() > 0)
					{
						// ueberpruefen ob zu erstellendes Attribut nicht schon
						// vorhanden
						String key = circles.toString();
						if (key != null && !key.equals("[]"))
						{
							a = newCircleAttributes.get(key);
							String circle = Messages
									.getString("AttributeLabel.Circle");
							if (circleCounter > 0)
								circle += "_" + circleCounter;

							if (a == null)
							{
								circleCounter++;

								a = new AttributeType();
								a.setLabel(circle);
								a.setDescription(circle);

								String[] vals = new String[circles.size()];
								circles.toArray(vals);

								a.setPredefinedValues(vals);

								newCircleAttributes.put(key, a);
							}
							bgInfo.setCirclesAsc(true);
							bgInfo.setCircleAttribute(a);
							bgInfo.setCirclesLabel(circle);
						}
					}

					List<SectorInfo> sectors = new ArrayList<SectorInfo>();
					for (int i = 0; i < bgInfo.getNumSectors(); i++)
						sectors.add(bgInfo.getSector(i));
					if (bgInfo.getSectorAttribute() == null && sectors.size() > 0)
					{
						String key = sectors.toString();
						if (key != null && !key.equals("[]"))
						{
							a = newSectorAttributes.get(key);
							String sector = Messages
									.getString("AttributeLabel.Sector");
							if (sectorCounter > 0)
								sector += "_" + sectorCounter;
							if (a == null)
							{
								a = new AttributeType();
								a.setLabel(sector);
								a.setDescription(sector);

								String[] vals = new String[sectors.size()];
								for (int i = 0; i < vals.length; i++)
									vals[i] = sectors.get(i).label;

								a.setPredefinedValues(vals);
								newSectorAttributes.put(key, a);
							}

							bgInfo.setSectorAttribute(a);
							bgInfo.setSectorLabel(sector);
						}
					}
				}
				Vector<AttributeType> aTypes = p.getAttributeTypes();
				if (newCircleAttributes.size() > 0)
					for (AttributeType a : newCircleAttributes.values())
						aTypes.add(a);

				if (newSectorAttributes.size() > 0)
					for (AttributeType a : newSectorAttributes.values())
						aTypes.add(a);

				p.setAttributeTypes(aTypes);
			}

			// Downward compatible for old projects without a PaintLegendPolicy
			if (p.getPaintLegendPolicy() == null)
				p.setPaintLegendPolicy(new PaintLegendPolicy(true, true, true));

			// patch activerelationcolorvisualizers
			// System.out.println("---- patching.. ----");
			for (Netzwerk n : p.netzwerke)
			{
				for (AttributeType at : p.attributeTypes)
				{
					Map<AttributeType, RelationColorVisualizer> relationColorVis = n
							.getRelationColorVisualizerByCollector(at.getType());
					Map<AttributeType, RelationDashVisualizer> relationDashVisualizerByCollector = n
							.getRelationDashVisualizerByCollector(at.getType());
					Map<AttributeType, RelationSizeVisualizer> relationSizeVisualizerByCollector = n
							.getRelationSizeVisualizerByCollector(at.getType());

					RelationColorVisualizer atColorVisualizer = n
							.getActiveRelationColorVisualizer(at.getType());
					RelationDashVisualizer atDashVisualizer = n
							.getActiveRelationDashVisualizer(at.getType());
					RelationSizeVisualizer atSizeVisualizer = n
							.getActiveRelationSizeVisualizer(at.getType());

					Map<Object, Color> colors = atColorVisualizer.getColors();
					Map<Object, float[]> dasharrays = atDashVisualizer
							.getDasharrays();
					Map<Object, Integer> sizes = atSizeVisualizer.getSizes();
					for (AttributeType t : relationColorVis.keySet())
					{

						RelationColorVisualizer rcv = relationColorVis.get(t);
						Map<Object, Color> iColors = rcv != null ? relationColorVis
								.get(t).getColors() : null;

						RelationDashVisualizer rdv = relationDashVisualizerByCollector
								.get(t);
						Map<Object, float[]> iDashArrays = rdv != null ? relationDashVisualizerByCollector
								.get(t).getDasharrays() : null;

						RelationSizeVisualizer rsv = relationSizeVisualizerByCollector
								.get(t);
						Map<Object, Integer> iSizes = rsv != null ? relationSizeVisualizerByCollector
								.get(t).getSizes() : null;

						if (iColors != null)
						{
							for (Object a : colors.keySet())
							{
								if (iColors.containsKey(a))
								{
									if (!colors.get(a).equals(iColors.get(a)))
									{
										colors.put(a, iColors.get(a));

										// System.out.printf("Replace %s with %s in %s\n",
										// colors,
										// dataColors, at);
										atColorVisualizer.setAttributeType(at);
										p.setMainGeneratorType(at.getType(), at);
										// System.out.printf("Set maingenerator type %s to attrib %s\n",
										// at.getType(), at);
									}
								}
							}
						}
						if (iDashArrays != null)
						{
							for (Object a : dasharrays.keySet())
							{
								if (iDashArrays.containsKey(a))
								{
									float[] tmpDasharrays = dasharrays.get(a);
									if ((tmpDasharrays != null)
											&& (dasharrays.get(a)!=iDashArrays.get(a)) )
									{
										dasharrays.put(a, iDashArrays.get(a));

										// System.out.printf("Replace %s with %s in %s\n",
										// colors,
										// dataColors, at);
										atDashVisualizer.setAttributeType(at);
										p.setMainGeneratorType(at.getType(), at);
										// System.out.printf("Set maingenerator type %s to attrib %s\n",
										// at.getType(), at);
									}
								}
							}
						}
						if (iSizes != null)
						{
							for (Object a : sizes.keySet())
							{
								if (iSizes.containsKey(a))
								{
									Integer tmpSizes = sizes.get(a);
									if ((tmpSizes != null)
											&& (!sizes.get(a).equals(iSizes.get(a))))
									{
										sizes.put(a, iSizes.get(a));

										// System.out.printf("Replace %s with %s in %s\n",
										// colors,
										// dataColors, at);
										atSizeVisualizer.setAttributeType(at);
										p.setMainGeneratorType(at.getType(), at);
										// System.out.printf("Set maingenerator type %s to attrib %s\n",
										// at.getType(), at);
									}
								}
							}
						}
					}
				}
			}

			return p;
		} catch (final FileNotFoundException e)
		{
			System.err.println(e.toString());
			return null;
		} catch (final IOException e)
		{
			System.err.println(e.toString());
			return null;
		} catch (final ConversionException e)
		{
			System.err.println(e.toString());
			e.printStackTrace();
			return null;
		}
		
	}

	private static void initXStream()
	{
		VennMakerXStream.init(Projekt.xstream);
		VennMakerXStream.appendBackwardsCompatibilityAliases(Projekt.xstream);
	}

	// Zaehler fuer die Hintergrundbilder damit die Reihenfolge nach dem
	// Speichern / Laden weiterlaeuft
	private int									backgroundNumber	= 0;

	// Alle Akteure im Projekt (unabhaengig von ihrer Sichtbarkeit oder
	// Netzwerkzugehoerigkeit
	private final Vector<Akteur>			akteure;

	@Deprecated
	private Vector<AkteurTyp>				akteurTypen;

	private Netzwerk							currentNetzwerk;

	private final Vector<Netzwerk>		netzwerke;

	private final Vector<RelationTyp>	relationTypen;

	private Vector<Netzwerk>				tempNetworks;

	private PaintLegendPolicy				paintLegendPolicy;

	/**
	 * Verweis aufs Ego in diesem Projekt.
	 */
	private Akteur								ego;

	/**
	 * Wenn dieses Projekt aus einem Interview entstanden ist, findet sich hier
	 * die Config dieses Interviews.
	 */
	private Config								interviewConfig;

	@SuppressWarnings("deprecation")
	public Projekt()
	{
		// erstelle Default-Projekt

		Projekt.initXStream();

		this.akteure = new Vector<Akteur>();
		this.relationTypen = new Vector<RelationTyp>();
		this.netzwerke = new Vector<Netzwerk>();
		this.tempNetworks = new Vector<Netzwerk>();
		this.filterIndex = new ArrayList<Integer>();
		this.paintLegendPolicy = new PaintLegendPolicy(true, true, true);

		final Netzwerk netzwerk = new Netzwerk();
		netzwerk.setBezeichnung(Messages.getString("Projekt.Netzwerk")); //$NON-NLS-1$
		this.netzwerke.add(netzwerk);

		final Akteur egoAkteur = new Akteur("EGO"); //$NON-NLS-1$
		// reset Actor IDs
		egoAkteur.resetGlobalID();
		this.akteure.add(egoAkteur);
		egoAkteur.setLocation(netzwerk, new Point2D.Double(0.0, 0.0));

		this.currentNetzwerk = netzwerk;

		// Standards
		RelationTyp relationTyp;
		relationTyp = new RelationTyp();
		relationTyp.setBezeichnung(Messages.getString("Projekt.RelationTyp1")); //$NON-NLS-1$
		final float[] dashPattern = { 25.0f, 9.0f, 5.0f, 9.0f };
		relationTyp.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10, dashPattern, 0));
		this.relationTypen.add(relationTyp);
		relationTyp = new RelationTyp();
		relationTyp.setBezeichnung(Messages.getString("Projekt.RelationTyp2")); //$NON-NLS-1$
		this.relationTypen.add(relationTyp);
		relationTyp = new RelationTyp();
		relationTyp.setBezeichnung(Messages.getString("Projekt.RelationTyp3")); //$NON-NLS-1$
		relationTyp.setStroke(new BasicStroke(4));
		this.relationTypen.add(relationTyp);
		relationTyp = new RelationTyp();
		relationTyp.setBezeichnung(Messages.getString("Projekt.RelationTyp4")); //$NON-NLS-1$
		relationTyp.setColor(Color.RED);
		this.relationTypen.add(relationTyp);

		EventProcessor.getInstance().resetEventListener();
		registerEventListeners();

		initAttributes();

		// Ego erst nach dem Registrieren der Listener einfuegen, sonst gibt's
		// doppelt.
		netzwerk.addAkteur(egoAkteur);
		setEgo(egoAkteur);
		projectFileName = ""; //$NON-NLS-1$
	}

	/**
	 * Initialisiert die Attribute und alles was dazugehoert (Typen,
	 * Visualisierer, ...)
	 */
	public void initAttributes()
	{
		// Wichtigkeit

		AttributeType attributeTypeImportance = new AttributeType(
				getNewAttributeTypeId());
		attributeTypeImportance.setLabel(Messages.getString("Projekt.0")); //$NON-NLS-1$
		attributeTypeImportance.setScope(Scope.NETWORK);
		attributeTypeImportance.setType("ACTOR"); //$NON-NLS-1$
		String defaultValue = Messages.getString("Projekt.1"); //$NON-NLS-1$
		String[] predefinedValues = {
				Messages.getString("Projekt.3"), Messages.getString("Projekt.2"), defaultValue, //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("Projekt.4"), Messages.getString("Projekt.5"), Messages.getString("Projekt.6") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		attributeTypeImportance.setPredefinedValues(predefinedValues);
		attributeTypeImportance.setDefaultValue(defaultValue);
		attributeTypes.add(attributeTypeImportance);

		for (Netzwerk netzwerk : netzwerke)
		{
			netzwerk.getActorSizeVisualizer().setAttributeType(
					attributeTypeImportance);
			Map<Object, Integer> sizes = new HashMap<Object, Integer>();
			sizes.put(predefinedValues[0], 5);
			sizes.put(predefinedValues[1], 7);
			sizes.put(predefinedValues[2], 9);
			sizes.put(predefinedValues[3], 11);
			sizes.put(predefinedValues[4], 13);
			sizes.put(predefinedValues[5], 16);
			netzwerk.getActorSizeVisualizer().setSizes(sizes);

			// Importiere Groessen
			for (Akteur akteur : netzwerk.getAkteure())
			{
				for (Object key : sizes.keySet())
					if (akteur.groesse.get(netzwerk) != null)
						if (akteur.groesse.get(netzwerk).equals(sizes.get(key)))
							akteur.setAttributeValue(attributeTypeImportance,
									netzwerk, key);
			}
		}

		// Akteurtyp

		AttributeType attributeTypeType = new AttributeType(
				getNewAttributeTypeId());
		attributeTypeType.setLabel(Messages.getString("Projekt.7")); //$NON-NLS-1$
		attributeTypeType.setScope(Scope.PROJECT);
		attributeTypeType.setType("ACTOR"); //$NON-NLS-1$
		if (this.akteurTypen != null)
		{
			// Importiere Attributtypen

			Vector<String> values = new Vector<String>();
			Map<Object, String> images = new HashMap<Object, String>();
			for (AkteurTyp typ : this.akteurTypen)
			{
				values.add(typ.getBezeichnung());
				images.put(typ.getBezeichnung(), typ.getImageFile());
			}
			attributeTypeType.setPredefinedValues(values.toArray());
			attributeTypeType.setDefaultValue(values.lastElement());
			attributeTypes.add(attributeTypeType);

			for (Netzwerk netzwerk : netzwerke)
			{
				netzwerk.getActorImageVisualizer().setAttributeType(
						attributeTypeType);
				netzwerk.getActorImageVisualizer().setImages(images);

				// setze Attribute
				for (Akteur akteur : netzwerk.getAkteure())
					for (String type : values)
						if (akteur.typ.getBezeichnung().equals(type))
							akteur.setAttributeValue(attributeTypeType, netzwerk,
									type, netzwerke);

			}
		}
		else
		{
			// Default-Attributtypen

			String defaultType = Messages.getString("Projekt.8"); //$NON-NLS-1$
			String[] predefinedTypes = {
					Messages.getString("Projekt.9"), Messages.getString("Projekt.10"), Messages.getString("Projekt.11"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					defaultType };
			attributeTypeType.setPredefinedValues(predefinedTypes);
			attributeTypeType.setDefaultValue(defaultType);
			attributeTypes.add(attributeTypeType);

			for (Netzwerk netzwerk : netzwerke)
			{
				netzwerk.getActorImageVisualizer().setAttributeType(
						attributeTypeType);
				Map<Object, String> images = new HashMap<Object, String>();
				images.put(predefinedTypes[0], "icons/Institut.svg"); //$NON-NLS-1$
				images.put(predefinedTypes[1], "icons/Male.svg"); //$NON-NLS-1$
				images.put(predefinedTypes[2], "icons/Female.svg"); //$NON-NLS-1$
				images.put(predefinedTypes[3], "icons/Circle.svg"); //$NON-NLS-1$
				netzwerk.getActorImageVisualizer().setImages(images);
			}
		}

		// Relation (Relationsfarbe)
		AttributeType attributeTypeRelation = new AttributeType(
				getNewAttributeTypeId());
		attributeTypeRelation.setLabel(Messages.getString("Projekt.Relation")); //$NON-NLS-1$
		attributeTypeRelation.setScope(Scope.NETWORK);
		attributeTypeRelation.setType("STANDARDRELATION"); //$NON-NLS-1$
		defaultValue = Messages.getString("Projekt.neutral"); //$NON-NLS-1$
		attributeTypeRelation
				.setPredefinedValues(new String[] {
						Messages.getString("Projekt.positive"), Messages.getString("Projekt.neutral"), Messages.getString("Projekt.negative") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		attributeTypeRelation.setDefaultValue(defaultValue);
		attributeTypes.add(attributeTypeRelation);

		// Kommentar (Freie Eingabe)
		AttributeType attributeTypeRelationComment = new AttributeType(
				getNewAttributeTypeId());
		attributeTypeRelationComment.setLabel(Messages
				.getString("Projekt.Comment")); //$NON-NLS-1$
		attributeTypeRelationComment.setScope(Scope.NETWORK);
		attributeTypeRelationComment.setType("STANDARDRELATION"); //$NON-NLS-1$
		attributeTypes.add(attributeTypeRelationComment);

		for (Netzwerk netzwerk : netzwerke)
		{
			// "if" prevents overwriting if already initialized (look at Ticket
			// Ticket #677)
			if (netzwerk
					.getRelationColorVisualizer("STANDARDRELATION",
							attributeTypeRelation).getColors().size() == 0)
			{
				netzwerk.getRelationColorVisualizer(
						"STANDARDRELATION", attributeTypeRelation) //$NON-NLS-1$
						.setAttributeType(attributeTypeRelation);

				HashMap<Object, Color> colors = new HashMap<Object, Color>();
				colors.put(Messages.getString("Projekt.neutral"), Color.black); //$NON-NLS-1$
				colors.put(Messages.getString("Projekt.positive"), Color.green); //$NON-NLS-1$
				colors.put(Messages.getString("Projekt.negative"), Color.red); //$NON-NLS-1$
				netzwerk.getRelationColorVisualizer(
						"STANDARDRELATION", attributeTypeRelation).setColors( //$NON-NLS-1$
						colors);

				netzwerk.setActiveRelationColorVisualizer("STANDARDRELATION",
						netzwerk.getRelationColorVisualizer("STANDARDRELATION",
								attributeTypeRelation));

				Map<Object, Integer> sizes = new HashMap<Object, Integer>();

				for (Object o : colors.keySet())
					sizes.put(o, 10);

				netzwerk.getRelationSizeVisualizer("STANDARDRELATION",
						attributeTypeRelation).setSizes(sizes);

				netzwerk.setActiveRelationSizeVisualizer("STANDARDRELATION",
						netzwerk.getRelationSizeVisualizer("STANDARDRELATION",
								attributeTypeRelation));

				Map<Object, float[]> dashing = new HashMap<Object, float[]>();

				for (Object o : colors.keySet())
					dashing.put(o, new float[] { 1.0f, 0.0f });

				netzwerk.getRelationDashVisualizer("STANDARDRELATION",
						attributeTypeRelation).setDasharrays(dashing);

				netzwerk.setActiveRelationDashVisualizer("STANDARDRELATION",
						netzwerk.getRelationDashVisualizer("STANDARDRELATION",
								attributeTypeRelation));
			}
		}

		// Alter des Akteurs als Beispielattribut

		AttributeType attributeTypeAge = new AttributeType(
				getNewAttributeTypeId());
		attributeTypeAge.setLabel(Messages.getString("Projekt.12")); //$NON-NLS-1$
		attributeTypeAge.setScope(Scope.PROJECT);
		attributeTypeAge.setType("ACTOR"); //$NON-NLS-1$
		attributeTypes.add(attributeTypeAge);

		// Generators

		if (this.getMainAttributeType("ACTOR") == null)
			this.setMainAttributeType("ACTOR", attributeTypeImportance); //$NON-NLS-1$
		if (this.getMainGeneratorType("ACTOR") == null)
			this.setMainGeneratorType("ACTOR", attributeTypeType); //$NON-NLS-1$

		if (this.getMainAttributeType("STANDARDRELATION") == null)
			this.setMainAttributeType("STANDARDRELATION", attributeTypeRelation); //$NON-NLS-1$
		if (this.getMainGeneratorType("STANDARDRELATION") == null)
			this.setMainGeneratorType("STANDARDRELATION", //$NON-NLS-1$
					attributeTypeRelation);
	}

	/**
	 * Liefert neue kleinstmoegliche Id fuer neuen AttributTyp zurueck
	 */
	public int getNewAttributeTypeId()
	{
		int id = -1;
		// Es werden die ids von 1 - anzahl+1 durchlaufen
		// kleinste wird zurueckgeliefert
		for (int i = 1; i <= attributeTypes.size(); i++)
		{
			id = i;
			// Es werden die Attribute die vorhanden sind auf diese Attribut ID
			// ueberprüft
			for (int u = 0; u < attributeTypes.size(); u++)
			{
				if (attributeTypes.get(u).getId() == i)
				{
					id = -1;
					break;
				}
			}
			// Wenn gefunden break
			if (id != -1)
				break;
		}
		if (id == -1)
			id = attributeTypes.size() + 1;
		return id;
	}

	public Vector<Akteur> getAkteure()
	{
		return this.akteure;
	}

	public Netzwerk getCurrentNetzwerk()
	{
		return this.currentNetzwerk;
	}

	public Vector<Netzwerk> getNetzwerke()
	{
		return this.netzwerke;
	}

	public Vector<String> getNetzwerknamen()
	{
		Vector<String> returnVector = new Vector<String>();
		for (Netzwerk tempNet : netzwerke)
		{
			returnVector.add(tempNet.getBezeichnung());
		}
		return returnVector;
	}

	public Vector<Netzwerk> getTempNetworks()
	{
		return this.tempNetworks;
	}

	public Vector<RelationTyp> getRelationTypen()
	{
		return this.relationTypen;
	}

	public boolean save(final String fileName, final File vmpFile,
			boolean wasVmpFile)
	{
		this.vmpFile = vmpFile;
		this.wasVmpFile = wasVmpFile;

		return save(fileName);
	}

	public boolean save(final String fileName)
	{
		
		if (this.modules != null)
		{
				this.modules.clear();
			for(int q=0; q < VennMaker.getInstance().getModule().size(); q++){
	
				ModuleData module = new ModuleData();
				
				module.setModuleName(VennMaker.getInstance().getModule().get(q).getModuleName());
				module.setModuleVersion(VennMaker.getInstance().getModule().get(q).getVersion());
				module.setModuleData(VennMaker.getInstance().getModule().get(q).getConfig().getSaveData());
				
				this.modules.add(module);
			}
		}
		
		try
		{
			/*
			 * refreshes the values of the times measured while the interview was
			 * active
			 */
			for (InterviewElement ie : InterviewLayer.getInstance()
					.getAllElements())
			{
				for (InterviewElementInformation iei : currentInterviewConfig
						.getElementInformations())
				{
					if (iei.getId() == ie.getId())
					{
						iei.setTime(ie.getTime());
					}
				}
			}

			// only copy, when sourcedir differs from targetdir. Only vennmaker
			// directories should be copied
			File currentWorkingDirectory = new File(
					VMPaths.getCurrentWorkingDirectory());
			File newFile = new File(fileName);

			if (!(currentWorkingDirectory.getCanonicalPath().equals(newFile
					.getCanonicalPath())) && !wasVmpFile)
			{
				String currentDir = VMPaths.getCurrentWorkingDirectory();

				if (!currentDir.endsWith(VMPaths.SEPARATOR))
					currentDir += VMPaths.SEPARATOR;

				File audio = new File(currentDir + "audio"); //$NON-NLS-1$
				File export = new File(currentDir + "export"); //$NON-NLS-1$
				File images = new File(currentDir + "images"); //$NON-NLS-1$

				/*
				 * Workaround: sollte keine vmp-Datei bis jetzt vorhanden sein (wie
				 * beispielsweise beim Starten ueber Configdialog und direktes
				 * Beenden von VennMaker ueber Next-Button) wird eine mit Namen
				 * "vmp.vmp" vorgegeben
				 */

				if (vmpFile == null)
					vmpFile = new File(newFile.getParent() + "/vmp.vmp");

				String vmpName = new String(vmpFile.getName().substring(0,
						vmpFile.getName().length() - 4));

				File tmp = new File(VMPaths.VENNMAKER_TEMPDIR
						+ "projects" + VMPaths.SEPARATOR + vmpName); //$NON-NLS-1$

				if (!tmp.exists())
					tmp.mkdirs();

				String tmpPath = tmp.getAbsolutePath();

				if (!tmpPath.endsWith(VMPaths.SEPARATOR))
					tmpPath += VMPaths.SEPARATOR;

				FileOperations.copyFolder(audio,
						new File(tmpPath + audio.getName()), 4096);
				FileOperations.copyFolder(export,
						new File(tmpPath + export.getName()), 4096);
				FileOperations.copyFolder(images,
						new File(tmpPath + images.getName()), 4096);

				this.cryptoElementsToSave = VennMakerCrypto.getInstance()
						.getCryptoElements();

				//			new ExportData().toCsv(tmpPath, "backup", ";", //$NON-NLS-1$ //$NON-NLS-2$
				//					","); //$NON-NLS-1$

				// File[] files = new File(audio.getParent()).listFiles();
				//
				// for(File f : files)
				// {
				//					if(f.getName().endsWith(".venn") || f.getName().endsWith(".vennEn"))  //$NON-NLS-1$//$NON-NLS-2$
				// {
				// FileOperations.copyFile(f,new
				// File(tmpPath+f.getName()),4096,true);
				// }
				// }
			}
		} catch (IOException exn)
		{
			exn.printStackTrace();
			return false;
		}
		Projekt.initXStream();
		try
		{
			FileOutputStream fos = new FileOutputStream(fileName);
			PrintWriter fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
					true);
			fw.write(toXml());
			fw.close();

			setProjectFileName(fileName);

			RandomAccessFile writtenFile = new RandomAccessFile(
					new File(fileName), "r");

			byte[] read = new byte[11];
			writtenFile.getChannel().position(writtenFile.length() - 10);

			writtenFile.read(read, 0, 10);

			writtenFile.close();

			if (new String(read).trim().equals(
					"</" + Projekt.class.getSimpleName().toLowerCase() + ">"))
				return true;
			else
				return false;

		} catch (final Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void setCurrentNetzwerk(final Netzwerk currentNetzwerk)
	{
		this.currentNetzwerk = currentNetzwerk;
	}

	/**
	 * Erzwingt das Anmelden aller Modellobjekte am <code>EventProcessor</code>.
	 * Nach dem Deserialisieren muss diese Methode explizit aufgerufen werden!
	 */
	protected void registerEventListeners()
	{
		// Listener anmelden.
		EventProcessor.getInstance().addEventListener(
				new EventListener<NewActorEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						((NewActorEvent) event).getAkteur().registerEventListeners();
						Projekt.this.akteure.add(((NewActorEvent) event).getAkteur());

					}

					@Override
					public Class<NewActorEvent> getEventType()
					{
						return NewActorEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<DeleteActorEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						// Lösche Akteur aus dem Pool...
						Projekt.this.akteure.remove(((DeleteActorEvent) event)
								.getAkteur());
					}

					@Override
					public Class<DeleteActorEvent> getEventType()
					{
						return DeleteActorEvent.class;
					}
				});

		// Listener anmelden
		EventProcessor.getInstance().addEventListener(
				new EventListener<NewRelationTypeEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						Projekt.this.relationTypen.add(((RelationTypeEvent) event)
								.getRelationtyp());
					}

					@Override
					public Class<NewRelationTypeEvent> getEventType()
					{
						return NewRelationTypeEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<ChangeRelationTypeEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						final ChangeRelationTypeEvent crte = (ChangeRelationTypeEvent) event;
						crte.getRelationtyp().overwrite(crte.getNewCopy());
					}

					@Override
					public Class<ChangeRelationTypeEvent> getEventType()
					{
						return ChangeRelationTypeEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<DeleteRelationTypeEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						Projekt.this.relationTypen.remove(((RelationTypeEvent) event)
								.getRelationtyp());

					}

					@Override
					public Class<DeleteRelationTypeEvent> getEventType()
					{
						return DeleteRelationTypeEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<NewNetworkEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						((NetworkEvent) event).getNetzwerk().registerEventListeners();
						Projekt.this.netzwerke.add(((NetworkEvent) event)
								.getNetzwerk());
					}

					@Override
					public Class<NewNetworkEvent> getEventType()
					{
						return NewNetworkEvent.class;
					}
				});

		EventProcessor.getInstance().addEventListener(
				new EventListener<DeleteNetworkEvent>()
				{

					@Override
					public void eventOccured(final VennMakerEvent event)
					{
						Projekt.this.netzwerke.remove(((NetworkEvent) event)
								.getNetzwerk());

					}

					@Override
					public Class<DeleteNetworkEvent> getEventType()
					{
						return DeleteNetworkEvent.class;
					}
				});

		// Rekursiv alle beteiligten Objekte ebenfalls registrieren lassen!
		for (final Akteur akteur : this.akteure)
		{
			akteur.registerEventListeners();
		}

		for (final RelationTyp typ : this.relationTypen)
		{
			typ.registerEventListeners();
		}

		for (final Netzwerk n : this.netzwerke)
		{
			n.registerEventListeners();
		}
	}

	// Nummerierung der Hintergrundbilder
	public int getBackgroundNumber()
	{
		return backgroundNumber;
	}

	public void incrementBackgroundNumber()
	{
		this.backgroundNumber++;
	}

	private String toXml()
	{
		final String xml = "<?xml version=\"1.0\"?>\n" + Projekt.xstream.toXML(this); //$NON-NLS-1$
		return xml;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#getLastEvent()
	 */
	public VennMakerEvent getLastEvent() throws NoSuchElementException
	{
		return eventLogger.getLastEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#getLastUndoEvent()
	 */
	public VennMakerEvent getLastUndoEvent()
	{
		return eventLogger.getLastUndoEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#isEmpty()
	 */
	public boolean isEmpty()
	{
		return eventLogger.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#logEvent(events.VennMakerEvent, boolean)
	 */
	public void logEvent(VennMakerEvent event, boolean clearUndoCache)
	{
		eventLogger.logEvent(event, clearUndoCache);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#redoLastEvent()
	 */
	public void redoLastEvent()
	{
		eventLogger.redoLastEvent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.EventLogger#undoLastEvent()
	 */
	public void undoLastEvent()
	{
		eventLogger.undoLastEvent();
	}

	public Akteur getEgo()
	{
		return ego;
	}

	public void setEgo(Akteur ego)
	{
		this.ego = ego;
	}

	public Config getInterviewConfig()
	{
		return interviewConfig;
	}

	public void setInterviewConfig(Config interviewConfig)
	{
		this.interviewConfig = interviewConfig;
	}

	/**
	 * Angabe, ob gegebene Attribute gerichtet oder ungerichtet sind
	 */
	private HashMap<String, Boolean>	isDirectedAttributeCollection	= new HashMap<String, Boolean>();

	public HashMap<String, Boolean> getIsDirectedAttributeCollection()
	{
		return isDirectedAttributeCollection;
	}

	public void setIsDirectedAttributeCollection(
			HashMap<String, Boolean> isDirectedAttributeCollection)
	{
		this.isDirectedAttributeCollection = isDirectedAttributeCollection;
	}

	public boolean getIsDirected(String collection)
	{
		if (isDirectedAttributeCollection.get(collection) == null)
			isDirectedAttributeCollection.put(collection, false);
		return isDirectedAttributeCollection.get(collection);
	}

	/**
	 * Die Attributtypen, die in diesem Projekt vorkommen.
	 */
	private Vector<AttributeType>	attributeTypes	= new Vector<AttributeType>();

	public Vector<AttributeType> getAttributeTypes()
	{
		return attributeTypes;
	}

	public Vector<AttributeType> getAttributeTypes(String type)
	{
		Vector<AttributeType> returnVector = new Vector<AttributeType>();
		for (AttributeType currentType : attributeTypes)
			if (type.equals(currentType.getType()))
				returnVector.add(currentType);

		return returnVector;
	}

	public void setAttributeTypes(Vector<AttributeType> t)
	{
		this.attributeTypes = t;
	}

	/**
	 * Gibt die Bezeichner der Attribute zurueck, um einzeln auf diese zugreifen
	 * zu koennen
	 * 
	 * @return Relation Group Names
	 */
	public Vector<String> getAttributeCollectors()
	{
		Vector<String> returnVector = new Vector<String>();
		for (AttributeType at : attributeTypes)
			if (!returnVector.contains(at.getType())
					&& !at.getType().equals("ACTOR")) //$NON-NLS-1$
				returnVector.add(at.getType());
		return returnVector;
	}

	/**
	 * Returns the attribute types containing predefined attribute values
	 * 
	 * @return attribute types with predefined attribute values
	 */
	public Vector<AttributeType> getAttributeTypesDiscrete()
	{
		Vector<AttributeType> r = new Vector<AttributeType>();
		for (AttributeType at : attributeTypes)
			if (at.getPredefinedValues() != null)
			{
				r.add(at);
			}
		return r;
	}

	public Vector<AttributeType> getAttributeTypesDiscrete(String getType)
	{
		List<AttributeType> aTypes = getAttributeTypesDiscrete();
		Vector<AttributeType> aDiscrete = new Vector<AttributeType>();

		for (AttributeType at : aTypes)
		{
			if (at.getType().equals(getType))
				aDiscrete.add(at);

		}
		return aDiscrete;
	}

	/**
	 * Dieser Attributtyp hat eine zentrale Bedeutung und kann zum Beispiel über
	 * das Mausrad verändert werden. Vielleicht kristallisiert sich irgendwann
	 * ein guter Name heraus.
	 */
	private HashMap<String, AttributeType>	mainAttributeTypeSet	= new HashMap<String, AttributeType>();

	/**
	 * für alte Versionen
	 */
	private AttributeType						mainAttributeType		= new AttributeType(
																							getNewAttributeTypeId());

	public AttributeType getMainAttributeType(String typeCollector)
	{
		if (mainAttributeTypeSet == null)
			mainAttributeTypeSet = new HashMap<String, AttributeType>();
		return mainAttributeTypeSet.get(typeCollector);
	}

	public void setMainAttributeType(String typeCollector,
			AttributeType mainAttributeType)
	{
		this.mainAttributeTypeSet.put(typeCollector, mainAttributeType);
	}

	/**
	 * Dieser Attributtyp wird als Generator links im Menue genutzt.
	 */
	private HashMap<String, AttributeType>	mainGeneratorTypeSet	= new HashMap<String, AttributeType>();

	/**
	 * fuer alte Versionen
	 */
	private AttributeType						mainGeneratorType		= new AttributeType(
																							getNewAttributeTypeId());

	public AttributeType getMainGeneratorType(String typeCollector)
	{
		if (mainGeneratorTypeSet == null)
			mainGeneratorTypeSet = new HashMap<String, AttributeType>();

		return mainGeneratorTypeSet.get(typeCollector);
	}

	public void setMainGeneratorType(String typeCollector,
			AttributeType mainGeneratorType)
	{
		this.mainGeneratorTypeSet.put(typeCollector, mainGeneratorType);
	}

	public InterviewSaveElement getCurrentInterviewConfig()
	{
		return currentInterviewConfig;
	}

	public void setCurrentInterviewConfig(
			InterviewSaveElement currentInterviewConfig)
	{
		this.currentInterviewConfig = currentInterviewConfig;
	}

	@Deprecated
	private Integer[]	actorSizes	= new Integer[] { 5, 7, 9, 11, 13, 16 };

	/**
	 * Wird nach dem Deserialisieren ausgefuehrt.
	 */
	private Object readResolve()
	{
		if (attributeTypes == null)
		{
			attributeTypes = new Vector<AttributeType>();
			// initAttributes();

			// for (Netzwerk netzwerk : netzwerke)
			// for (Akteur akteur : akteure)
			// {
			// // akteur.setAttributeValue(getMainAttributeType(), netzwerk,
			// // getMainAttributeType().getPredefinedValues()[Arrays
			// // .binarySearch(getMainAttributeType()
			// // .getPredefinedValues(), akteur.groesse
			// // .get(netzwerk))]);
			// }
		}

		return this;
	}

	public String getMetaInformation()
	{
		return this.metaInformation;
	}

	public void setMetaInformation(String s)
	{
		this.metaInformation = s;
	}

	/**
	 * Maximale Akteursanzahl fuer alle Netzwerkkarten festlegen
	 * 
	 * @param max
	 *           . Anzahl an Akteuren (9999: keine Maximum vorhanden)
	 */
	public void setMaxNumberActors(int n)
	{
		this.maxNumberActors = n;
	}

	/**
	 * Die maximale Akteursanzahl fuer alle Netzwerkkarten abfragen
	 * 
	 * @return maximale Akteursanzahl (9999: kein Maximum vorhanden)
	 */
	public int getMaxNumberActors()
	{
		return this.maxNumberActors;
	}

	public String getFilter()
	{
		return this.filter;
	}

	public HashMap<Integer, Vector<Filterparameter>> getFilters()
	{
		return this.filters;
	}

	public void setFilter(String filter)
	{
		this.filter = filter;
	}
	
	public void setFilters(HashMap<Integer, Vector<Filterparameter>> filters)
	{	
		this.filters = filters;
	}

	public ArrayList<Integer> getFilterIndex()
	{
		return filterIndex;
	}

	public void setFilterIndex(ArrayList<Integer> filterIndex)
	{	
		this.filterIndex = filterIndex;
	}

	public void clearFilterIndex()
	{
		this.filterIndex.clear();
	}

	// Relation Labels & Tooltips
	public void setDisplayedAtRelation(List<AttributeType> list)
	{
		displayedAtRelation = list;
	}

	public List<AttributeType> getDisplayedAtRelation()
	{
		return displayedAtRelation;
	}

	public void setDisplayedAtRelationTooltip(List<AttributeType> list)
	{
		displayedAtRelationTooltip = list;
	}

	public List<AttributeType> getDisplayedAtRelationTooltip()
	{
		return displayedAtRelationTooltip;
	}

	// Actor Label & Tooltips
	public void setDisplayedAtActor(List<AttributeType> list)
	{
		displayedAtActor = list;
	}

	public List<AttributeType> getDisplayedAtActor()
	{
		return displayedAtActor;
	}

	public void setDisplayedAtActorTooltip(List<AttributeType> list)
	{
		displayedAtActorTooltip = list;
	}

	public List<AttributeType> getDisplayedAtActorTooltip()
	{
		return displayedAtActorTooltip;
	}

	public void createTempNetworkList()
	{
		this.tempNetworks = new Vector<Netzwerk>();
	}

	/**
	 * Setzt das Flag, das anzeigt, ob die Akteursnamen ver- oder entschluesselt
	 * sind.
	 * 
	 * @param encodeFlag
	 *           the encodeFlag to set
	 */
	public void setEncodeFlag(boolean encodeFlag)
	{
		this.encodeFlag = encodeFlag;
	}

	/**
	 * Liefert den Status des Verschluesselungsflag zurueck
	 * 
	 * @return the encodeFlag: true, wenn verschluesselt
	 */
	public boolean isEncodeFlag()
	{
		return encodeFlag;
	}

	public String toString()
	{
		return projectFileName;
	}

	public Netzwerk getNetworkForId(int id)
	{
		for (Netzwerk n : netzwerke)
		{
			if (n.getId() == id)
				return n;
		}
		return null;
	}

	/**
	 * Returns the current <code>PaintLegendPolicy</code>
	 * 
	 * @return the current <code>PaintLegendPolicy</code>
	 */
	public PaintLegendPolicy getPaintLegendPolicy()
	{
		return paintLegendPolicy;
	}

	/**
	 * Sets the current <code>PaintLegendPolicy</code>
	 * 
	 * @param paintLegendPolicy
	 *           the current <code>PaintLegendPolicy</code>
	 */
	public void setPaintLegendPolicy(PaintLegendPolicy paintLegendPolicy)
	{
		this.paintLegendPolicy = paintLegendPolicy;
	}

	public void filter()
	{

	}
}
