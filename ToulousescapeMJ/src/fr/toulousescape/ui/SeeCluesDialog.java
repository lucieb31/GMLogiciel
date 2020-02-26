package fr.toulousescape.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import fr.toulousescape.util.Session;
import fr.toulousescape.util.ViewUtils;

public class SeeCluesDialog extends JDialog {

	
	String indicesAsHtml;
	public SeeCluesDialog(Component parent, String indicesAsHtml) {
		this.indicesAsHtml = indicesAsHtml;
		setTitle("Indices donnés");

	}
	
	public void openDialog()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//		mainPanel.setPreferredSize(new Dimension(600, 600));

		JLabel indices = new JLabel(indicesAsHtml);
		mainPanel.add(indices);
		add(mainPanel);
		pack();
		setLocationRelativeTo(null);

		setVisible(true);
	}
}
