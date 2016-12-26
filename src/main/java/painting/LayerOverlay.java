package painting;

import javax.swing.JLayeredPane;

public class LayerOverlay extends JLayeredPane
{
	private static final long	serialVersionUID	= 1L;
	
	private BGImageLayer background;

	public LayerOverlay()
	{
		super();
		this.add(background, 5);
	}
}
