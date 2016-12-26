/**
 * 
 */
package data;

import javax.swing.ImageIcon;

import events.MediaEvent;

/**
 * 
 *         Laedt alle Netzwerkkartenabbilder in gleichmaessigen Zeitabstaenden
 *         und loest entsprechendes Event aus zum Anzeigen der
 *         Netzwerkkartenabbilder
 * 
 */
public class MovieImagePlay extends Thread implements MediaListener
{

	private boolean	threadDone	= false;
	int q = 0;
	long eventtime = 0;

	
/**
 * Show events in real time
 * @param s Event number
 */
	public MovieImagePlay(int s)
	{
		
		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);
		this.q = s;
		this.eventtime = MovieCreate.getInstance().getTimecode(this.q);

	}

	/**
	 * Beendet den Thread
	 */
	public void done()
	{
		threadDone = true;
	}

	public void run()
	{
		
		long timediff;
		long nextevent;
		long playtimeStart = System.currentTimeMillis() - (eventtime - MovieCreate.getInstance().getFirstEventTime() );
		
		ImageIcon i = null;
		MediaObject m = new MediaObject();

		if (eventtime > -1)
			do
			{
				i = MovieCreate.getInstance().getImage(this.q);
				if (i==null) { 
					m.setObject(i);
					m.setMessage(MediaObject.END);
					MediaEventList.getInstance().notify(new MediaEvent(this, m));

					}
				if (i != null){
					if (threadDone == false){
				
				
					m.setObject(i);
					m.setMessage(MediaObject.LOAD_IMAGE);
					MediaEventList.getInstance().notify(new MediaEvent(this, m));

					nextevent = MovieCreate.getInstance().getNextEventTime(
							eventtime + 1);

					timediff = (nextevent - eventtime) + System.currentTimeMillis();
					
					m = new MediaObject();
					m.setObject("" + q);
					m.setMessage(MediaObject.IMAGE_NUMBER);
					MediaEventList.getInstance().notify(new MediaEvent(this, m));


					//Warten bis zum naechsten Event time Zeitpunkt
					while ((System.currentTimeMillis() < timediff) && (threadDone == false) )
					{
						m.setObject( ""+( ( System.currentTimeMillis() - playtimeStart) / 1000) );
						m.setMessage(MediaObject.TIME_CODE);
						MediaEventList.getInstance().notify(new MediaEvent(this, m));
						
						try
						{
							sleep(100);
						} catch (InterruptedException exn)
						{
							// TODO Auto-generated catch block
							exn.printStackTrace();
						}
					}
					eventtime = nextevent;
					q++;

				}
				}
			} while ((i != null) && (threadDone == false));

	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.IMediaListener#action(data.MediaEvent)
	 */
	@Override
	public void action(MediaEvent e)
	{
		/* Stoppt die Wiedergabe */
		if (e.getInfo().getMessage().equals(MediaObject.STOP))
		{
			threadDone = true;
		}


	}


}
