/**
 * 
 */
package gui.configdialog.elements;

import gui.Messages;
import gui.VennMaker;
import gui.configdialog.ConfigDialogElement;
import gui.configdialog.ConfigDialogTempCache;
import gui.configdialog.save.SaveElement;
import gui.configdialog.save.ZoomSaveElement;
import gui.configdialog.settings.ConfigDialogSetting;
import gui.configdialog.settings.SettingZoom;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog zum Veraendern der Schriftgroesse.
 * 
 * 
 */
public class CDialogZoom extends ConfigDialogElement
{
	private static final long	serialVersionUID	= 1L;

	private JTextField			textZoomField;

	@Override
	public void buildPanel()
	{
		if (dialogPanel != null)
			return;

		dialogPanel = new JPanel();
		dialogPanel.add(buildZoomBox());
	}

	private Box buildZoomBox()
	{
		Box hbox = Box.createHorizontalBox();
		Box vbox = Box.createVerticalBox();

		vbox.add(new JLabel(Messages.getString("BackgroundConfig.Tab_4_TextZoom"))); //$NON-NLS-1$
		hbox.add(vbox);
		hbox.add(Box.createHorizontalStrut(30));
		float textZoom = VennMaker.getInstance().getProject().getTextZoom();
		textZoomField = new JTextField(Float.toString(textZoom));
		vbox.add(textZoomField);
		return vbox;
	}

	@Override
	public ConfigDialogSetting getFinalSetting()
	{
		float textZoom = Float.parseFloat(textZoomField.getText().replace(",",
				"."));
		return new SettingZoom(textZoom);
	}

	@Override
	public void setAttributesFromSetting(SaveElement setting)
	{
		if (!(setting instanceof ZoomSaveElement))
			return;

		ZoomSaveElement saveElement = (ZoomSaveElement) setting;

		if (textZoomField == null)
			buildPanel();

		textZoomField.setText("" + saveElement.getZoomFactor());

		ConfigDialogTempCache.getInstance().addActiveElement(this);
		ConfigDialogTempCache.getInstance().addSetting(
				new SettingZoom(saveElement.getZoomFactor()));
	}

	@Override
	public SaveElement getSaveElement()
	{
		float zoom = 0;

		try
		{
			zoom = Float.parseFloat(textZoomField.getText().replace(",", "."));
		} catch (NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}

		ZoomSaveElement zse = new ZoomSaveElement(zoom);
		zse.setElementName(this.getClass().getSimpleName());

		return zse;
	}
}
