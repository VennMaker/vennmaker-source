package gui.configdialog;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class CirclePreview extends JPanel
{
	/**
	 * the circles which need to be previewed
	 */
	private List<String>	circles	= new ArrayList<String>();

	public void setCircles(List<String> circles)
	{
		this.circles = circles;
	}

	public List<String> getCircles()
	{
		return this.circles;
	}

	/**
	 * constructor to set backgroundcolor
	 */
	public CirclePreview()
	{
		this.setOpaque(true);
		// this.setBackground(Color.WHITE);
	}

	@Override
	public void paintComponent(final Graphics graphics)
	{
		super.paintComponent(graphics);
		this.paintCircles(graphics);
	}

	/**
	 * paints the given circles to create the preview
	 * 
	 * @param graphics
	 *           graphics object to paint onto
	 */
	private void paintCircles(final Graphics graphics)
	{
		final Graphics2D g = (Graphics2D) graphics.create();
		if ((circles != null) && (circles.size() > 0))
		{
			double x_mid = this.getWidth() / 2;
			double y_mid = this.getHeight() / 2;

			float egoSpace = this.getHeight() / 10;

			g.setFont(g.getFont().deriveFont(Font.PLAIN, 10));

			double minLength = Math.min(this.getWidth() / 2, this.getHeight() / 2);
			minLength = minLength - 1;

			int numCircles = circles.size();
			for (int i = 1; i <= numCircles; ++i)
			{
				double radius = egoSpace + ((minLength - egoSpace) / numCircles)
						* i;
				Ellipse2D e = new Ellipse2D.Double(x_mid - (int) radius, y_mid
						- (int) radius, 2 * (int) radius, 2 * (int) radius);
				g.draw(e);

				// der 0-te Kreis ist der Kreis direkt um Ego und wird an
				// anderer
				// Stelle nicht mitgezählt
				if (i != 0 && (i - 1) < circles.size()
						&& circles.get(i - 1) != null)
				{
					FontMetrics fm = getFontMetrics(g.getFont());
					g.drawString(circles.get(i - 1),
							(int) (x_mid - fm.stringWidth(circles.get(i - 1)) / 2),
							(int) (y_mid + radius - 3));
				}
			}
		}
	}
}
