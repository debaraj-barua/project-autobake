package maas.gui;
import maas.agents.*;
import maas.models.Order;
import maas.models.ProductsToOrder;
import maas.models.Status;

import java.awt.EventQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextField;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;

@SuppressWarnings("serial")
public class CustomerInfoUI extends JFrame {

	private JPanel contentPane;
	private static Customer[] customerinfo;
	private JTextField txtName;
	private int indexorder =0;
	private JTextField txtAid;
	private JTextField txtLocation;
	private JTextField txtStatus;
	private JTextField txtDeliverydate;
	private JTextField txtOrderdate;
	private JTextField txtOderStatus;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(()-> {
				Logger log = LogManager.getLogger(CustomerInfoUI.class);
				try {
					CustomerInfoUI frame = new CustomerInfoUI(customerinfo);
					frame.setVisible(true);
					
				} catch (Exception e) {
					log.error("Error while opening CustomerInfoUI Gui window", e);
				}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CustomerInfoUI(Customer[] customerInfo) {
		setTitle("Customer Information");
		customerinfo=customerInfo;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 487, 349);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/*
		 * Adding labels , text fields , combo-boxs for showing customer information 
		 */
		JLabel lblCustomerInformation = new JLabel("Customer Information");
		lblCustomerInformation.setBackground(new Color(176, 224, 230));
		lblCustomerInformation.setBounds(191, 12, 184, 24);
		contentPane.add(lblCustomerInformation);
		
		JLabel lblName = new JLabel("Name :");
		lblName.setBounds(39, 81, 70, 15);
		contentPane.add(lblName);
		
		JLabel lblAid = new JLabel("Guid :");
		lblAid.setBounds(39, 108, 70, 15);
		contentPane.add(lblAid);
		
		JLabel lblLocation = new JLabel("Location :");
		lblLocation.setBounds(39, 191, 86, 15);
		contentPane.add(lblLocation);
		
		txtName = new JTextField();
		txtName.setText(customerinfo[1].getCustomerName());
		txtName.setBounds(134, 79, 147, 19);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		txtAid = new JTextField();
		txtAid.setText(customerinfo[1].getGuid());
		txtAid.setBounds(134, 106, 147, 19);
		contentPane.add(txtAid);
		txtAid.setColumns(10);
		
		txtLocation = new JTextField();
		txtLocation.setToolTipText("X : Y coordinates");
		txtLocation.setText(Float.toString(customerinfo[1].getLocation().getX()) + " : " + Float.toString(customerinfo[1].getLocation().getY()));
		txtLocation.setBounds(134, 191, 147, 19);
		contentPane.add(txtLocation);
		txtLocation.setColumns(10);
		
		JLabel lblOrder = new JLabel("Order :");
		lblOrder.setBounds(305, 40, 70, 15);
		contentPane.add(lblOrder);
		
		JLabel lblStatus = new JLabel(" Status :");
		lblStatus.setToolTipText("customer status");
		lblStatus.setBounds(39, 221, 86, 15);
		contentPane.add(lblStatus);
		
		txtStatus = new JTextField();
		txtStatus.setFont(new Font("Dialog", Font.BOLD, 14));
		txtStatus.setText(customerinfo[1].getStatus().toString());
		txtStatus.setBounds(134, 221, 147, 30);
		contentPane.add(txtStatus);
		txtStatus.setColumns(10);
		
		String[] strCustomerList=new String[customerinfo.length];
		for (int i=0; i<customerinfo.length; i++){
			strCustomerList[i]=(customerinfo[i].getGuid());
		}
		
		List<Order> tmporders = customerinfo[0].getOrders();
		String[] strOrderlist = new String[tmporders.size()]
				;
		int k=0;
		for (Order tmporder : tmporders)
		{strOrderlist[k]=tmporder.getGuid();	k++;}
		
		DefaultListModel<String> listModelOder = new DefaultListModel<>();
		DefaultListModel<String> listOder = new DefaultListModel<>();
		
		
		JList oderList = new JList();
		oderList.setBounds(293, 56, 182, 249);
		contentPane.add(oderList);
		
		
		JComboBox comboBox = new JComboBox(strCustomerList);
		comboBox.setToolTipText("select customer form the list");
		comboBox.setBounds(12, 12, 161, 24);
		contentPane.add(comboBox);
		comboBox.setSelectedItem(1);
		
		JLabel lblDelivery = new JLabel("Delivery :");
		lblDelivery.setBounds(39, 164, 86, 15);
		contentPane.add(lblDelivery);
		
		JLabel lblOrderDate = new JLabel("Order date :");
		lblOrderDate.setBounds(39, 135, 97, 15);
		contentPane.add(lblOrderDate);
		
		
				
		txtDeliverydate = new JTextField();
		txtDeliverydate.setToolTipText("Day-Hour-Min");
		txtDeliverydate.setText("deliveryDate");
		txtDeliverydate.setBounds(134, 162, 147, 19);
		contentPane.add(txtDeliverydate);
		txtDeliverydate.setColumns(10);
		
		txtOrderdate = new JTextField();
		txtOrderdate.setToolTipText("Day-Hour-Min");
		txtOrderdate.setText("OrderDate");
		txtOrderdate.setBounds(134, 133, 147, 19);
		contentPane.add(txtOrderdate);
		txtOrderdate.setColumns(10);
		
		JComboBox cbOrders = new JComboBox();
		cbOrders.setToolTipText("Select order from the list");
		cbOrders.setBounds(356, 31, 119, 24);
		contentPane.add(cbOrders);
		
		JLabel lblOderStatus = new JLabel("Oder Status:");
		lblOderStatus.setBounds(12, 265, 97, 15);
		contentPane.add(lblOderStatus);
		
		txtOderStatus = new JTextField();
		txtOderStatus.setFont(new Font("Dialog", Font.BOLD, 13));
		txtOderStatus.setText("Oder Status");
		txtOderStatus.setBounds(134, 263, 147, 24);
		contentPane.add(txtOderStatus);
		txtOderStatus.setColumns(10);
		/*
		 * adding event handler for managing Orders specfic to customers
		 * combox which takes list input from the customer slection combox  
		 */
		
		cbOrders.addActionListener(e->{
				JComboBox cbOrder = (JComboBox)e.getSource();
				listOder.clear();

				 String selectdOrder = (String)cbOrder.getSelectedItem();
				 List<Order> checkorder=customerinfo[indexorder].getOrders();
				 Map<String, Status> orderStatus = customerinfo[indexorder].getOrderStatus();
				
				 for (Order check : checkorder){
					 if (selectdOrder==check.getGuid()){
						 for (Map.Entry<String, Status> orderStatu : orderStatus.entrySet())
						 {
							 if (selectdOrder==orderStatu.getKey()){txtOderStatus.setText(orderStatu.getValue().toString()); }
						 }
						 txtDeliverydate.setText(Integer.toString(check.getDeliveryDate().getDay())+" : "+ 
								   				Integer.toString(check.getDeliveryDate().getHour())+" : "+ 
								   				Integer.toString(check.getDeliveryDate().getMin()));
						 
						 txtOrderdate.setText(Integer.toString(check.getOrderDate().getDay())+" : "+ 
								   				Integer.toString(check.getOrderDate().getHour())+" : "+ 
								   				Integer.toString(check.getOrderDate().getMin()));
						 
						 List<ProductsToOrder> custorder=check.getProducts();
						 for(ProductsToOrder product : custorder ){
							 listOder.addElement(product.getProductid()+" -:- "+
									 				Integer.toString(product.getQuantity()));
							}
						
					 }
				 }
				 oderList.setModel(listOder);
		});

/*
 * adding event handler for selecting customer 
 * reading status , name , location, orders 
 * orders are passed to the above combobox 		
 */
	
		comboBox.addActionListener(e ->{
				JComboBox cb = (JComboBox)e.getSource();
				 String selectdBakery = (String)cb.getSelectedItem();
				 for (int i=0; i<customerinfo.length; i++){
						String bName =(customerinfo[i].getGuid());
						if (selectdBakery==bName){
							txtName.setText(customerinfo[i].getCustomerName());
							txtAid.setText(customerinfo[i].getGuid());
				
							txtLocation.setText(Float.toString(customerinfo[i].getLocation().getX()) + " : " + Float.toString(customerinfo[i].getLocation().getY()));
							indexorder=i;
							listModelOder.clear();
							List<Order> orders = new ArrayList<>();
							
						
							orders=customerinfo[i].getOrders();
							cbOrders.removeAllItems();
							for(Order order : orders) {
							cbOrders.addItem(order.getGuid());
							}
										
													
							

						}
							
						}
				 });
	}
}
