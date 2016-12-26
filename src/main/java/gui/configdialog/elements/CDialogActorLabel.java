/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.ActorLabelSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingActorLabel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import data.AttributeType;
import data.Config.LabelBehaviour;

/**
 * Dialog fuer Einstellungen rund um die Darstellung der Informationen um den
 * Akteur. </br> (Label position, Label, Tooltip)
 * 
 * 
 */
public class CDialogActorLabel extends ConfigDialogElement
{

	private static Double				Double				= new Ellipse2D.Double(8,
																				4, 16, 16);

	private static final BasicStroke	S						= new BasicStroke(2.5f);

	private static final long			serialVersionUID	= 1L;

	private ActorLabelTableModel		tModel;

	/**
	 * All possibilities for label placement
	 */
	private JToggleButton[]				labelTypeButtons;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		GridBagLayout gbl = new GridBagLayout();
		dialogPanel.setLayout(gbl);
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 10, 0, 10);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0.5;
		dialogPanel.add(createActorsBox(), gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1;
		gbc.weighty = 0.5;
		dialogPanel.add(new JScrollPane(createActorLabelTable()), gbc);
	}

	private JTable createActorLabelTable()
	{
		if (tModel == null)
			tModel = new ActorLabelTableModel();
		JTable table = new JTable(tModel);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);

		CenteredTableHeaderRenderer c = new CenteredTableHeaderRenderer();
		TableColumnModel cm = table.getColumnModel();
		cm.getColumn(0).setHeaderRenderer(c);
		cm.getColumn(1).setHeaderRenderer(c);
		cm.getColumn(2).setHeaderRenderer(c);
		return table;
	}

	private JComponent createActorsBox()
	{
		// Box vBox = Box.createVerticalBox();
		JPanel vBox = new JPanel(new GridLayout(5, 0));

		// aside
		// alte Werte: 96,32
		BufferedImage labelStrat1 = new BufferedImage(96, 32,
				BufferedImage.TYPE_4BYTE_ABGR);

		// aside_left
		BufferedImage labelStrat2 = new BufferedImage(96, 32,
				BufferedImage.TYPE_4BYTE_ABGR);

		// ontop
		BufferedImage labelStrat3 = new BufferedImage(110, 32,
				BufferedImage.TYPE_4BYTE_ABGR);

		// under
		BufferedImage labelStrat4 = new BufferedImage(96, 32,
				BufferedImage.TYPE_4BYTE_ABGR);

		// nolabel
		BufferedImage labelStrat5 = new BufferedImage(155, 32,
				BufferedImage.TYPE_4BYTE_ABGR);

		// numbers
		BufferedImage labelStrat6 = new BufferedImage(135, 32,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = (Graphics2D) labelStrat1.getGraphics();
		initImg(g);
		g.setColor(Color.blue);
		g.drawString(Messages.getString("BackgroundConfig.String_1"), 40, 18); //$NON-NLS-1$

		g = (Graphics2D) labelStrat2.getGraphics();
		Double.x = 44;
		initImg(g);
		Double.x = 8;
		g.setColor(Color.blue);
		g.drawString(Messages.getString("BackgroundConfig.String_1"), 0, 18); //$NON-NLS-1$

		g = (Graphics2D) labelStrat3.getGraphics();
		initImg(g);
		g.setColor(Color.blue);
		g.drawString(Messages.getString("BackgroundConfig.String_1"), 2, 18); //$NON-NLS-1$

		g = (Graphics2D) labelStrat4.getGraphics();
		initImg(g);
		g.setColor(Color.blue);
		g.drawString(Messages.getString("BackgroundConfig.String_1"), 2, 32); //$NON-NLS-1$

		g = (Graphics2D) labelStrat5.getGraphics();
		initImg(g);

		g = (Graphics2D) labelStrat6.getGraphics();
		initImg(g);
		g.setColor(Color.blue);
		g.drawString("132", 2, 32); //$NON-NLS-1$

		Dimension dim = new Dimension(250, 40);

		if (labelTypeButtons == null)
		{
			labelTypeButtons = new JToggleButton[6];
			labelTypeButtons[0] = new JToggleButton(
					Messages.getString("BackgroundConfig.Button_0"), new ImageIcon( //$NON-NLS-1$
							labelStrat1));

			labelTypeButtons[1] = new JToggleButton(
					Messages.getString("BackgroundConfig.Button_1"), new ImageIcon( //$NON-NLS-1$
							labelStrat2));

			labelTypeButtons[2] = new JToggleButton(
					Messages.getString("BackgroundConfig.Button_2"), new ImageIcon( //$NON-NLS-1$
							labelStrat3));

			labelTypeButtons[3] = new JToggleButton(
					Messages.getString("BackgroundConfig.Button_3"), new ImageIcon( //$NON-NLS-1$
							labelStrat4));
			labelTypeButtons[3].setPreferredSize(new Dimension(dim));

			labelTypeButtons[4] = new JToggleButton(
					Messages.getString("BackgroundConfig.Button_4"), new ImageIcon( //$NON-NLS-1$
							labelStrat5));

			labelTypeButtons[5] = new JToggleButton(
					Messages.getString("BackgroundConfig.Button_6"), new ImageIcon( //$NON-NLS-1$
							labelStrat6));
			ButtonGroup group = new ButtonGroup();

			group.add(labelTypeButtons[0]);
			group.add(labelTypeButtons[1]);
			group.add(labelTypeButtons[2]);
			group.add(labelTypeButtons[3]);
			group.add(labelTypeButtons[4]);
			group.add(labelTypeButtons[5]);

			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case ASIDE:
					labelTypeButtons[0].setSelected(true);
					break;
				case ASIDE_LEFT:
					labelTypeButtons[1].setSelected(true);
					break;
				case ON_TOP:
					labelTypeButtons[2].setSelected(true);
					break;
				case UNDER:
					labelTypeButtons[3].setSelected(true);
					break;
				case NO_LABEL:
					labelTypeButtons[4].setSelected(true);
					break;
				case NUMBER:
					labelTypeButtons[5].setSelected(true);
					break;
				default:

			}
		}

		vBox.add(labelTypeButtons[0]);
		vBox.add(labelTypeButtons[1]);
		vBox.add(labelTypeButtons[2]);
		vBox.add(labelTypeButtons[3]);
		vBox.add(labelTypeButtons[4]);
		vBox.add(labelTypeButtons[5]);

		JPanel basePanel = new JPanel();
		basePanel.add(vBox);
		return basePanel;
	}

	private void initImg(Graphics2D g)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// g.setBackground(Color.white);
		// g.clearRect(0, 0, 96, 32);
		g.setColor(Color.black);
		g.setStroke(S);
		g.draw(Double);
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		tModel.updateTable();
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		LabelBehaviour labelBehaviour = null;
		if (labelTypeButtons[0].isSelected())
			labelBehaviour = LabelBehaviour.ASIDE;
		else if (labelTypeButtons[1].isSelected())
			labelBehaviour = LabelBehaviour.ASIDE_LEFT;
		else if (labelTypeButtons[2].isSelected())
			labelBehaviour = LabelBehaviour.ON_TOP;
		else if (labelTypeButtons[3].isSelected())
			labelBehaviour = LabelBehaviour.UNDER;
		else if (labelTypeButtons[4].isSelected())
			labelBehaviour = LabelBehaviour.NO_LABEL;
		else if (labelTypeButtons[5].isSelected())
			labelBehaviour = LabelBehaviour.NUMBER;

		boolean[] lableArr = tModel.getLableArr();
		boolean[] tooltipArr = tModel.getTooltipArr();
		List<AttributeType> all = tModel.getAttributeTypes();

		// Lable
		ArrayList<AttributeType> labelList = new ArrayList<AttributeType>();
		for (int i = 0; i < lableArr.length; i++)
		{
			if (lableArr[i])
				labelList.add(all.get(i));
		}

		// Tooltip
		List<AttributeType> tooltipList = new ArrayList<AttributeType>();
		for (int i = 0; i < tooltipArr.length; i++)
		{
			if (tooltipArr[i])
				tooltipList.add(all.get(i));
		}

		return new SettingActorLabel(labelList, tooltipList, labelBehaviour);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof ActorLabelSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		ActorLabelSaveElement element = (ActorLabelSaveElement) setting;
		labelTypeButtons[element.getLabelBehavior()].setSelected(true);

		boolean[] labelArray = element.getSelectedLabel();
		boolean[] tooltipArray = element.getSelectedTooltip();

		for (int i = 0; i < labelArray.length && i < tModel.getLableArr().length; i++)
		{
			tModel.setValueAt(labelArray[i], i, 1);
			tModel.setValueAt(tooltipArray[i], i, 2);
		}

		ConfigDialogTempCache.getInstance().addSetting(getFinalSetting());
		refreshBeforeGetDialog();
	}

	@Override
	public SaveElement getSaveElement()
	{
		int labelBehaviour = 0;

		// first selected toggle is the right one
		for (; labelBehaviour <= 5; labelBehaviour++)
			if (labelTypeButtons[labelBehaviour].isSelected())
				break;

		ActorLabelSaveElement elem = new ActorLabelSaveElement(labelBehaviour,
				tModel.getLableArr(), tModel.getTooltipArr());
		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}
}

