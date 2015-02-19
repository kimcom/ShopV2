package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmCheckList extends AbstractTableModel{
    private final int colnum = 7;
    private int rownum;
    private final String[] colNames = {
        "Чек №","Дата и время","Статус","Оплата","Сумма б/ск.","Скидка","Сумма"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCheckList(ResultSet rs) {
		try {
			ResultSets = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = {
					rs.getBigDecimal("CheckID").setScale(4, RoundingMode.HALF_UP),
					rs.getString("CreateDateTime"), 
					rs.getString("CheckStatus"),
					rs.getString("TypePayment"),
					rs.getBigDecimal("SumBase").setScale(2, RoundingMode.HALF_UP),
					rs.getBigDecimal("SumDiscount").setScale(2, RoundingMode.HALF_UP),
					rs.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP),
				};
				ResultSets.add(row);
			}
		} catch (Exception ex) {
            //System.out.println(ex.toString());
			//Logger.getLogger(TmCheckList.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
			//ex.getStackTrace();
		}
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
        if (columnindex == 0) { //id
            if (row[columnindex] == null) return BigDecimal.ZERO;
			BigDecimal bd = (BigDecimal) row[columnindex];
			res = bd.setScale(4,RoundingMode.HALF_UP);
        }else if(columnindex == 2 || columnindex == 3){ //article and name
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        }else if(columnindex == 1){ //date
            if (row[columnindex] == null) return "";
			res = row[columnindex].toString();
        }else if(columnindex == 4 || columnindex == 5 || columnindex == 6) { //summa
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(2,RoundingMode.HALF_UP).toPlainString();
        }else{
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
