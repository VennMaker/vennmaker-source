/**
 * 
 */
package wizards;

import gui.Messages;
import gui.VennMaker;

import java.awt.Color;

import data.EventProcessor;
import data.Netzwerk;
import data.Question;
import events.AddActorEvent;
import events.ComplexEvent;
import events.NewNetworkEvent;

/**
 * Dieser Wizard kann zunächst einmal neue Netzwerke anlegen. Genaueres wird
 * sich noch herausstellen.
 * 
 * 
 */
public class NetworkWizard extends VennMakerWizard
{
	private String	infoText	= ""; //$NON-NLS-1$

	private String	newNetwork;

	public NetworkWizard()
	{
		waitForNextClick = true;
	}

	@Override
	public void invoke()
	{
		VennMaker.getInstance().setNextVisible(true);
		Netzwerk netzwerk;
		if (this.newNetwork != null)
		{
			netzwerk = new Netzwerk();
			netzwerk.setBezeichnung(this.newNetwork);
			ComplexEvent event = new ComplexEvent(Messages
					.getString("VennMaker.New_Network_Wizard")); //$NON-NLS-1$
			event.addEvent(new NewNetworkEvent(netzwerk));
			if (VennMaker.getInstance().getProject().getEgo() != null)
				event.addEvent(new AddActorEvent(VennMaker.getInstance()
						.getProject().getEgo(), netzwerk, VennMaker.getInstance()
						.getProject().getEgo().getLocation(
								VennMaker.getInstance().getProject()
										.getCurrentNetzwerk())));
			EventProcessor.getInstance().fireEvent(event);
		}
		else
			netzwerk = VennMaker.getInstance().getProject().getCurrentNetzwerk();

		for (Question question : VennMaker.getInstance().getConfig()
				.getQuestions())
		{
			/*
			 * Die Sektoren und Kreise des neuen Netzwerks werden konfiguriert,
			 * wenn eine entsprechende Frage vorgegeben wurde. Wir haben erstmal
			 * für alle Netzwerke die gleiche Konfiguration.
			 */
			if (question.getVisualMappingMethod() == Question.VisualMappingMethod.SECTOR)
			{
				netzwerk.getHintergrund().setSectorLabel(question.getQuestion());
				netzwerk.getHintergrund().setNumSectors(
						question.getPredefinedAnswers().length);
				for (int i = 0; i < question.getVisualMapping().length; i++)
				{
					netzwerk.getHintergrund().getSector(i).label = question
							.getPredefinedAnswers()[i];
					netzwerk.getHintergrund().getSector(i).sectorColor = (Color) question
							.getVisualMapping()[i];
				}
			}
			else if (question.getVisualMappingMethod() == Question.VisualMappingMethod.CIRCLE)
			{
				netzwerk.getHintergrund().setCirclesLabel(question.getQuestion());
				netzwerk.getHintergrund().setNumCircles(
						question.getPredefinedAnswers().length);
				for (int i = 0; i < question.getVisualMapping().length; i++)
					netzwerk.getHintergrund().getCircles().add(i,
							question.getPredefinedAnswers()[i]);
			}
		}
	}

	@Override
	public void shutdown()
	{
		VennMaker.getInstance().setNextVisible(false);
	}

	/**
	 * Merkt ein Netzwerk vor, das beim Ausführen des Wizards erstellt wird.
	 * 
	 * @param bezeichnung
	 *           Bezeichnug des Netzwerks
	 */
	public void addNetwork(String bezeichnung)
	{
		this.newNetwork = bezeichnung;
	}

	/**
	 * Fragt den Namen des zu erstellenden Netzwerks ab, falls vorhanden.
	 * 
	 * @return Name des neuen Netzwerks
	 */
	public String getNewNetworkName()
	{
		return this.newNetwork;
	}

	/**
	 * @return the infoText
	 */
	public String getInfoText()
	{
		return infoText;
	}

	/**
	 * @param infoText
	 *           the infoText to set
	 */
	public void setInfoText(String infoText)
	{
		this.infoText = infoText;
	}
}
