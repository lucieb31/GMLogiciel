package fr.toulousescape.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.Images;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.Session;
import fr.toulousescape.util.listeners.TimerListener;

public class ChronoPanel extends JPanel implements TimerListener {

	private static final long serialVersionUID = 8970100750129366954L;

	JButton startButton = new JButton(new ImageIcon(Images.PLAY_IMG));
	JButton stopButton = new JButton(new ImageIcon(Images.STOP_IMG));
	JButton nextMusicButton = new JButton(new ImageIcon(Images.NEXT_IMG));
	JButton pauseButton = new JButton(new ImageIcon(Images.PAUSE_IMG));
	JButton sessionSearch = new JButton(new ImageIcon(Images.REFRESH_IMG));
	Timer autoStartTimer;
	boolean autoStartTimerRunning = false;
	private int musicNumber = 0;
	
	private static final String NO_SESSION_TEXT = "Récupérer une session pour pouvoir lancer le chrono.";
	
	JTextField sessionField;
	JLabel sessionInfoLabel = new JLabel(NO_SESSION_TEXT);
	
	JLabel chronoTime = new JLabel();
	JLabel remainingTimeLabel = new JLabel();

	private int timeSpent = 0;

	private Chrono chrono;

	private Session session;

	private JButton validSetter;

	private Player player;

	private boolean hasAmbianceMusic = false;

	private boolean hasBeginMusic = false;

	private boolean hasElementsMusic = false;

	private boolean hasFinalMusic = false;
	
	private boolean isPaused = false;

	private boolean firstLaunch = true;

	private Map<Integer, String> elementsMusic = null;
	
	private Salle salle;

	private MainView parent;
	Thread currentMusicThread = null;

	public ChronoPanel(Chrono c, Session s, Salle salle, MainView parent) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		chrono = c;
		chrono.addTimerListener(this);
		session = s;
		player = salle.getMusicPlayer();
		this.salle = salle;
		hasAmbianceMusic = salle.getAmbianceMusique() != null;
		hasFinalMusic = salle.getFinalMusic() != null;
		hasBeginMusic = salle.getBeginMusic() != null;
		elementsMusic = salle.getElementsMusic();
		hasElementsMusic = elementsMusic != null;
		//initSession();
		initTitle();
		initTimerSetter();
		initButtons();
	}

	public void initTitle() {
		this.add(sessionInfoLabel);
	}

	public void initButtons() {
		JPanel globalPanel = new JPanel(new FlowLayout());
		
		JPanel chronoButtonsPanel = new JPanel(new FlowLayout());
		startButton.setEnabled(false);
		startButton.setToolTipText("Veuillez récupérer la session avant de lancer le chrono");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(false);
				pauseButton.setEnabled(true);
				stopButton.setEnabled(true);
				nextMusicButton.setEnabled(true);
				validSetter.setEnabled(false);
				sessionSearch.setEnabled(false);
				if (autoStartTimer != null) {
					autoStartTimerRunning = false;
					autoStartTimer.cancel();
				}
				session.setDate(new Date());
				System.out.println(hasAmbianceMusic + " " + isPaused);
				if (hasAmbianceMusic && !isPaused)
				{
					String musicToPlay = salle.getAmbianceMusique().split(";")[musicNumber];
					musicNumber++;
					if(musicNumber >= salle.getAmbianceMusique().split(";").length) {
						nextMusicButton.setEnabled(false);
					}
					currentMusicThread = new Thread(new Runnable() {

						@Override
						public void run() {
							System.out.println("PLAY!!! " + musicToPlay);
							player.play(salle.getPseudo() + "\\" + musicToPlay);
						}
					});
					currentMusicThread.start();
				}
				if (hasBeginMusic && !isPaused && firstLaunch)
				{
					String musicToPlay = salle.getBeginMusic();
					new Thread(new Runnable() {

						@Override
						public void run() {
							System.out.println("PLAY!!! " + musicToPlay);
							player.play(salle.getPseudo() + "\\" + musicToPlay);
						}
					}).start();
				}

				chrono.start();
				isPaused = false;
				firstLaunch = false;
			}
		});
		chronoButtonsPanel.add(startButton);

		pauseButton.setEnabled(false);
		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pauseButton.setEnabled(false);
				startButton.setEnabled(true);
				stopButton.setEnabled(true);
				player.pause();
				chrono.pause();
				isPaused = true;
			}
		});
		chronoButtonsPanel.add(pauseButton);

		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				musicNumber = 0;
				nextMusicButton.setEnabled(false);
				stopButton.setEnabled(false);
				startButton.setEnabled(false);
				sessionSearch.setEnabled(true);
				pauseButton.setEnabled(false);
				validSetter.setEnabled(true);
				
				player.stop();

				// TODO : Afficher temps passé et temps restant en seconde (pour
				// le score)
				session.setRemainingTime(chrono.getCurrentTime());
				session.setTimeSpent(timeSpent);
				if (! "".equals(sessionField.getText())) {
					session.setId(new Integer(sessionField.getText()));
				}
				sessionInfoLabel.setText(NO_SESSION_TEXT);
				sessionField.setText("");
				chrono.stop();
				
				StopSessionDialog dialog = new StopSessionDialog(getParent(), session);
				dialog.openDialog();
