package fr.toulousescape.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;

public class RoomView extends JFrame
{

	private static final long serialVersionUID = -6859566020614563506L;
	
	public RoomView(String name, RoomPanel panel, int rightLocation) {
		super();
		setTitle(name);
		setUndecorated(true);
		//Changer le premier chiffre pour déplacement vers la droite
		setLocation(rightLocation, 0);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//Changer en fonction de la résolution des écrans
		setPreferredSize(new Dimension(1920, 1080));
//		setLayout(new GridBagLayout());
//		GridBagConstraints constraints = new GridBagConstraints();
//		constraints.anchor = GridBagConstraints.CENTER;
		add(panel);
		pack();
//		setExtendedState(MAXIMIZED_BOTH);
	}

}
