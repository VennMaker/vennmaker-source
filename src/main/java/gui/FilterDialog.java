/**
 * Diese Klasse realisiert die Suche bzw die Filterfunktion
 */
package gui;

import interview.configuration.filterSelection.InterviewFilterDialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jvnet.flamingo.common.JCommandButton;

import data.Akteur;
import data.AttributeType;
import data.Filterparameter;
import data.Netzwerk;
import files.FileOperations;

/**
 * 
 * 
 */
public class FilterDialog extends JDialog implements WindowListener
{
	/**
	 * 
	 */
	private static final long									serialVersionUID			= 1L;

	/**
	 * speichert die Voraussetzungen zum Filtern - alle Filtereinstellungen, die
	 * mit AND verkn�pft sind, erhalten die gleiche Integer-Kennzahl. OR -
	 * Verkn�pfungen werden durch unterschiedliche Integer-Kennzahlen
	 * gekennzeichnet
	 */
	private HashMap<Integer, Vector<Filterparameter>>	filters						= new HashMap<Integer, Vector<Filterparameter>>();

	/**
	 * Enth�lt die Combo-Boxen
	 */
	private JPanel													comboBoxPanel;

	/**
	 * Combo-Box fuer die Attribute
	 */
	private JComboBox												attributes;

	/**
	 * Combo-Box fuer die Attribut-Werte
	 */
	private JComboBox												values;

	/**
	 * Combo-Box fuer UND, ODER, " "
	 */
	private JComboBox												booleanValues;

	/**
	 * Combo-Box for equal, not-equal etc
	 */

	private JComboBox												condition;

	/**
	 * ArrayList die alle ComboBoxen enthaelt
	 */
	private ArrayList<JComboBox>								boxes;

	/**
	 * Liste mit Attributen
	 */
	private Vector<AttributeType>								attributeList;

	/**
	 * Alle Akteure des Netzwerks
	 */
	private Vector<Akteur>										akteure;

	private Netzwerk												network;

	/**
	 * Liste mit Akteuren die die Kriterien der Suchanfrage erfuellen
	 */
	private ArrayList<Akteur>									results;

	private Akteur													currentActor;

	private VennMakerView										actualView;

	public static final int										LOWER							= 0;

	public static final int										LOWER_EQUALS				= 1;

	public static final int										EQUALS						= 2;

	public static final int										NOT_EQUALS					= 3;

	public static final int										HIGHER_EQUALS				= 4;

	public static final int										HIGHER						= 5;

	public static final int										OR								= 1;

	public static final int										AND							= 2;

	public static final int										EMPTY							= 0;

	private int														attributeBoxWidth			= 5;

	private int														conditionBoxWidth			= 3;

	private int														valuesBoxWidth				= 3;

	private int														booleanValuesBoxWidth	= 2;

	private int														xPosition					= 0;

	private int														row							= 0;

	private String													filter;

	private JScrollPane											scrollPane;

	private GridBagConstraints									gbc;

	public FilterDialog(boolean visible)
	{
		setModal(true);
		setTitle(Messages.getString("SearchDialog.0")); //$NON-NLS-1$
		setSize(700, 550);
		setLayout(new BorderLayout());
		setLocation(VennMaker.getInstance().getFrames()[1].getLocation());
		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		JLabel descriptionArea = new JLabel(Messages.getString("SearchDialog.15")); //$NON-NLS-1$

		add(descriptionArea, BorderLayout.NORTH);

		network = VennMaker.getInstance().getProject().getCurrentNetzwerk();
		akteure = VennMaker.getInstance().getProject().getAkteure();
		attributeList = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");

		results = new ArrayList<Akteur>();

		filter = VennMaker.getInstance().getProject().getFilter();
		filters = VennMaker.getInstance().getProject().getFilters();

		Vector<VennMakerView> views = VennMaker.getInstance().getViews();

		for (VennMakerView v : views)
		{
			if (v.getNetzwerk() == network)
			{
				actualView = v;
			}
		}

		boxes = new ArrayList<JComboBox>();
		comboBoxPanel = new JPanel(new GridBagLayout());

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(Messages.getString("SearchDialog.1")); //$NON-NLS-1$
		JButton cancelButton = new JButton(Messages.getString("SearchDialog.2")); //$NON-NLS-1$
		okButton.addActionListener(new MySearchListener());
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				results.clear();
				dispose();
			}
		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		attributes = new JComboBox(attributeList);
		attributes.addActionListener(new MyAttributeListener());

