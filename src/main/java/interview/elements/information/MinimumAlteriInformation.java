package interview.elements.information;

import interview.InterviewElementInformation;

/**
 * Information about the MinimumalteriElement; stores the infotext and the
 * minimum number of Alteri
 * 
 * 
 * 
 */
public class MinimumAlteriInformation extends InterviewElementInformation
{
	private int		minValue;

	private String	 info;

	public MinimumAlteriInformation(int minimumValue, String info)
	{
		this.minValue = minimumValue;
		this.info = info;
	}

	public int getValue()
	{
		return this.minValue;
	}

	public String getInfo(){
		return this.info;
	}
}
