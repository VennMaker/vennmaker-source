/**
 * 
 */
package gui.configdialog.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import data.AttributeType;
import data.FacadeVisualizer;
import data.Netzwerk;
import files.FileOperations;
import files.VMPaths;

/**
 * Setting to apply a new/edited selection of actor symbols.
 * 
 * 
 */
public class SettingActorImages implements ConfigDialogSetting
{
	private Netzwerk 				net;
	
	private AttributeType	 		attr;
	
	private Map<Object, String> 	images;
	
	private ArrayList<String> 		imagesToDelete;
	
	public SettingActorImages(Netzwerk net, AttributeType attr, Map<Object, String> images, ArrayList<String> imagesToDelete)
	{
		this.net = net;
		this.attr = attr;
		this.images = images;
		this.imagesToDelete = imagesToDelete;
	}
	@Override
	public void set()
	{ 
		File iconDir = new File(VMPaths.getCurrentWorkingDirectory() + "/" + VMPaths.VENNMAKER_SYMBOLS);
		
		for(String fileName : imagesToDelete)
		{
			File f = new File(iconDir.getAbsolutePath() + VMPaths.SEPARATOR+ fileName);
			f.delete();
		}
		FacadeVisualizer f = new FacadeVisualizer();
		f.newVisualizerAttributeType(
				attr, net,
				data.FacadeVisualizer.Visualization.SYMBOL, "ACTOR");
		f.updateVisualizerAttributeValues(
				attr, net);
		net.getActorImageVisualizer().setImages(images);
		
		FileOperations.deleteFolder(new File(VMPaths.VENNMAKER_TEMPDIR+"tempIcons")); //$NON-NLS-1$
	}
}
