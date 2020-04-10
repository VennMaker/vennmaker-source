package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import data.Akteur;
import data.AttributeMouseListener;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import data.Relation;
import data.RelationMonitor;
import events.AddActorEvent;
import events.AddRelationEvent;
import events.ComplexEvent;
import events.NewActorEvent;
import events.RemoveRelationEvent;
import gui.utilities.VennMakerUIConfig;

/**
 * 
 * Dialog which displays all relations of one network (by table) to edit their
 * properties and create new ones.<br>
 * <br>
 * 
 * displayed properties:<br>
 * 
 * - relation group<br>
 * - relation attribute<br>
 * - attribute value<br>
 * - start actor<br>
 * - end actor<br>
 * - "delete" (to delete this relation)<br>
 * 
 * 
 * 
 */
public class RelationTableDialog extends JDialog implements ActionListener
{
	private static final long	serialVersionUID		= 1L;

	private VennMakerView		view;

	private JTable					relationTable;

	private RelationTableModel	model;

	private Vector<Akteur>		actors;

	private String[]				relationTypes;

	private JButton				btnOkay;

	private JButton				btnCancel;

	private JButton				btnAddRelation;

	private JComboBox				cbStartActor;

	private JComboBox				cbEndActor;

	/**
	 * Jeder Relationstyp hat eine Liste von Object-Arrays <br>
	 * (ein Array = eine Zeile der Tabelle) <br>
	 * <br>
	 * Waehlt man als Relationstyp z.B. "STANDARDRELATION", <br>
	 * so wird die Tabelle mit diesen Eintraegen aus der Map gefuellt... <br>
	 * <br>
	 * Bsp.: <br>
	 * <br>
	 * Zeile = tableData.get(row) <br>
	 * <br>
	 * Zeile[INDEX_RELATION] = Relation NICHT ANZEIGEN (nur fuer Verarbeitung) <br>
	 * Zeile[INDEX_FIRST_ROW] = boolean-Wert "1. Zeile" NICHT ANZEIGEN (nur fuer
	 * Verarbeitung) <br>
	 * Zeile[INDEX_START_ACTOR] = StartAkteur nur in 1. Zeile dieser Relation
	 * anzeigen <br>
	 * Zeile[INDEX_RELATION_GROUP] = Relationsgruppe nur in 1. Zeile dieser
	 * Relation anzeigen <br>
	 * Zeile[INDEX_ATTR_TYPE] = Attribut Typ <br>
	 * Zeile[INDEX_ATTR_VALUE] = Attribut Wert <br>
	 * Zeile[INDEX_END_ACTOR] = EndAkteur nur in 1. Zeile dieser Relation
	 * anzeigen <br>
	 * Zeile[INDEX_DELETE] = boolean-Wert "loeschen" nur in 1. Zeile dieser
	 * Relation anzeigen <br>
	 * 
	 */
	private List<Object[]>		tableData;

	private static final int	HIDDEN_FIELDS			= 2;

	private static final int	INDEX_RELATION			= 0;

	private static final int	INDEX_FIRST_ROW		= 1;

	private static final int	INDEX_START_ACTOR		= 2;

	private static final int	INDEX_RELATION_GROUP	= 3;

	private static final int	INDEX_ATTR_TYPE		= 4;

	private static final int	INDEX_ATTR_VALUE		= 5;

	private static final int	INDEX_END_ACTOR		= 6;

	private static final int	INDEX_DELETE			= 7;

	public RelationTableDialog(VennMakerView view)
	{
		super(VennMaker.getInstance(), Messages.getString("VennMaker.22"));
		this.view = view;

		generateTableData();
		build();

		this.setModal(true);
		this.setSize(500, 500);
		this.setVisible(true);
	}

