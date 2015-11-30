package main;

import db.ConnectionDb;
import java.math.BigDecimal;

public class EKKA {
	private final ConfigReader conf;
	private ConnectionDb cnn;

	public EKKA() {
		this.conf = ConfigReader.getInstance();
		this.cnn = ConnectionDb.getInstance();
	}

	public void report(String reportType){
		if (conf.EKKA_NAME.equals("MINIFP")) {
			EKKA_MINIFP me = new EKKA_MINIFP();
			me.report(reportType);
		}
		if (conf.EKKA_NAME.equals("MGN707TS")) {
			EKKA_MGN707TS me = new EKKA_MGN707TS();
			me.report(reportType);
		}
	}
	public void in(String summa){
		if (conf.EKKA_NAME.equals("MINIFP")) {
			EKKA_MINIFP me = new EKKA_MINIFP();
			me.in(summa);
		}
		if (conf.EKKA_NAME.equals("MGN707TS")) {
			EKKA_MGN707TS me = new EKKA_MGN707TS();
			me.in(summa);
		}
	}
	public void out(String summa){
		if (conf.EKKA_NAME.equals("MINIFP")) {
			EKKA_MINIFP me = new EKKA_MINIFP();
			me.out(summa);
		}
		if (conf.EKKA_NAME.equals("MGN707TS")) {
			EKKA_MGN707TS me = new EKKA_MGN707TS();
			me.out(summa);
		}
	}
	public void nullCheck(){
		if (conf.EKKA_NAME.equals("MINIFP")) {
			EKKA_MINIFP me = new EKKA_MINIFP();
			me.nullCheck();
		}
		if (conf.EKKA_NAME.equals("MGN707TS")) {
			EKKA_MGN707TS me = new EKKA_MGN707TS();
			me.nullCheck();
		}
	}
	public void copyCheck(){
		if (conf.EKKA_NAME.equals("MINIFP")) {
			EKKA_MINIFP me = new EKKA_MINIFP();
			me.copyCheck();
		}
		if (conf.EKKA_NAME.equals("MGN707TS")) {
			EKKA_MGN707TS me = new EKKA_MGN707TS();
			me.copyCheck();
		}
	}
	public boolean printCheck(BigDecimal checkID, String typePay, BigDecimal returnIDFiscalNumber){
		if (conf.EKKA_NAME.equals("MINIFP")){
			EKKA_MINIFP me = new EKKA_MINIFP();
			return me.printCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber);
		}
		if (conf.EKKA_NAME.equals("MGN707TS")){
			EKKA_MGN707TS me = new EKKA_MGN707TS();
			return me.printCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber);
		}
		return false;
	}
}
