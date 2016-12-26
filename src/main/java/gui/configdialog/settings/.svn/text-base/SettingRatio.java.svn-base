/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

/**
 * Setting to change the view-area ratio of VennMaker.
 * 
 * 
 *
 */
public class SettingRatio implements ConfigDialogSetting
{
	private float ratio;
	
	public SettingRatio(float ratio)
	{
		this.ratio = ratio;
	}
	
	@Override
	public void set()
	{
		VennMaker vi = VennMaker.getInstance();
		vi.getConfig().setViewAreaRatio(ratio);
		vi.getProject().setViewAreaRatio(ratio);
	}
}
