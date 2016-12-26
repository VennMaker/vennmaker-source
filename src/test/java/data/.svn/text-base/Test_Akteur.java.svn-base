package data;

import static org.junit.Assert.fail;
import gui.VennMaker;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.AttributeType.Scope;

public class Test_Akteur
{
	private static VennMaker		instance;

	private Netzwerk					n1;

	private Netzwerk					n2;

	private Akteur						a1;

	private Akteur						a2;

	private AttributeType			aType;

	private static AttributeType	aTypeImportance;

	@BeforeClass
	public static void setUpClass() throws Exception
	{
		instance = VennMaker.getInstance();

		for (AttributeType a : instance.getProject().getAttributeTypes("ACTOR"))
		{
			/*
			 * WORKAROUND: Wenn Attribute anhand ihrer ID bestimmt werden koennen,
			 * diese verwenden! bis dahin greift das Workaround auf die aktuelle
			 * Sprache zu und fragt dementsprechend nach "Importance" oder aber
			 * "Wichtigkeit" (sollten weitere Entwickler mit anderen
			 * Spracheinstellungen testen wollen, muss dieses erweitert werden...
			 * alleine deswegen sollte schon auf IDs zurueckgegriffen werden...
			 */
			String importanceByLanguage = (Locale.getDefault() == Locale.GERMANY ? "Wichtigkeit"
					: "Importance");

			if (a.getLabel().equals(importanceByLanguage))
				aTypeImportance = a;
		}
	}

	@Before
	public void setUp() throws Exception
	{
		n1 = new Netzwerk();
		n2 = new Netzwerk();

		Map<Object, Integer> sizes = new HashMap<Object, Integer>();
		int i = 0;
		for (Object o : aTypeImportance.getPredefinedValues())
			sizes.put(o, ++i * 10);

		n1.getActorSizeVisualizer().setAttributeType(aTypeImportance);
		n2.getActorSizeVisualizer().setAttributeType(aTypeImportance);
		n1.getActorSizeVisualizer().setSizes(sizes);
		n2.getActorSizeVisualizer().setSizes(sizes);

		a1 = new Akteur("Heisenberg");
		a2 = new Akteur("Pinkman");

		a1.setLocation(n1, new Point2D.Double(10.0, 10.0));
		a1.setLocation(n2, new Point2D.Double(1.0, 1.0));

		a2.setLocation(n1, new Point2D.Double(5.0, 5.0));
		a2.setLocation(n2, new Point2D.Double(0.0, 0.0));

		aType = new AttributeType();
		aType.setScope(Scope.NETWORK);
		aType.setType("ACTOR");
		aType.setLabel("TestAType");
		aType.setDescription("TestAType");
		String[] predefinedValues = { "A", "B", "C" };
		aType.setPredefinedValues(predefinedValues);
		VennMaker.getInstance().getProject().getAttributeTypes().add(aType);

		a1.setAttributeValue(aType, n1, "C");
		a1.setAttributeValue(aType, n2, "B");

		a2.setAttributeValue(aType, n1, "A");
		a2.setAttributeValue(aType, n2, "C");
	}

	@Test
	public void testCopyNetworkPropertiesLocation()
	{
		a1.copyNetworkProperties(n1, n2);

		Assert.assertEquals(10.0, a1.getLocation(n2).getX());
	}

	@Test
	public void testCopyNetworkPropertiesAttribute()
	{
		a1.copyNetworkProperties(n1, n2);

		Assert.assertEquals("C", a1.getAttributeValue(aType, n2));
	}

	@Test
	public void testMergeNetworkProperties()
	{
		a1.mergeNetworkProperties(n1, n2);
		Assert.assertEquals(a1.getAttributeValue(aType, n1),
				a1.getAttributeValue(aType, n2));
	}

