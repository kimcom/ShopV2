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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
import tablemodel.TmOrderAutoContent;
import tablemodel.TmOrderAutoGoods;
import tablemodel.TmTree;

public class FrmOrderEditAuto extends javax.swing.JDialog {
    private ConfigReader conf;
    private ConnectionDb cnn;
    private boolean cnnState = true;
    private TmTree treeModel;
    private BigDecimal docID;
    
    public FrmOrderEditAuto(BigDecimal docID) {
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
        treeModel = new TmTree(getTreeNodeListRS(20));
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
//                System.out.println("Expansion: " + tp.getLastPathComponent()+" myNode.nodeID: " + Integer.toString(myNode.nodeID));
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
//                System.out.println("Selection event: " + tp.getLastPathComponent() + " myNode.nodeID: " + Integer.toString(myNode.nodeID));
            }
        });

        //реквизиты документа
        jComboBoxStatusDoc.addItem("предварительный");
        jComboBoxStatusDoc.addItem("отправить менеджеру");
        jButtonDateDoc.setEnabled(false);
        jButtonDateDoc.setVisible(false);
        requery();
        //табличные части
        requeryGoodsList(-1,0);
        requeryOrderContent();
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        jTableOrderContent.requestFocus();
        //уст. русской раскладки для клавиатуры
        this.getInputContext().selectInputMethod(new Locale("ru", "RU"));
    }
    private void requery(){
        if (!cnn.getOrderInfo(docID)){
            DialogBoxs.viewMessage("Нет информации о документе.\nСообщите разработчику!");
            return;
        }
        jTextFieldNumberDoc.setText(cnn.getOrderInfo("CheckID", "BigDecimal"));
        jTextFieldDateDoc.setText(cnn.getOrderInfo("CreateDateTime", "Date"));
        jTextFieldNotes.setText(cnn.getOrderInfo("Notes", "String"));
        
        if (cnn.getOrderInfo("CheckStatus", "int").equals("0"))
            jComboBoxStatusDoc.setSelectedItem("предварительный");
        if (cnn.getOrderInfo("CheckStatus", "int").equals("10")) {
            jComboBoxStatusDoc.setSelectedItem("отправить менеджеру");
        }
    }
	private void requeryGoodsList(int nodeID, int typeSource) {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        jTableGoodsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //jTableGoodsList.setModel(new TmOrderAutoGoods(getGoodsListRS(nodeID)));
		if (typeSource == 0) jTableGoodsList.setModel(new TmOrderAutoGoods(getGoodsListRS(nodeID)));
		if (typeSource == 1) jTableGoodsList.setModel(new TmOrderAutoGoods(getGoodsFoundListRS()));
        jTableGoodsList.setDefaultRenderer(jTableGoodsList.getColumnClass(0), new MyRenderer());
        jTableGoodsList.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        jTableGoodsList.setRowHeight(25);
        jTableGoodsList.getColumnModel().getColumn(0).setMinWidth(0);
        jTableGoodsList.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableGoodsList.getColumnModel().getColumn(0).setResizable(false);
        jTableGoodsList.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableGoodsList.getColumnModel().getColumn(2).setPreferredWidth(400);
        jTableGoodsList.getColumnModel().getColumn(3).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(4).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(5).setPreferredWidth(40);
        jTableGoodsList.getColumnModel().getColumn(6).setPreferredWidth(40);
    }
    private void requeryOrderContent() {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableOrderContent.getSelectedRow();
        jTableOrderContent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableOrderContent.setModel(new TmOrderAutoContent(getGoodsDocRS()));
        jTableOrderContent.setDefaultRenderer(jTableOrderContent.getColumnClass(0), new MyRenderer());
        jTableOrderContent.getTableHeader().setDefaultRenderer(new HeaderRenderer());
        jTableOrderContent.setRowHeight(25);
        jTableOrderContent.getColumnModel().getColumn(0).setMinWidth(0);
        jTableOrderContent.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableOrderContent.getColumnModel().getColumn(0).setResizable(false);
        jTableOrderContent.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableOrderContent.getColumnModel().getColumn(2).setPreferredWidth(400);
        jTableOrderContent.getColumnModel().getColumn(3).setPreferredWidth(40);
        jTableOrderContent.getColumnModel().getColumn(4).setPreferredWidth(40);
        jTableOrderContent.getColumnModel().getColumn(5).setPreferredWidth(40);
        jTableOrderContent.getColumnModel().getColumn(6).setPreferredWidth(40);
        if (jTableOrderContent.getRowCount() > 0) {
            if (selectedRow > jTableOrderContent.getRowCount() - 1) {
                selectedRow = jTableOrderContent.getRowCount() - 1;
            }
            if (selectedRow == -1) {
                jTableOrderContent.setRowSelectionInterval(0, 0);
            } else {
                jTableOrderContent.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
//        if (getFocusOwner()!=null)
//            getFocusOwner().requestFocus();
            //DialogBoxs.viewMessage("Focus owner: "+getFocusOwner().getName());
    }

    private void editQuantityGoodsList() {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableGoodsList.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableGoodsList.getRowSorter().convertRowIndexToModel(selectedRow);
		int zakazMin = Integer.parseInt(jTableGoodsList.getModel().getValueAt(rowNum, 3).toString());
		int balanceMin = Integer.parseInt(jTableGoodsList.getModel().getValueAt(rowNum, 4).toString());
		int balance = Integer.parseInt(jTableGoodsList.getModel().getValueAt(rowNum, 6).toString());
		if ( balanceMin <= balance) {
			JOptionPane.showMessageDialog(null, "Для товара: "+jTableGoodsList.getModel().getValueAt(rowNum, 2).toString()
					+ "\nУстановлено ограничение остатка: "+Integer.toString(balanceMin)
					+ "\nТекущее значение остатка: "+Integer.toString(balance)
					+ "\n\nЗаказ товара невозможен!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/dialog_warning.png")));
			return;
		}
        int goodID = Integer.parseInt(jTableGoodsList.getModel().getValueAt(rowNum, 0).toString());
//        final FrmQuantity frmQuantity = new FrmQuantity();
//        frmQuantity.jLabel1.setText("<html>" + jTableGoodsList.getModel().getValueAt(rowNum, 2).toString() + "</html>");
//        frmQuantity.goodID = Integer.parseInt(jTableGoodsList.getModel().getValueAt(rowNum, 0).toString());
//        frmQuantity.jFormattedTextField1.setValue(new BigDecimal(0));
//        frmQuantity.jFormattedTextField1.setSelectionStart(1);
//        frmQuantity.jFormattedTextField1.setSelectionEnd(2);
//        frmQuantity.jFormattedTextField1.setSelectionColor(Color.BLUE);
//        frmQuantity.setModal(true);
//		frmQuantity.setVisible(true);
//		BigDecimal bdQuantity = new BigDecimal(frmQuantity.jFormattedTextField1.getValue().toString());
		BigDecimal bdQuantity = new BigDecimal(balanceMin - balance);
		if(zakazMin > 1) {
			bdQuantity = new BigDecimal(Math.ceil((double) (balanceMin - balance) / zakazMin ) * zakazMin);
		}
		if (bdQuantity.compareTo(BigDecimal.ZERO) > 0) {
			if (balanceMin <= balance) {
				JOptionPane.showMessageDialog(null, "Для товара: " + jTableGoodsList.getModel().getValueAt(rowNum, 2).toString()
						+ "\nУстановлено ограничение остатка: " + Integer.toString(balanceMin)
						+ "\nТекущее значение остатка: " + Integer.toString(balance)
						+ "\n\nЗаказ товара невозможен!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/dialog_warning.png")));
				return;
			}
			if (cnn.editGoodQuantityInOrder(docID, goodID, bdQuantity)) {
				requeryOrderContent();
			} else {
				DialogBoxs.viewMessage("ОШИБКА при изменении кол-ва товара!");
			}
		}
		jTableGoodsList.requestFocus();
    }
    private void editQuantityOrderContent() {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableOrderContent.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableOrderContent.getRowSorter().convertRowIndexToModel(selectedRow);
        final FrmQuantity frmQuantity = new FrmQuantity();
        frmQuantity.jLabel1.setText("<html>" + jTableOrderContent.getModel().getValueAt(rowNum, 2).toString() + "</html>");
        frmQuantity.goodID = Integer.parseInt(jTableOrderContent.getModel().getValueAt(rowNum, 0).toString());
        frmQuantity.jFormattedTextField1.setValue(new BigDecimal(jTableOrderContent.getModel().getValueAt(rowNum, 4).toString()));
        frmQuantity.jFormattedTextField1.setSelectionStart(1);
        frmQuantity.jFormattedTextField1.setSelectionEnd(2);
        frmQuantity.jFormattedTextField1.setSelectionColor(Color.BLUE);
		frmQuantity.setModal(true);
		frmQuantity.setVisible(true);
		BigDecimal bdQuantity = new BigDecimal(frmQuantity.jFormattedTextField1.getValue().toString());
		if (bdQuantity.compareTo(BigDecimal.ZERO) > 0) {
			if (cnn.editGoodQuantityInOrder(docID, frmQuantity.goodID, bdQuantity)) {
				requeryOrderContent();
			} else {
				DialogBoxs.viewMessage("ОШИБКА при изменении кол-ва товара!");
			}
		}
		jTableOrderContent.requestFocus();
    }
    private void goodOperationQuantity(String typeOperation) {
        if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableOrderContent.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableOrderContent.getRowSorter().convertRowIndexToModel(selectedRow);
        int goodID = Integer.parseInt(jTableOrderContent.getModel().getValueAt(rowNum, 0).toString());
        boolean bl;
        if (typeOperation.equals("add")) {
            bl = cnn.addGoodInOrderQuantity(docID,goodID);
        } else if (typeOperation.equals("del")) {
            //bl = cnn.deleteGoodFromOrder(docID,goodID);
			bl = cnn.editGoodQuantityInOrder(docID, goodID, BigDecimal.ZERO);
        } else {
            DialogBoxs.viewMessage("Неверная операция с количеством товара: " + typeOperation);
            return;
        }
        if (bl) {
            requeryOrderContent();
        } else {
            DialogBoxs.viewMessage("Не найден товар с GoodID: " + Integer.toString(goodID));
        }
        jTableOrderContent.requestFocus();
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
    private void jComboBoxStatusDocActionPerformed(){
        if (jComboBoxStatusDoc.getSelectedItem().toString().equals("предварительный"))
            if (cnn.setOrderStatus(docID, 0));
        if (jComboBoxStatusDoc.getSelectedItem().toString().equals("отправить менеджеру"))
            if (cnn.setOrderStatus(docID, 10)) 
                dispose();
    }
    private void jTextFieldNotesActionPerformed(){
        cnn.setOrderNotes(docID,jTextFieldNotes.getText());
    }
	private void jButtonAddGroupAllActionPerformed() {
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		TreePath tp = jTree1.getSelectionPath();
		if (tp != null) {
			int i = JOptionPane.showConfirmDialog(null, "Добавить группу товара?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i == 0) {
				TmTree.MyDefaultMutableTreeNode myNode = (TmTree.MyDefaultMutableTreeNode) tp.getLastPathComponent();
				boolean bl = cnn.addGroupAllInOrder(docID, "all", myNode.nodeID);
				//if (bl) {
					requeryOrderContent();
				//}
			}
		} else {
			DialogBoxs.viewMessageWarning("Сначала выберите группу товаров!");
		}
	}
    private void jButtonSearchActionPerformed(){
		requeryGoodsList(-1, 1);
		if (1==0) {
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
				String art  = jTableGoodsList.getValueAt(row, 1).toString();
				String name = jTableGoodsList.getValueAt(row, 2).toString();
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
        ResultSet rs = cnn.getGoodsList(nodeID);
        return rs;
    }
	private ResultSet getGoodsFoundListRS() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return null;
		ResultSet rs = cnn.getSearchContent("", jTextFieldArticle.getText(), jTextFieldName.getText(), "_for_sticker");
		return rs;
	}
    private ResultSet getGoodsDocRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getOrderContent(docID,"desc");
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
            if (column > 2) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
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
                label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
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
                    if (e.getModifiers() != 0) break;
                    if (e.getSource() == jTableOrderContent)
                        goodOperationQuantity("del");
                    break;
                case KeyEvent.VK_ADD:            // добавить 1 к кол-ву по тек.товару
                    if (e.getModifiers() != 0) break;
                    if (e.getSource() == jTableGoodsList)
                        editQuantityGoodsList();
                    //if (e.getSource() == jTableOrderContent)
                        //goodOperationQuantity("add");
                    break;
                case KeyEvent.VK_MULTIPLY:       // изменить кол-во по тек.товару
                    if (e.getModifiers() != 0) break;
                    if (e.getSource()==jTableGoodsList)
                        editQuantityGoodsList();
                    //if (e.getSource() == jTableOrderContent)
                        //editQuantityOrderContent();
                    break;
                case KeyEvent.VK_ENTER:            // добавить 1 к кол-ву по тек.товару
                    if (e.getModifiers() != 0) break;
                    if (e.getSource() == jTableGoodsList) 
                        editQuantityGoodsList();
                    //if (e.getSource() == jTableOrderContent) 
                        //editQuantityOrderContent();
                    if (e.getSource() == jTextFieldArticle) {
						JTextField tf = (JTextField) e.getSource();
                        tf.transferFocus();
					}
                    if (e.getSource() == jTextFieldName) {
						JTextField tf = (JTextField) e.getSource();
                        tf.transferFocus();
					}
                    if (e.getSource() == jButtonSearch) 
						jButtonSearchActionPerformed();
					if (e.getSource() == jButtonExit) 
						jButtonExitActionPerformed();
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

        jPanel2 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTextFieldNumberDoc = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextFieldDateDoc = new ObservingTextField();
        jButtonDateDoc = new javax.swing.JButton();
        jTextFieldNotes = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jComboBoxStatusDoc = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jScrollPaneGoodsList = new javax.swing.JScrollPane();
        jTableGoodsList = new javax.swing.JTable();
        jScrollPaneOrderContent = new javax.swing.JScrollPane();
        jTableOrderContent = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldArticle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jButtonAddGroupAll = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Реквизиты документа:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

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

        jButtonDateDoc.setText("...");
        jButtonDateDoc.setToolTipText("выбор даты");
        jButtonDateDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDateDocActionPerformed(evt);
            }
        });

        jTextFieldNotes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNotes.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldNotes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
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

        jLabel27.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("Статус:");
        jLabel27.setFocusable(false);
        jLabel27.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel27.setRequestFocusEnabled(false);

        jComboBoxStatusDoc.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxStatusDoc.setBorder(null);
        jComboBoxStatusDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxStatusDocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldNumberDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDateDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDateDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxStatusDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(jTextFieldNotes)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldNumberDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldDateDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxStatusDoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonDateDoc, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, " ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPane1.setViewportView(jTree1);

        jScrollPaneGoodsList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров для добавления:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableGoodsList.setAutoCreateRowSorter(true);
        jTableGoodsList.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTableGoodsList.setForeground(java.awt.SystemColor.textHighlight);
        jTableGoodsList.setRowHeight(25);
        jScrollPaneGoodsList.setViewportView(jTableGoodsList);

        jScrollPaneOrderContent.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров в документе:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableOrderContent.setAutoCreateRowSorter(true);
        jTableOrderContent.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTableOrderContent.setForeground(java.awt.SystemColor.textHighlight);
        jTableOrderContent.setRowHeight(25);
        jScrollPaneOrderContent.setViewportView(jTableOrderContent);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Поиск товаров:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Артикул:");

        jTextFieldArticle.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Название:");

        jTextFieldName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jButtonSearch.setText("Искать");
        jButtonSearch.setToolTipText("искать товары");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jButtonAddGroupAll.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonAddGroupAll.setText("Авто заказ по группе");
        jButtonAddGroupAll.setToolTipText("Сформировать автом. заказ по группе товаров");
        jButtonAddGroupAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddGroupAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
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
                .addComponent(jButtonAddGroupAll, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAddGroupAll, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneOrderContent, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPaneGoodsList)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneGoodsList, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneOrderContent, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonDateDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDateDocActionPerformed
        jButtonDateDocActionPerformed();
    }//GEN-LAST:event_jButtonDateDocActionPerformed
    private void jComboBoxStatusDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxStatusDocActionPerformed
        if (evt.getModifiers()!=0)
            jComboBoxStatusDocActionPerformed();
    }//GEN-LAST:event_jComboBoxStatusDocActionPerformed
    private void jTextFieldNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNotesActionPerformed
        jTextFieldNotesActionPerformed();
    }//GEN-LAST:event_jTextFieldNotesActionPerformed
    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        jButtonSearchActionPerformed();
    }//GEN-LAST:event_jButtonSearchActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
		jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed

    private void jButtonAddGroupAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddGroupAllActionPerformed
        jButtonAddGroupAllActionPerformed();
    }//GEN-LAST:event_jButtonAddGroupAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAddGroupAll;
    private javax.swing.JButton jButtonDateDoc;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JComboBox jComboBoxStatusDoc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabel21;
    public javax.swing.JLabel jLabel22;
    public javax.swing.JLabel jLabel23;
    public javax.swing.JLabel jLabel27;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneGoodsList;
    private javax.swing.JScrollPane jScrollPaneOrderContent;
    private javax.swing.JTable jTableGoodsList;
    private javax.swing.JTable jTableOrderContent;
    private javax.swing.JTextField jTextFieldArticle;
    private javax.swing.JTextField jTextFieldDateDoc;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNotes;
    private javax.swing.JTextField jTextFieldNumberDoc;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
}
