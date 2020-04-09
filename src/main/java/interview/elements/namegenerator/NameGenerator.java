/**
 * 
 */
package interview.elements.namegenerator;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.individual.EditIndividualAttributeTypeDialog;
import gui.configdialog.settings.SettingAddAttributeType;
import gui.utilities.VennMakerUIConfig;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_NameGenerator;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.configuration.attributesSelection.AttributeValueSelector;
import interview.configuration.attributesSelection.SingleAttributeValuePanel;
import interview.configuration.filterSelection.FilterPanel;
import interview.elements.StandardElement;
import interview.elements.information.NameGeneratorInformation;
import interview.settings.UndoElement;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import data.Akteur;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import events.ComplexEvent;
import events.DeleteActorEvent;
import events.NewActorEvent;
import events.RemoveActorEvent;

/**
 * Objects of this class represent a name generator for generating actors with
 * one special attribute set
 * 
 * 
 */

public class NameGenerator extends StandardElement implements UndoElement,
		IECategory_NameGenerator
{
	private static final long				serialVersionUID	= 1L;

	private JTextArea							frageInput;

	private JCheckBox							limitedNumberOfActors;

	private JSpinner							maxActorSpinner;

	private JSpinner							minActorSpinner;

	private Object								selectedAttributeValue;

	private JTextArea							questionArea;

	private JTextField						nameInputField;

	private JList<Akteur>					nameList;

	private JButton							deleteButton;

	private DefaultListModel<Akteur>		listModel;

	private List<Akteur>						actorsToDelete;

	private List<Akteur>						addedActors;

	private JPanel								configurationPanel;

	private Boolean							checkBoxEnabled;

	private Map<String, AttributeType>	attributesAndQuestions;

	private AttributeValueSelector		aSelector;

	private JLabel								numberAllActors;

	private int									numberOfAllActors	= 0;

	private JButton							addButton;

	public NameGenerator()
	{
		super(null, new FilterPanel(), null, true);
	}

	/**
	 * Returns the dialog for the interview controller
	 */
	@Override
	public JPanel getControllerDialog()
	{
		if (actorsToDelete == null)
			actorsToDelete = new ArrayList<Akteur>();

		if (addedActors == null)
			addedActors = new ArrayList<Akteur>();

		if (attributesAndQuestions == null)
			attributesAndQuestions = new HashMap<String, AttributeType>();

		JPanel controllerDialog = new JPanel(new GridBagLayout());

		// in interviewController
		// "currentElement.getControllerDialog().requestFocusInWindow()"
		// is called, so catch this call here and set Focus to nameInputField
		controllerDialog.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				nameInputField.requestFocusInWindow();
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();

		questionArea = new JTextArea(frageInput.getText());
		questionArea.setEditable(false);
		questionArea.setFocusable(false);
		questionArea.setBackground(controllerDialog.getBackground());
		questionArea.setLineWrap(true);
		questionArea.setWrapStyleWord(true);
		Font f = questionArea.getFont().deriveFont(Font.BOLD);
		questionArea.setFont(f.deriveFont(VennMakerUIConfig.getFontSize() + 4f));

		JScrollPane questionAreaPane = new JScrollPane(questionArea);
		questionAreaPane.setFocusable(false);
		questionAreaPane.setBorder(null);

		nameList = new JList<Akteur>();
		nameList.setFocusable(false);

		if (listModel == null)
			listModel = new DefaultListModel<Akteur>();

		nameList.setModel(listModel);
		// Double click on entry ---> show Edit Name Dialog
		nameList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					if (nameList.getSelectedIndex() >= 0)
					{
						Window dia = SwingUtilities.windowForComponent(nameList);
						RenameDraggableActorDialog.showDialog(dia, listModel,
								addedActors, nameList.getSelectedValue());
					}
				}
			}
		});

		Vector<Akteur> actors = VennMaker.getInstance().getProject().getAkteure();

		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();

		if (actors.contains(VennMaker.getInstance().getProject().getEgo()))
			actors.remove(VennMaker.getInstance().getProject().getEgo());

		for (Akteur actor : actors)
		{
			for (Netzwerk network : networks)
			{
				Object aValue = actor.getAttributeValue(
						aSelector.getSelectedAttribute(), network);

				if (aValue == selectedAttributeValue && !listModel.contains(actor))
				{
					listModel.add(0, actor);
				}
			}
		}

		JScrollPane nameListPane = new JScrollPane(nameList);
		nameListPane.setMinimumSize(new Dimension(300, 200));
		nameListPane.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("ExistingActorsNameGenerator.Actors")));
