package db;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import main.ConfigReader;
import main.DialogBoxs;

import java.sql.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.MyUtil;

public final class ConnectionDb{
    private static ConnectionDb instance    = null;
    private String              cnnString   = "";
    private String              userName    = "";
    private String              password    = "";
    private Connection          cnn         = null;
    private Statement           stmt        = null;
    public int                  accessLevel = 999;
    public BigDecimal           currentCheckID;
    private int                 userID;
    public int					clientID;
    public int					matrixID;
	public int					checkFlagReturn;
    public int                  checkStatus;
    public int                  checkTypePayment;
    public BigDecimal           checkSumBase;
    public BigDecimal           checkSumDiscount;
    public BigDecimal           checkSum;
    //public sql.Date				checkDT_close;
    private ResultSet           resClientInfo;
    private ResultSet           resCardInfo;
    private ResultSet           resPromoInfo;
    private ResultSet           resOrderInfo;
    private static ConfigReader config = ConfigReader.getInstance();
    
//init cnn
    public static ConnectionDb getInstance() {
        if (instance == null) {
            try {
                instance = new ConnectionDb();
            } catch (Exception e) {
                instance = null;
				MyUtil.errorToLog("db.ConnectionDb", e);
                DialogBoxs.viewError(e);
            }
        }
        return instance;
    }
    public static ConnectionDb getInstanceSilent() {
        if (instance == null) {
            try {
                instance = new ConnectionDb();
            } catch (Exception e) {
                instance = null;
				MyUtil.errorToLog("db.ConnectionDb", e);
            }
        }
        return instance;
    }
    private void Init(){
        //ConfigReader config = ConfigReader.getInstance();
		//&amp;AutoCommit=true&amp;autoReconnect=true
        cnnString   = "jdbc:mysql://" + config.SERVER_ADDRESS + ":" + config.SERVER_PORT + "/" + config.SERVER_DB + "?useCompression=true&autoReconnect=true";
        //cnnString   = "jdbc:mariadb://" + config.SERVER_ADDRESS + ":" + config.SERVER_PORT + "/" + config.SERVER_DB + "?useCompression=true&allowMultiQueries=true";
        userName    = config.SERVER_DB;
        password    = "149521";
    }
	public boolean statusClosed(){
		try {
			return cnn.isClosed();
		} catch (SQLException ex) {
			//Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return true;
		}
	}
	public boolean statusValid() {
		try {
			return cnn.isValid(config.TIME_WAIT-2);
		} catch (SQLException ex) {
			//Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return false;
		}
	}
    public boolean startConnect() {
		boolean status = false;
//		java.util.Date dt = new java.util.Date();
//		System.out.println("1. поднимаем соединение: " + dt.toString());
		try {
			Init();
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			throw new IllegalArgumentException("Error load driver.");
		}
		try {
			cnn = DriverManager.getConnection(cnnString, userName, password);
			status = true;
		} catch (SQLException err) {
			MyUtil.errorToLog(this.getClass().getName(), err);
			//throw new IllegalArgumentException("Error: " + err);
		}
		return status;
	}
    protected ConnectionDb() {
//		java.util.Date dt = new java.util.Date();
//		System.out.println("0. поднимаем соединение: " + dt.toString());
		try {
			Init();
			Class.forName("com.mysql.jdbc.Driver");
			//Class.forName("org.mariadb.jdbc.Driver");
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			throw new IllegalArgumentException("Error load driver.");
		}
		try {
			cnn = DriverManager.getConnection(cnnString, userName, password);
		} catch (SQLException err) {
			//Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, err);
			MyUtil.errorToLog(this.getClass().getName(), err);
			throw new IllegalArgumentException("Error: " + err);
		}
    }
	public void close() {
		if (cnn != null) {
			try {
				cnn.close();
			} catch (SQLException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
			}
		}
	}
	public void destroy(){
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
        if (cnn != null) {
            try {
                cnn.close();
            } catch (SQLException ex) {
                //throw new IllegalArgumentException("Error: " + ex);
				MyUtil.errorToLog(this.getClass().getName(), ex);
				cnn = null;
				return;
            }
            cnn = null;
        }
        instance    = null;
    }
//test compression
	public void getListCards() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getListCards: parameter [cnn] cannot be null!"));
			return;
		}
		String sql = "SELECT * FROM cards";
		PreparedStatement pst = null;
		try {
			pst = cnn.prepareStatement(sql);
			long start = java.lang.System.currentTimeMillis();
			ResultSet res = pst.executeQuery();
			long stop = java.lang.System.currentTimeMillis();
			DialogBoxs.viewMessage("JDBC Time: " + String.valueOf((stop - start)));
//			while (res.next()) {
//				String row = res.getString("UserName") + ", " + res.getString("Position") + ", " + res.getRow();
//				//System.out.println(row);
//			}
			res.close();
			pst.close();
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
		}
	}

