/**WizardListModel
 * 
 */
package gui.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import wizards.AskActorImportanceWizard;
import wizards.AskActorNamesWizard;
import wizards.AskAlterEndQuestionsWizard;
import wizards.AskAlterStartQuestionsWizard;
import wizards.AskEgoEndQuestionsWizard;
import wizards.AskEgoStartQuestionsWizard;
import wizards.AskRelationsWizard;
import wizards.NetworkWizard;
import wizards.SaveProjectWizard;
import wizards.TextWizard;
import wizards.VennMakerWizard;
import data.AkteurTyp;
import data.Config;
import data.Projekt;
import data.Question;
import files.FileOperations;
import files.VMPaths;
import gui.Messages;
import gui.OpenFileDialog;
import gui.SaveFileDialog;
import gui.StartChooser;
import gui.VennMaker;

/**
 * This is a wrapper for the InterviewConfigurationFrame which should not be
 * used directly, since it is created automatically by NetBeans.
 * 
 * This contains VennMakerLogic(TM).
 * 
 * 
 * 
 */
public class InterviewConfigurationFrameImpl extends
		InterviewConfigurationFrame
{

	/**
	 * 
	 */
	private static final long					serialVersionUID		= 1L;

	/**
	 * This is the model for the wizard list at tab #4
	 */
	private WizardListModel						enabledWizardsModel	= new WizardListModel();

	/**
	 * Wurde die Konfiguration bereits abgespeichert?
	 */
	private boolean								alreadySaved			= false;

	private static HashMap<String, String>	usedVisualMapping		= new HashMap<String, String>();

	private static Vector<AkteurTyp>			actorsToChangePath	= new Vector<AkteurTyp>();

	/**
	 * 
	 */
	public InterviewConfigurationFrameImpl()
	{
		super();
		initStaticLogic();
	}

	/**
	 * 
	 */
	private void initStaticLogic()
	{
		setIconImage(new ImageIcon(
				FileOperations.getAbsolutePath(VMPaths.VENNMAKER_INTERNAL_ICONS + "/icon.png")).getImage()); //$NON-NLS-1$
		this.timeSequenceList.setModel(this.enabledWizardsModel);
		this.egoAttributeTable.setModel(egosQuestionTablemodel);

		this.config = new Config();

		String name;

		AskActorNamesWizard w1 = new AskActorNamesWizard();
		name = "Indirect Name Generator";
		w1.setName(name);
		enabledWizardsModel.add(name, w1);
		AskEgoStartQuestionsWizard w2 = new AskEgoStartQuestionsWizard();
		name = "Ego questions";
		w2.setName(name);
		enabledWizardsModel.add(name, w2);
		AskActorImportanceWizard w4 = new AskActorImportanceWizard();
		name = "Ask actor importance";
		w4.setName(name);
		enabledWizardsModel.add(name, w4);
		AskAlterStartQuestionsWizard w3 = new AskAlterStartQuestionsWizard();
		name = "Alteri questions (start)";
		w3.setName(name);
		enabledWizardsModel.add(name, w3);
		AskRelationsWizard w7 = new AskRelationsWizard();
		name = "Ask relations";
		w7.setName(name);
		enabledWizardsModel.add(name, w7);
		NetworkWizard w8 = new NetworkWizard();
		name = "Draw network";
		w8.setName(name);
		enabledWizardsModel.add(name, w8);
		AskAlterEndQuestionsWizard w5 = new AskAlterEndQuestionsWizard();
		name = "Alteri questions (end)";
		w5.setName(name);
		enabledWizardsModel.add(name, w5);
		AskEgoEndQuestionsWizard w9 = new AskEgoEndQuestionsWizard();
		name = "Ego questions (end)";
		w9.setName(name);
		enabledWizardsModel.add(name, w9);
		SaveProjectWizard w6 = new SaveProjectWizard();
		name = "Save and exit";
		w6.setName(name);
		enabledWizardsModel.add(name, w6);

		editEgoAttrButton.setEnabled(false);
		deleteEgoAttrButton.setEnabled(false);
		maxNumberAlteriSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));

		egoAttributeTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						alreadySaved = false;
						final boolean enabled = (egoAttributeTable.getSelectedRow() >= 0);
						editEgoAttrButton.setEnabled(enabled);
						deleteEgoAttrButton.setEnabled(enabled);
					}
				});

		// new ego attr. button in ego tab.
		newEgoAttrButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				VariableDefinitionDialogImpl varDef = new VariableDefinitionDialogImpl(
						InterviewConfigurationFrameImpl.this, true);
				varDef.setVisible(true);

				if (varDef.getModel() != null)
					egosQuestionTablemodel.addQuestion(varDef.getModel());
			}
		});

		// Edit Button in Ego tab
		editEgoAttrButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				VariableDefinitionDialogImpl varDef = new VariableDefinitionDialogImpl(
						InterviewConfigurationFrameImpl.this, true);
				varDef.setModel(egosQuestionTablemodel
						.getQuestionAt(egoAttributeTable.getSelectedRow()));
				varDef.setVisible(true);
			}
		});

		deleteEgoAttrButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				egosQuestionTablemodel.removeQuestion(egoAttributeTable
						.getSelectedRow());
			}
		});

		// Checkbox in Egotab to dis-/enable Egoconfiguration
		egoDisableBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				egoMoveableBox.setEnabled(!egoDisableBox.isSelected());
				egoResizableBox.setEnabled(!egoDisableBox.isSelected());
				newEgoAttrButton.setEnabled(!egoDisableBox.isSelected());
			}
		});

		newTextBoxButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				TextBoxDialogImpl dlg = new TextBoxDialogImpl(
						InterviewConfigurationFrameImpl.this, true);
				dlg.setVisible(true);
				if (!dlg.isCancelled())
				{
					alreadySaved = false;
					// Text label is "text message: <first 16 chars of text
					// message
					// (if avail)>"
					final String name = createNameForTextBox(dlg.getWizardText());
					final TextWizard tWiz = new TextWizard();
					tWiz.setText(dlg.getWizardText());
					tWiz.setName(name);
					enabledWizardsModel.add(name, tWiz);
				}
			}
		});

		this.editTextBoxButton.setEnabled(false);
		this.deleteTextBoxButton.setEnabled(false);

		this.timeSequenceList
				.addListSelectionListener(new ListSelectionListener()
				{

					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						final boolean enabled = !timeSequenceList.isSelectionEmpty();
						upButton.setEnabled(enabled
								&& timeSequenceList.getSelectedIndex() > 0);
						downButton.setEnabled(enabled
								&& timeSequenceList.getSelectedIndex() < enabledWizardsModel
										.getSize() - 1);

						boolean isWizardSelected = enabled
								&& enabledWizardsModel
										.getWizardByName((String) enabledWizardsModel
												.getElementAt(timeSequenceList
														.getSelectedIndex())) instanceof TextWizard;
						editTextBoxButton.setEnabled(isWizardSelected);
						deleteTextBoxButton.setEnabled(isWizardSelected);
					}
				});

		editTextBoxButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				/* Use copy of index -> thread-safe! */
				final int sel = timeSequenceList.getSelectedIndex();
				if (enabledWizardsModel
						.getWizardByName((String) enabledWizardsModel
								.getElementAt(sel)) instanceof TextWizard)
				{
					TextWizard wiz = (TextWizard) enabledWizardsModel
							.getWizardByName((String) enabledWizardsModel
									.getElementAt(sel));
					TextBoxDialogImpl dlg = new TextBoxDialogImpl(
							InterviewConfigurationFrameImpl.this, true, wiz.getText());
					dlg.setVisible(true);
					if (!dlg.isCancelled())
					{
						alreadySaved = false;
						final String name = createNameForTextBox(dlg.getWizardText());
						wiz.setText(dlg.getWizardText());
						enabledWizardsModel.set(sel, name, wiz);
					}
				}

			}
		});

		deleteTextBoxButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				/* Use copy of index -> thread-safe! */
				final int sel = timeSequenceList.getSelectedIndex();
				if (enabledWizardsModel
						.getWizardByName((String) enabledWizardsModel
								.getElementAt(sel)) instanceof TextWizard)
					enabledWizardsModel.remove(sel);
			}
		});

		this.downButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				enabledWizardsModel.moveDown(timeSequenceList.getSelectedIndex());
				timeSequenceList.setSelectedIndex(timeSequenceList
						.getSelectedIndex() + 1);
			}
		});

		this.upButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				enabledWizardsModel.moveUp(timeSequenceList.getSelectedIndex());
				timeSequenceList.setSelectedIndex(timeSequenceList
						.getSelectedIndex() - 1);
			}
		});

		nonRelationalModel = new AttributeQuestionTable(false);
		nonRelationalTable.setModel(nonRelationalModel);
		nonRelationalTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener()
				{
					@Override
					public void valueChanged(ListSelectionEvent e)
					{
						alreadySaved = false;
						final boolean enabled = nonRelationalTable.getSelectedRow() >= 0;
						editNonRelationalButton.setEnabled(enabled);
						deleteNonRelationalButton.setEnabled(enabled);
					}
				});
		newNonRelationalButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				VariableDefinitionDialogImpl varDef = new VariableDefinitionDialogImpl(
						InterviewConfigurationFrameImpl.this, false);
				varDef.setVisible(true);
				if (varDef.getModel() != null)
				{
					alreadySaved = false;
					nonRelationalModel.addQuestion(varDef.getModel());
				}
			}
		});
		editNonRelationalButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				VariableDefinitionDialogImpl varDef = new VariableDefinitionDialogImpl(
						InterviewConfigurationFrameImpl.this, false);
				varDef.setModel(nonRelationalModel.getQuestionAt(nonRelationalTable
						.getSelectedRow()));
				varDef.setVisible(true);
				if (varDef.getModel() != null)
				{
					alreadySaved = false;
					nonRelationalModel.fireTableDataChanged();
				}
			}
		});
		deleteNonRelationalButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				alreadySaved = false;
				nonRelationalModel.removeQuestion(nonRelationalTable
						.getSelectedRow());
			}
		});

		startInterviewButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (alreadySaved
						|| JOptionPane
								.showConfirmDialog(
										InterviewConfigurationFrameImpl.this,
										"Start interview without saving interview configuration?",
										"Start without saving?",
										JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
				{
					updateConfig();
					VennMaker.getInstance().setProjekt(new Projekt());
					VennMaker.getInstance().setConfig(config);

					VennMaker.getInstance().setInterviewMode(true);
					dispose();
					VennMaker.showMainWindow();
				}
			}
		});

		exitButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (alreadySaved
						|| JOptionPane
								.showConfirmDialog(
										InterviewConfigurationFrameImpl.this,
										"Discard current changes in interview configuration?",
										Messages
												.getString("VennMaker.ConfirmDiscardTitel"), JOptionPane.YES_NO_OPTION) //$NON-NLS-1$
						== JOptionPane.OK_OPTION)
				{
					StartChooser sc = new StartChooser();
					dispose();
					sc.setVisible(true);
					if (sc.isClosedWithoutDecision())
						System.exit(0);
					if (!sc.isConfiguring())
						VennMaker.showMainWindow();
				}
			}
		});

		saveButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				save();
			}
		});

		loadButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				OpenFileDialog chooser = new OpenFileDialog(VMPaths.getCurrentWorkingDirectory());
				chooser.setFilter(Messages
						.getString("VennMaker.ConfigSuffix").toLowerCase()); //$NON-NLS-1$

				chooser.show();

				if (chooser.getFilename() != null)
				{
					String fileName = chooser.getFilename();
					try
					{
						alreadySaved = false;
						Config config = Config.load(fileName);
						VennMaker.getInstance().setConfig(config);
						VennMaker.getInstance().setCurrentWorkingDirectory(
								chooser.getRootFolder());

						egosQuestionTablemodel.clear();
						nonRelationalModel.clear();
						enabledWizardsModel.clear();
						for (VennMakerWizard wizard : config.getWizards())
						{
							enabledWizardsModel.add(wizard.getName(), wizard);
							if (wizard instanceof AskActorNamesWizard)
							{
								indirectQuestionField
										.setText(((AskActorNamesWizard) wizard).getText());
								maxNumberAlteriSpinner
										.setValue(((AskActorNamesWizard) wizard)
												.getMaxActors());
							}
						}

						for (Question q : config.getQuestions())
						{
							if (q.getSubject() == Question.Subject.EGO)
								egosQuestionTablemodel.addQuestion(q);
							else if (q.getSubject() == Question.Subject.ALTER
									|| q.getSubject() == Question.Subject.RELATION)
								nonRelationalModel.addQuestion(q);
						}
						egoDisableBox.setSelected(config.isEgoDisabled());
						egoMoveableBox.setSelected(config.isEgoMoveable());
						egoResizableBox.setSelected(config.isEgoResizable());
						usedVisualMapping.clear();
					} catch (FileNotFoundException exn)
					{
						JOptionPane.showMessageDialog(null,
								"File not found: " + exn.getLocalizedMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
					} catch (IOException exn)
					{
						JOptionPane.showMessageDialog(
								null,
								"Error!",
								"IO-Error while opening file: "
										+ exn.getLocalizedMessage(),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}

	/**
	 * Erzeugt die Konfiguration und bietet an, diese zu speichern.
	 */
	private void save()
	{
		alreadySaved = true;
		SaveFileDialog chooser = new SaveFileDialog(VMPaths.getCurrentWorkingDirectory());
		chooser.setFilter(Messages.getString("VennMaker.ConfigSuffix")); //$NON-NLS-1$
		// String filename = chooser.showSaveFileDialog();
		String filename = chooser.showOldSaveDialog("Save");
		VennMaker.getInstance().setCurrentWorkingDirectory(
				chooser.getRootFolder());
		if (actorsToChangePath.size() > 0)
		{
			for (AkteurTyp akt : actorsToChangePath)
			{
				String file = akt.getImageFile();
				String[] fileparts = file.split("/");
				String svgFile = fileparts[fileparts.length - 1];
				String newImageFile = new String(VMPaths.VENNMAKER_SYMBOLS + svgFile);
				if (!file.equals(newImageFile))
				{
					akt.setImageFile(newImageFile);
				}
			}
		}
		// Pfad zu den Images im neuen Verzeichnis
		if (filename == null)
			return;
		if (!filename.toLowerCase().endsWith(
				Messages.getString("VennMaker.ConfigSuffix").toLowerCase())) //$NON-NLS-1$
			filename += Messages.getString("VennMaker.ConfigSuffix"); //$NON-NLS-1$
		File file = new File(filename);
		if (!file.exists()
				|| JOptionPane
						.showConfirmDialog(
								null,
								Messages.getString("VennMaker.ConfirmOverwrite") + file.getAbsolutePath() //$NON-NLS-1$
										+ Messages
												.getString("VennMaker.ConfirmOverwriteEnd"), Messages.getString("VennMaker.ConfirmOverwriteTitel"), //$NON-NLS-1$ //$NON-NLS-2$
								JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
		{
			updateConfig();
			if (config.save(file.getAbsolutePath()) == false)
				JOptionPane
						.showMessageDialog(
								null,
								Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
										+ chooser.getFilename(),
								Messages.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
		}
	}

	private void updateConfig()
	{
		fillQuestionController();
		fillWizardController();
		config.setEgoDisabled(egoDisableBox.isSelected());
		config.setEgoMoveable(egoMoveableBox.isSelected());
		config.setEgoResizable(egoResizableBox.isSelected());
	}

	/**
	 * Copies all wizards in the requested order to vennmaker's wizard
	 * controller.
	 */
	private void fillWizardController()
	{
		for (VennMakerWizard wizard : enabledWizardsModel.toVector())
		{
			if (wizard instanceof AskActorNamesWizard)
			{
				// Konfiguriere den indirekten Namensgenerator
				((AskActorNamesWizard) wizard).setText(indirectQuestionField
						.getText());
				((AskActorNamesWizard) wizard)
						.setMaxActors((Integer) maxNumberAlteriSpinner.getValue());
			}

		}
		config.setWizards(enabledWizardsModel.toVector());
	}

	/**
	 * Copies all question objects from the configuration dialog into vennmaker's
	 * question controller.
	 */
	private void fillQuestionController()
	{
		Vector<Question> questions = new Vector<Question>();
		for (Question q : egosQuestionTablemodel)
			questions.add(q);
		for (Question q : nonRelationalModel)
			questions.add(q);
		config.setQuestions(questions);
	}

	private AttributeQuestionTable	egosQuestionTablemodel	= new AttributeQuestionTable(
																						true);

	private AttributeQuestionTable	nonRelationalModel;

	private Config							config;

	/**
	 * Creates a beautiful label for a text box. This label is used in list views
	 * to refer to a text box.
	 * 
	 * @param dlg
	 * @return
	 */
	private String createNameForTextBox(String text)
	{
		return "Text message: " + text.substring(0, Math.min(text.length(), 16));
	}

	public static void setUsedVisualMapping(String questionLabel,
			String visualMapping)
	{
		usedVisualMapping.put(visualMapping, questionLabel);
	}

	public static boolean isVisualMappingUsed(String questionLabel,
			String visualMapping)
	{
		String label = usedVisualMapping.get(visualMapping);

		if (label == null || label.equals(questionLabel))
		{
			return false;
		}
		else
		{
			return true;
		}

	}

	public static void clearUsedVisualMapping()
	{
		usedVisualMapping.clear();
	}

	public static void addActorToChangePath(AkteurTyp akt)
	{
		actorsToChangePath.add(akt);
	}
}
