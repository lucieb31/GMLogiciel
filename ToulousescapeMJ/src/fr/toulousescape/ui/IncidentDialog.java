package fr.toulousescape.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class IncidentDialog extends JDialog {

	private JTextArea alertField;
	private JTextField discountField;
	private ChronoPanel chronoPanel;
	public IncidentDialog(Component parent, ChronoPanel chronoPanel) {
		this.chronoPanel = chronoPanel;		
		setLocationRelativeTo(getRootPane());
		setModal(true);
		setTitle("Enregistrer un incident");
	}
	
	public void openDialog()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		
		//Border border = mainPanel.getBorder();
		//Border margin = new EmptyBorder(10,10,10,10);
		//mainPanel.setBorder(new CompoundBorder(border, margin));
		
		JLabel alertLabel = new JLabel("Incident :");
		alertField = new JTextArea(2,30);
		alertField.setText(chronoPanel.discount);
		JPanel alertPanel = new JPanel();
		alertPanel.add(alertLabel);
		alertPanel.add(alertField);
		mainPanel.add(alertPanel);
		JLabel discountLabel = new JLabel("Code cadeau donné (si pas de code, laisser vide)");
		discountField = new JTextField(10);
		discountField.setText(chronoPanel.discount);
		JPanel discountPanel = new JPanel();
		discountPanel.add(discountLabel);
		discountPanel.add(discountField);
		mainPanel.add(discountPanel);
//		mainPanel.setPreferredSize(new Dimension(600, 600));

		JButton validateButton = new JButton("Valider");
		mainPanel.add(validateButton);
		
		validateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveIncident();
			}
		});
		add(mainPanel);
		pack();
		setLocationRelativeTo(null);

		setVisible(true);
	}
	
	public void saveIncident() {
		chronoPanel.incident = this.alertField.getText();
		chronoPanel.discount = this.discountField.getText();
		dispose();
	}
	


}
