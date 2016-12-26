/**
 * 
 */
package gui.admin;

import gui.ColorRenderer;
import gui.Messages;
import gui.RelationTypChooser;
import gui.RelationTypRenderer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import data.AkteurTyp;
import data.BackgroundInfo;
import data.Question;
import data.Question.DataType;
import data.Question.Subject;
import data.Question.Time;
import data.RelationTyp;

/**
 * 
 * 
 */
public class VariableDefinitionDialogImpl extends VariableDefinitonDialog
{

	private static final long	serialVersionUID	= 1L;

	/**
	 * This is usually <code>true</code>. If the dialog has been closed
	 * successfully using the OK button it is set to <code>false</code>. All
	 * calls to getQuestion() will return <code>null</code> unless this is set to
	 * <code>false</code>.
	 */
	private boolean				cancelled;

	/**
	 * The base model for the dialog. If none is given, a new model is created
	 * internally on demand.
	 */
	private Question				model;

	/**
	 * This is <code>true</code> when the dialog is used for ego's attributes.
	 * Then some options are not available or accessible in a different way.
	 */
	private boolean				ego;

	/**
	 * This is <code>true</code> when the dialog is used for relational
	 * attributes. Then some options are not available or accessible in a
	 * different way.
	 */
	private boolean				relation				= false;

	/**
	 * The ListModel for the central list of prefefined answers.
	 */
	private DefaultListModel	predefListModel;

	/**
	 * Enthält AkteurTyp-Objekte.
	 */
	private DefaultListModel	mapActorTypeListModel;

	/**
	 * Enthält die Größe der Akteure als int.
	 */
	private DefaultListModel	mapActorSizeListModel;

	/**
	 * Enthält RelationTyp-Objekte.
	 */
	private DefaultListModel	mapRelationTypeListModel;

	/**
	 * Enthält die Farben der Sektoren als Color-Objekt.
	 */
	private DefaultListModel	mapSectorListModel;

	/**
	 * Enthält leere Strings -- für die Circles haben wir erstmal keine weitere
	 * Konfiguration..
	 */
	private DefaultListModel	mapCircleListModel;

	private boolean				loaded				= false;

	/**
	 * @param parent
	 *           Parent-Frame of the Dialog
	 * @param ego
	 *           <code>true</code> if the dialog deals with ego's attributes
	 *           rather then attributes of alteri.
	 */
	public VariableDefinitionDialogImpl(Frame parent, boolean ego)
	{
		super(parent, true);
		this.ego = ego;
		this.cancelled = true;
		initGUILogic();
	}

	/**
	 * @param parent
	 *           Parent-Frame of the Dialog
	 * @param ego
	 *           <code>true</code> if the dialog deals with ego's attributes
	 *           rather then attributes of alteri.
	 */
	public VariableDefinitionDialogImpl(Frame parent, boolean ego,
			boolean relation)
	{
		super(parent, true);
		this.ego = ego;
		this.cancelled = true;
		this.relation = relation;
		initGUILogic();
	}

	/**
	 * This method sets the given question as the base model for the dialog. Any
	 * changes performed using the dialog are immediately available in this
	 * <code>Question</coDe> object after pressing the OK-button.
	 * 
	 * @param q
	 *           A valid question.
	 */
	public void setModel(Question q)
	{
		this.model = q;
		loaded = true;
		copyValuesFromQuestion(q);
		loaded = false;

	}

	/**
	 * Returns a <code>Question</code> object that contains the information
	 * neccessary for user interaction. If the dialog was given a model using
	 * <code>setQuestion</code> the model object is returned. Otherwise a new
	 * object is created.
	 * 
	 * @return A valid question or <code>null</code> if the dialog was cancelled
	 *         or closed via the window manager.
	 */
	public Question getModel()
	{
		/* THIS MIGHT WORK. */
		if (this.cancelled)
		{
			return null;
		}

		return model;
	}

