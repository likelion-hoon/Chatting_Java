import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Client extends JFrame {

	JTextField tf_ID, tf_IP, tf_PORT;
	JPanel contentPane;

	public Client() {
		init();
	}

	public void init() {
		setTitle("채팅-클라이언트");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 300, 300);

		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("ID");
		lblNewLabel.setBounds(50, 60, 90, 35);
		contentPane.add(lblNewLabel);

		tf_ID = new JTextField();
		tf_ID.setBounds(92, 64, 150, 21);
		tf_ID.setColumns(10);
		contentPane.add(tf_ID);

		JLabel lblServerIp = new JLabel("Server IP");
		lblServerIp.setBounds(10, 110, 90, 35);
		contentPane.add(lblServerIp);

		tf_IP = new JTextField();
		tf_IP.setBounds(92, 118, 150, 21);
		tf_IP.setColumns(10);
		contentPane.add(tf_IP);

		JLabel lblPort = new JLabel("PORT");
		lblPort.setBounds(36, 178, 90, 34);
		contentPane.add(lblPort);

		tf_PORT = new JTextField();
		tf_PORT.setBounds(92, 185, 150, 25);
		tf_PORT.setColumns(10);
		contentPane.add(tf_PORT);

		JButton btnNewbutton = new JButton("접속");
		btnNewbutton.setBounds(36, 266, 205, 52);
		btnNewbutton.setBackground(Color.YELLOW);
		contentPane.add(btnNewbutton);

		btnNewbutton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String _id = tf_ID.getText().trim();
					String _ip = tf_IP.getText().trim();
					int _port = Integer.parseInt(tf_PORT.getText().trim());

					MainView view = new MainView(_id, _ip, _port);
					setVisible(false);
				} catch (Exception ex) {
					System.out.println(ex);
				}

			}
		});
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.setVisible(true);
	}
}
