package gui.configdialog.save;

import java.util.List;
import java.util.Map;

import data.AttributeType;
import data.BackgroundInfo.SectorInfo;
import data.Netzwerk;

public class SectorSaveElement extends SaveElement
{

	public Netzwerk			net;

	public Map<AttributeType, List<SectorInfo>> sectorCache;

	private double				alpha;
	
	private String			sectorAttribute;

	public SectorSaveElement(Netzwerk net,
			Map<AttributeType, List<SectorInfo>> sectorCache, double alpha,
			String sectorAttribute)
	{
		this.net = net;
		this.sectorCache = sectorCache;
		this.alpha = alpha;
		this.sectorAttribute = sectorAttribute;
	}

	public Netzwerk getNet()
	{
		return net;
	}

	public Map<AttributeType, List<SectorInfo>> getSectorCache()
	{
		return sectorCache;
	}

	public double getAlpha()
	{
		return alpha;
	}

	public String getSectorAttribute()
	{
		return sectorAttribute;
	}

}
