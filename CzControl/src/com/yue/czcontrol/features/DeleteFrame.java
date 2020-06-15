package com.yue.czcontrol.features;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
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
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.MainFrame;
import com.yue.czcontrol.exception.NameNotFoundException;
import com.yue.czcontrol.exception.UploadFailedException;
import com.yue.czcontrol.listener.TimerListener;
import com.yue.czcontrol.utils.BoxInit;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.TimeProperty;

public class DeleteFrame extends JFrame implements TimeProperty, BoxInit, SocketSetting{
	private static final long serialVersionUID = 1L;
	
	private final PrintWriter out;
	
	MainFrame mf;
	
	private Connection conn = null;
	private PreparedStatement psst = null;
	private ResultSet rs = null;
	
	private static boolean isOK = false;

	private final String handler;

	private final JComboBox<String> idBox = new JComboBox<>();
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

	/**
	 * If the member exist, return true. if not, return false
	 * @param id Member id
	 * @return boolean
	 */
	private boolean isExist(String id) {
		try {
			//Get Data(SQL Syntax)
			String select = "SELECT * FROM player WHERE ID=?";
			
			//String select to PreparedStatement
			psst = conn.prepareStatement(select);
			psst.setString(1, id);
			
			rs = psst.executeQuery();

			//if get data
			//if not get
			return rs.next();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void addData(String msg) {
		
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
	 * Get the id member, if the member belong to handler, return true. If not return false
	 * @param id Member id
	 * @param handler Member's handler
	 * @return boolean
	 */
	private boolean isHandler(String id, String handler) {
		try {
			//Get Data(SQL Syntax)
			String select = "SELECT * FROM player WHERE ID=? AND HANDLER=?";
			
			//String select to PreparedStatement
			psst = conn.prepareStatement(select);
			
			psst.setString(1, id);
			psst.setString(2, handler);
			
			rs = psst.executeQuery();

			//If get data
			//if not
			return rs.next();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Delete member
	 * @param id member id
	 * @param handler member's handler
	 * @throws UploadFailedException When the data upload failed
	 */
	private void deleteMember(String id, String handler) throws UploadFailedException {
		try {
			boolean isExistOK = isExist(id);
			boolean isHandlerOK = isHandler(id, handler);
			
			if(isExistOK) {
				if(isHandlerOK) {
					//Delete data (SQL Syntax)
					String delete = "DELETE FROM `player` WHERE ID=?";
					//String delete to PreparedStatement
					psst = conn.prepareStatement(delete);
					psst.setString(1, id);
					
					if(psst.executeUpdate() > 0) {//if get update > 0
						JOptionPane.showMessageDialog(null, "\u522a\u9664 \u7de8\u865f[" + id + "] \u5df2\u6210\u529f");
						message(dateFormatter.format(new Date())+ "\t" + handler + "\u70ba\u7de8\u865f [" + id + "] \u8acb\u5047 ~[console]");
						isOK = true;
					} else {
						throw new UploadFailedException("Data is Upload failed.");
					}
					
				} else {
					isOK = false;
					JOptionPane.showMessageDialog(null, "\u4f60\u4e0d\u662f\u6b64\u6210\u54e1\u7684\u8ca0\u8cac\u4eba");
				}
			} else {
				isOK = false;
				JOptionPane.showMessageDialog(null, "\u6c92\u6709\u6b64id\u6210\u54e1");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initBox(JComboBox<String> box){
		try {
			String select = "SELECT `NAME` FROM PLAYER WHERE HANDLER=?";
			
			psst = conn.prepareStatement(select);
			psst.setString(1,handler);
			
			rs = psst.executeQuery();
			
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
			psst = conn.prepareStatement(select);
			psst.setString(1, name);
			
			rs = psst.executeQuery();
			
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
	
	/**
	 * Create a Frame
	 * 
	 * @param userName The user Name
	 * @param user user Account
	 * @param password user Password
	 * @param socket The user's socket
	 * @throws IOException IOException
	 * 
	 */
	public DeleteFrame(String userName, String user, String password, Socket socket) throws IOException {
		this.handler = userName;
		
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
		JPanel contentPane = new JPanel();
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
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u6210\u54E1\u522A\u9664\r\n");
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
		
		//idLabel init
		JLabel selectLabel = new JLabel("\u9078\u64C7:");
		selectLabel.setFont(new Font("Dialog", Font.PLAIN, 60));
		selectLabel.setBounds(70, 223, 173, 98);
		contentPane.add(selectLabel);
		
		//TimeLabel init
		JLabel timeLabel = new JLabel("");
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timeLabel.setBounds(10, 87, 484, 43);
		panel.add(timeLabel);
		new TimerListener(timeLabel);
		
		//deleteBtm init
		Button deleteBtm = new Button("\u78BA\u8A8D\u522A\u9664");
		deleteBtm.setBackground(new Color(135, 206, 235));
		deleteBtm.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		deleteBtm.setActionCommand("delete");
		deleteBtm.setBounds(601, 413, 167, 52);
		deleteBtm.addActionListener(e -> {
			try {
				String item = (String)idBox.getSelectedItem();
				String id = getID(item);
				deleteMember(id, userName);
				if(isOK) {
					setVisible(false);
					mf.setUser(user);
					mf.setPassword(password);
					mf.setSocket(socket);
					mf.setSocketName(socket);
					mf.init();
					mf.setVisible(true);
				}
			} catch(NameNotFoundException | UploadFailedException nne) {
				nne.printStackTrace();
			}
		});
		contentPane.add(deleteBtm);
		
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
				mf.setVisible(true);
			}
		});
		contentPane.add(back);
		
		//init idBox
		initBox(idBox);
		idBox.setFont(new Font("Dialog", Font.PLAIN, 30));
		idBox.setBounds(253, 237, 436, 73);
		contentPane.add(idBox);
	}


	
}
