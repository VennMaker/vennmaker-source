package interview.elements.message;

import gui.Messages;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.elements.StandardElement;
import interview.elements.information.TextElementInformation;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Ein simpler Text-Dialog im Interview.
 * 
 */
public class TextElement extends StandardElement implements IECategory_MetaElement
{
	private static final long 	serialVersionUID = 1L;
	
	public TextElement()
	{
		super(	new NameInfoPanel(true)
		{
			public JPanel getConfigurationPanel()
			{
				taDescription.addMouseListener(new MouseAdapter()
				{
					public void mousePressed(MouseEvent e)
					{
						if (taDescription.getText().equals(
								Messages.getString("TextElement.TextOfTextField")))
							taDescription.setText("");
					}
				});

				return super.getConfigurationPanel();
			}
		},
				null,
				null, 
				true  );
		nSelector.setName("TextElement");
		nSelector.setInfo(Messages.getString("TextElement.TextOfTextField"));
		
	}

	@Override
	public boolean writeData()
	{
		return true;
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}
	
	@Override
	public boolean addToTree()
	{
		setElementNameInTree(nSelector.getName());

		return true;
	}
	
	@Override
	public String getInstructionText()
	{
		return Messages.getString("IndirectNameGenerator.EnterTextInFieldBelow");	//$NON-NLS-1$
	}

	@Override
	public String toString()
	{
		return getElementNameInTree() +" "+nSelector.getInfo();
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		TextElementInformation info = new TextElementInformation(
				nSelector.getName(), nSelector.getInfo());
		info.setId(this.getId());
		info.createChildInformation(children);

		if (parent != null)
		{
			info.setParent(parent.getClass());
			info.setParentId(parent.getId());
		}

		info.setElementClass(this.getClass());
		return info;
	}
	
	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof TextElementInformation))
			return;
		
		getConfigurationDialog();
		TextElementInformation info = (TextElementInformation) information;
		this.setElementNameInTree(info.getElementName());

		this.nSelector.setInfo(info.getMessage());
		this.nSelector.setName(info.getElementName());
		InterviewLayer.getInstance().createChild(information, this);
	}
}
