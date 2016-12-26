package gui;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class BufferedImageTranscoder extends ImageTranscoder
{
	private BufferedImage	image;

	public BufferedImage createImage(int width, int height)
	{
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	public void writeImage(BufferedImage image, TranscoderOutput output)
			throws TranscoderException
	{
		this.image = image;
	}

	public BufferedImage getImage()
	{
		return image;
	}
}