	private void generateTableData()
	{
		tableData = new ArrayList<Object[]>();
		//actors = VennMaker.getInstance().getProject().getAkteure();
		
		actors = view.getNetzwerk().getAkteure();
		Vector<Akteur> actors2 = VennMaker.getInstance().getProject().getAkteure();
		
		
		if (actors.size() > actors2.size() )
		{
			boolean found = false;
			
			System.out.println("Different size: N: "+actors.size()+" P: "+actors2.size());
			
			for (Akteur a: view.getNetzwerk().getAkteure())
			{
				found = false;
				for (Akteur b: VennMaker.getInstance().getProject().getAkteure())
				{
					if (a.getId() == b.getId())
					{
						found = true;
						break;
					}
				}
				if (found == false)
				{
					System.out.println("Akteur not found: "+a.getName());
					
					ComplexEvent event = new ComplexEvent(
							Messages.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
					event.addEvent(new NewActorEvent(a));
					event.addEvent(new AddActorEvent(a, view.getNetzwerk(), a.getLocation(view.getNetzwerk())));
					view.getNetzwerk().removeAkteur(a);
					EventProcessor.getInstance().fireEvent(event);
					break;

				}
			}
		}
		
		

		// Tabellenwerte erzeugen:
		Vector<String> tmpTypes = new Vector<String>();

		for (String s : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
			if (!s.equals("ACTOR"))
				tmpTypes.add(s);

		relationTypes = new String[tmpTypes.size()];
		tmpTypes.toArray(relationTypes);

		// Alle Relationen finden
		RelationMonitor rm = RelationMonitor.getInstance();
		for (Relation r : rm.getNetworkRelations(view.getNetzwerk()))
		{
			Vector<AttributeType> allATypes = VennMaker.getInstance().getProject()
					.getAttributeTypes(r.getAttributeCollectorValue());

			Map<AttributeType, Object> attributes = r.getAttributes(view
					.getNetzwerk());
			int i = 0;
			for (AttributeType a : allATypes)
			{
				Object[] o = new Object[8];
				Object val = attributes.get(a);
				o[INDEX_RELATION] = r;
				o[INDEX_FIRST_ROW] = i == 0;
				o[INDEX_START_ACTOR] = rm.getStartActor(r);
				o[INDEX_RELATION_GROUP] = r.getAttributeCollectorValue();
				o[INDEX_END_ACTOR] = r.getAkteur();
				o[INDEX_DELETE] = false;
				o[INDEX_ATTR_TYPE] = a;
				o[INDEX_ATTR_VALUE] = val;

				i++;
				tableData.add(o);
			}
		}
	}

	private void build()
	{
		GridBagLayout gbl = new GridBagLayout();
		JPanel panel = new JPanel(gbl);
		GridBagConstraints c = new GridBagConstraints();

		// Relation Table
		model = new RelationTableModel();
		relationTable = new JTable(model);
		((JLabel) relationTable.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);
		relationTable.getTableHeader().setReorderingAllowed(false);
		relationTable.setColumnSelectionAllowed(false);
		relationTable.setRowSelectionAllowed(false);
		relationTable.setCellSelectionEnabled(true);
		relationTable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);

		relationTable.setRowHeight((int) (VennMakerUIConfig.getFontSize()+15)); 
		
		relationTable.addMouseListener(new AttributeMouseListener(
				INDEX_RELATION_GROUP, INDEX_RELATION_GROUP));

		Akteur[] act = new Akteur[actors.size()];
		actors.toArray(act);

		cbStartActor = new JComboBox(act);
		cbEndActor = new JComboBox(act);

		TableColumnModel cm = relationTable.getColumnModel();

		cm.getColumn(0).setCellRenderer(new ComboBoxRenderer(act, 4));
		cm.getColumn(3).setCellRenderer(new AttributeValueRenderer());
		cm.getColumn(4).setCellRenderer(new ComboBoxRenderer(act, 0));
		cm.getColumn(5).setCellRenderer(new DeleteCellRenderer());

		cm.getColumn(0).setCellEditor(new VennMakerCellEditor(cbStartActor));

		cm.getColumn(3).setCellEditor(new MyComboBoxEditor());
		cm.getColumn(4).setCellEditor(new VennMakerCellEditor(cbEndActor));

		JScrollPane scroll = new JScrollPane(relationTable);

		// Buttons
		btnOkay = new JButton(Messages.getString("VennMaker.OK")); //$NON-NLS-1$
		btnOkay.setActionCommand("ok");
		btnOkay.addActionListener(this);

		btnCancel = new JButton(Messages.getString("Filelist.Cancel")); //$NON-NLS-1$
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);

		btnAddRelation = new JButton(Messages.getString("VennMaker.AddRelation")); //$NON-NLS-1$
		btnAddRelation.setActionCommand("addRelation");
		btnAddRelation.addActionListener(this);

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 5;
		c.weighty = 5;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(5, 5, 0, 5);
		gbl.setConstraints(scroll, c);
		panel.add(scroll);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(0, 5, 0, 15);
		gbl.setConstraints(btnAddRelation, c);
		panel.add(btnAddRelation);

		c.gridx = 1;
		c.insets = new Insets(40, 5, 0, 0);
		gbl.setConstraints(btnCancel, c);
		panel.add(btnCancel);

		c.gridx = 2;
		c.insets = new Insets(40, 5, 0, 5);
		gbl.setConstraints(btnOkay, c);
		panel.add(btnOkay);

		this.setContentPane(panel);
	}

