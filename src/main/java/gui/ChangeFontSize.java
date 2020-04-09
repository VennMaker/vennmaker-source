package gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import files.FileOperations;
import gui.utilities.VennMakerUIConfig;

public class ChangeFontSize extends JDialog implements ChangeListener
{
	private float size = 0.0f;
	private JLabel infotext;
	private JTextArea infotext2;

	public ChangeFontSize()
	{
		setModal(true);
		
		size = VennMakerUIConfig.getFontSize();
		
		VennMakerUIConfig.setFontSize(size);
		
		
		setTitle(Messages.getString("VennMaker.ChangeFontSizeTitel")); //$NON-NLS-1$

		this.setResizable(false);

		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		GridBagConstraints constraints = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);

		infotext = new JLabel("Global font size: " + size ); //$NON-NLS-1$ //$NON-NLS-2$
		infotext.setFont(infotext.getFont().deriveFont(VennMakerUIConfig.getFontSize()));
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(10, 10, 10, 1);
		gbl.setConstraints(infotext, constraints);
		this.add(infotext, constraints);
		
		JSlider fontSizeSlider = new JSlider(JSlider.HORIZONTAL,
                12, 50, (int) size);
		fontSizeSlider.addChangeListener(this);
		fontSizeSlider.setPaintTicks(true);
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(10, 10, 10, 1);
		gbl.setConstraints(fontSizeSlider, constraints);
		this.add(fontSizeSlider, constraints);
		
		infotext2 = new JTextArea("Please restart VennMaker if you have changed the size." ); //$NON-NLS-1$ //$NON-NLS-2$
		infotext2.setEditable(false);
		infotext2.setFont(infotext.getFont().deriveFont(VennMakerUIConfig.getFontSize()));
		
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.insets = new Insets(10, 10, 10, 1);
		gbl.setConstraints(infotext2, constraints);
		this.add(infotext2, constraints);
		
		this.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - this.getHeight()) / 2;
		int left = (screenSize.width - this.getWidth()) / 2;
		this.setLocation(left, top);
		this.setLocation(0, 0);


		this.setSize(screenSize.width, screenSize.height);
		setVisible(true);

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		        JSlider source = (JSlider)e.getSource();
		        if (!source.getValueIsAdjusting()) {
		            size = (int)source.getValue();
		            VennMakerUIConfig.setFontSize(size);
		            System.out.println("Size: "+size);
		            
		    		infotext.setText("Global font size: " + size ); //$NON-NLS-1$ //$NON-NLS-2$
		    		infotext.setFont(infotext.getFont().deriveFont(VennMakerUIConfig.getFontSize()));
		    		infotext2.setFont(infotext.getFont().deriveFont(VennMakerUIConfig.getFontSize()));

		    		
		        }   
		
	}
}
