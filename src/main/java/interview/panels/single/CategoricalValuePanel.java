/**
 * 
 */
package interview.panels.single;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import data.AttributeType;

/**
 * panel which represents one categorical attribute with its answers by radio buttons
 * (needed for MixedAnswerPanel)
 * 
 * 
 * 
 */
public class CategoricalValuePanel extends ValuePanel
{
	private static final long	serialVersionUID	= 1L;

	private ButtonGroup			bg;

	private String					question;
	
	private Color					background;
	
	public CategoricalValuePanel(AttributeType a, String question,Color background)
	{
		super(a);
		this.question = question;
		this.background = background;
		build();
	}

	private JPanel buildAnswerPanel()
	{
		Object[] preVals = a.getPredefinedValues();
		GridBagLayout gbl = new GridBagLayout();
		JPanel panel = new JPanel(gbl);
		GridBagConstraints g = new GridBagConstraints();
		int x = 0, y = 0;
		

		bg = new ButtonGroup();
		
		//one label per value
		for(Object o : preVals)
		{
			g = new GridBagConstraints();
			g.fill = GridBagConstraints.HORIZONTAL;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.gridx = x++;
			g.gridy = 0;
			g.weightx = 1;
			g.weighty = 0;
			g.insets = new Insets(0, 0, 0, 5);
			JLabel l = new JLabel(o.toString(),SwingConstants.CENTER);
			l.setOpaque(true);
			l.setBackground(background);
			gbl.setConstraints(l, g);
			panel.add(l);
		}
		x = 0;
		g.gridy = y;
		
		//one radiobutton per value
		for(final Object o : preVals)
		{
			JRadioButton rb = new JRadioButton();
			rb.setHorizontalAlignment(SwingConstants.CENTER);
			rb.setActionCommand(o.toString());
			rb.setOpaque(true);
			rb.setBackground(background);
			rb.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					answer = o;
				}
			});
			
			
//			if(a.getDefaultValue() != null && o.toString().equals(a.getDefaultValue().toString()))
//				rb.setSelected(true);
			
			bg.add(rb);
			
			g = new GridBagConstraints();
			g.fill = GridBagConstraints.HORIZONTAL;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.gridx = x++;
			g.gridy = 1;
			g.weightx = 1;
			g.weighty = 0;
			g.insets = new Insets(0, 0, 0, 5);
			gbl.setConstraints(rb, g);
			panel.add(rb);
		}
		
		panel.setBackground(background);
		return panel;
	}
	@Override
	public void build()
	{
		this.setLayout(gbLayout);
		
		//Question
		JTextArea ta = new JTextArea(question);
		ta.setLineWrap(true);
		ta.setEditable(false);
		ta.setBackground(background);
		ta.setOpaque(true);
		ta.setBorder(null);

		JScrollPane scroll = new JScrollPane(ta);
		scroll.setBorder(null);
		scroll.setPreferredSize(new Dimension(200,70));

		//Answer
		JScrollPane answerScroll = new JScrollPane(buildAnswerPanel());
		answerScroll.setOpaque(true);
		answerScroll.setBorder(null);
		answerScroll.setPreferredSize(new Dimension(200,70));
		
		int x = 0;
		if(question!=null && !question.equals(""))
		{
			g = new GridBagConstraints();
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.gridx = x++;
			g.gridy = 0;
			g.weightx = 0.1;
			g.weighty = 1;
			g.insets = new Insets(0, 0, 0, 15);
			g.fill = GridBagConstraints.HORIZONTAL;	
			gbLayout.setConstraints(scroll, g);
			this.add(scroll);
		}
		
		g = new GridBagConstraints();
		g.gridx = x;
		g.gridy = 0;
		g.weightx = 0.9;
		g.weighty = 1;
		g.insets = new Insets(0, 0, 0, 5);
		g.anchor = GridBagConstraints.FIRST_LINE_START;
		g.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.setConstraints(answerScroll, g);
		this.add(answerScroll);
		this.setBackground(background);
	}

	@Override
	public void setValue(Object val)
	{
		Enumeration<AbstractButton> buttons = bg.getElements();
		while(buttons.hasMoreElements())
		{
			AbstractButton b = buttons.nextElement();
			if(b.getActionCommand().equals(val.toString()))
			{
				bg.setSelected(b.getModel(), true); 
				answer = val;
				break;
			}
		}
	}
}