package com.yue.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Server Thread
 */
public class ServerThead extends Servers implements Runnable{

	private static final long serialVersionUID = 1L;
	
	Socket socket;
    String socketName;
    String name;
    
    /**
     * System version
     */
    public static final String VERSION = "v2-34293";
    
    /**
     * Constructor 
     * @param socket Socket
     */
    public ServerThead(Socket socket){
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketName = socket.getRemoteSocketAddress().toString();
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(VERSION);
            out.flush();
            refreshIP();
            String line = reader.readLine();
            if(line.endsWith(" ~[version]")) {
            	System.out.println(line);
            	String[] versionIn = line.split("~");
            	System.out.println(versionIn[0]);
            	if(!VERSION.equals(versionIn[0].strip())) {
            		out.println("shutdown");
            		out.flush();
            		sockets.remove(socket);
            		socket.close();
            		refreshIP();
            	} else {
            		out.println("ok");
            		out.flush();
            	}
            } else {
            	sockets.remove(socket);
        		socket.close();
        		refreshIP();
            }
            logArea.append("IP:" + socketName + "\t\u5df2\u958b\u555f\u7ba1\u7406\u7cfb\u7d71\n");
            boolean flag = true;
            while (flag){
                //Connecting
                line = reader.readLine();
                //If client exit, disconnect
                if (line == null){
                    flag = false;
                    continue;
                }
                
                if(line.endsWith(" ~[console]")) {
                	System.out.println(line);
                	String[] msg = line.split("~");
                	System.out.println(msg[0]);
                	logArea.append(msg[0] + "\n");
                } else {
                	System.out.println("IP:" + socketName + "\t" + line + "\n");
                    console.append("IP:" + socketName + "\t" + line + "\n");
                    //Output the message
                    print(line);
                }
            }
            
            closeConnect();
            refreshIP();
        } catch (IOException e) {
            try {
                closeConnect();
                refreshIP();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    /**
     * Message to all socket
     * @param msg
     * @throws IOException
     */
    private void print(String msg) throws IOException {
        PrintWriter out = null;
        synchronized (sockets){
        	for (Socket sc : sockets){
        		out = new PrintWriter(sc.getOutputStream());
        		out.println(msg);
        		out.flush();
        	}
        }
    }
    /**
     * Close the socket connect
     * @throws IOException
     */
    public void closeConnect() throws IOException {
    	logArea.append("IP:" + socketName + "\t\u5df2\u95dc\u9589\u7ba1\u7406\u7cfb\u7d71\n");
        //Remove sockets
        synchronized (sockets){
            sockets.remove(socket);
        }
        
        socket.close();
    }
    
    /**
     * Refresh ip area
     */
    public void refreshIP() {
    	ip.setText("");
    	synchronized (sockets){
    		for(Socket sc : sockets) {
    			ip.append("IP:" + sc.getRemoteSocketAddress().toString() + "\n");
    		}
    	}
    }
}
