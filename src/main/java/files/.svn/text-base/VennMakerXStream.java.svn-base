package files;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import data.Projekt;
import files.oldClasses.SectorCirclePreview;

/**
 * Static initialisation of xstream.</br></br>
 * 1. use init()</br>
 * 2. use appendBackwardsCompatibilityAliases()</br>
 * 
 * 
 */
public class VennMakerXStream extends XStream
{		
		/**
		 * Used to make xstream backwards compatible to older VennMaker .venn Files 
		 * (further information: Ticket #1023)
		 * 
		 * @param xs - xstream object to append all the aliases to
		 */
		public static void appendBackwardsCompatibilityAliases(XStream xs)
		{
			xs.alias("gui.configdialog.SectorCirclePreview", SectorCirclePreview.class);
			xs.alias("gui.configdialog.SectorCirclePreview$FakeSectorInfo",SectorCirclePreview.FakeSectorInfo.class);
		}
		
		/**
		 * Used to init an xstream object to use it with VennMaker files.
		 * 
		 * @param xs - xstream object to init for VennMaker use
		 */
		public static void init(XStream xs)
		{
			xs.autodetectAnnotations(true);	
			xs.alias("projekt", data.Projekt.class); //$NON-NLS-1$
			xs.addImplicitCollection(Projekt.class, "netzwerke"); //$NON-NLS-1$
			xs.alias("netzwerk", data.Netzwerk.class); //$NON-NLS-1$
			xs.alias("akteur", data.Akteur.class); //$NON-NLS-1$
			xs.alias("akteurTyp", data.AkteurTyp.class); //$NON-NLS-1$
			xs.alias("relation", data.Relation.class); //$NON-NLS-1$
			xs.alias("relationTyp", data.RelationTyp.class); //$NON-NLS-1$
			xs.useAttributeFor(data.Akteur.class, "id"); //$NON-NLS-1$
			xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES); 
		}
		
		
		protected MapperWrapper wrapMapper(MapperWrapper next)
		{
			return new MapperWrapper(next)
			{
				public boolean shouldSerializeMember(Class def, String fieldName)
				{
					if(def != Object.class || fieldName.equals("netzwerk"))
						return super.shouldSerializeMember(def, fieldName);
					else
						return false;
				}
			};
		}
}

