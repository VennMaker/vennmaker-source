/**
 * 
 */
package gui.configdialog.individual;

import files.FileOperations;
import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.elements.CDialogNetworkClone;
import gui.configdialog.settings.SettingEditAttributeType;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.batik.apps.svgbrowser.JSVGViewerFrame.OpenAction;

import data.AttributeType;
import data.AttributeType.Scope;
import data.Netzwerk;

/**
 * 
 * 
 */
@SuppressWarnings("serial")
public class EditIndividualAttributeTypeDialog extends JDialog implements
		ActionListener
{

	int										max					= 5;

	private JTable							table;

	private MyTableModel					model;

	private List<Object>					tableValues			= new ArrayList<Object>();

	final JButton							deleteTableEntry	= new JButton(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.5"));	//$NON-NLS-1$

	final JButton							addTableEntry		= new JButton(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.6"));	//$NON-NLS-1$

	final JLabel							infoPredefined		= new JLabel(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.7"));	//$NON-NLS-1$

	private AttributeType				oldA;

	private AttributeType				tmpAttributeType;

	private AttributeType				attributeType;

	final JTextField						label					= new JTextField(null,
																				max);																		//$NON-NLS-1$

	final JTextArea						question				= new JTextArea(null, 3,
																				max);																		//$NON-NLS-1$

	final JTextArea						description			= new JTextArea(null, 5,
																				max);																		//$NON-NLS-1$

	final JRadioButton					networkScope				= new JRadioButton(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.8"));	//$NON-NLS-1$

	final JRadioButton					interviewScope				= new JRadioButton(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.9"));	//$NON-NLS-1$	

	final JRadioButton					openEnded		= new JRadioButton(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.10"));	//$NON-NLS-1$

	final JRadioButton					catgorial		= new JRadioButton(
																				Messages
																						.getString("EditIndividualAttributeTypeDialog.11"));	//$NON-NLS-1$	

	private Icon							icon;

	private boolean						canceled				= false;

	private SettingEditAttributeType	setting;

	/**
	 * <code>true</code> if this Dialog is NOT called from
	 * <code>CDialogAttributeTypes</code>
	 */
	private boolean						fastAdd;

	public EditIndividualAttributeTypeDialog()
	{
		super();

		List<ConfigDialogElement> activeElements = ConfigDialogTempCache
				.getInstance().getAllElements();

		boolean found = false;

		for (ConfigDialogElement elem : activeElements)
			if (elem.getClass().getSimpleName()
					.equals("CDialogEditAttributeTypes"))
				found = true;

		if (found)
			return;

		try
		{
			Class<? extends ConfigDialogElement> elem = (Class<? extends ConfigDialogElement>) Class
					.forName("gui.configdialog.elements.CDialogEditAttributeTypes");
			ConfigDialogTempCache.getInstance().addActiveElement(
					elem.newInstance());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public AttributeType showDialog(AttributeType a)
	{
		oldA = a;
		attributeType = a.clone();
		tmpAttributeType = (AttributeType) CDialogNetworkClone.deepCopy(a);
		tmpAttributeType.setDefaultValue(attributeType.getDefaultValue());

		label.setText(attributeType.getLabel());
		question.setText(attributeType.getQuestion());
		description.setText(attributeType.getDescription());

		this.setPreferredSize(new Dimension(700, 600));
		this.setMinimumSize(new Dimension(600, 600));
		this.setMaximumSize(new Dimension(800, 600));

		this.setModal(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle(Messages.getString("EditIndividualAttributeTypeDialog.12")); //$NON-NLS-1$

		icon = new ImageIcon(
				FileOperations.getAbsolutePath("icons/intern/icon.png")); //$NON-NLS-1$
		this.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$

		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);

		GridBagConstraints gbc;

		int zeile = 0;

		final JLabel infoLabel = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.0")); //$NON-NLS-1$

		final JLabel infoQuestion = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.1")); //$NON-NLS-1$
		question.setLineWrap(true);
		JScrollPane scrollQuestion = new JScrollPane(question);
		scrollQuestion
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		final JLabel infoDescription = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.2")); //$NON-NLS-1$
		description.setLineWrap(true);
		JScrollPane scrollDescripton = new JScrollPane(description);
		scrollDescripton
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		switch (attributeType.getScope())
		{
			case NETWORK:
				networkScope.setSelected(true);
				break;

			case PROJECT:
				interviewScope.setSelected(true);
				break;

			default:
				interviewScope.setSelected(true);
				break;
		}

		final ButtonGroup scopeGroup = new ButtonGroup();
		scopeGroup.add(networkScope);
		scopeGroup.add(interviewScope);
		final JLabel infoScope = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.3")); //$NON-NLS-1$

		/**
		 * Tabelle fuer Categories (predefined values)
		 */

		Object[] o = attributeType.getPredefinedValues();

		if (o != null)
		{
			for (int i = 0; i < o.length; i++)
			{
				tableValues.add(o[i]);
			}
		}

		model = new MyTableModel(tableValues);
		table = new JTable(model);

		table.setPreferredScrollableViewportSize(new Dimension(500, 500));
		table.setFillsViewportHeight(true);
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
				super.drop(dtde);

				model.switchRows(row, table.getSelectedRow());

				// Attribut Values im tmpAttribute anpassen
				Object[] vals = new Object[tableValues.size()];
				tableValues.toArray(vals);
				tmpAttributeType.setPredefinedValues(vals);
			}
		});

		TableColumn column = null;
		column = table.getColumnModel().getColumn(0);
		column.setPreferredWidth(250);
		column = table.getColumnModel().getColumn(1);
		column.setPreferredWidth(20);

		final ButtonGroup questionGroup = new ButtonGroup();
		questionGroup.add(openEnded);
		questionGroup.add(catgorial);

		if (attributeType.getPredefinedValues() != null)
		{
			catgorial.setSelected(true);
			table.setEnabled(true);
			deleteTableEntry.setEnabled(true);
			addTableEntry.setEnabled(true);
			infoPredefined.setEnabled(true);
		}
		else
		{
			openEnded.setSelected(true);
			table.setEnabled(false);
			deleteTableEntry.setEnabled(false);
			addTableEntry.setEnabled(false);
			infoPredefined.setEnabled(false);
		}

		final JLabel infoquestionType = new JLabel(
				Messages.getString("EditIndividualAttributeTypeDialog.4")); //$NON-NLS-1$

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(infoLabel, gbc);
		this.add(infoLabel);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = max;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(label, gbc);
		this.add(label);

		zeile++;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(infoQuestion, gbc);
		this.add(infoQuestion);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = max;
		gbc.gridheight = 3;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(scrollQuestion, gbc);
		this.add(scrollQuestion);

		zeile = zeile + 3;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(infoDescription, gbc);
		this.add(infoDescription);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = max;
		gbc.gridheight = 5;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(scrollDescripton, gbc);
		this.add(scrollDescripton);

		zeile = zeile + 5;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(infoScope, gbc);
		this.add(infoScope);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 5;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(networkScope, gbc);
		this.add(networkScope);

		zeile++;
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 5;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 10, 10);
		layout.setConstraints(interviewScope, gbc);
		this.add(interviewScope);

		zeile = zeile + 1;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(infoquestionType, gbc);
		this.add(infoquestionType);

		openEnded.setActionCommand("questionType1"); //$NON-NLS-1$
		openEnded.addActionListener(this);
		openEnded.setName("questionType1");//$NON-NLS-1$

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = max;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(10, 0, 0, 10);
		layout.setConstraints(openEnded, gbc);
		this.add(openEnded);

		zeile = zeile + 1;

		catgorial.setActionCommand("questionType2"); //$NON-NLS-1$
		catgorial.addActionListener(this);
		catgorial.setName("questionType2");//$NON-NLS-1$

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = max;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 10, 10);
		layout.setConstraints(catgorial, gbc);
		this.add(catgorial);

		zeile = zeile + 1;

		JScrollPane tableScrollPane = new JScrollPane(table);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(infoPredefined, gbc);
		this.add(infoPredefined);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = max;
		gbc.gridheight = 7;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(tableScrollPane, gbc);
		this.add(tableScrollPane);

		zeile = zeile + 7;

		addTableEntry.setActionCommand("add"); //$NON-NLS-1$
		addTableEntry.addActionListener(this);
		addTableEntry.setName("add");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(addTableEntry, gbc);
		this.add(addTableEntry);

		deleteTableEntry.setActionCommand("delete"); //$NON-NLS-1$
		deleteTableEntry.addActionListener(this);
		deleteTableEntry.setName("delete");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 0, 0, 10);
		layout.setConstraints(deleteTableEntry, gbc);
		this.add(deleteTableEntry);

		zeile = zeile + 1;

		JButton b1 = new JButton(
				Messages.getString("EditIndividualAttributeTypeDialog.13")); //$NON-NLS-1$
		b1.setActionCommand("ok"); //$NON-NLS-1$
		b1.addActionListener(this);
		b1.setName("ok");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(30, 0, 0, 10);
		layout.setConstraints(b1, gbc);
		this.add(b1);

		JButton b2 = new JButton(
				Messages.getString("EditIndividualAttributeTypeDialog.14")); //$NON-NLS-1$
		b2.setActionCommand("cancel"); //$NON-NLS-1$
		b2.addActionListener(this);
		b2.setName("cancel");//$NON-NLS-1$
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 2;
		gbc.gridy = zeile;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = new Insets(30, 0, 0, 10);
		layout.setConstraints(b2, gbc);
		this.add(b2);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - this.getHeight()) / 2;
		int left = (screenSize.width - this.getWidth()) / 2;
		this.setLocation(left, top);

		this.setVisible(true);

		return attributeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		canceled = false;
		if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();

		if ("add".equals(event.getActionCommand())) //$NON-NLS-1$
		{
			String category = (String) JOptionPane
					.showInputDialog(
							this,
							Messages.getString("EditIndividualAttributeTypeDialog.15"), Messages.getString("EditIndividualAttributeTypeDialog.18"), JOptionPane.QUESTION_MESSAGE, icon, null, null); //$NON-NLS-1$ //$NON-NLS-2$
			if (category != null)

				if (!category.equals("")) //$NON-NLS-1$
				{

					tableValues.add(category);
					Object[] o = new Object[tableValues.size()];

					if (o != null)
						for (int q = 0; q < o.length; q++)
						{
							o[q] = tableValues.get(q);
						}
					tmpAttributeType.setPredefinedValues(o);
					model.fireTableDataChanged();
				}

		}
		else if ("delete".equals(event.getActionCommand())) //$NON-NLS-1$

			if (table.getSelectedRowCount() > 0)
			{
				int row = table.getSelectedRow();

				Object oDefault = tmpAttributeType.getDefaultValue();
				Object oToDelete = tableValues.get(row);

				if (oDefault != null && oDefault.equals(oToDelete))
					tmpAttributeType.setDefaultValue(null);
				tableValues.remove(row);
				Object[] vals = new Object[tableValues.size()];
				tableValues.toArray(vals);
				tmpAttributeType.setPredefinedValues(vals);
				model.fireTableDataChanged();

				if (row >= tableValues.size())
					row--;
				table.getSelectionModel().setSelectionInterval(0, row);
			}

		if ("questionType1".endsWith(event.getActionCommand())) //$NON-NLS-1$
		{
			table.setEnabled(false);
			deleteTableEntry.setEnabled(false);
			addTableEntry.setEnabled(false);
			infoPredefined.setEnabled(false);

		}
		else if ("questionType2".endsWith(event.getActionCommand())) //$NON-NLS-1$
		{
			table.setEnabled(true);
			deleteTableEntry.setEnabled(true);
			addTableEntry.setEnabled(true);
			infoPredefined.setEnabled(true);
		}

		if ("ok".endsWith(event.getActionCommand())) //$NON-NLS-1$
		{
			// Ueberpruefen ob Attribut in einem Trigger verwendet wird,
			// denn dann ist es nicht erlaubt aus categorial
			// "open ended answer" zu machen (oder categorial mit 0 Kategorien)
			String s = "";
			for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
			{
				if (n.getActorImageVisualizer().getAttributeType()
						.equals(attributeType)
						|| n.getActorSizeVisualizer().getAttributeType()
								.equals(attributeType))
				{
					s += n.getBezeichnung() + "\n";
				}
			}

			if (s.length() > 0
					&& (!infoPredefined.isEnabled() || tableValues.size() <= 0))
			{
				JOptionPane
						.showMessageDialog(
								this,
								Messages
										.getString("EditIndividualAttributeTypeDialog.TriggerError")
										+ s);
				canceled = true;
				return;
			}

			/**
			 * check, if it's the last relational attribute with predefined values
			 * if so, deny to change to "open answer"
			 */
			
			
			int attributeType_count = VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete(attributeType.getType()).size();
			boolean hasOtherAttributes = (attributeType_count) >= 1;
			
			/** 
			 * 
			 */
			
			if ( (!attributeType.getType().equals("ACTOR") ) &&
			( openEnded.isSelected() && networkScope.isSelected() ) || (openEnded.isSelected() && !hasOtherAttributes ) )   {
			System.out.println("attributeType:"+attributeType.getType());
				JOptionPane
						.showMessageDialog(
								this,
								Messages
										.getString("EditIndividualAttributeTypeDialog.RelationalError"));
				canceled = true;
				return;
			}

			attributeType.setLabel(label.getText());
			attributeType.setQuestion(question.getText());
			attributeType.setDescription(description.getText());

			if (networkScope.isSelected())
				attributeType.setScope(Scope.NETWORK);
			else
				attributeType.setScope(Scope.PROJECT);

			if (infoPredefined.isEnabled() && tableValues.size() > 0)
			{
				Object[] o = new Object[tableValues.size()];
				for (int q = 0; q < tableValues.size(); q++)
				{
					o[q] = tableValues.get(q);
				}
				attributeType.setPredefinedValues(o);
			}
			else
			{
				attributeType.setPredefinedValues(null);
				tmpAttributeType.setDefaultValue(null);
				deleteVisualizers(attributeType);
			}

			attributeType.setDefaultValue(tmpAttributeType.getDefaultValue());

			setting = new SettingEditAttributeType(oldA, attributeType);
			ConfigDialogTempCache.getInstance().addSetting(setting);
			this.dispose();
		}
		else if ("cancel".endsWith(event.getActionCommand())) //$NON-NLS-1$
		{
			attributeType = null;
			canceled = true;
			this.dispose();
		}

	}

	/**
	 * Method to delete all visualizers of attributetypes without predefined
	 * values
	 * 
	 * @param at
	 *           the attributeType without predValues
	 */
	private void deleteVisualizers(AttributeType at)
	{
		for (Netzwerk n : VennMaker.getInstance().getProject().getNetzwerke())
		{
			n.getRelationColorVisualizer(at.getType(), at).setColors(null);
			n.getRelationDashVisualizer(at.getType(), at).setDasharrays(null);
			n.getRelationSizeVisualizer(at.getType(), at).setSizes(null);
		}
	}

	public SettingEditAttributeType getSetting()
	{
		return this.setting;
	}

	class MyTableModel extends AbstractTableModel
	{
		private List<Object>	value;

		static final long		serialVersionUID	= 1L;

		public MyTableModel(List<Object> at)
		{
			value = at;
		}

		/**
		 * @param row
		 * @param selectedRow
		 */
		public void switchRows(int index1, int index2)
		{
			Object o = value.get(index1);
			value.set(index1, value.get(index2));
			value.set(index2, o);

			fireTableDataChanged();
		}

		public int getColumnCount()
		{
			return 2;
		}

		public int getRowCount()
		{
			return value.size();
		}

		public String getColumnName(int col)
		{
			String r;
			switch (col)
			{
				case 0:
					r = Messages.getString("EditIndividualAttributeTypeDialog.16"); //$NON-NLS-1$
					break;
				default:
					r = Messages.getString("EditIndividualAttributeTypeDialog.17"); //$NON-NLS-1$
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
					r = value.get(row).toString();
					break;

				case 1:
					if (value.get(row) == tmpAttributeType.getDefaultValue())
						r = true;
					else
						r = false;
					break;

				default:
					r = "?"; //$NON-NLS-1$
					break;
			}
			return r;

		}

		public boolean isCellEditable(int row, int col)
		{
			return true;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		public void setValueAt(Object v, int row, int col)
		{
			switch (col)
			{
				case 0:
					if (!v.equals("")) //$NON-NLS-1$
					{
						if (((Boolean) getValueAt(row, 1)) == true)
							tmpAttributeType.setDefaultValue(null);
						tmpAttributeType.setPredefinedValue(v, row);

						value.set(row, tmpAttributeType.getPredefinedValue(row));
					}
					else
						JOptionPane.showMessageDialog(null,
								Messages.getString("CDialogEditAttributeTypes.12"), //$NON-NLS-1$
								Messages.getString("VennMaker.Error"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
					break;

				case 1:
					if (value.get(row).equals(tmpAttributeType.getDefaultValue()))
						tmpAttributeType.setDefaultValue(null);
					else
						tmpAttributeType.setDefaultValue(tmpAttributeType
								.getPredefinedValue(row));
					value.set(row, tmpAttributeType.getPredefinedValue(row));
					break;
			}
			fireTableDataChanged();
		}
	}

	public boolean wasCanceled()
	{
		return canceled;
	}
}
