package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.sampled.Mixer.Info;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;

public class MusicUI extends JDialog {

	private static final long serialVersionUID = -2113163270768489618L;

	private Info[] output;

	private JPanel dialogPanel;
	private Salle salle;
	private ArrayList<Thread> threadsList;
	private ArrayList<JButton> buttonsList = new ArrayList<JButton> ();
	public MusicUI(Salle salle, ChronoPanel chronoPanel) {
		Player musicPlayer = salle.getMusicPlayer();
		output = musicPlayer.getAudioOutList();
		this.salle = salle;
		threadsList = chronoPanel.threadsList;
		dialogPanel = new JPanel();
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
		this.add(dialogPanel);
		for (int i = 0; i < threadsList.size() ; i++) {
			addThreadPanel(i, threadsList.get(i));
		}
		//Indice player if exist
		setModal(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void addThreadPanel(int idx, Thread t) {
		JPanel threadPanel = new JPanel(new FlowLayout());
		JLabel musicLabel = new JLabel("Musique "+idx);
		musicLabel.setPreferredSize(new Dimension(80, 30));
		JLabel nameLabel = new JLabel(salle.getAmbianceMusique().split(";")[idx]);
		nameLabel.setPreferredSize(new Dimension(200, 30));
		JButton stopButton;
		if (threadsList.get(idx).isAlive()) {
			stopButton = new JButton("Forcer l'arrêt");
			stopButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					threadsList.get(idx).stop();
					buttonsList.get(idx).setText("Stoppé");
					buttonsList.get(idx).setEnabled(false);
					
				}
			});
		} else {
			stopButton = new JButton("Stoppé");
			stopButton.setEnabled(false);
		}
		stopButton.setPreferredSize(new Dimension(120,30));

		
		threadPanel.add(musicLabel);
		threadPanel.add(nameLabel);
		threadPanel.add(stopButton);
		buttonsList.add(stopButton);
		dialogPanel.add(threadPanel);
	}
}
