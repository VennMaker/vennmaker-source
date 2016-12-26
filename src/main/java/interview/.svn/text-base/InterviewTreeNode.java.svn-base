/**
 * 
 */
package interview;

import interview.elements.InterviewElement;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 *
 */
public class InterviewTreeNode extends DefaultMutableTreeNode
{
	
	private int id;
	
	public InterviewTreeNode(Object userObject)
	{
		super(userObject);
	}
	
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean equals(Object obj)
	{
		if (!(obj instanceof InterviewTreeNode))
			return false;

		InterviewTreeNode node = (InterviewTreeNode) obj;

		if (((InterviewElement) node.getUserObject())
				.equals((InterviewElement) this.getUserObject()))
			return true;

		return false;
	}

	public int hashCode()
	{
		return ((InterviewElement) this.getUserObject()).hashCode();
	}

	public String toString()
	{
		if (userObject instanceof String)
			return super.toString();
		else
			return ((InterviewElement) getUserObject()).getElementNameInTree();
	}
	
}
