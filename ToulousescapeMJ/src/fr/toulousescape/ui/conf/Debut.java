package fr.toulousescape.ui.conf;

public class Debut {
	int nb_joueur;
	String fichier;
	
	public Debut(int nb_joueur, String fichier) {
		super();
		this.nb_joueur = nb_joueur;
		this.fichier = fichier;
	}

	@Override
	public String toString() {
		return "nb_joueur: " + nb_joueur + " fichier " + fichier;
	}
}