	class RelationTableModel extends AbstractTableModel
	{
		private static final long	serialVersionUID	= 1L;

		private String[]				colHeader;

		public RelationTableModel()
		{
			colHeader = new String[6];
			colHeader[0] = Messages.getString("RelationTableDialog.Column0"); //$NON-NLS-1$
			colHeader[1] = Messages.getString("RelationTableDialog.Column1"); //$NON-NLS-1$
			colHeader[2] = Messages.getString("RelationTableDialog.Column2"); //$NON-NLS-1$
			colHeader[3] = Messages.getString("RelationTableDialog.Column3"); //$NON-NLS-1$
			colHeader[4] = Messages.getString("RelationTableDialog.Column4"); //$NON-NLS-1$
			colHeader[5] = Messages.getString("RelationTableDialog.Column5"); //$NON-NLS-1$
		}

		@Override
		public Class<?> getColumnClass(int col)
		{
			switch (col)
			{
				case 5:
					return Boolean.class;
				default:
					return JComboBox.class;
			}
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			if (tableData == null || tableData.size() < row)
			{
				return null;
			}
			return tableData.get(row)[HIDDEN_FIELDS + col];
		}

		@Override
		public int getColumnCount()
		{
			return colHeader.length;
		}

		@Override
		public String getColumnName(int col)
		{
			if (col < colHeader.length)
				return colHeader[col];
			return null;
		}