//				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//				JOptionPane.showMessageDialog(stopButton.getParent(),
//						"Date : " + dateFormat.format(session.getDate()) + " Temps restant : "
//								+ session.getRemainingTime() + "s Temps passé : " + formatTime(session.getTimeSpent())
//								+ " Nombre d'indice : " + session.getIndiceCount() + " \n" + session.getAllIndices());

				// Reset all
				session.clearAllIndices();
				session.setIndiceCount(0);
				parent.iPanel.nbIndiceLabel.setText("0");
				session.setDate(null);
				timeSpent = 0;
				isPaused = false;
				firstLaunch = true;
			}
		});
		chronoButtonsPanel.add(stopButton);
		
		nextMusicButton.setEnabled(false);
		nextMusicButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
				currentMusicThread.interrupt();
//				String musicToPlay = salle.getAmbianceMusique().split(";")[musicNumber];
//				musicNumber++;
//				if(musicNumber >= salle.getAmbianceMusique().split(";").length) {
//					nextMusicButton.setEnabled(false);
//				}
//				
//				player.play(salle.getPseudo() + "\\" + musicToPlay);
				
				currentMusicThread = new Thread(new Runnable() {

					@Override
					public void run() {
						String musicToPlay = salle.getAmbianceMusique().split(";")[musicNumber];
						musicNumber++;
						if(musicNumber >= salle.getAmbianceMusique().split(";").length) {
							nextMusicButton.setEnabled(false);
						}
						System.out.println("PLAY!!! " + musicToPlay);
						player.play(salle.getPseudo() + "\\" + musicToPlay);
					}
				});
				currentMusicThread.start();
			}
		});
		chronoButtonsPanel.add(nextMusicButton);
		
		globalPanel.add(chronoButtonsPanel);
		
		chronoTime.setHorizontalAlignment(JLabel.CENTER);
		chronoTime.setVerticalAlignment(JLabel.CENTER);
		chronoTime.setText("1:00:00");
		Font chronoFont = new Font("Arial", Font.BOLD, 30);
		chronoTime.setFont(chronoFont);
		globalPanel.add(chronoTime);

		
		this.add(globalPanel);
	}

	public void initTimeLabel() {
		chronoTime.setHorizontalAlignment(JLabel.CENTER);
		chronoTime.setVerticalAlignment(JLabel.CENTER);
		chronoTime.setText("1:00:00");
		Font chronoFont = new Font("Arial", Font.BOLD, 30);
		chronoTime.setFont(chronoFont);
		this.add(chronoTime);
	}

	private void initTimerSetter() {
		JPanel timerSetterAndChangeTimePanel = new JPanel (new FlowLayout());

		JPanel timerSetterPanel = new JPanel(new FlowLayout());

		JLabel sessionLabel = new JLabel();
		sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		sessionLabel.setVerticalAlignment(SwingConstants.CENTER);
		sessionLabel.setText("Session :");
		sessionField = new JTextField();
		sessionField.setPreferredSize(new Dimension(50, 27));
		sessionField.setEditable(false);
		sessionSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getSessionFromWebsite();
			}
		});
		timerSetterPanel.add(sessionLabel);
		timerSetterPanel.add(sessionField);
		timerSetterPanel.add(sessionSearch);
	
		JLabel label = new JLabel("Durée du chrono : ");
		timerSetterPanel.add(label);

		JTextField txtField = new JTextField();
		txtField.setPreferredSize(new Dimension(30, 27));
		timerSetterPanel.add(txtField);
		JLabel l = new JLabel("min");
		timerSetterPanel.add(l);
		validSetter = new JButton("Valider");
		validSetter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(txtField.getText());
				chrono.setTime(Integer.valueOf(txtField.getText()));
			}
		});
		timerSetterPanel.add(validSetter);
		
		timerSetterAndChangeTimePanel.add(timerSetterPanel);
		
		JPanel penalityPanel = new JPanel(new FlowLayout());

		JLabel label2 = new JLabel("Pénalité : ");
		penalityPanel.add(label2);

		JTextField txtField2 = new JTextField();
		txtField2.setPreferredSize(new Dimension(50, 27));

		txtField2.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar())) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		penalityPanel.add(txtField2);
		JLabel l2 = new JLabel("min");
		penalityPanel.add(l2);

		JButton removeTime = new JButton(new ImageIcon(Images.MINUS_IMG));
		removeTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chrono.removeTime(Integer.valueOf(txtField2.getText()));
			}
		});
		penalityPanel.add(removeTime);

		JButton addTime = new JButton(new ImageIcon(Images.PLUS_IMG));
		addTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chrono.addTime(Integer.valueOf(txtField2.getText()));
			}
		});
		penalityPanel.add(addTime);

		timerSetterAndChangeTimePanel.add(penalityPanel);


		this.add(timerSetterAndChangeTimePanel);
	}

	private void initChangeTimePanel() {
		JPanel penalityPanel = new JPanel(new FlowLayout());

		JLabel label = new JLabel("Pénalité : ");
		penalityPanel.add(label);

		JTextField txtField = new JTextField();
		txtField.setPreferredSize(new Dimension(50, 27));

		txtField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (!Character.isDigit(e.getKeyChar())) {
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		penalityPanel.add(txtField);
		JLabel l = new JLabel("min");
		penalityPanel.add(l);

		JButton removeTime = new JButton(new ImageIcon(Images.MINUS_IMG));
		removeTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chrono.removeTime(Integer.valueOf(txtField.getText()));
			}
		});
		penalityPanel.add(removeTime);

		JButton addTime = new JButton(new ImageIcon(Images.PLUS_IMG));
		addTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				chrono.addTime(Integer.valueOf(txtField.getText()));
			}
		});
		penalityPanel.add(addTime);

		this.add(penalityPanel);
	}

	@Override
	public void timeChanged(int currentTime) {

		chronoTime.setText(Chrono.formatTime(currentTime));
		if (hasElementsMusic) {
			for (Integer value : elementsMusic.keySet()) {
				if (currentTime == value) {
					String elementMusic = elementsMusic.get(value);
					new Thread(new Runnable() {
						@Override
						public void run() {
							System.out.println("PLAY !!! " + elementMusic);
							// player.stop();
							try {
								Thread.sleep(100L);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							player.play(salle.getPseudo() + "\\" + elementMusic);
						}
					}).start();					
				}
			}
		}
		
		if (currentTime == 0 && hasFinalMusic) {
			String finalMusic = salle.getFinalMusic();
			new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("PLAY 0 !!! " + finalMusic);
					player.stop();
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					player.play(salle.getPseudo() + "\\" + finalMusic);
				}
			}).start();
		}

		// Calculate remaining time
		timeSpent++;
	}

	public void getSessionFromWebsite() {

	  try {
		LoadConfig config = new LoadConfig();
		String room = config.getSelectedSalle();
		System.out.println("Salle : "+room);
		URL url = new URL("https://www.toulousescape.fr/process/wstescape?room="+room);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

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
		
		if (all.trim().equals("nosession")) {
			JOptionPane.showMessageDialog(this, "Merci de synchroniser une session "+room+" via le backend.");
		} else {
			String session = all.substring(all.indexOf(":")+2, all.indexOf(",") - 1);
			startButton.setEnabled(true);
			if (! autoStartTimerRunning) {
				scheduleAutoStartTimer();
			}
			String firstname = all.substring(all.indexOf("firstname\":")+12, all.indexOf(",\"lastname") - 1);
			String lastname = all.substring(all.indexOf("lastname\":")+11, all.indexOf(",\"phone") - 1);
			String phone = all.substring(all.indexOf("phone\":")+8, all.indexOf(",\"email") - 1);
			String players = all.substring(all.indexOf("players\":")+10, all.indexOf(",\"bonus") - 1);
			String running = all.substring(all.indexOf("running\":")+10, all.indexOf(",\"players") - 1);
			String special = all.substring(all.indexOf("special\":")+10, all.indexOf(",\"price_category") - 1);
			System.out.println(firstname);
			System.out.println(lastname);
			System.out.println(phone);
			System.out.println(players);
			String specialText = "";
			if ("D".equals(special)) {
				specialText = "Découverte - ";
			} else if ("A".equals(special)) {
				specialText = "Version anglaise - ";
			}
			sessionField.setText(session);
			sessionInfoLabel.setText(specialText +firstname+ " "+ lastname + " ("+phone+") "+players+" joueurs");
			if ("1".equals(running)) {
				startButton.doClick();
			}
		}

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  }

	}

	private void scheduleAutoStartTimer() {
		autoStartTimerRunning = true;

		autoStartTimer = new Timer();

		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("Getting info");
				getSessionFromWebsite();
				
			}
		};
		
		autoStartTimer.schedule(task, 2000, 2000);
	}

	public void setPlayable(boolean enabled) {
		this.startButton.setEnabled(enabled);
	}
	public void deactivateBeginMusic() {
		this.firstLaunch = false;
	}
}
