/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import data.AttributeType;
import data.Compute;
import data.EventListener;
import data.EventProcessor;
import data.Netzwerk;
import data.VennListener;
import events.ActorEvent;
import events.ActorInNetworkEvent;
import events.VennMakerEvent;
import export.ExportCSV;
import gui.utilities.VennMakerUIConfig;

/**
 * Dialog to show Statistics about the actors
 * 
 * 
 * 
 */

public class ViewStatisticDialog extends JDialog
{
	/**
	 * 
	 */
	private static final long				serialVersionUID	= 1L;

	/**
	 * Singleton: Referenz.
	 */
	private static ViewStatisticDialog	instance;

	/**
	 * Singleton: Zugriff.
	 * 
	 * @return Die einzige StatisticDialog-Instanz in diesem Prozess.
	 */
	public static ViewStatisticDialog showStatisticDialog()
	{
		if (instance == null)
		{
			instance = new ViewStatisticDialog();
		}
		return instance;
	}

	private JTable					myTable;

	private String[][]			tableContents;

	private ArrayList<String>	stringList;

	private ArrayList<String>	propList;

	private boolean				isCloned;
	private VennListener vennListener;
	private EventListener<ActorEvent> addActorEvent;
	private EventListener<ActorInNetworkEvent> actorInNetworkEvent;

