/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import files.FileOperations;

/**
 * 
 * 
 */
public class TestVersion extends JFrame
{

	private static Dialog	d;

	private static String	version	= "";

	private static String	endDate	= "";

	private static long		testEnd	= 0;

	private static int		level		= 0;
	
	private static boolean logoON = false;
	
	private static boolean exportON = true;

	/**
	 * Set the time status of the trial version
	 * 
	 * @param v
	 *           The version of VennMaker (trial version)
	 * @param dat
	 *           yyyy-MM-dd HH:mm:ss.S
	 * 
	 * 
	 */
	public static void setTime(String v, String dat)
	{

		version = v;
		endDate = dat;
		try
		{
			Date dt = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

			dt = df.parse(endDate);
			testEnd = dt.getTime();
		} catch (ParseException exn)
		{
			// TODO Auto-generated catch block
			exn.printStackTrace();
		}

	}

	/**
	 * Testet, ob die Testzeit schon abgelaufen ist
	 * 
	 * @return true ist abgelaufen
	 */
	public static Boolean testVersion()
	{
		long zeit = System.currentTimeMillis();

		if (zeit > testEnd)
			return true;

		return false;
	}

	/**
	 * Returns the VennMaker version information
	 * 
	 * @return current VennMaker version information
	 */
	public static String getVersion()
	{
		return version;
	}

	/**
	 * Returns the expiration date of the trial version
	 * 
	 * @return the expiration date of the trial version
	 */
	public static long getEndDate()
	{
		return testEnd;
	}

	/**
	 * Liefert den Informationsdialog zurueck
	 */
	public static void infoDialog()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dialog d = new Dialog(VennMaker.getInstance(), "INFO", true); //$NON-NLS-1$

		d.setLayout(new BorderLayout());
		d.setBackground(Color.green);

		d.addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent arg0)
			{
			}

			public void windowActivated(WindowEvent arg0)
			{
			}

			public void windowClosing(WindowEvent arg0)
			{
			}

			public void windowDeactivated(WindowEvent arg0)
			{

			}

			public void windowDeiconified(WindowEvent arg0)
			{
			}

			public void windowIconified(WindowEvent arg0)
			{
			}

			public void windowOpened(WindowEvent arg0)
			{
			}
		});

		Button ok = new Button("OK");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				d.setVisible(false);
			}
		});

		d.add(new JLabel(
				"<html><body>VennMaker "
						+ version
						+ " (trial version)!<br><br>"
						+ "In the trial version the export function is disabled.<br><br>"
						+ "For more information please visit: <b>www.vennmaker.com </b><br><br>"),
				BorderLayout.NORTH);
		d.add(ok, BorderLayout.SOUTH);
		d.pack();

		d.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$
		int top = (screenSize.height - d.getHeight()) / 2;
		int left = (screenSize.width - d.getWidth()) / 2;
		d.setLocation(left, top);

		d.setVisible(true);

	}

	/**
	 * Hier kann man einstellen, wie stark die Testversion eingeschränkt sein
	 * soll z.B. 0 = keine Einrschraenkungen ausser die zeitliche Frist 1 = Logo
	 * auf NWK, kein Export moeglich
	 * 
	 * @param testlevel
	 *           Level
	 */
	public static void setLevel(int testlevel)
	{
		level = testlevel;
	}
	
	
	/**
	 * Switch the VennMaker Logo on
	 * 
	 */
	public static void setLogoON(){
		logoON = true;
	}
	
	
	/**
	 * Switch export functionality off
	 */
	public static void setExportOFF(){
		exportON = false;
	}


	/**
	 * Soll das VennMaker-Logo in der NWK angezeigt werden?
	 * 
	 * @return true: ja, false: nein
	 */
	public static boolean isLogo()
	{

		return logoON;

	}

	/**
	 * Data export enabled?
	 * 
	 * @return true: yes, false: no
	 */
	public static boolean isExport()
	{
		return exportON;

	}
}
