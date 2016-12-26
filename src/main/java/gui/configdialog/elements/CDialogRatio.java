/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.RatioSaveElement;
import gui.configdialog.save.SaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingRatio;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog zum Einstellen des Verhaeltnisses von Breite zu Hoehe der
 * Netzwerkkarten.
 * 
 * 
 */
public class CDialogRatio extends ConfigDialogElement
{
	private static final long	serialVersionUID	= 1L;

	private JComboBox<String>	ratioField;

	@Override
	public void buildPanel()
	{
		dialogPanel = new JPanel();
		dialogPanel.add(createRatioBox());
	}

	private JComponent createRatioBox()
	{
		Box hbox = Box.createHorizontalBox();
		Box vbox = Box.createVerticalBox();

		vbox.add(new JLabel(Messages
				.getString("BackgroundConfig.Tab_4_ViewAreaRatio"))); //$NON-NLS-1$
		hbox.add(vbox);
		hbox.add(Box.createHorizontalStrut(30));

		if (ratioField == null)
		{
			float ratio = VennMaker.getInstance().getConfig().getViewAreaRatio();
			String[] values = { "0,71 (Din-Hoch)", "1,00 (Quadrat)", "1,33 (4:3)",
					"1,41 (Din-Quer)", "1,78 (16:9)", "1,85 (Widescreen)",
					"2,35 (Cinemascope)" };
			ratioField = new JComboBox<String>(values);
			ratioField.setEditable(true);
			ratioField.setSelectedItem(Float.toString(ratio));
		}
		vbox.add(ratioField);
		return vbox;
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		String s = ratioField.getSelectedItem().toString();
		if (s.indexOf("(") != -1) //$NON-NLS-1$
			s = s.substring(0, s.indexOf("(")); //$NON-NLS-1$
		s = s.trim().replace(",", "."); //$NON-NLS-1$ //$NON-NLS-2$
		float ratio = Float.parseFloat(s);

		return new SettingRatio(ratio);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof RatioSaveElement))
			return;

		String ratio = ((RatioSaveElement) setting).getRatio();
		ConfigDialogTempCache.getInstance().addActiveElement(this);

		if (dialogPanel == null)
			buildPanel();

		for (int i = 0; i < ratioField.getItemCount(); i++)
			if (ratio.equals(ratioField.getItemAt(i)))
				ratioField.setSelectedIndex(i);

		ConfigDialogTempCache.getInstance().addSetting(getFinalSetting());
	}

	@Override
	public SaveElement getSaveElement()
	{
		RatioSaveElement elem = new RatioSaveElement(
				(String) ratioField.getSelectedItem());
		elem.setElementName(this.getClass().getSimpleName());

		return elem;
	}
}
