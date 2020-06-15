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
import com.yue.czcontrol.listener.TimerListener;
import com.yue.czcontrol.utils.BoxInit;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.TimeProperty;

public class UpdateFrame extends JFrame implements TimeProperty, BoxInit, SocketSetting{

	private static final long serialVersionUID = 1L;
	
	private final PrintWriter out;
	
	private final String userName;
	
	MainFrame mf;

	private Connection conn = null;
	private PreparedStatement psst = null;
	private ResultSet rs = null;


	private final JLabel handler = new JLabel("\u8CA0\u8CAC\u4EBA:");
	private final JFormattedTextField nameInput = new JFormattedTextField();
	private final JFormattedTextField rankInput = new JFormattedTextField();
	private final JFormattedTextField activeInput = new JFormattedTextField();
	private final JComboBox<String> idBox = new JComboBox<>();
	private final JLabel error = new JLabel("");

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
	
	@Override
	public void addData(String msg){
		
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
	 * get data from DataBase
	 * and print it in TextField
	 * 
	 * @param id The arg's id
	 */
	private void getData(String id) {
		try {
			//Get data(SQL Syntax)
			String select = "SELECT * FROM PLAYER WHERE ID=?";

			//String select to PrepareStatement
			psst = conn.prepareStatement(select);
			psst.setString(1, id);

			rs = psst.executeQuery();

			//Detect data is exist
			if (rs.next()) {
				this.error.setText("");

				//Get the name and more data
				String name = rs.getString("NAME");
				String active = rs.getString("ACTIVE");
				String rank = rs.getString("RANK");
				String handler = rs.getString("HANDLER");

				//Write the data to input field
				this.nameInput.setText(name);
				this.activeInput.setText(active);
				this.rankInput.setText(rank);
				this.handler.setText("\u5275\u5efa\u8005: " + handler);
			} else {//if not
				//delete the data in input field
				this.nameInput.setText("");
				this.activeInput.setText("");
				this.rankInput.setText("");
				this.handler.setText("\u5275\u5efa\u8005: ");
				this.error.setText("\u7121\u6b64ID\u6210\u54e1");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Upload the data to dataBase
	 * 
	 * @param id The user's id
	 * @param name The user's name
	 * @param active The user's active
	 * @param rank The user's rank
	 * @throws UploadFailedException When the data upload failed
	 */
	private void updateData(String id, String name, String active, String rank) throws UploadFailedException {
		try {
			//Edit the data(SQL Syntax)
			String update = "UPDATE player SET NAME=?, `RANK`=?, ACTIVE=? WHERE ID=?";
			//String update to PrepareStatement
			psst = conn.prepareStatement(update);
			
			psst.setString(1, name);
			psst.setString(2, rank);
			psst.setString(3, active);
			psst.setString(4, id);
			
			//Detect the data is update success or not
			if (psst.executeUpdate() > 0) {
				JOptionPane.showMessageDialog(null, "\u7de8\u8f2f \u7de8\u865f[" + id + "] \u5df2\u6210\u529f");
				message(dateFormatter.format(new Date())+ "\t" + userName + "\u5df2\u66f4\u6539 [" + id + "] \u6210\u54e1\u8cc7\u6599 ~[console]");
			} else {
				throw new UploadFailedException("Data is Upload failed.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void initBox(JComboBox<String> box){
		try {
			String select = "SELECT `NAME` FROM PLAYER";
			
			psst = conn.prepareStatement(select);
			
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
	 * @param name The user Name
	 * @param user user Account
	 * @param password user Password
	 * @param socket The user's socket
	 * @throws IOException IOException
	 */
	public UpdateFrame(String name, String user, String password, Socket socket) throws IOException {
		this.userName = name;
		
		out = new PrintWriter(socket.getOutputStream());
		
		mf = new MainFrame(socket);
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setTitle("Cz\u7BA1\u7406\u7CFB\u7D71-\u6AA2\u8996\u6210\u54E1");
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
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u6210\u54E1\u7DE8\u8F2F\r\n");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);
		
		//userLabel init
		JLabel userLabel = new JLabel("\u76ee\u524d\u4f7f\u7528\u5e33\u865f: " + name);
		userLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		
		
		userLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		userLabel.setBounds(478, 87, 285, 43);
		panel.add(userLabel);
		
		//idLabel init
		JLabel selectLabel = new JLabel("\u9078\u64C7:");
		selectLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		selectLabel.setBounds(15, 155, 90, 50);
		contentPane.add(selectLabel);
		
		//TimeLabel init
		JLabel timeLabel = new JLabel("");
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timeLabel.setBounds(10, 87, 484, 43);
		panel.add(timeLabel);
		new TimerListener(timeLabel);
		
		//nameLabel init
		JLabel nameLabel = new JLabel("\u540D\u7A31:");
		nameLabel.setFont(new Font("Dialog", Font.PLAIN, 32));
		nameLabel.setBounds(15, 215, 90, 50);
		contentPane.add(nameLabel);
		
		//rankLabel init
		JLabel rankLabel = new JLabel("\u8077\u4F4D:");
		rankLabel.setFont(new Font("Dialog", Font.PLAIN, 32));
		rankLabel.setBounds(15, 275, 90, 50);
		contentPane.add(rankLabel);
		
		//activeLabel init
		JLabel activeLabel = new JLabel("\u6D3B\u8E8D:");
		activeLabel.setFont(new Font("Dialog", Font.PLAIN, 32));
		activeLabel.setBounds(15, 338, 90, 50);
		contentPane.add(activeLabel);
		
		//handler init
		handler.setFont(new Font("Dialog", Font.PLAIN, 32));
		handler.setBounds(15, 398, 395, 50);
		contentPane.add(handler);
		
		//nameInput init
		nameInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		nameInput.setBounds(110, 215, 357, 54);
		contentPane.add(nameInput);
		
		//rankInput init
		rankInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		rankInput.setBounds(110, 275, 357, 54);
		contentPane.add(rankInput);
		
		//activeInput init
		activeInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		activeInput.setBounds(110, 338, 357, 54);
		contentPane.add(activeInput);
		
		//error init
		error.setFont(new Font("Dialog", Font.PLAIN, 20));
		error.setBounds(511, 155, 234, 54);
		contentPane.add(error);
		
		//Update data Btm init
		Button update = new Button("\u78BA\u8A8D\u7DE8\u8F2F");
		update.setBackground(new Color(0, 255, 204));
		update.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		update.setActionCommand("edit");
		update.setBounds(570, 411, 198, 54);
		//If get clicked Event, run the override method
		update.addActionListener(e -> {
			//Get the input field data
			try {
				String item = (String)idBox.getSelectedItem();
				String id = getID(item);
				String name1 = nameInput.getText();
				String active = activeInput.getText();
				String rank = rankInput.getText();

				//Detect data is empty or not
				if (!id.isEmpty() && !name1.isEmpty() && !active.isEmpty() && !rank.isEmpty()) {
					updateData(id, name1, active, rank);
				} else {
					error.setText("\u8cc7\u6599\u4e0d\u9f4a\u5168");
				}
			} catch (NameNotFoundException nne) {
				JOptionPane.showMessageDialog(null, "Please refresh the window");
				nne.printStackTrace();
			} catch (UploadFailedException ufe) {
				error.setText("\u7121\u6cd5\u7de8\u8f2f");
				ufe.printStackTrace();
			}
		});
		contentPane.add(update);
		
		//back init
		JLabel back = new JLabel("\u56DE\u4E0A\u4E00\u9801");
		back.setHorizontalAlignment(SwingConstants.TRAILING);
		back.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 25));
		back.setBounds(482, 363, 286, 40);
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
		idBox.setBounds(110, 155, 357, 54);
		idBox.addActionListener(e -> {
			try {
				String name12 = (String)idBox.getSelectedItem();
				String id = getID(name12);
				getData(id);
			} catch (NameNotFoundException nne) {
				JOptionPane.showMessageDialog(null, "Please refresh the window");
				nne.printStackTrace();
			}
		});
		contentPane.add(idBox);
	}

	
}
