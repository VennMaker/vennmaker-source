package gui;

import gui.VennMaker.ViewMode;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import data.Akteur;
import data.Relation;

/**
 * Class which stores and contains informations about mouse events of each
 * VennMakerView. Depending on this context the VennMakerViewMouseController
 * decides what to do...
 * 
 * (for cleaning up the VennMakerViewMouseController)
 * 
 * 
 * 
 */
public class VennMakerViewMouseContext
{
	/**
	 * Where did a press or release happen? Actor,Relation,EmptySpace
	 */
	public static enum MouseAt
	{
		Actor, Relation, EmptySpace;
	}

	/**
	 * Which key was pressed / released Left,Right
	 */
	public static enum MouseKey
	{
		Left, Right;
	}

	private VennMakerView	view;

	private Akteur				pressedOnActor;

	private Akteur				releasedOnActor;

	private Relation			pressedOnRelation;

	private Relation			releasedOnRelation;

	private Point2D			pressedLocation;

	private Point2D			releasedLocation;

	private MouseAt			mPa;											// Mouse Pressed
																					// At

	private MouseAt			mRa;											// Mouse Released
																					// At
																					// --> Maybe
																					// important in
																					// future

	private MouseKey			mk;											// Mouse Key

	private boolean			pressedInMultiSelection		= false;

	private boolean			firstMultiSelection			= true;

	/**
	 * Der Akteur, der grade im Tooltip erläutert wird.
	 */
	private Akteur				tooltipAkteur;

	/**
	 * Der Akteur, auf den der Mauszeiger zuletzt zeigte.
	 */
	private Akteur				lastHoverAkteur;

	/**
	 * Timestamp, wann der Mauszeiger zuletzt auf einen anderen Akteur zeigte.
	 */
	private long				lastHoverChange;

	/**
	 * Die Relation, die gerade im Tooltip erlÃ¤utert wird.
	 */
	private Relation			tooltipRelation;

	/**
	 * Die Relation, auf die der Mauszeiger zuletzt zeigte.
	 */
	private Relation			lastHoverRelation;

	/**
	 * save last mousereleasetime to realize doubleclick.
	 */
	private long				releaseTime						= 0;

	/**
	 * defines, if there had been a double click lately;
	 */
	private boolean			doubleclicked					= false;

	/**
	 * Actorcopy solely to check, if one clicked it before, to check if it's a
	 * doubleclick; needed to let the reset()-function take care of
	 * releasedOnActorvariable
	 */
	private Akteur				doubleClickCheckActor		= null;

	/** relationcopy to check for doubleclick on this relation */
	private Relation			doubleClickCheckRelation	= null;

	public VennMakerViewMouseContext(VennMakerView owner)
	{
		this.view = owner;
	}

	public Akteur getPressedOnActor()
	{
		return pressedOnActor;
	}

	public Akteur getReleasedOnActor()
	{
		return releasedOnActor;
	}

	public Relation getPressedOnRelation()
	{
		return pressedOnRelation;
	}

	public Point2D getPressedLocation()
	{
		return pressedLocation;
	}

	/**
	 * 
	 * @return a MouseAt value where the mouse was pressed at (Actor, Relation,
	 *         EmptySpace)
	 */
	public MouseAt mousePressedAt()
	{
		return mPa;
	}

	/**
	 * Should be called FIRST when mousePressed event is fired in
	 * VennMakerViewMouseController
	 */
	public void mousePressed(MouseEvent e)
	{
		initKeyUsed(e);
		initPressLocation(e);
	}

	/**
	 * Should be called FIRST when mouseReleased event is fired in
	 * VennMakerViewMouseController
	 */
	public void mouseReleased(MouseEvent e)
	{
		initReleasedLocation(e);
	}

	/**
	 * Should be called FIRST when mouseMoved event is fired in
	 * VennMakerViewMouseController
	 */
	public void mouseMoved(final MouseEvent e)
	{

		Akteur akteur = view.searchAkteur(e.getX(), e.getY());

		if (akteur != null && akteur != tooltipAkteur)
		{	
			if (isActorDraggable(akteur))
				((VennMakerView)e.getSource()).useMovableCursor();
			// neuer Tooltip koennte bald kommen
			if (akteur != lastHoverAkteur)
			{
				lastHoverAkteur = akteur;
				lastHoverChange = System.currentTimeMillis();
			}
			else if (System.currentTimeMillis() - lastHoverChange >= 1000)
			{
				tooltipAkteur = akteur;
				view.tooltip(e.getPoint(), akteur);
			}
		}
		else if (akteur == null)
		{
			((VennMakerView)e.getSource()).useDefaultCursor();
			if (tooltipAkteur != null) {
				// Tooltip ausblenden
				view.hideTooltip();
				tooltipAkteur = null;
				lastHoverAkteur = null;
				lastHoverChange = System.currentTimeMillis();
			}
		}
		else if (akteur != null && akteur == tooltipAkteur)
		{
			// Tooltip auf gleichem Akteur, aktualisiere nur Position
			view.tooltip(e.getPoint(), akteur);
		}

		// Relation relation = view.searchRelation(e.getX(), e.getY());
		Relation relation = view.searchRelationPerf(e.getX(), e.getY());

		if (relation != VennMakerView.currentHoverRelation)
		{
			VennMakerView.currentHoverRelation = relation;
			view.repaint();
		}
		else if ((relation != VennMakerView.currentHoverRelation)
				&& (VennMakerView.currentHoverRelation != null))
		{
			VennMakerView.currentHoverRelation = null;
			view.repaint();
		}
		else if (relation != null && relation != tooltipRelation)
		{

			if (relation != lastHoverRelation)
			{
				lastHoverRelation = relation;
				lastHoverChange = System.currentTimeMillis();
			}
			else if (System.currentTimeMillis() - lastHoverChange >= 1000) //$NON-NLS-1$
			{
				tooltipRelation = relation;
				view.tooltip(e.getPoint(), relation);
			}
		}
		else if (relation == null && tooltipRelation != null)
		{
			// Tooltip ausblenden
			view.hideTooltip();
			tooltipRelation = null;
			lastHoverRelation = null;
			lastHoverChange = System.currentTimeMillis();
		}
		else if (relation != null && relation == tooltipRelation) //$NON-NLS-1$
		{
			// Tooltip auf gleicher Relation, aktualisiere nur Position
			view.tooltip(e.getPoint(), relation);
		}

	}

