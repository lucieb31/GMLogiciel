package fr.toulousescape.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import fr.toulousescape.Main;
import fr.toulousescape.ui.conf.Debut;
import fr.toulousescape.ui.conf.Musique;

/**
 * @author Toulousescape
 *
 */
public class Salle {

	private String name;
	
	private Player musicPlayer;
	
	private Player indicePlayer;
	
	private Properties props;
	
	private File propertyFile;
	
	private Musique musique;
	
//	private String preambuleMusic;

//	private String ambianceMusic;

//	private Map<Integer,String> beginMusic;

//	private String elementsMusic;
	
//	private String finalMusic;
	
	private int nbEcran;
	
	private List<Integer> resolutionEcrans;
	
	private String pseudo;
	
	public Salle(String pseudoN, Properties properties, File pFile) {
		this();
		pseudo = pseudoN;
		props = properties;
		propertyFile = pFile;
		loadPropFile();
	}
	
	public Salle(String salleName, String pseudo)
	{
		this();
		try {
			this.pseudo = pseudo;
			name = salleName;
			props = new Properties();
			File salleFolder = new File("src\\resources\\"+pseudo);
			salleFolder.mkdirs();
			propertyFile = new File("src\\resources\\"+pseudo+".properties");
			propertyFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Salle()
	{
		musicPlayer = new Player();
		indicePlayer = new Player();
	}
	

	public Salle(Musique musique) {
		super();
		this.musique = musique;
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

	public ArrayList<String> getAmbianceMusique() {
		return musique.getAmbiance();
	}

	public String getPreambuleMusic() {
		return musique.getPreambule();
	}

	public String getFinalMusic() {
		return musique.getFin();
	}

	public int getNbEcran() {
		return nbEcran;
	}

	public void setResolutionEcrans(List<Integer> resolutions) {
		resolutionEcrans = resolutions;
	}

	public void computePropFile() {
		System.out.println("COMPUTE PROP FILE");
		try {
			props.setProperty(SallesProperties.NAME, name);
			props.setProperty(SallesProperties.NB_ECRAN, ""+nbEcran);
			if (nbEcran != 0)
			{
				for(int i = 0; i < nbEcran; i++)
				{
					props.setProperty(SallesProperties.ECRAN_RESOLUTION + "." + (i+1), ""+resolutionEcrans.get(i));
				}
			}
			props.setProperty(SallesProperties.MUSIC_END, musique.getFin());
			System.out.println("PROPERTIES : "+SallesProperties.MUSIC_TO_PLAY_BEFORE+" : "+musique.getPreambule());
			props.setProperty(SallesProperties.MUSIC_TO_PLAY_BEFORE, musique.getPreambule());
			props.setProperty(SallesProperties.MUSIC_TO_PLAY, Main.printStringList(musique.getAmbiance()));
			props.setProperty(SallesProperties.MUSIC_BEGIN, musique.getDebut().get(0));
			for (int i = 2; i < 7 ; i++) {
				props.setProperty(SallesProperties.MUSIC_BEGIN+"."+i, musique.getDebut().get(i));	
			}
			props.setProperty(SallesProperties.MUSIC_ELEMENTS, Main.printStringList(musique.elements));
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
		musique.setFin(props.getProperty(SallesProperties.MUSIC_END));
		ArrayList<Debut> beginList = new ArrayList<>();
		Debut defaultDebut = new Debut(0, props.getProperty(SallesProperties.MUSIC_BEGIN));
		beginList.add(defaultDebut);
		for (int i = 2 ; i < 7 ; i++) {
			Debut nextDebut = new Debut(i, props.getProperty(SallesProperties.MUSIC_BEGIN+"."+i));
			beginList.add(nextDebut);
		}
		// TODO: à modifier;
//		elementsMusic = props.getProperty(SallesProperties.MUSIC_ELEMENTS);
		musique.setPreambule(props.getProperty(SallesProperties.MUSIC_TO_PLAY_BEFORE));
//		ambianceMusic = props.getProperty(SallesProperties.MUSIC_TO_PLAY);
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

	public String getBeginMusic(int players) {
		if (musique.getDebut().containsKey(players)) {
			return musique.getDebut().get(players);
		}
		return musique.getDebut().get(4);
	}

	public Map<Integer,String> getElementsMusic() {
		return musique.getElements();
	}
}
