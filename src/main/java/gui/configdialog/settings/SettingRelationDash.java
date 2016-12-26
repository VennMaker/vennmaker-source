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
import data.RelationDashingData;

/**
 * Setting to change the dashing of relations.
 * 
 * 
 * 
 */
public class SettingRelationDash implements ConfigDialogSetting
{
	private Netzwerk												net;

	private Map<String, AttributeType>						comboSelected;

	private Map<String, Vector<float[]>>					predefinedDashing;

	private Map<String, Vector<String>>						predefinedValues;

	private Map<String, Map<String, Vector<float[]>>>	dashing;

	private Map<String, Map<String, Vector<String>>>	predefValues;

	private boolean												paintDashingLegend;

	private Map<String, AttributeType>						aTypes;

	private Map<String, AttributeType>						activeTypes;

	public SettingRelationDash(
			Map<String, Map<String, Vector<float[]>>> dashing,
			Map<String, Map<String, Vector<String>>> predefValues, Netzwerk net)
	{
		this.dashing = dashing;
		this.predefValues = predefValues;
		this.paintDashingLegend = true;
		this.net = net;
		aTypes = new HashMap<String, AttributeType>();

		for (AttributeType aType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (aType.getType().equals("ACTOR"))
				continue;

			aTypes.put(aType.toString(), aType);
		}
	}

	public SettingRelationDash(
			Map<String, Map<String, Vector<float[]>>> dashing,
			Map<String, Map<String, Vector<String>>> predefValues,
			Map<String, AttributeType> selection, Netzwerk net)
	{
		this.dashing = dashing;
		this.predefValues = predefValues;
		this.paintDashingLegend = true;
		this.net = net;
		aTypes = new HashMap<String, AttributeType>();

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

		if (paintDashingLegend)
		{
			for (String atCollector : dashing.keySet())
			{
				Map<String, Vector<float[]>> dashing = this.dashing
						.get(atCollector);
				Map<String, Vector<String>> values = this.predefValues
						.get(atCollector);

				Map<Object, float[]> dashingToSet = new HashMap<Object, float[]>();

				for (String type : dashing.keySet())
				{
					Vector<float[]> dashingValues = dashing.get(type);
					Vector<String> predefValues = values.get(type);

					//Todo Workaround
					int laenge = dashingValues.size();
					
					if (predefValues.size() != dashingValues.size()){
						laenge = predefValues.size();
						if (dashingValues.size() > predefValues.size()) laenge = predefValues.size();
						if (predefValues.size() > dashingValues.size()) laenge = dashingValues.size();

						System.out.println("Error: settingRelationDash: different size");	
					}
					
					for (int i = 0; i < laenge; i++)
						dashingToSet.put(predefValues.get(i), dashingValues.get(i));

					FacadeVisualizer f = new FacadeVisualizer();

					f.newVisualizerAttributeType(aTypes.get(type), net,
							data.FacadeVisualizer.Visualization.RELATIONDASH,
							atCollector);
					f.updateVisualizerAttributeValues(aTypes.get(type), net);

					net.getRelationDashVisualizer(atCollector, aTypes.get(type))
							.setDasharrays(dashingToSet);

				}
			}

			policy.setDashingData(new RelationDashingData(predefinedDashing,
					predefinedValues));

		}
		else
		{
			for (String atCollector : comboSelected.keySet())			{
				net.getActiveRelationDashVisualizer(atCollector).setDasharrays(
						new HashMap<Object, float[]>());
			}
		}

		if (activeTypes != null)
		{
			for (String atCollector : activeTypes.keySet())
			{
				AttributeType selType = activeTypes.get(atCollector);
				net.setActiveRelationDashVisualizer(atCollector,
						net.getRelationDashVisualizer(atCollector, selType));
			}
		}

		policy.setPaintDashing(paintDashingLegend);
	}
}

