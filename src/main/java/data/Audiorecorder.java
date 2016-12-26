package data;

import java.io.File;

import events.ComplexEvent;
import events.MediaEvent;
import files.VMPaths;

public class Audiorecorder implements MediaListener
{

	private boolean					status	= false;

	/**
	 * Singleton: Referenz.
	 */
	private static Audiorecorder	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Audiorecorder-Instanz in diesem Prozess.
	 */
	public synchronized static Audiorecorder getInstance()
	{
		if (instance == null)
		{
			instance = new Audiorecorder();

		}
		return instance;
	}

	private Audiorecorder()
	{

		MediaEventList.getInstance().addListener(this);

	}
/**
 * Start the audio recording
 */
	public void startRecording()
	{

		File f = this.getAudiofilename();
		if (f != null)
		{
			MediaEventList.getInstance().notify(
					new MediaEvent(this, new MediaObject(MediaObject.STOP)));

			MovieRecorder.getInstance(f);
			this.status = true;
		}

	}
	
	
/**
 * Stop the audio recording
 */
	public void stopRecording()
	{
		MediaEventList.getInstance().notify(
				new MediaEvent(this, new MediaObject(MediaObject.STOP)));
		this.status = false;

	}

	/**
	 * Check if audio recording is running true = running false = not running
	 * 
	 * @return true: runnging, false: not running
	 */
	public boolean isRunning()
	{
		return this.status;
	}

	private File getAudiofilename()
	{

		File file = null;
		long freespace;

		ComplexEvent event = new ComplexEvent("AudioRecordStart"); //$NON-NLS-1$
		EventProcessor.getInstance().fireEvent(event);

		long audio_timestamp = EventProcessor.getInstance().getCurrentTimestamp();

		file = new File(VMPaths.getCurrentWorkingDirectory()
				+ "/audio/" + audio_timestamp + ".wav"); //$NON-NLS-1$ //$NON-NLS-2$

		freespace = new File(VMPaths.getCurrentWorkingDirectory()
				+ "/audio/").getUsableSpace(); //$NON-NLS-1$

		//System.out.println("Avaiable space:" + (freespace) + "Byte (" //$NON-NLS-2$
		//		+ (freespace / (1024 * 1024)) + " MB )"); //$NON-NLS-1$

		if (!file.exists())
			return file;

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see data.MediaListener#action(events.MediaEvent)
	 */
	@Override
	public void action(MediaEvent e)
	{
		if (e.getInfo().getMessage().equals(MediaObject.STOP))
		{
			this.status = false;
		}

	}
}
