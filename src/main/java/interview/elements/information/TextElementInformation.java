package interview.elements.information;

import interview.InterviewElementInformation;

public class TextElementInformation extends InterviewElementInformation
{

	private String	elementName;

	private String	message;

	public TextElementInformation(String elementName, String message)
	{
		this.elementName = elementName;
		this.message = message;
	}

	/**
	 * @return the elementName
	 */
	public String getElementName()
	{
		return elementName;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

}
