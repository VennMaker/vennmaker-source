package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import files.FileOperations;

public class AboutTeam extends JDialog
{

	public AboutTeam()
	{
		setModal(true);
		setTitle(Messages.getString("VennMaker.AboutTitel")); //$NON-NLS-1$

		this.setResizable(false);

		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		GridBagConstraints constraints = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;

		constraints.insets = new Insets(10, 10, 10, 1);

		BufferedImage image = null;
		BufferedImageTranscoder t = new BufferedImageTranscoder();
		t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, 80f);

		InputStream is;
		try
		{
			is = new FileInputStream(
					FileOperations.getAbsolutePath("icons/intern/VennMaker.svg"));

			TranscoderInput ti = new TranscoderInput(is);
			try
			{
				t.transcode(ti, null);

				image = t.getImage();

				ImageIcon imgIcon = new ImageIcon(image);
				JLabel logo = new JLabel(imgIcon);

				gbl.setConstraints(logo, constraints);
				this.add(logo, constraints);

			} catch (TranscoderException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //$NON-NLS-1$

		String team = (TestVersion.getEndDate()>0 ? "Trial version" //$NON-NLS-1$ //$NON-NLS-2$
				: "Version") //$NON-NLS-1$
				+ " " //$NON-NLS-1$
				+ VennMaker.VERSION
				+ ", more Information: www.vennmaker.com" //$NON-NLS-1$
				+ "<br><br> &copy; 2007 - 2018 M. Sch\u00f6nhuth, M. Gamper, M. Stark, M. Kronenwett" //$NON-NLS-1$
				+ "<br><br> Team:" //$NON-NLS-1$
				+ " M. Sch\u00f6nhuth, M. Gamper, M. Kronenwett, M. Stark. "; //$NON-NLS-1$

		JLabel infotext = new JLabel("<html><body>" + team + "</body></html>"); //$NON-NLS-1$ //$NON-NLS-2$

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(10, 10, 10, 1);
		gbl.setConstraints(infotext, constraints);
		this.add(infotext, constraints);

		this.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - this.getHeight()) / 2;
		int left = (screenSize.width - this.getWidth()) / 2;
		this.setLocation(left, top);

		setVisible(true);

	}
}
