/**
 * 
 */
package data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

import events.MediaEvent;
import files.VMPaths;
import gui.ErrorCenter;

/**
 * 
 *         Spielt die Audioaufnahme ab und aktualisiert das entsprechende
 *         Netzwerkkartenbild
 *
 */
public class MovieAudioPlay extends Thread implements MediaListener
{

	private Play									sound	= null;

	/**
	 * Singleton: Referenz.
	 */
//	private static MovieAudioPlay	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige MovieAudioPlay-Instanz in diesem Prozess.
	 */
	/*public synchronized static MovieAudioPlay getInstance()
	{
		if (instance == null)
		{
			instance = new MovieAudioPlay();
			
			MediaEventList.getInstance();
			MediaEventList.getInstance().addListener(instance);

		}
		return instance;
	}
*/
/**
 * 
 */
	public MovieAudioPlay()
	{
		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);
	}
	
	public void start(int position){
		long audioevent = MovieCreate.getInstance().getPreviousAudioRecordEvent(
				position);

		long currentevent = MovieCreate.getInstance().getTimecode(position);

		if (audioevent > 0)
		{
			File file = new File(VMPaths.getCurrentWorkingDirectory()
					+ "/audio/" + audioevent + ".wav"); //$NON-NLS-1$ //$NON-NLS-2$

			boolean stat = false;
			if (sound != null)
				stat = sound.isAlive();
			
			if ( (sound == null) || (stat == false) )
			{
			sound = new Play(file, currentevent - audioevent);
			sound.start();
			}
		}

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

			MediaEventList.getInstance().removeListener(this);
		}

		if (e.getInfo().getMessage().equals(MediaObject.TIME_CODE))
		{
		}
		
		if (e.getInfo().getMessage().equals(MediaObject.IMAGE_NUMBER))
		{
			String s = (String) e.getInfo().getObject();
			int value = new Integer(s).intValue();
			this.start(value);
		}

	}

}

class Play extends Thread implements MediaListener
{
	private File				soundFile;

	private long				fileSize					= 0;

	private long				fileTime					= 0;

	private byte[]				abData					= new byte[3200];

	private int					play_nBytesRead		= 0;

	private long				play_totalBytesRead	= 0;

	private SourceDataLine	line						= null;

	AudioInputStream			audioInputStream		= null;

	InputStream					inputStream				= null;

	private long				currentTimePosition	= 0;

	AudioFormat					audioFormat;

	private boolean			threadPlayDone			= false;

	private boolean			DEBUG_MODE				= false;

	private static long		audioWavePosition		= 0;

	public Play(File name, long audio_position)
	{

		soundFile = name;
		currentTimePosition = audio_position;

		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);
		
	}

	private BufferedInputStream streamOpenRead(File soundFile)
	{

		InputStream fileInputStream = null;

		try
		{

			fileInputStream = new FileInputStream(soundFile);
			return new BufferedInputStream(fileInputStream);

		} catch (FileNotFoundException exn1)
		{
			ErrorCenter
			.manageException(
					null,"Can't open audio file", ErrorCenter.ERROR, false, true); //$NON-NLS-1$	
			
			return null;
		}

	}

	public void done()
	{

		threadPlayDone = true;
		if (line != null)
		{
			line.drain();
			line.stop();
			line.close();
		}
		

	}

	public void proceed()
	{
		notify();
	}

	public void run()
	{

		threadPlayDone = false;

		boolean audioStatus = setAudioSystem(soundFile, currentTimePosition);

		if (audioStatus == false)
		{			
			ErrorCenter
			.manageException(
					null,
					"Can't initialize audio system", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

			this.done();
		}

		while ((threadPlayDone == false) && (play_nBytesRead > -1))
		{

			if (play_nBytesRead > -1)
			{
				try
				{
					play_nBytesRead = audioInputStream
							.read(abData, 0, abData.length);
					play_totalBytesRead += play_nBytesRead;
				} catch (Exception e)
				{
					ErrorCenter
					.manageException(
							null,"Can't read audio input stream", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

					this.done();
				};
			}

			// update the timecode info (microseconds)
			if (audioFormat != null)
				setAudioWavePosition((long) ((play_totalBytesRead / audioFormat
						.getFrameSize())
						/ audioFormat.getFrameRate() * 1000));
			else
				this.done();

			/*
			 * Write the audiodata to the audio output
			 */
			if (play_nBytesRead >= 0)
				line.write(abData, 0, play_nBytesRead);

		}

		if (line != null)
		{
			line.drain();
			line.stop();
			line.close();
		}

	}

	public void setAudioWavePosition(long pos)
	{

		audioWavePosition = pos;
	}

	public long getAudioWavePosition()
	{

		return audioWavePosition;
	}

	private boolean setAudioSystem(File soundFile, long audio_position)
	{

		long audio_position_byte = 0;

		inputStream = streamOpenRead(soundFile);

		if (inputStream == null)
		{

			ErrorCenter
			.manageException(
					null,
					"Can't open audio file", ErrorCenter.ERROR, false, true); //$NON-NLS-1$
			this.done();

		}
		else
		{

			try
			{
				audioInputStream = AudioSystem.getAudioInputStream(inputStream);
			} catch (Exception e)
			{
				System.out.print("Error:Bad file");
				this.done();
			}

			audioFormat = audioInputStream.getFormat();

			fileSize = soundFile.length();
			fileTime = (long) ((fileSize / audioFormat.getFrameSize()) / audioFormat
					.getFrameRate());

			audio_position_byte = (long) ((audio_position / 1000)
					* audioFormat.getFrameRate() * audioFormat.getFrameSize());

			play_nBytesRead = 0;

			while (play_nBytesRead <= audio_position_byte)
			{
				try
				{
					audioInputStream.read(abData, 0, abData.length);
				} catch (IOException exn)
				{
					ErrorCenter
					.manageException(
							null,
							"Can't read audio input stream", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

					this.done();

				}
				play_nBytesRead += abData.length;
			}

			debug("Filename:" + soundFile.toString());
			debug(audioFormat.toString());
			debug("Filesize:" + fileSize + " Bytes");
			debug("Duration:" + fileTime + "s"); //$NON-NLS-2$
			debug("Audio-Pos:" + audio_position);
			debug("Reposition:" + audio_position_byte + "Byte");

			DataLine.Info info = new DataLine.Info(SourceDataLine.class,
					audioFormat);

			try
			{
				line = (SourceDataLine) AudioSystem.getLine(info);
				line.open();
			} catch (Exception e)
			{
				ErrorCenter
				.manageException(
						null,
						"Can't get line info", ErrorCenter.ERROR, false, true); //$NON-NLS-1$

				return false;
			};

			line.addLineListener(new LineListener()
			{

				public void update(LineEvent evt)
				{

					if (evt.getType() == LineEvent.Type.STOP)
					{
						debug("LineEvent: Stop");
						done();
					}

					if (evt.getType() == LineEvent.Type.START)
					{
						debug("START");

					}
				}
			});

			line.start();

		}

		return true;

	}

	private void debug(String s)
	{
		if (DEBUG_MODE == true)
			System.out.println(s);
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
		if ( (e.getInfo().getMessage().equals(MediaObject.STOP)) && (threadPlayDone == false) )
		{
			this.done();
		}
	}

}
