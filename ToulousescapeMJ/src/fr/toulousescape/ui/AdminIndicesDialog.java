package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import fr.toulousescape.util.Enigme;
import fr.toulousescape.util.Indice;
import fr.toulousescape.util.IndiceManager;

public class AdminIndicesDialog extends JDialog {

	private static final long serialVersionUID = 7477968157995604129L;
	private boolean creationMode = false;
	private JPanel listPanel;
	private JPanel indicesPanel;
	private Enigme selectedEnigme;
	private Indice selectedIndice;
	private JComboBox<String> typeBox ;
	private JComboBox<String> colorBox ;
	private IndiceManager indiceManager;
	private JButton validateButton;
	private List<Enigme> enigmeList = new ArrayList<Enigme>();
	private List<Indice> indiceList = new ArrayList<Indice>();
	private JPanel detailsPanel = new JPanel(new FlowLayout());
	private JPanel subDetailsPanel = new JPanel(new FlowLayout());
	private JLabel infosLabel = new JLabel();
	private JTextField nameField = new JTextField(15);
	private JLabel textLabel = new JLabel("Texte :");
	private JTextArea textField = new JTextArea(4,20);
	
	private JButton fileButton = new JButton("Choisir le fichier");
	private JLabel fileLabel = new JLabel("Fichier :");
	private JTextField fileName = new JTextField(15);
	
	final JFileChooser fc = new JFileChooser();
	
