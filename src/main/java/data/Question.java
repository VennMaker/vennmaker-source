/**
 * 
 */
package data;

/**
 * Diese Klasse repräsentiert eine Frage, die im Interview gestellt werden soll.
 * Die Antwort wird im Datenmodell gespeichert.
 * 
 */
public class Question
{

	/**
	 * Text der Frage, die dem Proband gestellt werden soll.
	 */
	private String		question;

	/**
	 * Interner Bezeichner für diese Frage, der bei der Auswertung als alleinige
	 * Referenzierung der Frage dient. Der Forscher sollte innerhalb eines
	 * Interviews die labels eindeutig halten.
	 */
	private String		label;

	/**
	 * Liste der vorgegeben Antwortmöglichkeiten. Kann auch leer bleiben wenn nur
	 * freie Eingaben gewünscht sind.
	 */
	private String[]	predefinedAnswers;

	/**
	 * Verschiedene Arten des Visual Mappings.
	 */
	public enum VisualMappingMethod
	{
		NONE(), ACTOR_TYPE(), ACTOR_SIZE(), RELATION_TYPE(), SECTOR(), CIRCLE();
	}

	private VisualMappingMethod	visualMappingMethod;

	/**
	 * Soll diese Frage für alle Akteure gleichzeitig in einer Matrix erfolgen?
	 * 
	 * Bei der VisualMapping-Methode ACTOR_SIZE erfolgt die Abfrage durch
	 * Einteilung in Buckets.
	 */
	private boolean					matrix						= false;

	/**
	 * Kann zu jeder vorgegebenen Antwortmöglichkeit eine Visualisierung
	 * enthalten.
	 */
	private Object[]					visualMapping;

	/**
	 * Soll Mehrfachauswahl erlaubt sein?
	 */
	private boolean					allowMultipleSelection	= false;

	/**
	 * "no answer" als Antwort anbieten.
	 */
	private boolean					offerNoAnswer				= true;

	/**
	 * "don't know" als Antwort anbieten.
	 */
	private boolean					offerUnknown				= true;

	/**
	 * Sollen freie Eingaben erlaubt sein?
	 */
	private boolean					freeForm						= false;

	/**
	 * Datentyp für die freien Eingaben
	 */
	private DataType					dataType;

	/**
	 * Mögliche Datentypen: Text oder numerisch
	 */
	public enum DataType
	{
		STRING(), NUMERICAL();
	}

	/**
	 * Minimaler Wert bei numerischen Eingaben.
	 */
	private int		numericalMinValue	= Integer.MIN_VALUE;

	/**
	 * Maximaler Wert bei numerischen Eingaben.
	 */
	private int		numericalMaxValue	= Integer.MAX_VALUE;

	/**
	 * Einheit der numerischen Eingaben.
	 */
	private String	numericalUnit;

	/**
	 * Mögliche Elemente, zu denen Fragen gestellt werden können: Ego, Alteri,
	 * Relationen.
	 */
	public enum Subject
	{
		EGO(), ALTER(), RELATION();
	}

	/**
	 * Zu welchem Element soll eine Frage gestellt werden?
	 */
	private Subject	subject;

	/**
	 * Mögliche Zeitpunkte für die Frage: Zum Start, Ende oder beim hinzufügen
	 * des Elements.
	 */
	public enum Time
	{
		START(), ADDING(), END();
	}

	/**
	 * Wann soll die Frage gestellt werden?
	 * 
	 * Nicht alle Kombinationen aus AskSubject und AskTime geben Sinn und sind
	 * implementiert.
	 */
	private Time				time;

	/**
	 * Zugeordneter Attributtyp, dem die Antworten zugeordnet werden.
	 */
	private AttributeType	attributeType;

	/**
	 * @return the question
	 */
	public String getQuestion()
	{
		return question;
	}

	/**
	 * @param question
	 *           the question to set
	 */
	public void setQuestion(String question)
	{
		this.question = question;
	}

	/**
	 * @return the label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param label
	 *           the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/**
	 * @return the predefinedAnswers
	 */
	public String[] getPredefinedAnswers()
	{
		return predefinedAnswers;
	}

