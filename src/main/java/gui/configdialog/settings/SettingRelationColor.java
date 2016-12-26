/**
 * 
 */
package gui.configdialog.settings;

import gui.PaintLegendPolicy;
import gui.VennMaker;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import data.AttributeType;
import data.FacadeVisualizer;
import data.Netzwerk;
import data.RelationColorData;

/**
 * Setting to change the coloring of relations.
 * 
 * 
 * 
 */
public class SettingRelationColor implements ConfigDialogSetting
{
	private Netzwerk												net;

	private Map<String, AttributeType>						comboSelected;

	private Map<String, Vector<Color>>						predefinedColors;

	private Map<String, Vector<String>>						predefinedValues;

	private Map<String, Map<String, Vector<Color>>>		colors;

	private Map<String, Map<String, Vector<String>>>	predefValues;

	private boolean												paintColorLegend;

	private Map<String, AttributeType>						aTypes;

	private Map<String, AttributeType>						activeTypes;

	public SettingRelationColor(Netzwerk net,
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, Map<String, Vector<Color>>> colors,
			boolean paintColorLegend)
	{
		this.net = net;
		this.paintColorLegend = paintColorLegend;
		this.colors = colors;
		this.predefValues = predefValues;
		this.aTypes = new HashMap<String, AttributeType>();

		for (AttributeType aType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (aType.getType().equals("ACTOR"))
				continue;

			aTypes.put(aType.toString(), aType);
		}

	}

	public SettingRelationColor(Netzwerk net,
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, Map<String, Vector<Color>>> colors,
			Map<String, AttributeType> selection, boolean paintColorLegend)
	{
		this.net = net;
		this.paintColorLegend = paintColorLegend;
		this.colors = colors;
		this.predefValues = predefValues;
		this.aTypes = new HashMap<String, AttributeType>();

		for (AttributeType aType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (aType.getType().equals("ACTOR"))
				continue;

			aTypes.put(aType.toString(), aType);
		}
		activeTypes = selection;
	}

	@Override
	public void set()
	{
		
		PaintLegendPolicy policy = VennMaker.getInstance().getProject()
				.getPaintLegendPolicy();
		
		if (policy.getColorData() == null)
			policy.setColorData(new RelationColorData(predefinedColors,
					predefinedValues));

		if (this.paintColorLegend)
		{
			for (String atCollector : this.colors.keySet())
			{
				Map<String, Vector<Color>> attributeColors = this.colors
						.get(atCollector);
				Map<String, Vector<String>> attributeValues = this.predefValues
						.get(atCollector);

				Map<Object, Color> colorsToSet = new HashMap<Object, Color>();

				for (String type : attributeColors.keySet())
				{
					Vector<Color> attColors = attributeColors.get(type);
					Vector<String> values = attributeValues.get(type);

					//Todo Workaround
					int laenge = attColors.size();
					
					if (values.size() != attColors.size())
					{
						laenge = attColors.size();
						if (attColors.size() > values.size()) laenge = values.size();
						if (values.size() > attColors.size()) laenge = attColors.size();
					}
					for (int i = 0; i < laenge; i++)
					{					
						colorsToSet.put(values.get(i), attColors.get(i));
					}
					
					FacadeVisualizer f = new FacadeVisualizer();
					
					f.newVisualizerAttributeType(aTypes.get(type), net,
							data.FacadeVisualizer.Visualization.RELATIONCOLOR,
							atCollector);
					f.updateVisualizerAttributeValues(aTypes.get(type), net);

					net.getRelationColorVisualizer(atCollector, aTypes.get(type))
							.setColors(colorsToSet);
				}

			}

			policy.setColorData(new RelationColorData(predefinedColors,
					predefinedValues));
			
		}
		else
		{
			for (String atCollector : comboSelected.keySet()){
				net.getActiveRelationColorVisualizer(atCollector).setColors(
						new HashMap<Object, Color>());
			}
		}

		if (activeTypes != null)
		{
			for (String atCollector : activeTypes.keySet())
			{
				AttributeType selType = activeTypes.get(atCollector);
				net.setActiveRelationColorVisualizer(atCollector,
						net.getRelationColorVisualizer(atCollector, selType));
			}
		}

		policy.setPaintColor(paintColorLegend);
	}
}



