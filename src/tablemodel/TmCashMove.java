package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;
import reports.ReportSale;

public class TmCashMove extends AbstractTableModel{
    private final int colnum = 5;
    private int rownum;
    private final String[] colNames = {
        "MoveID","Дата и время","Сумма","Описание","Пользователь"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCashMove(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getBigDecimal("MoveID").setScale(4, RoundingMode.HALF_UP),
                    rs.getString("DT_create"), 
                    rs.getBigDecimal("Summa").setScale(2, RoundingMode.HALF_UP),
                    rs.getString("Notes"),
                    rs.getInt("UserID"),
                };
                ResultSets.add(row);
            }
        } catch (Exception e) {
			//System.out.println("Exception in TableModel: " + this.getClass().getName());
            //DialogBoxs.viewError(e);
			MyUtil.errorToLog(this.getClass().getName(), e);
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
        }else if(columnindex == 3){ //article and name
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        }else if(columnindex == 4){ //userID
            if (row[columnindex] == null) return 0;
			res = row[columnindex].toString();
        }else if(columnindex == 2) { //summa
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
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
