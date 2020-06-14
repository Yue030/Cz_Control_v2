package com.yue.czcontrol.features;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.MainFrame;
import com.yue.czcontrol.exception.UploadFailedException;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.TimeProperty;

public class InsertFrame extends JFrame implements TimeProperty, SocketSetting,Runnable{

	private static final long serialVersionUID = 1L;
	
	private PrintWriter out;
	
	MainFrame mf;
	
	private Connection conn = null;
	private PreparedStatement psmt = null;
	
	private JPanel contentPane;
	
	private JFormattedTextField nameInput = new JFormattedTextField();
	private JFormattedTextField rankInput = new JFormattedTextField();
	private JFormattedTextField activeInput = new JFormattedTextField();
	
	private JLabel timeLabel = new JLabel("");
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
	
	/**
	 * Set timeLabel Text every second.
	 */
	@Override
	public void run() {
		while (true) {
			timeLabel.setText("\u76ee\u524d\u6642\u9593:" + dateFormatter.format(Calendar.getInstance().getTime()));
			try {
				Thread.sleep(ONE_SECOND);
			} catch (Exception e) {
				timeLabel.setText("Error!!!");
			}
		}
	}
	
	/**
	 * Add a user data to DataBase without Active
	 * 
	 * @param name The user's name
	 * @param rank The user's rank
	 * @param handler Handler
	 * @throws UploadFailedException When the data upload failed
	 * 
	 */
	private void insertData(String name, String rank, String handler) throws UploadFailedException{
		try {
			//Add data(SQL Syntax)
			String insert = "INSERT INTO `player`(`NAME`, `RANK`, `HANDLER`) VALUES (?, ?, ?)";
			
			//String insert to PreparedStatement
			psmt = conn.prepareStatement(insert);
			psmt.setString(1, name);
			psmt.setString(2, rank);
			psmt.setString(3, handler);
			
			//Detect the data is add or not
			if(psmt.executeUpdate() > 0) {
				JOptionPane.showMessageDialog(null, "\u4f60\u5df2\u6210\u529f\u65b0\u589e\u4e00\u540d\u6210\u54e1");
				nameInput.setText("");
				rankInput.setText("");
			} else {
				throw new UploadFailedException("Data is Upload failed.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a user data to DataBase with Active
	 * 
	 * @param name The user's name
	 * @param rank The user's rank
	 * @param active The user's active
	 * @param handler Handler
	 * @throws UploadFailedException When the data upload failed
	 */
	private void insertData(String name, String rank, String active, String handler) throws UploadFailedException {
		try {
			//Add data(SQL Syntax)
			String insert = "INSERT INTO `player`(`NAME`, `RANK`,`ACTIVE`, `HANDLER`) VALUES (?, ?, ?, ?)";
			
			//String insert to PreparedStatement
			psmt = conn.prepareStatement(insert);
			psmt.setString(1, name);
			psmt.setString(2, rank);
			psmt.setString(3, active);
			psmt.setString(4, handler);
			
			//Detect the data is add or not
			if(psmt.executeUpdate() > 0) {
				JOptionPane.showMessageDialog(null, "\u4f60\u5df2\u6210\u529f\u65b0\u589e\u4e00\u540d\u6210\u54e1");
				message(dateFormatter.format(new Date())+ "\t" + handler + "\u65b0\u589e [" + name + "] \u6210\u54e1 ~[console]");
				nameInput.setText("");
				rankInput.setText("");
				activeInput.setText("");
			} else {
				throw new UploadFailedException("Data is Upload failed.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * Create a Frame
	 * @param userName The user Name
	 * @param user user Account
	 * @param password user Password
	 * @param socket The user's socket
	 * @throws IOException IOException
	 */
	public InsertFrame(String userName, String user, String password, Socket socket) throws IOException { 
		
		out = new PrintWriter(socket.getOutputStream());
		
		mf = new MainFrame(socket);
		
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-\u6210\u54e1\u65b0\u589e");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);		
		setBounds(100, 100, 787, 503);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Panel init
		JPanel panel = new JPanel();
		panel.setBackground(new Color(65,105,225));
		panel.setBounds(5, 5, 763, 140);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//Title init
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u6210\u54E1\u65B0\u589E\r\n");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);
		
		//userLabel init
		JLabel userLabel = new JLabel("\u76ee\u524d\u4f7f\u7528\u5e33\u865f: " + userName);
		userLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		
		
		userLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		userLabel.setBounds(478, 87, 285, 43);
		panel.add(userLabel);
		
		//TimeLabel init
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timeLabel.setBounds(10, 87, 484, 43);
		panel.add(timeLabel);
		
		//nameLabel init
		JLabel nameLabel = new JLabel("\u904A\u6232\u540D\u7A31:");
		nameLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		nameLabel.setBounds(15, 166, 200, 67);
		contentPane.add(nameLabel);
		
		//nameInput init
		nameInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		nameInput.setBounds(225, 176, 338, 55);
		contentPane.add(nameInput);
		
		//rankInput init
		rankInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		rankInput.setBounds(225, 271, 338, 55);
		contentPane.add(rankInput);
		
		//activeInput init
		activeInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		activeInput.setBounds(225, 371, 338, 55);
		contentPane.add(activeInput);
		
		//rankLabel init
		JLabel rankLabel = new JLabel("\u8077\u4F4D:");
		rankLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		rankLabel.setBounds(15, 259, 200, 67);
		contentPane.add(rankLabel);
		
		//activeLabel init
		JLabel activeLabel = new JLabel("\u6D3B\u8E8D\u5EA6:");
		activeLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		activeLabel.setBounds(15, 355, 200, 67);
		contentPane.add(activeLabel);
		
		//addBtm init
		Button addBtm = new Button("\u65B0\u589E\u6210\u54E1");
		addBtm.setBackground(new Color(135, 206, 250));
		addBtm.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 25));
		addBtm.setActionCommand("add\r\n");
		addBtm.setBounds(615, 410, 158, 55);
		addBtm.addActionListener(new ActionListener() {//If get clicked Event, run the override method

			@Override
			public void actionPerformed(ActionEvent e) {
				String name = new String(nameInput.getText());
				String rank = new String(rankInput.getText());
				String active = activeInput.getText().isEmpty() ? null : new String(activeInput.getText());
				//Detect name and rank is empty. if not, continue method
				if(!name.isEmpty() && !rank.isEmpty()) {
					//Detect active is empty or not
					if(active == null) {
						try {
							insertData(name, rank, userName);
						} catch (UploadFailedException nne) {
							nne.printStackTrace();
						}
					} else {
						try {
							insertData(name, rank, active, userName);
						} catch (UploadFailedException nne) {
							nne.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "\u8cc7\u6599\u4e0d\u5b8c\u6574");
				}
				
			}
			
		});
		
		contentPane.add(addBtm);
		
		//back init
		JLabel back = new JLabel("\u9EDE\u6211\u56DE\u4E0A\u4E00\u9801");
		back.setForeground(Color.RED);
		back.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		back.setBounds(634, 155, 134, 33);
		back.addMouseListener(new MouseAdapter() {//If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				
				mf.setUser(user);
				mf.setPassword(password);
				mf.setSocket(socket);
				mf.setSocketName(socket);
				
				mf.init();
				Thread thread = new Thread(mf);
				thread.start();
				mf.setVisible(true);
			}
		});
		contentPane.add(back);
	}

	
}

