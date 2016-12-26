/**
 * 
 */
package files;

import gui.ErrorCenter;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import data.Netzwerk;
import data.Projekt;

/**
 * 
 * 
 */
public class FileOperations
{
	/** saves the root-Folder of VennMaker */
	private static String				rootFolder	= "";

	private static ZipOutputStream	zipout;

	/**
	 * copies one file
	 * 
	 * @param src
	 *           Source
	 * @param dest
	 *           Destination
	 * @param bufSize
	 *           Buffersize
	 * @param force
	 *           Overwrite existing files?
	 * @throws IOException
	 */
	public static void copyFile(File src, File dest, int bufSize, boolean force)
			throws IOException
	{
		// Wenn Src-File und Dest-File die gleichen sind braucht nicht kopiert
		// zu
		// werden
		// ohne Ueberpruefung verschwindet die Datei in einem solchen Fall...
		if (src.getAbsolutePath().equals(dest.getAbsolutePath()))
			return;

		if (dest.exists())
		{
			if (force)
			{
				dest.delete();
			}
			else
			{

				gui.ErrorCenter.manageException(new Exception(),
						Messages.getString("FileOperations.0") //$NON-NLS-1$
								+ dest.getName(), ErrorCenter.ERROR, true, true);

			}
		}
		byte[] buffer = new byte[bufSize];
		int read = 0;
		InputStream in = null;
		OutputStream out = null;
		try
		{
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
			while (true)
			{
				read = in.read(buffer);
				if (read == -1)
				{
					// -1 bedeutet EOF
					break;
				}
				out.write(buffer, 0, read);
			}
		} finally
		{
			// Sicherstellen, dass die Streams auch
			// bei einem throw geschlossen werden.
			// Falls in null ist, ist out auch null!
			if (in != null)
			{
				// Falls tatsaechlich in.close() und out.close()
				// Exceptions werfen, die jenige von 'out' geworfen wird.
				try
				{
					in.close();
				} finally
				{
					if (out != null)
					{
						out.close();
					}
				}
			}
		}
	}

	/**
	 * copies a Folder
	 * 
	 * @param src
	 *           Source
	 * @param dest
	 *           Destination
	 * @param bufSize
	 *           Buffersize
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest, int bufSize)
			throws IOException
	{
		if (src.isDirectory())
		{
			if (!dest.exists())
				dest.mkdir();
			String[] entries = src.list();
			
			if (entries != null)
			for (int x = 0; x < entries.length; x++)
			{
				File aktFile = new File(src.getPath(), entries[x]);
				File destFile = new File(dest.getPath(), entries[x]);
				copyFolder(aktFile, destFile, bufSize);
			}
		}
		else
		{
			copyFile(src, dest, bufSize, true);
		}
	}

	/**
	 * copies a Folder without those Files to exclude
	 * 
	 * @param src
	 *           Source
	 * @param dest
	 *           Destination
	 * @param bufSize
	 *           Buffersize
	 * @throws IOException
	 */
	public static void exclusiveCopyFolder(File src, File dest, int bufSize,
			String exclude) throws IOException
	{
		if (src.isDirectory())
		{
			if (!dest.exists())
				dest.mkdir();
			String[] entries = src.list();
			if (entries != null)
			for (int x = 0; x < entries.length; x++)
			{
				File aktFile = new File(src.getPath(), entries[x]);
				File destFile = new File(dest.getPath(), entries[x]);
				exclusiveCopyFolder(aktFile, destFile, bufSize, exclude);
			}
		}
		else
		{
			if (!src.getName().toLowerCase().endsWith(exclude))
				copyFile(src, dest, bufSize, true);
		}
	}

