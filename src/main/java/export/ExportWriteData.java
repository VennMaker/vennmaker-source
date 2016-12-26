package export;

import gui.ErrorCenter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class ExportWriteData
{
	
	public static void writeToFile(String filename, String content){

		FileOutputStream fos;
		PrintWriter fw;
		
			try
			{
				fos = new FileOutputStream(filename);
				try
				{
					fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
							true);
					
					fw.write(content); 
					fw.close();
				} catch (UnsupportedEncodingException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e)
			{
				ErrorCenter
				.manageException(
						e,
						"Can't write file", ErrorCenter.ERROR, false, true); //$NON-NLS-1$
				// TODO Auto-generated catch block
			}

		
		
	}

}
