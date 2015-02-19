package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmPromoList extends AbstractTableModel{
    private final int colnum = 2;
    private int rownum;
    private final String[] colNames = {
        "№ акции","Название","Описание","Старт","Стоп","Кол-во"
    };
    private ArrayList<Object[]> ResultSets;

    public TmPromoList(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("PromoID"),
                    rs.getString("Name"),
//                    rs.getString("Description"),
//                    rs.getString("DT_start"),
//                    rs.getString("DT_stop"),
//                    rs.getBigDecimal("QuantityPromo"),
                };
                ResultSets.add(row);
            }
        } catch (Exception e) {
//            System.out.println("Exception in TableModel: "+this.getClass().getName());
//			System.out.println(e.getMessage());
			MyUtil.errorToLog(this.getClass().getName(), e);
        }
    }

    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
        if (columnindex == 0) {
            res = "";
            if (row[columnindex] == null) return res;
            res = row[columnindex];
        } else if (columnindex == 1 || columnindex == 2) {
            res = "";
            if (row[columnindex] == null) return res;
            res = row[columnindex].toString();
        }else if(columnindex == 3 || columnindex == 4) {
            res = "";
            if (row[columnindex] == null) return res;
			res = row[columnindex].toString();
        }else{
            res = "";
            if (row[columnindex] == null) return res;
			BigDecimal bd = new BigDecimal(row[columnindex].toString());
			res = bd.setScale(2, RoundingMode.HALF_UP);
//            DecimalFormat formatter = new DecimalFormat("#0.00");
//            if(((Double) row[columnindex]).doubleValue() == 0){
//                res = (Object) "";
//            }else{
//                res = (Object) formatter.format(row[columnindex]);
//            }
        }
        return res;
    }
	public Object getValueAtForClass(int rowindex, int columnindex) {
		Object res = (Integer) 0;
		if(rowindex < getRowCount() && columnindex < getColumnCount()){
//			System.out.println("rowindex:" + Integer.toString(rowindex) + "	columnindex:" + Integer.toString(columnindex));
			Object[] row = ResultSets.get(rowindex);
			res = row[columnindex];
		}
		return res;
	}

	@Override
	public Class getColumnClass(int column) {
		Class returnValue = null;
//		if ((column >= 0) && (column < getColumnCount())) {
		returnValue = getValueAtForClass(0, column).getClass();
//		} else {
//			returnValue = Object.class;
//		}
		return returnValue;
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
