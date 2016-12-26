/**
 * 
 */
package data;


import java.util.EventListener;

import events.MediaEvent;

/**
 * Interface fuer die Listener fuer Audio- und Bildwiedergabe
 */
public interface MediaListener extends EventListener {

	 void action( MediaEvent e ); 
	
}