	@Test
	public void testCopyVirtualNetworkProperties()
	{
		a1.setAttributeValue(aTypeImportance, n1,
				aTypeImportance.getPredefinedValues()[0]);
		a1.setAttributeValue(aTypeImportance, n2,
				aTypeImportance.getPredefinedValues()[1]);
		a1.copyVirtualNetworkProperties(n1, n2);

		Assert.assertEquals("Copying not successful",
				a1.getAttributeValue(aTypeImportance, n1),
				a1.getAttributeValue(aTypeImportance, n2));
	}

	@Test
	public void testGetLocation()
	{
		Assert.assertEquals("Location of a1 in n1 not set correctly",
				new Point2D.Double(10.0, 10.0), a1.getLocation(n1));
	}

	@Test
	public void testSetLocation()
	{
		Akteur aX = new Akteur("testSetLocation");
		aX.addNetzwerk(n1);

		aX.setLocation(n1, new Point2D.Double(15.0, 13.0));

		Assert.assertEquals("aX hat keine neue Position erhalten (n1)",
				new Point2D.Double(15.0, 13.0), aX.getLocation(n1));
	}

	@Test
	/** test, when asserts enabled */
	public void testSetLocationNetworkNull()
	{
		Akteur aX = new Akteur("testSetLocationWithNoNetwork");
		aX.addNetzwerk(n1);

		try
		{
			aX.setLocation(null, new Point2D.Double(15.0, 13.0));
		} catch (Exception e)
		{
		}
	}

	@Test
	/** test, when asserts enabled */
	public void testSetLocationAllNull()
	{
		Akteur aX = new Akteur("testSetLocationWithNothing");

		try
		{
			aX.setLocation(null, null);
		} catch (Exception e)
		{
		}
	}

	@Test
	/** test, when asserts enabled */
	public void testSetLocationPointNull()
	{
		Akteur aX = new Akteur("testSetLocationWithNoNetwork");
		aX.addNetzwerk(n1);

		try
		{
			aX.setLocation(n1, null);
		} catch (Exception e)
		{
		}
	}

	@Test
	public void testAkteurString()
	{
		Akteur aX = new Akteur("xxx");
		Assert.assertEquals(aX.toString(), "xxx");
	}

	@Test
	public void testAkteur()
	{
		Akteur a = new Akteur();
		Assert.assertNotNull(a);
	}

	@Test
	public void testToString()
	{
		Akteur a = new Akteur("Hans");
		Assert.assertEquals("Hans", a.toString());
	}

	@Test
	public void testToString_noName()
	{
		Akteur a = new Akteur();
		Assert.assertEquals("", a.toString());
	}

	@Test
	public void testRegisterEventListeners()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetId()
	{
		Akteur act1 = new Akteur();
		Akteur act2 = new Akteur();
		Assert.assertEquals(act1.getId() + 1, act2.getId());
	}

	@Test
	public void testGetId_notEqual()
	{
		Akteur act1 = new Akteur();
		Assert.assertNotSame(act1.getId(), a1.getId());
	}

	@Test
	public void testGetNetzwerke()
	{
		Netzwerk n = new Netzwerk();
		n.setId(-1);
		Akteur a = new Akteur("Hans");
		a.addNetzwerk(n);
		Set<Netzwerk> netzwerke = a.getNetzwerke();
		Assert.assertTrue("", netzwerke.contains(n));
	}

	@Test
	public void testAddNetzwerk()
	{
		Netzwerk n = new Netzwerk();
		n.setId(1);
		Akteur a = new Akteur("Hans");
		a.addNetzwerk(n);
		Set<Netzwerk> netzwerke = a.getNetzwerke();
		Assert.assertTrue("addNetzwerk() hat das Netzwerk nicht hinzugefuegt.",
				netzwerke.contains(n));
	}

	@Test
	public void testRemoveNetzwerk()
	{
		Netzwerk n = new Netzwerk();
		n.setId(1);
		Netzwerk n2 = new Netzwerk();
		n.setId(2);

		Akteur a = new Akteur("Hans");
		a.addNetzwerk(n);
		a.addNetzwerk(n2);

		a.removeNetzwerk(n);

		Set<Netzwerk> netzwerke = a.getNetzwerke();
		Assert.assertFalse("Netzwerk nicht geloescht in removeNetzwerk()",
				netzwerke.contains(n));
	}

