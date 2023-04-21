package fr.toulousescape.ui;

import java.awt.Color;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import org.json.JSONException;
import org.json.JSONObject;

import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.Images;
import fr.toulousescape.util.Player;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.Session;
import fr.toulousescape.util.listeners.TimerListener;

public class ChronoPanel extends JPanel implements TimerListener {

	private static final long serialVersionUID = 8970100750129366954L;

	ArrayList<Thread> threadsList = new ArrayList<Thread>();
	JButton soundButton = new JButton(new ImageIcon(Images.SOUND_IMG));
	JButton startButton = new JButton(new ImageIcon(Images.PLAY_IMG));
	JButton stopButton = new JButton(new ImageIcon(Images.STOP_IMG));
	JButton nextMusicButton = new JButton(new ImageIcon(Images.NEXT_IMG));
	JButton pauseButton = new JButton(new ImageIcon(Images.PAUSE_IMG));
	JButton sessionSearch = new JButton(new ImageIcon(Images.REFRESH_IMG));
	Map<String,String> sessionMap = new HashMap<String,String>();
	boolean paymentRegistered = false;
	Timer autoStartTimer;
	boolean autoStartTimerRunning = false;
	private int musicNumber = 0;
	private static final String NO_SESSION_TEXT = "Récupérer une session pour pouvoir lancer le chrono.";
	boolean paymentDialogFirstTime = true;
	int players = 0;
	JTextField sessionField;
	JLabel sessionInfoLabel = new JLabel(NO_SESSION_TEXT);
	ChronoPanel chronoPanel = this;
	JLabel chronoTime = new JLabel();
	JLabel remainingTimeLabel = new JLabel();

	private int timeSpent = 0;

	private Chrono chrono;

	private Session session;

	private JButton validSetter;

	private Player player;

	private boolean hasPreambuleMusic = false;

	private boolean preambuleMusicIsRunning = false;

	private boolean hasAmbianceMusic = false;

	private boolean hasBeginMusic = false;

	private boolean hasElementsMusic = false;

	private boolean hasFinalMusic = false;
	
	private boolean isPaused = false;

	private boolean firstLaunch = true;

	public String incident = "";
	
	public String discount = "";
	
	private Map<Integer, String> elementsMusic = null;
	
	private Salle salle;
	
	private JButton updateInfosButton = null;
	private JButton incidentButton = null;