	/**
	 * Copies all values from GUI elements to the given question-object.
	 * 
	 * @param retVal
	 *           A valid question object.
	 */
	private void copyValuesInQuestion(Question retVal)
	{
		retVal.setLabel(this.attributesNameField.getText());
		retVal.setQuestion(this.questionTextField.getText());
		retVal
				.setDataType((this.numericalAnswerButton.isSelected() ? DataType.NUMERICAL
						: DataType.STRING));
		retVal.setFreeForm(this.allowFreeAnswersBox.isSelected());
		retVal.setAllowMultipleSelection(this.allowMultipleSelectionsBox
				.isSelected());
		retVal
				.setNumericalMaxValue(((Number) this.maximalValueSpinner.getValue())
						.intValue());
		retVal
				.setNumericalMinValue(((Number) this.minimalValueSpinner.getValue())
						.intValue());
		retVal.setNumericalUnit(this.unitNameTextField.getText());
		retVal.setOfferNoAnswer(this.allowNoAnswerBox.isSelected());
		retVal.setOfferUnknown(this.allowDontKnowBox.isSelected());

		String[] answers = new String[this.predefListModel.getSize()];
		this.predefListModel.copyInto(answers);
		retVal.setPredefinedAnswers(answers);

		switch (this.askOnBox.getSelectedIndex())
		{
			case 0:
				retVal.setTime(Time.START);
				break;
			case 1:
				retVal.setTime(Time.ADDING);
				break;
			case 2:
				retVal.setTime(Time.END);
				break;
			default:
				assert false;
		}

		if (visualMappingSelection.getSelectedItem().equals("Actor Type"))
		{
			retVal.setVisualMappingMethod(Question.VisualMappingMethod.ACTOR_TYPE);
			retVal.setVisualMapping(mapActorTypeListModel.toArray());

			if (!loaded
					&& !InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "ActorType"))
			{
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "ActorType");
			}
			else if (!loaded
					&& InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "ActorType"))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"This attribute visualization is in use several times. It will be associated with this question");
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "ActorType");
			}
		}
		else if (visualMappingSelection.getSelectedItem().equals("Actor Size"))
		{
			retVal.setVisualMapping(mapActorSizeListModel.toArray());
			retVal.setVisualMappingMethod(Question.VisualMappingMethod.ACTOR_SIZE);

			if (!loaded
					&& !InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "ActorSize"))
			{
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "ActorSize");
			}
			else if (!loaded
					&& InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "ActorSize"))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"This attribute visualization is in use several times. It will be associated with this question");
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "ActorSize");
			}
		}
		else if (visualMappingSelection.getSelectedItem().equals("Relation Type"))
		{
			retVal
					.setVisualMappingMethod(Question.VisualMappingMethod.RELATION_TYPE);
			retVal.setVisualMapping(mapRelationTypeListModel.toArray());
			if (!loaded
					&& !InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "RelationType"))
			{
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "RelationType");
			}
			else if (!loaded
					&& InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "RelationType"))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"This attribute visualization is in use several times. It will be associated with this question");
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "RelationType");
			}
		}
		else if (visualMappingSelection.getSelectedItem().equals("Sector"))
		{
			retVal.setVisualMappingMethod(Question.VisualMappingMethod.SECTOR);
			retVal.setVisualMapping(mapSectorListModel.toArray());
			if (!loaded
					&& !InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "Sector"))
			{
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "Sector");
			}
			else if (!loaded
					&& InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "Sector"))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"This attribute visualization in use several times. It will be associated with this question");
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "Sector");
			}
		}
		else if (visualMappingSelection.getSelectedItem().equals("Circle"))
		{
			retVal.setVisualMappingMethod(Question.VisualMappingMethod.CIRCLE);
			retVal.setVisualMapping(mapCircleListModel.toArray());
			if (!loaded
					&& !InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "Circle"))
			{
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "Circle");
			}
			else if (!loaded
					&& InterviewConfigurationFrameImpl.isVisualMappingUsed(model
							.getLabel(), "Circle"))
			{
				JOptionPane
						.showMessageDialog(
								this,
								"This attribute visualization is in use several times. It will be associated with this question");
				InterviewConfigurationFrameImpl.setUsedVisualMapping(model
						.getLabel(), "Circle");
			}
		}

		retVal.setSubject((this.ego ? Subject.EGO : Subject.ALTER));

		if (visualMappingSelection.getSelectedItem().equals("Relation Type"))
			retVal.setSubject(Subject.RELATION);

		retVal.setMatrix(matrix.isSelected());
	}

	private void copyValuesFromQuestion(Question retVal)
	{
		this.attributesNameField.setText(retVal.getLabel());
		this.questionTextField.setText(retVal.getQuestion());
		this.numericalAnswerButton.setSelected(DataType.NUMERICAL.equals(retVal
				.getDataType()));
		this.allowFreeAnswersBox.setSelected(retVal.isFreeForm());
		this.allowMultipleSelectionsBox.setSelected(retVal
				.isAllowMultipleSelection());
		this.freeFormAnswerButton.setSelected(!this.numericalAnswerButton
				.isSelected()
				&& retVal.isFreeForm());

		this.maximalValueSpinner.setValue(retVal.getNumericalMaxValue());
		this.minimalValueSpinner.setValue(retVal.getNumericalMinValue());
		this.unitNameTextField.setText(retVal.getNumericalUnit());
		this.allowNoAnswerBox.setSelected(retVal.isOfferNoAnswer());
		this.allowDontKnowBox.setSelected(retVal.isOfferUnknown());
		this.predefListModel.clear();
		for (String s : retVal.getPredefinedAnswers())
			this.predefListModel.addElement(s);

		if (retVal.getTime() == Time.START)
			this.askOnBox.setSelectedIndex(0);
		else if (retVal.getTime() == Time.ADDING)
			this.askOnBox.setSelectedIndex(1);
		else if (retVal.getTime() == Time.END)
			this.askOnBox.setSelectedIndex(2);
		else
			assert (false);

		if (retVal.getSubject() == Subject.EGO)
			this.ego = true;
		else if (retVal.getSubject() == Subject.ALTER)
			this.ego = false;
		else if (retVal.getSubject() == Subject.RELATION)
			this.ego = false;
		else
			assert (false);

		if (retVal.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_TYPE)
		{
			visualMappingSelection.setSelectedIndex(1);
			mapActorTypeListModel.clear();
			for (Object o : retVal.getVisualMapping())
				mapActorTypeListModel.addElement(o);
		}
		else if (retVal.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_SIZE)
		{
			visualMappingSelection.setSelectedIndex(2);
			mapActorTypeListModel.clear();
			for (Object o : retVal.getVisualMapping())
				mapActorSizeListModel.addElement(o);
		}
		else if (retVal.getVisualMappingMethod() == Question.VisualMappingMethod.RELATION_TYPE)
		{
			visualMappingSelection.setSelectedIndex(3);
			mapRelationTypeListModel.clear();
			for (Object o : retVal.getVisualMapping())
				mapRelationTypeListModel.addElement(o);
		}
		else if (retVal.getVisualMappingMethod() == Question.VisualMappingMethod.SECTOR)
		{
			visualMappingSelection.setSelectedIndex(4);
			mapSectorListModel.clear();
			for (Object o : retVal.getVisualMapping())
				mapSectorListModel.addElement(o);
		}
		else if (retVal.getVisualMappingMethod() == Question.VisualMappingMethod.CIRCLE)
		{
			visualMappingSelection.setSelectedIndex(5);
			mapCircleListModel.clear();
			for (Object o : retVal.getVisualMapping())
				mapCircleListModel.addElement(o);
		}
		matrix.setSelected(retVal.isMatrix());

		boolean allowFree = allowFreeAnswersBox.isSelected();
		boolean num = numericalAnswerButton.isSelected();
		unitNameTextField.setEnabled(allowFree && num);
		minimalValueSpinner.setEnabled(allowFree && num);
		maximalValueSpinner.setEnabled(allowFree && num);

		predefinedAnswerDeleteButton.setEnabled(predefinedAnswersList
				.getSelectedIndex() != -1);
		predefinedAnswerEditButton.setEnabled(predefinedAnswersList
				.getSelectedIndex() != -1);
		freeFormAnswerButton.setEnabled(allowFree);
		numericalAnswerButton.setEnabled(allowFree);
	}

	/**
	 * Initializes the static GUI logic.
	 * 
	 * @param ego
	 */
	private void initGUILogic()
	{
		if (this.ego)
			dialogXHeader.setTitle(dialogXHeader.getTitle() + " for ego");
		else if (this.relation)
			dialogXHeader.setTitle(dialogXHeader.getTitle() + " for relations");
		else
			dialogXHeader.setTitle(dialogXHeader.getTitle() + " for alters");

		if (this.relation || this.ego)
		{
			// bei Relationen und Ego: Visual Mapping ausblenden
			jLabel8.setVisible(false);
			visualMappingSelection.setVisible(false);
			visualMappingList.setVisible(false);
			jScrollPane1.setVisible(false);
			visualMappingEdit.setVisible(false);
			matrix.setVisible(false);
		}

		visualMappingList.setModel(new DefaultListModel());
		mapActorTypeListModel = new DefaultListModel();
		mapActorSizeListModel = new DefaultListModel();
		mapRelationTypeListModel = new DefaultListModel();
		mapSectorListModel = new DefaultListModel();
		mapCircleListModel = new DefaultListModel();

		predefListModel = new DefaultListModel();
		predefinedAnswersList.setModel(predefListModel);
		predefinedAnswerNewButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// final String newAnswer = JOptionPane
				// .showInputDialog("Please enter new answer:");
				Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
				final String newAnswer = (String) JOptionPane.showInputDialog(
						getParent(), "", "Please enter new answer",
						JOptionPane.QUESTION_MESSAGE, icon, null, null);
				if (newAnswer != null)
				{
					predefListModel.addElement(newAnswer);

	
					AkteurTyp akteurTyp = new AkteurTyp(newAnswer);
					// akteurTyp.setImageFile(workingDirectory
					//							+ "images/icons/Circle.svg"); //$NON-NLS-1$
					akteurTyp.setImageFile("images/symbols/Circle.svg");
					mapActorTypeListModel.addElement(akteurTyp);
					InterviewConfigurationFrameImpl.addActorToChangePath(akteurTyp);

					// Trage eine sinnvolle Größe als Default ein.
					// Integer[] sizeArray = new Integer[] { 20, 40, 60, 80, 100, 120
					// };
					Integer[] sizeArray = new Integer[] { 5, 7, 9, 11, 13, 16 };
					int size = 50;
					if (mapActorSizeListModel.getSize() < sizeArray.length)
						size = sizeArray[mapActorSizeListModel.getSize()];
					mapActorSizeListModel.addElement(size);

					RelationTyp relationTyp = new RelationTyp(newAnswer);
					Color strokeColor = new Color(0, 0, 0);
					if (mapSectorListModel.getSize() < BackgroundInfo
							.getStandardColors().length)
						strokeColor = BackgroundInfo.getStandardColors()[mapSectorListModel
								.getSize()];
					relationTyp.setColor(strokeColor);
					mapRelationTypeListModel.addElement(relationTyp);

					Color color = new Color(255, 255, 255);
					if (mapSectorListModel.getSize() < BackgroundInfo
							.getStandardColors().length)
						color = BackgroundInfo.getStandardColors()[mapSectorListModel
								.getSize()];
					mapSectorListModel.addElement(color);

					mapCircleListModel.addElement(new String("")); //$NON-NLS-1$
				}
			}
		});

		predefinedAnswerEditButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// GUI logic ensures that this call is valid (hopefully!)
				// final String newAnswer = JOptionPane.showInputDialog(
				//						Messages.getString("VariableDefinitionDialogImpl.11"), predefinedAnswersList //$NON-NLS-1$
				// .getSelectedValue());
				Icon icon = new ImageIcon("icons/intern/icon.png"); //$NON-NLS-1$
				final String newAnswer = (String) JOptionPane.showInputDialog(
						getParent(), "", "Please enter new answer",
						JOptionPane.QUESTION_MESSAGE, icon, null, null);
				if (newAnswer != null)
				{
					predefListModel.set(predefinedAnswersList.getSelectedIndex(),
							newAnswer);
					if (mapActorTypeListModel.size() > 0)
					{
						((AkteurTyp) mapActorTypeListModel.get(predefinedAnswersList
								.getSelectedIndex())).setBezeichnung(newAnswer);
					}
					if (mapRelationTypeListModel.size() > 0)
					{
						((RelationTyp) mapRelationTypeListModel.get(predefinedAnswersList
								.getSelectedIndex())).setBezeichnung(newAnswer);
					}
				}
			}
		});
		visualMappingEdit.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (visualMappingSelection.getSelectedItem().equals("Actor Type"))
				{
					String availableImages[] = new File("icons/") //$NON-NLS-1$
							.list(new FilenameFilter()
							{
								@Override
								public boolean accept(File f, String s)
								{
									return s.toLowerCase().endsWith(".svg"); //$NON-NLS-1$
								}
							});
					if (availableImages != null) Arrays.sort(availableImages);
					String newAnswer = (String) JOptionPane.showInputDialog(
							getParent(), "Please select new actor type image.",
							"Property", JOptionPane.QUESTION_MESSAGE, null,
							availableImages, ((AkteurTyp) mapActorTypeListModel
									.get(visualMappingList.getSelectedIndex()))
									.getImageFile().substring("icons/".length())); //$NON-NLS-1$
					if (newAnswer != null)
					{
		
						((AkteurTyp) mapActorTypeListModel.get(visualMappingList
								.getSelectedIndex()))
								.setImageFile("images/symbols/" + newAnswer); //$NON-NLS-1$

						InterviewConfigurationFrameImpl
								.addActorToChangePath((AkteurTyp) mapActorTypeListModel
										.get(visualMappingList.getSelectedIndex()));
					}
				}
				else if (visualMappingSelection.getSelectedItem().equals(
						"Actor Size"))
				{
					final String newAnswer = JOptionPane.showInputDialog(
							getParent(), "Please enter new actor size:",
							visualMappingList.getSelectedValue());
					if (newAnswer != null)
					{
						try
						{
							int value = Integer.parseInt(newAnswer.trim());

							if (value > 30)
							{
								JOptionPane
										.showMessageDialog(
												getParent(),
												"The number you entered ist too large. Please choose a number between 0 and 30",
												"Number too large",
												JOptionPane.ERROR_MESSAGE);
							}
							else
							{
								mapActorSizeListModel.set(visualMappingList
										.getSelectedIndex(), value);
							}
						} catch (NumberFormatException exception)
						{
							JOptionPane.showMessageDialog(getParent(),
									"Please type in a numerical value.", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				else if (visualMappingSelection.getSelectedItem().equals(
						"Relation Type"))
				{
					RelationTypChooser chooser = new RelationTypChooser();
					chooser.show((RelationTyp) mapRelationTypeListModel
							.get(visualMappingList.getSelectedIndex()));
				}
				else if (visualMappingSelection.getSelectedItem().equals("Sector"))
				{
					Color newColor = JColorChooser.showDialog(null, Messages
							.getString("VennMaker.Pick_Color"), //$NON-NLS-1$
							(Color) mapSectorListModel.get(visualMappingList
									.getSelectedIndex()));
					if (newColor != null)
						mapSectorListModel.set(visualMappingList.getSelectedIndex(),
								newColor);
				}
				else if (visualMappingSelection.getSelectedItem().equals("Circle"))
				{
					// für Kreise gibt es (noch?) nichts zu bearbeiten.
				}
				repaint();
			}
		});

		predefinedAnswerDeleteButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				int visualMappingIndex = visualMappingList.getSelectedIndex();
				predefListModel.remove(predefinedAnswersList.getSelectedIndex());

				if (mapActorTypeListModel.size() > 0)
				{
					mapActorTypeListModel.remove(visualMappingIndex);
				}
				if (mapActorSizeListModel.size() > 0)
				{
					mapActorSizeListModel.remove(visualMappingIndex);
				}
				if (mapRelationTypeListModel.size() > 0)
				{
					mapRelationTypeListModel.remove(visualMappingIndex);
				}
				if (mapSectorListModel.size() > 0)
				{
					mapSectorListModel.remove(visualMappingIndex);
				}

			}
		});

		predefinedAnswersList
				.addListSelectionListener(new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						boolean isEditing = (predefinedAnswersList.getSelectedIndex() != -1);
						predefinedAnswerEditButton.setEnabled(isEditing);
						predefinedAnswerDeleteButton.setEnabled(isEditing);
						visualMappingEdit.setEnabled(isEditing
								&& visualMappingSelection.getSelectedIndex() != 0
								&& !visualMappingSelection.getSelectedItem().equals(
										"Circle"));

						visualMappingList.setSelectedIndex(predefinedAnswersList
								.getSelectedIndex());
					}
				});

		visualMappingList.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				predefinedAnswersList.setSelectedIndex(visualMappingList
						.getSelectedIndex());
			}
		});

		visualMappingSelection.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (visualMappingSelection.getSelectedItem()
						.equals("Relation Type"))
				{
					visualMappingList.setCellRenderer(new RelationTypRenderer(true));
				}
				else if (visualMappingSelection.getSelectedItem().equals("Sector"))
					visualMappingList.setCellRenderer(new ColorRenderer(true));
				else
					visualMappingList.setCellRenderer(new DefaultListCellRenderer());

				visualMappingList.setEnabled(visualMappingSelection
						.getSelectedIndex() != 0);
				boolean isEditing = (predefinedAnswersList.getSelectedIndex() != -1);
				visualMappingEdit
						.setEnabled(isEditing
								&& visualMappingSelection.getSelectedIndex() != 0
								&& !visualMappingSelection.getSelectedItem().equals(
										"Circle"));
				if (visualMappingSelection.getSelectedItem().equals("Actor Type"))
					visualMappingList.setModel(mapActorTypeListModel);
				else if (visualMappingSelection.getSelectedItem().equals(
						"Actor Size"))
					visualMappingList.setModel(mapActorSizeListModel);
				else if (visualMappingSelection.getSelectedItem().equals(
						"Relation Type"))
					visualMappingList.setModel(mapRelationTypeListModel);
				else if (visualMappingSelection.getSelectedItem().equals("Sector"))
					visualMappingList.setModel(mapSectorListModel);
				else if (visualMappingSelection.getSelectedItem().equals("Circle"))
					visualMappingList.setModel(mapCircleListModel);
				else
					visualMappingList.setModel(new DefaultListModel());

				visualMappingList.setSelectedIndex(predefinedAnswersList
						.getSelectedIndex());

				allowMultipleSelectionsBox.setEnabled(visualMappingSelection
						.getSelectedIndex() == 0);
				allowFreeAnswersBox.setEnabled(visualMappingSelection
						.getSelectedIndex() == 0);
				/*
				 * allowNoAnswerBox.setEnabled(visualMappingSelection
				 * .getSelectedIndex() == 0);
				 * allowDontKnowBox.setEnabled(visualMappingSelection
				 * .getSelectedIndex() == 0);
				 */
				askOnBox.setEnabled(visualMappingSelection.getSelectedIndex() == 0);
			}
		});

		visualMappingList
				.setEnabled(visualMappingSelection.getSelectedIndex() != 0);
		visualMappingEdit
				.setEnabled(visualMappingSelection.getSelectedIndex() != 0);

		allowMultipleSelectionsBox.setEnabled(visualMappingSelection
				.getSelectedIndex() == 0);
		/*
		 * allowFreeAnswersBox
		 * .setEnabled(visualMappingSelection.getSelectedIndex() == 0);
		 * allowNoAnswerBox .setEnabled(visualMappingSelection.getSelectedIndex()
		 * == 0);
		 */
		allowDontKnowBox
				.setEnabled(visualMappingSelection.getSelectedIndex() == 0);
		askOnBox.setEnabled(visualMappingSelection.getSelectedIndex() == 0);

		// Enables and disables related controls
		ActionListener answerTypeActionListener = new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean allowFree = allowFreeAnswersBox.isSelected();
				boolean num = numericalAnswerButton.isSelected();
				unitNameTextField.setEnabled(allowFree && num);
				minimalValueSpinner.setEnabled(allowFree && num);
				maximalValueSpinner.setEnabled(allowFree && num);

				predefinedAnswerDeleteButton.setEnabled(predefinedAnswersList
						.getSelectedIndex() != -1);
				predefinedAnswerEditButton.setEnabled(predefinedAnswersList
						.getSelectedIndex() != -1);
				freeFormAnswerButton.setEnabled(allowFree);
				numericalAnswerButton.setEnabled(allowFree);

			}

		};

		numericalAnswerButton.setActionCommand("num"); //$NON-NLS-1$
		numericalAnswerButton.addActionListener(answerTypeActionListener);
		freeFormAnswerButton.addActionListener(answerTypeActionListener);
		allowFreeAnswersBox.addActionListener(answerTypeActionListener);

		okButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (visualMappingSelection.getSelectedIndex() != 0
						&& ((DefaultListModel) visualMappingList.getModel())
								.getSize() == 0)
				// JOptionPane
				// .showMessageDialog(
				// null,
				// "At least one predefined answer is required for a question with visual mapping.",
				// "Error", JOptionPane.ERROR_MESSAGE);
				{
					JOptionPane
							.showMessageDialog(
									getParent(),
									"At least one predefined answer is required for a question with visual mapping.",
									"", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					if (model == null)
						model = new Question();
					copyValuesInQuestion(model);
					cancelled = false;
					setVisible(false);
					dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				cancelled = true;
				setVisible(false);
				dispose();
			}
		});
	}
}
