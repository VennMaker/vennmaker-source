package gui.configdialog.elements;

import gui.ConfigData;
import gui.ConfigModel;
import gui.ConfigPanel;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RelationDashSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRelationDash;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import data.AttributeType;
import data.RelationDashVisualizer;

public class CDialogRelationDashTable extends ConfigDialogElement
{
	private static final long	serialVersionUID	= 1L;

	private ConfigModel			dataModel;

	private boolean				firstBuild			= true;

	public CDialogRelationDashTable()
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

				Vector<float[]> dashings = new Vector<>();
				Vector<Object> keys = new Vector<>();

				for (Object preDef : typ.getPredefinedValues())
				{

					dashings.add(getNet().getRelationDashVisualizer(
							attributeCollector, typ).getDasharray(preDef));
					keys.add(preDef.toString());
				}

				String keyStrs[] = new String[keys.size()];
				keys.toArray(keyStrs);

				internalDataMap.put(typ,
						new ConfigData.Internal(keyStrs, dashings.toArray(),
								float[].class));
			}

			dataList.add(new ConfigData(attributeCollector, aTypes.toArray(),
					internalDataMap));

		}

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
			RelationDashVisualizer dashViz = net
					.getActiveRelationDashVisualizer(data.attributeGroupName);
			if (data.attributeSettings.keySet().contains(
					dashViz.getAttributeType()))
			{
				data.attributeSelector.setSelectedItem(dashViz.getAttributeType());
			}
		}
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		Map<String, Map<String, Vector<float[]>>> dashings = new HashMap<>();
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

			HashMap<String, Vector<float[]>> dashingData = new HashMap<>();

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
					Vector<float[]> dashingValues = new Vector<>();
					for (Object v : internal.values)
					{
						if (!internal.type.isAssignableFrom(float[].class))
						{
							continue;
						}
						dashingValues.add((float[]) v);
					}
	
					dashingData.put(o.toString(), dashingValues);
					indexData.put(o.toString(), indexValues);
				}
			}
			selection.put(config.attributeGroupName,
					(AttributeType) config.attributeSelector.getSelectedItem());
			dashings.put(config.attributeGroupName, dashingData);
			values.put(config.attributeGroupName, indexData);
		}

		return new SettingRelationDash(dashings, values, selection, net);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RelationDashSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);

		RelationDashSaveElement element = (RelationDashSaveElement) setting;
		// TODO: implement old old version
		if (element.getOldPredefinedValues() != null
				&& element.getOldPredefinedDashing() != null)
		{
			// load old version
		}
		else
		{
			Map<String, AttributeType> selections = element.getComboSelected();
			Map<String, Map<String, Vector<float[]>>> colors = element
					.getDashing();
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
								float[].class));
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

		Map<String, Map<String, Vector<float[]>>> dashings = new HashMap<>();
		Map<String, Map<String, Vector<String>>> values = new HashMap<>();
		HashMap<String, AttributeType> selection = new HashMap<>();

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

			HashMap<String, Vector<float[]>> dashData = new HashMap<>();

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
					Vector<float[]> dashValues = new Vector<>();
					for (Object v : internal.values)
					{
						if (!internal.type.isAssignableFrom(float[].class))
						{
							continue;
						}
						dashValues.add((float[]) v);
					}
	
					dashData.put(o.toString(), dashValues);
					indexData.put(o.toString(), indexValues);
				}
			}
			selection.put(config.attributeGroupName,
					(AttributeType) config.attributeSelector.getSelectedItem());
			dashings.put(config.attributeGroupName, dashData);
			values.put(config.attributeGroupName, indexData);
		}
		// RelationDashSaveElement element = new RelationDashSaveElement(dashings,
		// values, selection, net);
		RelationDashSaveElement element = new RelationDashSaveElement(dashings,
				values, selection, net);
		element.setElementName(CDialogRelationDashTable.class.getSimpleName());

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
			return;

		List<?> newData = dataModel.getDataList();
		Iterator<?> it = newData.iterator();
		AttributeType iteAtt = null;

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

				if (item.attributeGroupName.equals(newConfig.attributeGroupName)
						&& (item.attributeSettings.keySet().size() <= newConfig.attributeSettings
								.keySet().size()))
				{
					for (Object sel : newConfig.attributeSettings.keySet())
					{
						AttributeType selAtt = (AttributeType) sel;
						iteAtt = (AttributeType) item.attributeSelector
								.getSelectedItem();
						if ( (selAtt != null) && (iteAtt != null) )
						if (selAtt.getLabel().equals(iteAtt.getLabel()))
						{
							selection = sel;
							break;
						}

					}
					retrieved = item;
					break;
				}
			}

			if (retrieved == null && selection == null)
				continue;
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
								Vector<Object> keys = new Vector<>();

								for (Object preDef : iteAtt.getPredefinedValues())
								{
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
					}
				}
			}
			// newConfig.attributeSettings = retrieved.attributeSettings;
		}

		storedDataModel = null;
	}

}
