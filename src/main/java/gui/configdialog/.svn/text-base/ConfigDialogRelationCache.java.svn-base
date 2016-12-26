package gui.configdialog;

import java.awt.Color;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Vector;

import data.AttributeType;
import data.Netzwerk;

public class ConfigDialogRelationCache<E>
{
	private class SubCache<E>
	{
		private Vector<String>	names;

		private Vector<E>			values;

		public SubCache()
		{
			this.names = new Vector<String>();
			this.values = new Vector<E>();
		}

		public Vector<String> getNames()
		{
			return this.names;
		}

		public Vector<E> getValues()
		{
			return this.values;
		}

		public void setValues(Vector<E> values)
		{
			this.values = values;
		}

		public void setNames(Vector<String> names)
		{
			this.names = names;
		}
	}

	private final Class<E>								clazz;

	private Netzwerk										net;

	private HashMap<AttributeType, SubCache<E>>	typeMap;

	public ConfigDialogRelationCache(Netzwerk net, Class<E> clazz)
	{
		this.typeMap = new HashMap<AttributeType, SubCache<E>>();
		this.net = net;
		this.clazz = clazz;
	}

	/**
	 * returns the names of all values connected with the given attributetype
	 * 
	 * @param at
	 *           the attributetype in question
	 * @return the names connected with this attributetype
	 */
	public Vector<String> getNames(AttributeType at)
	{

		if (!this.typeMap.containsKey(at))
		{
			fillSubCache(at);
		}
		return this.typeMap.get(at).getNames();
	}

	/**
	 * returns the values for the relations connected with a given attributetype
	 * (e.g. size/color/dashing)
	 * 
	 * @param at
	 *           the attributetype in question
	 * @return the sizes/colors/dashings connected with this attributetypes
	 */
	public Vector<E> getValues(AttributeType at)
	{
		if (!this.typeMap.containsKey(at))
		{
			fillSubCache(at);
		}
		return this.typeMap.get(at).getValues();
	}

	/**
	 * gets the names and values for an attributetype, when it's not yet there
	 * 
	 * @param at
	 *           the attributetype which has no values stored yet
	 */
	private void fillSubCache(AttributeType at)
	{
		SubCache<E> tempSubCache = new SubCache<E>();

		for (Object w : at.getPredefinedValues())
		{
			tempSubCache.getNames().add(w.toString());
			E value = (E) getVisualizerValue(at.getType(), w);

			if (value != null)
			{
				tempSubCache.getValues().add(value);
			}
			else
			{
				tempSubCache.getValues().add((E) this.getStandardValue(clazz));
			}

			this.typeMap.put(at, tempSubCache);
		}
	}

	/**
	 * updates the cache: adds and removes attributetypes;
	 * 
	 * @param at
	 *           attributetype, which needs to be updated
	 */
	public void update(AttributeType at)
	{
		Vector<String> newNames = new Vector<String>();
		Vector<E> newValues = new Vector<E>();

		for (Object w : at.getPredefinedValues())
		{
			newNames.add(w.toString());
			if (!getNames(at).contains(w.toString()))
			{
				newValues.add((E) this.getStandardValue(clazz));
			}
			else
			{
				newValues.add(typeMap.get(at).getValues()
						.get(this.getNames(at).indexOf(w.toString())));
			}
		}

		typeMap.get(at).setNames(newNames);
		typeMap.get(at).setValues(newValues);
	}

	/**
	 * when theres nothing stored, this function returns the standardvalue for
	 * the given class (Integer = size; Color = Color; Array = dashing)
	 * 
	 * @param clazz
	 *           the class, to return the correct standardvalue
	 * @return the standardvalue for this specific class
	 */
	private Object getStandardValue(Class<E> clazz)
	{
		/* Relation Color */
		if (clazz.equals(Color.class))
			return new Color(000);
		/* DashArray */
		if (clazz.equals(Array.class))
			return new float[] { 2.0f };
		/* Relationsize */
		if (clazz.equals(Integer.class))
			return 5;
		/* none of the above */
		return null;
	}

	/**
	 * returns the value stored in the visualizer for the given name of the
	 * specified attributetype
	 * 
	 * @param at
	 *           the attributetype
	 * @param w
	 *           and the value of the given attributetype
	 * @return the value for the visualization given bei the correct visualizer
	 */
	private Object getVisualizerValue(String at, Object w)
	{
		/* Relation Color */
		if (clazz.equals(Color.class))
		{
			return net.getActiveRelationColorVisualizer(at).getColor(w);
		}
		/* DashArray */
		if (clazz.equals(Array.class))
		{
			return net.getActiveRelationDashVisualizer(at).getDasharray(w);
		}
		/* Relationsize */
		if (clazz.equals(Integer.class))
		{
			return net.getActiveRelationSizeVisualizer(at).getSize(w);
		}
		/* none of the above */
		return null;
	}
}
