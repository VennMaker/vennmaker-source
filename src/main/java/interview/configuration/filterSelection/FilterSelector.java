package interview.configuration.filterSelection;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import data.Akteur;

/**
 * 
 * Returns all Actors matching the selected filter
 * 
 * 
 */
public interface FilterSelector 
{
	/**
	 * returns a list filled with the actors matching the filter
	 */
	public List<Akteur> getFilteredActors();
	
	/**
	 * enables dummy actors for preview
	 */
	public void enableDummyActors(boolean b);
	
	/**
	 * checks if dummy actors are enabeld
	 * @return true if dummy actors are enabled, false otherwise
	 */
	public boolean isDummyActorsEnabled();
	
	/**
	 * returns the panel to select a filter
	 */
	public JPanel getConfigurationPanel();
	
	/**
	 * Returns the filter
	 * @return the filter
	 */
	public String getFilter();
	
	/**
	 * Sets the filter
	 * @param filter
	 */
	public void setFilter(String filter);
	
	/**
	 * Returns the indicies for the selected filter values
	 * @return  indicies for the selected filter values
	 */
	public ArrayList<Integer> getFilterIndex();
	
	/**
	 * Sets the indicies for the selected filter values
	 * @param filterIndex indicies for the selected filter values
	 */
	public void setFilterIndex(ArrayList<Integer> filterIndex);
}
