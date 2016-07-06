package main;
import db.ConnectionDb;
import com.jacob.activeX.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EKKA_MINIFP {
	private ActiveXComponent ecr = new ActiveXComponent("ecrmini.t400");
	private ConfigReader conf = ConfigReader.getInstance();
	private ConnectionDb cnn  = ConnectionDb.getInstance();
	public boolean blStatusPrintButton = false;
	public boolean blStatusPrinted = false;
	private BigDecimal currentCheckID;
	private String currentDT = "";
	private String last_result;
	private String[] last_result_array;

//			wstr = "read_fm_table;1;4;";//Налоговый номер надо проверять если будут фискализировать!!!
//			System.out.println(wstr + ":" + ecr.invoke("t400me", wstr).toString());
//			wstr = "get_date_time;";//Проверка времени
//			System.out.println(wstr + ":" + ecr.invoke("t400me", wstr).toString());
//			wstr = "execute_report;z1;12321;";//z-отчет
//			System.out.println(wstr + ":" + ecr.invoke("t400me", wstr).toString());
	private boolean sendCommand(String wstr){
		boolean bl = ecr.invoke("t400me", wstr).getBoolean();
		last_result = "";
		if(!bl){
			int error = ecr.invoke("get_last_error").getInt();
			if(error>0){
				String error_info = "Send command: "+wstr+"\n\nError number:"+Integer.toString(error)+"\nError message:"+ecr.invoke("get_error_info").toString();
				MyUtil.errorToLog(this.getClass().getName(), error_info);
				//DialogBoxs.viewMessageError(error_info);
			}
			return false;
		}else{
			last_result = ecr.invoke("get_last_result").toString();
			last_result_array = last_result.split(";");
			//System.out.println(wstr + ": " + last_result);
			return true;
		}
	}
	
	public void report(String reportType) {
		if (sendCommand("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "")) {
			sendCommand("execute_report;"+reportType+";12321;");
			if (!sendCommand("close_port"))	return;
		}
	}
	public void nullCheck() {
		if (sendCommand("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "")) {
			if (!sendCommand("cashier_registration;1;0")) return;//регистрация кассира
			sendCommand("print_empty_receipt;");
			if (!sendCommand("close_port"))	return;
		}
	}
	public void copyCheck() {
		if (sendCommand("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "")) {
			if (!sendCommand("cashier_registration;1;0")) return;//регистрация кассира
			sendCommand("print_receipt_copy;");
			if (!sendCommand("close_port"))	return;
		}
	}
	public void in(String summa) {
		if (sendCommand("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "")) {
			if (!sendCommand("cashier_registration;1;0")) return;//регистрация кассира
			sendCommand("in_out;0;0;0;0;"+summa+";;;");
			if (!sendCommand("close_port"))	return;
		}
	}
	public void out(String summa) {
		if (sendCommand("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "")) {
			if (!sendCommand("cashier_registration;1;0")) return;//регистрация кассира
			sendCommand("in_out;0;0;0;1;"+summa+";;;");
			if (!sendCommand("close_port"))	return;
		}
	}
	public boolean printCheck(BigDecimal checkID, String typePay, BigDecimal returnIDFiscalNumber) {
		if (cnn == null) return false;
		this.currentCheckID = checkID;
		if (this.currentCheckID == null) this.currentCheckID = cnn.currentCheckID;
//DialogBoxs.viewMessage("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "");
		if (sendCommand("open_port;" + conf.EKKA_PORT + ";" + conf.EKKA_BAUD + "")) {
			//sendCommand("execute_report;z1;12321;");
			if (!sendCommand("cashier_registration;1;0")) return false;//регистрация кассира
			//if (!sendCommand("get_status;0")) return false;//статус чека
			//if(last_result_array[2]=="0") 
			if ( returnIDFiscalNumber == null){
				if (!sendCommand("open_receipt;0;")) return false;//открытие чека
			}else{
//MyUtil.messageToLog("minifp", "open_receipt;1;");
				if (!sendCommand("open_receipt;1;")) return false;//открытие чека
			}
			ResultSet res = cnn.getCheckContent(currentCheckID);
			int y = 0, h;
			try {
				while (res.next()) {
					currentDT		= res.getString("CurrentDT").toString();
					String goodid	= res.getString("GoodID").toString();
					String weight	= Integer.toString(res.getInt("weight"));
					String division	= res.getString("division").toString();
					//int len = 74;
					//if(res.getString("Name").length()<47) len = res.getString("Name").length()-1;
					//String name		= res.getString("Name").toString().substring(0, len);
					String name		= res.getString("Name").toString();
					name			= name.replace(".", " ");
					name			= name.replace(",", " ");
					name			= name.replace(";", " ");
					name			= name.replaceAll("  ", " ");
					String quantity = res.getBigDecimal("Quantity").setScale(3, RoundingMode.HALF_UP).abs().toString();
					String price	= res.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP).abs().toString();
					String sum		= res.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP).abs().toString();
					//System.out.println(""+goodid+"	"+name+"	"+quantity+" "+price+" "+sum);
					//System.out.println("add_plu;" + goodid + ";0;" + weight + ";0;0;0;" + division + ";" + price + ";0;'" + name + "';" + quantity + ";");
//DialogBoxs.viewMessage("add_plu;"+goodid+";0;"+weight+";0;0;0;"+division+";0;0;"+name+";"+quantity+";");
//MyUtil.messageToLog("minifp", "goodid:"+goodid+"	quantity="+quantity+"	price="+price+"	sum="+sum);
//MyUtil.messageToLog("minifp", "add_plu;" + goodid + ";0;" + weight + ";0;0;0;" + division + ";0;0;" + name + ";" + quantity + ";");
					if (!sendCommand("add_plu;"+goodid+";0;"+weight+";0;0;0;"+division+";0;0;"+name+";"+quantity+";")) return false;
//DialogBoxs.viewMessage("sale_plu;0;0;1;"+quantity+";"+goodid+";"+price+"");
//MyUtil.messageToLog("minifp", "sale_plu;0;0;1;" + quantity + ";" + goodid + ";" + price + "");
					if (!sendCommand("sale_plu;0;0;1;"+quantity+";"+goodid+";"+price+"")) return false;
					//res.getBigDecimal("Quantity").setScale(2, RoundingMode.HALF_UP).toString() + " шт. X " + 
					//res.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP).toString()
					//res.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP).toString()+" "
				}
			} catch (SQLException e) {
				MyUtil.errorToLog(this.getClass().getName(), e);
				DialogBoxs.viewError(e);
				return false;
			}
			//show_subtotal
			//cancel_receipt
//MyUtil.messageToLog("minifp", "pay;" + typePay + ";0;");
			if (!sendCommand("pay;"+typePay+";0;"))return false;//закрытие чека "pay;2;0;"-безнал
			if (sendCommand("get_last_receipt_number;")){
				//MyUtil.errorToLog("test EKKA", last_result);
				cnn.setCheckFiscalNumber(last_result);
				//DialogBoxs.viewMessage(last_result);
			}//номер последнего чека
			if (!sendCommand("close_port"))return false;
		} else {
			DialogBoxs.viewMessageError("Нет связи с фиск.регистратором");
			return false;
		}
		return true;
	}
	public EKKA_MINIFP() {
	}
}
