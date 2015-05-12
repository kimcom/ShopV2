package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmStickerList extends AbstractTableModel{
    private final int colnum = 5;
    private final String[] colNames = {
        "Док №","Дата","Примечание","К-во стикеров","К-во планок"
    };
    private ArrayList<Object[]> ResultSets;

    public TmStickerList(ResultSet rs) {
		try {
			ResultSets = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = {
                    rs.getBigDecimal("DocID").setScale(4, RoundingMode.HALF_UP),
					rs.getString("DT_create"),
					rs.getString("Notes"),
                    rs.getInt("Qty_sticker"),
                    rs.getInt("Qty_plank"),
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
        } else if (columnindex == 1 || columnindex == 2) { //string
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        } else if (columnindex == 3 || columnindex == 4) { //int
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
