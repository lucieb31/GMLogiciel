package fr.toulousescape.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class IndiceManager {

	private List<Indice> indicesForEnigma = new ArrayList<Indice>();
	private List<Indice> allIndices = new ArrayList<Indice>();
	private List<Enigme> enigmes = new ArrayList<Enigme>();
	private List<Indice> interactions = new ArrayList<Indice>();
	
	private EnigmeComparator enigmeComparator = new EnigmeComparator();
	
	private static Salle currentSalle;
	
	private Properties indicesProps;
	private Properties enigmesProps;
	
	private int indiceNb;
	private int enigmeNb;
	
	private static final String DESC_PROP = ".desc";

	private static final String LINK_PROP = ".link";
	
	private static final String TEXTE_PROP = ".text";

	private static final String TYPE_PROP = ".type";

	private static final String COLOR_PROP = ".color";

	private static final String INDEX_PROP = ".idx";

	private static final String MUSIC_PROP = ".music";

	private static final String IMG_PROP = ".img";
	
	private static final String SON_PROP = ".son";

	private static final String FUNCTION_PROP = ".function";
	
	
	public IndiceManager(Salle salle) {
		enigmes = new ArrayList<>();
		currentSalle = salle;
		String nbIndices = currentSalle.getProperties().getProperty(SallesProperties.INDICES_NB);
		String nbEnigmes = currentSalle.getProperties().getProperty(SallesProperties.ENIGMES_NB);
		if (nbIndices != null)
		{
			indiceNb = Integer.valueOf(nbIndices);
			enigmeNb = Integer.valueOf(nbEnigmes);
		}
	}

	public void addIndice(Indice indice) {
		indicesForEnigma.add(indice);
	}

	public Indice getIndice(int idx) {
		return indicesForEnigma.get(idx);
	}

	public List<Indice> getAllIndices() {
		return allIndices;
	}
	public List<Indice> getAllInteractions() {
		return interactions;
	}
	public List<Enigme> getAllEnigmes() {
		enigmes.sort(enigmeComparator);
		return enigmes;
	}

	public Enigme getEnigmeByIndex(int index) {
		Enigme result = null;
		for(Enigme e : enigmes) {
			if (e.getIndex() == index) {
				result = e;
			}
		}
		return result;
	}
	
	public void loadIndices() {
		try {
			
			allIndices.clear();
			File enigmeFile = new File("src\\resources\\" + currentSalle.getPseudo() + "\\enigmes.properties");
			enigmesProps = new Properties();
			indicesForEnigma = new ArrayList<Indice>();
			if (enigmeFile.exists()) {
				FileReader fileReaderEnigme = new FileReader(enigmeFile);
				enigmesProps.load(fileReaderEnigme);
				
				for(int i = 0; i <= enigmeNb; i++)
				{
					File indiceFile = new File("src\\resources\\" + currentSalle.getPseudo() + "\\indices.properties");
					indicesProps = new Properties();
					
					FileReader fileReaderIndice = new FileReader(indiceFile);
					indicesProps.load(fileReaderIndice);
					Enigme currentEnigma =null;
					String name = enigmesProps.getProperty(i + DESC_PROP);
					String idx = enigmesProps.getProperty(i + INDEX_PROP);
					if (name != null)
					{
						currentEnigma = new Enigme(i, name, idx);
					}
						
						
					indicesForEnigma = new ArrayList<Indice>();
					if (indiceFile.exists())
					{
						FileReader fileReader = new FileReader(indiceFile);
						indicesProps.load(fileReader);
						
						for(int j = 1; j <= indiceNb; j++)
						{
							String link = indicesProps.getProperty(j + LINK_PROP);
							//System.out.println(link);
							if (link != null && Integer.parseInt(link.trim()) == i) {
								String desc = indicesProps.getProperty(j + DESC_PROP);
								if (desc != null)
								{
									String text = indicesProps.getProperty(j + TEXTE_PROP);
									String type = indicesProps.getProperty(j + TYPE_PROP);
									String color = indicesProps.getProperty(j + COLOR_PROP);
									String index = indicesProps.getProperty(j + INDEX_PROP);
									String img = null;
									String son = null;
									String function = null;
									String music = "false";
									if (Indice.TYPE_IMAGE.equals(type)) {
										img = indicesProps.getProperty(j + IMG_PROP);
									}
									if (Indice.TYPE_SON.equals(type))
									{
										son = indicesProps.getProperty(j + SON_PROP);
										music = indicesProps.getProperty(j + MUSIC_PROP);
									}
									if (Indice.TYPE_MODULE.equals(type))
									{
										function = indicesProps.getProperty(j + FUNCTION_PROP);
									}
									boolean bMusic = false;
									if ("true".equals(music)) {
										bMusic = true;
									}
									Indice current = new Indice(j, desc, img, text, son,type, color, index, function, bMusic, Integer.parseInt(link.trim()));
									indicesForEnigma.add(current);
									allIndices.add(current);
								}
							}
						}
						fileReader.close();
						if (currentEnigma != null) {
							currentEnigma.setIndices(indicesForEnigma);
						} else {
							if (indicesForEnigma.size() > 0) {
								interactions = indicesForEnigma;
							}
						}
					}
					else
					{
						indiceFile.createNewFile();
						currentSalle.getProperties().setProperty(SallesProperties.INDICES_NB, ""+0);
						FileWriter fileWriter = new FileWriter(currentSalle.getPropertyFile());
						currentSalle.getProperties().store(fileWriter, null);
						fileWriter.close();
					}
					
					fileReaderIndice.close();
					
					if (currentEnigma != null) {
						enigmes.add(currentEnigma);
					}
				}
				fileReaderEnigme.close();
				
			} else {
				enigmeFile.createNewFile();
				currentSalle.getProperties().setProperty(SallesProperties.ENIGMES_NB, ""+0);
				FileWriter fileWriter = new FileWriter(currentSalle.getPropertyFile());
				currentSalle.getProperties().store(fileWriter, null);
				fileWriter.close();
			}
			
			System.out.println("size " + indicesForEnigma.size());
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void writeIndiceInFile(Map<String, String> indicesTextToWrite, Map<String, String> indicesImgToWrite, Map<String, String> indicesSonToWrite)
	{
		try {
			Properties salleProps = currentSalle.getProperties();
			String fileName = currentSalle.getPseudo() + "\\indices.properties";
			//Ajout des indices texte
			for (String desc : indicesTextToWrite.keySet())
			{
				indiceNb++;
				indicesProps.setProperty(indiceNb + DESC_PROP, desc);
				indicesProps.setProperty(indiceNb + TEXTE_PROP, indicesTextToWrite.get(desc));
			}
			
			//Ajout des indices image
			for (String desc : indicesImgToWrite.keySet())
			{
				indiceNb++;
				indicesProps.setProperty(indiceNb + DESC_PROP, desc);
				indicesProps.setProperty(indiceNb + IMG_PROP, indicesImgToWrite.get(desc));
			}
			
			//Ajout des indices son
			for (String desc : indicesSonToWrite.keySet())
			{
				indiceNb++;
				indicesProps.setProperty(indiceNb + DESC_PROP, desc);
				indicesProps.setProperty(indiceNb + SON_PROP, indicesSonToWrite.get(desc));
			}
			
			indicesProps.store(new FileWriter(new File("src\\resources\\" + currentSalle.getPseudo() + fileName)), null);
			
			salleProps.setProperty(SallesProperties.INDICES_NB, "" + indiceNb);
			FileWriter fileWriter = new FileWriter(currentSalle.getPropertyFile());
			salleProps.store(fileWriter, null);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	
	public void reloadIndices()
	{
		indicesForEnigma.clear();
		enigmes.clear();
		loadIndices();
	}
	
	public void removeIndice(List<Indice> indiceToRemove)
	{
		try {
			indicesForEnigma.removeAll(indiceToRemove);
			Properties salleProps = currentSalle.getProperties();
			String fileName = "indices.properties";
			File file = new File("src\\resources\\" + fileName);
			file.delete();
			boolean newFile = file.createNewFile();
			
			if (newFile)
			{
				indicesProps = new Properties();
				int cpt = 1;
				for(Indice indice : indicesForEnigma)
				{
					indicesProps.setProperty(cpt + DESC_PROP, indice.getDescription());
					if (indice.getTexte() != null)
						indicesProps.setProperty(cpt + TEXTE_PROP, indice.getTexte());
					else
						indicesProps.setProperty(cpt + IMG_PROP, indice.getImageName());
					cpt++;
				}
				FileWriter fileWriter = new FileWriter(new File("src\\resources\\" + fileName));
				indicesProps.store(fileWriter, "Removing " + indiceToRemove.size() + " indices.");
				fileWriter.close();
				
				indiceNb = cpt;
			}
			
			salleProps.setProperty(SallesProperties.INDICES_NB, "" + indiceNb);
			FileWriter fileWriter = new FileWriter(currentSalle.getPropertyFile());
			salleProps.store(fileWriter, null);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateIndices(List<Indice> indicesToUpdate) {
		try {
			allIndices.clear();
			allIndices.addAll(indicesToUpdate);
			String filename = "src\\resources\\" + currentSalle.getPseudo() + "\\indices.properties";
			File file = new File(filename);
			System.out.println("PATH");
			System.out.println(file.getAbsolutePath());
			file.delete();
			boolean newFile = file.createNewFile();
			
			if (newFile)
			{
				indicesProps = new Properties();
				int cpt = 1;
				for (Indice indice : allIndices)
				{
					indicesProps.setProperty(cpt+INDEX_PROP, indice.getIndex()+"");
					indicesProps.setProperty(cpt+TYPE_PROP, indice.getType()+"");
					indicesProps.setProperty(cpt + DESC_PROP, indice.getDescription());
					indicesProps.setProperty(cpt+COLOR_PROP, indice.getColor()+"");
					indicesProps.setProperty(cpt+SON_PROP, indice.getSon()+"");
					indicesProps.setProperty(cpt+FUNCTION_PROP, indice.getFunction()+"");
					indicesProps.setProperty(cpt+LINK_PROP,indice.getLink()+"");
					System.out.println(indice.getIndex());
					System.out.println(indice.getType());
					System.out.println(indice.getDescription());
					System.out.println(indice.getTexte());
					if (Indice.TYPE_IMAGE.equals(indice.getType())) {
						indicesProps.setProperty(cpt + IMG_PROP, indice.getImageName());
					}
					else {
						String texte = "";
						if (indice.getTexte() != null) {
							texte = indice.getTexte();
						}
						indicesProps.setProperty(cpt + TEXTE_PROP, texte);
					}
					cpt++;
				}
				
				FileWriter fileWriter = new FileWriter(new File("src\\resources\\" + currentSalle.getPseudo() + "\\indices.properties"));
				indicesProps.store(fileWriter, null);
				fileWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateEnigmes(List<Enigme> enigmesToUpdate) {
		try {
			String filename = "src\\resources\\" + currentSalle.getPseudo() + "\\enigmes.properties";
			File file = new File(filename);
			file.delete();
			boolean newFile = file.createNewFile();
			
			if (newFile)
			{
				enigmesProps = new Properties();
				for (Enigme enigme : enigmesToUpdate)
				{
					enigmesProps.setProperty(enigme.getId() + DESC_PROP, enigme.getName());
					enigmesProps.setProperty(enigme.getId() + INDEX_PROP, enigme.getIndex()+"");
				}
				
				FileWriter fileWriter = new FileWriter(filename);
				enigmesProps.store(fileWriter, null);
				fileWriter.close();
				reloadIndices();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String indiceURL()
	{
		return currentSalle.getPseudo() + "\\";
	}
}
