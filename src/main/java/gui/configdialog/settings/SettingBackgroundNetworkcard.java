/**
 * 
 */
package gui.configdialog.settings;

import java.util.Vector;

import data.BackgroundInfo;
import data.Netzwerk;

/**
 * Setting to change the background-network-card of a network.
 * 
 * 
 *
 */
public class SettingBackgroundNetworkcard implements ConfigDialogSetting
{
	private BackgroundInfo 		bgConfig;
	
	private Vector<Netzwerk> 	tmpNetworks;
	
	public SettingBackgroundNetworkcard(BackgroundInfo bgConfig, Vector<Netzwerk> tmpNetworks)
	{
		this.bgConfig = bgConfig;
		this.tmpNetworks = tmpNetworks;
	}
	@Override
	public void set()
	{
		bgConfig.addBackgroundNetworkcard(tmpNetworks);
	}
}
