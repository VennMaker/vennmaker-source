/**
 * 
 */
package gui;

import gui.configdialog.elements.CDialogActorPie;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import data.Akteur;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import events.ComplexEvent;
import events.NewActorEvent;

/**
 * Importing data
 * 
 * 
 * 
 */
public class ImportData extends JDialog
{
	private static final long							serialVersionUID	= 1L;

	private Map<AttributeType, Object>				typeAndSelection;

	private Map<AttributeType, Object>				sectorColor;

	private Map<AttributeType, Vector<Object>>	auswahl;

	private Netzwerk										network;

	private ArrayList<CDialogActorPie>				actorPie;

	private JTable											table;

	private MyTableModel									model;

	private VennMakerView								view;

	private JPanel											windowPanel;

	private JScrollPane									tableScrollPane;

	private Vector<AttributeType>						attributeType;

	/**
	 * Singleton: Referenz.
	 */
	private static ImportData							instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige StatisticDialog-Instanz in diesem Prozess.
	 */
	public static ImportData showImportDataDialog()
	{
		if (instance == null)
		{
			instance = new ImportData();
		}
		return instance;
	}

	private JTextArea	dataField;

	private JLabel		infoLabel;

	/**
	 * Import the data from a text field window. Each line represents an actor.
	 */

	private ImportData()
	{

		super(VennMaker.getInstance(), true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);

		this.addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent arg0)
			{
				instance = null;
			}

			public void windowActivated(WindowEvent arg0)
			{
			}

			public void windowClosing(WindowEvent arg0)
			{
			}

			public void windowDeactivated(WindowEvent arg0)
			{

			}

			public void windowDeiconified(WindowEvent arg0)
			{
			}

			public void windowIconified(WindowEvent arg0)
			{
			}

