import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server extends JFrame {

	// 필드 선언
	private JPanel contentPane;
	private JTextField textField;
	private JButton start;
	JTextArea textArea;

	private ServerSocket serverSocket;
	private Socket socket;
	private int port;

	// 스레드간의 정보를 공유할 vector 객체 필요
	private Vector vector = new Vector();

	public Server() {
		init();
	}

	public void init() {

		setTitle("채팅-서버");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 150, 280, 400);

		contentPane = new JPanel();

		// 사용자 자유배치 Layout 방법
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JScrollPane js = new JScrollPane();
		js.setBounds(0, 0, 260, 250);
		contentPane.add(js);

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		js.setViewportView(textArea);

		textField = new JTextField();
		textField.setBounds(98, 264, 154, 37);
		textField.setColumns(10);
		contentPane.add(textField);

		JLabel lblNewLabel = new JLabel("포트 번호");
		lblNewLabel.setBounds(12, 264, 98, 37);
		contentPane.add(lblNewLabel);

		start = new JButton("서버 실행");

		/*
		 * 이벤트 연결 및 핸들러 처리를 한번에 해결하는 방법
		 */

		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textField.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "포트번호를 입력하세요!", "경고 메시지", JOptionPane.ERROR_MESSAGE);
					textField.requestFocus();
					return;
				} else {
					try {
						port = Integer.parseInt(textField.getText());
						server_start(port);
						// 사용자 정의 메소드
					} catch (Exception ex) {
						System.out.println(ex);
					}
				}

			}
		});

		start.setBackground(Color.GREEN);// 버튼 색깔 주기
		start.setBounds(0, 325, 264, 37);
		contentPane.add(start);
		textArea.setEditable(false);
	} // end init();

	public void server_start(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		start.setText("서버 실행중..");

		start.setEnabled(false);
		textField.setEditable(false);

		if (serverSocket != null) {
			Connection();
		}
	}

	public void Connection() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						textArea.append("사용자 접속 대기중..\n");
						// 서버에서 연결수락!
						socket = serverSocket.accept();
						textArea.append("사용자 접속!!\n");

						/*
						 * 접속을 계속 유지하면서 데이터 송수신을 하기위해서 스레드 핸들러 객체 생성
						 */

						Threadhandler user = new Threadhandler(socket, vector);
						vector.add(user); // 해당 벡터에 사용자 객체 추가
						user.start();

					} catch (Exception e) {
						System.out.println(e);
					}
				}
			}
		});
		
		th.start(); 
	}

	/*
	 * 내부 클래스 스레드 핸들러 클래스의 객체는 클라이언트당 한 개씩 생성된다. 서버로 들어온 클라이언트와의 연결을 개별적으로 처리하는
	 * 역할
	 */
	class Threadhandler extends Thread {

		// 필드 선언
		private InputStream is;
		private OutputStream os; // 기본 스트림
		private DataInputStream dis;
		private DataOutputStream dos; // 보조 스트림
		private Socket user_socket;
		private Vector user_vector;
		private String nickName;

		// 생성자 구현
		public Threadhandler(Socket socket, Vector vector) {
			user_socket = socket;
			user_vector = vector;
			User_network();
		}

		public void User_network() {
			try {
				is = user_socket.getInputStream(); // 기본 스트림 객체
				dis = new DataInputStream(is); // 보조 스트림 객체 연결

				os = user_socket.getOutputStream(); // 기본 스트림 객체
				dos = new DataOutputStream(os); // 보조 스트림 객체 연결

				nickName = dis.readUTF(); // 문자열을 읽는다.
				textArea.append("접속자 ID" + nickName + "\n");

				send_Message("정상 접속되었습니다.");

			} catch (Exception e) {
				textArea.append("스트림 셋팅 에러!!");
			}
		} // User_network() end

		public void send_Message(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				textArea.append("메시지 송신 에러 발생!");
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					String msg = dis.readUTF();
					InMessage(msg);
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		}

		public void InMessage(String str) {
			textArea.append("사용자로부터 들어온 메시지: " + str + "\n");
			broad_cast(str);
		}

		public void broad_cast(String str) {
			for (int i = 0; i < user_vector.size(); i++) {
				Threadhandler imsi = (Threadhandler) user_vector.elementAt(i);

				imsi.send_Message(nickName + ":" + str);
			}
		}

	} // ThreadHandler class end

	public static void main(String[] args) {
		Server frame = new Server();
		frame.setVisible(true);
	}

}
