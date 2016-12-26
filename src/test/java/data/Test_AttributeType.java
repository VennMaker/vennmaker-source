package data;

import static org.junit.Assert.fail;
import gui.VennMaker;
import interview.panels.other.AttributeCheckBox;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.AttributeType.Scope;

public class Test_AttributeType
{
	/**
	 * empty Attribute Type (generated before every test)
	 */
	private AttributeType aType1;
	
	/**
	 * Attribute values for the categorical attribute type catAType
	 */
	private final Object[] preVals = { "first", "second", "third" };
	
	/**
	 * categorical Attribute Type (generated before every test)
	 * attribute values : "first" , "second" , "third"
	 */
	private AttributeType catAType;
	
	/**
	 * VennMaker instance (generated once before all tests)
	 */
	private static VennMaker instance;
	
	@BeforeClass
	public static void setUpClass() throws Exception
	{
		instance = VennMaker.getInstance();
	}
	@Before
   public void setUp() throws Exception 
   {
      aType1 = new AttributeType();
      instance.getProject().getAttributeTypes().add(aType1);
      
      catAType = new AttributeType();
		catAType.setPredefinedValues(preVals);
		instance.getProject().getAttributeTypes().add(catAType);
   }
	
	@After
	public void clean()
	{
		instance.getProject().getAttributeTypes().remove(catAType);
		instance.getProject().getAttributeTypes().remove(aType1);
	}
	
	@Test
	public void testAttributeType()
	{
		AttributeType aType2 = new AttributeType();
		Assert.assertNull(aType2.getLabel());
		Assert.assertNull(aType2.getDescription());
		Assert.assertNull(aType2.getQuestion());
		Assert.assertNull(aType2.getPredefinedValues());
		Assert.assertEquals(Scope.PROJECT,aType2.getScope());
		Assert.assertNotSame(aType1.getId(), aType2.getId());
	}

	@Test
	public void testAttributeTypeInt()
	{
		AttributeType aType2 = new AttributeType(1000);
		Assert.assertEquals(1000, aType2.getId());
	}

	@Test
	public void testGetId()
	{
		AttributeType aType2 = new AttributeType();
		Assert.assertNotSame(aType1.getId(),aType2.getId());
		AttributeType aType3 = new AttributeType(1000);
		Assert.assertNotSame(1000,aType3.getId());
	}

	@Test
	public void testGetLabel()
	{
		aType1.setLabel( "aType1" );
		Assert.assertEquals( aType1.getLabel() , "aType1");
	}

	@Test
	public void testSetLabel()
	{
		aType1.setLabel( "aType1" );
		Assert.assertEquals(aType1.getLabel(), "aType1");
		aType1.setLabel( null );
		Assert.assertNull(aType1.getLabel());
	}

	@Test
	public void testGetScope()
	{
		Assert.assertEquals(Scope.PROJECT, aType1.getScope());
	}

	@Test
	public void testSetScope()
	{
		String scopeName = Scope.NETWORK.getTypeName();
		aType1.setScope(Scope.NETWORK);
		Assert.assertEquals(Scope.NETWORK, aType1.getScope());
		Assert.assertEquals(scopeName, aType1.getScope().getTypeName());
	}

	@Test
	public void testGetType()
	{
		Assert.assertEquals("ACTOR", aType1.getType());
	}

	@Test
	public void testSetType()
	{
		aType1.setType("Falscher Type");
		Assert.assertEquals("Falscher Type", aType1.getType());
	}

	@Test
	public void testToString()
	{
		aType1.setLabel("aType1");
		Assert.assertEquals(aType1.toString(), "aType1");
	}

	@Test
	public void testGetDescription()
	{
		Assert.assertNull(aType1.getDescription());
	}

	@Test
	public void testSetDescription()
	{
		aType1.setDescription("description");
		Assert.assertEquals(aType1.getDescription(), "description");
	}

	@Test
	public void testGetQuestion()
	{
		Assert.assertNull(aType1.getQuestion());
	}

	@Test
	public void testSetQuestion()
	{
		aType1.setQuestion("question?");
		Assert.assertEquals(aType1.getQuestion(), "question?");
	}

	@Test
	public void testGetPredefinedValues()
	{
		Assert.assertNull(aType1.getPredefinedValues());
		
		Assert.assertEquals(preVals[1], catAType.getPredefinedValue(1));
	}

