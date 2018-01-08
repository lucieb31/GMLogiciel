package fr.toulousescape.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Salle {

	private String name;
	
	private Player musicPlayer;
	
	private Player indicePlayer;
	
	private Properties props;
	
	private File propertyFile;
	
	private boolean hasAudioIndice;
	
	private String ambianceMusique;
	
	private String finalMusic;
	
	private int nbEcran;
	
	private List<Integer> resolutionEcrans;
	
	private String pseudo;
	
	public Salle(String pseudoN, Properties properties, File pFile) {
		pseudo = pseudoN;
		props = properties;
		propertyFile = pFile;
		musicPlayer = new Player();
		loadPropFile();
	}
	
	public Salle(String salleName, String pseudo)
	{
		try {
			this.pseudo = pseudo;
			name = salleName;
			props = new Properties();
			File salleFolder = new File("src\\resources\\"+pseudo);
			salleFolder.mkdirs();
			propertyFile = new File("src\\resources\\"+pseudo+".properties");
			propertyFile.createNewFile();
			musicPlayer = new Player();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void setNbEcran(int ecran) {
		nbEcran = ecran;
	}

	public boolean isHasAudioIndice() {
		return hasAudioIndice;
	}

	public void setHasAudioIndice(boolean hasAudioIndice) {
		this.hasAudioIndice = hasAudioIndice;
		if (hasAudioIndice)
			indicePlayer = new Player();
	}

	public String getAmbianceMusique() {
		return ambianceMusique;
	}

	public void setAmbianceMusique(String ambianceMusique) {
		this.ambianceMusique = ambianceMusique;
	}

	public String getFinalMusic() {
		return finalMusic;
	}

	public void setFinalMusic(String finalMusic) {
		this.finalMusic = finalMusic;
	}

	public int getNbEcran() {
		return nbEcran;
	}

	public void setResolutionEcrans(List<Integer> resolutions) {
		resolutionEcrans = resolutions;
	}

	public void computePropFile() {
		try {
			props.setProperty(SallesProperties.NAME, name);
			props.setProperty(SallesProperties.IS_AUDIO_INDICES, ""+hasAudioIndice);
			props.setProperty(SallesProperties.NB_ECRAN, ""+nbEcran);
			if (nbEcran != 0)
			{
				for(int i = 0; i < nbEcran; i++)
				{
					props.setProperty(SallesProperties.ECRAN_RESOLUTION + "." + (i+1), ""+resolutionEcrans.get(i));
				}
			}
			props.setProperty(SallesProperties.MUSIC_FINAL, finalMusic);
			props.setProperty(SallesProperties.MUSIC_TO_PLAY, ambianceMusique);
			FileWriter writer = new FileWriter(propertyFile);
			props.store(writer, "Create salle");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadPropFile()
	{
		name = props.getProperty(SallesProperties.NAME);
		hasAudioIndice = Boolean.parseBoolean(props.getProperty(SallesProperties.IS_AUDIO_INDICES));
		if (hasAudioIndice)
			indicePlayer = new Player();
		finalMusic = props.getProperty(SallesProperties.MUSIC_FINAL);
		ambianceMusique = props.getProperty(SallesProperties.MUSIC_TO_PLAY);
		nbEcran = Integer.parseInt(props.getProperty(SallesProperties.NB_ECRAN));
		if (nbEcran != 0)
		{
			resolutionEcrans = new ArrayList<>();
			for (int i = 1; i <= nbEcran; i++) {
				int res = Integer.parseInt(props.getProperty(SallesProperties.ECRAN_RESOLUTION + "." + i));
				resolutionEcrans.add(res);
			}
		}
	}

	public String getPseudo() {
		return pseudo;
	}
}
