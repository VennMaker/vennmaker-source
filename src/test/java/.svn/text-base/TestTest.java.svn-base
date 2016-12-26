import files.VMPaths;
import gui.VennMaker;
import junit.framework.Assert;

import org.junit.Test;


public class TestTest
{
	@Test
	public void test1()
	{
		Assert.assertNotNull(VennMaker.getInstance());
	}
	
	@Test
	public void test2()
	{
		Assert.assertEquals(VMPaths.SEPARATOR, System.getProperty("file.separator"));
	}
	
	@Test
	public void testMetaInformation()
	{
		VennMaker.getInstance().getProject().setMetaInformation("TEST");
		Assert.assertEquals(VennMaker.getInstance().getProject().getMetaInformation(),"TEST");		
	}
	
	@Test
	public void testEgoName()
	{
		VennMaker.getInstance().getProject().getCurrentNetzwerk().getEgo().setName("Ego");
		Assert.assertEquals(VennMaker.getInstance().getProject().getEgo().getName(),"Ego");	
	}
	

	
}
