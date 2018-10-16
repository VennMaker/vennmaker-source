package interview;

import files.FileOperations;
import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialog;
import gui.utilities.VennMakerUIConfig;
import interview.elements.InterviewElement;
import interview.elements.StandardElement;
import interview.elements.meta.SwitchToNetworkElement;
import interview.elements.meta.SwitchToNetworkElementAutoDraw;
import interview.settings.UndoElement;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import data.AttributeType;
import data.Audiorecorder;
import data.DrawAllActors;
import data.Netzwerk;

/**
 * The InterviewController controls the interview
 * 
 */
public class InterviewController extends WindowAdapter
{
	private InterviewLayer					layer;

	private InterviewElement				currentElement;

	private JPanel								buttonPanel;

	private JPanel								panel;

	private JDialog							dialog;

	private InterviewElement				currentParentElement;

	private static InterviewController	currentInstance;

	private boolean							calledFromConfigDialog;

	// some elements need this information
	private boolean							nextWasPressed	= true;

	private long								lastLayerModification;

	private JButton							prev;

	private JButton							next;

	private boolean							interviewOver;

	private boolean							startController;

	private boolean							inTestMode;

	private JProgressBar						pBar;

	private JPanel								barPanel;

	public InterviewController()
	{
		this.startController = true;

		Vector<InterviewElement> allIE = InterviewLayer.getInstance()
				.getAllElements();

		Vector<AttributeType> allAT = VennMaker.getInstance().getProject()
				.getAttributeTypes();

		InterviewLayer interviewLayerInstance = InterviewLayer.getInstance();

		String usingNonExistantAttributeType = "";

		for (InterviewElement ie : allIE)
		{
			if (ie instanceof StandardElement)
			{
				StandardElement se = (StandardElement) ie;
				if (((StandardElement) se).getUsedAttributes() != null)
				{
					for (AttributeType at : ((StandardElement) se)
							.getUsedAttributes())
					{
						if (!allAT.contains(at))
						{
							usingNonExistantAttributeType += ie.getElementNameInTree()
									+ "\r\n";
							interviewLayerInstance.removeElement(ie);
						}
					}
				}
				else
				{
					usingNonExistantAttributeType += ie.getElementNameInTree()
							+ "\r\n";
					interviewLayerInstance.removeElement(ie);
				}
			}
		}

		if (!usingNonExistantAttributeType.equals("") )
		{
			JOptionPane.showMessageDialog(null,
					Messages.getString("InterviewController.AttributesInElements")
							+ usingNonExistantAttributeType);
		}

		if (InterviewLayer.getInstance().getAllElements().size() == 0)
		{
			this.startController = false;
			return;
		}

		initLayer();
		layer.resetPointer();
		currentElement = layer.getCurrentElement();

		currentElement.startTime();

		currentInstance = this;

		panel = new JPanel(new BorderLayout());
		buttonPanel = new JPanel(new GridLayout(0, 2));
		pBar = new JProgressBar();
		pBar.setMinimum(0);
		JLabel lbl0Pro = new JLabel("<html><b>0</b>%");
		JLabel lbl100Pro = new JLabel("<html><b>100</b>%");

		barPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		barPanel.add(lbl0Pro, c);

		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 10, 0, 10);
		barPanel.add(pBar, c);

