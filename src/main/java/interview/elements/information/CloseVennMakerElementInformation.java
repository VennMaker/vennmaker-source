package interview.elements.information;

import interview.InterviewElementInformation;

public class CloseVennMakerElementInformation extends InterviewElementInformation
{
	private String info;
	
	public CloseVennMakerElementInformation(String info)
	{
		this.info = info;
	}

	/**
	 * @return Information text of the element
	 */
	public String getInfo()
	{
		return info;
	}
}
