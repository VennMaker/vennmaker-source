package gui.configdialog.individual;

import files.ImageOperations;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.elements.CDialogBackgroundImage;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Dialog um einen Teil eines Bilds auszuwaehlen. </br> (beinhaltet ein
 * ChoosePartOfImagePanel)
 * 
 * 
 * 
 */
public class ChooseImagePartDialog extends JDialog
{
	private static final long			serialVersionUID	= 1L;

	// Panel auf dem gezeichnet wird
	private ChoosePartOfImagePanel	prevPanel;

	public ChooseImagePartDialog(final VennMakerView view, String imgFilename,
			final CDialogBackgroundImage caller)
	{
		super(VennMaker.getInstance(), Messages
				.getString("ChooseImagePartDialog.Title"), true);

		if (imgFilename == null)
			dispose();

		BufferedImage img;
		int panelWidth, panelHeight;
		double faktor;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		try
		{
			if (imgFilename == null)
				return;
			File f = new File(imgFilename);
			if (!f.exists())
				return;
			img = ImageIO.read(f);

			/**
			 * Panelgroesse anhand der Bildschirmaufloesung berechnen
			 */
			faktor = Math.min(
					screenSize.getHeight() * 0.8 / (double) img.getHeight(),
					screenSize.getWidth() * 0.8 / (double) img.getWidth());
			// falls Bild komplett auf den Bildschirm passt
			if (faktor > 1.0)
				faktor = 1.0;
			panelWidth = (int) (faktor * (double) img.getWidth());
			panelHeight = (int) (faktor * (double) img.getHeight());

			prevPanel = new ChoosePartOfImagePanel(panelWidth, panelHeight);

			if (img.getType() == BufferedImage.TYPE_CUSTOM)
			{
				img = ImageOperations.pngToARGB(img, null);
			}

			prevPanel.setImg(img);

			// falls schon eine Auswahl gemacht wurde
			Rectangle oldSelection = view.getNetzwerk().getHintergrund()
					.getUpscaledImgSelection();
			if (oldSelection != null)
			{
				Rectangle r = ImageOperations.getScaledSelection(oldSelection,
						panelWidth, panelHeight, img.getWidth(), img.getHeight());
				prevPanel.setImgSelection(r);
			}
			JButton okButton = new JButton(Messages.getString("ProjectChooser.OK"));
			okButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					Rectangle upscaledImgSelection = prevPanel
							.getUpscalledSelection();
					if (upscaledImgSelection != null)
					{
						caller.setImgSelection(upscaledImgSelection);
					}
					else
					{
						// Auswahl aufloesen
						caller.setImgSelection(null);
					}
					dispose();
				}
			});

			JButton abortButton = new JButton(
					Messages.getString("ProjectChooser.Cancel"));
			abortButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					dispose();
				}
			});

			GridBagConstraints c = new GridBagConstraints();
			this.setLayout(new GridBagLayout());

			c.gridx = 2;
			c.weightx = 1;
			c.weighty = 1;
			c.gridy = 1;
			c.ipadx = 25;
			c.anchor = GridBagConstraints.EAST;
			this.add(okButton, c);

			c.weightx = 1;
			c.gridx = 3;
			c.ipadx = 0;
			c.anchor = GridBagConstraints.WEST;
			this.add(abortButton, c);

			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 0;
			c.gridheight = 1;
			c.gridwidth = 6;
			c.anchor = GridBagConstraints.NORTH;
			this.add(prevPanel, c);

			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setResizable(false);
			this.pack();
			this.setVisible(true);

		} catch (IOException e)
		{
			e.printStackTrace();
			JOptionPane
					.showMessageDialog(
							this,
							Messages
									.getString("BackgroundConfig.ErrorReadingImgDotFile")
									+ "\n\n"
									+ Messages
											.getString("BackgroundConfig.ErrorReadingImgDotFile"),
							Messages.getString("VennMaker.Error"),
							JOptionPane.ERROR_MESSAGE, null);
			this.dispose();
		}
	}

	public void paintComponent(Graphics g)
	{
		prevPanel.repaint();
	}
}

/**
 * MouseListener fuer den ChoosePartOfImageDialog
 * 
 * 
 * 
 * 
 */

