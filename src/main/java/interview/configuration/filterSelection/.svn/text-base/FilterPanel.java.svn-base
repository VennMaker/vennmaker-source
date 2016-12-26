package interview.configuration.filterSelection;

import gui.Messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import data.Akteur;

/**
 * Panel to select the filter performed for actor-selection
 * 
 * EGO + alteri
 * 
 * 
 * 
 */
public class FilterPanel implements FilterSelector, Serializable
{
	/**
	 * 
	 */
	private static final long		serialVersionUID		= 1L;

	protected String					filter;

	protected ArrayList<Integer>	filterIndex;

	protected boolean					dummyCreationEnabled	= false;

	final JTextArea					taFilter					= new JTextArea(
																				Messages
																						.getString("InterviewElement.NoFilter"));		//$NON-NLS-1$

	private JButton					deleteFilterButton	= new JButton(
																				Messages
																						.getString("InterviewElement.DeleteFilter"));	//$NON-NLS-1$;

	public FilterPanel()
	{
	}

	public FilterPanel(String messageKey)
	{
	}

	private String makeReadable(String filter)
	{
		if (filter == null)
			return "";
		String readable = "";
		String[] words = filter.split(";");
		int i = 0;
		int offset = 0;
		while (i < words.length)
		{
			readable += words[i++] + " ";
			if ((i - offset) % 3 == 0 && words.length - 1 > i)
			{
				readable += "\n" + words[i++] + "\n";
				offset++;
			}
		}
		return readable;
	}

	/**
	 * Callback for FilterDialog to set Filter bevore dispose()
	 * 
	 * @param filterIndex
	 */
	public void setFilterCallback(String filter, ArrayList<Integer> filterIndex)
	{
		this.filter = filter;
		this.filterIndex = filterIndex;
	}

	public ArrayList<Integer> getFilterIndex()
	{
		return this.filterIndex;
	}

	public void setFilterIndex(ArrayList<Integer> filterIndex)
	{
		this.filterIndex = filterIndex;
	}

	public JPanel getConfigurationPanel()
	{
		JPanel panel = new JPanel();
		GridBagLayout gbLayout = new GridBagLayout();
		panel.setLayout(gbLayout);

		taFilter.setEditable(false);
		JScrollPane filterScroll = new JScrollPane(taFilter);
		JButton editFilterButton = new JButton(
				Messages.getString("VennMaker.edit")); //$NON-NLS-1$

		/** Button to delete current filters (only enabled, when filter active) */

		deleteFilterButton.setEnabled((filter != null) ? true : false);

		final FilterPanel parent = this;

		editFilterButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				InterviewFilterDialog fDialog = new InterviewFilterDialog(parent, filter, true);
				filter = fDialog.getFilter();
				
System.out.println("FilterPanel: getConfigurationPanel(): filter:"+filter);				
				taFilter.setText(makeReadable(filter));
				deleteFilterButton.setEnabled((filter != null) ? true : false);
			}
		});

		deleteFilterButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setFilter(null);
				/* when no filter, there's nothing to delete */
				deleteFilterButton.setEnabled(false);
				taFilter.setText(Messages.getString("InterviewElement.NoFilter"));
			}
		});

		JLabel lblFilter = new JLabel(
				Messages.getString("InterviewElement.FilterDescription")); //$NON-NLS-1$
		GridBagConstraints g = new GridBagConstraints();

		int x = 0, y = 0;
		g.anchor = GridBagConstraints.NORTHWEST;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.gridx = x;
		g.gridy = y;
		g.gridwidth = 1;
		g.weightx = 1;
		gbLayout.setConstraints(lblFilter, g);
		panel.add(lblFilter);

		g = new GridBagConstraints();
		g.gridy = ++y;
		g.weighty = 2;
		g.gridwidth = 3;
		g.gridheight = 2;
		g.insets = new Insets(5, 0, 0, 0);
		g.fill = GridBagConstraints.BOTH;
		gbLayout.setConstraints(filterScroll, g);
		panel.add(filterScroll);

		y += g.gridheight;

		g = new GridBagConstraints();
		g.gridy = y;
		g.gridx = ++x;
		g.gridheight = 1;
		g.gridwidth = 1;
		g.weightx = 1;
		g.insets = new Insets(0, 0, 0, 0);
		g.anchor = GridBagConstraints.NORTHEAST;
		g.fill = GridBagConstraints.NONE;
		gbLayout.setConstraints(editFilterButton, g);
		panel.add(editFilterButton);

		g = new GridBagConstraints();
		g.gridy = y;
		g.gridx = ++x;
		g.gridheight = 1;
		g.gridwidth = 1;
		g.weightx = 1;
		g.insets = new Insets(0, 0, 0, 0);
		g.anchor = GridBagConstraints.NORTHEAST;
		g.fill = GridBagConstraints.NONE;
		gbLayout.setConstraints(deleteFilterButton, g);
		panel.add(deleteFilterButton);

		return panel;
	}

	@Override
	public void enableDummyActors(boolean b)
	{
		dummyCreationEnabled = b;
	}

	@Override
	public boolean isDummyActorsEnabled()
	{
		return dummyCreationEnabled;
	}

	public List<Akteur> getDummyFilteredActors()
	{
		String[] dummyNames = { "Andreas", "Michael", "Tim", "Florian" };
		List<Akteur> actors = new ArrayList<Akteur>();
		for (int i = 0; i < 4; i++)
			actors.add(new Akteur(dummyNames[i]));
		return actors;
	}

	@Override
	public List<Akteur> getFilteredActors()
	{
		if (dummyCreationEnabled)
			return getDummyFilteredActors();
		InterviewFilterDialog fid = new InterviewFilterDialog(this, this.filter, false);
		List<Akteur> actors = fid.getActors();
		fid.dispose();

		return actors;
	}

	public String getFilter()
	{
		return this.filter;
	}

	public void setFilter(String filter)
	{
System.out.println("FilterPanel setFilter:"+filter);		
		this.filter = filter;
		taFilter.setText(makeReadable(filter));

		if (filter != null && !deleteFilterButton.isEnabled())
			deleteFilterButton.setEnabled(true);
	}
}
