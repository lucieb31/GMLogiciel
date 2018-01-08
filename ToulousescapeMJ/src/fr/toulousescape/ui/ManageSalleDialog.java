package fr.toulousescape.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.toulousescape.util.Salle;

public class ManageSalleDialog extends JDialog{

	private Salle currentSalle;
	
	private Salle createdSalle;
	
	public ManageSalleDialog(JFrame parent, Salle curSalle)
	{
		currentSalle = curSalle;
		setLocationRelativeTo(parent);
	}
	
	public ManageSalleDialog()
	{
		super();
	}
	
	public void openAsCreate()
	{
		setTitle("Creation d'une salle");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		JPanel namePanel = new JPanel(new FlowLayout());
		JLabel nameLabel = new JLabel("Nom de la salle");
		namePanel.add(nameLabel);
		JTextField nameField = new JTextField();
		nameField.setPreferredSize(new Dimension(100, 27));
		namePanel.add(nameField);
		mainPanel.add(namePanel);
		
		JPanel pseudoPanel = new JPanel(new FlowLayout());
		JLabel pseudoLabel = new JLabel("Diminutif de la salle");
		pseudoPanel.add(pseudoLabel);
		JTextField pseudoField = new JTextField();
		pseudoField.setPreferredSize(new Dimension(100, 27));
		pseudoPanel.add(pseudoField);
		mainPanel.add(pseudoPanel);
		
		JPanel ecranPanel = new JPanel(new FlowLayout());
		JLabel ecranLabel = new JLabel("Nombre d'ecran");
		ecranPanel.add(ecranLabel);
		JTextField ecranField = new JTextField();
		ecranField.setPreferredSize(new Dimension(100, 27));
		ecranPanel.add(ecranField);
		mainPanel.add(ecranPanel);
		
		JPanel ambianceMusicPanel = new JPanel(new FlowLayout());
		JLabel ambianceMusicLabel = new JLabel("Musique d'ambiance");
		ambianceMusicPanel.add(ambianceMusicLabel);
		JTextField ambianceMusicField = new JTextField();
		ambianceMusicField.setPreferredSize(new Dimension(100, 27));
		ambianceMusicPanel.add(ambianceMusicField);
		mainPanel.add(ambianceMusicPanel);
		
		JPanel finalMusicPanel = new JPanel(new FlowLayout());
		JLabel finalMusicLabel = new JLabel("Musique finale");
		finalMusicPanel.add(finalMusicLabel);
		JTextField finalMusicField = new JTextField();
		finalMusicField.setPreferredSize(new Dimension(100, 27));
		finalMusicPanel.add(finalMusicField);
		mainPanel.add(finalMusicPanel);
		
		JCheckBox audioIndice = new JCheckBox("Activer audio indices");
		audioIndice.setSelected(false);
		mainPanel.add(audioIndice);
		
		JButton createButton = new JButton("Créer la salle");
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String pseudo = pseudoField.getText();
				Salle s = new Salle(name, pseudo);
				
				int nbEcran = Integer.parseInt(ecranField.getText());
				if (nbEcran != 0)
				{
					getEcranResolution(nbEcran, s);
				}
				
				s.setNbEcran(nbEcran);
				
				s.setAmbianceMusique(ambianceMusicField.getText());
				s.setFinalMusic(finalMusicField.getText());
				s.setHasAudioIndice(audioIndice.isSelected());
				
				s.computePropFile();
				createdSalle = s;
				JOptionPane.showMessageDialog(mainPanel, "La salle a bien été créée");
				dispose();
			}
		});
		mainPanel.add(createButton);
		add(mainPanel);
		
		pack();
		setVisible(true);
	}

	public Salle getCreatedSalle() {
		return createdSalle;
	}

	public void setCreatedSalle(Salle createdSalle) {
		this.createdSalle = createdSalle;
	}
	
	public void getEcranResolution(int nbEcran, Salle s)
	{
		List<JTextField> fields = new ArrayList<>();
		JDialog resolutionDialog = new JDialog(this, "Résolution des écrans", true);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		for (int i = 1; i <= nbEcran; i++) {
			JPanel resPanel = new JPanel(new FlowLayout());
			JLabel resLabel = new JLabel("Ecran " + i);
			resPanel.add(resLabel);
			JTextField resField = new JTextField();
			resField.setPreferredSize(new Dimension(50, 15));
			resPanel.add(resField);
			fields.add(resField);
			mainPanel.add(resPanel);
		}
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean closeDialog = true;
				List<Integer> resolutions = new ArrayList<>();
				for (JTextField txtF : fields) {
					if (txtF.getText() == null || txtF.getText().isEmpty()) {
						closeDialog = false;
					} else {
						resolutions.add(Integer.parseInt(txtF.getText()));
					}
				}

				s.setResolutionEcrans(resolutions);
				if (!closeDialog)
					JOptionPane.showMessageDialog(resolutionDialog,
							"Attention vous devez entrer toutes les résolutions", "Résolution erreur",
							JOptionPane.ERROR_MESSAGE);
				else
					resolutionDialog.dispose();
			}
		});
		mainPanel.add(okButton);
		resolutionDialog.add(mainPanel);
		resolutionDialog.pack();
		resolutionDialog.setVisible(true);

	}
}