	/**
	 * creates the subfolders for audio, etc and copies standardfiles
	 * 
	 * @param rootPath
	 *           defines the root for these subfolders
	 */
	public static boolean createSubfolders(String rootPath)
	{
		boolean check = false;

		check = new File(rootPath + "/" + VMPaths.VENNMAKER_IMAGES).mkdir(); //$NON-NLS-1$
		if (check == true)
			check = new File(rootPath + "/" + VMPaths.VENNMAKER_BACKGROUNDIMAGES).mkdir(); //$NON-NLS-1$
		if (check == true)
			check = new File(rootPath + "/" + VMPaths.VENNMAKER_SYMBOLS).mkdir(); //$NON-NLS-1$
		if (check == true)
			check = new File(rootPath + "/" + VMPaths.VENNMAKER_AUDIO).mkdir(); //$NON-NLS-1$
		if (check == true)
			check = new File(rootPath + "/" + VMPaths.VENNMAKER_EXPORT).mkdir(); //$NON-NLS-1$

		if (check == false)
			return false;

		String[] data = new File(getAbsolutePath("./icons")).list(); //$NON-NLS-1$

		if (data == null)
			return false;

		for (int i = 0; i < data.length; i++)
		{
			if (data[i].toLowerCase().endsWith(".svg")) //$NON-NLS-1$
			{
				File sourceFile = new File(getAbsolutePath("./icons/" + data[i])); //$NON-NLS-1$
				File destinationFile = new File(rootPath
						+ "/" + VMPaths.VENNMAKER_SYMBOLS //$NON-NLS-1$
						+ sourceFile.getName());
				try
				{
					FileOperations.copyFile(sourceFile, destinationFile, 1024, true);
				} catch (IOException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
					gui.ErrorCenter
							.manageException(
									exn,
									Messages.getString("FileOperations.1"), ErrorCenter.ERROR, true, true); //$NON-NLS-1$

					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns the Contents of rootDir filtered by the given filter
	 * 
	 * @param rootDir
	 * @param filter
	 * @return A Vector with the Foldercontents
	 */
	public static Vector<String> getFolderContents(String rootDir, String filter)
	{
		File dir = new File(rootDir);
		Vector<String> filteredData = new Vector<String>();

		if (dir != null && dir.exists())
		{
			String[] data = dir.list();
			if (data != null)
			{
				if (!filter.equals("FOLDER")) //$NON-NLS-1$
				{
					for (int i = 0; i < data.length; i++)
						if (data[i].toLowerCase().endsWith(filter))
							filteredData.add(data[i]);
				}
				else
				{
					for (int i = 0; i < data.length; i++)
						if (new File(rootDir + "/" + data[i]).isDirectory()) //$NON-NLS-1$
							filteredData.add(data[i]);
				}
			}
		}
		return filteredData;
	}

	/**
	 * loescht einen Ordner samt Unterordner
	 * 
	 * @param dir
	 *           der ordner, der geloescht werden soll
	 * @return erfolg?
	 */
	public static boolean deleteFolder(File dir)
	{
		if (dir.isDirectory())
		{
			String[] entries = dir.list();
			for (int x = 0; x < entries.length; x++)
			{
				File aktFile = new File(dir.getPath(), entries[x]);
				deleteFolder(aktFile);
			}
			if (dir.delete())
				return true;
			else
				return false;
		}
		else
		{
			if (dir.delete())
				return true;
			else
				return false;
		}
	}

	/**
	 * loescht den Temp-Ordner und setzt ihn auf standard zurueck
	 */
	public static void resetTempFolder()
	{
		//		deleteFolder(new File("./projects/temp")); //$NON-NLS-1$
		//		(new File("./projects/temp/")).mkdir(); //$NON-NLS-1$
		//		FileOperations.createSubfolders("./projects/temp/"); //$NON-NLS-1$

		deleteFolder(new File(VMPaths.VENNMAKER_TEMPDIR
				+ "projects" + VMPaths.SEPARATOR + "temp" + VMPaths.SEPARATOR)); //$NON-NLS-1$ //$NON-NLS-2$
		(new File(VMPaths.VENNMAKER_TEMPDIR
				+ "projects" + VMPaths.SEPARATOR + "temp" + VMPaths.SEPARATOR)).mkdir(); //$NON-NLS-1$ //$NON-NLS-2$
		FileOperations.createSubfolders(VMPaths.VENNMAKER_TEMPDIR
				+ "projects" + VMPaths.SEPARATOR + "temp" + VMPaths.SEPARATOR); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * checks, whether given folder is correct projectfolder or not
	 * 
	 * @param root
	 *           folder to check
	 * @return true, if root is correct projectfolder false, else
	 */
	public static boolean checkSubfolders(String root)
	{
		if (!root.endsWith("/")) //$NON-NLS-1$
			root = root + "/"; //$NON-NLS-1$
		if (new File(root + VMPaths.VENNMAKER_AUDIO).exists() //$NON-NLS-1$
				&& new File(root + VMPaths.VENNMAKER_IMAGES).exists()) //$NON-NLS-1$
			if (new File(root + VMPaths.VENNMAKER_AUDIO).isDirectory() //$NON-NLS-1$
					&& new File(root + VMPaths.VENNMAKER_IMAGES).isDirectory()) //$NON-NLS-1$
				return true;
		return false;
	}

	/**
	 * empties temp folder moves an old file to temp folder creates standard
	 * subfolders main issue: import files of older vennmaker versions does not
	 * delete the original file
	 * 
	 * @param fileToMove
	 *           the old file
	 * @return new filename
	 */
	public static String moveOldFile(String fileToMove)
	{
		FileOperations.resetTempFolder();
		File oldFile = new File(fileToMove);
		//		File newFile = new File("./projects/temp/" + oldFile.getName()); //$NON-NLS-1$
		File newFile = new File(VMPaths.VENNMAKER_TEMPDIR + "projects" //$NON-NLS-1$
				+ VMPaths.SEPARATOR + "temp" + VMPaths.SEPARATOR); //$NON-NLS-1$
		try
		{
			FileOperations.copyFile(oldFile, newFile, 256, true);
		} catch (IOException exn)
		{
			gui.ErrorCenter
					.manageException(
							exn,
							Messages.getString("FileOperations.1"), ErrorCenter.ERROR, true, true); //$NON-NLS-1$
			return null;
		}
		return newFile.toString();
	}

	/**
	 * checks, whether a given folder contains more than "audio/" and "images/" <br />
	 * done by a simple view at the number of files in this folder.
	 * 
	 * @param folder
	 *           - folder to check
	 * @return true, if it's empty <br />
	 *         false, else
	 */
	public static boolean isEmpty(String folder)
	{
		File f = new File(folder);
		if (f != null && f.list() != null)
		if (new File(folder).list().length > 2)
			return false;
		return true;
	}

	/**
	 * changes the pathname of all required images and sounds
	 * 
	 * @param folder
	 *           new foldername - replaces the old rootfolder
	 */
	public static void changeRootFolder(String folder)
	{
		// folder in standardform bringen
		if (!folder.endsWith("/") && !folder.endsWith("\\"))
			folder += "/";

		// Hauptverlinkungen setzen

		// alle Akteure in allen Netzwerken durchlaufen, und Bilder neu
		// verknuepfen
		for (Netzwerk network : VennMaker.getInstance().getProject()
				.getNetzwerke())
		{
			Map<Object, String> images = new HashMap<Object, String>();

			// Workaround fuer aeltere VennMaker Projekte
			// hier fehlen AttributTypen zur Darstellung,
			// diese muessen erst explizit erzeugt werden...
			if (network.getActorImageVisualizer().getImages() == null)
				VennMaker.getInstance().getProject().initAttributes();

			if (network.getActorImageVisualizer().getImages() != null)
			{
				for (Object activeImage : network.getActorImageVisualizer()
						.getImages().keySet())
				{

					images.put(activeImage,
							folder
									+ VMPaths.VENNMAKER_SYMBOLS
									+ new File(network.getActorImageVisualizer()
											.getImage(activeImage)).getName()); //$NON-NLS-1$
				}

			}

			network.getActorImageVisualizer().setImages(images);
		}

		// HintergrundBilder verschieben und neu verknuepfen

		// Ordner fuer Hintergruende erstellen
		File backImagesDir = new File(folder + "/"
				+ VMPaths.VENNMAKER_BACKGROUNDIMAGES);
		if (!backImagesDir.exists())
			backImagesDir.mkdir();

		for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
		{
			if (n.getHintergrund().getFilename() != null)
			{
				File oldBgSelectionFile = new File(n.getHintergrund().getFilename());
				File oldBgFile = new File(n.getHintergrund()
						.getFilenameOfOriginalImage());

				if (!oldBgSelectionFile.equals(oldBgFile))
				{
					File newBgSelectionFile = new File(backImagesDir,
							normalizeFileName(oldBgSelectionFile.getName()));
					n.getHintergrund().setFilenameOfSelection(
							newBgSelectionFile.getAbsolutePath());
				}

				File newBgFile = new File(backImagesDir,
						normalizeFileName(oldBgFile.getName()));
				n.getHintergrund().setFilename(newBgFile.getAbsolutePath());
			}
		}
	}

	/**
	 * Returns the filename in a path which is not cannonical to the path format
	 * used by the current platform. (e.g Windows VMPaths on a Linux platform)
	 * 
	 * @param filename
	 *           in wrong format
	 * @return filename in cannonical format to the currently used platform
	 */
	public static String normalizeFileName(String filename)
	{
		if (filename.contains("/"))
		{
			String[] files = filename.split("/");
			return files[files.length - 1];
		}
		else if (filename.contains("\\"))
		{
			String[] files = filename.split("\\\\");
			return files[files.length - 1];
		}

		return filename;
	}

	/**
	 * The public method wich is called if a zip file should be created
	 * 
	 * @param source
	 *           -> the source directory wich will be zipped
	 * @param dest
	 *           -> the destination directory the *.vmp file will be saved
	 */
	public static void zip(File source, File dest)
	{
		String separator = System.getProperty("file.separator"); //$NON-NLS-1$
		String destination = dest.getAbsolutePath();

		try
		{
			if (!destination.endsWith("vmp"))
			{
				if (!destination.endsWith(separator))
					destination += separator;

				zipout = new ZipOutputStream(
						new BufferedOutputStream(new FileOutputStream(destination
								+ source.getName() + ".vmp"))); //$NON-NLS-1$
			}

			else
				zipout = new ZipOutputStream(new FileOutputStream(destination));

			File[] files = source.listFiles();
			if (files != null)
			for (int i = 0; i < files.length; i++)
			{
				createZip(files[i], new StringBuffer(files[i].getName()), zipout);
			}
			zipout.close();
		} catch (FileNotFoundException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		} catch (Exception exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		}
	}

	/**
	 * This method zips a directory and its subdirectories to a *.vmp file
	 */

	private static void createZip(File file, StringBuffer stbuff,
			ZipOutputStream zipout) throws Exception
	{
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			if (files != null && files.length > 0)
			{
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						int chars = stbuff.length();

						stbuff.append("/" + files[i].getName()); //$NON-NLS-1$
						StringBuffer sbuff = new StringBuffer(stbuff);
						stbuff.delete(chars, stbuff.length());
						createZip(files[i], sbuff, zipout);
					}
					else
					{
						BufferedInputStream bin = new BufferedInputStream(
								new FileInputStream(files[i].getAbsolutePath()));
						ZipEntry entry = new ZipEntry(stbuff.toString() + "/" //$NON-NLS-1$
								+ files[i].getName());

						zipout.putNextEntry(entry);

						for (int c = bin.read(); c != -1; c = bin.read())
						{
							zipout.write(c);
						}
					}
				}
			}
			else
			{
				stbuff.append("/."); //$NON-NLS-1$
				ZipEntry entry = new ZipEntry(stbuff.toString());
				zipout.putNextEntry(entry);
				zipout.flush();
				zipout.closeEntry();
			}
		}
		else
		{
			BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
					file.getAbsolutePath()));
			ZipEntry entry = new ZipEntry(stbuff.toString());

			zipout.putNextEntry(entry);

			for (int c = bin.read(); c != -1; c = bin.read())
			{
				zipout.write(c);
			}
			zipout.flush();
			zipout.closeEntry();
		}
	}

