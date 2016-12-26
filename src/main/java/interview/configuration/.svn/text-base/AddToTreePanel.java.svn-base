package interview.configuration;

import gui.Messages;
import gui.configdialog.elements.CDialogInterviewCreator;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class AddToTreePanel extends JPanel implements ActionListener
{
	private static final long			serialVersionUID	= 4282058215376477492L;

	private CDialogInterviewCreator	interviewCreator;
	
	private GridBagConstraints			gbc;

	public AddToTreePanel(CDialogInterviewCreator interviewCreator)
	{
		super(new GridLayout(0,2));

		this.interviewCreator = interviewCreator;

		createAddButtonPanel();

	}
	
	public void addComponent(Component comp)
	{
		removeAll();
		gbc = new GridBagConstraints();
		
		JPanel childPanel = new JPanel(new GridBagLayout());
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0,0,0,100);
		childPanel.add(comp,gbc);
		
		add(childPanel);
		createAddButtonPanel();
		revalidate();
		repaint();
	}
	

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		interviewCreator.add(false);
	}
	
	
	private void createAddButtonPanel()
	{
		JButton btnAdd = new JButton(
				Messages.getString("CDialogInterviewCreator.AddToTree") + " --->"); //$NON-NLS-1$ //$NON-NLS-2$
		
		JPanel childPanel = new JPanel(new GridBagLayout());
		
		btnAdd.addActionListener(this);
		
		gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0,100,0,0);

		childPanel.add(btnAdd, gbc);
		
		add(childPanel);
	}
}