	/**
	 * 
	 */
	private ViewStatisticDialog()
	{

		super(VennMaker.getInstance(), true);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		stringList = new ArrayList<String>();
		propList = new ArrayList<String>();
		this.addWindowListener(new WindowListener()
		{
			public void windowClosed(WindowEvent arg0)
			{
				instance = null;
				VennMaker.getInstance().removeNetzwerkSwitchListener(vennListener);
				EventProcessor.getInstance().removeEventListener(addActorEvent);
				EventProcessor.getInstance().removeEventListener(actorInNetworkEvent);
				
			}

			public void windowActivated(WindowEvent arg0)
			{
			}

			public void windowClosing(WindowEvent arg0)
			{
			}

			public void windowDeactivated(WindowEvent arg0)
			{

			}

			public void windowDeiconified(WindowEvent arg0)
			{
			}

			public void windowIconified(WindowEvent arg0)
			{
			}

			public void windowOpened(WindowEvent arg0)
			{
			}
		});

		Container contentPane = this.getContentPane();

		setTitle(Messages.getString("ViewStatisticDialog.0")); //$NON-NLS-1$
		contentPane.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// statisticsField = new JTextArea();
		myTable = new JTable();
		JScrollPane myScrollPane = new JScrollPane(myTable);

		// statisticsField.setEditable(false);
		refresh();
		contentPane.add(myScrollPane);

		JPanel buttonPane = new JPanel();

		final JButton refreshButton = new JButton(
				Messages.getString("ViewStatisticDialog.5")); //$NON-NLS-1$

		final JButton springembedderButton = new JButton("Spring Embedder"); //$NON-NLS-1$

		final JButton cancelButton = new JButton(
				Messages.getString("ViewStatisticDialog.3")); //$NON-NLS-1$
		final JButton exportButton = new JButton(
				Messages.getString("ViewStatisticDialog.4")); //$NON-NLS-1$
		final JButton clipboardButton = new JButton(
				Messages.getString("ViewStatisticDialog.8")); //$NON-NLS-1$
		final JButton okButton = new JButton("OK"); //$NON-NLS-1$

		final JDialog copyConfirmed = new JDialog();
		copyConfirmed.setLayout(new BorderLayout());
		copyConfirmed
				.add(new JLabel(Messages.getString("ViewStatisticDialog.10")), BorderLayout.NORTH); //$NON-NLS-1$
		copyConfirmed.add(okButton, BorderLayout.CENTER);
		copyConfirmed.setSize(150, 100);

		setModal(false);
		pack();

		VennMaker v = VennMaker.getInstance();
		int xOff = (v.getWidth() - this.getWidth()) / 2;
		int yOff = (v.getHeight() - this.getHeight()) / 2;
		this.setLocation(v.getX() + xOff, v.getY() + yOff);
		copyConfirmed.setAlwaysOnTop(true);

		buttonPane.add(refreshButton);
		buttonPane.add(springembedderButton);
		buttonPane.add(exportButton);
		buttonPane.add(cancelButton);
		buttonPane.add(clipboardButton);

		contentPane.add(buttonPane);

		// Export button
		exportButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				new ExportDialog(VennMaker.getInstance().getProject());
			}
		});

		// Close button
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				EventProcessor.getInstance().removeEventListener(actorInNetworkEvent);
				EventProcessor.getInstance().removeEventListener(addActorEvent);
				VennMaker.getInstance().removeNetzwerkSwitchListener(vennListener);
				setVisible(false);
				instance = null;
				dispose();
			}
		});

		// Refresh button
		refreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				refresh();
			}
		});

		// SpringEmbedder button
		springembedderButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				Netzwerk newNet;
				if (isCloned != true)
				{
					Netzwerk net = VennMaker.getInstance().getProject()
							.getCurrentNetzwerk();
					String name = net.getBezeichnung()
							+ Messages.getString("ViewStatisticDialog.9"); //$NON-NLS-1$
					newNet = net.cloneNetwork(name);
					newNet.getHintergrund().setSectorAttribute(null);
					newNet.getHintergrund().setNumSectors(0);
					newNet.getHintergrund().setCircleAttribute(null);
					newNet.getHintergrund().setNumCircles(0);
					isCloned = true;
				}
				else
					newNet = VennMaker.getInstance().getProject()
							.getCurrentNetzwerk();

				SpringEmbedder s = new SpringEmbedder();
				s.useSpringEmbedder(newNet.getAkteure(), newNet);

			}
		});

		// ClipBoard
		clipboardButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				StringBuffer stbuff = new StringBuffer();
				for (int i = 0; i < tableContents.length; i++)
				{
					stbuff.append(tableContents[i][0]
							+ ":  " + tableContents[i][1] + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				StringSelection stringSelect = new StringSelection(stbuff
						.toString());
				Clipboard sysClip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				sysClip.setContents(stringSelect, null);
				copyConfirmed.setVisible(true);
			}
		});

		// OK-Button
		okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				copyConfirmed.dispose();
			}
		});

		setVisible(true);

		this.actorInNetworkEvent = new EventListener<ActorInNetworkEvent>()
		{

			@Override
			public void eventOccured(final VennMakerEvent event)
			{
				refresh();
			}

			@Override
			public Class<ActorInNetworkEvent> getEventType()
			{
				return ActorInNetworkEvent.class;
			}
		};	
		EventProcessor.getInstance().addEventListener(this.actorInNetworkEvent);
		
		this.addActorEvent = new EventListener<ActorEvent>()
		{

			@Override
			public void eventOccured(final VennMakerEvent event)
			{
				refresh();
			}

			@Override
			public Class<ActorEvent> getEventType()
			{
				return ActorEvent.class;
			}
		};	
		EventProcessor.getInstance().addEventListener(this.addActorEvent);
		
	
		this.vennListener = new VennListener()
		{
			public void update()
			{
				refresh();
			}
		};
		VennMaker.getInstance().addNetzwerkSwitchListener(this.vennListener);

	}

	private void refresh()
	{

		int circleIndex = 0;
		int sectorIndex = 0;
		stringList.clear();
		propList.clear();
		final Netzwerk network = VennMaker.getInstance().getProject()
				.getCurrentNetzwerk();
		List<String> circles = network.getHintergrund().getCircles();
		circles.add("dummy"); //$NON-NLS-1$

		ExportCSV export = new ExportCSV();

		Map<String, Integer> statisticMap = export.getActorStatistics(network);

		// TODO
		// for (AttributeType actortype : VennMaker.getInstance().getProject()
		// .getAttributeTypes())
		//			statisticsField.append("Type_" + actortype.getBezeichnung() + ": " //$NON-NLS-1$ //$NON-NLS-2$
		//					+ statisticMap.get("Type_" + actortype.getBezeichnung()) + "\n");  //$NON-NLS-1$//$NON-NLS-2$

		if (network.getHintergrund().getNumCircles() > 0)
			for (int x = -1; x <= network.getHintergrund().getNumCircles(); x++)
			{
				if (x == 0) // der wert "circle_0" bleibt kontinuierlich 0, die
				{
				}
				else if (x > 0)
				{
					stringList
							.add(Messages.getString("ViewStatisticDialog.Circle") + circles.get(circleIndex++)); //$NON-NLS-1$
					propList.add("" + statisticMap.get("Circle_" + x));//$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					stringList.add(Messages
							.getString("ViewStatisticDialog.CircleEx")); //$NON-NLS-1$
					propList.add("" + statisticMap.get("Circle_" + x));//$NON-NLS-1$ //$NON-NLS-2$ 
				}
			}

		if (network.getHintergrund().getNumSectors() > 0)
			for (int x = -1; x < network.getHintergrund().getNumSectors(); x++)
			{
				String currentLabel = network.getHintergrund().getSector(
						sectorIndex).label;

				if (x >= 0)
				{
					stringList
							.add(Messages.getString("ViewStatisticDialog.Sector") + currentLabel); //$NON-NLS-1$
					propList.add("" + statisticMap.get("Sector_" + x));//$NON-NLS-1$ //$NON-NLS-2$ 
					sectorIndex++;
				}
				else
				{
					stringList.add(Messages
							.getString("ViewStatisticDialog.SectorEx")); //$NON-NLS-1$
					propList.add("" + statisticMap.get("Sector_" + x));//$NON-NLS-1$ //$NON-NLS-2$ 
				}
			}

		stringList.add(Messages.getString("ViewStatisticDialog.Total"));//$NON-NLS-1$
		propList.add("" + statisticMap.get("Total"));//$NON-NLS-1$ //$NON-NLS-2$

		Compute m = new Compute(network, network.getAkteure());

		DecimalFormat df = new DecimalFormat("0.000"); //$NON-NLS-1$

		stringList.add(Messages.getString("ViewStatisticDialog.1")); //$NON-NLS-1$ 
		propList.add(df.format(m.densityWithEgo()));
		stringList.add(Messages.getString("ViewStatisticDialog.2"));//$NON-NLS-1$
		propList.add("" + m.densityWithOutEgo(network.getEgo()));//$NON-NLS-1$

		/* Read relation attribute values frequencies */
		HashMap<Object, Integer> relations = m.countRelations();
		
		/* Read attributeQuantities*/
		HashMap<AttributeType, HashMap<String, Integer>> attributeQuantities = m
				.attributeQuantities(network);

		tableContents = new String[stringList.size() + m.getAttributeCounter() + relations.size()][2];
		for (int i = 0; i < stringList.size(); i++)
		{
			tableContents[i][0] = stringList.get(i);
			tableContents[i][1] = propList.get(i);
		}

		/* add attributeQuantities to the table */
		int i = stringList.size();
		for (AttributeType at : attributeQuantities.keySet())
		{
			for (String s : attributeQuantities.get(at).keySet())
			{
				tableContents[i][0] = at.toString() + " [" + s + "]";
				tableContents[i][1] = attributeQuantities.get(at).get(s).toString();
				i++;
			}
		}

		/* Count attributequantities and add them to the table */
		m.attributeQuantities(network);
		
		for (Map.Entry<Object, Integer> entry : relations.entrySet()){
			tableContents[i][0] = entry.getKey().toString();
			tableContents[i][1] = ""+entry.getValue();
			i++;
		}
		
		StatisticTableModel dtm = new StatisticTableModel(
				tableContents,
				new String[] {
						Messages.getString("ViewStatisticDialog.6"), Messages.getString("ViewStatisticDialog.7") }); //$NON-NLS-1$ //$NON-NLS-2$
		myTable.setModel(dtm);
		myTable.setRowHeight((int) (VennMakerUIConfig.getFontSize()+15)); 
		dtm.fireTableDataChanged();
		dtm.fireTableStructureChanged();

		m.attributeQuantities(network);
		
	
	
	}

	class StatisticTableModel extends DefaultTableModel
	{
		public StatisticTableModel(Object[][] content, Object[] colHeaders)
		{
			super(content, colHeaders);
		}

		public boolean isCellEditable(int row, int col)
		{
			return false;
		}
	}

}
