package gui.sidemenu;

import events.MenuEvent;
import files.FileOperations;
import gui.ArrowLine;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;
import gui.VennMaker.ViewMode;
import gui.utilities.StringUtilities;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTaskPane;
import org.jvnet.flamingo.common.icon.DecoratedResizableIcon;
import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.AttributeType;
import data.MenuEventList;
import data.MenuObject;

/**
 * 
 * 
 * FIXME: seperate subpanes to get sidemenusubitems which then can be ordered
 */
public class VennMakerSideMenuRelationPanes
{
	final private ArrayList<JXTaskPane>	panes;

	final private Stack<JXTaskPane>		freePanes;

	public VennMakerSideMenuRelationPanes()
	{
		panes = new ArrayList<JXTaskPane>();
		freePanes = new Stack<>();
	}

	protected void build()
	{
		final Vector<String> attributeTypeCollectors = VennMaker.getInstance()
				.getProject().getAttributeCollectors();

		recyclePanes();

		for (String attributeType : attributeTypeCollectors)
		{

			JXTaskPane subpane = retrieveSubPane();
			buildSubPanel(subpane, attributeType);
		}
	}

	public ArrayList<JXTaskPane> retrieveAll()
	{
		return panes;
	}

	protected void recyclePanes()
	{
		for (JXTaskPane p : panes)
		{
			MouseMotionListener[] mouseMotionListeners = p.getMouseMotionListeners();
			for(MouseMotionListener l : mouseMotionListeners) {
				p.removeMouseMotionListener(l);
			}
			freePanes.push(p);
			p.removeAll();
		}
	}

	protected JXTaskPane retrieveSubPane()
	{
		JXTaskPane freePane;
		if (!freePanes.isEmpty())
		{
			freePane = freePanes.pop();
		}
		else
		{
			freePane = new JXTaskPane();
			panes.add(freePane);
		}

		return freePane;
	}

