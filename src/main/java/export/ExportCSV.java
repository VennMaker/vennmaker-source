/**
 * 
 */
package export;

import gui.ErrorCenter;
import gui.Messages;
import gui.VennMaker;
import interview.InterviewLayer;
import interview.elements.InterviewElement;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import data.Akteur;
import data.AttributeSubject;
import data.AttributeType;
import data.Compute;
import data.Netzwerk;
import data.Projekt;
import data.Question;
import data.QuestionController;
import data.Relation;
import data.RelationTyp;
import data.RelationTyp.DirectionType;

/**
 * 
 * 
 *         Exports the network data into csv files
 * 
 */
public class ExportCSV implements ExportData
{

	private Projekt				project;

	private AttributeSubject	subject;

	public ExportCSV()
	{

		project = VennMaker.getInstance().getProject();
	}

	/**
	 * Interview notes and attributes, attribute values and codes (a code is
	 * numerical representation of the attribute value)
	 * 
	 * @return Interview notes, attribute labels, attribute values and codes
	 */
	public String getAttributeValuesCodes()
	{

		String result = "Interview notes and list of attributes, values and codes";

		result += "\n=============================================\n";

		result += "\nNotes: ";
		if (VennMaker.getInstance().getProject().getMetaInformation() != null)
			result += VennMaker.getInstance().getProject().getMetaInformation();

		result += "\n---------------------------------------------";
		result += "\nList of attributes, values and codes:";
		result += "\n---------------------------------------------";
		for (AttributeType attributeType : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{

			result += "\n";
			if (attributeType.getType().equals("ACTOR"))
				result += "Actor\n";
			else
				result += "Relation (" + attributeType.getType() + ")\n";

			if (!attributeType.getType().equals("ACTOR"))
			{
				if (VennMaker.getInstance().getProject()
						.getIsDirected(attributeType.getType()))
					result += "directed";
				else
					result += "undirected";
			}
			result += "\nLabel: " + attributeType.getLabel();

			result += "\nQuestion: ";
			if (attributeType.getQuestion() != null)
				result += attributeType.getQuestion();

			result += "\nDescribtion: ";
			if (attributeType.getDescription() != null)
				result += attributeType.getDescription();

			if (attributeType.getPredefinedValues() != null)
			{
				result += "\nCode and Value: ";
				for (Object value : attributeType.getPredefinedValuesCodes())
				{
					int wert = (Integer) value;
					result += "\n  " + wert + ": "
							+ attributeType.getPredefinedValue(wert - 1);
				}

				result += "\n   :" + "missing";
			}
			else
				result += "\nFree answer";

			result += "\n---------------";
		}
		return result;
	}

	/**
	 * Export network data into different CSV Files Ego-file (non-relational
	 * data) Alter-file (non-relational data) Adjacency-Matrix-file
	 * Relation-Matrix-file
	 * 
	 * @param path
	 * @param name
	 */
	public void toCsv(String path, String n, String separator,
			String decimalseparator)
	{
		String fileName;
		int i;
		FileOutputStream fos;
		PrintWriter fw;

		String name;
		try
		{
			name = java.net.URLEncoder.encode(n, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			ErrorCenter.manageException(null,
					"Can't export data: wrong name", ErrorCenter.ERROR, false, true); //$NON-NLS-1$
			e.printStackTrace();

			return;
		}
		name = name.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
		fileName = path + "EGO_" + name + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

		try
		{
			fos = new FileOutputStream(fileName);

			fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
					true);

			fw.write(toCsv_Ego(separator, "\"", decimalseparator)); //$NON-NLS-1$

			fw.close();

			// Overview
			fileName = path + "OVERVIEW_" + name + ".txt";

			fos = new FileOutputStream(fileName);

			fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
					true);

			fw.write(getAttributeValuesCodes()); //$NON-NLS-1$

			fw.close();

			// Alteri

			fileName = path + "ALTER_" + name + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

			fos = new FileOutputStream(fileName);

			fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), //$NON-NLS-1$
					true);
			fw.write(toCsv_Alter(separator, "\"", decimalseparator)); //$NON-NLS-1$
			fw.close();

