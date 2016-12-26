package gui.configdialog.save;

import java.util.ArrayList;
import java.util.List;

/**
 * This element keeps all SaveElements wich are repsonsible for
 * project wide settings
 * 
 *
 */
public class ProjectSaveElement
{
	private List<SaveElement> projectElements;
	
	public ProjectSaveElement()
	{
		this.projectElements = new ArrayList<SaveElement>();
	}
	
	/**
	 * Gets all ProjectElements
	 * @return all ProjectElements
	 */
	public List<SaveElement> getProjectElements()
	{
		return this.projectElements;
	}
	
	/**
	 * Adds a ProjectElement
	 * @param elem Element to add
	 * @throws IllegalArgumentException
	 */
	public void addProjectElement(SaveElement elem)
	{
		if(elem == null)
			return;
		
		this.projectElements.add(elem);
	}
}
