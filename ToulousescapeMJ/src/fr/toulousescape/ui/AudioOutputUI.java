package fr.toulousescape.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import javax.sound.sampled.Mixer.Info;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import fr.toulousescape.util.Images;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.SallesProperties;

public class AudioOutputUI extends JDialog {

	private static final long serialVersionUID = -2113163270768489618L;

	private Set<String> output;

	private JComboBox combo_music;
	
	private JComboBox<String> combo_indices;

	private JPanel dialogPanel;
	
	private Salle currentSalle;
	
	private Logger logger;

	public AudioOutputUI(Salle salle) {
		logger = LogManager.getLogger(AudioOutputUI.class);
		Player musicPlayer = salle.getMusicPlayer();
		output = musicPlayer.getOutNames();
		
		dialogPanel = new JPanel();
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
		currentSalle = salle;
		this.add(dialogPanel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(dialogPanel.getParent(),
						"Sortie audio non modifiée.");
			}
		});
		
		// TODO: vérifier dans la config si on a une sortie différente pour les indices
		Player indicePlayer = salle.getIndicePlayer();
		initList(musicPlayer, null);
		initButtons(musicPlayer, indicePlayer);
		
		//Indice player if exist
		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initList(Player music, Player indice) {
		JPanel musicPanel = new JPanel(new FlowLayout());
		String labelName = "Sortie son : ";
		if (indice != null)
			labelName = "Sortie musique : ";
		JLabel musicLabel = new JLabel(labelName);
		musicPanel.add(musicLabel);
		Properties properties = currentSalle.getProperties();
		String selected = properties.getProperty(SallesProperties.MUSIC_OUTPUT);
		combo_music = new JComboBox<>(output.toArray());
		if (selected != null) {
			combo_music.setSelectedItem(selected);
		} else {
			combo_music.setSelectedIndex(0);
		}
		musicPanel.add(combo_music);
		musicPanel.add(initTestPlayer(music, combo_music));
		dialogPanel.add(musicPanel);
		
		if (indice != null)
		{
//			JPanel audioPanel = new JPanel(new FlowLayout());
//			JLabel indices = new JLabel("Sortie indices : ");
//			audioPanel.add(indices);
//			selected = properties.getProperty(SallesProperties.INDICES_OUTPUT);
//			combo_indices = new JComboBox<>(output.toArray());
//			if (selected != null) {
//				combo_indices.setSelectedIndex(new Integer(selected));
//			} else {
//				combo_indices.setSelectedIndex(0);
//			}
//			audioPanel.add(combo_indices);
//			audioPanel.add(initTestPlayer(indice, combo_indices));
//			dialogPanel.add(audioPanel);
		}
	}

	private void initButtons(Player music, Player indice) {
		JButton valid = new JButton("Valider");
		valid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = (String) combo_music.getSelectedItem();
				Properties properties = currentSalle.getProperties();
				properties.setProperty(SallesProperties.MUSIC_OUTPUT, name);
				music.setCurrentOut(name);
				
				if (indice != null)
				{
//					selectedOutput = combo_indices.getSelectedIndex();
//					properties.setProperty(SallesProperties.INDICES_OUTPUT, "" + selectedOutput);
					indice.setCurrentOut(name);
				}
				
				try {
					FileWriter fileWriter = new FileWriter(currentSalle.getPropertyFile());
					properties.store(fileWriter, null);
					fileWriter.close();
				} catch (IOException e1) {
					logger.error(e1.getMessage());
				}
				dispose();
			}
		});
		dialogPanel.add(valid);
	}

	private JButton initTestPlayer(Player player, JComboBox<Info> combo) {
		JButton playButton = new JButton();
		playButton.setIcon(new ImageIcon(Images.PLAY_IMG));

		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						player.setCurrentOut((String) combo.getSelectedItem());
						player.play("tools\\DING.mp3");
					}
				}).start();
			}
		});

		return playButton;
	}
	
}
