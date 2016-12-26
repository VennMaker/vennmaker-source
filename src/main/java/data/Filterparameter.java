package data;
	/**
	 * Hilfsklasse, um statt String eine Sammlung an Voraussetzungen zu speichern
	 */
	public class Filterparameter
	{
		/** der Attributstyp */
		public AttributeType	at;

		/** zu welchem Wert soll verglichen werden */
		public Object			value;

		/**
		 * In welche "Richtung" soll verglichen werden (LOWER, LOWER_EQUALS,
		 * NOT_EQUALS, HIGHER_EQUALS, HIGHER, EMPTY)
		 */
		public Integer			compareDirection;
	}