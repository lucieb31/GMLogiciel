package fr.toulousescape.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Session {

	private int indiceCount;
	
	private Date date;
	
	private int timeSpent;
	
	private int remainingTime;
	
	private List<String> indices;
	
	public Session() {
		indices = new ArrayList<>();
	}

	public int getIndiceCount() {
		return indiceCount;
	}

	public void setIndiceCount(int indiceCount) {
		this.indiceCount = indiceCount;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getTimeSpent() {
		return timeSpent;
	}

	public void setTimeSpent(int timeSpent) {
		this.timeSpent = timeSpent;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}
	
	public void addIndice(String indice)
	{
		indices.add(0, indice);
	}
	
	public void removeLastIndice()
	{
		indices.remove(0);
	}
	
	public void clearAllIndices()
	{
//		try {
//			File folderName = new File("src/indices");
//			if (!folderName.exists())
//			{
//				folderName.mkdirs();
//			}
//			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss");
//			String fileName = dateFormat.format(getDate()) + ".txt";
//
//			File f = new File(folderName.getAbsolutePath() + "\\" + fileName);
//
//			if (!f.exists()) {
//				f.createNewFile();
//				System.out.println(f.getAbsolutePath());
//			}
//
//			FileWriter fw = new FileWriter(f);
//			for (String i : indices) {
//				fw.write(i);
//				fw.write("\r\n");
//			}
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		indices.clear();
	}
	
	public String getAllIndices()
	{
		String indicesList = "Indices : ";
		
		for(String i : indices)
		{
			indicesList+= "\n" + i;
		}
		
		return indicesList;
	}
}
