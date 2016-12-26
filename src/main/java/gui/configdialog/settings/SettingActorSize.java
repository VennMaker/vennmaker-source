/**
 * 
 */
package gui.configdialog.settings;

import java.util.Map;

import data.AttributeType;
import data.FacadeVisualizer;
import data.Netzwerk;

/**
 * Setting to change the actor size.
 * 
 * 
 */
public class SettingActorSize implements ConfigDialogSetting
{
	private AttributeType 			aType;
	
	private Map<Object,Integer> 	sizes;
	
	private Netzwerk 				net;
	
	public SettingActorSize(AttributeType aType, Map<Object,Integer> sizes, Netzwerk net)
	{
		this.aType = aType;
		this.sizes = sizes;
		this.net = net;
	}
	
	@Override
	public void set()
	{
		FacadeVisualizer fv = new FacadeVisualizer();
		fv.newVisualizerAttributeType(aType, net, FacadeVisualizer.Visualization.SIZE, "ACTOR");
		net.getActorSizeVisualizer().setSizes(sizes);
	}
}
