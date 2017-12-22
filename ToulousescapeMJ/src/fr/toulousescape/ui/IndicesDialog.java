package fr.toulousescape.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.toulousescape.util.Indice;
import fr.toulousescape.util.IndiceManager;

public class IndicesDialog extends JDialog {

	private static final long serialVersionUID = 7477968157995604129L;
	
	private JPanel indicesPanel;
	
	private Map<JTextField, JTextField> indicesTextToAdd;
	private Map<JTextField, JTextField> indicesImgToAdd;
	private Map<JTextField, JTextField> indicesSonToAdd;
	
	private Map<Indice, JCheckBox> allIndiceCheckbox;
	private Map<Indice, JTextField[]> allIndiceTextField;
	
	private IndiceManager indiceManager;
	
	public IndicesDialog(IndiceManager indiceMngr, JFrame parent) {
		indiceManager = indiceMngr;

		setLocationRelativeTo(parent);
	}

	private JPanel getAddButton() {
		JPanel addPanel = new JPanel(new FlowLayout());
		
		JButton addTextButton = new JButton("Texte");
		addTextButton.setToolTipText("Ajouter un indice de texte");
		addTextButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel textIndicePanel = new JPanel();
				GridLayout layout = new GridLayout(2, 2);
				textIndicePanel.setLayout(layout);
				textIndicePanel.setBorder(BorderFactory.createEtchedBorder());
				
				JLabel name = new JLabel("Nom");
				textIndicePanel.add(name);
				
				JTextField nameField = new JTextField();
				textIndicePanel.add(nameField);
				
				JLabel contains = new JLabel("Texte");
				textIndicePanel.add(contains);
				
				JTextField containsField = new JTextField();
				textIndicePanel.add(containsField);
				
				indicesTextToAdd.put(nameField, containsField);
				
				indicesPanel.add(textIndicePanel);
				indicesPanel.repaint();
				pack();
			}
		});
		addPanel.add(addTextButton);
		
		JButton addImageButton = new JButton("Image");
		addImageButton.setToolTipText("Ajouter un indice avec une image");
		addImageButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (indicesImgToAdd.isEmpty())
				{
					JOptionPane.showMessageDialog(addPanel,
							"Attention pour les indices images vérifiez bien que le nom soit le bon.\nPlacer les images dans le dossier src\\resources\\");
				}
				JPanel textIndicePanel = new JPanel();
				GridLayout layout = new GridLayout(2, 2);
				textIndicePanel.setLayout(layout);
				textIndicePanel.setBorder(BorderFactory.createEtchedBorder());
				
				JLabel name = new JLabel("Nom");
				textIndicePanel.add(name);
				
				JTextField nameField = new JTextField();
				textIndicePanel.add(nameField);
				
				JLabel contains = new JLabel("Nom du fichier");
				textIndicePanel.add(contains);
				
				JTextField containsField = new JTextField();
				textIndicePanel.add(containsField);
				
				indicesImgToAdd.put(nameField, containsField);
				
				indicesPanel.add(textIndicePanel);
				indicesPanel.repaint();
				pack();
			}
		});
		addPanel.add(addImageButton);
		
		JButton addSonButton = new JButton("Son");
		addSonButton.setToolTipText("Ajouter un indice avec du son");
		addSonButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (indicesImgToAdd.isEmpty())
				{
					JOptionPane.showMessageDialog(addPanel,
							"Attention pour les indices son vérifiez bien que le nom soit le bon.\nPlacer les son dans le dossier src\\resources\\");
				}
				JPanel textIndicePanel = new JPanel();
				GridLayout layout = new GridLayout(2, 2);
				textIndicePanel.setLayout(layout);
				textIndicePanel.setBorder(BorderFactory.createEtchedBorder());
				
				JLabel name = new JLabel("Nom");
				textIndicePanel.add(name);
				
				JTextField nameField = new JTextField();
				textIndicePanel.add(nameField);
				
				JLabel contains = new JLabel("Nom du fichier");
				textIndicePanel.add(contains);
				
				JTextField containsField = new JTextField();
				textIndicePanel.add(containsField);
				
				indicesSonToAdd.put(nameField, containsField);
				
				indicesPanel.add(textIndicePanel);
				indicesPanel.repaint();
				pack();
			}
		});
		addPanel.add(addSonButton);
		
		return addPanel;
	}
	
	private JPanel getCreateButton()
	{
		JPanel createPanel = new JPanel();
		createPanel.setLayout(new FlowLayout());
		
		JButton createButton = new JButton("Valider");
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Map<String, String> indicesTextToWrite = new HashMap<>();
				Map<String, String> indicesImgToWrite = new HashMap<>();
				Map<String, String> indicesSonToWrite = new HashMap<>();
				
				for(JTextField txtField : indicesTextToAdd.keySet())
				{
					String desc = txtField.getText();
					String text = indicesTextToAdd.get(txtField).getText();
					
					if (desc != null && !desc.isEmpty() && text != null && !text.isEmpty())
					{
						indicesTextToWrite.put(desc, text);
					}
				}
				
				for(JTextField txtField : indicesImgToAdd.keySet())
				{
					String desc = txtField.getText();
					String text = indicesImgToAdd.get(txtField).getText();
					
					if (desc != null && !desc.isEmpty() && text != null && !text.isEmpty())
					{
						indicesImgToWrite.put(desc, text);
					}
				}
				
				for(JTextField txtField : indicesSonToAdd.keySet())
				{
					String desc = txtField.getText();
					String text = indicesSonToAdd.get(txtField).getText();
					if (desc != null && !desc.isEmpty() && text != null && !text.isEmpty())
					{
						indicesSonToWrite.put(desc, text);
					}
				}
				indiceManager.writeIndiceInFile(indicesTextToWrite, indicesImgToWrite, indicesSonToWrite);
				JOptionPane.showMessageDialog(indicesPanel, "Les indices ont été ajoutés. Vous devez rafraichir la liste d'indice.");
				dispose();
			}
		});
		createButton.setEnabled(true);
		createPanel.add(createButton);
		
		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		createPanel.add(cancelButton);
		
		return createPanel;
	}

	public void openAsCreate() {
		indicesTextToAdd = new HashMap<>();
		indicesImgToAdd = new HashMap<>();
		indicesSonToAdd = new HashMap<>();
		
		setTitle("Création d'indices");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(getAddButton());
		indicesPanel = new JPanel();
		indicesPanel.setLayout(new BoxLayout(indicesPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(indicesPanel);
		mainPanel.add(getCreateButton());
		add(mainPanel);
		
		pack();
		setVisible(true);
	}
	
	public void openAsRemove()
	{
		setTitle("Suppression d'indices");
		
		JPanel mainPanel =  new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(getListIndice());
		mainPanel.add(getRemoveButton());
		add(mainPanel);
		
		pack();
		setVisible(true);
	}

	private JPanel getRemoveButton() {
		JPanel removePanel = new JPanel();
		removePanel.setLayout(new FlowLayout());
		
		JButton createButton = new JButton("Valider");
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<Indice> indicesToRemove = new ArrayList<>();
				for (Indice indice : allIndiceCheckbox.keySet())
				{
					if (allIndiceCheckbox.get(indice).isSelected())
					{
						indicesToRemove.add(indice);
					}
				}
				indiceManager.removeIndice(indicesToRemove);	
				JOptionPane.showMessageDialog(indicesPanel, "Les indices ont été supprimés. Vous devez rafraichir la liste d'indice.");
				dispose();
			}
		});
		createButton.setEnabled(true);
		removePanel.add(createButton);
		
		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		removePanel.add(cancelButton);
		
		return removePanel;
	}

	private JPanel getListIndice() {
		allIndiceCheckbox = new HashMap<>();
		JPanel removePanel = new JPanel();
		removePanel.setLayout(new BoxLayout(removePanel, BoxLayout.PAGE_AXIS));
		List<Indice> allIndices = indiceManager.getAllIndices();
		
		for(Indice indice : allIndices)
		{
			JCheckBox chckBox = new JCheckBox(indice.getDescription());
			chckBox.setToolTipText(indice.getTexte() == null ? indice.getImageName() : indice.getTexte());
			allIndiceCheckbox.put(indice, chckBox);
			removePanel.add(chckBox);
		}
		
		removePanel.repaint();
		return removePanel;
	}
	
	public void openAsUpdate()
	{
		setTitle("Mise à jour d'indices");
		
		JPanel mainPanel =  new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(getListIndice(true));
		mainPanel.add(getUpdateButton());
		add(mainPanel);
		
		pack();
		setVisible(true);
	}

	private JPanel getUpdateButton() {
		JPanel updatePanel = new JPanel();
		updatePanel.setLayout(new FlowLayout());
		
		JButton createButton = new JButton("Valider");
		createButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				List<Indice> indicesToUpdate = new ArrayList<>();
				for (Indice indice : allIndiceTextField.keySet())
				{
					JTextField[] values = allIndiceTextField.get(indice);
					indice.setDescription(values[0].getText());
					if (indice.getTexte() != null)
						indice.setTexte(values[1].getText());
					else if (indice.getImageName() != null)
						indice.setImageName(values[1].getText());
					else
						indice.setSon(values[1].getText());
					indicesToUpdate.add(indice);
				}
				indiceManager.updateIndices(indicesToUpdate);	
				JOptionPane.showMessageDialog(indicesPanel, "Les indices ont été modifiés. Vous devez rafraichir la liste d'indice.");
				dispose();
			}
		});
		createButton.setEnabled(true);
		updatePanel.add(createButton);
		
		JButton cancelButton = new JButton("Annuler");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		updatePanel.add(cancelButton);
		
		return updatePanel;
	}

	private JPanel getListIndice(boolean needUpdate) {
		allIndiceTextField = new HashMap<>();
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.PAGE_AXIS));
		List<Indice> allIndices = indiceManager.getAllIndices();
		
		JPanel titlePanel = new JPanel();
		GridLayout gridlayout = new GridLayout(1, 2);
		titlePanel.setLayout(gridlayout);
		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		
		JLabel name = new JLabel("Description");
		titlePanel.add(name);
		
		JLabel contains = new JLabel("Contenu");
		titlePanel.add(contains);
		listPanel.add(titlePanel);
		
		for(Indice indice : allIndices)
		{
			JPanel textIndicePanel = new JPanel();
			GridLayout layout = new GridLayout(1, 2);
			textIndicePanel.setLayout(layout);
			textIndicePanel.setBorder(BorderFactory.createEtchedBorder());
			
			JTextField[] indiceTextField = new JTextField[2];
			JTextField descTextField = new JTextField(indice.getDescription());
			descTextField.setEnabled(needUpdate);
			indiceTextField[0] = descTextField;
			textIndicePanel.add(descTextField);
			
			String text = indice.getTexte();
			if (text == null)
			{
				text = indice.getImageName();
				if (text == null)
					text = indice.getSon();
			}
			JTextField nameTextField = new JTextField(text);
			nameTextField.setEnabled(needUpdate);
			indiceTextField[1] = nameTextField;
			textIndicePanel.add(nameTextField);
			
			allIndiceTextField.put(indice, indiceTextField);
			listPanel.add(textIndicePanel);
		}
		
		listPanel.repaint();
		return listPanel;
	}
	
	public void openAsView()
	{
		setTitle("Mise à jour d'indices");
		
		JPanel mainPanel =  new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(getListIndice(false));
		mainPanel.add(getViewButton());
		add(mainPanel);
		
		pack();
		setVisible(true);
	}

	private JPanel getViewButton() {
		JPanel viewPanel = new JPanel();
		viewPanel.setLayout(new FlowLayout());
		
		JButton cancelButton = new JButton("Ok");
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		viewPanel.add(cancelButton);
		
		return viewPanel;
	}
}
