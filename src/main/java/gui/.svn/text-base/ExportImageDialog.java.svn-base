/**
 * 
 */
package gui;

import gui.utilities.SelectAllOnFocus;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

/**
 * Dieser Dialog wird beim Export einer Netzwerkkarte als Bild dargestellt ...
 * 
 * 
 */

public class ExportImageDialog extends JDialog
{
	private static final long	serialVersionUID	= 1L;


	private JLabel pixelResoultionWidth = new JLabel();
	private JLabel pixelResoultionHeight = new JLabel();

	private JButton				btnOkay, btnCancel;

	private ExportImageDialog	dia;

	private VennMakerView		view;
	
	private JSlider resolutionSlider;
	
	private int newWidth = 0;
	private int newHeight = 0;


	public ExportImageDialog(VennMakerView view)
	{
		super(VennMaker.getInstance(), true);
		dia = this;
		this.view = view;
		this.setTitle(Messages.getString("ExportImageDialog.1"));
		this.add(buildPanel());

		this.setSize(500, 300);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		this.setVisible(true);
	}

	/**
	 * @return
	 */
	private Component buildPanel()
	{
		JPanel p = new JPanel(new GridLayout(0, 1));
		JPanel p1 = new JPanel(new GridLayout(1, 2));
		JPanel p2 = new JPanel(new GridLayout(1, 2));
		JPanel p3 = new JPanel(new GridLayout(0, 1));
		
		JPanel btnPanel = new JPanel(new GridLayout(0, 2));

		
		//Slider for changing the pixel values
		resolutionSlider = new JSlider(JSlider.HORIZONTAL, -80, 400, 0);
		
		resolutionSlider.setMajorTickSpacing(40);
		resolutionSlider.setPaintTicks(true);
		
		newWidth = (int) view.getViewArea().getWidth();
		newHeight = (int) view.getViewArea().getHeight();

		p1.add(new JLabel(Messages.getString("ExportImageDialog.2")));
		p2.add(new JLabel(Messages.getString("ExportImageDialog.3")));
		
		pixelResoultionWidth.setText(""+newWidth);
		pixelResoultionHeight.setText(""+newHeight);

		p1.add(pixelResoultionWidth);
		p2.add(pixelResoultionHeight);
		
		//label for slider
		Hashtable labelTable = new Hashtable();
		labelTable.put( new Integer( -80 ), new JLabel(Messages.getString("ExportImageDialog.6")) );
		labelTable.put( new Integer( 0 ), new JLabel(Messages.getString("ExportImageDialog.7")) );
		labelTable.put( new Integer( 400 ), new JLabel(Messages.getString("ExportImageDialog.8")) );
		resolutionSlider.setLabelTable( labelTable );
		
		resolutionSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				newWidth = (int) ( ( (resolutionSlider.getValue()+100) * 0.01) * (int) view.getViewArea().getWidth());
				newHeight = (int) ( ( (resolutionSlider.getValue()+100) * 0.01) * (int) view.getViewArea().getHeight());
				
				pixelResoultionWidth.setText(""+newWidth);
				pixelResoultionHeight.setText(""+newHeight);
				}
		});

		resolutionSlider.setPaintLabels(true);
		
		p3.add(resolutionSlider);

		btnOkay = new JButton(Messages.getString("SaveFileDialog.chooseFile"));
		btnCancel = new JButton(Messages.getString("SaveFileDialog.Cancel"));

		btnOkay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{

				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(Messages
						.getString("ExportImageFileChooser.Title"));

				// jpg-Filter
				FileFilter jpgFilter = new FileFilter()
				{
					public boolean accept(File f)
					{
						return f.isDirectory()
								|| f.getName()
										.toLowerCase()
										.endsWith(
												Messages
														.getString("VennMaker.ExportSuffixJpg")); //$NON-NLS-1$
					}

					public String getDescription()
					{
						return Messages
								.getString("VennMaker.ExportSuffixJpgBeschreibung"); //$NON-NLS-1$

					}
				};

				// png-Filter
				FileFilter pngFilter = new FileFilter()
				{
					public boolean accept(File f)
					{
						return f.isDirectory()
								|| f.getName()
										.toLowerCase()
										.endsWith(
												Messages
														.getString("VennMaker.ExportSuffixPng")); //$NON-NLS-1$
					}

					public String getDescription()
					{
						return Messages
								.getString("VennMaker.ExportSuffixPngBeschreibung"); //$NON-NLS-1$

					}
				};

				// svg-Filter
				FileFilter svgFilter = new FileFilter()
				{
					public boolean accept(File f)
					{
						return f.isDirectory()
								|| f.getName()
										.toLowerCase()
										.endsWith(
												Messages
														.getString("VennMaker.ExportSuffixSvg")); //$NON-NLS-1$
					}

					public String getDescription()
					{
						return Messages
								.getString("VennMaker.ExportSuffixSvgBeschreibung"); //$NON-NLS-1$
					}
				};

				chooser.addChoosableFileFilter(svgFilter);
				chooser.addChoosableFileFilter(jpgFilter);
				chooser.addChoosableFileFilter(pngFilter);
				
				chooser.setFileFilter(chooser.getChoosableFileFilters()[2]);

				if (chooser.showSaveDialog(VennMaker.getInstance()) == JFileChooser.APPROVE_OPTION)
				{
					File file = chooser.getSelectedFile();

					String filename = file.getAbsolutePath();
					String exportSuffix = Messages.getString("VennMaker.PNG"); //$NON-NLS-1$

					if (file.getAbsolutePath().endsWith(
							Messages.getString("VennMaker.ExportSuffixPng"))); //$NON-NLS-1$
					else if (file.getAbsolutePath().endsWith(
							Messages.getString("VennMaker.ExportSuffixJpg"))) //$NON-NLS-1$
						exportSuffix = Messages.getString("VennMaker.JPG"); //$NON-NLS-1$
					else if (file.getAbsolutePath().endsWith(
							Messages.getString("VennMaker.ExportSuffixSvg")))
						exportSuffix = Messages.getString("VennMaker.SVG");
					else
					{
						if (chooser.getFileFilter() == jpgFilter)
							exportSuffix = Messages.getString("VennMaker.JPG"); //$NON-NLS-1$
						if (chooser.getFileFilter() == svgFilter)
							exportSuffix = Messages.getString("VennMaker.SVG"); //$NON-NLS-1$
						filename = file.getAbsolutePath() + "." //$NON-NLS-1$
								+ exportSuffix.toLowerCase();

						file = new File(filename);
					}

					// Nachfrage wenn die Datei schon existiert
					if (file.exists())
					{
						if (JOptionPane.showConfirmDialog(
								VennMaker.getInstance(),
								Messages.getString("VennMaker.ConfirmOverwrite") + file.getName() + Messages.getString("VennMaker.ConfirmOverwriteEnd"), //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("VennMaker.ConfirmOverwriteTitel"), JOptionPane.YES_NO_OPTION) //$NON-NLS-1$
						!= JOptionPane.OK_OPTION)
						{
							return;
						}
					}
					try
					{
						view.screenshot(filename, exportSuffix, newWidth, newHeight);
					} catch (IOException exn)
					{
						JOptionPane.showMessageDialog(VennMaker.getInstance(),
								Messages.getString("VennMaker.Error_Occured")//$NON-NLS-1$
										+ exn.getMessage(),
								Messages.getString("VennMaker.Error"),//$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
					} catch (NegativeArraySizeException exn)
					{ // dimension exceeds integer values
						JOptionPane.showMessageDialog(
								VennMaker.getInstance(),
								Messages.getString("VennMaker.Error_Occured")//$NON-NLS-1$
										+ Messages
												.getString("ExportImage.NegativeDimension"),
								Messages.getString("VennMaker.Error"),//$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
						return; // let dialog open to try again
					}

				}
				dia.dispose();
			}
		});

		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dia.dispose();
			}
		});

		btnPanel.add(btnCancel);
		btnPanel.add(btnOkay);

		p.add(new JLabel(Messages.getString("ExportImageDialog.5")) );
		p.add(p1);
		p.add(p2);
		p.add(p3);
		p.add(new JPanel());

		p.add(btnPanel);

		return p;
	}
	
	
}




