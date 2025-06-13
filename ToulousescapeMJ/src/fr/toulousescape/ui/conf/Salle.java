package fr.toulousescape.ui.conf;

public class Salle {
	String surnom;
	public String getSurnom() {
		return surnom;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	String name;
	String image;
	
	@Override
	public String toString() {
		return " name " + name + " image " + image;
	}
}
