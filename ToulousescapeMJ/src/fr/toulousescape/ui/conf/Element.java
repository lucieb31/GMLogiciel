package fr.toulousescape.ui.conf;

public class Element {
	int time;
	public int getTime() {
		return time;
	}

	public String getFichier() {
		return fichier;
	}

	String fichier;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return " time " + time + ", fichier " + fichier;
	}
}
