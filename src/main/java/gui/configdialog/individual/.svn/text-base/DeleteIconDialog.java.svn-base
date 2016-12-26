/**
 * 
 */
package gui.configdialog.individual;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.configdialog.elements.CDialogActorImage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * 
 *
 */
public class DeleteIconDialog extends JDialog implements ActionListener
{
	private static final long				serialVersionUID	= 1L;
	
	private JList 								iconList;
	
	private DefaultListModel				iconModel;
	
	private DeleteIconDialog				dialog;
	
	private CDialogActorImage 				caller;
	
	public DeleteIconDialog(CDialogActorImage caller)
	{
		super(ConfigDialog.getInstance());
		this.caller = caller;
		iconModel = new DefaultListModel();
		this.iconList = new JList(iconModel);
		this.iconList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		iconList.setCellRenderer(new IconListCellRenderer());
		iconList.setBackground(new Color(244,244,244));
		setTitle(Messages.getString("CDialogDeleteIcon.4")); //$NON-NLS-1$
		this.setLayout(new BorderLayout());
	}
	
	public void makeVisible(ArrayList<ImageIcon> icons)
	{		
		iconModel.clear();
			
		for(int i=0; i<icons.size(); i++)
			iconModel.addElement(icons.get(i));
		
		if(this.dialog!=null)
		{
			this.setVisible(true);
			return;
		}
		dialog = this;
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		JButton deleteButton = new JButton(Messages.getString("CDialogDeleteIcon.1")); //$NON-NLS-1$
		deleteButton.setActionCommand("delete"); //$NON-NLS-1$
		deleteButton.addActionListener(this);
		
		JButton okButton = new JButton(Messages.getString("CDialogDeleteIcon.2")); //$NON-NLS-1$
		okButton.setActionCommand("ok"); //$NON-NLS-1$
		okButton.addActionListener(this);
		
		JButton cancelButton = new JButton(Messages.getString("CDialogDeleteIcon.3")); //$NON-NLS-1$
		cancelButton.setActionCommand("cancel"); //$NON-NLS-1$
		cancelButton.addActionListener(this);
		
		JPanel fieldPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		buttonPanel.add(deleteButton,gbc);
		
		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		buttonPanel.add(cancelButton,gbc);
		
		gbc.gridx = 6;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		buttonPanel.add(okButton,gbc);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		fieldPanel.add(new JScrollPane(iconList),gbc);
		
		this.add(fieldPanel,BorderLayout.CENTER);
		
		this.setSize(640,480);
		this.dialog = this;
		this.setLocation(VennMaker.getInstance().getLocation());
		this.setVisible(true);
	}
	
	private void deleteFromList()
	{
		int[] indices = iconList.getSelectedIndices();
		
		DefaultListModel model = (DefaultListModel)iconList.getModel();
		
		if(indices.length == model.size())
		{
			JOptionPane.showMessageDialog(this, Messages.getString("CDialogDeleteIcon.5")); //$NON-NLS-1$
			return;
		}
		
		for(int i = indices.length-1; i >= 0; i--)
		{
			ImageIcon icon = (ImageIcon)iconModel.get(indices[i]);
			caller.addIconToDelete(icon.getDescription());
			model.removeElementAt(indices[i]);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		JButton actionButton = (JButton)e.getSource();
		if(actionButton.getActionCommand().equals("delete")) //$NON-NLS-1$
		{
			deleteFromList();
		}
		else if(actionButton.getActionCommand().equals("cancel")) //$NON-NLS-1$
		{
			dispose();
		}
		else
		{
			caller.updateDelete();
			dispose();
		}
	}
	
	class IconListCellRenderer extends DefaultListCellRenderer
	{
		private static final long	serialVersionUID	= 1L;
		
		JLabel listLabel;
		public IconListCellRenderer()
		{
			super();
		}
		
		
		public Component getListCellRendererComponent(JList list, Object val,
				int index, boolean isSelected, boolean hasCellFocus)
		{
			listLabel = (JLabel)super.getListCellRendererComponent(list, val, index, isSelected, hasCellFocus);
			
			if (isSelected)
			{
				setForeground(Color.WHITE);
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			
			ImageIcon icon = (ImageIcon)val;
			
			setIcon(icon);
			setText(icon.getDescription());
			
			return this;
		}
	}
}
