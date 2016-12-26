/**
 * 
 */
package wizards;

import gui.Messages;
import gui.VennMaker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import data.Akteur;
import data.EventProcessor;
import events.NewActorEvent;

/**
 * Dieser Wizard fragt die Namen von Akteuren ab und erzeugt diese anschließend
 * im Projekt, das entspricht erstmal einem "indirekten Namensgenerator".
 * 
 * 
 */
public class AskActorNamesWizard extends VennMakerWizard
{
	/**
	 * Über der Namensabfrage anzuzeigender Text (Frage).
	 */
	private String				text;

	/**
	 * Maximale Anzahl an Akteuren.
	 */
	private int					maxActors;

	@XStreamOmitField
	private JTable				table;

	@XStreamOmitField
	private MyTableModel		model;

	/**
	 * Eine Liste der eingegebenen Namen.
	 */
	private Vector<String>	namen;

	@Override
	public void invoke()
	{
		/**
		 * Wir brauchen ein Anzeigefenster.
		 */
		prepareDialog();
		GridBagConstraints gbc;

		this.namen = new Vector<String>();
		this.namen.add(""); //$NON-NLS-1$
		model = new MyTableModel(this.namen);
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(400, 400));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableRowSorter<MyTableModel> sorter = new TableRowSorter<MyTableModel>(
				model);
		table.setRowSorter(sorter);

		if (getMaxActors() > 0)
			for (int i = 1; i < getMaxActors(); i++)
				namen.add(""); //$NON-NLS-1$

		JTextArea textArea = new JTextArea(text);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setCursor(null);
		textArea.setOpaque(false);
		textArea.setFocusable(false);
		textArea.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setBorder(null);
		scrollPane.setMinimumSize(new Dimension(400, 40));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 0, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		scrollPane = new JScrollPane(table);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 2;
		gbc.insets = new Insets(10, 10, 10, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		JButton removeButton = new JButton(Messages.getString("VennMaker.Remove")); //$NON-NLS-1$
		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (table.getSelectedRowCount() > 0
						&& !namen.get(table.getSelectedRow()).equals("")) { //$NON-NLS-1$
					namen.remove(table.getSelectedRow());
					if (getMaxActors() > 0)
						namen.add(""); //$NON-NLS-1$
					model.fireTableDataChanged();
				}
				else
					JOptionPane.showMessageDialog(null, Messages
							.getString("VennMaker.Select_to_delete"), Messages //$NON-NLS-1$
							.getString("VennMaker.Error"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
			}
		});
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 10, 20, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(removeButton, gbc);
		leftPanel.add(removeButton);

		dialog.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - dialog.getHeight()) / 2;
		int left = (screenSize.width - dialog.getWidth()) / 2;
		dialog.setLocation(left, top);

		// markiert die erste Tabellenzeile und ermöglicht sofortiges Lostippen
		table.changeSelection(0, 0, false, false);

		dialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wizards.VennMakerWizard#nextClicked()
	 */
	@Override
	public void nextClicked()
	{
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
		for (String name : namen)
			if (!name.equals("")) { //$NON-NLS-1$
				Akteur akteur = new Akteur(name);

				NewActorEvent newActorEvent = new NewActorEvent(akteur);
				EventProcessor.getInstance().fireEvent(newActorEvent);
			}
		dialog.dispose();
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	class MyTableModel extends AbstractTableModel
	{
		static final long			serialVersionUID	= 1L;

		private Vector<String>	namen;

		public MyTableModel(Vector<String> namen)
		{
			this.namen = namen;
		}

		public int getColumnCount()
		{
			return 1;
		}

		public int getRowCount()
		{
			return namen.size();
		}

		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = "Name";
					break;
				default:
					r = "?"; //$NON-NLS-1$
					break;
			}
			return r;
		}

		public Object getValueAt(int row, int col)
		{
			Object r;
			switch (col)
			{
				case 0:
					r = namen.elementAt(row);
					break;
				default:
					r = "?"; //$NON-NLS-1$
					break;
			}
			return r;
		}

		public Class<?> getColumnClass(int col)
		{
			Class<?> r;
			switch (col)
			{
				default:
					r = String.class;
					break;
			}
			return r;
		}

		public boolean isCellEditable(int row, int col)
		{
			return true;
		}

		public void setValueAt(Object value, int row, int col)
		{
			switch (col)
			{
				case 0:
					if (value.equals("")) { //$NON-NLS-1$
						// leere Einträge löschen, außer in letzter Zeile
						if (row + 1 < namen.size())
						{
							this.namen.remove(row);
							model.fireTableDataChanged();
						}
					}
					else
					{
						if (this.namen.contains(value)
								&& !this.namen.get(row).equals(value))
						{
							assert (VennMaker.getInstance() != null);
							assert (VennMaker.getInstance().getConfig() != null);
							switch (VennMaker.getInstance().getConfig()
									.getDuplicateBehaviour())
							{
								case NOT_ALLOWED:
									JOptionPane.showMessageDialog(null, Messages
											.getString("VennMaker.No_Duplicate")); //$NON-NLS-1$
									return;
								case ALLOWED:
									break;
								case ASK_USER:
									int val = JOptionPane.showConfirmDialog(null,
											Messages.getString("VennMaker.Already_Same"), //$NON-NLS-1$
											Messages
													.getString("VennMaker.Duplicate_Actor"), //$NON-NLS-1$
											JOptionPane.YES_NO_OPTION);
									if (val == JOptionPane.YES_OPTION)
									{
										break;
									}
									// eigentlich überflüssige abfrage, aber sicher ist
									// sicher
									else if (val == JOptionPane.NO_OPTION)
									{
										return;
									}
								default:
									assert (false);
							}
						}
						// neue Leerzeile einfügen, falls Namensanzahl unbegrenzt
						if (this.namen.get(row).equals("") && getMaxActors() == 0) //$NON-NLS-1$
							this.namen.add(""); //$NON-NLS-1$
						this.namen.set(row, (String) value);
					}
					break;
			}

			fireTableDataChanged();
		}
	}

	/**
	 * @return the maxActors
	 */
	public int getMaxActors()
	{
		return maxActors;
	}

	/**
	 * @param maxActors
	 *           the maxActors to set
	 */
	public void setMaxActors(int maxActors)
	{
		this.maxActors = maxActors;
	}
}
