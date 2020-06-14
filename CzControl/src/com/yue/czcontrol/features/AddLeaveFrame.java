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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.MainFrame;
import com.yue.czcontrol.exception.NameNotFoundException;
import com.yue.czcontrol.exception.UploadFailedException;
import com.yue.czcontrol.utils.BoxInit;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.TimeProperty;

public class AddLeaveFrame extends JFrame implements TimeProperty, BoxInit, SocketSetting, Runnable{

	private static final long serialVersionUID = 1L;
	private PrintWriter out = null;
	
	private String userName;
	
	MainFrame mf;
	
	private Connection conn = null;
	private PreparedStatement psmt = null;
	private ResultSet rs = null;
	
	private JPanel contentPane;
	private JLabel timeLabel = new JLabel("");
	
	private JComboBox<String> idBox = new JComboBox<String>();
	
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
	 * detect the id is exist or not
	 * @param id need detect id
	 * @return boolean
	 */
	private boolean detectExistID(String id) {
		try {
			//Get data(SQL Syntax)
			String select = "SELECT * FROM `player` WHERE ID=?";
			//String select to PreparedStatement
			psmt = conn.prepareStatement(select);
			psmt.setString(1, id);
			
			rs = psmt.executeQuery();
			
			if(rs.next()) {//if get data
				return true;
			} else {//if not
				return false;
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Add data to DB
	 * 
	 * @param lid The member id
	 * @param reason The member why leave
	 * @param date Leave Dates
	 * @param handler The handler
	 * @throws UploadFailedException When the data upload failed
	 */
	private void addLeave(String lid, String reason, String date, String handler) throws UploadFailedException {
		boolean isOK = detectExistID(lid);
		if(isOK) {
			try {
				//add data(SQL Syntax)
				String insert = "INSERT INTO `leave`(`LID`,`REASON`, `DATE`, `HANDLER`) VALUES (?, ?, ?, ?)";
				
				//String insert to PreparedStatement
				psmt = conn.prepareStatement(insert);
				psmt.setString(1, lid);
				psmt.setString(2, reason);
				psmt.setString(3, date);
				psmt.setString(4, handler);
				
				if(psmt.executeUpdate() > 0) {//if add success
					JOptionPane.showMessageDialog(null, "\u4f60\u5df2\u6210\u529f\u70ba\u7de8\u865f [" + lid + "] \u8acb\u5047");
					message(dateFormatter.format(new Date())+ "\t" + userName + "\u70ba\u7de8\u865f [" + lid + "] \u8acb\u5047 ~[console]");
				} else {//if not
					throw new UploadFailedException("Data is Upload failed.");
				}
				
			} catch(SQLException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(null, "\u6c92\u6709\u6b64id\u6210\u54e1");
		}
	}
	
	@Override
	public void initBox(JComboBox<String> box){
		try {
			String select = "SELECT `NAME` FROM PLAYER";
			
			psmt = conn.prepareStatement(select);
			
			rs = psmt.executeQuery();
			
			while(rs.next()) {
				box.addItem(rs.getString("NAME"));
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getID(String name) throws NameNotFoundException{
		try {
			String select = "SELECT `ID` FROM PLAYER WHERE NAME=?";
			psmt = conn.prepareStatement(select);
			psmt.setString(1, name);
			
			rs = psmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString("ID");
			} else {
				throw new NameNotFoundException("The name is can't get the user id");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
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
	 * create a Frame
	 * @param userName user's Name
	 * @param user user's Account
	 * @param password user's Password
	 * @param socket The user's socket
	 * @throws NameNotFoundException When the NameNotFound
	 * @throws IOException IOException
	 */
	public AddLeaveFrame(String userName, String user, String password, Socket socket) throws NameNotFoundException, IOException {
		
		this.userName = userName;
		
		out = new PrintWriter(socket.getOutputStream());
		
		mf = new MainFrame(socket);
		
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-\u522a\u9664\u6210\u54e1");
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
				
		//title init
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u8ACB\u5047\u7CFB\u7D71\r\n");
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
		
		//idLabel init
		JLabel selectLabel = new JLabel("\u9078\u64C7:");
		selectLabel.setFont(new Font("Dialog", Font.PLAIN, 50));
		selectLabel.setBounds(15, 155, 120, 54);
		contentPane.add(selectLabel);
		
		//reasonLanel init
		JLabel reasonLabel = new JLabel("\u539F\u56E0:");
		reasonLabel.setHorizontalAlignment(SwingConstants.LEFT);
		reasonLabel.setFont(new Font("Dialog", Font.PLAIN, 50));
		reasonLabel.setBounds(10, 273, 125, 54);
		contentPane.add(reasonLabel);
		
		//reasonInput init
		JFormattedTextField reasonInput = new JFormattedTextField();
		reasonInput.setHorizontalAlignment(SwingConstants.CENTER);
		reasonInput.setFont(new Font("Dialog", Font.PLAIN, 30));
		reasonInput.setBounds(145, 279, 333, 54);
		contentPane.add(reasonInput);
		
		//dateLabel init
		JLabel dateLabel = new JLabel("\u65E5\u671F:");
		dateLabel.setFont(new Font("Dialog", Font.PLAIN, 50));
		dateLabel.setBounds(10, 406, 125, 54);
		contentPane.add(dateLabel);
		
		//dateInput init
		JFormattedTextField dateInput = new JFormattedTextField();
		dateInput.setHorizontalAlignment(SwingConstants.CENTER);
		dateInput.setFont(new Font("Dialog", Font.PLAIN, 30));
		dateInput.setBounds(145, 406, 333, 54);
		contentPane.add(dateInput);
		
		//leave init
		Button leave = new Button("\u8ACB\u5047");
		leave.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		leave.setBackground(new Color(102, 255, 255));
		leave.setActionCommand("leave");
		leave.setBounds(602, 402, 171, 63);
		leave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String item = (String)idBox.getSelectedItem();
					String id = getID(item);
					String reason = reasonInput.getText();
					String date = dateInput.getText();
					String handler = userName;
					
					if(!id.isEmpty() && !reason.isEmpty() && !date.isEmpty() && !handler.isEmpty()) {
						addLeave(id, reason, date, handler);
					} else {
						JOptionPane.showMessageDialog(null, "\u8cc7\u6599\u4e0d\u5b8c\u6574");
					}
				} catch (NameNotFoundException nne) {
					JOptionPane.showMessageDialog(null, "Please refresh the window");
					nne.printStackTrace();
				} catch (UploadFailedException ufe) {
					JOptionPane.showMessageDialog(null, "\u8acb\u5047\u5931\u6557");
					ufe.printStackTrace();
				}
			}
			
		});
		contentPane.add(leave);
		
		//back input
		JLabel back = new JLabel("\u9EDE\u6211\u56DE\u4E3B\u9078\u55AE");
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
		
		//init idBox
		initBox(idBox);
		idBox.setFont(new Font("Dialog", Font.PLAIN, 30));
		idBox.setBounds(145, 161, 333, 54);
		contentPane.add(idBox);
	}
}
