package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmCheckList extends AbstractTableModel{
	protected Vector dataVector;
    private final int colnum = 7;
    private int rownum;
    private final String[] colNames = {
        "Чек №","Дата и время","Статус","Оплата","Сумма б/ск.","Скидка","Сумма"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCheckList(ResultSet rs) {
		try {
			ResultSets = new ArrayList<Object[]>();
			while (rs.next()) {
				Object[] row = {
					rs.getBigDecimal("CheckID").setScale(4, RoundingMode.HALF_UP),
					rs.getString("CreateDateTime"), 
					rs.getString("CheckStatus"),
					rs.getString("TypePayment"),
					rs.getBigDecimal("SumBase").setScale(2, RoundingMode.HALF_UP),
					rs.getBigDecimal("SumDiscount").setScale(2, RoundingMode.HALF_UP),
					rs.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP),
				};
				ResultSets.add(row);
			}
		} catch (Exception ex) {
            //System.out.println(ex.toString());
			//Logger.getLogger(TmCheckList.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
			//ex.getStackTrace();
		}
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
		//System.out.println("rowindex="+rowindex+" columnindex="+columnindex+" row[columnindex]="+row[columnindex]);
        if (columnindex == 0) { //id
            if (row[columnindex] == null) return BigDecimal.ZERO;
			BigDecimal bd = (BigDecimal) row[columnindex];
			res = bd.setScale(4,RoundingMode.HALF_UP);
        }else if(columnindex == 2 || columnindex == 3){ //article and name
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        }else if(columnindex == 1){ //date
            if (row[columnindex] == null) return "";
			res = row[columnindex].toString();
        }else if(columnindex == 4 || columnindex == 5 || columnindex == 6) { //summa
            if (row[columnindex] == null) return "";
            BigDecimal bd = (BigDecimal) row[columnindex];
            if (bd.compareTo(BigDecimal.ZERO)==0) return "";
//            DecimalFormat formatter = new DecimalFormat("#0.###");
//            res = formatter.format(bd).toString();
            res = bd.setScale(2,RoundingMode.HALF_UP).toPlainString();
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
	
	/**
	 * Adds a row to the end of the model. The new row will contain <code>null</code> values unless <code>rowData</code>
	 * is specified. Notification of the row being added will be generated.
	 *
	 * @param rowData optional data of the row being added
	 */
	public void addRow(Vector rowData) {
		insertRow(getRowCount(), rowData);
	}

	/**
	 * Adds a row to the end of the model. The new row will contain <code>null</code> values unless <code>rowData</code>
	 * is specified. Notification of the row being added will be generated.
	 *
	 * @param rowData optional data of the row being added
	 */
	public void addRow(Object[] rowData) {
		ResultSets.add(rowData);
		//addRow(convertToVector(rowData));
	}

	/**
	 * Inserts a row at <code>row</code> in the model. The new row will contain <code>null</code> values unless
	 * <code>rowData</code> is specified. Notification of the row being added will be generated.
	 *
	 * @param row the row index of the row to be inserted
	 * @param rowData optional data of the row being added
	 * @exception ArrayIndexOutOfBoundsException if the row was invalid
	 */
	public void insertRow(int row, Vector rowData) {
		dataVector.insertElementAt(rowData, row);
		justifyRows(row, row + 1);
		fireTableRowsInserted(row, row);
	}

	/**
	 * Inserts a row at <code>row</code> in the model. The new row will contain <code>null</code> values unless
	 * <code>rowData</code> is specified. Notification of the row being added will be generated.
	 *
	 * @param row the row index of the row to be inserted
	 * @param rowData optional data of the row being added
	 * @exception ArrayIndexOutOfBoundsException if the row was invalid
	 */
	public void insertRow(int row, Object[] rowData) {
		insertRow(row, convertToVector(rowData));
	}

	private void justifyRows(int from, int to) {
		// Sometimes the DefaultTableModel is subclassed
		// instead of the AbstractTableModel by mistake.
		// Set the number of rows for the case when getRowCount
		// is overridden.
		dataVector.setSize(getRowCount());

		for (int i = from; i < to; i++) {
			if (dataVector.elementAt(i) == null) {
				dataVector.setElementAt(new Vector(), i);
			}
			((Vector) dataVector.elementAt(i)).setSize(getColumnCount());
		}
	}

	protected static Vector convertToVector(Object[] anArray) {
		if (anArray == null) {
			return null;
		}
		Vector<Object> v = new Vector<Object>(anArray.length);
		for (Object o : anArray) {
			v.addElement(o);
		}
		return v;
	}
}
