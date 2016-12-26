package gui.sidemenu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTaskPane;
import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.AttributeType;
import data.MenuEventList;
import data.MenuObject;
import events.MenuEvent;
import files.ImageOperations;
import gui.ResizableImageIcon;
import gui.VennMaker;
import gui.VennMaker.ViewMode;
import gui.utilities.StringUtilities;

@SuppressWarnings("serial")
public class VennMakerSideMenuActorTypePane extends SideMenuSubItem
{


	private Map<String, ResizableIcon>	iconCache	= new HashMap<String, ResizableIcon>();

	public VennMakerSideMenuActorTypePane()
	{
		
		addMouseMotionListener(new MouseMotionListener()
		{
			
			@Override
			public void mouseMoved(MouseEvent e)
			{
				
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e)
			{
				
				System.out.println(""+e.getY());
			}
		});
	}

	protected void build()
	{
		removeAll();

		assert VennMaker.getInstance().getProject().getMainGeneratorType("ACTOR") != null; //$NON-NLS-1$
		assert VennMaker.getInstance().getProject()
				.getMainGeneratorType("ACTOR").getPredefinedValues() != null; //$NON-NLS-1$

		AttributeType mainGenType = VennMaker.getInstance().getProject()
				.getMainGeneratorType("ACTOR"); //$NON-NLS-1$
		if (mainGenType == null)
			return;

		setTitle(StringUtilities.truncate(mainGenType.getLabel(), StringUtilities.MAX_TASK_PANE_LABEL_LENGTH));
		setToolTipText(mainGenType.getLabel());
		setCollapsed(true);

		setLayout(new GridLayout(0, 1));

		ButtonGroup akteurTypButtonGroup = new ButtonGroup();
		int i = 0;

		String filename = "";
		if (VennMaker.getInstance().getProject().getMainGeneratorType("ACTOR")
				.getPredefinedValues() != null)
			for (Object value : VennMaker.getInstance().getProject()
					.getMainGeneratorType("ACTOR").getPredefinedValues()) //$NON-NLS-1$
			{
				ResizableIcon finalIcon;
				JToggleButton jrb;
				try
				{
					filename = VennMaker.getInstance().getProject()
							.getCurrentNetzwerk().getActorImageVisualizer()
							.getImage(value);
					if (iconCache.containsKey(filename))
					{
						finalIcon = iconCache.get(filename);
					}
					else
					{
						// check if svg or jpg/png
						String name = filename.toLowerCase();
						if (name.endsWith(".svg"))
							finalIcon = SvgBatikResizableIcon
									.getSvgIcon(new FileInputStream(filename),
											new Dimension(32, 32));
						else if (!name.equals(""))
							finalIcon = new ResizableImageIcon(new ImageIcon(
									ImageOperations.loadActorImage(filename, 32, 1)));
						else
							finalIcon = new ResizableImageIcon(new ImageIcon(
									ImageOperations.createActorIcon(null, 32, 1)));

						if (finalIcon == null)
							throw new IOException("Cant load Icon");
					}
				} catch (Exception exn)
				{
					BufferedImage img = new BufferedImage(32, 32,
							BufferedImage.TYPE_4BYTE_ABGR);
					Graphics2D g = img.createGraphics();
					g.setColor(Color.gray);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g.setStroke(new BasicStroke(3.0f));
					g.draw(new Ellipse2D.Double(4, 4, 24, 24));
					g.drawLine(4, 16, 28, 16);
					g.drawLine(16, 4, 16, 28);
					final ImageIcon icon = new ImageIcon(img);
					finalIcon = new ResizableImageIcon(icon);
				}
				jrb = new JToggleButton(StringUtilities.truncate(value.toString(), StringUtilities.MAX_TASK_PANE_LABEL_LENGTH),
						finalIcon);
				jrb.setHorizontalAlignment(SwingConstants.LEFT);
				jrb.setToolTipText(value.toString());
				final int j = i;
				final String fileNameIcon = filename;
				jrb.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						VennMaker v = VennMaker.getInstance();
						v.setCurrentActor(null);
						if (VennMaker.getInstance().getProject()
								.getMainGeneratorType("ACTOR").getPredefinedValues()[j] != null) //$NON-NLS-1$
							v.setMainGeneratorValue(VennMaker.getInstance()
									.getProject().getMainGeneratorType("ACTOR") //$NON-NLS-1$
									.getPredefinedValues()[j]);
						v.setActualButton(ViewMode.ALTER_NODES);
						//FIXME: irrelevanter code??
						// Akteurbuttons deaktivieren
						// akteurButtonGroup.clearSelection();

						// Relationtyp-Buttons deaktivieren
						// if (relationTypButtonGroup != null)
						// relationTypButtonGroup.clearSelection();

						MenuObject actorObject = new MenuObject();
						actorObject.setMessage("ButtonActorAttributeSelected");
						MenuEventList.getInstance().notify(
								new MenuEvent(this, actorObject));
					}
				});

				akteurTypButtonGroup.add(jrb);
				add(jrb);
				if (i == 0)
					jrb.doClick();
				i++;
			}
		setCollapsed(false);
	}
}
