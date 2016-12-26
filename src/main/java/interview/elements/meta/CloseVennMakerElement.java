package interview.elements.meta;

import gui.Messages;
import gui.VennMaker;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.elements.StandardElement;
import interview.elements.information.CloseVennMakerElementInformation;

import java.awt.image.BufferedImage;

/**
 * meta-element to shut down VennMaker
 * 
 * 
 */

public class CloseVennMakerElement extends StandardElement implements IECategory_MetaElement
{
	private static final long 	serialVersionUID = 1L;

	public CloseVennMakerElement()
	{
		super(	new NameInfoPanel(true),
				null,
				null,
				true  );
		nSelector.setName("CloseVennMakerElement");
		nSelector.setInfo(Messages.getString("CloseVennMakerElement.DefaultInformation"));
		this.isMetaElement = true;
	}

	@Override
	public boolean writeData() 
	{
		//only if "Next" was pressed
		if(InterviewController.getInstance().wasNextPressed())
		{
			VennMaker.exit();
		}
		return true;
	}


	@Override
	public boolean addToTree() 
	{	
		setElementNameInTree(nSelector.getName());
		return true;
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}
	
	@Override
	public String getInstructionText() 
	{
		return Messages.getString("CloseVennMakerElement.Description");		//$NON-NLS-1$
	}

	@Override
	public InterviewElementInformation getElementInfo() 
	{
		CloseVennMakerElementInformation info = new CloseVennMakerElementInformation(nSelector.getInfo());
		
		info.setId(this.getId());
		info.createChildInformation(children);
		info.setElementName(this.getElementNameInTree());
		
		if (parent != null)
		{
			info.setParentInformation(parent);
		}
		info.setElementClass(this.getClass());
		
		return info;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information) 
	{
		if(information instanceof CloseVennMakerElementInformation)
		{
			CloseVennMakerElementInformation info = (CloseVennMakerElementInformation) information;
			if(nSelector!=null)
			{
				nSelector.setInfo(info.getInfo());
				nSelector.setName(info.getElementName());
			}
			
			this.setId(info.getId());
			this.setElementNameInTree(info.getElementName());
			InterviewLayer.getInstance().createChild(information, this);
		}
	}
}