		c.gridx = 2;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 0, 0, 0);
		barPanel.add(lbl100Pro, c);

		barPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("Interview.Progress")));

		ResizableIcon nextIcon = null;
		ResizableIcon prevIcon = null;
		try
		{
			nextIcon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath(Messages
											.getString("VennMaker.Icon_Next"))), new Dimension(48, 48)); //$NON-NLS-1$
			prevIcon = SvgBatikResizableIcon
					.getSvgIcon(
							new FileInputStream(FileOperations
									.getAbsolutePath(Messages
											.getString("VennMaker.Icon_Previous"))), new Dimension(48, 48)); //$NON-NLS-1$
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		String nextText = "<html><b>" + Messages.getString("InterviewController.Next") + " </b><i>(F3)</html>"; //$NON-NLS-1$
		String prevText = "<html><i>(F2)</i><b> " + Messages.getString("InterviewController.Prev") + "</html>"; //$NON-NLS-1$

		next = new JButton();
		prev = new JButton();

		next.setFont(UIManager.getFont("Label.font").deriveFont(VennMakerUIConfig.getFontSize()));
		prev.setFont(UIManager.getFont("Label.font").deriveFont(VennMakerUIConfig.getFontSize()));
		
		/**
		 * add ActionListener and shortcuts to buttons
		 */
		PrevListener pl = new PrevListener();
		prev.setText(prevText);
		prev.setIcon(prevIcon);
		prev.addActionListener(pl);
		prev.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F2"), prevText);
		prev.getActionMap().put(prevText, pl);

		NextListener nl = new NextListener();
		/* move text to the left (icon to the right) */
		next.setHorizontalTextPosition(SwingConstants.LEFT);
		next.setText(nextText);
		next.setIcon(nextIcon);
		next.addActionListener(nl);
		next.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("F3"), nextText);
		next.getActionMap().put(nextText, nl);

		buttonPanel.add(prev);
		buttonPanel.add(next);

		panel.add(barPanel, BorderLayout.NORTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.add(currentElement.getControllerDialog(), BorderLayout.CENTER);

		dialog = new JDialog(VennMaker.getInstance());
		dialog.setContentPane(panel);
		//dialog.setModal(true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		/* prevent timer to tick continuously, when current interview is closed */
		dialog.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				if (currentElement != null)
					currentElement.stopTime();
			}
		});

		dialog.setSize(VennMaker.getInstance().getSize());
		dialog.setLocation(VennMaker.getInstance().getLocation());
		dialog.setMinimumSize(new Dimension(640, 480));
		dialog.addWindowListener(this);
	}

	/**
	 * Return an Instance of this InterviewController
	 */
	public static InterviewController getInstance()
	{
		return currentInstance;
	}

	private void showErrorMessage()
	{

		if (calledFromConfigDialog)
			JOptionPane.showMessageDialog(ConfigDialog.getInstance(),
					Messages.getString("InterviewController.NoInterview"));
		else
			JOptionPane.showMessageDialog(VennMaker.getInstance(),
					Messages.getString("InterviewController.NoInterview"));

	}

	/**
	 * Initalizes the InterviewLayer
	 */
	public void initLayer()
	{
		layer = InterviewLayer.getInstance();
	}

	class NextListener extends AbstractAction
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			next();
		}
	}

	class PrevListener extends AbstractAction
	{
		private static final long	serialVersionUID	= 1L;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			previous();
		}
	}

	/**
	 * Performs a control action
	 * 
	 * @param next
	 *           true if next was clicked, false if prev was clicked
	 */
	private void performAction(boolean next)
	{
		nextWasPressed = next;
		/* stop current timemeasuring */
		currentElement.stopTime();

		if (currentElement.getElementToJump() == -1
				|| !currentElement.isConditionTrue())
		{
			/**
			 * No jump neccessary
			 */

			int internalListPointer = currentElement.getInternalPointerValue();
			if (next
					&& currentElement.getInternalListSize() > 0
					&& (internalListPointer + 1) < currentElement
							.getInternalListSize())
			{
				/**
				 * Load the next Element in the internal List, if we have elements
				 * in it and if we are not at the end of the list
				 */
				currentElement = currentElement.getNextElementInList();
				// pBar.setMaximum(pBar.getMaximum() +
				// currentElement.getInternalListSize());

			}
			else if (!next && currentElement.getInternalListSize() > 0)
			{
				/**
				 * If we are not at the beginning of the internal list, load the
				 * previous element in the internal list. If we are at the beginning
				 * of the internal list and currentElement is a child, load it's
				 * parent
				 */
				if (internalListPointer > 0)
				{
					currentElement = currentElement.getPreviousElementInList();
				}
				else if (currentElement.isChild())
				{
					currentElement.resetInternalPointer();
					currentElement.resetChildPointer();
					currentElement = currentElement.getParent();
				}
				else
				{
					/**
					 * Reset all pointers before loading the next previous element.
					 * Pointers have to be resettet, because if "next" is clicked, we
					 * want to start at the beginning...
					 */

					currentElement.resetInternalPointer();
					currentElement.resetChildPointer();
					currentElement = layer.getPreviousElement();

					InterviewElement parent;

					/**
					 * If the loaded element has children, load the last element of
					 * the last child
					 */
					while (currentElement.getNumberOfChildren() > 0)
					{
						parent = currentElement;
						currentElement = currentElement.getChildAt(currentElement
								.getNumberOfChildren() - 1);

						parent.setChildPointer(parent.getNumberOfChildren());
					}
				}
			}
			else if (next
					&& (internalListPointer >= currentElement.getInternalListSize() - 1
							&& currentElement.getNumberOfChildren() > 0 || currentElement
							.getNumberOfChildren() > 0
							&& currentElement.getInternalListSize() == 0))
			{
				currentParentElement = currentElement;

				/**
				 * If "next" is clicked, and there is a child left, take this child
				 */
				if (next
						&& currentParentElement.getChildPointer() + 1 <= currentParentElement
								.getNumberOfChildren())
				{
					currentElement = currentParentElement.getNextChild();
				}
			}
			else if (currentElement.getNumberOfChildren() == 0 && next
					&& currentElement.isChild()
					&& currentElement.getInternalListSize() == 0)
			{
				/**
				 * If we are at the last child element of a branch in the tree, and
				 * "next" is clicked, we go to the last node wich parent has childs
				 * left and start at the top of that branch
				 */
				while (currentElement.isChild()
						&& (!(currentElement.getParent().getChildPointer() < currentElement
								.getParent().getNumberOfChildren()) || (currentElement
								.getParent().getChildPointer() == 0 && currentElement
								.getParent().getNumberOfChildren() == 1)))
				{
					currentElement.resetInternalPointer();
					currentElement.resetChildPointer();
					currentElement = currentElement.getParent();
				}

				/**
				 * If the element is not a "top-level" element, we take the next
				 * child of the current node's parent
				 */
				if (currentElement.getParent() != null)
				{
					currentElement.resetChildPointer();
					currentElement = currentElement.getParent().getNextChild();
				}
				else
				{
					/**
					 * If the Element is a "top-level" element, we take the next
					 * "top-level"-element from the layer
					 */
					currentElement.resetChildPointer();
					currentElement.resetInternalPointer();
					currentElement = layer.getNextElement();
				}
			}
			else if (currentElement.isChild() && !next)
			{
				/**
				 * If we are at a child element, and "previous" is clicked
				 */

				currentElement.resetInternalPointer();

				if (currentElement.getParent().getChildPointer() == 1
						&& currentElement.getParent().getNumberOfChildren() < 2)
				{
					/**
					 * If we are at the first child of a node, and no childs are left
					 * take the node
					 */

					currentElement.resetChildPointer();
					currentElement = currentElement.getParent();
					currentElement.resetChildPointer();
				}
				else
				{
					InterviewElement parent = currentElement.getParent(); // <<
																							// Testweise
					InterviewElement child = currentElement;
					currentElement = parent;

					/**
					 * Locate the position of the last child we were at
					 */

					int childIndex = currentElement.getIndexOfChild(child);

					if (childIndex > 0)
					{
						/**
						 * Get the previous element
						 */
						currentElement = parent.getChildAt(childIndex - 1);
						parent.setChildPointer(childIndex);

						/**
						 * If the previos element has childs, take the last child, and
						 * search for the last node
						 */
						while (currentElement.getNumberOfChildren() > 0)
						{
							parent = currentElement;
							currentElement = currentElement.getChildAt(currentElement
									.getNumberOfChildren() - 1);

							parent.setChildPointer(parent.getNumberOfChildren());
						}
					}
					else if (childIndex == 0)
					{
						currentElement.resetChildPointer();
						currentElement = parent;
						currentElement.resetChildPointer();
					}
				}
			}
			else
			{
				currentElement.resetChildPointer();
				currentElement.resetInternalPointer();

				if (currentParentElement != null)
				{
					currentParentElement.resetChildPointer();
					currentParentElement.resetInternalPointer();
				}

				currentParentElement = null;

				if (next)
				{			
					currentElement = layer.getNextElement();
				}
				else
				{
					InterviewElement elem = layer.getPreviousElement();
					if (elem.getNumberOfChildren() == 0)
					{
						currentElement = elem;

						if (currentElement.getInternalListSize() > 0)
							currentElement.setInternalPointer(currentElement
									.getInternalListSize() - 1);
					}
					else
					{
						InterviewElement startNode = elem.getChildAt(elem
								.getNumberOfChildren() - 1);

						while (startNode.getNumberOfChildren() > 0)
						{
							startNode.resetChildPointer();
							startNode.resetInternalPointer();
							startNode = startNode.getChildAt(elem
									.getNumberOfChildren() - 1);
						}

						if (startNode.getInternalListSize() > 0)
						{
							startNode.setInternalPointer(startNode
									.getInternalListSize() - 1);
						}
						currentElement = startNode;
					}
				}
			}

			if (next)
				pBar.setValue(pBar.getValue() + 1);
			else
				pBar.setValue(pBar.getValue() - 1);
		}
		else
		{
			currentElement = layer.getElementAt(currentElement.getElementToJump());

			InterviewElement rootElement = layer
					.getRootFromElement(currentElement);

			int index = layer.getTopLevelIndex(rootElement);

			layer.setPointer(index);

			if (currentElement.getParent() != null)
			{
				currentElement.getParent()
						.setChildPointer(
								currentElement.getParent().getIndexOfChild(
										currentElement) + 1);
			}

			int elementCounter = 0;

			for (int i = layer.getIndexOfElement(currentElement); i > 0; i--)
			{
				if (layer.getElementAt(i).getChildren().size() == 0)
					elementCounter++;
				else
					elementCounter += layer.getElementAt(i).getChildren().size();
			}

			pBar.setValue(elementCounter);
		}

		if (currentElement == null)
		{
			/**
			 * The end of the interview has been reached
			 */

			VennMaker.getInstance().setNextVisible(false);

			this.dialog.dispose();

			String info = Messages.getString("InterviewController.3");

			// Stop recording if running
			if (Audiorecorder.getInstance().isRunning())
			{
				Audiorecorder.getInstance().stopRecording();
				info += "\n\n"
						+ Messages
								.getString("AudioRecorderElement.UserInformationStop");
			}

			JOptionPane.showMessageDialog(VennMaker.getInstance(), info); //$NON-NLS-1$

			// if called from Config Dialog, set it visible again
			if (calledFromConfigDialog)
				ConfigDialog.getInstance().setVisible(true);
			else
				VennMaker.getInstance().refresh();

			VennMaker
					.getInstance()
					.getProject()
					.setCurrentNetzwerk(
							VennMaker.getInstance().getProject().getNetzwerke().get(0));

			InterviewLayer.getInstance().resetPointer();

			this.interviewOver = true;

			undo();

			this.inTestMode = false;
		}
		/* there is a current element to write onto (start timemeasuring) */
		else
		{
			currentElement.startTime();
		}

	}

	public void setLastLayerModification(long lastModified)
	{
		this.lastLayerModification = lastModified;
	}

	public long getLastLayerModification()
	{
		return this.lastLayerModification;
	}

	/**
	 * This method starts the dialog
	 */
	public void start()
	{
		if (!startController
				|| InterviewLayer.getInstance().getAllElements().size() == 0)
		{
			showErrorMessage();
			VennMaker.getInstance().refresh();
			return;
		}
		pBar.setValue(0);
		pBar.setMaximum(layer.getAllElements().size() - 1);
		currentElement = InterviewLayer.getInstance().getCurrentElement();
		this.interviewOver = false;
		if (currentElement != null)
			currentElement.setData(); // update the element

		if (!inTestMode)
		{
			while (currentElement != null && currentElement.shouldBeSkipped()
					&& !interviewOver)
				this.performAction(true);
		}
		else if (currentElement != null && inTestMode
				&& currentElement.shouldBeSkipped())
		{
			if (currentElement instanceof StandardElement)
				((StandardElement) currentElement).initPreview();
		}

		if (currentElement != null
				&& !(currentElement instanceof SwitchToNetworkElement))
		{
			recreatePanel(currentElement.getControllerDialog());

			currentElement.startTime();

			prev.setEnabled(false);

			this.dialog.setVisible(true);
		}
		
		
		if (currentElement != null
				&& !(currentElement instanceof SwitchToNetworkElementAutoDraw))
		{
			recreatePanel(currentElement.getControllerDialog());

			currentElement.startTime();

			prev.setEnabled(false);

			this.dialog.setVisible(true);
		}
	}

	/**
	 * Got the controller called from the ConfigDialog?
	 * 
	 * @return true if controller was called from the ConfigDialog
	 */
	public boolean calledFromConfigDialog()
	{
		return this.calledFromConfigDialog;
	}

	public JDialog getDialog()
	{
		return dialog;
	}

	public boolean wasNextPressed()
	{
		return nextWasPressed;
	}

	public void setCalledFromConfigDialog(boolean calledFromConfigDialog)
	{
		this.calledFromConfigDialog = calledFromConfigDialog;
	}

	/**
	 * Recreates the panel of the controller
	 * 
	 * @param p
	 *           panel to show
	 */
	private void recreatePanel(JPanel p)
	{
		panel.removeAll();
		panel.add(p, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.add(barPanel, BorderLayout.NORTH);
		panel.revalidate();
		panel.repaint();

		dialog.validate();
		dialog.repaint();

		p.requestFocusInWindow();
	}

	/**
	 * Switches from controller to VennMaker
	 */
	public void switchToNetwork(Netzwerk net)
	{
	
		this.dialog.setVisible(false);
		if (calledFromConfigDialog)
			ConfigDialog.getInstance().setVisible(false);
		VennMaker v = VennMaker.getInstance();
		Vector<Netzwerk> networks = v.getProject().getNetzwerke();
		// falls netzwerk schon geloescht ist setzte auf Netzwerk der ActualView
		if (!networks.contains(net))
			net = v.getActualVennMakerView().getNetzwerk();

		v.setNextVisible(true);
		v.refresh();
		v.getProject().setCurrentNetzwerk(net);
		v.setCurrentNetwork(net);
	}
	
	

	/**
	 * Switches from controller to VennMaker and draw all actors into the network map
	 */
	public void switchToNetworkAutoDraw(Netzwerk net)
	{
		this.dialog.setVisible(false);
		if (calledFromConfigDialog)
			ConfigDialog.getInstance().setVisible(false);
		VennMaker v = VennMaker.getInstance();
		Vector<Netzwerk> networks = v.getProject().getNetzwerke();
		// falls netzwerk schon geloescht ist setzte auf Netzwerk der ActualView
		if (!networks.contains(net))
			net = v.getActualVennMakerView().getNetzwerk();

		new DrawAllActors().drawSpringEmbedder(net);
		v.setNextVisible(true);
		v.refresh();
		v.getProject().setCurrentNetzwerk(net);
		v.setCurrentNetwork(net);
	}

	/**
	 * Behavior of this Method is equal to clicking "next" in the controller
	 */
	public void next()
	{
		boolean wasSwitchedToNetwork = currentElement instanceof SwitchToNetworkElement;
		boolean wasSwitchedToNetworkAutoDraw = currentElement instanceof SwitchToNetworkElementAutoDraw;
		
		if (!currentElement.shouldBeSkipped()) System.out.println("SHOULD NOT BE SKIPPED().............");
		
		if (!currentElement.validateInput() || !currentElement.shouldBeSkipped()
				&& !currentElement.writeData())
			return;

		if (inTestMode
				&& !(currentElement.getInternalPointerValue() + 1 < currentElement
						.getInternalListSize()))
		{
			// currentElement.deinitPreview();
			currentElement.resetInternalActorList();
		}
		
		this.performAction(true);

		if (!inTestMode)
		{
			while (currentElement != null && currentElement.shouldBeSkipped()
					&& !interviewOver)
				this.performAction(true);
		}
		else if (currentElement != null && inTestMode
				&& currentElement.shouldBeSkipped())
		{
			if (!(currentElement.getInternalPointerValue() + 1 <= currentElement
					.getInternalListSize()))
				currentElement.initPreview();
		}

		if (currentElement != null)
		{
			
			currentElement.setData();

			recreatePanel(currentElement.getControllerDialog());
		

			this.dialog.setVisible(false);
			
			if (InterviewLayer.getInstance().getPointer() == 0
					&& !currentElement.isChild()
					&& currentElement.getInternalPointerValue() == 0)
				prev.setEnabled(false);
			else
				prev.setEnabled(true);

			/*
			if (wasSwitchedToNetwork
					&& !(currentElement instanceof SwitchToNetworkElement)){
				this.dialog.setVisible(true);
			}else
			if (wasSwitchedToNetworkAutoDraw
					&& !(currentElement instanceof SwitchToNetworkElementAutoDraw)){
				this.dialog.setVisible(true);
			}
			*/
			
			//show dialog if the current element is no switchtonetwork (autodraw) element 
			if ( !(currentElement instanceof SwitchToNetworkElement) && !(currentElement instanceof SwitchToNetworkElementAutoDraw) )
				this.dialog.setVisible(true);

		}
	}

	/**
	 * Behaveior of this Method is equal to clicking "previous" in the controller
	 */
	public void previous()
	{
		// if(!currentElement.writeData())
		// return;

		if (inTestMode && currentElement.getInternalPointerValue() - 1 < 0)
		{
			currentElement.deinitPreview();
			currentElement.resetInternalActorList();
		}

		this.performAction(false);

		if (!inTestMode)
		{
			while (currentElement != null && currentElement.shouldBeSkipped()
					&& !interviewOver)
			{
				/**
				 * Falls erstes Element geskipped werden soll, wird von nun an
				 * vorw�rts gelaufen.
				 */
				if (layer.getPointer() == 0
						&& currentElement.getInternalPointerValue() == 0)
				{
					next();
					prev.setEnabled(false);
					return;
				}
				this.performAction(false);
			}
		}
		else if (currentElement != null && inTestMode
				&& currentElement.shouldBeSkipped())
		{
			if (currentElement.getInternalPointerValue() - 1 < 0)
				currentElement.initPreview();
		}

		if (currentElement != null)
		{
			currentElement.setData();
			recreatePanel(currentElement.getControllerDialog());

			if (InterviewLayer.getInstance().getPointer() == 0
					&& !currentElement.isChild()
					&& currentElement.getInternalPointerValue() == 0)
				prev.setEnabled(false);
			else
				prev.setEnabled(true);
		}

		if (!dialog.isVisible() && currentElement != null
				&& !currentElement.isMetaElement())
			dialog.setVisible(true);
	}

	@Override
	public void windowClosing(WindowEvent arg0)
	{
		undo();
		this.inTestMode = false;

		// Stop recording if running
		if (Audiorecorder.getInstance().isRunning())
			Audiorecorder.getInstance().stopRecording();

		// if called from Config Dialog, set it visible again
		if (calledFromConfigDialog)
			ConfigDialog.getInstance().setVisible(true);
		else
			VennMaker.getInstance().refresh();
	}

	public void startInTestMode()
	{
		this.inTestMode = true;

		start();
	}

	public boolean isInTestMode()
	{
		return this.inTestMode;
	}

	public void undo()
	{
		if (this.inTestMode)
			for (InterviewElement element : layer.getAllElements())
				if (element instanceof UndoElement)
					((UndoElement) element).undo();
	}

}
