package interview.elements;

import interview.configuration.NameInfoSelection.NameInfoSelector;
import interview.configuration.attributesSelection.AttributeSelector;
import interview.configuration.filterSelection.FilterSelector;

import java.util.List;

import data.Akteur;

public class StandardElementDemandingActors extends StandardElement
{

	public StandardElementDemandingActors(NameInfoSelector nSelector,
			FilterSelector fSelector, AttributeSelector aSelector,
			boolean hasPreview)
	{
		super(nSelector, fSelector, aSelector, hasPreview);
	}

	@Override
	public boolean shouldBeSkipped()
	{
System.out.println("shouldBeSkipped A");	
		if (fSelector != null)
		{
System.out.println("shouldBeSkipped B");			
			List<Akteur> filteredActors = fSelector.getFilteredActors();
			if (filteredActors == null || filteredActors.size() <= 0)
			{
System.out.println("shouldBeSkipped C");
				return true;
			}
		}
		if (getActors().size() < 1)
		{
System.out.println("shouldBeSkipped D");			
			return true;
		}
System.out.println("shouldBeSkipped E");		
		return false;
	}

}
