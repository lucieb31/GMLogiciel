package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.Images;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.IndiceComparator;
import fr.toulousescape.util.IndiceManager;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.SallesProperties;
import fr.toulousescape.util.Session;
import fr.toulousescape.util.listeners.EnigmeListener;
import fr.toulousescape.util.listeners.IndiceListener;
import fr.toulousescape.util.listeners.TimerListener;

public class IndicesPanel extends JPanel implements EnigmeListener, TimerListener {

	private static final long serialVersionUID = -8515748419839056660L;

	private IndiceManager manager;

	private int time = 0;
	
	private Color RED = new Color(255, 0, 0);
	
	private Color ORANGE = new Color(255, 150, 0);

	private Color YELLOW = new Color(255, 250, 0);

	private Color GREEN = new Color(0, 255, 0);

	private int lastClueTime = 0;
	
	private JLabel indiceLabel;
	
	private JPanel listPanel;

	private List<IndiceListener> listeners = new ArrayList<>();

	private JButton closeButton;

	protected Indice currentIndice;
	
	String roomPseudo;

	private JButton showIndice;

	private JButton showSilentIndice;

	private JCheckBox countCheckBox;
	
	private Session session;

	private Player indicePlayer;

	private Player musicPlayer;

	private JTextField txtField;

	public JLabel nbIndiceLabel;
	
	private Thread indiceThread;
	
	private Properties p;

	private List<Indice> interactionList = new ArrayList<Indice>();

	public IndicesPanel(IndiceManager m, Session s, Salle salle) {
		manager = m;
		session = s;
		
		p = salle.getProperties();
		roomPseudo = salle.getPseudo();
		indicePlayer = salle.getIndicePlayer();
		musicPlayer = salle.getMusicPlayer();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEtchedBorder());
		initTitle();
		//searchIndice();
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
		listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		
//		ImageIcon refreshIcon = new ImageIcon(Images.REFRESH_IMG);
//		JButton refresh = new JButton(refreshIcon);
		
//		listPanel.add(refresh);

/*		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				manager.reloadIndices();
				computeAllIndice();
			}
		});*/

