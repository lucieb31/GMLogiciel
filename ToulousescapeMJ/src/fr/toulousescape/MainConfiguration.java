package fr.toulousescape;

import fr.toulousescape.ui.conf.*;

public class MainConfiguration {

	public EcranInfo ecran;
	public int extra_time;
	public Musique musique;
	public Salle salle;
	
	@Override
	public String toString() {
		
		return "ecran info: " + ecran.toString() + "\n extra " + extra_time + "\n musique info: " + musique.toString()
			+ "\n salle info: " + salle.toString();
	}
}