	private void buildSubPanel(JXTaskPane pane, String attributeCollector)
	{
		/*
		 * Wenn generatorAttributeType noch existiert, dann uebernimm dieses,
		 * falls nicht, nimm erstes Attribut im aktuellen Collector
		 */
		final AttributeType generator = VennMaker
				.getInstance()
				.getProject()
				.getAttributeTypes(attributeCollector)
				.contains(
						VennMaker.getInstance().getProject()
								.getMainGeneratorType(attributeCollector)) ? VennMaker
				.getInstance().getProject()
				.getMainGeneratorType(attributeCollector) : VennMaker.getInstance()
				.getProject().getAttributeTypes(attributeCollector).firstElement();

		String attributeLabel = VennMaker.getInstance().getProject()
				.getMainGeneratorType(attributeCollector).getLabel();
		
		String paneTitle = attributeLabel + "(";
		if (attributeCollector.equals("STANDARDRELATION"))
			paneTitle += Messages.getString("AttributeCollector.StandardRelation");//$NON-NLS-1$
		else
			paneTitle += attributeCollector;

		paneTitle += ")";
		
		
		pane.setTitle(StringUtilities.truncate(paneTitle));
		pane.setToolTipText(paneTitle);
		
		pane.setCollapsed(false);
		pane.setLayout(new GridLayout(0, 1));
		
		
		ButtonGroup relationTypButtonGroup = new ButtonGroup();
		
		int i = 0;
		if (generator.getPredefinedValues() != null)
			for (final Object relationTyp : generator.getPredefinedValues())
			{
				try
				{
					ResizableIcon fontIcon;
					fontIcon = SvgBatikResizableIcon
							.getSvgIcon(
									new FileInputStream(FileOperations
											.getAbsolutePath(Messages
													.getString("VennMaker.Icon_Null"))), new Dimension(32, //$NON-NLS-1$
											32));
					ResizableIcon finalIcon = new DecoratedResizableIcon(fontIcon,
							new DecoratedResizableIcon.IconDecorator()
							{
								public void paintIconDecoration(Component c,
										Graphics g, int x, int y, int width, int height)
								{

									AttributeType currentGenerator = null;

									if (VennMaker.getInstance().getProject()
											.getAttributeTypes().size() == 0)
										return;

									for (AttributeType att : VennMaker.getInstance()
											.getProject().getAttributeTypes())
									{
										if (att.getLabel().equals(generator.getLabel()))
										{
											currentGenerator = att;
											break;
										}
									}

									if (currentGenerator == null)
										currentGenerator = VennMaker.getInstance()
												.getProject().getAttributeTypes().get(0);

									Graphics2D g2d = (Graphics2D) g.create();
									g2d.setRenderingHint(
											RenderingHints.KEY_ANTIALIASING,
											RenderingHints.VALUE_ANTIALIAS_ON);
									g2d.setPaint(VennMaker
											.getInstance()
											.getProject()
											.getCurrentNetzwerk()
											.getRelationColorVisualizer(
													currentGenerator.getType(),
													currentGenerator).getColor(relationTyp));

									float size = VennMaker
											.getInstance()
											.getProject()
											.getCurrentNetzwerk()
											.getRelationSizeVisualizer(
													currentGenerator.getType(),
													currentGenerator).getSize(relationTyp);
									float[] dashArray = VennMaker
											.getInstance()
											.getProject()
											.getCurrentNetzwerk()
											.getRelationDashVisualizer(
													currentGenerator.getType(),
													currentGenerator)
											.getDasharray(relationTyp);

									float strokeWidth = VennMakerView.getVmcs()
											.toJava2D(
													size / VennMakerView.LINE_WIDTH_SCALE);

									BasicStroke stroke = new BasicStroke();

									// BUGFIX (unter MacOS kommt es oefter zu einer
									// negativen strokeWidth
									// und damit zu einer Exception)
									if (strokeWidth > 0)
										stroke = new BasicStroke(strokeWidth, stroke
												.getEndCap(), stroke.getLineJoin(), stroke
												.getMiterLimit(), dashArray, stroke
												.getDashPhase());

									g2d.setStroke(stroke);
									if (VennMaker.getInstance().getProject()
											.getIsDirected(currentGenerator.getType()))
									{
										ArrowLine lineA = new ArrowLine(60, 32, 10, 10);
										g2d.draw(lineA);
									}
									else
									{
										Line2D.Double line = new Line2D.Double(10, 10,
												60, 32);
										g2d.draw(line);
									}
									/*
									 * switch (typ.getDirectionType()) { case DIRECTED:
									 * ArrowLine lineA = new ArrowLine(60, 32, 10, 10);
									 * g2d.draw(lineA); break; case UNDIRECTED:
									 * Line2D.Double line = new Line2D.Double(10, 10, 60,
									 * 32); g2d.draw(line); }
									 */

								}
							});
					JToggleButton jrb = new JToggleButton(
							StringUtilities.truncate((String) relationTyp), finalIcon);
					jrb.setHorizontalAlignment(SwingConstants.LEFT);

					AttributeType currentGenerator = null;

					for (AttributeType att : VennMaker.getInstance().getProject()
							.getAttributeTypes())
					{
						if (att.getLabel().equals(generator.getLabel()))
						{
							currentGenerator = att;
							break;
						}
					}

					if (currentGenerator == null)
						currentGenerator = VennMaker.getInstance().getProject()
								.getAttributeTypes().get(0);

					final AttributeType gen = currentGenerator;

					jrb.setToolTipText((String) relationTyp);

					jrb.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							VennMaker v = VennMaker.getInstance();
							v.setBeziehungsart(gen);
							v.setBeziehungsauspraegung(relationTyp);
							v.setActualButton(ViewMode.ALTER_EDGES);
							//FIXME: irrelevanter code??
							// Akteurtypbuttons deaktivieren
//							v.akteurTypButtonGroup.clearSelection();

							// Akteurbuttons deaktivieren
//							v.akteurButtonGroup.clearSelection();

							MenuEventList.getInstance().notify(
									new MenuEvent(this, new MenuObject(
											"ButtonRelationAttributeSelected")));
						}
					});
					relationTypButtonGroup.add(jrb);
					pane.add(jrb);
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				i++;
			}

	}
}
