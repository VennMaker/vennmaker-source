/**
 * 
 */
package data;

import files.FileOperations;
import files.ImageOperations;
import gui.VennMaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.jvnet.flamingo.common.JCommandButton;
import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.AttributeType.Scope;
import events.AddActorEvent;
import events.AddRelationEvent;
import events.VennMakerEvent;

/**
 * Der QuestionController sammelt Fragen über Ego, Alteri und Relationen und
 * stellt diese im Interview an den richtigen Stellen.
 * 
 * Das Hinzufügen von Akteuren und Relationen wird über Events abgefangen. Die
 * Fragen bei Interviewstart und -ende werden gezielt über Wizards ausgelöst,
 * um den Fragezeitpunkt frei wählen zu können.
 * 
 * 
 * 
 */
public class QuestionController
{
	/**
	 * Sollen debug-Ausgaben auf stdout ausgegeben werden?
	 */
	private static final boolean			debug	= false;

	/**
	 * Singleton: Referenz.
	 */
	private static QuestionController	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige QuestionController-Instanz in diesem Prozess.
	 */
	public static QuestionController getInstance()
	{
		if (instance == null)
		{
			instance = new QuestionController();
		}
		return instance;
	}

	/**
	 * Constructor: meldet den EventListener an.
	 */
	private QuestionController()
	{
		EventProcessor.getInstance().addEventPerformedListener(
				new EventListener());
	}

	/**
	 * Initialisiert den QuestionController mit der Liste konfigurierter Fragen.
	 * 
	 * @param questions
	 */
	public void init(Vector<Question> questions)
	{
		this.questions = new Vector<Question>(questions);

		for (Question question : questions)
		{
			/* RELATION_TYPE - Visualisierung wird spaeter eingestellt */
			if ((question.getVisualMappingMethod() != null)
					&& (question.getVisualMappingMethod()
							.equals(Question.VisualMappingMethod.RELATION_TYPE)))
				continue;

			AttributeType at = new AttributeType();

			at.setLabel(question.getLabel());
			at.setQuestion(question.getQuestion());
			at.setScope(Scope.PROJECT);
			if (!question.isFreeForm())
			{
				Vector<String> values = new Vector<String>();
				for (String value : question.getPredefinedAnswers())
					values.add(value);
				if (question.isOfferNoAnswer())
					values.add("No answer");
				if (question.isOfferUnknown())
					values.add("Unknown");
				at.setPredefinedValues(values.toArray());
			}
			VennMaker.getInstance().getProject().getAttributeTypes().add(at);

			question.setAttributeType(at);
		}
	}

	/**
	 * Lauscht auf alle Events, pickt sich die passenden raus und sucht ob es
	 * eine zu Zeit und Situation passende Frage gibt.
	 */
	class EventListener implements EventPerformedListener
	{
		@Override
		public void eventConsumed(VennMakerEvent event)
		{
			// Akteur wird hinzugefügt
			if (event instanceof AddActorEvent)
			{
				AddActorEvent ev = (AddActorEvent) event;
				for (Question question : questions)
				{
					if (question.getSubject() == Question.Subject.ALTER
							&& question.getTime() == Question.Time.ADDING
							&& ev.getAkteur() != ev.getNetzwerk().getEgo())
						ask(question, ev.getAkteur());
					else if (question.getSubject() == Question.Subject.ALTER
							&& question.getTime() == Question.Time.START
							&& ev.getAkteur().getAnswer(question.getQuestion()) == null
							&& ev.getAkteur() != ev.getNetzwerk().getEgo())
						// Frage soll eigentlich beim Interview-Start gestellt
						// werden, Akteur wurde aber vermutlich nachträglich
						// erstellt,
						// so dass die Frage jetzt drankommst.
						ask(question, ev.getAkteur());
				}
			}

			// Relation wird hinzugefügt
			if (event instanceof AddRelationEvent)
			{
				AddRelationEvent ev = (AddRelationEvent) event;
				for (Question question : questions)
					if (question.getSubject() == Question.Subject.RELATION
							&& question.getTime() == Question.Time.ADDING)
						ask(question, ev.getRelation());
			}
		}
	}

