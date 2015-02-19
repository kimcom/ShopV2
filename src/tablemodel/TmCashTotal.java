package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmCashTotal extends AbstractTableModel{
    private final int colnum = 4;
    private int rownum;
    private final String[] colNames = {
        " ","Отдел 1","Отдел 2","Отдел 3"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCashTotal(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getString("Notes"), 
                    rs.getBigDecimal("Division1").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Division2").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Division3").setScale(2, RoundingMode.HALF_UP),
                };
				ResultSets.add(row);
            }
        } catch (Exception e) {
			//System.out.println("Exception in TableModel: " + this.getClass().getName());
			MyUtil.errorToLog(this.getClass().getName(), e);
            //DialogBoxs.viewError(e);
        }
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
        if(columnindex == 0){ //notes
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        } else { //summa division 1,2,3
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
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