			// ----Relations: only actor drawn on the network map-------------
			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "RELATIONGROUPS_NWM_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				try
				{
					fos = new FileOutputStream(fileName);
				} catch (FileNotFoundException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				}
				try
				{
					fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
				} catch (UnsupportedEncodingException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				} //$NON-NLS-1$
				fw.write(toCsv_Relationgroups(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}


			//For each relation attribute value one adjacency matrix:
			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;
				// Relationsgruppen-Liste erstellen
				for (AttributeType at : VennMaker.getInstance().getProject()
						.getAttributeTypes())
				{

					String att_name = at.getLabel().replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
					
					if (!at.getType().equals("ACTOR"))
					{

						Object[] values = at.getPredefinedValues();
						if (values != null)
							for (Object value : values)
							{

								value = ((String) value).replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");
								fileName = path
										+ "ADJACENCY_NWM_ATTRIBUTE_" + name + "_" + i+ "_" + att_name + "_" + value + "_" + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

								fos = new FileOutputStream(fileName);
								fw = new PrintWriter(new OutputStreamWriter(fos,
										"UTF-8"), true); //$NON-NLS-1$
								fw.write(toCsv_AdjacencyAttribute(network, separator,
										"\"", decimalseparator, at, value)); //$NON-NLS-1$
								fw.close();
							}
					}
				}
			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "ADJACENCY_NWM_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
				fw.write(toCsv_Adjacency(network, separator, "\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "ADJACENCY_ALTERI_NWM_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
				fw.write(toCsv_AdjacencyAlteri(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;
				for (AttributeType attributetype : VennMaker.getInstance()
						.getProject().getAttributeTypes())
				{
					if (!attributetype.getType().equals("ACTOR"))
					{

						String label = java.net.URLEncoder.encode(
								attributetype.getLabel(), "UTF-8");
						
						label = label.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");

						fileName = path
								+ "ADJACENCY_NWM_" + name + "_" + i + "_" + label + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

						fos = new FileOutputStream(fileName);
						fw = new PrintWriter(
								new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
						fw.write(toCsv_File_RelationType(network, separator,
								"\"", decimalseparator, attributetype)); //$NON-NLS-1$
						fw.close();

					}
				}
			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "MULTIPLEXITY_NWM_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
				fw.write(toCsv_Mulitplexity(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();
			}

			// ----Relations: all actors from one network map (visualized and not
			// visualized actors)-------------
			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "RELATIONGROUPS_ALL_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				try
				{
					fos = new FileOutputStream(fileName);
				} catch (FileNotFoundException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				}
				try
				{
					fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true);
				} catch (UnsupportedEncodingException exn)
				{
					// TODO Auto-generated catch block
					exn.printStackTrace();
				} //$NON-NLS-1$
				fw.write(this.toCsv_Relationgroups_allActors(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "ADJACENCY_ALL_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
				fw.write(this.toCsv_Adjacency_allActors(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "ADJACENCY_ALTERI_ALL_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
				fw.write(this.toCsv_AdjacencyAlteri_allAlteri(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;
				for (AttributeType attributetype : VennMaker.getInstance()
						.getProject().getAttributeTypes())
				{
					if (!attributetype.getType().equals("ACTOR"))
					{

						String label = java.net.URLEncoder.encode(
								attributetype.getLabel(), "UTF-8");
						
						label = label.replaceAll("[^a-zA-Z0-9_\\-\\.]", "_");

						fileName = path
								+ "ADJACENCY_ALL_" + name + "_" + i + "_" + label + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

						fos = new FileOutputStream(fileName);
						fw = new PrintWriter(
								new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
						fw.write(this.toCsv_File_RelationType_allActors(network,
								separator, "\"", decimalseparator, attributetype)); //$NON-NLS-1$
						fw.close();

					}
				}
			}

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "MULTIPLEXITY_ALL_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
				fw.write(this.toCsv_Mulitplexity_allActors(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();
			}

			// -------------------------------------

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "COMPUTE_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$

				fw.write(toCsv_Statistics(network, separator,
						"\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			// -------------------------------------

			i = 0;
			for (final Netzwerk network : project.getNetzwerke())
			{
				i++;

				fileName = path + "INTERVIEWTIME_" + name + "_" + i + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$

				fos = new FileOutputStream(fileName);
				fw = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$

				fw.write(toCsv_InterviewTimes(InterviewLayer.getInstance(),
						separator, "\"", decimalseparator)); //$NON-NLS-1$
				fw.close();

			}

			/*
			 * i = 0; for (final Netzwerk network : project.getNetzwerke()) { i++;
			 * for (final RelationTyp relationtyp : project.getRelationTypen()) {
			 * 
			 * fileName = path + "RELATION_CHANGE_SQ_FRQ_" + name + "_" + i + "_" +
			 * relationtyp.getBezeichnung() + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$
			 * 
			 * fos = new FileOutputStream(fileName); fw = new PrintWriter(new
			 * OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
			 * fw.write(relationChangeSequenceFrequency(separator, "\"",
			 * decimalseparator)); //$NON-NLS-1$ fw.close();
			 * 
			 * } }
			 * 
			 * i = 0; for (final Netzwerk network : project.getNetzwerke()) { i++;
			 * for (final RelationTyp relationtyp : project.getRelationTypen()) {
			 * 
			 * fileName = path + "RELATION_CHANGE_FRQ_" + name + "_" + i + "_" +
			 * relationtyp.getBezeichnung() + ".csv"; //$NON-NLS-1$ //$NON-NLS-2$
			 * 
			 * fos = new FileOutputStream(fileName); fw = new PrintWriter(new
			 * OutputStreamWriter(fos, "UTF-8"), true); //$NON-NLS-1$
			 * fw.write(relationChangeFrequency(separator, "\"",
			 * decimalseparator)); //$NON-NLS-1$ fw.close();
			 * 
			 * } }
			 */
		} catch (FileNotFoundException exn1)
		{
			// TODO Auto-generated catch block
			exn1.printStackTrace();
		} catch (UnsupportedEncodingException exn1)
		{
			// TODO Auto-generated catch block
			exn1.printStackTrace();
		}

	}

	/**
	 * Ego (non-relational information about Ego)
	 * 
	 * Erzeugt einen String, der in eine CSV Datei geschrieben werden kann
	 * 
	 */
	public String toCsv_Ego(String separator, String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add("id_Ego");

		boolean groesseGegeben = false;

		for (final Netzwerk networks : project.getNetzwerke())
		{
			row.add("x_" + networks.getBezeichnung()); //$NON-NLS-1$

			row.add("y_" + networks.getBezeichnung()); //$NON-NLS-1$

		}

		for (final Question frage : QuestionController.getInstance()
				.getQuestions())
		{
			if (frage.getSubject() == Question.Subject.EGO)
			{
				row.add(frage.getQuestion());

				if (frage.getVisualMappingMethod() == Question.VisualMappingMethod.ACTOR_SIZE)
					groesseGegeben = true;
			}

		}

		// Attribute Types

		for (final Netzwerk networks : project.getNetzwerke())
			for (final AttributeType attributeType : VennMaker.getInstance()
					.getProject().getAttributeTypes())
				if (attributeType.getType().equals("ACTOR"))
					row.add(attributeType + "_" + networks.getBezeichnung());

		for (final Netzwerk network : project.getNetzwerke())
		{
			row.add("Indegree_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("Outdegree_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("InCloseness_" + network.getBezeichnung()); //$NON-NLS-1$

			row.add("OutCloseness_" + network.getBezeichnung()); //$NON-NLS-1$

			if (!groesseGegeben)
				row.add("Size_" + network.getBezeichnung()); //$NON-NLS-1$

			row.add("InDegreeStd_" + network.getBezeichnung()); //$NON-NLS-1$

			row.add("OutDegreeStd_" + network.getBezeichnung()); //$NON-NLS-1$

			row.add("ProximityPrestige_" + network.getBezeichnung()); //$NON-NLS-1$

			row.add("OutClosenessStd_" + network.getBezeichnung());//$NON-NLS-1$

		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// Ego-Name
		String tmp = "";
		switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
		{
			case NUMBER:
				tmp = ""+project.getEgo().getId();
				break;
			default:
				tmp = project.getEgo().getName(); //$NON-NLS-1$
				break;
		}

		row.add(tmp);

		for (final Netzwerk networks : project.getNetzwerke())
		{
			if (project.getEgo().getLocation(networks) != null)
			{
				row.add(project.getEgo().getLocation(networks).getX());
				row.add(project.getEgo().getLocation(networks).getY());
			}
			else
			{
				row.add("");
				row.add("");
			}
		}

		for (final Question frage : QuestionController.getInstance()
				.getQuestions())
		{
			if (frage.getSubject() == Question.Subject.EGO)
			{

				// TODO
				// if (frage.getVisualMappingMethod() ==
				// Question.VisualMappingMethod.ACTOR_SIZE)
				// {
				// int i = 0;
				// while ((i < frage.getVisualMapping().length)
				// && (this.getEgo().getDefaultGroesse() != (Integer) frage
				// .getVisualMapping()[i]))
				// {
				// i++;
				// }
				// csv += frage.getPredefinedAnswers()[i];
				// }
				// else
				if (project.getEgo().getAnswer(frage.getQuestion()) != null)
					row.add(project.getEgo().getAnswer(frage.getQuestion()));
				else
					row.add("0"); //$NON-NLS-1$

			}
		}

		// Attribute Values

		int code = 0;

		for (final Netzwerk n : project.getNetzwerke())
			for (final AttributeType attributeType : VennMaker.getInstance()
					.getProject().getAttributeTypes())
				if (attributeType.getType().equals("ACTOR"))
				{

					if (((AttributeSubject) project.getEgo()).getAttributes(n).get(
							attributeType) != null)
					{

						if (attributeType.getPredefinedValues() != null)
						{
							code = attributeType.getPredefinedValueCode(project
									.getEgo().getAttributes(n).get(attributeType)
									.toString());
							if (code > 0)
								row.add(code);
							else
								row.add(((AttributeSubject) project.getEgo())
										.getAttributes(n).get(attributeType));
						}
						else
							// Free answer
							row.add(((AttributeSubject) project.getEgo())
									.getAttributes(n).get(attributeType));
					}
					else
						row.add("");
				}

		for (final Netzwerk networks : project.getNetzwerke())
		{
			Compute m = new Compute(networks, networks.getAkteure());
			if (networks.getAkteure().contains(project.getEgo()))
			{
				row.add(m.inDegree(project.getEgo()));
				row.add(m.outDegree(project.getEgo()));
				row.add(m.inCloseness(project.getEgo()));
				row.add(m.outCloseness(project.getEgo()));

				// TODO
				// if (!groesseGegeben)
				// csv += csvNumberStart + this.getEgo().getGroesse(networks)
				// + csvNumberEnd + csvSeparator;
				// Vorläufig (wird später entfernt, da auch kommentar ein attribut
				row.add(""); //$NON-NLS-1$
				// -----

				row.add(m.inDegreeStd(project.getEgo()));

				row.add(m.outDegreeStd(project.getEgo()));

				row.add(m.proximityPrestige(project.getEgo()));
				row.add(m.outClosenessStd(project.getEgo()));

			}
			else
			{
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
			}

		}

		// Comment
		// row.add("");

		// if (this.getEgo().getKommentar() != null)
		// csv += this.getEgo().getKommentar().toString();

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		return csv;
	}

	/**
	 * Alteri (non-relational informations about Alter)
	 * 
	 * Erzeugt einen String, der in eine CSV Datei geschrieben werden kann
	 * 
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 * 
	 */
	public String toCsv_Alter(String separator, String textelement, String format)
	{

		Vector<Akteur> akteure = project.getAkteure();

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add("id_Alter"); //$NON-NLS-1$
		row.add("id_Ego"); //$NON-NLS-1$

		for (final Netzwerk network : project.getNetzwerke())
		{
			row.add("x_" + network.getBezeichnung()); //$NON-NLS-1$
			row.add("y_" + network.getBezeichnung()); //$NON-NLS-1$
		}

		// Attribute Types

		for (final Netzwerk network : project.getNetzwerke())
		{
			for (final AttributeType attributeType : project.getAttributeTypes())
			{

				if (attributeType.getType().equals("ACTOR"))
				{
					row.add(attributeType.toString()
							+ "_" + network.getBezeichnung());//$NON-NLS-1$
				}
			}
		}

		for (final Netzwerk network : project.getNetzwerke())
		{
			row.add("Indegree_NWM_" + network.getBezeichnung()); //$NON-NLS-1$

			row.add("Outdegree_NWM_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("IndegreeStd_NWM_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("OutdegreeStd_NWM_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("InCloseness_NWM_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("OutCloseness_NWM_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("ProximityPrestige_NWM_" + network.getBezeichnung());//$NON-NLS-1$

			row.add("OutClosenessStd_NWM_" + network.getBezeichnung());//$NON-NLS-1$

		}

		// Calculate network values for all actors (including those outside the
		// visual network map)
		/*
		 * for (final Netzwerk network : project.getNetzwerke()) {
		 * row.add("Indegree_All_" + network.getBezeichnung()); //$NON-NLS-1$
		 * 
		 * row.add("Outdegree_All_" + network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * row.add("IndegreeStd_All_" + network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * row.add("OutdegreeStd_All_" + network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * row.add("InCloseness_All_" + network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * row.add("OutCloseness_All_" + network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * row.add("ProximityPrestige_All_" +
		 * network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * row.add("OutClosenessStd_All_" +
		 * network.getBezeichnung());//$NON-NLS-1$
		 * 
		 * }
		 */
		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// ------Data---------------------------------

		// next rows
		for (Akteur alter : akteure)
		{

			if (alter != project.getEgo())
			{

				// Actor name and id
				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName().replace(';', '_'); //$NON-NLS-1$
						break;
				}

				
				row.add(tmp);

				// Ego name and id
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:

						break;
					default:
						if (VennMaker.getInstance().getProject().getEgo() != null)
							tmp = VennMaker.getInstance().getProject().getEgo()
									.getName(); //$NON-NLS-1$
						else
							tmp = "";
						break;
				}
				if (VennMaker.getInstance().getProject().getEgo() != null)
					tmp += VennMaker.getInstance().getProject().getEgo().getId();
				else
					tmp += "";

				row.add(tmp);

				// x- and y- coordinate
				for (final Netzwerk network : project.getNetzwerke())
				{
					Point2D position = alter.getLocation(network);
					if (position != null)
					{
						row.add(alter.getLocation(network).getX());

						row.add(alter.getLocation(network).getY());
					}
					else
					{
						row.add(null);
						row.add(null);
					}

				}

				// --- Attribute Values --------

				int code = 0; // value code
				for (final Netzwerk n : project.getNetzwerke())
					for (final AttributeType attributeType : VennMaker.getInstance()
							.getProject().getAttributeTypes())

						if (attributeType.getType().equals("ACTOR"))
						{
							if (((AttributeSubject) alter).getAttributes(n).get(
									attributeType) != null)
							{

								if (attributeType.getPredefinedValues() != null)
								{
									code = attributeType.getPredefinedValueCode(alter
											.getAttributes(n).get(attributeType)
											.toString());
									if (code > 0) // use the value code instead of the
														// value
										row.add(code);
									else
										row.add(((AttributeSubject) alter).getAttributes(
												n).get(attributeType));
								}
								else
									// Free answer
									row.add(((AttributeSubject) alter).getAttributes(n)
											.get(attributeType));
							}
							else
								row.add("");
						}

				// --- Calculations --------------

				// ------only actors who are shown on the network
				// map----------------
				for (final Netzwerk networks : project.getNetzwerke())
				{
					Compute m = new Compute(networks, networks.getAkteure());

					if (networks.getAkteure().contains(alter))
					{
						row.add(m.inDegree(alter));
						row.add(m.outDegree(alter));

						row.add(m.inDegreeStd(alter));
						row.add(m.outDegreeStd(alter));

						row.add(m.inCloseness(alter));
						row.add(m.outCloseness(alter));
						row.add(m.proximityPrestige(alter));
						row.add(m.outClosenessStd(alter));
					}
					else
					{
						row.add("");
						row.add("");

						row.add("");
						row.add("");

						row.add("");
						row.add("");

						row.add("");
						row.add("");
						// 8
					}

				}

				// ------All actors ----------------
				/*
				 * for (final Netzwerk networks2 : project.getNetzwerke()) { Compute
				 * m = new Compute(networks2, akteure, true);
				 * 
				 * row.add(m.inDegree(alter)); row.add(m.outDegree(alter));
				 * 
				 * row.add(m.inDegreeStd(alter)); row.add(m.outDegreeStd(alter));
				 * 
				 * row.add(m.inCloseness(alter)); row.add(m.outCloseness(alter));
				 * row.add(m.proximityPrestige(alter));
				 * row.add(m.outClosenessStd(alter));
				 * 
				 * }
				 */
				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

				row.clear();

			}
		}
		return csv;
	}

	/**
	 * Relationgruppen
	 * 
	 * Erzeugt einen String, der in eine CSV Datei geschrieben werden kann. Der
	 * String enthaelt Ego-ID, Netzwerkbezeichnung, Alter-ID und die Anzahl der
	 * Relationsgruppen zwischen Ego-Alter und Alter-Alter
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_Relationgroups(Netzwerk network, String separator,
			String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}
				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// Von den Relation ausgehend die Analyse betrachten

		ArrayList<String> tmpRelGroup = new ArrayList<String>();

		boolean vorhanden = false;
		int counter1 = 0;
		int counter2 = 0;

		// Relationsgruppen-Liste erstellen
		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{

			if ((!at.getType().equals("ACTOR"))
					&& (tmpRelGroup.contains(at.getType()) == false))
			{
				tmpRelGroup.add(at.getType());
			}
		}

		// Erste Spalte
		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{
				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

				// Nachfolgende Spalten ---------------------

				if ((network.getAkteure().contains(alter)))
				{

					// Gerichtete Relationen
					for (final Akteur alter2 : VennMaker.getInstance().getProject()
							.getAkteure())
					{
						counter1 = 0;
						counter2 = 0;

						if ((network.getAkteure().contains(alter2)))
						{
							// Gerichtete Relationen durchgehen und schauen, ob
							// Relationsgruppe vorkommt

							for (String relG : tmpRelGroup)
							{
								vorhanden = false;

								for (AttributeType at : VennMaker.getInstance()
										.getProject().getAttributeTypes())
								{
									for (Relation tmpR : alter.getRelations(network))

										if ((tmpR.getAttributes(network) != null)
												&& (tmpR.getAkteur().equals(alter2)))
										{

											if ((!at.getType().equals("ACTOR"))
													&& (relG.equals(at.getType()))
													&& (VennMaker.getInstance().getProject()
															.getIsDirected(at.getType()))
													&& (tmpR.getAttributeCollectorValue()
															.equals(at.getType())))
											{
												vorhanden = true;
											}
										}

								}
								if (vorhanden == true)
									counter1++;
							}

							// Ungerichtete Relationen

							for (String relG : tmpRelGroup)
							{
								vorhanden = false;

								for (AttributeType at : VennMaker.getInstance()
										.getProject().getAttributeTypes())
								{

									// Alter -> Alter2
									for (Relation tmpR : alter.getRelations(network))

										if ((tmpR.getAttributes(network) != null)
												&& (tmpR.getAkteur().equals(alter2)))
										{

											if ((!at.getType().equals("ACTOR"))
													&& (relG.equals(at.getType()))
													&& (!VennMaker.getInstance()
															.getProject()
															.getIsDirected(at.getType()))
													&& (tmpR.getAttributeCollectorValue()
															.equals(at.getType())))
											{
												vorhanden = true;
											}
										}

									// Alter2 -> Alter
									for (Relation tmpR : alter2.getRelations(network))

										if ((tmpR.getAttributes(network) != null)
												&& (tmpR.getAkteur().equals(alter)))
										{

											if ((!at.getType().equals("ACTOR"))
													&& (relG.equals(at.getType()))
													&& (!VennMaker.getInstance()
															.getProject()
															.getIsDirected(at.getType()))
													&& (tmpR.getAttributeCollectorValue()
															.equals(at.getType())))
											{
												vorhanden = true;
											}
										}

								}
								if (vorhanden == true)
									counter2++;
							}

							row.add(counter1 + counter2);
						}

					}
				}
				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);
				row.clear();
			}
		}
		return csv;
	}

	/**
	 * Relationgruppen (auch fuer nichtvisualisierte Akteure)
	 * 
	 * Erzeugt einen String, der in eine CSV Datei geschrieben werden kann. Der
	 * String enthaelt Ego-ID, Netzwerkbezeichnung, Alter-ID und die Anzahl der
	 * Relationsgruppen zwischen Ego-Alter und Alter-Alter
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_Relationgroups_allActors(Netzwerk network,
			String separator, String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// Von den Relation ausgehend die Analyse betrachten

		ArrayList<String> tmpRelGroup = new ArrayList<String>();

		boolean vorhanden = false;
		int counter1 = 0;
		int counter2 = 0;

		// Relationsgruppen-Liste erstellen
		for (AttributeType at : VennMaker.getInstance().getProject()
				.getAttributeTypes())
		{

			if ((!at.getType().equals("ACTOR"))
					&& (tmpRelGroup.contains(at.getType()) == false))
			{
				tmpRelGroup.add(at.getType());
			}
		}

		// Erste Spalte
		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

			// Nachfolgende Spalten ---------------------

			// Gerichtete Relationen
			for (final Akteur alter2 : VennMaker.getInstance().getProject()
					.getAkteure())
			{
				counter1 = 0;
				counter2 = 0;

				// Gerichtete Relationen durchgehen und schauen, ob
				// Relationsgruppe vorkommt

				for (String relG : tmpRelGroup)
				{
					vorhanden = false;

					for (AttributeType at : VennMaker.getInstance().getProject()
							.getAttributeTypes())
					{
						for (Relation tmpR : alter.getRelations(network))

							if ((tmpR.getAttributes(network) != null)
									&& (tmpR.getAkteur().equals(alter2)))
							{

								if ((!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									vorhanden = true;
								}
							}

					}
					if (vorhanden == true)
						counter1++;
				}

				// Ungerichtete Relationen

				for (String relG : tmpRelGroup)
				{
					vorhanden = false;

					for (AttributeType at : VennMaker.getInstance().getProject()
							.getAttributeTypes())
					{

						// Alter -> Alter2
						for (Relation tmpR : alter.getRelations(network))

							if ((tmpR.getAttributes(network) != null)
									&& (tmpR.getAkteur().equals(alter2)))
							{

								if ((!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (!VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									vorhanden = true;
								}
							}

						// Alter2 -> Alter
						for (Relation tmpR : alter2.getRelations(network))

							if ((tmpR.getAttributes(network) != null)
									&& (tmpR.getAkteur().equals(alter)))
							{

								if ((!at.getType().equals("ACTOR"))
										&& (relG.equals(at.getType()))
										&& (!VennMaker.getInstance().getProject()
												.getIsDirected(at.getType()))
										&& (tmpR.getAttributeCollectorValue().equals(at
												.getType())))
								{
									vorhanden = true;
								}
							}

					}
					if (vorhanden == true)
						counter2++;
				}

				row.add(counter1 + counter2);

			}

			// Row in String schreiben
			csv += new CsvOutput().csvToRow(row, separator, textelement, format);
			row.clear();
		}
		return csv;
	}

	/**
	 * Adjazenz-Matrix
	 * 
	 * Erzeugt eine Adjazenz-Matrix fuer jedes Merkmal eines predef.
	 * Relationsattributs (enthaelt nur 0 oder 1) 0=es ist keine Beziehung mit diesem Wert 
	 * vorhanden 1=es ist eine Beziehung mit diesem Wert vorhanden.
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 * @param attributeType
	 * @param value
	 */
	public String toCsv_AdjacencyAttribute(Netzwerk network, String separator,
			String textelement, String format, AttributeType attributeType,
			Object value)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung() + "_" + attributeType.getLabel() + "_"
				+ value);

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}
				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{
				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

					for (final Akteur alter2 : VennMaker.getInstance().getProject()
							.getAkteure())
					{

						int eintrag = 0;
						if ((alter != alter2)
								&& network.getAkteure().contains(alter2))
						{
							
							Vector<Relation> tempRelations = alter.getAllRelationsTo(alter2,network, attributeType);		
							if (tempRelations != null){
							for(int q=0; q < tempRelations.size(); q++)
							if ((tempRelations.get(q) != null)
									&& (tempRelations.get(q).getAttributeValue(attributeType,
											network) == value))
							{
								eintrag = 1; //$NON-NLS-1$
								break;
							}
							else
								eintrag = 0;
							}
							
							if (eintrag == 0){
									
								Relation tempRel2 = alter2.getRelationTo(alter, network);
								Vector<Relation> tempRelations2 = alter2.getAllRelationsTo(alter,network, attributeType);
								if (tempRelations2 != null){
									
								for(int w=0; w < tempRelations2.size(); w++)
									if ((tempRelations2.get(w) != null)
										&& (VennMaker
												.getInstance()
												.getProject()
												.getIsDirected(
														tempRel2.getAttributeCollectorValue()) == false)
										&& (tempRelations2.get(w).getAttributeValue(
												attributeType, network) == value))
								{
										eintrag = 1;
										break;
								}

								else
									eintrag = 0;
							}
								else
									eintrag = 0;
							}
							row.add(eintrag);
						}
						else if (network.getAkteure().contains(alter2)){
							eintrag = 0; // Die Diagonale in Matrix bleibt leer
							row.add(eintrag);
						}
					}

				

				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

				row.clear();
			}
		}

		return csv;
	}

	/**
	 * Adjazenz-Matrix
	 * 
	 * Erzeugt eine Adjazenz-Matrix (enthaelt nur 0 oder 1) 0=es ist keine
	 * Beziehung vorhanden 1=es ist eine Beziehung vorhanden
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_Adjacency(Netzwerk network, String separator,
			String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}


				row.add(tmp);

				if ((network.getAkteure().contains(alter)))
				{

					for (final Akteur alter2 : VennMaker.getInstance().getProject()
							.getAkteure())
					{

						if ((alter != alter2)
								&& network.getAkteure().contains(alter2))
						{
							Relation tempRelation = alter.getRelationTo(alter2,
									network);
							if (tempRelation != null)
								row.add(1); //$NON-NLS-1$

							else
							{
								Relation tempRelation2 = alter2.getRelationTo(alter,
										network);
								if ((tempRelation2 != null)
										&& (VennMaker
												.getInstance()
												.getProject()
												.getIsDirected(
														tempRelation2
																.getAttributeCollectorValue()) == false))
									row.add(1); //$NON-NLS-1$

								else
									row.add(0);
							}
						}
						else if (network.getAkteure().contains(alter2))
							row.add(0); // Die Diagonale in Matrix bleibt leer
					}

				}

				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

				row.clear();
			}
		}

		return csv;
	}

	/**
	 * Adjazenz-Matrix (all actors)
	 * 
	 * Erzeugt eine Adjazenz-Matrix (enthaelt nur 0 oder 1) 0=es ist keine
	 * Beziehung vorhanden 1=es ist eine Beziehung vorhanden
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_Adjacency_allActors(Netzwerk network, String separator,
			String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

			for (final Akteur alter2 : VennMaker.getInstance().getProject()
					.getAkteure())
			{
				if (alter.getId() != alter2.getId())
				{
					Relation tempRelation = alter.getRelationTo(alter2, network);

					if (tempRelation != null)
					{
						row.add(1); //$NON-NLS-1$
					}
					else
					{
						Relation tempRelation2 = alter2.getRelationTo(alter, network);
						if ((tempRelation2 != null)
								&& (VennMaker
										.getInstance()
										.getProject()
										.getIsDirected(
												tempRelation2.getAttributeCollectorValue()) == false))
							row.add(1); //$NON-NLS-1$

						else
							row.add(0);
					}
				}
				else
					row.add(0); // Die Diagonale in Matrix bleibt leer
			}

			// Row in String schreiben
			csv += new CsvOutput().csvToRow(row, separator, textelement, format);

			row.clear();

		}

		return csv;
	}

	/**
	 * Adjazenz-Matrix, ohne EGO
	 * 
	 * Erzeugt eine Adjazenz-Matrix (enthaelt nur 0 oder 1) 0=es ist keine
	 * Beziehung vorhanden 1=es ist eine Beziehung vorhanden
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_AdjacencyAlteri(Netzwerk network, String separator,
			String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			if ((network.getAkteure().contains(alter))
					&& (!alter.equals(VennMaker.getInstance().getProject().getEgo())))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter))
					&& (!alter.equals(VennMaker.getInstance().getProject().getEgo())))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

				if ((network.getAkteure().contains(alter)))
				{
					for (final Akteur alter2 : VennMaker.getInstance().getProject()
							.getAkteure())
					{

						if ((alter != alter2)
								&& (network.getAkteure().contains(alter2))
								&& (!alter.equals(VennMaker.getInstance().getProject()
										.getEgo()))
								&& (!alter2.equals(VennMaker.getInstance().getProject()
										.getEgo())))
						{

							Relation tempRelation = alter.getRelationTo(alter2,
									network);
							if (tempRelation != null)
								row.add(1);

							else
							{
								Relation tempRelation2 = alter2.getRelationTo(alter,
										network);
								if ((tempRelation2 != null)
										&& (VennMaker
												.getInstance()
												.getProject()
												.getIsDirected(
														tempRelation2
																.getAttributeCollectorValue()) == false))
									row.add(1);

								else
									row.add(0);
							}
						}
						else if ((network.getAkteure().contains(alter2))
								&& (!alter2.equals(VennMaker.getInstance().getProject()
										.getEgo())))
							row.add(0); // Diagonale in Matrix bleibt leer
					}

				}
				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

				row.clear();
			}

		}
		return csv;
	}

	/**
	 * Adjazenz-Matrix ohne EGO aber mit allen Alteri, auch solchen, die nicht in
	 * die NWK eingezeichnet sind
	 * 
	 * Erzeugt eine Adjazenz-Matrix (enthaelt nur 0 oder 1) 0=es ist keine
	 * Beziehung vorhanden 1=es ist eine Beziehung vorhanden
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_AdjacencyAlteri_allAlteri(Netzwerk network,
			String separator, String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			if (!alter.equals(VennMaker.getInstance().getProject().getEgo()))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if (!alter.equals(VennMaker.getInstance().getProject().getEgo()))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

				for (final Akteur alter2 : VennMaker.getInstance().getProject()
						.getAkteure())
				{

					if ((alter.getId() != alter2.getId())
							&& (!alter.equals(VennMaker.getInstance().getProject()
									.getEgo()))
							&& (!alter2.equals(VennMaker.getInstance().getProject()
									.getEgo())))
					{

						Relation tempRelation = alter.getRelationTo(alter2, network);
						if (tempRelation != null)
						{
							row.add(1);
						}
						else
						{
							Relation tempRelation2 = alter2.getRelationTo(alter,
									network);
							if ((tempRelation2 != null)
									&& (VennMaker
											.getInstance()
											.getProject()
											.getIsDirected(
													tempRelation2
															.getAttributeCollectorValue()) == false))
								row.add(1);

							else
								row.add(0);
						}
					}
					else if (!alter2.equals(VennMaker.getInstance().getProject()
							.getEgo()))
						row.add(0); // Diagonale in Matrix bleibt leer
				}

				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

				row.clear();
			}

		}
		return csv;
	}

	/**
	 * @param network
	 *           the network, these statistics describe
	 * @return the number of actors (Total, by sectors, by circles, by actortype)
	 *         as a HashMap (sorted)
	 */
	public Map<String, Integer> getActorStatistics(Netzwerk network)
	{
		// Initialize the HashMap
		Map<String, Integer> statisticMap = new HashMap<String, Integer>();
		// TODO
		// for (AkteurTyp actortype : akteurTypen)
		// statisticMap.put("Type_" + actortype.getBezeichnung(), 0);

		if (network.getHintergrund().getNumCircles() > 0)
			for (int x = -1; x <= network.getHintergrund().getNumCircles(); x++)
				statisticMap.put("Circle_" + x, 0); //$NON-NLS-1$
		else
			statisticMap.put("Circle_-1", 0); //$NON-NLS-1$

		if (network.getHintergrund().getNumSectors() > 0)
			for (int x = -1; x < network.getHintergrund().getNumSectors(); x++)
				statisticMap.put("Sector_" + x, 0); //$NON-NLS-1$
		else
			statisticMap.put("Sector_-1", 0); //$NON-NLS-1$

		statisticMap.put("Total", 0); //$NON-NLS-1$

		for (Akteur actor : network.getAkteure())
		{
			if (actor != network.getEgo())
			{
				int actorsSector = actor.getSector(network);
				int actorsCircle = actor.getCircle(network);
				// TODO
				//String actorsTyp = ""; //$NON-NLS-1$
				// if (actor.getTyp() != null)
				// actorsTyp = actor.getTyp().getBezeichnung();
				// Count all actors in network
				statisticMap.put("Total", (statisticMap.get("Total") + 1)); //$NON-NLS-1$ //$NON-NLS-2$
				// Count actors in the different sectors
				statisticMap.put("Sector_" + actorsSector, (statisticMap //$NON-NLS-1$
						.get("Sector_" + actorsSector) + 1)); //$NON-NLS-1$
				// Count actors in the different circles
				statisticMap.put("Circle_" + actorsCircle, (statisticMap //$NON-NLS-1$
						.get("Circle_" + actorsCircle) + 1)); //$NON-NLS-1$
				// Count occurence of the different actortypes
				// statisticMap.put("Type_" + actorsTyp, (statisticMap.get("Type_"
				// + actorsTyp) + 1));
			}
		}
		return statisticMap;
	}

	/**
	 * Returns the number of all actors (without ego) of a network map (including
	 * visualized actors and non visualized actors)
	 * 
	 * @param network
	 *           the network, these statistics describe
	 * @return the number of actors (Total, by sectors, by circles, by actortype)
	 *         as a HashMap (sorted)
	 */
	public Map<String, Integer> getActorStatistics_allActors(Netzwerk network)
	{
		// Initialize the HashMap
		Map<String, Integer> statisticMap = new HashMap<String, Integer>();
		// TODO

		statisticMap.put("Total", 0); //$NON-NLS-1$

		for (Akteur actor : VennMaker.getInstance().getProject().getAkteure())
		{
			if (actor != network.getEgo())
			{
				statisticMap.put("Total", (statisticMap.get("Total") + 1)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return statisticMap;
	}

	public String toCsv_Statistics(Netzwerk network, String separator,
			String textelement, String format)
	{
		List<Object> row = new ArrayList<Object>();

		String csv = "";

		Map<String, Integer> statisticMap = getActorStatistics(network);

		Map<String, Integer> statisticMap_all = getActorStatistics_allActors(network);

		row.add("Network map");

		row.add("Actors Total_nwm");

		row.add("Actors Total_all");

		// for (AkteurTyp actortype : akteurTypen)
		//			text += "Actortype_" + actortype.getBezeichnung() + ";"; //$NON-NLS-2$

		// Circles
		if (network.getHintergrund().getNumCircles() > 0)
			for (int x = -1; x <= network.getHintergrund().getNumCircles(); x++)
			{
				if (x > 0)
				{
					row.add("Circle_" + x + "_" //$NON-NLS-1$//$NON-NLS-2$
							+ network.getHintergrund().getCircles().get(x - 1));

				}
				else
				{
					row.add("Circle_" + x);//$NON-NLS-1$
				}

			}
		else
		{
			row.add("Circle_-1");//$NON-NLS-1$

		}

		// Sectors
		if (network.getHintergrund().getNumSectors() > 0)
			for (int x = -1; x < network.getHintergrund().getNumSectors(); x++)
			{
				if (x > -1)
				{
					row.add("Sector_" //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
							+ network.getHintergrund().getSector(x).label);

				}
				else
				{
					row.add(Messages.getString("ExportData.13") + "_" + x); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		else
		{
			row.add(Messages.getString("ExportData.15")); //$NON-NLS-1$
		}
		row.add(Messages.getString("ExportData.16") + "_NWM"); //$NON-NLS-1$
		row.add(Messages.getString("ExportData.17") + "_NWM"); //$NON-NLS-1$

		row.add(Messages.getString("ExportData.16") + "_all"); //$NON-NLS-1$
		row.add(Messages.getString("ExportData.17") + "_all"); //$NON-NLS-1$

		// Calculate some values for actors
		Compute m = new Compute(network, network.getAkteure());

		/* add attributeTypes */
		HashMap<AttributeType, HashMap<String, Integer>> attributeQuantities = m
				.attributeQuantities(network);
		for (AttributeType at : attributeQuantities.keySet())
		{
			for (String s : attributeQuantities.get(at).keySet())
			{
				row.add(at.toString() + " [" + s + "]_nwm");
			}
		}

		// Calculate some values for all actors
		Compute m2 = new Compute(network, VennMaker.getInstance().getProject()
				.getAkteure(), true);

		/* add attributeTypes */
		HashMap<AttributeType, HashMap<String, Integer>> attributeQuantities_allActors = m2
				.attributeQuantities_allActors(network);
		for (AttributeType at : attributeQuantities_allActors.keySet())
		{
			for (String s : attributeQuantities_allActors.get(at).keySet())
			{
				row.add(at.toString() + " [" + s + "]_all");
			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// Second row

		row.add(network.getBezeichnung());

		row.add(statisticMap.get("Total")); //$NON-NLS-1$

		row.add(statisticMap_all.get("Total")); //$NON-NLS-1$

		// for (AkteurTyp actortype : akteurTypen)
		//			text += statisticMap.get("Type_" + actortype.getBezeichnung()) + ";"; //$NON-NLS-2$

		// Circles
		if (network.getHintergrund().getNumCircles() > 0)
			for (int x = -1; x <= network.getHintergrund().getNumCircles(); x++)
			{
				if (x > 0)
				{
					row.add(statisticMap.get("Circle_" + x)); //$NON-NLS-1$//$NON-NLS-2$
				}
				else
					row.add(statisticMap.get("Circle_" + x)); //$NON-NLS-1$//$NON-NLS-2$

			}
		else
			row.add(statisticMap.get("Circle_-1")); //$NON-NLS-1$//$NON-NLS-2$

		// Sectors
		if (network.getHintergrund().getNumSectors() > 0)
			for (int x = -1; x < network.getHintergrund().getNumSectors(); x++)
			{
				if (x > 0)
					row.add(statisticMap.get("Sector_" + x)); //$NON-NLS-1$//$NON-NLS-2$
				else
					row.add(statisticMap.get("Sector_" + x)); //$NON-NLS-1$//$NON-NLS-2$
			}
		else
			row.add(statisticMap.get("Sector_-1")); //$NON-NLS-1$//$NON-NLS-2$

		// Density
		row.add(m.densityWithEgo()); //$NON-NLS-1$
		row.add(m.densityWithOutEgo(network.getEgo())); //$NON-NLS-1$

		row.add(m2.densityWithEgo()); //$NON-NLS-1$
		row.add(m2.densityWithOutEgo(network.getEgo())); //$NON-NLS-1$

		// add attributeTypeQuantities
		for (AttributeType at : attributeQuantities.keySet())
		{
			for (String s : attributeQuantities.get(at).keySet())
			{
				row.add(attributeQuantities.get(at).get(s));
			}
		}

		// add attributeTypeQuantities of all actors
		for (AttributeType at : attributeQuantities_allActors.keySet())
		{
			for (String s : attributeQuantities_allActors.get(at).keySet())
			{
				row.add(attributeQuantities_allActors.get(at).get(s));
			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		return csv;
	}

	/**
	 * Relationtype
	 * 
	 * Erzeugt einen String, der in eine CSV Datei geschrieben werden kann. Der
	 * String enthaelt Ego-ID, Netzwerkbezeichnung, Alter-ID und der jeweilige
	 * Beziehungstyp zwischen Ego-Alter und Alter-Alter.
	 * 
	 * Es werden nur Relationen eines bestimmten Typs beruecksichtigt.
	 * 
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_File_RelationType(Netzwerk network, String separator,
			String textelement, String format, AttributeType attributetype)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung() + "_" + attributetype.getLabel());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// First column (actor name)
		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}



				row.add(tmp);

				// Other columns

				for (final Akteur alter2 : VennMaker.getInstance().getProject()
						.getAkteure())
				{

					boolean directed = true;

					Relation r = null;
					Relation r2 = null;

					// ------

					for (Relation tmpR : alter.getRelations(network))
					{
						if ((tmpR.getAttributes(network) != null)
								&& (tmpR.getAkteur().equals(alter2)))
						{
							if (tmpR.getAttributes(network).get(attributetype) != null)
								if (tmpR.getAttributes(network).containsKey(
										attributetype)
										&& !tmpR.getAttributes(network)
												.get(attributetype).toString().equals(""))
								{
									r = tmpR;

									break;
								}
						}
					}
					// ---

					for (Relation tmpR2 : alter2.getRelations(network))
					{
						if ((tmpR2.getAttributes(network) != null)
								&& (tmpR2.getAkteur().equals(alter)))
						{
							if (tmpR2.getAttributes(network).get(attributetype) != null)
								if (tmpR2.getAttributes(network).containsKey(
										attributetype)
										&& !tmpR2.getAttributes(network)
												.get(attributetype).toString().equals(""))
								{
									r2 = tmpR2;

									break;

								}

						}
					}

					// ---

					if (r != null)
						if ((VennMaker.getInstance().getProject()
								.getIsDirected(r.getAttributeCollectorValue()) == false))
							directed = false;

					if (r2 != null)
						if ((VennMaker.getInstance().getProject()
								.getIsDirected(r2.getAttributeCollectorValue()) == false))
							directed = false;

					if ((alter != alter2) && (r != null) && (directed == true))
					{
						int value = attributetype.getPredefinedValueCode((String) r
								.getAttributeValue(attributetype, network));
						if (value > 0) // Predefined answers
							row.add(value);
						else
							// no predefined answers
							row.add(r.getAttributeValue(attributetype, network));

					}
					else

					if ((alter != alter2) && ((r != null) || (r2 != null))
							&& (directed == false))
					{
						if (r != null)
						{
							int value = attributetype
									.getPredefinedValueCode((String) r
											.getAttributeValue(attributetype, network));
							// Predefined answers
							if (value > 0)
								row.add(value);
							else
								// no predefined answers
								row.add(r.getAttributeValue(attributetype, network));
						}
						else if (r2 != null)
						{
							int value = attributetype
									.getPredefinedValueCode((String) r2
											.getAttributeValue(attributetype, network));
							// Predefined answers
							if (value > 0)
								row.add(value);
							else
								// no predefined answers
								row.add(r2.getAttributeValue(attributetype, network));
						}
					}

					else if (network.getAkteure().contains(alter2))
						row.add("");
				}
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

			}

			row.clear();
		}

		return csv;
	}

	/**
	 * Relationtype Matrix, fuer alle Akteur, auch solche, die nicht in die
	 * Netzwerkkarte eingezeichnet sind
	 * 
	 * Erzeugt einen String, der in eine CSV Datei geschrieben werden kann. Der
	 * String enthaelt Ego-ID, Netzwerkbezeichnung, Alter-ID und der jeweilige
	 * Beziehungstyp zwischen Ego-Alter und Alter-Alter.
	 * 
	 * Es werden nur Relationen eines bestimmten Typs beruecksichtigt.
	 * 
	 * @param network
	 * @param separator
	 * @param textelement
	 * @param format
	 * @param attributeType
	 */
	public String toCsv_File_RelationType_allActors(Netzwerk network,
			String separator, String textelement, String format,
			AttributeType attributetype)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung() + "_" + attributetype.getLabel());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// First column (actor name)
		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}


			row.add(tmp);

			// Other columns

			for (final Akteur alter2 : VennMaker.getInstance().getProject()
					.getAkteure())
			{

				boolean directed = true;

				Relation r = null;
				Relation r2 = null;

				// ------

				for (Relation tmpR : alter.getRelations(network))
				{
					if ((tmpR.getAttributes(network) != null)
							&& (tmpR.getAkteur().equals(alter2)))
					{
						if (tmpR.getAttributes(network).get(attributetype) != null)
							if (tmpR.getAttributes(network).containsKey(attributetype)
									&& !tmpR.getAttributes(network).get(attributetype)
											.toString().equals(""))
							{
								r = tmpR;

								break;
							}
					}
				}
				// ---

				for (Relation tmpR2 : alter2.getRelations(network))
				{
					if ((tmpR2.getAttributes(network) != null)
							&& (tmpR2.getAkteur().equals(alter)))
					{
						if (tmpR2.getAttributes(network).get(attributetype) != null)
							if (tmpR2.getAttributes(network)
									.containsKey(attributetype)
									&& !tmpR2.getAttributes(network).get(attributetype)
											.toString().equals(""))
							{
								r2 = tmpR2;

								break;

							}

					}
				}

				// ---

				if (r != null)
					if ((VennMaker.getInstance().getProject()
							.getIsDirected(r.getAttributeCollectorValue()) == false))
						directed = false;

				if (r2 != null)
					if ((VennMaker.getInstance().getProject()
							.getIsDirected(r2.getAttributeCollectorValue()) == false))
						directed = false;

				if ((alter != alter2) && (r != null) && (directed == true))
				{
					int value = attributetype.getPredefinedValueCode((String) r
							.getAttributeValue(attributetype, network));
					if (value > 0) // Predefined answers
						row.add(value);
					else
						// no predefined answers
						row.add(r.getAttributeValue(attributetype, network));

				}
				else

				if ((alter != alter2) && ((r != null) || (r2 != null))
						&& (directed == false))
				{
					if (r != null)
					{
						int value = attributetype.getPredefinedValueCode((String) r
								.getAttributeValue(attributetype, network));
						// Predefined answers
						if (value > 0)
							row.add(value);
						else
							// no predefined answers
							row.add(r.getAttributeValue(attributetype, network));
					}
					else if (r2 != null)
					{
						int value = attributetype.getPredefinedValueCode((String) r2
								.getAttributeValue(attributetype, network));
						// Predefined answers
						if (value > 0)
							row.add(value);
						else
							// no predefined answers
							row.add(r2.getAttributeValue(attributetype, network));
					}
				}

				else if (network.getAkteure().contains(alter2))
					row.add("");
			}
			csv += new CsvOutput().csvToRow(row, separator, textelement, format);

			row.clear();
		}

		return csv;
	}

	/**
	 * Vergleicht der Reihe nach ein Netzwerk mit dem nachfolgenden Netzwerk und
	 * schaut, ob sich der RelationTyp zwischen den jeweiligen Akteurspaaren
	 * geaendert hat.
	 * 
	 * Der Rueckgabewert sagt aus, bei wievielen Netzwerkkartenwechsel sich auch
	 * der Relationstyp aendert. Bei 3 Karten kann man zweimal wechseln: 1 auf 2
	 * und 2 auf 3 Ein Rueckgabewert von 0.5 bedeutet hier, dass bei der haelfte
	 * der Kartenwechsel sich auch der Relationstyp geaendert hat.
	 * 
	 * @return CSV string
	 */
	public String relationChangeSequenceFrequency(String separator,
			String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add("");

		Vector<Akteur> besuchteAkteure = new Vector<Akteur>();

		for (final Netzwerk network : project.getNetzwerke())
		{
			if (!network.getAkteure().isEmpty())
			{
				for (final Akteur actor : network.getAkteure())
				{
					if ((actor != network.getEgo())
							&& !besuchteAkteure.contains(actor))
						besuchteAkteure.add(actor);
				}
			}
		}

		for (final Akteur alter : besuchteAkteure)
		{
			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// Alle Akteure durchgehen
		for (Akteur actor_source : besuchteAkteure)
		{

			// Actor name
			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					break;
				default:
					tmp = actor_source.getName(); //$NON-NLS-1$
					break;
			}
			tmp += "_" + actor_source.getId(); //$NON-NLS-1$
			row.add(tmp);

			for (Akteur actor_target : besuchteAkteure)
			{
				int change = 0;
				RelationTyp tmprelationtypN2 = null;

				if (actor_source != actor_target)

					for (Netzwerk network : project.getNetzwerke())
					{

						Relation r = actor_source
								.getRelationTo(actor_target, network);
						Relation r2 = actor_target.getRelationTo(actor_source,
								network);
						RelationTyp tmprelationtypN1 = null;

						// Relation vereinheitlichen
						if (r != null)
							tmprelationtypN1 = r.getTyp();
						else if (r2 != null)
							if (r2.getTyp().getDirectionType() == DirectionType.UNDIRECTED)
								tmprelationtypN1 = r2.getTyp();

						if (tmprelationtypN1 != tmprelationtypN2)
						{
							if (project.getNetzwerke().indexOf(network) > 0)
								change++;
						}

						tmprelationtypN2 = tmprelationtypN1;

					}

				// calculate change frequency
				float frequency = 0.0f;
				if (project.getNetzwerke().size() > 1)
					frequency = (float) ((float) change / (float) (project
							.getNetzwerke().size() - 1));
				else
					frequency = 0;

				row.add((int) (frequency * 100));

			}

			// Row in String schreiben
			csv += new CsvOutput().csvToRow(row, separator, textelement, format);

			row.clear();

		}
		return csv;
	}

	/**
	 * Vergleicht alle Netzwerke miteinander, ob sich zwischen Akteurspaaren der
	 * Relationstyp geaendert hat
	 * 
	 * @param separator
	 *           the separator character
	 * @param textelement
	 *           the source text string
	 * @param format
	 *           comma or semicolon: Defines the numeric print output format.
	 *           (e.g. comma or point: 1,3 or 1.3)
	 * 
	 * @return CSV string
	 */
	public String relationChangeFrequency(String separator, String textelement,
			String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add("");

		Vector<Akteur> besuchteAkteure = new Vector<Akteur>();

		for (final Netzwerk network : project.getNetzwerke())
		{
			if (!network.getAkteure().isEmpty())
			{
				for (final Akteur actor : network.getAkteure())
				{
					if ((actor != network.getEgo())
							&& !besuchteAkteure.contains(actor))
						besuchteAkteure.add(actor);
				}
			}
		}

		for (final Akteur alter : besuchteAkteure)
		{
			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		// Alle Akteure durchgehen

		for (Akteur actor_source : besuchteAkteure)
		{

			// Actor name
			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					break;
				default:
					tmp = actor_source.getName(); //$NON-NLS-1$
					break;
			}
			tmp += "_" + actor_source.getId(); //$NON-NLS-1$
			row.add(tmp);

			for (Akteur actor_target : besuchteAkteure)
			{

				int change = 0;
				if (actor_source != actor_target)

					for (Netzwerk network : project.getNetzwerke())
					{

						Relation r = actor_source
								.getRelationTo(actor_target, network);
						Relation r2 = actor_target.getRelationTo(actor_source,
								network);
						RelationTyp tmprelationtypN1 = null;

						// Relation vereinheitlichen
						if (r != null)
							tmprelationtypN1 = r.getTyp();
						else if (r2 != null)
							if (r2.getTyp().getDirectionType() == DirectionType.UNDIRECTED)
								tmprelationtypN1 = r2.getTyp();

						for (Netzwerk network2 : project.getNetzwerke())
						{
							if (!network.equals(network2))
							{
								r = actor_source.getRelationTo(actor_target, network2);
								r2 = actor_target.getRelationTo(actor_source, network2);
								RelationTyp tmprelationtypN2 = null;

								// Relation vereinheitlichen
								if (r != null)
									tmprelationtypN2 = r.getTyp();
								else if (r2 != null)
									if (r2.getTyp().getDirectionType() == DirectionType.UNDIRECTED)
										tmprelationtypN2 = r2.getTyp();

								if (tmprelationtypN1 != tmprelationtypN2)
									change++;

							}
						}
					}
				// calculate change frequency
				float frequency = 0.0f;
				if (project.getNetzwerke().size() > 1)
					frequency = (float) ((float) change / (float) (project
							.getNetzwerke().size() * (project.getNetzwerke().size() - 1)));
				else
					frequency = 0;

				row.add((int) (frequency * 100));

			}
			// Row in String schreiben
			csv += new CsvOutput().csvToRow(row, separator, textelement, format);

			row.clear();

		}
		return csv;
	}

	/**
	 * Multipexity (weighted)-Matrix
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_Mulitplexity(Netzwerk network, String separator,
			String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

			}
		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{
			if ((network.getAkteure().contains(alter)))
			{

				String tmp = "";
				switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
				{
					case NUMBER:
						tmp = ""+alter.getId();
						break;
					default:
						tmp = alter.getName(); //$NON-NLS-1$
						break;
				}

				row.add(tmp);

				if ((network.getAkteure().contains(alter)))
				{

					for (final Akteur alter2 : VennMaker.getInstance().getProject()
							.getAkteure())
					{
						int anzahl = 0;
						if ((alter != alter2)
								&& network.getAkteure().contains(alter2))
						{

							// gerichtete Relationen
							for (Relation tempRelation : alter.getRelations(network))
							{
								if (tempRelation.getAkteur().equals(alter2))
								{
									anzahl++;
								}
							}

							// ungerichtete Relationen
							for (Relation tempRelation : alter2.getRelations(network))
							{
								if ((tempRelation.getAkteur().equals(alter))
										&& (VennMaker
												.getInstance()
												.getProject()
												.getIsDirected(
														tempRelation
																.getAttributeCollectorValue()) == false))
								{
									anzahl++;
								}
							}
							row.add(anzahl);

						}
						else if (network.getAkteure().contains(alter2))
							row.add(0); // Die Diagonale in Matrix bleibt leer
					}

				}

				// Row in String schreiben
				csv += new CsvOutput()
						.csvToRow(row, separator, textelement, format);

				row.clear();
			}

		}

		return csv;
	}

	/**
	 * Multipexity (weighted)-Matrix fuer alle Akteure, auch solche, die nicht in
	 * die Netzwerkkarte eingezeichnet sind.
	 * 
	 * @param separator
	 * @param textelement
	 * @param format
	 */
	public String toCsv_Mulitplexity_allActors(Netzwerk network,
			String separator, String textelement, String format)
	{

		List<Object> row = new ArrayList<Object>();

		String csv = "";

		row.add(network.getBezeichnung());

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

		}

		// Row in String schreiben
		csv += new CsvOutput().csvToRow(row, separator, textelement, format);

		row.clear();

		for (final Akteur alter : VennMaker.getInstance().getProject()
				.getAkteure())
		{

			String tmp = "";
			switch (VennMaker.getInstance().getConfig().getLabelBehaviour())
			{
				case NUMBER:
					tmp = ""+alter.getId();
					break;
				default:
					tmp = alter.getName(); //$NON-NLS-1$
					break;
			}

			row.add(tmp);

			for (final Akteur alter2 : VennMaker.getInstance().getProject()
					.getAkteure())
			{
				int anzahl = 0;
				if (alter.getId() != alter2.getId())
				{

					// gerichtete Relationen
					for (Relation tempRelation : alter.getRelations(network))
					{
						if (tempRelation.getAkteur().equals(alter2))
						{
							anzahl++;
						}
					}

					// ungerichtete Relationen
					for (Relation tempRelation : alter2.getRelations(network))
					{
						if ((tempRelation.getAkteur().equals(alter))
								&& (VennMaker
										.getInstance()
										.getProject()
										.getIsDirected(
												tempRelation.getAttributeCollectorValue()) == false))
						{
							anzahl++;
						}
					}
					row.add(anzahl);

				}
				else
					row.add(0); // Die Diagonale in Matrix bleibt leer
			}

			// Row in String schreiben
			csv += new CsvOutput().csvToRow(row, separator, textelement, format);

			row.clear();
		}

		return csv;
	}

	/**
	 * method to create the CSV-File with all interviewelements and the time
	 * spent on them (in milliseconds)
	 * 
	 * @return
	 */
	public String toCsv_InterviewTimes(InterviewLayer interview,
			String separator, String textelement, String format)
	{
		List<Object> row1 = new ArrayList<Object>();
		List<Object> row2 = new ArrayList<Object>();

		for (InterviewElement ie : interview.getAllElements())
		{
			row1.add(ie.getElementNameInTree());
			row2.add(ie.getTime());
		}

		String csv = new CsvOutput().csvToRow(row1, separator, textelement,
				format);
		csv += new CsvOutput().csvToRow(row2, separator, textelement, format);

		return csv;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void setNetwork(Netzwerk n)
	{
		// TODO Auto-generated method stub

	}

}