//users
    public boolean login(String userName, String userPassword) {
        if ( cnn == null ) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("login: parameter [cnn] cannot be null!"));
			return false;
		}
        if ( userName.equals("") || userPassword.equals("")) {
            DialogBoxs.viewError(new Exception("Имя пользователя или пароль не может быть пустым!"));
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_Login(?,?,?,?,?,?)}");
            cs.setString(1,userName);
            cs.setString(2, userPassword);
            cs.registerOutParameter(3, Types.INTEGER);
            cs.registerOutParameter(4, Types.INTEGER);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.execute();
            int res = cs.getInt(5);
            if(res==0){
                DialogBoxs.viewError(new Exception("Неверно введено:\nИмя пользователя\nили пароль!"));
                return false;
            }else{
                userID = cs.getInt(3);
                clientID = cs.getInt(4);
                accessLevel = cs.getInt(6);
                if (lastCheck().compareTo(BigDecimal.ZERO)==0){
                    DialogBoxs.viewMessage("Возникла ошибка при получении\nномера последнего чека!\n\nПродолжение работы невозможно!");
                    return false;
                }
                getClientInfo(clientID);
                return true;
            }
        } catch (SQLException ex) {
			MyUtil.errorToLog(this.getClass().getName(),ex);
            DialogBoxs.viewError(ex);
            return false;
        }
    }
    public void getUserProperties(int userId) {
        if ( cnn == null ) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getUserProperties: parameter [cnn] cannot be null!"));
			return;
		}
        if ( userId == 0 ) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getUserProperties: Could not user ID [0]!"));
			return;
		}
        String sql = "SELECT * FROM users WHERE UserID=?";
        PreparedStatement pst = null;
        try {
            pst = cnn.prepareStatement(sql);
            pst.setInt(1,userId);
            ResultSet res = pst.executeQuery();
            while (res.next()) {
                String row = res.getString("UserName") + ", " + res.getString("Position")+ ", " + res.getRow();
                //System.out.println(row);
            }
            res.close();
            pst.close();
        } catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
            DialogBoxs.viewError(e);
        }
    }
    public ResultSet getUserList() {
        if ( cnn == null ) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getUserList: parameter [cnn] cannot be null!"));
			return null;
		}
        String sql = "SELECT * FROM users";
        PreparedStatement pst = null;
        try {
            pst = cnn.prepareStatement(sql);
            ResultSet res = pst.executeQuery();
            //System.out.println("query OK");
            return res;
            //res.close();
            //pst.close();
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
	public void setAppVersion() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setAppVersion: parameter [cnn] cannot be null!"));
			return;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_user(?,?,?,?,?,?)}");
			cs.setString(1, "setAppVersion");
			cs.setInt(2, 0);
			cs.setString(3, config.APP_VERSION);
			cs.setString(4, "");
			cs.setInt(5, clientID);
			cs.setInt(6, 0);
			cs.execute();
			return;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return;
		}
	}
