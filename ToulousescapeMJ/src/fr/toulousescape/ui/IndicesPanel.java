package fr.toulousescape.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.toulousescape.util.Images;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.IndiceManager;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.Session;
import fr.toulousescape.util.listeners.IndiceListener;

public class IndicesPanel extends JPanel {

	private static final long serialVersionUID = -8515748419839056660L;

	private IndiceManager manager;

	private JLabel indiceLabel;

	private List<IndiceListener> listeners = new ArrayList<>();

	private JButton closeButton;

	protected Indice currentIndice;

	private JButton showIndice;

	private Session session;

	private Player player;

	private JTextField txtField;

	private JLabel nbIndiceLabel;
	
	private Thread indiceThread;

	private JComboBox<Indice> indiceList;

	public IndicesPanel(IndiceManager m, Session s, Salle salle) {
		manager = m;
		session = s;
		Player indicePlayer = salle.getIndicePlayer();
		if (indicePlayer != null)
		{
			player = indicePlayer;
		}
		else
			player = salle.getMusicPlayer();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEtchedBorder());
		initTitle();
		searchIndice();
		initListPanel();
		initAddIndice();
		initIndiceView();
		initCloseIndice();
		initNombreIndice();
	}

	private void initTitle() {
		JLabel title = new JLabel("Indices");
		this.add(title);
	}

	private void initListPanel() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		
		ImageIcon refreshIcon = new ImageIcon(Images.REFRESH_IMG);
		JButton refresh = new JButton(refreshIcon);
		
		listPanel.add(refresh);

		indiceList = new JComboBox<>();
		computeAllIndice();
		listPanel.add(indiceList);
		
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.reloadIndices();
				computeAllIndice();
			}
		});

		JButton validate = new JButton("Valider");
		validate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Indice indice = (Indice) indiceList.getSelectedItem();
				if (indice.getImageName() != null) {
					indiceLabel.setText("");
					Image img = scaleImage(indice.getImage().getImage(), 200);
					indiceLabel.setIcon(new ImageIcon(img));
				} else if (indice.getSon() != null)
				{
					indiceLabel.setText(indice.getSon());
					indiceThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							player.play(indice.getSonWithUrl());
						}
					});
				}
				else
				{
					indiceLabel.setIcon(null);
					indiceLabel.setText(indice.getTexte());
				}
				currentIndice = indice;
				showIndice.setEnabled(true);
				closeButton.setEnabled(true);
			}
		});
		listPanel.add(validate);

		this.add(listPanel);
	}

	public static Image scaleImage(Image source, int size) {
		int width = source.getWidth(null);
		int height = source.getHeight(null);
		double f = 0;
		if (width < height) {// portrait
			f = (double) height / (double) width;
			width = (int) (size / f);
			height = size;
		} else {// paysage
			f = (double) width / (double) height;
			width = size;
			height = (int) (size / f);
		}
		return scaleImage(source, width, height);
	}

	public static Image scaleImage(Image source, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(source, 0, 0, width, height, null);
		g.dispose();
		return img;
	}

	private void initAddIndice() {
		JPanel addIndicePanel = new JPanel(new FlowLayout());

		JLabel label = new JLabel("Afficher un indice : ");
		addIndicePanel.add(label);

		txtField = new JTextField();
		txtField.setPreferredSize(new Dimension(200, 27));
		addIndicePanel.add(txtField);
		JButton valid = new JButton("Valider");
		valid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				currentIndice = new Indice(null, null, txtField.getText(), null);
				indiceLabel.setText(txtField.getText());
				indiceLabel.setIcon(null);
				showIndice.setEnabled(true);
				closeButton.setEnabled(true);
			}
		});
		addIndicePanel.add(valid);

		this.add(addIndicePanel);
	}

	private void initIndiceView() {
		indiceLabel = new JLabel();
		this.add(indiceLabel);
	}

	private void initCloseIndice() {
		JPanel showIndicePanel = new JPanel(new FlowLayout());

		JCheckBox countCheckBox = new JCheckBox("Ne pas compter l'indice");
		countCheckBox.setEnabled(false);
		countCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (countCheckBox.isSelected()) {
					session.setIndiceCount(session.getIndiceCount() - 1);
					session.removeLastIndice();
				}
				System.out.println(session.getIndiceCount());
				
			}
		});
		showIndicePanel.add(countCheckBox);

		ImageIcon icon = new ImageIcon(Images.CLOSE_IMG);
		closeButton = new JButton(icon);
		ImageIcon eyeIcon = new ImageIcon(Images.EYE_IMG);
		showIndice = new JButton(eyeIcon);
		showIndice.setEnabled(false);
		closeButton.setEnabled(false);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeButton.setEnabled(false);
				showIndice.setEnabled(false);
				indiceLabel.setText("");
				indiceLabel.setIcon(null);
				currentIndice = new Indice(null, null, null, null);
				txtField.setText("");
				nbIndiceLabel.setText("" + session.getIndiceCount());
				player.stop();
				indiceThread = null;
				countCheckBox.setEnabled(false);
				countCheckBox.setSelected(false);
				fireAddIndice();
			}
		});

		showIndicePanel.add(closeButton);

		showIndice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeButton.setEnabled(true);
				showIndice.setEnabled(false);
				countCheckBox.setEnabled(true);
				countCheckBox.setSelected(false);
				session.setIndiceCount(session.getIndiceCount() + 1);
				String indiceTxt = currentIndice.getTexte();
				if (indiceTxt != null)
					session.addIndice(currentIndice.getTexte());
				else
					session.addIndice("image ou son");
				if (indiceThread == null)
				{
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							player.play("tools\\DING.mp3");
						}
					});
					t.start();
				}
				else
					indiceThread.start();
				// try {
				// Info[] infos = AudioSystem.getMixerInfo();
				// Mixer mixer = AudioSystem.getMixer(infos[0]);
				// mixer.open();
				//
				// MpegAudioFileReader mp = new MpegAudioFileReader();
				// AudioInputStream audioFileFormat = mp.getAudioInputStream(new
				// File("src\\resources\\portrait.mp3"));
				//// AudioInputStream audioInputStream =
				// AudioSystem.getAudioInputStream(new
				// File("src\\resources\\portrait.mp3"));
				// AudioFormat format = audioFileFormat.getFormat();
				// AudioInputStream audioInputStream =
				// AudioSystem.getAudioInputStream(new AudioFormat(
				// AudioFormat.Encoding.PCM_SIGNED,
				// format.getSampleRate(),
				// 16,
				// format.getChannels(),
				// format.getChannels() * 2,
				// format.getSampleRate(),
				// false), audioFileFormat);
				// AudioFormat audioFormat = audioInputStream.getFormat();
				// javax.sound.sampled.DataLine.Info info = new
				// DataLine.Info(SourceDataLine.class, audioFormat);
				// SourceDataLine line = (SourceDataLine) mixer.getLine(info);
				// line.open(audioFormat);
				// line.start();
				//
				// byte bytes[] = new byte[1024];
				// int bytesRead=0;
				// while (((bytesRead = audioInputStream.read(bytes, 0,
				// bytes.length)) != -1)) {
				// line.write(bytes, 0, bytesRead);
				// }
				//
				//// InputStream in = (InputStream)new BufferedInputStream(new
				// FileInputStream(new File("src\\resources\\portrait.mp3")));
				//// AdvancedPlayer advancedPlayer = new AdvancedPlayer(in);
				//// advancedPlayer.play();
				// } catch (UnsupportedAudioFileException | IOException |
				// LineUnavailableException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }
				fireAddIndice();
			}
		});

		showIndicePanel.add(showIndice);

		JButton bipButton = new JButton("Bip");

		bipButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						player.play("tools\\DING.mp3");
					}
				}).start();
			}
		});

		showIndicePanel.add(bipButton);
		
		JButton alarmButton = new JButton("Alarme");
		
		alarmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				player.play("tools\\alarm.mp3");
			}
		});
		
		showIndicePanel.add(alarmButton);

		add(showIndicePanel);
	}

	private void initNombreIndice() {
		JPanel nbIndicePanel = new JPanel(new FlowLayout());
		nbIndicePanel.add(new JLabel("Nombre d'indice : "));
		nbIndiceLabel = new JLabel("0");
		nbIndicePanel.add(nbIndiceLabel);

		add(nbIndicePanel);
	}
	
	private void searchIndice()
	{
		JPanel searchPanel = new JPanel(new FlowLayout());
		JTextField searchField = new JTextField();
		searchField.setPreferredSize(new Dimension(150, 27));
		searchPanel.add(searchField);
		ImageIcon icon = new ImageIcon(Images.SEARCH_IMG);
		JButton searchButton = new JButton(icon);
		searchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				List<Indice> allIndices = manager.getAllIndices();
				List<Indice> indiceToList = new ArrayList<>();
				for (Indice indice : allIndices)
				{
					String description = indice.getDescription();
					if (description.contains(searchField.getText()))
					{
						indiceToList.add(indice);
					}
				}
				computeIndiceList(indiceToList);
			}
		});
		searchPanel.add(searchButton);
		
		ImageIcon iconClear = new ImageIcon(Images.CLOSE_IMG);
		JButton clearSearchButton = new JButton(iconClear);
		clearSearchButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchField.setText("");
				computeAllIndice();
			}
		});
		searchPanel.add(clearSearchButton);
		
		add(searchPanel);
	}

	public JLabel getIndiceLabel() {
		return indiceLabel;
	}

	private void computeAllIndice()
	{
		computeIndiceList(manager.getAllIndices());
	}
	
	private void computeIndiceList(List<Indice> indices) {
		indiceList.removeAllItems();
		for (Indice i : indices) {
			indiceList.addItem(i);
		}
	}

	public void addListeners(IndiceListener listen) {
		listeners.add(listen);
	}

	public void fireAddIndice() {
		for (IndiceListener l : listeners) {
			l.showIndice(currentIndice);
		}
	}

	public IndiceManager getManager() {
		return manager;
	}
}