class ScaleKeyListener extends KeyAdapter
{
	private double			scaleFactor;

	private JTextField	tfToScale;

	private JCheckBox		cbShouldDo;

	/**
	 * Regular expression which defines the allowed characters.
	 */
	private String			allowedRegex	= "[^0-9]";

	public ScaleKeyListener(JCheckBox cbShouldDo, double scaleFactor,
			JTextField tfToScale)
	{
		this.cbShouldDo = cbShouldDo;
		this.scaleFactor = scaleFactor;
		this.tfToScale = tfToScale;
	}

	public void keyReleased(KeyEvent arg0)
	{
		try
		{
			JTextField tf = (JTextField) arg0.getSource();

			String curText = tf.getText();
			String oldText = curText;
			int cursorPos = tf.getCaretPosition();
			curText = curText.replaceAll(allowedRegex, "");

			/* if text had to be changed - update the shown text */
			if (!oldText.equals(curText))
			{
				tf.setText(curText);
				if (cursorPos < curText.length())
				{
					/* position the caret at the last position */
					tf.setCaretPosition(cursorPos - 1);
				}
			}

			if (!(tf.getText().equals("") || tf.getText().isEmpty()))
			{
				int i = Integer.parseInt(curText);
				int maximumValue = 12000;
				i = Math.max(1, Math.min(i, maximumValue));
				/* check if either side might be larger than 12000 pixel */
				if ((i * scaleFactor) >= 12000)
					maximumValue = (int) (12000 / scaleFactor);
				i = Math.max(10, Math.min(i, maximumValue));

				/* only set new text, if it changed */
				if (!curText.equals("" + i))
				{
					tf.setText("" + (int) i);
				}
				if (cbShouldDo.isSelected())
				{
					tfToScale.setText("" + (int) (i * scaleFactor));
				}
			}
			else
			{ // no entry guard
				if (cbShouldDo.isSelected())
				{
					tfToScale
							.setText(tfToScale.getName().equals("tfHeight") ? Messages
									.getString("ExportImage.InvalidWidth") : Messages
									.getString("ExportImage.InvalidHeight"));
					return;
				}
			}
		} catch (NumberFormatException e)
		{
		}
	}
}
