package gui.utilities;

/**
 * 
 * 
 *
 */
public class StringUtilities
{
	/**
	 * Maximale Lnge fr Strings im TaskPane 
	 */
	public static final int	MAX_TASK_PANE_LABEL_LENGTH	= 14;
	
	/**
	 * 
	 */
	public static final int DEFAULT_MAX_LABEL_LENGTH = 14;
	
	/**
	 * Shortens a string in VennMaker style
	 * 
	 * @param label
	 * @return 
	 */
	public static String truncate(String label)
	{
		return truncate(label, MAX_TASK_PANE_LABEL_LENGTH);
	}

	public static String truncate(String label, int maxLength) {
		String truncatedString = label;
		if(label != null && label.length() > maxLength) {
			truncatedString = label.substring(0, maxLength) + "..."; //$NON-NLS-N$
		}
		return truncatedString;
	}
	
}
