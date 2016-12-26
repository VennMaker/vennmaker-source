/**
 * 
 */
package gui.configdialog.elements;

import files.ImageOperations;
import files.VMPaths;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.individual.ChooseImagePartDialog;
import gui.configdialog.save.EditBackgroundSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingBackgroundImage;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import net.iharder.Base64;
import data.BackgroundInfo;
import data.BackgroundInfo.BackgroundImageOptions;

/**
 * Dialog fuer die Auswahl und Manipulation eines Bilds als Hintergrund fuer ein
 * Netzwerk.
 * 
 * 
 */
public class CDialogBackgroundImage extends ConfigDialogElement
{
	private static final long	serialVersionUID	= 1L;

	/**
	 * Convenience. Stores the last opened directory or <code>null</code>.
	 */
	private static String		curbgdir				= null;

	/**
	 * The currently selected color (or the initial color of the view).
	 */
	private Color					bgColor				= null;

	/**
	 * The current model for the transparency spinner
	 */
	private SpinnerNumberModel	colorModel;

	private Rectangle				upscaledImgSelection;

	/**
	 * filename UI entry field
	 */

	private JLabel					filenameField;

	/**
	 * UI for background select
	 */
	private JCheckBox				usebackgroundbox;

	/**
	 * UI for image type selection
	 */
	private JComboBox				imgTypeBox;

	/**
	 * Background Image preview
	 */
	private JLabel					imagePreview;

	private String					imgFileName;

	/**
	 * Checkbox to choose, if all comments are to be shown
	 */
	Checkbox							showCommentsBox;

	boolean							forBackgroundImage;

	boolean							imgSelected			= false;

