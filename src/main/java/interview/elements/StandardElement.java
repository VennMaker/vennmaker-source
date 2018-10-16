package interview.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.utilities.VennMakerUIConfig;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.UpdateListener;
import interview.configuration.NameInfoSelection.NameInfoSelector;
import interview.configuration.attributesSelection.AttributeSelector;
import interview.configuration.attributesSelection.MultiAttributePanel;
import interview.configuration.attributesSelection.SingleAttributePanel;
import interview.configuration.filterSelection.FilterSelector;
import interview.elements.information.InputElementInformation;
import interview.panels.SpecialPanel;
import interview.settings.UndoElement;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Akteur;
import data.AttributeType;

public class StandardElement extends InterviewElement implements
		UpdateListener, UndoElement
{
	private static final long					serialVersionUID	= 1L;

	protected boolean								inPreviewMode		= false;

	/**
	 * Get the name and information text from
	 */
	protected NameInfoSelector					nSelector;

	/**
	 * Get the attribute / attributes from (depending on input element)
	 */
	protected AttributeSelector				aSelector;

	/**
	 * unique panel depending on child (concrete input element)
	 */
	protected SpecialPanel						specialPanel;

	/**
	 * the whole panel displayed in the controller (getControllerDialog())
	 */
	private JPanel									controllerPanel;

	protected String								instructionText;

	protected JPanel								configurationPanel;

	protected Map<String, AttributeType>	attributesAndQuestions;

	/** String to store the active attributetype (enabling not only "ACTOR") */
	private String									currentType			= "ACTOR";

	public StandardElement(NameInfoSelector nSelector, FilterSelector fSelector,
			AttributeSelector aSelector, boolean hasPreview)
	{
		
		System.out.println("StandardElement: constructor: fSelector:"+fSelector);
		this.nSelector = nSelector;
		this.fSelector = fSelector;
		this.aSelector = aSelector;
		this.hasPreview = hasPreview;
		if (aSelector != null)
			aSelector.addUpdateListener(this);
	}

	@Override
	public void initPreview()
	{
		this.inPreviewMode = true;
		if (this.fSelector != null)
			this.fSelector.enableDummyActors(true);
	}

	public void deinitPreview()
	{
		this.inPreviewMode = false;
		if (this.fSelector != null)
			this.fSelector.enableDummyActors(false);
	}

	@Override
	public JPanel getControllerDialog()
	{
		controllerPanel = new JPanel();
		//
		// controllerPanel = new JPanel(new BorderLayout());

		// push Focus forward to special Panel
		controllerPanel.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if (specialPanel != null)
					specialPanel.requestFocusInWindow();
			}
		});
		// controllerPanel.removeAll();
		GridBagLayout gbLayout = new GridBagLayout();
		controllerPanel.setLayout(gbLayout);
		GridBagConstraints g = new GridBagConstraints();

		int x = 0, y = 0;
		// if SingleAttribute, display question first,
		// instead of displaying in special panel
		if (aSelector != null && aSelector.isSingleAttributeSelector())
		{
			Map<String, AttributeType> questions = aSelector
					.getAttributesAndQuestions();
			if (questions == null)
			{
				controllerPanel
						.add(new JLabel("no Question/Attribute configured!"));
				return controllerPanel;
			}
			String q = questions.keySet().iterator().next();
			JLabel lblQuestion = new JLabel("<html>" + q + "</html>");
			lblQuestion.setFont(lblQuestion.getFont().deriveFont(Font.BOLD)
					.deriveFont(VennMakerUIConfig.getFontSize() + 4));
			g.gridx = x;
			g.gridy = y;
			g.gridheight = 1;
			g.gridwidth = 1;
			g.weightx = 1;
			g.weighty = 0;
			g.insets = new Insets(5, 5, 5, 5);
			g.anchor = GridBagConstraints.NORTHWEST;
			g.fill = GridBagConstraints.HORIZONTAL;

			gbLayout.setConstraints(lblQuestion, g);
			controllerPanel.add(lblQuestion);
		}

		if (nSelector != null)
		{
			JPanel panel = nSelector.getControllerPanel();

			g.gridx = x;
			g.gridy = ++y;
			g.gridheight = 1;
			g.gridwidth = 1;
			g.weightx = 1;
			g.weighty = 1;
			g.insets = new Insets(5, 5, 5, 5);
			g.anchor = GridBagConstraints.NORTHWEST;
			g.fill = GridBagConstraints.BOTH;

			gbLayout.setConstraints(panel, g);
			controllerPanel.add(panel);
		}

		if (specialPanel != null)
		{
			specialPanel.setAttributesAndQuestions(aSelector
					.getAttributesAndQuestions());
			specialPanel.setActors(fSelector.getFilteredActors());
			specialPanel.setActorPointer(getInternalPointerValue());
			specialPanel.rebuild();
			g.gridx = x;
			g.gridy = ++y;
			g.gridheight = 1;
			g.gridwidth = 1;
			g.weightx = 1;
			g.weighty = 5;
			g.insets = new Insets(5, 0, 0, 0);
			g.anchor = GridBagConstraints.CENTER;
			g.fill = GridBagConstraints.BOTH;
			if (specialPanel.shouldBeScrollable())
			{
				JScrollPane scroll = new JScrollPane(specialPanel);
				gbLayout.setConstraints(scroll, g);
				controllerPanel.add(scroll);
			}
			else
			{
				gbLayout.setConstraints(specialPanel, g);
				controllerPanel.add(specialPanel);
			}
		}

		/*
		 * if(specialPanel!=null) { specialPanel.setAttributesAndQuestions(
		 * aSelector.getAttributesAndQuestions() );
		 * specialPanel.setActors(fSelector.getFilteredActors());
		 * specialPanel.setActorPointer(getInternalPointerValue());
		 * specialPanel.rebuild(); controllerPanel.add(specialPanel); }
		 */
		return controllerPanel;
	}

	@Override
	public boolean writeData()
	{
		if (specialPanel != null)
		{
			boolean b = specialPanel.performChanges();
			VennMaker.getInstance().getActualVennMakerView().updateView();
			VennMaker.getInstance().getActualVennMakerView().repaint();
			return b;
		}
		return false;
	}

	@Override
	public void setData()
	{
		if (specialPanel != null)
			specialPanel.rebuild();
	}

	@Override
	public JPanel getConfigurationDialog()
	{
		if (configurationPanel != null)
		{
			if (aSelector != null)
			{
				if (attributesAndQuestions != null)
				{
					List<AttributeType> aTypesProject = VennMaker.getInstance()
							.getProject().getAttributeTypes("ACTOR");
					List<String> toDelete = new ArrayList<String>();
					for (String s : attributesAndQuestions.keySet())
					{
						AttributeType a = attributesAndQuestions.get(s);
						boolean stillPresent = false;
						for (AttributeType ap : aTypesProject)
						{
							if (ap.getId() == a.getId())
							{
								stillPresent = true;
								break;
							}
						}
						if (!stillPresent)
						{
							toDelete.add(s);
						}
					}
					for (String s : toDelete)
						attributesAndQuestions.remove(s);
				}

				aSelector.setAttributesAndQuestions(attributesAndQuestions);
				if (!aSelector.hasAttributes())
					this.update();
			}

			return configurationPanel;
		}

		configurationPanel = new JPanel();
		update();

		return configurationPanel;
	}

	/**
	 * every Input element has to set name, description
	 */
	@Override
	public boolean addToTree()
	{
		if (aSelector != null)
		{
			if (!aSelector.hasAttributes())
			{
				Icon icon = new ImageIcon("icons/intern/icon.png");
				JOptionPane
						.showMessageDialog(
								ConfigDialog.getInstance(),
								Messages
										.getString("InterviewElement.AtLeastOneAttribute.Description"), //$NON-NLS-1$
								Messages
										.getString("InterviewElement.AtLeastOneAttribute.Title"), //$NON-NLS-1$
								JOptionPane.INFORMATION_MESSAGE, icon);
				return false;
			}
		}
		setElementNameInTree(nSelector.getName());
		setDescription(nSelector.getInfo());

		if (aSelector != null)
			attributesAndQuestions = aSelector.getAttributesAndQuestions();

		return true;
	}

	@Override
	public String getInstructionText()
	{
		return instructionText;
	}

	public void update(AttributeType a)
	{
		if (a != null)
			if (aSelector != null)
			{
				// aSelector.updatePanel(VennMaker.getInstance().getProject().getAttributeTypes("ACTOR"));
				this.currentType = a.getType();
				aSelector.updatePanel(VennMaker.getInstance().getProject()
						.getAttributeTypes(this.currentType));
				if (aSelector instanceof SingleAttributePanel)
					((SingleAttributePanel) aSelector).setSelectedAType(a);
				this.update();
			}
	}

	@Override
	public void update()
	{

		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints g = new GridBagConstraints();

		configurationPanel.removeAll();
		configurationPanel.setLayout(gbLayout);
		configurationPanel.setMinimumSize(new Dimension(280, 250));

		int y = 0;
		g = new GridBagConstraints();
		JPanel p = null;

		if (nSelector != null)
		{
			p = nSelector.getConfigurationPanel();

			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.fill = GridBagConstraints.BOTH;
			g.gridx = 0;
			g.gridy = y++;
			g.gridwidth = 1;
			g.gridheight = 1;
			g.weightx = 1;
			g.weighty = 1;
			gbLayout.setConstraints(p, g);
			configurationPanel.add(p);
		}

		if (aSelector != null)
		{
			aSelector.updatePanel(VennMaker.getInstance().getProject()
					.getAttributeTypes(currentType)); //$NON-NLS-1$
			p = aSelector.getConfigurationPanel();
			g.gridy = y++;
			g.weighty = (aSelector instanceof MultiAttributePanel) ? 1 : 0.2;
			g.fill = GridBagConstraints.BOTH;
			g.insets = new Insets(10, 0, 0, 0);
			gbLayout.setConstraints(p, g);
			configurationPanel.add(p);
		}

		if (this.fSelector != null)
		{
			p = this.fSelector.getConfigurationPanel();

			g.gridy = y++;
			g.weighty = 1;
			g.anchor = GridBagConstraints.FIRST_LINE_START;
			g.fill = GridBagConstraints.BOTH;
			gbLayout.setConstraints(p, g);
			configurationPanel.add(p);
		}

		configurationPanel.revalidate();
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		Integer[] filterIndex = null;
		String filter = null;

		if (this.fSelector != null && this.fSelector.getFilterIndex() != null)
		{
			filterIndex = new Integer[this.fSelector.getFilterIndex().size()];

			filterIndex = this.fSelector.getFilterIndex().toArray(filterIndex);
			filter = this.fSelector.getFilter();
		}

		InputElementInformation elem = new InputElementInformation(
				nSelector.getName(), filter, filterIndex,
				aSelector.getAttributesAndQuestions(), nSelector.getInfo());

		String name = nSelector.getName();
		if (name == null || name.equals(""))
			name = getElementNameInTree();
		elem.setElementName(name);
		elem.setId(this.getId());
		elem.setElementClass(this.getClass());
		elem.createChildInformation(children);

		if (parent != null)
			elem.setParentInformation(parent);

		return elem;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (!(information instanceof InputElementInformation))
			return;

		getConfigurationDialog();

		InputElementInformation info = (InputElementInformation) information;
		nSelector.setName(info.getElementName());
		nSelector.setInfo(info.getInfo());
	System.out.println("StandardElement: setElementInfo()");
		if (info.getFilter() != null && info.getFilterIndex() != null)
		{
			this.fSelector.setFilter(info.getFilter());
			this.fSelector.setFilterIndex(new ArrayList<Integer>(Arrays.asList(info
					.getFilterIndex())));	
		}

		setElementNameInTree(nSelector.getName());
		Map<String, AttributeType> newAttributesAndQuestions = new HashMap<String, AttributeType>();

		Vector<AttributeType> aTypes = VennMaker.getInstance().getProject()
				.getAttributeTypes(); //$NON-NLS-1$
System.out.println("info.getAttributesAndQuestions(): "+info.getAttributesAndQuestions() );
System.out.println("info name: "+info.getElementName());

if (info.getAttributesAndQuestions() != null)
		for (Map.Entry<String, AttributeType> entry : info
				.getAttributesAndQuestions().entrySet())
		{
			for (AttributeType type : aTypes)
			{
				if (type.toString().equals(entry.getValue().toString()))
				{
					newAttributesAndQuestions.put(entry.getKey(), type);
					break;
				}
			}
		}

		aSelector.setAttributesAndQuestions(newAttributesAndQuestions);
		attributesAndQuestions = newAttributesAndQuestions;

		InterviewLayer.getInstance().createChild(information, this);
	}

	@Override
	public boolean shouldBeSkipped()
	{
System.out.println("StandardElement shouldBeSkipped A");
		if (this.fSelector != null)
		{
System.out.println("StandardElement shouldBeSkipped B");			
			List<Akteur> filteredActors = this.fSelector.getFilteredActors();
			if (filteredActors == null || filteredActors.size() <= 0)
			{
System.out.println("StandardElement shouldBeSkipped C");				
				return true;
			}
		}
System.out.println("StandardElement shouldBeSkipped D");		
		return false;
	}

	@Override
	public boolean undo()
	{
		if (specialPanel != null)
		{
			boolean b = specialPanel.undoChanges();
			VennMaker.getInstance().getActualVennMakerView().updateView();
			VennMaker.getInstance().getActualVennMakerView().repaint();
			return b;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return getElementNameInTree();
	}

	/**
	 * Returns <code>true</code> if this <code>InterviewElement</code> is in
	 * preview mode. Returns <code>false</code> if not.
	 * 
	 * @return <code>true</code> if this <code>InterviewElement</code> is in
	 *         preview mode. Returns <code>false</code> if not.
	 */
	public boolean isInPreviewMode()
	{
		return this.inPreviewMode;
	}

	public boolean isAttributeTypeInUse(AttributeType aType)
	{
		if (aSelector == null)
			return false;
		Map<String, AttributeType> quest = aSelector.getAttributesAndQuestions();
		for (String s : quest.keySet())
		{
			AttributeType a = quest.get(s);
			if (a.getId() == aType.getId())
				return true;
		}
		return false;
	}

	/**
	 * returns the attributes used by this element
	 * 
	 * @return
	 */
	public Vector<AttributeType> getUsedAttributes()
	{
		Vector<AttributeType> returnVector = new Vector<AttributeType>();

		if (aSelector != null)
		{
			Map<String, AttributeType> quest = aSelector
					.getAttributesAndQuestions();
			if (quest != null)
			{
				for (String s : quest.keySet())
				{
					AttributeType a = quest.get(s);
					returnVector.add(a);
				}
			}
			else
			{
				return null;
			}
		}
		return returnVector;
	}

	@Override
	public boolean validateInput()
	{
		if (specialPanel != null)
			return specialPanel.validateInput();
		else
			return true;
	}
}
