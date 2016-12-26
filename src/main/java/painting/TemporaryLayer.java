package painting;

import java.awt.Graphics;
import java.util.Vector;

import data.Relation;

public class TemporaryLayer extends PaintLayer implements PaintLayerInterface
{
	private static final long	serialVersionUID	= 1L;

	private Vector<Object>		temporaryItems		= new Vector<Object>();

	public TemporaryLayer()
	{
		
	}

	@Override
	public void setTransparency(byte alpha)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public byte getTransparency()
	{
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void paintComponent(final Graphics graphics)
	{
		for (Object o : temporaryItems)
		{
			if (o instanceof Relation);
				//Utilities.drawRelation((Relation) o, (Graphics2D) graphics);
		}
	}
}