		@Override
		public int getRowCount()
		{
			if (tableData == null)
				return 0;
			return tableData.size();
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			// wenn es nicht um die erste Zeile dieser Beziehung geht wird nur ein
			// leeres Label zurueckgegeben
			boolean first = tableData.get(row)[INDEX_FIRST_ROW] != null
					&& (Boolean) tableData.get(row)[INDEX_FIRST_ROW];

			if (first)
			{
				if (col >= 1 && col <= 2)
					return false;
				return true;
			}
			else
			{
				if (col == 3)
					return true;
				return false;
			}
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			tableData.get(row)[HIDDEN_FIELDS + col] = value;
			fireTableCellUpdated(row, col);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("addRelation"))
		{
			if (actors.size() > 1)
			{

				if (relationTypes.length > 1)
				{
					/* popupmenu which contains all relationtypes */
					JPopupMenu relationTypeChooser = new JPopupMenu();

					int i = 0;
					while (i < relationTypes.length)
					{
						final String tempRelationName = relationTypes[i];
						relationTypeChooser.add(new AbstractAction(tempRelationName)
						{
							private static final long	serialVersionUID	= 1L;

							public void actionPerformed(ActionEvent e)
							{
								addRelation(tempRelationName);
							}
						});

						i++;
					}

					relationTypeChooser.setLocation(((JButton) e.getSource())
							.getLocationOnScreen());
					relationTypeChooser.setInvoker(this);
					relationTypeChooser.setVisible(true);
				}
				/*
				 * if there's only one relationtype, immediatly add this one as
				 * relation
				 */
				else
					addRelation(relationTypes[0]);

			}
			else
			{
				JOptionPane.showMessageDialog(this, Messages
						.getString("RelationTableDialog.2ActorsAtLeast"),//$NON-NLS-1$
						Messages.getString("ProjectChooser.Error"),//$NON-NLS-1$
						JOptionPane.ERROR_MESSAGE, new ImageIcon(VennMaker
								.getInstance().getIconImage()));
			}
		}
		else if (e.getActionCommand().equals("ok"))
		{
			Netzwerk net = view.getNetzwerk();
			RelationMonitor rm = RelationMonitor.getInstance();
			Relation r = null;
			Akteur startActor = null, endActor = null;
			AttributeType aType;

			if (relationTable.getCellEditor() != null)
				relationTable.getCellEditor().stopCellEditing();

			boolean delete = false;
			ComplexEvent ce = new ComplexEvent("ChangedRelationsEvent");
			for (Object[] row : tableData)
			{
				// Wenn das die erste Reihe fuer diese Relation ist, nehme alle
				// Informationen wie Start-Akteur, End-Akteur usw...
				if (row[INDEX_FIRST_ROW] != null && (Boolean) row[INDEX_FIRST_ROW])
				{
					// Alte Relation loeschen, und neue erzeugen
					Relation oldRel = (Relation) row[INDEX_RELATION];
					RemoveRelationEvent rE = new RemoveRelationEvent(
							rm.getStartActor(oldRel), net, oldRel);
					ce.addEvent(rE);

					// Wenn Relation nicht als "zu loeschen" makiert ist
					// uebernehme Informationen
					if (!(delete = (Boolean) row[INDEX_DELETE]))
					{
						startActor = (Akteur) row[INDEX_START_ACTOR];
						endActor = (Akteur) row[INDEX_END_ACTOR];

						r = new Relation(startActor,
								oldRel.getAttributeCollectorValue());
						r.setAkteur(endActor);

						aType = (AttributeType) row[INDEX_ATTR_TYPE];
						r.setAttributeValue(aType, net, row[INDEX_ATTR_VALUE]);
					}

					AddRelationEvent aE = new AddRelationEvent(startActor, net, r);
					ce.addEvent(aE);
				}
				// falls nicht die erste Zeile einer Relation und diese nicht
				// geloescht wird
				// werden nur die Attribut Werte uebernommen
				else if (!delete)
				{
					aType = (AttributeType) row[INDEX_ATTR_TYPE];
					r.setAttributeValue(aType, net, row[INDEX_ATTR_VALUE]);
				}
			}

			EventProcessor.getInstance().fireEvent(ce);
			this.dispose();
		}
		else if (e.getActionCommand().equals("cancel"))
		{
			this.dispose();
		}
	}

	/**
	 * adds a new Relation between two actors
	 * 
	 * @param chosenRelation
	 *           the relationstype, which will be added
	 */
	public void addRelation(String chosenRelation)
	{
		Relation r = new Relation(actors.get(0), chosenRelation);
		int i = 0;
		for (AttributeType a : VennMaker.getInstance().getProject()
				.getAttributeTypes(chosenRelation))
		{
			Object[] o = new Object[8];
			Object val = a.getDefaultValue();
			if (val == null)
			{
				Object[] preVals = a.getPredefinedValues();
				if (preVals != null && preVals.length > 0)
					val = preVals[0];
			}

			o[INDEX_RELATION] = r;
			if (i == 0)
			{
				o[INDEX_FIRST_ROW] = i == 0;
				o[INDEX_START_ACTOR] = actors.get(0);
				o[INDEX_RELATION_GROUP] = r.getAttributeCollectorValue();
				o[INDEX_END_ACTOR] = actors.get(1);
				o[INDEX_DELETE] = false;
			}
			o[INDEX_ATTR_TYPE] = a;
			o[INDEX_ATTR_VALUE] = val;
			i++;

			tableData.add(o);
		}
		model.fireTableDataChanged();
	}

