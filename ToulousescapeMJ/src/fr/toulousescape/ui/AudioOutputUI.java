package fr.toulousescape.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.sound.sampled.Mixer.Info;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.toulousescape.util.Images;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.SallesProperties;

public class AudioOutputUI extends JDialog {

	private static final long serialVersionUID = -2113163270768489618L;

	private Info[] output;

	private Player player;

	private JComboBox<Info> combo;

	private JPanel dialogPanel;
	
	private Salle currentSalle;

	public AudioOutputUI(Player p, Salle salle) {
		output = p.getAudioOutList();
		player = p;
		dialogPanel = new JPanel(new FlowLayout());
		currentSalle = salle;
		this.add(dialogPanel);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(dialogPanel.getParent(),
						"Attention vous n'avez pas choisi de sortie audio, celle du pc est définie par défaut");
			}
		});
		initList();
		initTestPlayer();
		initButtons();
		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void initList() {
		combo = new JComboBox<>(output);
		combo.setSelectedIndex(0);
		dialogPanel.add(combo);
	}

	private void initButtons() {
		JButton valid = new JButton("Valider");
		valid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedOutput = combo.getSelectedIndex();
				Properties properties = currentSalle.getProperties();
				properties.setProperty(SallesProperties.OUTPUT, "" + selectedOutput);
				try {
					properties.store(new FileWriter(currentSalle.getPropertyFile()), null);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				player.setCurrentOut(selectedOutput);
				dispose();
			}
		});
		dialogPanel.add(valid);
	}

	private void initTestPlayer() {
		JButton playButton = new JButton();
		playButton.setIcon(new ImageIcon(Images.PLAY_IMG));

		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						player.setCurrentOut(combo.getSelectedIndex());
						player.play("tools\\DING.mp3");
					}
				}).start();
			}
		});

		dialogPanel.add(playButton);
	}
}
