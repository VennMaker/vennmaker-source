package files;

import java.io.File;

/**
 * This class contains constants for every folder in VennMaker. Every object
 * which uses paths to vennmaker related folders or files should use the
 * constants specified in this class.
 * NOTE: Every pathname stored in a constant ends with a path seperator
 * 
 * 
 * 
 */
public class VMPaths
{
	/**
	 * File Seperator of current system
	 */
	public static final String	SEPARATOR						= System.getProperty("file.separator");	//$NON-NLS-1$
	
	/**
	 * The temporary directory of VennMaker. The path of this directory depends
	 * to the used operating system
	 */
	public static final String	VENNMAKER_TEMPDIR				= createTempDir();

	/**
	 * The temporary image directory of VennMaker
	 */
	public static final String	VENNMAKER_IMAGES				= "images/";

	/**
	 * The temporary audio directory of VennMaker
	 */
	public static final String	VENNMAKER_AUDIO				= "audio/";

	/**
	 * The temporary export directory of VennMaker
	 */
	public static final String	VENNMAKER_EXPORT				= "export/";

	/**
	 * The temporary symbols folder of VennMaker
	 */
	public static final String	VENNMAKER_SYMBOLS				= VENNMAKER_IMAGES+"symbols/";

	/**
	 * The temporary background images folder of VennMaker
	 */
	public static final String	VENNMAKER_BACKGROUNDIMAGES	= VENNMAKER_IMAGES+"backgroundimages/";

	/**
	 * The icon folder for all standard used icons in VennMaker. These are copied to
	 * <code>VENNMAKER_SYMBOLS</code> after starting VennMaker.
	 */
	public static final String	VENNMAKER_ICONS				= "icons/";

	/**
	 * The internal icon folder contains all icons which are used for buttons,
	 * menue entries etc.
	 */
	public static final String	VENNMAKER_INTERNAL_ICONS	= VENNMAKER_ICONS
																					+ "intern/";
	/**
	 * Das aktuelle Wurzelverzeichnis der Projekte
	 */
	private static String currentWorkingRoot					= VENNMAKER_TEMPDIR + "projects" + VMPaths.SEPARATOR;			//$NON-NLS-1$

	/**
	 * Der aktuelle Pfad (Aktuelles Projekt)
	 */
	private static String currentWorkingDirectory			= currentWorkingRoot	+ "temp" + VMPaths.SEPARATOR;				//$NON-NLS-1$

	/**
	 * Last visited directory of some dialogs (for example: open project dialog)
	 */
	private static String lastVisitedDirectory;
	
	/**
	 * Last used file name
	 */
	private static String lastFileName;
	
	/**
	 * currently opened VennMaker project file
	 */
	private static File vmpFile;
	
	
	
	/**
	 * reset all current used paths or files
	 */
	public static void reset()
	{
		currentWorkingRoot = VENNMAKER_TEMPDIR + "projects" + VMPaths.SEPARATOR;			//$NON-NLS-1$
		currentWorkingDirectory	= currentWorkingRoot	+ "temp" + VMPaths.SEPARATOR;				//$NON-NLS-1$
		vmpFile = null;
		lastVisitedDirectory = null;
		lastFileName = null;
	}

	/**
	 * Creates a temporary Directory to work with "unsaved" VennMaker
	 */
	private static String createTempDir()
	{
		String randomString = FileOperations.createRandomFolder();
		String sysTemp = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		StringBuffer stbuff = new StringBuffer();
		boolean again = false;

		while (!again)
		{
			if (sysTemp.endsWith(SEPARATOR))
			{
				stbuff.append(sysTemp);
				stbuff.append(randomString);
				stbuff.append("VennMaker"); //$NON-NLS-1$
				stbuff.append(SEPARATOR);
			}
			else
			{
				stbuff.append(sysTemp);
				stbuff.append(SEPARATOR);
				stbuff.append(randomString);
				stbuff.append("VennMaker"); //$NON-NLS-1$
				stbuff.append(SEPARATOR);
			}

			String tempDir = stbuff.toString();
			File directory = new File(tempDir);

			again = !directory.exists();
		}

		return stbuff.toString();
	}
	
	/**
	 *	Returns the last file name
	 */
	public static String getLastFileName()
	{
		return lastFileName;
	}

	/**
	 * Set the last file name
	 */
	public static void setLastFileName(String s)
	{
		lastFileName = s;
	}
	
	/**
	 * Returns the current Working Directory
	 */
	public static String getCurrentWorkingDirectory()
	{
		return currentWorkingDirectory;
	}

	/**
	 * Set the current working directory
	 */
	public static void setCurrentWorkingDirectory(String newWorkingDirectory)
	{
		currentWorkingDirectory = newWorkingDirectory;
	}
	
	/**
	 * Returns the current working root
	 */
	public static String getCurrentWorkingRoot()
	{
		return currentWorkingRoot;
	}
	
	/**
	 * Set the value for current working root
	 */
	public static void setCurrentWorkingRoot(String newWorkingRoot)
	{
		currentWorkingRoot = newWorkingRoot;
	}
	
	/**
	 * Returns the last visited directory
	 */
	public static String getLastVisitedDirectory()
	{
		return lastVisitedDirectory;
	}

	/**
	 * Set the value for last visited directory
	 */
	public static void setLastVisitedDirectory(String s)
	{
		lastVisitedDirectory = s;
	}
	
	/**
	 * Returns the VennMaker Projekt File
	 */
	public static File getVmpFile()
	{
		return vmpFile;
	}

	/**
	 * Set the VennMaker Projekt File
	 */
	public static void setVmpFile(File s)
	{
		vmpFile = s;
	}
}
