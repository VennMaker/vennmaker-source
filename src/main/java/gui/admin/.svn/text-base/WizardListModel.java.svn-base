/**
 * 
 */
package gui.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import wizards.VennMakerWizard;

/**
 * This class is the list model for wizards in the interview configuration
 * frame. <code>getElementAt(int)</code> should return objects of type
 * <code>String</code>. To obtain the correspondant wizard use
 * <code>getWizard(String)</code>.
 * 
 * 
 * 
 */
public class WizardListModel implements ListModel
{
	/**
	 * This maps a wizard description (String) to the wizard (Wizard)
	 * 
	 * BEWARE: The key set may contain entries that are not contained in the
	 * list.
	 * 
	 * 
	 * DO NOT RELY ON THE KEY SET WHEN ITERATING OVER VALID VALUES! THIS COULD
	 * CAUSE DANGEROUS HARM!
	 * 
	 * WARNING! WARNING! WARNING!
	 */
	private final Map<String, VennMakerWizard>	wizardsDictionary;

	/**
	 * Keeps the list of the wizards in the model (refer to wizardsDictionary to
	 * obtain the VennMakerWizard-objects)
	 */
	private final DefaultListModel					wizardsList;

	/**
	 * Creates a new empty model
	 */
	public WizardListModel()
	{
		this.wizardsDictionary = new HashMap<String, VennMakerWizard>();
		this.wizardsList = new DefaultListModel();
	}

	/**
	 * Returns a vector with the enabled Wizards in the defined order.
	 * 
	 * @return A valid vector (may be empty if nothing is defined).
	 */
	public Vector<VennMakerWizard> toVector()
	{
		Vector<VennMakerWizard> retVal = new Vector<VennMakerWizard>();
		for (int i = 0; i < wizardsList.getSize(); ++i)
		{
			retVal.add(wizardsDictionary.get(this.wizardsList.get(i)));
		}
		return retVal;
	}

	/**
	 * Adds the given wizard at the specified index position
	 * 
	 * @param index
	 *           An index such that <code>0 &lt;= index &lt; size()</code>
	 * @param name
	 *           A name for the wizard
	 * @param wiz
	 *           The wizard itself.
	 * @throws java.lang.IllegalArgumentException
	 *            If a wizard with the specified name already exists!
	 */
	public void add(int index, String name, VennMakerWizard wiz)
			throws java.lang.IllegalArgumentException
	{
		if (this.wizardsList.contains(name))
			throw new IllegalArgumentException("name of wizard is not unique!");
		this.wizardsDictionary.put(name, wiz);
		this.wizardsList.add(index, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.admin.WizardContainer#add(java.lang.String,
	 *      wizards.VennMakerWizard)
	 */
	public void add(String name, VennMakerWizard wiz)
			throws IllegalArgumentException
	{
		if (this.wizardsList.contains(name))
			throw new IllegalArgumentException("name of wizard is not unique!");
		this.wizardsDictionary.put(name, wiz);
		this.wizardsList.addElement(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener )
	 */
	@Override
	public void addListDataListener(ListDataListener l)
	{
		this.wizardsList.addListDataListener(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index)
	{
		return this.wizardsList.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize()
	{
		return this.wizardsList.size();
	}

	/**
	 * Leert die Wizardliste.
	 */
	public void clear()
	{
		this.wizardsList.clear();
		this.wizardsDictionary.clear();
	}

	/**
	 * Returns the wizard with the given name
	 * 
	 * @param name
	 *           A valid name
	 * @return A valid wizard or <code>null</code> if none could be found.
	 */
	public VennMakerWizard getWizardByName(String name)
	{
		return this.wizardsDictionary.get(name);
	}

	/**
	 * Moves the list item at the given index down (from the GUI's point of view =
	 * increases index) If the index is invalid or equals size()-1 nothing
	 * happens.
	 * 
	 * @param index
	 *           A valid index
	 */
	public void moveDown(int index)
	{
		swap(index, index + 1);
	}

	/**
	 * Moves the list item at the given index up (from the GUI's point of view =
	 * decreases index) If the index is invalid or 0 nothing happens.
	 * 
	 * @param index
	 *           A valid index.
	 */
	public void moveUp(int index)
	{
		swap(index, index - 1);
	}

	/**
	 * Removes the wizard and its descriptive name from the model
	 * 
	 * @param index
	 *           The index which position is to be removed
	 */
	public void remove(int index)
	{
		String rStr = (String) this.wizardsList.remove(index);
		this.wizardsDictionary.remove(rStr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.swing.ListModel#removeListDataListener(javax.swing.event.
	 * ListDataListener)
	 */
	@Override
	public void removeListDataListener(ListDataListener l)
	{
		this.wizardsList.removeListDataListener(l);
	}

	/**
	 * Sets the given wizard at the given position.
	 * 
	 * @param index
	 *           The position in the list for the wizard
	 * @param name
	 *           The name for the wizard
	 * @param wizard
	 *           The wizard itself.
	 */
	public void set(int index, String name, VennMakerWizard wizard)
	{
		this.wizardsDictionary.put(name, wizard);
		this.wizardsList.set(index, name);
	}

	/**
	 * Swaps the objects at the given indices. If one or both indices are invalid
	 * (out of range or negative) nothing happens.
	 * 
	 * @param index1
	 *           A valid index
	 * @param index2
	 *           Another valid index
	 */
	private void swap(int index1, int index2)
	{
		if (index1 == index2 || index1 < 0 || index2 < 0
				|| index1 + 1 > this.wizardsList.getSize()
				|| index2 + 1 > this.wizardsList.getSize())
		{
			return;
		}

		Object helpObject = this.wizardsList.getElementAt(index1);
		this.wizardsList.set(index1, this.wizardsList.getElementAt(index2));
		this.wizardsList.set(index2, helpObject);
	}

}
