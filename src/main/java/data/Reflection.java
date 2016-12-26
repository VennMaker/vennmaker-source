package data;

import interview.categories.IECategory;
import interview.elements.InterviewElement;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Reflection
{
	/**
	 * creates a new instance of some class
	 */
	public static Object createInstance( Class c )
	{
		try
		{
			Object ni = c.newInstance();
			return ni;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public static Class<? extends IECategory> getCategory(Class<? extends InterviewElement> elem)
	{
		try
		{
			Class[] ifaces = elem.getInterfaces();
			for(Class c : ifaces)
			{
				
				Class[] is = c.getInterfaces();
				if(is != null && is.length==1 && is[0].getSimpleName().equals("IECategory"))
					return c;
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static <T> T getClassAttribute( Class c, String s )
	{
		T t = null;
		try {
			t = (T)c.getField(s).get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
}
