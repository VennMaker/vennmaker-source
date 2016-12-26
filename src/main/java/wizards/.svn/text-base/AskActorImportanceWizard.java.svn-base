/**
 * 
 */
package wizards;

import gui.VennMaker;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import data.Akteur;
import data.Question;

/**
 * Wizard zur Einteilung der Akteure in festgelegte Wichtigkeitsstufen durch
 * Drag&Drop.
 * 
 * 
 */
public class AskActorImportanceWizard extends VennMakerWizard
{
	/**
	 * Über der Abfrage anzuzeigender Text (Frage).
	 */
	private String								text;

	/**
	 * Die Beschriftungen der Wichtigkeits-Kategorien.
	 */
	private String[]							importanceNames;

	/**
	 * Die Größen der Akteure für jede Wichtigkeits-Kategorie.
	 */
	private Integer[]							importanceSizes;

	ListTransferHandler						lh;

	private JList[]							listen;

	JList											list;

	/**
	 * Speichert eine Abbildung von Namen auf Akteure. Durch diesen Umweg sind
	 * die Seiteneffekte der (De-)Serialisierung (wg. Drag'n'Drop) nicht mehr
	 * spürbar, da Strings ohne Probleme serialisierbar sind.
	 */
	final private Map<String, Akteur>	actorRepresentatives;

	public AskActorImportanceWizard()
	{
		actorRepresentatives = new HashMap<String, Akteur>();
	}

