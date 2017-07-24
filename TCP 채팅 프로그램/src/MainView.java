import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MainView extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	private String id;
	private String ip;
	private int port;

	JButton sendBtn;
	JTextArea textArea;

	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	public MainView(String id, String ip, int port) {
		this.id = id;
		this.ip = ip;
		this.port = port;

		init();
		start();

		textArea.append("매개 변수로 넘어온 값 : " + id + " " + ip + " " + port + "\n");

		network();
	}

	public void network() {
		// 서버에 접속
		try {
			socket = new Socket(ip, port);
			if (socket != null) {
				Connection();
			}
		} catch (UnknownHostException e) {

		} catch (IOException e) {
			textArea.append("소켓 접속 에러!!\n");
		}

	}

	public void Connection() {
		try {
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
		} catch (IOException e) {
			textArea.append("스트림 설정 에러!!\n");
		}
		send_Message(id);

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String msg = dis.readUTF();
						textArea.append(msg + "\n");
					} catch (IOException e) {
						textArea.append("메세지 수신 에러!!\n");
						try {
							os.close();
							is.close();
							dos.close();
							dis.close();
							socket.close();
							break;
						} catch (IOException e1) {
						}
					}
				}

			}
		});
		th.start();
	}

	public void send_Message(String str) {
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			textArea.append("메세지 송신 에러!!\n");
		}
	}

	public void init() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 288, 392);
		setTitle("채팅프로그램");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 272, 302);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		textField = new JTextField();
		textField.setBounds(0, 312, 155, 42);
		textField.setColumns(10);
		contentPane.add(textField);

		sendBtn = new JButton("전   송");
		sendBtn.setBounds(161, 312, 111, 42);
		contentPane.add(sendBtn);

		textArea.setEnabled(false);

		setVisible(true);

	}

	public void start() {
		sendBtn.addActionListener(new Myaction());

		textField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send_Message(textField.getText());
					textField.setText("");
					textField.requestFocus();
				}
			}
		});

	}

	class Myaction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == sendBtn) {
				send_Message(textField.getText());
				textField.setText("");
				textField.requestFocus();
			}
		}
	}
}