	/**
	 * Stellt die Fragen über Ego.
	 */
	public void askEgoStartQuestions()
	{
		for (Question question : questions)
			if (question.getSubject() == Question.Subject.EGO
					&& question.getTime() == Question.Time.START)
				if (question.getSubject() == Question.Subject.EGO)
					ask(question, VennMaker.getInstance().getProject().getEgo());
	}

	/**
	 * Stellt die Fragen über Ego.
	 */
	public void askEgoEndQuestions()
	{
		for (Question question : questions)
			if (question.getSubject() == Question.Subject.EGO
					&& question.getTime() == Question.Time.END)
				if (question.getSubject() == Question.Subject.EGO)
					ask(question, VennMaker.getInstance().getProject().getEgo());
	}

	/**
	 * Stellt die Fragen über Alteri, die beim Interview-Start gestellt werden
	 * sollen.
	 */
	public void askAlterStartQuestions()
	{
		for (Question question : questions)
			if (question.getSubject() == Question.Subject.ALTER
					&& question.getTime() == Question.Time.START)
			{
				if (question.isMatrix())
					askMatrix(question);
				else
					for (Akteur akteur : VennMaker.getInstance().getProject()
							.getAkteure())
						if (akteur != VennMaker.getInstance().getProject().getEgo())
							ask(question, akteur);
			}
	}

	/**
	 * Stellt die Fragen über Alteri, die beim Interview-Ende gestellt werden
	 * sollen.
	 */
	public void askAlterEndQuestions()
	{
		for (Question question : questions)
			if (question.getSubject() == Question.Subject.ALTER
					&& question.getTime() == Question.Time.END)
			{
				if (question.isMatrix())
					askMatrix(question);
				else
					for (Akteur akteur : VennMaker.getInstance().getProject()
							.getAkteure())
						if (akteur != VennMaker.getInstance().getProject().getEgo())
							ask(question, akteur);
			}
	}

	/**
	 * Liste der angemeldeten Fragen.
	 */
	private Vector<Question>	questions	= new Vector<Question>();

	private JRadioButton			freeFormRadioButton;

	/**
	 * Füge eine Frage hinzu.
	 * 
	 * @param question
	 *           Die Frage.
	 */
	public void addQuestion(Question question)
	{
		questions.add(question);
	}

	/**
	 * Gibt die Liste aller Fragen zurück.
	 * 
	 * @return Fragen
	 */
	public Vector<Question> getQuestions()
	{
		return this.questions;
	}

