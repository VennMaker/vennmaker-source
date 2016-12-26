/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import data.Akteur;
import data.AttributeMouseListener;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import events.ComplexEvent;
import events.NewActorEvent;
import events.RenameActorEvent;
import events.SetAttributeEvent;
import files.FileOperations;

/**
 * 
 * 
 *         Ermoeglicht das Erstellen/Editieren von Akteuren (Namen und
 *         Attributwerte)
 * 
 */
public class ActorTableDialog extends JDialog implements AdjustmentListener,
		TableColumnModelListener, ActionListener
{
	private static final long			serialVersionUID	= 1L;

	private Netzwerk						net;

	private JTable							actorNamesTable;

	private JTable							attributeValueTable;

	private VennMakerView				view;

	private JPanel							jspPanel;

	private JScrollBar					attributeTypesHorizontal;

	private JScrollBar					attributeValuesHorizontal;

	private JScrollBar					attributeValuesVertical;

	private JScrollBar					actorNamesVertical;

	private TableColumnModel			cmNames;

	private TableColumnModel			cmVals;

	private ActorDataBase				adb;

	private ActorsNameTableModel		nameM;

	private AttributeValueTableModel	valM;

	private JTableHeader					th1, th2;

	public ActorTableDialog(VennMakerView view, Netzwerk net)
	{

		this.net = net;
		this.view = view;
		this.setSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		this.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$
		this.setTitle(Messages.getString("ActorLabelDialog.0")); //$NON-NLS-1$
		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel labelPanel = new JPanel(new GridLayout(3, 0));
		JLabel placeholder1 = new JLabel(""); //$NON-NLS-1$
		JLabel descLabel = new JLabel(Messages.getString("ActorLabelDialog.1")); //$NON-NLS-1$
		JLabel descLabel2 = new JLabel(Messages.getString("ActorLabelDialog.3")); //$NON-NLS-1$

		labelPanel.add(placeholder1);
		labelPanel.add(descLabel);
		labelPanel.add(descLabel2);

		add(labelPanel, BorderLayout.NORTH);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		jspPanel = new JPanel(new BorderLayout());

		jspPanel.add(buildJSP(), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("Ok"); //$NON-NLS-1$
		JButton cancelButton = new JButton(
				Messages.getString("ActorLabelDialog.2")); //$NON-NLS-1$
		JButton addActorButton = new JButton(
				Messages.getString("VennMaker.AddActor")); //$NON-NLS-1$

		addActorButton.setActionCommand("addActor");
		okButton.setActionCommand("ok"); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel"); //$NON-NLS-1$

		addActorButton.addActionListener(this);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		buttonPanel.add(addActorButton);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		this.add(buttonPanel, BorderLayout.SOUTH);

		this.add(jspPanel, BorderLayout.CENTER);

		setVisible(true);
	}

	private JSplitPane buildJSP()
	{
		Object[] actors = net.getAkteure().toArray();
		Arrays.sort(actors);
		Object[] tmpActors = new Object[net.getTemporaryActorSize()];

		for (int i = 0; i < net.getTemporaryActorSize(); i++)
		{
			tmpActors[i] = net.getTemporaryActorAt(i);
		}
		Arrays.sort(tmpActors);

		List<Akteur> allActors = new ArrayList<Akteur>();

		for (Object o : actors)
			allActors.add((Akteur) o);
		for (Object o : tmpActors)
			allActors.add((Akteur) o);

		adb = new ActorDataBase(net, allActors);

		nameM = new ActorsNameTableModel();
		valM = new AttributeValueTableModel();

		actorNamesTable = new JTable(nameM);
		attributeValueTable = new JTable(valM);

		// prepare Table Header for Sorting and add a Renderer
		final OrderedHeaderRenderer ren1 = new OrderedHeaderRenderer(0);
		th1 = actorNamesTable.getTableHeader();
		th1.setReorderingAllowed(false);
		th1.setDefaultRenderer(ren1);

		final OrderedHeaderRenderer ren2 = new OrderedHeaderRenderer(1);
		th2 = attributeValueTable.getTableHeader();
		th2.setReorderingAllowed(false);
		th2.setDefaultRenderer(ren2);

		th1.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				adb.sortByNames();

				ren1.setAsc(adb.isAsc());
				ren1.ordered(true);
				ren2.ordered(false);
				refresh();
			}
		});

		th2.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int col = attributeValueTable.columnAtPoint(e.getPoint());
				/* idcolumn clicked */
				if (col == 0)
				{
					adb.sortById();
					ren1.ordered(false);
					ren2.setAsc(adb.isAsc());
					ren2.ordered(true);

					refresh();
				}
				/* attributecolumn clicked */
				else
				{
					adb.sortByAttribute(col);
					ren1.ordered(false);
					ren2.setAsc(adb.isAsc());
					ren2.ordered(true);

					refresh();
				}
			}
		});

		adb.sortById();
		ren1.ordered(false);
		ren2.setAsc(adb.isAsc());
		ren2.ordered(true);

		final JScrollPane attributeValuePanel = new JScrollPane(
				attributeValueTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		final JScrollPane actorNamesPanel = new JScrollPane(actorNamesTable,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane scrollAttributeValuePanel = new JScrollPane(
				attributeValuePanel);
		scrollAttributeValuePanel.setPreferredSize(new Dimension(300, 600));

		attributeTypesHorizontal = actorNamesPanel.getHorizontalScrollBar();

		attributeValuesHorizontal = attributeValuePanel.getHorizontalScrollBar();
		attributeValuesVertical = attributeValuePanel.getVerticalScrollBar();
		actorNamesVertical = actorNamesPanel.getVerticalScrollBar();

		attributeTypesHorizontal.addAdjustmentListener(this);
		attributeValuesHorizontal.addAdjustmentListener(this);

		actorNamesVertical.addAdjustmentListener(this);
		attributeValuesVertical.addAdjustmentListener(this);

		List<AttributeType> aTypes = adb.getAttributeTypes();
		TableColumnModel cm = attributeValueTable.getColumnModel();
		for (int i = 0; i < aTypes.size(); i++)
		{
			Component editorBox = createEditorBox(aTypes.get(i));

			/*
			 * different editors for different types; if there are predefined
			 * values, use combobox, else use textfield
			 */
			if (editorBox instanceof JComboBox)
				cm.getColumn(i + 1).setCellEditor(
				// new DefaultCellEditor((JComboBox) editorBox));
				// new ActorTableCellEditor((JComboBox) editorBox));
						new VennMakerCellEditor((JComboBox) editorBox));

			else
				cm.getColumn(i + 1).setCellEditor(
				// new DefaultCellEditor((JTextField) editorBox));
				// new ActorTableCellEditor((JTextField) editorBox));
						new VennMakerCellEditor((JTextField) editorBox));
		}

		actorNamesTable.setColumnSelectionAllowed(false);
		actorNamesTable.setRowSelectionAllowed(true);

		attributeValueTable.setColumnSelectionAllowed(false);
		attributeValueTable.setRowSelectionAllowed(true);

		attributeValueTable.addMouseListener(new AttributeMouseListener(1,
				attributeValueTable.getColumnCount() - 1));

		actorNamesTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent arg0)
					{
						int row = actorNamesTable.getSelectedRow();
						if (row >= 0)
							attributeValueTable.setRowSelectionInterval(row, row);
					}

				});

		attributeValueTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						int row = attributeValueTable.getSelectedRow();
						if (row >= 0)
							actorNamesTable.setRowSelectionInterval(row, row);
					}

				});

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
				actorNamesPanel, attributeValuePanel);
		jsp.setDividerLocation(160 + jsp.getInsets().left);

		return jsp;
	}

	public Netzwerk getNet()
	{
		return net;
	}

	public void setNet(Netzwerk net)
	{
		this.net = net;
	}

	private void refresh()
	{
		nameM.fireTableDataChanged();
		valM.fireTableDataChanged();

		th1.resizeAndRepaint();
		th2.resizeAndRepaint();
	}

	/**
	 * Wird ausgeführt wenn die Änderungen in den Tabellen übernommen werden
	 * sollen
	 */
	public void acceptChanges()
	{
		// fire events for all renamed actors
		{
			adb.acceptChanges();
			adb.clear();
		}

		view.updateView();
		view.revalidate();
		VennMaker.getInstance().refresh();
		VennMaker.getInstance().repaint();
	}

	/**
	 * Diese Methode sorgt dafür, dass sich gegenüberliegende ScrollBars
	 * synchron verschieben
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		JScrollBar sourceBar = (JScrollBar) e.getSource();

		if (sourceBar == attributeTypesHorizontal)
		{
			attributeValuesHorizontal
					.setValue(attributeTypesHorizontal.getValue());
		}
		else if (sourceBar == attributeValuesHorizontal)
		{
			attributeTypesHorizontal
					.setValue(attributeValuesHorizontal.getValue());
		}
		else if (sourceBar == actorNamesVertical)
		{
			attributeValuesVertical.setValue(actorNamesVertical.getValue());
		}
		else if (sourceBar == attributeValuesVertical)
		{
			actorNamesVertical.setValue(attributeValuesVertical.getValue());
		}
	}

	@Override
	public void columnAdded(TableColumnModelEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Sorgt dafür, dass beim Einstellen der Spalten-Breite die Tabelle
	 * "attributeValueTable" ebenfalls die Breite ändert
	 */
	@Override
	public void columnMarginChanged(ChangeEvent arg0)
	{
		for (int i = 0; i < cmNames.getColumnCount(); i++)
		{
			cmNames.getColumn(i).setPreferredWidth(cmVals.getColumn(i).getWidth());
		}
	}

	@Override
	public void columnMoved(TableColumnModelEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void columnRemoved(TableColumnModelEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void columnSelectionChanged(ListSelectionEvent arg0)
	{
		// TODO Auto-generated method stub
	}

	/**
	 * Erstellt die ComboBoxen für den CellEditor
	 */
	private Component createEditorBox(AttributeType type)
	{

		if (type.getPredefinedValues() != null)
		{
			JComboBox valueBox = new JComboBox();
			valueBox.setEditable(false);
			for (Object obj : type.getPredefinedValues())
			{
				valueBox.addItem(obj);
			}
			return valueBox;
		}
		else
		{
			JTextField valueBox = new JTextField();
			// valueBox.setEditable(true);
			return valueBox;
		}

	}

	private boolean isDup(String newName)
	{
		boolean isDup = false;
		for (Akteur a : VennMaker.getInstance().getProject().getAkteure())
		{
			String name = a.getName();
			/**
			 * wurde sein name geaendert? dann geaenderten namen verwenden
			 */

			if (name.equals(newName))
			{
				isDup = true;
				break;
			}
		}
		return isDup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("ok")) //$NON-NLS-1$
		{
			acceptChanges();
			this.dispose();
		}
		else if (e.getActionCommand().equals("addActor"))
		{
			int val;
			String result = null;
			do
			{
				val = JOptionPane.YES_OPTION;
				result = JOptionPane.showInputDialog(this,
						Messages.getString("VennMaker.Actor_Naming"), result); //$NON-NLS-1$
				if (result != null && result.length() > 0)
				{
					if (isDup(result))
					{
						switch (VennMaker.getInstance().getConfig()
								.getDuplicateBehaviour())
						{
							case NOT_ALLOWED:
								JOptionPane.showMessageDialog(null,
										Messages.getString("VennMaker.No_Duplicate")); //$NON-NLS-1$
								return;
							case ALLOWED:
								break;
							case ASK_USER:
								val = JOptionPane
										.showConfirmDialog(
												null,
												Messages
														.getString("VennMaker.Already_Same"), Messages //$NON-NLS-1$
														.getString("VennMaker.Duplicate_Actor"), //$NON-NLS-1$
												JOptionPane.YES_NO_OPTION);
								if (val == JOptionPane.CLOSED_OPTION)
									return;
							default:
								assert (false);
						}
					}
				}
				else
					return;
			} while (val == JOptionPane.NO_OPTION);

			Akteur akteur = new Akteur(result);
			EventProcessor.getInstance().fireEvent(new NewActorEvent(akteur));
			net.addTemporaryActor(akteur);

			adb.addAkteur(akteur);
			refresh();
		}
		else
		{
			this.dispose();
		}
	}

	/**
	 * Renderer for Table Header Column String + ascending/descending/no - order
	 * Icon
	 * 
 * 
	 */
	class OrderedHeaderRenderer extends JLabel implements TableCellRenderer
	{
		private static final long	serialVersionUID	= 1L;

		private boolean				ordered				= false;

		private boolean				asc					= true;

		private Icon					ascIcon, descIcon;

		private int						colOffset;

		public OrderedHeaderRenderer(int colOffset)
		{
			this.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			this.setHorizontalTextPosition(LEFT);
			this.colOffset = colOffset;
			ascIcon = UIManager.getIcon("Table.ascendingSortIcon");
			descIcon = UIManager.getIcon("Table.descendingSortIcon");
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			this.setText(value.toString());
			if (ordered && adb.orderedByCol() == (column + colOffset))
				this.setIcon(asc ? ascIcon : descIcon);
			else
				this.setIcon(null);

			return this;
		}

		public void setAsc(boolean asc)
		{
			this.asc = asc;
		}

		public void ordered(boolean ordered)
		{
			this.ordered = ordered;
		}

	}

	/**
	 * Base Table Model to work with ActorDataBase. Provides base functionality
	 * for the real table models (ActorsNameTableModel &
	 * AttributeValueTableModel)
	 * 
 * 
	 * 
	 */
	abstract class ActorDataBaseModel extends DefaultTableModel
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public int getRowCount()
		{
			return adb.getRowCount();
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			switch (col)
			{
				case 1:
					return false;
				default:
					return true;
			}
		}

		@Override
		public String getColumnName(int col)
		{
			return adb.getHeader()[col];
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return adb.getValueAt(row, col);
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			adb.setValueAt(value, row, col);
		}
	}

	/**
	 * table model displaying actor names
	 * 
 * 
	 */
	class ActorsNameTableModel extends ActorDataBaseModel
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public int getColumnCount()
		{
			return 1;
		}
	}

	/**
	 * table model displaying attribute values and the id of an actor
	 * 
 * 
	 */
	class AttributeValueTableModel extends ActorDataBaseModel
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public int getColumnCount()
		{
			return adb.getColumnCount() - 1; // all cols without name col
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			super.setValueAt(value, row, col + 1); // translate col index +1
																// because of name col (col 0)
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return super.getValueAt(row, col + 1); // translate col index +1
																// because of name col (col 0)
		}

		@Override
		public String getColumnName(int col)
		{
			return super.getColumnName(col + 1); // translate col index +1 because
																// of name col (col 0)
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return col != 0; // all cols editable instead of col 0 (ID)
		}
	}

	/**
	 * Editor f�r editierbare Zellen in der ActorTable, um zus�tzliche
	 * funktionalit�t zu implementieren.
	 */
	class ActorTableCellEditor extends DefaultCellEditor
	{

		private static final long	serialVersionUID	= 1L;

		public ActorTableCellEditor(JTextField tf)
		{
			super(tf);
		}

		public ActorTableCellEditor(JComboBox cb)
		{
			super(cb);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			// If cell's component JTextField, set displayed text to ""
			if (editorComponent instanceof JTextField)
			{
				((JTextField) editorComponent).setText("");
			}
			else
			{ // JComboBox
				if (((JComboBox) editorComponent).getSelectedItem() != value)
				{
					((JComboBox) editorComponent).setSelectedItem(value);

				}
			}
			return editorComponent;
			// return super.getTableCellEditorComponent(table, value, isSelected,
			// row, column);
		}

		@Override
		public Object getCellEditorValue()
		{
			if (editorComponent instanceof JComboBox)
			{
				return super.getCellEditorValue();
			}
			return ((JTextField) editorComponent).getText();
		}
	}
}

