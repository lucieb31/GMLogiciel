package fr.toulousescape.ui.conf;

import java.util.ArrayList;


public class EcranInfo {
	int nombre;
	ArrayList<Integer> decalage;
	Couleur couleur_fond;
	Couleur couleur_texte;
	
	public int getNombre() {
		return nombre;
	}

	public ArrayList<Integer> getDecalage() {
		return decalage;
	}

	public Couleur getCouleur_fond() {
		return couleur_fond;
	}

	public Couleur getCouleur_texte() {
		return couleur_texte;
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "nb: " + nombre + " decalage " + decalage.toString() + 
				" fond " + couleur_fond.toString() + " texte " + couleur_texte.toString();
	}
}
