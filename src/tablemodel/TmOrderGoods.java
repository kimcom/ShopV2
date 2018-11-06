package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.DialogBoxs;
import main.MyUtil;

public class TmOrderGoods extends AbstractTableModel{
    private final int colnum = 8;
    private int rownum;
    private final String[] colNames = {
        "GoodID","Артикул","Название","Отдел","Мин.ост.","Цена","Продано","Остаток"
    };
    private ArrayList<Object[]> ResultSets;

    public TmOrderGoods(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("GoodID"),
                    rs.getString("Article"), 
                    rs.getString("Name"),
                    rs.getInt("Division"),
                    rs.getInt("BalanceMin"),
                    rs.getBigDecimal("PriceShop").setScale(2, RoundingMode.HALF_UP),
                    rs.getInt("QuantitySale"),
                    rs.getInt("Balance"),
                };
                ResultSets.add(row);
            }
        } catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
            //System.out.println(e.toString());
            //DialogBoxs.viewError(e);
        }
    }

	public int getRowByID(int id) {
		Object[] row;
		for(int i=0; i < ResultSets.size(); i++){
			row = ResultSets.get(i);
			//DialogBoxs.viewMessage("id: " + row[0].toString());
			if(Integer.parseInt(row[0].toString()) == id)
				return i;
		}
		return 0;
	}
	
	@Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
        if (columnindex == 0) { //goodid division
            if (row[columnindex] == null) return 0;
            res = row[columnindex].toString();
        }else if(columnindex == 1 || columnindex == 2){ //article and name
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        }else if(columnindex == 3 || columnindex == 4 || columnindex == 6 || columnindex == 7){ //qty sale
            if (row[columnindex] == null || row[columnindex].toString().equals("0")) return "";
            res = row[columnindex].toString();
//        } else if (columnindex == 4) { //DiscountMax
//            if (row[columnindex] == null) return "";
//            Double bd = (Double) row[columnindex];
//            if (bd.doubleValue() == 0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
//        } else if (columnindex == 6) { //Balance
//            if (row[columnindex] == null) return "";
//            BigDecimal bd = (BigDecimal) row[columnindex];
//            if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
        } else if (columnindex == 5) { //price
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
            res = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }else{
            if (row[columnindex] == null) return "";
	            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
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
