package com.yue.czcontrol.features;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.yue.czcontrol.MainFrame;
import com.yue.czcontrol.utils.TimeProperty;
import com.yue.czcontrol.utils.VersionProperty;

public class InfoFrame extends JFrame implements TimeProperty, VersionProperty, Runnable{

	private static final long serialVersionUID = 1L;

	MainFrame mf = new MainFrame();
	
	private JPanel contentPane;
	
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
			} catch (Exception e) {
				timeLabel.setText("Error!!!");
			}
		}

	}
	
	@Override
	public String getReleaseDate() {
		return RELEASE_DATE;
	}
	
	@Override
	public String getVersion() {
		int version = ((DATE_YEAR * DATE_MD) / (RELEASE_TIME * RELEASE_COUNT)) * (16 + RELEASE_AM_PM);
		return "v2-" + version;
	}
	
	/**
	 * Create a Frame
	 * @param userName The user Name
	 * @param user user Account
	 * @param password user Password
	 */
	public InfoFrame(String userName, String user, String password) {
		//JFrame init
		setTitle("Cz\u7ba1\u7406\u7cfb\u7d71-info");
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
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-info\r\n");
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
		
		//Back Label init
		JLabel back = new JLabel("\u9EDE\u6211\u56DE\u4E0A\u4E00\u9801");
		back.setForeground(Color.RED);
		back.setFont(new Font("\u65b0\u7d30\u660e\u9ad4", Font.PLAIN, 30));
		back.setBounds(581, 418, 192, 47);
		back.addMouseListener(new MouseAdapter() {//If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				
				mf.setUser(user);
				mf.setPassword(password);
				mf.init();
				Thread thread = new Thread(mf);
				thread.start();
				mf.setVisible(true);
			}
		});
		contentPane.add(back);
		
		//authorLabel init
		JLabel author = new JLabel("\u4F5C\u8005: \u6708Yue");
		author.setFont(new Font("Dialog", Font.PLAIN, 40));
		author.setBounds(15, 155, 277, 79);
		contentPane.add(author);
		
		//versionLabel init
		JLabel version = new JLabel("版本: " + getVersion());
		version.setFont(new Font("Dialog", Font.PLAIN, 40));
		version.setBounds(15, 244, 505, 79);
		contentPane.add(version);
		
		//dateLabel init
		JLabel date = new JLabel("釋出日期: " + getReleaseDate());
		date.setFont(new Font("Dialog", Font.PLAIN, 40));
		date.setBounds(15, 337, 393, 79);
		contentPane.add(date);
	}

}
