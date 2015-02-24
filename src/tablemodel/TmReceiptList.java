package tablemodel;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmReceiptList extends AbstractTableModel{
    private final int colnum = 5;
    private int rownum;
    private final String[] colNames = {
        "Док №","Дата","Поставщик","К-во товаров","Примечание"
    };
    private ArrayList<Object[]> ResultSets;

    public TmReceiptList(ResultSet rs) {
		try {
			ResultSets = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = {
					rs.getString("DocID"), 
					rs.getString("DT"),
					rs.getString("PartnerName"),
					rs.getInt("CountGood"),
					rs.getString("Notes"),
				};
				ResultSets.add(row);
			}
		} catch (Exception ex) {
			//System.out.println(this.getClass().getName().toString() + " " + ex.toString());
			//Logger.getLogger(TmPriceOverList.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
		if (columnindex == 3) { //id
            if (row[columnindex] == null) return 0;
			res = row[columnindex].toString();
		} else {
			if (row[columnindex] == null) return "";
			res = row[columnindex].toString();
		}
        return res;
    }
    @Override
    public int getRowCount() {
        return ResultSets.size();
    }
    @Override
    public int getColumnCount() {
        return colnum;
    }
    @Override
    public String getColumnName(int param) {
        return colNames[param];
    }
}
