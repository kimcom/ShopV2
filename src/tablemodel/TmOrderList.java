package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmOrderList extends AbstractTableModel{
    private final int colnum = 6;
    private int rownum;
    private final String[] colNames = {
        "Док №","Дата","Примечание","Статус","К-во товаров","Сумма"
    };
    private ArrayList<Object[]> ResultSets;

    public TmOrderList(ResultSet rs) {
		try {
			ResultSets = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = {
                    rs.getBigDecimal("CheckID").setScale(4, RoundingMode.HALF_UP),
					rs.getString("CreateDateTime"),
					rs.getString("Notes"),
                    rs.getString("CheckStatus"),
                    rs.getInt("CountGood"),
                    rs.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP),
                };
				ResultSets.add(row);
			}
		} catch (Exception ex) {
			//System.out.println(this.getClass().getName().toString() + " " + ex.toString());
			//Logger.getLogger(TmOrderList.class.getName()).log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
		if (columnindex == 0) { //id
            if (row[columnindex] == null) return BigDecimal.ZERO;
            BigDecimal bd = (BigDecimal) row[columnindex];
            res = bd.setScale(4, RoundingMode.HALF_UP);
        } else if (columnindex == 1 || columnindex == 2 || columnindex == 3) { //string
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        } else if (columnindex == 5) { //summa
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
        } else if (columnindex == 4) { //int
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
