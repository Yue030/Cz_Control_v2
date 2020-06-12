package com.yue.czcontrol;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
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

public class RegisterFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	
	private Connection conn = null;
	private ResultSet rs = null;
	
	private JLabel error = new JLabel("");
	private JFormattedTextField accInput = new JFormattedTextField();
	private JFormattedTextField pasInput = new JFormattedTextField();
	private JFormattedTextField nameInput = new JFormattedTextField();
	private JFormattedTextField conPasInput = new JFormattedTextField();
	
	private boolean isOK;

	/**
	 * Detect account is exist or not
	 * @param account Input account
	 * @return boolean
	 */
	private boolean isExistAccount(String account) {
		try {
			//Get Data(SQL Syntax)
			String accountList = "SELECT * FROM admin WHERE ACCOUNT=?";
			
			//String accountList to PreparedStatement
			PreparedStatement psmt = conn.prepareStatement(accountList);
			psmt.setString(1, account);
			
			rs = psmt.executeQuery();

			//Detect the data is exist or not
			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			this.error.setText("\u8cc7\u6599\u5eab\u4e0d\u5b58\u5728");
		}
		return false;
	}
	
	
	/**
	 * Register Account
	 * @param name Input name
	 * @param account Input account
	 * @param password Input password
	 * @param confirmPassword Input confirmPassword
	 * @throws UploadFailedException When the data upload failed
	 */
	
	private void register(String name, String account, String password, String confirmPassword) throws UploadFailedException {
		boolean accountExist = isExistAccount(account);

		if (accountExist) {
			this.error.setText("\u5e33\u865f\u5df2\u5b58\u5728");
		} else {
			//Detect the password and confirmPassword is equals or not
			if (password.equals(confirmPassword)) {
				try {
					//Add data(SQL Syntax)
					String insertAccount = "INSERT INTO `admin`(`NAME`, `ACCOUNT`, `PASSWORD`) VALUES (?,?,?)";
					
					//String insertAccount to PreparedStatement
					PreparedStatement psmt = conn.prepareStatement(insertAccount);
					psmt.setString(1, name);
					psmt.setString(2, account);
					psmt.setString(3, password);
					
					//Detect the data is upload success of not
					if (psmt.executeUpdate() > 0) {
						isOK = true;
					} else {
						throw new UploadFailedException("Data is Upload failed.");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else {
				this.error.setText("\u5bc6\u78bc\u5fc5\u9808\u76f8\u540c");
			}
		}
	}
	
	/**
	 * Create a Frame
	 */
	public RegisterFrame() {
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn, error);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 683, 503);
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-\u8a3b\u518a");
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Panel init
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setBounds(0, 0, 315, 476);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//imageLabel init
		JLabel imageLabel = new JLabel("");
		imageLabel.setBounds(-45, 5, 405, 192);
		imageLabel.setIcon(new ImageIcon(LoginFrame.class.getResource("/com/yue/czcontrol/resource/kaguya.jpg")));
		panel.add(imageLabel);
		
		//Title init
		JLabel title = new JLabel("Cz\u7BA1\u7406\u7CFB\u7D71\u8A3B\u518A");
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 35));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		title.setBounds(20, 232, 285, 52);
		panel.add(title);
		
		//info init
		JLabel infoLabel = new JLabel("<html>\u8ACB\u8F38\u5165\u4E4B\u5F8C\u767B\u5165\u7684\u4F7F\u7528\u8005\u66B1\u7A31\uFF0C\u5E33\u865F\u53CA\u5BC6\u78BC\uFF0C\u4EE5\u53CA\u518D\u8F38\u5165\u4E00\u6B21\u5BC6\u78BC\uFF0C\u4EE5\u78BA\u4FDD\u6C92\u6709\u8F38\u5165\u932F\u8AA4</html>");
		infoLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setBounds(10, 294, 295, 172);
		panel.add(infoLabel);
		
		//error init
		error.setForeground(Color.WHITE);
		error.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 13));
		error.setBounds(10, 431, 295, 35);
		panel.add(error);
		
		//RegisterBtm init
		Button registerBtm = new Button("\u8A3B\u518A");
		registerBtm.addActionListener(new ActionListener() {//�������辣嚗銵ethod
			public void actionPerformed(ActionEvent e) {
				isOK = false;
				//Get the input data
				String name = new String(nameInput.getText());
				String account = new String(accInput.getText());
				String password = new String(pasInput.getText());
				String confirmPassword = new String(conPasInput.getText());

				//Set String regex format
				final String regex = "[A-Za-z0-9]\\w{3,10}";
				if (!name.isEmpty() && !account.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {//Detect the input data is empty. if not, continue the method
					if (account.matches(regex) && password.matches(regex) && confirmPassword.matches(regex)) {//Detect the input data is meets the format
						try {
							register(name, account, password, confirmPassword);
						} catch (UploadFailedException ufe) {
							error.setText("\u8a3b\u518a\u5931\u6557");
							ufe.printStackTrace();
						}//Register
					} else {
						error.setText("\u683c\u5f0f\u4e0d\u7b26\u5408(\u6700\u5c114,\u6700\u591a10\u5b57)");
					}
				} else {
					error.setText("\u8cc7\u6599\u4e0d\u5b8c\u6574");
				}

				if (isOK) {
					JOptionPane.showMessageDialog(null, "\u8a3b\u518a\u5e33\u865f\u6210\u529f");
					setVisible(false);
					new LoginFrame().setVisible(true);;
				}
			}
		});
		registerBtm.setFont(new Font("Dialog", Font.PLAIN, 25));
		registerBtm.setForeground(Color.WHITE);
		registerBtm.setBackground(new Color(241, 57, 83));
		registerBtm.setActionCommand("login");
		registerBtm.setBounds(395, 395, 195, 49);
		contentPane.add(registerBtm);
		
		//accInput init
		accInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		accInput.setBounds(364, 152, 263, 40);
		contentPane.add(accInput);
		
		//pasInput init
		pasInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		pasInput.setBounds(364, 244, 263, 40);
		contentPane.add(pasInput);
		
		//accLabel init
		JLabel accLabel = new JLabel("\u5E33\u865F:");
		accLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		accLabel.setBounds(364, 102, 93, 40);
		contentPane.add(accLabel);
		
		//pasLabel init
		JLabel pasLabel = new JLabel("\u5BC6\u78BC:");
		pasLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		pasLabel.setBounds(364, 202, 77, 32);
		contentPane.add(pasLabel);
		
		//nameLabel init
		JLabel nameLabel = new JLabel("\u66B1\u7A31:");
		nameLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		nameLabel.setBounds(364, 10, 77, 32);
		contentPane.add(nameLabel);
		
		nameInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		nameInput.setBounds(364, 52, 263, 40);
		contentPane.add(nameInput);
		
		//conPasLabel init
		JLabel conPasLabel = new JLabel("\u78BA\u8A8D\u5BC6\u78BC:");
		conPasLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 30));
		conPasLabel.setBounds(364, 294, 137, 32);
		contentPane.add(conPasLabel);
		conPasInput.setFont(new Font("Dialog", Font.PLAIN, 25));
		
		conPasInput.setBounds(364, 336, 263, 40);
		contentPane.add(conPasInput);
		
		//loginLabel init
		JLabel loginLabel = new JLabel("\u82E5\u5DF2\u6709\u5E33\u865F\uFF0C\u9EDE\u6211\u524D\u5F80\u767B\u5165");
		loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		loginLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		loginLabel.setBounds(364, 439, 263, 26);
		loginLabel.addMouseListener(new MouseAdapter() {//If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				new LoginFrame().setVisible(true);
			}
		});
		contentPane.add(loginLabel);
	}

}
