/**
 * 
 */
package data;

import java.util.Map;
import java.util.Vector;

/**
 * Dieses Interface implementieren alle Klassen, die Attribute verarbeiten
 * können.
 * 
 * 
 */
public interface AttributeSubject
{
	public void setAttributeValue(AttributeType type, Netzwerk network,
			Object value);

	public void setAttributeValue(AttributeType type, Netzwerk network,
			Object value, Vector<Netzwerk> networks);

	public Object getAttributeValue(AttributeType type, Netzwerk network);

	public Map<AttributeType, Object> getAttributes(Netzwerk network);
}
