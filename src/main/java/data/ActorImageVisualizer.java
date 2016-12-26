/**
 * 
 */
package data;

import java.util.Map;

public class ActorImageVisualizer extends Visualizer
{
	private Map<Object, String>	images;

	public String getImage(AttributeSubject subject, Netzwerk network)
	{
		assert this.images != null;

		String i = this.images.get(subject.getAttributeValue(this
				.getAttributeType(), network));
		
		if (i == null)
			i = "";

		return i;
	}

	public String getImage(Object value)
	{
		assert this.images != null;
		return this.images.get(value);
	}

	public Map<Object, String> getImages()
	{
		return this.images;
	}

	public void setImages(Map<Object, String> images)
	{
		this.images = images;
	}

}
