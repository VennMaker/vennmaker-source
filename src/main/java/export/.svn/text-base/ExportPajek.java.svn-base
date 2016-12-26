package export;

import gui.VennMaker;

import java.util.ArrayList;
import java.util.List;

import data.Akteur;
import data.AttributeType;
import data.Netzwerk;
import data.Relation;

/**
 * Export network data into a pajek net file
 * 
 * 
 * 
 */
public class ExportPajek implements ExportData
{

	Netzwerk	network	= null;

	String	nl			= "\n";

	/**
	 * Set the network
	 * 
	 * @param network
	 */
	@Override
	public void setNetwork(Netzwerk n)
	{
		this.network = n;
	}

	private String createOutput()
	{

		List<Vertice> vertices = new ArrayList<Vertice>();
		int relAttNr = 1;
		String output = "";

		int vID = 1;

		if (this.network == null)
			this.network = VennMaker.getInstance().getProject()
					.getCurrentNetzwerk();

		// Vertice-Liste anlegen
		for (final Akteur actor : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((this.network.getAkteure().contains(actor)))
			{
				vertices.add(new Vertice(vID, actor));
				vID++;
			}
		}

		// Edge-Liste anlegen

		// Output erzeugen

		output += "*Vertices " + vertices.size() + nl;

		for (Vertice v : vertices)
		{
			output += v.getvID() + " " + "\"" + v.getActor().getName() + "\"" + nl;
		}

		// die Relationsgruppen durch gehen
		for (String relGroup : VennMaker.getInstance().getProject()
				.getAttributeCollectors())
		{
			// die einzelnen relationalen Attribute einer Relationsgruppe
			// durchgehen
			for (AttributeType relAtt : VennMaker.getInstance().getProject()
					.getAttributeTypes(relGroup))
			{

				if (VennMaker.getInstance().getProject().getIsDirected(relGroup))
				{
					output += "*Arcs :" + relAttNr + " \"" + relAtt.getLabel()
							+ "\"";
				}
				else
					output += "*Edges :" + relAttNr + " \"" + relAtt.getLabel()
							+ "\"";

				output += nl;

				relAttNr++;

				for (Vertice v : vertices)
				{
					for (Relation targetRel : v.getActor()
							.getRelations(this.network))
					{

						// wenn relation zur gleichen relationsgruppe gehoert
						if (targetRel.getAttributeCollectorValue().equals(relGroup))
						{
							// wenn die relation zum target das relationsattribut der
							// relationsgruppe enthaelt
							if (targetRel.getAttributes(this.network).containsKey(
									relAtt))
							{
								for (Vertice vTarget : vertices)
								{

									if (targetRel.getAkteur().getId() == vTarget
											.getActor().getId())
									{
							
								int weight = 1;
								if (relAtt.getPredefinedValues()!=null){
									weight = relAtt.getPredefinedValueCode((String)targetRel.getAttributeValue(relAtt,this.network));
								}
									
										output += v.getvID() + " " + vTarget.getvID() + " " + weight 
												+ nl;

										// reziprok, wenn ungerichtete Beziehung
										if (!VennMaker.getInstance().getProject()
												.getIsDirected(relGroup))
										{
											output += vTarget.getvID() + " " + v.getvID() + " " + weight
													+ nl;
										}

										break;
									}
								}
							}
						}
					}
				}

			}

		}

		return output;

	}

	@Override
	public String toString()
	{

		return this.createOutput();

	}

	class Vertice
	{

		Akteur	actor;

		int		vID;

		Vertice(int id, Akteur a)
		{
			this.actor = a;
			this.vID = id;
		}

		public int getvID()
		{
			return this.vID;
		}

		public Akteur getActor()
		{
			return this.actor;
		}
	}

}
