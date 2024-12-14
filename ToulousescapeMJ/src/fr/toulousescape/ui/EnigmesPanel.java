package fr.toulousescape.ui;

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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.toulousescape.util.Enigme;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.IndiceManager;
import fr.toulousescape.util.Salle;
import fr.toulousescape.util.Session;
import fr.toulousescape.util.listeners.EnigmeListener;

public class EnigmesPanel extends JPanel {

	private static final long serialVersionUID = -8515748419839056660L;

	private IndiceManager manager;

	private JLabel enigmeLabel;

	private List<EnigmeListener> listeners = new ArrayList<>();

	private List<Enigme> enigmeList = new ArrayList<Enigme>();

	public EnigmesPanel(IndiceManager m, Session s, Salle salle) {
		manager = m;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEtchedBorder());
		initTitle();
		initListPanel();
	}

	private void initTitle() {
		JLabel title = new JLabel("Énigmes");
		this.add(title);
	}

	private void initListPanel() {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		
		computeAllEnigme();
		for (Enigme en : enigmeList) {
			JButton button = new JButton();
			button.setText(en.getName());
			String toolTipText = "Aucun indice";
			if (en.getIndices() != null && en.getIndices().size() > 0) {
				if (en.getIndices().size() == 1) {
					toolTipText = "1 indice";
				} else {
					toolTipText = en.getIndices().size()+" indices";
				}
			}
			button.setToolTipText(toolTipText);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showIndices(en.getIndex());
				}
			});
			listPanel.add(button);
		}
		
		this.add(listPanel);
	}

	protected void showIndices(int index) {
		Enigme chosen = manager.getEnigmeByIndex(index);
		if (chosen != null) {
			fireShowIndices(chosen.getIndices());			
		}
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

	public JLabel getIndiceLabel() {
		return enigmeLabel;
	}

	private void computeAllEnigme()
	{
		computeEnigmeList(manager.getAllEnigmes());
	}
	
	private void computeEnigmeList(List<Enigme> enigmes) {
		enigmeList.clear();
		for (Enigme i : enigmes) {
			enigmeList.add(i);
		}
	}

	public void addListeners(EnigmeListener listen) {
		listeners.add(listen);
	}

	public void fireShowIndices(List<Indice> indices) {
		for (EnigmeListener l : listeners) {
			l.showIndices(indices);
		}
	}

	public IndiceManager getManager() {
		return manager;
	}
}