class ChoosePartOfImageMouseListener extends MouseAdapter implements
		MouseMotionListener, MouseWheelListener
{
	private ChoosePartOfImagePanel	source;

	private int								dX, dY;

	private Point							mousePressed;

	private int								selection;

	public ChoosePartOfImageMouseListener(ChoosePartOfImagePanel source)
	{
		this.source = source;
		selection = -1;
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		mousePressed = (Point) e.getPoint().clone(); // Punkt wo geklickt worden
																	// ist speichern
		selection = source.getSelection(e.getX(), e.getY(), false); // Was wurde
																						// angeklickt?
		// Wenn in die Bildauswahl geklickt wurde
		if (selection == 0)
		{
			// Wieviel wurde vom linken Rand weg in die Auswahl geklickt
			// (beim verschieben wichtig)
			dX = source.getImgSelection().x - e.getX();
			dY = source.getImgSelection().y - e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		mousePressed = null;
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (mousePressed == null)
			return;

		// Auswahl verschieben
		if (selection == 0)
		{
			source.setImgPartX(e.getX() + dX);
			source.setImgPartY(e.getY() + dY);

			this.dX = source.getImgSelection().x - e.getX();
			this.dY = source.getImgSelection().y - e.getY();
		}
		// Neue Auswahl erzeugen
		else if (selection < 0)
		{
			selection = 5;
		}
		// Auswahl Groesse aendern
		else
		{
			Rectangle r = source.getImgSelection();
			int newX = r.x;
			int newY = r.y;
			int newW = r.width;
			int newH = r.height;
			switch (selection)
			// Selection enthaelt das angeklickte "Aenderungs-Quadrat"
			{
				case 1:
					newX = e.getX(); // oben links
					newY = e.getY();
					newW = r.width + (r.x - e.getX());
					newH = r.height + (r.y - newY);
					break;
				case 2:
					newY = e.getY(); // oben mitte
					newH = r.height + (r.y - newY);
					break;
				case 3:
					newW = e.getX() - r.x; // oben rechts
					newY = e.getY();
					newH = r.height + (r.y - newY);
					break;
				case 4:
					newW = e.getX() - r.x; // rechts mitte
					break;
				case 5:
					newW = e.getX() - r.x; // rechts unten
					newH = e.getY() - r.y;
					break;
				case 6:
					newH = e.getY() - r.y; // unten mitte
					break;
				case 7:
					newX = e.getX(); // unten links
					newW = r.width - (newX - r.x);
					newH = r.height - (r.y + r.height - e.getY());
					break;
				case 8:
					newX = e.getX(); // links mitte
					newW = r.width - (newX - r.x);
					break;
				default:
					;
			}

			// wenn die neue Breite negativ ist
			// wird die x-Coordinate der Auswahl geaendert (anstatt der Breite)
			if (newW < 0)
			{
				newX = r.x;
				newW *= -1;

				// wenn links angefasst wird muss das momentane X
				// abgefragt werden und nicht das alte
				if (selection == 1 || selection == 7 || selection == 8)
				{
					newX = e.getX();
				}

				newX -= newW;
				selection = source.getMirrorSelectionNegativeW(selection); // "Aenderungs-Quadrat"
																								// in
																								// Selection
																								// muss
																								// angepasst
																								// werden
			} // man zieht ja dann genau das spiegelverkehrte zu dem momentanen...
			// wie bei newW...
			if (newH < 0)
			{
				newY = r.y;
				newH *= -1;

				// wenn links angefasst wird muss das momentane X
				// abgefragt werden und nicht das alte
				if (selection == 1 || selection == 2 || selection == 3)
				{
					newY = e.getY();
				}

				newY -= newH;

				selection = source.getMirrorSelectionNegativeH(selection);
			}

			source.setImgPartComplete(newX, newY, newW, newH);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		int i = source.getSelection(e.getX(), e.getY(), true);
		int c = Cursor.DEFAULT_CURSOR;

		switch (i)
		{
			case 0:
				c = Cursor.MOVE_CURSOR;
				break;
			case 1:
				c = Cursor.NW_RESIZE_CURSOR;
				break;
			case 2:
				c = Cursor.N_RESIZE_CURSOR;
				break;
			case 3:
				c = Cursor.NE_RESIZE_CURSOR;
				break;
			case 4:
				c = Cursor.E_RESIZE_CURSOR;
				break;
			case 5:
				c = Cursor.SE_RESIZE_CURSOR;
				break;
			case 6:
				c = Cursor.S_RESIZE_CURSOR;
				break;
			case 7:
				c = Cursor.SW_RESIZE_CURSOR;
				break;
			case 8:
				c = Cursor.W_RESIZE_CURSOR;
				break;
			default:
				c = Cursor.DEFAULT_CURSOR;
		}

		source.setCursor(Cursor.getPredefinedCursor(c));
	}
}

/**
 * Panel welches ein Bild in gewuenschter Groesse darstellt und eine Teilauswahl
 * ermoeglicht
 * 
 * 
 * 
 */

class ChoosePartOfImagePanel extends JPanel
{
	private static final long	serialVersionUID	= 1L;

	private BufferedImage		originImg;

	private BufferedImage		img;

	private ImageObserver		imgObserver;

	private Rectangle				imgPart;					// choosenPart of the
																		// Image

	private Rectangle[]			sizeRectangles;			// Rectangles for Changing
																		// the size of the
																		// imagePart

	private int						panelWidth, panelHeight;

	public ChoosePartOfImagePanel(int panelWidth, int panelHeight)
	{
		super();
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		initialize();
		this.setPreferredSize(new Dimension(panelWidth, panelHeight));
	}

	private void initialize()
	{
		img = null;
		imgObserver = this;
		ChoosePartOfImageMouseListener listener = new ChoosePartOfImageMouseListener(
				this);

		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);
		this.addMouseWheelListener(listener);

		imgPart = new Rectangle(-1, -1, -1, -1);
		sizeRectangles = new Rectangle[8];
		for (int i = 0; i < sizeRectangles.length; i++)
		{
			sizeRectangles[i] = new Rectangle(-1, -1, 10, 10);
		}
	}

	public void setImg(BufferedImage newImg)
	{
		if (newImg.getType() == BufferedImage.TYPE_CUSTOM)
		{
			newImg = ImageOperations.pngToARGB(newImg, null);
		}
		int curH = newImg.getHeight();
		int curW = newImg.getWidth();
		originImg = newImg;
		AffineTransformOp ato = new AffineTransformOp(
				AffineTransform.getScaleInstance((double) panelWidth / curW,
						(double) panelHeight / curH), AffineTransformOp.TYPE_BICUBIC);
		BufferedImage smallImg = new BufferedImage(panelWidth, panelHeight,
				newImg.getType());
		ato.filter(newImg, smallImg);
		this.img = smallImg;
	}

	public void setImgPartX(int x)
	{
		boolean leftEnd = x < 0;
		boolean rightEnd = (x + imgPart.width > panelWidth);
		if (leftEnd)
		{
			x = 0;
		}
		else if (rightEnd)
		{
			x = panelWidth - imgPart.width;
		}
		imgPart.x = x;
		repaint();
	}

	public void setImgPartY(int y)
	{
		boolean topEnd = y < 0;
		boolean bottomEnd = (y + imgPart.height > panelHeight);
		if (topEnd)
		{
			y = 0;
		}
		else if (bottomEnd)
		{
			y = panelHeight - imgPart.height;
		}
		imgPart.y = y;
		repaint();
	}

	public void setImgPartW(int w)
	{
		boolean rightEnd = (imgPart.x + w) > panelWidth;
		if (rightEnd)
			w = panelWidth - imgPart.x;
		imgPart.width = w;
		repaint();
	}

	public void setImgPartH(int h)
	{
		boolean bottomEnd = (imgPart.y + h) > panelHeight;
		if (bottomEnd)
			h = panelHeight - imgPart.y;
		imgPart.height = h;
		repaint();
	}

	public BufferedImage getImg()
	{
		return img;
	}

	/**
	 * returns the selected rectangle
	 */
	public Rectangle getImgSelection()
	{
		if (imgPart.x < 0)
			return null;
		return (Rectangle) imgPart.clone();
	}

	/**
	 * 
	 * @return - Rectangle containing the Upscaled Img Selection of the User
	 */
	public Rectangle getUpscalledSelection()
	{
		Rectangle r = ImageOperations.getScaledSelection(imgPart,
				originImg.getWidth(), originImg.getHeight(), panelWidth,
				panelHeight);

		if (r.width <= 0 || r.height <= 0)
			return null;

		return r;
	}

	/**
	 * 
	 * @param mouseX
	 * @param mouseY
	 * @param onlyForCursor
	 *           (if <code>true</code> the Area will not be repainted
	 * 
	 * @return 0 when mouseX and mouseY are in imgSelection, 1 to 8 when mousex
	 *         and mouseY are in one of the SizeRectangles (the int returned
	 *         spezifies the sizeRectangle), -1 when mouseX and mouseY are
	 *         outside of both.
	 * 
	 */
	public int getSelection(int mouseX, int mouseY, boolean onlyForCursor)
	{
		int startX = imgPart.x;
		int startY = imgPart.y;
		int endX = startX + imgPart.width;
		int endY = startY + imgPart.height;

		int r = -1;

		if ((r = isInSizeRectangle(mouseX, mouseY)) >= 0)
		{
			return r + 1;
		}
		else if (((mouseX >= startX) && (mouseX <= endX))
				&& ((mouseY >= startY) && (mouseY <= endY)))
			return 0;

		if (!onlyForCursor)
		{
			imgPart.x = mouseX;
			imgPart.y = mouseY;
			imgPart.width = 0;
			imgPart.height = 0;
			repaint();
		}
		return -1;
	}

	@Override
	public void paintComponent(Graphics graphics)
	{
		// Hintergrund Bild
		calculateSizeRectangles();
		Graphics2D g = (Graphics2D) graphics.create();
		g.drawImage(img, 0, 0, imgObserver);

		// Ueberblendungen um die Auswahl
		g.setComposite(AlphaComposite.SrcOver.derive(.70f));
		g.setColor(Color.black);
		g.fillRect(0, 0, panelWidth, imgPart.y); // oben
		g.fillRect(0, imgPart.y + imgPart.height, panelWidth, panelHeight
				- (imgPart.y + imgPart.height)); // unten
		g.fillRect(0, imgPart.y, imgPart.x, imgPart.height); // links
		g.fillRect(imgPart.x + imgPart.width, imgPart.y, panelWidth
				- (imgPart.x + imgPart.width), imgPart.height); // rechts

		// Auwahl
		g.setColor(Color.white);
		g.setComposite(AlphaComposite.SrcOver.derive(.00f));
		g.drawRect(imgPart.x, imgPart.y, imgPart.width, imgPart.height);
		g.setComposite(AlphaComposite.SrcOver.derive(.80f));
		for (int i = 0; i < sizeRectangles.length; i++)
		{
			g.drawRect(sizeRectangles[i].x, sizeRectangles[i].y,
					sizeRectangles[i].width, sizeRectangles[i].height);
		}

	}

	/**
	 * cascade methode to avoid seperate calls for setImgPartX, setImgPartY ...
	 */
	public void setImgPartComplete(int x, int y, int w, int h)
	{
		if (x < 0)
			w = imgPart.width + imgPart.x;
		if (y < 0)
			h = imgPart.height + imgPart.y;
		setImgPartX(x);
		setImgPartY(y);
		setImgPartW(w);
		setImgPartH(h);
	}

	private void calculateSizeRectangles()
	{
		int x = imgPart.x;
		int y = imgPart.y;
		int w = imgPart.width;
		int h = imgPart.height;
		// northWest
		sizeRectangles[0].x = x - 5;
		sizeRectangles[0].y = y - 5;
		// north
		sizeRectangles[1].x = x + w / 2 - 5;
		sizeRectangles[1].y = y - 5;
		// northEast
		sizeRectangles[2].x = x + w - 5;
		sizeRectangles[2].y = y - 5;
		// east
		sizeRectangles[3].x = x + w - 5;
		sizeRectangles[3].y = y + h / 2 - 5;
		// southEast
		sizeRectangles[4].x = x + w - 5;
		sizeRectangles[4].y = y + h - 5;
		// south
		sizeRectangles[5].x = x + w / 2 - 5;
		sizeRectangles[5].y = y + h - 5;
		// southWest
		sizeRectangles[6].x = x - 5;
		sizeRectangles[6].y = y + h - 5;
		// west
		sizeRectangles[7].x = x - 5;
		sizeRectangles[7].y = y + h / 2 - 5;
	}

	/**
	 * returns the index of the sizing rectangle matching with the mouse position
	 * or -1 if pointer matches none of them...
	 */
	public int isInSizeRectangle(int mouseX, int mouseY)
	{
		for (int i = 0; i < sizeRectangles.length; i++)
		{
			Rectangle r = sizeRectangles[i];
			int startX = r.x;
			int startY = r.y;
			int endX = startX + r.width;
			int endY = startY + r.height;

			if (((mouseX >= startX) && (mouseX <= endX))
					&& ((mouseY >= startY) && (mouseY <= endY)))
				return i;
		}
		return -1;
	}

	/**
	 * returns the index of the sizing rectangle if the user drags it over the
	 * WIDTH outside line. by this movement the dragged sizing rectangle and the
	 * oposite have to switch indexes.
	 * 
	 */
	public int getMirrorSelectionNegativeW(int selection)
	{
		switch (selection)
		{
			case 1:
				return 3;
			case 3:
				return 1;
			case 4:
				return 8;
			case 5:
				return 7;
			case 7:
				return 5;
			case 8:
				return 4;
		}
		return selection;
	}

	/**
	 * returns the index of the sizing rectangle if the user drags it over the
	 * HEIGHT outside line. by this movement the dragged sizing rectangle and the
	 * oposite have to switch indexes.
	 */
	public int getMirrorSelectionNegativeH(int selection)
	{
		switch (selection)
		{
			case 1:
				return 7;
			case 2:
				return 6;
			case 3:
				return 5;
			case 5:
				return 3;
			case 6:
				return 2;
			case 7:
				return 1;
		}
		return selection;
	}

	/**
	 * @param oldSelection
	 */
	public void setImgSelection(Rectangle oldSelection)
	{
		if (oldSelection != null)
			this.imgPart = oldSelection;
		repaint();
	}
}