	/**
	 * Use this Method to completely reset the Context
	 */
	public void reset()
	{
		pressedOnActor = null;
		pressedOnRelation = null;

		releasedOnActor = null;
		releasedOnRelation = null;

		pressedLocation = null;
		releasedLocation = null;

		doubleclicked = false;

		mPa = null;
		mk = null;
		mRa = null;

		view.setInteractiveSelection(null, null); // reset interactive selection
		view.setInteractiveRelation(null, 0, 0); // reset interactive relation
		//setTemporay Actor only if alter node mode is activated
		if (VennMaker.getInstance().getActualButton()==VennMaker.ViewMode.ALTER_NODES){
		view.setTemporaryActors(null);
		}
		view.repaint();
	}

	private void initPressLocation(MouseEvent e)
	{
		Point p = e.getPoint();
		pressedLocation = p;
		pressedOnActor = view.searchAkteur(p.x, p.y);
		Rectangle2D.Double multiSelection = view.getCurrentMultiSelection();
		if (pressedOnActor != null)
		{
			initSingleSelection();
		}
		else if (mk == MouseKey.Left && multiSelection != null
				&& multiSelection.contains(p))
		{
			pressedInMultiSelection = true;
		}
		else
		{
			initSingleSelection();
		}
	}

	private void initSingleSelection()
	{
		firstMultiSelection = true;
		pressedInMultiSelection = false;
		view.setInteractiveSelection(null, null);

		//setTemporay Actor only if alter node mode is activated
		if (VennMaker.getInstance().getActualButton()==VennMaker.ViewMode.ALTER_NODES){
		ArrayList<Akteur> tmpActors = null;
					
		if (pressedOnActor != null)
		{
			tmpActors = new ArrayList<Akteur>();
			tmpActors.add(pressedOnActor);
		}
		view.setTemporaryActors(tmpActors);

		}
		
		pressedOnRelation = view.searchRelationPerf(
				(int) Math.round(pressedLocation.getX()),
				(int) Math.round(pressedLocation.getY()));

		if (pressedOnActor == null && pressedOnRelation == null)
			mPa = MouseAt.EmptySpace;
		else if (pressedOnActor != null)
			mPa = MouseAt.Actor;
		else if (pressedOnRelation != null)
			mPa = MouseAt.Relation;
	}

	private void initKeyUsed(MouseEvent e)
	{
		switch (e.getButton())
		{
			case MouseEvent.BUTTON1:
				mk = MouseKey.Left;
				break;
			case MouseEvent.BUTTON3:
				mk = MouseKey.Right;
				break;
			default:
				mk = null;
		}
	}

	private void initReleasedLocation(MouseEvent e)
	{
		Point p = e.getPoint();

		releasedLocation = p;

		releasedOnActor = view.searchAkteur(p.x, p.y);

		releasedOnRelation = view.searchRelationPerf(p.x, p.y);

		if ((releasedOnActor != null)
				&& releasedOnActor.equals(doubleClickCheckActor)
				&& ((System.currentTimeMillis() - releaseTime) < 300))
		{
			doubleclicked = true;
			doubleClickCheckActor = null;
		}

		if ((releasedOnRelation != null)
				&& releasedOnRelation.equals(doubleClickCheckRelation)
				&& ((System.currentTimeMillis() - releaseTime) < 300))
		{
			doubleclicked = true;
			doubleClickCheckRelation = null;
		}

		doubleClickCheckRelation = releasedOnRelation;
		doubleClickCheckActor = releasedOnActor;

		//setTemporay Actor only if alter node mode is activated
		if (VennMaker.getInstance().getActualButton()==VennMaker.ViewMode.ALTER_NODES){
		// get temp actors, because if multi actor drag is running, we have to set
		// them again
		// after finishing this drag by setTemporaryActors(null)
		// to save the all selected actors
		List<Akteur> tmpActors = view.getTemporaryActors();

		view.setTemporaryActors(null); // this finish one drag (multi or single
													// actor)

		if (pressedInMultiSelection) // here the old actors will be restored if
												// multi selection is running
			view.setTemporaryActors(tmpActors);

		}
		
		if (releasedOnActor == null && releasedOnRelation == null)
			mRa = MouseAt.EmptySpace;
		else if (releasedOnActor != null)
			mRa = MouseAt.Actor;
		else if (releasedOnRelation != null)
			mRa = MouseAt.Relation;

		releaseTime = System.currentTimeMillis();
	}

