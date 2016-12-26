/**
 * 
 */
package export;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Wandelt Dateninput in CSV formatierten Datenoutput um
 * 
 * 
 * 
 */
public class CsvOutput
{

	/**
	 * Returns a CSV formated string. Equates to a row in CSV format.
	 * 
	 * @param elementlist
	 *           Input: String or Number (int, double)
	 * @param separator
	 *           Defines the delimiter symbol (e.g. , or ; ...)
	 * @param textelement
	 *           Defines the identification character for text elements
	 * @param format
	 *           Defines the numeric print output format. (e.g. comma or point:
	 *           1,3 or 1.3)
	 *           
	 * @return CSV formated string
	 */
	public String csvToRow(List<Object> elementlist, String separator,
			String textelement, String format)
	{
		String result = "";
		String numberformat = "";

		/**
		 * Defines the number format: comma or point
		 */
		if (format.equals(","))
			numberformat = "%,4f";
		else
			numberformat = "%.4f";

		Iterator iter = elementlist.iterator();
		while (iter.hasNext())
		{
			Object o = iter.next();

			String value;

			if (o!=null){

			if (o.getClass().getName().equals("java.lang.Double")
					|| o.getClass().getName().equals("java.lang.Float"))
			{
				if (format.equals(","))
					value = String.format(Locale.GERMAN, "%,4f", o);
				else
					value = String.format(Locale.US, "%.4f", o);	
			}
			else if (o.getClass().getName().equals("java.lang.Integer")
					|| o.getClass().getName().equals("java.lang.Long"))
				value = "" + o;
			else

			if (o.getClass().getName().equals("java.lang.String"))
				value = textelement + o + textelement;
			else
				value = "";
			}
			else
				value ="";

			if (iter.hasNext())
				value += separator;

			result += value;
		}

		result += "\n";
		
		return result;
	}
}
