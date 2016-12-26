package interview;

import gui.configdialog.save.InterviewSaveElement;
import gui.configdialog.settings.SettingInterviewCreator;
import interview.categories.IECategory;
import interview.elements.InterviewElement;
import interview.elements.RootElement;
import interview.elements.StandardElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTree;

import data.AttributeType;
import data.Projekt;
import data.Reflection;

/**
 * Am InterviewLayer melden sich alle InterviewElemente an. Der
 * InterviewController beschafft sich diesen Layer und stellt die
 * InterviewElemente in der Liste ueber die Methode "getDialog()" grafisch da
 */
public class InterviewLayer implements Serializable
{
	private static final long																							serialVersionUID	= 1L;

	private int																												currentID;

	public static final boolean																						OVERWRITE			= true;

	public static final boolean																						INSERT				= false;

	private static InterviewLayer																						currentLayer;

	private InterviewLayer																								oldLayer;

	private List<InterviewElement>																					topLevelElements;

	// Map< Key = Category , Value = List with Elements >
	private LinkedHashMap<Class<? extends IECategory>, List<Class<? extends InterviewElement>>>	registeredElements;

	private List<InterviewElement>																					allElements;

	private List<UpdateListener>																						updateListeners;

	private int																												pointer;

	private boolean																										layerChanged;

	private JTree																											interviewTree;

	private InterviewTreeNode																							root;

	private long																											lastModified;

	private int																												lastId;

	public InterviewLayer()
	{
		topLevelElements = new ArrayList<InterviewElement>();

		// Map needed because of categories
		// Map< Key = Category , Value = List with Elements >
		registeredElements = new LinkedHashMap<Class<? extends IECategory>, List<Class<? extends InterviewElement>>>();

		allElements = new ArrayList<InterviewElement>();
		updateListeners = new ArrayList<UpdateListener>();
		currentLayer = this;
	}

	/**
	 * Returns an Instance of an InterviewLayer
	 * 
	 * @return a instance of InterviewLayer
	 */
	public static InterviewLayer getInstance()
	{
		if (currentLayer != null)
			return currentLayer;
		else
		{
			new InterviewLayer();
			return currentLayer;
		}
	}

	/**
	 * Sets a new instance of this layer
	 * 
	 * @param layer
	 *           to set
	 */
	public static void setInstance(InterviewLayer layer)
	{
		if (layer == null)
			return;

		currentLayer = layer;
	}

	/**
	 * Add an element to the Layer, only elements wich were added to the layer
	 * will be displayed by the InterviewController
	 * 
	 * @param element
	 *           InterviewElement
	 */
	public void addElement(InterviewElement element)
	{
		if (element == null)
			fireException("Element should not be null");
		else
		{
			boolean contained = false;

			if (!this.topLevelElements.contains(element))
			{
				this.topLevelElements.add(element);
				contained = true;
			}

			if (!this.allElements.contains(element))
			{
				this.allElements.add(element);
				contained = true;
			}

			if (contained)
			{
				this.lastModified = System.currentTimeMillis();
			}
			updateListeners();
		}
	}

	public void addChildElement(InterviewElement element)
	{
		if (!this.allElements.contains(element))
		{
			this.allElements.add(element);
		}
	}

	/**
	 * Resets the internal Pointer
	 */
	public void resetPointer()
	{
		this.pointer = 0;
	}

	public long getLastModified()
	{
		return this.lastModified;
	}

	/**
	 * Removes the given element from the element list
	 * 
	 * @param element
	 */
	public void removeElement(InterviewElement element)
	{
		this.allElements.remove(element);

		if (element.getParent() == null)
			this.topLevelElements.remove(element);
		else
			element.getParent().removeChild(element);

		this.lastModified = System.currentTimeMillis();
		updateListeners();
	}

	/**
	 * Removes the element on the given position
	 * 
	 * @param index
	 */
	public void removeElementAt(int index)
	{
		if (index < 0 || index >= topLevelElements.size())
			fireException("Invalid index (" + index + ") Size: "
					+ topLevelElements.size());
		else
			this.topLevelElements.remove(index);

		this.lastModified = System.currentTimeMillis();
	}

	/**
	 * Returns the index of the given <code>InterviewElement</code>
	 * 
	 * @param elem
	 *           <code>InterviewElement</code> to get the index of
	 * @return the index of the given <code>InterviewElement</code>
	 */
	public int getIndexOfElement(InterviewElement elem)
	{
		return this.allElements.indexOf(elem);
	}

