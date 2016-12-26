/**
 * 
 */
package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

/**
 * 
 * 
 */
public class ColorRenderer extends JLabel implements ListCellRenderer
{
	static final long	serialVersionUID	= 1L;

	Border				unselectedBorder	= null;

	Border				selectedBorder		= null;

	boolean				isBordered			= true;

	public ColorRenderer(boolean isBordered)
	{
		this.isBordered = isBordered;
		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getListCellRendererComponent(JList list, Object color,
			int pos, boolean isSelected, boolean hasFocus)
	{
		setText(" "); //$NON-NLS-1$
		Color newColor = (Color) color;
		setBackground(newColor);
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

		setToolTipText(Messages.getString("VennMaker.RGB_Value") //$NON-NLS-1$
				+ newColor.getRed() + ", " + newColor.getGreen() + ", " //$NON-NLS-1$ //$NON-NLS-2$
				+ newColor.getBlue());
		return this;
	}
}
