package forms;

import datepicker.DatePicker;
import datepicker.ObservingTextField;
import db.ConnectionDb;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import reports.ReportPricePlank;
import reports.ReportPricePlankClub;
import reports.ReportPriceSticker;
import reports.ReportPriceStickerBarcode;
import reports.ReportPriceStickerClub;
import tablemodel.TmStickerContentEdit;
import tablemodel.TmStickerGoods;
import tablemodel.TmTree;

public class FrmStickerEdit extends javax.swing.JDialog {
    private ConfigReader conf;
    private ConnectionDb cnn;
    private boolean cnnState = true;
    private TmTree treeModel;
    private BigDecimal docID;
	private int countStickers = 0;
	private int countPlanks = 0;
    
    public FrmStickerEdit(BigDecimal docID) {
//		try {
//			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//				if ("Nimbus".equals(info.getName())) {
//					javax.swing.UIManager.setLookAndFeel(info.getClassName());
//					break;
//				}
//			}
//		} catch (ClassCastException | IndexOutOfBoundsException | NullPointerException | IllegalArgumentException | ArithmeticException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//			//java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//			MyUtil.errorToLog(FrmMain.class.getName(), ex);
//		}
        this.docID = docID;
        initComponents();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.height = dim.height - 20;
		setSize(dim);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        conf = ConfigReader.getInstance();
        setTitle(conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        setLocationRelativeTo(null);

		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
        //дерево
        treeModel = new TmTree(getTreeNodeListRS(10));
        jTree1.setModel(treeModel);
        jTree1.setEditable(false);
        TreeSelectionModel tsm = jTree1.getSelectionModel();
        tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        jTree1.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent tee) {
                TreePath tp = tee.getPath();
                TmTree.MyDefaultMutableTreeNode myNode = (TmTree.MyDefaultMutableTreeNode) tp.getLastPathComponent();
                if (myNode.getChildCount() == 1) {
                    treeModel.addNodes(getTreeNodeListRS(myNode.nodeID), myNode);
                }
            }
            @Override
            public void treeCollapsed(TreeExpansionEvent tee) {
//                TreePath tp = tee.getPath();
//                System.out.println("Collapse: " + tp.getLastPathComponent());
            }
        });
        jTree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                TreePath tp = tse.getPath();
                TmTree.MyDefaultMutableTreeNode myNode = (TmTree.MyDefaultMutableTreeNode) tp.getLastPathComponent();
                if (myNode.getChildCount() == 0) {
                    requeryGoodsList(myNode.nodeID,0);
                }
            }
        });

        //реквизиты документа
        jComboBoxStickerType.addItem("только стикеры");						//0
        jComboBoxStickerType.addItem("только стикеры клуб. цена");			//1
		jComboBoxStickerType.addItem("только ценовые планки");				//2
		jComboBoxStickerType.addItem("только ценовые планки клуб. цена");	//3
		jComboBoxStickerType.addItem("все как стикеры");					//4
		jComboBoxStickerType.addItem("все как ценовые планки");				//5
		jComboBoxStickerType.addItem("штрих-коды");							//6
		jFormattedTextFieldQtySticker.setText("65");
		jFormattedTextFieldQtySticker.setVisible(false);
		jLabelQtySticker.setVisible(false);
		requery();
        //табличные части
        requeryGoodsList(-1,0);
        requeryDocContent();
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        jTableDocContent.requestFocus();
        //уст. русской раскладки для клавиатуры
        this.getInputContext().selectInputMethod(new Locale("ru", "RU"));
    }
	private void viewQty() {
		int type = jComboBoxStickerType.getSelectedIndex();
		if (type == 0 || type == 1 || type == 4 || type == 6) {
			jLabelQtySticker.setVisible(true);
			jFormattedTextFieldQtySticker.setVisible(true);
		} else {
			jLabelQtySticker.setVisible(false);
			jFormattedTextFieldQtySticker.setVisible(false);
		}
		viewQtyInfo();
	}
	private void viewQtyInfo() {//для расчета к-ва ценников
		String str = "";
		int type = jComboBoxStickerType.getSelectedIndex();
		countStickers = 0;
		countPlanks = 0;
		for (int i=0; i<jTableDocContent.getModel().getRowCount(); i++){
			str = jTableDocContent.getModel().getValueAt(i, 4).toString(); // StickerType
			if (type == 0) { 
				int qty = new BigDecimal(jTableDocContent.getModel().getValueAt(i, 5).toString()).intValue();
				if(str.equalsIgnoreCase("стикеры")) countStickers += qty;
			} else if (type == 1) {
				int qty = new BigDecimal(jTableDocContent.getModel().getValueAt(i, 5).toString()).intValue();
				if(str.equalsIgnoreCase("стикеры клуб. цена")) countStickers += qty;
			} else if (type == 2) {
				int qty = new BigDecimal(jTableDocContent.getModel().getValueAt(i, 5).toString()).intValue();
				if(str.equalsIgnoreCase("ценовые планки")) countPlanks ++;
			} else if (type == 3) {
				int qty = new BigDecimal(jTableDocContent.getModel().getValueAt(i, 5).toString()).intValue();
				if(str.equalsIgnoreCase("ценовые планки клуб. цена")) countPlanks ++;
			} else if (type == 4) {
				int qty = new BigDecimal(jTableDocContent.getModel().getValueAt(i, 5).toString()).intValue();
				countStickers += qty;
			} else if (type == 6) {
				int qty = new BigDecimal(jTableDocContent.getModel().getValueAt(i, 5).toString()).intValue();
				countStickers += qty;
			} else if (type == 5) {
				countPlanks++;
			}
		}
		jLabelStickerInfo.setText("Стикеров  : "+countStickers);
		jLabelPlankInfo.setText(  "Цен.планок: "+countPlanks);
	}
    private void requery(){
        if (!cnn.getStickerInfo(docID)){
            DialogBoxs.viewMessage("Нет информации о документе.\nСообщите разработчику!");
            return;
        }
        jTextFieldNumberDoc.setText(cnn.getStickerInfo("DocID", "BigDecimal"));
        jTextFieldDateDoc.setText(cnn.getStickerInfo("DT_create", "Date"));
        jTextFieldNotes.setText(cnn.getStickerInfo("Notes", "String"));
        int type = Integer.parseInt(cnn.getStickerInfo("DocStatus", "int"));
		jComboBoxStickerType.setSelectedIndex(type);
		viewQty();
    }
    private void requeryGoodsList(int nodeID, int typeSource) {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        jTableGoodsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if (typeSource==0) jTableGoodsList.setModel(new TmStickerGoods(getGoodsListRS(nodeID)));
		if (typeSource==1) jTableGoodsList.setModel(new TmStickerGoods(getGoodsFoundListRS()));
        jTableGoodsList.setDefaultRenderer(jTableGoodsList.getColumnClass(0), new MyRenderer());
        jTableGoodsList.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        jTableGoodsList.setRowHeight(25);
        jTableGoodsList.getColumnModel().getColumn(0).setMinWidth(0);
        jTableGoodsList.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableGoodsList.getColumnModel().getColumn(0).setResizable(false);
        jTableGoodsList.getColumnModel().getColumn(1).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTableGoodsList.getColumnModel().getColumn(3).setPreferredWidth(200);
        jTableGoodsList.getColumnModel().getColumn(4).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(5).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(6).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(7).setPreferredWidth(40);
    }
    private void requeryDocContent() {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableDocContent.getSelectedRow();
        jTableDocContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableDocContent.setModel(new TmStickerContentEdit(getGoodsDocRS()));
        jTableDocContent.setDefaultRenderer(jTableDocContent.getColumnClass(0), new MyRenderer());
        jTableDocContent.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        jTableDocContent.setRowHeight(25);
        jTableDocContent.getColumnModel().getColumn(0).setMinWidth(0);
        jTableDocContent.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableDocContent.getColumnModel().getColumn(0).setResizable(false);
		jTableDocContent.getColumnModel().getColumn(1).setPreferredWidth(40);
		jTableDocContent.getColumnModel().getColumn(2).setPreferredWidth(100);
		jTableDocContent.getColumnModel().getColumn(3).setPreferredWidth(200);
		jTableDocContent.getColumnModel().getColumn(4).setPreferredWidth(100);
		jTableDocContent.getColumnModel().getColumn(5).setPreferredWidth(40);
		jTableDocContent.getColumnModel().getColumn(6).setPreferredWidth(40);
		jTableDocContent.getColumnModel().getColumn(7).setPreferredWidth(40);
        if (jTableDocContent.getRowCount() > 0) {
            if (selectedRow > jTableDocContent.getRowCount() - 1) {
                selectedRow = jTableDocContent.getRowCount() - 1;
            }
            if (selectedRow == -1) {
                jTableDocContent.setRowSelectionInterval(0, 0);
            } else {
                jTableDocContent.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
		viewQtyInfo();
    }

    private void editQuantityGoodsList() {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableGoodsList.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableGoodsList.getRowSorter().convertRowIndexToModel(selectedRow);
        final FrmQuantity frmQuantity = new FrmQuantity();
        frmQuantity.jLabel1.setText("<html>" + 
				jTableGoodsList.getModel().getValueAt(rowNum, 2).toString() + "<br>" +
				jTableGoodsList.getModel().getValueAt(rowNum, 3).toString() + "</html>");
        frmQuantity.goodID = Integer.parseInt(jTableGoodsList.getModel().getValueAt(rowNum, 0).toString());
        frmQuantity.jFormattedTextField1.setValue(BigDecimal.ONE);
        frmQuantity.jFormattedTextField1.setSelectionStart(1);
        frmQuantity.jFormattedTextField1.setSelectionEnd(2);
        frmQuantity.jFormattedTextField1.setSelectionColor(Color.BLUE);
        frmQuantity.setModal(true);
		frmQuantity.setVisible(true);
		BigDecimal bdQuantity = new BigDecimal(frmQuantity.jFormattedTextField1.getValue().toString());
		if (bdQuantity.compareTo(BigDecimal.ZERO) > 0) {
			if (cnn.editGoodQuantityInSticker(docID,frmQuantity.goodID, bdQuantity)) {
				requeryDocContent();
			} else {
				DialogBoxs.viewMessage("ОШИБКА при изменении кол-ва товара!");
			}
		}
		jTableGoodsList.requestFocus();
    }
    private void editQuantityDocContent() {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableDocContent.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableDocContent.getRowSorter().convertRowIndexToModel(selectedRow);
        final FrmQuantity frmQuantity = new FrmQuantity();
        frmQuantity.jLabel1.setText("<html>" + 
				jTableDocContent.getModel().getValueAt(rowNum, 2).toString() + "<br>" + 
				jTableDocContent.getModel().getValueAt(rowNum, 3).toString() + "</html>");
        frmQuantity.goodID = Integer.parseInt(jTableDocContent.getModel().getValueAt(rowNum, 0).toString());
        frmQuantity.jFormattedTextField1.setValue(new BigDecimal(jTableDocContent.getModel().getValueAt(rowNum, 5).toString()));
        frmQuantity.jFormattedTextField1.setSelectionStart(1);
        frmQuantity.jFormattedTextField1.setSelectionEnd(2);
        frmQuantity.jFormattedTextField1.setSelectionColor(Color.BLUE);
		frmQuantity.setModal(true);
		frmQuantity.setVisible(true);
		BigDecimal bdQuantity = new BigDecimal(frmQuantity.jFormattedTextField1.getValue().toString());
		if (bdQuantity.compareTo(BigDecimal.ZERO) > 0) {
			if (cnn.editGoodQuantityInSticker(docID, frmQuantity.goodID, bdQuantity)) {
				requeryDocContent();
			} else {
				DialogBoxs.viewMessage("ОШИБКА при изменении кол-ва товара!");
			}
		}
		jTableDocContent.requestFocus();
    }
    private void goodOperationQuantity(String typeOperation) {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableDocContent.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableDocContent.getRowSorter().convertRowIndexToModel(selectedRow);
        int goodID = Integer.parseInt(jTableDocContent.getModel().getValueAt(rowNum, 0).toString());
        boolean bl;
        if (typeOperation.equals("add")) {
            bl = cnn.addGoodInStickerQuantity(docID,goodID);
        } else if (typeOperation.equals("del")) {
            bl = cnn.deleteGoodFromSticker(docID,goodID);
        } else {
            DialogBoxs.viewMessage("Неверная операция с количеством товара: " + typeOperation);
            return;
        }
        if (bl) {
            requeryDocContent();
        } else {
            DialogBoxs.viewMessage("Не найден товар с GoodID: " + Integer.toString(goodID));
        }
        jTableDocContent.requestFocus();
    }
    private void jButtonDateDocActionPerformed() {
        final Locale locale = new Locale("ru");
        final DatePicker dp = new DatePicker((Observer) jTextFieldDateDoc, locale);
        // previously selected date
        Date selectedDate = dp.parseDate(jTextFieldDateDoc.getText());
        dp.setSelectedDate(selectedDate);
        dp.start(jTextFieldDateDoc);
        dp.getScreen().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                //здесь обработчик срабатывает 2 раза - не понятно почему так
                //requery();
                //DialogBoxs.viewMessage(dp.toString());
            }
        });
    }
    private void jTextFieldNotesActionPerformed(){
        cnn.setStickerNotes(docID,jTextFieldNotes.getText());
    }
	private void jComboBoxStickerTypeActionPerformed() {
		int type = jComboBoxStickerType.getSelectedIndex();
		cnn.setStickerStatus(docID, type);
		viewQty();
	}
    private void jButtonAddGroupAllActionPerformed(){
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		TreePath tp = jTree1.getSelectionPath();
		if (tp != null) {
			int i = JOptionPane.showConfirmDialog(null, "Добавить группу товара?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i == 0) {
				TmTree.MyDefaultMutableTreeNode myNode = (TmTree.MyDefaultMutableTreeNode) tp.getLastPathComponent();
				if (myNode.getChildCount() == 0) {
					boolean bl = cnn.addGroupAllInSticker(docID, "all", myNode.nodeID);
					if (bl) requeryDocContent();
				}
			}
		} else {
			DialogBoxs.viewMessageWarning("Сначала выберите группу товаров!");
		}
	}
    private void jButtonAddGroupBalanceActionPerformed(){
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		TreePath tp = jTree1.getSelectionPath();
		if (tp != null) {
			int i = JOptionPane.showConfirmDialog(null, "Добавить группу товара (только остатки)?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i == 0) {
				TmTree.MyDefaultMutableTreeNode myNode = (TmTree.MyDefaultMutableTreeNode) tp.getLastPathComponent();
				if (myNode.getChildCount() == 0) {
					boolean bl = cnn.addGroupAllInSticker(docID, "balance", myNode.nodeID);
					if (bl)	requeryDocContent();
				}
			}
		} else {
			DialogBoxs.viewMessageWarning("Сначала выберите группу товаров!");
		}
	}
    private void jButtonSearchActionPerformed(){
		requeryGoodsList(-1,1);
		if (1==0){
			int selRow = jTableGoodsList.getSelectedRow();
			if (selRow==-1) selRow = 0;
			String searchArt = jTextFieldArticle.getText();
			String searchName = jTextFieldName.getText();
			if (searchArt.equals("")&&searchName.equals("")){
				DialogBoxs.viewMessage("Для поиска товара нужно ввести артикул или название!");
				return;
			}
			if (jTableGoodsList.getRowCount()<1){
				DialogBoxs.viewMessage("Негде искать!\nВыберите группу товара!");
				return;
			}
			int row = 0;
			for (row = 0; row <= jTableGoodsList.getRowCount() - 1; row++) {
				String art  = jTableGoodsList.getValueAt(row, 2).toString();
				String name = jTableGoodsList.getValueAt(row, 3).toString();
				if (    art.toLowerCase().contains(searchArt.toLowerCase())
					&& name.toLowerCase().contains(searchName.toLowerCase())
					){
					break;
				}
			}
			if (row == jTableGoodsList.getRowCount()) {
				row = selRow;
				DialogBoxs.viewMessage("Товар в выбранной группе не найден!");
			}
			Rectangle cellRect = jTableGoodsList.getCellRect(row, 1, true);
			jTableGoodsList.scrollRectToVisible(cellRect);
			jTableGoodsList.setRowSelectionInterval(row, row);
			jTableGoodsList.requestFocus();
		}
    }
	private void jButtonPrintActionPerformed() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		int type = jComboBoxStickerType.getSelectedIndex();
		if (type == 0 || type == 1 || type == 4 || type == 6) {
			int countStickers1Page = Integer.parseInt(jFormattedTextFieldQtySticker.getText());
			if (countStickers1Page > 65){
				DialogBoxs.viewMessage("Стикеров на 1 странице не может быть больше 65!");
				jFormattedTextFieldQtySticker.setText("65");
				return;
			}
			if(type == 0 || type == 4){
				ReportPriceSticker reportPrice = new ReportPriceSticker(docID, type, countStickers1Page);
				reportPrice.setModal(true);
				reportPrice.setVisible(true);
			} else if (type == 6) {
					ReportPriceStickerBarcode reportPrice = new ReportPriceStickerBarcode(docID, type, countStickers1Page);
					reportPrice.setModal(true);
					reportPrice.setVisible(true);
			}else{
				ReportPriceStickerClub reportPrice = new ReportPriceStickerClub(docID, type, countStickers1Page);
				reportPrice.setModal(true);
				reportPrice.setVisible(true);
			}
		} else if (type == 2 || type == 3 || type == 5){
			if (type == 2 || type == 5) {
				ReportPricePlank reportPrice = new ReportPricePlank(docID, type);
				reportPrice.setModal(true);
				reportPrice.setVisible(true);
			}else{
				ReportPricePlankClub reportPrice = new ReportPricePlankClub(docID, type);
				reportPrice.setModal(true);
				reportPrice.setVisible(true);
			}
		}
	}
	private void jButtonExitActionPerformed() {
		dispose();
	}
    
    private ResultSet getTreeNodeListRS(int nodeID) {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getTreeNodeList(nodeID);
        return rs;
    }
    private ResultSet getGoodsListRS(int nodeID) {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getGoodsListForSticker(nodeID);
        return rs;
    }
    private ResultSet getGoodsFoundListRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
		ResultSet rs = cnn.getSearchContent("", jTextFieldArticle.getText(), jTextFieldName.getText(),"_for_sticker");
        return rs;
    }
    private ResultSet getGoodsDocRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getStickerContent(docID,"_edit");
        return rs;
    }
    private boolean checkCnnStatus() {
        if (!cnnState) {
            JOptionPane.showMessageDialog(null, "Нет связи с сервером!\n\nПовторите операцию позже.", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/connect lost.png")));
        }
        return cnnState;
    }

    private class MyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column >= 0 && column <= 3) {
				setHorizontalAlignment(SwingConstants.LEFT);
			} else if (column == 4) {
				setHorizontalAlignment(SwingConstants.CENTER);
			} else if (column > 4) {
				setHorizontalAlignment(SwingConstants.RIGHT);
			}
            return this;
        }
    }
    private class HeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
