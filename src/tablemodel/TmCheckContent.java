package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.DialogBoxs;
import main.MyUtil;

public class TmCheckContent extends AbstractTableModel{
    private final int colnum = 11;
    private int rownum;
    private final String[] colNames = {
        "ID","Артикул","Название","Акция","Кол-во","Прайс","%","Скидка","Цена","Сумма","Продавец"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCheckContent(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("GoodID"),
                    rs.getString("Article"), 
                    rs.getString("Name"),
                    rs.getString("PromoName"),
                    rs.getBigDecimal("Quantity").setScale(3, RoundingMode.HALF_UP),
                    rs.getBigDecimal("PriceBase").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("DiscountPercent").setScale(3, RoundingMode.HALF_UP),
                    rs.getBigDecimal("PriceDiscount").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP),
                    rs.getString("SellerName"),
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
        }else if(columnindex == 1 || columnindex == 2 || columnindex == 3 || columnindex == 10){ //article and name and promoName
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        } else if (columnindex == 4 || columnindex == 6) { //quantity, discount
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(3, RoundingMode.HALF_UP).toPlainString();
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