/**
 * saves all changes and information about one actor and can apply its changes
 * to name and attribute values.
 * 
 * 
 */
class ActorData
{
	public Akteur								originalActor;

	public final int							Id;

	public String								Name;				// can be changed

	private Map<AttributeType, Object>	attributeValues;	// can be changed

	private List<AttributeType>			changedValues;

	private Netzwerk							net;

	public ActorData(Akteur a, Netzwerk net)
	{
		this.originalActor = a;
		this.net = net;
		this.Id = a.getId();
		this.Name = a.getName();
		changedValues = new ArrayList<AttributeType>();
		this.attributeValues = a.getAttributes(net);
	}

	public Object getAttributeValue(AttributeType a)
	{
		return attributeValues.get(a);
	}

	public void setAttributeValue(AttributeType a, Object val)
	{
		Object oldVal = attributeValues.get(a);
		if ((oldVal == null && val != null)
				|| (oldVal != null && !oldVal.equals(val)))
		{
			if (changedValues.indexOf(a) == -1)
				changedValues.add(a);
			attributeValues.put(a, val);
		}
	}

	/**
	 * This method fires an rename event if this actor has been renamed and does
	 * nothing if not.
	 */
	public void applyNewName()
	{
		if (Name != null && !Name.equals(originalActor.getName()))
		{
			EventProcessor.getInstance().fireEvent(
					new RenameActorEvent(originalActor, Name));
		}
		else
			Name = originalActor.getName(); // ensure that Name for sure contains a
														// name
	}