	/**
	 * Stellt eine Frage über das gegebene Subject und speichert das Ergebnis.
	 * 
	 * @param question
	 *           Die zu stellende Frage
	 * @param subject
	 *           Das Subject, zu dem gefragt wird
	 */
	private void ask(Question question, QuestionSubject subject)
	{
		if (debug)
			System.out.println("[QuestionController] Frage: " + question);

		if (question.isMatrix())
		{
			/**
			 * Die Matrix-Abfrage wird einmalig durch einen Wizard erledigt, die
			 * Frage wird hier deswegen nicht gestellt.
			 */
			return;
		}

		if (question.getVisualMappingMethod() == Question.VisualMappingMethod.RELATION_TYPE)
		{
			/**
			 * Die Abfrage der Relationstypen wird einmalig durch einen Wizard
			 * erledigt, die Frage wird hier deswegen nicht gestellt.
			 */
			return;
		}

		if (question.getVisualMappingMethod() == Question.VisualMappingMethod.SECTOR
				|| question.getVisualMappingMethod() == Question.VisualMappingMethod.CIRCLE)
		{
			/**
			 * Die Abfrage von Kreis und Sektor wird nicht als Frage gestellt,
			 * sondern implizit durch das Platzieren des Akteurs beantwortet.
			 */
			return;
		}

		final Vector<AbstractButton> answerButtons = new Vector<AbstractButton>();
		final JTextField textField = new JTextField();

		final JDialog dialog = new JDialog((JFrame) null, "VennMaker", true); //$NON-NLS-1$
		dialog.setIconImage(new ImageIcon("icons/intern/icon.png").getImage()); //$NON-NLS-1$
		dialog.setMinimumSize(new Dimension(600, 500));
		GridBagConstraints gbc;

		JPanel leftPanel = new JPanel();
		leftPanel.setMinimumSize(new Dimension(400, 500));
		GridBagLayout leftLayout = new GridBagLayout();
		leftPanel.setLayout(leftLayout);

		JPanel rightPanel = new JPanel();
		rightPanel.setMinimumSize(new Dimension(200, 500));
		rightPanel.setBackground(new Color(200, 200, 200));
		GridBagLayout rightLayout = new GridBagLayout();
		rightPanel.setLayout(rightLayout);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightPanel);
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(400);
		splitPane.setContinuousLayout(true);
		dialog.add(splitPane, BorderLayout.CENTER);

		/**
		 * VennMaker-Logo oben in rechter Fensterhälfte
		 */
		JLabel logoLabel;
		logoLabel = new JLabel();

