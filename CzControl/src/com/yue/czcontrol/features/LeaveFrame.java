package com.yue.czcontrol.features;

import com.yue.czcontrol.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class LeaveFrame extends JFrame{

	private static final long serialVersionUID = 1L;
	
	MainFrame mf;
	
	private Connection conn = null;
	private PreparedStatement psst = null;
	private ResultSet rs = null;

	private JTable table;
	private final JScrollPane scrollPane = new JScrollPane();
	
	/**
	 * create table without id
	 */
	private void createData() {
		DefaultTableModel tableModel;
		
		if(table != null) {
			table = null;
		}
		
		try {
			//Get data(SQL Syntax)
			String select = "SELECT b.id, NAME, DATE, REASON, b.HANDLER FROM `PLAYER` AS A INNER JOIN `leave` as b ON a.id = b.lid";
			//String select to PreparedStatement
			psst = conn.prepareStatement(select);
			
			rs = psst.executeQuery();
			
			//Set colunm name
			String[] columnName = {"\u7de8\u865f", "\u540d\u7a31", "\u6642\u9593" , "\u539f\u56e0" , "\u8ca0\u8cac\u4eba"};
			
			tableModel = new DefaultTableModel(columnName, 0);
			
			//Detect data
			while(rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				String date = rs.getString("DATE");
				String reason = rs.getString("REASON");
				String handler = rs.getString("HANDLER");
				
			    String[] data = {id, name, date, reason, handler};
				
				tableModel.addRow(data);
			}
			table = new JTable(tableModel);
			table.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 12));
			table.setPreferredScrollableViewportSize(new Dimension(500, 200));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			scrollPane.setViewportView(table);
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * create table with id
	 * @param aid search id
	 */
	private void createData(String aid) {
		
		if(table != null) {
			table = null;
		}
		try {
			conn.createStatement();
			
			//Get data(SQL Syntax)
			String select = "SELECT b.id, NAME, DATE, REASON, b.HANDLER FROM `PLAYER` AS A INNER JOIN `leave` as b ON a.id = b.lid WHERE a.id= ?";
			//String select to PreparedStatement
			psst = conn.prepareStatement(select);
			psst.setString(1, aid);
			
			rs = psst.executeQuery();
			
			//Set colunm name
			String[] columnName = {"\u7de8\u865f", "\u540d\u7a31", "\u6642\u9593" , "\u539f\u56e0" , "\u8ca0\u8cac\u4eba"};
			
			DefaultTableModel tableModel = new DefaultTableModel(columnName, 0);

			//Detect data
			while(rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				String date = rs.getString("DATE");
				String reason = rs.getString("REASON");
				String handler = rs.getString("HANDLER");
				
			    String[] data = {id, name, date, reason, handler};
				
			    System.out.println(Arrays.toString(data));
			    
				tableModel.addRow(data);
			}
			table = new JTable(tableModel);
			table.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 12));
			table.setPreferredScrollableViewportSize(new Dimension(500, 200));
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			scrollPane.setViewportView(table);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a Frame
	 * @param userName user's name
	 * @param user user's account
	 * @param password user's password
	 * @param socket The user's socket
	 */
	public LeaveFrame(String userName, String user, String password, Socket socket) {
		
		mf = new MainFrame(socket);
		
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-\u8acb\u5047\u7cfb\u7d71");
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
		
		//Title init
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u8ACB\u5047\u7CFB\u7D71\r\n");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);
		
		//searchLabel init
		JLabel searchLabel = new JLabel("\u5229\u7528\u6210\u54E1ID\u641C\u5C0B:");
		searchLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		searchLabel.setBounds(20, 79, 261, 55);
		panel.add(searchLabel);
		
		//searchInput init
		JFormattedTextField searchInput = new JFormattedTextField();
		searchInput.setFont(new Font("Dialog", Font.PLAIN, 30));
		searchInput.setBounds(291, 79, 406, 50);
		searchInput.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				String id = searchInput.getText();
				if(!id.isEmpty()) {
					createData(id);
				} else {
					createData();
				}
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {				
				String id = searchInput.getText();
				if(!id.isEmpty()) {
					createData(id);
				} else {
					createData();
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				String id = searchInput.getText();
				if(!id.isEmpty()) {
					createData(id);
				} else {
					createData();
				}
			}
		});
		
		panel.add(searchInput);
		
		//ScrollPane init
		scrollPane.setBounds(5, 155, 763, 255);
		contentPane.add(scrollPane);
		
		//table init
		createData();
		
		//leave init
		Button leave = new Button("\u8ACB\u5047");
		leave.setBackground(new Color(102, 255, 255));
		leave.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		leave.setActionCommand("leave");
		leave.setBounds(597, 412, 171, 63);
		leave.addActionListener(e -> {
			setVisible(false);
			AddLeaveFrame frame;
			try {
				frame = new AddLeaveFrame(userName, user, password, socket);
				frame.setVisible(true);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		contentPane.add(leave);
		
		//back init
		JLabel back = new JLabel("\u9EDE\u6211\u56DE\u4E0A\u4E00\u9801");
		back.setForeground(Color.RED);
		back.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		back.setBounds(10, 432, 134, 33);
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
	}
}
