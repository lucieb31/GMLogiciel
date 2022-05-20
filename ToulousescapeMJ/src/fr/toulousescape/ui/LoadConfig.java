package fr.toulousescape.ui;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import fr.toulousescape.util.SallesProperties;


public class LoadConfig {

	private boolean firstStart;
	
	private List<String> salles;
	
	private File configFile;
	
	private String selectedSalle;

	private Properties props;
	
	public LoadConfig()
	{
		try {
			configFile = new File("src\\resources\\config.properties");
			FileReader reader = new FileReader(configFile);
			props = new Properties();
			props.load(reader);
			
			firstStart = Boolean.parseBoolean(props.getProperty(SallesProperties.FIRST_START));
			
			//TODO sera peut être nécessaire si on veut gérer plusieurs salles dans un seul logiciel
//			int nbSalle = Integer.parseInt(props.getProperty(SallesProperties.NB_SALLE));
//
//			salles = new ArrayList<>();
//			for (int i = 1; i <= nbSalle + 1; i++)
//			{
//				String sName = props.getProperty("salle."+i);
//				salles.add(sName);
//			}
			selectedSalle = props.getProperty("salle.selected");
			reader.close();
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isFirstStart() {
		return firstStart;
	}

	public void setFirstStart(boolean firstStart) {
		this.firstStart = firstStart;
		props.setProperty(SallesProperties.FIRST_START, "" + firstStart);
		writeProperties();
	}

	public List<String> getSalles() {
		return salles;
	}

	public String getSelectedSalle() {
		return selectedSalle;
	}

	public void setSelectedSalle(String selectedSalle) {
		this.selectedSalle = selectedSalle;
		props.setProperty("salle.selected", selectedSalle);
		writeProperties();
	}
	
	private void writeProperties()
	{
		try {
			FileWriter writer = new FileWriter(configFile);
			props.store(writer, "");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
