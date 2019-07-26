package data;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * Beschreibt den Typ der Relation zwischen zwei Akteuren. Dies beinhaltet die
 * graphische Repraesentation bei Auswahl (Icon) und die Liniendarstellung.
 * 
 * 
 */
@Deprecated
public class RelationTyp

{
	/**
	 * Gibt verschiedene Möglichkeiten an wie die Richtung von Kanten zu
	 * interpretieren ist. Diese <code>enum</code> ist sicherlich noch nicht
	 * vollständig. Möglicherweise sind auch interaktive Fragen denkbar.
	 * 
 * 
	 * 
	 */
	public enum DirectionType
	{
		UNDIRECTED("undirected"), DIRECTED("directed");

		private String	label;

		private DirectionType(String s)
		{
			this.label = s;
		}

		public String getTypeName()
		{
			return label;
		}
	}

	/**
	 * Der Richtungstyp
	 */
	private DirectionType	dirType	= DirectionType.UNDIRECTED;

	/**
	 * Der Name der Relationsart
	 */
	private String				bezeichnung;

	/**
	 * Die Farbe der zu zeichnenden visuellen Darstellung (Kante)
	 */
	private Color				color		= Color.BLACK;

	/**
	 * Der Stift, mit dem die Relation gezeichnet werden soll.
	 */
	private BasicStroke		stroke	= new BasicStroke(2);

	/**
	 * Gibt den Richtungstyp des Kantentyps zurück. Wird u.a. zum Zeichnen
	 * gebraucht.
	 * 
	 * @return Der Richtungstyp des Relationstyps.
	 */
	public DirectionType getDirectionType()
	{
		// Legacy: Alte Dateien enthalten keine Information. Daher wird Standard
		// gesetzt.
		if (dirType == null)
			dirType = DirectionType.UNDIRECTED;
		return dirType;
	}

	/**
	 * Setzt die Richtungseigenschaft des Relationstyp
	 * 
	 * @param newType
	 */
	public void setDirectionType(DirectionType newType)
	{
		this.dirType = newType;
	}

	/**
	 * Fertigt eine tiefe Kopie (alle Elemente werden ebenfalls kopiert) an.
	 */
	public Object clone()
	{
		RelationTyp t = new RelationTyp();

		// Strings sind unveränderbar, daher reicht Referenz aus.
		t.bezeichnung = bezeichnung;
		// Farben sind unveränderbar, dienen nur zur Kapselung
		t.color = color;

		// enums sind unveränderbar
		t.dirType = dirType;

		// Erzeuge Klon von BasicStroke.
		t.stroke = new BasicStroke(stroke.getLineWidth(), stroke.getEndCap(),
				stroke.getLineJoin(), stroke.getMiterLimit(),
				stroke.getDashArray(), stroke.getDashPhase());

		return t;
	}

	/**
	 * Überschreibt alle Werte durch diejenigen, die in newCopy gespeichert sind.
	 * 
	 * @param newCopy
	 *           Die neuen Werte für diesen Typ.
	 */
	public void overwrite(RelationTyp newCopy)
	{
		bezeichnung = newCopy.bezeichnung;
		color = newCopy.color;
		dirType = newCopy.dirType;
		stroke = new BasicStroke(newCopy.stroke.getLineWidth(), newCopy.stroke
				.getEndCap(), newCopy.stroke.getLineJoin(), newCopy.stroke
				.getMiterLimit(), newCopy.stroke.getDashArray(), newCopy.stroke
				.getDashPhase());
	}

	/**
	 * Liefert <code>true</code> wenn die beiden Relationstypen inhaltlich
	 * identisch sind. Dies wird gebraucht um mögliche Änderungen zu sehen.
	 * 
	 * @return <code>false</code> wenn es Unterschiede gibt.
	 */
	public boolean equals(Object o)
	{
		RelationTyp r = (RelationTyp) o;

		if ( (r != null) && (dirType != null) )
		return (dirType.equals(r.dirType) && bezeichnung.equals(r.bezeichnung)
				&& color.equals(r.color) && stroke.equals(r.stroke));
		
		return false;
	}

	public RelationTyp()
	{
	}

	/**
	 * Erzwingt das Anmelden aller Modellobjekte am <code>EventProcessor</code>.
	 * Nach dem Deserialisieren muss diese Methode explizit aufgerufen werden!
	 * Dies wird von <code>Projekt.registerEventListeners()</code> erledigt.
	 */
	protected void registerEventListeners()
	{

	}

	public RelationTyp(String bezeichnung)
	{
		this.bezeichnung = bezeichnung;
	}

	public String getBezeichnung()
	{
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung)
	{
		this.bezeichnung = bezeichnung;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public BasicStroke getStroke()
	{
		return stroke;
	}

	public void setStroke(BasicStroke strole)
	{
		this.stroke = strole;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return getBezeichnung();
	}
	
}
