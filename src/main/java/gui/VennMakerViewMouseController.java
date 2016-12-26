package gui;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import data.Akteur;
import data.AttributeType;
import data.EventProcessor;
import data.Relation;
import events.ActorSelectionEvent;
import events.AddRelationEvent;
import events.SetAttributeEvent;

/**
 * MouseController provides basic mouse interaction methods. Creation of
 * objects, dragging of objects.
 * 
 * 
 * Mouseevents often depend on the current button that is clicked. The next
 * lines explain, which index is standing for which statement:
 * 
 * getActualButton() == 1 bedeutet, dass Button 'Akteur setzen' aktiv
 * getActualButton() == 2 bedeutet, dass Button 'Dimension' aktiv
 * getActualButton() == 3 bedeutet, dass Button 'Verschieben' aktiv
 * getActualButton() == 4 bedeutet, dass Button 'Pfeil zeichnen' aktiv
 * 
 */
public class VennMakerViewMouseController implements MouseMotionListener,
		MouseListener, MouseWheelListener
{
	private final VennMakerView					vennMakerView;

	private final VennMakerViewMouseContext	context;

	private final MouseMenuFactory				menuFactory;

	public VennMakerViewMouseController(VennMakerView vennMakerView)
	{
		this.vennMakerView = vennMakerView;
		this.context = new VennMakerViewMouseContext(vennMakerView);
		menuFactory = new MouseMenuFactory(vennMakerView);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		Akteur mouseOnActor = this.vennMakerView.searchAkteur(e.getX(), e.getY());
		if (mouseOnActor == vennMakerView.getNetzwerk().getEgo()
				&& VennMaker.getInstance().getConfig().isEgoResizable() == false)
			return;

		/**
		 * Methode verlassen wenn zu veränderndes Attribut schon durch Circles
		 * oder Sektoren festgelegt wird
		 */
		AttributeType actorSize = VennMaker.getInstance().getProject()
				.getCurrentNetzwerk().getActorSizeVisualizer().getAttributeType();
		if (actorSize.equals(vennMakerView.getNetzwerk().getHintergrund()
				.getCircleAttribute())
				|| actorSize.equals(vennMakerView.getNetzwerk().getHintergrund()
						.getSectorAttribute()))
			return;

		/**
		 * Methode verlassen wenn kein Trigger Attribut ausgewählt worden ist
		 */
		if (VennMaker.getInstance().getProject().getMainAttributeType("ACTOR") == null)
			return;

		if ((mouseOnActor != null)
				&& (e.getWheelRotation() > 0)
				&& !VennMaker
						.getInstance()
						.getProject()
						.getMainAttributeType("ACTOR")
						.isFirst(
								mouseOnActor.getAttributeValue(VennMaker.getInstance()
										.getProject().getMainAttributeType("ACTOR"),
										vennMakerView.getNetzwerk())))
		{
			mousewheelChangeAttributeValue(mouseOnActor, -1);
		}
		else if ((mouseOnActor != null)
				&& (e.getWheelRotation() < 0)
				&& !VennMaker
						.getInstance()
						.getProject()
						.getMainAttributeType("ACTOR")
						.isLast(
								mouseOnActor.getAttributeValue(VennMaker.getInstance()
										.getProject().getMainAttributeType("ACTOR"),
										vennMakerView.getNetzwerk())))
		{
			mousewheelChangeAttributeValue(mouseOnActor, 1);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{

	}

	@Override
	public void mouseExited(MouseEvent e)
	{

	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		context.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		context.mouseReleased(e);
		
		if (context.emptyLeftClick())
			addActorRoutine();
		else if (context.doubleClickOnActor())
			new EditAttributes(null, true, context.getReleasedOnActor(), VennMaker
					.getInstance().getProject().getCurrentNetzwerk(), "ACTOR");
		else if (context.doubleClickOnRelation())
			editAttributes();
		else if (context.completedRelation())
			addRelationRoutine();
		else if (context.emptyRightPress())
			popupMenuEmptySpace(e.getPoint());
		else if (context.actorRightPress())
			popupMenuActor(e.getPoint());
		else if (context.relationRightPress())
			popupMenuRelation(e.getPoint());
		

		if (vennMakerView.getCurrentMultiSelection() != null)
		{
			if (context.isFirstMultiSelection())
				EventProcessor.getInstance().fireEvent(
						new ActorSelectionEvent(vennMakerView
								.getCurrentMultiSelection()));
		}
		else
			context.reset();
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		if (context.dragActor())
		{
			Point2D pressedLocation = context.getPressedLocation();
			// Perform dragging
			final double dx = e.getPoint().getX()
					- (double) pressedLocation.getX();
			final double dy = e.getPoint().getY()
					- (double) pressedLocation.getY();	
			this.vennMakerView.setTemporaryMovement(dx, dy);
		}
		else if (context.dragRelation())
		{
			if (VennMaker.getInstance().getBeziehungsart() == null)
				return;

			this.vennMakerView.setInteractiveRelation(context.getPressedOnActor(),
					e.getPoint().getX(), e.getPoint().getY());
			this.vennMakerView.setInteractiveSelection(null, null);
		}
		// else if (context.dragMultiSelection())
		// {
		// Perform dragging
		// final double dx = e.getPoint().getX() NOT Possible in Version 1.3 --->
		// implemented in 1.4
		// - context.getPressedLocation().getX();
		// final double dy = e.getPoint().getY()
		// - context.getPressedLocation().getY();
		// this.vennMakerView.setTemporaryMovement(dx, dy);
		// }
		// else if (context.emptyLeftPress())
		// vennMakerView.setInteractiveSelection(context.getPressedLocation(),
		// e.getPoint());
	}

	public void mouseMoved(MouseEvent e)
	{
		context.mouseMoved(e);
	}

	/**
	 * Mousewheel Trigger aendert den Attributwert eines Akteurs und ruft dazu
	 * passende Events auf.
	 * 
	 * @param actor
	 * @param f
	 */
	private void mousewheelChangeAttributeValue(Akteur actor, int f)
	{
		EventProcessor.getInstance().fireEvent(
				new SetAttributeEvent(actor, VennMaker.getInstance().getProject()
						.getMainAttributeType("ACTOR"), vennMakerView.getNetzwerk(),
						f));
	}

	private void popupMenuActor(Point point)
	{
		JPopupMenu actorMenu = menuFactory.getActorMenu(context
				.getPressedOnActor());
		actorMenu.show(vennMakerView, (int) Math.round(point.getX()),
				(int) Math.round(point.getY()));
	}

	private void popupMenuEmptySpace(Point point)
	{
		JPopupMenu emptySpaceMenu = menuFactory.getEmptySpaceMenu();
		emptySpaceMenu.show(this.vennMakerView, (int) point.getX(),
				(int) point.getY());
	}

	private void popupMenuRelation(Point point)
	{
		JPopupMenu relationMenu = menuFactory.getRelationMenu(context
				.getPressedOnRelation());
		relationMenu.show(this.vennMakerView, (int) point.getX(),
				(int) point.getY());
	}

	/**
	 * edit attributes of the current relation
	 */
	private void editAttributes()
	{
		new EditAttributes(null, true, context.getPressedOnRelation(), VennMaker
				.getInstance().getProject().getCurrentNetzwerk(), context
				.getPressedOnRelation().getAttributeCollectorValue());
	}

	private void addRelationRoutine()
	{
		EventProcessor.getInstance().fireEvent(
				new AddRelationEvent(context.getPressedOnActor(), vennMakerView
						.getNetzwerk(), new Relation(
						this.vennMakerView.getNetzwerk(), context
								.getReleasedOnActor(), VennMaker.getInstance()
								.getBeziehungsart(), VennMaker.getInstance()
								.getBeziehungsauspraegung())));
	}

	private void addActorRoutine()
	{
		Point2D p = context.getPressedLocation();
		int x = (int) Math.round(p.getX());
		int y = (int) Math.round(p.getY());
		Akteur a = null;
		if (VennMaker.getInstance().getCurrentActor() != null)
		{
			a = this.vennMakerView.addActor(x, y);
			vennMakerView.getNetzwerk().removeTemporaryActor(a);
		}
		else if (VennMaker.getInstance().getMainGeneratorValue() != null)
		{

			if (VennMaker.getInstance().getProject().isEncodeFlag())
				new EditDataProtection();
			else
				a = this.vennMakerView.createNewActorByAttribute(x, y);
		}
		else
		{
			for (Akteur akteur : VennMaker.getInstance().getProject().getAkteure())
			{
				if (!vennMakerView.getNetzwerk().getAkteure().contains(akteur))
				{
					VennMaker.getInstance().setCurrentActor(akteur);
					a = this.vennMakerView.addActor(x, y);
					break;
				}
			}
		}
	}
}
