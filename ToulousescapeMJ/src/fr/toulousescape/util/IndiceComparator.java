package fr.toulousescape.util;

import java.util.Comparator;

public class IndiceComparator implements Comparator<Indice> {

	@Override
	public int compare(Indice o1, Indice o2) {
		
		return o1.getIndex() - o2.getIndex();
	}
}
