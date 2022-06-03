package fr.toulousescape;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
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

public class Main {

	public static void main(String[] args) {

		File file = new File("src\\resources\\log4j2.xml");
	    
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(file.toURI());
		//Configurator.initialize("Test", "src\\resources\\log4j2.properties");
		PropertyConfigurator.configure("src\\resources\\log4j2.properties");
		Logger logger =  LogManager.getLogger(Main.class);
		try {
			logger.info("Start application");
			//System.setProperty("log4j.configurationFile", "../src/resources/log4j.properties");
			//TODO créer une session à chaque démarrage du chrono
			Session session = new Session();
			
			LoadConfig config = new LoadConfig();
			
			Salle salle;
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
			Chrono chrono = new Chrono(new Integer(p.getProperty(SallesProperties.EXTRA_TIME,"0")));
			String outputMusic = p.getProperty(SallesProperties.MUSIC_OUTPUT);
			//String outputClues = p.getProperty(SallesProperties.INDICES_OUTPUT);
			if (outputMusic == null) {
				new AudioOutputUI(salle);
			} else {
				logger.info("Set output to " + outputMusic);
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
	
			new MainView(chrono, panel1, panel2, enigmePanel, indicePanel, session, salle);
			
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
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
	}

}
