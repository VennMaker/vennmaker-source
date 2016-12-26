package interview.elements.information;

import interview.InterviewElementInformation;

public class NameGeneratorInformation extends InterviewElementInformation
{
	private String question;
	private String attributeValue;
	private String attribute;
	private String infoText;
	private String filter;
	private int minActors;
	private int maxActors;
	private boolean checkboxSelected;
	
	public NameGeneratorInformation(String question, String infoText, boolean checkboxSelected, int minActors, int maxActors, String attributeValue, String attribute, String filter)
	{
		this.question = question;
		this.infoText = infoText;
		this.checkboxSelected = checkboxSelected;
		this.minActors = minActors;
		this.maxActors = maxActors;
		this.attributeValue = attributeValue;
		this.attribute = attribute;
		this.filter = filter;
	}

	/**
	 * @return the question
	 */
	public String getQuestion()
	{
		return question;
	}

	/**
	 * @return the minActors
	 */
	public int getMinActors()
	{
		return minActors;
	}

	/**
	 * @return the maxActors
	 */
	public int getMaxActors()
	{
		return maxActors;
	}

	/**
	 * @return the attributeValue
	 */
	public String getAttributeValue()
	{
		return attributeValue;
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute()
	{
		return attribute;
	}
	
	/**
	 * 
	 * @return the info text
	 */
	public String getInfoText()
	{
		return infoText;
	}
	
	/**
	 * Returns the filter for this <code>InterviewElement</code>
	 * @return
	 */
	public String getFilter()
	{
		return filter;
	}

	public boolean isCheckBoxEnabled()
	{
		return checkboxSelected;
	}
}
