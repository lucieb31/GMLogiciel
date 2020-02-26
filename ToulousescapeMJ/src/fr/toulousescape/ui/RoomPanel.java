package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.SallesProperties;
import fr.toulousescape.util.listeners.IndiceListener;
import fr.toulousescape.util.listeners.TimerListener;

public class RoomPanel extends JPanel implements TimerListener, IndiceListener{

	private static final long serialVersionUID = 815643021921333105L;
	private JLabel chronoLabel;
	private JLabel clueLabel;
	private JTextArea clueText;
	private Color normalBackgroundColor;
	private Color endBackgroundColor;
	private Color normalForegroundColor;
	private Color endForegroundColor;
	private JPanel chronoPanel;
	private JPanel labelPanel;
	private Font classic = new Font("Arial", Font.BOLD, 90);
	private Font little = new Font("Arial", Font.BOLD, 70);
	private Font verylittle = new Font("Arial", Font.BOLD, 50);
	public RoomPanel(Chrono chrono, Properties p) {
		normalBackgroundColor = getPropertyColor(p,SallesProperties.BACKGROUND_COLOR_NORMAL);
		endBackgroundColor = getPropertyColor(p,SallesProperties.BACKGROUND_COLOR_END);
		normalForegroundColor = getPropertyColor(p,SallesProperties.FOREGROUND_COLOR_NORMAL);
		endForegroundColor = getPropertyColor(p,SallesProperties.FOREGROUND_COLOR_END);
		
		this.setBackground(normalBackgroundColor);
		this.repaint();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		chronoPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		chronoLabel = new JLabel("1:00:00");
		chronoLabel.setHorizontalAlignment(JLabel.CENTER);
		chronoLabel.setFont(new Font("Arial", Font.BOLD, 250));
		chronoLabel.setForeground(normalForegroundColor);
		chronoLabel.setBackground(normalBackgroundColor);
		chronoPanel.setForeground(normalForegroundColor);
		chronoPanel.setBackground(normalBackgroundColor);
		chronoPanel.add(chronoLabel, constraints);
		this.add(chronoPanel);
		
		labelPanel = new JPanel(new GridBagLayout());
		clueLabel = new JLabel();
		clueLabel.setFont(new Font("Arial", Font.BOLD, 90));
//		clueLabel.setPreferredSize(new Dimension(1500, 1000));
		labelPanel.setForeground(normalForegroundColor);
		labelPanel.setBackground(normalBackgroundColor);
		labelPanel.add(clueLabel, constraints);
		this.add(labelPanel);
		
		clueText = new JTextArea();
		clueText.setPreferredSize(new Dimension(1500, 300));
		clueText.setFont(new Font("Arial", Font.BOLD, 90));
		clueText.setLineWrap(true);
		clueText.setBackground(labelPanel.getBackground());
		clueText.setForeground(labelPanel.getForeground());
		clueText.setWrapStyleWord(true);
		clueText.setEditable(false);
		labelPanel.add(clueText, constraints);
		
		chrono.addTimerListener(this);
	}

	private Color getPropertyColor(Properties p, String colorType) {
		String data = p.getProperty(colorType);
		System.out.println("Raw color : "+data);
		if (data != null) {
			String[] elements = data.split(",");
			if (elements != null && elements.length == 3) {
				System.out.println("Color is ok");
				return new Color(new Integer(elements[0]),new Integer(elements[1]), new Integer(elements[2]));
			}
		}
		if (colorType.equals(SallesProperties.BACKGROUND_COLOR_NORMAL) || colorType.equals(SallesProperties.BACKGROUND_COLOR_END)) {
			return Color.WHITE;
		} else if (colorType.equals(SallesProperties.FOREGROUND_COLOR_NORMAL)) {
			return Color.BLACK;
		} else if (colorType.equals(SallesProperties.FOREGROUND_COLOR_END)) {
			return Color.RED;
		}
		return Color.BLACK;
	}

	@Override
	public void timeChanged(int currentTime) {
		int hour = currentTime / 3600;
		int minutes = (currentTime % 3600) / 60;
		int secondes = ((currentTime - minutes * 60) % 60);

		String s = String.valueOf(secondes);

		if (secondes < 10) {
			s = "0" + s;
		}
		
		String m = String.valueOf(minutes);
		if(minutes < 10)
		{
			m = "0" + m;
		}
		
		if (minutes < 5 && hour == 0)
		{
			chronoLabel.setForeground(endForegroundColor);
			chronoLabel.setBackground(endBackgroundColor);
			clueText.setForeground(endForegroundColor);
			clueText.setBackground(endBackgroundColor);
			chronoPanel.setBackground(endBackgroundColor);
			labelPanel.setBackground(endBackgroundColor);
		}
		else
		{
			chronoLabel.setForeground(normalForegroundColor);
			chronoLabel.setBackground(normalBackgroundColor);
			clueText.setForeground(normalForegroundColor);
			clueText.setBackground(normalBackgroundColor);
			chronoPanel.setBackground(normalBackgroundColor);
			labelPanel.setBackground(normalBackgroundColor);

		}
		if (hour > 0)
		{
			chronoLabel.setText(hour + ":" + m + ":" + s);
		}
		else
		{
			chronoLabel.setText(m + ":" + s);
		}
	}

	@Override
	public void showIndice(Indice indice) {
		String texte = indice.getTexte();
		clueText.setText("");
		clueLabel.setText("");
		clueLabel.setIcon(null);
		if (texte != null) {
			System.out.println("TAILLE : "+texte.length());
			clueText.setFont(classic);
			if (texte.length() > 90) {
				clueText.setFont(little);
			}
			if (texte.length() > 120) {
				clueText.setFont(verylittle);
			}
			clueText.setEditable(true);
			clueText.setText(indice.getTexte());
			clueText.setVisible(true);
			clueLabel.setVisible(false);
		} else {
			clueLabel.setVisible(true);
			clueLabel.setIcon(indice.getImage());
			clueText.setVisible(false);
		}
	}

}
