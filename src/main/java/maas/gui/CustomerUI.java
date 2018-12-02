package maas.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import maas.agents.Customer;



@SuppressWarnings("serial")
public class CustomerUI extends JFrame {

	private JPanel contentPane;
	private static Customer[] Customerinfo; 
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(()-> {
				Logger log = LogManager.getLogger(CustomerUI.class);
				try {
					CustomerUI frame = new CustomerUI(Customerinfo);
					frame.setVisible(true);
				} catch (Exception e) {
					log.error("Error while opening CustomerUI frame", e);
				}
		});
	}

	/**
	 * Create the frame.
	 */
	public CustomerUI(Customer[] customerInfo) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
	}
	
}

