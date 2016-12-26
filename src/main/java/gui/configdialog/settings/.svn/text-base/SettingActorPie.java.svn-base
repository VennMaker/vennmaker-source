/**
 * 
 */
package gui.configdialog.settings;

import java.util.Map;

import data.AttributeType;
import data.Netzwerk;

/**
 * Setting to apply changes of the actor pie.
 * 
 * 
 */
public class SettingActorPie implements ConfigDialogSetting
{
	private Netzwerk 					net;
	
	private Map<AttributeType,Object> 	typeAndSelection;
	
	private Map<AttributeType,Object> 	sectorColor;
	
	public SettingActorPie(Netzwerk net, Map<AttributeType, Object> typeAndSelection, Map<AttributeType,Object> sectorColor)
	{
		this.net = net;
		this.typeAndSelection = typeAndSelection;
		this.sectorColor = sectorColor;
	}
	
	@Override
	public void set()
	{
		net.getActorSectorVisualizer().setAttributeTypeAndSelection(typeAndSelection);
		net.getActorSectorVisualizer().setSectorColor(sectorColor);
	}

}
