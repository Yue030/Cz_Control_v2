package com.yue.servers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Servers extends JFrame {
	public Servers() {
		
	}
	
	private static final long serialVersionUID = 1L;
	
	protected static JTextArea ip = new JTextArea();
	protected JScrollPane ipScroll = new JScrollPane();
	
	protected static JTextArea console = new JTextArea();
	protected JScrollPane consoleScroll = new JScrollPane();
	
	protected JLabel title = new JLabel("\u804A\u5929\u5BA4");
	protected JLabel ipTitle = new JLabel("\u4F7F\u7528\u8005IP");
	protected JButton sudoExit = new JButton("\u5F37\u5236\u65B7\u7DDA");
	
	protected JScrollPane logScroll = new JScrollPane();
	protected static JTextArea logArea = new JTextArea();
	protected JLabel log = new JLabel("\u65E5\u8A8C");
	
	protected static final List<Socket> sockets = new Vector<>();

	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(() -> {
			try {
				Servers frame = new Servers();
				frame.initServersUI();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		ServerSocket server = new ServerSocket(5200);
        boolean flag = true;
        while (flag){
            try {
            	Socket accept = server.accept();
            
            	synchronized (sockets){
            		sockets.add(accept);
            	}
            
            	Thread thread = new Thread(new ServerThead(accept));
            	thread.start();
            } catch (Exception e){
            	flag = false;
           		e.printStackTrace();
            }
        }
        server.close();
    }

	public void initServersUI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 809, 530);
		setTitle("CzServer-v46721");
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		console.setFont(new Font("Dialog", Font.PLAIN, 12));
		console.setEditable(false);
		consoleScroll.setViewportView(console);
		
		consoleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScroll.setBounds(10, 47, 369, 261);
		contentPane.add(consoleScroll);
		
		ip.setFont(new Font("Dialog", Font.PLAIN, 15));
		ip.setEditable(false);
		ipScroll.setViewportView(ip);
		
		ipScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ipScroll.setBounds(10, 372, 369, 111);
		contentPane.add(ipScroll);
		
		
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Dialog", Font.PLAIN, 35));
		title.setBounds(10, 0, 369, 37);
		contentPane.add(title);
		
		ipTitle.setHorizontalAlignment(SwingConstants.CENTER);
		ipTitle.setFont(new Font("Dialog", Font.PLAIN, 30));
		ipTitle.setBounds(10, 318, 369, 44);
		contentPane.add(ipTitle);
		
		sudoExit.setFont(new Font("Dialog", Font.PLAIN, 25));
		sudoExit.setBounds(541, 372, 191, 62);
		sudoExit.addActionListener(e -> {
			synchronized(sockets){
				System.out.println(sockets.size());
				for(Socket sc : sockets) {
					try {
						sc.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				sockets.clear();
			}
		});
		contentPane.add(sudoExit);
		
		
		logScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		logScroll.setBounds(426, 47, 369, 261);
		contentPane.add(logScroll);
		
		logArea.setFont(new Font("Dialog", Font.PLAIN, 12));
		logArea.setEditable(false);
		logScroll.setViewportView(logArea);
		
		log.setHorizontalAlignment(SwingConstants.CENTER);
		log.setFont(new Font("Dialog", Font.PLAIN, 35));
		log.setBounds(426, 0, 369, 37);
		contentPane.add(log);
	}
}
