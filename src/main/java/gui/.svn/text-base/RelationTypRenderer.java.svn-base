/**
 * 
 */
package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import data.RelationTyp;

/**
 * 
 * 
 */
public class RelationTypRenderer extends JLabel implements TableCellRenderer,
		ListCellRenderer
{
	static final long		serialVersionUID	= 1L;

	Border					unselectedBorder	= null;

	Border					selectedBorder		= null;

	boolean					isBordered			= true;

	private RelationTyp	relationTyp;

	public RelationTypRenderer(boolean isBordered)
	{
		this.isBordered = isBordered;
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object typ,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		relationTyp = (RelationTyp) typ;

		return this;
	}

	public Component getListCellRendererComponent(JList list, Object typ,
			int pos, boolean isSelected, boolean hasFocus)
	{
		relationTyp = (RelationTyp) typ;
		setText(" "); //$NON-NLS-1$

		if (isBordered)
		{
			if (isSelected)
			{
				if (selectedBorder == null)
				{
					selectedBorder = BorderFactory.createMatteBorder(1, 5, 1, 5,
							list.getSelectionBackground());
				}
				setBorder(selectedBorder);
			}
			else
			{
				if (unselectedBorder == null)
				{
					unselectedBorder = BorderFactory.createMatteBorder(1, 5, 1, 5,
							list.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}

		return this;
	}

	public void paintComponent(final Graphics g)
	{
		final Graphics2D g2 = (Graphics2D) g;
		final Dimension size = getSize();
		final Insets insets = getInsets();
		final double xx = insets.left;
		final double yy = insets.top;
		final double ww = size.getWidth() - insets.left - insets.right;
		final double hh = size.getHeight() - insets.top - insets.bottom;

		g2.setStroke(relationTyp.getStroke());
		g2.setPaint(relationTyp.getColor());
		g2.setStroke(relationTyp.getStroke());
		Shape line = null;
		switch (relationTyp.getDirectionType())
		{
			case DIRECTED:
				line = new ArrowLine(xx + 6, yy + hh / 2, xx + ww - 6, yy + hh / 2);
				break;
			case UNDIRECTED:
				line = new Line2D.Double(xx + 6, yy + hh / 2, xx + ww - 6, yy + hh
						/ 2);
				break;
			default:
				// Sollte niemals passieren!
				assert false;
		}
		g2.draw(line);
	}

}
