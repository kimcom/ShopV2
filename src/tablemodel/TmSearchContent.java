package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmSearchContent extends AbstractTableModel{
    private final int colnum = 7;
    private int rownum;
    private final String[] colNames = {
        "ID","Артикул","Название","Отдел","Прайс","MAX%","Остаток"
    };
    private ArrayList<Object[]> ResultSets;

    public TmSearchContent(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("GoodID"),
                    rs.getString("Article"), 
                    rs.getString("Name"),
                    rs.getInt("Division"),
                    rs.getDouble("PriceShop"),
                    rs.getDouble("DiscountMax"),
                    rs.getDouble("BalanceStop"),
                };
                ResultSets.add(row);
            }
        } catch (Exception e) {
            //System.out.println("Exception in TableModel: "+this.getClass().getName());
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
        }else if(columnindex < 3){
            res = "";
            if (row[columnindex] == null) return res;
            res = row[columnindex].toString();
        }else if(columnindex == 3) {
            res = "";
            if (row[columnindex] == null) return res;
            if (((Integer) row[columnindex]) == 0) return res;
			res = ((Integer) row[columnindex]).toString();
        }else if(columnindex == 4) {
			res = "";
			if (row[columnindex] == null) return res;
			BigDecimal bd = new BigDecimal(row[columnindex].toString());
			res = bd.setScale(2, RoundingMode.HALF_UP);
        }else if(columnindex == 5) {
			res = "";
			if (row[columnindex] == null) return res;
			BigDecimal bd = new BigDecimal(row[columnindex].toString());
			res = bd.setScale(0, RoundingMode.HALF_UP);
        }else if(columnindex == 6) {
			res = "";
			if (row[columnindex] == null) return res;
			BigDecimal bd = new BigDecimal(row[columnindex].toString());
			if(bd.compareTo(BigDecimal.ZERO)==0) {
				res = "";
			}else{
				res = bd.setScale(0, RoundingMode.HALF_UP);
			}
        }else{
			res = "";
			if (row[columnindex] == null) return res;
			res = row[columnindex].toString();
//			BigDecimal bd = new BigDecimal(row[columnindex].toString());
//			res = bd.setScale(2, RoundingMode.HALF_UP);
        }
        return res;
    }
	public Object getValueAtForClass(int rowindex, int columnindex) {
		//Object res = (Integer) 0;
		Object res = (String) "";
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
