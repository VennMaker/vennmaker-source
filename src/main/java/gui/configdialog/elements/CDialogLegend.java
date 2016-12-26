package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.LegendSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingNetworkLegend;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.AttributeType;

public class CDialogLegend extends ConfigDialogElement implements
		ActionListener
{
	private static final long								serialVersionUID		= 1L;

	private JCheckBox											displaySymbolsCB;

	private JCheckBox											displaySizesCB;

	private JCheckBox											displayPieCB;

	private HashMap<AttributeType, JCheckBox>			displayRelations		= new HashMap<AttributeType, JCheckBox>();

	/** stores, when the last changes occured in this specific dialog */
	private long												latestChanges			= 0;

	/* keep relationconfigurations together in one panel */
	private JPanel												relationPanel;

	/* statics - if legend should be the same for all networks */
	private static boolean									useDefaults				= false;

	private static boolean									symbolDefaults			= true;

	private static boolean									sizesDefaults			= true;

	private static boolean									pieDefaults				= true;

	/** stores, when the last changes were made to the default values */
	private static long										latestDefaultChanges	= 0;

	private static HashMap<AttributeType, Boolean>	relationDefaults		= new HashMap<AttributeType, Boolean>();

	@Override
	public void buildPanel()
	{
		if (dialogPanel != null)
		{
			refreshBeforeGetDialog();
			return;
		}

		dialogPanel = new JPanel();
		dialogPanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		int y = 0;

		JLabel actorLabel = new JLabel(
				Messages.getString("CDialogLegend.ActorLabel"));
		actorLabel.setFont(actorLabel.getFont().deriveFont(16f));
		displaySymbolsCB = new JCheckBox(
				Messages.getString("CDialogLegend.DisplaySymbols"));
		displaySizesCB = new JCheckBox(
				Messages.getString("CDialogLegend.DisplaySizes"));
		displayPieCB = new JCheckBox(
				Messages.getString("CDialogLegend.DisplayPies"));

		displayPieCB.addActionListener(this);
		displaySizesCB.addActionListener(this);
		displaySymbolsCB.addActionListener(this);

		if (useDefaults && (latestDefaultChanges > latestChanges))
		{
			displayPieCB.setSelected(pieDefaults);
			displaySizesCB.setSelected(sizesDefaults);
			displaySymbolsCB.setSelected(symbolDefaults);
		}
		else
		{
			displayPieCB.setSelected(VennMakerView.getLegend(net)
					.isShowActorPies());
			displaySizesCB.setSelected(VennMakerView.getLegend(net)
					.isShowActorSizes());
			displaySymbolsCB.setSelected(VennMakerView.getLegend(net)
					.isShowActorSymbols());
		}

		c.insets = new Insets(5, 0, 5, 5);
		dialogPanel.add(actorLabel, c);
		c.insets = new Insets(5, 15, 5, 5);
		c.gridy = ++y;
		dialogPanel.add(displaySymbolsCB, c);

		c.gridy = ++y;
		dialogPanel.add(displaySizesCB, c);

		c.gridy = ++y;
		dialogPanel.add(displayPieCB, c);

		JLabel relationLabel = new JLabel(
				Messages.getString("CDialogLegend.RelationLabel"));
		relationLabel.setFont(relationLabel.getFont().deriveFont(16f));
		c.insets = new Insets(5, 0, 5, 5);
		c.gridy = ++y;
		dialogPanel.add(relationLabel, c);
		c.insets = new Insets(5, 15, 5, 5);

		relationPanel = new JPanel();
		relationPanel
				.setLayout(new BoxLayout(relationPanel, BoxLayout.PAGE_AXIS));

		buildRelationPanel();

		c.gridy = ++y;
		dialogPanel.add(relationPanel, c);

		JButton setDefaultButton = new JButton(
				Messages.getString("CDialogLegend.asDefault"));
		c.gridy = ++y;
		dialogPanel.add(setDefaultButton, c);

		setDefaultButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				symbolDefaults = displaySymbolsCB.isSelected();
				sizesDefaults = displaySizesCB.isSelected();
				pieDefaults = displayPieCB.isSelected();
				useDefaults = true;
				latestDefaultChanges = System.currentTimeMillis();

				HashMap<AttributeType, Boolean> relationMap = new HashMap<AttributeType, Boolean>();
				for (AttributeType at : displayRelations.keySet())
				{
					relationMap.put(at, displayRelations.get(at).isSelected());
				}
				relationDefaults = relationMap;
			}
		});

	}

	private void buildRelationPanel()
	{
		relationPanel.removeAll();

		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypesDiscrete())
		{
			if (!at.getType().equals("ACTOR"))
			{
				if (!displayRelations.containsKey(at))
				{
					displayRelations.put(at,
							new JCheckBox("Display " + at.getLabel()));
					if (useDefaults && (latestDefaultChanges > latestChanges))
					{
						displayRelations.get(at).setSelected(isShowRelation(at));
					}
					else
					{
						displayRelations.get(at).setSelected(
								VennMakerView.getLegend(net).isShowRelation(at));
					}
				}

				relationPanel.add(displayRelations.get(at));
				displayRelations.get(at).addActionListener(this);
			}
		}
	}

	private boolean isShowRelation(AttributeType at)
	{
		if (relationDefaults.get(at) == null)
			relationDefaults.put(at, true);

		return relationDefaults.get(at);
	}

	@Override
	public void refreshBeforeGetDialog()
	{
		buildRelationPanel();

		if (useDefaults && (latestDefaultChanges > latestChanges))
		{
			displayPieCB.setSelected(pieDefaults);
			displaySizesCB.setSelected(sizesDefaults);
			displaySymbolsCB.setSelected(symbolDefaults);
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		SettingNetworkLegend element;

		if (useDefaults && (latestDefaultChanges > latestChanges))
			element = new SettingNetworkLegend(net, symbolDefaults, sizesDefaults,
					pieDefaults, relationDefaults);
		else
		{
			HashMap<AttributeType, Boolean> relationMap = new HashMap<AttributeType, Boolean>();
			for (AttributeType at : displayRelations.keySet())
			{
				relationMap.put(at, displayRelations.get(at).isSelected());
			}
			element = new SettingNetworkLegend(net, displaySymbolsCB.isSelected(),
					displaySizesCB.isSelected(), displayPieCB.isSelected(),
					relationMap);
		}

		return element;
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof LegendSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);

		displayPieCB.setSelected(((LegendSaveElement) setting).getDrawPies());
		displaySizesCB.setSelected(((LegendSaveElement) setting).getDrawSizes());
		displaySymbolsCB.setSelected(((LegendSaveElement) setting)
				.getDrawSymbols());

		for (AttributeType at : displayRelations.keySet())
		{
			if (((LegendSaveElement) setting).getDrawRelations().containsKey(at))
			{
				displayRelations.get(at).setSelected(
						((LegendSaveElement) setting).getDrawRelations().get(at));
			}
			else
			{
				displayRelations.get(at).setSelected(true);
			}
		}

	}

	@Override
	public SaveElement getSaveElement()
	{
		LegendSaveElement lse = null;;
		if (useDefaults && (latestDefaultChanges > latestChanges))
			lse = new LegendSaveElement(symbolDefaults, sizesDefaults,
					pieDefaults, relationDefaults);
		else
		{
			HashMap<AttributeType, Boolean> relationMap = new HashMap<AttributeType, Boolean>();
			for (AttributeType at : displayRelations.keySet())
			{
				relationMap.put(at, displayRelations.get(at).isSelected());
			}
			lse = new LegendSaveElement(displaySymbolsCB.isSelected(),
					displaySizesCB.isSelected(), displayPieCB.isSelected(),
					relationMap);
		}

		lse.setElementName(this.getClass().getSimpleName());

		return lse;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.latestChanges = System.currentTimeMillis();
	}

}