//client
    private boolean getClientInfo(int clientID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getClientInfo: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_client(?,?,?)}");
            cs.setString(1, "getClientInfoById");
            cs.setInt(2, clientID);
            cs.registerOutParameter(3, Types.INTEGER);
            resClientInfo = cs.executeQuery();
            return true;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public String getClientInfo(String fieldName) {
        if (resClientInfo == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getClientInfo: parameter [resClientInfo] cannot be null!"));
			return "";
        }
        try {
            String strResult = "";
            if(resClientInfo.absolute(1)){
                strResult = resClientInfo.getString(fieldName);
            }
            return strResult;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
//discount card
    public boolean getDiscountCardInfo(String barCode) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getDiscountCardInfo: parameter [cnn] cannot be null!"));
			return false;
        }
        if (barCode.equals("")) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_card(?,?,?)}");
            cs.setString(1, "info");
            cs.setString(2, barCode);
            cs.registerOutParameter(3, Types.INTEGER);
            resCardInfo = cs.executeQuery();
            //resCardInfo.last();
            resCardInfo.absolute(1);
            return resCardInfo.getRow() != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public String getDiscountCardInfo(String fieldName, String typeValue) {
        if (resCardInfo == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getDiscountCardInfo: parameter [resCardInfo] cannot be null!"));
			return "";
        }
        try {
            if (fieldName.equals("")) return "";
            String strResult = "";
            if (resCardInfo.absolute(1)) {
                if(typeValue.equals("String")) {
                    strResult = resCardInfo.getString(fieldName);
                    strResult = (strResult==null)?"":strResult;
                } else if (typeValue.equals("int")) {
                    strResult = Integer.toString(resCardInfo.getInt(fieldName));
                } else if (typeValue.equals("BigDecimal")) {
                    if (resCardInfo.getBigDecimal(fieldName) != null)
                        strResult = resCardInfo.getBigDecimal(fieldName).setScale(2,RoundingMode.HALF_UP).toPlainString();
                } else if (typeValue.equals("DateTime")) {
                    if (resCardInfo.getDate(fieldName) != null)
                        strResult = resCardInfo.getString(fieldName).toString();
                }
            }
            return strResult;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public BigDecimal getDiscountScalePercent(BigDecimal sum) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getDiscountScalePercent: parameter [cnn] cannot be null!"));
			return BigDecimal.ZERO;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_card(?,?,?)}");
            cs.setString(1, "scale_discount");
            cs.setString(2, "");
            cs.registerOutParameter(3, Types.INTEGER);
            ResultSet res = cs.executeQuery();
            BigDecimal bdDiscountPercent = new BigDecimal(BigInteger.ZERO);
            while (res.next()) {
                if(sum.compareTo(res.getBigDecimal("SumFrom"))>=0 && sum.compareTo(res.getBigDecimal("SumTo"))<0){
                    //System.out.println(res.getBigDecimal("Percent").setScale(2,RoundingMode.HALF_UP).toPlainString());
                    bdDiscountPercent = res.getBigDecimal("Percent");
                    break;
                }
            }
            return bdDiscountPercent;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public BigDecimal getDiscountScaleStart() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getDiscountScaleStart: parameter [cnn] cannot be null!"));
			return BigDecimal.ZERO;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_card(?,?,?)}");
            cs.setString(1, "scale_discount");
            cs.setString(2, "");
            cs.registerOutParameter(3, Types.INTEGER);
            ResultSet res = cs.executeQuery();
            res.first();
            return res.getBigDecimal("SumFrom");
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
	public ResultSet getDiscountScaleTable() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getDiscountScaleTable: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_card(?,?,?)}");
			cs.setString(1, "scale_discount");
			cs.setString(2, "");
			cs.registerOutParameter(3, Types.INTEGER);
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
    public boolean setDiscountCardAttribute(String action, String strCardID, String strName, 
                                            String strAddress, String strPhone, 
                                            String strEmail, String strAnimal, String strNotes,
                                            String dtDateOfIssue, BigDecimal bdPercentOfDiscount, 
                                            BigDecimal bdAmountOfBuying, String dtDateOfCancellation,
                                            String strHowWeLearn) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setDiscountCardAttribute: parameter [cnn] cannot be null!"));
			return false;
        }
        if (strCardID.equals("")) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_card_attribute(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            if(action.equals("card_attr_new")){
                cs.setString(1, "card_attr_new");
                cs.setBigDecimal(10, bdPercentOfDiscount);
                cs.setBigDecimal(11, bdAmountOfBuying);
            } else if(action.equals("card_attr_edit")) {
                cs.setString(1, "card_attr_edit");
                cs.setBigDecimal(10, bdPercentOfDiscount);
                cs.setBigDecimal(11, bdAmountOfBuying);
            } else return false;
            cs.setString(2, strCardID);
            cs.setString(3, strName);
            cs.setString(4, strAddress);
            cs.setString(5, strPhone);
            cs.setString(6, strEmail);
            cs.setString(7, strAnimal);
            cs.setString(8, strNotes);
            cs.setString(9, dtDateOfIssue);
            cs.setString(12, dtDateOfCancellation);
            cs.setString(13, Integer.toString(clientID));
            cs.setString(14, strHowWeLearn);
            cs.registerOutParameter(15, Types.DOUBLE);
            cs.execute();
            BigDecimal percent = cs.getBigDecimal(15);
            if (percent.compareTo(bdPercentOfDiscount)==0) return true;
            return false;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
//cash
    public ResultSet getCashMove() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCashMove: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
            cs.setString(1, "content");
            cs.setInt(2, userID);
            cs.setInt(3, clientID);
			cs.setBigDecimal(4, null);
            cs.setDate(5, null);
            cs.setBigDecimal(6, null);
            cs.setString(7, null);
            cs.registerOutParameter(8, Types.INTEGER);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public ResultSet getCashTotal() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCashMove: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
            cs.setString(1, "total");
            cs.setInt(2, userID);
            cs.setInt(3, clientID);
			cs.setBigDecimal(4, null);
			cs.setDate(5, null);
			cs.setBigDecimal(6, null);
			cs.setString(7, null);
			cs.registerOutParameter(8, Types.INTEGER);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
	public ResultSet getCashReport() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCashReport: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "report_cash");
			cs.setInt(2, userID);
			cs.setInt(3, clientID);
			cs.setBigDecimal(4, null);
			cs.setDate(5, null);
			cs.setBigDecimal(6, null);
			cs.setString(7, null);
			cs.registerOutParameter(8, Types.INTEGER);
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
    public boolean addCashNewRecord(BigDecimal bdSumma, String strNotes) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("addCashNewRecord: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
            cs.setString(1, "add_rec");
            cs.setInt(2, userID);
            cs.setInt(3, clientID);
			cs.setBigDecimal(4, null);
			cs.setDate(5, null);
			cs.setBigDecimal(6, bdSumma);
			cs.setString(7, strNotes);
			cs.registerOutParameter(8, Types.INTEGER);
            cs.execute();
            return (cs.getInt(8)!=0);
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
	public boolean editCashRecord(BigDecimal moveID, BigDecimal bdSumma, String strNotes) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("editCashRecord: parameter [cnn] cannot be null!"));
			return false;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "edit_rec");
			cs.setInt(2, userID);
			cs.setInt(3, clientID);
			cs.setBigDecimal(4, moveID);
			cs.setDate(5, null);
			cs.setBigDecimal(6, bdSumma);
			cs.setString(7, strNotes);
			cs.registerOutParameter(8, Types.INTEGER);
			cs.execute();
			return (cs.getInt(8) != 0);
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return false;
		}
	}
	public boolean delCashRecord(BigDecimal moveID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("delCashRecord: parameter [cnn] cannot be null!"));
			return false;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "del_rec");
			cs.setInt(2, userID);
			cs.setInt(3, clientID);
			cs.setBigDecimal(4, moveID);
			cs.setDate(5, null);
			cs.setBigDecimal(6, null);
			cs.setString(7, null);
			cs.registerOutParameter(8, Types.INTEGER);
			cs.execute();
			return (cs.getInt(8) != 0);
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return false;
		}
	}
	public boolean setCashStart(BigDecimal summa) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCashStart: parameter [cnn] cannot be null!"));
			return false;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "set_cash_in");
			cs.setInt(2, userID);
			cs.setInt(3, clientID);
			cs.setBigDecimal(4, null);
			cs.setDate(5, null);
			cs.setBigDecimal(6, summa);
			cs.setString(7, null);
			cs.registerOutParameter(8, Types.INTEGER);
			cs.execute();
			return (cs.getInt(8) != 0);
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return false;
		}
	}
