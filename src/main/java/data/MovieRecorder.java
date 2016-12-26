/**
 * 
 */
package data;

import events.MediaEvent;
import gui.ErrorCenter;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

/**
 * 
 *         Klasse zur Audioaufnahme
 * 
 */
public class MovieRecorder extends Thread implements MediaListener
{

	private AudioFormat				audioFormat		= null;

	private File						audioFile		= null;

	private TargetDataLine			targetDataLine	= null;

	private AudioFileFormat.Type	fileType			= null;

	private DataLine.Info			dataLineInfo	= null;

	private AudioInputStream		input				= null;

	private boolean					running			= false;

	/**
	 * Singleton: Referenz.
	 */
	private static MovieRecorder	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Audiorecorder-Instanz in diesem Prozess.
	 */
	public synchronized static MovieRecorder getInstance(File f)
	{
		if (instance == null)
		{
			instance = new MovieRecorder(f);

		}
		return instance;
	}

	/**
	 * 
	 * @param audioFile
	 */
	private MovieRecorder(File audioFile)
	{

		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);

		fileType = AudioFileFormat.Type.WAVE;

		// float sampleRate = 44100.0F; // 8000,11025,16000,22050,44100
		float sampleRate = 22050.0F; // 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16; // 8,16
		int channels = 1; // 1,2
		boolean signed = true; // true,false
		boolean bigEndian = false; // true,false

		audioFormat = new AudioFormat(sampleRate, sampleSizeInBits, channels,
				signed, bigEndian);

		this.audioFile = audioFile;
		
		dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

		if (!AudioSystem.isLineSupported(dataLineInfo))
		{
			
			ErrorCenter
			.manageException(
					null,
					dataLineInfo + " is not supported.", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

			return;
		}

		try
		{
			targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
			input = new AudioInputStream(targetDataLine);
			// mixer(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

		} catch (LineUnavailableException exn)
		{
			
			ErrorCenter
			.manageException(
					null,"Line not available", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

			exn.printStackTrace();

		}

		this.start();

	}

	/**
	 * Beendet die Audioaufnahme
	 */
	public void done()
	{
		this.running = false;
		targetDataLine.stop();
		targetDataLine.close();
		MediaEventList.getInstance().notify(
				new MediaEvent(this, new MediaObject(MediaObject.STOP)));

		instance = null;
	}

	public void run()
	{
		this.running = true;
		try
		{
			AudioSystem.write(input, this.fileType, this.audioFile);
		} catch (IOException exn)
		{
			exn.printStackTrace();
			ErrorCenter
			.manageException(
					null,"Can't write audio record data", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

		} finally
		{
			this.running = false;
			done();
		}
	}

	/**
	 * Print some audio and audio mixer information
	 * 
	 * @param info DataLine.info
	 */
	public void mixer(DataLine.Info info)
	{

		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		System.out.println("Available mixers:");
		for (int cnt = 0; cnt < mixerInfo.length; cnt++)
		{
			System.out.println(mixerInfo[cnt].getName());
		}

		System.out.println("----------");

		Line.Info[] lineInfo = AudioSystem.getTargetLineInfo(info);
		System.out.println("Line info:");
		for (int cnt = 0; cnt < lineInfo.length; cnt++)
		{
			System.out.println(lineInfo[cnt].getClass().getName() + ": "
					+ lineInfo[cnt].toString());

		}

		System.out.println("----------");
		System.out.println("Available file types:");
		AudioFileFormat.Type[] fileType = AudioSystem.getAudioFileTypes();
		for (int cnt = 0; cnt < fileType.length; cnt++)
		{
			System.out.println(fileType[cnt]);
		}

	}

	/**
	 * Check if audio recording is running
	 * true = running
	 * false = not running
	 * @return true / false
	 */
	public boolean isRunning(){
		return this.running;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see data.MediaListener#action(events.MediaEvent)
	 */
	@Override
	public void action(MediaEvent e)
	{
		/* Stoppt die Wiedergabe */
		if (e.getInfo().getMessage().equals(MediaObject.STOP) && (this.running == true) )
		{
			this.done();
		}

	}
}
