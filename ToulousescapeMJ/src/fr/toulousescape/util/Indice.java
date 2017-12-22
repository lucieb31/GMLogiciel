package fr.toulousescape.util;

import javax.swing.ImageIcon;

public class Indice {

	private String description;
	
	private String image;
	
	private String texte;
	
	private String son;
	

	public Indice(String description, String image, String texte, String son) {
		this.description = description;
		this.image = image;
		this.texte = texte;
		this.son = son;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageIcon getImage() {
		return new ImageIcon("src\\resources\\" + image);
	}

	public String getTexte() {
		return texte;
	}
	
	public void setTexte(String texte) {
		this.texte = texte;
	}
	
	public String getImageName()
	{
		return image;
	}
	
	public void setImageName(String name)
	{
		image = name;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getSon() {
		return son;
	}

	public void setSon(String son) {
		this.son = son;
	}
}