//sales
	public ResultSet getSaleReport(int division) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getSaleReport: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_cash(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "report_sale");
			cs.setInt(2, userID);
			cs.setInt(3, clientID);
			cs.setInt(4, division);
//			long t = new java.util.Date().getTime() - 24 * 60 * 60 * 1000 * 1;
//			cs.setDate(5, new Date(t));
			cs.setDate(5, null);
			cs.setBigDecimal(6, null);
			cs.setString(7, null);
			cs.registerOutParameter(8, Types.INTEGER);
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
//checks
    public BigDecimal newCheck() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("newCheck: parameter [cnn] cannot be null!"));
			return BigDecimal.ZERO;
        }
        try {
			CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "new");
            cs.setBigDecimal(2, BigDecimal.ZERO);
            cs.setInt(3, userID);
            cs.setInt(4, clientID);
            cs.setInt(5, config.TERMINAL_ID);
            cs.registerOutParameter(6, Types.DOUBLE);
            cs.execute();
            currentCheckID = cs.getBigDecimal(6);
            getCheckInfo(currentCheckID);
            return currentCheckID;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return BigDecimal.ZERO;
        }
    }
    public BigDecimal lastCheck() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("lastCheck: parameter [cnn] cannot be null!"));
			return BigDecimal.ZERO;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "last");
            cs.setBigDecimal(2, BigDecimal.ZERO);
            cs.setInt(3, userID);
            cs.setInt(4, clientID);
            cs.setInt(5, config.TERMINAL_ID);
            cs.registerOutParameter(6, Types.DOUBLE);
            cs.execute();
            currentCheckID = cs.getBigDecimal(6);
            getCheckInfo(currentCheckID);
            return currentCheckID;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return BigDecimal.ZERO;
        }
    }
    public boolean getCheckInfo(BigDecimal checkID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCheckInfo: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
			CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "info");
            cs.setBigDecimal(2, checkID);
            cs.setInt(3, userID);
            cs.setInt(4, clientID);
            cs.setInt(5, config.TERMINAL_ID);
            cs.registerOutParameter(6, Types.DOUBLE);
            ResultSet res = cs.executeQuery();
            while (res.next()) {
                checkStatus         = res.getInt("CheckStatus");
                checkTypePayment    = res.getInt("TypePayment");
                checkFlagReturn     = res.getInt("FlagReturn");
                checkSumBase        = res.getBigDecimal("SumBase").setScale(2);
                checkSumDiscount    = res.getBigDecimal("SumDiscount").setScale(2);
                checkSum            = res.getBigDecimal("Sum").setScale(2);
            }
            return true;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean setCheckStatus(int StatusCheck) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCheckStatus: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "status");
            cs.setBigDecimal(2, currentCheckID);
            cs.setInt(3, StatusCheck);
            cs.setInt(4, 0);
            cs.setInt(5, 0);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.execute();
            if (cs.getInt(6) == StatusCheck) {
                checkStatus = StatusCheck;
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при установке статуса для чека!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean setCheckPaymentType(int paymentType, BigDecimal checkID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCheckPaymentType: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "payment");
            cs.setBigDecimal(2, checkID);
            cs.setInt(3, paymentType);
            cs.setInt(4, 0);
            cs.setInt(5, 0);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.execute();
            if (cs.getInt(6) == paymentType) {
                getCheckInfo(currentCheckID);
                //DialogBoxs.viewMessage("Тип оплаты установлен успешно!");
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при установке типа оплаты для чека!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean setCheckFlagReturn(int flagReturn, BigDecimal checkID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCheckFlagReturn: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "flagReturn");
            cs.setBigDecimal(2, checkID);
            cs.setInt(3, flagReturn);
            cs.setInt(4, 0);
            cs.setInt(5, 0);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.execute();
            if (cs.getInt(6) == flagReturn) {
                getCheckInfo(currentCheckID);
                //DialogBoxs.viewMessage("Тип оплаты установлен успешно!");
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при установке типа оплаты для чека!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean checkIsBlank(){
        if (checkSum.compareTo(BigDecimal.ZERO) == 0) return true;
        return false;
    }
	public ResultSet getCheckList() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCheckList: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
			cs.setString(1, "list");
			cs.setBigDecimal(2, null);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setInt(5, config.TERMINAL_ID);
			cs.registerOutParameter(6, Types.DOUBLE);
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
    public ResultSet getCheckContent(BigDecimal checkID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCheckContent: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "content");
            cs.setBigDecimal(2, checkID);
            cs.setInt(3, userID);
            cs.setInt(4, clientID);
            cs.setInt(5, config.TERMINAL_ID);
            cs.registerOutParameter(6, Types.DOUBLE);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public ResultSet getCheckContentLastModi(BigDecimal checkID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getCheckContent: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check(?,?,?,?,?,?)}");
            cs.setString(1, "content_lastmodi");
            cs.setBigDecimal(2, checkID);
            cs.setInt(3, userID);
            cs.setInt(4, clientID);
            cs.setInt(5, config.TERMINAL_ID);
            cs.registerOutParameter(6, Types.INTEGER);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
//check add good
    public int addGoodInCheck(String barCode){
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("addGoodInCheck: parameter [cnn] cannot be null!"));
			return 0;
        }
        if (barCode.equals("")) return 0;
        try {
			String s = "";
            CallableStatement cs = cnn.prepareCall("{call pr_"+s+"check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_add");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, 0);
            cs.setString(7, barCode);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2);
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return 0;
        }
    }
    public int addGoodInCheck(int goodID,String barCodeNew) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("deleteGoodFromCheck: parameter [cnn] cannot be null!"));
			return 0;
        }
        if (goodID == 0) {
            return 0;
        }
        try {
			String s = "";
            CallableStatement cs = cnn.prepareCall("{call pr_"+s+"check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_add");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setString(7, barCodeNew);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2);
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return 0;
        }
    }
    public int deleteGoodFromCheck(int goodID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("deleteGoodFromCheck: parameter [cnn] cannot be null!"));
			return 0;
        }
        if (goodID==0) {
            return 0;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_delete");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, userID);
			cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setString(7, "");
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2);
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return 0;
        }
    }
    public int addGoodInCheckQuantity(int goodID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("addGoodInCheckQuantity: parameter [cnn] cannot be null!"));
			return 0;
        }
        if (goodID == 0) {
            return 0;
        }
        try {
			String s = "";
            CallableStatement cs = cnn.prepareCall("{call pr_"+s+"check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_plus");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setString(7, "");
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2);
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return 0;
        }
    }
    public int editGoodQuantityInCheck(int goodID, BigDecimal newQuantity) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("editGoodQuantityInCheck: parameter [cnn] cannot be null!"));
			return 0;
        }
        if (goodID == 0) {
            return 0;
        }
        try {
			String s = "";
            CallableStatement cs = cnn.prepareCall("{call pr_"+s+"check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_edit_quantity");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setBigDecimal(7, newQuantity);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2);
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return 0;
        }
    }
