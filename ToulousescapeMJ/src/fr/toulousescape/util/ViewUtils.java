package fr.toulousescape.util;

import java.util.ArrayList;
import java.util.List;

public class ViewUtils {


	public static List<String> getGameMasterNames()
	{
		List<String> gms = new ArrayList<>();
		gms.add(new GameMaster("Barthas", "Jérôme").getNameToDisplay());
		gms.add(new GameMaster("Durbize", "Sandrine").getNameToDisplay());
		gms.add(new GameMaster("Legrand", "Maxime").getNameToDisplay());
		gms.add(new GameMaster("Derioz", "Thibault").getNameToDisplay());
		gms.add(new GameMaster("Roche", "Thibault").getNameToDisplay());
		
		return gms;
	}
}