class ActorLabelTableModel extends AbstractTableModel
{
	private static final long		serialVersionUID	= 1L;

	private List<AttributeType>	aTypes;

	private boolean[]					lableArr;

	private boolean[]					tooltipArr;

	public ActorLabelTableModel()
	{
		initializeAttr();
	}

	private void initializeAttr()
	{
		this.aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"); //$NON-NLS-1$
		boolean[] lableArrOld = new boolean[aTypes.size()];
		boolean[] tooltipArrOld = new boolean[aTypes.size()];

		// Alte werte fuer Lable uebernehmen
		List<AttributeType> display = VennMaker.getInstance().getProject()
				.getDisplayedAtActor();
		if (display != null)
		{
			for (AttributeType a : display)
			{
				int index = aTypes.indexOf(a);
				if ((index >= 0) && (index < lableArrOld.length))
					lableArrOld[index] = true;
			}
		}

		if (lableArr != null)
		{
			int len = lableArr.length < lableArrOld.length ? lableArr.length
					: lableArrOld.length;
			for (int i = 0; i < len; i++)
			{
				if (lableArr != null && i < lableArr.length)
					lableArrOld[i] |= lableArr[i];
			}
		}
		lableArr = lableArrOld;

		// Alte werte fuer Tooltip uebernehmen
		display = VennMaker.getInstance().getProject()
				.getDisplayedAtActorTooltip();
		if (display != null)
		{
			for (AttributeType a : display)
			{
				if (aTypes.indexOf(a) >= 0)
					tooltipArrOld[aTypes.indexOf(a)] = true;
			}
		}
		if (tooltipArr != null)
		{
			int len = tooltipArr.length < tooltipArrOld.length ? tooltipArr.length
					: tooltipArrOld.length;
			for (int i = 0; i < len; i++)
			{
				if (tooltipArr != null && i < tooltipArr.length)
					tooltipArrOld[i] |= tooltipArr[i];
			}
		}
		tooltipArr = tooltipArrOld;
	}