//check discount
    public boolean setCheckDiscount(int typeDiscount, BigDecimal discount, int goodID, String cardID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCheckDiscount: parameter [cnn] cannot be null!"));
			return false;
        }
        if (goodID == 0) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "discount"+Integer.toString(typeDiscount));
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setBigDecimal(4, discount);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setString(7, cardID);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2) != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean setCheckDiscountByCard(String cardID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCheckDiscountByCard: parameter [cnn] cannot be null!"));
			return false;
        }
        if (cardID.equals("")) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "discount_by_card");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, 0);
            cs.setInt(5, clientID);
			cs.setInt(6, 0);
			cs.setString(7, cardID);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2) != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean setCheckNewCard(String cardID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setCheckNewCard: parameter [cnn] cannot be null!"));
			return false;
        }
        if (cardID.equals("")) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_check_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "check_new_card");
			cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, currentCheckID);
            cs.setInt(4, 0);
            cs.setInt(5, clientID);
            cs.setInt(6, 0);
            cs.setString(7, cardID);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2) != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
//price over
	public ResultSet getPriceOverList() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getPriceOverList: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_price_over_stat(?,?,?,?,?)}");
			cs.setString(1, "list");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, "");//DocID
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public ResultSet getPriceOverContent(String docID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getPriceOverContent: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_price_over_stat(?,?,?,?,?)}");
			cs.setString(1, "content");
			cs.registerOutParameter(2, Types.DOUBLE);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, docID);//DocID
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public ResultSet getPriceOverReport(String docID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getPriceOverReport: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_price_over_stat(?,?,?,?,?)}");
			cs.setString(1, "report");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, docID);//DocID
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public boolean setPriceOverStatus(String docID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setPriceOverStatus: parameter [cnn] cannot be null!"));
			return false;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_price_over_stat(?,?,?,?,?)}");
			cs.setString(1, "status1");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, docID);//DocID
			cs.execute();
			if (cs.getInt(2) > 0 ) {
				return true;
			} else {
				DialogBoxs.viewMessage("Ошибка при установке статуса для переоценки!");
				return false;
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return false;
		}
	}
