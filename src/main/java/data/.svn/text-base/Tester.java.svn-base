/**
 * 
 */
package data;

import events.AddRelationEvent;
import events.ComplexEvent;
import events.NewActorEvent;
import gui.Messages;
import gui.VennMaker;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

/**
 * 
 * 
 */
public class Tester
{

	/**
	 * Erzeugt zufällig Akteure, Relationen und Attributauswahl
	 */
	public void createActorsAndRelations()
	{

		final Netzwerk network = VennMaker.getInstance().getProject()
				.getCurrentNetzwerk();

		Vector<AttributeType> tempTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		Vector<AttributeType> tempTypeList;

		Vector<AttributeType> tempActorTypeList;

		Random randomGenerator = new Random();

		for (int q = 0; q < 10; q++)
		{

			// Akteur erzeugen

			Akteur actor = new Akteur(""); //$NON-NLS-1$
			actor.setName("TestActor" + actor.getId());

			// Attributwerte auswaehlen und setzen
			tempActorTypeList = new Vector<AttributeType>();

			for (AttributeType tempType : tempTypes)
			{
				if (tempType.getType().equals("ACTOR") //$NON-NLS-1$
						&& (!tempActorTypeList.contains(tempType.getType())))
				{
					if (tempType.getPredefinedValues() != null)
						tempActorTypeList.add(tempType);
				}
			}

			//Attribut und -wert zufaellig auswaehlen
			AttributeType actor_attrib = tempActorTypeList.get(randomGenerator
					.nextInt(tempActorTypeList.size()));
			Object actor_value = actor_attrib.getPredefinedValue(randomGenerator
					.nextInt(actor_attrib.getPredefinedValues().length));

			actor.setAttributeValue(actor_attrib, network, actor_value);

			// Akteursposition setzen
			Point2D point = new Point2D.Double((-100 + randomGenerator
					.nextInt(200)), (-100 + randomGenerator.nextInt(200)));
			actor.setLocation(network, point);

			// Akteur in Netzwerkkarte einfuegen und Event ausloesen

			network.addAkteur(actor);

			ComplexEvent event = new ComplexEvent(Messages
					.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
			event.addEvent(new NewActorEvent(actor));
			EventProcessor.getInstance().fireEvent(event);

			// Relationen erzeugen und Attributwert setzen
			tempTypeList = new Vector<AttributeType>();

			for (AttributeType tempType : tempTypes)
			{
				if (!tempType.getType().equals("ACTOR") //$NON-NLS-1$
						&& (!tempTypeList.contains(tempType.getType()))
						&&
						( tempType.getPredefinedValues()!=null)
						)
				{
					tempTypeList.add(tempType);
				}
			}

			for (Akteur a : network.getAkteure())

				if ((randomGenerator.nextInt(100) >= 90) //Grad der Vernetzung
						&& (a.getId() != actor.getId()))
				{

					//Attribut und -wert zufaellig auswaehlen
					AttributeType rel_attrib = tempTypeList.get(randomGenerator
							.nextInt(tempTypeList.size()));
					Object value = rel_attrib.getPredefinedValue(randomGenerator
							.nextInt(rel_attrib.getPredefinedValues().length));

					EventProcessor.getInstance().fireEvent(
							new AddRelationEvent(actor, network, new Relation(network,
									a, rel_attrib, value)));
				}

		}
	}
}
