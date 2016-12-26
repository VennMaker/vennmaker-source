package interview.elements.information;

import interview.InterviewElementInformation;

public class AudioRecorderElementInformation extends InterviewElementInformation
{
	private boolean startRecording;
	
	public AudioRecorderElementInformation(boolean startRecording)
	{
		this.startRecording = startRecording;
	}

	/**
	 * @return true - if recording should start, false if recording should stop
	 */
	public boolean shouldStartRecording()
	{
		return startRecording;
	}
}