	/**
	 * Returns the index of the given <code>InterviewElement</code> if the
	 * element is top level
	 * 
	 * @param elem
	 *           <code>InterviewElement</code> to get the index of
	 * @return the index of the given <code>InterviewElement</code> if the
	 *         element is top level
	 */
	public int getTopLevelIndex(InterviewElement elem)
	{
		return this.topLevelElements.indexOf(elem);
	}

	/**
	 * Returns all <code>InterviewElement</code>s at top level
	 * 
	 * @return all <code>InterviewElement</code>s at top level
	 */
	public List<InterviewElement> getTopLevelElements()
	{
		return this.topLevelElements;
	}

	/**
	 * Gets the element on the given position.
	 * 
	 * @param index
	 * @return an InterviewElement
	 * @throws IllegalArgumentException
	 */
	public InterviewElement getElementAt(int index)
	{
		if (index < 0 || index >= this.allElements.size())
			fireException("Invalid index");
		else
		{
			return this.allElements.get(index);
		}

		return null;
	}

	public void save()
	{
		for (InterviewElement elem : allElements)
			elem.addToTree();
	}

	/**
	 * Returns the root of an InterviewElement
	 * 
	 * @param elem
	 *           element to get root from
	 * @return root of elem
	 */
	public InterviewElement getRootFromElement(InterviewElement elem)
	{
		InterviewElement element = elem;

		while (element.getParent() != null)
		{
			element = element.getParent();
		}

		return element;
	}

	/**
	 * Returns the current element
	 * 
	 * @return InterviewElement
	 */
	public InterviewElement getCurrentElement()
	{
		return this.topLevelElements.get(pointer);
	}

	/**
	 * Returns the next element on the layer
	 * 
	 * @return InterviewElement
	 */
	public InterviewElement getNextElement()
	{
		// return this.topLevelElements.get(++pointer %
		// this.topLevelElements.size());

		if ((pointer + 1) < topLevelElements.size())
			return this.topLevelElements.get(++pointer);

		return null;
	}

	/**
	 * Returns the previous element of the current element
	 * 
	 * @return InterviewElement
	 */
	public InterviewElement getPreviousElement()
	{
		// int p = pointer-1;
		//
		// if(p < 0)
		// this.pointer = this.topLevelElements.size();
		//
		// return this.topLevelElements.get(--pointer %
		// this.topLevelElements.size());

		if ((pointer - 1) >= 0)
			return this.topLevelElements.get(--pointer);

		return this.topLevelElements.get(0);
	}

	/**
	 * Returns all elements
	 * 
	 * @return Vector of all InterviewElements
	 */
	public Vector<InterviewElement> getAllElements()
	{
		return new Vector<InterviewElement>(allElements);
	}

	/**
	 * @return the lastId
	 */
	public int getLastId()
	{
		return lastId;
	}

	/**
	 * @param lastId
	 *           the lastId to set
	 */
	public void setLastId(int lastId)
	{
		this.lastId = lastId;
	}

	public void fireException(String message)
	{
		IllegalArgumentException exn = new IllegalArgumentException(message);
		exn.printStackTrace();
	}

	/**
	 * Checks if the given InterviewElement is already in the elements list
	 * 
	 * @param elem
	 *           InterviewElement to check
	 * @return true if elem is in the element list, false otherwise
	 */
	public boolean contains(InterviewElement elem)
	{
		return this.allElements.contains(elem);
	}

	/**
	 * Returns all categories with matching elements
	 * 
	 * @return A Map with all categories with included elements
	 */
	public Map<Class<? extends IECategory>, List<Class<? extends InterviewElement>>> getRegisteredElements()
	{
		return registeredElements;
	}

	/**
	 * Registers an element to the "registeredElements"list. Only elements that
	 * are registered can be selected in the "TreeCreator" dialog
	 * 
	 * @param elem
	 *           InterviewElement to register
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void registerElement(Class<? extends InterviewElement> elem)
	{
		if (elem == null)
			return;

		// use the element itself as key (implements the Category)
		List<Class<? extends InterviewElement>> elemList = registeredElements
				.get(Reflection.getCategory(elem));

		if (elemList == null)
		{
			// create new category list
			elemList = new ArrayList<Class<? extends InterviewElement>>();
			elemList.add(elem);
		}
		else
			// append to existing list
			elemList.add(elem);

		registeredElements.put(Reflection.getCategory(elem), elemList);
	}

	/**
	 * Adds a listener to listen for changes in the layer
	 * 
	 * @param listener
	 *           Listener to register
	 */
	public void addUpdateListener(UpdateListener listener)
	{
		if (listener == null)
			throw new IllegalArgumentException("UpdateListener should not be null");

		this.updateListeners.add(listener);
	}

