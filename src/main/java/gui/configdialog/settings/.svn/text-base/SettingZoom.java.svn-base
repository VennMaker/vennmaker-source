/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

/**
 * Setting to change the text zoom factor.
 * 
 * 
 *
 */
public class SettingZoom implements ConfigDialogSetting
{
	private float textZoom;
	
	public SettingZoom(float textZoom)
	{
		this.textZoom = textZoom;
	}
	
	@Override
	public void set()
	{
		VennMaker.getInstance().getProject().setTextZoom(textZoom);
	}
}
