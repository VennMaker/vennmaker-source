package gui.sidemenu;

import org.jdesktop.swingx.JXTaskPane;

@SuppressWarnings("serial")
public abstract class SideMenuSubItem extends JXTaskPane
{
	public SideMenuSubItem()
	{
	}
	
	abstract protected void build();
}
