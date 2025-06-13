package fr.toulousescape.ui.conf;

import java.util.ArrayList;

public class Couleur {
	ArrayList<Integer> normal;
	public ArrayList<Integer> getNormal() {
		return normal;
	}

	public ArrayList<Integer> getFin() {
		return fin;
	}

	ArrayList<Integer> fin;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return normal.toString() + " " + fin.toString();
	}
}