	private MainView parent;
	Thread preambuleMusicThread = null;
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
		hasPreambuleMusic = salle.getPreambuleMusic() != null;
		hasAmbianceMusic = salle.getAmbianceMusique() != null;
		hasFinalMusic = salle.getFinalMusic() != null;
		hasBeginMusic = salle.getBeginMusic(0) != null;
		elementsMusic = salle.getElementsMusic();
		hasElementsMusic = elementsMusic != null;
		updateInfosButton = new JButton("Modifier");
		updateInfosButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showPaymentDialog(chronoPanel);
			}
		});
		incidentButton = new JButton("Incident");
		incidentButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showIncidentDialog(chronoPanel);
			}
		});
		//initSession();
		initTitle();
		initTimerSetter();
		initButtons();
	}

	public void initTitle() {
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel,BoxLayout.X_AXIS));
		titlePanel.add(sessionInfoLabel);
		titlePanel.add(updateInfosButton);
		titlePanel.add(incidentButton);
		this.add(titlePanel);
	}

	public void initButtons() {
		JPanel globalPanel = new JPanel(new FlowLayout());
		
		JPanel chronoButtonsPanel = new JPanel(new FlowLayout());
		System.out.println("PREAMBULE : "+hasPreambuleMusic);
		if (hasPreambuleMusic) {
			soundButton.setEnabled(false);
			soundButton.setToolTipText("Veuillez récupérer la session avant de lancer la musique d'ambiance");
			soundButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(hasPreambuleMusic + " " + isPaused);
					String musicToPlay = salle.getPreambuleMusic();
					preambuleMusicThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							//threadsList.add(preambuleMusicThread);
							System.out.println("PLAY!!! " + musicToPlay);
							player.play(salle.getPseudo() + "\\" + musicToPlay);
						}
					});
					preambuleMusicThread.start();
					preambuleMusicIsRunning = true;
				}
			});
			chronoButtonsPanel.add(soundButton);
		}
		
		startButton.setEnabled(false);
		startButton.setToolTipText("Veuillez récupérer la session avant de lancer le chrono");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				soundButton.setEnabled(false);
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
				if (hasPreambuleMusic && preambuleMusicIsRunning) {
					interruptPreambuleMusic();
				}

				
				session.setDate(new Date());
				System.out.println(hasAmbianceMusic + " " + isPaused);
				if (hasAmbianceMusic && !isPaused)
				{

					String musicToPlay = salle.getAmbianceMusique().split(";")[musicNumber];
					musicNumber++;
					if(musicNumber >= salle.getAmbianceMusique().split(";").length) {
						nextMusicButton.setEnabled(false);
						nextMusicButton.setToolTipText("");
					} else {
						String nextMusicToPlay = salle.getAmbianceMusique().split(";")[musicNumber];
						nextMusicButton.setToolTipText(nextMusicToPlay);
					}
					currentMusicThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							threadsList.add(currentMusicThread);
							System.out.println("PLAY!!! " + musicToPlay);
							player.play(salle.getPseudo() + "\\" + musicToPlay);
						}
					});
					currentMusicThread.start();
				}
				if (hasBeginMusic && !isPaused && firstLaunch)
				{
					String musicToPlay = salle.getBeginMusic(players);
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
				paymentDialogFirstTime = true;
				paymentRegistered = false;
				musicNumber = 0;
				nextMusicButton.setEnabled(false);
				stopButton.setEnabled(false);
				startButton.setEnabled(false);
				sessionSearch.setEnabled(true);
				pauseButton.setEnabled(false);
				validSetter.setEnabled(true);
				player.stop();

				session.setRemainingTime(chrono.getCurrentTime());
				session.setTimeSpent(timeSpent);
				if (! "".equals(sessionField.getText())) {
					session.setId(new Integer(sessionField.getText()));
				}
				sessionInfoLabel.setText(NO_SESSION_TEXT);
				sessionInfoLabel.setForeground(Color.BLACK);
				sessionField.setText("");
				chrono.stop();
				
				session.setIncident(incident);
				session.setDiscount(discount);
				incident = "";
				discount = "";
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
				preambuleMusicIsRunning = false;
			}
		});
		chronoButtonsPanel.add(stopButton);
		
		nextMusicButton.setEnabled(false);

		nextMusicButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
				currentMusicThread.interrupt();
				
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
						threadsList.add(currentMusicThread);

						musicNumber++;
						if(musicNumber >= salle.getAmbianceMusique().split(";").length) {
							nextMusicButton.setEnabled(false);
							nextMusicButton.setToolTipText("");
						} else {
							String nextMusicToPlay = salle.getAmbianceMusique().split(";")[musicNumber];
							nextMusicButton.setToolTipText(nextMusicToPlay);
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
		chronoTime.setText(Chrono.formatTime(3600+chrono.getExtraTime()));
		Font chronoFont = new Font("Arial", Font.BOLD, 30);
		chronoTime.setFont(chronoFont);
		globalPanel.add(chronoTime);

		
		this.add(globalPanel);
	}

	public void initTimeLabel() {
		chronoTime.setHorizontalAlignment(JLabel.CENTER);
		chronoTime.setVerticalAlignment(JLabel.CENTER);
		chronoTime.setText(Chrono.formatTime(3600+chrono.getExtraTime()));
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
		
		JSONObject jsonAll = new JSONObject(all);
		
		if (all.trim().equals("nosession")) {
			JOptionPane.showMessageDialog(this, "Merci de synchroniser une session "+room+" via le backend.");
		} else {
			boolean somethingToPay = false;
			String session = all.substring(all.indexOf(":")+2, all.indexOf(",") - 1);
			soundButton.setEnabled(true);
			startButton.setEnabled(true);
			if (! autoStartTimerRunning) {
				scheduleAutoStartTimer();
			}
			String firstname = jsonAll.getString("firstname");
			String lastname = jsonAll.getString("lastname");
			String phone = jsonAll.getString("phone");
			String players = jsonAll.getString("players");
			String running = jsonAll.getString("running");
			String price_category = jsonAll.getString("price_category");
			String special = jsonAll.getString("special");
			String comment = jsonAll.getString("comment");
			String ancv = jsonAll.getString("ancv");
			String amount = jsonAll.getString("amount");
			String payment_type = jsonAll.getString("payment_type");
			
			sessionMap.put("firstname", firstname);
			sessionMap.put("lastname", lastname);
			sessionMap.put("phone", phone);
			sessionMap.put("players", players);
			sessionMap.put("special", special);
			sessionMap.put("price_category", price_category);
			sessionMap.put("comment", comment);
			sessionMap.put("ancv", ancv);
			sessionMap.put("amount", amount);
			sessionMap.put("payment_type", payment_type);
			sessionMap.put("id", session);
			
			this.players = Integer.parseInt(players);
			
			boolean unpaid = (!"0".equals(amount) && "null".equals(payment_type));
			
			if (unpaid) {
				sessionMap.put("unpaid", "1");				
			} else {
				sessionMap.put("unpaid", "0");				
			}
			
			String specialText = "";
			if ("D".equals(special)) {
				specialText = "Découverte - ";
			} else if ("A".equals(special)) {
				specialText = "Version anglaise - ";
			} else if ("B".equals(special)) {
				specialText = "Découverte version anglaise - ";
			}
			String sessionInfos = (specialText +firstname+ " "+ lastname + " ("+phone+") "+players+" joueurs");
			if (! "".equals(comment)) {
				sessionInfos += "<br>"+comment;
			}
			sessionField.setText(session);
			String alertText = "";
			if (ancv != null && ! "".equals(ancv) && ! "0".equals(ancv)) {
				if (!paymentRegistered) {
					sessionInfoLabel.setForeground(Color.RED);
				}
				alertText += "<br>"+ancv+" € de chèques vacances à récupérer";
				somethingToPay = true;
			}
			if (unpaid) {
				if (! paymentRegistered) {
					sessionInfoLabel.setForeground(Color.RED);
				}
				if (! "".equals(alertText)) {
					alertText += "<br>";
				}
				alertText += "Session non payée ("+amount+" €).";
				somethingToPay = true;
			}
			sessionInfoLabel.setText("<html>"+sessionInfos+alertText+"<html>");
			if ("1".equals(running)) {
				startButton.doClick();
			} else if ("2".equals(running) && ! preambuleMusicIsRunning) {
				soundButton.doClick();
			}
			if (somethingToPay && paymentDialogFirstTime && ! paymentRegistered) {
				paymentDialogFirstTime = false;
				showPaymentDialog(this);
			}
			//parent.pack();
		}

	  } catch (MalformedURLException e) {

		e.printStackTrace();

	  } catch (IOException e) {

		e.printStackTrace();

	  } catch (JSONException e) {
		// TODO Auto-generated catch block
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
		this.soundButton.setEnabled(enabled);
		this.startButton.setEnabled(enabled);
	}
	public void deactivateBeginMusic() {
		this.firstLaunch = false;
	}
	public void showPaymentDialog(ChronoPanel chronoPanel) {
		PaymentDialog dialog = new PaymentDialog(getParent(), sessionMap,chronoPanel);
		dialog.openDialog();
	}
	public void showIncidentDialog(ChronoPanel chronoPanel) {
		IncidentDialog dialog = new IncidentDialog(getParent(), chronoPanel);
		dialog.openDialog();
	}

	private void interruptPreambuleMusic() {
		System.out.println("Interruption de "+preambuleMusicThread);
		preambuleMusicThread.stop();
	}
}