	/**
	 * init the attributes and refresh the table model
	 */
	public void updateTable()
	{
		initializeAttr();
		this.fireTableDataChanged();
	}

	/**
	 * get all attribute types
	 */
	public List<AttributeType> getAttributeTypes()
	{
		return aTypes;
	}

	/**
	 * get the attribute for the label
	 */
	public boolean[] getLableArr()
	{
		return lableArr;
	}

	/**
	 * returns the array representing the selection for the tooltip (each
	 * attribute - true / false)
	 */
	public boolean[] getTooltipArr()
	{
		return tooltipArr;
	}

	@Override
	public int getRowCount()
	{
		return aTypes.size();
	}

	@Override
	public int getColumnCount()
	{
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return aTypes.get(rowIndex).getLabel();
			case 1:
				return lableArr[rowIndex];
		}
		return tooltipArr[rowIndex];
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int col)
	{
		switch (col)
		{
			case 0:
				return String.class;
		}
		return Boolean.class;
	}

	@Override
	public String getColumnName(int col)
	{
		switch (col)
		{
			case 0:
				return Messages.getString("CDialogActorLabel.1");
			case 1:
				return Messages.getString("CDialogActorLabel.2");
		}
		return Messages.getString("CDialogActorLabel.3");
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return false;
		}

		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		switch (columnIndex)
		{
			case 1:
				lableArr[rowIndex] = (Boolean) aValue;
				break;
			case 2:
				tooltipArr[rowIndex] = (Boolean) aValue;
				break;
		}
	}
}

class CenteredTableHeaderRenderer extends DefaultTableCellRenderer
{
	private static final long	serialVersionUID	= 1L;

	public CenteredTableHeaderRenderer()
	{
		super();
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component comp = table
				.getTableHeader()
				.getDefaultRenderer()
				.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
		if (comp instanceof JLabel)
			((JLabel) comp).setHorizontalAlignment(CENTER);
		return comp;
	}
}