	@Test
	public void testGetName()
	{
		Akteur a = new Akteur("Hans");
		Assert.assertEquals("Hans", a.getName());
	}

	@Test
	public void testGetEmptyName()
	{
		Akteur a = new Akteur();
		Assert.assertEquals("", a.getName());
	}

	@Test
	public void testSetName()
	{
		Akteur a = new Akteur();
		a.setName("Hans");
		Assert.assertEquals("Hans", a.getName());
	}

	@Test
	public void testSetNewName()
	{
		Akteur a = new Akteur("Peter");
		a.setName("Hans");
		Assert.assertEquals("Hans", a.getName());
	}

	@Test
	public void testGetRelations_Null()
	{
		Akteur a = new Akteur("Peter");
		a.addNetzwerk(n1);

		Assert.assertEquals("There are relations, although none has been added",
				0, a.getRelations(n1).size());
	}

	@Test
	public void testGetRelations()
	{
		Akteur a = new Akteur("Peter");
		a.addNetzwerk(n1);

		Akteur b = new Akteur("Peter");
		b.addNetzwerk(n1);

		Relation r = new Relation(b, "relation to b");
		a.getRelations(n1).add(r);

		Assert.assertEquals(
				"There are no relations, although one has been added", 1, a
						.getRelations(n1).size());
	}

	@Test
	public void testGetAnswer()
	{
		Akteur a = new Akteur("Gibson");
		a.saveAnswer("Hallo", "Welt");

		Assert.assertEquals(a.getAnswer("Hallo"), "Welt");
	}

	/**
	 * tests, if there's a map to put answers into. If not, fail.
	 */
	@Test
	public void testSaveAnswer()
	{
		Akteur a = new Akteur("Joe");
		try
		{
			a.saveAnswer("alpha", "beta");
		} catch (Throwable expected)
		{
			fail("no map to store the answer.. ");
		}
	}

	@Test
	public void testGetRelationToAkteurNetzwerk_Null()
	{
		Akteur a = new Akteur("Mister Pink");
		Akteur b = new Akteur("Einfach Pink");
		Akteur c = new Akteur("Yo");

		a.addNetzwerk(n1);
		b.addNetzwerk(n1);
		c.addNetzwerk(n1);

		Relation r = new Relation(c, "Relation to c");
		a.getRelations(n1).add(r);

		Assert.assertNull("Relation to b, although never added one",
				a.getRelationTo(b, n1));
	}

	@Test
	public void testGetRelationToAkteurNetzwerk_NotNull()
	{
		Akteur a = new Akteur("Mister Pink");
		Akteur b = new Akteur("Einfach Pink");

		a.addNetzwerk(n1);
		b.addNetzwerk(n1);

		Relation r = new Relation(b, "Relation to b");
		a.getRelations(n1).add(r);

		Assert.assertNotNull("No relation to b, although added",
				a.getRelationTo(b, n1));
	}

	@Test
	public void testGetRelationToAkteurNetzwerkAttributeType()
	{
		Akteur a = new Akteur("Mister Pink");
		Akteur b = new Akteur("Einfach Pink");
		Akteur c = new Akteur("Pink halt");

		a.addNetzwerk(n1);
		b.addNetzwerk(n1);
		c.addNetzwerk(n1);

		Relation r = new Relation(n1, b, aType, aType.getPredefinedValue(0));
		/* test for getRelation of b with c as third actor */
		Relation r2 = new Relation(n1, c, aType, aType.getPredefinedValue(0));
		/* test for other attributetype */
		Relation r3 = new Relation(n1, b, aTypeImportance,
				aTypeImportance.getPredefinedValue(0));

		a.getRelations(n1).add(r2);
		a.getRelations(n1).add(r3);
		a.getRelations(n1).add(r);

		Assert.assertNotNull("No relation to b, although added",
				a.getRelationTo(b, n1, aType));
	}

