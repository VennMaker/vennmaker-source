package interview.elements.information;

import interview.InterviewElementInformation;

public class DataProtectionInformation extends InterviewElementInformation
{
	private String info;
	private String institute;
	
	public DataProtectionInformation(String info, String institute)
	{
		this.info = info;
		this.institute = institute;
	}
	
	public String getInstitute()
	{
		return institute;
	}
	
	public String getInfo()
	{
		return info;
	}
}
