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
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class PaymentDialog extends JDialog {

	private Map<String,String> sessionMap;
	private JComboBox playersBox;
	private JPanel lessPlayersPanel;
	private JPanel morePlayersPanel;
	private float unpaidAmount = 0;
	private float morePlayersAmount = 0;
	private float ancvAmount = 0;
	private int price_category = 0;
	private JRadioButton discountButton;
	private JRadioButton noDiscountButton;
	private JTextField cbF;
	private JTextField espF;
	private JTextField chF;
	private JTextField ancvF;
	private JRadioButton invoiceButton;
	private JRadioButton noInvoiceButton;
	private boolean alreadyTried = false;
	private ChronoPanel chronoPanel;
	
	private boolean unpaid;
	
	public PaymentDialog(Component parent, Map<String, String> sessionMap, ChronoPanel chronoPanel) {
		this.sessionMap = sessionMap;
		this.chronoPanel = chronoPanel;
		
//		setLocationRelativeTo(null);
		setLocationRelativeTo(getRootPane());
		setModal(true);
		setTitle("Paiements sur place");
	}
	
	public void openDialog()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		Border border = mainPanel.getBorder();
		Border margin = new EmptyBorder(10,10,10,10);
		mainPanel.setBorder(new CompoundBorder(border, margin));
		

//		mainPanel.setPreferredSize(new Dimension(600, 600));

		JLabel customer = new JLabel("Session : "+sessionMap.get("firstname")+ " " + sessionMap.get("lastname")+" ("+sessionMap.get("phone")+")");
		
		mainPanel.add(customer);
				
		JPanel playersPanel = new JPanel(new FlowLayout());
		lessPlayersPanel = new JPanel(new FlowLayout());
		morePlayersPanel = new JPanel(new FlowLayout());
		JPanel sessionPanel = new JPanel(new FlowLayout());
		sessionPanel.add(customer);
		Object[] elements = new Object[]{"2 joueurs", "3 joueurs", "4 joueurs", "5 joueurs", "6 joueurs", "7 joueurs", "8 joueurs"};
		 
		playersBox = new JComboBox(elements);
		playersBox.setSelectedIndex(new Integer(sessionMap.get("players"))-2);
		
		playersBox.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				playerNumberChanged(e);
			}
		});
		
		sessionPanel.add(playersBox);
		mainPanel.add(sessionPanel);
		
		JLabel discountLabel = new JLabel("Envoyer un avoir ?");
		discountLabel.setForeground(Color.RED);
		ButtonGroup discountGroup = new ButtonGroup();
		discountButton = new JRadioButton("Oui");
		noDiscountButton = new JRadioButton("Non");
		discountGroup.add(discountButton);
		discountGroup.add(noDiscountButton);
		lessPlayersPanel.add(discountLabel);
		lessPlayersPanel.add(discountButton);
		lessPlayersPanel.add(noDiscountButton);

		mainPanel.add(lessPlayersPanel);
		lessPlayersPanel.setVisible(false);
		
		JLabel morePlayerLabel = new JLabel("Chaque joueur supplémentaire doit régler : "+("0".equals(sessionMap.get("price_category")) ? "20€" : "25€"));
		price_category = new Integer(sessionMap.get("price_category"));
		morePlayerLabel.setForeground(Color.RED);
		morePlayersPanel.add(morePlayerLabel);
		mainPanel.add(morePlayersPanel);
		morePlayersPanel.setVisible(false);
		
		if(! "0".equals(sessionMap.get("ancv"))) {
			JLabel ancvLabel = new JLabel(sessionMap.get("ancv")+" € en chèques vacances à récupérer");
			ancvLabel.setForeground(Color.RED);
			ancvAmount = new Integer(sessionMap.get("ancv"));
			mainPanel.add(ancvLabel);
		}
		if("1".equals(sessionMap.get("unpaid"))) {
			JLabel toPayLabel = new JLabel(sessionMap.get("amount")+" € à régler sur place (et / ou facture à envoyer)");
			toPayLabel.setForeground(Color.RED);
			unpaidAmount= new Integer(sessionMap.get("amount"));
			mainPanel.add(toPayLabel);
		}
		JLabel payments = new JLabel("Paiements complémentaires sur place");
		
		JPanel paymentPanel = new JPanel(new FlowLayout());
		JLabel ancv = new JLabel("ANCV : ");
		JLabel esp = new JLabel("Espèces : ");
		JLabel ch = new JLabel("Chèque : ");
		JLabel cb = new JLabel("CB : ");
		cbF = new JTextField("0",3);
		espF = new JTextField("0",3);
		chF = new JTextField("0",3);
		ancvF = new JTextField("0",3);
		
		paymentPanel.add(ancv);
		paymentPanel.add(ancvF);
		paymentPanel.add(cb);
		paymentPanel.add(cbF);
		paymentPanel.add(esp);
		paymentPanel.add(espF);
		paymentPanel.add(ch);
		paymentPanel.add(chF);

		
		JLabel sendInvoiceLabel = new JLabel("Envoyer une facture par mail ?");
		ButtonGroup invoiceGroup = new ButtonGroup();
		invoiceButton = new JRadioButton("Oui");
		noInvoiceButton = new JRadioButton("Non");
		invoiceGroup.add(invoiceButton);
		invoiceGroup.add(noInvoiceButton);
		paymentPanel.add(sendInvoiceLabel);
		paymentPanel.add(invoiceButton);
		paymentPanel.add(noInvoiceButton);

		mainPanel.add(payments);
		mainPanel.add(paymentPanel);
		
		JButton validateButton = new JButton("Valider");
		mainPanel.add(validateButton);
		
		validateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				checkInfos();
			}
		});
		add(mainPanel);
		repaint();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	protected void playerNumberChanged(ActionEvent e) {
		alreadyTried = false;
		int newPlayers = playersBox.getSelectedIndex() + 2;
		int currentPlayers = new Integer(sessionMap.get("players"));
		if (newPlayers < currentPlayers) {
			lessPlayersPanel.setVisible(true);
			morePlayersPanel.setVisible(false);
			morePlayersAmount = 0;
		} else if (newPlayers > currentPlayers) {
			lessPlayersPanel.setVisible(false);
			morePlayersPanel.setVisible(true);
			if (price_category == 0) {
				morePlayersAmount = 20 * (newPlayers - currentPlayers);
			} else {
				morePlayersAmount = 25 * (newPlayers - currentPlayers);
			}
		} else {
			lessPlayersPanel.setVisible(false);
			morePlayersPanel.setVisible(false);
			morePlayersAmount = 0;
		}
		pack();
	}

	public void sendDataToWebsite() {
		if (sessionMap.get("id") != "0") {
		  try {

				LoadConfig config = new LoadConfig();
				
				URL url = new URL("https://www.toulousescape.fr/process/wsupdate");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("POST");
				Map<String, String> postParams = new LinkedHashMap<>();
				postParams.put("session", ""+sessionMap.get("id"));
				postParams.put("players", ""+sessionMap.get("players"));
				postParams.put("new_players", ""+(playersBox.getSelectedIndex() + 2));
				postParams.put("discount", discountButton.isSelected()?"1":"0");
				postParams.put("invoice", invoiceButton.isSelected()?"1":"0");
				postParams.put("ancv", ""+ancvF.getText());
				postParams.put("cb", ""+cbF.getText());
				postParams.put("ch", ""+chF.getText());
				postParams.put("esp", ""+espF.getText());
				postParams.put("unpaid", ""+sessionMap.get("unpaid"));
				postParams.put("price_category", ""+sessionMap.get("price_category"));
				System.out.println("ESPECES : "+espF.getText());
				System.out.println("CB : "+cbF.getText());
				byte[] postDataBytes = generatePostData(postParams);
		        
			    conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				conn.setRequestProperty("Accept", "application/json");
				conn.setDoOutput(true);
		        conn.getOutputStream().write(postDataBytes);
		        

				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				String output;
				String all ="";
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					all += output;
				}
				dispose();
				System.out.println(all);
			  } catch (MalformedURLException e) {

				e.printStackTrace();

			  } catch (IOException e) {

				e.printStackTrace();

			  }
		}
	}
	
	
	public byte[] generatePostData(Map<String, String> postParams) 
    {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> dParam : postParams.entrySet())
        {
            if (postData.length() != 0) 
                postData.append('&');
            try {
                postData.append(URLEncoder.encode(dParam.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(dParam.getValue()), "UTF-8"));
            } 
            catch (UnsupportedEncodingException e) 
            {
                e.printStackTrace();
            }
        }
        byte[] postDataBytes = null;
        try 
        {
            postDataBytes = postData.toString().getBytes("UTF-8");
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        }
        return postDataBytes;
    }
	
	private void checkInfos() {
		String error = "";
		if (lessPlayersPanel.isVisible()) {
			if (!discountButton.isSelected() && ! noDiscountButton.isSelected()) {
				error += "Choisir si un avoir doit être envoyé ou non.<br><br>";
			}
		}
		// Check amounts to pay
		float total = ancvAmount + morePlayersAmount + unpaidAmount;
		
		float indication = new Float(chF.getText()) + new Float(cbF.getText()) + new Float(ancvF.getText()) + new Float(espF.getText());
		
		if ( total != indication && !invoiceButton.isSelected()) {
			error += "Montants non valides : "+indication+"€ payés, "+total+"€ attendus.<br><br>";
		}
		if (!invoiceButton.isSelected() && ! noInvoiceButton.isSelected()) {
			error += "Choisir si une facture doit être envoyée ou non.<br><br>";
		}
		if (! "".equals(error)){
			if (alreadyTried) {
				JOptionPane.showMessageDialog(this,
					    "<html>"+error+"<html>",
					    "Validation impossible, vous insistez dis-donc !",
					    JOptionPane.ERROR_MESSAGE);				
			} else {
				JOptionPane.showMessageDialog(this,
					    "<html>"+error+"<html>",
					    "Validation impossible",
					    JOptionPane.ERROR_MESSAGE);				
			}
			alreadyTried = true;
		} else {
			sendDataToWebsite();
			chronoPanel.sessionInfoLabel.setForeground(Color.BLACK);
			chronoPanel.paymentRegistered = true;
		}
	}

}