		this.add(listPanel);
	}

	protected void validateAction(Indice indice) {
		// TODO Auto-generated method stub
		
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
				currentIndice = new Indice(null, null, txtField.getText(), null, Indice.TYPE_TEXTE, null, null, null, false);
				indiceLabel.setText(txtField.getText());
				indiceLabel.setIcon(null);
				showIndice.setEnabled(true);
				showSilentIndice.setEnabled(true);
				closeButton.setEnabled(true);
			}
		});
		addIndicePanel.add(valid);
		JButton see = new JButton(new ImageIcon(Images.SEE_IMG));
		see.setToolTipText("Voir les indices déjà envoyés");
		see.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SeeCluesDialog dialog = new SeeCluesDialog(getParent(), session.getAllIndicesAsHTML());
				dialog.openDialog();
			}
		});
		addIndicePanel.add(see);

		this.add(addIndicePanel);
	}

	private void initIndiceView() {
		indiceLabel = new JLabel();
		this.add(indiceLabel);
	}

	private void initCloseIndice() {
		JPanel showIndicePanel = new JPanel(new FlowLayout());

		countCheckBox = new JCheckBox("Ne pas compter l'indice");
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
		showSilentIndice = new JButton("Modifier");
		showSilentIndice.setEnabled(false);
		closeButton.setEnabled(false);
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				closeButton.setEnabled(false);
				showIndice.setEnabled(false);
				showSilentIndice.setEnabled(false);
				indiceLabel.setText("");
				indiceLabel.setIcon(null);
				currentIndice = new Indice(null, null, null, null, Indice.TYPE_TEXTE, null, null, null, false);
				txtField.setText("");
				nbIndiceLabel.setText("" + session.getIndiceCount());
				indicePlayer.stop();
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
				launchShowIndice(false);
			}

			
		});

		showSilentIndice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				launchShowIndice(true);
			}

			
		});

		showIndicePanel.add(showIndice);
		showIndicePanel.add(showSilentIndice);

		JButton bipButton = new JButton("Bip");

		bipButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						indicePlayer.play("tools\\DING.mp3");
					}
				}).start();
			}
		});

		showIndicePanel.add(bipButton);
		
		JButton alarmButton = new JButton("Alarme");
		
		alarmButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				indicePlayer.play("tools\\alarm.mp3");
			}
		});
		
		showIndicePanel.add(alarmButton);
		
		computeAllInteraction();
		interactionList.sort(new IndiceComparator());
		for (Indice en : interactionList) {
			JButton button = new JButton();
			button.setText(en.getDescription());
			if (en.getColor() != null) {
				Color color = new Color(new Integer(p.getProperty(en.getColor()+".R", "255")),new Integer(p.getProperty(en.getColor()+".G", "255")),new Integer(p.getProperty(en.getColor()+".B", "255")));
				
				button.setBackground(color);
			}
			if (en.getTexte() != null && ! en.getTexte().equals("")) {
				button.setToolTipText(en.getTexte());
			}
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					validateIndice(en, true);
				}
			});
			showIndicePanel.add(button);
		}

		add(showIndicePanel);
	}

	private void initNombreIndice() {
		JPanel nbIndicePanel = new JPanel(new FlowLayout());
		nbIndicePanel.add(new JLabel("Nombre d'indice : "));
		nbIndiceLabel = new JLabel("0");
		nbIndicePanel.add(nbIndiceLabel);

		add(nbIndicePanel);
	}
	
	/*private void searchIndice()
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
	 */
	public JLabel getIndiceLabel() {
		return indiceLabel;
	}

	private void computeAllInteraction()
	{
		computeInteractionList(manager.getAllInteractions());
	}
	
	private void computeInteractionList(List<Indice> interactions) {
		interactionList.clear();
		for (Indice i : interactions) {
			interactionList.add(i);
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

	@Override
	public void showIndices(List<Indice> indices) {
		listPanel.removeAll();
		listPanel.repaint();
		listPanel.revalidate();
		for (Indice in : indices) {
			JButton button = new JButton();
			button.setText(in.getDescription());
			if (in.getTexte() != null) {
				button.setToolTipText(in.getTexte());
			} else if (in.getImage() != null) {
				button.setToolTipText("Image");
			} else if (in.getSon() != null) {
				button.setToolTipText("Son");
			}
			if (in.getColor() != null) {
				Color color = new Color(new Integer(p.getProperty(in.getColor()+".R", "255")),new Integer(p.getProperty(in.getColor()+".G", "255")),new Integer(p.getProperty(in.getColor()+".B", "255")));
				
				button.setBackground(color);
			}
			
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					validateIndice(in,false);
				}
			});
			listPanel.add(button);
		}
		listPanel.repaint();
		listPanel.revalidate();

	}

	protected void validateIndice(Indice indice, boolean interaction) {
		indiceLabel.setText(indice.getTexte());
		if (Indice.TYPE_IMAGE.equals(indice.getType())) {
			indiceLabel.setText("");
			Image img = scaleImage(indice.getImage().getImage(), 200);
			indiceLabel.setIcon(new ImageIcon(img));
			indiceThread = null;
		} else if (Indice.TYPE_SON.equals(indice.getType())){
			if (indice.getTexte() != null && ! indice.getTexte().equals("")) {
				indiceLabel.setText(indice.getTexte());
			} else {
				indiceLabel.setText(indice.getSon());
			}
			indiceThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					indicePlayer.play(indice.getSonWithUrl());
				}
			});
		}
		else
		{
			indiceLabel.setIcon(null);
			indiceLabel.setText(indice.getTexte());
			indiceThread = null;
		}
		currentIndice = indice;
		if (interaction) {
			// Interaction, play sound direct or send webservice
			if (currentIndice.getType().equals(Indice.TYPE_SON)){
				if (currentIndice.isMusic()) {
					musicPlayer.stop();
					
					String musicToPlay = currentIndice.getSon();
					new Thread(new Runnable() {

						@Override
						public void run() {
							System.out.println("PLAY!!! " + musicToPlay);
							musicPlayer.play(roomPseudo +"\\" + musicToPlay);
						}
					}).start();
				} else {
					indiceThread.start();
				}
				
			} else {
				callWebService(currentIndice.getFunction());
			}
		} else { //Indice
			showIndice.setEnabled(true);
			showSilentIndice.setEnabled(true);
			if (! Indice.TYPE_SON.equals(currentIndice.getType())) {
				closeButton.setEnabled(true);
			}
		}
	}

	private void callWebService(String function) {
		System.out.println("APPEL DU WEBSERVICE DES MODULES");
		try {
			URL url;
//			url = new URL("https://www.toulousescape.fr/process/wsmod");
			url = new URL("http://localhost/toulousescape/process/wsmod");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			Map<String, String> postParams = new LinkedHashMap<>();
			postParams.put("function", function);
			System.out.println("FONCTION : "+function);

			byte[] postDataBytes = generatePostData(postParams);

		    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
//			conn.setRequestProperty("Accept", "application/json");
			conn.setDoOutput(true);
	        conn.getOutputStream().write(postDataBytes);

	        
	        if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	        
	        System.out.println("FIN APPEL : "+conn.getResponseCode());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void timeChanged(int currentTime) {
		this.time = currentTime;
		updateColor();
	}
	
	private void updateColor() {
		if (lastClueTime < time) {
			lastClueTime = time;
		}
		int difference = lastClueTime - time;
		int fiveMinutes = 60*5;
		int fourMinutes = 60*4;
		int threeMinutes = 60*3;
		if (difference > fiveMinutes) {
			showIndice.setBackground(GREEN);
		} else if (difference > fourMinutes) {
			showIndice.setBackground(YELLOW);
		} else if (difference > threeMinutes) {
			showIndice.setBackground(ORANGE);
		} else {
			showIndice.setBackground(RED);
		}
	}
	public void launchShowIndice(boolean s) {
		boolean silent = s;
		showIndice.setEnabled(false);
		showSilentIndice.setEnabled(false);
	
		if (! Indice.TYPE_SON.equals(currentIndice.getType())){
			closeButton.setEnabled(true);
		}
		countCheckBox.setEnabled(true);
		countCheckBox.setSelected(false);
		if (! Indice.TYPE_MODULE.equals(currentIndice.getType())) {
			session.setIndiceCount(session.getIndiceCount() + 1);
			lastClueTime = time;
			updateColor();
		}
		nbIndiceLabel.setText("" + session.getIndiceCount());
		
		
		String indiceTxt = currentIndice.getTexte();
		if (indiceTxt != null) {
			String toAdd = Chrono.formatTime(time) + " "+ currentIndice.getTexte();
			session.addIndice(toAdd);
		}
		else {
			String toAdd = Chrono.formatTime(time) + " image ou son";
	
			session.addIndice(toAdd);
		}
		if (indiceThread == null)
		{
			if (! silent) {
				Thread t = new Thread(new Runnable() {
		
					@Override
					public void run() {
						indicePlayer.play("tools\\DING.mp3");
					}
				});
				t.start();
			}
		}
		else {
			indiceThread.start();
		}
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
