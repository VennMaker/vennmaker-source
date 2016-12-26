package data;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import files.SystemOperations;
import gui.Messages;

public class AttributeMouseListener implements MouseListener, ActionListener
{
	/**
	 * rowindexes to exclude non-valid rows
	 */
	int			startIndex		= -1;

	int			endIndex			= -1;

	/**
	 * popupmenu
	 */
	JPopupMenu	popup;

	/** currently selected AttributeValue */
	String		attributeValue	= "";

	/** constructor without any indexes - uses whole table */
	public AttributeMouseListener()
	{
		this(-1, -1);
	}

	/**
	 * constructor with startIndex - starts at this index, uses all columns of
	 * higher value
	 */
	public AttributeMouseListener(int startIndex)
	{
		this(startIndex, -1);
	}

	/**
	 * constructor to provide indexes, if there are columns, which should not
	 * react to this mouselistener (e.g. to exclude Actornames, Attributenames,
	 * etc)
	 * 
	 * @param startIndex
	 * @param endIndex
	 */
	public AttributeMouseListener(int startIndex, int endIndex)
	{
		this.startIndex = startIndex;
		this.endIndex = endIndex;

		popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem(
				Messages.getString("AttributeMouseListener.openInBrowser"));
		menuItem.addActionListener(this);
		menuItem.setActionCommand("openLink");
		popup.add(menuItem);
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if (arg0.getButton() == arg0.BUTTON3)
		{
			JTable table = (JTable) arg0.getSource();
			int mouseRow = table.rowAtPoint(table.getMousePosition());
			int mouseCol = table.columnAtPoint(table.getMousePosition());
			/* if no startindex is defined: use whole table */
			if (startIndex == -1)
			{
				startIndex = 0;
			}
			/* same with endindex */
			if (endIndex == -1)
			{
				endIndex = table.getColumnCount() - 1;
			}
			if ((mouseCol >= startIndex) && (mouseCol <= endIndex)
					&& (mouseRow > -1) && (mouseRow < table.getRowCount()))
			{
				table.setRowSelectionInterval(mouseRow, mouseRow);
				table.setColumnSelectionInterval(mouseCol, mouseCol);

				Object value = table.getValueAt(table.getSelectedRow(),
						table.getSelectedColumn());
				if (value != null)
				{
					attributeValue = value.toString();
					popup.show(table, arg0.getX(), arg0.getY());
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{

	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		/* open currently selected AttributeValue as link in the standardbrowser */
		if ("openLink".equals(arg0.getActionCommand()))
		{
			String link = attributeValue.trim();
			if (!link.toLowerCase().matches("^(https?|ftp)://.*$"))
			{
				link = "http://" + link;
			}
			try
			{
				SystemOperations.openUrl(link);
			} catch (IOException e)
			{
				e.printStackTrace();
			} catch (URISyntaxException e)
			{
				e.printStackTrace();
			}
		}
	}

}
