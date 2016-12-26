/**
 * 
 */
package gui.configdialog.settings;

import java.util.HashMap;

import gui.VennMakerView;
import data.AttributeType;
import data.Netzwerk;

/**
 * Setting to apply changes of the legend
 * 
 * 
 */
public class SettingNetworkLegend implements ConfigDialogSetting
{
	private Netzwerk	net;

	private Boolean	drawActorSymbols;

	private Boolean	drawActorSizes;

	private Boolean	drawActorPies;
	
	private HashMap<AttributeType, Boolean> drawRelations = new HashMap<AttributeType, Boolean>();

	public SettingNetworkLegend(Netzwerk net, Boolean drawActorSymbols,
			Boolean drawActorSizes, Boolean drawActorPies, HashMap<AttributeType, Boolean> drawRelations)
	{
		this.net = net;
		this.drawActorSymbols = drawActorSymbols;
		this.drawActorSizes = drawActorSizes;
		this.drawActorPies = drawActorPies;
		this.drawRelations = drawRelations;
	}

	@Override
	public void set()
	{
		VennMakerView.getLegend(net).setShowActorSizes(drawActorSizes);
		VennMakerView.getLegend(net).setShowActorPies(drawActorPies);
		VennMakerView.getLegend(net).setShowActorSymbols(drawActorSymbols);
		
		VennMakerView.getLegend(net).setShowRelations(drawRelations);
	}

}
