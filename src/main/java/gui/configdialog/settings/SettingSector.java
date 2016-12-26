/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;
import gui.VennMakerView;

import java.util.List;

import data.AttributeType;
import data.BackgroundInfo.SectorInfo;
import data.Netzwerk;

/**
 * Setting to apply / change sectors on a network-map.
 * 
 * 
 * 
 */
public class SettingSector implements ConfigDialogSetting
{
	private Netzwerk				net;

	private List<SectorInfo>	sectors;

	private AttributeType		sectorAttr;

	private double					alpha;

	public SettingSector(Netzwerk net, List<SectorInfo> sectors,
			AttributeType sectorAttr, double alpha)
	{
		this.net = net;
		this.sectors = sectors;
		this.sectorAttr = sectorAttr;
		this.alpha = alpha;
	}

	@Override
	public void set()
	{
		int num = sectors.size();
		net.getHintergrund().setNumSectors(num);
		if (sectorAttr != null)
		{
			net.getHintergrund().setSectorLabel(sectorAttr.getLabel());
			net.getHintergrund().setSectorAttribute(sectorAttr);
			net.getHintergrund().clearTemporarySectors();
			for (int i = 0; i < num; i++)
			{
				SectorInfo si = net.getHintergrund().getSector(i);
				si.label = sectors.get(i).label;
				si.sectorColor = sectors.get(i).sectorColor;
				si.width = sectors.get(i).width;
				si.off = sectors.get(i).off;
			}
			net.getHintergrund().alpha = alpha;

			VennMakerView v = VennMaker.getInstance().getViewOfNetwork(net);
			if (v != null)
				v.updateSectorAndCircleAttributes();
		}
		else
			net.getHintergrund().setSectorAttribute(null);
	}

}
