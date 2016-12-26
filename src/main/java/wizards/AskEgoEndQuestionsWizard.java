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
public class AskEgoEndQuestionsWizard extends VennMakerWizard
{
	@Override
	public void invoke()
	{
		QuestionController.getInstance().askEgoEndQuestions();
		WizardController.getInstance().proceed();
	}
}
