package fr.toulousescape.ui.conf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Musique {
	String output;
	ArrayList<String> ambiance;
	String fin;
	String preambule;
	ArrayList<Debut> debut;
	public ArrayList<Element> elements;
	
	public void setAmbiance(ArrayList<String> ambiance) {
		this.ambiance = ambiance;
	}

	public void setFin(String fin) {
		this.fin = fin;
	}

	public void setPreambule(String preambule) {
		this.preambule = preambule;
	}

	public void setDebut(ArrayList<Debut> debut) {
		this.debut = debut;
	}

	public void setElements(ArrayList<Element> elements) {
		this.elements = elements;
	}

	public String getOutput() {
		return output;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}

	public ArrayList<String> getAmbiance() {
		return ambiance;
	}

	public String getFin() {
		return fin;
	}

	public String getPreambule() {
		return preambule;
	}

	public Map<Integer, String> getDebut() {
		Map<Integer, String> debutMap = new HashMap<Integer, String>();
		for (Debut d : debut)
		{
			debutMap.put(d.nb_joueur, d.fichier);
		}
		return debutMap;
	}

	public Map<Integer,String> getElements() {
		Map<Integer,String> elementMap = new HashMap<Integer, String>();
		for (Element e : elements)
		{
			elementMap.put(e.time, e.fichier);
		}
		return elementMap;
	}


	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "output: " + output + "\n ambiance " + ambiance.toString() +
			"\n fin " + fin + "\n preambule " + preambule + "\n debut " + debut.toString()
			+ "\n elements " + elements.toString();
	}
}
