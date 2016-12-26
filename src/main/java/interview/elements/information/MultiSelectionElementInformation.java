package interview.elements.information;

import java.util.Map;

import data.AttributeType;

/**
 * Objects of this class save <code>InterviewElement</code> depended information
 * of <code>AlteriMultiAttributeOneActorElement</code>
 * 
 * 
 * 
 */
public class MultiSelectionElementInformation extends InputElementInformation
{
	/**
	 * This <code>Map</code> contains the attribute order, the user has defined
	 * by drag&drop in the configuration dialog of the
	 * <code>AlteriMultiAttributeOneActorElement</code>
	 */
	private Map<String, Integer>	attributeTypeOrder;

	/**
	 * Constructs a new <code>MultiSelectionElementInformation</code>
	 * 
	 * @param question
	 *           the question for the selected element
	 * @param filter
	 *           the filter for the element
	 * @param filterIndex
	 *           the filterIndex of the element
	 * @param attributesAndQuestions
	 *           the selected attributes with their related questions
	 * @param info
	 *           information to show to the user
	 * @param attributeTypeOrder
	 *           the attribute order the user has definied
	 */
	public MultiSelectionElementInformation(String question, String filter,
			Integer[] filterIndex,
			Map<String, AttributeType> attributesAndQuestions, String info,
			Map<String, Integer> attributeTypeOrder)
	{
		super(question, filter, filterIndex, attributesAndQuestions, info);

		this.attributeTypeOrder = attributeTypeOrder;
	}

	/**
	 * Returns the attribute order, the user has defined by drag&drop in the
	 * configuration dialog of the
	 * <code>AlteriMultiAttributeOneActorElement</code>
	 * 
	 * @return the attribute order, the user has defined by drag&drop in the
	 *         configuration dialog of the
	 *         <code>AlteriMultiAttributeOneActorElement</code>
	 */
	public Map<String, Integer> getAttributeTypeOrder()
	{
		return attributeTypeOrder;
	}

	/**
	 * Sets the attribute order, the user has defined by drag&drop in the
	 * configuration dialog of the
	 * <code>AlteriMultiAttributeOneActorElement</code>
	 * 
	 * @param attributeTypeOrder
	 *           the attribute order, the user has defined by drag&drop in the
	 *           configuration dialog of the
	 *           <code>AlteriMultiAttributeOneActorElement</code>
	 */
	public void setAttributeTypeOrder(Map<String, Integer> attributeTypeOrder)
	{
		this.attributeTypeOrder = attributeTypeOrder;
	}
}
