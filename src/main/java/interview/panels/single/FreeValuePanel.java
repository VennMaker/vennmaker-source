package interview.panels.single;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import data.AttributeType;

/**
 * panel with label and an textfield representing question and free answer
 * 
 * 
 * 
 */
public class FreeValuePanel extends ValuePanel
{
	private static final long	serialVersionUID	= 1L;
	
	private JTextField 			tfAnswer;
	
	private String 				question;
	
	private Color					background;
	
	public FreeValuePanel(AttributeType a, String question, Color background)
	{
		super(a);
//		this.setLayout(new GridLayout(0,2));
		this.question = question;
		this.background = background;
		build();
	}
	
	@Override
	public void build()
	{
		tfAnswer = new JTextField();
		tfAnswer.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				answer = tfAnswer.getText();
			}
		});

		JTextArea ta = new JTextArea(question);
		ta.setOpaque(true);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(false);
		ta.setEditable(false);
		ta.setBackground(background);
		ta.setBorder(null);
		JScrollPane scroll = new JScrollPane(ta);
		scroll.setBorder(null);
		scroll.setOpaque(true);
		scroll.setBackground(background);
		scroll.setPreferredSize(new Dimension(100,70));

		int x = 0;
		if(question!=null && !question.equals(""))
		{
			g = new GridBagConstraints();
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.gridx = x++;
			g.gridy = 0;
			g.weightx = 1;
			g.weighty = 1;
			g.insets = new Insets(0, 0, 0, 15);
			g.fill = GridBagConstraints.HORIZONTAL;	
			gbLayout.setConstraints(scroll, g);
			this.add(scroll);
		}
	
		g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = 0;
		g.weightx = 1;
		g.weighty = 1;
		g.insets = new Insets(0, 0, 0, 5);
		g.anchor = GridBagConstraints.CENTER;
		g.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.setConstraints(tfAnswer, g);
		this.add(tfAnswer);
		
		this.setBackground(background);
	}

	@Override
	public void setValue(Object val)
	{
		if(val != null)
		{
			tfAnswer.setText(val.toString());
			answer = val;
		}
	}
}
