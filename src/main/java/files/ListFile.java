package files;

import java.io.File;

public class ListFile extends File
{
	/**
	 * @param pathname
	 */
	public ListFile(String pathname)
	{
		super(pathname);
	}

	public String toString()
	{
		return getName();
	}
}