package maas.gui;
import maas.visualization.*;
import maas.agents.*;
import maas.models.Meta;
import maas.models.Reader;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import java.awt.Font;
import utils.Time;
import java.awt.event.MouseMotionAdapter;


@SuppressWarnings("serial")
public class Overview extends JFrame {


	private static Reader reader;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Logger log = LogManager.getLogger(Overview.class);
			try {
				Overview frame = new Overview(reader);
				
				frame.setVisible(true);
			} catch (Exception e) {
				log.error("Error while opening Overview window", e);
			}
		});
	}

	public Overview(Reader readerInfo) {
		setFont(new Font("Dialog", Font.BOLD, 14));
		setTitle("Overview");
		reader=readerInfo;
		initialize();
	}
	
	/**
	 * Create the frame.
	 */
	private void initialize() {
		JPanel contentPane;
		Meta meta;
		Customer[] customerInfo;
		 Bakery[] bakeryInfo; 
		customerInfo=reader.getCustomers();
		bakeryInfo=reader.getBakeries();
		meta=reader.getMeta();
		int numOfBakery =meta.getNumberOfBakeries();
		int numOfOrder  =meta.getNumberOfOrders();
		int numOfProducts= meta.getNumberOfProducts();
		int durationDays = meta.getDurationDays();
		int customerType1 = meta.getCustomers().getTotalType1();
		int customerType2 = meta.getCustomers().getTotalType2();
		int customerType3 = meta.getCustomers().getTotalType3();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 571, 441);
		contentPane = new JPanel();
		
		JLabel lblTime = new JLabel("Time");
		lblTime.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				lblTime.setText(Time.getTime().getCurrentDate().toString());
			}
		});
		lblTime.setText(Time.getTime().getCurrentDate().toString());
		
		lblTime.setBounds(288, 383, 256, 15);
		
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				lblTime.setText(Time.getTime().getCurrentDate().toString());
				
			}
		});
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(lblTime);
		JLabel lblCustomerInfo = new JLabel("Customer Information");
		lblCustomerInfo.setBounds(12, 100, 171, 30);
		contentPane.add(lblCustomerInfo);
		
		JLabel lblBakeryInfo = new JLabel("Bakery Information");
		lblBakeryInfo.setBounds(356, 104, 188, 22);
		contentPane.add(lblBakeryInfo);
	
		JButton btnNetworks = new JButton("Networks");
		btnNetworks.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				/*
				 * need to execute networks visualization here
				 */
				
				Runnable networks = () -> Application.launch(Visualization.class);
				new Thread(networks).start();
			}
		});
		btnNetworks.setToolTipText("Press to view Networks");
		btnNetworks.setBounds(208, 346, 146, 25);
		contentPane.add(btnNetworks);
		

		
		JButton btnBakery = new JButton("Bakeries Info");
		btnBakery.setToolTipText("Click to Veiw detail operation inside bakeries");
		btnBakery.addActionListener(e ->
				new BakeryUI(bakeryInfo).setVisible(true));
	
		btnBakery.setBounds(356, 54, 141, 25);
		contentPane.add(btnBakery);
		
		JButton btnCustomersInfo = new JButton("Customers Info");
		btnCustomersInfo.setToolTipText("Click to see all cumtomer details");
		btnCustomersInfo.addActionListener(e ->
				new CustomerInfoUI(customerInfo).setVisible(true));
		btnCustomersInfo.setBounds(22, 54, 141, 25);
		contentPane.add(btnCustomersInfo);
		
		JLabel lblWelcomeToAutobake = new JLabel("Welcome To AutoBake !");
		lblWelcomeToAutobake.setFont(new Font("Century Schoolbook L", Font.BOLD, 16));
		lblWelcomeToAutobake.setBounds(169, 0, 221, 30);
		contentPane.add(lblWelcomeToAutobake);
		
		JLabel lblType = new JLabel("Type-1");
		lblType.setBounds(12, 142, 70, 15);
		contentPane.add(lblType);
		
		JLabel lblType1 = new JLabel("Type-2");
		lblType1.setBounds(12, 169, 70, 15);
		contentPane.add(lblType1);
		
		JLabel lblType2 = new JLabel("Type-3");
		lblType2.setBounds(12, 200, 70, 15);
		contentPane.add(lblType2);
		
		JTextField txtTyp = new JTextField();
		txtTyp.setText(Integer.toString(customerType1));
		txtTyp.setBounds(79, 142, 60, 19);
		contentPane.add(txtTyp);
		txtTyp.setColumns(10);
		
		JTextField txtTyp2 = new JTextField();
		txtTyp2.setText(Integer.toString(customerType2));
		txtTyp2.setBounds(79, 169, 60, 19);
		contentPane.add(txtTyp2);
		txtTyp2.setColumns(10);
		
		JTextField txtTyp3 = new JTextField();
		txtTyp3.setText(Integer.toString(customerType3));
		txtTyp3.setBounds(79, 198, 60, 19);
		contentPane.add(txtTyp3);
		txtTyp3.setColumns(10);
		
		JLabel lblBakeries = new JLabel("Bakeries :");
		lblBakeries.setBounds(362, 169, 83, 15);
		contentPane.add(lblBakeries);
		
		JTextField txtNumbakery = new JTextField();
		txtNumbakery.setText(Integer.toString(numOfBakery));
		txtNumbakery.setBounds(445, 167, 92, 19);
		contentPane.add(txtNumbakery);
		txtNumbakery.setColumns(10);
		
		JLabel lblProducts = new JLabel("Products :");
		lblProducts.setBounds(356, 200, 89, 15);
		contentPane.add(lblProducts);
		
		JTextField txtNumprod = new JTextField();
		txtNumprod.setText(Integer.toString(numOfProducts));
		txtNumprod.setBounds(445, 198, 92, 19);
		contentPane.add(txtNumprod);
		txtNumprod.setColumns(10);
		
		JLabel lblOrders = new JLabel("Orders :");
		lblOrders.setBounds(12, 243, 70, 15);
		contentPane.add(lblOrders);
		
		JTextField txtNumorders = new JTextField();
		txtNumorders.setText(Integer.toString(numOfOrder));
		txtNumorders.setBounds(79, 241, 60, 19);
		contentPane.add(txtNumorders);
		txtNumorders.setColumns(10);
		
		JLabel lblDurationDays = new JLabel("Duration Days :");
		lblDurationDays.setBounds(169, 294, 118, 22);
		contentPane.add(lblDurationDays);
		
		JTextField txtDuration = new JTextField();
		txtDuration.setText(Integer.toString(durationDays));
		txtDuration.setBounds(288, 296, 77, 19);
		contentPane.add(txtDuration);
		txtDuration.setColumns(10);
		
	
		

		
		
		
		
	}
}