	/**
	 * @param predefinedAnswers
	 *           the predefinedAnswers to set
	 */
	public void setPredefinedAnswers(String[] predefinedAnswers)
	{
		this.predefinedAnswers = predefinedAnswers;
	}

	/**
	 * @return the allowMultipleSelection
	 */
	public boolean isAllowMultipleSelection()
	{
		return allowMultipleSelection;
	}

	/**
	 * @param allowMultipleSelection
	 *           the allowMultipleSelection to set
	 */
	public void setAllowMultipleSelection(boolean allowMultipleSelection)
	{
		this.allowMultipleSelection = allowMultipleSelection;
	}

	/**
	 * @return the offerNoAnswer
	 */
	public boolean isOfferNoAnswer()
	{
		return offerNoAnswer;
	}

	/**
	 * @param offerNoAnswer
	 *           the offerNoAnswer to set
	 */
	public void setOfferNoAnswer(boolean offerNoAnswer)
	{
		this.offerNoAnswer = offerNoAnswer;
	}

	/**
	 * @return the offerUnknown
	 */
	public boolean isOfferUnknown()
	{
		return offerUnknown;
	}

	/**
	 * @param offerUnknown
	 *           the offerUnknown to set
	 */
	public void setOfferUnknown(boolean offerUnknown)
	{
		this.offerUnknown = offerUnknown;
	}

	/**
	 * @return the freeForm
	 */
	public boolean isFreeForm()
	{
		return freeForm;
	}

	/**
	 * @param freeForm
	 *           the freeForm to set
	 */
	public void setFreeForm(boolean freeForm)
	{
		this.freeForm = freeForm;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType()
	{
		return dataType;
	}

	/**
	 * @param dataType
	 *           the dataType to set
	 */
	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * @return the numericalMinValue
	 */
	public int getNumericalMinValue()
	{
		return numericalMinValue;
	}

	/**
	 * @param numericalMinValue
	 *           the numericalMinValue to set
	 */
	public void setNumericalMinValue(int numericalMinValue)
	{
		this.numericalMinValue = numericalMinValue;
	}

	/**
	 * @return the numericalMaxValue
	 */
	public int getNumericalMaxValue()
	{
		return numericalMaxValue;
	}

	/**
	 * @param numericalMaxValue
	 *           the numericalMaxValue to set
	 */
	public void setNumericalMaxValue(int numericalMaxValue)
	{
		this.numericalMaxValue = numericalMaxValue;
	}

	/**
	 * @return the numericalUnit
	 */
	public String getNumericalUnit()
	{
		return numericalUnit;
	}

	/**
	 * @param numericalUnit
	 *           the numericalUnit to set
	 */
	public void setNumericalUnit(String numericalUnit)
	{
		this.numericalUnit = numericalUnit;
	}

	/**
	 * @return the subject
	 */
	public Subject getSubject()
	{
		return subject;
	}

	/**
	 * @param subject
	 *           the subject to set
	 */
	public void setSubject(Subject subject)
	{
		this.subject = subject;
	}

	/**
	 * @return the time
	 */
	public Time getTime()
	{
		return time;
	}

	/**
	 * @param time
	 *           the time to set
	 */
	public void setTime(Time time)
	{
		this.time = time;
	}

	/**
	 * @return the visualMappingMethod
	 */
	public VisualMappingMethod getVisualMappingMethod()
	{
		return visualMappingMethod;
	}

	/**
	 * @param visualMappingMethod
	 *           the visualMappingMethod to set
	 */
	public void setVisualMappingMethod(VisualMappingMethod visualMappingMethod)
	{
		this.visualMappingMethod = visualMappingMethod;
	}

	/**
	 * @return the visualMapping
	 */
	public Object[] getVisualMapping()
	{
		return visualMapping;
	}

	/**
	 * @param visualMapping
	 *           the visualMapping to set
	 */
	public void setVisualMapping(Object[] visualMapping)
	{
		this.visualMapping = visualMapping;
	}

	public boolean isMatrix()
	{
		return matrix;
	}

	public void setMatrix(boolean buckets)
	{
		this.matrix = buckets;
	}

	public AttributeType getAttributeType()
	{
		return attributeType;
	}

	public void setAttributeType(AttributeType attributeType)
	{
		this.attributeType = attributeType;
	}

}
