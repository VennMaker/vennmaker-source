/**
 * 
 */
package events;

import java.util.EventObject;

import data.MediaObject;

/**
 * 
 * 
 * Eventr fuer die Audio- und Bildwiedergabe
 * 
 */
public class MediaEvent extends EventObject 
{ 
	  private MediaObject info; 
	 
	  /**
	   * 
	   * @param source the source object
	   * @param info MediaObject
	   */
	  public MediaEvent( Object source, MediaObject info) 
	  { 
	    super( source ); 
	    this.info = info; 
	  } 
	 /**
	  * 
	  * @return MediaObject
	  */
	  public MediaObject getInfo() 
	  { 
	    return info; 
	  } 
	  
	}

