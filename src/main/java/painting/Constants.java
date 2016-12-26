package painting;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * stores all constants to paint VennMakerView (e.g. LineWidth of Relations
 * 
 * 
 * 
 */
public class Constants
{
	private static final BasicStroke	STANDARD_STROKE				= new BasicStroke(
																							1.0f);

	/**
* 
*/
	public static final int				BORDER_SAFETY_PADDING		= 25;

	/**
* 
*/
	public static final Color			COLOR_LABEL						= new Color(64,
																							64, 64);

	/**
* 
*/
	public static final Color			COLOR_LABEL_BOX				= new Color(224,
																							224, 224,
																							200);

	public static final Color			COLOR_LABEL_BOX_EGO			= new Color(180,
																							224, 224,
																							200);

	/**
* 
*/
	public static final Color			COLOR_CONCENTRIC_CIRCLES	= new Color(92,
																							92, 92);

	/**
* 
*/
	public static final Color			COLOR_SECTOR_LINE				= new Color(150,
																							150, 150);

	/**
	 * Hintergrundfarbe des inaktiven Bereiches um die Zeichenfläche.
	 */
	public static final Color			COLOR_BACKGROUND				= new Color(235,
																							235, 235);

	public static final long			serialVersionUID				= 1L;

	/**
	 * Die Breite der Kanten wird gemäß aktueller Skalierung berechnet,
	 * zusätzlich wird dieser Korrekturfaktor angewendet, damit die Linien in den
	 * nicht-skalierten Bereichen (Buttons) in etwa mit denen auf der
	 * Zeichenfläche harmonieren.
	 */
	public static final int				LINE_WIDTH_SCALE				= 6;

	/**
	 * additional space between multiple relations
	 */
	public static final int				RELATION_DISTANCE				= 2;

}