//receipt
	public ResultSet getReceiptList() {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getReceiptList: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_receipt_stat(?,?,?,?,?)}");
			cs.setString(1, "list");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, "");//DocID
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public ResultSet getReceiptContent(String docID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getReceiptContent: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_receipt_stat(?,?,?,?,?)}");
			cs.setString(1, "content");
			cs.registerOutParameter(2, Types.DOUBLE);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, docID);//DocID
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public ResultSet getReceiptReport(String docID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getReceiptReport: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_receipt_stat(?,?,?,?,?)}");
			cs.setString(1, "report");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, docID);//DocID
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public boolean setReceiptStatus(String docID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setReceiptStatus: parameter [cnn] cannot be null!"));
			return false;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_receipt_stat(?,?,?,?,?)}");
			cs.setString(1, "status1");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, docID);//DocID
			cs.execute();
			if (cs.getInt(2) > 0) {
				return true;
			} else {
				DialogBoxs.viewMessage("Ошибка при установке статуса для переоценки!");
				return false;
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return false;
		}
	}

//order - заказы магазинов
    public ResultSet getOrderList(Date dt1, Date dt2) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getOrderList: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "list");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, null);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, dt1);
            cs.setDate(8, dt2);
            cs.setString(9, null);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public ResultSet getOrderContent(BigDecimal docID, String sort) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getOrderContent: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "content "+sort);
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, null);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public ResultSet getOrderReport(BigDecimal docID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getOrderReport: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
//            CallableStatement cs = cnn.prepareCall("{call pr_order_stat(?,?,?,?,?)}");
//            cs.setString(1, "report");
//            cs.registerOutParameter(2, Types.INTEGER);
//            cs.setInt(3, userID);
//            cs.setInt(4, clientID);
//            cs.setString(5, docID);//DocID
//            ResultSet res = cs.executeQuery();
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
			cs.setString(1, "content asc");
			cs.registerOutParameter(2, Types.DOUBLE);
			cs.setBigDecimal(3, docID);
			cs.setInt(4, userID);
			cs.setInt(5, clientID);
			cs.setInt(6, config.TERMINAL_ID);
			cs.setDate(7, null);
			cs.setDate(8, null);
			cs.setString(9, null);
			ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public boolean delOrder(BigDecimal docID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("delOrder: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "del");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, null);
            cs.execute();
            if (cs.getInt(2) > 0) {
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при удалении документа!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean copyOrder(BigDecimal docID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("copyOrder: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "copy");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, null);
            cs.execute();
            if (cs.getInt(2) > 0) {
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при удалении документа!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public BigDecimal newOrder() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("newOrder: parameter [cnn] cannot be null!"));
			return BigDecimal.ZERO;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "new");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, null);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, null);
            cs.execute();
            BigDecimal currentID = cs.getBigDecimal(2);
            return currentID;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return BigDecimal.ZERO;
        }
    }
    public boolean editGoodQuantityInOrder(BigDecimal docID, int goodID, BigDecimal newQuantity) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("editGoodQuantityInOrder: parameter [cnn] cannot be null!"));
			return false;
        }
        if (goodID == 0) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_edit_quantity");
            cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setBigDecimal(7, newQuantity);
            cs.execute();
            if (cs.getInt(2) == 0) {
                return false;
            }
            return true;
        } catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean deleteGoodFromOrder(BigDecimal docID,int goodID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("deleteGoodFromOrder: parameter [cnn] cannot be null!"));
			return false;
        }
        if (goodID == 0) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_delete");
            cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setString(7, "");
            cs.execute();
            if (cs.getInt(2) == 0) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean addGoodInOrderQuantity(BigDecimal docID,int goodID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("addGoodInOrderQuantity: parameter [cnn] cannot be null!"));
			return false;
        }
        if (goodID == 0) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order_content(?,?,?,?,?,?,?)}");
            cs.setString(1, "good_plus");
            cs.registerOutParameter(2, Types.INTEGER);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, goodID);
            cs.setString(7, "");
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            if (cs.getInt(2) == 0) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean getOrderInfo(BigDecimal docID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getOrderInfo: parameter [cnn] cannot be null!"));
			return false;
        }
        if (docID.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "info");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, null);
            resOrderInfo = cs.executeQuery();
            //resOrderInfo.last();
			resOrderInfo.absolute(1);
            return resOrderInfo.getRow() != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public String getOrderInfo(String fieldName, String typeValue) {
        if (resOrderInfo == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getOrderInfo: parameter [resOrderInfo] cannot be null!"));
			return "";
        }
        try {
            if (fieldName.equals("")) {
                return "";
            }
            String strResult = "";
            if (resOrderInfo.absolute(1)) {
                if (typeValue.equals("String")) {
                    strResult = resOrderInfo.getString(fieldName);
                    strResult = (strResult == null) ? "" : strResult;
                } else if (typeValue.equals("int")) {
                    strResult = Integer.toString(resOrderInfo.getInt(fieldName));
                } else if (typeValue.equals("BigDecimal")) {
                    if (resOrderInfo.getBigDecimal(fieldName) != null) {
                        strResult = resOrderInfo.getBigDecimal(fieldName).setScale(4, RoundingMode.HALF_UP).toPlainString();
                    }
                } else if (typeValue.equals("DateTime")) {
                    if (resOrderInfo.getDate(fieldName) != null) {
                        strResult = resOrderInfo.getString(fieldName).toString();
                    }
                } else if (typeValue.equals("Date")) {
                    java.sql.Date dt = resOrderInfo.getDate(fieldName);
                    if (dt != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
                        strResult = dateFormat.format(dt).toString();
                    }
                }
            }
            return strResult;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public boolean setOrderStatus(BigDecimal docID,int statusCheck) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setOrderStatus: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "status");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, statusCheck);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, null);
            cs.execute();
            if (cs.getInt(2) == statusCheck) {
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при установке статуса для документа!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public boolean setOrderNotes(BigDecimal docID, String notes) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("setOrderNotes: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_order(?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "notes");
            cs.registerOutParameter(2, Types.DOUBLE);
            cs.setBigDecimal(3, docID);
            cs.setInt(4, userID);
            cs.setInt(5, clientID);
            cs.setInt(6, config.TERMINAL_ID);
            cs.setDate(7, null);
            cs.setDate(8, null);
            cs.setString(9, notes);
            cs.execute();
            if (cs.getInt(2) == 1) {
                return true;
            } else {
                DialogBoxs.viewMessage("Ошибка при установке статуса для документа!");
                return false;
            }
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
//goods    
    public ResultSet getGoodsList(int catID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getGoodsList: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_goods_list(?,?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "in category for shop");
            cs.setString(2, "Name");
            cs.setString(3, "asc");
            cs.setInt(4, 0);
            cs.setInt(5, 0);
            cs.setInt(6, 99999999);
            cs.setString(7, Integer.toString(clientID));
            cs.setString(8, "%");
            cs.setString(9, "%");
            cs.setString(10, "");
            cs.setInt(11, catID);
            cs.setInt(12, 0);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
//barcode
	public ResultSet getBarcodeShortReport(int division) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getBarcodeShortReport: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_barcode(?,?,?,?,?,?)}");
			cs.setString(1, "barcode_short_report");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, userID);
			cs.setInt(4, clientID);
			cs.setString(5, "");
			cs.setInt(6, division);
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
//search
    public ResultSet getSearchContent(String _group, String _article, String _name) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getSearchContent: parameter [cnn] cannot be null!"));
			return null;
        }
