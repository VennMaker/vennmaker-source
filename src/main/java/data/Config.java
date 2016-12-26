package data;

import interview.elements.InterviewElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Vector;

import wizards.VennMakerWizard;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;

/**
 * Ein Config-Objekt beschreibt eine vorkonfigurierte Umgebung, die im
 * Kiosk-Modus genutzt wird.
 * 
 * Enthalten ist das initial zu ladende Netzwerk, ein zu startender Wizard sowie
 * verschiedene Eigenschaften die die Programmoberfläche verändern/einschränken.
 * 
 * Methoden zum Speichern und öffnen in/aus einer XML-Datei sind vorhanden.
 * 
 */
public class Config
{
	/**
	 * Ein Wrapper für die Möglichkeiten, doppelte Akteure geeignet zu behandeln.
	 * 
	 * 
	 */
	public enum DuplicateBehaviour
	{
		NOT_ALLOWED("Not allowed"), ALLOWED("Allowed"), ASK_USER("Ask");
		public String toString()
		{
			return name;
		}

		private String	name;

		private DuplicateBehaviour(String name)
		{
			this.name = name;
		}
	}

	/**
	 * Ein Wrapper für die Möglichkeiten, die Labels zu platzieren.
	 * 
	 * 
	 */
	public enum LabelBehaviour
	{
		ASIDE("right"), ASIDE_LEFT("left"), ON_TOP("over"), NO_LABEL("no label"), UNDER("under"), NUMBER(
				"number");
		final private String	label;

		public String getLabel()
		{
			return label;
		}

		private LabelBehaviour(final String s)
		{
			this.label = s;
		}
	}

	/**
	 * Enthält eine Liste aller Wizards, diese werden im Interview nacheinander
	 * in der gegebenen Reihenfolge gestartet.
	 * 
	 * @deprecated
	 */
	private Vector<VennMakerWizard>	wizards;
	
	/**
	 * Das Root-Element des Interviews.
	 */
	private InterviewElement interviewElementRoot;
	
	/**
	 * Liste der bereits besuchten InterviewElements für die Zurück-Funktion.
	 */
	private Vector<InterviewElement> interviewElementsFinished = new Vector<InterviewElement>();

	/**
	 * Enthält eine Liste aller Fragen, die zu, gegebenen zeitpunkt vom
	 * QuestionController gestellt werden.
	 */
	private Vector<Question>			questions;

	private boolean						egoDisabled				= false;

	private boolean						egoMoveable				= false;

	private boolean						egoResizable			= true;

	private DuplicateBehaviour			duplicateBehaviour	= DuplicateBehaviour.ASK_USER;

	private LabelBehaviour				labelBehaviour			= LabelBehaviour.UNDER;

	private static XStream				xstream					= new XStream();

	private static void initXStream()
	{
		xstream.alias("config", data.Config.class); //$NON-NLS-1$
		xstream.addImplicitCollection(Projekt.class, "netzwerke"); //$NON-NLS-1$
		xstream.alias("netzwerk", data.Netzwerk.class); //$NON-NLS-1$
		xstream.alias("akteur", data.Akteur.class); //$NON-NLS-1$
		xstream.alias("akteurTyp", data.AkteurTyp.class); //$NON-NLS-1$
		xstream.alias("relation", data.Relation.class); //$NON-NLS-1$
		xstream.alias("AkteurTyp", data.AkteurTyp.class); //$NON-NLS-1$
		xstream.useAttributeFor(data.Akteur.class, "id"); //$NON-NLS-1$
		xstream.omitField(data.Akteur.class, "view"); //$NON-NLS-1$
		xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
	}

	public Config()
	{
		initXStream();
		wizards = new Vector<VennMakerWizard>();
		questions = new Vector<Question>();
	}

	private String toXml()
	{
		String xml = "<?xml version=\"1.0\"?>\n" + xstream.toXML(this); //$NON-NLS-1$
		return xml;
	}

