package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmCardsList extends AbstractTableModel{
    private final int colnum = 5;
    private int rownum;
    private final String[] colNames = {
        "<html>№ карты</html>","<html>ФИО</html>","<html>Дата выдачи<br>Дата анулир.</html>","<html>% скидки<br>Сумма накоп.</html>","ID"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCardsList(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
		String strIDCard = "";
		String strParentCardID = "";
		String strFamily = "";
		String strName = "";
		String strMiddleName = "";
		String strDateOfIssue = "";
		String strDateOfCancellation = "";
		String strAmountOfBuying = "";
		String strPercentOfDiscount = "";
		
        try {
            while (rs.next()) {
				//System.out.println("data:"+("<html>") + (strPercentOfDiscount) + ("<br>") + (strAmountOfBuying) + ("</html>")+"|");
				strIDCard				= rs.getString("IDCard");
				strParentCardID			= rs.getString("ParentCardID");
				strFamily				= rs.getString("Family");
				strName					= rs.getString("Name");
				strMiddleName			= rs.getString("MiddleName");
				strDateOfIssue			= rs.getString("DateOfIssue");
				strDateOfCancellation	= rs.getString("DateOfCancellation");
				strPercentOfDiscount	= rs.getString("PercentOfDiscount").trim();
				strAmountOfBuying		= rs.getString("AmountOfBuying").trim();
                Object[] row = {
                    ("<html>") + (strIDCard) + ("<br>") + (strParentCardID) + ("</html>"),
                    ("<html>") + (strFamily) + ("<br>") + (strName) + (" ") + (strMiddleName) + ("</html>"),
                    ("<html>") + (strDateOfIssue)+("<br>")+(strDateOfCancellation)+("</html>"),
					("<html>") + (strPercentOfDiscount) + ("<br>") + (strAmountOfBuying) + ("</html>"),
					strIDCard,
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
//        if (columnindex == 0) {
//            res = "";
//            if (row[columnindex] == null) return res;
//            res = row[columnindex];
//        } else if (columnindex == 1 || columnindex == 2) {
//            res = "";
//            if (row[columnindex] == null) return res;
//            res = row[columnindex].toString();
//        }else{
			res = "";
			if (row[columnindex] == null) return res;
			res = row[columnindex].toString();
//        }
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
