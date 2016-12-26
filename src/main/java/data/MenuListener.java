/**
 * 
 */
package data;


import java.util.EventListener;

import events.MenuEvent;

/**
 * 
 * Interface fuer die Listener fuer Audio- und Bildwiedergabe
 */
public interface MenuListener extends EventListener {

	 void action( MenuEvent e ); 
	
}
