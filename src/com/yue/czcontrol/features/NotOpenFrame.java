package com.yue.czcontrol.features;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.MainFrame;

public class NotOpenFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	/**
	 * Create a Frame
	 * @param user user Account
	 * @param password user Passoword
	 */
	public NotOpenFrame(String user, String password) {
		//JFrame init
		setTitle("\u5C1A\u672A\u958B\u555F");
		setResizable(false);		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);
		
		//errorImg init
		JLabel errorImg = new JLabel("");
		errorImg.setIcon(new ImageIcon(NotOpenFrame.class.getResource("/com/yue/czcontrol/resource/error.png")));
		errorImg.setBounds(21, 168, 241, 297);
		contentPane.add(errorImg);
		
		//error init
		JLabel error = new JLabel("<html>\u5C1A\u672A\u958B\u653E\u6B64\u6700\u65B0\u529F\u80FD\u8ACB\u9EDE\u6211\u56DE\u4E0A\u4E00\u9801</html>");
		error.setHorizontalAlignment(SwingConstants.CENTER);
		error.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 50));
		error.setBounds(272, 205, 456, 225);
		error.addMouseListener(new MouseAdapter() {//If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				MainFrame mf = new MainFrame();
				mf.setUser(user);
				mf.setPassword(password);
				mf.init();
				Thread thread = new Thread(mf);
				thread.start();
				mf.setVisible(true);
			}
		});
		contentPane.add(error);
	}

}
