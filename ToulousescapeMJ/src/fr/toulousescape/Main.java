package fr.toulousescape;

import java.util.Properties;

import fr.toulousescape.ui.AudioOutputUI;
import fr.toulousescape.ui.IndicesPanel;
import fr.toulousescape.ui.LoadProperties;
import fr.toulousescape.ui.MainView;
import fr.toulousescape.ui.RoomPanel;
import fr.toulousescape.ui.RoomView;
import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.IndiceManager;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.SallesProperties;
import fr.toulousescape.util.Session;

public class Main {

	public static void main(String[] args) {

		Session session = new Session();
		Chrono chrono = new Chrono();
		Player player = new Player();
		
		LoadProperties properties = new LoadProperties();
		
		Salle salle = properties.getSalle();
		
		Properties p = salle.getProperties();
		System.out.println(salle.getName() + " " + p.getProperty(SallesProperties.FIRST_START));
		String output = p.getProperty(SallesProperties.OUTPUT);
		if (output == null)
			new AudioOutputUI(player, salle);
		else
			player.setCurrentOut(Integer.valueOf(output));
		
		RoomPanel panel1 = new RoomPanel(chrono);
		RoomPanel panel2 = new RoomPanel(chrono);

		IndiceManager manager = new IndiceManager(salle);
		manager.loadIndices();

		IndicesPanel indicePanel = new IndicesPanel(manager, session, player);
		indicePanel.addListeners(panel1);
		indicePanel.addListeners(panel2);

		new MainView(chrono, panel1, panel2, indicePanel, session, player, salle);
		
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