	public void applyAttributeChanges()
	{
		if (changedValues.size() > 0)
		{
			ComplexEvent ce = new ComplexEvent("set attributes for actor :" + Name);

			for (AttributeType a : changedValues)
				ce.addEvent(new SetAttributeEvent(originalActor, a, net,
						attributeValues.get(a)));

			EventProcessor.getInstance().fireEvent(ce);
		}
	}

}

/**
 * a database for:<br>
 * - managing changes on actor names and attribute values<br>
 * - sorting of actors (by Name and ID)<br>
 * - firing final events<br>
 * <br>
 * database behind both of the tables in the ActorTableDialog
 * 
 * 
 * 
 * 
 */
class ActorDataBase
{
	private List<ActorData>			actors;

	private List<AttributeType>	aTypes;

	private String[]					header;

	private Netzwerk					net;

	private int							orderedByCol	= -1;

	private boolean					asc				= false;

	public ActorDataBase(Netzwerk net, List<Akteur> akteure)
	{
		actors = new ArrayList<ActorData>();
		this.net = net;
		aTypes = new ArrayList<AttributeType>();
		for (AttributeType a : VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"))
		{
			aTypes.add(a);
		}

		header = new String[aTypes.size() + 2];
		header[0] = Messages.getString("EditActorLabelDialog.1");
		header[1] = Messages.getString("EditActorLabelDialog.0");;
		for (int i = 0; i < aTypes.size(); i++)
			header[i + 2] = aTypes.get(i).getLabel();

		for (Akteur a : akteure)
		{
			ActorData ad = new ActorData(a, net);
			actors.add(ad);
		}
	}

	/**
	 * removes all actorData and other references and cleans up
	 */
	public void clear()
	{
		actors.clear();
		aTypes.clear();
		net = null;
		header = null;
	}

	/**
	 * 
	 * @return a list with all included AttributeTypes
	 */
	public List<AttributeType> getAttributeTypes()
	{
		return aTypes;
	}

	/**
	 * call this to fire all the events for changed attribute values and renamed
	 * actors
	 */
	public void acceptChanges()
	{
		for (ActorData ad : actors)
		{
			ad.applyNewName();
			ad.applyAttributeChanges();
		}
	}

	/**
	 * adds an Actor to the managed list
	 */
	public void addAkteur(Akteur a)
	{
		ActorData ad = new ActorData(a, net);
		actors.add(ad);
	}

	/**
	 * @return the index of the column by which the data is currently ordered<br>
	 *         possible : 0 = Name, 1 = ID
	 */
	public int orderedByCol()
	{
		return orderedByCol;
	}

	/**
	 * Use this in the table Model <code>getValueAt</code>
	 */
	public Object getValueAt(int row, int col)
	{
		if (row < actors.size())
		{
			switch (col)
			{
				case 0:
					return actors.get(row).Name;
				case 1:
					return actors.get(row).Id;
				default:
					return actors.get(row).getAttributeValue(aTypes.get(col - 2));
			}
		}
		return null;
	}

	/**
	 * sorts the whole data by Names (first call = ascending, second call =
	 * descending ...)
	 */
	public void sortByNames()
	{
		asc = !asc;
		Collections.sort(actors, new Comparator<ActorData>()
		{
			@Override
			public int compare(ActorData a1, ActorData a2)
			{
				int val = a2.Name.compareTo(a1.Name);
				if (asc)
					val = -val;
				return val;
			}
		});
		orderedByCol = 0;
	}

	/**
	 * sorts the whole data by Id (first call = ascending, second call =
	 * descending ...)
	 */
	public void sortById()
	{
		asc = !asc;
		Collections.sort(actors, new Comparator<ActorData>()
		{
			@Override
			public int compare(ActorData a1, ActorData a2)
			{
				int val = a2.Id > a1.Id ? 1 : 0;
				if (val == 0)
					val = a2.Id < a1.Id ? -1 : 0;

				if (asc)
					val = -val;
				return val;
			}
		});
		orderedByCol = 1;
	}

	/**
	 * sorts the whole data by Id (first call = ascending, second call =
	 * descending ...)
	 */
	public void sortByAttribute(final int column)
	{
		asc = !asc;
		Collections.sort(actors, new Comparator<ActorData>()
		{
			@Override
			public int compare(ActorData a1, ActorData a2)
			{
				/*
				 * get Values to prevent null.toString --> NPE (column -1) because
				 * of id, which is at the first column and isn't an attribute
				 */
				String a2Value = a2.getAttributeValue(aTypes.get(column - 1)) == null ? ""
						: a2.getAttributeValue(aTypes.get(column - 1)).toString();

				String a1Value = a1.getAttributeValue(aTypes.get(column - 1)) == null ? ""
						: a1.getAttributeValue(aTypes.get(column - 1)).toString();

				int val = a2Value.compareTo(a1Value);

				if (asc)
					val = -val;
				return val;
			}
		});

		/* (Column +1) because of the actornamecolumn */
		orderedByCol = column + 1;
	}

	/**
	 * @return current state of ordering (ascending, descending)
	 */
	public boolean isAsc()
	{
		return asc;
	}

	public int getColumnCount()
	{
		return 2 + aTypes.size();
	}

	public int getRowCount()
	{
		return actors.size();
	}

	public String[] getHeader()
	{
		return header;
	}

	/**
	 * Use this in the table Model <code>setValueAt</code>
	 */
	public void setValueAt(Object value, int row, int col)
	{
		if (row <= actors.size())
		{
			switch (col)
			{
				case 0:
					actors.get(row).Name = value.toString();
					break;
				case 1:
					break;
				default:
					actors.get(row).setAttributeValue(aTypes.get(col - 2), value);
					break;
			}
		}
	}
}
