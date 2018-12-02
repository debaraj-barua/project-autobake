package maas.gui;
import maas.models.*;
import java.awt.EventQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

public class AutobakeUI {

	private JFrame frame;
	private  Reader readerInfo;
	/**
	 * Launch the application.
	 */

	public void start() {
		EventQueue.invokeLater(() -> {
			Logger log = LogManager.getLogger(AutobakeUI.class);
			try {
			
				AutobakeUI window = new AutobakeUI(readerInfo);
				window.frame.setVisible(true);
			} catch (Exception fe) {
				log.error("Error while opening Gui window", fe);
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AutobakeUI(Reader readerobj) {
		readerInfo=readerobj;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 388, 281);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("Start Application!");
		btnNewButton.addActionListener(event -> {
			new Overview(readerInfo).setVisible(true);
			frame.dispose();
		});
		btnNewButton.setBounds(50, 139, 203, 31);
		frame.getContentPane().add(btnNewButton);

		JLabel lblNewLabel = new JLabel("Welcome to Autobake Bakery!!");
		lblNewLabel.setFont(new Font("LM Roman Slanted 9", Font.BOLD, 18));
		lblNewLabel.setBounds(60, 47, 296, 31);
		frame.getContentPane().add(lblNewLabel);
	}
}
