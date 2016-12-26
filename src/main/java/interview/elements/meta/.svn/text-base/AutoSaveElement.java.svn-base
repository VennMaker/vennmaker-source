package interview.elements.meta;

import files.FileOperations;
import gui.Messages;
import gui.SaveFileDialog;
import gui.VennMaker;
import interview.InterviewController;
import interview.InterviewElementInformation;
import interview.InterviewLayer;
import interview.categories.IECategory_MetaElement;
import interview.configuration.NameInfoSelection.NameInfoPanel;
import interview.elements.StandardElement;
import interview.elements.information.AutoSaveElementInformation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.Semaphore;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import data.OpenFileInBackground;

/**
 * meta-element to save the interview while performing it
 * 
 * 
 */
public class AutoSaveElement extends StandardElement implements
		IECategory_MetaElement
{
	private static final long	serialVersionUID	= 1L;

	private JTextField			tfInterviewName;

	public AutoSaveElement()
	{
		super(new NameInfoPanel(true), null, null, true);
		nSelector.setName("AutoSaveElement");
		nSelector.setInfo(Messages.getString("AutoSaveElement.Info"));
	}

	@Override
	public boolean writeData()
	{

		// perform only when reached by "Next"
		if (InterviewController.getInstance().wasNextPressed())
		{
			String rootDir = System.getProperty("user.home"); //$NON-NLS-1$

			if (new File(rootDir).equals(new File("./projects/temp"))) //$NON-NLS-1$
				rootDir = "./projects/"; //$NON-NLS-1$

			SaveFileDialog chooser = new SaveFileDialog(rootDir);
			chooser.setFilter(".vmp"); //$NON-NLS-1$

			do
			{
				chooser.showSaveFileDialog();
			} while (chooser.getVmpFile() == null);

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_S");
			Date dt = new Date(System.currentTimeMillis());
			String interviewName = tfInterviewName.getText() + "-" + df.format(dt);

			chooser.setFilter(Messages.getString("VennMaker.Suffix")); //$NON-NLS-1$
			chooser.setInterviewName(interviewName);
			chooser.performSaving();

			String filename = chooser.getFilename();
			if (filename != null && !chooser.wasCanceled())
			{
				filename = filename.replace("\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$

				if (!filename.endsWith(Messages.getString("VennMaker.Suffix")))//$NON-NLS-1$
					filename += Messages.getString("VennMaker.Suffix"); //$NON-NLS-1$

				FileOperations.changeRootFolder(chooser.getRootFolder());

				// von WasVmp hängt ab ob die Bilder, Audio-Files usw kopiert
				// werden...
				// workaround ... bleibt erstmal false, damit immer kopiert wird.
				boolean wasVmp = false;

				if (!VennMaker.getInstance().getProject()
						.save(filename, new File(chooser.getVmpPath()), wasVmp))
				{
					JOptionPane
							.showMessageDialog(
									VennMaker.getInstance(),
									Messages.getString("VennMaker.ErrorWriting") //$NON-NLS-1$
											+ filename,
									Messages.getString("VennMaker.Error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$

					return false;
				}
				else
				{
					VennMaker.getInstance().setCurrentWorkingDirectory(
							chooser.getRootFolder());
					// VennMaker.getInstance().setLastFileName(filename);

					VennMaker
							.getInstance()
							.getConfig()
							.save(filename.substring(0, filename.indexOf(".venn")) + ".vennEn"); //$NON-NLS-1$

					VennMaker.getInstance().setChangesSaved(true);

					File destination = new File(chooser.getDestinationFolder());
					File source = new File(chooser.getRootFolder());

					// FileOperations.zip(source, destination);
					Semaphore sem = new Semaphore(1);
					OpenFileInBackground ofib = new OpenFileInBackground(source, destination, sem,
							InterviewController.getInstance().getDialog(),
							OpenFileInBackground.ZIP);
					ofib.startAction();

				}
			}
		}

		return true;
	}

	@Override
	public void setData()
	{
	}

	@Override
	public JPanel getConfigurationDialog()
	{
		if (configurationPanel != null)
			return configurationPanel;
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		GridBagLayout gbl = new GridBagLayout();

		configurationPanel = new JPanel(gbl);

		JLabel lblInterviewName = new JLabel(
				Messages.getString("AutoSaveElement.InterviewConfigName")); //$NON-NLS-1$
		if (tfInterviewName == null)
		{
			tfInterviewName = new JTextField(
					Messages.getString("VennMaker.Interview")); //$NON-NLS-1$
		}
		JPanel panel = new JPanel(gbLayout);

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		gbLayout.setConstraints(lblInterviewName, c);
		panel.add(lblInterviewName);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 3;
		c.insets = new Insets(0, 0, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		gbLayout.setConstraints(tfInterviewName, c);
		panel.add(tfInterviewName);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(panel, c);
		configurationPanel.add(panel);

		return configurationPanel;
	}

	@Override
	public BufferedImage getPreview()
	{
		return null;
	}

	@Override
	public boolean addToTree()
	{
		setElementNameInTree("AutoSaveElement"); //$NON-NLS-1$
		return true;
	}

	@Override
	public String getInstructionText()
	{
		return Messages.getString("AutoSaveElement.Description"); //$NON-NLS-1$
	}

	@Override
	public InterviewElementInformation getElementInfo()
	{
		AutoSaveElementInformation info = new AutoSaveElementInformation(
				tfInterviewName.getText());
		info.setId(this.getId());
		info.createChildInformation(children);
		info.setElementName(this.getElementNameInTree());

		if (parent != null)
		{
			info.setParentInformation(parent);
		}
		info.setElementClass(this.getClass());

		return info;
	}

	@Override
	public void setElementInfo(InterviewElementInformation information)
	{
		if (information instanceof AutoSaveElementInformation)
		{
			AutoSaveElementInformation info = (AutoSaveElementInformation) information;
			if (tfInterviewName == null)
				tfInterviewName = new JTextField();
			tfInterviewName.setText(info.getInterviewName());

			this.setId(info.getId());
			this.setElementNameInTree(info.getElementName());
			InterviewLayer.getInstance().createChild(information, this);
		}
	}
}
