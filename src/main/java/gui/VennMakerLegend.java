package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;
import data.Relation;
import files.FileOperations;
import files.ImageOperations;

public class VennMakerLegend
{

	private boolean									showActorSizes		= true;

	private boolean									showActorSymbols	= true;

	private boolean									showActorPies		= true;

	private HashMap<AttributeType, Boolean>	showRelations		= new HashMap<AttributeType, Boolean>();

	private ArrayList<String>						bes					= new ArrayList<String>();

	private ArrayList<Integer>						sizes					= new ArrayList<Integer>();

	private Netzwerk netzwerk;
	
	public VennMakerLegend(Netzwerk n)
	{
		this.netzwerk = n;
	}
	
	public void paintLegend(final Graphics graphics, Netzwerk n)
	{
		if (n==null) return;
		double x, y;
		float one = VennMakerView.getVmcs().toJava2D(1);
		Graphics2D g = (Graphics2D) graphics.create();
		
		VennMakerView view = VennMaker.getInstance().getViewOfNetwork(n);	
		FontMetrics fm = view.getFontMetrics(g.getFont());
		g.setPaint(Color.BLACK);
		g.setFont(g.getFont().deriveFont(
				Font.PLAIN,
				VennMakerView.getVmcs().toJava2D(3)
						* VennMaker.getInstance().getProject().getTextZoom()));
		ImageObserver imgObserver = new JPanel();

		// Symbol Size Zeichnen
		// --------------------

		createSizesWithCaptions(n);

		TranscoderInput ti;
		BufferedImageTranscoder t = new BufferedImageTranscoder();
		float biggestSize = VennMakerView.getVmcs().toJava2D(
				sizes.size() > 0 ? sizes.get(sizes.size() - 1) : 0);

		float egoGroesse = 30.0f;
		BufferedImage actorsImage = null;
		double groesse = Math.ceil(biggestSize == 0 ? VennMakerView.getVmcs()
				.toJava2D(egoGroesse) : biggestSize);

		if (n.getEgo() != null)
		{
			egoGroesse = n.getEgo().getGroesse(n);
			/* Image des Egos laden */
			actorsImage = ImageOperations.loadActorImage(n
					.getActorImageVisualizer().getImage(n.getEgo(), n), groesse,
					VennMaker.getInstance().getViewOfNetwork(n)
							.getImageScalingFactor());
		}

		double curSize = 0;
		double circleOffset = 0;
		String curBes = null;
		float textZoom = VennMaker.getInstance().getProject().getTextZoom();
		int fontSize = g.getFont().getSize();
		boolean first = true;
		double yUeber = 0;

		x = VennMaker.getInstance().getViewOfNetwork(n).getViewArea().getMinX()
				+ VennMakerView.getVmcs().toJava2D(2);
		y = VennMaker.getInstance().getViewOfNetwork(n).getViewArea().getMaxY()
				- VennMakerView.getVmcs().toJava2D(2);
		if (showActorSizes)
		{
			/* Variablen fuer die "Knick-Linie" */
			double kLineEndX = 0, kLineEndY;
			/** describes the last endpoint, to measure correct distances */
			double lastEndY = y - (fontSize * (sizes.size())) - 5;

			for (int i = (sizes.size() - 1); i >= 0; i--)
			{
				curSize = VennMakerView.getVmcs().toJava2D(sizes.get(i));
				curBes = bes.get(i);

				circleOffset = (biggestSize - curSize) / 2;

				if (actorsImage == null)
				{
					/* Default - Bild */
					actorsImage = VennMakerView.getDefaultActorImage((int) curSize);
				}

				Image actorsIcon = actorsImage.getScaledInstance(
						(int) Math.ceil(curSize
								* VennMaker.getInstance().getViewOfNetwork(n)
										.getImageScalingFactor()),
						(int) Math.ceil(curSize
								* VennMaker.getInstance().getViewOfNetwork(n)
										.getImageScalingFactor()),
						BufferedImage.SCALE_SMOOTH);

				if (actorsIcon != null)
				{
					/**
					 * switch colors between black and light gray to improve the
					 * traceability of corresponding lines, sizes and captions
					 */
					if ((i % 2) == 0)
						g.setPaint(Color.BLACK);
					else
						g.setPaint(Color.GRAY);

					g.drawImage(actorsIcon, (int) Math.round(x + circleOffset),
							(int) Math.round(y - curSize), (int) curSize,
							(int) curSize, imgObserver);

					double startLineX = x + (biggestSize / 2);
					double lineY = y - curSize;
					double endLineX = startLineX + (biggestSize / 2)
							+ VennMakerView.getVmcs().toJava2D(2 * textZoom);

					kLineEndX = endLineX
							+ VennMakerView.getVmcs().toJava2D(2 + textZoom);
					kLineEndY = lastEndY;

					/** make sure, nothing is overlapping */
					if (!first)
						while ((Math.abs(kLineEndY - lastEndY) < fontSize))
							kLineEndY += VennMakerView.getVmcs().toJava2D(1.0);
					else if (kLineEndY > lineY)
						kLineEndY = lineY;

					g.setStroke(new BasicStroke(VennMakerView.getVmcs()
							.toJava2D(0.3)));

					/* gerade Linie */
					g.drawLine((int) Math.round(startLineX),
							(int) Math.round(lineY), (int) Math.round(endLineX),
							(int) Math.round(lineY));
					/* geknickte Linie */
					g.drawLine((int) Math.round(endLineX + 1),
							(int) Math.round(lineY), (int) Math.round(kLineEndX),
							(int) Math.round(kLineEndY));

					/** after lines switch color back to black */
					g.setPaint(Color.BLACK);
					/* Beschreibung */
					g.drawString(curBes, (int) Math.round(kLineEndX + 5),
							(int) Math.round(kLineEndY + (fontSize / 3)));

					/* Ueberschrift */
					if (first)
					{
						yUeber = ((kLineEndY < lineY ? kLineEndY : lineY) - fontSize);
						g.drawString(
								Messages.getString("VennMakerView.0"), (int) Math.round(x), (int) Math //$NON-NLS-1$
										.round(yUeber));
						first = false;
					}

					lastEndY = kLineEndY;
				}
			}

			// y = yDERueberschrift + offset
			y = yUeber
					- (fontSize + VennMakerView.getVmcs().toJava2D(4 * textZoom));
		}

		InputStream is;

		/* Ist die Bedeutung der Kreise hinterlegt? */
		if (VennMaker.getInstance().getProject().getCurrentNetzwerk()
				.getHintergrund().getCirclesLabel() != null)
		{
			/* Laden des Symbols fuer die Kreise */
			BufferedImage legendImage = null;

			try
			{
				is = new FileInputStream(
						FileOperations
								.getAbsolutePath("icons/intern/CircleSymbol.svg")); //$NON-NLS-1$
				ti = new TranscoderInput(is);
				t.transcode(ti, null);
				legendImage = t.getImage();
			} catch (FileNotFoundException e)
			{
				gui.ErrorCenter
						.manageException(
								e,
								Messages.getString("VennMakerView.6"), ErrorCenter.ERROR, true, true); //$NON-NLS-1$
			} catch (TranscoderException e)
			{
				e.printStackTrace();
			}
			if (legendImage != null)
			{
				Image legendIcon = legendImage.getScaledInstance(
						(int) (VennMakerView.getVmcs().toJava2D(4) * VennMaker
								.getInstance().getProject().getTextZoom()),
						(int) (VennMakerView.getVmcs().toJava2D(4) * VennMaker
								.getInstance().getProject().getTextZoom()),
						BufferedImage.SCALE_SMOOTH);
				if (legendIcon != null)
				{
					g.drawImage(
							legendIcon,
							(int) (x + VennMakerView.getVmcs().toJava2D(2)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (y - VennMakerView.getVmcs().toJava2D(2)
									* VennMaker.getInstance().getProject().getTextZoom()),
							imgObserver);
				}
			}
			g.drawString(VennMaker.getInstance().getProject().getCurrentNetzwerk()
					.getHintergrund().getCirclesLabel(), (int) x
					+ VennMakerView.getVmcs().toJava2D(10)
					* VennMaker.getInstance().getProject().getTextZoom(), (int) y
					+ VennMakerView.getVmcs().toJava2D(1)
					* VennMaker.getInstance().getProject().getTextZoom());
			y = y - VennMakerView.getVmcs().toJava2D(6)
					* VennMaker.getInstance().getProject().getTextZoom();
		}

		/* Ist die Bedeutung der Sektoren hinterlegt? */
		if (VennMaker.getInstance().getProject().getCurrentNetzwerk()
				.getHintergrund().getSectorAttribute() != null)
		{
			/* Laden des Symbols fuer die Sektoren */
			BufferedImage legendImage = null;

			try
			{
				is = new FileInputStream(
						FileOperations
								.getAbsolutePath("icons/intern/SectorsSymbol.svg")); //$NON-NLS-1$
				ti = new TranscoderInput(is);
				t.transcode(ti, null);
				legendImage = t.getImage();
			} catch (FileNotFoundException e)
			{
				gui.ErrorCenter
						.manageException(
								e,
								Messages.getString("VennMakerView.6"), ErrorCenter.ERROR, true, true); //$NON-NLS-1$
			} catch (TranscoderException e)
			{
				e.printStackTrace();
			}

			if (legendImage != null)
			{
				Image legendIcon = legendImage.getScaledInstance(
						(int) (VennMakerView.getVmcs().toJava2D(4) * VennMaker
								.getInstance().getProject().getTextZoom()),
						(int) (VennMakerView.getVmcs().toJava2D(4) * VennMaker
								.getInstance().getProject().getTextZoom()),
						BufferedImage.SCALE_SMOOTH);
				if (legendIcon != null)
				{
					g.drawImage(
							legendIcon,
							(int) (x + VennMakerView.getVmcs().toJava2D(2)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (y - VennMakerView.getVmcs().toJava2D(1)
									* VennMaker.getInstance().getProject().getTextZoom()),
							imgObserver);

				}
			}
			/*
			 * Workaround: wenn kein Sectorlabel gesetzt, dann leeren String
			 * zurueckgeben
			 */
			String sLabel = (VennMaker.getInstance().getProject()
					.getCurrentNetzwerk().getHintergrund().getSectorLabel() == null ? ""
					: VennMaker.getInstance().getProject().getCurrentNetzwerk()
							.getHintergrund().getSectorLabel());

			g.drawString(sLabel, (int) x + VennMakerView.getVmcs().toJava2D(10)
					* VennMaker.getInstance().getProject().getTextZoom(), (int) y
					+ VennMakerView.getVmcs().toJava2D(1)
					* VennMaker.getInstance().getProject().getTextZoom());
			y = y - VennMakerView.getVmcs().toJava2D(6)
					* VennMaker.getInstance().getProject().getTextZoom();
		}

		/*
		 * alle verwendeten Relations-Attribute, Actor-Symbol-Attribute und
		 * Akteursektoren mit Werten raussuchen
		 * 
		 * Alle dargestellten Relationsgruppen und die davon verwendeten Attribute
		 * raussuchen
		 */
		Vector<String> rGroups = VennMaker.getInstance().getProject()
				.getAttributeCollectors();

		List<AttributeType> colorA = new ArrayList<AttributeType>();
		for (String s : rGroups)
			colorA.add(n.getActiveRelationColorVisualizer(s).getAttributeType());

		List<AttributeType> dashA = new ArrayList<AttributeType>();
		for (String s : rGroups)
			dashA.add(n.getActiveRelationDashVisualizer(s).getAttributeType());

		List<AttributeType> sizeA = new ArrayList<AttributeType>();
		for (String s : rGroups)
			sizeA.add(n.getActiveRelationSizeVisualizer(s).getAttributeType());

		boolean visualized = true;

		Map<AttributeType, List<Object>> relationMap = new HashMap<AttributeType, List<Object>>();
		Map<AttributeType, List<Object>> symbolMap = new HashMap<AttributeType, List<Object>>();
		Map<AttributeType, Object> actorSectorMap = new HashMap<AttributeType, Object>();
		Map<AttributeType, Object> allActorSectorTypes = n
				.getActorSectorVisualizer().getAttributeTypesAndSelection();

		AttributeType symbolAtt = n.getActorImageVisualizer().getAttributeType();
		for (Akteur a : n.getAkteure())
		{
			/* Relation */
			for (Relation r : a.getRelations(n))
			{
				Map<AttributeType, Object> map = r.getAttributes(n);
				for (AttributeType att : map.keySet())
				{
					visualized = false;
					for (AttributeType ca : colorA)
						if (att.equals(ca))
						{
							visualized = true;
							break;
						}
					if (!visualized)
						for (AttributeType da : dashA)
							if (att.equals(da))
							{
								visualized = true;
								break;
							}
					if (!visualized)
						for (AttributeType sa : sizeA)
							if (att.equals(sa))
							{
								visualized = true;
								break;
							}

					if (visualized)
					{
						List<Object> oList = relationMap.get(att);
						Object o = map.get(att);

						if (oList == null)
						{
							oList = new ArrayList<Object>();
							oList.add(o);

							relationMap.put(att, oList);
						}
						else
						{
							if (!oList.contains(o))
								oList.add(o);
						}
					}
				}
			}
			/* Actor-Symbol */
			if (symbolAtt != null)
			{
				Object o = a.getAttributeValue(symbolAtt, n);
				if (o != null)
				{
					List<Object> oList = symbolMap.get(symbolAtt);
					if (oList == null)
					{
						oList = new ArrayList<Object>();
						oList.add(o);

						symbolMap.put(symbolAtt, oList);
					}
					else
					{
						if (!oList.contains(o))
							oList.add(o);
					}
				}
			}

			/* Akteursektor */
			for (Entry<AttributeType, Object> e : a.getAttributes(n).entrySet())
			{
				if (actorSectorMap.containsKey(e.getKey()))
					continue;
				Object displayedValue = allActorSectorTypes.get(e.getKey());
				Object actorValue = e.getValue();
				if (displayedValue != null && actorValue != null
						&& displayedValue.equals(actorValue))
					actorSectorMap.put(e.getKey(), displayedValue);
			}
		}

		/* Zeichne Akteurssektoren-Legende */
		if (this.showActorPies)
		{
			if (n.getActorSectorVisualizer() != null)
			{
				for (Map.Entry<AttributeType, Object> entry : actorSectorMap
						.entrySet())
				{
					g.setColor((Color) n.getActorSectorVisualizer().getSectorColor()
							.get(entry.getKey()));
					g.fillArc(
							(int) (x + VennMakerView.getVmcs().toJava2D(1)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (y - VennMakerView.getVmcs().toJava2D(3)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (VennMakerView.getVmcs().toJava2D(6) * VennMaker
									.getInstance().getProject().getTextZoom()),
							(int) (VennMakerView.getVmcs().toJava2D(6) * VennMaker
									.getInstance().getProject().getTextZoom()), -45, 90);
					g.setColor(Color.WHITE);
					g.fillArc(
							(int) (x + VennMakerView.getVmcs().toJava2D(3)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (y - VennMakerView.getVmcs().toJava2D(1)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (VennMakerView.getVmcs().toJava2D(2) * VennMaker
									.getInstance().getProject().getTextZoom()),
							(int) (VennMakerView.getVmcs().toJava2D(2) * VennMaker
									.getInstance().getProject().getTextZoom()), 0, 360);
					g.setColor(Color.BLACK);
					g.drawArc(
							(int) (x + VennMakerView.getVmcs().toJava2D(3)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (y - VennMakerView.getVmcs().toJava2D(1)
									* VennMaker.getInstance().getProject().getTextZoom()),
							(int) (VennMakerView.getVmcs().toJava2D(2) * VennMaker
									.getInstance().getProject().getTextZoom()),
							(int) (VennMakerView.getVmcs().toJava2D(2) * VennMaker
									.getInstance().getProject().getTextZoom()), 0, 360);

					String text = entry.getKey().toString()
							+ ": " + entry.getValue(); //$NON-NLS-1$
					if (text.length() > 30)
						text = text.substring(0, 30) + "..."; //$NON-NLS-1$
					g.drawString(text, (int) x
							+ VennMakerView.getVmcs().toJava2D(10)
							* VennMaker.getInstance().getProject().getTextZoom(),
							(int) y + VennMakerView.getVmcs().toJava2D(1)
									* VennMaker.getInstance().getProject().getTextZoom());

					y = y - VennMakerView.getVmcs().toJava2D(6)
							* VennMaker.getInstance().getProject().getTextZoom();
				}
			}
		}

		if (this.showActorSymbols)
		{
			/* Akteurs-Symbole + Werte zeichnen */
			Image actorsIcon = null;
			t = new BufferedImageTranscoder();

			String s = null;
			for (AttributeType a : symbolMap.keySet())
			{
				x += 2 * one;
				for (Object o : symbolMap.get(a))
				{
					String imageName = n.getActorImageVisualizer().getImage(o);
					if (imageName != null && imageName.endsWith(".svg"))
					{
						groesse = VennMakerView.getVmcs().toJava2D(9.0);
						t.addTranscodingHint(
								ImageTranscoder.KEY_HEIGHT,
								(float) Math.round(groesse
										* VennMaker.getInstance().getViewOfNetwork(n)
												.getImageScalingFactor()));
						try
						{
							is = new FileInputStream(imageName);
							ti = new TranscoderInput(is);
							t.transcode(ti, null);
							actorsImage = t.getImage();
						} catch (FileNotFoundException e)
						{
						} catch (TranscoderException e)
						{
							e.printStackTrace();
						}

						actorsIcon = actorsImage.getScaledInstance(
								(int) Math.round(groesse
										* VennMaker.getInstance().getViewOfNetwork(n)
												.getImageScalingFactor()),
								(int) Math.round(groesse
										* VennMaker.getInstance().getViewOfNetwork(n)
												.getImageScalingFactor()),
								BufferedImage.SCALE_SMOOTH);

						if (actorsIcon != null)
						{
							g.drawImage(actorsIcon, (int) Math.round(x),
									(int) Math.round(y - groesse), (int) groesse,
									(int) groesse, imgObserver);
							s = "" + o;
							g.drawString(s, (int) x + (float) groesse + (one * 2),
									(int) (y - ((float) (groesse / 2) + (fontSize / 3)))
											+ (one * 2) * textZoom);
							y = y - groesse - one;
						}
					}
				}
				s = a.getLabel();
				y = y - (one * 3) * textZoom;
				g.drawString(s, (int) x, (int) y + one * textZoom);
				y = y - (one * 2);

				x -= 2 * one;
			}
			/* Symbol-Attributes */
			if (s != null)
			{
				s = Messages.getString("VennMakerView.2");
				y = y - (one * 5) * textZoom;
				g.drawString(s, (int) x, (int) y + (one * 2) * textZoom);
				y = y - (one * 5) * textZoom;
			}
		}

		/* Relations-Attribute und Werte zeichnen */
		String s = null;

		boolean hasDrawnRelationColor = false;
		boolean hasDrawnRelationDashing = false;
		boolean hasDrawnRelationThickness = false;

		if (VennMaker.getInstance().getProject().getPaintLegendPolicy()
				.paintColor())
		{

			/* color */
			for (AttributeType ac : colorA)
			{
				HashMap<Color, Shape> colorShapeMap = new HashMap<Color, Shape>();
				HashMap<Color, String> colorStringMap = new HashMap<Color, String>();
				HashMap<Color, Point2D.Float> colorStringPointMap = new HashMap<Color, Point2D.Float>();

				double tmp_y = y;

				for (AttributeType a : relationMap.keySet())
				{
					if (a.equals(ac) && isShowRelation(a))
					{
						for (Object o : relationMap.get(a))
						{
							Color color = n.getActiveRelationColorVisualizer(
									a.getType()).getColor(o);

							s = "" + o;
							tmp_y = tmp_y - (one * 3) * textZoom;

							colorStringMap.put(color, s);

							colorStringPointMap.put(color, new Point2D.Float((int) x
									+ (one * 6), (int) tmp_y + one * textZoom));

							double stringWidth = fm.stringWidth(s);
							double xStart = x + (one * 6) + stringWidth + (one * 2);
							Shape line = new Line2D.Double(xStart, tmp_y, xStart
									+ (one * 10), tmp_y);

							colorShapeMap.put(color, line);

						}
						if (colorShapeMap.size() != 0)
						{
							y = tmp_y;

							boolean combineColorNames = colorShapeMap.size() == 1;

							for (Color c : colorShapeMap.keySet())
							{
								g.setColor(c);
								g.setStroke(new BasicStroke(VennMakerView.getVmcs()
										.toJava2D(1.0f / VennMakerView.LINE_WIDTH_SCALE)));
								g.draw(colorShapeMap.get(c));
							}
							g.setColor(Color.black);
							if (combineColorNames)
							{
								Point2D.Float fpoint = colorStringPointMap
										.get(colorShapeMap.keySet().iterator().next());
								g.drawString(
										Messages
												.getString("VennMakerView.CombinedRelationColorText"),
										fpoint.x, fpoint.y);
							}
							else
							{
								for (Color c : colorStringMap.keySet())
								{
									Point2D.Float fpoint = colorStringPointMap.get(c);
									g.drawString(colorStringMap.get(c), fpoint.x,
											fpoint.y);
								}
							}

							s = a.getLabel() + " "
									+ Messages.getString("VennMakerView.RelationColor");
							y = y - (one * 3) * textZoom;
							g.drawString(s, (int) x + (one * 2), (int) y + one
									* textZoom);
							// y = y - (one * 2);

							hasDrawnRelationColor = true;
						}

					}
				}
			}
		}
		/* dash */
		BasicStroke stroke = new BasicStroke(1.0f);
		if (VennMaker.getInstance().getProject().getPaintLegendPolicy()
				.paintDashing())
		{
			if (hasDrawnRelationColor)
				y = y - (one * 2);
			for (AttributeType ad : dashA)
			{
				HashMap<BasicStroke, Shape> strokeShapeMap = new HashMap<BasicStroke, Shape>();
				HashMap<BasicStroke, String> strokeStringMap = new HashMap<BasicStroke, String>();
				HashMap<BasicStroke, Point2D.Float> strokeStringPointMap = new HashMap<BasicStroke, Point2D.Float>();

				double tmp_y = y;

				for (AttributeType a : relationMap.keySet())
				{
					if (a.equals(ad) && isShowRelation(a))
					{
						for (Object o : relationMap.get(a))
						{
							float strokeSize = 1.0f / VennMakerView.LINE_WIDTH_SCALE;
							float[] dashArr = n.getActiveRelationDashVisualizer(
									ad.getType()).getDasharray(o);

							stroke = new BasicStroke(VennMakerView.getVmcs().toJava2D(
									strokeSize), stroke.getEndCap(),
									stroke.getLineJoin(), stroke.getMiterLimit(),
									dashArr, stroke.getDashPhase());

							s = "" + o;
							tmp_y = tmp_y - (one * 3) * textZoom;

							strokeStringMap.put(stroke, s);
							strokeStringPointMap.put(stroke, new Point2D.Float((int) x
									+ (one * 6), (int) tmp_y + one * textZoom));

							double stringWidth = fm.stringWidth(s);
							double xStart = x + (one * 6) + stringWidth + (one * 2);
							Shape line = new Line2D.Double(xStart, tmp_y, xStart
									+ (one * 10), tmp_y);

							strokeShapeMap.put(stroke, line);
						}
						if (strokeShapeMap.size() != 0)
						{
							y = tmp_y;

							boolean combineStrokeNames = strokeShapeMap.size() == 1;

							for (BasicStroke basicStroke : strokeShapeMap.keySet())
							{
								g.setStroke(basicStroke);
								g.draw(strokeShapeMap.get(basicStroke));
								Point2D.Float fpoint = strokeStringPointMap
										.get(basicStroke);
								if (!combineStrokeNames)
									g.drawString(strokeStringMap.get(basicStroke),
											fpoint.x, fpoint.y);
							}
							if (combineStrokeNames)
							{
								/*
								 * get first item position and draw combinedString there
								 */
								Point2D.Float fpoint = strokeStringPointMap
										.get(strokeShapeMap.keySet().iterator().next());
								g.drawString(
										Messages
												.getString("VennMakerView.CombinedRelationDashingText"),
										fpoint.x, fpoint.y);
							}

							s = a.getLabel()
									+ " "
									+ Messages
											.getString("VennMakerView.RelationDashing");
							y = y - (one * 3) * textZoom;
							g.drawString(s, (int) x + (one * 2), (int) y + one
									* textZoom);

							hasDrawnRelationDashing = true;
						}
					}
				}
			}
		}

		if (VennMaker.getInstance().getProject().getPaintLegendPolicy()
				.paintThickness())
		{
			/*
			 * Either has drawn dashing legend with already shifted; or has not
			 * drawn but color drawn -> so shift
			 */
			if (hasDrawnRelationDashing
					|| (hasDrawnRelationColor && !hasDrawnRelationDashing))
				y = y - (one * 2);
			/* size */
			for (AttributeType as : sizeA)
			{
				double tmp_y = y;

				HashMap<Float, Shape> sizeShapeMap = new HashMap<Float, Shape>();
				HashMap<Float, String> sizeStringMap = new HashMap<Float, String>();
				HashMap<Float, Point2D.Float> sizeStringPointMap = new HashMap<Float, Point2D.Float>();

				for (AttributeType a : relationMap.keySet())
				{
					if (a.equals(as) && isShowRelation(a))
					{
						for (Object o : relationMap.get(a))
						{
							float strokeSize = VennMakerView.getVmcs().toJava2D(
									(float) (n.getActiveRelationSizeVisualizer(as
											.getType()).getSize(o))
											/ VennMakerView.LINE_WIDTH_SCALE)
									/ VennMakerView.LINE_WIDTH_SCALE;

							s = "" + o;
							tmp_y = tmp_y - (one * 3) * textZoom;
							sizeStringMap.put(strokeSize, s);
							sizeStringPointMap.put(strokeSize, new Point2D.Float(
									(int) x + (one * 6), (int) tmp_y + one * textZoom));

							double stringWidth = fm.stringWidth(s);
							double xStart = x + (one * 6) + stringWidth + (one * 2);
							Shape line = new Line2D.Double(xStart, tmp_y, xStart
									+ (one * 10), tmp_y);

							sizeShapeMap.put(strokeSize, line);
						}
						if (sizeShapeMap.size() != 0)
						{

							y = tmp_y;

							boolean combineRelationThickness = sizeShapeMap.size() == 1;

							for (float f : sizeShapeMap.keySet())
							{
								g.setStroke(new BasicStroke(f));
								g.draw(sizeShapeMap.get(f));

								Point2D.Float fpoint = sizeStringPointMap.get(f);
								if (!combineRelationThickness)
									g.drawString(sizeStringMap.get(f), fpoint.x,
											fpoint.y);
							}
							if (combineRelationThickness)
							{
								Point2D.Float fpoint = sizeStringPointMap
										.get(sizeShapeMap.keySet().iterator().next());
								g.drawString(
										Messages
												.getString("VennMakerView.CombinedRelationThicknessText"),
										fpoint.x, fpoint.y);
							}

							g.setStroke(new BasicStroke());

							s = a.getLabel()
									+ " "
									+ Messages
											.getString("VennMakerView.RelationThickness");
							y = y - (one * 3) * textZoom;
							g.drawString(s, (int) x + (one * 2), (int) y + one
									* textZoom);

							hasDrawnRelationThickness = true;
						}
					}
				}
			}
		}
		/* Relational Attributes */
		if (s != null
				&& (hasDrawnRelationColor || hasDrawnRelationDashing || hasDrawnRelationThickness))
		{
			s = Messages.getString("VennMakerView.1");
			y = y - (one * 5) * textZoom;
			g.drawString(s, (int) x, (int) y + (one * 2) * textZoom);
		}
	}

	/**
	 * creates the different actorSizes for the legend of the given Network n
	 * 
	 * @param n
	 */
	private void createSizesWithCaptions(Netzwerk n)
	{
		this.bes.clear();
		this.sizes.clear();

		AttributeType at = n.getActorSizeVisualizer().getAttributeType();
		for (Akteur a : n.getAkteure())
		{
			String actorBes = (String) a.getAttributeValue(at, n);
			if (actorBes == null)
				actorBes = ""; //$NON-NLS-1$

			int actorSize = n.getActorSizeVisualizer().getSize(a, n);

			boolean contains = false;

			for (String s : bes)
			{
				if (s.equals(actorBes))
					contains = true;
			}

			if (!contains)
			{
				int pos = -1;
				Integer newSize = new Integer(actorSize);

				for (int u = 0; u < sizes.size(); u++)
				{
					if (sizes.get(u).intValue() > actorSize)
					{
						pos = u;
						break;
					}
				}

				/*
				 * wenn alle vorhandenen kleiner sind werden "bez" und "newI" am
				 * jeweiligen Ende eingefuegt
				 */
				if (pos == -1)
				{
					sizes.add(newSize);
					bes.add(actorBes);
				}
				else
				/* Sonst werden sie an die richtige Stelle einsortiert */
				{
					Integer intBefore = newSize;
					String stringBefore = actorBes;
					Integer intNow;
					String stringNow;

					for (int u = pos; u < sizes.size(); u++)
					{
						intNow = sizes.get(u);
						stringNow = bes.get(u);

						sizes.set(u, intBefore);
						bes.set(u, stringBefore);

						intBefore = intNow;
						stringBefore = stringNow;
					}

					sizes.add(intBefore);
					bes.add(stringBefore);
				}
			}
		}

	}

	/**
	 * paints THIS legend - static method refers to the "standard"-legend
	 */
	public void paintLegend(Graphics2D g)
	{
		/*
		Netzwerk n = VennMaker.getInstance().getProject().getCurrentNetzwerk();
		if (n != null)			
		paintLegend(g, VennMaker.getInstance().getProject().getCurrentNetzwerk());
		*/
		paintLegend(g, this.netzwerk);
	}

	public boolean isShowActorSizes()
	{
		return showActorSizes;
	}

	public void setShowActorSizes(boolean showActorSizes)
	{
		this.showActorSizes = showActorSizes;
	}

	public boolean isShowActorSymbols()
	{
		return showActorSymbols;
	}

	public void setShowActorSymbols(boolean showActorSymbols)
	{
		this.showActorSymbols = showActorSymbols;
	}

	public boolean isShowActorPies()
	{
		return showActorPies;
	}

	public void setShowActorPies(boolean showActorPies)
	{
		this.showActorPies = showActorPies;
	}

	public boolean isShowRelation(AttributeType at)
	{
		if (showRelations.get(at) == null)
			showRelations.put(at, true);

		return showRelations.get(at);
	}

	public void setShowRelations(HashMap<AttributeType, Boolean> drawRelations)
	{
		this.showRelations = drawRelations;
	}
}
