package interview.panels.multi;

import gui.Messages;
import gui.VennMaker;
import gui.utilities.VennMakerUIConfig;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import data.Akteur;
import data.EventProcessor;
import data.Netzwerk;
import data.Relation;
import events.AddRelationEvent;
import events.RemoveRelationEvent;

/**
 * Objects of this class represent a radio panel similar to
 * <code>RadioAnswerPanel</code>. Instead of setting an attribute values to an
 * actor, relations between actors will be set
 * 
 * 
 * 
 */
public class RelationRadioAnswerPanel extends RadioAnswerPanel implements
		ActionListener
{

	/**
	 * 
	 */
	private static final long					serialVersionUID	= -6171182327507993154L;

	public static final int						NAME_LENGTH			= 40;

	public static final int						WORD_OFFSET			= 10;

	/**
	 * The owner of the <code>Relation</code> which will be created
	 */
	private Akteur									relationOwner;

	/**
	 * The network in which the relations should be created
	 */
	private Netzwerk								network;

	/**
	 * The message to show to the user during the display of this panel
	 */
	private String									message;

	/**
	 * The <code>ButtonGroup</code> containg every <code>JRadioButton</code>
	 */
	private Map<ButtonModel, ButtonGroup>	buttonGroups;

	/**
	 * The <code>ButtonModel</code> of the last selected
	 * <code>JRadioButton</code> in the given <code>ButtonGroup</code>
	 */
	private Map<ButtonGroup, ButtonModel>	lastSelectedButtonModel;

	/**
	 * Actors with no <code>Relation</code> are saved in this map with their
	 * actors where they have no <code>Relation</code> to
	 */
	private Map<Akteur, List<Akteur>>		actorsWithNoRelation;

	/**
	 * <code>true</code> if this panel is in pair mode. <code>false</code> if
	 * this panel is in list mode
	 */
	private boolean								inPairMode;

	public RelationRadioAnswerPanel(Akteur relationOwner, Netzwerk network,
			String message, Map<Akteur, List<Akteur>> actorsWithNoRelation)
	{
		this.relationOwner = relationOwner;
		this.network = network;
		this.actorsWithNoRelation = actorsWithNoRelation;

		if (message != null)
		{
			this.message = message;
		}
		else
		{
			this.message = "<html><h1>"
					+ Messages.getString("InterviewElement.NoMatchingActors") //$NON-NLS-1$
					+ "</h1></html>";
		}
	}

	public boolean performChanges()
	{

		for (Akteur actor : actors)
		{
			ButtonModel bm = answers.get(actor).getSelection();

			if (bm == null)
			{

				addToNoRelations(relationOwner, actor);

				if (!VennMaker.getInstance().getProject()
						.getIsDirected(aType.getType()))
					addToNoRelations(actor, relationOwner);

				Relation existing = relationOwner.getRelationTo(actor, network,
						aType);

				if (existing != null)
				{

					EventProcessor.getInstance().fireEvent(
							new RemoveRelationEvent(relationOwner, network, existing));
				}
			}
			else
			{
				String value = bm.getActionCommand();

				Relation existing = relationOwner.getRelationTo(actor, network,
						aType);

				if (existing == null)
				{
					if (!VennMaker.getInstance().getProject()
							.getIsDirected(aType.getType())
							&& actorsWithNoRelation.get(relationOwner) != null
							&& actorsWithNoRelation.get(actor) != null)
					{
						actorsWithNoRelation.get(relationOwner).remove(actor);
						actorsWithNoRelation.get(actor).remove(relationOwner);
					}
					else if (VennMaker.getInstance().getProject()
							.getIsDirected(aType.getType())
							&& actorsWithNoRelation.get(relationOwner) != null)
					{
						actorsWithNoRelation.get(relationOwner).remove(actor);
					}

					Relation relation = new Relation(network, actor, aType, value);
					EventProcessor.getInstance().fireEvent(
							new AddRelationEvent(relationOwner, network, relation));
				}
				else
				{
					existing.setAttributeValue(aType, network, value);
				}
			}

		}

		return true;
	}

	public boolean shouldBeScrollable()
	{
		return true;
	}

	/**
	 * Adds <code>relatedActor</code> to the <code>List</code> of
	 * <code>owner</code> with actors with no <code>Relation</code>
	 */
	private void addToNoRelations(Akteur owner, Akteur relatedActor)
	{
		if (actorsWithNoRelation.get(owner) == null)
		{
			List<Akteur> noRelations = new ArrayList<Akteur>();
			noRelations.add(relatedActor);

			actorsWithNoRelation.put(owner, noRelations);
		}
		else
		{
			actorsWithNoRelation.get(owner).add(relatedActor);
		}
	}

	protected void build()
	{
		if (inPairMode)
			buildPairList();
		else
			buildSingleList();
	}

	private void buildPairList()
	{
		Netzwerk net = VennMaker.getInstance().getActualVennMakerView()
				.getNetzwerk();

		buttonGroups = new HashMap<ButtonModel, ButtonGroup>();
		lastSelectedButtonModel = new HashMap<ButtonGroup, ButtonModel>();

		if (actors != null && actors.size() > 0)
		{
			JPanel completePanel = new JPanel(new BorderLayout());

			String direction = VennMaker.getInstance().getProject().getIsDirected(aType.getType()) ? " -- > " : " < -- > ";
		
			JLabel pairTitle = new JLabel(relationOwner.getName() + direction + actors.get(0).getName(), SwingConstants.CENTER);
			pairTitle.setFont(pairTitle.getFont().deriveFont(VennMakerUIConfig.getFontSize()));
			
			completePanel.add(pairTitle, BorderLayout.NORTH);

			JPanel panel = new JPanel(new GridLayout(actors.size() + 1,
					aType.getPredefinedValues().length + 1));
			
			panel.add(new JPanel());
			Object[] preVals = aType.getPredefinedValues();

			for (Object o : preVals)
			{
				String newString = wrapString(o.toString());
				JLabel label = new JLabel("<html>&nbsp;&nbsp;" + newString
						+ "&nbsp;&nbsp;</html>", SwingConstants.CENTER);

				label.setOpaque(true);
				panel.add(label);
			}

			int i = 0;
			for (Akteur a : actors)
			{
				Color c = (i % 2 == 0) ? panel.getBackground() : Color.lightGray;

				panel.add(new JLabel(" "));

				ButtonGroup bg = new ButtonGroup();

				Object answer = null;

				Relation relation = relationOwner.getRelationTo(a, net, aType);

				if (relation != null)
					answer = relation.getAttributeValue(aType, net);

				i++;
				for (Object o : preVals)
				{
					JRadioButton rb = new JRadioButton();
					rb.setBackground(c);
					rb.setHorizontalAlignment(SwingConstants.CENTER);
					if (o.equals(answer))
					{
						rb.setSelected(true);
						lastSelectedButtonModel.put(bg, rb.getModel());
					}
					rb.setActionCommand(o.toString());
					rb.addActionListener(this);
					buttonGroups.put(rb.getModel(), bg);

					bg.add(rb);
					panel.add(rb);
				}
				answers.put(a, bg);
			}
			completePanel.add(panel, BorderLayout.CENTER);
			this.add(completePanel);
		}
		else
		{
			this.add(new JLabel(this.message));
		}
	}

	private String wrapString(String str)
	{
		if (str.length() <= NAME_LENGTH)
			return str;

		if (str.length() % NAME_LENGTH == 0)
		{
			StringBuffer stbuff = new StringBuffer();

			for (int i = 0; i < str.length(); i += NAME_LENGTH)
				stbuff.append(str.substring(i, i + NAME_LENGTH)).append("<br>");

			if (stbuff.toString().endsWith("<br>"))
				stbuff.delete(stbuff.length() - 4, stbuff.length());

			return stbuff.toString();
		}

		int blanks = 0;

		for (int i = 0; i < str.length(); i++)
			if (str.charAt(i) == 32)
				blanks++;

		int lastBlank = str.lastIndexOf(32);

		if (lastBlank == -1 && str.length() >= NAME_LENGTH + WORD_OFFSET)
		{
			return str.substring(0, NAME_LENGTH).concat("<br>")
					.concat(wrapString(str.substring(NAME_LENGTH, str.length())));
		}

		if (lastBlank != -1)
		{

			int[] blankPositions = new int[blanks];
			int blankIndex = 0;

			for (int i = 0; i < str.length(); i++)
				if (str.charAt(i) == 32)
					blankPositions[blankIndex++] = i;

			int middleBlank = blankPositions[blankPositions.length / 2];

			if (str.length() < NAME_LENGTH + WORD_OFFSET)
				return str.substring(0, middleBlank).concat("<br>")
						.concat(str.substring(middleBlank, str.length()).trim());
			else
				return wrapString(str.substring(0, middleBlank).concat("<br>"))
						.concat(
								wrapString(str.substring(middleBlank, str.length())
										.trim()));
		}

		return str;
	}

	private void buildSingleList()
	{
		Netzwerk net = VennMaker.getInstance().getActualVennMakerView()
				.getNetzwerk();

		buttonGroups = new HashMap<ButtonModel, ButtonGroup>();
		lastSelectedButtonModel = new HashMap<ButtonGroup, ButtonModel>();

		/** actorsize > 0, because EGO is part of it! */
		if (actors != null && actors.size() > 0)
		{
			JPanel completePanel = new JPanel(new BorderLayout());

			completePanel.add(
					new JLabel("<html><h1> &emsp &emsp " + relationOwner.getName()
							+ "</h1></html>", SwingConstants.CENTER),
					BorderLayout.NORTH);

			JPanel combinationPanel = new JPanel(new BorderLayout());

			JPanel topPanel = new JPanel(new GridLayout(1,
					aType.getPredefinedValues().length + 1));

			JPanel panel = new JPanel(new GridLayout(0,
					aType.getPredefinedValues().length + 1));

			Object[] preVals = aType.getPredefinedValues();

			topPanel.add(new JPanel());

			for (Object o : preVals)
			{
				String newString = wrapString(o.toString());
				JLabel label = new JLabel("<html>&nbsp;&nbsp;" + newString
						+ "&nbsp;&nbsp;</html>", SwingConstants.CENTER);

				label.setOpaque(true);
				topPanel.add(label);
			}

			int i = 0;

			for (Akteur a : actors)
			{
				Color c = (i % 2 == 0) ? panel.getBackground() : Color.lightGray;

				JLabel actorLabel = new JLabel(a.getName() + " ",
						SwingConstants.CENTER);
				actorLabel.setVerticalAlignment(SwingConstants.TOP);
				actorLabel.setOpaque(true);
				actorLabel.setBackground(c);

				panel.add(actorLabel);

				ButtonGroup bg = new ButtonGroup();

				Object answer = null;

				Relation relation = relationOwner.getRelationTo(a, net, aType);

				if (relation != null)
					answer = relation.getAttributeValue(aType, net);

				i++;
				for (Object o : preVals)
				{
					JRadioButton rb = new JRadioButton();
					rb.setBackground(c);
					rb.setHorizontalAlignment(SwingConstants.CENTER);
					if (o.equals(answer))
					{
						rb.setSelected(true);
						lastSelectedButtonModel.put(bg, rb.getModel());
					}
					rb.setActionCommand(o.toString());
					rb.addActionListener(this);
					buttonGroups.put(rb.getModel(), bg);

					bg.add(rb);
					JPanel p = new JPanel();
					p.setBackground(c);
					p.add(rb);
					panel.add(p);
				}
				answers.put(a, bg);
			}

			combinationPanel.add(topPanel, BorderLayout.NORTH);

			JScrollPane scrollPane = new JScrollPane(panel);
			scrollPane.setPreferredSize(new Dimension(1000, 350));
			combinationPanel.add(scrollPane, BorderLayout.CENTER);
			completePanel.add(combinationPanel, BorderLayout.CENTER);
			this.add(completePanel);

		}
		else
		{
			this.add(new JLabel(this.message));
		}
	}

	/**
	 * Returns the owner of the current <code>Relations</code>
	 * 
	 * @return the owner of the current <code>Relations</code>
	 */
	public Akteur getRelationOwner()
	{
		return relationOwner;
	}

	/**
	 * Sets the owner of the current <code>Relations</code>
	 * 
	 * @param relationOwner
	 *           the owner of the current <code>Relations</code>
	 */
	public void setRelationOwner(Akteur relationOwner)
	{
		this.relationOwner = relationOwner;
	}

	/**
	 * Returns the network in which the relations should be created
	 * 
	 * @return the network in which the relations should be created
	 */
	public Netzwerk getNetwork()
	{
		return network;
	}

	/**
	 * Sets the network in which the relations should be created
	 * 
	 * @param network
	 *           the network in which the relations should be created
	 */
	public void setNetwork(Netzwerk network)
	{
		this.network = network;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * <code>true</code> if this panel is currently in pair mode.
	 * <code>false</code> if this panel is currently in list mode
	 * 
	 * @param inPairMode
	 *           <code>true</code> if this panel is currently in pair mode.
	 *           <code>false</code> if this panel is currently in list mode
	 */
	public void setPairMode(boolean inPairMode)
	{
		this.inPairMode = inPairMode;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		ButtonModel model = ((JRadioButton) e.getSource()).getModel();
		ButtonGroup selectedGroup = buttonGroups.get(model);

		if (lastSelectedButtonModel.get(selectedGroup) == model
				&& selectedGroup.getSelection() == model)
		{
			lastSelectedButtonModel.put(selectedGroup, null);
			selectedGroup.clearSelection();
		}
		else
		{
			lastSelectedButtonModel.put(selectedGroup, model);
			selectedGroup.setSelected(model, true);
		}
	}
}
