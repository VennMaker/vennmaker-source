/**
 * 
 */
package data;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 */
public class IconTreeNode extends DefaultMutableTreeNode
{
	private Icon icon;
	
	public IconTreeNode(Object obj,ImageIcon icon)
	{
		super(obj);
		this.icon = icon;
	}
	
	public Icon getIcon()
	{
		return icon;
	}
	
	public void setIcon(Icon icon)
	{
		this.icon = icon;
	}
}
