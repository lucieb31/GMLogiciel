package fr.toulousescape.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import fr.toulousescape.util.Salle;
import fr.toulousescape.util.SallesProperties;

public class LoadProperties extends JDialog {
	private static final long serialVersionUID = 1774246272215109108L;

	private JComboBox<String> sallesName;

	private JPanel chooseSallePanel;

	private Salle salle;

	private static final String DameRouge = "Dame Rouge";

	private static final String Atelier = "Atelier du musicien";

	private static final String Cirque = "Black circus";

	private Map<String, File> allFiles;
	
	public LoadProperties(String fileName) {
		allFiles = new HashMap<>();
		
		try {
			loadAllPropertiesFiles(fileName);
			
			//Recherche d'uns salle déjà chargé une première fois
			for (File file : allFiles.values())
			{
				Properties props = new Properties();
				props.load(new FileReader(file));
//				boolean firstStart = Boolean.parseBoolean(props.getProperty(SallesProperties.FIRST_START));
//				
//				if (!firstStart)
//				{
					salle = new Salle(fileName, props, file);
					return;
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		chooseSallePanel = new JPanel(new FlowLayout());

		add(chooseSallePanel);

		initCombo();
		initButtons();

		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initButtons() {
		JButton valid = new JButton("Valider");

		valid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String salleName = (String) sallesName.getSelectedItem();
					Properties p = new Properties();
					File file = allFiles.get(salleName);
					p.load(new FileReader(file));
					p.setProperty(SallesProperties.FIRST_START, String.valueOf(false));
					p.store(new FileWriter(file), null);

					salle = new Salle(salleName, p, file);
					dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		chooseSallePanel.add(valid);
	}

	private void initCombo() {
		sallesName = new JComboBox<>(new String[] { Atelier, DameRouge, Cirque });
		sallesName.setSelectedItem(0);
		chooseSallePanel.add(sallesName);
	}

	public Salle getSalle() {
		return salle;
	}

	private void loadAllPropertiesFiles(String fileName) throws Exception {
		// load atelier file
		File file = new File("src\\resources\\" + fileName + ".properties");
		allFiles.put(Atelier, file);

//		// load dame rouge file
//		file = new File("src\\resources\\" + SallesProperties.DAME_ROUGE + ".properties");
//		allFiles.put(DameRouge, file);
//
//		// load black circus file
//		file = new File("src\\resources\\" + SallesProperties.CIRQUE + ".properties");
//		allFiles.put(Cirque, file);
	}
}
