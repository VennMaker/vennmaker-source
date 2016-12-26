package gui.configdialog.elements;

import gui.ConfigData;
import gui.ConfigData.Internal;
import gui.ConfigModel;
import gui.ConfigPanel;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RelationColorSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRelationColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import data.AttributeType;
import data.RelationColorVisualizer;

/**
 * 
 * Currently design and test purpose class will be base class for new Relation
 * Attribute configuration (color)
 * 
 * Once color works, easy port for dashing and thickness possible
 * 
 */
public class CDialogRelationColorTable extends ConfigDialogElement
{

	private static final long	serialVersionUID	= 1L;

	private ConfigModel			dataModel;

	private boolean				firstBuild			= true;

	public CDialogRelationColorTable()
	{
		dataModel = new ConfigModel();
	}

	@Override
	public void buildPanel()
	{
		
		storePanel();

		List<ConfigData> dataList = new ArrayList<ConfigData>();

		Vector<String> tempTypes = VennMaker.getInstance().getProject()
				.getAttributeCollectors();

		for (String attributeCollector : tempTypes)
		{
			List<AttributeType> aTypes = VennMaker.getInstance().getProject()
					.getAttributeTypesDiscrete(attributeCollector);

			HashMap<Object, ConfigData.Internal> internalDataMap = new HashMap<>();

			for (AttributeType typ : aTypes)
			{
				if (typ.getPredefinedValues() == null)
					continue;

				Vector<Color> colors = new Vector<>();
				Vector<Object> keys = new Vector<>();

				for (Object preDef : typ.getPredefinedValues())
				{
					colors.add(getNet().getRelationColorVisualizer(
							attributeCollector, typ).getColor(preDef));
					keys.add(preDef.toString());
				}

				String keyStrs[] = new String[keys.size()];
				keys.toArray(keyStrs);

				internalDataMap.put(typ,
						new ConfigData.Internal(keyStrs, colors.toArray(),
								Color.class));
			}

			dataList.add(new ConfigData(attributeCollector, aTypes.toArray(),
					internalDataMap));

		}
		// System.out.println(dataList);
		if (firstBuild)
		{
			retrieveSelections(dataList);
			firstBuild = false;
		}
		
		dataModel.setData(dataList);
		restorePanel();
		dialogPanel = new ConfigPanel(dataModel);
	}

	/**
	 * Retrieve the correct selection
	 * 
	 * @param list
	 */
	private void retrieveSelections(List<ConfigData> list)
	{
		for (ConfigData data : list)
		{
			RelationColorVisualizer colorViz = net
					.getActiveRelationColorVisualizer(data.attributeGroupName);
			if (data.attributeSettings.keySet().contains(
					colorViz.getAttributeType()))
			{
				data.attributeSelector.setSelectedItem(colorViz.getAttributeType());
			}
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		// Main goal: use ActiveRelationColorVisualizer (or respective ones) to
		// set the actual valid visualizers per attributeCollector
		Map<String, Map<String, Vector<Color>>> colors = new HashMap<>();
		Map<String, Map<String, Vector<String>>> values = new HashMap<>();
		Map<String, AttributeType> selection = new HashMap<>();

		List<?> data = dataModel.getDataList();
		Iterator<?> it = data.iterator();

		while (it.hasNext())
		{
			Object setting = it.next();
			// skip if not configData
			if (!(setting instanceof ConfigData))
			{
				continue;
			}
			ConfigData config = (ConfigData) setting;

			HashMap<String, Vector<Color>> colorData = new HashMap<>();

			Map<String, Vector<String>> indexData = new HashMap<>();

			// fetch from config
			for (Object o : config.attributeSettings.keySet())
			{
				//Take only the selected attribute value
				if (o.equals( config.attributeSelector.getSelectedItem() ) )
				{
					ConfigData.Internal internal = config.attributeSettings.get(o);
					Vector<String> indexValues = new Vector<>(
							Arrays.asList(internal.keys));
					Vector<Color> colorValues = new Vector<>();
					for (Object v : internal.values)
					{
						if (!internal.type.isAssignableFrom(Color.class))
						{
							continue;
						}
						colorValues.add((Color) v);
					}
	
					colorData.put(o.toString(), colorValues);
					indexData.put(o.toString(), indexValues);
				
				}

			}

			selection.put(config.attributeGroupName,
					(AttributeType) config.attributeSelector.getSelectedItem());
			colors.put(config.attributeGroupName, colorData);
			values.put(config.attributeGroupName, indexData);
			
		}

		return new SettingRelationColor(net, values, colors, selection, true);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RelationColorSaveElement))
			return;
		ConfigDialogTempCache.getInstance().addActiveElement(this);

