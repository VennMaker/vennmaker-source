package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jvnet.flamingo.common.icon.ResizableIcon;

import data.EventProcessor;
import data.MediaEventList;
import data.MediaListener;
import data.MediaObject;
import data.MovieAudioPlay;
import data.MovieCreate;
import data.MovieImagePlay;
import data.MovieImageSlideShow;
import events.ComplexEvent;
import events.MediaEvent;
import files.FileOperations;
import files.ImageOperations;
import files.VMPaths;

public class AudiorecorderWindow extends JDialog implements ActionListener,
		MediaListener, ChangeListener
{

	/**
	 * 
	 */
	private static final long				serialVersionUID		= 1L;

	private JButton							buttonPlayStart		= new JButton(
																						Messages
																								.getString("VennMaker.Play"));		//$NON-NLS-1$

	private JButton							buttonRecordPlayStop	= new JButton(
																						Messages
																								.getString("VennMaker.Stop"));		//$NON-NLS-1$

	private JButton							buttonPlaySlideShow	= new JButton(
																						Messages
																								.getString("VennMaker.Slideshow")); //$NON-NLS-1$

	private JSlider							playSlider				= new JSlider(0,
																						100, 0);

	private static JLabel					labelPlayTime			= new JLabel(
																						Messages
																								.getString("VennMaker.PlayTime"));	//$NON-NLS-1$

	private static JCheckBox				checkLoop				= new JCheckBox(
																						"Loop");											//$NON-NLS-1$

	ResizableIcon								img1;

	ResizableIcon								img2;

	ResizableIcon								img3;

	ResizableIcon								img4;

	ResizableIcon								img5;

	ResizableIcon								img6;

	JLabel										nwmImageLabel;

	int											counter					= 1;

	private String								path						= VMPaths.VENNMAKER_TEMPDIR;

	private MovieImageSlideShow			mslideshow;

	private MovieImagePlay					mimageplay;

	private int									internposition			= 0;

	/**
	 * Singleton: Referenz.
	 */
	private static AudiorecorderWindow	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige Audiorecorder-Instanz in diesem Prozess.
	 */
	public static AudiorecorderWindow showAudioWindow()
	{
		if (instance == null)
		{
			instance = new AudiorecorderWindow();

		}
		return instance;
	}

	/**
	 * Shows the record, play and stop buttons and the timecode and slider
	 */
	private AudiorecorderWindow()
	{

		super(VennMaker.getInstance(), false);

		MediaEventList.getInstance().notify(
				new MediaEvent(this, new MediaObject(MediaObject.STOP)));

		MovieCreate.getInstance().setPath(path);

		MovieCreate.getInstance().generateSlides();

		labelPlayTime.setText(calculateTime(0));

		MediaEventList.getInstance();
		MediaEventList.getInstance().addListener(this);

		int row = 0;

		setTitle(Messages.getString("VennMaker.Audiorecorder")); //$NON-NLS-1$
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(false);
		this.setSize(400, 600);

		setIconImage(new ImageIcon(FileOperations.getAbsolutePath(Messages
				.getString("VennMaker.Audiorecorder_Icon7"))).getImage()); //$NON-NLS-1$

		this.addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent arg0)
			{
			}

			public void windowActivated(WindowEvent arg0)
			{
			}

			public void windowClosing(WindowEvent arg0)
			{
				MediaEventList.getInstance().notify(
						new MediaEvent(this, new MediaObject(MediaObject.STOP)));
				removeListener();
				instance = null;

			}

			public void windowDeactivated(WindowEvent arg0)
			{

			}

			public void windowDeiconified(WindowEvent arg0)
			{
			}

			public void windowIconified(WindowEvent arg0)
			{
			}

			public void windowOpened(WindowEvent arg0)
			{
			}
		});

		GridBagConstraints constraints = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();
		setLayout(gbl);

		JPanel menuepanel = new JPanel();
		menuepanel.setLayout(gbl);
		menuepanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Play-Button:

		BufferedImage i;

		i = ImageOperations.loadImage(new File(Messages
				.getString("VennMaker.Audiorecorder_Icon2"))); //$NON-NLS-1$
		ImageIcon img2 = new ImageIcon(i);
		buttonPlayStart.setIcon(img2);

		buttonPlayStart.setActionCommand("play"); //$NON-NLS-1$
		buttonPlayStart.addActionListener(this);

		// Stop-Button:

		i = ImageOperations.loadImage(new File(FileOperations
				.getAbsolutePath(Messages
						.getString("VennMaker.Audiorecorder_Icon3")))); //$NON-NLS-1$
		ImageIcon img3 = new ImageIcon(i);
		buttonRecordPlayStop.setIcon(img3);

		buttonRecordPlayStop.setActionCommand("stop"); //$NON-NLS-1$
		buttonRecordPlayStop.addActionListener(this);

		// Slide Show-Button:

		i = ImageOperations.loadImage(new File(Messages
				.getString("VennMaker.Audiorecorder_Icon8"))); //$NON-NLS-1$
		ImageIcon img8 = new ImageIcon(i);
		buttonPlaySlideShow.setIcon(img8);

		buttonPlaySlideShow.setActionCommand("slideshow"); //$NON-NLS-1$
		buttonPlaySlideShow.addActionListener(this);

		buttonPlayStart.setEnabled(true);
		buttonRecordPlayStop.setEnabled(true);
		buttonPlaySlideShow.setEnabled(true);

		playSlider.setPaintTicks(true);
		playSlider.setMajorTickSpacing(10);
		playSlider
				.setMaximum(MovieCreate.getInstance().getActivityList().size() - 1);
		playSlider.addChangeListener(this);

		checkLoop.setActionCommand("checkLoop"); //$NON-NLS-1$

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 1;
		constraints.gridy = row;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		gbl.setConstraints(buttonPlayStart, constraints);
		menuepanel.add(buttonPlayStart, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 2;
		constraints.gridy = row;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		gbl.setConstraints(buttonRecordPlayStop, constraints);
		menuepanel.add(buttonRecordPlayStop, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 3;
		constraints.gridy = row;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		gbl.setConstraints(buttonPlaySlideShow, constraints);
		menuepanel.add(buttonPlaySlideShow, constraints);

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 4;
		constraints.gridy = row;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		gbl.setConstraints(checkLoop, constraints);
		menuepanel.add(checkLoop, constraints);

		row++;

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.gridwidth = 4;
		constraints.gridheight = 1;
		gbl.setConstraints(labelPlayTime, constraints);
		constraints.insets = new Insets(10, 0, 0, 0);
		menuepanel.add(labelPlayTime, constraints);

		row++;

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.gridwidth = 7;
		constraints.gridheight = 1;
		gbl.setConstraints(playSlider, constraints);
		constraints.insets = new Insets(10, 0, 0, 0);
		menuepanel.add(playSlider, constraints);

		row++;

		// Network map image

		nwmImageLabel = new JLabel();
		nwmImageLabel.setIcon(MovieCreate.getInstance().getLastImage());

		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = row;
		constraints.gridwidth = 20;
		constraints.gridheight = 1;
		gbl.setConstraints(nwmImageLabel, constraints);
		menuepanel.add(nwmImageLabel, constraints);
		// ----

		add(menuepanel);

		pack();

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);

		setVisible(true);

	}

	public void actionPerformed(ActionEvent event)
	{

		if ("play".equals(event.getActionCommand())) //$NON-NLS-1$
		{

			MediaEventList.getInstance().notify(
					new MediaEvent(this, new MediaObject(MediaObject.STOP)));

			mimageplay = new MovieImagePlay(this.internposition);
			mimageplay.start();
			// MovieAudioPlay.getInstance().start(this.internposition);
			new MovieAudioPlay().start(this.internposition);
		}

		if ("stop".equals(event.getActionCommand())) //$NON-NLS-1$
		{
			MediaEventList.getInstance().notify(
					new MediaEvent(this, new MediaObject(MediaObject.STOP)));

		}

		if ("slideshow".equals(event.getActionCommand()))
		{
			MediaEventList.getInstance().notify(
					new MediaEvent(this, new MediaObject(MediaObject.STOP)));

			mslideshow = new MovieImageSlideShow(this.internposition);
			mslideshow.start();
		}

	}

	private void removeListener()
	{
		MediaEventList.getInstance().removeListener(this);
	}

	// Convert Seconds to h:m:s - Format
	private String calculateTime(long t)
	{

		String format = String.format("%%0%dd", 2); //$NON-NLS-1$

		int time = (int) (t);
		String seconds = String.format(format, ((int) (time % 60)));
		String minutes = String.format(format, ((int) ((time % 3600) / 60)));
		String hours = String.format(format, ((int) (time / 3600)));

		int time2 = (int) (MovieCreate.getInstance().getMovieLength() / 1000);
		String seconds2 = String.format(format, ((int) (time2 % 60)));
		String minutes2 = String.format(format, ((int) ((time2 % 3600) / 60)));
		String hours2 = String.format(format, ((int) (time2 / 3600)));

		return "" + hours + ":" + minutes + ":" + seconds + " (" + hours2 + ":"
				+ minutes2 + ":" + seconds2 + ")";

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

		freespace = new File(VMPaths.getCurrentWorkingDirectory() + "/audio/").getUsableSpace(); //$NON-NLS-1$

		System.out.println("Available space:" + (freespace) + "Byte (" //$NON-NLS-2$
				+ (freespace / (1024 * 1024)) + " MB )"); //$NON-NLS-1$

		if (!file.exists()
				|| (JOptionPane
						.showConfirmDialog(
								null,
								Messages.getString("VennMaker.ConfirmOverwrite") + file.getName(), //$NON-NLS-1$ //$NON-NLS-2$
								Messages.getString("VennMaker.ConfirmOverwriteTitel"), JOptionPane.YES_NO_OPTION) //$NON-NLS-1$
				== JOptionPane.OK_OPTION))
		{

			return file;
		}

		return null;
	}

	@Override
	public void action(MediaEvent e)
	{
		/* Laedt das Netzwerkbild */
		if (e.getInfo().getMessage().equals(MediaObject.LOAD_IMAGE))
		{
			ImageIcon i = (ImageIcon) e.getInfo().getObject();
			nwmImageLabel.setIcon(i);
		}

		/* Zeigt den Zeitcode */
		if (e.getInfo().getMessage().equals(MediaObject.TIME_CODE))
		{
			String s = (String) e.getInfo().getObject();
			if (s != null)
			{
				String t = calculateTime(new Long(s).longValue());
				labelPlayTime.setText(t);
			}
		}

		if (e.getInfo().getMessage().equals(MediaObject.IMAGE_NUMBER))
		{
			String s = (String) e.getInfo().getObject();
			if (s != null)
			{
				int value = new Integer(s).intValue();
				playSlider.setValue(value);
				this.setInternPosition(value);
			}
		}

		if (e.getInfo().getMessage().equals(MediaObject.END))
		{

			if (checkLoop.isSelected() == true)
			{
				MediaEventList.getInstance().notify(
						new MediaEvent(this, new MediaObject(MediaObject.STOP)));

				mslideshow = new MovieImageSlideShow(0);
				mslideshow.start();
			}
		}

	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider) e.getSource();
		if (source.getValueIsAdjusting() == true) // wurde Slider "per-Hand"
																// bewegt?
		{
			int position = (int) source.getValue();

			if (position >= 0)
			{
				this.setInternPosition(position);
				MediaEventList.getInstance().notify(
						new MediaEvent(this, new MediaObject(MediaObject.STOP)));

				String t = calculateTime(MovieCreate.getInstance()
						.getTime(position) / 1000);
				labelPlayTime.setText(t);

				MediaObject m = new MediaObject();
				m.setObject(MovieCreate.getInstance().getImage(position));
				m.setMessage(MediaObject.LOAD_IMAGE);
				MediaEventList.getInstance().notify(new MediaEvent(this, m));
			}

		}

	}

	/**
	 * Set the current image position
	 * 
	 * @param p
	 */
	private void setInternPosition(int p)
	{
		this.internposition = p;
	}

}
