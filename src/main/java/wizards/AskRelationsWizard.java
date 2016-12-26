/**
 * 
 */
package wizards;

import gui.VennMaker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import data.Question;

/**
 * Dieser Wizard fordert zum Ziehen von Relationen auf.
 * 
 * 
 */
public class AskRelationsWizard extends VennMakerWizard
{
	/**
	 * Anzuzeigender Text.
	 */
	private String	text;

	public AskRelationsWizard()
	{
		waitForNextClick = true;
	}

	@Override
	public void invoke()
	{
		/*
		 * Gibt es eine Frage nach Relationen?
		 */
		boolean found = false;
		for (Question question : VennMaker.getInstance().getConfig()
				.getQuestions())
		{
			if (question.getVisualMappingMethod() == Question.VisualMappingMethod.RELATION_TYPE)
			{
				this.text = question.getQuestion();
				found = true;
			}
		}
		if (!found)
		{
			WizardController.getInstance().proceed();
			return;
		}

		/**
		 * Wir brauchen ein Anzeigefenster.
		 */
		prepareDialog();

		GridBagConstraints gbc;

		JTextArea textArea = new JTextArea(text, 10, 40);
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
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		dialog.pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - dialog.getHeight()) / 2;
		int left = (screenSize.width - dialog.getWidth()) / 2;
		dialog.setLocation(left, top);

		dialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wizards.VennMakerWizard#nextClicked()
	 */
	@Override
	public void nextClicked()
	{
		dialog.dispose();
		VennMaker.getInstance().setNextVisible(true);
	}

	@Override
	public void shutdown()
	{
		VennMaker.getInstance().setNextVisible(false);
	}
}
