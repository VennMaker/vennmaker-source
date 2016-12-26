/**
 * 
 */
package wizards;

import data.QuestionController;

/**
 * Dieser Wizard beauftragt den QuestionController, Fragen übers Ego zu stellen.
 * 
 * 
 */
public class AskEgoStartQuestionsWizard extends VennMakerWizard
{
	@Override
	public void invoke()
	{
		QuestionController.getInstance().askEgoStartQuestions();
		WizardController.getInstance().proceed();
	}
}
