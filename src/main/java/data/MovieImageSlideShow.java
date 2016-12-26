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
public class MovieImageSlideShow extends Thread implements MediaListener
{

	private boolean	threadDone	= false;
	int q = -1;

	public MovieImageSlideShow()
	{
		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);
	}
	
	public MovieImageSlideShow(int s)
	{
		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);
		this.q = s;
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

		long zeit = 0;
		long zeit2 = 0;
		long zeitgesamt = 0;

		ImageIcon i = null;
		MediaObject m = new MediaObject();

		do
		{
			i = MovieCreate.getInstance().getImage(q);
			
			if (i==null) { 
				m.setObject(i);
				m.setMessage(MediaObject.END);
				MediaEventList.getInstance().notify(new MediaEvent(this, m));

				}

			if ((i != null) && (threadDone == false))
			{
				m.setObject(i);
				m.setMessage(MediaObject.LOAD_IMAGE);
				MediaEventList.getInstance().notify(new MediaEvent(this, m));

				m.setObject("" + (MovieCreate.getInstance().getTime(q) / 1000));
				m.setMessage(MediaObject.TIME_CODE);
				MediaEventList.getInstance().notify(new MediaEvent(this, m));

				m.setObject("" + q);
				m.setMessage(MediaObject.IMAGE_NUMBER);
				MediaEventList.getInstance().notify(new MediaEvent(this, m));

				try
				{
					sleep(500);
				} catch (InterruptedException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				}

				q++;

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
