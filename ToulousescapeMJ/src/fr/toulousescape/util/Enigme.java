package fr.toulousescape.util;

import java.util.List;

public class Enigme {
	
	private IndiceComparator indiceComparator = new IndiceComparator();
	private String name;
	private List<Indice> indices;
	private int index;
	public Enigme(String name, String index2) {
		this.name = name;
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
	
	
}
