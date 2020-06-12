package com.yue.servers;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

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
	
	protected static List<Socket> sockets = new Vector<>();

	private JPanel contentPane;

	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Servers frame = new Servers();
					frame.initServersUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		setTitle("\u5ba2\u6236\u7aef\u76e3\u63a7\u7cfb\u7d71");
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		console.setFont(new Font("Dialog", Font.PLAIN, 15));
		console.setEditable(false);
		consoleScroll.setViewportView(console);
		
		consoleScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		consoleScroll.setBounds(10, 47, 722, 261);
		contentPane.add(consoleScroll);
		
		ip.setFont(new Font("Dialog", Font.PLAIN, 15));
		ip.setEditable(false);
		ipScroll.setViewportView(ip);
		
		ipScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		ipScroll.setBounds(10, 372, 369, 111);
		contentPane.add(ipScroll);
		
		
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Dialog", Font.PLAIN, 35));
		title.setBounds(10, 0, 722, 37);
		contentPane.add(title);
		
		ipTitle.setHorizontalAlignment(SwingConstants.CENTER);
		ipTitle.setFont(new Font("Dialog", Font.PLAIN, 30));
		ipTitle.setBounds(10, 318, 369, 44);
		contentPane.add(ipTitle);
		
		sudoExit.setFont(new Font("Dialog", Font.PLAIN, 25));
		sudoExit.setBounds(541, 372, 191, 62);
		sudoExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
			}
		});
		contentPane.add(sudoExit);
	}
}