	class ComboBoxRenderer extends JComboBox implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		// Column in Table to compare for same value
		// needed to prevent that start/end actor are the same
		private int						compareColumn;

		public ComboBoxRenderer(Object[] items, int compareColumn)
		{
			super(items);
			this.compareColumn = compareColumn;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			if (compareColumn >= 0)
			{
				Object o = table.getValueAt(row, compareColumn);
				if (o instanceof Akteur)
				{
					Akteur toCompare = (Akteur) o;

					// if both items would be the same after the change
					// switch the values to prevent it
					if (toCompare.getId() == ((Akteur) value).getId())
						table.setValueAt(super.getSelectedItem(), row, compareColumn);
				}
			}
			// wenn es nicht um die erste Zeile dieser Beziehung geht wird nur ein
			// leeres Label zurueckgegeben
			if (tableData.get(row)[INDEX_FIRST_ROW] == null
					|| !(Boolean) tableData.get(row)[INDEX_FIRST_ROW])
				return new JLabel("");

			this.setSelectedItem(value);
			return this;
		}
	}

	class AttributeValueRenderer extends JComboBox implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		public AttributeValueRenderer()
		{
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			AttributeType a = (AttributeType) tableData.get(row)[INDEX_ATTR_TYPE];
			Object[] preVals = a.getPredefinedValues();
			if (preVals == null || preVals.length <= 0)
			{
				String content = "";
				if (value != null)
					content = value.toString();
				return new JLabel(content);
			}

			this.removeAllItems();
			for (Object val : preVals)
				this.addItem(val);
			this.setSelectedItem(value);

			return this;
		}
	}

	class DeleteCellRenderer extends JCheckBox implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		public DeleteCellRenderer()
		{
			super();
			setOpaque(true);
			this.setBackground(Color.white);
			setHorizontalAlignment(SwingConstants.CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row, int col)
		{
			if (tableData.get(row)[INDEX_FIRST_ROW] != null
					&& (Boolean) tableData.get(row)[INDEX_FIRST_ROW])
			{
				this.setSelected((Boolean) tableData.get(row)[INDEX_DELETE]);
				return this;
			}
			/* don't return NULL, it leads to a NPE; return empty JLabel instead */
			return new JLabel("");
		}
	}

	class MyComboBoxEditor extends DefaultCellEditor
	{
		private static final long	serialVersionUID	= 1L;

		private JTextField			tf;

		private JComboBox				cb;

		public MyComboBoxEditor()
		{
			super(new JComboBox());
			tf = new JTextField();
			cb = new JComboBox();
			cb.setEditable(false);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			AttributeType att = (AttributeType) tableData.get(row)[INDEX_ATTR_TYPE];
			Object[] vals = null;
			if (att != null)
				vals = att.getPredefinedValues();

			// Textfield
			if (vals == null || vals.length <= 0)
			{
				tf.setText("");
				// if (value != null)
				// tf.setText(value.toString());

				editorComponent = tf;
			}
			// ComboBox
			else
			{
				cb.removeAllItems();
				for (Object o : vals)
					cb.addItem(o);
				cb.setSelectedItem(value);
				editorComponent = cb;
			}

			return editorComponent;
		}

		public Object getCellEditorValue()
		{
			if (editorComponent instanceof JComboBox)
				return ((JComboBox) editorComponent).getSelectedItem();

			return tf.getText();
		}
	}
}
