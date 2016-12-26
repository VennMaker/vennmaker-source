package gui;

/**
 * 
 *
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class DrawText
{

	private static final Color	COLOR_LABEL_BOX	= new Color(207, 207, 0, 52);

	private static final Color	COLOR_LABEL_TEXT	= new Color(0, 0, 0, 92);

	public static DrawText create(Graphics2D g2d, String text, int x, int y,
			boolean tranz_mode)
	{
		DrawText instance = new DrawText(g2d, x, y, text, tranz_mode);
		return instance;
	}

	DrawText(Graphics2D g2d, int x, int y, String text, boolean tranz_mode)
	{

		FontMetrics fm = g2d.getFontMetrics();

		int hoehe = (int) (fm.getHeight() * 1);
		int tmp_y = y;

		int width = 0;

		String[] lines = text.split("\\n"); //$NON-NLS-1$

		// Tranzparente Darstellung?
		if (tranz_mode == true)
		{

			Font alt = g2d.getFont();

			g2d.setColor(COLOR_LABEL_BOX);

			// Hoehe und Breite des Textfelds berechnen
			for (String line : lines)
			{
				if (fm.stringWidth(line) > width)
					width = fm.stringWidth(line);

				tmp_y += hoehe;
			}

			g2d.fillRect(x, y - fm.getAscent(), width, tmp_y - y);

			g2d.setFont(alt);
			g2d.setColor(COLOR_LABEL_TEXT);
		}

		for (String line : lines)
		{
			g2d.drawString(line, x, y);

			y += hoehe;
		}

	}

}
