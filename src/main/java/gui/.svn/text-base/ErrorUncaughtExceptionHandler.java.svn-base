package gui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.ErrorSender;
import files.FileOperations;
import files.IO;
import files.VMPaths;

public class ErrorUncaughtExceptionHandler implements
		Thread.UncaughtExceptionHandler, ActionListener
{

	StringBuffer	info;

	JDialog			f;
	JTextArea descriptionText;
	
	private JTextArea textoutput = new JTextArea();


	public void uncaughtException(Thread thread, Throwable throwable)
	{

		info = new StringBuffer();
		f = new JDialog();

		Container c = f.getContentPane();

		f.setTitle("Error center"); //$NON-NLS-1$
		f.setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		f.setModal(true);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);


		Calendar cal = Calendar.getInstance();

		JButton ok = new JButton(
				Messages.getString("ErrorUncaughtExceptionHandler.0")); //$NON-NLS-1$

		ok.setActionCommand("Close"); //$NON-NLS-1$
		ok.addActionListener(this);

		JButton send = new JButton(
				Messages.getString("ErrorUncaughtExceptionHandler.3")); //$NON-NLS-1$
		JButton save = new JButton(
				Messages.getString("ErrorUncaughtExceptionHandler.4")); //$NON-NLS-1$
		JButton end = new JButton("Close VennMaker");

		end.setActionCommand("END");
		end.addActionListener(this);

		save.setActionCommand("SAVE"); //$NON-NLS-1$
		save.addActionListener(this);

		send.setActionCommand("SEND"); //$NON-NLS-1$
		send.addActionListener(this);

		info.append("\n==== Uncaught Exception ===="); //$NON-NLS-1$
		info.append("\nDate: " + cal.get(Calendar.DAY_OF_MONTH) + "." + //$NON-NLS-1$ //$NON-NLS-2$
				+(cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR) + //$NON-NLS-1$
				"\nTime: " + cal.get(Calendar.HOUR_OF_DAY) + ":" //$NON-NLS-1$ //$NON-NLS-2$
				+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + ":" //$NON-NLS-1$ //$NON-NLS-2$
				+ cal.get(Calendar.MILLISECOND));
		String str = Arrays.toString(thread.getStackTrace());
		info.append("\n" + str); //$NON-NLS-1$
		info.append("\n" + sw.toString()); //$NON-NLS-1$

		Runtime rt = Runtime.getRuntime();

		Properties systemProps = System.getProperties();
		Set<Entry<Object, Object>> sets = systemProps.entrySet();

		info.append("\nVennMaker Version:" + VennMaker.VERSION); //$NON-NLS-1$
		if (TestVersion.getEndDate()>0) //$NON-NLS-1$
			info.append(" (trial)"); //$NON-NLS-1$

		info.append("\nVennMaker Revision:" + VennMaker.REVISION); //$NON-NLS-1$
		info.append("\n\n" + "System properties:"); //$NON-NLS-1$ //$NON-NLS-2$

		for (Entry<Object, Object> entry : sets)
		{
			info.append("\n" + entry.getKey() + ", value: " + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
		}

		info.append("\n\nMemory information (in bytes):"); //$NON-NLS-1$
		info.append("\nFree memory: " + rt.freeMemory()); //$NON-NLS-1$
		info.append("\nMax memory: " + rt.maxMemory()); //$NON-NLS-1$
		info.append("\nTotal memory: " + rt.totalMemory()); //$NON-NLS-1$
		info.append("\nMemory used: " + (rt.totalMemory() - rt.freeMemory())); //$NON-NLS-1$
		info.append("\nDisk space: " + new File(".").getUsableSpace()); //$NON-NLS-1$ //$NON-NLS-2$
		info.append("\n"); //$NON-NLS-1$

		System.err.println(info.toString());

		textoutput.setText("The following message was written to debug.log: " //$NON-NLS-1$
				+ info.toString());
		textoutput.setWrapStyleWord(true);
		textoutput.setLineWrap(true);
		textoutput.setEditable(false);

		
		descriptionText = new JTextArea(10,10);
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		descriptionText.setFocusable(true);
		descriptionText.requestFocus();
		
		JScrollPane spane = new JScrollPane(textoutput);

		JLabel errorInfo = new JLabel();
		errorInfo.setText("<html><b>An error occurred:</b></html>");
		
		JLabel textInfo = new JLabel();
		textInfo.setText("<html><b>Please feel free to describe by which action the error occurred:</b><br><i>(If you type in your name and email address, we will contact you)</i></html>");
	
		
		
		JScrollPane scrollPaneText = new JScrollPane(descriptionText); 
		
		GridBagLayout gbl = new GridBagLayout();

		c.setLayout(gbl);

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 10;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(10,10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(errorInfo, gbc);
		c.add(errorInfo);
		
		
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 10;
		gbc.gridheight = 10;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(spane, gbc);
		c.add(spane);
		
		
		gbc.gridx = 0;
		gbc.gridy = 11;
		gbc.gridwidth = 10;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(textInfo, gbc);
		c.add(textInfo);
		
		
		gbc.gridx = 0;
		gbc.gridy = 16;
		gbc.gridwidth = 10;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(scrollPaneText, gbc);
		c.add(scrollPaneText);

		gbc.gridx = 0;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(ok, gbc);
		c.add(ok);

		gbc.gridx = 3;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(save, gbc);
		c.add(save);

		gbc.gridx = 4;
		gbc.gridy = 17;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(end, gbc);
		c.add(end);

		gbc.gridx = 5;
		gbc.gridy = 17;
		gbc.gridwidth = 5;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(send, gbc);
		c.add(send);

		// Write message to file
		File ausgabedatei = new File("./debug.log"); //$NON-NLS-1$
		try
		{
			FileWriter fw = new FileWriter(ausgabedatei);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(info.toString());
			bw.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		f.setSize(new Dimension(800, 500));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - f.getHeight()) / 2;
		int left = (screenSize.width - f.getWidth()) / 2;
		f.setLocation(left, top);
		f.setAlwaysOnTop(true);
		
		VennMaker.getInstance().callUncaughtExceptionListeners();
	
		f.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if ("SEND".equals(ae.getActionCommand())) //$NON-NLS-1$
		{
			if (ErrorSender.sendError(info.toString()+"\r\nUser description:\r\n"+descriptionText.getText()+"\r\n")==true){			
				textoutput.setText("Data successfully sent.");
				}
				else
					textoutput.setText("Error: Data NOT successfully sent!");

		}

		else if ("Close".equals(ae.getActionCommand())) //$NON-NLS-1$
		{

			f.dispose();

		}
		else if ("SAVE".equals(ae.getActionCommand())) //$NON-NLS-1$
		{
			if (IO.saveRoutine() == IO.OPERATION_SUCCEEDED)
			{
				JOptionPane
						.showMessageDialog(
								this.f,
								Messages.getString("ErrorUncaughtExceptionHandler.1"), Messages.getString("ErrorUncaughtExceptionHandler.2"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else if ("END".equals(ae.getActionCommand())) //$NON-NLS-1$
		{
			File tmp = new File(VMPaths.VENNMAKER_TEMPDIR);

			if (tmp.exists())
				FileOperations.deleteFolder(tmp);
			System.exit(0);
		}

	}

}
