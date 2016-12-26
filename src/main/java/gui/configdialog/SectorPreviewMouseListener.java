/**
 * 
 */
package gui.configdialog;

import gui.Messages;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JColorChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * 
 * 
 */
public class SectorPreviewMouseListener implements MouseMotionListener,
		MouseListener, MouseWheelListener
{

	private SectorPreview	parentCanvas;

	private int							mouseOnSector		= -1;

	private int							mouseOnTouchPoint	= -1;

	public SectorPreviewMouseListener(SectorPreview parent)
	{
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		parentCanvas = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		if ((mouseOnTouchPoint > -1)
				&& (javax.swing.SwingUtilities.isLeftMouseButton(arg0)))
		{
			parentCanvas.moveTouchPoint(mouseOnTouchPoint, arg0.getX(), arg0
					.getY());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		if (mouseOnTouchPoint != parentCanvas.getTouchPoint(arg0.getX(), arg0
				.getY()))
		{
			mouseOnTouchPoint = parentCanvas.getTouchPoint(arg0.getX(), arg0
					.getY());
			parentCanvas.setActiveTouchPoint(mouseOnTouchPoint);
		}

		if (mouseOnTouchPoint > -1)
			parentCanvas.setActiveSector(-1);
		else if (mouseOnSector != parentCanvas
				.getSector(arg0.getX(), arg0.getY()))
		{
			mouseOnSector = parentCanvas.getSector(arg0.getX(), arg0.getY());
			parentCanvas.setActiveSector(mouseOnSector);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)// TODO Auto-generated method stub
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0)
	{
		if (arg0.getButton() == MouseEvent.BUTTON1) // Linksklick
		{
			/*if (mouseOnTouchPoint > -1)
				parentCanvas.disableHighDefRendering();
			else if (mouseOnSector > -1) // Name fuer Sektor eingeben
			{
				String newName = (String) JOptionPane.showInputDialog(parentCanvas,
						Messages.getString("VennMaker.NewName") //$NON-NLS-1$
						, Messages.getString("VennMaker.NameDialog") //$NON-NLS-1$
						, JOptionPane.PLAIN_MESSAGE, null, null, parentCanvas
								.getSectors().get(mouseOnSector).label);
				if (newName != null)
				{
					parentCanvas.lastchanged = true;
					parentCanvas.getSectors().get(mouseOnSector).label = newName;
					//parentCanvas.getParentConfig().updateTempSector(mouseOnSector);
					parentCanvas.repaint();
					parentCanvas.lastchanged = false;
				}
			}*/
		}
		else if (arg0.getButton() == MouseEvent.BUTTON3) // Rechtsklick
		{
			if (mouseOnSector > -1)
			{
				JPopupMenu sectorMenu = new JPopupMenu(parentCanvas.getSectors()
						.get(mouseOnSector).label);

				final JMenuItem changeColorItem = new JMenuItem(Messages
						.getString("VennMaker.ChangeSectorColor")); //$NON-NLS-1$
				changeColorItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent ve)
					{
						Color c = JColorChooser
								.showDialog(
										parentCanvas,
										Messages.getString("BackgroundConfig.String_6"), parentCanvas.getSectors().get(mouseOnSector).sectorColor); //$NON-NLS-1$
						if (c != null)
						{
							parentCanvas.getSectors().get(mouseOnSector).sectorColor = c;
							parentCanvas.repaint();
							parentCanvas.getParentConfig().setSectorColor(mouseOnSector, c);
						}
					}
				});
				sectorMenu.add(changeColorItem);
				sectorMenu.show(arg0.getComponent(), (int) arg0.getX(), (int) arg0
						.getY());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		parentCanvas.enableHighDefRendering();
		// parentCanvas.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
	 * MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0)
	{
		if (arg0.getWheelRotation() < 0)
			parentCanvas.enlargeSector(parentCanvas.getActiveSector());
		else
			parentCanvas.shrinkSector(parentCanvas.getActiveSector());
	}

}
