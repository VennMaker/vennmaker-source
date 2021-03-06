/**
 * 
 */
package data;

import java.awt.Color;
import java.util.Map;

import files.VMPaths;

/**
 * 
 * 
 *         Diese Klasse koordiniert die Zugriffe auf die jeweiligen Visualizer.
 *         (facade pattern)
 * 
 */
public class FacadeVisualizer
{

	private Object	symbolAttributeType;

	private Object	sizeAttributeType;

	public static enum Visualization
	{
		SYMBOL, SIZE, RELATIONCOLOR, RELATIONDASH, RELATIONSIZE
	}

	/**
	 * Verbindet den jeweiligen Visualizer mit einem neuen AttributeType
	 * 
	 * @param attributeType
	 * @param network
	 * @param visualizer
	 *           : SYMBOL, SIZE
	 * @param attributeCollector
	 *           TODO
	 */
	public void newVisualizerAttributeType(AttributeType attributeType,
			Netzwerk network, Visualization visualizer, String attributeCollector)
	{

		switch (visualizer)
		{
			case SYMBOL:
				this.symbolAttributeType = attributeType;
				network.getActorImageVisualizer().setAttributeType(attributeType);
				break;
			case SIZE:
				this.sizeAttributeType = attributeType;
				network.getActorSizeVisualizer().setAttributeType(attributeType);
				break;
			case RELATIONSIZE:
				this.sizeAttributeType = attributeType;
				network
						.getRelationSizeVisualizer(attributeCollector, attributeType)
						.setAttributeType(attributeType);
				break;
			case RELATIONCOLOR:
				this.sizeAttributeType = attributeType;
				network.getRelationColorVisualizer(attributeCollector,
						attributeType).setAttributeType(attributeType);
				break;
			case RELATIONDASH:
				this.sizeAttributeType = attributeType;
				network
						.getRelationDashVisualizer(attributeCollector, attributeType)
						.setAttributeType(attributeType);
				break;

			default:
				break;

		}

	}

	/**
	 * Aktualisiert die AttributeValues des jeweiligen Visualizer.
	 * 
	 * @param attributeType
	 *           the attribute type (e.g. ACTOR, ...)
	 * @param n
	 *           network
	 */
	public void updateVisualizerAttributeValues(AttributeType attributeType,
			Netzwerk n)
	{
		/*
		 * Alle Netzwerkkarten werden durchgegangen und es wird geprueft, ob der
		 * ImageVisualizer mit dem jeweiligen AttributeType zusammenhaengt.
		 */

		// ActorImageVisualizer aktualisieren
		if ((attributeType != null) && (attributeType.getType().equals("ACTOR")))
		{
			if (n.getActorImageVisualizer().getAttributeType()
					.equals(attributeType))
			{
				n.getActorImageVisualizer().setAttributeType(attributeType);

				Map<Object, String> i = n.getActorImageVisualizer().getImages();

				/*
				 * Ueberpruefen, ob alle AttributeValues mit einem AttributeImage
				 * assoziiert sind. Wenn nicht, dann wird ein Defaultimage
				 * eingesetzt.
				 */
				for (Object v : attributeType.getPredefinedValues())
					if (!i.containsKey(v))
						i.put(v, VMPaths.getCurrentWorkingDirectory()
								+ "/icons/Circle.svg");

				n.getActorImageVisualizer().setImages(i);
			}

			AttributeType aSize = n.getActorSizeVisualizer().getAttributeType();
			// ActorSizeVisualizer aktualisieren
			if (aSize != null && aSize.equals(attributeType))
			{
				n.getActorSizeVisualizer().setAttributeType(attributeType);

				Map<Object, Integer> i = n.getActorSizeVisualizer().getSizes();

				/*
				 * Ueberpruefen, ob alle AttributeValues mit einem AttributeSize
				 * assoziiert sind. Wenn nicht, dann wird ein DefaultSize
				 * eingesetzt.
				 */
				for (Object v : attributeType.getPredefinedValues())
					if (!i.containsKey(v))
						i.put(v, 20);

				n.getActorSizeVisualizer().setSizes(i);

			}
		}
		else if ((attributeType != null)
				&& (attributeType.getPredefinedValues() != null))
		{
			// RelationSizeVisualizer aktualisieren
			if (n.getRelationSizeVisualizer(attributeType.getType(), attributeType)
					.getAttributeType().equals(attributeType))
			{
				n.getRelationSizeVisualizer(attributeType.getType(), attributeType)
						.setAttributeType(attributeType);

				Map<Object, Integer> i = n.getRelationSizeVisualizer(
						attributeType.getType(), attributeType).getSizes();

				/*
				 * Ueberpruefen, ob alle AttributeValues mit einem AttributeSize
				 * assoziiert sind. Wenn nicht, dann wird ein DefaultSize
				 * eingesetzt.
				 */
				for (Object v : attributeType.getPredefinedValues())
					if (!i.containsKey(v))
						i.put(v, RelationSizeVisualizer.DEFAULT_SIZE);

				n.getRelationSizeVisualizer(attributeType.getType(), attributeType)
						.setSizes(i);

			}

			// RelationColorVisualizer aktualisieren
			if (n.getRelationColorVisualizer(attributeType.getType(),
					attributeType).getAttributeType().equals(attributeType))
			{
				n.getRelationColorVisualizer(attributeType.getType(), attributeType)
						.setAttributeType(attributeType);
				
				Map<Object, Color> i = n.getRelationColorVisualizer(
						attributeType.getType(), attributeType).getColors();

				/*
				 * Ueberpruefen, ob alle AttributeValues mit einem AttributeSize
				 * assoziiert sind. Wenn nicht, dann wird ein DefaultSize
				 * eingesetzt.
				 */
				for (Object v : attributeType.getPredefinedValues())
					if (!i.containsKey(v))
					{				
						i.put(v, RelationColorVisualizer.DEFAULT_COLOR);
					}
				
				n.getRelationColorVisualizer(attributeType.getType(), attributeType)
						.setColors(i);

			}

			// RelationDashVisualizer aktualisieren
			if (n.getRelationDashVisualizer(attributeType.getType(), attributeType)
					.getAttributeType().equals(attributeType))
			{
				n.getRelationDashVisualizer(attributeType.getType(), attributeType)
						.setAttributeType(attributeType);

				Map<Object, float[]> i = n.getRelationDashVisualizer(
						attributeType.getType(), attributeType).getDasharrays();

				/*
				 * Ueberpruefen, ob alle AttributeValues mit einem AttributeSize
				 * assoziiert sind. Wenn nicht, dann wird ein DefaultSize
				 * eingesetzt.
				 */
				for (Object v : attributeType.getPredefinedValues())
					if (!i.containsKey(v))
						i.put(v, RelationDashVisualizer.DEFAULT_DASHING);

				n.getRelationDashVisualizer(attributeType.getType(), attributeType)
						.setDasharrays(i);

			}
		}

	}

}
