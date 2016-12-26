package data;

/**
 * Objects implementing this interface listen for the occurence of an uncaught exception.
 * Every time such an exception occurs, the method will be called. 
 * All Listeners have to register themselfs via <code>VennMaker.getInstance().addUncaughtExceptionListener()</code>
 * 
 *
 */
public interface UncaughtExceptionListener
{
	/**
	 * This method is called, every time a uncaught exception is thrown
	 */
	public void exceptionOccured();
}
