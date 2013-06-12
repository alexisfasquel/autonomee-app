package com.insa.carosif;

import android.util.Log;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
	 
	 
public class TCPClient implements Runnable{
	
	public static final int OFF = 0;
	public static final int CONNECTING = 1;
	public static final int CONNECTED = 2;
	public static final int FAILED = 3;
	public static final int LOST = 4;
	
    public  String SERVERIP = "192.168.1.1"; //your computer IP address
    public static int SERVERPORT = 8080;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
    
    
    PrintWriter out;
    BufferedReader in;
 
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener, String ip, int port) {
        mMessageListener = listener;
        SERVERPORT = port;
        SERVERIP = ip;
    }

    public void setOnMessageReceived(OnMessageReceived listener) {
    	mMessageListener = listener;
    }
    
	/**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError() && !message.equals("")) {
            out.println(message);
        }
    }
 
    public void stopClient(){
        mRun = false;
    }
    
    @Override
    public void run() {
 
        mRun = true;
        mMessageListener.changeState(CONNECTING);
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);
 
            
 
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
 
            try {

            	
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
 
                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
                mMessageListener.changeState(CONNECTED);
                
                //in this while the client listens for the messages sent by the server
                while (mRun) {
 
                    if (mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(in.readLine());
                    }
 
                }
 
            } catch (Exception e) {
            	Log.e("TCPClient", e.getMessage());
            	mMessageListener.changeState(LOST);
 
            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }
 
        } catch (Exception e) {
    		Log.e("TCPClient", e.getMessage());
        	mMessageListener.changeState(FAILED);
 
        }
 
    }
 
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
        public void changeState(int state);
    }
}
