package maas.gui;
import java.util.List;
import java.util.ArrayList;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import maas.agents.Bakery;
import maas.agents.DoughPrepTable;
import maas.agents.KneadingMachine;
import maas.agents.Oven;
import maas.agents.Truck;
import maas.models.Product;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class BakeryUI extends JFrame {

	private JPanel contentPane;
	private static final String FONT_1 = "DejaVu Sans";
	private static Bakery[] bakery;
	private int index=0;
	private int ovenTemp=20;
	private JTextField txtGuid;
	private JTextField txtOvens;
	private JTextField txtKneading;
	private JTextField txtPreptables;
	private JTextField txtTrucks;
	private JTextField txtLocation;
	private List<Product> products ;
	private List<Oven> ovens ;
	private List<DoughPrepTable> prepTables;
	private List<Truck> trucks ;
	private List<KneadingMachine> kneadMachines;
	private JTextField txtKneadStatus;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Logger log = LogManager.getLogger(BakeryUI.class);
			try {
				BakeryUI frame = new BakeryUI(bakery);
				frame.setVisible(true);
			} catch (Exception e) {
				log.error("Error while opening Bakery_UI window", e);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BakeryUI(Bakery[] bakeryInfo) {
		setTitle("Bakeries Information");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 786, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		bakery=bakeryInfo;
		
		DefaultListModel<String> listModel = new DefaultListModel<>();



		JList productList = new JList();
		productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
		productList.setLayoutOrientation(JList.VERTICAL_WRAP);
		productList.setVisibleRowCount(10);
		productList.setBounds(363, 72, 308, 205);
		contentPane.add(productList);
		

		JPanel ovenPanel = new JPanel();
		ovenPanel.setBackground(Color.GRAY);
		ovenPanel.setBounds(26, 269, 263, 221);
		contentPane.add(ovenPanel);
		ovenPanel.setLayout(null);
		JTextPane txtpnOvenStatus = new JTextPane();
		txtpnOvenStatus.setFont(new Font("LM Roman 6", Font.BOLD, 13));
		txtpnOvenStatus.setText("oven status");
		txtpnOvenStatus.setBounds(78, 144, 121, 54);
		ovenPanel.add(txtpnOvenStatus);
		
		JProgressBar tempStatus = new JProgressBar();
		tempStatus.setForeground(new Color(255, 165, 0));
		tempStatus.setValue(40);
		tempStatus.setBackground(Color.LIGHT_GRAY);
		tempStatus.setMaximum(255);
		tempStatus.setToolTipText("oven temperature");
		tempStatus.setStringPainted(true);
		tempStatus.setBounds(234, 33, 23, 170);
		ovenPanel.add(tempStatus);
		tempStatus.setOrientation(SwingConstants.VERTICAL);
		
		JTextPane txtpnPreptable = new JTextPane();
		txtpnPreptable.setText("prepTable");
		txtpnPreptable.setBounds(545, 341, 126, 36);
		contentPane.add(txtpnPreptable);
		
		JTextPane txtpnTruckstatus = new JTextPane();
		txtpnTruckstatus.setText("truckstatus");
		txtpnTruckstatus.setBounds(545, 440, 126, 36);
		contentPane.add(txtpnTruckstatus);

		
		ovens = bakery[0].getOvens();
		prepTables= bakery[0].getDoughPrepTables();
		trucks =bakery[0].getTrucks();
		products=bakery[0].getProducts();
		kneadMachines=bakery[0].getKneadingMachines();
		
		String[] strBakeryList=new String[bakery.length];
		for (int i=0; i<bakery.length; i++){
			strBakeryList[i]=(bakery[i].getBakeryName());
		}
		
		
		JComboBox comboBox = new JComboBox();
		comboBox.setToolTipText("Select Oven from the list see its status");
		comboBox.setBounds(128, 6, 108, 25);
		ovenPanel.add(comboBox);
		for (Oven oven :ovens){
			comboBox.addItem(oven.getGuid());}
		comboBox.setSelectedItem(1);

		comboBox.addActionListener(event -> {
				JComboBox cbox = (JComboBox)event.getSource();
				 String selectdOven = (String)cbox.getSelectedItem();
				 List<Oven> tempovens = bakery[index].getOvens();
					for (Oven oven :tempovens){
						if (selectdOven==oven.getGuid()){
							ovenTemp=oven.getcurrentTemperature();
							tempStatus.setValue(ovenTemp);
							txtpnOvenStatus.setText(oven.getStatus().toString()) ;
							
						}
						}
			});
		
		
		
		JComboBox selectPrepTable = new JComboBox();
		selectPrepTable.setToolTipText("select Prep Tables to see their status");
		selectPrepTable.setBounds(535, 296, 156, 33);
		contentPane.add(selectPrepTable);
		for (DoughPrepTable prepTable : prepTables ){
			selectPrepTable.addItem(prepTable.getGuid());
		}
		selectPrepTable.setSelectedItem(1);
		
		selectPrepTable.addActionListener(event -> {
				JComboBox cbPrepTable = (JComboBox)event.getSource();
				 String selectdPrepTable = (String)cbPrepTable.getSelectedItem();
				 List<DoughPrepTable> tmpPrepTables =bakery[index].getDoughPrepTables();
					for ( DoughPrepTable prepTable : tmpPrepTables ){
					
						if (selectdPrepTable== prepTable.getGuid()){
							txtpnPreptable.setText(prepTable.getStatus().toString());
						}
						}
			
			});
		
		
		JComboBox selectTruck = new JComboBox();
		selectTruck.setToolTipText("select Truck from the list to check status");
		selectTruck.setBounds(535, 395, 146, 33);
		contentPane.add(selectTruck);
		for (Truck truck : trucks ){
			selectTruck.addItem(truck.getGuid());}
		selectTruck.setSelectedItem(1);
		
		selectTruck.addActionListener(event -> {
				JComboBox cbTruck = (JComboBox)event.getSource();
				 String selectdTruck = (String)cbTruck.getSelectedItem();
				 List<Truck> tmpTrucks =bakery[index].getTrucks();
					for (Truck truck : tmpTrucks){
					
						if (selectdTruck==truck.getGuid()){
							txtpnTruckstatus.setText(Float.toString(truck.getLocation().getX()) + " , " + Float.toString(truck.getLocation().getY()));
							
						}
						}
			});
		
		JComboBox cbKneadMachines = new JComboBox();
		cbKneadMachines.setToolTipText("select Kneading Machine from the list");
		cbKneadMachines.setBounds(301, 295, 212, 33);
		contentPane.add(cbKneadMachines);
		for (KneadingMachine kneadingMachine : kneadMachines ){
			cbKneadMachines.addItem(kneadingMachine.getGuid());}

		cbKneadMachines.setSelectedItem(1);
		
		cbKneadMachines.addActionListener(event -> {
				JComboBox cbKnead = (JComboBox)event.getSource();
				 String selectKnead = (String)cbKnead.getSelectedItem();
				 List<KneadingMachine> tmpKnead =bakery[index].getKneadingMachines();
					for (KneadingMachine keadingMachine : tmpKnead){
					
						if (selectKnead==keadingMachine.getGuid()){
							
							txtKneadStatus.setText(keadingMachine.getStatus().toString());
							
						}
						}
			});
		
		
		
		
		JComboBox cmbMessageList = new JComboBox(strBakeryList);

		cmbMessageList.setSelectedItem(1);

		cmbMessageList.addActionListener(event -> {
				JComboBox cb = (JComboBox)event.getSource();
				 String selectdBakery = (String)cb.getSelectedItem();
				 for (int i=0; i<bakery.length; i++){
						String bName =(bakery[i].getBakeryName());
						if (selectdBakery==bName){
							int numKneadings= bakery[i].getKneadingMachines().size();
							txtKneading.setText(Integer.toString(numKneadings));
							int numOvens =bakery[i].getOvens().size();
							txtOvens.setText(Integer.toString(numOvens));
							
							int numTrucks =bakery[i].getTrucks().size();
							txtTrucks.setText(Integer.toString(numTrucks));
							
							int numDoughprep =bakery[i].getDoughPrepTables().size();
							txtPreptables.setText(Integer.toString(numDoughprep));
							
							txtGuid.setText(bakery[i].getGuid());
							String coordinates = Float.toString(bakery[i].getLocation().getX()) + " , " + Float.toString(bakery[i].getLocation().getY()) ;
							txtLocation.setText(coordinates);
							index =i;
							List<Product> iproducts = new ArrayList<>();
							List<Oven> iovens = new ArrayList<>();
							List<DoughPrepTable> iprepTables=new ArrayList<>();
							List<Truck> itrucks =new ArrayList<>();	
							List<KneadingMachine> iKneadingMachines= new ArrayList<>();
								listModel.clear();
								iovens.clear();
								iprepTables.clear();
								itrucks.clear();
								iproducts.clear();
								iKneadingMachines.clear();
								
								
								iovens = bakery[i].getOvens();
								iprepTables= bakery[i].getDoughPrepTables();
								itrucks =bakery[i].getTrucks();
								iKneadingMachines=bakery[i].getKneadingMachines();
							
							if (products!=null){
								for(Product product : products) {
									listModel.addElement(product.getGuid()+" : "+Float.toString(product.getProductionCost()));
										}}
							productList.setModel(listModel);
							
							
							comboBox.removeAllItems();
							selectPrepTable.removeAllItems();
							selectTruck.removeAllItems();
							cbKneadMachines.removeAllItems();
							
							for (Oven oven :iovens){
								comboBox.addItem(oven.getGuid());}
							
							for (DoughPrepTable prepTable : iprepTables ){
								selectPrepTable.addItem(prepTable.getGuid());}
						
							for (Truck truck : itrucks ){
								selectTruck.addItem(truck.getGuid());}
							
							for (KneadingMachine kneadingMachine : iKneadingMachines ){
								cbKneadMachines.addItem(kneadingMachine.getGuid());}

						
						}
					}
		});
		cmbMessageList.setBounds(179, 24, 179, 36);
		contentPane.add(cmbMessageList);
		
		JLabel lblSelectBakery = new JLabel("Select Bakery :");
		lblSelectBakery.setFont(new Font(FONT_1, Font.BOLD, 14));
		lblSelectBakery.setBounds(26, 29, 141, 15);
		contentPane.add(lblSelectBakery);
		
		JLabel lblGuid = new JLabel("Guid :");
		lblGuid.setBounds(26, 74, 70, 15);
		contentPane.add(lblGuid);
		
		JLabel lblOvens = new JLabel("Ovens :");
		lblOvens.setBounds(26, 101, 70, 15);
		contentPane.add(lblOvens);
		
		JLabel lblKneadingMachines = new JLabel("Kneading Machines :");
		lblKneadingMachines.setBounds(26, 128, 147, 15);
		contentPane.add(lblKneadingMachines);
		
		JLabel lblPrepTables = new JLabel("Prep Tables :");
		lblPrepTables.setBounds(26, 161, 106, 15);
		contentPane.add(lblPrepTables);
		
		JLabel lblTrucks = new JLabel("Trucks :");
		lblTrucks.setBounds(26, 188, 70, 15);
		contentPane.add(lblTrucks);
		
		JLabel lblLocation = new JLabel("Location :");
		lblLocation.setBounds(26, 215, 70, 15);
		contentPane.add(lblLocation);
		
		txtGuid = new JTextField();
		txtGuid.setText("Guid");
		txtGuid.setBounds(179, 72, 114, 21);
		contentPane.add(txtGuid);
		txtGuid.setColumns(10);
		
		txtOvens = new JTextField();
		txtOvens.setText("Ovens");
		txtOvens.setBounds(179, 99, 114, 21);
		contentPane.add(txtOvens);
		txtOvens.setColumns(10);
		
		txtKneading = new JTextField();
		txtKneading.setText("Kneading");
		txtKneading.setBounds(179, 126, 114, 21);
		contentPane.add(txtKneading);
		txtKneading.setColumns(10);
		
		txtPreptables = new JTextField();
		txtPreptables.setText("PrepTables");
		txtPreptables.setBounds(179, 157, 114, 21);
		contentPane.add(txtPreptables);
		txtPreptables.setColumns(10);
		
		txtTrucks = new JTextField();
		txtTrucks.setText("Trucks");
		txtTrucks.setBounds(179, 186, 114, 21);
		contentPane.add(txtTrucks);
		txtTrucks.setColumns(10);
		
		txtLocation = new JTextField();
		txtLocation.setText("Location");
		txtLocation.setBounds(179, 213, 114, 21);
		contentPane.add(txtLocation);
		txtLocation.setColumns(10);
		

		
		
		JLabel lblproductList = new JLabel("Product List ");
		lblproductList.setFont(new Font(FONT_1, Font.BOLD, 13));
		lblproductList.setBounds(404, 24, 109, 15);
		contentPane.add(lblproductList);
		

		
		
		
		
		
		JLabel lblSelectOven = new JLabel("Select Oven :");
		lblSelectOven.setFont(new Font(FONT_1, Font.BOLD, 12));
		lblSelectOven.setForeground(new Color(248, 248, 255));
		lblSelectOven.setBounds(6, 11, 99, 15);
		ovenPanel.add(lblSelectOven);
		
		JLabel lblOvenStatus = new JLabel("Status :");
		lblOvenStatus.setBounds(6, 144, 60, 15);
		ovenPanel.add(lblOvenStatus);
		
		txtKneadStatus = new JTextField();
		txtKneadStatus.setBounds(301, 359, 162, 46);
		contentPane.add(txtKneadStatus);
		txtKneadStatus.setColumns(10);
		
		
		
		

		
		

		
	}
}
