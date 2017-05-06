package main;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.sun.org.apache.bcel.internal.generic.FNEG;
import db.ConnectionDb;
import forms.FrmMain;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class POS_Terminal {
	private ActiveXComponent pos;
	private final ConfigReader conf;
	private final ConnectionDb cnn;
	public String lastResult = "";
	public String lastErrorCode = "";
	public String lastErrorDescription = "";
	public String lastStatMsgCode = "";
	public String lastStatMsgDescription = "";
//	public FrmMain frmMain;
	private String sum = "";
	
	public POS_Terminal() {
		this.conf = ConfigReader.getInstance();
		this.cnn = ConnectionDb.getInstance();
	}
	public void checkError() {
		lastResult = Dispatch.call(pos, "LastResult").toString();
		lastErrorCode = Dispatch.call(pos, "LastErrorCode").toString();
		lastErrorDescription = Dispatch.call(pos, "LastErrorDescription").toString();
		lastStatMsgCode = Dispatch.call(pos, "LastStatMsgCode").toString();
		lastStatMsgDescription = Dispatch.call(pos, "LastStatMsgDescription").toString();
//System.out.println("lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
	}

	public boolean load() {
		try {
			pos = new ActiveXComponent("ECRCommX.BPOS1Lib");
		} catch (NoClassDefFoundError | ComFailException ex) {
//System.out.println("ComFailException: "+ex.toString());
			MyUtil.errorToLog(this.getClass().getName(), (Exception) ex);
			String filename = "lib/ECRcommX.dll";
			File file = new File(filename);
			if (!file.exists()) {
				DialogBoxs.viewMessage("Отсутствует файл: " + file.getPath() + "\n\nРабота с POS-терминалом невозможна!\n\nError message: ".concat(ex.getMessage()));
				return false;
			}
			try {
				Runtime.getRuntime().exec("regsvr32 /s " + filename);
				Thread.sleep(1000);
			} catch (IOException e) {
				//MyUtil.errorToLog(this.getClass().getName(), ex);
				JOptionPane.showMessageDialog(new JFrame(), "Ошибка при регистрации COM-объекта!\nРабота с POS-терминалом невозможна!\n\nError message: ".concat(ex.getMessage()), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
				return false;
			} catch (InterruptedException ex1) {
				JOptionPane.showMessageDialog(new JFrame(), "Ошибка в модуле POS-terminal!\nРабота с POS-терминалом невозможна!\n\nError message: ".concat(ex.getMessage()), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (UnsatisfiedLinkError ex) {
//System.out.println("UnsatisfiedLinkError: "+ex.toString());
			MyUtil.errorToLog(this.getClass().getName(), ex.toString());
			JOptionPane.showMessageDialog(new JFrame(), "Ошибка в библиотеке JACOB!\nРабота с POS-терминалом невозможна!\n\nError message: ".concat(ex.getMessage()), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try {
			pos = new ActiveXComponent("ECRCommX.BPOS1Lib");
		} catch (NoClassDefFoundError | ComFailException ex) {
//System.out.println("ComFailException: " + ex.toString());
			MyUtil.errorToLog(this.getClass().getName(), (Exception) ex);
			JOptionPane.showMessageDialog(new JFrame(), "Ошибка COM-объект не зарегистрирован!\nРабота с POS-терминалом невозможна!\n\nError message: ".concat(ex.getMessage()), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (UnsatisfiedLinkError ex) {
//System.out.println("UnsatisfiedLinkError: " + ex.toString());
			MyUtil.errorToLog(this.getClass().getName(), ex.toString());
			JOptionPane.showMessageDialog(new JFrame(), "Ошибка в библиотеке JACOB!\nРабота с POS-терминалом невозможна!\n\nError message: ".concat(ex.getMessage()), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
//System.out.println(pos.toString());
		if(!conf.POS_SendInfo) getInfo();
		return true;
	}
	public boolean checkStatus() {
		String str = "";
		pos.invoke("CommClose");
		//pos.invoke("SetErrorLang", "2");
		Dispatch.call(pos, "SetErrorLang", "2");
		Dispatch.call(pos, "useLogging", 2, conf.CURDIR+"\\logs\\pos-terminal.log");
		if (conf.POS_COM_PORT.equals("0")) {
			Dispatch.call(pos, "CommOpenAuto", conf.POS_BAUD_RATE);
		}else{
			Dispatch.call(pos, "CommOpen", conf.POS_COM_PORT, conf.POS_BAUD_RATE);
		}
//		str = Dispatch.call(pos, "MerchantID").toString();

		str = Dispatch.call(pos, "PosGetInfo").toString();
		waitResponse();
//		str = Dispatch.call(pos, "TerminalInfo").toString();
//System.out.println("TerminalInfo=" + str);
		checkError();
//System.out.println("1 lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
		Dispatch.call(pos, "CommClose");
		if (lastResult.equals("0")) {
			return true;
		}
		return false;
	}
	
	private void waitResponse(){
		boolean res = false;
		String pan = "";
		String entrymode = "";
		String lastStMsCode = "0";
		while (Dispatch.call(pos, "LastResult").toString().equals("2")){
			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				MyUtil.errorToLog(this.getClass().getName(), (Exception) ex);
			}
			String result = Dispatch.call(pos, "LastStatMsgCode").toString();
//System.out.println("2 lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
//System.out.println("result:"+result);
			if (!result.equals("0") && !result.equals(lastStMsCode)) lastStMsCode = result;
			if (lastStMsCode.equals("11")) {
				pan = Dispatch.call(pos, "PAN").toString();
				entrymode = Dispatch.call(pos, "EntryMode").toString();
				result = Dispatch.call(pos, "CorrectTransaction", sum, "0").toString();
				String str = "\npan:" + pan + "\n"
						+ "entrymode:" + entrymode + "\n"
						+ "lastStatMsgCode:" + result + "\n";
				MyUtil.messageToLog(getClass().getName(), str);
			}
		}
	}
	private void returnValues(){
		waitResponse();
		String status = "";
		String str = "";
		if (Dispatch.call(pos, "LastResult").toString().equals("1") && !Dispatch.call(pos, "ResponseCode").toString().equals("20")) {
			status = "ERROR";
			lastErrorCode = Dispatch.call(pos, "LastErrorCode").toString();
			lastErrorDescription = Dispatch.call(pos, "LastErrorDescription").toString();
			if (lastErrorCode.equals("4")){
				str  = "\nResponseCode\t\t:"+Dispatch.call(pos, "ResponseCode").toString()+"\n";
				str += "TerminalID\t\t:"+Dispatch.call(pos, "TerminalID").toString()+"\n";
				str += "MerchantID\t\t:"+Dispatch.call(pos, "MerchantID").toString()+"\n";
				str += "LastResult:\t\t" + Dispatch.call(pos, "LastResult").toString() + "\n";
				str += "LastErrorCode:\t\t" + Dispatch.call(pos, "LastErrorCode").toString() + "\n";
				str += "LastErrorDescription:\t\t" + Dispatch.call(pos, "LastErrorDescription").toString() + "\n";
				str += "LastStatMsgCode:\t\t" + Dispatch.call(pos, "LastStatMsgCode").toString() + "\n";
				str += "LastStatMsgDescription:\t\t" + Dispatch.call(pos, "LastStatMsgDescription").toString() + "\n";
			}
			//System.out.println(status+":"+lastErrorCode+" "+lastErrorDescription+"\n"+str);
			MyUtil.messageToLog(getClass().getName(), str);
		}else{
				str  = "\nSuccessful:\t\tOK\n";
//				str += "LastResult:\t\t"+Dispatch.call(pos, "LastResult").toString()+"\n";
//				str += "LastErrorCode:\t\t"+Dispatch.call(pos, "LastErrorCode").toString()+"\n";
//				str += "LastErrorDescription:\t\t"+Dispatch.call(pos, "LastErrorDescription").toString()+"\n";
				str += "StatMsgCode:\t"+Dispatch.call(pos, "LastStatMsgCode").toString()+"\n";
				str += "StatMsgDesc:\t"+Dispatch.call(pos, "LastStatMsgDescription").toString()+"\n";
//				str += "ResponseCode:\t\t"+Dispatch.call(pos, "ResponseCode").toString()+"\n";
				str += "TerminalID:\t\t"+Dispatch.call(pos, "TerminalID").toString()+"\n";
				str += "MerchantID:\t\t"+Dispatch.call(pos, "MerchantID").toString()+"\n";
				str += "PAN:\t\t\t"+Dispatch.call(pos, "PAN").toString()+"\n";
				str += "RRN:\t\t\t"+Dispatch.call(pos, "RRN").toString()+"\n";
				str += "AuthCode:\t\t"+Dispatch.call(pos, "AuthCode").toString()+"\n";
				str += "DateTime:\t\t"+Dispatch.call(pos, "DateTime").toString()+"\n";
//				str += "InvoiceNum:\t\t"+Dispatch.call(pos, "InvoiceNum").toString()+"\n";
//				str += "ExpDate:\t\t"+Dispatch.call(pos, "ExpDate").toString()+"\n";
//				str += "CardHolder:\t\t"+Dispatch.call(pos, "CardHolder").toString()+"\n";
//				str += "IssuerName:\t\t"+Dispatch.call(pos, "IssuerName").toString()+"\n";
//				str += "SignVerif:\t\t"+Dispatch.call(pos, "SignVerif").toString()+"\n";
//				str += "TxnNum:\t\t"+Dispatch.call(pos, "TxnNum").toString()+"\n";
//				str += "TxnType:\t\t"+Dispatch.call(pos, "TxnType").toString()+"\n";
//				str += "TotalsDebitNum:\t\t"+Dispatch.call(pos, "TotalsDebitNum").toString()+"\n";
//				str += "TotalsDebitAmt:\t\t"+Dispatch.call(pos, "TotalsDebitAmt").toString()+"\n";
//				str += "TotalsCreditNum:\t\t"+Dispatch.call(pos, "TotalsCreditNum").toString()+"\n";
//				str += "TotalsCreditAmt:\t\t"+Dispatch.call(pos, "TotalsCreditAmt").toString()+"\n";
//				str += "TotalsCancelledNum:\t\t"+Dispatch.call(pos, "TotalsCancelledNum").toString()+"\n";
//				str += "TotalsCancelledAmt:\t\t"+Dispatch.call(pos, "TotalsCancelledAmt").toString()+"\n";
//			System.out.println(str);
			MyUtil.messageToLog(getClass().getName(), str);
		}
	}
	public void getInfo(){
		conf.POS_SendInfo = true;
		if (conf.POS_ACTIVE.equals("0")) return;
//		load();
//TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/						   I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/X1HA1TEP/X1HA2TEP/X1HAXTE /G1HA0TEP
//TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/L1HA2TEP/				   I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/X1HA1TEP/X1HA2TEP/X1HAXTEP/G1HA0TEP
//TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/L1HA2TEP/L1HA3TEP/P1HACTEP/I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/S112T4QP/G11234QP/G1HA0TEP/X1/HA1TEP/X1HA2TEP/X1HAXTEP
		String str = "";
		pos.invoke("CommClose");
		//pos.invoke("SetErrorLang","2");
		Dispatch.call(pos, "SetErrorLang", "2");
		Dispatch.call(pos, "useLogging", 2, conf.CURDIR + "\\logs\\pos-terminal.log");

		if (conf.POS_COM_PORT.equals("0")) {
			//pos.invoke("CommOpenAuto", conf.POS_BAUD_RATE);
			Dispatch.call(pos, "CommOpenAuto", conf.POS_BAUD_RATE);
		} else {
			Dispatch.call(pos, "CommOpen", conf.POS_COM_PORT, conf.POS_BAUD_RATE);
		}
		waitResponse();
//тип оплаты "оплата частями"
		str = Dispatch.call(pos, "PosGetInfo").toString();
		waitResponse();
		str = Dispatch.call(pos, "TerminalInfo").toString();
		Dispatch.call(pos, "CommClose");
		cnn.setCheckInfo(str);
	}
	public boolean purchase(){
//TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/						   I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/X1HA1TEP/X1HA2TEP/X1HAXTE /G1HA0TEP
//TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/L1HA2TEP/				   I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/X1HA1TEP/X1HA2TEP/X1HAXTEP/G1HA0TEP
//TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/L1HA2TEP/L1HA3TEP/P1HACTEP/I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/S112T4QP/G11234QP/G1HA0TEP/X1/HA1TEP/X1HA2TEP/X1HAXTEP
		String str = "";
		pos.invoke("CommClose");
		//pos.invoke("SetErrorLang","2");
		Dispatch.call(pos, "SetErrorLang", "2");
		Dispatch.call(pos, "useLogging", 2, conf.CURDIR + "\\logs\\pos-terminal.log");
		
		if (conf.POS_COM_PORT.equals("0")) {
			//pos.invoke("CommOpenAuto", conf.POS_BAUD_RATE);
			Dispatch.call(pos, "CommOpenAuto", conf.POS_BAUD_RATE);
		} else {
			Dispatch.call(pos, "CommOpen", conf.POS_COM_PORT, conf.POS_BAUD_RATE);
		}
		checkError();
		if (lastResult.equals("1")) {
			MyUtil.messageToLog(getClass().getName(), "lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
			Dispatch.call(pos, "CommClose");
			return false;
		}else if (lastResult.equals("2")) {
			waitResponse();
		}
		//получим сумму чека * 100 и без запятой
		sum = cnn.checkSum.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).abs().toString();
//тип оплаты "оплата частями"
		str = Dispatch.call(pos, "PosGetInfo").toString();
		waitResponse();
		str = Dispatch.call(pos, "TerminalInfo").toString();
MyUtil.messageToLog(getClass().getName(), str);
		if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
//System.out.println("TerminalInfo="+str);
			String mer = "";
			String mes = "";
			int mid = 0;
			String[] s = str.split("/");
			//mer = s[1].replace("S1", "X1").replace("0", "1");//мерчант оплаты частями - не работает со старыми терминалами
			//mer = s[1].replace("S1", "X1").replace("0", "2");//мерчант мгновенной рассрочки
			//для старых терминалов надо менять 1-ый и 5-ый символы на X и 1 соответственно
			mer = "X" + s[1].substring(1);
			mer = mer.substring(0, 4) + "1" + mer.substring(5);
			int i = 0;
			while (i < s.length) {
				if (s[i].equals(mer)) {
					mid = i;
					break;
				}else{
					mes += Integer.toString(i)+" - "+s[i]+"\n";
				}
				i++;
			}
			if (mid==0) {
				JOptionPane.showMessageDialog(new JFrame(), "Не определен номер мерчанта!\n\n"
						+ "Список мерчантов в терминале:\n"
						+ mes, "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
				Dispatch.call(pos, "CommClose");
				return false;
			}else{
				Dispatch.call(pos, "PurchaseService", mid, sum, "0046/"+Integer.toString(cnn.checkTypePayment).substring(1));
			}
		}else{
			Dispatch.call(pos, "Purchase", sum, "0", conf.POS_MerchantIdx);
		}

		//returnValues();
		//MyUtil.messageToLog(getClass().getName(), "returnValues() - ok");
		waitResponse();
		//MyUtil.messageToLog(getClass().getName(), "waitResponse() - ok");
		String receiptSlip = null;
		checkError();
		//MyUtil.messageToLog(getClass().getName(), "checkError() - ok");
		if (lastResult.equals("0")) {
			Dispatch.call(pos, "Confirm");
			//MyUtil.messageToLog(getClass().getName(), "Confirm() - ok");
			waitResponse();
			if (Dispatch.call(pos, "LastResult").toString().equals("0")) {
				Dispatch.call(pos, "ReqCurrReceipt").toString();
				waitResponse();
				checkError();
				if (Dispatch.call(pos, "LastResult").toString().equals("0")) {
					String s = Dispatch.call(pos, "Receipt").toString();
					MyUtil.messageToLog(getClass().getName(), "\nReceipt: "+s);
				}else{
					MyUtil.messageToLog(getClass().getName(), "lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " "+lastErrorDescription + "\tlastStatMsg: "+ lastStatMsgCode + " "+lastStatMsgDescription);
					Dispatch.call(pos, "CommClose");
					return false;
				}
			}else{
				checkError();
				MyUtil.messageToLog(getClass().getName(), "lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
				Dispatch.call(pos, "CommClose");
				return false;
			}
		}else if (lastResult.equals("1") && lastErrorCode.equals("4")) {
			Dispatch.call(pos, "ReqCurrReceipt").toString();
			waitResponse();
			checkError();
			MyUtil.messageToLog(getClass().getName(), "lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
			Dispatch.call(pos, "CommClose");
			JOptionPane.showMessageDialog(new JFrame(), "Операция отменена!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return false;
		}else{
			checkError();
			MyUtil.messageToLog(getClass().getName(), "lastResult: " + lastResult + "\tlastError: " + lastErrorCode + " " + lastErrorDescription + "\tlastStatMsg: " + lastStatMsgCode + " " + lastStatMsgDescription);
			Dispatch.call(pos, "CommClose");
			return false;
		}

		Dispatch.call(pos, "CommClose");
		return true;
	}
}
/*

get_LastErrorCode (BYTE* pVal), property LastErrorCode
[OUT] pVal - returned values:
1 – error opening COM port
2 – need to open COM port
3 – error connecting with terminal
4 – terminal returned an error. For additional analysis Response Code is used (see
below).

get_LastStatMsgCode(BYTE* pVal), property LastStatMsgCode
[OUT] pVal - returns one following status codes:
0 – status code is not available.
1 – card was read
2 – used a chip card
3 – authorization in progress
4 – waiting for cashier action
5 – printing receipt
6 – pin entry is needed
7 – card was removed
8 – EMV multi aid’s
9 – waiting for card
10 – in progress
11 – correct transaction

get_ResponseCode(ULONG* pVal), property ResponseCode
[OUT] pVal – returns response code.
Is used in case of approved / not approved authorization.
Codes below 1000 are received from host.
1000 – General error (should be used in exceptional case)
1001 – Transaction canceled by user
1002 – EMV Decline
1003 – Transaction log is full. Need close batch
1004 – No connection with host
1005 – No paper in printer
1006 – Error Crypto keys
1007 – Card reader is not connected
1008 – Transaction is already complete


get_TxnType(BYTE* pVal), property TxnType
[OUT] pVal – Gets transaction type of current transaction. Intended for usagefor
GetTxnDataByInv and GetTxnDataByOrder methods.
TxnType has one of following values:
0 – undefined
1 – Purchase
2 – Refund
3 – Void


get_EntryMode(BYTE* pVal), property EntryMode
[OUT] pVal – Get type of the card that was used to perform transaction. Length - three
bytes.
EntryMode has one of following values:
0 – undefined
1 – Magnetic stripe card
2 – EMV chip card
3 – Contactless chip card
4 – Contactless stripe card
5 – Fallback (magnetic stripe was used by card that has EMV chip)
6 – Manual (card number was entered manually)
*/