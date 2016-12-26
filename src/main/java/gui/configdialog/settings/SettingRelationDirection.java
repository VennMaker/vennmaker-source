/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;

/**
 * 
 * 
 *         Setzt die Eigenschaft "directed" der angegebenen Relationsgruppe
 */
public class SettingRelationDirection implements ConfigDialogSetting
{

	private String		attributeCollector;

	private boolean	isDirected;

	public SettingRelationDirection(String attributeCollector, boolean isDirected)
	{
		this.attributeCollector = attributeCollector;
		this.isDirected = isDirected;
	}

	@Override
	public void set()
	{
		VennMaker.getInstance().getProject().getIsDirectedAttributeCollection()
				.put(this.attributeCollector, this.isDirected);
	}

}