	/**
	 * 
	 * @return true if View Mode is set To "Node"
	 */
	private boolean isViewModeSetToNode()
	{
		return VennMaker.getInstance().getActualButton()
				.equals(ViewMode.ALTER_NODES);
	}

	/**
	 * 
	 * @return true if left mouse key was pressed on an actor
	 */
	private boolean pressedLeftOnActor()
	{
		return pressedOnActor != null && mk == MouseKey.Left;
	}

	/**
	 * @return true if left mouse button was pressed twice on same actor
	 */
	public boolean doubleClickOnActor()
	{
		return (doubleclicked && (releasedOnActor != null));
	}

	/**
	 * @return true if left mouse button was pressed twice on same relation
	 */
	public boolean doubleClickOnRelation()
	{
		return (doubleclicked && (releasedOnRelation != null));
	}

	/**
	 * 
	 * @return true if an actor currently is dragged (if asked from
	 *         mouseDragged())
	 */
	public boolean dragActor()
	{
		boolean egoMovable = VennMaker.getInstance().getConfig().isEgoMoveable();

		boolean notEgo = (pressedOnActor == null || pressedOnActor.getId() != VennMaker
				.getInstance().getProject().getEgo().getId());
		return pressedLeftOnActor() && isViewModeSetToNode()
				&& (notEgo || egoMovable);
	}
	
	/**
	 * check if an actor is draggable
	 * 
	 * @param a {Aktuer} actor element
	 * @return
	 */
	public boolean isActorDraggable(Akteur a) {
		boolean egoMovable = VennMaker.getInstance().getConfig().isEgoMoveable();
		boolean notEgo = (a == null || a.getId() != VennMaker
				.getInstance().getProject().getEgo().getId());
		return notEgo || egoMovable;
	}

	/**
	 * 
	 * @return true if an actor currently is dragged (if asked from
	 *         mouseDragged())
	 */
	public boolean dragRelation()
	{
		return pressedLeftOnActor() && !isViewModeSetToNode();
	}

	/**
	 * 
	 * @return true if mouse was pressed in EmptySpace
	 */
	public boolean emptyPress()
	{
		return mPa == MouseAt.EmptySpace;
	}

	/**
	 * 
	 * @return true if mouse was LEFT pressed in empty space
	 */
	public boolean emptyLeftPress()
	{
		return emptyPress() && mk == MouseKey.Left;
	}

	/**
	 * 
	 * @return true if mouse was RIGHT pressed in empty space
	 */
	public boolean emptyRightPress()
	{
		return emptyPress() && mk == MouseKey.Right;
	}

	/**
	 * 
	 * @return true if mouse was pressed on an actor
	 */
	public boolean actorPress()
	{
		return mPa == MouseAt.Actor;
	}

	/**
	 * 
	 * @return true if mouse was pressed on a relation
	 */
	public boolean relationPress()
	{
		return mPa == MouseAt.Relation;
	}

	/**
	 * 
	 * @return true if mouse was RIGHT pressed on an actor
	 */
	public boolean actorRightPress()
	{
		boolean egoPress = (pressedOnActor != view.getNetzwerk().getEgo() || !VennMaker
				.getInstance().getConfig().isEgoDisabled());
		return egoPress && actorPress() && mk == MouseKey.Right;
	}

	/**
	 * 
	 * @return true if mouse clicked LEFT on empty space
	 */
	public boolean emptyLeftClick()
	{
		return emptyLeftPress() && pressedLocation.equals(releasedLocation)
				&& !pressedInMultiSelection;
	}

	/**
	 * 
	 * @return true if a relation is completed (start actor, drag, end actor)
	 */
	public boolean completedRelation()
	{

		return dragRelation() && releasedOnActor != null
				&& !releasedOnActor.equals(pressedOnActor);
	}

	/**
	 * 
	 * @return true if there is a right mouse press on a relation
	 */
	public boolean relationRightPress()
	{
		return relationPress() && mk == MouseKey.Right;
	}

	/**
	 * 
	 * @return true if the MultiSelection is dragged
	 */
	public boolean dragMultiSelection()
	{
		return pressedInMultiSelection && mk == MouseKey.Left;
	}

	/**
	 * If Multiselection is done the first time (not dragged or something) an
	 * ActorMultiSelectionEvent has to be fired. With this method you can test if
	 * the current multiselection is first or only a dragged old one...
	 */
	public boolean isFirstMultiSelection()
	{
		boolean b = firstMultiSelection;
		firstMultiSelection = false;
		return b;
	}
}