	private JLabel					bgColorSample;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		dialogPanel.add(createBackgroundImageBox());
	}

	private Box createBackgroundImageBox()
	{
		final Box vbox = Box.createVerticalBox();

		Box hbox3 = Box.createHorizontalBox();

		hbox3.add(Box.createVerticalStrut(50));

		imgTypeBox = new JComboBox(BackgroundInfo.BackgroundImageOptions.values());

		// Reused: The rows of the box
		Box hbox = Box.createHorizontalBox();

		// "Background color" section
		final JPanel bgColorPanel = new JPanel();
		bgColorPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("BackgroundConfig.BackgroundColor"))); //$NON-NLS-1$

		if (this.bgColor == null)
			this.bgColor = net.getHintergrund().getBgcolor();

		final Image i = createFilledImage(this.bgColor);
		bgColorSample = new JLabel(new ImageIcon(i));
		final JButton bgColorChooser = new JButton(
				Messages.getString("BackgroundConfig.Select")); //$NON-NLS-1$

		this.colorModel = new SpinnerNumberModel(net.getHintergrund()
				.getTransparency(), 0, 100, 10);

		hbox.add(bgColorChooser);

		hbox.add(Box.createHorizontalStrut(50));
		hbox.add(bgColorSample);
		hbox.add(Box.createHorizontalStrut(20));
		final JLabel label = new JLabel(
				Messages.getString("BackgroundConfig.String_4")); //$NON-NLS-1$
		hbox.add(label);
		final JSpinner spinner = new JSpinner(colorModel);
		hbox.add(spinner);
		final JLabel label2 = new JLabel(
				Messages.getString("BackgroundConfig.String_8")); //$NON-NLS-1$
		hbox.add(label2);
		bgColorPanel.add(hbox);

		vbox.add(bgColorPanel);

		bgColorChooser.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				bgColor = JColorChooser.showDialog(dialogPanel,
						Messages.getString("BackgroundConfig.SelectBackgroundColor"), //$NON-NLS-1$
						bgColor);
				bgColorSample.setIcon(new ImageIcon(createFilledImage(bgColor)));
			}
		});

		// "Use image" box
		final JPanel backgroundImgPanel = new JPanel();
		backgroundImgPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("BackgroundConfig.BackgroundImage"))); //$NON-NLS-1$
		Box vbox2 = Box.createVerticalBox();

		boolean boxSelected = false;

		if (usebackgroundbox != null)
			boxSelected = usebackgroundbox.isSelected();

		usebackgroundbox = new JCheckBox(
				Messages.getString("BackgroundConfig.UseBackgroundImage")); //$NON-NLS-1$
		usebackgroundbox.setPreferredSize(new Dimension(200, 25));
		usebackgroundbox.setAlignmentX(1.0f);
		usebackgroundbox.setSelected(boxSelected);

		vbox2.add(usebackgroundbox);
		vbox2.add(Box.createVerticalStrut(20));
		vbox2.add(new JSeparator());
		vbox2.add(Box.createVerticalStrut(20));

		// "Select filename" box
		hbox = Box.createHorizontalBox();
		final JLabel filenameLabel = new JLabel(
				Messages.getString("BackgroundConfig.FilenameColon")); //$NON-NLS-1$

		String backgroundFileName = net.getHintergrund()
				.getFilenameOfOriginalImage();

		if (imgFileName == null && backgroundFileName != null)
			imgFileName = backgroundFileName;

		if (imgFileName != null && backgroundFileName != null)
		{
			int separatorIndex = backgroundFileName.lastIndexOf(File.separator);

			filenameField = new JLabel(backgroundFileName.substring(
					separatorIndex + 1, backgroundFileName.length()));
		}
		else
		{
			filenameField = new JLabel();
		}

		filenameField.setMaximumSize(new Dimension(195, 25));
		filenameField.setPreferredSize(filenameField.getSize());
		filenameField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		filenameField.setOpaque(true);
		filenameField.setBackground(Color.WHITE);
		final JButton filenameChooserButton = new JButton(
				Messages.getString("BackgroundConfig.SelectDotDotDot")); //$NON-NLS-1$

		hbox.add(filenameLabel);
		hbox.add(filenameField);
		hbox.add(filenameChooserButton);
		vbox2.add(hbox);

		// Preview icon
		BufferedImage img = null;
		if (net.getHintergrund().getFilename() == null)
		{
			img = createNullPreview();
		}
		else
		{
			try
			{
				BufferedImage newImg = ImageIO.read(new File(net.getHintergrund()
						.getFilename()));

				// wenn PNG dann Umwandeln, sonst kommt es zu
				// Problemen weil der Image Type (TYPE_CUSTOM)
				// beim Transformieren und Scalieren nicht funktioniert
				if (newImg.getType() == BufferedImage.TYPE_CUSTOM)
				{
					newImg = ImageOperations.pngToARGB(newImg, bgColor);
				}

				final int curW = newImg.getWidth();
				final int curH = newImg.getHeight();
				final AffineTransformOp ato = new AffineTransformOp(
						AffineTransform.getScaleInstance(220.0 / curW, 220.0 / curH),
						AffineTransformOp.TYPE_BICUBIC);
				img = new BufferedImage(220, 220, newImg.getType());
				ato.filter(newImg, img);
			} catch (Exception e)
			{
				JOptionPane.showMessageDialog(this,
						Messages.getString("BackgroundConfig.String_9"), Messages //$NON-NLS-1$
								.getString("BackgroundConfig.String_10"), //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);
				img = createNullPreview();
			}
		}

		final ImageIcon imgIcon = new ImageIcon(img);
		imagePreview = new JLabel(imgIcon);
		imagePreview.setAlignmentX(0.5f);
		vbox2.add(imagePreview);

		// "Fit to screen" combo box
		hbox = Box.createHorizontalBox();
		final JLabel imgTypeLabel = new JLabel(
				Messages.getString("BackgroundConfig.String_11")); //$NON-NLS-1$
		imgTypeBox.setSelectedItem(net.getHintergrund().getImgoption());
		hbox.add(imgTypeLabel);
		hbox.add(imgTypeBox);
		vbox2.add(hbox);

		// Button um Teil des Bildes auszusuchen
		final CDialogBackgroundImage dia = this;
		final JButton choosePartButton = new JButton(
				Messages.getString("BackgroundConfig.Button_7"));

		for (VennMakerView v : VennMaker.getInstance().getViews())
		{
			if (v.getNetzwerk().equals(net))
			{
				final VennMakerView view = v;
				choosePartButton.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						if (imgFileName != null)
							new ChooseImagePartDialog(view, imgFileName, dia);
					}
				});
				break;
			}
		}

		vbox2.add(Box.createVerticalStrut(20));
		vbox2.add(choosePartButton);

		// include img panel in dialog
		backgroundImgPanel.add(vbox2);
		vbox.add(backgroundImgPanel);

		// "Dialog elements" section
		// "Usebackground" enables controls!
		usebackgroundbox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final boolean selected = usebackgroundbox.isSelected();
				label.setEnabled(selected);
				spinner.setEnabled(selected);
				label2.setEnabled(selected);
				filenameLabel.setEnabled(selected);
				filenameField.setEnabled(selected);
				filenameChooserButton.setEnabled(selected);
				imagePreview.setEnabled(selected);

				if (selected == false)
					imgSelected = false;

				if ((selected == true) && (filenameField.getText().length() > 0))
					imgSelected = true;

				imgTypeLabel.setEnabled(imgSelected);
				imgTypeBox.setEnabled(imgSelected);
				choosePartButton.setEnabled(imgSelected);
				net.getHintergrund().setFilename(null);
				net.getHintergrund().setFilenameOfSelection(null);
			}
		});

		// Initial values for enabled controls...
		final boolean selected = net.getHintergrund().getFilename() != null;
		label.setEnabled(selected);
		spinner.setEnabled(selected);
		label2.setEnabled(selected);
		usebackgroundbox.setSelected(usebackgroundbox.isSelected() ? true
				: selected);
		filenameLabel.setEnabled(selected);
		filenameField.setEnabled(selected);
		filenameChooserButton.setEnabled(selected);
		imagePreview.setEnabled(selected);

		if ((selected == true) && (filenameField.getText().length() > 0))
			imgSelected = true;

		imgTypeLabel.setEnabled(imgSelected);
		imgTypeBox.setEnabled(imgSelected);
		choosePartButton.setEnabled(imgSelected);

		// Create file chooser for select button
		filenameChooserButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final JFileChooser jfc = new JFileChooser(curbgdir);
				jfc.setFileFilter(new FileFilter()
				{
					@Override
					public boolean accept(final File f)
					{

						final String lowerCase = f.getName().toLowerCase();
						return f.isDirectory()
								|| lowerCase.endsWith(Messages
										.getString("BackgroundConfig.String_JPG")) //$NON-NLS-1$
								|| lowerCase.endsWith(Messages
										.getString("BackgroundConfig.String_GIF")) //$NON-NLS-1$
								|| lowerCase.endsWith(Messages
										.getString("BackgroundConfig.String_PNG")); //$NON-NLS-1$
					}

					@Override
					public String getDescription()
					{
						return Messages
								.getString("BackgroundConfig.ImageBrPNGComJPGComGIFBr"); //$NON-NLS-1$
					}
				});
				final int status = jfc.showOpenDialog(dialogPanel);
				if (status == JFileChooser.APPROVE_OPTION)
				{
					imgFileName = jfc.getSelectedFile().getPath();

					filenameField.setText(jfc.getSelectedFile().getName());

					BufferedImage smallImg = null;

					try
					{
						File parent = jfc.getSelectedFile().getParentFile();
						if (parent != null)
						{
							curbgdir = parent.getAbsolutePath();
						}

						BufferedImage newImg = ImageIO.read(jfc.getSelectedFile());

						// wenn PNG dann Umwandeln, sonst kommt es zu
						// Problemen weil der Image Type (TYPE_CUSTOM)
						// beim Transformieren und Scalieren nicht funktioniert
						if (newImg.getType() == BufferedImage.TYPE_CUSTOM)
						{
							newImg = ImageOperations.pngToARGB(newImg, bgColor);
						}

						final int curW = newImg.getWidth();
						final int curH = newImg.getHeight();
						final AffineTransformOp ato = new AffineTransformOp(
								AffineTransform.getScaleInstance(220.0 / curW,
										220.0 / curH), AffineTransformOp.TYPE_BICUBIC);
						smallImg = new BufferedImage(220, 220, newImg.getType());
						ato.filter(newImg, smallImg);

						imgSelected = true;
						imgTypeLabel.setEnabled(imgSelected);
						imgTypeBox.setEnabled(imgSelected);
						choosePartButton.setEnabled(imgSelected);

					}
					// Fehler beim Lesen
					catch (final IOException exn)
					{
						JOptionPane.showMessageDialog(
								null,
								Messages
										.getString("BackgroundConfig.ErrorReadingImgDotFile")
										+ "\n\n" + exn.getMessage(), Messages
										.getString("VennMaker.Error"),
								JOptionPane.ERROR_MESSAGE, null);
					}
					/**
					 * Falls Bilddatei zu groß: UM DEM AUS DEM WEG ZU GEHEN sollte
					 * mit Java VM Argument "-XmxSPEICHERGROESSE" der Virtuellen
					 * Machine von Java mehr Speicher als die default 128m zugewiesen
					 * werden Bei heutigen Rechner sollte "-Xmx512" okay sein
					 */

					catch (OutOfMemoryError err)
					{
						JOptionPane.showMessageDialog(null,
								Messages.getString("BackgroundConfig.ErrorImgToBig")
										+ "\n\n" + err.getMessage(),
								Messages.getString("VennMaker.Error"),
								JOptionPane.ERROR_MESSAGE, null);
					}

					if (smallImg != null)
					{
						// Selection auf "null" setzen, sonst besteht die
						// alte Selection noch
						net.getHintergrund().setUpscaledImgSelection(null);
						net.getHintergrund().setFilenameOfSelection(null);

						imagePreview.setIcon(new ImageIcon(smallImg));
					}
				}
			}
		});
		return vbox;
	}

	/**
	 * Erzeugt ein Vorschaubild, wenn kein Bild ausgewählt ist.
	 * 
	 * @return
	 */
	private BufferedImage createNullPreview()
	{
		BufferedImage img;
		img = new BufferedImage(220, 220,

		BufferedImage.TYPE_4BYTE_ABGR);

		final Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.black);
		g.drawString(
				Messages.getString("BackgroundConfig.BrNoPreviewAvailableBr"), 50, 150); //$NON-NLS-1$
		return img;
	}

	/**
	 * Erzeugt ein Farbvorschaubild.
	 * 
	 * @param c
	 */
	private Image createFilledImage(final Color c)
	{
		final Image img = new BufferedImage(80, 20, BufferedImage.TYPE_4BYTE_ABGR);
		final Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(c);
		g.fillRect(0, 0, 79, 19);
		g.setColor(Color.black);
		g.drawRect(0, 0, 79, 19);
		return img;
	}

	/**
	 * @param upscaledSelection
	 *           - containing the upscaled Selection of the original Image
	 */
	public void setImgSelection(Rectangle upscaledSelection)
	{
		this.upscaledImgSelection = upscaledSelection;
		this.updateImagePreview();
	}

	/**
	 * Needed to update the Image Preview when a Part of an Image is choosen
	 */
	public void updateImagePreview()
	{
		BufferedImage smallImg = null;
		try
		{
			File imageFile = new File(imgFileName);
			if (!imageFile.exists())
				return;
			BufferedImage newImg = ImageIO.read(imageFile);

			// wenn PNG dann Umwandeln, sonst kommt es zu
			// Problemen weil der Image Type (TYPE_CUSTOM)
			// beim Transformieren und Scalieren nicht funktioniert
			if (newImg.getType() == BufferedImage.TYPE_CUSTOM)
			{
				newImg = ImageOperations.pngToARGB(newImg, bgColor);
			}
			if (upscaledImgSelection != null)
				newImg = newImg.getSubimage(upscaledImgSelection.x,
						upscaledImgSelection.y, upscaledImgSelection.width,
						upscaledImgSelection.height);

			final int curW = newImg.getWidth();
			final int curH = newImg.getHeight();
			final AffineTransformOp ato = new AffineTransformOp(
					AffineTransform.getScaleInstance(220.0 / curW, 220.0 / curH),
					AffineTransformOp.TYPE_BICUBIC);
			smallImg = new BufferedImage(220, 220, newImg.getType());
			ato.filter(newImg, smallImg);
		} catch (final IOException exn)
		{
			exn.printStackTrace();
		}
		if (smallImg != null)
		{
			imagePreview.setIcon(new ImageIcon(smallImg));
			imagePreview.repaint();
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		SettingBackgroundImage s = null;

		if (upscaledImgSelection == null)
			upscaledImgSelection = net.getHintergrund().getUpscaledImgSelection();

		if (!(usebackgroundbox.isSelected() && imgFileName == null))
			s = new SettingBackgroundImage(net, usebackgroundbox.isSelected(),
					(BackgroundImageOptions) imgTypeBox.getSelectedItem(), bgColor,
					(Integer) colorModel.getValue(), imgFileName,
					upscaledImgSelection);

		return s;
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof EditBackgroundSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		EditBackgroundSaveElement elem = (EditBackgroundSaveElement) setting;

		bgColor = elem.getBackgroundColor();
		colorModel.setValue(elem.getTransparency());
		imgFileName = elem.getImageName();
		bgColorSample.setIcon(new ImageIcon(createFilledImage(bgColor)));

		if (imgFileName == null)
			return;

		try
		{
			Base64.decodeToFile(elem.getImageData(), VMPaths.VENNMAKER_TEMPDIR
					+ imgFileName);
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		upscaledImgSelection = elem.getImageSelection();
		net.getHintergrund().setUpscaledImgSelection(upscaledImgSelection);

		filenameField.setText(imgFileName);
		imgFileName = VMPaths.VENNMAKER_TEMPDIR + imgFileName;
		usebackgroundbox.doClick();
		updateImagePreview();

	}

	@Override
	public SaveElement getSaveElement()
	{
		EditBackgroundSaveElement elem = null;

		if (usebackgroundbox.isSelected() && imgFileName != null)
		{
			try
			{
				String imageData = Base64.encodeFromFile(imgFileName);
				File imgFile = new File(imgFileName);
				if (upscaledImgSelection == null)
					upscaledImgSelection = net.getHintergrund()
							.getUpscaledImgSelection();
				elem = new EditBackgroundSaveElement(bgColor,
						(Integer) colorModel.getValue(), imageData,
						imgFile.getName(), upscaledImgSelection);
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		else
		{
			elem = new EditBackgroundSaveElement(bgColor,
					(Integer) colorModel.getValue());
		}

		if (elem != null)
			elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}
}
