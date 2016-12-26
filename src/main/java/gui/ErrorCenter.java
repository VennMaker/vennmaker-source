package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.ErrorSender;
import files.FileOperations;

/**
 * 
 * 
 *         Diese Klasse verwaltet die Exceptions und prüft je nach Priorität
 *         (Info, Warning, Error) ob die Exception gelogged werden soll oder
 *         nicht.
 * 
 * 
 */
public class ErrorCenter
{
	public static final int			INFO		= 0;

	public static final int			WARNING	= 1;

	public static final int			ERROR		= 2;

	public static final boolean	LOG		= true;

	public static final boolean	DIALOG	= true;

	private static JTextArea		messageField = new JTextArea();

	private static JRadioButton	rbutton;

	private static JPanel			messagePanel;
	

	/***
	 * 
	 * @param e
	 *           : die Exception
	 * @param message
	 *           : Die Nachricht die im Dialog anzeigt werden soll
	 * @param prio
	 *           : Die Priorität der Exception
	 * @param log
	 *           : true wenn die Exception geloggt werden soll
	 * @param dialog
	 *           true wenn ein Dialog angezeigt werden soll
	 */
	public static void manageException(Exception e, String message, int prio,
			boolean log, boolean dialog)
	{
		if (dialog)
		{
			createDialog(e, message, prio);
		}
		String msg = createMessageBody(e, message, prio);

		if (log)
		{
			logToFile(msg);
		}
	}

	private static void createDialog(final Exception e, final String message,
			int prio)
	{

		ImageIcon i = null;
		final JDialog frame = new JDialog();
		String title = "";

		switch (prio)
		{
			case INFO:
				i = new ImageIcon("./icons/intern/large-info.png");
				title = "Info";
				break;
			case WARNING:
				i = new ImageIcon("./icons/intern/large-alert.png");
				title = "Warning";
				break;
			case ERROR:
				i = new ImageIcon("./icons/intern/large-stop.png");
				title = "Error";
				break;

			default:
				throw new IllegalArgumentException("Falsche Priorität");
		}

		messagePanel = new JPanel(new CardLayout());
		JButton okButton = new JButton("OK");
		JLabel errorLabel = new JLabel(message);
		JLabel icon = new JLabel(i);
		rbutton = new JRadioButton("Mehr Infos");

		rbutton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent ev)
			{
				CardLayout c = (CardLayout) messagePanel.getLayout();

				if ((rbutton.isSelected()) && (e != null))
				{
					StackTraceElement[] elem = e.getStackTrace();
					messageField.setText("");
					messageField.append(e.toString() + "\n");
					for (StackTraceElement el : elem)
					{
						messageField.append(el.toString() + "\n");
						messageField.revalidate();
						messageField.repaint();
					}
					messageField
							.append("\nThis information was also written to logfile \"errors.log\"\nin the VennMaker directory");
					e.printStackTrace();
					c.show(messagePanel, "text");
				}
				else
				{
					c.show(messagePanel, "empty");
				}

			}
		});

		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
			}
		});

		JButton sendButton = new JButton("Send error");
		sendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (ErrorSender.sendError(message)==true){			
					messageField.setText("Data successfully sent.");
				}
				else
					messageField.setText("Error: Data NOT successfully sent!");

			}
		});
		JPanel panel = new JPanel(new GridLayout(2, 1));
		JPanel labelPanel = new JPanel();

		labelPanel.add(icon);
		labelPanel.add(errorLabel);

		panel.add(labelPanel);
		panel.add(rbutton);

		JPanel emptyPanel = new JPanel();
		JPanel textFieldPanel = new JPanel(new GridLayout(0, 1));

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		// buttonPanel.add(sendButton);

		JScrollPane jp = new JScrollPane(messageField);
		textFieldPanel.add(jp);

		messagePanel.add(emptyPanel, "empty");
		messagePanel.add(textFieldPanel, "text");

		frame.setLayout(new BorderLayout());
		frame.add(panel, BorderLayout.NORTH);
		frame.add(messagePanel, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage());
		frame.setModal(true);
		frame.setSize(562, 300);
		frame.setTitle(title);

		int x = VennMaker.getInstance().getX() + frame.getWidth() / 2;
		int y = VennMaker.getInstance().getY() + frame.getHeight() / 2;

		frame.setLocation(x, y);
		frame.setVisible(true);

	}

	private static void logToFile(final String message)
	{
		File output = new File("./errors.log");
		try
		{
			FileWriter fw = new FileWriter(output, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(message);
			bw.close();
		} catch (IOException exc)
		{
			// TODO Auto-generated catch block
			exc.printStackTrace();
		}
	}

	private static String createMessageBody(Exception e, final String message,
			int prio)
	{
		StringBuffer stbuff = new StringBuffer();

		switch (prio)
		{
			case INFO:
				stbuff.append("===INFO===");
				break;
			case WARNING:
				stbuff.append("===WARNING===");
				break;
			case ERROR:
				stbuff.append("===ERROR===");
				break;

			default:
				throw new IllegalArgumentException("Falscher Prioritätswert");
		}

		Calendar cal = Calendar.getInstance();
		stbuff.append("\nDate: " + cal.get(Calendar.DAY_OF_MONTH) + "."
				+ +(cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR)
				+ "\nTime: " + cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + ":"
				+ cal.get(Calendar.MILLISECOND));

		stbuff.append("\n");
		if (e != null){
			 String arraystring = Arrays.toString(e.getStackTrace());      
			stbuff.append(arraystring);
		}

		Runtime rt = Runtime.getRuntime();

		Properties systemProps = System.getProperties();
		Set<Entry<Object, Object>> sets = systemProps.entrySet();

		stbuff.append("\n" + "systems properties");

		for (Entry<Object, Object> entry : sets)
		{
			stbuff.append("\n" + entry.getKey() + ", value: " + entry.getValue());
		}

		stbuff.append("\n\nMemory information (in bytes):");
		stbuff.append("\nFree memory: " + rt.freeMemory());
		stbuff.append("\nMax memory: " + rt.maxMemory());
		stbuff.append("\nTotal memory: " + rt.totalMemory());
		stbuff.append("\nMemory used: " + (rt.totalMemory() - rt.freeMemory()));
		stbuff.append("\nDisk space: " + new File(".").getUsableSpace());
		stbuff.append("\n");
		stbuff.append(message + "\n");
		stbuff.append("Occured Exception:\n");
		if (e != null)
			stbuff.append(e.getMessage());

		stbuff.append("\n\n");

		return stbuff.toString();
	}

}