	/**
	 * Speichert die Konfiguration in einer <code>.vennEn</code> Datei ab.
	 * 
	 * @param fileName
	 *           Dateiname wo gespeichert wird.
	 * @return <code>true</code> wenn das Speichern erfolgreich war, sonst
	 *         <code>false</code>.
	 */
	public boolean save(String fileName)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(fileName);
			PrintWriter fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
					true);
			fw.write(toXml());
			fw.close();
			
			RandomAccessFile writtenFile = new RandomAccessFile(new File(fileName), "r");
			
			byte[] read = new byte[11];
			writtenFile.getChannel().position(writtenFile.length() - 10);
			
			writtenFile.read(read, 0, 10);

			writtenFile.close();
			
			if (new String(read).trim().equals(
					"</" + Config.class.getSimpleName().toLowerCase() + ">"))
				return true;
			else
				return false;
			
		} catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * Lädt eine <code>.vennEn</code>-Datei an der angegebenen Position. Außerdem
	 * werden benötigte Registrierungen am <code>EventProcessor</code>
	 * vorgenommen.
	 * 
	 * @param fileName
	 *           Die zu öffnende Datei.
	 * @return Eine gültige Konfiguration.
	 * @throws FileNotFoundException
	 *            wenn die Datei nicht gefunden wurde.
	 * @throws IOException
	 *            wenn die Datei nicht geöffnet werden könnte.
	 */
	public static Config load(String fileName) throws FileNotFoundException,
			IOException, ConversionException
	{
		initXStream();
		FileInputStream is = new FileInputStream(fileName);
		Config p = (Config) xstream.fromXML(is);
		is.close();

		return p;
	}

	/**
	 * Gibt die Liste der Wizards zurück
	 * 
	 * @return Liste der Wizards
	 */
	public Vector<VennMakerWizard> getWizards()
	{
		return wizards;
	}

	/**
	 * Setzt die Liste der Wizards.
	 * 
	 * @param wizards
	 *           Liste der Wizards
	 */
	public void setWizards(Vector<VennMakerWizard> wizards)
	{
		this.wizards = wizards;
	}

	/**
	 * Gibt die Liste der Wizards zurück
	 * 
	 * @return Liste der Wizards
	 */
	public Vector<Question> getQuestions()
	{
		return questions;
	}

	/**
	 * Setzt die Liste der Fragen.
	 * 
	 * @param questions
	 *           Liste der Fragen
	 */
	public void setQuestions(Vector<Question> questions)
	{
		this.questions = questions;
	}

	/**
	 * @return the duplicateBehaviour
	 */
	public final DuplicateBehaviour getDuplicateBehaviour()
	{
		return duplicateBehaviour;
	}

	/**
	 * @param duplicateBehaviour
	 *           the duplicateBehaviour to set
	 */
	public final void setDuplicateBehaviour(DuplicateBehaviour duplicateBehaviour)
	{
		this.duplicateBehaviour = duplicateBehaviour;
	}

	/**
	 * @return the labelBehaviour
	 */
	public final LabelBehaviour getLabelBehaviour()
	{
		if (this.labelBehaviour == null)
			labelBehaviour = LabelBehaviour.UNDER;
		return labelBehaviour;
	}

	/**
	 * @param labelBehaviour
	 *           the labelBehaviour to set
	 */
	public final void setLabelBehaviour(LabelBehaviour labelBehaviour)
	{
		this.labelBehaviour = labelBehaviour;
	}

	/**
	 * @return the egoDisabled
	 */
	public boolean isEgoDisabled()
	{
		return egoDisabled;
	}

	/**
	 * @param egoDisabled
	 *           the egoDisabled to set
	 */
	public void setEgoDisabled(boolean egoDisabled)
	{
		this.egoDisabled = egoDisabled;
	}

	/**
	 * @return the egoResizable
	 */
	public boolean isEgoMoveable()
	{
		return egoMoveable;
	}

	/**
	 * Set ego as moveable
	 * @param egoMoveable true / false
	 * 
	 */
	public void setEgoMoveable(boolean egoMoveable)
	{
		this.egoMoveable = egoMoveable;
	}

	/**
	 * @return the egoResizable
	 */
	public boolean isEgoResizable()
	{
		return egoResizable;
	}

	/**
	 * @param egoResizable
	 *           the egoResizable to set
	 */
	public void setEgoResizable(boolean egoResizable)
	{
		this.egoResizable = egoResizable;
	}

	/**
	 * Seitenverhältnis der Zeichenfläche (Breite/Höhe)
	 */
	private float	viewAreaRatio	= 1.33f;

	public float getViewAreaRatio()
	{
		return viewAreaRatio;
	}

	public void setViewAreaRatio(float viewAreaRatio)
	{
		this.viewAreaRatio = viewAreaRatio;
	}

}