//			if (row<0) return super.getTableCellRendererComponent(
//						table, value, isSelected, hasFocus,
//						row, column);
            try {
//				System.out.println("row:"+Integer.toString(row)+"	col:" + Integer.toString(column));
                // получаем настроенную надпись от базового класса
                JLabel label
                        = (JLabel) super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus,
                                row, column);
                label.setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
                label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
                label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                return label;
            } catch (IndexOutOfBoundsException ex) {
//				System.out.println("errror row:" + Integer.toString(row) + "	col:" + Integer.toString(column));
//				DialogBoxs.viewError(ex);
				MyUtil.errorToLog(this.getClass().getName(), ex);
                return new JLabel();
            }
        }
    }
    private List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
//            String canonicalName = comp.getClass().getCanonicalName();
//            System.out.println("addKeyListener: " + comp.getName() + "  " + canonicalName + "    focus=" + comp.isDisplayable());
            if (comp.isDisplayable()) {
                comp.addKeyListener(new MyKeyListener());
            }
            if (comp.isFocusable()) {
                comp.addFocusListener(new MyFocusListener());
            }
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }
    private class MyFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            //System.out.println("gain: " + e.getSource().getClass().getCanonicalName());
        }
        @Override
        public void focusLost(FocusEvent e) {
            //System.out.println("lost: " + e.getSource().getClass().getCanonicalName());
        }
    }
    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
