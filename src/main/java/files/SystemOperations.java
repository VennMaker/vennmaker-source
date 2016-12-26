package files;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * class to provide different functions using the system, VennMaker is currently
 * running on
 * 
 * 
 * 
 */
public class SystemOperations
{
	private Desktop	desktop;

	/** method to load a given url in the system standard browser */
	public static void openUrl(String url) throws IOException,
			URISyntaxException
	{
		if (java.awt.Desktop.isDesktopSupported())
		{
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			if (desktop.isSupported(java.awt.Desktop.Action.BROWSE))
			{
				java.net.URI uri = new java.net.URI(url);
				desktop.browse(uri);
			}
		}
	}

	/**
	 * method to open a file or an URI with the default program
	 * 
	 * @param o
	 *           the file/URI to open
	 * @return true, if successful, false else
	 */
	public boolean open(Object o)
	{

		if (o != null)
			// Is the object type correct?
			if ((o.getClass().getName().equals("java.io.File"))
					|| (o.getClass().getName().equals("java.net.URI")))

				// Is the given action supported by the the current platform?
				if (Desktop.isDesktopSupported())
				{
					desktop = Desktop.getDesktop();

					if (o.getClass().getName().equals("java.io.File"))
					{
						if (((File) o).exists())

							try
							{
								desktop.open((File) o);
							} catch (IOException exn)
							{
								return false;
							}
						else
							return false;

					}
					else if (o.getClass().getName().equals("java.net.URI"))
					{
						try
						{
							desktop.browse((URI) o);
						} catch (IOException exn)
						{
							return false;
						}
					}
					else
					{
						return false;
					}

					return true;
				}

		return false;
	}

}
