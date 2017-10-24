package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmStickerGoods extends AbstractTableModel{
    private final int colnum = 8;
    private int rownum;
    private final String[] colNames = {
        "GoodID", "Производитель", "Артикул", "Название", "Тип ценника", "Старая цена", "Цена", "Остаток"
    };
    private ArrayList<Object[]> ResultSets;

    public TmStickerGoods(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("GoodID"),
					rs.getString("Producer"),
					rs.getString("Article"), 
                    rs.getString("Name"),
					rs.getString("StickerType"),
                    rs.getBigDecimal("PriceOld").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("PriceShop").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Balance").setScale(0, RoundingMode.HALF_UP),
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
		if (columnindex == 0) { //id
			if (row[columnindex] == null) return 0;
			res = row[columnindex].toString();
		} else if (columnindex >= 1 && columnindex <= 4) { //article and name
			if (row[columnindex] == null) return "";
			res = row[columnindex].toString();
		} else if (columnindex == 5) { //кво
			if (row[columnindex] == null) return "";
			BigDecimal bd = (BigDecimal) row[columnindex];
			if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
			res = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
		} else if (columnindex == 6) { //цена
			if (row[columnindex] == null) return "";
			BigDecimal bd = (BigDecimal) row[columnindex];
			if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
			res = bd.setScale(2, RoundingMode.HALF_UP).toPlainString();
		} else { //остаток
			if (row[columnindex] == null) return "";
			BigDecimal bd = (BigDecimal) row[columnindex];
			if (bd.compareTo(BigDecimal.ZERO) == 0) return "";
			DecimalFormat formatter = new DecimalFormat("#0.###");
			res = formatter.format(bd);
			//res = bd.setScale(0, RoundingMode.HALF_UP).toPlainString();
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
