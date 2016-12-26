package interview.elements.meta;

import gui.Messages;
import gui.VennMaker;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.elements.StandardElement;
import interview.elements.information.SwitchToNetworkInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import data.Netzwerk;


/**
 * Objects of this class allow to switch from the controller dialog to the 
 * VennMaker drawing mode.
 * 
 * 
 *
 */

public class SwitchToNetworkElement extends StandardElement implements IECategory_MetaElement
{
	private static final long serialVersionUID = 1L;
	
	private Netzwerk network;

	private JComboBox cbNetworks;
	
	private JPanel configPanel;
	
	private ActionListener listener;
	
	public SwitchToNetworkElement()
	{
		super(	null,
				null,
				null,
				false  );
		
		setElementNameInTree("SwitchElement");
		
		this.isMetaElement = true;
	}
	@Override
	public JPanel getControllerDialog()
	{
		return new JPanel();
	}
	
	@Override
	public boolean writeData()
	{
		VennMaker.getInstance().setNextVisible(false);
		VennMaker.getInstance().refresh();
		return true;
	}
	
	/**
	 * Every Time this element is called, switch to the drawing mode
	 * of VennMaker
	 */
	@Override
	public void setData()
	{
		InterviewController controller = InterviewController.getInstance();

		controller.switchToNetwork(network);
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}
	
	@Override
	public JPanel getConfigurationDialog()
	{
		if(configPanel!=null)
		{
			updateComboBox();
			return configPanel;
		}
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		configPanel = new JPanel(gbl);
		cbNetworks = new JComboBox();
		listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				network = (Netzwerk)cbNetworks.getSelectedItem();
			}
		};
		
		cbNetworks.addActionListener(listener);
		updateComboBox();
		JLabel label = new JLabel(Messages.getString("SwitchToNetworkElement.ChooseNetwork"));
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(12, 0, 0, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		gbl.setConstraints(label, c);
		configPanel.add(label);
		
		c.gridx = 1;
		c.weightx = 5;
		c.weighty = 5;
		c.insets = new Insets(10, 0, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(cbNetworks, c);
		configPanel.add(cbNetworks);
		
		return configPanel;
	}

	private void updateComboBox()
	{
		if(cbNetworks != null)
		{
			Vector<Netzwerk> networks = VennMaker.getInstance().getProject().getNetzwerke();
			cbNetworks.removeActionListener(listener);
			cbNetworks.removeAllItems();
			
			for(Netzwerk n : networks)
				cbNetworks.addItem(n);
			
			if(network == null || !networks.contains(network))
				network = (Netzwerk)cbNetworks.getItemAt(0);
			else
				cbNetworks.setSelectedItem(network);
			cbNetworks.addActionListener(listener);
		}
	}
	
	@Override
	public boolean addToTree()
	{
		return true;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("SwitchToNetworkElement.InstructionText");	//$NON-NLS-1$
	}
	@Override
	public InterviewElementInformation getElementInfo()
	{
		SwitchToNetworkInformation info = new SwitchToNetworkInformation(cbNetworks.getSelectedItem().toString());
		
		info.setId(this.getId());
		info.createChildInformation(children);
		
		info.setParentInformation(parent);

		info.setElementClass(this.getClass());
		
		return info;
	}
	
	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if(!(information instanceof SwitchToNetworkInformation))
			return;
		
		getConfigurationDialog();
		
		SwitchToNetworkInformation info = (SwitchToNetworkInformation)information;
		this.setId(info.getId());
		
		InterviewLayer.getInstance().createChild(information, this);
		
		for(int i = 0; i < cbNetworks.getItemCount(); i++)
			if(cbNetworks.getItemAt(i).toString().equals(info.getNetworkToSwitch()))
				cbNetworks.setSelectedIndex(i);
	}

}
