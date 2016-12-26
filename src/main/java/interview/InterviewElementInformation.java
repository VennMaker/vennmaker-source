package interview;

import interview.elements.InterviewElement;

import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class provides informations about the contents of an
 * InterviewElement
 * 
 * 
 * 
 */
public abstract class InterviewElementInformation
{
	private int													id;

	private int													parentId;

	private String												filter;

	private Integer[]											filterIndex;

	private String												elementName;

	private long												time;

	private Class<? extends InterviewElement>			parent;

	private Class<? extends InterviewElement>			elementClass;

	private List<InterviewElementInformation>			childInformation;

	private List<Class<? extends InterviewElement>>	childClasses;

	/**
	 * @return the parent
	 */
	public Class<? extends InterviewElement> getParent()
	{
		return parent;
	}

	/**
	 * @param parent
	 *           the parent to set
	 */
	public void setParent(Class<? extends InterviewElement> parent)
	{
		this.parent = parent;
	}

	/**
	 * @return the elementClass
	 */
	public Class<? extends InterviewElement> getElementClass()
	{
		return elementClass;
	}

	/**
	 * @param elementClass
	 *           the elementClass to set
	 */
	public void setElementClass(Class<? extends InterviewElement> elementClass)
	{
		this.elementClass = elementClass;
	}

	/**
	 * @return the childInformation
	 */
	public List<InterviewElementInformation> getChildInformation()
	{
		return childInformation;
	}

	/**
	 * @param childInformation
	 *           the childInformation to set
	 */
	public void setChildInformation(
			List<InterviewElementInformation> childInformation)
	{
		this.childInformation = childInformation;
	}

	/**
	 * @return the childClasses
	 */
	public List<Class<? extends InterviewElement>> getChildClasses()
	{
		return childClasses;
	}

	/**
	 * @param childClasses
	 *           the childClasses to set
	 */
	public void setChildClasses(
			List<Class<? extends InterviewElement>> childClasses)
	{
		this.childClasses = childClasses;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id
	 *           the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return the time of the element
	 */
	public long getTime()
	{
		return this.time;
	}

	/**
	 * @param time
	 *           the time to set
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/**
	 * @return the parentInd
	 */
	public int getParentId()
	{
		return parentId;
	}

	/**
	 * @param parentId
	 *           the parentInd to set
	 */
	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}

	public String getElementName()
	{
		return this.elementName;
	}

	public void setElementName(String elementName)
	{
		if (elementName == null || elementName.equals("")) //$NON-NLS-1$
			throw new IllegalArgumentException(
					"elementName must not null or empty"); //$NON-NLS-1$

		this.elementName = elementName;
	}

	/**
	 * Returns the filter for this <code>InterviewElement</code>
	 * 
	 * @return the filter for this <code>InterviewElement</code>
	 */
	public String getFilter()
	{
		return filter;
	}

	/**
	 * Sets the filter for this <code>InterviewElement</code>
	 * 
	 * @param filter
	 *           the filter for this <code>InterviewElement</code>
	 */
	public void setFilter(String filter)
	{
		System.out.println("->setFilter:"+filter);
		this.filter = filter;
	}

	/**
	 * Return the filter indexes of the filter for this
	 * <code>InterviewElement</code>
	 * 
	 * @return the filter indexes of the filter for this
	 *         <code>InterviewElement</code>
	 */
	public Integer[] getFilterIndex()
	{
		return filterIndex;
	}

	/**
	 * Sets the filter indexes of the filter for this
	 * <code>InterviewElement</code>
	 * 
	 * @param filterIndex
	 *           the filter indexes of the filter for this
	 *           <code>InterviewElement</code>
	 */
	public void setFilterIndex(Integer[] filterIndex)
	{
		this.filterIndex = filterIndex;
	}

	/**
	 * Set the <code>InterviewElementInformation</code> of all children of this
	 * <code>InterviewElement</code>
	 * 
	 * @param children
	 *           all children of this <code>InterviewElement</code>
	 */
	public void createChildInformation(List<InterviewElement> children)
	{
		if (childClasses == null)
			childClasses = new ArrayList<Class<? extends InterviewElement>>();

		if (childInformation == null)
			childInformation = new ArrayList<InterviewElementInformation>();

		for (InterviewElement child : children)
		{
			childClasses.add(child.getClass());
			childInformation.add(child.getElementInfo());
		}
	}

	public void setParentInformation(InterviewElement parent)
	{
		if (parent == null)
		{
			return;
		}

		setParent(parent.getClass());
		setParentId(parent.getId());
	}

}