		RelationColorSaveElement element = (RelationColorSaveElement) setting;

		if (element.getOldSelectedType() != null
				&& element.getOldSelectedColors() != null
				&& (element.getSelectedColor() == null && element.getPredefValues() == null)) // oldest
																														// compatible
																														// saveelement
		{
			System.out.println(element.getOldSelectedType()); // Relation
			System.out.println(element.getOldSelectedColors()); // Color[]

			List<ConfigData> dataList = new ArrayList<>();

			AttributeType selectedType = null;
			System.out.println(element.getActiveType()); // STANDARDRELATION

			for (AttributeType type : VennMaker.getInstance().getProject()
					.getAttributeTypes(element.getActiveType()))
			{
				if (type.toString().equals(element.getOldSelectedType()))
				{
					selectedType = type;
					break;
				}
			}

			if (selectedType == null || selectedType.getPredefinedValues() == null)
				return;

			Object[] preVals = selectedType.getPredefinedValues();
			String[] preValNames = new String[preVals.length];
			for (int i = 0; i < preValNames.length; ++i)
			{
				preValNames[i] = preVals[i].toString();
			}

			System.out.println(Arrays.toString(preVals)); // [positive, neutral,
																			// negative]

			HashMap<Object, Internal> attributeSettings = new HashMap<>();
			Object[] attributes = new Object[1];

			attributes[0] = selectedType;

			attributeSettings.put(selectedType, new ConfigData.Internal(
					preValNames, element.getOldSelectedColors(), Color.class));

			ConfigData data = new ConfigData(element.getActiveType(), attributes,
					attributeSettings);

			dataList.add(data);
			this.dataModel.setData(dataList);
		}
		else
		{
			Map<String, AttributeType> selections = element
					.getSelectedAttributes();
			Map<String, Map<String, Vector<Color>>> colors = element
					.getSelectedColor();
			Map<String, Map<String, Vector<String>>> values = element
					.getPredefValues();

			List<ConfigData> dataList = new ArrayList<ConfigData>();

			for (String attribCollector : values.keySet())
			{

				List<AttributeType> aTypes = VennMaker.getInstance().getProject()
						.getAttributeTypesDiscrete(attribCollector);
				HashMap<Object, ConfigData.Internal> internalDataMap = new HashMap<>();
				for (AttributeType typ : aTypes)
				{
					if (values.get(attribCollector).get(typ.toString()) != null)
					{
						String keyStrs[] = new String[values.get(attribCollector)
								.get(typ.toString()).size()];
						values.get(attribCollector).get(typ.toString()).toArray(keyStrs);
	
						internalDataMap.put(typ, new ConfigData.Internal(keyStrs, colors
								.get(attribCollector).get(typ.toString()).toArray(),
								Color.class));
					}
				}

				ConfigData data = new ConfigData(attribCollector, aTypes.toArray(),
						internalDataMap);

				dataList.add(data);

				if (selections != null)
				{
					data.attributeSelector.setSelectedItem(selections
							.get(attribCollector));
				}
			}

			this.dataModel.setData(dataList);
		}
	}

	@Override
	public SaveElement getSaveElement()
	{

		Map<String, Map<String, Vector<Color>>> colors = new HashMap<>();
		Map<String, Map<String, Vector<String>>> values = new HashMap<>();
		Map<String, AttributeType> selection = new HashMap<>();

		List<?> data = dataModel.getDataList();
		Iterator<?> it = data.iterator();

		while (it.hasNext())
		{
			Object setting = it.next();
			// skip if not configData
			if (!(setting instanceof ConfigData))
			{
				continue;
			}
			ConfigData config = (ConfigData) setting;

			HashMap<String, Vector<Color>> colorData = new HashMap<>();

			Map<String, Vector<String>> indexData = new HashMap<>();

			// fetch from config
			for (Object o : config.attributeSettings.keySet())
			{
				//Take only the selected attribute value
				if (o.equals( config.attributeSelector.getSelectedItem() ) )
				{
					ConfigData.Internal internal = config.attributeSettings.get(o);
					Vector<String> indexValues = new Vector<>(
							Arrays.asList(internal.keys));
					Vector<Color> colorValues = new Vector<>();
					for (Object v : internal.values)
					{
						if (!internal.type.isAssignableFrom(Color.class))
						{
							continue;
						}
						colorValues.add((Color) v);
					}
	
					colorData.put(o.toString(), colorValues);
					indexData.put(o.toString(), indexValues);
				}
			}

			selection.put(config.attributeGroupName,
					(AttributeType) config.attributeSelector.getSelectedItem());
			colors.put(config.attributeGroupName, colorData);
			values.put(config.attributeGroupName, indexData);
		}

		RelationColorSaveElement element = new RelationColorSaveElement(values,
				colors, selection);
		element.setElementName(CDialogRelationColorTable.class.getSimpleName());

		return element;

	}

	@Override
	public void refreshBeforeGetDialog()
	{
		super.refreshBeforeGetDialog();

		buildPanel();

	}

	List<?>	storedDataModel	= null;

	private void storePanel()
	{
		storedDataModel = dataModel.getDataList();

	}

	// restores previous status before refresh (on selections)
	private void restorePanel()
	{

		if (storedDataModel == null)
		{
			return;
		}

		AttributeType iteAtt = null;
		
		List<?> newData = dataModel.getDataList();

		Iterator<?> it = newData.iterator();

		while (it.hasNext())
		{
			Object o = it.next();
			if (!(o instanceof ConfigData))
				continue;

			ConfigData newConfig = (ConfigData) o;
			ConfigData retrieved = null;
			Object selection = null;

			for (Object cd : storedDataModel)
			{
				if (!(cd instanceof ConfigData))
					continue;

				ConfigData item = (ConfigData) cd;
				iteAtt = (AttributeType) item.attributeSelector
						.getSelectedItem();
								
				if (item.attributeGroupName.equals(newConfig.attributeGroupName)
						&& (item.attributeSettings.keySet().size() <= newConfig.attributeSettings
								.keySet().size()))
				{
					for (Object sel : newConfig.attributeSettings.keySet())
					{
						AttributeType selAtt = (AttributeType) sel;

						if (selAtt.getLabel().equals(iteAtt.getLabel()))
						{
							selection = sel;
							retrieved = item;
							break;
						}

					}
					retrieved = item;
					break;
				}
			}

			if (retrieved == null && selection == null)
			{
				continue;
			}
			newConfig.attributeSelector.setSelectedItem(selection);
			
			if ( (retrieved != null) && (retrieved.attributeSettings != null) )
			for (Object rO : retrieved.attributeSettings.keySet())
			{
				if (newConfig.attributeSettings.containsKey(rO))
				{			
					
					//If an additional predef value was added to the relation attribute
				if (iteAtt.getPredefinedValues().length > retrieved.attributeSettings.get(rO).keys.length)
					{						
						Vector<String> tempTypes = VennMaker.getInstance().getProject()
								.getAttributeCollectors();
						
						for (String attributeCollector : tempTypes)
						{
							if (attributeCollector.equals(newConfig.attributeGroupName))
							{
								Vector<Color> colors = new Vector<>();
								Vector<Object> keys = new Vector<>();

								for (Object preDef : iteAtt.getPredefinedValues())
								{
									/*
									colors.add(getNet().getRelationColorVisualizer(
											attributeCollector, iteAtt).getColor(preDef));
									*/
									colors.add(Color.BLUE);
									keys.add(preDef.toString());
								}

								String keyStrs[] = new String[keys.size()];
								keys.toArray(keyStrs);
								
								newConfig.attributeSelector.setSelectedItem(keys);
								return;
							}
						}
					}
				else
					{
					
						newConfig.attributeSettings.get(rO).values = retrieved.attributeSettings.get(rO).values;
					//	newConfig.attributeSettings.get(rO).keys = retrieved.attributeSettings.get(rO).keys;
						
					}
					
					
				}
			}
			
	//		 newConfig.attributeSettings = retrieved.attributeSettings;
		}

		storedDataModel = null;
	}
}
