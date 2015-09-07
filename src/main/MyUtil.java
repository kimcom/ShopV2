package main;

import db.ConnectionDb;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MyUtil {
	public static String getCurrentDateTime(){
		Date curdate = new Date();
		SimpleDateFormat formatDT = new SimpleDateFormat("Дата: dd.MM.yyyy Время: hh:mm");
		return formatDT.format(curdate);
	}
	public static String getCurrentDate(){
		Date curdate = new Date();
		SimpleDateFormat formatDT = new SimpleDateFormat("Дата: dd.MM.yyyy");
		return formatDT.format(curdate);
	}
	public static String getClientID(){
		ConnectionDb cnn = ConnectionDb.getInstance();
		return Integer.toString(cnn.clientID);
	}
	public static String getCurrentCheckID() {
		ConnectionDb cnn = ConnectionDb.getInstance();
		String str = "";
		if(cnn.currentCheckID != null)
			str = cnn.currentCheckID.setScale(2, RoundingMode.HALF_UP).toPlainString();
		return str;
	}
	public static class MyFormatedTextFocusListener implements FocusListener {
		@Override
		public void focusGained(final FocusEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JFormattedTextField jft = (JFormattedTextField) e.getSource();
					jft.selectAll();
				}
			});
		}
		@Override
		public void focusLost(FocusEvent e) {
		}
	}
	public static class MyTextFocusListener implements FocusListener {

		@Override
		public void focusGained(final FocusEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JTextField jt = (JTextField) e.getSource();
					jt.selectAll();
				}
			});
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	}
	public static void errorToLog(String className, Exception err) {
		File dir = new File("Logs");
		dir.mkdir();
		Logger logger = Logger.getLogger(className);
		FileHandler fh;
		try {
			// This block configure the logger with handler and formatter  
			//fh = new FileHandler("shopV2.log");
			fh = new FileHandler(dir.getName()+"/"+className+".log",true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			// the following statement is used to log any messages  
			//logger.info(err.getMessage());
			logger.log(Level.SEVERE, "Exception:",err);
			fh.close();
		} catch (SecurityException | IOException e) {
			MyUtil.errorToLog(MyUtil.class.getName(), e);
		}
	}
	public static void errorToLog(String className, String error_info) {
		File dir = new File("Logs");
		dir.mkdir();
		Logger logger = Logger.getLogger(className);
		FileHandler fh;
		try {
			// This block configure the logger with handler and formatter  
			//fh = new FileHandler("shopV2.log");
			fh = new FileHandler(dir.getName()+"/"+className+".log",true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			// the following statement is used to log any messages  
			//logger.info(error_info);
			logger.log(Level.SEVERE, "Exception:"+error_info);
			fh.close();
		} catch (SecurityException | IOException e) {
			MyUtil.errorToLog(MyUtil.class.getName(), e);
		}
	}
	public static void messageToLog(String className, String error_info) {
		File dir = new File("Logs");
		dir.mkdir();
		Logger logger = Logger.getLogger(className);
		FileHandler fh;
		try {
			// This block configure the logger with handler and formatter  
			//fh = new FileHandler("shopV2.log");
			fh = new FileHandler(dir.getName() + "/" + className + ".log", true);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			// the following statement is used to log any messages  
			logger.info(error_info);
			//logger.log(Level.SEVERE, "Exception:" + error_info);
			fh.close();
		} catch (SecurityException | IOException e) {
			MyUtil.errorToLog(MyUtil.class.getName(), e);
		}
	}
	public static void replaceInFile(String fileName, String str_search, String str_new){
		//замена подстроки в текстовом файле
		File file = new File(fileName);
		if (file.exists() && file.isFile()) {
			InputStream fis = null;
			OutputStream fos = null;
			try {
				fis = new BufferedInputStream(new FileInputStream(file));
				byte[] str_in = new byte[fis.available()];
				fis.read(str_in);
				fis.close();
				String str_out = new String(str_in);
				if (str_out.indexOf(str_search) != 0) {
					String buffer = str_out.replaceAll(str_search, str_new);
					fos = new BufferedOutputStream(new FileOutputStream(file));
					fos.write(buffer.getBytes());
					if (fos != null) {
						fos.close();
					}
				}
			} catch (FileNotFoundException ex) {
				MyUtil.errorToLog(MyUtil.class.getName(), ex);
			} catch (IOException ex) {
				MyUtil.errorToLog(MyUtil.class.getName(), ex);
			}
		}
	}
}
