package forms;

import com.healthmarketscience.jackcess.*;
import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import tablemodel.TmCashTotal;
import tablemodel.TmCheckList;

public class FrmOffline extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;
    public boolean blDisposeStatus = false;
	private BigDecimal lastCheckID = BigDecimal.ZERO;

    public FrmOffline() {
        initComponents();
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
		lastCheckID = cnn.currentCheckID;
        setTitle("Данные автономной программы. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
		setLocationRelativeTo(null);
        
		requery();
		requeryListOffline();
		jButtonExit.requestFocus();
	}
    private void requery(){
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        jTableCashTotal.setModel(new TmCashTotal(getCashTotal()));
        jTableCashTotal.setDefaultRenderer(jTableCashTotal.getColumnClass(0), new MyRendererTotal());
        jTableCashTotal.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCashTotal.setRowHeight(17);
        jTableCashTotal.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableCashTotal.getColumnModel().getColumn(1).setPreferredWidth(30);
        jTableCashTotal.getColumnModel().getColumn(2).setPreferredWidth(30);
        jTableCashTotal.getColumnModel().getColumn(3).setPreferredWidth(30);

		jTableCashTotalOffline.setModel(new TmCashTotal(getCashTotalOffline()));
        jTableCashTotalOffline.setDefaultRenderer(jTableCashTotalOffline.getColumnClass(0), new MyRendererTotal());
        jTableCashTotalOffline.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCashTotalOffline.setRowHeight(17);
        jTableCashTotalOffline.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableCashTotalOffline.getColumnModel().getColumn(1).setPreferredWidth(30);
        jTableCashTotalOffline.getColumnModel().getColumn(2).setPreferredWidth(30);
        jTableCashTotalOffline.getColumnModel().getColumn(3).setPreferredWidth(30);

		jTableCheckList.setModel(new TmCheckList(getCheckListRS()));
		jTableCheckList.setDefaultRenderer(jTableCheckList.getColumnClass(0), new MyRendererList());
		//jTableCheckList.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		jTableCheckList.setRowHeight(17);
		jTableCheckList.getColumnModel().getColumn(0).setPreferredWidth(50);
		jTableCheckList.getColumnModel().getColumn(1).setPreferredWidth(100);
		jTableCheckList.getColumnModel().getColumn(2).setPreferredWidth(30);
		jTableCheckList.getColumnModel().getColumn(3).setPreferredWidth(30);
		jTableCheckList.getColumnModel().getColumn(4).setPreferredWidth(30);
		jTableCheckList.getColumnModel().getColumn(5).setPreferredWidth(30);
		jTableCheckList.getColumnModel().getColumn(6).setPreferredWidth(30);

		if (jTableCheckList.getRowCount() > 0) {
			jTableCheckList.setRowSelectionInterval(0, 0);
		}
		
		TmCheckList model = (TmCheckList) jTableCheckList.getModel();
		BigDecimal totalSumW = BigDecimal.ZERO;
		BigDecimal totalSumS = BigDecimal.ZERO;
		BigDecimal totalSum = BigDecimal.ZERO;
		for (int i=0;i<model.getRowCount();i++){
			String val = model.getValueAt(i, 4).toString();
			if (val.equals("")) val = "0";
			totalSumW = totalSumW.add(new BigDecimal(val));
			val = model.getValueAt(i, 5).toString();
			if (val.equals("")) val = "0";
			totalSumS = totalSumS.add(new BigDecimal(val));
			val = model.getValueAt(i, 6).toString();
			if (val.equals("")) val = "0";
			totalSum = totalSum.add(new BigDecimal(val));
		}
		model.addRow(new Object[]{
			null,
			"ИТОГО:",
			null,
			null,
			totalSumW,
			totalSumS,
			totalSum
		});
	}
	private void requeryListOffline(){
		DefaultTableModel model = (DefaultTableModel) new DefaultTableModel();
		model.addColumn("№ чека");
		model.addColumn("Дата");
		model.addColumn("Статус");
		model.addColumn("Оплата");
		model.addColumn("Без скидки");
		model.addColumn("Скидка");
		model.addColumn("Сумма");
		jTableCheckListOffline.setModel(model);
		jTableCheckListOffline.setDefaultRenderer(jTableCheckListOffline.getColumnClass(0), new MyRendererList());
		
		Table bills;
		Table billsdetails;
		try {
			File file = new File("../shop-k/data.mdb");
			Database db = DatabaseBuilder.open(file);
			bills = db.getTable("bills");
			billsdetails = db.getTable("billsdetails");
			SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat fmtS = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date curdate = new Date();
			BigDecimal totalSumW = BigDecimal.ZERO;
			BigDecimal totalSumS = BigDecimal.ZERO;
			BigDecimal totalSum = BigDecimal.ZERO;
			for (Row row : bills) {
				Date dt = (Date) row.get("Дата");
				//System.out.println(""+row.get("Number").toString()+"	"+dt.toString());
				if (!fmt.format(dt).equals(fmt.format(curdate))) continue;
				//if (!row.get("Status").toString().equals("1")) continue;
				if (!row.get("CheckSum").toString().equals("0.0")) {
					continue;
				}
				String status = "не проведен";
				String payment = "нал";
				if (row.get("Status").toString().equals("1")) status = "проведен";
				if (row.get("Payment").toString().equals("true")) payment = "безнал";

				BigDecimal sumW = BigDecimal.ZERO;
				BigDecimal sumS = BigDecimal.ZERO;
				BigDecimal sum = BigDecimal.ZERO;
				Object id = row.get("Number");
				IndexCursor cursor = CursorBuilder.createCursor(billsdetails.getIndex("Number"));
				for (Row rd : cursor.newEntryIterable(id)) {
					BigDecimal quantity = new BigDecimal(rd.get("Quantity").toString());
					BigDecimal pricebase = new BigDecimal(rd.get("PriceBase").toString());
					BigDecimal price = new BigDecimal(rd.get("Price").toString());
					sumW = sumW.add(quantity.multiply(pricebase));
					sum = sum.add(quantity.multiply(price));
					sumS = sumS.add(sumW.subtract(sum));
				}
				model.addRow(new Object[] {
					row.get("Number").toString(), 
					fmtS.format(dt), 
					status, 
					payment, 
					sumW.setScale(2, RoundingMode.HALF_UP).toPlainString(),
					sumS.setScale(2, RoundingMode.HALF_UP).toPlainString(),
					sum.setScale(2, RoundingMode.HALF_UP).toPlainString()
				});
				totalSumW = totalSumW.add(sumW);
				totalSumS = totalSumS.add(sumS);
				totalSum = totalSum.add(sum);
			}
			model.addRow(new Object[]{
				"",
				"ИТОГО:",
				"",
				"",
				totalSumW.setScale(2, RoundingMode.HALF_UP).toPlainString(),
				totalSumS.setScale(2, RoundingMode.HALF_UP).toPlainString(),
				totalSum.setScale(2, RoundingMode.HALF_UP).toPlainString()
			});
			db.flush();
			db.close();
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			DialogBoxs.viewError(ex);
		}
	}
	
	private ResultSet getCheckListRS() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) {
			return null;
		}
		ResultSet rs = cnn.getCheckListByTerminalID(9);
		return rs;
	}
	private void jButtonCheckMoveActionPerformed(){
		int i = JOptionPane.showOptionDialog(this, "Операция переноса чеков\nиз автономной программы\nнеобратима!\n\nВыполнить перенос?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Да", "Нет"}, "Да");
		if (i != 0) return;
		Table bills;
		Table billsdetails;
		try {
			File file = new File("../shop-k/data.mdb");
			Database db = DatabaseBuilder.open(file);
			bills = db.getTable("bills");
			billsdetails = db.getTable("billsdetails");
			SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
			SimpleDateFormat fmtS = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date curdate = new Date();
			for (Row row : bills) {
				Date dt = (Date) row.get("Дата");
				if (!fmt.format(dt).equals(fmt.format(curdate))) continue;
				//if (!row.get("Status").toString().equals("1")) continue;
				if (!row.get("CheckSum").toString().equals("0.0")) continue;
				//create check
		        cnn.newCheck(9);
				row.put("CheckSum", cnn.currentCheckID);
				//System.out.println(row.get("IDCard"));
				if (row.get("IDCard") != null)
					cnn.setCheckDiscountByCard(row.get("IDCard").toString());
				if (row.get("Payment").toString().equals("true"))
					cnn.setCheckPaymentType(1, cnn.currentCheckID); // уст. тип оплаты
				
				Object id = row.get("Number");
				IndexCursor cursor = CursorBuilder.createCursor(billsdetails.getIndex("Number"));
//call pr_check_update('good add', @_id, 'CheckID=80111&BarCode=0123&Quantity=3&PriceBase=1&PriceDiscount=3&Price=4&DiscountPercent=5&UserID=1453001&SellerID=0');
				String str = "";
				for (Row rd : cursor.newEntryIterable(id)) {
//					System.out.println("" + rd.get("Number") + " | " + rd.get("Goods") + " | " + rd.get("Quantity") + " | " + rd.get("Price"));
//					System.out.println(""+Integer.toString(cnn.userID));
					str =	 "CheckID=" + cnn.currentCheckID.setScale(4, RoundingMode.HALF_UP).toPlainString()
							+"&BarCode="+rd.get("Goods").toString()
							+"&Quantity="+rd.get("Quantity").toString()
							+"&PriceBase="+rd.get("PriceBase").toString()
							+"&PriceDiscount="+rd.get("PriceDiscount").toString()
							+"&Price="+rd.get("Price").toString()
							+"&DiscountPercent="+rd.get("DiscountPercent").toString()
							+"&UserID="+Integer.toString(cnn.userID)
							+"&SellerID=0";
					//System.out.println(str);
					cnn.addGoodInCheckOffline(str);
				}
				cnn.assignSellerByID(-1, 0);
				cnn.setCheckStatus(2);
				bills.updateRow(row);
//				if (1==1) break;
			}
			db.flush();
			db.close();
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			DialogBoxs.viewError(ex);
		}
		requery();
		requeryListOffline();
	}
	private void jButtonExitActionPerformed() {
		dispose();
    }

    private ResultSet getCashTotalOffline() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return null;
        }
        ResultSet rs = cnn.getCashTotalOffline();
        return rs;
    }
    private ResultSet getCashTotal() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return null;
        }
        ResultSet rs = cnn.getCashTotal();
        return rs;
    }
    public class MyRendererTotal extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column > 0) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            return this;
        }
    }
    public class MyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 3) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (column == 1 || column == 2) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            return this;
        }
    }
    public class MyRendererList extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 0 || column > 3) {
				setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				setHorizontalAlignment(SwingConstants.LEFT);
			}
			return this;
		}
	}

	public class HeaderRenderer extends DefaultTableCellRenderer {
        // метод возвращает компонент для прорисовки
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            // получаем настроенную надпись от базового класса
            JLabel label
                    = (JLabel) super.getTableCellRendererComponent(
                            table, value, isSelected, hasFocus,
                            row, column);
            label.setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
            label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            return label;
        }
    }
    private List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
            if (comp.isDisplayable()) {
                comp.addKeyListener(new MyKeyListener());
            }
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }
    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            keyOverride(e);
            super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
        }
        private KeyEvent keyOverride(KeyEvent e) {
            String objCanonicalName = e.getSource().getClass().getCanonicalName();
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_ENTER:    // штрих-код
					if (e.getModifiers() != 0) {
						break;
					}
                    if (objCanonicalName.endsWith("Field")) {
                        JTextField tf = (JTextField) e.getSource();
                        tf.transferFocus();
                    }
                    if (e.getSource() == jButtonCheckMove) jButtonCheckMoveActionPerformed();
                    //if (e.getSource() == jButtonRecordSave) jButtonRecordSaveActionPerformed();
                    if (e.getSource() == jButtonExit)   jButtonExitActionPerformed();
                    break;
                case KeyEvent.VK_UP:
					if (e.getModifiers() != 0) {
						break;
					}
                    if (objCanonicalName.endsWith("Field")) {
                        JTextField tf = (JTextField) e.getSource();
                        tf.transferFocusBackward();
                    } else if (objCanonicalName.endsWith("JButton")) {
                        JButton bt = (JButton) e.getSource();
                        bt.transferFocusBackward();
                    }
                    break;
                case KeyEvent.VK_DOWN:
					if (e.getModifiers() != 0) {
						break;
					}
                    if (objCanonicalName.endsWith("Field")) {
                        JTextField tf = (JTextField) e.getSource();
                        tf.transferFocus();
                    } else if (objCanonicalName.endsWith("JButton")) {
                        JButton bt = (JButton) e.getSource();
                        bt.transferFocus();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
					if (e.getModifiers() != 0) {
						break;
					}
					jButtonExit.requestFocus();
                    break;
                default:
                    //String objCanonicalName = e.getSource().getClass().getCanonicalName();
                    //System.out.println("keyOverride: " + objCanonicalName + " keycode:" + Integer.toString(e.getKeyCode()));
                    break;
            }
            return e;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneCashTotal = new javax.swing.JScrollPane();
        jTableCashTotal = new javax.swing.JTable();
        jPanelButtonMove = new javax.swing.JPanel();
        jButtonCheckMove = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jScrollPaneCashTotalOffline = new javax.swing.JScrollPane();
        jTableCashTotalOffline = new javax.swing.JTable();
        jScrollPaneCheckList = new javax.swing.JScrollPane();
        jTableCheckList = new javax.swing.JTable();
        jScrollPaneCheckListOffline = new javax.swing.JScrollPane();
        jTableCheckListOffline = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPaneCashTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Касса общая:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneCashTotal.setFocusable(false);
        jScrollPaneCashTotal.setRequestFocusEnabled(false);

        jTableCashTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableCashTotal.setForeground(new java.awt.Color(0, 0, 102));
        jTableCashTotal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCashTotal.setAutoscrolls(false);
        jTableCashTotal.setFocusable(false);
        jTableCashTotal.setRequestFocusEnabled(false);
        jTableCashTotal.setRowSelectionAllowed(false);
        jScrollPaneCashTotal.setViewportView(jTableCashTotal);

        jPanelButtonMove.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));

        jButtonCheckMove.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonCheckMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-add-32.png"))); // NOI18N
        jButtonCheckMove.setText("Перенести чеки");
        jButtonCheckMove.setToolTipText("Перенести чеки из автономной программы");
        jButtonCheckMove.setActionCommand("Добавить выдачу денег");
        jButtonCheckMove.setBorderPainted(false);
        jButtonCheckMove.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonCheckMove.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonCheckMove.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonCheckMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckMoveActionPerformed(evt);
            }
        });

        jButtonExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/exit.png"))); // NOI18N
        jButtonExit.setToolTipText("Выход из программы");
        jButtonExit.setActionCommand("Выход");
        jButtonExit.setBorderPainted(false);
        jButtonExit.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonExit.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonExit.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonMoveLayout = new javax.swing.GroupLayout(jPanelButtonMove);
        jPanelButtonMove.setLayout(jPanelButtonMoveLayout);
        jPanelButtonMoveLayout.setHorizontalGroup(
            jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonMoveLayout.createSequentialGroup()
                .addComponent(jButtonCheckMove, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jButtonExit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelButtonMoveLayout.setVerticalGroup(
            jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonMoveLayout.createSequentialGroup()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 142, Short.MAX_VALUE)
                .addComponent(jButtonCheckMove, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(242, Short.MAX_VALUE))
        );

        jScrollPaneCashTotalOffline.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Касса - только чеки автономной программы:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneCashTotalOffline.setFocusable(false);
        jScrollPaneCashTotalOffline.setRequestFocusEnabled(false);

        jTableCashTotalOffline.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableCashTotalOffline.setForeground(new java.awt.Color(0, 0, 102));
        jTableCashTotalOffline.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCashTotalOffline.setAutoscrolls(false);
        jTableCashTotalOffline.setFocusable(false);
        jTableCashTotalOffline.setRequestFocusEnabled(false);
        jTableCashTotalOffline.setRowSelectionAllowed(false);
        jScrollPaneCashTotalOffline.setViewportView(jTableCashTotalOffline);

        jScrollPaneCheckList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список перенесенных чеков:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneCheckList.setFocusable(false);
        jScrollPaneCheckList.setRequestFocusEnabled(false);

        jTableCheckList.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableCheckList.setForeground(new java.awt.Color(0, 0, 102));
        jTableCheckList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCheckList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneCheckList.setViewportView(jTableCheckList);

        jScrollPaneCheckListOffline.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список чеков в автономной программе:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneCheckListOffline.setFocusable(false);
        jScrollPaneCheckListOffline.setRequestFocusEnabled(false);

        jTableCheckListOffline.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTableCheckListOffline.setForeground(new java.awt.Color(0, 0, 102));
        jTableCheckListOffline.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCheckListOffline.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneCheckListOffline.setViewportView(jTableCheckListOffline);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPaneCashTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPaneCashTotalOffline, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPaneCheckList, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPaneCheckListOffline, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButtonMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelButtonMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPaneCashTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                            .addComponent(jScrollPaneCashTotalOffline, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPaneCheckList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPaneCheckListOffline, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))))
        );

        jScrollPaneCashTotal.getAccessibleContext().setAccessibleName("Касса общая:");
        jScrollPaneCashTotalOffline.getAccessibleContext().setAccessibleName("Касса автономной программы:");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jButtonCheckMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckMoveActionPerformed
        jButtonCheckMoveActionPerformed();
    }//GEN-LAST:event_jButtonCheckMoveActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCheckMove;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JPanel jPanelButtonMove;
    private javax.swing.JScrollPane jScrollPaneCashTotal;
    private javax.swing.JScrollPane jScrollPaneCashTotalOffline;
    private javax.swing.JScrollPane jScrollPaneCheckList;
    private javax.swing.JScrollPane jScrollPaneCheckListOffline;
    private javax.swing.JTable jTableCashTotal;
    private javax.swing.JTable jTableCashTotalOffline;
    private javax.swing.JTable jTableCheckList;
    private javax.swing.JTable jTableCheckListOffline;
    // End of variables declaration//GEN-END:variables
}
