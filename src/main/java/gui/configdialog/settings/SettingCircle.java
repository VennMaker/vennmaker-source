/**
 * 
 */
package gui.configdialog.settings;

import gui.VennMaker;
import gui.VennMakerView;

import java.util.List;

import data.AttributeType;
import data.BackgroundInfo;
import data.Netzwerk;

/**
 * Setting to change the circles of a network-card.
 * 
 * 
 *
 */
public class SettingCircle implements ConfigDialogSetting
{
	private AttributeType 	circleAttr;
	
	private boolean 		isAsc;
	
	private Netzwerk 		net;
	
	public SettingCircle(AttributeType circleAttr, boolean isAsc, Netzwerk net)
	{
		this.circleAttr = circleAttr;
		this.isAsc = isAsc;
		this.net = net;
	}
	
	@Override
	public void set()
	{
		BackgroundInfo bgConfig = net.getHintergrund();
		bgConfig.setCirclesAsc(isAsc);
		bgConfig.setCircleAttribute(circleAttr);
		int num = 0;
		String label = null;
		if(circleAttr!=null)
		{
			num = circleAttr.getPredefinedValues().length;
			if(num>0)
				label = circleAttr.getLabel();
			List<String> circles = bgConfig.getCircles();
			circles.clear();
			for(int i=num-1; i>=0; i--)
			{
				circles.add(circleAttr.getPredefinedValue(i).toString());
			}
			bgConfig.setCircles(circles);
		}
		bgConfig.setNumCircles(num);
		bgConfig.setCirclesLabel(label);
		VennMakerView v = VennMaker.getInstance().getViewOfNetwork(net);
		if(v != null)
		{
			v.updateView();
			v.updateSectorAndCircleAttributes();
		}
//		else
//		{
//			Netzwerk clone = net.cloneNetwork(net.getBezeichnung()+"-clone");
//			VennMakerView view = VennMaker.getInstance().getViewOfNetwork(clone);
//			
//			if(view != null)
//			{
//				view.updateView();
//				view.updateSectorAndCircleAttributes();
//			}
//		}
	}
	
	/**
	 * delete circles from map
	 * 
	 */
	public void delete()
	{
		BackgroundInfo bgConfig = net.getHintergrund();
		bgConfig.setNumCircles(0);
//		bgConfig.setCirclesLabel(label);
		VennMakerView v = VennMaker.getInstance().getViewOfNetwork(net);
		if(v != null)
		{
			v.updateView();
			v.updateSectorAndCircleAttributes();
		}
		bgConfig.setCircleAttribute(circleAttr);
	}
}
