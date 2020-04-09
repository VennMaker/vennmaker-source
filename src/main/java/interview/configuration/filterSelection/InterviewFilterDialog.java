/**
 * Diese Klasse realisiert die Suche bzw die Filterfunktion
 */
package interview.configuration.filterSelection;

import files.FileOperations;
import gui.Messages;
import gui.VennMaker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * 
 * modified version of "FilterDialog" class (tweaked to fit in the interview
 * configurator dialog)
 * 
 */
public class InterviewFilterDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long		serialVersionUID			= 1L;

	/**
	 * Enthält die Combo-Boxen
	 */
	private JPanel						comboBoxPanel;

	/**
	 * Combo-Box für die Attribute
	 */
	private JComboBox					attributes;

	/**
	 * Combo-Box für die Attribut-Werte
	 */
	private JComboBox					values;

	/**
	 * Combo-Box für UND, ODER, " "
	 */
	private JComboBox					booleanValues;

	/**
	 * Combo-Box for equal, not-equal etc
	 */

	private JComboBox					condition;

	/**
	 * ArrayList die alle ComboBoxen enthält
	 */
	private ArrayList<JComboBox>	boxes;

	/**
	 * Liste mit Attributen
	 */
	private Vector<AttributeType>	attributeListe;

	/**
	 * Alle Akteure des Netzwerks
	 */
	private Vector<Akteur>			akteure;

	private Netzwerk					network;

	/**
	 * Liste mit Akteuren die die Kriterien der Suchanfrage erfüllen
	 */
	private ArrayList<Akteur>		results;

	private Akteur						currentActor;

	public static final int			LOWER							= 0;

	public static final int			LOWER_EQUALS				= 1;

	public static final int			EQUALS						= 2;

	public static final int			NOT_EQUALS					= 3;

	public static final int			HIGHER_EQUALS				= 4;

	public static final int			HIGHER						= 5;

	public static final int			OR								= 1;

	public static final int			AND							= 2;

	public static final int			EMPTY							= 0;

	private int							attributeBoxWidth			= 5;

	private int							conditionBoxWidth			= 3;

	private int							valuesBoxWidth				= 3;

	private int							booleanValuesBoxWidth	= 2;

	private int							xPosition					= 0;

	private int							row							= 0;

	private String						filter;

	private JScrollPane				scrollPane;

	private GridBagConstraints		gbc;

	private MySearchListener		msl;

	private FilterPanel				caller;

	private boolean					visible						= false;

	public InterviewFilterDialog(FilterPanel caller, String f,
			boolean visible)
	{
		this.caller = caller;
		this.filter = f;
System.out.println("InterviewFilterDialog: filter string:"+f);	
		setModal(visible);
		setTitle(Messages.getString("SearchDialog.0")); //$NON-NLS-1$
		setSize(700, 550);
		setLayout(new BorderLayout());
		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Icon_Kommentar"))).getImage()); //$NON-NLS-1$

		JLabel descriptionArea = new JLabel(Messages.getString("SearchDialog.15")); //$NON-NLS-1$

		add(descriptionArea, BorderLayout.NORTH);
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				results.clear();
			}
		});
		network = VennMaker.getInstance().getProject().getCurrentNetzwerk();
		akteure = VennMaker.getInstance().getProject().getAkteure();
		this.attributeListe = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");
		results = new ArrayList<Akteur>();

		Akteur ego = VennMaker.getInstance().getProject().getEgo();

		if (!akteure.contains(ego))
			akteure.add(ego);

		boxes = new ArrayList<JComboBox>();
		comboBoxPanel = new JPanel(new GridBagLayout());

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton(Messages.getString("SearchDialog.1")); //$NON-NLS-1$
		JButton cancelButton = new JButton(Messages.getString("SearchDialog.2")); //$NON-NLS-1$

		// msl = new MySearchListener();
		msl = new MySearchListener(this.filter);

		okButton.addActionListener(msl);
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

		attributes = new JComboBox(this.attributeListe);
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
		fillAttributes(0);

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		if (this.filter != null)
		{
			if (this.filter.split(";").length > 4) //$NON-NLS-1$
			{
				for (int i = 4; i < this.filter.split(";").length; i += 4) //$NON-NLS-1$
				{
					addComponents();
				}
			}
			setFilterNow();
		}

		setVisible(visible);
	}

	/**
	 * Die Methode wird aufgerufen wenn der Benutzer "UND" oder "ODER" als
	 * Verknüpfung auswählt
	 */
	private void addComponents()
	{
		JComboBox b1 = new JComboBox(this.attributeListe);
		JComboBox b2 = new JComboBox(
				new String[] {
						Messages.getString("SearchDialog.12"), Messages.getString("SearchDialog.4"), Messages.getString("SearchDialog.5") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		JComboBox b3 = new JComboBox(
				new String[] {
						Messages.getString("SearchDialog.6"), Messages.getString("SearchDialog.7"), Messages.getString("SearchDialog.8"), Messages.getString("SearchDialog.9"), Messages.getString("SearchDialog.10"), Messages.getString("SearchDialog.11") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		JComboBox b4 = new JComboBox();
		b1.addActionListener(new MyAttributeListener());
		b2.addActionListener(new MyBooleanListener());

		// b1.setPreferredSize(new Dimension(139, 26));
		// b3.setPreferredSize(new Dimension(139, 26));
		// b4.setPreferredSize(new Dimension(139, 26));

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

		boxes.get(index + 2).removeAllItems();
		AttributeType currentAttribute = null;
		Object o = boxes.get(index).getSelectedItem();

		// TODO: Workaround. Sollte eigentlich nicht vom Typ String sein?
		if (o instanceof String)
		{

			for (AttributeType attributetype : VennMaker.getInstance()
					.getProject().getAttributeTypes("ACTOR"))
			{
				if (attributetype.getLabel().equals(o))
				{
					currentAttribute = attributetype;
					break;
				}
			}
			if (currentAttribute == null)
				System.out.println("Nichts gefunden...");

		}
		else
		{
			currentAttribute = (AttributeType) o;
		}

		Object[] preValues = null;
		if (currentAttribute != null) preValues = currentAttribute.getPredefinedValues();
		if (preValues != null)
		{
			boxes.get(index + 2).addItem(Messages.getString("SearchDialog.16")); //$NON-NLS-1$
			for (Object obj : preValues)
			{
System.out.println("Werte "+obj.toString());
				boxes.get(index + 2).addItem(obj);
				boxes.get(index + 2).setPreferredSize(new Dimension(139, 26));
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

	private void setFilterNow()
	{
System.out.println("InterviewDialog: setFilterNow():"+caller+" filter: "+this.filter);
		String[] myFilter = this.filter.split(";"); //$NON-NLS-1$
		ArrayList<Integer> filterIndex = caller.getFilterIndex();
		Vector<AttributeType> attributeVector = VennMaker.getInstance()
				.getProject().getAttributeTypes("ACTOR");

		//if (filterIndex != null)
		{
			for (int i = 0; i < boxes.size(); i++)
			{
				if ( filterIndex!=null && (filterIndex.get(i) != -1 && boxes.size() < filterIndex.get(i)) )
				{
					boxes.get(i).setSelectedIndex(filterIndex.get(i));
				}
				else
				{
					// only for attributeType:
					// ..first box (=attribute):
					if (i % 4 == 0)
					{
						for (AttributeType at : attributeVector)
						{
							if (at.getLabel().equals(myFilter[i]))
							{
								boxes.get(i).insertItemAt(at, 0);
								break;
							}
						}
						boxes.get(i).removeItemAt(1);
						boxes.get(i).setSelectedIndex(0);
					}
					/* LOWER...HIGHER - box */
					else if (i % 4 == 1)
					{
						if (filterIndex != null) boxes.get(i).setSelectedIndex(filterIndex.get(i));
					}
					else
					{
						boxes.get(i).insertItemAt(myFilter[i], 0);
						boxes.get(i).removeItemAt(1);
						boxes.get(i).setSelectedIndex(0);
					}
				}
			}
		}
	}

	/**
	 * Transform filter map to filter string
	 * @param filters
	 */
	private void setNewFilter(HashMap<Integer, Vector<Filterparameter>> filters)
	{
		this.filter = "";
System.out.println("InterviewFilterDialog: setNewFilter");
		int counterOR = 0;
		for (int i : filters.keySet())
		{

			int counterUND = 0;

			for (Filterparameter fp : filters.get(i))
			{
				String comp = "";
				switch (fp.compareDirection)
				{
					case LOWER:
						comp = "smaller";
						break;
					case LOWER_EQUALS:
						comp = "smaller equal";
						break;
					case EQUALS:
						comp = "equal";
						break;
					case NOT_EQUALS:
						comp = "not equal";
						break;
					case HIGHER_EQUALS:
						comp = "higher equal";
						break;
					case HIGHER:
						comp = "higher";
						break;
				}

				this.filter += fp.at + ";" + comp + ";" + fp.value + ";";

				counterUND++;
				if (counterUND < filters.get(i).size())
				{
					this.filter += "AND;";
				}

			}

			counterOR++;
			if ((counterOR > 0) && (counterOR < filters.size()))
			{
				this.filter += "OR;";
			}
			else
			{
				this.filter += " ;";
			}

		}
	}

	public String getFilter()
	{
		System.out.println("InterviewFilterDialog: getFilter(): "+this.filter);
		return this.filter;
	}

	class MyAttributeListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			int index = boxes.indexOf(e.getSource());
			
			System.out.println("MyAttributeListener: actionPerformed(): "+index);
			
			fillAttributes(index);
		}

	}

	class MyBooleanListener implements ActionListener
	{

		/**
		 * Wird aufgerufen wenn der Benutzer "UND" oder "ODER" oder " " als
		 * Verknüpfung auswählt
		 */
		@Override
		public void actionPerformed(ActionEvent e)
		{
			JComboBox box = (JComboBox) e.getSource();

			/**
			 * Wird " " als Verknüpfung gewählt werden die restlichen Combo-Boxen
			 * entfernt, ansonsten werden Combo-Boxen hinzugefügt
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
	 * Hilfsklasse, um statt String eine Sammlung an Voraussetzungen zu speichern
	 */
	class Filterparameter
	{
		/** der Attributstyp */
		protected AttributeType	at;

		/** zu welchem Wert soll verglichen werden */
		protected Object			value;

		/**
		 * In welche "Richtung" soll verglichen werden (LOWER, LOWER_EQUALS,
		 * NOT_EQUALS, HIGHER_EQUALS, HIGHER, EMPTY)
		 */
		protected Integer			compareDirection;
	}

	/**
	 * Die Klasse realisiert die Suche
	 */
	class MySearchListener implements ActionListener
	{

		private String													filter;

		private ArrayList<Integer>									filterIndex;

		private boolean												makeFilter;

		private int														conditionBox	= 1;

		/**
		 * speichert die Voraussetzungen zum Filtern - alle Filtereinstellungen,
		 * die mit AND verknuepft sind, erhalten die gleiche Integer-Kennzahl. OR -
		 * Verknuepfungen werden durch unterschiedliche Integer-Kennzahlen
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
		public MySearchListener(String f)
		{
			this.filter = f;
			filterIndex = new ArrayList<Integer>();
System.out.println("MySearchListener: "+f);			
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			makeFilter = true;
			filter();
			dispose();

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
						for (AttributeType att : attributeListe)
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

					boxIndex++;
				}
			}

			/** after build: use this filter */
			for (Akteur actor : akteure)
			{
				currentActor = actor;

				if (checkOrExpressions(currentActor))
					results.add(currentActor);

			}

			setNewFilter(filters);
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

			// if (fp.value == null) fp.value = "";
			if (value == null)
				value = "";

			switch (fp.compareDirection)
			{
			// TODO: Comparator schreiben, um Vergleiche zu realisieren.
				case LOWER:
					if (compare(value, fp.value, fp.at) < 0)
						return true;
					break;
				case LOWER_EQUALS:
					if (compare(value, fp.value, fp.at) <= 0)
						return true;
					break;
				case EQUALS:
					if (compare(value, fp.value, fp.at) == 0)
						return true;
					break;
				case NOT_EQUALS:
					if (compare(value, fp.value, fp.at) != 0)
						return true;
					break;
				case HIGHER_EQUALS:
					if (compare(value, fp.value, fp.at) >= 0)
						return true;
					break;
				case HIGHER:
					if (compare(value, fp.value, fp.at) > 0)
						return true;
					break;
				default:
					return false;
			}
			return false;
		}
	}
	
	/**
	 * 
	 * @param Akteur a
	 * @param String attributeName
	 * @param Filterarameter fp
	 * @return boolean
	 */
	private Boolean checkExpressionString(Akteur a, String attributeName, Filterparameter fp)
	{		
		AttributeType aName = null;
		
		for (final AttributeType attributeType : VennMaker.getInstance().getProject().getAttributeTypes()) {

			if (attributeType.getType().equals("ACTOR"))
			{
				if (attributeType.getLabel().equals(attributeName)) {
					aName = attributeType;
				}
			}
		}
		
		Object value = currentActor.getAttributeValue(aName, network);

		// if (fp.value == null) fp.value = "";
		if (value == null)
			value = "";

		switch (fp.compareDirection)
		{
		// TODO: Comparator schreiben, um Vergleiche zu realisieren.
			case LOWER:
				if (compare(value, fp.value, fp.at) < 0)
					return true;
				break;
			case LOWER_EQUALS:
				if (compare(value, fp.value, fp.at) <= 0)
					return true;
				break;
			case EQUALS:
				if (compare(value, fp.value, fp.at) == 0)
					return true;
				break;
			case NOT_EQUALS:
				if (compare(value, fp.value, fp.at) != 0)
					return true;
				break;
			case HIGHER_EQUALS:
				if (compare(value, fp.value, fp.at) >= 0)
					return true;
				break;
			case HIGHER:
				if (compare(value, fp.value, fp.at) > 0)
					return true;
				break;
			default:
				return false;
		}
		return false;
	}

	public List<Akteur> getActors()
	{
		System.out.println("getActors...");
		// If no filter return all Actors
		if (this.filter == null || this.filter.equals("")){
	System.out.println("getActors: NIX");
			return VennMaker.getInstance().getProject().getAkteure();
		}
		
		
//		msl.actionPerformed(null);
		System.out.println("Neue Filterfunktion....");
		
		String[] myFilter = this.filter.split(";");
		
		for (Akteur actor : akteure)
		{
			currentActor = actor;
			Filterparameter fp = new Filterparameter();	
			
			if (myFilter[1].equals("smaller") ) {
				fp.compareDirection = 0;
			}

			if (myFilter[1].equals("smaller	equal") ) {
				fp.compareDirection = 1;
			}
			
			if (myFilter[1].equals("equal") ) {
				fp.compareDirection = 2;
			}
			
			if (myFilter[1].equals("higher equal") ) {
				fp.compareDirection = 3;
			}
			
			if (myFilter[1].equals("higher") ) {
				fp.compareDirection = 4;
			}
			
			fp.value = myFilter[2];

			for (final AttributeType attributeType : VennMaker.getInstance().getProject().getAttributeTypes()) {

				if (attributeType.getType().equals("ACTOR"))
				{
					if (attributeType.getLabel().equals(myFilter[0])) {
						fp.at = attributeType;
						break;
					}
				}
			}
			
			if (checkExpressionString(currentActor, myFilter[0], fp )) {
				this.results.add(currentActor);
			}			
		}
		
		return this.results;
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
		if ((at.getPredefinedValues() != null)
				&& (at.getPredefinedValues().length > 0))
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
}
