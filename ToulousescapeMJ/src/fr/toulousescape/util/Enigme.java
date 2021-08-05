package fr.toulousescape.util;

import java.util.List;

public class Enigme {
	
	private IndiceComparator indiceComparator = new IndiceComparator();
	private String name;
	private List<Indice> indices;
	private int index;
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Enigme(int id, String name, String index2) {
		this.name = name;
		this.id = id;
		if (index2 != null) {
			this.index = new Integer(index2);
		} else {
			index2 = null;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Indice> getIndices() {
		return indices;
	}

	public void setIndices(List<Indice> indices) {
		indices.sort(indiceComparator);
		this.indices = indices;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
	public String toString() {
		return this.name + "/"+this.index;
	}
}