	@Test
	public void testGetRelationToAkteurNetzwerkAttributeType_Null()
	{
		Akteur a = new Akteur("Mister Pink");
		Akteur b = new Akteur("Einfach Pink");

		a.addNetzwerk(n1);
		b.addNetzwerk(n1);

		Assert.assertNull("Relation to b, although none added",
				a.getRelationTo(b, n1, aType));
	}

	@Test
	public void testGetCircleNoCircle()
	{
		Netzwerk n3 = new Netzwerk();

		Akteur a = new Akteur("SektorTestAkteur");
		n3.addAkteur(a);

		Assert.assertEquals(a.getCircle(n3), -1);
	}

	@Test
	public void testGetCircle()
	{
		Netzwerk n3 = new Netzwerk();

		Akteur a = new Akteur("SektorTestAkteur");
		n3.addAkteur(a);

		ArrayList<String> circles = new ArrayList<String>();

		circles.add("alpha");
		circles.add("beta");
		circles.add("gamma");

		a.setLocation(n3, new Point(50, 50));
		n3.getHintergrund().setCircles(circles);

		Assert.assertEquals("getCircle returns the wrong circle",
				a.getCircle(n3), 2);
	}

	@Test
	public void testGetCircleHighDistance()
	{
		Netzwerk n3 = new Netzwerk();

		ArrayList<String> circles = new ArrayList<String>();

		circles.add("alpha");
		circles.add("beta");
		circles.add("gamma");

		Akteur a = new Akteur("KreisTestAkteur");
		n3.addAkteur(a);

		/* Place actor at (5000, 5000) to make sure, it's not in a circle */
		a.setLocation(n3, new Point(5000, 5000));
		n3.getHintergrund().setCircles(circles);

		Assert.assertEquals(a.getCircle(n3), -1);
	}

	@Test
	public void testGetCircleActorInMiddle()
	{
		Netzwerk n3 = new Netzwerk();

		ArrayList<String> circles = new ArrayList<String>();

		circles.add("alpha");
		circles.add("beta");
		circles.add("gamma");

		Akteur a = new Akteur("KreisTestAkteur");
		n3.addAkteur(a);

		a.setLocation(n3, new Point(0, 0));
		n3.getHintergrund().setCircles(circles);

		Assert.assertEquals(
				"actor is in the middle, but getCircle() doesn't return the corresponding circle (1)",
				a.getCircle(n3), 1);
	}

	@Test
	public void testGetSectorNoSector()
	{
		Netzwerk n3 = new Netzwerk();

		Akteur a = new Akteur("SektorTestAkteur");
		n3.addAkteur(a);

		Assert.assertEquals(a.getSector(n3), -1);
	}

	@Test
	public void testGetSector()
	{
		Netzwerk n3 = new Netzwerk();

		n3.getHintergrund().setSectorAttribute(aType);

		Akteur a = new Akteur("SektorTestAkteur");
		n3.addAkteur(a);

		a.setLocation(n3, new Point(0, 2));

		Assert.assertEquals(a.getSector(n3), 2);
	}

	@Test
	public void testGetSectorNegative()
	{
		Netzwerk n3 = new Netzwerk();

		n3.getHintergrund().setSectorAttribute(aType);

		Akteur a = new Akteur("SektorTestAkteur");
		n3.addAkteur(a);

		a.setLocation(n3, new Point(-1, -2));

		Assert.assertEquals(a.getSector(n3), 1);
	}

	@Test
	public void testGetSectorHighValue()
	{
		Netzwerk n3 = new Netzwerk();

		n3.getHintergrund().setSectorAttribute(aType);

		Akteur a = new Akteur("SektorTestAkteur");
		n3.addAkteur(a);

		/* assert, that vectorlength is really high */
		a.setLocation(n3, new Point(5000, 5000));

		Assert.assertEquals(a.getSector(n3), -1);
	}

	@Test
	public void testGetAttributeValue()
	{
		Akteur a = new Akteur("Ja Rule");
		n1.addAkteur(a);
		/* make sure, there is an attributevalue to return */
		a.setAttributeValue(aType, n1, aType.getDefaultValue());

		Assert.assertEquals("returned attributetype either not equal or null",
				a.getAttributeValue(aType, n1), aType.getDefaultValue());
	}

