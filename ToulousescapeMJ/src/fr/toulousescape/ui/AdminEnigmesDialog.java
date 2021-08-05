package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.toulousescape.util.Enigme;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.IndiceManager;

public class AdminEnigmesDialog extends JDialog {

	private static final long serialVersionUID = 7477968157995604129L;
	private boolean creationMode = false;
	private JPanel listPanel;
	private IndiceManager indiceManager;
	private JButton validateButton;
	private List<Enigme> enigmeList = new ArrayList<Enigme>();
	private JPanel detailsPanel = new JPanel(new FlowLayout());
	private JLabel infosLabel = new JLabel();
	private JTextField nameField = new JTextField(15);
	private int idx = 0;
	private JComboBox<Enigme> emplacementBox = new JComboBox<Enigme>();
	int idInFile = 0;
	private int modifiedPosition = 0;
	public AdminEnigmesDialog(IndiceManager indiceMngr, JFrame parent) {
		indiceManager = indiceMngr;
		initDialog();
		setLocationRelativeTo(parent);
		setMinimumSize(new Dimension(1000,400));
		setLocationRelativeTo(null);
		repaint();
	}

	private void initDialog() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
		this.setTitle("Administration des énigmes");
		//this.setPreferredSize(new Dimension(800,600));
		listPanel = new JPanel();
		JLabel title = new JLabel("Énigmes");
		mainPanel.add(title);
		listPanel.setLayout(new FlowLayout());
		loadEnigmes();
		validateButton = new JButton("Enregistrer");
		validateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveUpdates();
			}
		});
		detailsPanel.add(new JLabel("Nom de l'énigme :"));
		detailsPanel.add(nameField);
		detailsPanel.add(new JLabel("Placer après :"));
		detailsPanel.add(emplacementBox);
		detailsPanel.add(validateButton);
		detailsPanel.setVisible(false);
		listPanel.setPreferredSize(new Dimension(400,200));
		mainPanel.add(listPanel);
		mainPanel.add(infosLabel);
		mainPanel.add(detailsPanel);
		this.add(mainPanel);
		pack();
		setVisible(true);
		setModal(true);
	}
	

	protected void showEnigme(int id) {
		if (id == 0) {
			creationMode = true;
		} else {
			creationMode = false;
		}
		emplacementBox.removeAllItems();
		emplacementBox.addItem(null);
		Enigme selected = null;
		int i = 0;
		modifiedPosition = 0;
		int justBefore = 0;
		int determinedJustBefore = 0;
		String labelTitle  = "Nouvelle énigme";
		String enigmeName = "Nouvelle énigme";
		int enigmeIndex = 0;
		int enigmeId = 0;
		int greatestId = 0;
		for (Enigme e : enigmeList) {
			justBefore = i;
			if (e.getId() == id) {
				selected = e;
				modifiedPosition = i;
				labelTitle = "Énigme "+selected.getName();
				enigmeName = selected.getName();
				enigmeIndex = selected.getIndex();
				enigmeId = selected.getId ();
				determinedJustBefore = justBefore;
			} else {
				emplacementBox.addItem(e);
			}
			if (e.getId() > greatestId) {
				greatestId = e.getId();
			}
			i++;
		}
		if (id == 0) {
			determinedJustBefore = i;
			enigmeIndex = i + 1;
			enigmeId = greatestId + 1;
		}
		emplacementBox.setSelectedIndex(determinedJustBefore);
		emplacementBox.setEnabled(! creationMode);
		infosLabel.setText(labelTitle);
		nameField.setText(enigmeName);
		detailsPanel.setVisible(true);
		idx = enigmeIndex;
		idInFile = enigmeId;
		this.pack();
	}

	private void computeAllEnigme()
	{
		computeEnigmeList(indiceManager.getAllEnigmes());
	}
	
	private void computeEnigmeList(List<Enigme> enigmes) {
		enigmeList.clear();
		for (Enigme i : enigmes) {
			enigmeList.add(i);
		}
	}
	private void saveUpdates() {
		// Calculate new index
		int newIdx;
		if (emplacementBox.getSelectedItem() == null) {
			newIdx = 0;
		} else {
			Enigme e = (Enigme) emplacementBox.getSelectedItem();
			newIdx = e.getIndex();
		}
		// Update indexes
		String idxToSet = idx + "";
		if (newIdx + 1 > idx ) {
			for (Enigme en : enigmeList) {
				if (en.getIndex() > idx && en.getIndex() <= newIdx) {
					en.setIndex(en.getIndex() -1);
				}
			}
			idxToSet = (newIdx) + "";
		} else if (newIdx + 1 < idx) {
			for (Enigme en : enigmeList) {
				if (en.getIndex() > newIdx && en.getIndex() < idx) {
					en.setIndex(en.getIndex() +1);
				}
			}
			idxToSet = (newIdx + 1) + "";
		}
		if (creationMode) {
			// Creation mode
			Enigme e = new Enigme(idInFile, nameField.getText(), idxToSet);
			enigmeList.add(e);
		} else {
			// Update mode
			Enigme e = new Enigme(idInFile, nameField.getText(), idxToSet);
			enigmeList.set(modifiedPosition, e);
		}
		
		indiceManager.updateEnigmes(enigmeList);
		loadEnigmes();
		detailsPanel.setVisible(false);
		infosLabel.setText("");
	}
	private void loadEnigmes() {
		indiceManager.reloadIndices();
		listPanel.removeAll();
		computeAllEnigme();
		for (Enigme en : enigmeList) {
			
			JButton button = new JButton();
			button.setText(en.getName() + "/"+en.getIndex());
			String toolTipText = "Aucun indice";
			toolTipText = "Index : "+en.getIndex();
			button.setToolTipText(toolTipText);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showEnigme(en.getId());
				}
			});
			listPanel.add(button);
		}
		// Add enigme button
		JButton button = new JButton("Nouvelle énigme");
		button.setBackground(Color.white);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showEnigme(0);
			}
		});
		listPanel.add(button);
		pack();
	}
	
}
