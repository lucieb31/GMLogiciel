package fr.toulousescape.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import fr.toulousescape.util.Session;
import fr.toulousescape.util.ViewUtils;

public class StopSessionDialog extends JDialog {

	private Session finishedSession;
	private JTextField bonusField;
	private JTextField timeField;
	private JTextArea alertField;
	private JRadioButton perduButton;
	private JRadioButton gagneButton;
	private JTextField JrealIndicesField;
	private JTextField realIndicesField;
	
	
	public StopSessionDialog(Component parent, Session session) {
		finishedSession = session;
		
		setLocationRelativeTo(null);
		setModal(true);
		setTitle("Fin de la session");
	}
	
	public void openDialog()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//		mainPanel.setPreferredSize(new Dimension(600, 600));

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		JLabel infos = new JLabel("Date : " + dateFormat.format(finishedSession.getDate())
		+ " - "+"Temps restant : " + finishedSession.getRemainingTime() + "s"
		+ " - "+"Temps passé : " + formatTime(finishedSession.getTimeSpent()));
		mainPanel.add(infos);
				
		JLabel indices = new JLabel(finishedSession.getAllIndicesAsHTML());
		mainPanel.add(indices);

		JPanel indicesPanel = new JPanel(new FlowLayout());
		JLabel realIndicesLabel = new JLabel("Indices à comptabiliser :");
		realIndicesField = new JTextField(4);
		realIndicesField.setText(""+finishedSession.getIndiceCount());
		indicesPanel.add(realIndicesLabel);
		indicesPanel.add(realIndicesField);
		mainPanel.add(indicesPanel);

		JPanel victoryPanel = new JPanel(new FlowLayout());
		ButtonGroup victoryGroup = new ButtonGroup();
		perduButton = new JRadioButton("Perdu");
		gagneButton = new JRadioButton("Gagné");
		victoryPanel.add(perduButton);
		victoryPanel.add(gagneButton);
		victoryGroup.add(perduButton);
		victoryGroup.add(gagneButton);
		
		if (finishedSession.getRemainingTime() <= 0) {
			perduButton.setSelected(true);
		} else {
			gagneButton.setSelected(true);
		}
		
		mainPanel.add(victoryPanel);
		
		JPanel timePanel = new JPanel(new FlowLayout());
		JLabel timeLabel = new JLabel("Secondes restantes");
		timeField = new JTextField(finishedSession.getRemainingTime()+"");
		timePanel.add(timeLabel);
		timePanel.add(timeField);
		mainPanel.add(timePanel);
		
		JPanel bonusPanel = new JPanel(new FlowLayout());
		JLabel bonusLabel = new JLabel("Bonus (P = partition, S = sauver le monde, X = trésors trouvés");
		bonusField = new JTextField(4);
		bonusPanel.add(bonusLabel);
		bonusPanel.add(bonusField);
		mainPanel.add(bonusPanel);

		
		JPanel alertPanel = new JPanel(new FlowLayout());
		JLabel alertLabel = new JLabel("Incidents :");
		alertField = new JTextArea(2,30);
		alertPanel.add(alertLabel);
		alertPanel.add(alertField);
		mainPanel.add(alertPanel);
		JButton okButton;
		if (finishedSession.getId() != 0) {
			okButton = new JButton("Clore la session");		
		} else {
			okButton = new JButton("Fermer (attention à clore la session sur le site)");
		}
		okButton.setEnabled(true);
		
//		JPanel namePanel = new JPanel(new FlowLayout());
//		ButtonGroup groupName = new ButtonGroup();
//		List<String> gameMasterNames = ViewUtils.getGameMasterNames();
//		for (String name : gameMasterNames)
//		{
//			JRadioButton b = new JRadioButton(name);
//			b.addActionListener(new ActionListener() {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					okButton.setEnabled(true);
//					finishedSession.setGm(b.getText());
//				}
//			});
//			groupName.add(b);
//			namePanel.add(b);
//		}
//		mainPanel.add(namePanel);
		
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sendDataToWebsite();
				//writeIndiceFile();
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
	
//	private void writeIndiceFile()
//	{
//		try {
//			File folderName = new File("src/indices/" + finishedSession.getGm());
//			if (!folderName.exists()) {
//				folderName.mkdirs();
//			}
//			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
//			String fileName = dateFormat.format(new Date()) + ".txt";
//
//			File f = new File(folderName.getAbsolutePath() + "\\" + fileName);
//
//			if (!f.exists()) {
//				f.createNewFile();
//				System.out.println(f.getAbsolutePath());
//			}
//
//			FileWriter fw = new FileWriter(f, true);
//			for (String i : finishedSession.getIndices()) {
//				fw.write(i);
//				fw.write("\r\n");
//			}
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void sendDataToWebsite() {
		if (finishedSession.getId() != 0) {
		  try {

				LoadConfig config = new LoadConfig();
				String room = config.getSelectedSalle();
				
				URL url = new URL("https://www.toulousescape.fr/process/wsend?room="+room);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				Map<String, String> postParams = new LinkedHashMap<>();
				postParams.put("txtUserName", "mUsername");
				postParams.put("session", ""+finishedSession.getId());
				postParams.put("clues", realIndicesField.getText());
				postParams.put("victory", gagneButton.isSelected()?"1":"0");
				postParams.put("remaining", timeField.getText());
				postParams.put("bonus", bonusField.getText());
				postParams.put("flow", finishedSession.getAllIndicesAsHTML());
				postParams.put("alert", alertField.getText());
				byte[] postDataBytes = generatePostData(postParams);
		        
			    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
		        conn.getOutputStream().write(postDataBytes);
		        

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				String output;
				String all ="";
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					all += output;
				}
				System.out.println(all);
			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }
		}
	}
	
	
	public byte[] generatePostData(Map<String, String> postParams) 
    {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> dParam : postParams.entrySet())
        {
            if (postData.length() != 0) 
                postData.append('&');
            try {
                postData.append(URLEncoder.encode(dParam.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(dParam.getValue()), "UTF-8"));
            } 
            catch (UnsupportedEncodingException e) 
            {
                e.printStackTrace();
            }
        }
        byte[] postDataBytes = null;
        try 
        {
            postDataBytes = postData.toString().getBytes("UTF-8");
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        }
        return postDataBytes;
    }

}
