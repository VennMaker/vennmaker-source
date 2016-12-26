package interview.elements.alter;

import gui.Messages;
import gui.VennMaker;
import interview.categories.IECategory_NameInterpretator;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.configuration.attributesSelection.SingleAttributePanel;
import interview.configuration.filterSelection.AlteriFilterPanel;
import interview.elements.StandardElement;
import interview.panels.multi.FreeAnswerPanel;

import java.util.List;

import javax.swing.JPanel;

import data.AttributeType;

/**
 * Dialog to set the value of a single attribute for each selected actor through
 * a textfield (free answer). (multiple actors, single attribute with free
 * answer possibility)
 * 
 * dialog 5 in the following document:
 * https://vennmaker.uni-trier.de/trac/projects
 * /vennmaker/attachment/wiki/Interviewmodus/fragebogenkonfiguration_2.pdf
 * 
 * 
 * 
 */
public class AlterSingleAttributeFreeAnswerElement extends StandardElement
		implements IECategory_NameInterpretator
{
	private static final long	serialVersionUID	= 1L;

	public AlterSingleAttributeFreeAnswerElement()
	{
		super(new NameInfoPanel(false), new AlteriFilterPanel(),
				new SingleAttributePanel(true), true);
		aSelector.setParent(this);
		// selection of attributes to show (only free answer attributes)
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");
		for (int i = aTypes.size() - 1; i >= 0; i--)
		{
			Object[] preVals = aTypes.get(i).getPredefinedValues();
			if (preVals != null && preVals.length > 0)
				aTypes.remove(i);
		}
		aSelector.init(aTypes);
		// my own panel
		specialPanel = new FreeAnswerPanel();

		// set instruction text
		this.instructionText = Messages
				.getString("AlterSingleAttributeFreeAnswerElement.Description"); //$NON-NLS-1$

	}

	@Override
	public JPanel getConfigurationDialog()
	{
		// first update Attributes
		List<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes("ACTOR");
		for (int i = aTypes.size() - 1; i >= 0; i--)
		{
			Object[] preVals = aTypes.get(i).getPredefinedValues();
			if (preVals != null && preVals.length > 0)
				aTypes.remove(i);
		}
		aSelector.updatePanel(aTypes);

		return super.getConfigurationDialog();
	}
}
