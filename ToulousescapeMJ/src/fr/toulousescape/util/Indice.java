package fr.toulousescape.util;

import javax.swing.ImageIcon;

public class Indice {
	
	public final static String TYPE_TEXTE = "txt";
	public final static String TYPE_IMAGE = "img";
	public final static String TYPE_SON = "snd";

	private String description;
	
	private String image;
	
	private String texte;
	
	private String son;

	private String type;

	public Indice(String description, String image, String texte, String son, String type) {
		this.description = description;
		this.image = image;
		this.texte = texte;
		this.son = son;
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ImageIcon getImage() {
		return new ImageIcon("src\\resources\\" + IndiceManager.indiceURL() + image);
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
	
	public String getSonWithUrl()
	{
		return IndiceManager.indiceURL() + son;
	}

	public String getType() {
		if (type == null) {
			// Default
			return TYPE_TEXTE;			
		} else {
			return type;
		}
	}

	public void setType(String type) {
		if (type == null) {
			this.type = TYPE_TEXTE;
		} else {
			this.type = type;			
		}
	}
	
}