if (aSelector.getAttributesAndQuestions() != null)
		this.questionArea.setText(aSelector.getAttributesAndQuestions().keySet()
				.iterator().next());

		final String content = Messages
				.getString("NameGenerator.InsertActorNames"); //$NON-NLS-1$
		nameInputField = new JTextField(content);
		nameInputField.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (nameInputField.getText().equals(content))
					nameInputField.setText("");
			}
		});

		nameInputField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					addActorRoutine();
				}
			}
		});

		nameInputField.setSelectionStart(0);
		nameInputField.setSelectionEnd(content.length());
		addButton = new JButton(Messages.getString("NameGenerator.Add"));

		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addActorRoutine();
				nameInputField.requestFocusInWindow();
			}
		});

		deleteButton = new JButton(Messages.getString("NameGenerator.Delete")); //$NON-NLS-1$

		deleteButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Akteur a = (Akteur) nameList.getSelectedValue();
				if (addedActors.contains(a))
				{
					addedActors.remove(a);
					actorsToDelete.add(a);
				}
				listModel.removeElement(nameList.getSelectedValue());
				nameInputField.requestFocusInWindow();
				updateNumberActors();
				updateNumberActorsText();

				if (listModel.size() == 0)
					deleteButton.setEnabled(false);
			}
		});

		this.updateNumberActors();
		numberAllActors = new JLabel();
		this.updateNumberActorsText();

		int y = 0;

		gbc.gridx = 0;
		gbc.gridy = y;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 2.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 3;
		controllerDialog.add(questionAreaPane, gbc);

		gbc.gridy = ++y;
		controllerDialog.add(nSelector.getControllerPanel(), gbc);

		gbc.gridy = ++y;
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(20, 5, 0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		controllerDialog.add(nameInputField, gbc);

		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(18, 5, 0, 0);
		controllerDialog.add(addButton, gbc);

		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(18, 5, 0, 0);
		controllerDialog.add(numberAllActors, gbc);

		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.weighty = 2.0;
		gbc.gridwidth = 3;
		gbc.insets = new Insets(10, 5, 5, 5);
		gbc.fill = GridBagConstraints.BOTH;
		controllerDialog.add(nameListPane, gbc);

		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.weighty = 0.1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		controllerDialog.add(deleteButton, gbc);

		return controllerDialog;
	}

	private void addActorRoutine()
	{
		int maxActors = (Integer) maxActorSpinner.getValue();

		if (nameInputField.getText().equals("")) //$NON-NLS-1$
		{
			JOptionPane.showMessageDialog(VennMaker.getInstance(),
					Messages.getString("NameGenerator.InsertName")); //$NON-NLS-1$
			return;
		}
		else if (listModel.size() + 1 > maxActors && maxActors != 0)
		{
			JOptionPane
					.showMessageDialog(
							VennMaker.getInstance(),
							Messages.getString("NameGenerator.Define") + maxActors + Messages.getString("NameGenerator.Actors")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		String name = nameInputField.getText();
		Vector<Akteur> projectActors = VennMaker.getInstance().getProject()
				.getAkteure();

		boolean unique = true;
		boolean take_existing = false;
		boolean cancel = false;

		Akteur existing_actor = null;
		do
		{
			unique = true;
			for (Akteur a : projectActors)
				if (a.getName().equals(name))
				{
					unique = false; // actor name is already existing
					existing_actor = a;
					break;
				}
			if (unique)
			{
				for (Akteur a : addedActors)
					if (a.getName().equals(name))
					{
						unique = false;
						existing_actor = a;
						break;
					}
			}
			if (!unique)
			{
				// user possibilities
				// 1. enter different name
				// 2. user means an actor who already exists
				// 3. cancel
				Window dia = SwingUtilities.windowForComponent(this.questionArea);
				ExistingActorDialog d = new ExistingActorDialog(dia);
				d.setActorName(name, addedActors);

				Point p = dia.getLocation();
				int dh = dia.getHeight();
				int dw = dia.getWidth();
				int w = d.getWidth();
				int h = d.getHeight();

				d.setLocation(new Point(p.x + (dw - w) / 2, p.y + (dh - h) / 2));
				d.setVisible(true);

				cancel = d.cancel;
				take_existing = d.yes;
				if (!d.yes)
				{
					JOptionPane pane = new JOptionPane();
					h = pane.getHeight();
					w = pane.getWidth();
					pane.setLocation(new Point(p.x + (dw - w) / 2, p.y + (dh - h)
							/ 2));
					name = pane.showInputDialog(
							Messages.getString("NameGenerator.InsertDifferentName"),
							name);

					if (name == null)
					{
						cancel = true;
					}
					else if (name.equals(""))
					{
						JOptionPane
								.showMessageDialog(null, Messages
										.getString("NameGenerator.InsertDifferentName"));
						cancel = true;
					}
				}
			}
		} while (!unique && !take_existing && !cancel);

		if (cancel)
			return;

		if (take_existing)
		{
			if (!listModel.contains(existing_actor))
				listModel.add(0, existing_actor);
		}
		else if (unique)
		{
			Akteur newActor = new Akteur(name);
			listModel.add(0, newActor);
			addedActors.add(newActor);
		}

		nameInputField.setText(""); //$NON-NLS-1$

		this.updateNumberActors();
		this.updateNumberActorsText();
		if (deleteButton.isEnabled() == false)
			deleteButton.setEnabled(true);
	}

	/**
	 * Executed if user clicks "next" in the interview controller This method
	 * controlls if the max. or min. actors numbers match the properties made in
	 * the configuration dialog
	 */
	@Override
	public boolean writeData()
	{
		ComplexEvent removeAndDelete = new ComplexEvent("DeleteAndRemoveActors"); //$NON-NLS-1$

		if (InterviewController.getInstance().isInTestMode())
			removeAndDelete.setIsLogEvent(false);

		for (Akteur actor : actorsToDelete)
		{

			if (actor == null)
				continue;

			Set<Netzwerk> actorNetworks = actor.getNetzwerke();

			if (actorNetworks.size() > 0)
			{
				for (Iterator<Netzwerk> iterator = actorNetworks.iterator(); iterator
						.hasNext();)
				{
					Netzwerk net = iterator.next();

					removeAndDelete.addEvent(new RemoveActorEvent(actor, net, actor
							.getLocation(net)));
				}
			}

			removeAndDelete.addEvent(new DeleteActorEvent(actor));

		}

		EventProcessor.getInstance().fireEvent(removeAndDelete);

		Vector<Akteur> currentActors = VennMaker.getInstance().getProject()
				.getAkteure();

		// Maximale Anzahl der Akteure ueberpruefen
		this.updateNumberActors();

		// int maxActors = (Integer)maxActorSpinner.getValue();
		// int minActors = (Integer)minActorSpinner.getValue();
		//
		// if (listModel.getSize() < minActors && minActors != 0)
		// {
		// JOptionPane
		// .showMessageDialog(
		// VennMaker.getInstance(),
		//							Messages.getString("NameGenerator.DefineLast") + minActors + Messages.getString("NameGenerator.Actors")); //$NON-NLS-1$ //$NON-NLS-2$
		//
		// return false;
		// }
		// else if (listModel.getSize() > maxActors && maxActors > 0)
		// {
		// JOptionPane
		// .showMessageDialog(
		// VennMaker.getInstance(),
		//							Messages.getString("NameGenerator.Only") + maxActors + Messages.getString("NameGenerator.ActorsAllowed") + (listModel.getSize() - maxActors) + Messages.getString("NameGenerator.Actors2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//
		// return false;
		// }

		AttributeType attribute = aSelector.getSelectedAttribute();

		ComplexEvent createActors = new ComplexEvent("AddActors"); //$NON-NLS-1$

		if (InterviewController.getInstance().isInTestMode())
			createActors.setIsLogEvent(false);

		/**
		 * loop threw the actors represented in the list of this interviewelement
		 * - don't forget the already existent actors and do not alter all actors
		 * of the current project
		 */
		for (int i = 0; i < listModel.size(); i++)
		{
			Akteur actor = listModel.get(i);

			if (!currentActors.contains(actor))
			{
				createActors.addEvent(new NewActorEvent(actor));
			}

			/* always set the attribute - even when actor already existed */
			Vector<Netzwerk> allNetworks = VennMaker.getInstance().getProject()
					.getNetzwerke();
			for (Netzwerk net : allNetworks)
				actor.setAttributeValue(attribute, net, selectedAttributeValue);

		}

		EventProcessor.getInstance().fireEvent(createActors);

		return true;
	}

	@Override
	public void setData()
	{

	}

	@Override
	public JPanel getConfigurationDialog()
	{
		if (configurationPanel != null)
		{
			aSelector.updatePanel(VennMaker.getInstance().getProject()
					.getAttributeTypes("ACTOR"));

			if (attributesAndQuestions != null)
				aSelector.setAttributesAndQuestions(attributesAndQuestions);

			return configurationPanel;
		}
		if (checkBoxEnabled == null)
			checkBoxEnabled = false;
		nSelector = new NameInfoPanel(false);
		JPanel nameInfoPanel = nSelector.getConfigurationPanel();

		frageInput = new JTextArea(
				Messages.getString("NameGenerator.InputQuestionHere"), 50, 50); //$NON-NLS-1$

		JButton btnAddAttribute = new JButton(new ImageIcon(
				"./icons/intern/PlusIcon.png")); //$NON-NLS-1$
		btnAddAttribute.addActionListener(new NewCategoricalAttributeListener(
				this));
		btnAddAttribute.setToolTipText(Messages
				.getString("NameGenerator.AddAttribute")); //$NON-NLS-1$

		JButton editAttribute = new JButton(new ImageIcon(
				"./icons/intern/emblem-system_small.png")); //$NON-NLS-1$
		editAttribute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				new EditIndividualAttributeTypeDialog().showDialog(aSelector
						.getSelectedAttribute());
			}
		});
		editAttribute.setToolTipText(Messages
				.getString("NameGenerator.EditAttribute")); //$NON-NLS-1$

		JScrollPane frageInputPane = new JScrollPane(frageInput);
		frageInputPane.setMinimumSize(new Dimension(100, 100));

		JLabel maxActor = new JLabel(
				Messages.getString("NameGenerator.MaxActors2")); //$NON-NLS-1$

		maxActorSpinner = new JSpinner();
		SpinnerNumberModel maxSpinnerModel = new SpinnerNumberModel();
		maxActorSpinner.setModel(maxSpinnerModel);

		maxActorSpinner.setEnabled(false);
		maxSpinnerModel.setMinimum(0);

		JLabel minActor = new JLabel(
				Messages.getString("NameGenerator.MinActors")); //$NON-NLS-1$

		minActorSpinner = new JSpinner();
		SpinnerNumberModel minSpinnerModel = new SpinnerNumberModel();
		minActorSpinner.setModel(minSpinnerModel);
		maxSpinnerModel.setMinimum(0);

		minActorSpinner.setEnabled(checkBoxEnabled);
		maxActorSpinner.setEnabled(checkBoxEnabled);

		limitedNumberOfActors = new JCheckBox(
				"<html>" + //$NON-NLS-1$
						Messages.getString("NameGenerator.LimitedNumberOfActors") + "</html>", checkBoxEnabled); //$NON-NLS-1$ //$NON-NLS-2$
		limitedNumberOfActors.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent arg0)
			{
				if (arg0.getStateChange() == ItemEvent.SELECTED)
					checkBoxEnabled = true;
				else
					checkBoxEnabled = false;

				minActorSpinner.setEnabled(checkBoxEnabled);
				maxActorSpinner.setEnabled(checkBoxEnabled);
			}
		});

		GridBagConstraints gbc;

		configurationPanel = new JPanel(new GridBagLayout());

		int x = 0;
		int y = 0;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.weighty = 2.0;
		gbc.gridwidth = 4;
		gbc.insets = new Insets(0, 0, 10, 0);
		configurationPanel.add(nameInfoPanel, gbc);

		aSelector = new SingleAttributeValuePanel(false);
		super.aSelector = aSelector;
		aSelector.updatePanel(VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"));

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = x;
		gbc.gridy = ++y;
		gbc.gridwidth = 4;
		gbc.weighty = 2.0;
		gbc.insets = new Insets(0, 0, 10, 0);
		configurationPanel.add(aSelector.getConfigurationPanel(), gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.gridwidth = 2;
		configurationPanel.add(limitedNumberOfActors, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.insets = new Insets(15, 0, 0, 0);
		configurationPanel.add(maxActor, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.weightx = 0.01;
		gbc.insets = new Insets(15, 0, 0, 0);
		configurationPanel.add(maxActorSpinner, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.weightx = 0.10;
		gbc.insets = new Insets(15, 0, 0, 0);
		configurationPanel.add(minActor, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 1;
		gbc.gridy = y;
		gbc.weightx = 0.01;
		gbc.insets = new Insets(15, 0, 0, 0);
		configurationPanel.add(minActorSpinner, gbc);

		// gbc.gridx = 0;
		// gbc.gridy = ++y;
		// gbc.weightx = 1.0;
		// gbc.gridwidth = 3;
		// gbc.weighty = 1.5;
		// gbc.fill = GridBagConstraints.BOTH;
		// gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		// gbc.insets = new Insets(10, 0, 0, 0);
		// configurationPanel.add(fSelector.getConfigurationPanel(), gbc);

		return configurationPanel;
	}

	@Override
	public boolean addToTree()
	{
		setElementNameInTree(nSelector.getName());
		int maxActors = 0, minActors = 0;
		if (checkBoxEnabled)
		{
			try
			{
				minActors = ((Integer) minActorSpinner.getValue()).intValue();
				maxActors = ((Integer) maxActorSpinner.getValue()).intValue();
			} catch (Exception nfe)
			{
				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						Messages.getString("NameGenerator.InputNumbers")); //$NON-NLS-1$
				return false;
			}
			if (minActors < 0 || maxActors == 0 || maxActors < 0)
			{
				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						Messages.getString("NameGenerator.NoNegativeValues")); //$NON-NLS-1$
				return false;
			}
			else if (minActors > maxActors)
			{
				JOptionPane.showMessageDialog(VennMaker.getInstance(),
						Messages.getString("NameGenerator.MinMaxActors")); //$NON-NLS-1$
			}
		}
		this.selectedAttributeValue = aSelector.getSelectedValue();
		this.attributesAndQuestions = aSelector.getAttributesAndQuestions();

		return true;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("NameGenerator.Description"); //$NON-NLS-1$
	}

	@Override
	public void deinitPreview()
	{
		super.deinitPreview();
		addedActors.clear();
		listModel = new DefaultListModel<Akteur>();
		nameList.setModel(listModel);
		actorsToDelete.clear();
	}

	@Override
	public boolean undo()
	{

		ComplexEvent deleteAddedActors = new ComplexEvent("DeleteAddedActors");
		deleteAddedActors.setIsLogEvent(false);

		if (addedActors != null)
		{
			for (Akteur actor : addedActors)
			{
				Set<Netzwerk> actorNetworks = actor.getNetzwerke();

				if (actorNetworks.size() > 0)
				{
					for (Iterator<Netzwerk> iterator = actorNetworks.iterator(); iterator
							.hasNext();)
					{
						Netzwerk net = iterator.next();

						deleteAddedActors.addEvent(new RemoveActorEvent(actor, net,
								actor.getLocation(net)));
					}
				}

				DeleteActorEvent evt = new DeleteActorEvent(actor);
				evt.setIsLogEvent(false);

				listModel.removeElement(actor);
				deleteAddedActors.addEvent(evt);
			}
			addedActors.clear();
		}
		EventProcessor.getInstance().fireEvent(deleteAddedActors);

		ComplexEvent restoreActors = new ComplexEvent("RestoreDeletedActors");
		restoreActors.setIsLogEvent(false);

		if (actorsToDelete != null)
		{
			for (Akteur actor : actorsToDelete)
			{
				if (addedActors.contains(actor))
					continue;

				restoreActors.addEvent(new NewActorEvent(actor));
				listModel.addElement(actor);
				addedActors.add(actor);
			}
			actorsToDelete.clear();
		}

		EventProcessor.getInstance().fireEvent(restoreActors);
		return true;
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		int maxActors = (Integer) maxActorSpinner.getValue();
		int minActors = (Integer) minActorSpinner.getValue();

		// NameGeneratorInformation info = new NameGeneratorInformation(aSelector
		// .getAttributesAndQuestions().keySet().iterator().next(),
		// nSelector.getInfo(), checkBoxEnabled, minActors, maxActors,
		// aSelector.getSelectedValue().toString(), aSelector
		// .getSelectedAttribute().toString(), fSelector.getFilter());

		NameGeneratorInformation info = new NameGeneratorInformation(aSelector
				.getAttributesAndQuestions().keySet().iterator().next(),
				nSelector.getInfo(), checkBoxEnabled, minActors, maxActors,
				aSelector.getSelectedValue().toString(), aSelector
						.getSelectedAttribute().toString(), null);
		info.createChildInformation(children);
		info.setElementName(nSelector.getName());
		info.setId(this.getId());

		if (parent != null)
		{
			info.setParentInformation(parent);
		}

		info.setElementClass(this.getClass());

		return info;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof NameGeneratorInformation))
			return;

		getConfigurationDialog();

		NameGeneratorInformation info = (NameGeneratorInformation) information;
		// this.frageInput.setText(info.getQuestion());
		this.checkBoxEnabled = info.isCheckBoxEnabled();
		this.limitedNumberOfActors.setSelected(checkBoxEnabled);
		this.minActorSpinner.setValue(Integer.valueOf(info.getMinActors()));
		this.maxActorSpinner.setValue(Integer.valueOf(info.getMaxActors()));

		super.setElementInfo(information);

		nSelector.setName(information.getElementName());
		nSelector.setInfo(info.getInfoText());
		setElementNameInTree(information.getElementName());

		Map<String, AttributeType> attsAndQuestions = new HashMap<String, AttributeType>();

		Vector<AttributeType> types = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		AttributeType searchedType = null;

		for (int i = 0; i < types.size(); i++)
		{
			if (types.get(i).toString().equals(info.getAttribute()))
			{
				searchedType = types.get(i);
				break;
			}
		}

		attsAndQuestions.put(info.getQuestion(), searchedType);
		attributesAndQuestions = attsAndQuestions;
		aSelector.setSelectedAttributeFromString(info.getAttribute());
		aSelector.setSelectedValueFromString(info.getAttributeValue());
		aSelector.setAttributesAndQuestions(attsAndQuestions);
		// fSelector.setFilter(info.getFilter());
		fSelector.setFilter(null);

		selectedAttributeValue = aSelector.getSelectedValue();

		InterviewLayer.getInstance().createChild(information, this);
	}

	public boolean shouldBeSkipped()
	{
		return false;
	}

	private void updateNumberActorsText()
	{
		if (this.numberOfAllActors >= 0)
			numberAllActors.setText(""
					+ Messages.getString("NameGenerator.Alteri") + ": "
					+ this.numberOfAllActors);
		/*
		 * if ((this.numberOfAllActors > VennMaker.getInstance().getProject()
		 * .getMaxNumberActors()) &&
		 * (VennMaker.getInstance().getProject().getMaxNumberActors() < 9999))
		 * numberAllActors.setText("" + this.numberOfAllActors + " von maximal " +
		 * VennMaker.getInstance().getProject().getMaxNumberActors() +
		 * " möglichen Personen (ZU VIELE PERSONEN!)");
		 */
	}

	public boolean validateInput()
	{
		int maxActors = (Integer) maxActorSpinner.getValue();
		int minActors = (Integer) minActorSpinner.getValue();

		String nameInputFieldText = nameInputField.getText();
		

		if (listModel.getSize() < minActors && minActors != 0)
		{
			JOptionPane
					.showMessageDialog(
							VennMaker.getInstance(),
							Messages.getString("NameGenerator.DefineLast") + minActors + Messages.getString("NameGenerator.Actors")); //$NON-NLS-1$ //$NON-NLS-2$

			return false;
		}
		else if (listModel.getSize() > maxActors && maxActors > 0)
		{
			JOptionPane
					.showMessageDialog(
							VennMaker.getInstance(),
							Messages.getString("NameGenerator.Only") + maxActors + Messages.getString("NameGenerator.ActorsAllowed") + (listModel.getSize() - maxActors) + Messages.getString("NameGenerator.Actors2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			return false;
		}
		
		boolean hasPossibleInputNotAdded = !(nameInputFieldText.isEmpty()
				|| nameInputFieldText.equals("") || nameInputFieldText //$NON-NLS-1$
				.equals(Messages.getString("NameGenerator.InsertActorNames")));  //$NON-NLS-2$
		if (hasPossibleInputNotAdded) // if
												// not
												// empty!
		{

			int i = JOptionPane
					.showConfirmDialog(
							VennMaker.getInstance(),
							String.format(
									Messages
											.getString("NameGenerator.NameInputFieldEnterValue"), 
									nameInputFieldText), 
							Messages
									.getString("NameGenerator.NameInputFieldInvalidated"), JOptionPane.YES_NO_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
			/*
			 * i = 0 -> yes clicked i = 1 -> no clicked
			 */
			if (i == 0)
			{
				addButton.doClick(); // simulate click on gui to add
			}
			return true;
		}

		return true;
	}

	private void updateNumberActors()
	{

		Vector<Akteur> currentActors = VennMaker.getInstance().getProject()
				.getAkteure();

		this.numberOfAllActors = currentActors.size() + this.addedActors.size();

		if (currentActors.contains(VennMaker.getInstance().getProject().getEgo()))
			this.numberOfAllActors--;

		for (Akteur actor : this.addedActors)
		{
			if (currentActors.contains(actor))
				this.numberOfAllActors--;
		}

		for (Akteur actor : actorsToDelete)
		{
			if (currentActors.contains(actor))
				this.numberOfAllActors--;
		}

	}

}

class NewCategoricalAttributeListener implements ActionListener
{
	private AttributeType	attr	= null;

	private NameGenerator	caller;

	public NewCategoricalAttributeListener(NameGenerator caller)
	{
		this.caller = caller;
	}

	private boolean existingName(String s)
	{
		s = s.toLowerCase();
		for (AttributeType a : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{
			if (a.getLabel().toLowerCase().equals(s))
				return true;
		}
		return false;
	}

	public AttributeType getAttributeType()
	{
		return attr;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.attr = null;
		Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
		String label = (String) JOptionPane
				.showInputDialog(
						ConfigDialog.getInstance(),
						Messages.getString("EditIndividualAttributeTypeDialog.15"), Messages.getString("EditIndividualAttributeTypeDialog.19"), JOptionPane.QUESTION_MESSAGE, icon, null, null); //$NON-NLS-1$ //$NON-NLS-2$
		if (label != null)
		{
			if (existingName(label))
			{
				JOptionPane
						.showMessageDialog(
								null,
								Messages.getString("VennMaker.Allready_Existing"), Messages //$NON-NLS-1$
										.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
			else if (!label.equals("")) //$NON-NLS-1$
			{
				attr = new AttributeType();
				attr.setLabel(label);
			}
			else
				JOptionPane
						.showMessageDialog(
								null,
								Messages.getString("VennMaker.Empty_Name"), Messages //$NON-NLS-1$
										.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}

		if (attr != null)
		{
			Object yes = Messages.getString("NewCategoricalAttributeListener.Yes"); //$NON-NLS-1$
			Object no = Messages.getString("NewCategoricalAttributeListener.No"); //$NON-NLS-1$
			attr.setPredefinedValues(new Object[] { yes, no });

			EditIndividualAttributeTypeDialog ed = new EditIndividualAttributeTypeDialog();

			ed.showDialog(attr);

			if (!ed.wasCanceled())
			{
				ConfigDialogTempCache.getInstance().addSetting(
						new SettingAddAttributeType(attr));

				// quick and dirty update of attribute list
				caller.getConfigurationDialog();
			}
			else
				attr = null;
		}
	}
}
