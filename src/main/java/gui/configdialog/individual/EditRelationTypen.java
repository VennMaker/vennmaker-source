package gui.configdialog.individual;

import gui.Messages;
import gui.RelationTypChooser;
import gui.RelationTypRenderer;
import gui.VennMaker;
import gui.VennMakerView;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import data.Akteur;
import data.EventProcessor;
import data.Netzwerk;
import data.Relation;
import data.RelationTyp;
import events.ChangeRelationTypeEvent;
import events.ComplexEvent;
import events.DeleteRelationTypeEvent;
import events.NewRelationTypeEvent;
/**
 * Dialog zur Bearbeitung der verschiedenen Relationstypen.
 * 
 * 
 */
public class EditRelationTypen implements ActionListener
{
	private static final long		serialVersionUID	= 1L;

	/**
	 * Die aktuell eingestellten Werte. Nur Zeiger auf Modell.
	 */
	protected Vector<RelationTyp>	relationTypen;

	/**
	 * Eine Kopie der Ursprungswerte. Damit ist Undo moeglich.
	 */
	private List<RelationTyp>		relationTypenAlt;

	private JTable						table;

	private MyTableModel				model;

	private ArrayList<JButton>		buttons				= new ArrayList<JButton>();

	private Vector<RelationTyp>	tempRelations		= new Vector<RelationTyp>();

	private Vector<RelationTyp>	deleteCanidates	= new Vector<RelationTyp>();
	
	private JPanel 					panel;

	public EditRelationTypen()
	{
		buttons = new ArrayList<JButton>();

	}

	private void constructArrayList()
	{
		relationTypen = VennMaker.getInstance().getProject().getRelationTypen();
		tempRelations = new Vector<RelationTyp>();
		for (int i = 0; i < relationTypen.size(); i++)
		{
			tempRelations.add((RelationTyp) relationTypen.get(i).clone());
		}
		this.relationTypenAlt = new ArrayList<RelationTyp>();
		for (RelationTyp t : this.relationTypen)
		{
			relationTypenAlt.add((RelationTyp) t);
		}
	}

	private JPanel createDialog()
	{
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		GridBagConstraints gbc;

		int zeile = 0;

		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(300, 200));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableRowSorter<MyTableModel> sorter = new TableRowSorter<MyTableModel>(
				model);
		table.setRowSorter(sorter);

		table
				.setDefaultRenderer(RelationTyp.class,
						new RelationTypRenderer(true));
		table.getColumnModel().getColumn(1).setCellEditor(
				new RelationTypChooser());

