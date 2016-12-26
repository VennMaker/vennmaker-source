package gui.configdialog.save;

import java.util.HashMap;

import data.AttributeType;

public class LegendSaveElement extends SaveElement
{
	private Boolean									drawActorSymbols;

	private Boolean									drawActorSizes;

	private Boolean									drawActorPies;

	private HashMap<AttributeType, Boolean>	drawRelations	= new HashMap<AttributeType, Boolean>();

	public LegendSaveElement(Boolean drawActorSymbols, Boolean drawActorSizes,
			Boolean drawActorPies, HashMap<AttributeType, Boolean> drawRelations)
	{
		this.drawActorSymbols = drawActorSymbols;
		this.drawActorSizes = drawActorSizes;
		this.drawActorPies = drawActorPies;
		this.drawRelations = drawRelations;
	}

	public Boolean getDrawSymbols()
	{
		return this.drawActorSymbols;
	}

	public Boolean getDrawSizes()
	{
		return this.drawActorSizes;
	}

	public Boolean getDrawPies()
	{
		return this.drawActorPies;
	}

	public HashMap<AttributeType, Boolean> getDrawRelations()
	{
		return this.drawRelations;
	}

}
