package interview.configuration.filterSelection;

import gui.VennMaker;

import java.util.List;

import data.Akteur;

/**
 * Panel to select the filter performed for Alteri-selection
 * 
 * same as FilterPanel but only for alteri
 * 
 * 
 *
 */
public class AlteriFilterPanel extends FilterPanel
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public AlteriFilterPanel()
	{
		super("InterviewElement.FilterDescription");
	}
	
	@Override
	public List<Akteur> getFilteredActors()
	{
		if(dummyCreationEnabled)
			return getDummyFilteredActors();
System.out.println("AlteriFilterPanel...."+filter);
		InterviewFilterDialog fid = new InterviewFilterDialog(this, filter, false);
		List<Akteur> actors = fid.getActors();
		fid.dispose();
		
		Akteur ego = VennMaker.getInstance().getProject().getEgo();
		if(actors.contains(ego))
			actors.remove(ego);
		
		return actors;
	}
}