	/**
	 * Removes an updatelistener from the layer
	 * 
	 * @param listener
	 *           Listener to remove
	 */
	public void removeUpdateListener(UpdateListener listener)
	{
		this.updateListeners.remove(listener);
	}

	/**
	 * Called if layer changes
	 */
	public void updateListeners()
	{
		for (UpdateListener l : this.updateListeners)
			l.update();
	}

	/**
	 * Sets the interna list pointer
	 * 
	 * @param value
	 *           Pointervalue
	 */
	public void setPointer(int value)
	{
		this.pointer = value;
	}

	/**
	 * Get value of internal list pointer
	 * 
	 * @return pointer Value of internal pointer
	 */
	public int getPointer()
	{
		return this.pointer;
	}

	/**
	 * @return the root
	 */
	public InterviewTreeNode getRoot()
	{
		return root;
	}

	/**
	 * @param root
	 *           the root to set
	 */
	public void setRoot(InterviewTreeNode root)
	{
		this.root = root;
	}

	/**
	 * Saves the state of the current layer
	 * 
	 * @param tmpLayer
	 *           InterviewLayer
	 */
	public void saveLayerSettings(InterviewLayer tmpLayer)
	{
		currentLayer = tmpLayer;
	}

	/**
	 * Saves the current interview tree
	 * 
	 * @param interviewTree
	 *           interview tree from CDInterviewCreator
	 */
	public void saveInterviewTree(JTree interviewTree)
	{
		this.interviewTree = interviewTree;
	}

	/**
	 * Load a saved interview tree
	 * 
	 * @return inverviewTree Tree to load
	 */
	public JTree loadInterviewTree()
	{
		return this.interviewTree;
	}

	/**
	 * Reset all values of the InterviewLayer
	 */
	public void reset()
	{
		InterviewTreeNode rootNode = new InterviewTreeNode(new RootElement());
		interviewTree = new InterviewTreeDragAndDrop(rootNode);
		interviewTree.setModel(new InterviewTreeModel(rootNode,
				new ArrayList<InterviewTreeNode>()));
		this.pointer = 0;
		this.topLevelElements.clear();
		this.allElements.clear();
	}

	/**
	 * Sets an element to the specified index
	 * 
	 * @param index
	 *           index for value to set
	 * @param elem
	 *           value to set
	 */
	public void setElementAt(int index, InterviewElement elem, boolean overwrite)
	{
		if (index < 0 || index > topLevelElements.size())
			throw new IllegalArgumentException("Wrong index (" + index
					+ ") Size: " + topLevelElements.size());

		if (topLevelElements.contains(elem))
		{
			int i = topLevelElements.indexOf(elem);
			topLevelElements.remove(elem);
			if (index > i)
				index--;
		}

		if (overwrite)
			topLevelElements.set(index, elem);
		else
			topLevelElements.add(index, elem);

	}

	public void removeTopLevelElement(InterviewElement elem)
	{
		if (elem == null)
			throw new IllegalArgumentException("elem should not be null");

		this.topLevelElements.remove(elem);
	}

	public int generateId()
	{
		return ++currentID;
	}

	public void setStartId(int startId)
	{
		this.currentID = startId;
	}