	@Test
	public void testGetPredefinedValuesCodes()
	{
		Assert.assertEquals(0,aType1.getPredefinedValuesCodes().size());
		aType1.setPredefinedValues(null);
		Assert.assertEquals(0,aType1.getPredefinedValuesCodes().size());
		
		List<Object> codes = catAType.getPredefinedValuesCodes();
		for(int i=0; i<preVals.length; i++)
			Assert.assertEquals(i+1, codes.get(i));
	}

	@Test
	public void testGetPredefinedValueCode()
	{
		Assert.assertEquals(-1,aType1.getPredefinedValueCode("blub"));
		
		Assert.assertEquals(1,catAType.getPredefinedValueCode(preVals[0].toString()));
	}

	@Test
	public void testGetPredefinedValue()
	{
		for(int i=0; i<preVals.length; i++)
		{
			Assert.assertEquals(preVals[i], catAType.getPredefinedValue(i));
		}
	}

	@Test
	public void testSetPredefinedValue()
	{
		Object[] preVals2 = {"one","two"};
		
		aType1.setPredefinedValues(new Object[]{"blab","blub"});
		for(int i=0; i<preVals2.length; i++)
			aType1.setPredefinedValue(preVals2[i], i);
		
		for(int i=0; i<preVals2.length; i++)
			Assert.assertEquals(preVals2[i], aType1.getPredefinedValue(i));
	}

	@Test
	public void testSetPredefinedValues()
	{
		Object[] preVals2 = {"one","two"};
		aType1.setPredefinedValues(preVals2);
		for(int i=0; i<preVals2.length; i++)
			Assert.assertEquals(preVals2[i], aType1.getPredefinedValue(i));
	}

