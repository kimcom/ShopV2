package main;

import db.ConnectionDb;
import forms.FrmAdmin;
import forms.FrmMain;
import java.awt.Dialog;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Node;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class EKKA {
	private final ConfigReader conf;
	private final ConnectionDb cnn;
	private final JFrame parent;

	public EKKA(JFrame parent) {
		this.conf = ConfigReader.getInstance();
		this.cnn = ConnectionDb.getInstance();
		this.parent = parent;
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
		boolean pos = true;
		if (typePay.equals("2") && returnIDFiscalNumber==null) { //если безнал
			if (!conf.POS_ACTIVE.equals("0")){
				POS_Terminal t = new POS_Terminal();
				//FrmMessage t = new FrmMessage();
				if (!t.load()) {
					//JOptionPane.showMessageDialog(new JFrame(), "Ошибка регистрации библиотеки!\nРабота с POS-терминалом невозможна!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if (!t.checkStatus()) {
					JOptionPane.showMessageDialog(new JFrame(), "Ошибка подключения к POS-терминалу!\n\n"
							+ "Номер ошибки: "+t.lastErrorCode+"\nОписание: "+t.lastErrorDescription + "\n\n"
							+ "1. Проверте подключение терминала к компьютеру (USB кабель).\n"
							+ "2. Проверте питание терминала (кабель к розетке).\n"
							+ "3. Проверте в каком режиме терминал:\n"
							+ "     нажмите красную кнопку несколько раз,\n"
							+ "     затем повторите печать чека."
							, "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					int i = JOptionPane.showOptionDialog(null, "Вы можете продолжить работу\n"
							+ "с POS-терминалом в РУЧНОМ режиме!\n\n"
							+ "ВНИМАНИЕ!\n"
							+ "Автоматический режим снова будет\n"
							+ "активирован после перезапуска программы!\n\n"
							+ "Активировать РУЧНОЙ режим?"
							, "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
					if (i == 0) {
						conf.POS_ACTIVE = "0";
//						JOptionPane.showMessageDialog(new JFrame(), "РУЧНОЙ режим работы с POS-терминалом\n"
//								+ "успешно активирован!\n\n"
//								+ "Для печати чека на регистраторе - снова нажмите 'Печать'!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
						//return true;
					}else{
						return false;
					}
				}
				pos = t.purchase();
			}
		}
		if (!pos) return false;
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
	public boolean terminalCheck(BigDecimal checkID, String typePay, BigDecimal returnIDFiscalNumber){
//		try {
//			Process proc = Runtime.getRuntime().exec("calc.exe");
//			parent.setEnabled(false);
//			int res = proc.waitFor();
//			parent.setEnabled(true);
//			if(res!=0) return false;
//		} catch (IOException ex) {
//			Logger.getLogger(EKKA.class.getName()).log(Level.SEVERE, null, ex);
//		} catch (InterruptedException ex) {
//			Logger.getLogger(EKKA.class.getName()).log(Level.SEVERE, null, ex);
//		}
//		if(1==1) return false;

		boolean pos = false;
		if (typePay.equals("2")) { //если безнал
			if (!conf.POS_ACTIVE.equals("0")){
				//MyUtil.messageToLog(getClass().getName(), "test 1");
				POS_Terminal t = new POS_Terminal();
				//MyUtil.messageToLog(getClass().getName(), "test 2");
				if (!t.load()) {
					//JOptionPane.showMessageDialog(new JFrame(), "Ошибка регистрации библиотеки!\nРабота с POS-терминалом невозможна!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				//MyUtil.messageToLog(getClass().getName(), "test 3");
				if (!t.checkStatus()) {
					JOptionPane.showMessageDialog(new JFrame(), "Ошибка подключения к POS-терминалу!\n\n"
							+ "Номер ошибки: "+t.lastErrorCode+"\nОписание: "+t.lastErrorDescription + "\n\n"
							+ "1. Проверте подключение терминала к компьютеру (USB кабель).\n"
							+ "2. Проверте питание терминала (кабель к розетке).\n"
							+ "3. Проверте в каком режиме терминал:\n"
							+ "     нажмите красную кнопку несколько раз,\n"
							+ "     затем повторите печать чека."
							, "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					int i = JOptionPane.showOptionDialog(null, "Вы можете продолжить работу\n"
							+ "с POS-терминалом в РУЧНОМ режиме!\n\n"
							+ "ВНИМАНИЕ!\n"
							+ "Автоматический режим снова будет\n"
							+ "активирован после перезапуска программы!\n\n"
							+ "Активировать РУЧНОЙ режим?"
							, "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
					if (i == 0) {
						conf.POS_ACTIVE = "0";
//						JOptionPane.showMessageDialog(new JFrame(), "РУЧНОЙ режим работы с POS-терминалом\n"
//								+ "успешно активирован!\n\n"
//								+ "Для печати чека на регистраторе - снова нажмите 'Печать'!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
						//return true;
					}else{
						return false;
					}
				} else {
					pos = t.purchase();
				}
			}
		}
//		if (!pos) return false;
//		return true;
		return pos;
	}
}
