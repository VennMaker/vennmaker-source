package interview.elements.relation;

import gui.VennMaker;
import interview.categories.IECategory_RelationInterpretator;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import data.Akteur;

/**
 * Objects of this class represent an relation generator for alteri only
 * <code>EGO</code> will be ignored
 * 
 * 
 * 
 */
public class RelationGeneratorAlteriPairElement extends
		RelationGeneratorPairElement implements IECategory_RelationInterpretator
{

	public RelationGeneratorAlteriPairElement()
	{
		super();
	}

	public JPanel getControllerDialog()
	{
		if (inPreviewMode)
		{
			setData();
			JPanel controllerPanel = super.getControllerDialog();
			List<Akteur> actors = new ArrayList<Akteur>();
			actors.add(fSelector.getFilteredActors().get(1));

			specialPanel.setActors(actors);
			specialPanel.rebuild();
			return controllerPanel;
		}
		else
		{
			return super.getControllerDialog();
		}
	}

	public void setData()
	{
		setIgnoreEgo(true);
		List<Akteur> actorsToSet = fSelector.getFilteredActors();
		actorsToSet.remove(VennMaker.getInstance().getProject().getEgo());
		setActors(actorsToSet);
	}
}
