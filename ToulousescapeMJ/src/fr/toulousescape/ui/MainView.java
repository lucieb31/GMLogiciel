package fr.toulousescape.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.Images;
import fr.toulousescape.util.IndiceManager;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.Session;

public class MainView extends JFrame {

	private static final long serialVersionUID = 824654556946838548L;

	private Chrono chrono;
	
	private Session session;

	private JSlider gainSlider;
	
	private JPanel chronoPanel;
	
	private Salle salle;
	
	private IndiceManager indiceManager;
	
	public IndicesPanel iPanel;
	
	public MainView(Chrono c, RoomPanel p1, RoomPanel p2, EnigmesPanel ePanel, IndicesPanel iPanel, Session s, Salle sa) {
		super();
		setName("ToulousescapeMJ");
		this.iPanel = iPanel ;
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800, 800));
		chrono = c;
		session = s;
		salle = sa;
		indiceManager = iPanel.getManager();
		loadMenuBar();
		init(p1, p2, iPanel, ePanel);
		pack();
		setLocationRelativeTo(null);
	}

	private void init(RoomPanel p1, RoomPanel p2, IndicesPanel iPanel, EnigmesPanel ePanel) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		add(mainPanel);
		// Logo
		JLabel logoImg = new JLabel(new ImageIcon(Images.LOGO_IMG));
		logoImg.setAlignmentX(CENTER_ALIGNMENT);
		Border paddingBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		logoImg.setBorder(paddingBorder);
		mainPanel.add(logoImg);
		
		//Change output
//		mainPanel.add(getConfigButton());

		// Chrono
		mainPanel.add(getChronoPanel());

		// Enigmes
		mainPanel.add(ePanel);

		// Indices
		mainPanel.add(iPanel);

		// Ecran 1 externe
//		mainPanel.add(getEcranPanel(1, p1));

		// Ecran 2 externe
//		mainPanel.add(getEcranPanel(2, p2));
	}

	private JPanel getConfigButton() {
		JPanel configPanel = new JPanel(new FlowLayout());
		
		JButton outputButton = new JButton("Sortie audio");
		outputButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new AudioOutputUI(salle);
			}
		});
		
		configPanel.add(outputButton);
		
//		gainSlider = new JSlider((int) player.getMasterGainControl()
//                .getMinimum(), (int) player.getMasterGainControl()
//                .getMaximum());
//        gainSlider.setValue((int) player.getMasterGainControl().getValue());
//        gainSlider.setPaintLabels(true);
//        gainSlider.setPaintTicks(true);
//        gainSlider.setPaintTrack(true);
//        gainSlider.setMinorTickSpacing(1);
//        gainSlider.setMajorTickSpacing((int) player.getMasterGainControl()
//                .getMaximum()
//                - (int) player.getMasterGainControl().getMinimum());
//        gainSlider.setSnapToTicks(true);
		
		return configPanel;
	}

	private JPanel getChronoPanel() {
		chronoPanel = new ChronoPanel(chrono, session, salle,this);
		chronoPanel.setPreferredSize(new Dimension(200, 50));
		return chronoPanel;
	}

	private JPanel getEcranPanel(int ecranNb, RoomPanel panel) {
		JPanel ecranPanel = new JPanel();
		ecranPanel.setLayout(new BoxLayout(ecranPanel, BoxLayout.PAGE_AXIS));
		JLabel ecranLabel = new JLabel("Ecran " + ecranNb);
		ecranPanel.add(ecranLabel);
		ecranPanel.setPreferredSize(new Dimension(200, 200));
		ecranPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		ecranPanel.add(panel);

		return ecranPanel;
	}

	private void loadMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		
		JMenu configMenu = new JMenu("Configuration");

		JMenuItem musicItem = new JMenuItem("Musiques");
		musicItem.setToolTipText("Changer la sortie audio");
		musicItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new MusicUI(salle,(ChronoPanel) chronoPanel);
			}
		});
		configMenu.add(musicItem);
		
		JMenuItem audioItem = new JMenuItem("Sortie audio");
		audioItem.setToolTipText("Changer la sortie audio");
		audioItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AudioOutputUI(salle);
			}
		});
		configMenu.add(audioItem);
		
		JMenuItem propsItem = new JMenuItem("Session hors ligne");
		propsItem.setToolTipText("Attention la session ne sera pas synchronisée avec le site");
		propsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				allowNoSynchroSession();
			}
		});
		configMenu.add(propsItem);

		JMenuItem noStartItem = new JMenuItem("Désactiver la musique de lancement");
		noStartItem.setToolTipText("La musique se relancera du début, mais le son de départ ne se lancera pas. Utilisez cette fonctionnalité si vous redémarrez le logiciel au milieu d'une session.");
		noStartItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deactivateBeginMusic();
			}
		});
		configMenu.add(noStartItem);

		
		final JFrame parent = this;
		
		JMenu salleMenu = new JMenu("Salle");
		JMenuItem createItem = new JMenuItem("Créer une salle");
		createItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ManageSalleDialog dialog = new ManageSalleDialog(parent, null);
				dialog.openAsCreate();
			}
		});
		salleMenu.add(createItem);
		
		JMenu indicesMenu = new JMenu("Administration");
		JMenuItem changeEnigmes = new JMenuItem("Énigmes");
		
		changeEnigmes.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AdminEnigmesDialog dialog = new AdminEnigmesDialog(indiceManager, parent);
				//dialog.openAsCreate();
			}
		});
		indicesMenu.add(changeEnigmes);
		
		JMenuItem changeIndices = new JMenuItem("Indices");
		changeIndices.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AdminIndicesDialog indicesDialog = new AdminIndicesDialog(indiceManager, parent);
				//indicesDialog.openAsUpdate();
			}
		});
		indicesMenu.add(changeIndices);
		
//		menuBar.add(salleMenu);
		menuBar.add(configMenu);
		menuBar.add(indicesMenu);
		setJMenuBar(menuBar);
	}
	
	public void allowNoSynchroSession() {
		((ChronoPanel) chronoPanel).setPlayable(true);
	}
	public void deactivateBeginMusic() {
		((ChronoPanel) chronoPanel).deactivateBeginMusic();
	}
}
