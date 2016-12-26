package interview.elements.relation;

import gui.Messages;
import gui.VennMaker;
import interview.InterviewElementInformation;
import interview.categories.IECategory_RelationInterpretator;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.configuration.attributesSelection.SingleAttributePanel;
import interview.configuration.filterSelection.FilterPanel;
import interview.elements.InterviewElement;
import interview.elements.StandardElementDemandingActors;
import interview.elements.information.InputElementInformation;
import interview.panels.multi.RelationRadioAnswerPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;
import data.Relation;

/**
 * Objects of this class create a <code>Relation</code> beteween the given
 * actors based on the user's choice
 * 
 * 
 * 
 */
public class RelationGeneratorListElement extends
		StandardElementDemandingActors implements
		IECategory_RelationInterpretator
{
	/**
	 * 
	 */
	private static final long				serialVersionUID		= 6832247297377687109L;

	protected JComboBox<Netzwerk>			networkComboBox;

	/**
	 * Actors are saved in this map with their actors where they have no relation
	 * to
	 */
	protected Map<Akteur, List<Akteur>>	actorsWithNoRelation;

	/**
	 * The actors to ask the first time. This Map is only used if "previous" is
	 * clicked in the <code>InterviewController</code>
	 */
	protected Map<Akteur, List<Akteur>>	initalActorsToAsk;

	protected boolean							filterDirectedActors	= true;

	/**
	 * Create a new object of <code>RelationGeneratorElement</code>
	 */
	public RelationGeneratorListElement()
	{
		super(new NameInfoPanel(false), new FilterPanel(), null, true);
		actorsWithNoRelation = new HashMap<Akteur, List<Akteur>>();
		initalActorsToAsk = new HashMap<Akteur, List<Akteur>>();

		List<Akteur> actorsToSet = fSelector.getFilteredActors();
		Collections.sort(actorsToSet);

		setActors(actorsToSet);
		// TODO Auto-generated constructor stub
	}

	@Override
	public JPanel getControllerDialog()
	{
		specialPanel = new RelationRadioAnswerPanel(
				getCurrentActor(),
				networkComboBox.getItemAt(networkComboBox.getSelectedIndex()),
				Messages.getString("RelationGeneratorPairElement.RelationsCreated"), actorsWithNoRelation); //$NON-NLS-1$

		specialPanel.setAttributesAndQuestions(aSelector
				.getAttributesAndQuestions());

		List<Akteur> akteure = null;

		if (filterDirectedActors)
		{
			akteure = filterDirectedActors(fSelector.getFilteredActors());
			if (initalActorsToAsk.get(getCurrentActor()) == null)
				initalActorsToAsk.put(getCurrentActor(), akteure);
		}
		else
		{
			akteure = initalActorsToAsk.get(getCurrentActor());
		}

		// if (getActors().size() < 1)
		// {
		// if (InterviewController.getInstance().wasNextPressed())
		// {
		// InterviewController.getInstance().next();
		// }
		// else
		// {
		// InterviewController.getInstance().previous();
		// }
		// }
		
		for (Akteur actor : getActors()){
			if (getCurrentActor().equals(actor)){
				akteure.remove(actor);
			}

		}
		JPanel p = super.getControllerDialog();
		specialPanel.setActors(akteure);
		specialPanel.rebuild();

		return p;
	}

	@Override
	public boolean writeData()
	{
		return specialPanel.performChanges();
	}

	public void deinitPreview()
	{
		super.deinitPreview();
		initalActorsToAsk.clear();
	}

	@Override
	public void setData()
	{
		List<Akteur> actorsToSet = fSelector.getFilteredActors();
		Collections.sort(actorsToSet);
		setActors(actorsToSet);
	}

	@Override
	public JPanel getConfigurationDialog()
	{
		if (configurationPanel != null)
		{
			List<AttributeType> allRelationAttributes = new ArrayList<AttributeType>();

			for (String collector : VennMaker.getInstance().getProject()
					.getAttributeCollectors())
				allRelationAttributes.addAll(VennMaker.getInstance().getProject()
						.getAttributeTypes(collector));

			aSelector.updatePanel(allRelationAttributes);

			if (attributesAndQuestions != null)
				aSelector.setAttributesAndQuestions(attributesAndQuestions);

			return configurationPanel;
		}

		/* create the SingleAttributePanel with the option to create new groups */
		aSelector = new SingleAttributePanel(false, true);
		aSelector.setParent(this);

		JPanel selectNetworkPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 10);

		selectNetworkPanel.add(new JLabel(Messages
				.getString("RelationGeneratorListElement.ComboBox"))); //$NON-NLS-1$

		gbc.gridx = 1;
		gbc.gridwidth = 4;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(0, 15, 0, 10);

		networkComboBox = new JComboBox<Netzwerk>(VennMaker.getInstance()
				.getProject().getNetzwerke());

		selectNetworkPanel.add(networkComboBox, gbc);
		JPanel configPanel = super.getConfigurationDialog();
		configurationPanel = new JPanel(new GridBagLayout());
		configurationPanel.setMinimumSize(new Dimension(280, 250));

		GridBagConstraints panelConstraints = new GridBagConstraints();
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.insets = new Insets(20, 0, 0, 0);

		configurationPanel.add(selectNetworkPanel, panelConstraints);

		panelConstraints.gridy = 1;
		panelConstraints.weighty = 1;
		panelConstraints.weightx = 1;
		panelConstraints.fill = GridBagConstraints.BOTH;

		configurationPanel.add(configPanel, panelConstraints);

		List<AttributeType> allRelationAttributes = new ArrayList<AttributeType>();

		for (String collector : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
			allRelationAttributes.addAll(VennMaker.getInstance().getProject()
					.getAttributeTypes(collector));

		aSelector.updatePanel(allRelationAttributes);

		return configurationPanel;
	}
	
/**
 * Only use the filtered actors
 * @param actor list
 * @return new actor list
 */
	public List<Akteur> filterDirectedActors(List<Akteur> actors)
	{

		List<Akteur> temp = new ArrayList<Akteur>();

		for (Akteur a : actors)
			temp.add(a);

		actors = new ArrayList<Akteur>();

		for (Akteur a : temp)
		{
			if (a.equals(getCurrentActor())
					|| actorsWithNoRelation.get(getCurrentActor()) != null
					&& actorsWithNoRelation.get(getCurrentActor()).contains(a))
				continue;

			if (actorsWithNoRelation.get(a) != null
					&& actorsWithNoRelation.get(a).contains(getCurrentActor()))
				continue;

			Relation existing = a.getRelationTo(getCurrentActor(),
					(Netzwerk) networkComboBox.getSelectedItem(), aSelector
							.getAttributesAndQuestions().values().iterator().next());

			if (existing == null
					|| existing != null
					&& VennMaker.getInstance().getProject()
							.getIsDirected(existing.getAttributeCollectorValue()))
				actors.add(a);
		}

		return actors;
	}

	public List<Akteur> filterDirectedActors(List<Akteur> actors, Akteur owner)
	{
		List<Akteur> temp = new ArrayList<Akteur>();

		for (Akteur a : actors)
			temp.add(a);

		actors = new ArrayList<Akteur>();

		for (Akteur a : temp)
		{
			if (a.equals(owner))
				continue;

			Relation existing = a.getRelationTo(owner,
					(Netzwerk) networkComboBox.getSelectedItem());

			if (existing == null
					|| existing != null
					&& VennMaker.getInstance().getProject()
							.getIsDirected(existing.getAttributeCollectorValue()))
				actors.add(a);
		}

		return actors;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("RelationGeneratorListElement.Info"); //$NON-NLS-1$
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		InputElementInformation information = (InputElementInformation) super
				.getElementInfo();

		information.setNetwork((Netzwerk) networkComboBox.getSelectedItem());

		return information;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof InputElementInformation))
			return;

		super.setElementInfo(information);

		getConfigurationDialog();

		InputElementInformation info = (InputElementInformation) information;

		for (int i = 0; i < networkComboBox.getItemCount(); i++)
			if (networkComboBox.getItemAt(i).toString()
					.equals(info.getNetwork().toString()))
				networkComboBox.setSelectedIndex(i);

		aSelector.setAttributesAndQuestions(info.getAttributesAndQuestions());

	}

	public JComboBox<Netzwerk> getNetworkComboBox()
	{
		return networkComboBox;
	}

	public void setNetworkComboBox(JComboBox<Netzwerk> networkComboBox)
	{
		this.networkComboBox = networkComboBox;
	}

	public InterviewElement getPreviousElementInList()
	{
		this.filterDirectedActors = false;

		return super.getPreviousElementInList();
	}

	public InterviewElement getNextElementInList()
	{
		this.filterDirectedActors = true;

		return super.getNextElementInList();
	}

	public void resetInternalPointer()
	{
		this.actorsWithNoRelation.clear();
		super.resetInternalPointer();
	}
}