	@Test
	public void testGetDefaultValue()
	{
		catAType.setDefaultValue(preVals[1]);
		Assert.assertEquals(preVals[1], catAType.getDefaultValue());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSetDefaultValue()
	{
		catAType.setDefaultValue(preVals[1]);
		Assert.assertEquals(preVals[1], catAType.getDefaultValue());

		catAType.setDefaultValue(null);
		Assert.assertNull(catAType.getDefaultValue());
		
		catAType.setDefaultValue("blab"); //Exception
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsFirst()
	{
		for(int i=0; i<preVals.length; i++)
		{
			Assert.assertEquals((i==0),(boolean)catAType.isFirst(preVals[i]));
		}	
		
		try
		{
			aType1.isFirst("testWert");
			Assert.fail("No Exception raised!");
		}
		catch(IllegalArgumentException e)
		{
			
		}
		aType1.setPredefinedValues(new Object[0]);
		aType1.isFirst("testWert");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIsLast()
	{
		for(int i=0; i<preVals.length; i++)
		{
			Assert.assertEquals((i==(preVals.length-1)),(boolean)catAType.isLast(preVals[i]));
		}
		
		try
		{
			aType1.isLast("testWert");
			Assert.fail("No Exception raised!");
		}
		catch(IllegalArgumentException e)
		{
			
		}
		
		aType1.setPredefinedValues(new Object[0]);
		aType1.isLast("testWert");
	}

	@Test
	public void testEqualsObject()
	{
		catAType.setDefaultValue(preVals[1]);
		catAType.setQuestion("Welchem ihrer Freunde wuerden sie Geld leihen?");
		catAType.setLabel("Geld");
		catAType.setScope(Scope.NETWORK);
		catAType.setDescription("Geld verleihen");
		catAType.setType("ACTOR");
		
		AttributeType catAType2 = catAType.clone();
		Assert.assertTrue(catAType.equals(catAType2)); //True
		
		catAType2 = catAType.clone();
		catAType2.setLabel(catAType.getLabel()+"-");
		Assert.assertFalse(catAType.equals(catAType2)); //False
		
		catAType2 = catAType.clone();
		catAType2.setDescription(catAType.getDescription()+"-");
		Assert.assertFalse(catAType.equals(catAType2)); //False
		
		catAType2 = catAType.clone();
		catAType2.setQuestion(catAType.getQuestion()+"-");
		Assert.assertFalse(catAType.equals(catAType2)); //False
		
		catAType2 = catAType.clone();
		catAType2.setType(catAType.getType()+"-");
		Assert.assertFalse(catAType.equals(catAType2)); //False
		
		catAType2 = catAType.clone();
		catAType2.setScope(Scope.PROJECT);
		Assert.assertFalse(catAType.equals(catAType2)); //False
		
		catAType2 = catAType.clone();
		catAType2.setPredefinedValues(null);
		Assert.assertFalse(catAType.equals(catAType2)); //False
		
		Assert.assertFalse(catAType2.equals("Hallo")); //False
		
		Assert.assertFalse(catAType.equals(aType1)); //False
		Assert.assertFalse(aType1.equals(catAType)); //False;
		
		AttributeType aType2 = aType1.clone();
		Assert.assertTrue(aType1.equals(aType2));   //True
		
		aType1.setPredefinedValues(new Object[0]);
		Assert.assertFalse(catAType.equals(aType1)); //False
		Assert.assertFalse(aType1.equals(aType2));	//False
		Assert.assertFalse(aType2.equals(aType1));	//False
		Assert.assertFalse(catAType.equals(aType1)); //False
		
		aType1.setPredefinedValues(null);
		Assert.assertTrue(aType1.equals(aType2));	//False
		Assert.assertTrue(aType2.equals(aType1)); //False
		
		catAType2.setPredefinedValues(new Object[]{"bla","blub"});
		Assert.assertFalse(catAType.equals(catAType2));
		Assert.assertFalse(catAType2.equals(catAType));
		
		catAType.setPredefinedValues(new Object[]{"bla","blub1"});
		Assert.assertFalse(catAType.equals(catAType2));
	}

	@Test
	public void testClone()
	{
		catAType.setDefaultValue(preVals[1]);
		catAType.setQuestion("Welchem ihrer Freunde wuerden sie Geld leihen?");
		catAType.setLabel("Geld");
		catAType.setScope(Scope.NETWORK);
		catAType.setDescription("Geld verleihen");
		catAType.setType("ACTOR");
		
		AttributeType catAType2 = catAType.clone();
		
		Assert.assertEquals(catAType2.getDefaultValue(), catAType.getDefaultValue());
		
		Object[] preVals2 = catAType2.getPredefinedValues();
		for(int i=0; i<preVals2.length; i++)
		{
			Assert.assertEquals(preVals2[i], preVals[i]);
		}
		
		Assert.assertEquals(catAType2.getQuestion(), catAType.getQuestion());
		Assert.assertEquals(catAType2.getLabel(), catAType.getLabel());
		Assert.assertEquals(catAType2.getDescription(), catAType.getDescription());
		Assert.assertEquals(catAType2.getType(), catAType.getType());
		Assert.assertEquals(catAType2.getScope(), catAType.getScope());
		Assert.assertNotSame(catAType2.getId(), catAType.getId());
		
		Assert.assertNotSame(catAType2, catAType);
	}

	@Test
	public void testChangeTo()
	{
		catAType.setDefaultValue(preVals[1]);
		catAType.setQuestion("Welchem ihrer Freunde wuerden sie Geld leihen?");
		catAType.setLabel("Geld");
		catAType.setScope(Scope.NETWORK);
		catAType.setDescription("Geld verleihen");
		catAType.setType("ACTOR");
		
		aType1.changeTo(catAType);
		
		Assert.assertEquals(aType1.getDefaultValue(), catAType.getDefaultValue());
		
		Object[] preVals2 = aType1.getPredefinedValues();
		for(int i=0; i<preVals2.length; i++)
		{
			Assert.assertEquals(preVals2[i], preVals[i]);
		}
		
		Assert.assertEquals(aType1.getQuestion(), catAType.getQuestion());
		Assert.assertEquals(aType1.getLabel(), catAType.getLabel());
		Assert.assertEquals(aType1.getDescription(), catAType.getDescription());
		Assert.assertEquals(aType1.getType(), catAType.getType());
		Assert.assertEquals(aType1.getScope(), catAType.getScope());
		Assert.assertNotSame(aType1.getId(), catAType.getId());
		
		Assert.assertNotSame(aType1, catAType);
	}

	@Test
	public void testGetTransferData()
	{
		try
		{
			Assert.assertEquals(catAType, catAType.getTransferData(null));
		}
		catch(Exception e)
		{
			fail("Exception raised!");
		}
	}

	@Test
	public void testGetTransferDataFlavors()
	{
		Assert.assertEquals(catAType.getTransferDataFlavors(),AttributeCheckBox.flavors);
	}

	@Test
	public void testIsDataFlavorSupported()
	{
		Assert.assertTrue(catAType.isDataFlavorSupported(AttributeCheckBox.ATTRIBUTE_FLAVOR));
	}

}
