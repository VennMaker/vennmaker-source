/**
 * 
 */
package gui;


import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Collection;

import data.Akteur;
import data.EventProcessor;
import data.Netzwerk;
import data.Relation;
import events.MoveActorEvent;

class Node
{
	double			x;

	double			y;

	double			xv;

	double			yv;

	double			dx;

	double			dy;

	boolean			fixed;

	int				id;

	String			name;

	Akteur			actor;

}

class Edge
{
	int		from;

	int		to;

	double	len;
}

public class SpringEmbedder
{

	int								nnodes;

	Node[]							nodes;

	int								nedges;

	Edge[]							edges;

	Netzwerk							network;

	/**
	 * siehe auch: http://www.inf.uni-konstanz.de/algo/lehre/ss04/gd/demo.html
	 */
	private static final long	serialVersionUID	= 1L;

	public void useSpringEmbedder(Collection<Akteur> actors, Netzwerk network)
	{

		int abstand = 150;
		int number_actors = 0;
		int number_relations = 0;

		this.network = network;

		for (Akteur akteur : actors)
		{
			boolean connected = false;

			if (this.network.getFilter(akteur) == false)
			{
				for (final Relation relation : akteur.getRelations(network))
					if (this.network.getAkteure().contains(relation.getAkteur()))
					{
						Akteur arrowAkteur = relation.getAkteur();
						if (this.network.getFilter(arrowAkteur) == false)
						{
							number_relations = number_relations + 2;
							connected = true;
						}
					}
			}
			if (connected == true)
				number_actors = number_actors + 2;
		}
		
		
	/*	  Node nodes[] = new Node[number_actors]; 
		  Edge edges[] = new Edge[number_relations];
		  
		  nodes = new Node[number_actors + 1]; 
		  edges = new Edge[number_relations + 1];
		*/ 

		//TODO: Achtung: fixe Groesse ist hier vorgegeben:
	this.nodes = new Node[500];
	this.edges = new Edge[500];

		// Add nodes and edges to the internal arrays
		for (Akteur akteur : actors)
		{

			if (network.getFilter(akteur) == false)
			{
				for (final Relation relation : akteur.getRelations(network))
					if (network.getAkteure().contains(relation.getAkteur()))
					{
						Akteur arrowAkteur = relation.getAkteur();
						if (network.getFilter(arrowAkteur) == false)
						{

							addEdge(akteur, arrowAkteur, abstand);
						//	addEdge(arrowAkteur, akteur, abstand);

						}
					}
			}
		}

		//-------------------

		for (int i = 0; i < 1000; i++)
			relax();

		verschieben();

		for (int i = 0; i < nnodes; i++)
		{
			if (this.nodes[i] != null)
			{
				double x = this.nodes[i].xv;
				double y = this.nodes[i].yv;

				MoveActorEvent moveActorEvent = new MoveActorEvent(this.nodes[i].actor,
						network, new Point2D.Double(x
								- this.nodes[i].actor.getLocation(network).getX(), y
								- this.nodes[i].actor.getLocation(network).getY()));
				EventProcessor.getInstance().fireEvent(moveActorEvent);
			}
		}

	}

	private int findNode(Akteur lbl)
	{
		for (int i = 0; i < nnodes; i++)
		{
			if (this.nodes[i].id == lbl.getId())
			{
				return i;
			}
		}
		return addNode(lbl);
	}

	private int addNode(Akteur lbl)
	{
		Node n = new Node();
		n.x = -5000 + (10000 * Math.random());
		n.y = -5000 + (10000 * Math.random());
		n.id = lbl.getId();
		n.name = lbl.getName();
		n.actor = lbl;
		this.nodes[nnodes] = n;
		return nnodes++;
	}

	private void addEdge(Akteur from, Akteur to, int len)
	{
		Edge e = new Edge();
		e.from = findNode(from);
		e.to = findNode(to);
		e.len = len;
		this.edges[nedges++] = e;
	}

