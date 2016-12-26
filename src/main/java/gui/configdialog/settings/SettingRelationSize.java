/**
 * 
 */
package gui.configdialog.settings;

import gui.PaintLegendPolicy;
import gui.VennMaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import data.AttributeType;
import data.FacadeVisualizer;
import data.Netzwerk;

/**
 * Setting to change the sizes of relations.
 * 
 * 
 * 
 */
public class SettingRelationSize implements ConfigDialogSetting
{
	private Netzwerk												net;

	private Map<String, AttributeType>						comboSelected;

	private boolean												paintSizeLegend;

	private Map<String, Map<String, Vector<String>>>	predefValues;

	private Map<String, Map<String, Vector<Integer>>>	selectedSizes;

	private Map<String, AttributeType>						aTypes;

	Map<String, AttributeType>									activeTypes;

	public SettingRelationSize(Netzwerk net,
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, Map<String, Vector<Integer>>> selectedSizes,
			boolean paintSizeLegend)
	{
		this.net = net;
		this.predefValues = predefValues;
		this.selectedSizes = selectedSizes;
		this.paintSizeLegend = paintSizeLegend;
		this.aTypes = new HashMap<String, AttributeType>();

		for (AttributeType aType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (aType.getType().equals("ACTOR"))
				continue;

			aTypes.put(aType.toString(), aType);
		}
	}

	public SettingRelationSize(Netzwerk net,
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, Map<String, Vector<Integer>>> selectedSizes,
			Map<String, AttributeType> selection, 
			boolean paintSizeLegend)
	{
		this.net = net;
		this.predefValues = predefValues;
		this.selectedSizes = selectedSizes;
		this.paintSizeLegend = paintSizeLegend;
		this.aTypes = new HashMap<String, AttributeType>();

		for (AttributeType aType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (aType.getType().equals("ACTOR"))
				continue;

			aTypes.put(aType.toString(), aType);
		}
		this.activeTypes = selection;
	}

	@Override
	public void set()
	{
		PaintLegendPolicy policy = VennMaker.getInstance().getProject()
				.getPaintLegendPolicy();

		// if(policy.getSizeData() == null)
		// policy.setSizeData(new RelationSizeData(predefinedSizes,
		// predefinedValues));

		if (paintSizeLegend)
		{
			for (String atCollector : selectedSizes.keySet())
			{
				Map<String, Vector<Integer>> sizes = this.selectedSizes
						.get(atCollector);
				Map<String, Vector<String>> values = this.predefValues
						.get(atCollector);

				Map<Object, Integer> sizesToSet = new HashMap<Object, Integer>();

				for (String type : sizes.keySet())
				{
					Vector<Integer> sizeValues = sizes.get(type);
					Vector<String> predefValues = values.get(type);

					//Todo Workaround
					int laenge = sizeValues.size();
					if (predefValues.size() != sizeValues.size()){
						laenge = predefValues.size();
						if (sizeValues.size() > predefValues.size()) laenge = predefValues.size();
						if (predefValues.size() > sizeValues.size()) laenge = sizeValues.size();

						System.out.println("Error: settingRelationSize: different size");	
					}
					
					for (int i = 0; i < laenge; i++)
						sizesToSet.put(predefValues.get(i), sizeValues.get(i));

					FacadeVisualizer f = new FacadeVisualizer();

					f.newVisualizerAttributeType(aTypes.get(type), net,
							data.FacadeVisualizer.Visualization.RELATIONDASH,
							atCollector);
					f.updateVisualizerAttributeValues(aTypes.get(type), net);

					net.getRelationSizeVisualizer(atCollector, aTypes.get(type))
							.setSizes(sizesToSet);

				}
			}

			// policy.setSizeData(new RelationSizeData(predefinedSizes,
			// predefinedValues));
		}
		else
		{
			for (String atCollector : comboSelected.keySet())
				net.getActiveRelationSizeVisualizer(atCollector).setSizes(
						new HashMap<Object, Integer>());
		}

		if (this.activeTypes != null)
		{
			for (String atCollector : this.activeTypes.keySet())
			{

				AttributeType selType = this.activeTypes.get(atCollector);
				net.setActiveRelationSizeVisualizer(atCollector,
						net.getRelationSizeVisualizer(atCollector, selType));
			}
		}
		policy.setPaintThickness(paintSizeLegend);
	}
}
