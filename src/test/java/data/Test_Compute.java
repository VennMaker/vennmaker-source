package data;

import static org.junit.Assert.fail;
import gui.VennMaker;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test_Compute
{

	public static final String	ATTRIBUTE_COLLECTOR	= "ScienceRelations";

	private Relation				testRelation;

	private Netzwerk				network;

	private AttributeType		type;

	private AttributeType		atype;

	private Akteur					actor1;

	private Akteur					actor2;

	private Akteur					actor3;

	private Akteur					actor4;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

	}

	@Before
	public void setUp() throws Exception
	{

		actor1 = new Akteur("Hans");
		actor2 = new Akteur("Curie");
		actor3 = new Akteur("Tesla");
		actor4 = new Akteur("Newton");

		network = VennMaker.getInstance().getProject().getCurrentNetzwerk();

		String[] predefinedAkteurValues = new String[3];
		predefinedAkteurValues[0] = "Koeln";
		predefinedAkteurValues[1] = "Trier";
		predefinedAkteurValues[2] = "Hamburg";

		atype = new AttributeType();
		atype.setType("ACTOR");
		atype.setLabel("Wohnort");
		atype.setPredefinedValues(predefinedAkteurValues);

		type = new AttributeType();

		type.setLabel("TestType2");
		type.setType("RELATION");

		String[] predefValues = { "positive", "negatvie", "neutral" };
		type.setPredefinedValues(predefValues);

		HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
		temp.put(type.getType(), true);
		Vector<AttributeType> allTypes = new Vector<AttributeType>();

		allTypes.add(type);
		allTypes.add(atype);

		VennMaker.getInstance().getProject().setAttributeTypes(allTypes);
		VennMaker.getInstance().getProject()
				.setIsDirectedAttributeCollection(temp);

		this.network.addAkteur(actor1);
		this.network.addAkteur(actor2);
		this.network.addAkteur(actor3);
		this.network.addAkteur(actor4);

		// Actor1 --> Actor2
		testRelation = new Relation(actor2, ATTRIBUTE_COLLECTOR);
		testRelation.setAttributeCollectorValue(type.getType());
		testRelation.setAttributeValue(this.type, this.network,
				this.type.getPredefinedValue(1));
		actor1.addRelation(this.network, testRelation);

		// Actor1 --> Actor3
		testRelation = new Relation(actor3, ATTRIBUTE_COLLECTOR);
		testRelation.setAttributeCollectorValue(type.getType());
		testRelation.setAttributeValue(this.type, this.network,
				this.type.getPredefinedValue(1));
		actor1.addRelation(this.network, testRelation);

		// Actor1 --> Actor4
		testRelation = new Relation(actor4, ATTRIBUTE_COLLECTOR);
		testRelation.setAttributeCollectorValue(type.getType());
		testRelation.setAttributeValue(this.type, this.network,
				this.type.getPredefinedValue(0));
		actor1.addRelation(this.network, testRelation);

		// Actor4 --> Actor1
		testRelation = new Relation(actor1, ATTRIBUTE_COLLECTOR);
		testRelation.setAttributeCollectorValue(type.getType());
		testRelation.setAttributeValue(this.type, this.network,
				this.type.getPredefinedValue(2));
		actor4.addRelation(this.network, testRelation);

		// Ego ist mit keinem Akteur verbunden
		actor1.setAttributeValue(atype, this.network, atype.getPredefinedValue(1));
		actor2.setAttributeValue(atype, this.network, atype.getPredefinedValue(0));
		actor3.setAttributeValue(atype, this.network, atype.getPredefinedValue(1));
		actor4.setAttributeValue(atype, this.network, atype.getPredefinedValue(2));

	}

	@After
	public final void tearDown()
	{

		this.network.removeAkteur(actor1);
		this.network.removeAkteur(actor2);
		this.network.removeAkteur(actor3);
		this.network.removeAkteur(actor4);

	}

	@Test
	public void testComputeNetzwerkCollectionOfAkteur()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testComputeNetzwerkCollectionOfAkteurBoolean()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetAdjacencyMatrix()
	{
		String result = "";
		Compute c = new Compute(this.network, this.network.getAkteure());
		int[][] matrix = c.getAdjacencyMatrix();
		for (int q = 0; q < matrix.length; q++)
		{
			for (int w = 0; w < matrix.length; w++)
			{
				result = result + matrix[w][q];
			}
		}
		Assert.assertEquals("0000000111000000000001000", result);
	}

	@Test
	public void testOutDegree()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		int wert = c.outDegree(this.actor1);

		Assert.assertEquals(3, wert);
	}

	@Test
	public void testOutDegreeStd()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.outDegreeStd(this.actor1);

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(0.750, myDec.doubleValue());
	}

	@Test
	public void testInDegree()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		int wert = c.inDegree(this.actor1);
		Assert.assertEquals(1, wert);
	}

	@Test
	public void testInDegreeStd()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.inDegreeStd(this.actor1);

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(0.250, myDec.doubleValue());

	}

	@Test
	public void testDensityWithEgo()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.densityWithEgo();

		Assert.assertEquals(0.2, wert);

	}

	@Test
	public void testDensityWithOutEgo()
	{
		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.densityWithOutEgo(this.network.getEgo());

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(0.334, myDec.doubleValue());
	}

	@Test
	public void testCalculateDistancesFromActor()
	{

		HashMap<Akteur, Integer> distances = new HashMap<Akteur, Integer>();

		Compute c = new Compute(this.network, this.network.getAkteure());

		distances = c.calculateDistancesFromActor(actor4);

		int distanzen = 0;
		for (int it : distances.values())
		{
			distanzen = distanzen + it;
		}
		Assert.assertEquals(5, distanzen);
	}

	@Test
	public void testCalculateDistancesToActor()
	{

		HashMap<Akteur, Integer> distances = new HashMap<Akteur, Integer>();

		Compute c = new Compute(this.network, this.network.getAkteure());

		distances = c.calculateDistancesToActor(actor1);

		int distanzen = 0;
		for (int it : distances.values())
		{
			distanzen = distanzen + it;
		}

		Assert.assertEquals(1, distanzen);
	}

	@Test
	public void testInCloseness()
	{
		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.inCloseness(this.actor4);

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(1.0, myDec.doubleValue());
	}

	@Test
	public void testOutCloseness()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.outCloseness(this.actor4);

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(5.0, myDec.doubleValue());
	}

	@Test
	public void testProximityPrestige()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.proximityPrestige(this.actor1);

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(0.25, myDec.doubleValue());
	}

	@Test
	public void testOutClosenessStd()
	{

		Compute c = new Compute(this.network, this.network.getAkteure());

		double wert = c.outClosenessStd(this.actor1);

		BigDecimal myDec = new BigDecimal(wert);
		myDec = myDec.setScale(3, BigDecimal.ROUND_UP);

		Assert.assertEquals(0.75, myDec.doubleValue());
	}

	@Test
	public void testAttributeQuantities()
	{
		Compute c = new Compute(this.network, this.network.getAkteure());

		HashMap<AttributeType, HashMap<String, Integer>> wert = c
				.attributeQuantities(this.network);

		String ausgabe = "";
		for (AttributeType at : wert.keySet())
		{
			for (String s : wert.get(at).keySet())
			{
				ausgabe = ausgabe + at.toString() + "[" + s + "]"
						+ wert.get(at).get(s).toString();
			}
		}

		// System.out.println(ausgabe);
		Assert.assertEquals("Wohnort[Trier]2Wohnort[Koeln]1Wohnort[Hamburg]1",
				ausgabe);
	}

	@Test
	public void testAttributeQuantities_allActors()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetAttributeCounter()
	{
		Compute c = new Compute(this.network, this.network.getAkteure());

		HashMap<AttributeType, HashMap<String, Integer>> wert = c
				.attributeQuantities(this.network);

		int anzahl = c.getAttributeCounter();

		Assert.assertEquals(3, anzahl);
	}

	@Test
	public void testCountRelations()
	{
		Compute c = new Compute(this.network, this.network.getAkteure());
		HashMap<Object, Integer> wert = c.countRelations();

		String ausgabe = "";
		for (Object at : wert.keySet())
		{
			ausgabe = ausgabe + at;
		}

		Assert.assertEquals(
				"TestType2[neutral]TestType2[negatvie]TestType2[positive]", ausgabe);
	}

}
