package interview.panels.multi;

import gui.Messages;
import gui.VennMaker;
import gui.utilities.VennMakerUIConfig;
import interview.panels.SpecialPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * panel - multiple actors - one (categorical) attribute types
 * 
 * selecting the value for each actor (for the choosen attribute) by radio
 * buttons
 * 
 * 
 */
public class RadioAnswerPanel extends SpecialPanel implements
		AdjustmentListener
{
	private static final long							serialVersionUID	= 1L;

	protected List<Akteur>								actors;

	protected AttributeType								aType;

	protected Map<Akteur, ButtonGroup>				answers;

	private Map<Netzwerk, Map<Akteur, Object>>	oldAttributeValues;

	private JTable											actorTable;

	private JTable											radioTable;

	private JPanel											leftPanel;

	private JPanel											rightPanel;

	private JScrollBar									leftScrollBar;

	private JScrollBar									rightScrollBar;

	private JSplitPane									split;

	/**
	 * Vector, which contains all rows, where input is missing (after a click on
	 * the next-button)
	 */
	private Vector<Integer>								notFilled			= new Vector<Integer>();

	public RadioAnswerPanel()
	{
		super();
		answers = new HashMap<Akteur, ButtonGroup>();
		actors = new ArrayList<Akteur>();
		oldAttributeValues = new HashMap<Netzwerk, Map<Akteur, Object>>();
	}

	/**
	 * Method to build the right part of the split pane (init the rightPanel)
	 */
	private void buildRightPanel()
	{
		rightPanel = new JPanel(new BorderLayout());
		RadioTableModel radioTableModel = new RadioTableModel(actors, aType,
				answers);
		radioTable = new JTable(radioTableModel);
		radioTable.setDefaultRenderer(JRadioButton.class, new ComponentRenderer(this));
		radioTable.setDefaultEditor(JRadioButton.class, new RadioButtonEditor(new JCheckBox()));

		WordWrapHeaderRenderer headerRenderer = new WordWrapHeaderRenderer(this);
		radioTable.getTableHeader().setDefaultRenderer(headerRenderer);
		radioTable.setRowHeight((int) (VennMakerUIConfig.getFontSize()+15));

		JScrollPane rightScroll = new JScrollPane(radioTable);

		rightPanel.add(radioTable.getTableHeader(), BorderLayout.NORTH);
		rightPanel.add(rightScroll, BorderLayout.CENTER);

		rightScrollBar = rightScroll.getVerticalScrollBar();
		rightScrollBar.addAdjustmentListener(this);
	}

	/**
	 * Method to build the left part of the split pane (init the leftPanel)
	 */
	private void buildLeftPanel()
	{
		ActorTableModel actorTableModel = new ActorTableModel(actors);
		actorTable = new JTable(actorTableModel);
		actorTable.setDefaultRenderer(JLabel.class, new ComponentRenderer(this));
		actorTable.getTableHeader().setDefaultRenderer(
				new WordWrapHeaderRenderer(this));
		actorTable.setRowHeight((int) (VennMakerUIConfig.getFontSize()+15));
		leftPanel = new JPanel(new BorderLayout());
		JScrollPane leftScroll = new JScrollPane(actorTable);
		leftPanel.add(actorTable.getTableHeader(), BorderLayout.NORTH);
		leftPanel.add(leftScroll, BorderLayout.CENTER);

		// getScrollBar and add the AdjustmentListener
		leftScrollBar = leftScroll.getVerticalScrollBar();
		leftScrollBar.addAdjustmentListener(this);
	}

	protected void build()
	{
		if (actors != null && actors.size() > 0)
		{
			buildLeftPanel();
			buildRightPanel();
			split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel,
					rightPanel);
			this.setLayout(new BorderLayout());
			this.add(split, BorderLayout.CENTER);
			split.setDividerLocation(120);
		}
		else
		{
			this.add(new JLabel("<html><h1>"
					+ Messages.getString("InterviewElement.NoMatchingActors") //$NON-NLS-1$
					+ "</h1></html>"));
		}
	}

	@Override
	public boolean performChanges()
	{
		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();
		for (Netzwerk n : networks)
		{
			HashMap<Akteur, Object> hm = new HashMap<Akteur, Object>();
			for (Akteur a : actors)
			{
				ButtonModel bm = answers.get(a).getSelection();
				if (bm == null) {
					return false;
				}
				String value = bm.getActionCommand();
				hm.put(a, value);
				a.setAttributeValue(aType, n, value);

			}
			oldAttributeValues.put(n, hm);
		}
		return true;
	}

	@Override
	public void rebuild()
	{
		answers.clear();
		this.removeAll();
		build();
		validate();
	}

	@Override
	public void setAttributesAndQuestions(
			Map<String, AttributeType> attributesAndQuestions)
	{
		String question = attributesAndQuestions.keySet().iterator().next();
		this.aType = attributesAndQuestions.get(question);
	}

	@Override
	public void setActors(List<Akteur> actors)
	{
		this.actors.clear();
		for (Akteur act : actors)
		{
			if (!this.actors.contains(act))
				this.actors.add(act);
		}
	}

	public boolean undoChanges()
	{
		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();
		for (Netzwerk n : networks)
		{
			Map<Akteur, Object> hm = oldAttributeValues.get(n);
			for (Akteur a : hm.keySet())
				a.setAttributeValue(aType, n, hm.get(a));
		}
		return true;
	}

	@Override
	public boolean shouldBeScrollable()
	{
		return false;
	}

	/**
	 * Diese Methode sorgt dafür, dass sich gegenüberliegende ScrollBars
	 * synchron verschieben
	 */
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		JScrollBar sourceBar = (JScrollBar) e.getSource();

		if (sourceBar == leftScrollBar)
		{
			rightScrollBar.setValue(leftScrollBar.getValue());
		}
		else if (sourceBar == rightScrollBar)
		{
			leftScrollBar.setValue(rightScrollBar.getValue());
		}
	}

	/**
	 * Check if both header (leftTable and rightTable) do have the same height if
	 * not set leftHeader.Height to rightHeader.Height
	 * 
	 */
	public void checkHeaderHeights()
	{

		if (actorTable == null || radioTable == null)
			return;
		JTableHeader ah = actorTable.getTableHeader();
		JTableHeader rh = radioTable.getTableHeader();

		if (ah.getHeight() != rh.getHeight())
		{
			ah.setPreferredSize(new Dimension(ah.getWidth(), rh.getHeight()));
			split.setDividerLocation(split.getDividerLocation() + 1);
		}

	}

	@Override
	public boolean validateInput()
	{
		/**
		 * do not return immediately, when there's a value missing - color ALL the
		 * RadioButtons which do not contain a selection
		 */
		boolean endsuccessful = true;

		int rowCounter = 0;
		this.notFilled.clear();

		for (Akteur a : actors)
		{
			ButtonGroup selected = answers.get(a);

			if ( (selected == null) || (selected.getSelection() == null) )
			{
				endsuccessful = false;
				this.notFilled.add(rowCounter);
			}

			rowCounter++;
		}

		if (!endsuccessful)
		{
			this.repaint();

			JOptionPane.showMessageDialog(this,
					"You have to complete your selection to proceed",
					"Complete your selection", JOptionPane.WARNING_MESSAGE);
		}

		return endsuccessful;
	}

	/**
	 * returns all rows, where input is missing (after a click on next)
	 * 
	 * @return
	 */
	public Vector<Integer> getEmptyRows()
	{
		return this.notFilled;
	}
}

