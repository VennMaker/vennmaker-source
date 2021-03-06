/**
 * 
 */
package data;

import java.text.DateFormat;
import java.util.Date;

/**
 * 
 * Repräsentiert den Inhalt eines Kommentarfeldes sowie organisatorischen Krams...
 * Als Typargument wird der Typ des Kommentarinhalts definiert. Kann also auch Multimedia-Krams
 * oder sonstiges sein, nicht nur String.
 * @param <E> Der Typ des Kommentars.
 * 
 * 
 *
 */
public class Kommentar<E>
{
	private E content;
	private long timestamp;
	
	public Kommentar(E content)
	{
		this.content = content;
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Creation Time
	 * @return String Repräsentation des Erzeugungszeitpunktes.
	 */
	public String getCTime()
	{
		return DateFormat.getTimeInstance().format(new Date(this.timestamp));
	}
	
	public String toString()
	{
		return this.content.toString();
	}
}
