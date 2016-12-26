package interview.configuration.NameInfoSelection;

import gui.Messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;


/**
 * Panel for configuring and displaying name and info of an interview element
 * 
 * 
 * 
 *
 */
public class NameInfoPanel implements NameInfoSelector, Serializable 
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	protected JTextField 	tfName;
	
	protected JTextArea 	taDescription;
	
	private boolean		infoOnlyElement;
	
	public NameInfoPanel(boolean infoOnlyElement)
	{
		this.infoOnlyElement = infoOnlyElement;
		final String defName = Messages.getString("EditActorLabelDialog.1");	//$NON-NLS-1$
		tfName = new JTextField(defName);
		tfName.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(defName.equals(tfName.getText()))
					tfName.setText("");
			}
		});
		taDescription = new JTextArea();	//$NON-NLS-1$
		taDescription.setLineWrap(true);
	}
	
	@Override
	public JPanel getConfigurationPanel()
	{
		JPanel panel = new JPanel();
		GridBagLayout gbLayout = new GridBagLayout();
		panel.setLayout(gbLayout);
		
		JLabel lblName = new JLabel(
				Messages.getString("EditActorLabelDialog.1"));	//$NON-NLS-1$
		JLabel lblDescription = new JLabel(
				Messages.getString("InterviewElement.InfoText"));	//$NON-NLS-1$
		
		JScrollPane scroll = new JScrollPane(taDescription);
		GridBagConstraints g = new GridBagConstraints();
		
		int x = 0, y = 0;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.gridx = x;
		g.gridy = y;
		g.gridwidth = 1;
		g.weightx = 1;
		g.insets = new Insets(2,0,2,0);
		gbLayout.setConstraints(lblName, g);
		panel.add(lblName);
		
		g.gridy = ++y;
		g.gridwidth = 1;
		g.weightx = 4.5;
		g.insets = new Insets(2,0,2,0);
		gbLayout.setConstraints(tfName, g);
		panel.add(tfName);
				
		g.gridy = ++y;
		g.gridwidth = 2;
		g.weightx = 1;
		g.insets = new Insets(2,0,2,0);
		gbLayout.setConstraints(lblDescription, g);
		panel.add(lblDescription);
		
		g.gridy = ++y;
		g.gridwidth = 1;
		g.weightx = 4.5;
		g.gridheight = 2;
		g.weighty = 2;
		g.insets = new Insets(2,0,2,0);
		g.fill = GridBagConstraints.BOTH;
		gbLayout.setConstraints(scroll, g);
		panel.add(scroll);
		
		return panel;
	}
	
	@Override
	public JPanel getControllerPanel()
	{
		JPanel panel = new JPanel();

		if(infoOnlyElement)
		{
			panel.setLayout(new BorderLayout());
			JEditorPane label = new JEditorPane();
			label.setEditable(false);
			label.setContentType("text/html");
			label.setText(getInfo());			
			
			panel.add(label,BorderLayout.CENTER);
		}
		else
		{	
			JEditorPane ta = new JEditorPane();
			ta.setEditable(false);
			ta.setFocusable(false);
			ta.setContentType("text/html");
			ta.setText(getInfo());			
			ta.setBorder(null);
			ta.setBackground(new Color(0, 100, 100, 20));
			
			JScrollPane scroll = new JScrollPane(ta);
			scroll.setBorder(null);
			scroll.setFocusable(false);
			scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); //damit der Inhalt nicht verwischt
			GridBagLayout gbLayout = new GridBagLayout();
			panel.setLayout(gbLayout);
			GridBagConstraints g = new GridBagConstraints();
			
			g.anchor = GridBagConstraints.NORTHWEST;
			g.gridx = 0;
			g.gridy = 0;
			g.gridwidth = 1;
			g.weightx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.BOTH;
			
			g.insets = new Insets(0,0,0,0);
			gbLayout.setConstraints(scroll, g);
			panel.add(scroll);
		}

		return panel;
	}
	
	@Override
	public String getName()
	{
		return tfName.getText();
	}
	
	@Override
	public void setName(String name)
	{
		if(name == null)
			tfName.setText(""); //$NON-NLS-1$
		else
			tfName.setText(name);
	}

	@Override
	public String getInfo() 
	{
		return taDescription.getText();
	}
	
	@Override
	public void setInfo(String info)
	{
		if(info == null)
			taDescription.setText(""); //$NON-NLS-1$
		else
			taDescription.setText(info);
	}
	
	   	
	
}