//            if (!checkCnnStatus()) {
//                e.consume();
//                return;
//            }
            keyOverride(e);
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getSource().getClass().getCanonicalName().endsWith("JTable")) {
                e.consume();
                return;
            }
//            if (e.getKeyCode() == KeyEvent.VK_F10) {
//                e.consume();
//            }
            super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
        }
        private KeyEvent keyOverride(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_SUBTRACT:       // минус 1 из кол-ва по тек.товару
					//System.out.println("минус 1 из кол-ва по тек.товару");
                    if (e.getModifiers() != 0) break;
                    if (e.getSource() == jTableDocContent)
                        goodOperationQuantity("del");
                    break;
                case KeyEvent.VK_ADD:            // добавить 1 к кол-ву по тек.товару
                    if (e.getModifiers() != 0) break;
                    if (e.getSource() == jTableGoodsList)
                        editQuantityGoodsList();
                    if (e.getSource() == jTableDocContent)
                        goodOperationQuantity("add");
                    break;
                case KeyEvent.VK_MULTIPLY:       // изменить кол-во по тек.товару
                    if (e.getModifiers() != 0) break;
                    if (e.getSource()==jTableGoodsList)
                        editQuantityGoodsList();
                    if (e.getSource() == jTableDocContent)
                        editQuantityDocContent();
                    break;
                case KeyEvent.VK_ENTER:            // добавить 1 к кол-ву по тек.товару
                    if (e.getModifiers() != 0) break;
                    if (e.getSource() == jTableGoodsList) 
                        editQuantityGoodsList();
                    if (e.getSource() == jTableDocContent) 
                        editQuantityDocContent();
					String objCanonicalName = e.getSource().getClass().getCanonicalName();
					//System.out.println("keyOverride: "+ objCanonicalName + " keycode:" + Integer.toString(e.getKeyCode()));
					if (objCanonicalName.endsWith("JButton")) {
						if (e.getSource() == jButtonSearch) {
							jButtonSearchActionPerformed();
						}
						if (e.getSource() == jButtonExit) {
							jButtonExitActionPerformed();
						}
					} else if (objCanonicalName.endsWith("Field")) {
						JTextField tf = (JTextField) e.getSource();
						tf.transferFocus();
					}
                    break;
                default:
                    break;
            }
            return e;
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelTitle = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTextFieldNumberDoc = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextFieldDateDoc = new ObservingTextField();
        jTextFieldNotes = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanelPrint = new javax.swing.JPanel();
        jLabelStickerType = new javax.swing.JLabel();
        jComboBoxStickerType = new javax.swing.JComboBox();
        jLabelQtySticker = new javax.swing.JLabel();
        jFormattedTextFieldQtySticker = new javax.swing.JFormattedTextField();
        jButtonPrint = new javax.swing.JButton();
        jLabelStickerInfo = new javax.swing.JLabel();
        jLabelPlankInfo = new javax.swing.JLabel();
        jScrollPaneTree = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanelSearch = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldArticle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jButtonAddGroupAll = new javax.swing.JButton();
        jButtonAddGroupBalance = new javax.swing.JButton();
        jScrollPaneGoodsList = new javax.swing.JScrollPane();
        jTableGoodsList = new javax.swing.JTable();
        jScrollPaneDocContent = new javax.swing.JScrollPane();
        jTableDocContent = new javax.swing.JTable();
        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelTitle.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Реквизиты документа:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jPanelTitle.setPreferredSize(new java.awt.Dimension(345, 93));

        jLabel21.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("№ док.:");
        jLabel21.setAutoscrolls(true);
        jLabel21.setFocusable(false);
        jLabel21.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel21.setRequestFocusEnabled(false);
        jLabel21.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jTextFieldNumberDoc.setEditable(false);
        jTextFieldNumberDoc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNumberDoc.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldNumberDoc.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldNumberDoc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldNumberDoc.setFocusable(false);
        jTextFieldNumberDoc.setRequestFocusEnabled(false);

        jLabel22.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Дата док.:");
        jLabel22.setAutoscrolls(true);
        jLabel22.setFocusable(false);
        jLabel22.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel22.setRequestFocusEnabled(false);
        jLabel22.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jTextFieldDateDoc.setEditable(false);
        jTextFieldDateDoc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldDateDoc.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldDateDoc.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldDateDoc.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldDateDoc.setFocusable(false);
        jTextFieldDateDoc.setRequestFocusEnabled(false);

        jTextFieldNotes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNotes.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldNotes.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldNotes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldNotes.setMaximumSize(new java.awt.Dimension(280, 22));
        jTextFieldNotes.setMinimumSize(new java.awt.Dimension(280, 22));
        jTextFieldNotes.setPreferredSize(new java.awt.Dimension(280, 22));
        jTextFieldNotes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNotesActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("Примечание:");
        jLabel23.setAutoscrolls(true);
        jLabel23.setFocusable(false);
        jLabel23.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel23.setRequestFocusEnabled(false);
        jLabel23.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanelTitleLayout = new javax.swing.GroupLayout(jPanelTitle);
        jPanelTitle.setLayout(jPanelTitleLayout);
        jPanelTitleLayout.setHorizontalGroup(
            jPanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTitleLayout.createSequentialGroup()
                .addGroup(jPanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelTitleLayout.createSequentialGroup()
                        .addComponent(jTextFieldNumberDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDateDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelTitleLayout.setVerticalGroup(
            jPanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTitleLayout.createSequentialGroup()
                .addGroup(jPanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldNumberDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDateDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelPrint.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Параметры печати:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jPanelPrint.setPreferredSize(new java.awt.Dimension(564, 100));

        jLabelStickerType.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabelStickerType.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelStickerType.setText("Вид ценника:");
        jLabelStickerType.setAutoscrolls(true);
        jLabelStickerType.setFocusable(false);
        jLabelStickerType.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabelStickerType.setRequestFocusEnabled(false);
        jLabelStickerType.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jComboBoxStickerType.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxStickerType.setBorder(null);
        jComboBoxStickerType.setMinimumSize(new java.awt.Dimension(240, 26));
        jComboBoxStickerType.setPreferredSize(new java.awt.Dimension(240, 26));
        jComboBoxStickerType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStickerTypeActionPerformed(evt);
            }
        });

        jLabelQtySticker.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabelQtySticker.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelQtySticker.setText("К-во стикеров на 1-ой странице:");
        jLabelQtySticker.setAutoscrolls(true);
        jLabelQtySticker.setFocusable(false);
        jLabelQtySticker.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabelQtySticker.setRequestFocusEnabled(false);
        jLabelQtySticker.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jFormattedTextFieldQtySticker.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jFormattedTextFieldQtySticker.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        jFormattedTextFieldQtySticker.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jFormattedTextFieldQtySticker.setFocusCycleRoot(true);
        jFormattedTextFieldQtySticker.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jButtonPrint.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButtonPrint.setText("<html><center>Печать<br>ценников</html>");
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });

        jLabelStickerInfo.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabelStickerInfo.setForeground(new java.awt.Color(0, 0, 255));
        jLabelStickerInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelStickerInfo.setText("Стикеров:");

        jLabelPlankInfo.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabelPlankInfo.setForeground(new java.awt.Color(0, 0, 255));
        jLabelPlankInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelPlankInfo.setText("Цен.планок:");
        jLabelPlankInfo.setMaximumSize(new java.awt.Dimension(200, 24));
        jLabelPlankInfo.setMinimumSize(new java.awt.Dimension(44, 24));
        jLabelPlankInfo.setPreferredSize(new java.awt.Dimension(44, 24));

        javax.swing.GroupLayout jPanelPrintLayout = new javax.swing.GroupLayout(jPanelPrint);
        jPanelPrint.setLayout(jPanelPrintLayout);
        jPanelPrintLayout.setHorizontalGroup(
            jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrintLayout.createSequentialGroup()
                .addGroup(jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPrintLayout.createSequentialGroup()
                        .addComponent(jLabelStickerType, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxStickerType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelPrintLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabelQtySticker, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextFieldQtySticker, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelPlankInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelStickerInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelPrintLayout.setVerticalGroup(
            jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrintLayout.createSequentialGroup()
                .addGroup(jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelPrintLayout.createSequentialGroup()
                        .addGroup(jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jComboBoxStickerType, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelStickerInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelStickerType, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelPlankInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelQtySticker, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jFormattedTextFieldQtySticker, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButtonPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPaneTree.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneTree.setViewportView(jTree1);

        jPanelSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Поиск товаров:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Артикул:");

        jTextFieldArticle.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldArticle.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Название:");

        jTextFieldName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonSearch.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonSearch.setText("Искать");
        jButtonSearch.setToolTipText("искать товары");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jButtonAddGroupAll.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonAddGroupAll.setText("Добавить группу");
        jButtonAddGroupAll.setToolTipText("добавить группу");
        jButtonAddGroupAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddGroupAllActionPerformed(evt);
            }
        });

        jButtonAddGroupBalance.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonAddGroupBalance.setText("Добавить группу (только ост.)");
        jButtonAddGroupBalance.setToolTipText("добавить группу (только остаток)");
        jButtonAddGroupBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddGroupBalanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelSearchLayout = new javax.swing.GroupLayout(jPanelSearch);
        jPanelSearch.setLayout(jPanelSearchLayout);
        jPanelSearchLayout.setHorizontalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddGroupAll, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAddGroupBalance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelSearchLayout.setVerticalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddGroupAll, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddGroupBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jScrollPaneGoodsList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров для добавления:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableGoodsList.setAutoCreateRowSorter(true);
        jTableGoodsList.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTableGoodsList.setForeground(java.awt.SystemColor.textHighlight);
        jTableGoodsList.setRowHeight(25);
        jScrollPaneGoodsList.setViewportView(jTableGoodsList);

        jScrollPaneDocContent.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров в документе:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableDocContent.setAutoCreateRowSorter(true);
        jTableDocContent.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTableDocContent.setForeground(java.awt.SystemColor.textHighlight);
        jTableDocContent.setRowHeight(25);
        jScrollPaneDocContent.setViewportView(jTableDocContent);

        jPanelButton.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));
        jPanelButton.setPreferredSize(new java.awt.Dimension(82, 102));

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

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPaneTree, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDocContent, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPaneGoodsList)
                    .addComponent(jPanelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanelPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneTree)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanelPrint, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(jPanelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addComponent(jPanelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPaneGoodsList, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPaneDocContent, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jTextFieldNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNotesActionPerformed
        jTextFieldNotesActionPerformed();
    }//GEN-LAST:event_jTextFieldNotesActionPerformed
    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        jButtonSearchActionPerformed();
    }//GEN-LAST:event_jButtonSearchActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
		jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jComboBoxStickerTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStickerTypeActionPerformed
		if (evt.getModifiers() != 0) {
			jComboBoxStickerTypeActionPerformed();
		}
    }//GEN-LAST:event_jComboBoxStickerTypeActionPerformed
    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        jButtonPrintActionPerformed();
    }//GEN-LAST:event_jButtonPrintActionPerformed
    private void jButtonAddGroupAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddGroupAllActionPerformed
        jButtonAddGroupAllActionPerformed();
    }//GEN-LAST:event_jButtonAddGroupAllActionPerformed
    private void jButtonAddGroupBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddGroupBalanceActionPerformed
        jButtonAddGroupBalanceActionPerformed();
    }//GEN-LAST:event_jButtonAddGroupBalanceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddGroupAll;
    private javax.swing.JButton jButtonAddGroupBalance;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JComboBox jComboBoxStickerType;
    private javax.swing.JFormattedTextField jFormattedTextFieldQtySticker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel21;
    public javax.swing.JLabel jLabel22;
    public javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabelPlankInfo;
    public javax.swing.JLabel jLabelQtySticker;
    private javax.swing.JLabel jLabelStickerInfo;
    public javax.swing.JLabel jLabelStickerType;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelPrint;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JPanel jPanelTitle;
    private javax.swing.JScrollPane jScrollPaneDocContent;
    private javax.swing.JScrollPane jScrollPaneGoodsList;
    private javax.swing.JScrollPane jScrollPaneTree;
    private javax.swing.JTable jTableDocContent;
    private javax.swing.JTable jTableGoodsList;
    private javax.swing.JTextField jTextFieldArticle;
    private javax.swing.JTextField jTextFieldDateDoc;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNotes;
    private javax.swing.JTextField jTextFieldNumberDoc;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
