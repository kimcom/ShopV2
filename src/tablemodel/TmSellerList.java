package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmSellerList extends AbstractTableModel{
    private final int colnum = 3;
    private int rownum;
    private final String[] colNames = {
        "№","ФИО","Должность"
    };
    private ArrayList<Object[]> ResultSets;

    public TmSellerList(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("SellerID"),
                    rs.getString("Name"),
                    rs.getString("Post"),
                };
                ResultSets.add(row);
            }
        } catch (Exception e) {
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
        }else{
			res = "";
			if (row[columnindex] == null) return res;
			res = row[columnindex].toString();
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
