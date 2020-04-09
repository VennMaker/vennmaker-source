package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import data.Akteur;
import data.AttributeMouseListener;
import data.AttributeSubject;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import events.SetAttributeEvent;
import files.FileOperations;

/**
 * Dialog zur Anzeige und Bearbeitung von Attributen.
 * 
 * 
 */
public class EditAttributes extends JDialog implements ActionListener
{
	private static final long	serialVersionUID	= 1L;

	private Netzwerk				network;

	private AttributeSubject	subject;

	private JTable					table;

	private MyTableModel			model;

	private String					getType				= "ACTOR";

	public EditAttributes(JFrame parent, boolean modal,
			final AttributeSubject subject, final Netzwerk network,
			final String getType)
	{
		super(parent, modal);
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		if (getType.equals("ACTOR"))
		{
			Akteur akteur = (Akteur) subject;
			this.setTitle(akteur.getName()
					+ ": " + Messages.getString("EditAttributes.3")); //$NON-NLS-1$
		}
		else
			this.setTitle(Messages.getString("EditAttributes.3")); //$NON-NLS-1$
		this.subject = subject;
		this.network = network;
		this.getType = getType;

		this.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$

		GridBagConstraints gbc;

		int zeile = 0;

		model = new MyTableModel();
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(500, 300));
		table.setFillsViewportHeight(true);
		table.setRowHeight(20);

		table.getColumnModel().getColumn(1).setCellEditor(new MyComboBoxEditor());
		table.putClientProperty("cancelEditOnFocusLost", true);

		table.setCellSelectionEnabled(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDragEnabled(true);

		table.setDropTarget(new DropTarget()
		{
			private static final long	serialVersionUID	= 1L;

			public void drop(DropTargetDropEvent dtde)
			{
				Point point = dtde.getLocation();
				int row = table.rowAtPoint(point);
				this.switchRows(row, table.getSelectedRow());
				// handle drop inside current table
				super.drop(dtde);
			}

			private void switchRows(int row, int selectedRow)
			{
				Vector<AttributeType> att = VennMaker.getInstance().getProject()
						.getAttributeTypes();
				Vector<AttributeType> typeAttributes = VennMaker.getInstance()
						.getProject().getAttributeTypes(getType);

				if (row == selectedRow || row < 0 || selectedRow < 0
						|| row >= typeAttributes.size()
						|| selectedRow >= typeAttributes.size())
					return;

				AttributeType at = typeAttributes.get(selectedRow);
				AttributeType toChange = typeAttributes.get(row);

				att.remove(at);
				int insertBefore = (selectedRow <= row) ? 1 : 0;
				att.add(att.indexOf(toChange) + insertBefore, at);

				VennMaker.getInstance().getProject().setAttributeTypes(att);
				model.fireTableDataChanged();
			}
		});

		table.addMouseListener(new AttributeMouseListener(1, 1));

		JScrollPane scrollPane = new JScrollPane(table);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 6;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		layout.setConstraints(scrollPane, gbc);
		add(scrollPane);

		zeile++;

		JButton b1 = new JButton(Messages.getString("BackgroundConfig.OK")); //$NON-NLS-1$
		b1.setActionCommand("ok"); //$NON-NLS-1$
		b1.addActionListener(this);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(10, 20, 10, 20);
		layout.setConstraints(b1, gbc);
		add(b1);

		getRootPane().setDefaultButton(b1);
		pack();

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		setVisible(true);
	}

	public void actionPerformed(ActionEvent event)
	{
		if (table.getCellEditor() != null)
		{
			table.getCellEditor().stopCellEditing();
		}
		if ("ok".equals(event.getActionCommand())) //$NON-NLS-1$
			this.dispose();
	}

	class MyTableModel extends AbstractTableModel
	{
		static final long	serialVersionUID	= 1L;

		public MyTableModel()
		{
		}

		public int getColumnCount()
		{
			return 2;
		}

		public int getRowCount()
		{
			return VennMaker.getInstance().getProject().getAttributeTypes(getType)
					.size();
		}

		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = Messages.getString("EditAttributes.1"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("EditAttributes.2"); //$NON-NLS-1$
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

					r = VennMaker.getInstance().getProject()
							.getAttributeTypes(getType).get(row).getQuestion();

					if (r == null)
						r = VennMaker.getInstance().getProject()
								.getAttributeTypes(getType).get(row);
					else if (r.toString().length() == 0)
						r = VennMaker.getInstance().getProject()
								.getAttributeTypes(getType).get(row);

					break;

				case 1:
					AttributeType att = VennMaker.getInstance().getProject()
							.getAttributeTypes(getType).get(row);
					Object[] values = att.getPredefinedValues();
					r = subject
							.getAttributeValue(VennMaker.getInstance().getProject()
									.getAttributeTypes(getType).get(row), network);
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
				case 0:
					r = AttributeType.class;
					break;
				default:
					r = Object.class;
					break;
			}
			return r;
		}

		public boolean isCellEditable(int row, int col)
		{
			AttributeType at = VennMaker.getInstance().getProject()
					.getAttributeTypes(getType).get(row);
			AttributeType circleAttrib = VennMaker.getInstance().getProject()
					.getCurrentNetzwerk().getHintergrund().getCircleAttribute();
			AttributeType sectorAttrib = VennMaker.getInstance().getProject()
					.getCurrentNetzwerk().getHintergrund().getSectorAttribute();
			if ((circleAttrib != null && at.equals(circleAttrib))
					|| (sectorAttrib != null && at.equals(sectorAttrib)))
				return false;
			switch (col)
			{
				case 0:
					return false;
				default:
					return true;
			}
		}

		public void setValueAt(Object value, int row, int col)
		{
			EventProcessor.getInstance().fireEvent(
					new SetAttributeEvent(subject, VennMaker.getInstance()
							.getProject().getAttributeTypes(getType).get(row),
							network, value));
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
			AttributeType att = VennMaker.getInstance().getProject()
					.getAttributeTypes(getType).get(row);
			
			Object[] vals = null;
			
			if (att != null){
				vals = att.getPredefinedValues();
			}

			// Textfield
			if (vals == null || vals.length <= 0)
			{
				/*
				 * Intern bereits gesichert gegen berschreiben beim editieren,
				 * setze Feld leer, falls gecancelt. Wird alte wert wieder
				 * hergestellt
				 */
				String text = "";
				if ((subject != null) && (att != null))
				{
					Object tmp = subject.getAttributeValue(att, network);
					if (tmp != null)
						text = tmp.toString();
				}
				tf.setText(text);

				editorComponent = tf;
			}
			// ComboBox
			else
			{
				cb.removeAllItems();
				for (Object o : vals)
					cb.addItem(o);
				cb.setSelectedItem(subject.getAttributeValue(att, network));
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
