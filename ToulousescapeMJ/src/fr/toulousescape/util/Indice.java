package fr.toulousescape.util;

import java.awt.Color;

import javax.swing.ImageIcon;

public class Indice {
	
	public final static String TYPE_TEXTE = "txt";
	public final static String TYPE_IMAGE = "img";
	public final static String TYPE_SON = "snd";
	public final static String TYPE_MODULE = "mod";

	private String description;
	
	private String image;
	
	private String texte;
	
	private String son;

	private boolean music;

	private String type;
	
	private String color;
	
	private String function;
	
	private int index;

	public Indice(String description, String image, String texte, String son, String type, String color, String index, String function, boolean music) {
		this.description = description;
		this.image = image;
		this.texte = texte;
		this.son = son;
		this.type = type;
		if (index != null) {
			this.index = new Integer(index);
		} else {
			this.index = 0;
		}
		this.color = color;
		if (function != null) {
			this.function = function;
		}
		this.music = music;
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

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
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
		System.out.println(IndiceManager.indiceURL() + son);
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isMusic() {
		return music;
	}

	public void setMusic(boolean music) {
		this.music = music;
	}


	
	
}