			public void windowOpened(WindowEvent arg0)
			{
			}
		});

		/*
		 * Auswahltabelle
		 */

		network = VennMaker.getInstance().getProject().getCurrentNetzwerk();

		sectorColor = new HashMap<AttributeType, Object>(network
				.getActorSectorVisualizer().getSectorColor());

		typeAndSelection = new HashMap<AttributeType, Object>();

		// Auswahlliste neu aufbauen und mit einer weiteren Auswahl versehen
		auswahl = new HashMap<AttributeType, Vector<Object>>();

		attributeType = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");

		for (AttributeType o : attributeType)
		{
			Vector<Object> tmp = new Vector<Object>();
			tmp.add(""); //$NON-NLS-1$

			// If Attribute has no Predifined Values add Value --> ""
			if (o.getPredefinedValues() != null)
				for (Object t : o.getPredefinedValues())
					tmp.add(t);

			auswahl.put(o, tmp);
			if (sectorColor.get(o) == null)
				sectorColor.put(o, new Color(255, 255, 255));
		}

		/**
		 * Tabelle fuer Categories (predefined values)
		 */

		model = new MyTableModel();

		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(300, 300));
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getColumnModel().getColumn(0).setPreferredWidth(150);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);

		table.setRowHeight(20);

		table.getColumnModel().getColumn(1)
				.setCellEditor(new MyComboBoxEditor(attributeType));

		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());

		tableScrollPane = new JScrollPane(table);
		// ----------

		Container contentPane = this.getContentPane();

		setTitle(Messages.getString("ImportData.1")); //$NON-NLS-1$

		JPanel infoPanel = new JPanel();
		infoPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		FlowLayout infoFlowLayout = new FlowLayout();
		infoPanel.setLayout(infoFlowLayout);

		infoLabel = new JLabel("<html><body>" //$NON-NLS-1$
				+ Messages.getString("ImportData.2") + "</body></html>"); //$NON-NLS-1$ //$NON-NLS-2$
		infoLabel.setPreferredSize(new Dimension(300, 30));

		infoPanel.add(infoLabel);

		contentPane.add(infoPanel);

		dataField = new JTextArea("", 25, 20); //$NON-NLS-1$
		dataField.setEditable(true);

		JScrollPane dataFieldPane = new JScrollPane(dataField);

		contentPane.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		contentPane.add(dataFieldPane);

		contentPane.add(tableScrollPane);

		JPanel clipboardPanel = new JPanel();
		infoPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		FlowLayout clipboardFlowLayout = new FlowLayout();
		clipboardPanel.setLayout(clipboardFlowLayout);
		final JButton clipboardButton = new JButton(
				Messages.getString("ImportData.5")); //$NON-NLS-1$
		clipboardPanel.add(clipboardButton);
		contentPane.add(clipboardPanel);

		JPanel buttonPane = new JPanel();
		final JButton cancelButton = new JButton(
				Messages.getString("ImportData.4")); //$NON-NLS-1$
		final JButton importButton = new JButton(
				Messages.getString("ImportData.3")); //$NON-NLS-1$

		buttonPane.add(importButton);

		buttonPane.add(cancelButton);

		contentPane.add(buttonPane);

		/*
		 * Start Import button
		 */
		importButton.addActionListener(new ActionListener()
		{

			/*
			 * Read the data from the input text windows
			 */
			public void actionPerformed(final ActionEvent e)
			{
				/* stop editing, or else the last edited value won't be applied */
				if (table.getCellEditor() != null) 
					table.getCellEditor().stopCellEditing();

				final Netzwerk network = VennMaker.getInstance().getProject()
						.getCurrentNetzwerk();

				// Each line represents an actor
				StringTokenizer st = new StringTokenizer(dataField.getText(),
						"\r\n"); //$NON-NLS-1$

				while (st.hasMoreTokens())
				{

					Akteur actor = new Akteur(""); //$NON-NLS-1$

					int row = 0;
					for (AttributeType a : attributeType)
					{
						if (!model.getValueAt(row, 1).equals("")) //$NON-NLS-1$
							actor.setAttributeValue(a, network,
									model.getValueAt(row, 1));
						row++;
					}

					/*
					 * Object value =
					 * VennMaker.getInstance().getMainGeneratorValue(); assert (value
					 * != null);
					 * 
					 * actor.setAttributeValue(VennMaker.getInstance().getProject()
					 * .getMainGeneratorType(), VennMaker.getInstance()
					 * .getProject().getCurrentNetzwerk(), value);
					 */
					actor.setName(st.nextToken());

					// Adding allowed!
					ComplexEvent event = new ComplexEvent(Messages
							.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
					event.addEvent(new NewActorEvent(actor));
					EventProcessor.getInstance().fireEvent(event);

					setVisible(false);
					instance = null;
					dispose();
				}
			}
		});

		// Close button
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				setVisible(false);
				instance = null;
				dispose();
			}
		});

		// clipboardButton
		clipboardButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{

				Clipboard systemClipboard = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Transferable transferData = systemClipboard.getContents(null);

				for (DataFlavor dataFlavor : transferData.getTransferDataFlavors())
				{
					Object content = null;
					try
					{
						content = transferData
								.getTransferData(DataFlavor.stringFlavor);
					} catch (UnsupportedFlavorException exn)
					{
						// TODO Auto-generated catch block
						exn.printStackTrace();
					} catch (IOException exn)
					{
						// TODO Auto-generated catch block
						exn.printStackTrace();
					}

					if (content instanceof String)
					{
						dataField.append((String) content);
						break;
					}
				}
			}
		});

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setModal(false);
		pack();

		int top = (screenSize.height - this.getHeight()) / 2;
		int left = (screenSize.width - this.getWidth()) / 2;

		setLocation(left, top);
		setVisible(true);

	}

	/*
	 * class MyComboBoxEditor extends DefaultCellEditor { private static final
	 * long serialVersionUID = 1L;
	 * 
	 * private Map<AttributeType, Vector<Object>> selection;
	 * 
	 * public MyComboBoxEditor(Map<AttributeType, Vector<Object>> t) { super(new
	 * JComboBox()); selection = t; }
	 * 
	 * public Component getTableCellEditorComponent(JTable table, Object value,
	 * boolean isSelected, int row, int column) { delegate.setValue(value);
	 * 
	 * ((JComboBox) editorComponent).removeAllItems();
	 * 
	 * if (column == 1) { Vector<Object> preVals =
	 * auswahl.get(attributeType.get(row)); if ( preVals != null &&
	 * preVals.size()>0 ) { for (Object o : preVals) ((JComboBox)
	 * editorComponent).addItem(o); ((JComboBox)
	 * editorComponent).setEditable(false); } else ((JComboBox)
	 * editorComponent).setEditable(true);
	 * 
	 * }
	 * 
	 * return editorComponent; }
	 * 
	 * }
	 */

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
			return attributeType.size();
		}

		public String getColumnName(int col)
		{
			String r = null;
			switch (col)
			{
				case 0:
					r = Messages.getString("ImportData.0"); //$NON-NLS-1$
					break;

				case 1:
					r = Messages.getString("ImportData.8"); //$NON-NLS-1$
					break;

				default:
					break;
			}
			return r;
		}

		public Object getValueAt(int row, int col)
		{
			Object r = null;
			switch (col)
			{
				case 0:
					r = attributeType.get(row);
					break;

				case 1:
					Object tmp = typeAndSelection.get(attributeType.get(row));
					if (tmp != null)
						r = tmp;
					else
						r = ""; //$NON-NLS-1$

					break;

				default:
					break;
			}

			return r;
		}

		public Class<?> getColumnClass(int col)
		{
			return getValueAt(0, col).getClass();

		}

		public boolean isCellEditable(int row, int col)
		{
			switch (col)
			{

				case 1:
					return true;

				default:
					return false;
			}
		}

		public void setValueAt(Object v, int row, int col)
		{
			switch (col)
			{

				case 1:
					if (v.equals("")) //$NON-NLS-1$
					{
						typeAndSelection.remove(attributeType.get(row));
					}
					else
						typeAndSelection.put(attributeType.get(row), v);

					break;

				default:
					break;

			}
			fireTableDataChanged();

		}

	}

	public class ColorRenderer extends JLabel implements TableCellRenderer
	{
		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		Border							unselectedBorder	= null;

		Border							selectedBorder		= null;

		boolean							isBordered			= true;

		public ColorRenderer(boolean isBordered)
		{
			this.isBordered = isBordered;
			setOpaque(true); // MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table,
				Object color, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			Color newColor = (Color) color;
			setBackground(newColor);
			if (isBordered)
			{
				if (isSelected)
				{
					if (selectedBorder == null)
					{
						selectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5,
								table.getSelectionBackground());
					}
					setBorder(selectedBorder);
				}
				else
				{
					if (unselectedBorder == null)
					{
						unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
								5, table.getBackground());
					}
					setBorder(unselectedBorder);
				}
			}

			return this;
		}
	}
}

class MyComboBoxEditor extends DefaultCellEditor
{
	private static final long		serialVersionUID	= 1L;

	private JTextField				tf;

	private JComboBox					cb;

	private Vector<AttributeType>	attributeType;

	public MyComboBoxEditor(Vector<AttributeType> attributeType)
	{
		super(new JComboBox());
		this.attributeType = attributeType;
		tf = new JTextField();
		cb = new JComboBox();
		cb.setEditable(false);
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		AttributeType att = attributeType.get(row);
		Object[] vals = att.getPredefinedValues();

		// Textfield
		if (vals == null || vals.length <= 0)
		{
			if (value != null)
				tf.setText(value.toString());
			editorComponent = tf;
		}
		// ComboBox
		else
		{
			cb.removeAllItems();
			for (Object o : vals)
				cb.addItem(o);
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