	/**
	 * tests, if getAttributeValue always returns <code>null</code>, when there's
	 * no attribute given
	 */
	@Test
	public void testGetAttributeValueNull()
	{
		Akteur a = new Akteur("BigD");
		n1.addAkteur(a);

		Assert.assertNull(a.getAttributeValue(null, n1));
	}

	@Test
	public void testGetAttributeValueNetworkNull()
	{
		Akteur a = new Akteur("BigD");
		n1.addAkteur(a);

		Assert.assertNull(a.getAttributeValue(aType, n1));
	}

	@Test
	public void testSetAttributeValue()
	{
		Netzwerk n1 = new Netzwerk();

		Vector<AttributeType> at = VennMaker.getInstance().getProject()
				.getAttributeTypes();
		AttributeType attributeTypeTest = new AttributeType();
		attributeTypeTest.setScope(Scope.NETWORK);
		attributeTypeTest.setType("ACTOR");
		String[] predefinedValues = { "A", "B", "C" };
		attributeTypeTest.setPredefinedValues(predefinedValues);
		at.add(attributeTypeTest);

		Akteur a = new Akteur("Heisenberg");
		a.setAttributeValue(attributeTypeTest, n1, "C");

		Assert.assertEquals("C", a.getAttributeValue(attributeTypeTest, n1));
	}

	@Test
	public void testGetAttributes()
	{
		Akteur a = new Akteur("Jupp");
		n1.addAkteur(a);
		a.setAttributeValue(aType, n1, aType.getDefaultValue());

		Assert.assertEquals(
				"attributetype either not set correctly or not returned correctly",
				a.getAttributes(n1).get(aType), aType.getDefaultValue());

	}

	/**
	 * tests if getAttributes() still returns a new Map, when there are no
	 * attributes to return
	 */
	@Test
	public void testGetAttributesNull()
	{
		Akteur a = new Akteur("Jupp");

		Assert.assertEquals("did not return a HashMap, but something else", a
				.getAttributes(n1).getClass(), HashMap.class);
	}

	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObject()
	{
		a1.setAttributeValue(aType, n1, "A");
		Assert.assertEquals("A", a1.getAttributeValue(aType, n1));
	}

	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectVectorOfNetzwerk()
	{
		Akteur a = new Akteur("alpha");

		a.addNetzwerk(n1);
		a.addNetzwerk(n2);

		n1.addAkteur(a);
		n2.addAkteur(a);

		Vector<Netzwerk> networks = new Vector<Netzwerk>();

		networks.add(n1);
		networks.add(n2);

		a.setAttributeValue(aType, n1, aTypeImportance.getPredefinedValue(0),
				networks);

		Assert.assertEquals(a.getAttributeValue(aType, n1),
				aTypeImportance.getPredefinedValue(0));
	}

	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectVectorOfNetzwerkProjectScope()
	{
		Akteur a = new Akteur("alpha");

		AttributeType at = new AttributeType();
		Object[] predefinedValues = new Object[] { "a", "b", "c" };

		at.setType("ACTOR");
		at.setPredefinedValues(predefinedValues);
		at.setDefaultValue(predefinedValues[0]);
		at.setDescription("testAttributeType(Scope.PROJECT)");
		at.setLabel("testAttributeType");
		at.setQuestion("Why? How? Who?");
		at.setScope(Scope.PROJECT);

		a.addNetzwerk(n1);
		a.addNetzwerk(n2);

		n1.addAkteur(a);
		n2.addAkteur(a);

		Vector<Netzwerk> networks = new Vector<Netzwerk>();

		networks.add(n1);
		networks.add(n2);

		a.setAttributeValue(at, n1, at.getPredefinedValue(0), networks);

		Assert.assertEquals(a.getAttributeValue(aType, n1),
				a.getAttributeValue(aType, n2));
	}

