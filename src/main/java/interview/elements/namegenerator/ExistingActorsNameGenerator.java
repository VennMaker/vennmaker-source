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
import interview.configuration.NameInfoSelection.NameInfoSelector;
import interview.configuration.attributesSelection.AttributeValueSelector;
import interview.configuration.attributesSelection.SingleAttributeValuePanel;
import interview.configuration.filterSelection.FilterPanel;
import interview.elements.StandardElement;
import interview.elements.information.NameGeneratorInformation;
import interview.panels.other.ActorTransferHandler;
import interview.panels.other.DraggableActor;
import interview.settings.UndoElement;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.Akteur;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import events.ComplexEvent;
import events.DeleteActorEvent;
import events.NewActorEvent;
import events.RenameActorEvent;

/**
 * Almost like the "NameGenerator" Element, but the actors are chosen from a
 * list (of already entered actors)
 * 
 * 
 * 
 */
public class ExistingActorsNameGenerator extends StandardElement implements
		UndoElement, IECategory_NameGenerator
{

	private static final long						serialVersionUID	= 1L;

	private NameInfoSelector						nSelector;

	private JTextArea									frageInput;

	private JTextField								nameInputField;

	private Object										selectedAttributeValue;

	private JTextArea									questionArea;

	private Vector<Akteur>							actorsInNameList;

	private Vector<Akteur>							actorsToCreate;

	private JList<DraggableActor>					selectedActorsList;

	private DefaultListModel<DraggableActor>	selectedActorsListModel;

	private DefaultListModel<DraggableActor>	alreadyNamedListModel;

	private JPanel										configurationPanel;

	private JPanel										controllerDialog;

	private Map<Akteur, Object>					oldActorAttributes;

	private AttributeValueSelector				aSelector;

	private JLabel										numberAllActors;

	private int											numberOfAllActors	= 0;

	private JCheckBox									limitedNumberOfActors;

	private JSpinner									maxActorSpinner;

	private JSpinner									minActorSpinner;

	private Boolean									checkBoxEnabled;

	public ExistingActorsNameGenerator()
	{
		super(null, new FilterPanel(), null, true);
	}

	/**
	 * Returns the dialog for the interview controller
	 */
	@Override
	public JPanel getControllerDialog()
	{
		controllerDialog = new JPanel(new GridBagLayout());

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

		Vector<Netzwerk> networks = VennMaker.getInstance().getProject()
				.getNetzwerke();
		List<Akteur> actors = fSelector.getFilteredActors();

		if (actors.contains(VennMaker.getInstance().getProject().getEgo()))
		{
			actors.remove(VennMaker.getInstance().getProject().getEgo());
		}

		oldActorAttributes = new HashMap<Akteur, Object>();

		// nameList
		selectedActorsListModel = new DefaultListModel<DraggableActor>();
		selectedActorsList = new JList<DraggableActor>(selectedActorsListModel);
		selectedActorsList.setFocusable(false);

		actorsInNameList = new Vector<Akteur>();
		actorsToCreate = new Vector<Akteur>();

		for (int i = actors.size() - 1; i >= 0; i--)
		{
			Akteur actor = actors.get(i);
			for (Netzwerk network : networks)
			{
				Object aValue = actor.getAttributeValue(
						aSelector.getSelectedAttribute(), network);

				if (aValue == selectedAttributeValue
						&& !actorsInNameList.contains(actor))
				{
					oldActorAttributes.put(actor, aValue);
					DraggableActor da = new DraggableActor(actor);
					selectedActorsListModel.addElement(da);
					actorsInNameList.add(actor);
				}
			}
		}

		for (Akteur actor : actorsToCreate)
		{
			if (!actorsInNameList.contains(actor))
			{
				DraggableActor da = new DraggableActor(actor);
				selectedActorsListModel.addElement(da);
			}
		}

		selectedActorsList.setDragEnabled(true);
		selectedActorsList.setDropMode(DropMode.INSERT);
		selectedActorsList.setTransferHandler(new ActorTransferHandler(
				selectedActorsList, false));

		// Double click on entry ---> show Edit Name Dialog
		selectedActorsList.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					if (selectedActorsList.getSelectedIndex() >= 0)
					{
						Window dia = SwingUtilities
								.windowForComponent(selectedActorsList);
						RenameDraggableActorDialog.showDialog(dia,
								selectedActorsListModel, actorsToCreate,
								selectedActorsList.getSelectedValue());
					}
				}
			}
		});

		JScrollPane nameListPane = new JScrollPane(selectedActorsList);
		nameListPane.setFocusable(false);
		nameListPane.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("ExistingActorsNameGenerator.Actors")));
		// -------------------------------

		// alreadyNamedList
		final JList<DraggableActor> alreadyNamedList = new JList<DraggableActor>();
		alreadyNamedList.setFocusable(false);
		alreadyNamedListModel = new DefaultListModel<DraggableActor>();
		alreadyNamedList.setModel(alreadyNamedListModel);
		alreadyNamedList.setDragEnabled(true);
		alreadyNamedList.setDropMode(DropMode.INSERT);
		alreadyNamedList.setTransferHandler(new ActorTransferHandler(
				alreadyNamedList, false));
		JScrollPane alreadyNamedPane = new JScrollPane(alreadyNamedList);
		alreadyNamedList.setFocusable(false);
		alreadyNamedPane.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("ExistingActorsNameGenerator.AvailableActors")));
		for (Akteur actor : actors)
		{
			if (!actorsInNameList.contains(actor))
			{
				DraggableActor da = new DraggableActor(actor);
				alreadyNamedListModel.addElement(da);
			}
		}

		selectedActorsList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent arg0)
			{
				if (selectedActorsList.getSelectedIndex() >= 0) {
					alreadyNamedList.clearSelection();
				}
								
				updateNumberActors();
				updateNumberActorsText();
			}
		});

		alreadyNamedList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent arg0)
			{
				if (alreadyNamedList.getSelectedIndex() >= 0) {
					selectedActorsList.clearSelection();
				}

				updateNumberActors();
				updateNumberActorsText();
			}
		});

		// questionArea
		questionArea = new JTextArea(aSelector.getAttributesAndQuestions()
				.keySet().iterator().next());
		questionArea.setEditable(false);
		questionArea.setFocusable(false);
		questionArea.setLineWrap(true);
		questionArea.setWrapStyleWord(true);
		questionArea.setBackground(controllerDialog.getBackground());
		Font f = questionArea.getFont().deriveFont(Font.BOLD);
		questionArea.setFont(f.deriveFont(VennMakerUIConfig.getFontSize() + 4f));

		JScrollPane questionAreaPane = new JScrollPane(questionArea);
		questionAreaPane.setFocusable(false);
		questionAreaPane.setBorder(null);

		// -------------------------------
		final String content = Messages
				.getString("NameGenerator.InsertActorNames");
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

		JButton addButton = new JButton(Messages.getString("NameGenerator.Add")); //$NON-NLS-1$

		addButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				addActorRoutine();
				nameInputField.requestFocusInWindow();

			}
		});

		JButton deleteButton = new JButton(
				Messages.getString("NameGenerator.Delete")); //$NON-NLS-1$

		deleteButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				DraggableActor da = (DraggableActor) selectedActorsList
						.getSelectedValue();

				if (da != null)
				{
					if (actorsToCreate.contains(da.actor))
						actorsToCreate.remove(da.actor);
					else
						alreadyNamedListModel.add(0, da);

					selectedActorsListModel.removeElement(da);
					actorsInNameList.remove(da.actor);
				}
				else
				{
					da = alreadyNamedList.getSelectedValue();
					if (da != null)
					{
						// TODO: remove ACTORS - no matter whuuttt
						actorsToCreate.remove(da.actor);
						VennMaker.getInstance().getProject().getAkteure()
								.remove(da.actor);
						alreadyNamedListModel.remove(alreadyNamedList
								.getSelectedIndex());

						// alreadyNamedList.remove(alreadyNamedList.getSelectedIndex());
						// Window dia = SwingUtilities
						// .windowForComponent(selectedActorsList);
						// JOptionPane.showMessageDialog(
						// dia,
						// Messages
						// .getString("ExistingActorsNameGenerator.CantDeleteAvailableActor"));
					}
				}
				// selectedActorsListModel.removeElement(selectedActorsList.getSelectedValue());

				updateNumberActors();
				updateNumberActorsText();
			}
		});

		JPanel listPanel = new JPanel(new GridLayout(0, 2));
		listPanel.add(nameListPane);
		listPanel.add(alreadyNamedPane);

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
		controllerDialog.add(listPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.weighty = 0.1;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.fill = GridBagConstraints.NONE;
		controllerDialog.add(deleteButton, gbc);

		return controllerDialog;
	}
	
	
	private boolean isMaximum() {
		int maxActors = (Integer) maxActorSpinner.getValue();

		if (numberOfAllActors + 1 > maxActors && maxActors != 0)
		{
			JOptionPane
					.showMessageDialog(
							VennMaker.getInstance(),
							Messages.getString("NameGenerator.Define") + maxActors + Messages.getString("NameGenerator.Actors")); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		return false;
	}

	/**
	 * This Routine gets started if the user trys to add a new actor. It tests if
	 * the name is unique and handles cases if not.
	 */
	private void addActorRoutine()
	{
		int maxActors = (Integer) maxActorSpinner.getValue();

		if (nameInputField.getText().equals("")) //$NON-NLS-1$
		{
			JOptionPane.showMessageDialog(VennMaker.getInstance(),
					Messages.getString("NameGenerator.InsertName")); //$NON-NLS-1$
			return;
		}
		else if (isMaximum() == true)
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
				if (a != null && a.getName().equals(name))
				{
					unique = false; // actor name is already existing
					existing_actor = a;
					break;
				}
			if (unique)
			{
				for (Akteur a : actorsToCreate)
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
				d.setActorName(name, actorsToCreate);

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
				}
			}
		} while (!unique && !take_existing && !cancel);

		if (cancel)
			return;

		if (take_existing)
		{
			for (int i = 0; i < selectedActorsListModel.getSize(); i++)
			{
				DraggableActor da = (DraggableActor) selectedActorsListModel
						.getElementAt(i);
				if (da.actor.equals(existing_actor))
					return;
			}

			selectedActorsListModel.add(0, new DraggableActor(existing_actor));

			int pos = -1;
			for (int i = 0; i < alreadyNamedListModel.size(); i++)
			{
				DraggableActor da = (DraggableActor) alreadyNamedListModel
						.getElementAt(i);
				if (da.actor.equals(existing_actor))
				{
					pos = i;
					break;
				}
			}
			if (pos != -1)
				alreadyNamedListModel.remove(pos);
			return;
		}

		if (unique)
		{
			Akteur newActor = new Akteur(name);
			selectedActorsListModel.add(0, new DraggableActor(newActor));
			actorsToCreate.add(newActor);
		}

		nameInputField.setText(""); //$NON-NLS-1$

		updateNumberActors();
		updateNumberActorsText();
	}

	/**
	 * Executed if user clicks "next" in the interview controller This method
	 * controlls if the max. or min. actors numbers match the properties made in
	 * the configuration dialog
	 */
	@Override
	public boolean writeData()
	{

		Vector<Akteur> currentActors = VennMaker.getInstance().getProject()
				.getAkteure();

		int gesamtAnzahl = currentActors.size();
		for (Akteur actor : actorsToCreate)
		{
			if (!currentActors.contains(actor))
				gesamtAnzahl++;
		}

		AttributeType attribute = aSelector.getSelectedAttribute();
		ComplexEvent createActors = new ComplexEvent("AddActors");

		if (InterviewController.getInstance().isInTestMode())
			createActors.setIsLogEvent(false);

		for (Akteur actor : actorsToCreate)
		{
			if (!currentActors.contains(actor))
			{
				createActors.addEvent(new NewActorEvent(actor));

				Vector<Netzwerk> allNetworks = VennMaker.getInstance().getProject()
						.getNetzwerke();

				for (Netzwerk net : allNetworks)
					actor.setAttributeValue(attribute, net, selectedAttributeValue);
			}
		}

		EventProcessor.getInstance().fireEvent(createActors);

		for (int i = 0; i < selectedActorsListModel.size(); i++)
		{
			Akteur actor = ((DraggableActor) selectedActorsListModel
					.getElementAt(i)).actor;
			actor.setAttributeValue(attribute, VennMaker.getInstance()
					.getProject().getCurrentNetzwerk(), selectedAttributeValue);
		}
		// Bei allen Akteuren der rechten Liste (wurde nicht ausgewaehlt)
		// wird der Attributwert auf default bzw. null gesetzt
		for (int i = 0; i < alreadyNamedListModel.size(); i++)
		{
			Akteur actor = ((DraggableActor) alreadyNamedListModel.elementAt(i)).actor;
			Object defaultValue = attribute.getDefaultValue();
			actor.setAttributeValue(attribute, VennMaker.getInstance()
					.getProject().getCurrentNetzwerk(), defaultValue);
		}

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
		btnAddAttribute.addActionListener(new NewCategoricalAttributeListener2(
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

		aSelector = new SingleAttributeValuePanel(false);
		super.aSelector = aSelector;
		aSelector.updatePanel(VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR"));

		JScrollPane frageInputPane = new JScrollPane(frageInput);
		frageInputPane.setMinimumSize(new Dimension(100, 100));

		GridBagConstraints gbc;

		configurationPanel = new JPanel(new GridBagLayout());

		int x = 0;
		int y = 0;

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 4;
		gbc.insets = new Insets(0, 0, 10, 0);
		configurationPanel.add(nameInfoPanel, gbc);

		gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = ++y;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 4;
		gbc.insets = new Insets(10, 0, 10, 0);
		configurationPanel.add(aSelector.getConfigurationPanel(), gbc);

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

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.gridwidth = 3;
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

		gbc.gridx = 0;
		gbc.gridy = ++y;
		gbc.weightx = 1.0;
		gbc.gridwidth = 3;
		gbc.weighty = 1.5;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(10, 0, 0, 0);
		configurationPanel.add(fSelector.getConfigurationPanel(), gbc);

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
	public void deinitPreview()
	{
		super.deinitPreview();
		actorsToCreate.clear();
		selectedActorsListModel = new DefaultListModel<DraggableActor>();
		selectedActorsList.setModel(selectedActorsListModel);
		actorsInNameList.clear();
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("ExistingActorsNameGenerator.Description"); //$NON-NLS-1$
	}

	public boolean validateInput()
	{
		int maxActors = (Integer) maxActorSpinner.getValue();
		int minActors = (Integer) minActorSpinner.getValue();

		if (numberOfAllActors < minActors && minActors != 0)
		{
			JOptionPane
					.showMessageDialog(
							VennMaker.getInstance(),
							Messages.getString("NameGenerator.DefineLast") + minActors + Messages.getString("NameGenerator.Actors")); //$NON-NLS-1$ //$NON-NLS-2$

			return false;
		}
		else if (numberOfAllActors > maxActors && maxActors > 0)
		{
			JOptionPane
					.showMessageDialog(
							VennMaker.getInstance(),
							Messages.getString("NameGenerator.Only") + " " + maxActors + Messages.getString("NameGenerator.ActorsAllowed") + (numberOfAllActors - maxActors) + Messages.getString("NameGenerator.Actors2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			return false;
		}

		return true;
	}

	@Override
	public boolean undo()
	{
		if (selectedActorsListModel == null)
			return true;

		for (int i = 0; i < selectedActorsListModel.size(); i++)
		{
			Akteur actor = ((DraggableActor) selectedActorsListModel
					.getElementAt(i)).actor;
			actor.setAttributeValue(aSelector.getSelectedAttribute(), VennMaker
					.getInstance().getProject().getCurrentNetzwerk(),
					oldActorAttributes.get(actor));
		}

		ComplexEvent deleteAddedActors = new ComplexEvent("DeleteAddedActors");

		if (InterviewController.getInstance().isInTestMode())
			deleteAddedActors.setIsLogEvent(false);

		for (Akteur actor : actorsToCreate)
			deleteAddedActors.addEvent(new DeleteActorEvent(actor));

		EventProcessor.getInstance().fireEvent(deleteAddedActors);

		return true;
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		int maxActors = (Integer) maxActorSpinner.getValue();
		int minActors = (Integer) minActorSpinner.getValue();

		NameGeneratorInformation info = new NameGeneratorInformation(aSelector
				.getAttributesAndQuestions().keySet().iterator().next(),
				nSelector.getInfo(), checkBoxEnabled, minActors, maxActors,
				aSelector.getSelectedValue().toString(), aSelector
						.getSelectedAttribute().toString(), fSelector.getFilter());
		info.setId(this.getId());
		info.createChildInformation(children);
		info.setElementName(this.getElementNameInTree());

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
		fSelector.setFilter(info.getFilter());

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

	private void updateNumberActors()
	{
		Vector<Akteur> currentActors = VennMaker.getInstance().getProject().getAkteure();

//		this.numberOfAllActors = currentActors.size() + actorsToCreate.size();
		this.numberOfAllActors = selectedActorsListModel.size();

		if (currentActors.contains(VennMaker.getInstance().getProject().getEgo()))
			this.numberOfAllActors--;

		for (Akteur actor : actorsToCreate)
		{
			if (currentActors.contains(actor))
				this.numberOfAllActors--;
		}

		/*
		 * for (Akteur actor : actorsToDelete) { if (
		 * currentActors.contains(actor)) this.numberOfAllActors--; }
		 */
	}

}

class NewCategoricalAttributeListener2 implements ActionListener
{

	private AttributeType					attr	= null;

	private ExistingActorsNameGenerator	caller;

	public NewCategoricalAttributeListener2(ExistingActorsNameGenerator caller)
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

class RenameDraggableActorDialog
{
	public static void showDialog(Window parent, DefaultListModel listModel,
			List<Akteur> actorsToCreate, Object elem)
	{
		boolean draggableA = false;
		if (elem instanceof DraggableActor)
			draggableA = true;

		Akteur actor = null;
		if (draggableA)
			actor = ((DraggableActor) elem).actor;
		else
			actor = (Akteur) elem;
		String name = actor.getName();

		Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
		String result = (String) JOptionPane
				.showInputDialog(
						parent,
						Messages.getString("VennMaker.Rename_Actor"), Messages.getString("VennMaker.Rename_Actor"), JOptionPane.QUESTION_MESSAGE, icon, null, name); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		if (result != null)
		{
			boolean isDup = false;
			for (Akteur a : VennMaker.getInstance().getProject().getAkteure())
			{
				if ((a.getName().equals(result)) && (a != actor))
				{
					isDup = true;
					break;
				}
			}

			if (isDup)
				switch (VennMaker.getInstance().getConfig().getDuplicateBehaviour())
				{
					case NOT_ALLOWED:
						JOptionPane.showMessageDialog(parent,
								Messages.getString("VennMaker.No_Duplicate")); //$NON-NLS-1$
						return;
					case ALLOWED:
						break;
					case ASK_USER:
						int val = JOptionPane.showConfirmDialog(parent,
								Messages.getString("VennMaker.Already_Same"), //$NON-NLS-1$
								Messages.getString("VennMaker.Duplicate_Actor"), //$NON-NLS-1$
								JOptionPane.YES_NO_OPTION);

						if (val == JOptionPane.YES_OPTION)
						{
							break;
						}
						// eigentlich überlüssige abfrage, aber
						// sicher
						// ist sicher
						else if (val == JOptionPane.NO_OPTION)
						{
							return;
						}
					default:
						assert (false);
				}

			listModel.removeElement(elem);
			// not existing actor
			if (actorsToCreate.contains(actor))
			{
				Akteur a = actorsToCreate.get(actorsToCreate.indexOf(actor));
				a.setName(result);
				actor = a;
			}
			else
			{
				RenameActorEvent actorEvent = new RenameActorEvent(actor, result);
				EventProcessor.getInstance().fireEvent(actorEvent);
			}

			if (draggableA)
				listModel.addElement(new DraggableActor(actor));
			else
				listModel.addElement(actor);
		}
	}
}
