/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import data.UncaughtExceptionListener;

/**
 * 
 * 
 */
public class WaitDialog extends JDialog implements UncaughtExceptionListener
{
	private Component		parent;

	private JProgressBar	progressBar;

	private boolean			intermediate;

	private int				min;

	private int				max;

	/**
	 * Creates an new "WaitDialog". If no min and max value are set, the progressbar will
	 * set to intermediate mode
	 */
	public WaitDialog(JDialog parent, boolean visible, int min, int max)
	{
		super(parent, visible);
		this.parent = parent;
		this.min = min;
		this.max = max;
		
	}
	
	/**
	 * Creates an new "WaitDialog". If no min and max value are set, the progressbar will
	 * set to intermediate mode
	 */
	public WaitDialog(Window parent, boolean visible)
	{
		super(parent,Dialog.ModalityType.TOOLKIT_MODAL);
		this.parent = parent;
		this.intermediate = true;
		addComponents(visible);
	}
	
	/**
	 * Creates an new "WaitDialog". If no min and max value are set, the progressbar will
	 * set to intermediate mode
	 */
	public WaitDialog(JFrame parent, boolean visible)
	{
		super(parent, true);
		this.parent = parent;
		this.intermediate = true;
		addComponents(visible);
	}
	
	/**
	 * Creates an new "WaitDialog". If no min and max value are set, the progressbar will
	 * set to intermediate mode
	 */
	public WaitDialog(JFrame parent, boolean visible, int min, int max)
	{
		super(parent, visible);
		this.parent = parent;
		this.min = min;
		this.max = max;
	}

	private void addComponents(boolean visible)
	{
		this.setLayout(new BorderLayout());
		JLabel waitLabel = new JLabel(Messages
				.getString("OpenFileInBackground.0"), JLabel.CENTER); //$NON-NLS-1$
		this.add(waitLabel, BorderLayout.NORTH);

		this.progressBar = new JProgressBar();

		if (this.intermediate)
		{
			this.progressBar.setIndeterminate(true);
		}
		else
		{
			this.progressBar.setMinimum(min);
			this.progressBar.setMaximum(max);
		}

		this.add(this.progressBar, BorderLayout.CENTER);

		this.setSize(150, 70);
	 	//this.setSize(150, 35);
		//this.setUndecorated(true);

		
		int x = parent.getWidth() / 2 - this.getWidth() / 2;
		int y = parent.getHeight() / 2 - this.getHeight() / 2;

		Point location = parent.getLocation();
		int oldX = (int) location.getX();
		int oldY = (int) location.getY();

		this.setLocation(oldX + x, oldY + y);
		
		VennMaker.getInstance().addUncaughtExceptionListener(this);
		
		if (visible)
			this.setVisible(true);
	}
	
	/**
	 * Increments the value of the progressbar, if not in intermediate mode
	 */
	public void incrementBar()
	{
		if(!intermediate)
		{
			int value = this.progressBar.getValue();
			this.progressBar.setValue(value++);
		}
	}
	
	
	/**
	 * Decrements the value of the progressbar, if not in intermediate mode
	 */
	public void decrementBar()
	{
		if(!intermediate)
		{
			int value = this.progressBar.getValue();
			this.progressBar.setValue(value--);
		}
	}
	
	/**
	 * Sets the value of the progressbar
	 * @param value progressbar value
	 */
	public void setValue(int value)
	{
		this.progressBar.setValue(value);
	}
	
	/**
	 * Returns the value of the progressbar
	 * @return current value of the progressbar
	 */
	public int getValue()
	{
		return this.progressBar.getValue();
	}

	@Override
	public void exceptionOccured()
	{
		this.setVisible(false);
	}
}
