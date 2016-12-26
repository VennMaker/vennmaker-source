/**
 * 
 */
package data;

/**
 *
 *Interface fuer alle Objekte, die ueber MediaEventListener ausgetauscht werden
 */
public class MediaObject
{
	private String name;
	
	private Object o;
	
	public static String STOP = "Stop";
	public static String IMAGE_NUMBER = "ImageNumber";
	public static String LOAD_IMAGE = "LoadImage";
	public static String TIME_CODE = "TimeCode";
	public static String END ="End";

	public MediaObject(){
	}
	
	public MediaObject(String n){
		this.name = n;
	}
	
	public void setObject(Object o){
		this.o = o;
	}
	
	public Object getObject(){
		return this.o;
	}
	
	public void setMessage(String m){
		this.name = m;
	}
	
	public String getMessage(){
		return this.name;
	}
	
}
