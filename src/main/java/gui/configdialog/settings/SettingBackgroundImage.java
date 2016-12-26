/**
 * 
 */
package gui.configdialog.settings;

import files.FileOperations;
import files.ImageOperations;
import files.VMPaths;
import gui.Messages;
import gui.VennMaker;
import gui.VennMakerView;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.BackgroundInfo;
import data.BackgroundInfo.BackgroundImageOptions;
import data.Netzwerk;

/**
 * Setting which changes the background-related stuff.
 * 
 * 
 */
public class SettingBackgroundImage implements ConfigDialogSetting
{
	private VennMakerView 				view;
	
	private boolean 					useBg;
	
	private BackgroundImageOptions 		options;
	
	private Color 						bgColor;
	
	private Integer 					transperency;
	
	private String 						bgImageFilename;
	
	private Rectangle 					upscaledImgSelection;
	
	public SettingBackgroundImage(Netzwerk net,
			boolean useBg,
			BackgroundImageOptions options, 
			Color bgColor, 
			Integer transperency,
			String bgImageFilename,
			Rectangle upscaledImgSelection)
	{
		this.useBg = useBg;
		this.options = options;
		this.bgColor = bgColor;
		this.transperency = transperency;
		this.bgImageFilename = bgImageFilename;
		this.upscaledImgSelection = upscaledImgSelection;
		//View anhand netzwerk finden
		for(VennMakerView v : VennMaker.getInstance().getViews())
		{
			if(v.getNetzwerk().equals(net))
			{
				view = v;
				break;
			}
		}
	}
	
	@Override
	public void set()
	{
		try
		{
			if(view == null || view.getNetzwerk().getHintergrund()==null)
				return;
			
			BackgroundInfo bgConfig = view.getNetzwerk().getHintergrund();
			bgConfig.setImgoption(options);
			bgConfig.setBgcolor(bgColor);
			bgConfig.setTransparency(transperency);
			if(useBg)
			{
				String oldImgFileName = bgConfig.getFilenameOfOriginalImage();
				//Wenn das Bild gleich geblieben ist (nur neue Selection gemacht) wird
				//nur das Selection Image ueberschrieben
				String newBGFilename = bgImageFilename;
				if(oldImgFileName != null && !oldImgFileName.equals(bgImageFilename))
				{
					VennMaker.getInstance().getProject()
					.incrementBackgroundNumber();
					int backgroundNumber = VennMaker.getInstance().getProject().getBackgroundNumber();
					newBGFilename = VMPaths.getCurrentWorkingDirectory()
							+ "/" + VMPaths.VENNMAKER_BACKGROUNDIMAGES + "networkBackground"
							+ backgroundNumber;
					FileOperations.copyFile(new File(bgImageFilename),new File(newBGFilename), 128, true);
				}
				else
				{
					String image = new File(newBGFilename).getName();
					File imageDir = new File(VMPaths.getCurrentWorkingDirectory()+"/" + VMPaths.VENNMAKER_BACKGROUNDIMAGES + image);

					FileOperations.copyFile(new File(newBGFilename),imageDir,128,true);
					newBGFilename = imageDir.getAbsolutePath();
				}
				
				BufferedImage newImg = ImageIO.read(new File(newBGFilename));
				
				// wenn PNG dann Umwandeln, sonst kommt es zu
				// Problemen weil der Image Type (TYPE_CUSTOM)
				// beim Transformieren und Scalieren nicht funktioniert
				if (newImg.getType() == BufferedImage.TYPE_CUSTOM)
					newImg = ImageOperations.pngToARGB(newImg, bgColor);
				
				
				bgConfig.setFilename(newBGFilename);
				bgConfig.setUpscaledImgSelection(upscaledImgSelection);
				
				if (upscaledImgSelection != null)
					ImageOperations.saveNewImgSelection(upscaledImgSelection, newBGFilename, view);
			}
			else
			{
				view.setBackgroundImage(null);
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (final IOException exn)
		{
			System.out.println(Messages
					.getString("BackgroundConfig.ErrorReadingImgDotFile")); //$NON-NLS-1$
	}
		
		view.updateView();
	}

}