	/**
	 * Auf Bildschirm einpassen
	 */
	synchronized void verschieben()
	{

		double max_x = 0;
		double max_y = 0;

		double min_x = 0;
		double min_y = 0;

		double diff_x;
		double diff_y;
		double fak_x;
		double fak_y;

		double max_groesse = 0;

		int height = (int) (VennMaker.getInstance().getProject()
				.getViewAreaHeight());

		int width = (int) (VennMaker.getInstance().getProject()
				.getViewAreaHeight() * VennMaker.getInstance().getConfig()
				.getViewAreaRatio());

		for (int i = 0; i < nnodes; i++)
		{
			Node n = this.nodes[i];

			if (n.x >= max_x)
				max_x = n.x;
			if (n.y >= max_y)
				max_y = n.y;

			if (n.x < min_x)
				min_x = n.x;
			if (n.y < min_y)
				min_y = n.y;

			// maximale Akteursgroesse herausfinden
			if (n.actor.getGroesse(this.network) > max_groesse)
				max_groesse = n.actor.getGroesse(this.network)*2;
		}

		// Hoehe und Breite des Zeichenbereiches setzen
		Dimension d = new Dimension((width - (int) max_groesse),
				(height - (int) max_groesse));

		// Minimum herausfinden
		min_x = max_x;
		min_y = max_y;
		for (int i = 0; i < this.nnodes; i++)
		{
			Node n = this.nodes[i];
			if (!n.fixed)
			{
				if (n.x < min_x)
					min_x = n.x;
				if (n.y < min_y)
					min_y = n.y;
			}

		}

		for (int i = 0; i < nnodes; i++)
		{
			Node n = this.nodes[i];

			if (min_x < max_x)
				n.xv = n.x - min_x;
			else
				n.xv = n.x - max_x;

			if (min_y < max_y)
				n.yv = n.y - min_y;
			else
				n.yv = n.y - max_y;

			diff_x = max_x - min_x;
			diff_y = max_y - min_y;

			fak_x = diff_x / d.width;
			fak_y = diff_y / d.height;

			n.xv = n.xv / fak_x;
			n.yv = n.yv / fak_y;

			n.xv = -(d.width / 2) + n.xv;
			n.yv = -(d.height / 2) + n.yv;

		}
	}

	/**
	 * Kraefte berechnen und anwenden
	 */
	synchronized void relax()
	{
		for (int i = 0; i < nedges; i++)
		{
			Edge e = edges[i];
			double vx = this.nodes[e.to].x - this.nodes[e.from].x;
			double vy = this.nodes[e.to].y - this.nodes[e.from].y;
			double len = Math.sqrt(vx * vx + vy * vy);
			len = (len == 0) ? .0001 : len;
			double f = (edges[i].len - len) / (len * 50);
			double dx = f * vx;
			double dy = f * vy;

			this.nodes[e.to].dx += dx;
			this.nodes[e.to].dy += dy;
			this.nodes[e.from].dx += -dx;
			this.nodes[e.from].dy += -dy;
		}

		for (int i = 0; i < nnodes; i++)
		{
			Node n1 = this.nodes[i];
			double dx = 0;
			double dy = 0;

			for (int j = 0; j < nnodes; j++)
			{
				if (i == j)
				{
					continue;
				}
				Node n2 = this.nodes[j];
				double vx = n1.x - n2.x;
				double vy = n1.y - n2.y;
				double len = vx * vx + vy * vy;
				if (len == 0)
				{
					dx += Math.random() * 10;
					dy += Math.random() * 10;
				}
				else if (len < 100 * 100 * 10)
				{
					dx += vx / len;
					dy += vy / len;
				}
			}
			double dlen = dx * dx + dy * dy;
			if (dlen > 0)
			{
				dlen = Math.sqrt(dlen) / 2;
				n1.dx += dx / dlen;
				n1.dy += dy / dlen;
			}
		}

		for (int i = 0; i < nnodes; i++)
		{
			Node n = this.nodes[i];
			if (!n.fixed)
			{
				n.x += Math.max(-5, Math.min(5, n.dx));
				n.y += Math.max(-5, Math.min(5, n.dy));

			}

			n.dx /= 2;
			n.dy /= 2;
		}
	}

}
