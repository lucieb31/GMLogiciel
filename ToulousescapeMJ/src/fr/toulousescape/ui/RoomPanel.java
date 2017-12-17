package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import fr.toulousescape.util.Chrono;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.listeners.IndiceListener;
import fr.toulousescape.util.listeners.TimerListener;

public class RoomPanel extends JPanel implements TimerListener, IndiceListener{

	private static final long serialVersionUID = 815643021921333105L;
	private JLabel chronoLabel;
	private JLabel clueLabel;
	private JTextArea clueText;
	
	public RoomPanel(Chrono chrono) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		JPanel chronoPanel = new JPanel(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		chronoLabel = new JLabel("1:00:00");
		chronoLabel.setHorizontalAlignment(JLabel.CENTER);
		chronoLabel.setFont(new Font("Arial", Font.BOLD, 250));
		chronoPanel.add(chronoLabel, constraints);
		this.add(chronoPanel);
		
		JPanel labelPanel = new JPanel(new GridBagLayout());
		clueLabel = new JLabel();
		clueLabel.setFont(new Font("Arial", Font.BOLD, 90));
//		clueLabel.setPreferredSize(new Dimension(1500, 1000));
		labelPanel.add(clueLabel, constraints);
		this.add(labelPanel);
		
		clueText = new JTextArea();
		clueText.setPreferredSize(new Dimension(1500, 300));
		clueText.setFont(new Font("Arial", Font.BOLD, 90));
		clueText.setLineWrap(true);
		clueText.setBackground(labelPanel.getBackground());
		clueText.setWrapStyleWord(true);
		clueText.setEditable(false);
		labelPanel.add(clueText, constraints);
		
		chrono.addTimerListener(this);
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
			chronoLabel.setForeground(Color.RED);
		}
		else
		{
			chronoLabel.setForeground(Color.BLACK);
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
