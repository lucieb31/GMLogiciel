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

	private List<Indice> indices;
	
	private Salle currentSalle;
	
	private Properties indicesProps;
	
	private int indiceNb;
	
	private static final String DESC_PROP = ".desc";
	
	private static final String TEXTE_PROP = ".text";
	
	private static final String IMG_PROP = ".img";

	public IndiceManager(Salle salle) {
		indices = new ArrayList<>();
		currentSalle = salle;
		indiceNb = Integer.valueOf(currentSalle.getProperties().getProperty(SallesProperties.INDICES_NB));
	}

	public void addIndice(Indice indice) {
		indices.add(indice);
	}

	public Indice getIndice(int idx) {
		return indices.get(idx);
	}

	public List<Indice> getAllIndices() {
		return indices;
	}

	public void loadIndices() {
		SAXBuilder sxb = new SAXBuilder();
		try {
			
			String fileName = currentSalle.getProperties().getProperty(SallesProperties.INDICES_PROP_FILE);
			if (fileName != null)
			{
				File indiceFile = new File("src\\resources\\" + fileName);
				indicesProps = new Properties();
				FileReader fileReader = new FileReader(indiceFile);
				indicesProps.load(fileReader);
				
				for(int i = 1; i <= indiceNb; i++)
				{
					String desc = indicesProps.getProperty(i + DESC_PROP);
					if (desc != null)
					{
						String text = indicesProps.getProperty(i + TEXTE_PROP);
						String img = null;
						if (text == null) {
							img = indicesProps.getProperty(i + IMG_PROP);
						}
						Indice current = new Indice(desc, img, text);
						indices.add(current);
					}
				}
				fileReader.close();
			}
			
			String hasBeenTransfered = currentSalle.getProperties().getProperty(SallesProperties.INDICES_TRANSFERED);
			if (hasBeenTransfered == null)
			{
				Map<String, String> indicesTextToTransfert = new HashMap<>();
				Map<String, String> indicesImgToTransfert = new HashMap<>();
				//Load xml file
				String xmlFileName = currentSalle.getProperties().getProperty(SallesProperties.INDICES_XML_FILE);
				if (xmlFileName != null) {
					Document document = sxb.build(new File("src\\resources\\" + xmlFileName));

					Element rootElement = document.getRootElement();
					List<Element> children = rootElement.getChildren();

					for (Element i : children) {
						Element child = i.getChildren().get(0);
						String desc = i.getAttributeValue("desc");
						String text = null;
						String img = null;
						if ("text".equals(child.getName())) {
							text = child.getText();
							indicesTextToTransfert.put(desc, text);
						} else if ("img".equals(child.getName())) {
							img = child.getText();
							indicesImgToTransfert.put(desc, img);
						}
						Indice current = new Indice(desc, img, text);
						indices.add(current);

					}
				}
				
				//Transfer xml to properties file
				writeIndiceInFile(indicesTextToTransfert, indicesImgToTransfert);
				Properties salleProps = currentSalle.getProperties();
				salleProps.setProperty(SallesProperties.INDICES_TRANSFERED, "true");
				FileWriter fileWriter = new FileWriter(currentSalle.getPropertyFile());
				salleProps.store(fileWriter, null);
				fileWriter.close();
			}
			System.out.println("size " + indices.size());

		} catch (IOException | JDOMException e) {
			e.printStackTrace();
		}
	}
	
	public void writeIndiceInFile(Map<String, String> indicesTextToWrite, Map<String, String> indicesImgToWrite)
	{
		try {
			Properties salleProps = currentSalle.getProperties();
			String fileName = salleProps.getProperty(SallesProperties.INDICES_PROP_FILE);
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
			
			indicesProps.store(new FileWriter(new File("src\\resources\\" + fileName)), null);
			
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
		indices.clear();
		loadIndices();
	}
	
	public void removeIndice(List<Indice> indiceToRemove)
	{
		try {
			indices.removeAll(indiceToRemove);
			Properties salleProps = currentSalle.getProperties();
			String fileName = salleProps.getProperty(SallesProperties.INDICES_PROP_FILE);
			File file = new File("src\\resources\\" + fileName);
			file.delete();
			boolean newFile = file.createNewFile();
			
			if (newFile)
			{
				indicesProps = new Properties();
				int cpt = 1;
				for(Indice indice : indices)
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
			indices.removeAll(indicesToUpdate);
			indices.addAll(indicesToUpdate);
			Properties salleProps = currentSalle.getProperties();
			String fileName = salleProps.getProperty(SallesProperties.INDICES_PROP_FILE);
			File file = new File("src\\resources\\" + fileName);
			file.delete();
			boolean newFile = file.createNewFile();
			
			if (newFile)
			{
				indicesProps = new Properties();
				int cpt = 1;
				for (Indice indice : indices)
				{
					indicesProps.setProperty(cpt + DESC_PROP, indice.getDescription());
					if (indice.getTexte() != null)
						indicesProps.setProperty(cpt + TEXTE_PROP, indice.getTexte());
					else
						indicesProps.setProperty(cpt + IMG_PROP, indice.getImageName());
					cpt++;
				}
				
				FileWriter fileWriter = new FileWriter(new File("src\\resources\\" + fileName));
				indicesProps.store(fileWriter, null);
				fileWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
