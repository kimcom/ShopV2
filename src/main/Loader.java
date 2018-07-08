/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

//import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingWorker;

public class Loader {

	private static int PORT = 0;
	public static final int PORTmain = 9999;
	public static final int PORTadmin = 9998;
	private static ServerSocket serverSocket = null;  // Server
	private static Socket socket = null;  // CLient
	public static final String focusProgram = "FOCUS";
	public static final String exitProgram = "EXIT";

	public static void checkFrmStart() {
		if (ShopMain.startAdmin) {
			PORT = PORTadmin;
		}else{
			PORT = PORTmain;
		}
		if (!isProgramRunning()) {
		} else {
			System.out.println("Program "+(ShopMain.startAdmin?"ShopV2-Main":"ShopV2-Admin")+" is running!");
			System.exit(0);
		}
	}
	public static boolean checkFrmAdmin() {
		return clientSocketListener(PORTadmin,focusProgram);
	}
	public static boolean checkFrmMain() {
		return clientSocketListener(PORTmain,focusProgram);
	}

	private static boolean isProgramRunning() {
		//System.out.println("isProgramRunning");
		try {
			serverSocket = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[]{127, 0, 0, 1}));  // Bind to localhost adapter with a zero connection queue. 
			SwingWorker<String, Void> anotherThread = new SwingWorker<String, Void>() {  // Do some code in another normal thread.
				@Override
				public String doInBackground() {  // This method is to execute a long code in the other thread in background.
					serverSocketListener();
					return "";
				}
			};
			anotherThread.execute();  // Execute the other tread.
		} catch (BindException e) {
			//System.err.println("Already running.");
			MyUtil.errorToLog("Loader","isProgramRunning - Already running");
			clientSocketListener(PORT,focusProgram);
			return true;
		} catch (IOException e) {
			//System.err.println("Unexpected error.");
			MyUtil.errorToLog("Loader", e);
			return true;
		}

		return false;
	}

	public static void serverSocketListener() {  
		//Server socket
		//System.out.println("serverSocketListener");
//		try {
////			MyUtil.messageToLog("Loader", "server Listener:"+socket.getInputStream().toString());
//		} catch (IOException ex) {
//			//MyUtil.errorToLog("Loader", ex);
//			//return;
//		}
		try {
			while (true) {
				//System.out.println("Listener socket!");
				socket = serverSocket.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String input;
				//MyUtil.messageToLog("serverSocketListener", "status read ok!");
				while ((input = in.readLine()) != null) {
					//System.out.println(input);
					//MyUtil.messageToLog("Loader receive:", input);
					//MyUtil.messageToLog("Loader", input);
					if (input.equalsIgnoreCase(exitProgram)) {
						//break;
						System.exit(0);
					}
					if (input.equals(focusProgram)){
						//JOptionPane.showMessageDialog(new JFrame(), "TEST!\n|" + input + "|\n", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
						if (ShopMain.startAdmin) {
							ShopMain.frmAdmin.setState(JFrame.ICONIFIED);
							ShopMain.frmAdmin.setState(JFrame.NORMAL);
							ShopMain.frmAdmin.toFront();
						}else{
							ShopMain.frmMain.setState(JFrame.ICONIFIED);
							ShopMain.frmMain.setState(JFrame.NORMAL);
							ShopMain.frmMain.toFront();
						}
//					}else{
//						MyUtil.messageToLog("Loader receive:", input);
					}
				}
				//System.out.println("no input line");
			}
		} catch (IOException e) {
			MyUtil.errorToLog("Loader", e);
			System.exit(-1);
		}
	}

	public static boolean clientSocketListener(int port, String command) {  // Client socket
		try {
			//MyUtil.messageToLog("Loader port:", port+"	command:"+command);
			socket = new Socket(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}), port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(command);
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	
	}
}