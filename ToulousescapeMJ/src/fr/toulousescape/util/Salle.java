package fr.toulousescape.util;

import java.io.File;
import java.util.Properties;

public class Salle {

	private String name;
	
	private Player musicPlayer;
	
	private Player indicePlayer;
	
	private Properties props;
	
	private File propertyFile;
	
	public Salle(String salleName, Properties properties, File pFile) {
		name = salleName;
		props = properties;
		propertyFile = pFile;
		musicPlayer = new Player();
		if ("true".equals(props.getProperty(SallesProperties.IS_AUDIO_INDICES)));
			indicePlayer = new Player();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Properties getProperties() {
		return props;
	}

	public File getPropertyFile() {
		return propertyFile;
	}

	public Player getMusicPlayer() {
		return musicPlayer;
	}

	public Player getIndicePlayer() {
		return indicePlayer;
	}
}