		values = new JComboBox();
		booleanValues = new JComboBox(
				new String[] {
						Messages.getString("SearchDialog.3"), Messages.getString("SearchDialog.4"), Messages.getString("SearchDialog.5") }); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		booleanValues.addActionListener(new MyBooleanListener());
		condition = new JComboBox(
				new String[] {
						Messages.getString("SearchDialog.6"), //$NON-NLS-1$
						Messages.getString("SearchDialog.7"), Messages.getString("SearchDialog.8"), Messages.getString("SearchDialog.9"), Messages.getString("SearchDialog.10"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						Messages.getString("SearchDialog.11") }); //$NON-NLS-1$

		gbc = new GridBagConstraints();

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(attributes, gbc);

		xPosition += attributeBoxWidth;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = conditionBoxWidth;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(condition, gbc);

		xPosition += conditionBoxWidth;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = valuesBoxWidth;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(values, gbc);

		xPosition += valuesBoxWidth;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = booleanValuesBoxWidth;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(booleanValues, gbc);

		scrollPane = new JScrollPane(comboBoxPanel);

		boxes.add(attributes);
		boxes.add(condition);
		boxes.add(values);
		boxes.add(booleanValues);
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		setSize(580, 314);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		if ((filter != null || filters != null) && visible)
		{
			if (filter != null)
			{
				String[] splittedFilter = filter.split(";");
				if (splittedFilter.length > 4) //$NON-NLS-1$
				{
					for (int i = 4; i < splittedFilter.length; i += 4) //$NON-NLS-1$
					{
						addComponents();
					}
				}
				setFilter();
			}
			else
			{
				for (int i : filters.keySet())
				{
					for (Filterparameter fp : filters.get(i))
					{
						addComponents();
					}
				}
				setFilter();
			}
		}
		else
		{
			fillAttributes(0);
		}
		if (visible)
		{
			setVisible(true);
		}
	}

