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
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
		if (Files.exists(Paths.get("configuration.json")))
		{
			
		} else {		
			LoadConfig config = new LoadConfig();
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
		}

		Properties p = salle.getProperties();
		System.out.println(salle.getName() + " " + p.getProperty(SallesProperties.FIRST_START));
		Chrono chrono = new Chrono(Integer.parseInt(p.getProperty(SallesProperties.EXTRA_TIME,"0")));
		String outputMusic = p.getProperty(SallesProperties.MUSIC_OUTPUT);
		if (outputMusic == null) {
			new AudioOutputUI(salle);
		} else {
			salle.getMusicPlayer().setCurrentOut(String.valueOf(outputMusic));
			salle.getIndicePlayer().setCurrentOut(String.valueOf(outputMusic));
		}
		RoomPanel panel1 = new RoomPanel(chrono,p);
		RoomPanel panel2 = new RoomPanel(chrono,p);

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
		int nbRoomView = Integer.parseInt(p.getProperty(SallesProperties.NB_ECRAN));
		if (nbRoomView == 1)
		{
			int resolution = Integer.parseInt(p.getProperty(SallesProperties.ECRAN_RESOLUTION + ".1"));
			new RoomView("Ecran 1", panel1, resolution);
		}
		else if (nbRoomView == 2)
		{
			int resolution = Integer.parseInt(p.getProperty(SallesProperties.ECRAN_RESOLUTION + ".1"));
			new RoomView("Ecran 1", panel1, resolution);
			resolution = Integer.parseInt(p.getProperty(SallesProperties.ECRAN_RESOLUTION + ".2"));
			new RoomView("Ecran 2", panel2, resolution);
		}
	}

}
