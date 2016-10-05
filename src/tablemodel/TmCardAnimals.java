package tablemodel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import main.MyUtil;

public class TmCardAnimals extends AbstractTableModel{
    private final int colnum = 3;
    private int rownum;
    private final String[] colNames = {
        "AnimalID","Вид животного","Порода"
    };
    private ArrayList<Object[]> ResultSets;

    public TmCardAnimals(ResultSet rs) {
        ResultSets = new ArrayList<Object[]>();
        try {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("AnimalID"), 
                    rs.getString("TypeAnimal"), 
                    rs.getString("Breed"), 
                };
				ResultSets.add(row);
            }
        } catch (Exception e) {
			//System.out.println("Exception in TableModel: " + this.getClass().getName());
			MyUtil.errorToLog(this.getClass().getName(), e);
            //DialogBoxs.viewError(e);
        }
    }
    @Override
    public Object getValueAt(int rowindex, int columnindex) {
        Object res;
        Object[] row = ResultSets.get(rowindex);
        if(columnindex == 1 || columnindex == 2){ //notes
            if (row[columnindex] == null) return "";
            res = row[columnindex].toString();
        } else {
            if (row[columnindex] == null) return 0;
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
