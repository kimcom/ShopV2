package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmOrderContent extends AbstractTableModel{
    private final int colnum = 7;
    private int rownum;
    private final String[] colNames = {
        "ID", "Артикул", "Название", "Отдел", "Кол-во", "Цена", "Сумма"
//        "ID","Артикул","Название","Отдел","Остаток","Новая цена","Старая цена"
    };
    private ArrayList<Object[]> ResultSets;

    public TmOrderContent(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("GoodID"),
                    rs.getString("Article"), 
                    rs.getString("Name"),
                    rs.getInt("Division"),
                    rs.getBigDecimal("Quantity").setScale(3, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP),
                };
                ResultSets.add(row);
            }
        } catch (Exception ex) {
            //System.out.println(this.getClass().getName().toString()+" "+ex.toString());
			MyUtil.errorToLog(this.getClass().getName(), ex);
            //DialogBoxs.viewError(e);
        }
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
        //"ID", "Артикул", "Название", "Отдел", "Кол-во", "Цена", "Сумма"
        if (columnindex == 0 || columnindex == 3) { //id
            if (row[columnindex] == null) return 0;
            res = row[columnindex].toString();
        } else if (columnindex == 1 || columnindex == 2) { //article and name
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        } else if (columnindex == 4) { //quantity
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(3, RoundingMode.HALF_UP).toPlainString();
        } else {
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
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
