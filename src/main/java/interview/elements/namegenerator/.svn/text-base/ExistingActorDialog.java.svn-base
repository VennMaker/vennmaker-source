package interview.elements.namegenerator;

import files.ImageOperations;
import gui.Messages;
import gui.VennMaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;

/**
 * Dialog displayed in NameGenerator-Elements, if a user wants to add an actor 
 * by an already taken name.
 * 
 * 
 * 
 *
 */
public class ExistingActorDialog extends JDialog implements ActionListener
{
	private static final long	serialVersionUID	= 1L;
	private List<Akteur> projectActors;
	private Akteur actor;
	private DrawActorPanel actorPanel;
	
	public boolean yes;		//true if yes was said
	public boolean cancel;	//true if the dialog was canceled
	
	public ExistingActorDialog(Window parent)
	{
		super(parent);
		this.setModal(true);
		
		yes = false;
		cancel = false;
		
		this.setTitle(Messages.getString("ExistingActorDialog.Title")); //$NON-NLS-1$
		this.projectActors = VennMaker.getInstance().getProject().getAkteure();
		this.add(buildPanel());
		this.setMinimumSize(new Dimension(400,400));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				cancel = true;
			}
		});
		this.pack();
	}
	
	private BufferedImage getDefaultActorImage(int groesse)
	{
		BufferedImage actorsImage = new BufferedImage(groesse, groesse,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D gi = actorsImage.createGraphics();
		gi.setColor(Color.gray);
		gi.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		gi.setStroke(new BasicStroke(3.0f));
		gi.draw(new Ellipse2D.Double(2, 2, groesse - 4, groesse - 4));
		gi.drawLine((int) (groesse / 2), 2, (int) (groesse / 2),
				(int) groesse - 4);
		gi.drawLine(2, (int) (groesse / 2), (int) groesse - 4,
				(int) (groesse / 2));

		return actorsImage;
	}
	
	private JPanel buildPanel()
	{
		JPanel panel = new JPanel();
		actorPanel = new DrawActorPanel();
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(actorPanel);
		
		JLabel lblDesc = new JLabel(Messages.getString("ExistingActorDialog.Question")); //$NON-NLS-1$
		JButton btnYes = new JButton(Messages.getString("ProjectChooser.Yes")); //$NON-NLS-1$
		btnYes.setActionCommand("yes"); 
		btnYes.addActionListener(this);
		
		JButton btnNo = new JButton(Messages.getString("ProjectChooser.No")); //$NON-NLS-1$
		btnNo.setActionCommand("no");
		btnNo.addActionListener(this);
		
		JButton btnCancel = new JButton(Messages.getString("ProjectChooser.Cancel")); //$NON-NLS-1$
		btnCancel.setActionCommand("cancel");
		btnCancel.addActionListener(this);
		
		GridBagLayout gbl = new GridBagLayout();
		panel.setLayout(gbl);
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(10, 0, 10, 0);
		c.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(scroll, c);
		panel.add(scroll);
		
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 0.1;
		c.gridwidth = 3;
		c.insets = new Insets(0, 0, 10, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(lblDesc, c);
		panel.add(lblDesc);
		
		c.gridy = 3;
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.gridwidth = 1;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(btnYes, c);
		panel.add(btnYes);
		
		c.gridx = 1;
		gbl.setConstraints(btnNo, c);
		panel.add(btnNo);
		
		c.gridx = 2;
		gbl.setConstraints(btnCancel, c);
		panel.add(btnCancel);
		
		return panel;
	}
	
	public void setActorName(String name, List<Akteur> addedActors)
	{
		actor = null;
		for(Akteur a : projectActors)
			if(a.getName().equals(name))
			{
				actor = a;
				break;
			}
		if(actor==null)
			for(Akteur a : addedActors)
				if(a.getName().equals(name))
				{
					actor = a;
					break;
				}
		actorPanel.setActor(actor);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if(cmd.equals("yes"))
			yes = true;
		else if(cmd.equals("no"))
			yes = false;
		else //cancel
			cancel = true;
		
		this.dispose();
	}
	
	class DrawActorPanel extends JPanel
	{
		private static final long	serialVersionUID	= 1L;
		private Akteur a;
		private Image actorsIcon = null;
		private Netzwerk net;

		public void setActor(Akteur a)
		{
			this.a = a;
			cancel = false;
			yes = true;
			if(a!=null)
			{
				int groesse = 50;
				int imageScalingFactor = 1;
				if(a.getNetzwerke().size() > 0)
					net = a.getNetzwerke().iterator().next();
				else
					net = VennMaker.getInstance().getActualVennMakerView().getNetzwerk();
				String filename = net.getActorImageVisualizer().getImage(a, net);
				BufferedImage actorsImage = ImageOperations.loadActorImage(filename, 50, imageScalingFactor);
				if (actorsImage == null)
					actorsImage = getDefaultActorImage(50);
	
				actorsIcon = ImageOperations.createActorIcon(actorsImage, groesse,imageScalingFactor);
			}
			repaint();
		}
		
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if(a!=null)
			{
				int fs = g.getFont().getSize();
				int cx = this.getWidth()/2;
				
				String name = a.getName();
				FontMetrics fm = g.getFontMetrics();
				int x = cx - (fm.stringWidth(name)/2);
				int y = fs + 5;
				
				g.drawString(name, x, y);
				y += 10;

				if(actorsIcon != null)
				{
					x = cx - (actorsIcon.getWidth(null)/2);
					g.drawImage(actorsIcon,x,y,null);
					y += actorsIcon.getHeight(null) ;
				}
				
				/*///Akteursattribute anzeigen...
				  //x = 5;		
		  		Map<AttributeType,Object> aTypes = a.getAttributes(net);
				int biggest_sw = 0;
				List<AttributeType> projectAttributes = VennMaker.getInstance().getProject().getAttributeTypes("ACTOR");
				List<String> properties = new ArrayList<String>();
				//get biggest stringwidth
				for(AttributeType a : projectAttributes)
				{
					Object o = aTypes.get(a);
					String ques = a.getQuestion();
					String s = (ques!=null?ques:a.getLabel())+" : ";
					if(o!=null && !o.toString().equals(""))
						s += o.toString();

					biggest_sw = Math.max(fm.stringWidth(s), biggest_sw);
					properties.add(s);
				}
				
				x = cx - (biggest_sw/2);
				for(String s : properties)
				{
					g.drawString(s, x, y);
					y += fs + 5;
				}
				this.setPreferredSize(new Dimension(biggest_sw,y));
				*/
				this.setPreferredSize(new Dimension(x,y));
			}
		}
	}
}
