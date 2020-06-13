package com.yue.czcontrol;

import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.exception.UploadFailedException;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.VersionProperty;


/***************************
 *                         *
 * @author Yue             *
 * @version v2-20611       *
 *                         *
 ***************************/

public class LoginFrame extends JFrame implements SocketSetting, VersionProperty{
	
	private static final long serialVersionUID = 1L;
	
	private Socket socket = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	MainFrame mf;
	
	/**
	 * Connect to MySQL DataBase source
	 */
	public static final String DATA_SOURCE = "jdbc:mysql://27.147.3.116:3306/cz_control?user=TESTER&password=tester&serverTimezone=UTC";
	
	private Connection conn = null;
	private ResultSet rs = null;
	
	private JPanel contentPane;
	
	private JPanel panel = new JPanel();
	private JLabel imageLabel = new JLabel("");
	private JLabel title = new JLabel("Cz\u7BA1\u7406\u7CFB\u7D71\u767B\u5165");
	private JLabel infoLabel = new JLabel("<html>\u8ACB\u8F38\u5165\u60A8\u7684\u5E33\u865F\u53CA\u5BC6\u78BC\u4E14\u767B\u5165\uFF0C\u57FA\u65BC\u5B89\u5168\u6027\u554F\u984C\uFF0C\u767B\u5165\u6642\u9084\u8981\u591A\u586B\u5165\u9A57\u8B49\u78BC\u9078\u9805</html>");
	private Button loginBtm = new Button("\u767B\u5165");
	private JFormattedTextField accountInput = new JFormattedTextField();
	private JFormattedTextField passwordInput = new JFormattedTextField();
	private JLabel accountLabel = new JLabel("\u5E33\u865F:");
	private JLabel passwordLabel = new JLabel("\u5BC6\u78BC:");
	private JLabel registerLabel = new JLabel("\u82E5\u6C92\u6709\u5E33\u865F\uFF0C\u9EDE\u6211\u524D\u5F80\u8A3B\u518A");
	private JLabel error = new JLabel("");
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (ConnectException e) {
					JOptionPane.showMessageDialog(null, "\u9023\u7dda\u903e\u6642");
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void addData(String msg) throws UploadFailedException {
		
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void message(String msg) {
		out.println(msg);
		out.flush();
	}
	
	@Override
	public String getVersion() {
		int version = ((DATE_YEAR * DATE_MD) / (RELEASE_TIME * RELEASE_COUNT)) * (16 + RELEASE_AM_PM);
		return "v2-" + version;
	}

	@Override
	public String getReleaseDate() {
		return RELEASE_DATE;
	}
	
	/**
	 * Init DataBase with errorLabel, Conncetion DB
	 * 
	 * @param conn Connection
	 * @param error JLabel
	 * @return Connection
	 * @throws ClassNotFoundException if class not found
	 * 
	 */
	public static Connection initDB(Connection conn, JLabel error) throws ClassNotFoundException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			conn = DriverManager.getConnection(com.yue.czcontrol.LoginFrame.DATA_SOURCE);
		} catch (SQLException e) {
			error.setText("\u8cc7\u6599\u5eab\u4e0d\u5b58\u5728");
			e.printStackTrace();
		}
		
		return conn;
	}
	
	/**
	 * Init DataBase without errorLabel, Connction DB
	 * 
	 * @param conn Connection
	 * @return Connection
	 * @throws ClassNotFoundException if class not found
	 * 
	 */
	public static Connection initDB(Connection conn) throws ClassNotFoundException{
		Class.forName("com.mysql.cj.jdbc.Driver");
		try {
			conn = DriverManager.getConnection(com.yue.czcontrol.LoginFrame.DATA_SOURCE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "\u8cc7\u6599\u5eab\u4e0d\u5b58\u5728");
			e.printStackTrace();
		}
		
		return conn;
	}
	