	@Override
	public void invoke()
	{
		/*
		 * Gibt es eine Frage nach Akteursgrößen, die durch Buckets beantwortet
		 * werden soll?
		 */
		boolean found = false;
		for (Question question : VennMaker.getInstance().getConfig()
				.getQuestions())
		{
			if (question.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_SIZE
					&& question.isMatrix())
			{
				this.importanceNames = question.getPredefinedAnswers();
				this.importanceSizes = new Integer[this.importanceNames.length];
				int i = 0;
				for (Object o : question.getVisualMapping())
					this.importanceSizes[i++] = (Integer) o;
				found = true;
				this.text = question.getQuestion();
			}
		}
		if (!found)
		{
			WizardController.getInstance().proceed();
			return;
		}

		/**
		 * Wir brauchen ein Anzeigefenster.
		 */
		prepareDialog();
		GridBagConstraints gbc;

		lh = new ListTransferHandler();
		actorRepresentatives.clear();

		JScrollPane scrollPane;
		int zeile = 0;

		int num = importanceNames.length;

		JTextArea textArea = new JTextArea(text);
		scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setCursor(null);
		textArea.setOpaque(false);
		textArea.setFocusable(false);
		textArea.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		scrollPane.setBorder(null);
		scrollPane.setMinimumSize(new Dimension(400, 40));
		textArea.setMinimumSize(new Dimension(400, 40));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = num;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(10, 10, 0, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		zeile++;

		DefaultListModel listModel;
		listModel = new DefaultListModel();
		list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setDragEnabled(true);
		list.setTransferHandler(lh);
		list.setDropMode(DropMode.INSERT);
		scrollPane = new JScrollPane(list);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = zeile;
		gbc.gridwidth = num;
		gbc.gridheight = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		((GridBagLayout) leftPanel.getLayout()).setConstraints(scrollPane, gbc);
		leftPanel.add(scrollPane);

		for (Akteur akteur : VennMaker.getInstance().getProject().getAkteure())
			if (akteur != VennMaker.getInstance().getProject().getEgo())
			{
				String akteurName = akteur.getName();

				// String eindeutig machen!
				while (actorRepresentatives.containsKey(akteurName))
				{
					akteurName = akteurName.concat("*"); //$NON-NLS-1$
				}

				actorRepresentatives.put(akteurName, akteur);
				listModel.addElement(akteurName);

			}

		zeile++;

		listen = new JList[num];
		DefaultListModel[] models = new DefaultListModel[num];
		for (int i = 0; i < num; i++)
		{
			JLabel label = new JLabel(importanceNames[i]);
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = i;
			gbc.gridy = zeile;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.insets = new Insets(10, 10, 0, 10);
			((GridBagLayout) leftPanel.getLayout()).setConstraints(label, gbc);
			leftPanel.add(label);

			models[i] = new DefaultListModel();
			listen[i] = new JList(models[i]);
			listen[i].setDragEnabled(true);
			listen[i].setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			listen[i].setTransferHandler(lh);
			listen[i].setDropMode(DropMode.INSERT);
			scrollPane = new JScrollPane(listen[i]);
			scrollPane.setPreferredSize(new Dimension(75, 100));
			gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = i;
			gbc.gridy = zeile + 1;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.insets = new Insets(10, 10, 10, 10);
			((GridBagLayout) leftPanel.getLayout())
					.setConstraints(scrollPane, gbc);
			leftPanel.add(scrollPane);
		}

		dialog.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - dialog.getHeight()) / 2;
		int left = (screenSize.width - dialog.getWidth()) / 2;
		dialog.setLocation(left, top);

		dialog.setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wizards.VennMakerWizard#nextClicked()
	 */
	@Override
	public void nextClicked()
	{
		for (int i = 0; i < importanceNames.length; i++)
		{
			for (int j = 0; j < listen[i].getModel().getSize(); j++)
			{
				String akteurName = (String) listen[i].getModel().getElementAt(j);
				Akteur akteur = actorRepresentatives.get(akteurName);
				// TODO
				// akteur.setDefaultGroesse(importanceSizes[i]);
			}
		}

		// sortiere Akteure absteigend nach Größe
//		Comparator<Akteur> comparator = new Akteur.DefaultSizeComparator();
		// comparator = Collections.reverseOrder(comparator);
		// Vector<Akteur> akteure = VennMaker.getInstance().getProject()
		// .getAkteure();
		// Collections.sort(akteure, comparator);

		dialog.dispose();
	}

	class ListTransferHandler extends TransferHandler
	{

		/**
		 * 
		 */
		private static final long	serialVersionUID	= 1L;

		/**
		 * Perform the actual data import.
		 */
		public boolean importData(TransferHandler.TransferSupport info)
		{
			String data = null;

			JList list = (JList) info.getComponent();
			DefaultListModel model = (DefaultListModel) list.getModel();
			try
			{
				data = (String) info.getTransferable().getTransferData(
						DataFlavor.stringFlavor);
				// System.out.println("Import: Akteur: "+data.getName()+", "+data);
			} catch (UnsupportedFlavorException ufe)
			{
				ufe.printStackTrace();
				return false;
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
				return false;
			} catch (Exception e)
			{
				e.printStackTrace();
			}

			if (info.isDrop())
			{
				// This is a drop
				JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
				int index = dl.getIndex();
				if (dl.isInsert())
				{
					model.add(index, data);
					return true;
				}
				else
				{
					model.set(index, data);
					return true;
				}
			}
			else
			{
				// This is a paste
				int index = list.getSelectedIndex();
				if (index >= 0)
				{
					model.add(list.getSelectedIndex() + 1, data);
				}
				else
				{
					model.addElement(data);
				}
				return true;
			}
		}

		/**
		 * Bundle up the data for export.
		 */
		protected Transferable createTransferable(JComponent c)
		{
			JList list = (JList) c;
			int index = list.getSelectedIndex();
			String value = (String) list.getSelectedValue();
			// System.out.println("Export: Akteur: "+value.getName()+", "+value);
			return new GenericTransferable(value);
		}

		/**
		 * The list handles both copy and move actions.
		 */
		public int getSourceActions(JComponent c)
		{
			return MOVE;
		}

		/**
		 * When the export is complete, remove the old list entry if the action
		 * was a move.
		 */
		protected void exportDone(JComponent c, Transferable data, int action)
		{
			if (action != MOVE)
			{
				return;
			}
			JList list = (JList) c;
			DefaultListModel model = (DefaultListModel) list.getModel();
			int index = list.getSelectedIndex();
			model.remove(index);
		}

		/**
		 * We only support importing strings.
		 */
		public boolean canImport(TransferHandler.TransferSupport support)
		{
			if (!support.isDataFlavorSupported(DataFlavor.stringFlavor))
			{
				return false;
			}
			return true;
		}
	}

	class GenericTransferable implements Transferable
	{
		private Object					data;

		private final DataFlavor[]	flavors	= new DataFlavor[1];

		public GenericTransferable(final Object data)
		{
			super();
			this.data = data;
			flavors[0] = DataFlavor.stringFlavor;
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			return flavors;
		}

		public boolean isDataFlavorSupported(final DataFlavor flavor)
		{
			return flavor.equals(DataFlavor.stringFlavor);
		}

		public Object getTransferData(final DataFlavor flavor)
				throws UnsupportedFlavorException, IOException
		{
			return data;
		}
	}
}
