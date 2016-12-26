/**
 * 
 */
package files;

import gui.BufferedImageTranscoder;
import gui.Messages;
import gui.VennMakerView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * Enthaellt ein paar statische Methoden fuer die Bearbeitung von Images
 * 
 * 
 */
public class ImageOperations
{
	/**
	 * Load an image file
	 * 
	 * @param filename
	 *           : svg, png or jpg format
	 * @param groesse
	 * @param imageScalingFactor
	 *           (Default: 1.0)
	 * @return image
	 */
	public static BufferedImage loadActorImage(String filename, double groesse,
			double imageScalingFactor)
	{
		if (filename == null)
			return new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
		String name = filename.toLowerCase();
		BufferedImage actorsImage = null;
		double size = Math.round(groesse * imageScalingFactor);

		if (name.endsWith(".svg"))
		{
			BufferedImageTranscoder t = new BufferedImageTranscoder();
			t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) size);

			try
			{
				InputStream is = new FileInputStream(filename);
				TranscoderInput ti = new TranscoderInput(is);
				t.transcode(ti, null);
				actorsImage = t.getImage();

				return actorsImage;
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} catch (TranscoderException e)
			{
				e.printStackTrace();
			}
		}
		else if (name.endsWith(".jpg") || name.endsWith(".png"))
		{
			try
			{
				BufferedImage actorsImageFull = ImageIO.read(new File(filename));
				Image img = actorsImageFull.getScaledInstance((int) groesse,
						(int) groesse, BufferedImage.SCALE_SMOOTH);
				actorsImage = new BufferedImage((int) groesse, (int) groesse,
						BufferedImage.TYPE_INT_RGB);
				actorsImage.getGraphics().drawImage(img, 0, 0, null);

				return actorsImage;
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Image createActorIcon(BufferedImage actorsImage,
			double groesse, double imageScalingFactor)
	{
		if (actorsImage == null)
		{
			// Default - Bild
			actorsImage = new BufferedImage((int) groesse, (int) groesse,
					BufferedImage.TYPE_4BYTE_ABGR);

			Graphics2D gi = actorsImage.createGraphics();
			gi.setColor(Color.gray);
			gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			gi.setStroke(new BasicStroke(3.0f));
			gi.draw(new Ellipse2D.Double(2, 2, groesse - 4, groesse - 4));
			gi.drawLine((int) (groesse / 2), 2, (int) (groesse / 2),
					(int) groesse - 4);
			gi.drawLine(2, (int) (groesse / 2), (int) groesse - 4,
					(int) (groesse / 2));
		}
		Image actorsIcon = actorsImage.getScaledInstance(
				(int) (int) Math.round(groesse * imageScalingFactor),
				(int) Math.round(groesse * imageScalingFactor),
				BufferedImage.SCALE_SMOOTH);

		return actorsIcon;
	}

	/**
	 * Change PNG Image which have ImageType = TYPE_CUSTOM to ImageType =
	 * TYPE_INT_ARGB
	 * 
	 * @param src
	 *           (BufferedImage containing PNG)
	 * @param bgColor
	 *           (Background Color)
	 * @return BufferedImage with ImageType = TYPE_INT_ARGB
	 */
	public static BufferedImage pngToARGB(BufferedImage src, Color bgColor)
	{
		if (bgColor == null)
			bgColor = Color.black;

		BufferedImage dummyImg = new BufferedImage(src.getWidth(),
				src.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = (Graphics2D) dummyImg.createGraphics();
		g2d.setBackground(bgColor);
		g2d.drawImage(src, 0, 0, null);
		return dummyImg;
	}

	/**
	 * Scale Selection of an Image (Rectangle) to another size
	 * 
	 * @param selection
	 *           (Rectangle to get another Scale of)
	 * @param width
	 * @param height
	 * @param originalWidth
	 * @param originalHeight
	 * @return rescaled image
	 */
	public static Rectangle getScaledSelection(Rectangle selection, int width,
			int height, int originalWidth, int originalHeight)
	{
		Rectangle r = new Rectangle();
		double scaleX = (double) width / (double) originalWidth;
		double scaleY = (double) height / (double) originalHeight;
		r.x = (int) (Math.round((double) scaleX * selection.x));
		r.y = (int) (Math.round((double) scaleY * selection.y));
		r.width = (int) (Math.round((double) scaleX * selection.width));
		r.height = (int) (Math.round((double) scaleY * selection.height));

		// Überprüfung ob width oder height (durchs Runden nicht groesser
		// originalWidth oder originalHeight wurden
		if ((r.x + r.width) > originalWidth)
			r.width--;
		if ((r.y + r.height) > originalHeight)
			r.height--;
		return r;
	}

	/**
	 * 
	 * @param imgSelection
	 *           - Rectangle with the upscaled Image Selection
	 * @param filename
	 *           - filename of the Original Image
	 */
	public static void saveNewImgSelection(Rectangle imgSelection,
			String filename, VennMakerView view)
	{
		File file = new File(filename);
		String selectionFilename = filename + "Selection";
		if (file != null)
		{
			try
			{
				File selectionFile = new File(selectionFilename);
				if (selectionFile.exists())
					selectionFile.delete();
				BufferedImage original = ImageIO.read(file);
				BufferedImage selection = original.getSubimage(imgSelection.x,
						imgSelection.y, imgSelection.width, imgSelection.height);
				ImageIO.write(selection, "PNG", selectionFile);
				view.getNetzwerk().getHintergrund()
						.setFilenameOfSelection(selectionFilename);
				view.setBackgroundImage(selection);
			} catch (IOException exn)
			{
				JOptionPane.showMessageDialog(
						null,
						Messages
								.getString("ChoosePartOfImageDialog.cantSaveImagePart")
								+ "\n\n" + exn.getMessage(), Messages
								.getString("VennMaker.Error"),
						JOptionPane.ERROR_MESSAGE, null);
			}
		}
	}

	public static boolean copyScaledImage(File srcFile, File destFile,
			int destWidth, int destHeight)
	{
		try
		{
			String name = srcFile.getName();
			BufferedImage bi = ImageIO.read(srcFile);
			BufferedImage newBi = new BufferedImage(200, 200,
					BufferedImage.TYPE_INT_RGB);
			Image image = bi.getScaledInstance(200, 200,
					BufferedImage.SCALE_SMOOTH);
			newBi.createGraphics().drawImage(image, 0, 0, null);
			int pos = name.lastIndexOf(".");
			ImageIO.write(newBi, name.substring(pos + 1, name.length()), destFile);
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * proxymethod to load image files - if not found, returns a standardimage
	 * 
	 * @return
	 */
	public static BufferedImage loadImage(File imageFile)
	{
		BufferedImage image = null;

		try
		{
			if (imageFile != null)
				image = ImageIO.read(imageFile);

			return image;
		} catch (IOException exn)
		{
			/* margin to define a border around the text */
			final int margin = 4;

			image = new BufferedImage(100, 15, BufferedImage.TYPE_INT_ARGB);

			/* calculate of String, to define Imagewidth */
			Graphics2D g2 = image.createGraphics();
			final FontMetrics fm = g2.getFontMetrics();
			int imageWidth = SwingUtilities.computeStringWidth(fm,
					Messages.getString("ImageOperations.FileNotFound"));


			image = new BufferedImage(imageWidth + (margin * 2) + 1,
					10 + (margin * 2) + 1, BufferedImage.TYPE_INT_ARGB);
			g2 = image.createGraphics();

			g2.setColor(Color.RED);
			g2.drawRect(0, 0, imageWidth + (margin * 2), 10 + (margin * 2));
			g2.drawLine(0, 0, imageWidth + (margin * 2), 10 + (margin * 2));
			g2.drawLine(0, 10 + (margin * 2), imageWidth + (margin * 2), 0);
			g2.setColor(Color.black);
			g2.drawString(Messages.getString("ImageOperations.FileNotFound"),
					margin, 10 + margin);
			return image;
		}

	}

	/**
	 * same as loadImage(File), but just as the path as parameter
	 * 
	 * @param imageFile
	 *           the path to the imagefile to load
	 * @return
	 */
	public static BufferedImage loadImage(String imageFile)
	{
		return loadImage(new File(imageFile));
	}
}
