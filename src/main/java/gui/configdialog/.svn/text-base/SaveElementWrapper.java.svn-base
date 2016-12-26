 package gui.configdialog;

import gui.VennMaker;
import gui.configdialog.save.NetworkSaveElement;
import gui.configdialog.save.ProjectSaveElement;

import java.util.List;

/**
 * Objects of this class contain all information about the elements which should
 * be saved or loaded from a template
 * 
 * 
 * 
 */
public class SaveElementWrapper
{
	private String							version;

	private String							revision;

	private List<NetworkSaveElement>	networkSaveElements;

	private ProjectSaveElement			projectSaveElement;

	public SaveElementWrapper(List<NetworkSaveElement> networkSaveElements,
			ProjectSaveElement projectSaveElement)
	{
		this.networkSaveElements = networkSaveElements;
		this.projectSaveElement = projectSaveElement;

		this.version = VennMaker.VERSION;
		this.revision = VennMaker.REVISION;
	}

	/**
	 * @return the networkSaveElements
	 */
	public List<NetworkSaveElement> getNetworkSaveElements()
	{
		return networkSaveElements;
	}

	/**
	 * @param networkSaveElements
	 *           the networkSaveElements to set
	 */
	public void setNetworkSaveElements(
			List<NetworkSaveElement> networkSaveElements)
	{
		this.networkSaveElements = networkSaveElements;
	}

	/**
	 * @return the projectSaveElements
	 */
	public ProjectSaveElement getProjectSaveElement()
	{
		return projectSaveElement;
	}

	/**
	 * @param projectSaveElements
	 *           the projectSaveElements to set
	 */
	public void setProjectSaveElements(ProjectSaveElement projectSaveElement)
	{
		this.projectSaveElement = projectSaveElement;
	}
}
