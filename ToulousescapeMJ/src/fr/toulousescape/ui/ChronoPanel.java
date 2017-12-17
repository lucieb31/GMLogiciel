package fr.toulousescape.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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
import fr.toulousescape.util.SallesProperties;
import fr.toulousescape.util.Session;
import fr.toulousescape.util.listeners.TimerListener;

public class ChronoPanel extends JPanel implements TimerListener {

	private static final long serialVersionUID = 8970100750129366954L;

	JButton startButton = new JButton(new ImageIcon(Images.PLAY_IMG));
	JButton stopButton = new JButton(new ImageIcon(Images.STOP_IMG));
	JButton pauseButton = new JButton(new ImageIcon(Images.PAUSE_IMG));

	JLabel chronoTime = new JLabel();
	JLabel remainingTimeLabel = new JLabel();

	private int timeSpent = 0;

	private Chrono chrono;

	private Session session;

	private JButton validSetter;

	private Player player;
	
	private Properties props;
	
	private boolean hasAmbianceMusic = false;
	
	private boolean hasFinalMusic = false;
	
	private boolean isPaused = false;

	public ChronoPanel(Chrono c, Session s, Player p, Properties salleProperties) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		chrono = c;
		chrono.addTimerListener(this);
		session = s;
		player = p;
		props = salleProperties;
		hasAmbianceMusic = props.getProperty(SallesProperties.MUSIC_TO_PLAY) != null;
		hasFinalMusic = props.getProperty(SallesProperties.MUSIC_FINAL) != null;
		initTitle();
		initTimerSetter();
		initChangeTimePanel();
		initButtons();
		initTimeLabel();
	}

	public void initTitle() {
		JLabel chronoLabel = new JLabel();
		chronoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		chronoLabel.setVerticalAlignment(SwingConstants.CENTER);
		chronoLabel.setText("Chronomètre :");
		this.add(chronoLabel);
	}

	public void initButtons() {
		JPanel chronoButtonsPanel = new JPanel(new FlowLayout());
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startButton.setEnabled(false);
				pauseButton.setEnabled(true);
				stopButton.setEnabled(true);
				validSetter.setEnabled(false);
				session.setDate(new Date());
				System.out.println(hasAmbianceMusic + " " + isPaused);
				if (hasAmbianceMusic && !isPaused)
				{
					String musicToPlay = props.getProperty(SallesProperties.MUSIC_TO_PLAY);
					new Thread(new Runnable() {

						@Override
						public void run() {
							System.out.println("PLAY!!! " + musicToPlay);
							player.play(musicToPlay);
						}
					}).start();
				}
				chrono.start();
				isPaused = false;
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
				stopButton.setEnabled(false);
				startButton.setEnabled(true);
				pauseButton.setEnabled(false);
				validSetter.setEnabled(true);
				player.stop();

				// TODO : Afficher temps passé et temps restant en seconde (pour
				// le score)
				session.setRemainingTime(chrono.getCurrentTime());
				session.setTimeSpent(timeSpent);
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
				session.setDate(null);
				timeSpent = 0;
				isPaused = false;
			}
		});
		chronoButtonsPanel.add(stopButton);
		this.add(chronoButtonsPanel);
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
		JPanel timerSetterPanel = new JPanel(new FlowLayout());

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

		this.add(timerSetterPanel);
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

		chronoTime.setText(formatTime(currentTime));

		if (currentTime == 0 && hasFinalMusic) {
			String finalMusic = props.getProperty(SallesProperties.MUSIC_FINAL);
			new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("PLAY 0 !!! " + finalMusic);
					player.stop();
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					player.play(finalMusic);
				}
			}).start();
		}

		// Calculate remaining time
		timeSpent++;
	}

	private String formatTime(int timeToFormat) {
		// Calculate current time
		int hour = timeToFormat / 3600;
		int minutes = (timeToFormat % 3600) / 60;
		int secondes = ((timeToFormat - minutes * 60) % 60);

		String s = String.valueOf(secondes);

		if (secondes < 10) {
			s = "0" + s;
		}

		String m = String.valueOf(minutes);
		if (minutes < 10) {
			m = "0" + m;
		}

		if (hour > 0) {
			return hour + ":" + m + ":" + s;
		} else if (minutes > 0) {
			return m + ":" + s;
		} else {
			return s + "'";
		}
	}
}