	/**
	 * Die Methode wird aufgerufen wenn der Benutzer "UND" oder "ODER" als
	 * Verkn�pfung ausw�hlt
	 */
	private void addComponents()
	{
		JComboBox<AttributeType> b1 = new JComboBox<AttributeType>(attributeList);
		JComboBox<String> b2 = new JComboBox<String>(
				new String[] {
						Messages.getString("SearchDialog.12"), Messages.getString("SearchDialog.4"), Messages.getString("SearchDialog.5") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		JComboBox<String> b3 = new JComboBox<String>(
				new String[] {
						Messages.getString("SearchDialog.6"), Messages.getString("SearchDialog.7"), Messages.getString("SearchDialog.8"), Messages.getString("SearchDialog.9"), Messages.getString("SearchDialog.10"), Messages.getString("SearchDialog.11") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		JComboBox b4 = new JComboBox();
		b1.addActionListener(new MyAttributeListener());
		b2.addActionListener(new MyBooleanListener());

		boxes.add(b1);
		boxes.add(b3);
		boxes.add(b4);
		boxes.add(b2);

		xPosition = 0;
		row++;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(b1, gbc);

		xPosition += attributeBoxWidth;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = conditionBoxWidth;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(b3, gbc);

		xPosition += conditionBoxWidth;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = valuesBoxWidth;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(b4, gbc);

		xPosition += valuesBoxWidth;

		gbc.gridx = xPosition;
		gbc.gridy = row;
		gbc.gridwidth = booleanValuesBoxWidth;
		gbc.gridheight = 1;
		gbc.weightx = 0.1;
		gbc.insets = new Insets(10, 10, 10, 10);
		comboBoxPanel.add(b2, gbc);

		fillAttributes(boxes.indexOf(b1));
		repaint();
		scrollPane.validate();
		scrollPane.getVerticalScrollBar().setValue(
				scrollPane.getVerticalScrollBar().getMaximum());
	}

	/**
	 * Fuellt die Combo-Boxen mit den Attributen
	 */
	private void fillAttributes(int index)
	{
		if (boxes.size() > index)
		{
			boxes.get(index + 2).removeAllItems();

			Object boxAttribute = boxes.get(index).getSelectedItem();

			AttributeType currentAttribute = null;

			if (attributeList.contains(boxAttribute))
			{
				currentAttribute = (AttributeType) boxAttribute;
			}
			else
			{
				return;
			}

			if (currentAttribute != null)
			{
				Object[] preValues = currentAttribute.getPredefinedValues();
				if (preValues != null)
				{
					boxes.get(index + 2).addItem(
							Messages.getString("SearchDialog.16")); //$NON-NLS-1$
					for (Object obj : preValues)
					{
						boxes.get(index + 2).addItem(obj);
						boxes.get(index + 2).setPreferredSize(new Dimension(139, 26));
						JTextField boxField = (JTextField) boxes.get(index + 2)
								.getEditor().getEditorComponent();
						Dimension dim = boxField.getSize();
					}
					boxes.get(index + 2).setEditable(false);
				}
				else
				{
					boxes.get(index + 2).setEditable(true);
					boxes.get(index + 2).addItem(""); //$NON-NLS-1$
					boxes.get(index + 2).setPreferredSize(new Dimension(139, 26));
				}
			}
		}
	}

	private void setFilter()
	{
		ArrayList<Integer> filterIndex = VennMaker.getInstance().getProject()
				.getFilterIndex();

		if ((filterIndex != null) && (filterIndex.size() > 0))
		{
			for (int i = 0; i < boxes.size(); i++)
			{
				if ((filterIndex.get(i) != -1)
						&& (filterIndex.get(i) < boxes.get(i).getItemCount()))
				{
					boxes.get(i).setSelectedIndex(filterIndex.get(i));
				}
				else
				{
					// boxes.get(i).insertItemAt(myFilter[i], 0);
					// boxes.get(i).removeItemAt(1);
					boxes.get(i).setSelectedIndex(0);
				}
			}
		}
	}

	class MyAttributeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int index = boxes.indexOf(e.getSource());
			fillAttributes(index);
		}

	}

	class MyBooleanListener implements ActionListener
	{

		/**
		 * Wird aufgerufen wenn der Benutzer "UND" oder "ODER" oder " " als
		 * Verkn�pfung ausw�hlt
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JComboBox box = (JComboBox) e.getSource();

			/**
			 * Wird " " als Verkn�pfung gew�hlt werden die restlichen
			 * Combo-Boxen entfernt, ansonsten werden Combo-Boxen hinzugef�gt
			 */
			if (box.getSelectedItem().equals(" ")) //$NON-NLS-1$
			{
				int index = boxes.indexOf(box);
				int size = boxes.size();

				for (int i = index + 1; i < size; i++)
				{
					comboBoxPanel.remove(boxes.get(i));
				}
				for (int i = index + 1; i < size; i++)
				{
					boxes.remove(index + 1);
				}
				comboBoxPanel.validate();
				comboBoxPanel.repaint();
			}
			else
			{
				if (boxes.indexOf(box) == boxes.size() - 1)
				{
					addComponents();
				}
			}
		}
	}


	/**
	 * Die Klasse realisiert die Suche
	 */
	public class MySearchListener implements ActionListener
	{

		private String													filter;

		private ArrayList<Integer>									filterIndex;

		private boolean												makeFilter;

		private int														conditionBox	= 1;

		/**
		 * speichert die Voraussetzungen zum Filtern - alle Filtereinstellungen,
		 * die mit AND verkn�pft sind, erhalten die gleiche Integer-Kennzahl. OR -
		 * Verkn�pfungen werden durch unterschiedliche Integer-Kennzahlen
		 * gekennzeichnet
		 */
		private HashMap<Integer, Vector<Filterparameter>>	filters			= new HashMap<Integer, Vector<Filterparameter>>();

		public MySearchListener()
		{
			filterIndex = new ArrayList<Integer>();
		}

		/**
		 * Der Konstruktor wird genutzt wenn schon ein Filter-String definiert
		 * wurde
		 * 
		 * @param filter
		 */
		public MySearchListener(String filter)
		{
			this.filter = filter;
			filterIndex = new ArrayList<Integer>();
		}

		/**
		 * Der Konstruktor wird genutzt wenn schon eine Filter-HashMap existiert
		 * wurde
		 * 
		 * @param filters
		 *           die bestehende Hashmap
		 */
		public MySearchListener(HashMap<Integer, Vector<Filterparameter>> filters)
		{
			this.filters = filters;
			filterIndex = new ArrayList<Integer>();
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			makeFilter = true;
			filter();
		}

		/**
		 * new filterfunction using filterparameters and not relying on Strings
		 * anymore.
		 */
		public void filter()
		{
			results.clear();
			if (true)
			// always build filter to stay downward compatible
			// if (filter == null || makeFilter)
			{
				int boxIndex = 1;
				int andGroupIndex = 0;

				Filterparameter fp = new Filterparameter();

				for (JComboBox<String> box : boxes)
				{
					/** last Combobox (AND, nothing or OR) */
					if (boxIndex != 0 && boxIndex % 4 == 0)
					{
						if (!filters.containsKey(andGroupIndex))
						{
							filters.put(andGroupIndex, new Vector<Filterparameter>());
						}
						filters.get(andGroupIndex).add(fp);
						fp = new Filterparameter();

						switch (box.getSelectedIndex())
						{
							case InterviewFilterDialog.OR:
								andGroupIndex++;
							default:
								break;
						}
					}
					/** comparisoncombobox (LOWER, LOWER_EQUALS, ... ) */
					if (boxIndex != 0 && boxIndex % 4 == 2)
					{
						fp.compareDirection = box.getSelectedIndex();
					}

					/** box 1 - Attributetype */
					if (boxIndex != 0 && boxIndex % 4 == 1)
					{
						for (AttributeType att : attributeList)
						{
							if (att.toString().equals(
									(box.getSelectedItem().toString().trim())))
							{
								fp.at = att;
							}
						}
					}

					/** box 3 - Attributevalue */
					if (boxIndex != 0 && boxIndex % 4 == 3)
					{
						fp.value = box.getSelectedItem();
					}

					int index = box.getSelectedIndex();

					if (index >= 0 && box.getItemCount() > 1)
					{
						filterIndex.add(new Integer(index));
					}
					else
					{
						filterIndex.add(new Integer(-1));
					}

					boxIndex++;
				}
			}

			/** after build: use this filter */
			for (Akteur actor : akteure)
			{
				currentActor = actor;

				if (checkOrExpressions(currentActor)){
					results.add(currentActor);
				}
			}
			/**
			 * Die Methode durchlaeuft nach der Suche die gefunden Akteure und
			 * markiert sie
			 */
			for (Akteur akt : results)
			{
				VennMaker.getInstance().getProject().getCurrentNetzwerk()
						.setMarkedActor(akt);
			}

			actualView.setFilter();

			if (filterIndex.size() > 0)
			{
				VennMaker.getInstance().getProject().setFilterIndex(filterIndex);
			}

			VennMaker.getInstance().getProject().setFilters(filters);

			setVisible(false);
		}

		/**
		 * checks the expressions connected with OR - if one is true, returns true
		 * 
		 * @param currentActor
		 *           check the premises for this actor
		 * @return true, if at least one premise is true
		 */
		private Boolean checkOrExpressions(Akteur currentActor)
		{
			for (int i : filters.keySet())
			{
				if (checkAndExpressions(currentActor, filters.get(i)))
					return true;
			}

			return false;
		}

		/**
		 * checks the expressions connected with AND - if one is false, returns
		 * false
		 * 
		 * @param currentActor
		 *           the actor to check the parameters
		 * @param fparameters
		 *           the parameters, connected with AND
		 * @return true, if all expressions are true, false else
		 */
		private Boolean checkAndExpressions(Akteur currentActor,
				Vector<Filterparameter> fparameters)
		{
			for (Filterparameter fp : fparameters)
			{
				if (!checkExpression(currentActor, fp))
					return false;
			}
			return true;
		}

		/**
		 * checks one single expression for a given actor
		 * 
		 * @param a
		 *           the actor to check
		 * @param fp
		 *           the filters which need to be applied
		 * @return true, if the expression is true, false else
		 */
		private Boolean checkExpression(Akteur a, Filterparameter fp)
		{
			Object value = currentActor.getAttributeValue(fp.at, network);
		System.out.println("value:"+value+", fp.value:"+fp.value+", fp.at:"+fp.at);	
		
		if (value == null) value="";
			int compareValue = compare(value, fp.value, fp.at);
			
			switch (fp.compareDirection)
			{
				case LOWER:
					if (compareValue < 0)
						return true;
					break;
				case LOWER_EQUALS:
					if (compareValue <= 0)
						return true;
					break;
				case EQUALS:
					if (compareValue == 0)
						return true;
					break;
				case NOT_EQUALS:
					if (compareValue != 0)
						return true;
					break;
				case HIGHER_EQUALS:
					if (compareValue >= 0)
						return true;
					break;
				case HIGHER:
					if (compareValue > 0)
						return true;
					break;
				default:
					return false;
			}
			return false;
		}
	}

	public List<Akteur> getActors()
	{
		// If no filter return all Actors
		if ((filter == null || filter.equals("")) && (filters == null
				|| filters.size() == 0))
			return VennMaker.getInstance().getProject().getAkteure();

		// msl.actionPerformed(null);
		return results;
	}

	/**
	 * compares two values
	 * 
	 * @param a
	 *           a value as String, Integer or Float
	 * @param b
	 *           another value as String, Integer or Float
	 * @return a value < 0, if a < b, 0 if a == b, a value > 0, if a > b
	 */
	private int compare(Object a, Object b)
	{
		/* test, if number */
		String regex = "\\d+([.,]\\d+)?";
		String aStr = a.toString();
		String bStr = b.toString();

		if (aStr.matches(regex) && bStr.matches(regex))
		{
			double aDbl = Double.parseDouble(aStr);
			double bDbl = Double.parseDouble(bStr);
			if (aDbl > bDbl)
				return 1;
			if (aDbl < bDbl)
				return -1;
			return 0;
		}

		return aStr.compareTo(bStr);
	}

	/**
	 * compares two values of AttributeType at, if it contains predefined values
	 * if not, returns the value of compare(Object, Object)
	 * 
	 * @param a
	 *           a value as String, Integer or Float
	 * @param b
	 *           another value as String, Integer or Float
	 * @param at
	 *           the AttributeType to check, whether there are predefined values
	 * @return a value < 0, if a < b, 0 if a == b, a value > 0, if a > b
	 */
	private int compare(Object a, Object b, AttributeType at)
	{
		if (at.getPredefinedValues() != null)
		{
			String aStr = a.toString();
			String bStr = b.toString();

			if (aStr.equals(bStr))
				return 0;

			for (Object o : at.getPredefinedValues())
			{
				if (o.toString().equals(aStr))
					return -1;
				if (o.toString().equals(bStr))
					return 1;
			}
		}
		else
		{
			return compare(a, b);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowActivated(WindowEvent arg0)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosed(WindowEvent arg0)
	{
		results.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent arg0)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent
	 * )
	 */
	@Override
	public void windowDeactivated(WindowEvent arg0)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent
	 * )
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowIconified(WindowEvent arg0)
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowOpened(WindowEvent arg0)
	{
	}

	public void setCommandButton(JCommandButton activateFilterButton)
	{

	}
}
