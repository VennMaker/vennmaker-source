/**
 * 
 */
package wizards;

import data.QuestionController;

/**
 * Dieser Wizard beauftragt den QuestionController, Fragen über Alteri zu
 * stellen.
 * 
 * 
 */
public class AskAlterEndQuestionsWizard extends VennMakerWizard
{
	@Override
	public void invoke()
	{
		QuestionController.getInstance().askAlterEndQuestions();
		WizardController.getInstance().proceed();
	}
}
