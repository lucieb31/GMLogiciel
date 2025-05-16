package fr.toulousescape;

import java.util.ArrayList;

public class MainConfiguration {

	private TEcran ecran;
	private int extra_time;
	Musique musique;
	Salle salle;
	
	@Override
	public String toString() {
		
		return "ecran info: " + ecran.toString() + "\n extra " + extra_time + "\n musique info: " + musique.toString()
			+ "\n salle info: " + salle.toString();
	}
}

class TEcran {
	int nombre;
	ArrayList<Integer> decalage;
	Couleur couleur_fond;
	Couleur couleur_texte;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "nb: " + nombre + " decalage " + decalage.toString() + 
				" fond " + couleur_fond.toString() + " texte " + couleur_texte.toString();
	}
}

class Couleur {
	ArrayList<Integer> normal;
	ArrayList<Integer> fin;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return normal.toString() + " " + fin.toString();
	}
}

class Musique {
	
	String output;
	ArrayList<String> ambiance;
	String fin;
	String preambule;
	ArrayList<Debut> debut;
	ArrayList<Element> elements;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "output: " + output + "\n ambiance " + ambiance.toString() +
			"\n fin " + fin + "\n preambule " + preambule + "\n debut " + debut.toString()
			+ "\n elements " + elements.toString();
	}
}

class Debut {
	int nb_joueur;
	String fichier;
	
	@Override
	public String toString() {
		return "nb_joueur: " + nb_joueur + " fichier " + fichier;
	}
}

class Element {
	int time;
	String fichier;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return " time " + time + " fichier " + fichier;
	}
}

class Salle {
	String name;
	String image;
	
	@Override
	public String toString() {
		return " name " + name + " image " + image;
	}
}