	@Test
	public void testSetAttributeValueAttributeTypeNetzwerkObjectVectorOfNetzwerkProjectScopeAllNetworksSet()
	{
		Akteur a = new Akteur("alpha");

		AttributeType at = new AttributeType();
		Object[] predefinedValues = new Object[] { "a", "b", "c" };

		at.setType("ACTOR");
		at.setPredefinedValues(predefinedValues);
		at.setDefaultValue(predefinedValues[0]);
		at.setDescription("testAttributeType(Scope.PROJECT)");
		at.setLabel("testAttributeType");
		at.setQuestion("Why? How? Who?");
		at.setScope(Scope.PROJECT);

		a.addNetzwerk(n1);
		a.addNetzwerk(n2);

		n1.addAkteur(a);
		n2.addAkteur(a);

		Vector<Netzwerk> networks = new Vector<Netzwerk>();

		networks.add(n1);
		networks.add(n2);

		a.setAttributeValue(at, n1, at.getPredefinedValue(0), networks);
		a.setAttributeValue(at, n2, at.getPredefinedValue(1), networks);

		Assert.assertEquals(a.getAttributeValue(aType, n1),
				a.getAttributeValue(aType, n2));
	}

	@Test
	public void testGetGroesseSameNet()
	{
		a1.addNetzwerk(n1);
		a2.addNetzwerk(n2);

		Assert.assertEquals(a1.getGroesse(n1), a1.getGroesse(n2));
	}

	@Test
	public void testGetGroesseTwoActors()
	{
		a1.addNetzwerk(n1);
		a2.addNetzwerk(n2);

		Assert.assertEquals(a1.getGroesse(n1), a1.getGroesse(n2));
	}

	@Test
	public void testGetGroesseDifferentSizes()
	{
		a1.setAttributeValue(aTypeImportance, n1,
				aTypeImportance.getPredefinedValue(0));
		a2.setAttributeValue(aTypeImportance, n1,
				aTypeImportance.getPredefinedValue(4));

		Assert.assertNotSame(a1.getGroesse(n1), a2.getGroesse(n1));
	}

	@Test
	public void testCompareTo()
	{
		Akteur a = new Akteur();
		Akteur b = new Akteur();

		Assert.assertEquals(0, a.compareTo(b));
	}

	@Test
	public void testCompareToName1NULL()
	{
		Akteur a = new Akteur();
		Akteur b = new Akteur();
		a.setName(null);

		Assert.assertEquals(0, a.compareTo(b));
	}

	@Test
	public void testCompareToName2NULL()
	{
		Akteur a = new Akteur();
		Akteur b = new Akteur();
		b.setName(null);

		Assert.assertEquals(0, a.compareTo(b));
	}

	@Test
	public void testCompareTo_notEqual()
	{
		Akteur a = new Akteur("Peter");
		Akteur b = new Akteur();

		Assert.assertEquals(5, a.compareTo(b));
	}

	@Test
	public void testRemoveAttributeValue()
	{
		Akteur a = new Akteur("Hannes");

		/* add attribute, to be able to remove an attribute */
		a.setAttributeValue(aType, n1, aType.getDefaultValue());
		a.removeAttributeValue(aType, n1);

		/* tests, if it really has been removed */
		Assert.assertNull(a.getAttributeValue(aType, n1));
	}

	@Test
	public void testSetGlobalId()
	{
		Akteur a = new Akteur();
		a.setGlobalId(5);

		Assert.assertEquals("GlobalId not set correctly", 5, a.getGlobalId());

	}

	@Test
	public void testGetGlobalId()
	{
		Akteur a = new Akteur();
		Assert.assertEquals("GlobalId not of expected value", (a.getId() + 1),
				a.getGlobalId());
	}

	@Test
	public void testResetGlobalID()
	{
		/* first, make sure, there are values added to the global ID */
		Akteur a = new Akteur();
		Akteur b = new Akteur();

		a.resetGlobalID();
		Assert.assertEquals(
				"after a reset of the global ID, the new value was not equal 1", 1,
				a.getGlobalId());
	}

}
