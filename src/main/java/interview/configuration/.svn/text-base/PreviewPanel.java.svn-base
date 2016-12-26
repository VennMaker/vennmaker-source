package interview.configuration;

import gui.Messages;
import gui.configdialog.ConfigDialog;
import interview.elements.InterviewElement;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 * Contains one button "show preview", to spawn a Dialog containing 
 * a preview (of this interview element) with actual configuration.
 * 
 * 
 * 
 */
public class PreviewPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private InterviewElement caller;
	
	public PreviewPanel(InterviewElement caller)
	{
		this.caller = caller;
		
		JButton b = new JButton(Messages.getString("InterviewElement.ShowPreview"));
		b.addActionListener(this);
		
		this.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.CENTER;
		
		
		this.add(b,gbc);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) 
	{
		JDialog d = new JDialog(ConfigDialog.getInstance(),true);
		d.setLayout(new BorderLayout());
		d.setMinimumSize(new Dimension(640,480));
		d.setTitle(Messages.getString("InterviewElement.Preview"));
		caller.initPreview();
		JPanel p = caller.getControllerDialog();
		d.setContentPane(p);
		d.setSize(new Dimension(640,480));
		
		d.addWindowListener(new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0)
			{
				caller.deinitPreview();
			}

			@Override
			public void windowClosing(WindowEvent arg0)
			{
				caller.deinitPreview();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0)
			{
				// TODO Auto-generated method stub
				
			}
			
		});
		
		d.setVisible(true);
	}
}

class WordWrapHeaderRenderer extends JPanel implements TableCellRenderer 
{
	private static final long	serialVersionUID	= 1L;
	private JTextArea ta;
	
	public WordWrapHeaderRenderer()
	{
		super(new BorderLayout());
		ta = new JTextArea();
		ta.setEditable(false);
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		this.add(ta,BorderLayout.CENTER);
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
	{
		ta.setText((String)value);
		return this;
	}
}