/**
 * CellEditor to use with JRadioButtons
 * 
 */
class RadioButtonEditor extends DefaultCellEditor implements ItemListener
{
	private static final long	serialVersionUID	= 1L;

	private JRadioButton			button;

	public RadioButtonEditor(JCheckBox checkBox)
	{
		super(checkBox);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column)
	{
		if (value == null)
			return null;
		button = (JRadioButton) value;
		button.addItemListener(this);

		return (Component) value;
	}

	@Override
	public Object getCellEditorValue()
	{
		button.removeItemListener(this);
		return button;
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		super.fireEditingStopped();
	}
}

/**
 * Renderer for all kind of components within a table cell
 * 
 */
class ComponentRenderer implements TableCellRenderer
{
	RadioAnswerPanel	parent;

	public ComponentRenderer(RadioAnswerPanel parent)
	{
		this.parent = parent;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{

		if (value == null)
			return null;

		if (row % 2 == 0)
		{
			((Component) value).setBackground(Color.lightGray);
		}
		else
		{
			((Component) value).setBackground(Color.WHITE);
		}

		/*
		 * fill the rows, where input is missing, light red (only after a click on
		 * next)
		 */
		if (parent.getEmptyRows().contains(row))
			((Component) value).setBackground(new Color(0xffbab0));


//		table.setRowHeight(row, (int) (VennMakerUIConfig.getFontSize()+5));

		return (Component) value;
	}
}

/**
 * TableModel for the actorTable (displaying all actors of this dialog)
 * 
 */
class ActorTableModel extends AbstractTableModel
{
	private static final long	serialVersionUID	= 1L;

