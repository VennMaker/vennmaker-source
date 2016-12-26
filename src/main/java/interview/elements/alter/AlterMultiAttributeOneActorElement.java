package interview.elements.alter;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import interview.InterviewElementInformation;
import interview.UpdateListener;
import interview.categories.IECategory_NameInterpretator;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.configuration.attributesSelection.MultiAttributePanel;
import interview.configuration.filterSelection.AlteriFilterPanel;
import interview.elements.StandardElement;
import interview.elements.information.MultiSelectionElementInformation;
import interview.panels.multi.MixedAnswerPanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Akteur;
import data.AttributeType;

/**
 * Dialog to set many attributes (free & categorical) for one actor. (one actor,
 * multiple attributes with categorical or free values)
 * 
 * dialog 7 in the following document:
 * https://vennmaker.uni-trier.de/trac/projects
 * /vennmaker/attachment/wiki/Interviewmodus/fragebogenkonfiguration_2.pdf
 * 
 * 
 * 
 */
public class AlterMultiAttributeOneActorElement extends StandardElement
		implements UpdateListener, IECategory_NameInterpretator
{
	private static final long		serialVersionUID	= 1L;

	private Icon						icon;

	private List<AttributeType>	attributes;

	private Map<String, Integer>	attributeOrder;

	public AlterMultiAttributeOneActorElement()
	{
		super(new NameInfoPanel(false), new AlteriFilterPanel(),
				new MultiAttributePanel(), true);

		attributes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");

		aSelector.setParent(this);
		aSelector.init(attributes);
		aSelector.addUpdateListener(this);
		// my own panel
		specialPanel = new MixedAnswerPanel();

		// set instruction text
		this.instructionText = Messages
				.getString("AlterMultiAttributeOneActorElement.Description"); //$NON-NLS-1$

		icon = new ImageIcon("icons/intern/icon.png");

	}

	@Override
	public boolean addToTree()
	{
		// check if at least one attribute is selected
		Map<String, AttributeType> questions = aSelector
				.getAttributesAndQuestions();
		if (questions == null || questions.size() <= 0)
		{
			JOptionPane
					.showMessageDialog(
							ConfigDialog.getInstance(),
							Messages
									.getString("InterviewElement.AtLeastOneAttribute.Description"), //$NON-NLS-1$
							Messages
									.getString("InterviewElement.AtLeastOneAttribute.Title"), //$NON-NLS-1$
							JOptionPane.INFORMATION_MESSAGE, icon);
			return false;
		}

		Map<String, AttributeType> attsAndQuestions = aSelector
				.getAttributesAndQuestions();

		if (attributeOrder == null)
			attributeOrder = new HashMap<String, Integer>();

		for (String question : attsAndQuestions.keySet())
			attributeOrder
					.put(attsAndQuestions.get(question).toString(), Integer
							.parseInt(question.substring(0, question
									.indexOf(MultiAttributePanel.QUESTION_SEPERATOR))));

		return super.addToTree();
	}

	@Override
	public JPanel getConfigurationDialog()
	{
		// first update Attributes

		List<AttributeType> projectAttributes = VennMaker.getInstance()
				.getProject().getAttributeTypes("ACTOR");

		if (attributes.size() > projectAttributes.size())
		{
			for (int i = 0; i < attributes.size(); i++)
				if (!projectAttributes.contains(attributes.get(i)))
					attributes.remove(i);

		}
		else if (attributes.size() < projectAttributes.size())
		{
			for (AttributeType at : projectAttributes)
				if (!attributes.contains(at))
					attributes.add(at);
		}

		aSelector.updatePanel(attributes);

		return super.getConfigurationDialog();
	}

	public JPanel getControllerDialog()
	{
		setActors(fSelector.getFilteredActors());
		return super.getControllerDialog();
	}

	public void setActors(List<Akteur> actors)
	{
		this.actors.clear();

		for (Akteur actor : actors)
		{
			if (!this.actors.contains(actor))
				this.actors.add(actor);
		}
	}

	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof MultiSelectionElementInformation))
			return;

		this.attributeOrder = ((MultiSelectionElementInformation) information)
				.getAttributeTypeOrder();

		List<AttributeType> tmpAtts = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		if (attributeOrder != null)
		{
			for (AttributeType type : tmpAtts)
			{
				if (attributeOrder.get(type.toString()) == null)
					continue;

				attributes.remove(type);
				attributes.add(attributeOrder.get(type.toString()), type);
			}
		}

		super.setElementInfo(information);

		aSelector.updatePanel(attributes);
	}

	public InterviewElementInformation getElementInfo()
	{

		Integer[] filterIndex = null;
		String filter = null;

		if (fSelector != null && fSelector.getFilterIndex() != null)
		{
			filterIndex = new Integer[fSelector.getFilterIndex().size()];

			filterIndex = fSelector.getFilterIndex().toArray(filterIndex);
			filter = fSelector.getFilter();
		}

		MultiSelectionElementInformation elem = new MultiSelectionElementInformation(
				nSelector.getName(), filter, filterIndex,
				aSelector.getAttributesAndQuestions(), nSelector.getInfo(),
				attributeOrder);

		String name = nSelector.getName();
		if (name == null || name.equals(""))
			name = getElementNameInTree();
		elem.setElementName(name);
		elem.setId(this.getId());
		elem.setElementClass(this.getClass());
		elem.createChildInformation(children);

		if (parent != null)
			elem.setParentInformation(parent);

		return elem;
	}
}
