package fr.toulousescape.util;

import java.util.Comparator;

public class EnigmeComparator implements Comparator<Enigme> {

	@Override
	public int compare(Enigme o1, Enigme o2) {
		
		return o1.getIndex() - o2.getIndex();
	}
}
