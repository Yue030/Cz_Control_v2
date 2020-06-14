package com.yue.czcontrol.features;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.MainFrame;
import com.yue.czcontrol.exception.UploadFailedException;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.TimeProperty;

public class MsgFrame extends JFrame implements TimeProperty, SocketSetting{
	
	MainFrame mf;

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	private JLabel timeLabel = new JLabel("");
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
	
	private Connection conn = null;
	private ResultSet rs = null;
	
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	private JScrollPane scrollPane = new JScrollPane();
	private JTextArea msgArea = new JTextArea();
	private JFormattedTextField msgInput = new JFormattedTextField();
	private JButton msgBtm = new JButton("\u50B3\u9001");
	private JLabel back = new JLabel("\u9EDE\u6211\u56DE\u4E3B\u9078\u55AE");
	
	@Override
	public void addData(String msg) throws UploadFailedException{
		try {
    		String insert = "INSERT INTO `message`(`msg`) VALUES (?)";
    		PreparedStatement psmt = conn.prepareStatement(insert);
    		psmt.setString(1, msg);
    		
    		if(psmt.executeUpdate() > 0) {
    			
    		} else {
    			throw new UploadFailedException("Data is Upload failed.");
    		}
    		
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
	}

	@Override
	public void initData() {
		try {
    		String select = "SELECT * FROM `MESSAGE`";
    		PreparedStatement psmt = conn.prepareStatement(select);
    		
    		rs = psmt.executeQuery();
    		
    		while(rs.next()) {
    			msgArea.append(rs.getString("msg") + "\n");
    		}
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
	}
	
	@Override
	public void message(String msg) {
		out.println(msg);
        out.flush();
	}
	
	/**
	 * Clear the msgInput
	 */
	private void clear() {
		msgInput.setText("");
	}
	
	/**
	 * Detect key, if ok, add data to DB
	 * @param keyCode key code
	 * @param userName user name
	 */
	private void addData_key(int keyCode, String userName) {
		if(keyCode == KeyEvent.VK_ENTER) {
			String msg = msgInput.getText();
			if(!msg.isEmpty()) {
				clear();
				String sendMsg = dateFormatter.format(new Date()) + "\t" + userName + ":" + msg;
				message(sendMsg);
				try {
					addData(sendMsg);
				} catch (UploadFailedException ufe) {
					ufe.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Create a frame
	 * @param userName user name
	 * @param user userAccount
	 * @param password user Password
	 * @param socket The user's socket
	 * @throws IOException IOException
	 * @throws UnknownHostException if host is unknow
	 * @throws ConnectException If connect failed
	 */
	public MsgFrame(String userName, String user, String password, Socket socket) throws IOException, UnknownHostException, ConnectException{
		
		out = new PrintWriter(socket.getOutputStream());
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		mf = new MainFrame(socket);
		
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-\u804a\u5929\u5ba4/\u516c\u544a");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);		
		setBounds(100, 100, 787, 503);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
				
		//panel init
		JPanel panel = new JPanel();
		panel.setBackground(new Color(65,105,225));
		panel.setBounds(5, 5, 763, 140);
		contentPane.add(panel);
		panel.setLayout(null);
				
		//Title init
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u804A\u5929\u5BA4/\u516C\u544A\r\n");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u8cc7\u6599\u4e0d\u5b8c\u6574", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);
				
		//TimeLabel init
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timeLabel.setBounds(10, 87, 484, 43);
		panel.add(timeLabel);
				
		//userLabel init
		JLabel userLabel = new JLabel("\u76ee\u524d\u4f7f\u7528\u5e33\u865f: " + userName);
		userLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		userLabel.setFont(new Font("\u8cc7\u6599\u4e0d\u5b8c\u6574", Font.PLAIN, 20));
		userLabel.setBounds(478, 87, 285, 43);
		panel.add(userLabel);
		
		//Scrollpane init
		scrollPane.setBounds(5, 155, 763, 225);
		contentPane.add(scrollPane);
		
		//msgArea init
		msgArea.setFont(new Font("Dialog", Font.PLAIN, 25));
		msgArea.setEditable(false);
		scrollPane.setViewportView(msgArea);
		
		//msgInput init
		msgInput.setFont(new Font("Dialog", Font.PLAIN, 30));
		msgInput.setBounds(5, 390, 352, 75);
		msgInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				addData_key(e.getKeyCode(), userName);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				addData_key(e.getKeyCode(), userName);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				addData_key(e.getKeyCode(), userName);
			}
			
		});
		contentPane.add(msgInput);
		
		//msgBtm init
		msgBtm.setFont(new Font("Dialog", Font.PLAIN, 20));
		msgBtm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = msgInput.getText();
				if(!msg.isEmpty()) {
					clear();
					String sendMsg = dateFormatter.format(new Date()) + "\t" + userName + ":" + msg;
					message(sendMsg);
					try {
						addData(sendMsg);
					} catch (UploadFailedException ufe) {
						ufe.printStackTrace();
					}
				}
			}
		});
		msgBtm.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				addData_key(e.getKeyCode(), userName);
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		});
		msgBtm.setBounds(609, 424, 159, 41);
		contentPane.add(msgBtm);
		
		//back init
		back.setForeground(Color.RED);
		back.setFont(new Font("\u8cc7\u6599\u4e0d\u5b8c\u6574", Font.PLAIN, 20));
		back.setBounds(368, 432, 134, 33);
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
		
		new Thread(new Runnable() {
            @Override
            public void run() {
            	initData();
                try {
                    while (true){
                    	try {
                    		String text = in.readLine();
                        	if(!text.isEmpty()) {
                        		msgArea.append(text + "\n");
                        	}
                    	} catch(NullPointerException e) {
                    		msgArea.append("\u4f3a\u670d\u5668\u9023\u7dda\u65bc\u6642\u6216\u662f\u4f60\u5df2\u88ab\u5f37\u5236\u65b7\u7dda\uff0c\u8acb\u7a0d\u5f8c\u518d\u91cd\u958b\u6b64\u4ecb\u9762");
                    		msgBtm.setEnabled(false);
                    		msgInput.setEnabled(false);
                    		break;
                    	}
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
	}
}
