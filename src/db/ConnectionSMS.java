package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;

public final class ConnectionSMS {
	private static ConnectionSMS instance = null;
	private static ConfigReader config = ConfigReader.getInstance();
	private String cnnString = "";
	private String userName = "";
	private String password = "";
	private Connection sms = null;
	private Statement stmt = null;
//init cnn
	public static ConnectionSMS getInstance() {
		if (instance == null) {
			try {
				instance = new ConnectionSMS();
			} catch (Exception e) {
				instance = null;
				MyUtil.errorToLog("db.ConnectionSMS", e);
				DialogBoxs.viewError(e);
			}
		}
		return instance;
	}
	public static ConnectionSMS getInstanceSilent() {
		if (instance == null) {
			try {
				instance = new ConnectionSMS();
			} catch (Exception e) {
				instance = null;
				MyUtil.errorToLog("db.ConnectionSMS", e);
			}
		}
		return instance;
	}
	private void Init() {
		cnnString = "jdbc:mysql://" + config.SERVER_ADDRESS_SMS + ":" + config.SERVER_PORT_SMS + "/users" + "?useCompression=true&autoReconnect=true&noAccessToProcedureBodies=true";
		userName = "kimcom";
		password = "sasasa21";
	}
	public boolean statusClosed() {
		try {
			return sms.isClosed();
		} catch (SQLException ex) {
			//Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return true;
		}
	}
	public boolean statusValid() {
		try {
			return sms.isValid(config.TIME_WAIT - 2);
		} catch (SQLException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return false;
		}
	}
	public boolean statusValid(int timeout) {
		try {
			return sms.isValid(timeout);
		} catch (SQLException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return false;
		}
	}
	public boolean startConnect() {
		boolean status = false;
		try {
			Init();
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			throw new IllegalArgumentException("Error load driver.");
		}
		try {
//System.out.println(MyUtil.getCurrentDateTime(1) + " " + cnnString1);
			sms = DriverManager.getConnection(cnnString, userName, password);
			status = true;
		} catch (SQLException err) {
			MyUtil.errorToLog(this.getClass().getName(), err);
		}
		return status;
	}
	protected ConnectionSMS() {
//		java.util.Date dt = new java.util.Date();
//		System.out.println("0. поднимаем соединение: " + dt.toString());
		try {
			Init();
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			throw new IllegalArgumentException("Error load driver.");
		}
		try {
//System.out.println(MyUtil.getCurrentDateTime(1) + " " + cnnString1);
			sms = DriverManager.getConnection(cnnString, userName, password);
		} catch (SQLException err) {
			MyUtil.errorToLog(this.getClass().getName(), cnnString + "\n" + err);
			//throw new IllegalArgumentException("Error: " + err);
		}
	}
	public void close() {
		if (sms != null) {
			try {
				sms.close();
			} catch (SQLException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
			}
		}
	}
	public void destroy() {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				//throw new IllegalArgumentException("Error: " + ex);
				MyUtil.errorToLog(this.getClass().getName(), ex);
				stmt = null;
				return;
			}
			stmt = null;
		}
		if (sms != null) {
			try {
				sms.close();
			} catch (SQLException ex) {
				//throw new IllegalArgumentException("Error: " + ex);
				MyUtil.errorToLog(this.getClass().getName(), ex);
				sms = null;
				return;
			}
			sms = null;
		}
		instance = null;
	}
	
	public boolean sendSMS(String strPhone, String strMessage) {
		if (sms == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("sendSMS: parameter [sms] cannot be null!"));
			return false;
		}
		String sql = "insert ignore into kimcom (number, `sign`, message, wappush, is_flash, send_time) \n"
				+ "	select '" + strPhone + "','MasterZoo', '" + strMessage + "',0,0,now();";
		
		MyUtil.messageToLog(this.getClass().getName(), sql);
		try {
			//System.out.println(sql);
			Statement st = sms.createStatement();
			st.executeUpdate(sql);
			return true;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), sql + "\n\n" + e);
			DialogBoxs.viewError(e);
			return false;
		}
	}
}
