package interview.elements;

import gui.Messages;
import interview.InterviewElementInformation;
import interview.InterviewTreeModel;
import interview.configuration.filterSelection.FilterSelector;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import data.Akteur;
import data.Reflection;

/**
 * This is the abstract superclass of all interview elements. Every interview
 * element has to implement its abstract methods
 * */

public abstract class InterviewElement implements Serializable, Transferable,
		Cloneable
{
	private static final long			serialVersionUID	= 1L;

	/**
	 * The name of this node
	 */
	private String							elementName;

	private String							elementNameInTree;

	private String							description;

	private int								id;

	protected boolean						conditionIsTrue;

	private int								jumpToElement		= -1;

	public int								internalPointer;

	private int								childPointer;

	public List<Akteur>					actors;

	public List<InterviewElement>		children;

	public InterviewElement				parent;

	private boolean						isChild;

	protected boolean						isMetaElement;

	protected boolean						hasPreview;

	protected FilterSelector			fSelector;

	/**
	 * timevalue, to measure the time, the user needed to work on this element
	 * (measured in ms)
	 */
	private long							time					= 0;

	/**
	 * boolean variable to check, if timing is in progress (needed to return the
	 * correct value, when timing is active and the user wants to check for the
	 * current time
	 */
	private boolean						timing				= false;

	public static final DataFlavor	INTERVIEW_FLAVOR	= new DataFlavor(
																				InterviewElement.class,
																				"Interview_Element");

	static DataFlavor						flavors[]			= { INTERVIEW_FLAVOR };

	public InterviewElement()
	{
		children = new ArrayList<InterviewElement>();
		actors = new ArrayList<Akteur>();
	}

	public void set(String name)
	{
		if (name == null)
			return;

		this.elementName = name;
	}

	/**
	 * Set the children of this element
	 * 
	 * @param children
	 *           - the children of this element
	 */
	public void setChildren(List<InterviewElement> children)
	{
		this.children = new ArrayList<InterviewElement>();

		for (InterviewElement child : children)
			this.children.add(child);
	}

	/**
	 * Returns the children of this <code>InterviewElement</code>
	 * 
	 * @return the children of this <code>InterviewElement</code>
	 */
	public List<InterviewElement> getChildren()
	{
		return this.children;
	}

	/**
	 * Set the actors of this element
	 * 
	 * @param actors
	 *           - actors of this element
	 */
	public void setActors(List<Akteur> actors)
	{
		this.actors.clear();
		for (Akteur act : actors)
		{
			if (!this.actors.contains(act))
				this.actors.add(act);
		}
	}

	/**
	 * Returns the current actors of this <code>InterviewElement</code>
	 * 
	 * @return the current actors of this <code>InterviewElement</code>
	 */
	public List<Akteur> getActors()
	{
		return this.actors;
	}

	/**
	 * Returns the currently selected actor of this <code>InterviewElement</code>
	 * 
	 * @return the currently selected actor of this <code>InterviewElement</code>
	 */
	public Akteur getCurrentActor()
	{
		return this.actors.get(getInternalPointerValue());
	}

	@Override
	public String toString()
	{
		return this.elementNameInTree;
	}

	public boolean isConditionTrue()
	{
		return this.conditionIsTrue;
	}

	/**
	 * The index to which the controller jumps if a previous condition was true
	 * Standard value is -1 -> no jump
	 * */
	public void setElementToJump(int index)
	{
		this.jumpToElement = index;
	}

	public int getElementToJump()
	{
		return this.jumpToElement;
	}

	/**
	 * Returns the size of the internal list The internal list contains actors,
	 * to which this element is called
	 * 
	 * @return size of internal list
	 */
	public int getInternalListSize()
	{
		if (actors != null)
			return actors.size();
		else
			return 0;
	}

	/**
	 * Gets the value of the internal list pointer
	 * 
	 * @return value of internal list pointer
	 */
	public int getInternalPointerValue()
	{
		return this.internalPointer;
	}

	/**
	 * Resets the internal list pointer
	 */
	public void resetInternalPointer()
	{
		this.internalPointer = 0;
	}

	/**
	 * Resets the internal actors list
	 */
	public void resetInternalActorList()
	{
		if (this.actors != null)
			this.actors.clear();
	}

	/**
	 * Gets the next actor in the internal list
	 * 
	 * @return next value in internal list
	 */
	public InterviewElement getNextElementInList()
	{
		++this.internalPointer;
		getControllerDialog();
		return this;
	}

	/**
	 * Returns the previous element in list
	 * 
	 * @return the previous element in list
	 */
	public InterviewElement getPreviousElementInList()
	{
		--this.internalPointer;
		getControllerDialog();
		return this;
	}

	/**
	 * Sets the internal list pointer
	 * 
	 * @param n
	 *           value of the internal pointer
	 */
	public void setInternalPointer(int n)
	{
		if (n < 0)
			throw new IllegalArgumentException(
					Messages
							.getString("InterviewElement.InternalPointerSmallerZero"));

		this.internalPointer = n;
	}

	/**
	 * Adds a child to this InterviewElement. By adding childs to a
	 * InterviewElement you can get a tree structure
	 * 
	 * @param elem
	 *           InterviewElement to add as child
	 * @throws IllegalArgumentException
	 */

	public void addChild(InterviewElement elem)
	{
		if (elem == null)
			throw new IllegalArgumentException(
					Messages.getString("InterviewElement.ElementShouldNotBeNull")); //$NON-NLS-1$

		elem.setParent(this);
		elem.setChildStatus(true);
		this.children.add(elem);
	}

	/**
	 * Adds a child to this InterviewElement at the specified index
	 * 
	 * @param elem
	 *           InterviewElement to add
	 * @param index
	 *           Position to add elem
	 * @throws IllegalArgumentException
	 * */

	public void addChildAt(InterviewElement elem, int index)
	{
		if (index < 0 || index > this.children.size())
			throw new IllegalArgumentException("invalid index position"); //$NON-NLS-1$

		elem.setParent(this);
		elem.setChildStatus(true);
		this.children.add(index, elem);
	}

	/**
	 * Removes a child from this InterviewElement
	 * 
	 * @param elem
	 *           element to remove
	 */
	public void removeChild(InterviewElement elem)
	{
		elem.setChildStatus(false);

		if (childPointer == children.size())
			childPointer--;

		this.children.remove(elem);
	}

	/**
	 * Returns the number of children from this InterviewElement
	 * 
	 * @return number of children
	 */
	public int getNumberOfChildren()
	{
		return this.children == null ? 0 : children.size();
	}

	/**
	 * Returns the next child of this InterviewElement
	 * 
	 * @return child of this InterviewElement
	 */
	public InterviewElement getNextChild()
	{
		if (children.size() < childPointer + 1)
			return null;

		return children.get(childPointer++);
	}

	/**
	 * Returns the previous child of this InterviewElement
	 * 
	 * @return child of this InterviewElement
	 */
	public InterviewElement getPreviousChild()
	{
		if (childPointer - 1 < 0)
			return null;

		return children.get(--childPointer);
	}

	/**
	 * Gets the child at the specified index
	 * 
	 * @param index
	 *           of child
	 * @return Child at index
	 */
	public InterviewElement getChildAt(int index)
	{
		if (index < 0 || index >= children.size())
			return null;

		return this.children.get(index);
	}

	/**
	 * Sets the childpointer to the specified value
	 * 
	 * @param n
	 *           value of childpointer
	 */
	public void setChildPointer(int n)
	{
		// if(n < 0 || n >= children.size())
		// throw new IllegalArgumentException("ChildPointer: "+n);

		this.childPointer = n;
	}

	/**
	 * Returns the current value of the childpointer
	 * 
	 * @return value of childpointer
	 */
	public int getChildPointer()
	{
		return this.childPointer;
	}

	/**
	 * Inrecments the childpointer by 1
	 */
	public void incrementChildPointer()
	{
		this.childPointer++;
	}

	/**
	 * Decrement the childpointer by 1
	 */
	public void decrementChildPointer()
	{
		this.childPointer--;
	}

	/**
	 * Resets the childpointer to 0
	 */
	public void resetChildPointer()
	{
		this.childPointer = 0;
	}

	/**
	 * Set parent of this InterviewElement
	 * 
	 * @param parent
	 *           parent InterviewElement
	 */
	public void setParent(InterviewElement parent)
	{
		this.parent = parent;
	}

	/**
	 * Gets the parent of this InterviewElement
	 * 
	 * @return parent of this Element or <code>null</code> if InterviewElement
	 *         has no parent
	 */
	public InterviewElement getParent()
	{
		return this.parent;
	}

	/**
	 * True if this InterviewElement is a child
	 * 
	 * @return true if InterviewElement is a child, false otherwise
	 */
	public boolean isChild()
	{
		return this.isChild;
	}

	/**
	 * Sets if this InterviewElement is a child or not
	 * 
	 * @param isChild
	 *           true if InterviewElement is child, false if not
	 */
	public void setChildStatus(boolean isChild)
	{
		this.isChild = isChild;
		if (!isChild)
		{
			this.setParent(null);
		}
	}

	/**
	 * Gets the index of an specified child of this InterviewElement
	 * 
	 * @param child
	 *           to get index
	 * @return index of child
	 */
	public int getIndexOfChild(InterviewElement child)
	{
		return this.children.indexOf(child);
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
	 * Name of this InterviewElement
	 * 
	 * @param description
	 *           name of this element
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Returns the description of this InterviewElement
	 * 
	 * @return name of this element
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * @return the elementNameInTree
	 */
	public String getElementNameInTree()
	{
		return elementNameInTree;
	}

	/**
	 * @param elementNameInTree
	 *           the elementNameInTree to set
	 */
	public void setElementNameInTree(String elementNameInTree)
	{

		if (elementNameInTree == null || elementNameInTree.equals(""))
		{
			this.elementNameInTree = (this.elementNameInTree == null) ? Messages
					.getString("EditActorLabelDialog.1") : this.elementNameInTree;
			return;
		}
		else if (elementNameInTree.length() > InterviewTreeModel.NAME_LENGTH)
		{
			if (getFilter() == null || getFilter().equals(""))
			{
				elementNameInTree = elementNameInTree.substring(0,
						InterviewTreeModel.NAME_LENGTH
								- InterviewTreeModel.NAME_SUFFIX.length())
						+ InterviewTreeModel.NAME_SUFFIX;
			}
			else
			{
				elementNameInTree = elementNameInTree
						.substring(
								0,
								InterviewTreeModel.NAME_LENGTH
										- (InterviewTreeModel.NAME_SUFFIX.length() + InterviewTreeModel.FILTER_SUFFIX
												.length()))
						+ InterviewTreeModel.NAME_SUFFIX
						+ InterviewTreeModel.FILTER_SUFFIX;
			}
		}
		else if (getFilter() != null
				&& !getFilter().equals("")
				&& (elementNameInTree.length() + InterviewTreeModel.FILTER_SUFFIX
						.length()) > InterviewTreeModel.NAME_LENGTH)
		{
			elementNameInTree = elementNameInTree
					.substring(
							0,
							InterviewTreeModel.NAME_LENGTH
									- (InterviewTreeModel.NAME_SUFFIX.length() + InterviewTreeModel.FILTER_SUFFIX
											.length()))
					+ InterviewTreeModel.NAME_SUFFIX
					+ InterviewTreeModel.FILTER_SUFFIX;
		}
		else if (getFilter() != null && !getFilter().equals(""))
		{
			elementNameInTree = elementNameInTree
					+ InterviewTreeModel.FILTER_SUFFIX;
		}

		this.elementNameInTree = elementNameInTree;

		if (this.description == null || this.description.trim().equals(""))
		{
			this.description = elementNameInTree;
		}
	}

	/**
	 * Overrides <code>equals(Object obj)</code> of Object Two InterviewElements
	 * are equal if their ids are equal
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof InterviewElement))
			return false;

		InterviewElement elem = (InterviewElement) obj;

		if (elem.getId() == this.getId())
			return true;

		return false;
	}

	@Override
	public int hashCode()
	{
		return this.id;
	}

	/**
	 * Should Element be skipped (for example if no actor is matching the filter)
	 * 
	 * @return default is <code>false</code>, override if necessary
	 */
	public boolean shouldBeSkipped()
	{
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException
	{
		if (flavor.equals(INTERVIEW_FLAVOR))
			return this;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return flavors;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.equals(INTERVIEW_FLAVOR);
	}

	/**
	 * Returns <code>true</code>, if this element is an MetaElement
	 * 
	 * @return <code>true</code> if this element is an MetaElement,
	 *         <code>false</code> if not
	 */
	public boolean isMetaElement()
	{
		return this.isMetaElement;
	}

	/*
	 * This Methods have to be implemented by every subclass of InterviewElement
	 */

	/**
	 * Returns dialog, displayed in the InterviewController
	 * 
	 * @return controller dialog
	 */
	public abstract JPanel getControllerDialog();

	/**
	 * Used to write Data to the data model if "next" or "previous" is clicked
	 * 
	 * @return true if successfull, false else
	 */
	public abstract boolean writeData();

	/**
	 * Used to set data from the data model to this element
	 */
	public abstract void setData();

	/**
	 * Returns the dialog, wich is displayed in the TreeCreator
	 * 
	 * @return dialog in TreeCreator
	 */
	public abstract JPanel getConfigurationDialog();

	/**
	 * Can be overwritten when needed
	 */
	public void initPreview()
	{
	}

	/**
	 * Only needed if <code>initPreview()</code> was implemented
	 */
	public void deinitPreview()
	{

	}

	/**
	 * Each Interview Element should override this method to make available an
	 * dummy preview of its own
	 */
	public BufferedImage getPreview()
	{
		InterviewElement ie = (InterviewElement) Reflection.createInstance(this
				.getClass());
		if (ie != null)
		{
			ie.getConfigurationDialog();
			ie.initPreview();
			JPanel p = ie.getControllerDialog();
			JFrame f = new JFrame();
			p.setPreferredSize(new Dimension(400, 400));
			f.add(p);
			f.pack();

			BufferedImage img = new BufferedImage(400, 400,
					BufferedImage.TYPE_INT_RGB);

			Graphics g = img.createGraphics();
			p.paint(g);
			g.dispose();
			f.dispose();

			return img;
		}

		return null;
	}

	/**
	 * Sets the current filter selector for this InterviewElement
	 * 
	 * @param fSelector
	 *           the current filter selector for this InterviewElement
	 */
	public void setFilterSelector(FilterSelector fSelector)
	{
		System.out.println("InterviewElement: setFilterSelector()");	
		this.fSelector = fSelector;
	}

	/**
	 * Returns the current Filter of this InterviewElement definied in the
	 * FilterSelector
	 * 
	 * @return the current Filter of this InterviewElement definied in the
	 *         FilterSelector
	 */
	public String getFilter()
	{
		if ( this.fSelector != null)
		{
			System.out.println("InterviewElement: getFilter()"+this.fSelector.getFilter());	
			return this.fSelector.getFilter();
		}		
		return null;
	}

	/**
	 * Sets the current Filter of this InterviewElement in the FilterSelector
	 * 
	 * @param filter
	 *           the current Filter of this InterviewElement in the
	 *           FilterSelector
	 */
	public void setFilter(String filter)
	{
		System.out.println("setFilter():"+filter);
		
		if (this.fSelector == null)
			return;
		this.fSelector.setFilter(filter);
	}

	/**
	 * Retruns the selected Filter indicies from the FilterSelector
	 * 
	 * @return the selected Filter indicies from the FilterSelector
	 */
	public List<Integer> getFilterIndex()
	{
		System.out.println("InterviewElement:getFilterIndex()"+this.fSelector);
		if (this.fSelector == null)
			return null;

		return fSelector.getFilterIndex();
	}

	/**
	 * Sets the selected Filter indicies of the FilterSelector
	 * 
	 * @param filterIndex
	 *           the selected Filter indicies of the FilterSelector
	 */
	public void setFilterIndex(Integer[] filterIndex)
	{
		if (this.fSelector == null || filterIndex == null)
			return;

		fSelector.setFilterIndex(new ArrayList<Integer>(Arrays
				.asList(filterIndex)));
	}

	/**
	 * Create a copy of this Element.
	 * 
	 * @return a copy of this element
	 */
	public InterviewElement cloneElement()
	{

		Class<? extends InterviewElement> elementToClone = this.getClass();
		InterviewElement elem = null;

		try
		{
			elem = elementToClone.newInstance();
			elem.setElementInfo(this.getElementInfo());
			elem.setFilter(getFilter());

			if (getFilterIndex() != null)
				elem.setFilterIndex(getFilterIndex().toArray(
						new Integer[getFilterIndex().size()]));

		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return elem;
	}

	/**
	 * Returns whether the InterviewElement has Preview or not
	 * 
	 * @return true if this Element has a Preview view, <code>false</code>
	 *         otherwise
	 */
	public boolean hasPreview()
	{
		return this.hasPreview;
	}

	/**
	 * Sets if the InterviewElement has a Preview view or not
	 * 
	 * @param hasPreview
	 */
	public void setHasPreview(boolean hasPreview)
	{
		this.hasPreview = hasPreview;
	}

	/**
	 * starts/continues timemeasuring for the current element
	 */
	public void startTime()
	{
		if (!this.timing)
		{
			this.timing = true;
			this.time = System.currentTimeMillis() - this.time;
		}
	}

	/**
	 * stops the current timemeasurement
	 */
	public void stopTime()
	{
		if (this.timing)
		{
			this.timing = false;
			this.time = System.currentTimeMillis() - this.time;
		}
	}

	/**
	 * to set the current time to a predefined value (e.g. while loading)
	 */
	public void setTime(long time)
	{
		this.time = time;
	}

	/**
	 * returns the time, the user spent on working with this element.
	 */
	public long getTime()
	{
		if (this.timing)
		{
			return (System.currentTimeMillis() - this.time);
		}
		else
		{
			return this.time;
		}
	}

	/**
	 * Operations wich should be performed if this InterviewElement is added to
	 * the interview tree
	 * 
	 * @return true if sucessfull, false otherwise
	 */
	public abstract boolean addToTree();

	/**
	 * Gets the instruction Text displayed in the InterviewCreator
	 * 
	 * @return description of this InterviewElement
	 */
	public abstract String getInstructionText();

	/**
	 * Gets the information wich are important to recreate this element
	 * 
	 * @return Object wich contains important information of this element
	 */
	public abstract InterviewElementInformation getElementInfo();

	/**
	 * Sets all fields and necessary information to recreate this element from
	 * the given information
	 * 
	 * @param information
	 *           Information to recreate this InterviewElement
	 */
	public abstract void setElementInfo(InterviewElementInformation information);

	/**
	 * Validates the input of the user
	 * 
	 * @return <code>true</code> if the input of the user is correct (e.g all
	 *         fields all filled) <code>false</code> if the input is not correct
	 */
	public abstract boolean validateInput();
}
