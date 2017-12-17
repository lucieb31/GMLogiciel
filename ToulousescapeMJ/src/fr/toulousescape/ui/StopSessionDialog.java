package fr.toulousescape.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import fr.toulousescape.util.Session;
import fr.toulousescape.util.ViewUtils;

public class StopSessionDialog extends JDialog {

	private Session finishedSession;
	
	
	public StopSessionDialog(Component parent, Session session) {
		finishedSession = session;
		
		setLocationRelativeTo(parent);
		setModal(true);
		setTitle("Fin de la session");
	}
	
	public void openDialog()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		JLabel date = new JLabel("Date : " + dateFormat.format(finishedSession.getDate()));
		mainPanel.add(date);
		
		JLabel tpsRestant = new JLabel("Temps restant : " + finishedSession.getRemainingTime() + "s");
		mainPanel.add(tpsRestant);
		
		JLabel tpsPasse = new JLabel("Tems passé : " + formatTime(finishedSession.getTimeSpent()));
		mainPanel.add(tpsPasse);
		
		JLabel nbIndices = new JLabel("Nombre d'indice : " + finishedSession.getIndiceCount());
		mainPanel.add(nbIndices);
		
		JLabel indices = new JLabel(finishedSession.getAllIndicesAsHTML());
		mainPanel.add(indices);

		JButton okButton = new JButton("OK");
		okButton.setEnabled(false);
		
		JPanel namePanel = new JPanel(new FlowLayout());
		ButtonGroup groupName = new ButtonGroup();
		List<String> gameMasterNames = ViewUtils.getGameMasterNames();
		for (String name : gameMasterNames)
		{
			JRadioButton b = new JRadioButton(name);
			b.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					okButton.setEnabled(true);
					finishedSession.setGm(b.getText());
				}
			});
			groupName.add(b);
			namePanel.add(b);
		}
		mainPanel.add(namePanel);
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				writeIndiceFile();
				dispose();
			}
		});
		mainPanel.add(okButton);
		add(mainPanel);
		pack();
		setVisible(true);
	}
	
	private String formatTime(int timeToFormat) {
		// Calculate current time
		int hour = timeToFormat / 3600;
		int minutes = (timeToFormat % 3600) / 60;
		int secondes = ((timeToFormat - minutes * 60) % 60);

		String s = String.valueOf(secondes);

		if (secondes < 10) {
			s = "0" + s;
		}

		String m = String.valueOf(minutes);
		if (minutes < 10) {
			m = "0" + m;
		}

		if (hour > 0) {
			return hour + ":" + m + ":" + s;
		} else if (minutes > 0) {
			return m + ":" + s;
		} else {
			return s + "'";
		}
	}
	
	private void writeIndiceFile()
	{
		try {
			File folderName = new File("src/indices/" + finishedSession.getGm());
			if (!folderName.exists()) {
				folderName.mkdirs();
			}
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
			String fileName = dateFormat.format(new Date()) + ".txt";

			File f = new File(folderName.getAbsolutePath() + "\\" + fileName);

			if (!f.exists()) {
				f.createNewFile();
				System.out.println(f.getAbsolutePath());
			}

			FileWriter fw = new FileWriter(f, true);
			for (String i : finishedSession.getIndices()) {
				fw.write(i);
				fw.write("\r\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
