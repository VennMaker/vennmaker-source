/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.BackgroundNetworkCardSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingBackgroundNetworkcard;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import data.Netzwerk;
import data.Projekt;

/**
 * Dialog fuer die Auswahl einer anderen Netzwerkkarte als Hintergrund einer
 * Netzwerkkarte.
 * 
 * 
 */
public class CDialogBackgroundNetworkCard extends ConfigDialogElement
{
	private static final long		serialVersionUID	= 1L;

	private BGNetworkTableModel	model;

	private Vector<Netzwerk>		networks;

	private Projekt					p;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		this.p = VennMaker.getInstance().getProject();
		dialogPanel.add(createNetworkMapBox());
	}

	private Box createNetworkMapBox()
	{
		final Box vbox = Box.createVerticalBox();

		Box hbox3 = Box.createHorizontalBox();

		hbox3.add(Box.createVerticalStrut(50));

		final JButton bgNetworkButtonAdd = new JButton(
				Messages.getString("BackgroundConfig.BackgroundNetwork_2")); //$NON-NLS-1$
		final JButton bgNetworkButtonDel = new JButton(
				Messages.getString("BackgroundConfig.BackgroundNetwork_3")); //$NON-NLS-1$
		final JPanel bgNetworkPanel = new JPanel();
		bgNetworkPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("BackgroundConfig.BackgroundNetwork"))); //$NON-NLS-1$

		if (networks == null)
		{
			networks = new Vector<Netzwerk>();
			if (net.getHintergrund().getBackgroundNetworkcards() != null)
				for (final Netzwerk n : net.getHintergrund()
						.getBackgroundNetworkcards())
					if (n != net)
						networks.add(n);
		}
		model = new BGNetworkTableModel();
		Vector<String> header = new Vector<String>();
		header.add(Messages.getString("BackgroundConfig.BackgroundNetwork_1"));
		model.setColumnIdentifiers(header);

		final JTable table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(200, 50));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(table);
		hbox3.add(scrollPane);
		hbox3.add(bgNetworkButtonAdd);
		hbox3.add(bgNetworkButtonDel);
		bgNetworkPanel.add(hbox3);

		bgNetworkButtonAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				networks.clear();
				for (final Netzwerk n : VennMaker.getInstance().getProject()
						.getNetzwerke())
					if (n != net)
						networks.add(n);
				model.fireTableDataChanged();
			}
		});
		bgNetworkButtonDel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				int row = table.getSelectedRow();
				if (row >= 0)
				{
					networks.remove(row);
					model.fireTableDataChanged();
				}
			}
		});
		vbox.add(bgNetworkPanel);
		return vbox;
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		return new SettingBackgroundNetworkcard(net.getHintergrund(), networks);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof BackgroundNetworkCardSaveElement))
			return;

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		BackgroundNetworkCardSaveElement elem = (BackgroundNetworkCardSaveElement) setting;

		networks.clear();
		for (Integer id : elem.getNetworkIDs())
		{
			Netzwerk n = p.getNetworkForId(id);
			if (n != null)
				networks.add(n);
		}
		model.fireTableDataChanged();
	}

	@Override
	public SaveElement getSaveElement()
	{
		Vector<Integer> networkIds = new Vector<Integer>();
		for (Netzwerk n : networks)
			networkIds.add(n.getId());

		BackgroundNetworkCardSaveElement elem = new BackgroundNetworkCardSaveElement(
				networkIds);
		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}

	class BGNetworkTableModel extends DefaultTableModel
	{
		private static final long	serialVersionUID	= 1L;

		public BGNetworkTableModel()
		{
			super();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			if (networks.size() <= row)
				return null;
			Netzwerk n = networks.get(row);
			return n.getBezeichnung();
		}

		@Override
		public int getRowCount()
		{
			return networks.size();
		}
	}
}
