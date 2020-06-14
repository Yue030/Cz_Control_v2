package com.yue.czcontrol;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.exception.NameNotFoundException;
import com.yue.czcontrol.features.DeleteFrame;
import com.yue.czcontrol.features.InfoFrame;
import com.yue.czcontrol.features.InsertFrame;
import com.yue.czcontrol.features.LeaveFrame;
import com.yue.czcontrol.features.MsgFrame;
import com.yue.czcontrol.features.SelectFrame;
import com.yue.czcontrol.features.UpdateFrame;
import com.yue.czcontrol.utils.TimeProperty;

public class MainFrame extends JFrame implements TimeProperty, Runnable {
	
	private Socket socket;
	private String socketName;
	
	private static final long serialVersionUID = 1L;
	
	private Connection conn = null;
	private ResultSet rs = null;

	private JPanel contentPane;

	private String user = null;
	private String password = null;
	private String userName = null;
	private JLabel userLabel = new JLabel("");
	private JLabel timeLabel = new JLabel("");
	
	/**
	 * Set timeLabel Text every second.
	 */
	@Override
	public void run() {
		while (true) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
			timeLabel.setText("\u76ee\u524d\u6642\u9593:" + dateFormatter.format(Calendar.getInstance().getTime()));
			try {
				Thread.sleep(ONE_SECOND);
			} catch (InterruptedException e) {
				timeLabel.setText("Error");
				e.printStackTrace();
			}
		}

	}
	
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void setSocketName(Socket socket) {
		this.socketName = socket.getRemoteSocketAddress().toString();
	}
	
	public String getSocketName() {
		return this.socketName;
	}
	
	/**
	 * Set User
	 * 
	 * @param user String
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get User
	 * 
	 * @return user String
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Set Password
	 * 
	 * @param password String
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get Password
	 * 
	 * @return password String
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Use user to get userName
	 * 
	 * @param user String
	 * @return userName String
	 */
	public String getUserName(String user){
		userName = null;
		try {
			// Get the data(SQL Syntax)
			String userList = "SELECT * FROM admin WHERE ACCOUNT= ?";

			// String userList to PrepareStatement
			PreparedStatement psmt = conn.prepareStatement(userList);
			psmt.setString(1, user);

			rs = psmt.executeQuery();

			// Detect data is exist or not
			if (rs.next()) {
				userName = rs.getString("NAME");
			} else {
				
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userName;
	}

	/**
	 * init DataBase
	 */
	public void init() {
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
			userLabel.setText("\u76ee\u524d\u4f7f\u7528\u5e33\u865f: " + getUserName(getUser()));
			System.out.println(getUserName(getUser()) + "/" + getUser() + "-" + getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a Frame
	 * @param socket Socket
	 */
	public MainFrame(Socket socket){
		
		this.socket = socket;
		this.socketName = socket.getRemoteSocketAddress().toString();
		
		// JFrame init
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 787, 503);
		setResizable(false);
		setTitle("Cz\u7BA1\u7406\u7CFB\u7D71-\u4E3B\u4ECB\u9762");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// MainPanel init
		JPanel panel = new JPanel();
		panel.setBackground(new Color(65, 105, 225));
		panel.setBounds(10, 5, 755, 140);
		contentPane.add(panel);
		panel.setLayout(null);

		// Title init
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);

		// userLabel init
		userLabel.setHorizontalAlignment(SwingConstants.TRAILING);

		userLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		userLabel.setBounds(494, 87, 259, 43);
		panel.add(userLabel);
		
		//TimeLabel init
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timeLabel.setBounds(10, 87, 484, 43);
		panel.add(timeLabel);
		// viewPanel init
		JPanel viewPanel = new JPanel();
		viewPanel.setBackground(Color.LIGHT_GRAY);
		viewPanel.setBounds(10, 176, 140, 120);
		contentPane.add(viewPanel);
		viewPanel.setLayout(null);
		viewPanel.addMouseListener(new MouseAdapter() {// If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				SelectFrame frame;
				try {
					frame = new SelectFrame(userName, user, password, socket);
					Thread thread = new Thread(frame);
					thread.start();
					frame.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// viewImg init
		JLabel viewImg = new JLabel("");
		viewImg.setHorizontalAlignment(SwingConstants.CENTER);
		viewImg.setFont(new Font("Arial", Font.PLAIN, 5));
		viewImg.setBounds(0, 5, 137, 81);
		viewImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/eye.png")));
		viewPanel.add(viewImg);

		// viewLabel init
		JLabel viewLabel = new JLabel("\u6AA2\u8996\u6210\u54E1");
		viewLabel.setForeground(Color.BLACK);
		viewLabel.setBounds(32, 82, 80, 28);
		viewPanel.add(viewLabel);
		viewLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		viewLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// addPanel init
		JPanel addPanel = new JPanel();
		addPanel.setBackground(Color.LIGHT_GRAY);
		addPanel.setLayout(null);
		addPanel.setBounds(470, 176, 140, 120);
		contentPane.add(addPanel);
		addPanel.addMouseListener(new MouseAdapter() {// If get clicked event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				InsertFrame frame;
				try {
					frame = new InsertFrame(userName, user, password, socket);
					Thread thread = new Thread(frame);
					thread.start();
					frame.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});

		// addImg init
		JLabel addImg = new JLabel("");
		addImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/friends.png")));
		addImg.setHorizontalAlignment(SwingConstants.CENTER);
		addImg.setFont(new Font("Arial", Font.PLAIN, 5));
		addImg.setBounds(0, 5, 137, 81);
		addPanel.add(addImg);

		// addLabel init
		JLabel addLabel = new JLabel("\u65B0\u589E\u6210\u54E1");
		addLabel.setForeground(Color.BLACK);
		addLabel.setHorizontalAlignment(SwingConstants.CENTER);
		addLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		addLabel.setBounds(32, 82, 80, 28);
		addPanel.add(addLabel);

		// editPanel init
		JPanel editPanel = new JPanel();
		editPanel.setBackground(Color.LIGHT_GRAY);
		editPanel.setLayout(null);
		editPanel.setBounds(625, 176, 140, 120);
		contentPane.add(editPanel);
		editPanel.addMouseListener(new MouseAdapter() {// If
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				UpdateFrame frame;
				try {
					frame = new UpdateFrame(userName, user, password, socket);
					Thread thread = new Thread(frame);
					thread.start();
					frame.setVisible(true);
				} catch (NameNotFoundException nne) {
					nne.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// editImg init
		JLabel editImg = new JLabel("");
		editImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/edit.png")));
		editImg.setHorizontalAlignment(SwingConstants.CENTER);
		editImg.setFont(new Font("Arial", Font.PLAIN, 5));
		editImg.setBounds(0, 5, 137, 81);
		editPanel.add(editImg);

		// editLabel init
		JLabel editLabel = new JLabel("\u7DE8\u8F2F\u6210\u54E1");
		editLabel.setHorizontalAlignment(SwingConstants.CENTER);
		editLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		editLabel.setBounds(32, 82, 80, 28);
		editPanel.add(editLabel);

		// logoutPanel init
		JPanel logoutPanel = new JPanel();
		logoutPanel.setBackground(Color.LIGHT_GRAY);
		logoutPanel.setLayout(null);
		logoutPanel.setBounds(470, 323, 140, 120);
		contentPane.add(logoutPanel);
		logoutPanel.addMouseListener(new MouseAdapter() {// If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				int option = JOptionPane
						.showConfirmDialog(null,
								"\u78ba\u5b9a\u8981\u5f9e\u4f7f\u7528\u8005 [" + getUserName(getUser())
										+ "] \u767b\u51fa\u55ce?",
								"\u78ba\u5b9a\u767b\u51fa?", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					userLabel = null;
					userName = null;
					user = null;

					setVisible(false);
					
					try {
						LoginFrame frame = new LoginFrame();
						frame.setVisible(true);;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {}
			}
		});

		// logoutImg init
		JLabel logoutImg = new JLabel("");
		logoutImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/logout.png")));
		logoutImg.setBackground(Color.LIGHT_GRAY);
		logoutImg.setHorizontalAlignment(SwingConstants.CENTER);
		logoutImg.setFont(new Font("Arial", Font.PLAIN, 5));
		logoutImg.setBounds(0, 5, 137, 81);
		logoutPanel.add(logoutImg);

		// logoutLabel init
		JLabel logoutLabel = new JLabel("\u767B\u51FA");
		logoutLabel.setHorizontalAlignment(SwingConstants.CENTER);
		logoutLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		logoutLabel.setBounds(32, 82, 80, 28);
		logoutPanel.add(logoutLabel);

		// infoPanel init
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(null);
		infoPanel.setBackground(Color.LIGHT_GRAY);
		infoPanel.setBounds(165, 323, 140, 120);
		contentPane.add(infoPanel);
		infoPanel.addMouseListener(new MouseAdapter() {// If get clicked event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				InfoFrame frame = new InfoFrame(userName, user, password, socket);
				Thread thread = new Thread(frame);
				thread.start();
				frame.setVisible(true);
			}
		});

		// infoImg init
		JLabel infoImg = new JLabel("");
		infoImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/info.png")));
		infoImg.setHorizontalAlignment(SwingConstants.CENTER);
		infoImg.setFont(new Font("Arial", Font.PLAIN, 5));
		infoImg.setBounds(0, 5, 137, 81);
		infoPanel.add(infoImg);

		// infoLabel init
		JLabel infoLabel = new JLabel("\u7A0B\u5F0F\u8CC7\u8A0A");
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setForeground(Color.BLACK);
		infoLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		infoLabel.setBounds(32, 82, 80, 28);
		infoPanel.add(infoLabel);

		// leavePanel init
		JPanel leavePanel = new JPanel();
		leavePanel.setLayout(null);
		leavePanel.setBackground(Color.LIGHT_GRAY);
		leavePanel.setBounds(165, 176, 140, 120);
		contentPane.add(leavePanel);
		leavePanel.addMouseListener(new MouseAdapter() {// If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				new LeaveFrame(userName, user, password, socket).setVisible(true);
			}
		});

		// leaveImg init
		JLabel leaveImg = new JLabel("");
		leaveImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/leave.png")));
		leaveImg.setHorizontalAlignment(SwingConstants.CENTER);
		leaveImg.setFont(new Font("Arial", Font.PLAIN, 5));
		leaveImg.setBounds(0, 5, 137, 81);
		leavePanel.add(leaveImg);

		// leaveLabel init
		JLabel leaveLabel = new JLabel("\u6210\u54E1\u8ACB\u5047");
		leaveLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leaveLabel.setForeground(Color.BLACK);
		leaveLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		leaveLabel.setBounds(32, 82, 80, 28);
		leavePanel.add(leaveLabel);

		// deletePanel init
		JPanel deletePanel = new JPanel();
		deletePanel.setLayout(null);
		deletePanel.setBackground(Color.LIGHT_GRAY);
		deletePanel.setBounds(318, 176, 140, 120);
		contentPane.add(deletePanel);
		deletePanel.addMouseListener(new MouseAdapter() {// If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				DeleteFrame frame;
				try {
					frame = new DeleteFrame(userName, user, password, socket);
					Thread thread = new Thread(frame);
					thread.start();
					frame.setVisible(true);
				} catch (NameNotFoundException nne) {
					nne.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// deleteImg init
		JLabel deleteImg = new JLabel("");
		deleteImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/delete.png")));
		deleteImg.setHorizontalAlignment(SwingConstants.CENTER);
		deleteImg.setFont(new Font("Arial", Font.PLAIN, 5));
		deleteImg.setBounds(0, 5, 137, 81);
		deletePanel.add(deleteImg);

		// deleteLabel init
		JLabel deleteLabel = new JLabel("\u522A\u9664\u6210\u54E1");
		deleteLabel.setHorizontalAlignment(SwingConstants.CENTER);
		deleteLabel.setForeground(Color.BLACK);
		deleteLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		deleteLabel.setBounds(32, 82, 80, 28);
		deletePanel.add(deleteLabel);
		
		//msgPanel init
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(null);
		msgPanel.setBackground(Color.LIGHT_GRAY);
		msgPanel.setBounds(320, 323, 140, 120);
		msgPanel.addMouseListener(new MouseAdapter() {// If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				MsgFrame frame;
				try {
					frame = new MsgFrame(userName, user, password, socket);
					frame.setVisible(true);
				} catch (ConnectException e1) {
					setVisible(true);
					JOptionPane.showMessageDialog(null, "\u804a\u5929\u5ba4\u9023\u7dda\u903e\u6642");
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					setVisible(true);
					JOptionPane.showMessageDialog(null, "\u672a\u77e5\u7684Host");
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(msgPanel);
		
		//msgImg
		JLabel msgImg = new JLabel("");
		msgImg.setIcon(new ImageIcon(MainFrame.class.getResource("/com/yue/czcontrol/resource/message.png")));
		msgImg.setHorizontalAlignment(SwingConstants.CENTER);
		msgImg.setFont(new Font("Arial", Font.PLAIN, 5));
		msgImg.setBounds(0, 5, 137, 81);
		msgPanel.add(msgImg);
		
		//msgLabel
		JLabel msgLabel = new JLabel("\u804A\u5929\u5BA4/\u516C\u544A");
		msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		msgLabel.setForeground(Color.BLACK);
		msgLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		msgLabel.setBounds(10, 82, 120, 28);
		msgPanel.add(msgLabel);
	}
}