	private int idx = 0;
	private JComboBox<Indice> emplacementBox = new JComboBox<Indice>();
	int idInFile = 0;
	private int modifiedPosition = 0;
	public AdminIndicesDialog(IndiceManager indiceMngr, JFrame parent) {
		indiceManager = indiceMngr;
		initDialog();
		setLocationRelativeTo(parent);
		setMinimumSize(new Dimension(1000,400));
		setLocationRelativeTo(null);
		repaint();
	}
/*TODO :
	- faire la récupération des indices par énigme / interaction / orphelins
	*/
	private void initDialog() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.PAGE_AXIS));
		this.setTitle("Administration des indices");
		//this.setPreferredSize(new Dimension(800,600));
		listPanel = new JPanel();
		JLabel title = new JLabel("Énigmes");
		mainPanel.add(title);
		listPanel.setLayout(new FlowLayout());
		indicesPanel = new JPanel();
		indicesPanel.setLayout(new FlowLayout());
		loadEnigmes();
		validateButton = new JButton("Enregistrer");
		validateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveUpdates();
			}
		});
		detailsPanel.add(new JLabel("Nom"));
		detailsPanel.add(nameField);
		detailsPanel.add(new JLabel("Placer après :"));
		detailsPanel.add(emplacementBox);
		detailsPanel.add(new JLabel("Couleur :"));
		colorBox = new JComboBox<String>();
		colorBox.addItem("IND");
		colorBox.addItem("INS");
		colorBox.addItem("INF");
		colorBox.addItem("REM");
		colorBox.addItem("POS");
		colorBox.addItem("NEG");
		colorBox.addItem("CODE");
		colorBox.addItem("PART");
		colorBox.addItem("ALT");
		colorBox.addItem("LOL");
		detailsPanel.add(colorBox);
		detailsPanel.add(validateButton);
		detailsPanel.setVisible(false);
		
		subDetailsPanel.add(new JLabel("Type"));
		typeBox = new JComboBox<String>();
		typeBox.addItem(Indice.TYPE_TEXTE);
		typeBox.addItem(Indice.TYPE_SON);
		typeBox.addItem(Indice.TYPE_IMAGE);
		typeBox.addItem(Indice.TYPE_MODULE);
		
		typeBox.addActionListener (new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		        changeType();
		    }
		});
		
		subDetailsPanel.add(typeBox);
		
		subDetailsPanel.add(textLabel);
		subDetailsPanel.add(textField);

		fileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				chooseFile();
			}
		});
		
		subDetailsPanel.add(fileButton);
		subDetailsPanel.add(fileLabel);
		fileName.setEnabled(false);
		subDetailsPanel.add(fileName);
		subDetailsPanel.setVisible(false);
		listPanel.setPreferredSize(new Dimension(400,200));
		mainPanel.add(listPanel);
		mainPanel.add(infosLabel);
		mainPanel.add(indicesPanel);
		mainPanel.add(detailsPanel);
		mainPanel.add(subDetailsPanel);
		this.add(mainPanel);
		pack();
		setVisible(true);
		setModal(true);
	}
	

	protected void showEnigme(int id) {
		detailsPanel.setVisible(false);
		subDetailsPanel.setVisible(false);
		selectedEnigme = null;
		int i = 0;
		String labelTitle  = "Nouvelle énigme";
		selectedEnigme = null;
		indicesPanel.removeAll();
		for (Enigme e : enigmeList) {
			if (e.getId() == id) {
				selectedEnigme = e;
				labelTitle = "Indices de l'énigme "+selectedEnigme.getName();
			}
		}
		if (selectedEnigme == null) {
			labelTitle = "Interactions directes";
			
			selectedEnigme = new Enigme(0,"Interactions","0");
			selectedEnigme.setIndices(indiceManager.getAllInteractions());
		}
		
		for (Indice ind : selectedEnigme.getIndices()) {
			JButton button = new JButton();
			button.setText(ind.getDescription() + "/" + ind.getIndex());
			String toolTipText = ind.getTexte();
			button.setToolTipText(toolTipText);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showIndice(ind.getId());
				}
			});
			indicesPanel.add(button);
			
		}
		// Add enigme button
		JButton button = new JButton("Nouvel indice");
		button.setBackground(Color.WHITE);
		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showIndice(0);
			}
		});
		indicesPanel.add(button);
		pack();
		setLocationRelativeTo(null);

		
		infosLabel.setText(labelTitle);
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
	private void computeAllIndice()
	{
		computeIndiceList(indiceManager.getAllIndices());
	}
	
	private void computeIndiceList(List<Indice> indices) {
		indiceList.clear();
		for (Indice i : indices) {
			indiceList.add(i);
		}
	}
	private void saveUpdates() {
		// Calculate new index
		int newIdx;
		if (emplacementBox.getSelectedItem() == null) {
			newIdx = 0;
		} else {
			Indice ind = (Indice) emplacementBox.getSelectedItem();
			newIdx = ind.getIndex();
		}
		// Update indexes
		String idxToSet = idx + "";
		if (newIdx + 1 > idx ) {
			for (Indice in : selectedEnigme.getIndices()) {
				if (in.getIndex() > idx && in.getIndex() <= newIdx) {
					in.setIndex(in.getIndex() -1);
				}
			}
			idxToSet = (newIdx) + "";
		} else if (newIdx + 1 < idx) {
			for (Indice in : selectedEnigme.getIndices()) {
				if (in.getIndex() > newIdx && in.getIndex() < idx) {
					in.setIndex(in.getIndex() +1);
				}
			}
			idxToSet = (newIdx + 1) + "";
		}
		String type = typeBox.getSelectedItem().toString();
		String img = null;
		String snd = null;
		String color = colorBox.getSelectedItem().toString();
		String function = "";
		String text = textField.getText();
		boolean music = false;
		if (Indice.TYPE_IMAGE.equals(type)) {
			img = fileName.getText().toString();
			text = "";
		}
		if (Indice.TYPE_SON.equals(type)) {
			snd = fileName.getText().toString();
		}
		if (Indice.TYPE_MODULE.equals(type)) {
			function = textField.getText().toString();
			text = "";
		}
		if (creationMode) {
			// Creation mode
			//	public Indice(int id, String description, String image, String texte, String son, String type, String color, String index, String function, boolean music) {
			System.out.println("ENIGME ID : "+selectedEnigme.getId());
			Indice newI = new Indice(idInFile, nameField.getText(), img,text, snd, type, color, idxToSet, function, music,selectedEnigme.getId());
			indiceList.add(newI);
		} else {
			// Update mode
			Indice newI = new Indice(idInFile, nameField.getText(), img,text, snd, type, color, idxToSet, function, music, selectedEnigme.getId());
			int indiceIndexInList = 0;
			int i = 0;
			for (Indice search : indiceList) {
				if (search.getId() == idInFile) {
					indiceIndexInList = i;
				}
				i++;
			}
			
			indiceList.set(indiceIndexInList, newI);
		}
		
		indiceManager.updateIndices(indiceList);
		loadEnigmes();
		
		detailsPanel.setVisible(false);
		subDetailsPanel.setVisible(false);
		indicesPanel.removeAll();
		infosLabel.setText("");
	}
	private void loadEnigmes() {
		indiceManager.reloadIndices();
		listPanel.removeAll();
		computeAllEnigme();
		computeAllIndice();
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
		JButton button = new JButton("[Interactions]");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showEnigme(0);
			}
		});
		listPanel.add(button);
		pack();
	}
	protected void showIndice(int id) {
		idInFile = id;
		if (id == 0) {
			creationMode = true;
		} else {
			creationMode = false;
		}
		typeBox.setSelectedItem("txt");
		emplacementBox.removeAllItems();
		emplacementBox.addItem(null);
		Indice selected = null;
		int i = 0;
		modifiedPosition = 0;
		int justBefore = 0;
		int determinedJustBefore = 0;
		String labelTitle  = "Nouvel indice";
		String indiceDesc = "Nouvel indice";
		String text = "Entrez ici le texte qui sera affiché sur les écrans";
		int indiceIndex = 0;
		int indiceId = 0;
		int greatestId = 0;
		for (Indice ind : selectedEnigme.getIndices()) {
			justBefore = i;
			if (ind.getId() == id) {
				selectedIndice = ind;
				idx = ind.getIndex();
				selected = ind;
				modifiedPosition = i;
				labelTitle = "Indice "+selected.getDescription();
				indiceDesc = selected.getDescription();
				indiceIndex = selected.getIndex();
				indiceId = selected.getId ();
				text = selected.getTexte();
				determinedJustBefore = justBefore;
				typeBox.setSelectedItem(selected.getType());
				if (selected.getColor() != null) {
					colorBox.setSelectedItem(selected.getColor());
				} else {
					colorBox.setSelectedIndex(0);
				}
				if (selected.getType().equals(Indice.TYPE_MODULE)) {
					text = selected.getFunction();
				}
			} else {
				emplacementBox.addItem(ind);
			}
			if (ind.getId() > greatestId) {
				greatestId = ind.getId();
			}
			i++;
		}
		if (id == 0) {
			determinedJustBefore = i;
			indiceIndex = i + 1;
			indiceId = greatestId + 1;
		}
		emplacementBox.setSelectedIndex(determinedJustBefore);
		emplacementBox.setEnabled(! creationMode);
		infosLabel.setText(labelTitle);
		nameField.setText(indiceDesc);
		textField.setText(text);
		detailsPanel.setVisible(true);
		subDetailsPanel.setVisible(true);
		idx = indiceIndex;
		idInFile = indiceId;
		this.pack();
		this.setLocationRelativeTo(null);
	}
	private void chooseFile() {
		int returnVal = fc.showOpenDialog(this);

	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	        File file = fc.getSelectedFile();
	        String destinationPath = "src\\resources\\" + IndiceManager.indiceURL() + file.getName();
	        
	        try {
				Files.copy(file.toPath(), new File(destinationPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
				  fileName.setText(file.getName());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	      
	    }
		
	}
	private void changeType() {
		switch(typeBox.getSelectedItem().toString()) {
			case Indice.TYPE_TEXTE:
				textLabel.setText("Texte :");
				textLabel.setVisible(true);
				textField.setVisible(true);
				fileButton.setVisible(false);
				fileLabel.setVisible(false);
				fileName.setVisible(false);
				pack();
				break;
			case Indice.TYPE_IMAGE:
				textLabel.setVisible(false);
				textField.setVisible(false);
				fileButton.setVisible(true);
				fileLabel.setVisible(true);
				fileName.setVisible(true);
				if (selectedIndice != null) {
					fileName.setText(selectedIndice.getImageName());					
				} else {
					fileName.setText("");					
				}
				pack();
				break;
			case Indice.TYPE_SON:
				textLabel.setVisible(false);
				textField.setVisible(false);
				fileButton.setVisible(true);
				fileLabel.setVisible(true);
				fileName.setVisible(true);
				if (selectedIndice != null) {
					fileName.setText(selectedIndice.getSon());					
				} else {
					fileName.setText("");					
				}
				pack();
				break;
			case Indice.TYPE_MODULE:
				textLabel.setText("Nom de la fonction :");
				textLabel.setVisible(true);
				textField.setVisible(true);
				fileButton.setVisible(false);
				fileLabel.setVisible(false);
				fileName.setVisible(false);
				pack();
				break;
		}
	}
}
