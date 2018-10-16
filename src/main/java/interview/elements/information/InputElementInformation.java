package interview.elements.information;

import interview.InterviewElementInformation;

import java.util.Map;

import data.AttributeType;
import data.Netzwerk;

public class InputElementInformation extends InterviewElementInformation
{
	private String question;
	private String filter;
	private String info;
	private Integer[] filterIndex;
	private Netzwerk network;
	
	private Map<String,AttributeType> attributesAndQuestions;
	
	/**
	 * Creates a new object of <code>InputElementInformation</code>
	 * @param question the question for the selected element
	 * @param filter the filter for the element
	 * @param filterIndex the filterIndex of the element
	 * @param attributesAndQuestions the selected attributes with their related questions
	 * @param info information to show to the user
	 */
	public InputElementInformation(String question, String filter, Integer[] filterIndex,
			Map<String, AttributeType> attributesAndQuestions, String info)
	{
		this.question = question;
		this.filter = filter;
		this.attributesAndQuestions = attributesAndQuestions;
		this.info = info;
		this.filterIndex = filterIndex;
	}

	
	/**
	 * Creates a new empty object of <code>InputElementInformatoin</code>
	 */
	public InputElementInformation()
	{
		
	}
	
	/**
	 * @return the question
	 */
	public String getQuestion()
	{
		return question;
	}


	/**
	 * @return the filter
	 */
	public String getFilter()
	{
		return filter;
	}


	/**
	 * @return the attributesAndQuestions
	 */
	public Map<String, AttributeType> getAttributesAndQuestions()
	{
		return attributesAndQuestions;
	}
	
	/**
	 * Return the information text
	 * @return information text
	 */
	public String getInfo()
	{
		return info;
	}
	
	/**
	 * Returns the indicies of the filter words
	 * @return the indicies of the filter words
	 */
	public Integer[] getFilterIndex()
	{
		return filterIndex;
	}

	/**
	 * Returns the network for the <code>StandardElement</code>
	 * @return the network for the <code>StandardElement</code>
	 */
	public Netzwerk getNetwork()
	{
		return network;
	}

	/**
	 * Sets the network for the <code>StandardElement</code>
	 * @param network the network for the <code>StandardElement</code>
	 */
	public void setNetwork(Netzwerk network)
	{
		this.network = network;
	}
	
	/**
	 * Sets the Question for this <code>InputElementInformation</code>
	 * @param question the question
	 */
	public void setQuestion(String question)
	{
		this.question = question;
	}
	
}
