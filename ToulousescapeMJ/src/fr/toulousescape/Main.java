package fr.toulousescape;

import fr.toulousescape.ui.AudioOutputUI;
import fr.toulousescape.ui.EnigmesPanel;
import fr.toulousescape.ui.IndicesPanel;
import fr.toulousescape.ui.LoadConfig;
import fr.toulousescape.ui.LoadProperties;
import fr.toulousescape.ui.MainView;
import fr.toulousescape.ui.ManageSalleDialog;
import fr.toulousescape.ui.RoomPanel;
import fr.toulousescape.ui.RoomView;
import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.IndiceManager;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.SallesProperties;
import fr.toulousescape.util.Session;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

	public static void main(String[] args) {

		Logger logger = Logger.getLogger("MyLog");

		FileHandler fh;  

		try {  

			// This block configure the logger with handler and formatter  
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");  
			LocalDateTime now = LocalDateTime.now();  
			fh = new FileHandler("src/resources/LogicielGM_"+dtf.format(now)+".log");  
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

			// the following statement is used to log any messages  
			logger.info("My first log");  

		} catch (Exception e) {  
			e.printStackTrace();  
		}
		
		//TODO créer une session à chaque démarrage du chrono
		Session session = new Session();
		
		Salle salle = null;
		Path config_path = Paths.get("src/resources/configuration.json");
		LoadConfig config;
		if (Files.exists(config_path))
		{
			config = new LoadConfig(true);
			System.out.println("Json conf " + config.conf);
			salle = new Salle(config.conf.musique);
			
		} else {		
			config = new LoadConfig(true);
			if (config.isFirstStart() && config.getSelectedSalle() == null)
			{
				ManageSalleDialog createSalle = new ManageSalleDialog();
				createSalle.setModal(true);
				createSalle.openAsCreate();
				salle = createSalle.getCreatedSalle();
				config.setSelectedSalle(salle.getPseudo());
				config.setFirstStart(false);
			}
			else
			{
				LoadProperties properties = new LoadProperties(config.getSelectedSalle());
				salle = properties.getSalle();
			}
			Properties p = salle.getProperties();
			System.out.println(salle.getName() + " " + p.getProperty(SallesProperties.FIRST_START));
			config.conf.extra_time = Integer.parseInt(p.getProperty(SallesProperties.EXTRA_TIME,"0"));
			config.conf.musique.setOutput(p.getProperty(SallesProperties.MUSIC_OUTPUT));
			//TODO: loading info ecran
		}

		Chrono chrono = new Chrono(config.conf.extra_time);
		String outputMusic = config.conf.musique.getOutput();
		if (outputMusic == null) {
			new AudioOutputUI(salle);
		} else {
			salle.getMusicPlayer().setCurrentOut(String.valueOf(outputMusic));
			salle.getIndicePlayer().setCurrentOut(String.valueOf(outputMusic));
		}
		RoomPanel panel1 = new RoomPanel(chrono, config.conf.ecran);
		RoomPanel panel2 = new RoomPanel(chrono, config.conf.ecran);

		IndiceManager manager = new IndiceManager(salle);
		manager.loadIndices();

		EnigmesPanel enigmePanel = new EnigmesPanel(manager, session, salle);
		IndicesPanel indicePanel = new IndicesPanel(manager, session, salle);
		indicePanel.addListeners(panel1);
		indicePanel.addListeners(panel2);
		chrono.addTimerListener(indicePanel);
		enigmePanel.addListeners(indicePanel);

		new MainView(chrono, panel1, panel2, enigmePanel, indicePanel, session, salle, logger);
		
		//TODO: Gérer plus de 2 ecrans
		int nbRoomView = config.conf.ecran.getNombre();
		if (nbRoomView == 1)
		{
			int resolution = config.conf.ecran.getDecalage().get(0);
			new RoomView("Ecran 1", panel1, resolution);
		}
		else if (nbRoomView == 2)
		{
			int resolution = config.conf.ecran.getDecalage().get(0);
			new RoomView("Ecran 1", panel1, resolution);
			resolution = config.conf.ecran.getDecalage().get(1);
			new RoomView("Ecran 2", panel2, resolution);
		}
	}

	
	static public String printStringList(ArrayList musicList) {
		String str = "";
		int i = 1;
		for (Object music : musicList)
		{
			str = str + music.toString();
			
			if (i != musicList.size())
			{
				str = str + ";";
			}
		}
		return str;
	}
}
