package main;

import com.healthmarketscience.jackcess.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {
	public static void main(final String[] args){
		Table bills;
		Table billsdetails;
		//File file = new File("d:\\shop-k\\data.mdb");
		try {
			File file = new File("../shop-k/data.mdb");
			Database db = DatabaseBuilder.open(file);
			bills = db.getTable("bills");
			billsdetails = db.getTable("billsdetails");
			SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
			Date curdate = new Date();
			for (Row row : bills) {
				//formatDT.format(row.get("Дата"))
				Date dt = (Date) row.get("Дата");
				//Date dt = (Date) row.get("Status");
				//if (!fmt.format(dt).equals(fmt.format(curdate))) continue;
				//if (!row.get("Status").toString().equals("1")) continue;
				if (!row.get("CheckSum").toString().equals("0.0")) continue;
				//create check
				//insert check details
				String str = "";
				for (Column column : bills.getColumns()) {
					String columnName = column.getName();
					Object value = row.get(columnName);
					str += value + " | ";
				}
				//System.out.println("Number: " + row.get("Number") + "|" + row.get("Дата"));
				System.out.println(str);
				Object id = row.get("Number");
				//Row rd = CursorBuilder.findRowByPrimaryKey(billsdetails, row.get("Number"));
				//IndexCursor cursor = CursorBuilder.createCursor(billsdetails.getPrimaryKeyIndex());
				IndexCursor cursor = CursorBuilder.createCursor(billsdetails.getIndex("Number"));
				for (Row rd : cursor.newEntryIterable(id)) {
					System.out.println(""+rd.get("Number") + " | " + rd.get("Goods") + " | " + rd.get("Quantity") + " | " + rd.get("Price"));
				}
				System.out.println("**********************************************");
			}
			db.flush();
			db.close();
		} catch (IOException ex) {
			Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
		}
	}	
}
