package gui.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VennMakerUIConfig {

	private static float fontSize = 0.0f;
	
	/**
	 * Get global font size.
	 * @return
	 */
	public static float getFontSize() {
		
		if (fontSize == 0.0f)
		{
			fontSize = 12.0f;
			BufferedReader brTest;
			try {
				brTest = new BufferedReader(new FileReader("vennmakerfontsize.txt"));
				
				try {
					fontSize = new Float(brTest.readLine());
				} catch (NumberFormatException e) {
				} catch (IOException e) {
				}
			} catch (FileNotFoundException e1) {
			    System.out.println("vennmakerfontsize.txt not found");

			}
			
		    System.out.println("Firstline is : " + fontSize);
		}
		return fontSize;
	}
	
	/**
	 * Set the global font size
	 * @param float size
	 * @return 
	 */
	public static boolean setFontSize(float size) {
		
		PrintWriter writer;
		try {
			writer = new PrintWriter("vennmakerfontsize.txt", "UTF-8");
			writer.println(size);
			writer.close();
			
		} catch (FileNotFoundException e) {
			return false;
		} catch (UnsupportedEncodingException e) {
			return false;
		}
		fontSize = size;
		return true;
	}
}
