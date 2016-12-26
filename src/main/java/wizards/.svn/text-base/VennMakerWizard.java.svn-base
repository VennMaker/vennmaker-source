/**
 * 
 */
package wizards;

import files.FileOperations;
import files.ImageOperations;
import gui.Messages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jvnet.flamingo.common.JCommandButton;
import org.jvnet.flamingo.common.icon.ResizableIcon;
import org.jvnet.flamingo.svg.SvgBatikResizableIcon;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Ein Wizard wird vom WizardController ausgeführt und kann über Dialoge mit
 * dem Anwender kommunizieren oder steuernd auf das VennMaker-Fenster einwirken.
 * 
 * Die Methoden invoke() und shutdown() werden beim Start bzw. vor Ende des
 * Wizards aufgerufen. Genauer ergibt sich folgender Ablauf:
 * 
 * 1) invoke() kann dazu genutzt werden, ein Fenster anzuzeigen.
 * 
 * 2) Wird in diesem Fenster auf Next geklickt wird nextClicked() aufgerufen.
 * 
 * Ist waitForNextClick = true folgt:
 * 
 * 3) VennMaker wird angezeigt.
 * 
 * 4) Beim Klick von Next im VennMaker-Fenster wird shutdown() aufgerufen.
 * 
 * 
 * 
 */
public abstract class VennMakerWizard
{
	/**
	 * Diese Methode startet den Wizard.
	 */
	public abstract void invoke();

	/**
	 * Name dieses Wizards.
	 */
	private String	name;

	/**
	 * Diese Methode wird vom WizardController beim Klick auf den Next-Button im
	 * Wizard aufgerufen.
	 */
	public void nextClicked()
	{
	}

	/**
	 * Diese Methode wird vom WizardController vor Ende des Wizards aufgerufen
	 * und bietet sich für abschließende und aufräumende Arbeiten an.
	 */
	public void shutdown()
	{
	}

	/**
	 * Legt fest, ob der Wizard erst endet nachdem der Anwender im
	 * VennMaker-Fenster auf einen "Next"-Button geklickt hat oder direkt nach
	 * Abarbeiten der invoke()-Methode wieder stoppt (Standard).
	 */
	protected boolean	waitForNextClick	= false;

	/**
	 * Anzeigefenster, falls der Wizard eins braucht.
	 */
	@XStreamOmitField
	protected JDialog	dialog;

	/**
	 * Die links Fensterhälfte: Inhalt.
	 */
	@XStreamOmitField
	protected JPanel	leftPanel;

	/**
	 * Die rechte Fensterhälfte: Deko, Weiter-Knopf...
	 */
	@XStreamOmitField
	protected JPanel	rightPanel;

	/**
	 * Diese Methode präpariert ein Anzeigefenster mit zwei Bereichen und
	 * VennMaker-Logo, das ein Wizard als Grundlage für eigene Inhalte nehmen
	 * kann.
	 */
	protected void prepareDialog()
	{
		dialog = new JDialog((JFrame) null,
				Messages.getString("VennMaker.VennMaker"), true); //$NON-NLS-1$
		dialog.setIconImage(new ImageIcon(FileOperations
				.getAbsolutePath("icons/intern/icon.png")).getImage()); //$NON-NLS-1$
		dialog.setMinimumSize(new Dimension(600, 500));
		GridBagConstraints gbc;

		leftPanel = new JPanel();
		leftPanel.setMinimumSize(new Dimension(400, 500));
		GridBagLayout leftLayout = new GridBagLayout();
		leftPanel.setLayout(leftLayout);

		rightPanel = new JPanel();
		rightPanel.setSize(new Dimension(200, 500));
		rightPanel.setBackground(new Color(200, 200, 200));
		GridBagLayout rightLayout = new GridBagLayout();
		rightPanel.setLayout(rightLayout);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightPanel);
		splitPane.setResizeWeight(1);
		splitPane.setContinuousLayout(true);
		dialog.add(splitPane, BorderLayout.CENTER);

		/**
		 * VennMaker-Logo oben in rechter Fensterhälfte
		 */
		JLabel label;
		label = new JLabel();

		BufferedImage bufferedImg = ImageOperations.loadImage(new File(Messages
				.getString("VennMaker.Icon_Logo"))); //$NON-NLS-1$
		Image img = bufferedImg.getScaledInstance(140, -1, Image.SCALE_SMOOTH);
		ImageIcon imgIcon = new ImageIcon(img);
		label.setIcon(imgIcon);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(20, 10, 20, 10);
		gbc.anchor = GridBagConstraints.PAGE_START;
		rightLayout.setConstraints(label, gbc);
		rightPanel.add(label);

		/**
		 * Dummy-Label im Sinne von \vfill, warum auch immer
		 */
		label = new JLabel();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 2;
		gbc.fill = GridBagConstraints.BOTH;
		rightLayout.setConstraints(label, gbc);
		rightPanel.add(label);

		ResizableIcon icon;

		/**
		 * Previous-Button
		 */
		if (!WizardController.getInstance().isFirst(this))
		{
			icon = null;
			try
			{
				icon = SvgBatikResizableIcon.getSvgIcon(
						new FileInputStream(FileOperations.getAbsolutePath(Messages
								.getString("VennMaker.Icon_Previous"))), //$NON-NLS-1$
						new Dimension(24, 24));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			JCommandButton previousButton = new JCommandButton(
					Messages.getString("VennMaker.Previous"), icon); //$NON-NLS-1$
			previousButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent event)
				{
					if (dialog != null)
						dialog.dispose();
					WizardController.getInstance().previous();
				}
			});
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			gbc.gridy = 8;
			gbc.anchor = GridBagConstraints.PAGE_END;
			gbc.insets = new Insets(20, 10, 20, 10);
			((GridBagLayout) rightPanel.getLayout()).setConstraints(
					previousButton, gbc);
			rightPanel.add(previousButton);
		}

		/**
		 * Next-Button
		 */
		icon = null;
		try
		{
			icon = SvgBatikResizableIcon.getSvgIcon(
					new FileInputStream(FileOperations.getAbsolutePath(Messages
							.getString("VennMaker.Icon_Next"))), new Dimension( //$NON-NLS-1$
							48, 48));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		JCommandButton nextButton = new JCommandButton(
				Messages.getString("VennMaker.Next"), icon); //$NON-NLS-1$
		nextButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				nextClicked();

				/**
				 * Wenn nicht auf einen "Next"-Klick gewartet werden soll wird
				 * sofort in der Wizard-Ausführung fortgefahren.
				 */
				if (!waitForNextClick)
					WizardController.getInstance().perform();
			}
		});
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 9;
		gbc.anchor = GridBagConstraints.PAGE_END;
		gbc.insets = new Insets(20, 10, 20, 10);
		((GridBagLayout) rightPanel.getLayout()).setConstraints(nextButton, gbc);
		rightPanel.add(nextButton);
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *           the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

}
