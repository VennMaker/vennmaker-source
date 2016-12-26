package interview.elements.ego;

import gui.Messages;
import gui.VennMaker;
import interview.categories.IECategory_Ego;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.configuration.attributesSelection.SingleAttributePanel;
import interview.configuration.filterSelection.EgoFilterPanel;
import interview.elements.StandardElement;
import interview.panels.multi.EnhancedFreeAnswerPanel;

import java.util.List;

import javax.swing.JPanel;

import data.AttributeType;

/**
 * Dialog to set a free attribute value by an enhanced text area on the ego
 * actor.
 * 
 * 
 * 
 */
public class EgoEnhancedFreeAnswerElement extends StandardElement implements
		IECategory_Ego
{
	private static final long	serialVersionUID	= 1L;

	public EgoEnhancedFreeAnswerElement()
	{
		super(new NameInfoPanel(false), new EgoFilterPanel(),
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
		aSelector.addUpdateListener(this);
		// my own panel
		specialPanel = new EnhancedFreeAnswerPanel();

		// set instruction text
		this.instructionText = Messages
				.getString("EgoEnhancedFreeAnswerElement.Description"); //$NON-NLS-1$
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