//        if(_group.equals("") && _article.equals("") && _name.equals("")) return null;
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_goods_search(?,?,?,?,?,?)}");
            cs.setString(1, "good_search");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, clientID);
            cs.setString(4, _group);
            cs.setString(5, _article);
            cs.setString(6, _name);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
//promo
    public ResultSet getPromoList() {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getPromoList: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            //CALL pr_promo_info('getPromoListForShop',0,'',0,'','','',null,null,null);
            CallableStatement cs = cnn.prepareCall("{call pr_promo_info(?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "getPromoListForShop");
            cs.setInt(2, 0);
            cs.setString(3, "");
            cs.setInt(4, 0);
            cs.setString(5, "");
            cs.setString(6, "");
            cs.setString(7, "");
            cs.setInt(8, 0);
            cs.setInt(9, 0);
            cs.setDouble(10, 0);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public boolean getPromoInfo(int promoID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getPromoInfo: parameter [cnn] cannot be null!"));
			return false;
        }
        if (promoID == 0) {
            return false;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_promo_info(?,?,?,?,?,?,?,?,?,?)}");
            cs.setString(1, "getById");
            cs.setInt(2, promoID);
            cs.setString(3, "");
            cs.setInt(4, 0);
            cs.setString(5, "");
            cs.setString(6, "");
            cs.setString(7, "");
            cs.setInt(8, 0);
            cs.setInt(9, 0);
            cs.setDouble(10, 0);
            resPromoInfo = cs.executeQuery();
            //resPromoInfo.last();
            resPromoInfo.absolute(1);
            return resPromoInfo.getRow() != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
    public String getPromoInfo(String fieldName, String typeValue) {
        if (resPromoInfo == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getPromoInfo: parameter [resPromoInfo] cannot be null!"));
			return "";
        }
        try {
            if (fieldName.equals("")) {
                return "";
            }
            String strResult = "";
            if (resPromoInfo.absolute(1)) {
                if (typeValue.equals("String")) {
                    strResult = resPromoInfo.getString(fieldName);
                    strResult = (strResult == null) ? "" : strResult;
                } else if (typeValue.equals("int")) {
                    strResult = Integer.toString(resPromoInfo.getInt(fieldName));
                } else if (typeValue.equals("BigDecimal")) {
                    if (resPromoInfo.getBigDecimal(fieldName) != null) {
                        strResult = resPromoInfo.getBigDecimal(fieldName).setScale(2, RoundingMode.HALF_UP).toPlainString();
                    }
                } else if (typeValue.equals("DateTime")) {
                    if (resPromoInfo.getDate(fieldName) != null) {
                        strResult = resPromoInfo.getString(fieldName).toString();
                    }
                }
            }
            return strResult;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
    public boolean assignPromoByID(int promoID) {
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("assignPromoByID: parameter [cnn] cannot be null!"));
			return false;
        }
        try {
            if (promoID==0) return false;
            CallableStatement cs = cnn.prepareCall("{call pr_promo_assign(?,?,?,?)}");
            cs.setString(1, "assignPromoByID");
            cs.registerOutParameter(2, Types.INTEGER);
            cs.setInt(3, promoID);
            cs.setBigDecimal(4, currentCheckID);
            cs.execute();
            getCheckInfo(currentCheckID);
            return cs.getInt(2) != 0;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return false;
        }
    }
//tree
    public ResultSet getTreeNodeList(int nodeID){
        if (cnn == null) {
            MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getTreeNodeList: parameter [cnn] cannot be null!"));
			return null;
        }
        try {
            CallableStatement cs = cnn.prepareCall("{call pr_tree_NS(?,?,?,?,?,?)}");
            cs.setString(1, "category");
            cs.setString(2, "CatID");
            cs.setInt(3, nodeID);
            cs.setString(4, null);
            cs.setString(5, null);
            cs.setString(6, null);
            ResultSet res = cs.executeQuery();
            return res;
        } catch (SQLException e) {
            MyUtil.errorToLog(this.getClass().getName(),e);
			DialogBoxs.viewError(e);
            return null;
        }
    }
//seller
    public ResultSet getSellerList(String findSellerID, String findSellerName) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("getSellerList: parameter [cnn] cannot be null!"));
			return null;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_seller_info(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "getSellerListForShop");
			cs.setInt(2, 0);
			cs.setInt(3, clientID);
			cs.setBigDecimal(4, BigDecimal.ZERO);
			cs.setInt(5, 0);
			cs.setInt(6, 0);
			cs.setString(7, findSellerID);
			cs.setString(8, findSellerName);
			ResultSet res = cs.executeQuery();
			return res;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return null;
		}
	}
	public boolean assignSellerByID(int sellerID, int goodID) {
		if (cnn == null) {
			MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("assignSellerByID: parameter [cnn] cannot be null!"));
			return false;
		}
		try {
			CallableStatement cs = cnn.prepareCall("{call pr_seller_info(?,?,?,?,?,?,?,?)}");
			cs.setString(1, "setSellerID");
			cs.registerOutParameter(2, Types.INTEGER);
			cs.setInt(3, 0);
			cs.setBigDecimal(4, currentCheckID);
			cs.setInt(5, sellerID);
			cs.setInt(6, goodID);
			cs.setString(7, "");
			cs.setString(8, "");
			cs.execute();
			return cs.getInt(2) != 0;
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return false;
		}
	}
}
