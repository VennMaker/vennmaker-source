package data;

import static org.junit.Assert.fail;
import data.AttributeType.Scope;
import events.AddActorEvent;
import events.NewNetworkEvent;
import gui.VennMaker;

import java.awt.geom.Point2D;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Test_Netzwerk
{

	private Netzwerk				n;

	private static VennMaker	instance;

	private Akteur					globalEgo	= new Akteur("Ego");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		instance = VennMaker.getInstance();
	}

	@Before
	public void setUp() throws Exception
	{

		AttributeType mainAttributeType = instance.getProject()
				.getMainAttributeType("ACTOR");
		n = new Netzwerk();
		n.setBezeichnung("das Netz");
		NewNetworkEvent nne = new NewNetworkEvent(n);
		EventProcessor.getInstance().fireEvent(nne);
		n.createNewVisualizers();
		n.createVisualizer();
		n.setActorSizeVisualizer(new ActorSizeVisualizer());
		n.getActorSizeVisualizer().setAttributeType(mainAttributeType);

		HashMap<Object, Integer> sizes = new HashMap<Object, Integer>();
		for (Object o : mainAttributeType.getPredefinedValues())
			sizes.put(o, 5);
		n.getActorSizeVisualizer().setSizes(sizes);

		n.setActorImageVisualizer(new ActorImageVisualizer());
		n.getActorImageVisualizer().setAttributeType(mainAttributeType);

		HashMap<Object, String> images = new HashMap<Object, String>();
		for (Object o : mainAttributeType.getPredefinedValues())
			images.put(o, "5");
		n.getActorImageVisualizer().setImages(images);

		instance.getProject().setEgo(globalEgo);

		/**
		 * noetig, da cloneNetwork und cloneVirtualNetwork auf die views zugreift,
		 * um diese zu klonen
		 */
		instance.createViewersTabbedPane();

		instance.getProject().setCurrentNetzwerk(n);
		globalEgo
				.setAttributeValue(
						instance.getProject().getMainAttributeType("ACTOR"), n,
						instance.getProject().getMainAttributeType("ACTOR")
								.getDefaultValue());

	}

	/**
	 * destroy all Networks after testing, to have clean test results
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		instance.getProject().getNetzwerke().clear();
	}

	@Test
	public void testNetzwerk()
	{
		Netzwerk n3 = new Netzwerk();
		Assert.assertNotNull(n3);
	}

	@Test
	public void testNetzwerkSameID()
	{
		Netzwerk n1 = new Netzwerk();
		NewNetworkEvent nne = new NewNetworkEvent(n1);
		EventProcessor.getInstance().fireEvent(nne);
		n1.setId(1);
		// Das duerfte eigentlich nicht moeglich sein:
		Netzwerk n2 = new Netzwerk();

		nne = new NewNetworkEvent(n2);
		EventProcessor.getInstance().fireEvent(nne);
		n2.setId(1);
		Assert.assertNotSame(n1.getId(), n2.getId());
	}

	@Test
	public void testNetzwerkSameID2()
	{
		Netzwerk n2 = new Netzwerk();
		n2.setId(-1);
		Assert.assertNotSame(this.n.getId(), n2.getId());
	}

	@Test
	public void testGetId()
	{
		this.n.setId(10);

		Assert.assertEquals(10, this.n.getId());
	}

	@Test
	public void testGetId2()
	{
		Netzwerk n2 = new Netzwerk();
		n2.setId(12);
		Assert.assertEquals(12, n2.getId());
	}

	@Test
	public void testSetId()
	{
		Netzwerk n2 = new Netzwerk();
		n2.setId(15);
		Assert.assertEquals(15, n2.getId());
	}

	@Test
	public void testSetIdNetworkCount()
	{
		Netzwerk n2 = new Netzwerk();
		n2.setId(50);
		Assert.assertEquals(50, n2.getId());
	}

	@Test
	public void testSetId_lesserID()
	{
		Netzwerk n2 = new Netzwerk();
		n2.setId(0);
		Assert.assertEquals(0, n2.getId());
	}

	@Test
	public void testRegisterEventListeners()
	{
		Akteur akteur = new Akteur();
		Netzwerk n2 = new Netzwerk();
		instance.getProject().setCurrentNetzwerk(n2);
		instance.getProject().getCurrentNetzwerk().addAkteur(akteur);

		AddActorEvent event = new AddActorEvent(akteur, n2, new Point2D.Double(1,
				1));

		EventProcessor.getInstance().fireEvent(event);

		Assert.assertTrue(n2.getAkteure().contains(akteur));
	}

	@Test
	public void testRegisterEventListeners_alreadyRegistered()
	{
		Akteur akteur = new Akteur();
		Netzwerk n2 = new Netzwerk();
		instance.getProject().setCurrentNetzwerk(n2);
		instance.getProject().getCurrentNetzwerk().addAkteur(akteur);

		AddActorEvent event = new AddActorEvent(akteur, n2, new Point2D.Double(1,
				1));

		EventProcessor.getInstance().fireEvent(event);

		n2.registerEventListeners();
		n2.registerEventListeners();

		Assert.assertTrue(n2.getAkteure().contains(akteur));
	}

	@Test
	public void testAddAkteur()
	{
		Akteur a = new Akteur();
		a.setName("Franz");
		n.addAkteur(a);
		Assert.assertTrue(n.getAkteure().contains(a));
	}

	@Test
	public void testAddAkteurNULL()
	{
		Akteur a = null;
		n.addAkteur(a);
		Assert.assertFalse(n.getAkteure().contains(a));
	}

	@Test
	public void testRemoveAkteur1()
	{
		Akteur a = new Akteur();
		n.addAkteur(a);
		n.removeAkteur(a);
		Assert.assertFalse(n.getAkteure().contains(a));
	}

	@Test
	public void testRemoveAkteur2()
	{
		Akteur a = null;
		n.removeAkteur(a);
		Assert.assertFalse(n.getAkteure().contains(a));
	}

	@Test
	public void testGetEgo()
	{
		Assert.assertNull(n.getEgo());
	}

	@Test
	public void testGetEgoNotNull()
	{
		Netzwerk n2 = new Netzwerk();
		if (instance.getProject().getEgo() == null)
			fail("no Ego in ProjectSettings...");
		n2.addAkteur(instance.getProject().getEgo());

		Assert.assertNotNull(n2.getEgo());
	}

	@Test
	public void testGetAkteure()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("Schröder");
		n2.addAkteur(a);
		Akteur a2 = new Akteur("Bernd");
		n2.addAkteur(a2);

		Assert.assertNotNull(n2.getAkteure());
	}

	@Test
	public void testGetAkteure2()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("Schröder");
		n2.addAkteur(a);
		Akteur a2 = new Akteur("Bernd");
		n2.addAkteur(a2);

		Assert.assertNotNull(n2.getAkteure().contains(a));
	}

	@Test
	public void testGetHintergrund()
	{
		BackgroundInfo binfo = new BackgroundInfo();
		n.setHintergrund(binfo);

		Assert.assertEquals(n.getHintergrund(), binfo);
	}

	@Test
	public void testSetHintergrund()
	{
		BackgroundInfo binfo2 = new BackgroundInfo();
		binfo2.setNumSectors(2);
		n.setHintergrund(binfo2);

		Assert.assertEquals(n.getHintergrund().getNumSectors(), 2);

	}

	@Test
	public void testGetBezeichnung()
	{
		n.setBezeichnung("TestNetzwerkKarte");
		Assert.assertEquals(n.getBezeichnung(), "TestNetzwerkKarte");
	}

	@Test
	public void testGetBezeichnungNull()
	{
		n.setBezeichnung(null);
		Assert.assertEquals(n.getBezeichnung(), "");
	}

	@Test
	public void testSetBezeichnung()
	{
		n.setBezeichnung("TestNetzwerkKarte2");
		Assert.assertEquals(n.getBezeichnung(), "TestNetzwerkKarte2");
	}

	@Test
	public void testAddTemporaryActor()
	{
		Akteur a = new Akteur("TemporaryActor");
		n.addTemporaryActor(a);

		Assert.assertEquals(a,
				n.getTemporaryActorAt(n.getTemporaryActorSize() - 1));
	}

	@Test
	public void testGetTemporarySectors()
	{
		Assert.assertNotNull(n.getTemporarySectors());
	}

	@Test
	public void testGetTemporaryCircles()
	{
		Assert.assertNotNull(n.getTemporaryCircles());
	}

	/**
	 * get a temporary actor, who is really represented in the vector
	 */
	@Test
	public void testGetTemporaryActorAt()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("FirstActor");

		n2.addTemporaryActor(a);
		Assert.assertEquals(a, n2.getTemporaryActorAt(0));
	}

	/**
	 * Ask for a index, which is definately not represented in the tempActors
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetTemporaryActorAtHighIndex()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("FirstActor");

		n2.addTemporaryActor(a);
		n2.getTemporaryActorAt(9000);
	}

	@Test
	public void testGetTemporaryActorSize()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("FirstActor");

		n2.addTemporaryActor(a);
		Assert.assertEquals(n2.getTemporaryActorSize(), 1);
	}

	@Test
	public void testGetTemporaryActorSizeNull()
	{
		Netzwerk n2 = new Netzwerk();
		Assert.assertEquals(n2.getTemporaryActorSize(), 0);
	}

	@Test
	public void testRemoveTemporaryActor()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("FirstActor");

		n2.addTemporaryActor(a);
		n2.removeTemporaryActor(a);
		Assert.assertEquals(n2.getTemporaryActorSize(), 0);
	}

	/** tries to remove a tempActor, who is not represented in the vector */
	@Test
	public void testRemoveTemporaryActorNoActor()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("FirstActor");
		n2.removeTemporaryActor(a);
	}

	@Test
	public void testIsLockedAkteur()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("FirstActor");
		n2.addAkteur(a);
		n2.lockActor(a);

		Assert.assertTrue(n2.isLocked(a));
	}

	@Test
	public void testLockActor()
	{
		Akteur a = new Akteur("Paule");
		n.lockActor(a);
		Assert.assertTrue(n.isLocked(a));
	}

	@Test
	public void testUnlockActor()
	{
		Akteur a = new Akteur("Paule");
		Akteur b = new Akteur("Bademeister");
		n.lockActor(a);
		n.lockActor(b);
		n.unlockActor(a);
		Assert.assertFalse(n.isLocked(a));
	}

	@Test
	public void testUnlockAllActors()
	{
		Netzwerk n2 = new Netzwerk();
		Akteur a = new Akteur("Paule");
		Akteur b = new Akteur("Bademeister");
		n2.addAkteur(a);
		n2.addAkteur(b);
		n2.lockActor(a);
		n2.lockActor(b);
		n2.unlockAllActors();
		Assert.assertFalse(n2.isLocked(b));
	}

	@Test
	public void testAddLockedRelation()
	{
		Netzwerk n3 = new Netzwerk();
		Akteur a = new Akteur("Nummer 1");
		Akteur b = new Akteur("Nummer 2");

		Relation r = new Relation(a, "STANDARDRELATION");
		b.addRelation(n3, r);

		n3.unlockAllRelations();
		n3.addLockedRelation(r);

		Assert.assertTrue(n3.isLocked(r));
	}

	@Test
	public void testUnlockAllRelations()
	{
		Netzwerk n3 = new Netzwerk();
		Akteur a = new Akteur("a");
		Relation r = new Relation(a, "STANDARDRELATION");
		n3.addLockedRelation(r);

		n3.unlockAllRelations();

		Assert.assertFalse(n3.isLocked(r));
	}

	@Test
	public void testIsLockedRelation()
	{
		Netzwerk n3 = new Netzwerk();
		Akteur a = new Akteur("a");
		Relation r = new Relation(a, "STANDARDRELATION");
		n3.addLockedRelation(r);

		Assert.assertTrue(n3.isLocked(r));
	}

	@Test
	public void testGetLastEvent()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetLastUndoEvent()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsEmpty()
	{
		Netzwerk n2 = n.getNewNetwork("DasNeue");
		Assert.assertTrue(n2.isEmpty());
	}

	@Test
	public void testLogEvent()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRedoLastEvent()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUndoLastEvent()
	{
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetFilter()
	{
		Akteur a = new Akteur("Bla");

		n.setFilter(a);
		Assert.assertTrue(n.getFilter(a));
	}

	@Test
	public void testSetFilter()
	{
		Akteur a = new Akteur("Bla");

		n.setFilter(a);
		Assert.assertTrue(n.getFilter(a));
	}

	@Test
	public void testSetFilterDoubleActor()
	{
		Akteur a = new Akteur("Bla");

		n.setFilter(a);
		n.setFilter(a);

		Assert.assertTrue(n.getFilter(a));
	}

	@Test
	public void testRemoveFilterNoActor()
	{
		n.removeFilter(new Akteur());
	}

	@Test
	public void testRemoveFilterWithActor()
	{
		Akteur a = new Akteur("fritz");
		n.setFilter(a);
		n.removeFilter(a);
	}

	@Test
	public void testGetStatusFilter_false()
	{
		Assert.assertFalse(n.getStatusFilter());
	}

	@Test
	public void testGetStatusFilter_true()
	{
		Akteur a = new Akteur("a");
		n.addAkteur(a);
		n.setFilter(a);

		Assert.assertTrue(n.getStatusFilter());
	}

	@Test
	public void testCreateFilter()
	{
		Akteur a = new Akteur("blub");
		n.setFilter(a);

		n.createFilter();

		Assert.assertFalse(n.getFilter(a));
	}

	@Test
	public void testGetActorSizeVisualizer()
	{
		Assert.assertNotNull(n.getActorSizeVisualizer());
	}

	@Test
	public void testSetActorSizeVisualizer()
	{
		ActorSizeVisualizer asv = new ActorSizeVisualizer();
		n.setActorSizeVisualizer(asv);

		Assert.assertEquals(n.getActorSizeVisualizer(), asv);
	}

	/** tests the setActorSizeVisualizer, when parameter equals null */
	@Test
	public void testSetActorSizeVisualizer_null()
	{
		n.setActorSizeVisualizer(null);

		Assert.assertNull(n.getActorSizeVisualizer());
	}

	@Test
	public void testGetActorImageVisualizer()
	{
		Assert.assertNotNull(n.getActorImageVisualizer());
	}

	@Test
	public void testSetActorImageVisualizer()
	{
		ActorImageVisualizer aiv = new ActorImageVisualizer();
		n.setActorImageVisualizer(aiv);

		Assert.assertEquals(n.getActorImageVisualizer(), aiv);
	}

	/** tests the setActorImageVisualizer, when parameter equals null */
	@Test
	public void testSetActorImageVisualizer_null()
	{
		n.setActorImageVisualizer(null);

		Assert.assertNull(n.getActorImageVisualizer());
	}

	@Test
	public void testGetActorSectorVisualizer()
	{
		ActorSectorVisualizer asv = new ActorSectorVisualizer();
		n.setActorSectorVisualizer(asv);

		Assert.assertEquals(n.getActorSectorVisualizer(), asv);
	}

	/** tests the setActorImageVisualizer, when parameter equals null */
	@Test
	public void testSetActorSectorVisualizer_null()
	{
		n.setActorSectorVisualizer(null);

		Assert.assertNull(n.getActorSectorVisualizer());
	}

	@Test
	public void testGetRelationColorVisualizer()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");
		Assert.assertNotNull(n1.getRelationColorVisualizer("STANDARDRELATION",
				instance.getProject().getAttributeTypes("STANDARDRELATION").get(0)));
	}

	@Test
	public void testSetRelationColorVisualizer()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		RelationColorVisualizer rcv = new RelationColorVisualizer();
		AttributeType at = instance.getProject()
				.getAttributeTypes("STANDARDRELATION").get(0);
		n1.setRelationColorVisualizer("STANDARDRELATION", at, rcv);
		Assert.assertEquals(
				n1.getRelationColorVisualizer("STANDARDRELATION", at), rcv);
	}

	@Test
	public void testGetRelationDashVisualizer()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");
		Assert.assertNotNull(n1.getRelationDashVisualizer("STANDARDRELATION",
				instance.getProject().getAttributeTypes("STANDARDRELATION").get(0)));
	}

	@Test
	public void testSetRelationDashVisualizer()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		RelationDashVisualizer rdv = new RelationDashVisualizer();
		AttributeType at = instance.getProject()
				.getAttributeTypes("STANDARDRELATION").get(0);
		n1.setRelationDashVisualizer("STANDARDRELATION", at, rdv);
		Assert.assertEquals(n1.getRelationDashVisualizer("STANDARDRELATION", at),
				rdv);
	}

	@Test
	public void testGetRelationSizeVisualizer()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");
		Assert.assertNotNull(n1.getRelationSizeVisualizer("STANDARDRELATION",
				instance.getProject().getAttributeTypes("STANDARDRELATION").get(0)));
	}

	@Test
	public void testSetRelationSizeVisualizer()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		RelationSizeVisualizer rsv = new RelationSizeVisualizer();
		AttributeType at = instance.getProject()
				.getAttributeTypes("STANDARDRELATION").get(0);
		n1.setRelationSizeVisualizer("STANDARDRELATION", at, rsv);
		Assert.assertEquals(n1.getRelationSizeVisualizer("STANDARDRELATION", at),
				rsv);
	}

	@Test
	public void testSetRelationSizeVisualizer_VisualizerNull()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		AttributeType at = instance.getProject()
				.getAttributeTypes("STANDARDRELATION").get(0);
		n1.setRelationSizeVisualizer("STANDARDRELATION", at, null);
		Assert.assertNotNull(n1.getRelationSizeVisualizer("STANDARDRELATION", at));
	}

	@Test
	public void testSetRelationSizeVisualizer_wrongTypeCollector()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		RelationSizeVisualizer rsv = new RelationSizeVisualizer();
		AttributeType at = instance.getProject()
				.getAttributeTypes("STANDARDRELATION").get(0);
		n1.setRelationSizeVisualizer("wrong Type Collector", at, rsv);
		Assert.assertNotNull(n1.getRelationSizeVisualizer("STANDARDRELATION", at));
	}

	/**
	 * tests, if setRelationSizeVisualizer works correctly, when the given
	 * AttributeType equals null
	 */
	@Test
	public void testSetRelationSizeVisualizer_AttributetypeNull()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		RelationSizeVisualizer rsv = new RelationSizeVisualizer();
		n1.setRelationSizeVisualizer("wrong Type Collector", null, rsv);

		AttributeType at = instance.getProject()
				.getAttributeTypes("STANDARDRELATION").get(0);
		Assert.assertNotNull(n1.getRelationSizeVisualizer("STANDARDRELATION", at));
	}

	@Test
	public void testToString()
	{
		Netzwerk nameNetzwerk = new Netzwerk();
		nameNetzwerk.setBezeichnung("dieses hat einen Namen");

		Assert.assertEquals("dieses hat einen Namen", nameNetzwerk.toString());
	}

	@Test
	public void testCreateVisualizer()
	{
		n.createVisualizer();

		Assert.assertNotNull("relationDashVisualizer not set correctly",
				n.getRelationDashVisualizerByCollector("STANDARDRELATION"));
		Assert.assertNotNull("relationSizeVisualizer not set correctly",
				n.getRelationSizeVisualizerByCollector("STANDARDRELATION"));
		Assert.assertNotNull("relationColorVisualizer not set correctly",
				n.getRelationColorVisualizerByCollector("STANDARDRELATION"));
	}

	@Test
	public void testCreateNewVisualizer()
	{
		Netzwerk n2 = new Netzwerk();

		n2.createNewVisualizers();

		Assert.assertNotNull("relationDashVisualizer not set correctly",
				n2.getRelationDashVisualizerByCollector("STANDARDRELATION"));
		Assert.assertNotNull("relationSizeVisualizer not set correctly",
				n2.getRelationSizeVisualizerByCollector("STANDARDRELATION"));
		Assert.assertNotNull("relationColorVisualizer not set correctly",
				n2.getRelationColorVisualizerByCollector("STANDARDRELATION"));
	}

	@Test
	public void testGetNewNetworkStringBoolean()
	{
		Netzwerk neues = n.getNewNetwork("DasNeue", true);

		Assert.assertNotNull(neues);
	}

	@Test
	public void testGetNewNetworkString()
	{
		Netzwerk ursprungsNetz = new Netzwerk();
		ursprungsNetz.setBezeichnung("Ursprung");

		Netzwerk neuesNetz = ursprungsNetz.getNewNetwork("DasNeue");

		Assert.assertEquals("ActorImageVisualizer nicht richtig gesetzt",
				ursprungsNetz.getActorImageVisualizer().getAttributeType(),
				neuesNetz.getActorImageVisualizer().getAttributeType());
		Assert.assertEquals("ActorSectorVisualizer nicht richtig gesetzt",
				ursprungsNetz.getActorSectorVisualizer().getAttributeType(),
				neuesNetz.getActorSectorVisualizer().getAttributeType());
		Assert.assertEquals("ActorSizeVisualizer nicht richtig gesetzt",
				ursprungsNetz.getActorSizeVisualizer().getAttributeType(),
				neuesNetz.getActorSizeVisualizer().getAttributeType());

		Assert.assertEquals("Bezeichnung nicht richtig gesetzt", "DasNeue",
				neuesNetz.getBezeichnung());
	}

	@Test
	public void testGetNewNetworkString_withoutEgo()
	{
		instance.getProject().setEgo(null);

		Netzwerk ursprungsNetz = new Netzwerk();
		ursprungsNetz.setBezeichnung("Ursprung");

		Netzwerk neuesNetz = ursprungsNetz.getNewNetwork("DasNeue");

		Assert.assertEquals("ActorImageVisualizer nicht richtig gesetzt",
				ursprungsNetz.getActorImageVisualizer().getAttributeType(),
				neuesNetz.getActorImageVisualizer().getAttributeType());
		Assert.assertEquals("ActorSectorVisualizer nicht richtig gesetzt",
				ursprungsNetz.getActorSectorVisualizer().getAttributeType(),
				neuesNetz.getActorSectorVisualizer().getAttributeType());
		Assert.assertEquals("ActorSizeVisualizer nicht richtig gesetzt",
				ursprungsNetz.getActorSizeVisualizer().getAttributeType(),
				neuesNetz.getActorSizeVisualizer().getAttributeType());

		Assert.assertEquals("Bezeichnung nicht richtig gesetzt", "DasNeue",
				neuesNetz.getBezeichnung());
	}

	@Test
	public void testGetNewNetworkString_DoubleName()
	{
		Netzwerk ursprungsNetz = new Netzwerk();
		ursprungsNetz.setBezeichnung("Ursprung");

		Netzwerk neuesNetz = ursprungsNetz.getNewNetwork("DasNeue");
		Netzwerk doppelNetz = ursprungsNetz.getNewNetwork("DasNeue");

		Assert.assertNotNull("New net not created!", doppelNetz);
		Assert.assertEquals("Name of new network has not been set correctly",
				doppelNetz.getBezeichnung(), "DasNeue(1)");
	}

	@Test
	public void testCloneNetworkStringBoolean()
	{
		Netzwerk neuesNetz = n.cloneNetwork("DasNeue", false);
		Assert.assertNotNull("neuesNetz has not been created", neuesNetz);

		Assert.assertEquals("Name of new Net has not been set correctly",
				neuesNetz.getBezeichnung(), "DasNeue");
		Assert.assertEquals("ActorVisualizer has not been set correctly",
				neuesNetz.getActorImageVisualizer().getImages(), n
						.getActorImageVisualizer().getImages());
		Assert.assertEquals("RelationVisualizer has not been set correctly",
				neuesNetz.getActiveRelationColorVisualizer("STANDARDRELATION")
						.getColors(),
				n.getActiveRelationColorVisualizer("STANDARDRELATION").getColors());
	}

	@Test
	public void testCloneNetworkString()
	{
		Netzwerk n2 = n.cloneNetwork("Neu");
		Assert.assertNotNull(n2);
	}

	@Test
	public void testCloneNetworkString_DoubleName()
	{
		Netzwerk n1 = n.getNewNetwork("Neu");
		Netzwerk n2 = n.cloneNetwork("Neu");

		Assert.assertEquals(n2.getBezeichnung(), "Neu(1)");
	}

	@Test
	public void testMergeNetwork()
	{
		fail("mergeNetwork not yet implemented!");

		Netzwerk n1 = n.cloneNetwork("a network");
		Netzwerk n2 = n.cloneNetwork("another one");

		Akteur a = new Akteur("a");
		Akteur b = new Akteur("b");
		Akteur c = new Akteur("c");

		n1.addAkteur(a);
		n2.addAkteur(b);
		n2.addAkteur(c);

		Netzwerk n3 = n1.mergeNetwork(n2);

		Assert.assertTrue(
				"Akteur a is not in n3, although it should be merged from n1", n3
						.getAkteure().contains(a));
		Assert.assertTrue(
				"Akteur b is not in n3, although it should be merged from n2", n3
						.getAkteure().contains(b));
		Assert.assertTrue(
				"Akteur c is not in n3, although it should be merged from n2", n3
						.getAkteure().contains(c));

		Assert.assertEquals("Visualizer not merged correctly",
				n3.getActorSizeVisualizer(), n2.getActorSizeVisualizer());

	}

	@Test
	public void testCloneVirtualNetwork()
	{
		Netzwerk clonedNetwork = n.cloneVirtualNetwork("clone");

		Assert.assertEquals("Ego has not been set correctly", instance
				.getProject().getEgo(), clonedNetwork.getEgo());
		Assert.assertNotNull("The new network has not been set", clonedNetwork);
	}

	@Test
	public void testCloneVirtualNetwork_NameNull()
	{
		Netzwerk clonedNetwork = n.cloneVirtualNetwork(null);

		Assert.assertNotNull(clonedNetwork);
	}

	@Test
	public void testCloneVirtualNetwork_NameEmpty()
	{
		Netzwerk clonedNetwork = n.cloneVirtualNetwork("");

		Assert.assertNotNull(clonedNetwork);
	}

	@Test
	public void testCloneVirtualNetwork_MarkedActors()
	{
		Akteur a = new Akteur("markThis");
		n.addAkteur(a);
		n.setMarkedActor(a);
		n.setFilter(a);
		instance.getViewOfNetwork(n).setFilter();
		Netzwerk clonedNetwork = n.cloneVirtualNetwork("");

		Assert.assertNotNull(clonedNetwork);
	}

	@Test
	public void testSetMarkedActor()
	{
		Akteur a = new Akteur("a");

		n.setMarkedActor(a);

		Assert.assertTrue(n.getMarkedActors().contains(a));
	}

	@Test
	public void testGetMarkedActors()
	{
		Akteur a = new Akteur("a");

		n.setMarkedActor(a);

		Assert.assertNotNull(n.getMarkedActors());
		Assert.assertTrue(
				"Actor a has not been added to markedActors of this network", (n
						.getMarkedActors().size() > 0));
	}

	@Test
	public void testRemoveAllMarkedActors()
	{
		Akteur a = new Akteur("a");

		n.setMarkedActor(a);

		n.removeAllMarkedActors();

		Assert.assertNotNull(n.getMarkedActors());
		Assert.assertTrue("MarkedActors weren't removed..", (n.getMarkedActors()
				.size() == 0));
	}

	@Test
	public void testUpdateAkteure()
	{
		Akteur a = new Akteur("a");
		AttributeType at = instance.getProject().getAttributeTypes().get(0);

		n.addAkteur(a);
		n.updateAkteure(at);

		Assert.assertNotNull(a.getAttributeValue(at, n));
	}

	@Test
	public void testUpdateAkteure_predefinedValuesNull()
	{
		Akteur a = new Akteur("a");
		AttributeType at = new AttributeType();
		at.setType("ACTOR");
		at.setScope(Scope.PROJECT);

		n.addAkteur(a);
		n.updateAkteure(at);

		Assert.assertNull(a.getAttributeValue(at, n));
	}

	@Test
	public void testUpdateAkteure_changedAttributeType()
	{
		Akteur a = new Akteur("a");
		AttributeType at = new AttributeType();
		at.setType("ACTOR");
		at.setScope(Scope.PROJECT);
		Object[] predefinedValuesOriginal = { "A", "B", "C", "D" };
		at.setPredefinedValues(predefinedValuesOriginal);

		n.addAkteur(a);

		a.setAttributeValue(at, n, at.getPredefinedValue(2));
		n.updateAkteure(at);

		/* now change the attributetype */
		Object[] predefinedValues = { "Alpha", "Beta", "Gamma", "Delta" };

		at.setPredefinedValues(predefinedValues);

		n.updateAkteure(at);

		Assert.assertNull(a.getAttributeValue(at, n));
	}

	@Test
	public void testGetMetaInfo()
	{
		Netzwerk n3 = new Netzwerk();
		n3.setMetaInfo("Hallo Welt");

		Assert.assertEquals(n3.getMetaInfo(), "Hallo Welt");
	}

	@Test
	public void testSetMetaInfo()
	{
		Netzwerk n3 = new Netzwerk();
		n3.setMetaInfo("Hallo Welt");

		Assert.assertEquals(n3.getMetaInfo(), "Hallo Welt");
	}

	@Test
	public void testRefreshRelationAttributes()
	{
		Netzwerk n1 = n.getNewNetwork("DasNeue");

		n1.refreshRelationAttributes();
		Assert.assertNotNull(
				"relationColorVisualizer not set correctly",
				n1.getRelationColorVisualizer("STANDARDRELATION", instance
						.getProject().getAttributeTypes("STANDARDRELATION").get(0)));
		Assert.assertNotNull(
				"relationSizeVisualizer not set correctly",
				n1.getRelationSizeVisualizer("STANDARDRELATION", instance
						.getProject().getAttributeTypes("STANDARDRELATION").get(0)));
		Assert.assertNotNull(
				"relationDashVisualizer not set correctly",
				n1.getRelationDashVisualizer("STANDARDRELATION", instance
						.getProject().getAttributeTypes("STANDARDRELATION").get(0)));
	}
}