	/**
	 * This Method unzips a *.vmp file into the temp directory of VennMaker wich
	 * is in temp directory of the operating system (e.g Linux: /tmp/VennMaker)
	 * 
	 * @param file
	 *           : *.vmp file to unzip
	 */
	public static void unzip(File file)
	{

		String[] name = file.getName().split(".vmp"); //$NON-NLS-1$

		String tmpDir = VMPaths.VENNMAKER_TEMPDIR
				+ "projects" + VMPaths.SEPARATOR + name[0] + VMPaths.SEPARATOR; //$NON-NLS-1$

		File projectFolder = new File(tmpDir);

		if (!projectFolder.exists())
		{
			projectFolder.mkdirs();
		}

		String lastPath = ""; //$NON-NLS-1$

		try
		{
			ZipFile zip = new ZipFile(file);

			for (Enumeration<? extends ZipEntry> entr = zip.entries(); entr
					.hasMoreElements();)
			{
				ZipEntry entry = (ZipEntry) entr.nextElement();
				String entryPath = new File(entry.toString()).getParent();

				if (entry.toString().endsWith("/.")) //$NON-NLS-1$
				{
					new File(tmpDir
							+ entry.toString().substring(0,
									entry.toString().length() - 1)).mkdirs();
				}
				else
				{
					if (entry.toString().contains("/") && !lastPath.equals(entryPath)) //$NON-NLS-1$
					{
						StringBuffer path = new StringBuffer(tmpDir);
						String[] folders = entry.toString().split("/"); //$NON-NLS-1$

						for (int i = 0; i < folders.length - 1; i++)
						{
							path.append(folders[i] + VMPaths.SEPARATOR);
						}

						File f = new File(path.toString());
						f.mkdirs();
						lastPath = entryPath;
					}

					InputStream zipin = zip.getInputStream(entry);
					BufferedOutputStream bout = new BufferedOutputStream(
							new FileOutputStream(tmpDir + entry.getName()));
					int c = 0;
					while ((c = zipin.read()) != -1)
					{
						bout.write(c);
					}
					bout.flush();
					bout.close();
					zipin.close();
				}
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			if (e instanceof ZipException)
				System.out
						.println("File not unzipped, no zip file or an new created vmp file"); //$NON-NLS-1$
			else
				e.printStackTrace();
		}
	}

	/**
	 * Creates a random named folder in the temp directory (e.g
	 * /tmp/xsdadfpakVennMaker)
	 * 
	 * @return a random String
	 */
	public static String createRandomFolder()
	{
		char[] chars = new char[10];

		for (int i = 0; i < chars.length; i++)
		{
			chars[i] = (char) ((int) (Math.random() * 25) + 97);
		}

		return new String(chars);
	}

	/**
	 * searches for the root-path of VennMaker by looking for the icons directory
	 * and returns the given relative path as an absolute path relative to the
	 * VennMaker rootpath
	 * 
	 * @param relativePath
	 *           the relative path pointing to a resource needed by VennMaker
	 *           (e.g. icons/intern/VennMakerLogo-gross.png)
	 * @return returns the absolute path (e.g.
	 *         /home/user/VennMaker/icons/intern/VennMakerLogo-gross.png)
	 */
	public static String getAbsolutePath(String relativePath)
	{
		/*
		 * if not yet set, set the rootFolder now and store it in the local
		 * variable
		 */
		if (rootFolder.equals(""))
		{
			/*
			 * URLDecoder to allow special chars in the current directory ([, ],
			 * ..)
			 */
			try
			{
				// System.out.println(URLClassLoader.getSystemResource("gui/VennMaker.class").getFile());
				String filename = URLDecoder.decode(URLClassLoader
						.getSystemResource("gui/VennMaker.class").getFile(), "UTF-8");

				rootFolder = new File(filename.replace("file:/", "/"))
						.getAbsolutePath().replaceAll("\\\\", "/");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();

			}

			/* cutting the path (removing className) */
			rootFolder = rootFolder.substring(0, rootFolder.lastIndexOf('/'))
					+ "/";
			/*
			 * go back in the filestructure until ./icons is found or the root of
			 * the filesystem is reached
			 */
			while (!new File(rootFolder + "/icons").exists()
					&& rootFolder.length() > 0)
			{
				int i = rootFolder.lastIndexOf('/');
				if (i != -1)
				{
				rootFolder = rootFolder.substring(0, i);
				//System.out.println(rootFolder);
				}else break;
				
			}

			/* set this folder to the new rootFolder */
			rootFolder = rootFolder + "/";
		}

		return rootFolder + relativePath;
	}

	/**
	 * Check whether the file exists
	 * 
	 * @param filename
	 * @return true if the file exists or false if the file not exists
	 */
	public static boolean existFile(String file)
	{
		File f = new File(file);
		return f.exists();
	}

	/**
	 * Loads a vmp file as project
	 * 
	 * @param vmpFile
	 *           the vmp file to load
	 * @param vennFile
	 *           the venn file inside the vmp file to load
	 * @param directory
	 *           the last visited directory, if <code>null</code> the home
	 *           directory of the user will be choosen
	 */
	public static void openVmpFile(File vmpFile, String vennFile,
			String directory)
	{
		// backward compatibility: (vmpFile can be null)
		//
		// if(vmpFile == null)
		// return;

		if (vennFile == null)
			return;

		if (directory == null)
			directory = System.getProperty("user.home");

		VMPaths.setVmpFile(vmpFile);

		VMPaths.setLastVisitedDirectory(directory);

		Projekt p = Projekt.load(vennFile);
		if (p == null)
		{
			JOptionPane.showMessageDialog(VennMaker.getInstance(),
					Messages.getString("VennMaker.ErrorLoading") //$NON-NLS-1$
							+ vennFile, Messages.getString("VennMaker.Error"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			VennMaker.getInstance().performLoadProject(new File(vennFile), p);
		}

		for (VennMakerView view : VennMaker.getInstance().getViews())
			view.updateSectorAndCircleAttributes();

		VennMaker.getInstance().setChangesSaved(true);
	}

	/**
	 * checks the VennMaker-folder, if the given folders are present - if not,
	 * returns the foldernames of the ones missing.
	 * 
	 * @return the missing but needed folders in the VennMaker-mainfolder
	 */
	public static String getMissingFolders()
	{
		Vector<String> neededFolders = new Vector<String>();
		neededFolders.add("./icons");
		neededFolders.add("./icons/intern");
		
		String returnString = "";
		
		
		for (String folder: neededFolders){
			if (!(new File(folder).exists()))
				returnString += folder + "\r\n";
		}
		
		return returnString;
	}
}
