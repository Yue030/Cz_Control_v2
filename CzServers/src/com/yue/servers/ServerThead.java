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

    Socket socket;
    String socketName;
    
    public ServerThead(Socket socket){
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketName = socket.getRemoteSocketAddress().toString();
            refreshIP();
            console.append("IP:" + socketName + "\t\u5df2\u52a0\u5165\u804a\u5929\n");
            boolean flag = true;
            while (flag){
                //Connecting
                String line = reader.readLine();
                //If client exit, disconnect
                if (line == null){
                    flag = false;
                    continue;
                }
                System.out.println("IP:" + socketName + "\t" + line + "\n");
                console.append("IP:" + socketName + "\t" + line + "\n");
                //Output the message
                print(line);
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
    	console.append("IP:" + socketName + "\t\u5df2\u9000\u51fa\u804a\u5929\n");
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
