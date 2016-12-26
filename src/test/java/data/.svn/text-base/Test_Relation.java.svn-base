package data;

import gui.VennMaker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.AttributeType.Scope;

public class Test_Relation
{

	public static final String	ATTRIBUTE_COLLECTOR	= "ScienceRelations";

	private Relation				testRelation;
	
	private RelationTyp			relationType;

	private Netzwerk				network;

	private AttributeType		type;

	private Akteur					actor1;

	private Akteur					actor2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

	}

	@Before
	public void setUp() throws Exception
	{
		actor1 = new Akteur("Einstein");
		actor2 = new Akteur("Tesla");

		network = VennMaker.getInstance().getProject().getCurrentNetzwerk();
		type = new AttributeType();
		type.setLabel("TestType");
		String[] predefValues = { "positive", "negatvie", "neutral" };
		type.setPredefinedValues(predefValues);
		
		relationType = new RelationTyp("RelationType");
		
		testRelation = new Relation(actor2, ATTRIBUTE_COLLECTOR);
		testRelation.setTyp(relationType);
		
	}

	@Test
	public void testRelationAkteurString()
	{
		Relation relation = new Relation(actor2, ATTRIBUTE_COLLECTOR);

		Assert.assertNotNull(relation);
	}

	@Test
	public void testRelationNetzwerkAkteurAttributeTypeObject()
	{

		Relation relation = new Relation(network, actor1, type, "positive");

		Assert.assertNotNull(relation);
	}

	@Test
	public void testGetAkteur()
	{
		Assert.assertEquals(actor2, testRelation.getAkteur());
	}

	@Test
	public void testSetAkteur()
	{
		testRelation.setAkteur(actor2);

		Assert.assertEquals(actor2, testRelation.getAkteur());
	}

	@Test
	public void testGetTyp()
	{
		Assert.assertEquals(testRelation.getTyp(), relationType);
	}

	@Test
	public void testSetTyp()
	{
		RelationTyp testType = new RelationTyp("Test");
		testRelation.setTyp(testType);
		Assert.assertEquals(testRelation.getTyp(), testType);
	}

	@Test
	public void testToString()
	{
		Assert.assertFalse(testRelation.toString().equals(""));
	}

	@Test
	public void testGetAnswer()
	{
		testRelation.saveAnswer("TestLabel", "TestAnswer");

		Assert.assertEquals(testRelation.getAnswer("TestLabel"), "TestAnswer");
	}

	@Test
	public void testSaveAnswer()
	{
		testRelation.saveAnswer("Label1", "LabelAnswer");

		Assert.assertEquals(testRelation.getAnswer("Label1"), "LabelAnswer");
	}

	@Test
	public void testGetAttributeValue()
	{
		Netzwerk net = VennMaker.getInstance().getProject().getNetzwerke().get(0);

		testRelation.setAttributeValue(type, net, "positive");

		Assert.assertEquals("positive", testRelation.getAttributeValue(type, net));
	}
	
	@Test
	public void testGetAttributeValueTypeNull()
	{
		Assert.assertEquals(testRelation.getAttributeValue(null, network), null);
	}
	
	@Test
	public void testGetAttributeValueNetworkNull()
	{
		testRelation.setAttributes(network, null);
		type.setDefaultValue("positive");
		
		Assert.assertEquals(testRelation.getAttributeValue(type, network), type.getDefaultValue());
		
	}
	
	@Test
	public void testReadResolveAttributesNotNull()
	{
		Assert.assertTrue(checkReadResolve(false));
	}
	
	@Test
	public void testReadResolveAttributesNull()
	{
		Assert.assertTrue(checkReadResolve(true));
	}
	

	@Test
	public void testGetAttributes()
	{
		Map<AttributeType, Object> attributeMap = new HashMap<AttributeType, Object>();
		attributeMap.put(type, "Test");
		testRelation.setAttributes(network, attributeMap);

		Assert.assertEquals(attributeMap.get(type),
				testRelation.getAttributes(network).get(type));
	}
	
	@Test
	public void testGetAttributesNullTest()
	{
		testRelation.setAttributes(network, null);
		
		Assert.assertEquals(testRelation.getAttributes(network).size(), 0);
	}

	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObject()
	{
		testRelation.setAttributeValue(type, network, "positive");

		Assert.assertEquals("positive",
				testRelation.getAttributeValue(type, network));
	}

	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectVectorOfNetzwerk()
	{

		Netzwerk network2 = new Netzwerk();
		Netzwerk network3 = new Netzwerk();
		Vector<Netzwerk> vector = new Vector<Netzwerk>();

		vector.add(network2);
		vector.add(network3);

		testRelation.setAttributeValue(type, network, "TestValue", vector);

		Object obj1 = testRelation.getAttributeValue(type, vector.get(0));
		Object obj2 = testRelation.getAttributeValue(type, vector.get(1));

		Assert.assertTrue(obj1.equals(obj2) && obj2.equals("TestValue"));

	}
	
	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectkWithScope()
	{

		type.setScope(Scope.NETWORK);
		
		testRelation.setAttributeValue(type, network, "positive");

		Assert.assertEquals("positive",
				testRelation.getAttributeValue(type, network));
	}
	
	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectWithScopeNotNull()
	{
		type.setScope(Scope.NETWORK);
		
		testRelation.setAttributeValue(type, network, "positive");
		testRelation.setAttributeValue(type, network, "negative");
		
		Assert.assertEquals("negative",
				testRelation.getAttributeValue(type, network));
	}
	
	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectVectorOfNetzwerkNotNull()
	{
		Netzwerk network2 = new Netzwerk();
		Netzwerk network3 = new Netzwerk();
		Vector<Netzwerk> vector = new Vector<Netzwerk>();

		vector.add(network2);
		vector.add(network3);

		testRelation.setAttributeValue(type, network, "TestValue", vector);
		testRelation.setAttributeValue(type, network, "NextTestValue", vector);

		Object obj1 = testRelation.getAttributeValue(type, vector.get(0));
		Object obj2 = testRelation.getAttributeValue(type, vector.get(1));

		Assert.assertTrue(obj1.equals(obj2) && obj2.equals("NextTestValue"));
	}

	@Test
	public void testGetGroesse()
	{
		testRelation = new Relation(actor2, ATTRIBUTE_COLLECTOR);
		testRelation.setTyp(relationType);
		
		this.type.setScope(Scope.NETWORK);
		
		Map<Object, Integer> sizes = new HashMap<Object, Integer>();

		for(Object obj : this.type.getPredefinedValues()){
			int value = ((int)(Math.random() * 10) + 11);
			sizes.put(obj, value);
		}

		this.network.getRelationSizeVisualizer(ATTRIBUTE_COLLECTOR, this.type).setAttributeType(this.type);
		this.network.getRelationSizeVisualizer(ATTRIBUTE_COLLECTOR, this.type).setSizes(sizes);
			
		testRelation.setAttributeValue(this.type, this.network, this.type.getPredefinedValues()[0]);	
		testRelation.setAttributeCollectorValue(ATTRIBUTE_COLLECTOR);
		
		int size = 	testRelation.getGroesse(this.network, this.type);
		Assert.assertTrue(size == sizes.get(this.type.getPredefinedValues()[0]));
	}

	@Test
	public void testRemoveAttributeValue()
	{

		Netzwerk network2 = new Netzwerk();
		this.type.setScope(Scope.NETWORK);

		testRelation.setAttributeValue(this.type, network2, this.type.getPredefinedValues()[0]);
		testRelation.removeAttributeValue(this.type, network2);

		Assert.assertTrue(testRelation.getAttributeValue(this.type, network2) == null);
	}

	@Test
	public void testGetAttributeCollectorValue()
	{
		Assert.assertEquals("ScienceRelations",
				testRelation.getAttributeCollectorValue());
	}

	@Test
	public void testSetAttributeCollectorValue()
	{
		testRelation.setAttributeCollectorValue("CollectorValue");

		Assert.assertEquals("CollectorValue",
				testRelation.getAttributeCollectorValue());
	}

	@Test
	public void testSetAttributes()
	{
		Map<AttributeType, Object> attributeMap = new HashMap<AttributeType, Object>();
		attributeMap.put(type, "Test");
		testRelation.setAttributes(network, attributeMap);

		Assert.assertEquals(attributeMap.get(type),
				testRelation.getAttributes(network).get(type));
	}
	
	private boolean checkReadResolve(boolean areAttributesNull)
	{
		Method readResolve = null;
		
		try
		{
			readResolve = Relation.class.getDeclaredMethod("readResolve", new Class<?>[0]);
		} 
		catch (NoSuchMethodException e)
		{
			return false;
		} 
		catch (SecurityException e)
		{
			return false;
		}
		
		readResolve.setAccessible(true);
		
		try
		{
			
			if(areAttributesNull)
			{
				Class<Relation> relation = Relation.class;
				Field attributes = relation.getDeclaredField("attributes");
				attributes.setAccessible(true);
				attributes.set(testRelation, null);
			}
			
			return testRelation.equals(readResolve.invoke(testRelation, new Object[0]));
			
//			Assert.assertEquals(testRelation, readResolve.invoke(testRelation, new Object[0]));
		} 
		catch (IllegalAccessException e)
		{
			Assert.fail(e.getMessage());
		} 
		catch (IllegalArgumentException e)
		{
			Assert.fail(e.getMessage());
		} 
		catch (InvocationTargetException e)
		{
			Assert.fail(e.getMessage());
		} 
		catch (SecurityException e)
		{
			Assert.fail(e.getMessage());
		} catch (NoSuchFieldException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