	private List<Akteur>			actors;

	private String[]				header;

	public ActorTableModel(List<Akteur> actors)
	{
		this.actors = actors;
		header = new String[] { "Name" };
	}

	@Override
	public Class getColumnClass(int col)
	{
		return JLabel.class;
	}

	@Override
	public String getColumnName(int col)
	{
		return header[col];
	}

	@Override
	public int getRowCount()
	{
		return actors.size();
	}

	@Override
	public int getColumnCount()
	{
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < actors.size())
		{
			JLabel label = new JLabel(actors.get(rowIndex).getName());
			label.setOpaque(true);
			return label;
		}
		return null;
	}
}

/**
 * TableModel to select AttributeValue by RadioButtons
 * 
 */
class RadioTableModel extends AbstractTableModel
{
	private static final long			serialVersionUID	= 1L;

	private Map<Akteur, ButtonGroup>	data;

	private Object[]						preVals;

	private AttributeType				aType;

	private List<Akteur>					actors;

	public RadioTableModel(List<Akteur> actors, AttributeType aType,
			Map<Akteur, ButtonGroup> answers)
	{
		this.aType = aType;
		preVals = aType.getPredefinedValues();
		this.actors = actors;
		this.data = answers;
		Netzwerk net = VennMaker.getInstance().getActualVennMakerView()
				.getNetzwerk();
		for (Akteur a : actors)
		{
			ButtonGroup bg = new ButtonGroup();
			Object answer = a.getAttributeValue(aType, net);
			for (Object o : preVals)
			{
				JRadioButton rb = new JRadioButton();
				rb.setActionCommand(o.toString());
				rb.setHorizontalAlignment(SwingConstants.CENTER);
				rb.setOpaque(true);
				bg.add(rb);
				if (o.equals(answer))
				{
					rb.setSelected(true);
				}
			}
			if (answer == null) {
				answers.put(a, null);
			}
			
			data.put(a, bg);
		}
	}

	@Override
	public Class getColumnClass(int col)
	{
		return JRadioButton.class;
	}

	@Override
	public String getColumnName(int col)
	{
		return preVals[col].toString();
	}

	@Override
	public int getRowCount()
	{
		return data.size();
	}

	@Override
	public void setValueAt(Object o, int row, int column)
	{
		fireTableDataChanged();
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return true;
	}

	@Override
	public int getColumnCount()
	{
		return preVals.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Enumeration<AbstractButton> buttons = data.get(actors.get(rowIndex))
				.getElements();
		while (columnIndex-- > 0)
			buttons.nextElement();
		return buttons.nextElement();
	}
}

/**
 * This CellRenderer can be used to display multi line table Header with
 * word-wrap function.
 * 
 */
class WordWrapHeaderRenderer extends JPanel implements TableCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	private JTextArea				tp;

	public WordWrapHeaderRenderer(final RadioAnswerPanel radioAnswerPanel)
	{
		super();

		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weighty = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 2, 0, 2);

		tp = new JTextArea();
		tp.setLineWrap(true);
		tp.setWrapStyleWord(true);
		tp.setSize(new Dimension(300, 100));
		
		tp.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (radioAnswerPanel != null)
				{
					radioAnswerPanel.checkHeaderHeights();
				}
			}
		});

		/*
		 * set the right height and adept the backgroundcolor to the one, the
		 * Textpane uses
		 */

		this.setPreferredSize(new Dimension(this.getWidth(), 200));
		this.setBackground(tp.getBackground());

		this.add(tp, gbc);

	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
//		tp.setText("<p>"+(String) value+"</p><small><br></small>");
		tp.setText((String) value);
		tp.setFont(tp.getFont().deriveFont(VennMakerUIConfig.getFontSize()));
		return this;
	}
}