		JScrollPane scrollPane = new JScrollPane(table);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 6;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 0, 10, 10);
		layout.setConstraints(scrollPane, gbc);
		panel.add(scrollPane);

		zeile++;

		JButton b2 = new JButton(Messages
				.getString("VennMaker.Relation_New_Value")); //$NON-NLS-1$
		b2.setActionCommand("new"); //$NON-NLS-1$
		b2.addActionListener(this);
		buttons.add(b2);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(b2, gbc);
		panel.add(b2);

		JButton b3 = new JButton(Messages
				.getString("VennMaker.Relation.Delete_Value")); //$NON-NLS-1$
		b3.setActionCommand("delete"); //$NON-NLS-1$
		b3.addActionListener(this);
		buttons.add(b3);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.EAST;
		gbc.gridx = 3;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(b3, gbc);
		panel.add(b3);

		this.panel = panel;
		
		return panel;
	}

	public void actionPerformed(ActionEvent event)
	{
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
		if ("new".equals(event.getActionCommand())) //$NON-NLS-1$
		{
			String bezeichnung = JOptionPane.showInputDialog(Messages
					.getString("VennMaker.Type_in_RelationValue")); //$NON-NLS-1$
			
			if (bezeichnung != null)
			{
				if (!bezeichnung.equals("")) //$NON-NLS-1$
				{
					tempRelations.add(new RelationTyp(bezeichnung));
					model.fireTableDataChanged();
					table.changeSelection(tempRelations.size() - 1, 0, false, false);
				}
				else
					JOptionPane.showMessageDialog(null, Messages
							.getString("VennMaker.Empty_Relation_Name"), Messages //$NON-NLS-1$
							.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
		}
		else if ("delete".equals(event.getActionCommand())) //$NON-NLS-1$
		{
			if (table.getSelectedRowCount() > 0)
			{
				boolean used = false;
				// Wird der Relationstyp noch irgendwo verwendet?
				for (Netzwerk n : VennMaker.getInstance().getProject()
						.getNetzwerke())
					for (Akteur a : n.getAkteure())
						for (Relation r : a.getRelations(n))
							if (r.getTyp().equals(
									tempRelations.get(table.getSelectedRow())))
								used = true;
				if (!used)
				{
					deleteCanidates.add(tempRelations.get(table.getSelectedRow()));
					tempRelations.remove(table.getSelectedRow());
					model.fireTableDataChanged();
				}
				else
					JOptionPane.showMessageDialog(null, Messages
							.getString("EditRelationTypen.7"), //$NON-NLS-1$
							Messages.getString("VennMaker.Error"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(null, Messages
						.getString("VennMaker.Select_to_delete"), Messages //$NON-NLS-1$
						.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}
	}

	public JPanel getDialog()
	{
		constructArrayList();
		model = new MyTableModel(tempRelations);
		table = new JTable(model);
		deleteCanidates = new Vector<RelationTyp>();
		JPanel relPanel = createDialog();
		for (JButton b : buttons)
		{
			b.removeActionListener(this);
			b.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					if (table.getCellEditor() != null)
						table.getCellEditor().stopCellEditing();
					if ("new".equals(event.getActionCommand())) //$NON-NLS-1$
					{
//						String bezeichnung = JOptionPane.showInputDialog(Messages
//								.getString("VennMaker.Type_in_RelationValue")); //$NON-NLS-1$
						
						Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
						String bezeichnung = (String) JOptionPane
								.showInputDialog(
										panel,
										"", Messages.getString("VennMaker.Type_in_RelationValue"), //$NON-NLS-1$//$NON-NLS-2$
										JOptionPane.QUESTION_MESSAGE, icon, null, null);
						if (bezeichnung != null)
						{
							if (!bezeichnung.equals("")) //$NON-NLS-1$
							{
								tempRelations.add(new RelationTyp(bezeichnung));
								model.fireTableDataChanged();
								table.changeSelection(tempRelations.size() - 1, 0,
										false, false);
							}
							else
								JOptionPane
										.showMessageDialog(
												panel,
												Messages
														.getString("VennMaker.Empty_Relation_Name"), Messages //$NON-NLS-1$
														.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						}
					}
					else if ("delete".equals(event.getActionCommand())) //$NON-NLS-1$
					{
						if (table.getSelectedRowCount() > 0)
						{
							boolean used = false;
							// Wird der Relationstyp noch irgendwo verwendet?
							for (Netzwerk n : VennMaker.getInstance().getProject()
									.getNetzwerke())
								for (Akteur a : n.getAkteure())
									for (Relation r : a.getRelations(n))
										if (r.getTyp().equals(
												tempRelations.get(table.getSelectedRow())))
											used = true;
							if (!used)
							{
								deleteCanidates.add(tempRelations.get(table
										.getSelectedRow()));
								tempRelations.remove(table.getSelectedRow());
								model.fireTableDataChanged();
							}
							else
								JOptionPane.showMessageDialog(panel, Messages
										.getString("EditRelationTypen.7"), //$NON-NLS-1$
										Messages.getString("VennMaker.Error"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
						}
						else
							JOptionPane
									.showMessageDialog(
											panel,
											Messages
													.getString("VennMaker.Select_to_delete"), Messages //$NON-NLS-1$
													.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					}
				}
			});
		}

		return relPanel;
	}

	public String getDialogTitle()
	{
		return Messages.getString("EditRelationTypen.8"); //$NON-NLS-1$
	}

	public void update()
	{
		if (tempRelations != null && deleteCanidates != null
				&& relationTypenAlt != null)
		{
			ComplexEvent event = new ComplexEvent("EditRelations");//$NON-NLS-1$
			if (relationTypenAlt.size() > tempRelations.size())
			{
				for (int i = 0; i < deleteCanidates.size(); i++)
				{
					relationTypenAlt.remove(deleteCanidates.get(i));
					event.addEvent(new DeleteRelationTypeEvent(deleteCanidates
							.get(i)));
				}
			}
			for (int i = 0; i < relationTypenAlt.size(); i++)
			{
				event.addEvent(new ChangeRelationTypeEvent(relationTypenAlt.get(i),
						(RelationTyp) tempRelations.get(i).clone(),
						(RelationTyp) relationTypenAlt.get(i).clone()));
			}

			for (int i = relationTypenAlt.size(); i < tempRelations.size(); i++)
			{
				relationTypenAlt.add(tempRelations.get(i));
				event.addEvent(new NewRelationTypeEvent(tempRelations.get(i)));
			}
			EventProcessor.getInstance().fireEvent(event);
		}
	}

	public String toString()
	{
		return Messages.getString("EditRelationTypen.9"); //$NON-NLS-1$
	}

	public String getDescription()
	{
		return Messages.getString("EditRelationTypen.11"); //$NON-NLS-1$
	}

	public void cancel()
	{
		if (tempRelations != null && deleteCanidates != null
				&& relationTypenAlt != null)
		{
			relationTypen.clear();
			for (int i = 0; i < relationTypenAlt.size(); i++)
			{
				relationTypen.add((RelationTyp) relationTypenAlt.get(i));
			}
			constructArrayList();
			if (deleteCanidates != null)
			{
				deleteCanidates.clear();
			}
		}
	}

	public ImageIcon getIcon()
	{
		return null;
	}

	public void setView(VennMakerView view)
	{

	}

	class MyTableModel extends AbstractTableModel
	{
		static final long					serialVersionUID	= 1L;

		private Vector<RelationTyp>	types;

		public MyTableModel(Vector<RelationTyp> types)
		{
			this.types = types;
		}

		public int getColumnCount()
		{
			return 2;
		}

		public int getRowCount()
		{
			return types.size();
		}

		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = Messages.getString("VennMaker.Name"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("RelationTypChooser.5"); //$NON-NLS-1$
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
					r = types.elementAt(row).getBezeichnung();
					break;
				case 1:
					r = types.elementAt(row);
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
				case 1:
					r = RelationTyp.class;
					break;
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
					if (!value.equals("")) //$NON-NLS-1$
					{
						types.elementAt(row).setBezeichnung((String) value);
					}
					else
						JOptionPane.showMessageDialog(null, Messages
								.getString("VennMaker.Empty_Relation_Name"), Messages //$NON-NLS-1$
								.getString("VennMaker.Error"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
					break;

				case 1:
					break;
			}

			fireTableCellUpdated(row, col);
		}
	}
}
