package export;

import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Vector;

import gui.Messages;
import gui.VennMaker;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.Akteur;
import data.AttributeType;
import data.EventProcessor;
import data.Netzwerk;
import data.Relation;
import data.RelationTyp;
import events.ComplexEvent;
import events.DeleteActorEvent;
import events.NewActorEvent;

public class Test_ExportCSV
{
	
	public static final String	ATTRIBUTE_COLLECTOR	= "ScienceRelations";

	private Relation				testRelation;
	
	private Netzwerk				network;

	private AttributeType		type;

	private Akteur					actor1;

	private Akteur					actor2;
	private Akteur					actor3;
	private Akteur					actor4;
	
	private ExportCSV 	export;

	private AttributeType		atype;
	private Boolean firstStart = true;

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

	}

	@Before
	public void setUp() throws Exception
	{
		actor1 = new Akteur("Hans");
		actor1.resetGlobalID();
		actor2 = new Akteur("Curie");
		actor3 = new Akteur("Tesla");
		actor4 = new Akteur("Franz");

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
		//VennMaker.getInstance().getProject().setIsDirectedAttributeCollection(temp);

		this.network.addAkteur(actor1);
		this.network.addAkteur(actor2);
		this.network.addAkteur(actor3);
		

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


		// Ego ist mit keinem Akteur verbunden
		actor1.setAttributeValue(atype, this.network, atype.getPredefinedValue(1));
		actor2.setAttributeValue(atype, this.network, atype.getPredefinedValue(0));
		actor3.setAttributeValue(atype, this.network, atype.getPredefinedValue(1));	
		
		
		this.export = new ExportCSV();
		//testRelation.setTyp(relationType);
		
	}

	@Test
	public void testExportCSV()
	{
		Assert.assertNotNull(this.export);
	}

	@Test
	public void testGetAttributeValuesCodes()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Ego()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Alter()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Relationgroups()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Relationgroups_allActors()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Adjacency()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Adjacency_allActors()
	{
		
		ComplexEvent event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);
	
		String result = this.export.toCsv_Adjacency_allActors(this.network, ";", "\"", ",");

		String vergleich = "\"Netzwerkkarte\";\"EGO_0\";\"Hans_0\";\"Curie_1\";\"Tesla_2\";\"Franz_3\"\n\"EGO_0\";0;0;0;0;0\n\"Hans_0\";0;0;1;1;0\n\"Curie_1\";0;1;0;0;0\n\"Tesla_2\";0;1;0;0;0\n\"Franz_3\";0;0;0;0;0\n";
			
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);	
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);	
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);			
		
		Assert.assertEquals(vergleich,result);	}

	@Test
	public void testToCsv_AdjacencyAlteri()
	{
		
		ComplexEvent event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);		
	
		String result = this.export.toCsv_AdjacencyAlteri(this.network, ";", "\"", ",");
	
		String vergleich = "\"Netzwerkkarte\";\"Hans_0\";\"Curie_1\";\"Tesla_2\"\n\"Hans_0\";0;1;1\n\"Curie_1\";1;0;0\n\"Tesla_2\";1;0;0\n";
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);	
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);			
		
		Assert.assertEquals(vergleich,result);
		
	}	

	@Test
	public void testToCsv_AdjacencyAlteri_allAlteri()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetActorStatistics()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testGetActorStatistics_allActors()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Statistics()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_File_RelationType()
	{
		String vergleich = "\"Netzwerkkarte_TestType2\";\"EGO_0\";\"Hans_0\";\"Curie_1\";\"Tesla_2\"\n\"EGO_0\";\"\";\"\";\"\";\"\"\n\"Hans_0\";\"\";\"\";2;2\n\"Curie_1\";\"\";2;\"\";\"\"\n\"Tesla_2\";\"\";2;\"\";\"\"\n";

		ComplexEvent event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);		
		
		String result = this.export.toCsv_File_RelationType(this.network, ";", "\"", ",", this.type );
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);	
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);
		
		Assert.assertEquals(vergleich,result);
	}

	@Test
	public void testToCsv_File_RelationType_allActors()
	{
		
		String vergleich = "\"Netzwerkkarte_TestType2\";\"EGO_0\";\"Hans_0\";\"Curie_1\";\"Tesla_2\";\"Franz_3\"\n\"EGO_0\";\"\";\"\";\"\";\"\"\n\"Hans_0\";\"\";\"\";2;2\n\"Curie_1\";\"\";2;\"\";\"\"\n\"Tesla_2\";\"\";2;\"\";\"\"\n\"Franz_3\";\"\";\"\";\"\";\"\"\n";
		
		ComplexEvent event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Create_Actor")); //$NON-NLS-1$
		event.addEvent(new NewActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);		
		
		String result = this.export.toCsv_File_RelationType_allActors(this.network, ";", "\"", ",", this.type );
		System.out.println("toCsv_File_RelationType_allActors:");	
		System.out.println(result);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor1));
		EventProcessor.getInstance().fireEvent(event);
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor2));
		EventProcessor.getInstance().fireEvent(event);	
		
		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor3));
		EventProcessor.getInstance().fireEvent(event);

		event = new ComplexEvent(Messages
				.getString("VennMaker.Removing_2")); //$NON-NLS-1$
		event.addEvent(new DeleteActorEvent(actor4));
		EventProcessor.getInstance().fireEvent(event);
		
		Assert.assertEquals(vergleich,result);
	
	}

	@Test
	public void testRelationChangeSequenceFrequency()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testRelationChangeFrequency()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Mulitplexity()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToCsv_Mulitplexity_allActors()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testToString()
	{
		fail("Not yet implemented");
	}

	@Test
	public void testSetNetwork()
	{
		fail("Not yet implemented");
	}

}
