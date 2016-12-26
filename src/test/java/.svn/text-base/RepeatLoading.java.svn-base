import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import util.Repeat;
import util.RepeatRunner;
import util.ResetSingleton;

/**
 * Nur ein Test-Case für Repeat und Reset von Singletons
 * 
 * 
 * 
 */

class SingletonRandom
{
	static private SingletonRandom	self	= null;

	private int[]							test	= { 0, 1, 2, 3, 4 };

	private int								index	= 0;

	private SingletonRandom()
	{

	}

	static public SingletonRandom getInstance()
	{
		if (self == null)
			self = new SingletonRandom();
		return self;
	}

	public int getNext()
	{
		return test[(index++) % test.length];
	}
}

@RunWith(RepeatRunner.class)
public class RepeatLoading
{

	@Test
	@Repeat(5)
	@ResetSingleton(type = SingletonRandom.class, field = "self")
	public void test()
	{

		int val = SingletonRandom.getInstance().getNext();

		System.out.println(val);
		Assert.assertFalse(val == 4);
	}

}