	/**
	 * Make user can Login
	 * 
	 * @param account User input account
	 * @param password User input password
	 * 
	 */
	private void login(String account, String password){
		
		try {
			conn.createStatement();
			String userList = "SELECT * FROM admin WHERE ACCOUNT= ? AND PASSWORD= ?";
			
			PreparedStatement psmt = conn.prepareStatement(userList);
			psmt.setString(1, account);
			psmt.setString(2, password);
			
			rs = psmt.executeQuery();
			
			if(rs.next()) {
				String pin = JOptionPane.showInputDialog("\u8acb\u8f38\u5165\u9a57\u8b49\u78bc");
				if(pin.equals(rs.getString("PIN"))) {
					mf.setUser(account);
					mf.setPassword(password);
					mf.init();
					mf.setVisible(true);
					message("\u5e33\u865f: " + account + ", \u5bc6\u78bc: " + password + " \u6210\u529f\u767b\u5165! \u5728" + socket.getRemoteSocketAddress() + " ~[console]");
					Thread thread = new Thread(mf);
					thread.start();
					setVisible(false);
				} else {
					error.setText("\u9a57\u8b49\u78bc\u932f\u8aa4");
				}
				
			} else {
				this.error.setText("<html> \u6c92\u6709\u6b64\u5e33\u865f\u6216\u5bc6\u78bc\u8f38\u5165\u932f\u8aa4 </html>");
			}
			
		} catch (SQLException e) {
			this.error.setText("\u8cc7\u6599\u5eab\u4e0d\u5b58\u5728");
		}
	}


	/**
	 * Create a Frame
	 * @throws IOException IOException
	 */
	public LoginFrame() throws IOException{
		socket = new Socket("27.147.3.116",5200);
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		if(in.readLine() != null) {
			message(getVersion() + " ~[version]");
			String versionOK = in.readLine();
			if(versionOK.equals("shutdown")) {
				JOptionPane.showMessageDialog(null, "\u7248\u672c\u904e\u6642\u6216\u4e0d\u7b26\u5408\u4f3a\u670d\u5668\u898f\u7bc4");
				System.exit(0);
			}
		}
		
		mf = new MainFrame(socket);
		
		try {
			this.conn = initDB(this.conn, error);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		//JFrame init
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 683, 503);
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-\u767b\u5165");
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Panel init
		panel.setBackground(Color.DARK_GRAY);
		panel.setBounds(0, 0, 315, 476);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//ImageLabel init
		imageLabel.setBounds(-45, 5, 405, 192);
		imageLabel.setIcon(new ImageIcon(LoginFrame.class.getResource("/com/yue/czcontrol/resource/kaguya.jpg")));
		panel.add(imageLabel);
		
		//Title init
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 35));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setBounds(20, 232, 285, 52);
		panel.add(title);
		
		//InfoLabel init
		infoLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setBounds(10, 294, 295, 172);
		panel.add(infoLabel);
		
		//LoginBtm init
		loginBtm.addActionListener(new ActionListener() {//If get clicked event, run the override method
			public void actionPerformed(ActionEvent e) {
				String account = accountInput.getText();
				String password = passwordInput.getText();
				login(account, password);
			}
		});
		loginBtm.setFont(new Font("Dialog", Font.PLAIN, 25));
		loginBtm.setForeground(Color.WHITE);
		loginBtm.setBackground(new Color(241, 57, 83));
		loginBtm.setActionCommand("login");
		loginBtm.setBounds(395, 380, 195, 49);
		contentPane.add(loginBtm);
		
		//accountInput init
		accountInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		accountInput.setBounds(364, 76, 263, 40);
		contentPane.add(accountInput);
		
		//passwordInput init
		passwordInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		passwordInput.setBounds(364, 230, 263, 40);
		contentPane.add(passwordInput);
		
		//accountLabel init
		accountLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		accountLabel.setBounds(364, 26, 93, 40);
		contentPane.add(accountLabel);
		
		//passwordLabel init
		passwordLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		passwordLabel.setBounds(364, 188, 77, 32);
		contentPane.add(passwordLabel);
		
		//registerLabel init
		registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		registerLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		registerLabel.setBounds(364, 435, 263, 26);
		registerLabel.addMouseListener(new MouseAdapter() {//If get clicked event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				new RegisterFrame().setVisible(true);
			}
		});
		contentPane.add(registerLabel);
		
		//errorLabel init
		error.setHorizontalAlignment(SwingConstants.CENTER);
		error.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		error.setBounds(325, 334, 344, 40);
		contentPane.add(error);
	}

	
}