		BufferedImage bufferedImg = ImageOperations.loadImage(new File(
				"icons/intern/VennMakerLogo-gross.png")); //$NON-NLS-1$
		Image img = bufferedImg.getScaledInstance(140, -1, Image.SCALE_SMOOTH);
		ImageIcon imgIcon = new ImageIcon(img);
		logoLabel.setIcon(imgIcon);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 10, 20, 10);
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightLayout.setConstraints(logoLabel, gbc);
		rightPanel.add(logoLabel);

		/**
		 * Dummy-Label im Sinne von \vfill, warum auch immer
		 */
		logoLabel = new JLabel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;
		rightLayout.setConstraints(logoLabel, gbc);
		rightPanel.add(logoLabel);

		ResizableIcon icon;

		/**
		 * Next-Button
		 */
		icon = null;
		try
		{
			icon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath("icons/intern/go-next.svg")), new Dimension(48, 48)); //$NON-NLS-1$
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		JCommandButton nextButton = new JCommandButton("Next", icon);
		final QuestionSubject s = subject;
		final Question q = question;
		nextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean ok = true;

				// prüfe Eingaben
				if (q.isFreeForm()
						&& q.getDataType() == Question.DataType.NUMERICAL
						&& freeFormRadioButton.isSelected())
				{
					int value = 0;
					try
					{
						value = Integer.parseInt(textField.getText());
					} catch (NumberFormatException exception)
					{
						ok = false;
						JOptionPane.showMessageDialog(null,
								"Please type in a numerical value.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

					if (ok)
					{
						if (value < q.getNumericalMinValue())
						{
							ok = false;
							JOptionPane.showMessageDialog(null,
									"Please type in a numerical value greater than or equal "
											+ q.getNumericalMinValue() + ".", //$NON-NLS-1$
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					if (ok)
					{
						if (value > q.getNumericalMaxValue())
						{
							ok = false;
							JOptionPane.showMessageDialog(null,
									"Please type in a numerical value lesser than or equal "
											+ q.getNumericalMaxValue() + ".", //$NON-NLS-1$
									"Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}

				if (ok)
				{
					String answer = ""; //$NON-NLS-1$
					for (AbstractButton button : answerButtons)
						if (button.isSelected())
						{
							if (!answer.equals("")) //$NON-NLS-1$
								answer += ";"; //$NON-NLS-1$
							answer += button.getActionCommand();
						}
					if (q.isFreeForm())
						if (!textField.getText().equals("")) { //$NON-NLS-1$
							if (!answer.equals("")) //$NON-NLS-1$
								answer += ";"; //$NON-NLS-1$
							answer += textField.getText();
						}
					if (debug)
						System.out.println("[QuestionController] Antwort: " + answer);

					s.saveAnswer(q.getQuestion(), answer);
					s.setAttributeValue(q.getAttributeType(), null, answer);

					// Visual Mapping
					// TODO
					// if (q.getVisualMappingMethod() ==
					// Question.VisualMappingMethod.ACTOR_TYPE)
					// {
					// AkteurTyp typ = null;
					// for (int j = 0; j < q.getPredefinedAnswers().length; j++)
					// {
					// if (q.getPredefinedAnswers()[j].equals(answer))
					// typ = (AkteurTyp) q.getVisualMapping()[j];
					// }
					// if (typ != null)
					// ((Akteur) s).setTyp(typ);
					// }
					// else
					if (q.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_SIZE)
					{
						Integer groesse = null;
						for (int j = 0; j < q.getPredefinedAnswers().length; j++)
						{
							if (q.getPredefinedAnswers()[j].equals(answer))
								groesse = (Integer) q.getVisualMapping()[j];
						}
						// TODO
						// if (groesse != null)
						// ((Akteur) s).setDefaultGroesse(groesse);
					}

					dialog.dispose();
				}
			}
		});
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(20, 10, 20, 10);
		((GridBagLayout) rightPanel.getLayout()).setConstraints(nextButton, gbc);
		rightPanel.add(nextButton);

		String questionText;
		if (question.getSubject() == Question.Subject.ALTER)
		{
			// ggf. Alter-Namen in der Frage einbauen oder davorhängen
			if (question.getQuestion().contains("$name")) //$NON-NLS-1$
				questionText = question.getQuestion().replace("$name", //$NON-NLS-1$
						((Akteur) subject).getName());
			else
				questionText = ((Akteur) subject).getName() + ": " //$NON-NLS-1$
						+ question.getQuestion();
		}
		else
			questionText = question.getQuestion();
		JTextArea textArea = new JTextArea(questionText, 3, 20);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setCursor(null);
		textArea.setOpaque(false);
		textArea.setFocusable(false);
		textArea.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		scrollPane.setBorder(null);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 3;
		gbc.weightx = 1;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		int zeile = 3;
		final ButtonGroup group = new ButtonGroup();

		if (question.getPredefinedAnswers() != null)
		{
			for (String text : question.getPredefinedAnswers())
			{
				AbstractButton radioButton;
				if (question.isAllowMultipleSelection())
					radioButton = new JCheckBox(text);
				else
				{
					radioButton = new JRadioButton(text);
					group.add(radioButton);
				}
				radioButton.setActionCommand(text);
				gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridx = 0;
				gbc.gridy = ++zeile;
				gbc.gridwidth = 2;
				gbc.insets = new Insets(0, 0, 0, 0);
				((GridBagLayout) leftPanel.getLayout()).setConstraints(radioButton,
						gbc);
				leftPanel.add(radioButton);
				answerButtons.add(radioButton);
			}
		}
		if (question.isFreeForm())
		{
			freeFormRadioButton = new JRadioButton();
			group.add(freeFormRadioButton);
			freeFormRadioButton.setActionCommand("free"); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = ++zeile;
			gbc.insets = new Insets(0, 0, 0, 0);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(
					freeFormRadioButton, gbc);
			leftPanel.add(freeFormRadioButton);
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.insets = new Insets(0, 0, 0, 0);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(textField, gbc);
			leftPanel.add(textField);
			textField.addFocusListener(new FocusListener()
			{
				@Override
				public void focusGained(FocusEvent e)
				{
					freeFormRadioButton.setSelected(true);
				}

				@Override
				public void focusLost(FocusEvent e)
				{
				}
			});
		}
		if (question.isOfferNoAnswer())
		{
			AbstractButton radioButton;
			if (question.isAllowMultipleSelection())
				radioButton = new JCheckBox();
			else
			{
				radioButton = new JRadioButton();
				group.add(radioButton);
			}
			radioButton.setActionCommand("no answer");
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = ++zeile;

			gbc.insets = new Insets(0, 0, 0, 0);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(radioButton,
					gbc);
			leftPanel.add(radioButton);
			answerButtons.add(radioButton);

			JLabel missingLabel = new JLabel("no answer"); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.insets = new Insets(0, 0, 0, 0);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(missingLabel,
					gbc);
			leftPanel.add(missingLabel);

		}
		if (question.isOfferUnknown())
		{
			AbstractButton radioButton;
			if (question.isAllowMultipleSelection())
				radioButton = new JCheckBox();
			else
			{
				radioButton = new JRadioButton();
				group.add(radioButton);
			}

			radioButton.setActionCommand("don't know"); //$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = ++zeile;
			gbc.insets = new Insets(0, 0, 0, 0);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(radioButton,
					gbc);
			leftPanel.add(radioButton);
			answerButtons.add(radioButton);

			JLabel missingLabel2 = new JLabel("don't know");//$NON-NLS-1$
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			gbc.gridy = zeile;
			gbc.insets = new Insets(0, 0, 0, 0);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(missingLabel2,
					gbc);
			leftPanel.add(missingLabel2);

		}

		dialog.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - dialog.getHeight()) / 2;
		int left = (screenSize.width - dialog.getWidth()) / 2;
		dialog.setLocation(left, top);

		dialog.setVisible(true);
	}

	/**
	 * Stellt eine Frage, die in einer Matrix für alle Akteure gleichzeitig
	 * beantwortet wird.
	 * 
	 * @param question
	 *           Die zu stellende Frage
	 */
	private void askMatrix(Question question)
	{
		if (debug)
			System.out.println("[QuestionController] Matrix-Frage: " + question);

		assert (question.isMatrix());

		if (question.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_SIZE)
		{
			/**
			 * Die Frage nach Akteursgröße wird durch eien Drag&Drop-Einteilung
			 * in Buckets realisiert, die der AskActorImprtanceWizard bereitstellt.
			 * Solche Fragen werden deswegen hier ignoriert.
			 */
			return;
		}

		final Map<QuestionSubject, Vector<AbstractButton>> allAnswerButtons = new HashMap<QuestionSubject, Vector<AbstractButton>>();

		final JDialog dialog = new JDialog((JFrame) null, "VennMaker", true); //$NON-NLS-1$
		dialog.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$
		dialog.setMinimumSize(new Dimension(600, 500));
		GridBagConstraints gbc;

		JPanel leftPanel = new JPanel();
		leftPanel.setMinimumSize(new Dimension(400, 500));
		GridBagLayout leftLayout = new GridBagLayout();
		leftPanel.setLayout(leftLayout);

		JPanel rightPanel = new JPanel();
		rightPanel.setMinimumSize(new Dimension(200, 500));
		rightPanel.setBackground(new Color(200, 200, 200));
		GridBagLayout rightLayout = new GridBagLayout();
		rightPanel.setLayout(rightLayout);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightPanel);
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(400);
		splitPane.setContinuousLayout(true);
		dialog.add(splitPane, BorderLayout.CENTER);

		/**
		 * VennMaker-Logo oben in rechter Fensterhälfte
		 */
		JLabel logoLabel;
		logoLabel = new JLabel();

		BufferedImage bufferedImg = ImageOperations.loadImage(new File(
				"icons/intern/VennMakerLogo-gross.png")); //$NON-NLS-1$
		Image img = bufferedImg.getScaledInstance(140, -1, Image.SCALE_SMOOTH);
		ImageIcon imgIcon = new ImageIcon(img);
		logoLabel.setIcon(imgIcon);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 10, 20, 10);
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightLayout.setConstraints(logoLabel, gbc);
		rightPanel.add(logoLabel);

		/**
		 * Dummy-Label im Sinne von \vfill, warum auch immer
		 */
		logoLabel = new JLabel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;
		rightLayout.setConstraints(logoLabel, gbc);
		rightPanel.add(logoLabel);

		ResizableIcon icon;

		/**
		 * Next-Button
		 */
		icon = null;
		try
		{
			icon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath("icons/intern/go-next.svg")), new Dimension(48, 48)); //$NON-NLS-1$
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		JCommandButton nextButton = new JCommandButton("Next", icon);

		final Question q = question;
		nextButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (QuestionSubject s : VennMaker.getInstance().getProject()
						.getAkteure())
					if (s != VennMaker.getInstance().getProject().getEgo())
					{
						String answer = ""; //$NON-NLS-1$
						Vector<AbstractButton> answerButtons = allAnswerButtons
								.get(s);
						for (AbstractButton button : answerButtons)
							if (button.isSelected())
							{
								if (!answer.equals("")) //$NON-NLS-1$
									answer += ";"; //$NON-NLS-1$
								answer += button.getActionCommand();
							}
						if (debug)
							System.out.println("[QuestionController] Antwort: "
									+ answer);

						s.setAttributeValue(q.getAttributeType(), null, answer);
						s.saveAnswer(q.getQuestion(), answer);

						// Visual Mapping
						if (q.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_TYPE)
						{
							AkteurTyp typ = null;
							for (int j = 0; j < q.getPredefinedAnswers().length; j++)
							{
								if (q.getPredefinedAnswers()[j].equals(answer))
									typ = (AkteurTyp) q.getVisualMapping()[j];
							}
							// TODO
							// if (typ != null)
							// ((Akteur) s).setTyp(typ);
						}
					}

				dialog.dispose();
			}
		});

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(20, 10, 20, 10);
		((GridBagLayout) rightPanel.getLayout()).setConstraints(nextButton, gbc);
		rightPanel.add(nextButton);

		String questionText;
		questionText = question.getQuestion();
		JTextArea textArea = new JTextArea(questionText, 3, 40);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setCursor(null);
		textArea.setOpaque(false);
		textArea.setFocusable(false);
		textArea.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		scrollPane.setBorder(null);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 20;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		int zeile = 1;

		// Spaltenbeschriftungen in erster Zeile
		int spalte = 1;
		if (question.getPredefinedAnswers() != null)
		{
			for (String text : question.getPredefinedAnswers())
			{
				JTextArea questionTextArea = new JTextArea(text);
				JScrollPane questionScrollPane = new JScrollPane(questionTextArea);
				questionTextArea.setLineWrap(true);
				questionTextArea.setWrapStyleWord(true);
				questionTextArea.setEditable(false);
				questionTextArea.setCursor(null);
				questionTextArea.setOpaque(false);
				questionTextArea.setFocusable(false);
				questionTextArea.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
				questionScrollPane.setBorder(null);
				questionScrollPane.setMinimumSize(new Dimension(80, 50));
				gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridx = ++spalte;
				gbc.gridy = zeile;
				gbc.gridwidth = 1;
				gbc.insets = new Insets(5, 5, 5, 5);
				gbc.anchor = GridBagConstraints.CENTER;
				((GridBagLayout) leftPanel.getLayout()).setConstraints(
						questionScrollPane, gbc);
				leftPanel.add(questionScrollPane);
			}
		}
		if (question.isOfferNoAnswer())
		{
			JLabel questionLabel = new JLabel("no answer");
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = ++spalte;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.anchor = GridBagConstraints.CENTER;
			((GridBagLayout) leftPanel.getLayout()).setConstraints(questionLabel,
					gbc);
			leftPanel.add(questionLabel);
		}
		if (question.isOfferUnknown())
		{
			JLabel questionLabel = new JLabel("don't know");
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = ++spalte;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.anchor = GridBagConstraints.CENTER;
			((GridBagLayout) leftPanel.getLayout()).setConstraints(questionLabel,
					gbc);
			leftPanel.add(questionLabel);
		}

		for (Akteur akteur : VennMaker.getInstance().getProject().getAkteure())
			if (akteur != VennMaker.getInstance().getProject().getEgo())
			{
				Vector<AbstractButton> answerButtons = new Vector<AbstractButton>();
				allAnswerButtons.put(akteur, answerButtons);

				JLabel label = new JLabel(akteur.getName());
				gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridx = 0;
				gbc.gridy = ++zeile;
				gbc.gridwidth = 1;
				gbc.insets = new Insets(0, 0, 0, 0);
				gbc.anchor = GridBagConstraints.CENTER;
				((GridBagLayout) leftPanel.getLayout()).setConstraints(label, gbc);
				leftPanel.add(label);

				final ButtonGroup group = new ButtonGroup();

				spalte = 1;
				if (question.getPredefinedAnswers() != null)
				{
					for (String text : question.getPredefinedAnswers())
					{
						AbstractButton radioButton;
						if (question.isAllowMultipleSelection())
							radioButton = new JCheckBox();
						else
						{
							radioButton = new JRadioButton();
							group.add(radioButton);
						}
						radioButton.setToolTipText(text);
						radioButton.setActionCommand(text);
						gbc = new GridBagConstraints();
						gbc.fill = GridBagConstraints.BOTH;
						gbc.gridx = ++spalte;
						gbc.gridy = zeile;
						gbc.gridwidth = 1;
						gbc.insets = new Insets(0, 0, 0, 0);
						gbc.anchor = GridBagConstraints.CENTER;
						((GridBagLayout) leftPanel.getLayout()).setConstraints(
								radioButton, gbc);
						leftPanel.add(radioButton);
						answerButtons.add(radioButton);
					}
				}
				if (question.isOfferNoAnswer())
				{
					AbstractButton radioButton;
					if (question.isAllowMultipleSelection())
						radioButton = new JCheckBox();
					else
					{
						radioButton = new JRadioButton();
						group.add(radioButton);
					}
					radioButton.setToolTipText("no answer");
					radioButton.setActionCommand("no answer"); //$NON-NLS-1$
					gbc = new GridBagConstraints();
					gbc.fill = GridBagConstraints.BOTH;
					gbc.gridx = ++spalte;
					gbc.gridy = zeile;
					gbc.gridwidth = 1;
					gbc.insets = new Insets(0, 0, 0, 0);
					gbc.anchor = GridBagConstraints.CENTER;
					((GridBagLayout) leftPanel.getLayout()).setConstraints(
							radioButton, gbc);
					leftPanel.add(radioButton);
					answerButtons.add(radioButton);
				}
				if (question.isOfferUnknown())
				{
					AbstractButton radioButton;
					if (question.isAllowMultipleSelection())
						radioButton = new JCheckBox();
					else
					{
						radioButton = new JRadioButton();
						group.add(radioButton);
					}
					radioButton.setToolTipText("don't know");
					radioButton.setActionCommand("don't know"); //$NON-NLS-1$
					gbc = new GridBagConstraints();
					gbc.fill = GridBagConstraints.BOTH;
					gbc.gridx = ++spalte;
					gbc.gridy = zeile;
					gbc.gridwidth = 1;
					gbc.insets = new Insets(0, 0, 0, 0);
					gbc.anchor = GridBagConstraints.CENTER;
					((GridBagLayout) leftPanel.getLayout()).setConstraints(
							radioButton, gbc);
					leftPanel.add(radioButton);
					answerButtons.add(radioButton);
				}
			}

		dialog.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - dialog.getHeight()) / 2;
		int left = (screenSize.width - dialog.getWidth()) / 2;
		dialog.setLocation(left, top);

		dialog.setVisible(true);
	}
}