	/**
	 * Create children from this InterviewElement
	 * 
	 * @param information
	 * @param parent
	 */
	public void createChild(InterviewElementInformation information,
			InterviewElement parent)
	{
		if (information.getChildInformation() == null)
		{
			return;
		}

		List<InterviewElementInformation> childInfos = information
				.getChildInformation();
		List<Class<? extends InterviewElement>> childClasses = information
				.getChildClasses();

		for (int i = 0; i < childInfos.size(); i++)
		{
			try
			{
				InterviewElement childParent = InterviewLayer.getInstance()
						.getElementFromId(childInfos.get(i).getParentId());

				InterviewElement child = childClasses.get(i).newInstance();

				child.setId(childInfos.get(i).getId());
				child.set(childInfos.get(i).getElementName());

				child.setElementInfo(childInfos.get(i));
				childParent.addChild(child);

				InterviewLayer.getInstance().addChildElement(child);

				((InterviewTreeModel) interviewTree.getModel()).insertNodeInto(
						child, childParent, i);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void loadInterviewFromProject(Projekt p)
	{
		InterviewTreeNode interviewRoot = new InterviewTreeNode(new RootElement());
		interviewTree = new InterviewTreeDragAndDrop(interviewRoot);
		interviewTree.setModel(new InterviewTreeModel(interviewRoot,
				new ArrayList<InterviewTreeNode>()));
		InterviewTreeModel treeModel = (InterviewTreeModel) interviewTree
				.getModel();
		treeModel.setRoot(interviewRoot);

		InterviewSaveElement element = p.getCurrentInterviewConfig();

		int startId = 0;

		for (InterviewElementInformation info : element.getElementInformations())
		{

			if (info == null)
				continue;

			try
			{
				InterviewElement elem = info.getElementClass().newInstance();

				if (startId < info.getId())
					startId = info.getId();

				InterviewTreeNode node = new InterviewTreeNode(elem);
				interviewRoot.add(node);
				treeModel.addNodeToData(node);

				elem.setId(info.getId());
				elem.setElementInfo(info);
				elem.setTime(info.getTime());

				SettingInterviewCreator settingToCache = new SettingInterviewCreator();
				settingToCache.setInterviewNode(node);
				settingToCache.setInterviewTree(interviewTree);
				settingToCache.setInterviewValue(elem);
				settingToCache.setParentElement(null);
				settingToCache.setParent(null);
				settingToCache.setRoot(interviewRoot);

				settingToCache.set();

			} catch (IllegalAccessException iae)
			{
				System.err.println("Could not access class"); //$NON-NLS-1$
				iae.printStackTrace();
			} catch (InstantiationException ie)
			{
				System.err.println("Could not create an instance of class"); //$NON-NLS-1$
				ie.printStackTrace();
			}
		}
		treeModel.reload();
		InterviewLayer.getInstance().setStartId(startId);
	}

	/**
	 * Returns the element with the specified id
	 * 
	 * @param id
	 * @return InterviewElement with this id
	 */
	public InterviewElement getElementFromId(int id)
	{
		if (id == 0)
			return null;

		for (InterviewElement elem : allElements)
			if (elem.getId() == id)
				return elem;

		return null;
	}

	/**
	 * Replace the given <code>InterviewElement</code> with an
	 * <code>InterviewElement</code> with the same ID
	 * 
	 * @param elementToReplace
	 *           the new <code>InterviewElement</code>
	 * @return <code>true</code> if an <code>InterviewElement</code> was
	 *         replaced, <code>false</code> if not
	 */
	public boolean replaceElement(InterviewElement elementToReplace)
	{
		boolean replaced = false;

		if (elementToReplace.getParent() == null)
		{
			for (int i = 0; i < topLevelElements.size(); i++)
			{
				if (topLevelElements.get(i).getId() == elementToReplace.getId())
				{
					topLevelElements.remove(i);
					topLevelElements.add(i, elementToReplace);

					replaced = true;
					break;
				}
			}
		}

		for (int i = 0; i < allElements.size(); i++)
		{
			if (allElements.get(i).getId() == elementToReplace.getId())
			{
				InterviewElement parent = elementToReplace.getParent();

				int childPosition = -1;

				if (parent != null)
				{
					for (int j = 0; j < parent.getNumberOfChildren(); j++)
					{
						if (parent.getChildAt(j) == allElements.get(i))
						{
							childPosition = j;
							break;
						}
					}

					if (childPosition > -1)
					{
						parent.removeChild(allElements.get(i));
						parent.addChildAt(elementToReplace, childPosition);
					}
				}

				allElements.remove(i);
				allElements.add(i, elementToReplace);

				replaced = true;
				break;
			}
		}

		return replaced;
	}

	/**
	 * Used by CDialogEditAttributeTypes (of ConfigDialog) to test if an
	 * AttributeType, the user wants to delete, is in use in the current
	 * interview</br>
	 * 
	 * @return <code>null</code> - if not in use </br> String containing all
	 *         element names which use it otherwise
	 * 
	 */
	public String isAttribUsedInElement(AttributeType a)
	{
		StringBuffer elemsThatUseIt = new StringBuffer("");
		boolean inUse = false;
		for (InterviewElement e : getAllElements())
		{
			if (e instanceof StandardElement)
			{
				StandardElement se = (StandardElement) e;
				if (se.isAttributeTypeInUse(a))
				{
					elemsThatUseIt.append(e.getElementNameInTree() + "\n");
					inUse = true;
				}
			}
		}
		return inUse ? elemsThatUseIt.toString() : null;
	}
}
