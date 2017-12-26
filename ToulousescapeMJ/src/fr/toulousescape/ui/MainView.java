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
	
	private Salle salle;
	
	private IndiceManager indiceManager;
	
	public MainView(Chrono c, RoomPanel p1, RoomPanel p2, IndicesPanel iPanel, Session s, Salle sa) {
		super();
		setName("ToulousescapeMJ");
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(800, 800));
		chrono = c;
		session = s;
		salle = sa;
		indiceManager = iPanel.getManager();
		loadMenuBar();
		init(p1, p2, iPanel);
		pack();
		setLocationRelativeTo(null);
	}

	private void init(RoomPanel p1, RoomPanel p2, IndicesPanel iPanel) {
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
		JPanel chronoPanel = new ChronoPanel(chrono, session, salle);
		chronoPanel.setPreferredSize(new Dimension(200, 200));
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
		JMenuItem audioItem = new JMenuItem("Sortie audio");
		audioItem.setToolTipText("Changer la sortie audio");
		audioItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AudioOutputUI(salle);
			}
		});
		configMenu.add(audioItem);
		
		JMenuItem propsItem = new JMenuItem("Propriétés");
		propsItem.setToolTipText("Modifier les propriétés de la salle");
		configMenu.add(propsItem);
		
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
		
		JMenu indicesMenu = new JMenu("Indices");
		JMenuItem addItem = new JMenuItem("Créer des indices");
		
		addItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IndicesDialog indicesDialog = new IndicesDialog(indiceManager, parent);
				indicesDialog.openAsCreate();
			}
		});
		indicesMenu.add(addItem);
		
		JMenuItem changeItem = new JMenuItem("Modifier les indices");
		changeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IndicesDialog indicesDialog = new IndicesDialog(indiceManager, parent);
				indicesDialog.openAsUpdate();
			}
		});
		indicesMenu.add(changeItem);
		
		JMenuItem removeItem = new JMenuItem("Supprimer les indices");
		removeItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IndicesDialog indicesDialog = new IndicesDialog(indiceManager, parent);
				indicesDialog.openAsRemove();
			}
		});
		indicesMenu.add(removeItem);
		
		JMenuItem showItem = new JMenuItem("Voir les indices");
		showItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				IndicesDialog indicesDialog = new IndicesDialog(indiceManager, parent);
				indicesDialog.openAsView();
			}
		});
		indicesMenu.add(showItem);
		
		menuBar.add(indicesMenu);
		menuBar.add(salleMenu);
		menuBar.add(configMenu);
		setJMenuBar(menuBar);
	}
}
