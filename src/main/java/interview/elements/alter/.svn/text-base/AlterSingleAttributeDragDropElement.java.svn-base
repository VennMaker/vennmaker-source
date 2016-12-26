/**
 * 
 */
package interview.elements.alter;

import gui.Messages;
import gui.VennMaker;
import interview.categories.IECategory_NameInterpretator;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.configuration.attributesSelection.SingleAttributePanel;
import interview.configuration.filterSelection.AlteriFilterPanel;
import interview.elements.StandardElement;
import interview.panels.multi.DragDropAnswerPanel;

import javax.swing.JPanel;

/**
 * Dialog to set the value of a single attribute through drag & drop the actor into the list 
 * representing this value. (multiple actors, single attribute with catigorial values)
 * 
 * dialog 3 in the following document:
 * https://vennmaker.uni-trier.de/trac/projects/vennmaker/attachment/wiki/Interviewmodus/fragebogenkonfiguration_2.pdf
 *
 * 
 *
 */
public class AlterSingleAttributeDragDropElement extends StandardElement implements IECategory_NameInterpretator
{
	private static final long	serialVersionUID	= 1L;
	
	public AlterSingleAttributeDragDropElement()
	{		
		super(	new NameInfoPanel(false),
				new AlteriFilterPanel(),
				new SingleAttributePanel(false), 
				true  );
		System.out.println("AlterSingleAttributeDragDropElement...");
		
		aSelector.setParent(this);
		//selection of attributes to show (only categorical attributes)
		aSelector.init(VennMaker.getInstance().getProject().getAttributeTypesDiscrete("ACTOR"));
		//my own panel
		specialPanel = new DragDropAnswerPanel();
		
		//set instruction text
		this.instructionText = Messages.getString("AlterSingleAttributeDragDropElement.Description");		//$NON-NLS-1$
	}
	
	@Override
	public JPanel getConfigurationDialog()
	{
		//first update Attributes
		aSelector.updatePanel(VennMaker.getInstance().getProject().getAttributeTypesDiscrete("ACTOR"));
		
		return super.getConfigurationDialog();
	}
}


