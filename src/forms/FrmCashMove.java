package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import tablemodel.TmCashMove;
import tablemodel.TmCashTotal;

public class FrmCashMove extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;
    public boolean blDisposeStatus = false;
	private BigDecimal currentMoveID = BigDecimal.ZERO;

    public FrmCashMove() {
        initComponents();
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
        setTitle("Движение денег в кассе. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        
		requery();
        setjPanelNewCashMove(false);
		setLocationRelativeTo(null);
		jButtonRecordAdd.requestFocus();
		jFormattedTextField1.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#0.00"))));
		jFormattedTextField1.addFocusListener(new MyUtil.MyFormatedTextFocusListener());
	}
    private void requery(){
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        jTableCashMove.setModel(new TmCashMove(getCashMove()));
        jTableCashMove.setDefaultRenderer(jTableCashMove.getColumnClass(0), new MyRenderer());
        jTableCashMove.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCashMove.setRowHeight(25);
        jTableCashMove.getColumnModel().getColumn(0).setMinWidth(0);
        jTableCashMove.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableCashMove.getColumnModel().getColumn(0).setResizable(false);
        jTableCashMove.getColumnModel().getColumn(1).setPreferredWidth(80);
        jTableCashMove.getColumnModel().getColumn(2).setPreferredWidth(30);
        jTableCashMove.getColumnModel().getColumn(3).setPreferredWidth(300);
        jTableCashMove.getColumnModel().getColumn(4).setPreferredWidth(80);

        jTableCashTotal.setModel(new TmCashTotal(getCashTotal()));
        jTableCashTotal.setDefaultRenderer(jTableCashTotal.getColumnClass(0), new MyRendererTotal());
        jTableCashTotal.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCashTotal.setRowHeight(17);
        jTableCashTotal.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTableCashTotal.getColumnModel().getColumn(1).setPreferredWidth(30);
        jTableCashTotal.getColumnModel().getColumn(2).setPreferredWidth(30);
        jTableCashTotal.getColumnModel().getColumn(3).setPreferredWidth(30);
    }

	private void jFormattedTextField1ActionPerformed(){
		if (jFormattedTextField1.getText().equals("")) jFormattedTextField1.setText("0");
		jFormattedTextField1.setText(jFormattedTextField1.getText().replace(".", ","));
		try {
			jFormattedTextField1.commitEdit();
		} catch (ParseException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			DialogBoxs.viewError(ex);
		}
	}
	private void setjPanelNewCashMove(boolean blVisible){
		if (!blVisible) currentMoveID = BigDecimal.ZERO;
		jPanelNewCashMove.setEnabled(blVisible);
		jPanelNewCashMove.setVisible(blVisible);
		pack();
	}
    private void jButtonRecordAddActionPerformed(){
		jFormattedTextField1.setValue(BigDecimal.ZERO);
        jTextField2.setText("");
		setjPanelNewCashMove(true);
        jFormattedTextField1.requestFocus();
    }
	private void jButtonRecordEditActionPerformed(){
		int selectedRow = jTableCashMove.getSelectedRow();
		if (selectedRow == -1) return;
		currentMoveID = (BigDecimal) jTableCashMove.getModel().getValueAt(selectedRow, 0);
		jFormattedTextField1.setValue(new BigDecimal(jTableCashMove.getModel().getValueAt(selectedRow, 2).toString()));
		jTextField2.setText(jTableCashMove.getModel().getValueAt(selectedRow, 3).toString());
		setjPanelNewCashMove(true);
		jFormattedTextField1.requestFocus();
	}
	private void jButtonRecordDelActionPerformed() {
		int selectedRow = jTableCashMove.getSelectedRow();
		if (selectedRow == -1) return;
		currentMoveID = (BigDecimal) jTableCashMove.getModel().getValueAt(selectedRow, 0);
		int i = JOptionPane.showConfirmDialog(this, "Подтвердите удаление:\nДата:"+jTableCashMove.getModel().getValueAt(selectedRow, 1).toString()+"\nСумма: "+jTableCashMove.getModel().getValueAt(selectedRow, 2).toString()+"\nОписание: "+jTableCashMove.getModel().getValueAt(selectedRow, 3).toString(),"ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (i==0) {
			cnn.delCashRecord(currentMoveID);
			requery();
		}
		jButtonRecordAdd.requestFocus();
	}
	private void jButtonRecordSaveActionPerformed(){
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		BigDecimal bdSumma = new BigDecimal(jFormattedTextField1.getValue().toString());
		if (bdSumma.compareTo(BigDecimal.ZERO)<0){
			DialogBoxs.viewMessage("Сумма должна быть положительной!");
			return;
		}
        if (bdSumma.equals(BigDecimal.ZERO)) {
			DialogBoxs.viewMessage("Необходимо указать сумму!");
			jFormattedTextField1.requestFocus();
			return;
		}
        if (jTextField2.getText().equals("")) {
			DialogBoxs.viewMessage("Необходимо указать причину выдачи!");
			jTextField2.requestFocus();
			return;
		}
		try {
			if (currentMoveID.compareTo(BigDecimal.ZERO)==0) {
				cnn.addCashNewRecord(bdSumma,jTextField2.getText());
			} else {
				cnn.editCashRecord(currentMoveID, bdSumma, jTextField2.getText());
			}
			requery();
			setjPanelNewCashMove(false);
			jButtonRecordAdd.requestFocus();
		} catch (NumberFormatException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
		} 
    }
	private void jButtonSetCashInputActionPerformed() {
		String sum_cash = JOptionPane.showInputDialog(this, "Введите сумму начального остатка в кассе:", "ВНИМАНИЕ!", JOptionPane.QUESTION_MESSAGE);
		if (sum_cash.contains("-")) {
			DialogBoxs.viewMessage("Сумма должна быть положительной!");
			return;
		}
		if (!sum_cash.equals("")) {
			cnn.setCashStart(new BigDecimal(sum_cash));
			requery();
		}
		jButtonRecordAdd.requestFocus();
	}
	private void jTableCashMoveMouseClicked(){
		jButtonRecordEditActionPerformed();
	}
	private void jButtonExitActionPerformed() {
        dispose();
    }

    private ResultSet getCashMove() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return null;
        }
        ResultSet rs = cnn.getCashMove();
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
            if (column == 2) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (column == 1) {
                setHorizontalAlignment(SwingConstants.CENTER);
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
                    if (e.getSource() == jButtonRecordAdd) jButtonRecordAddActionPerformed();
                    if (e.getSource() == jButtonRecordSave) jButtonRecordSaveActionPerformed();
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
                    setjPanelNewCashMove(false);
					jButtonRecordAdd.requestFocus();
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

        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jScrollPaneCashTotal = new javax.swing.JScrollPane();
        jTableCashTotal = new javax.swing.JTable();
        jPanelButtonMove = new javax.swing.JPanel();
        jButtonRecordAdd = new javax.swing.JButton();
        jButtonRecordEdit = new javax.swing.JButton();
        jButtonRecordDel = new javax.swing.JButton();
        jButtonSetCashInput = new javax.swing.JButton();
        jPanelNewCashMove = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButtonRecordSave = new javax.swing.JButton();
        jScrollPaneCashMove = new javax.swing.JScrollPane();
        jTableCashMove = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelButton.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jScrollPaneCashTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Краткий отчет по кассе:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
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

        jButtonRecordAdd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonRecordAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-add-32.png"))); // NOI18N
        jButtonRecordAdd.setText("Добавить запись");
        jButtonRecordAdd.setToolTipText("Добавить выдачу денег");
        jButtonRecordAdd.setActionCommand("Добавить выдачу денег");
        jButtonRecordAdd.setBorderPainted(false);
        jButtonRecordAdd.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonRecordAdd.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonRecordAdd.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonRecordAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecordAddActionPerformed(evt);
            }
        });

        jButtonRecordEdit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonRecordEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-edit-32.png"))); // NOI18N
        jButtonRecordEdit.setText("Изменить запись");
        jButtonRecordEdit.setToolTipText("Изменить запись");
        jButtonRecordEdit.setBorderPainted(false);
        jButtonRecordEdit.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonRecordEdit.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonRecordEdit.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonRecordEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecordEditActionPerformed(evt);
            }
        });

        jButtonRecordDel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonRecordDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-delete-32.png"))); // NOI18N
        jButtonRecordDel.setText("Удалить запись");
        jButtonRecordDel.setToolTipText("Удалить запись");
        jButtonRecordDel.setBorderPainted(false);
        jButtonRecordDel.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonRecordDel.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonRecordDel.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonRecordDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecordDelActionPerformed(evt);
            }
        });

        jButtonSetCashInput.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonSetCashInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Cash-register-32.png"))); // NOI18N
        jButtonSetCashInput.setText("Нач. ост. в кассе");
        jButtonSetCashInput.setToolTipText("Установить нач. ост.");
        jButtonSetCashInput.setActionCommand("Установить нач. ост.");
        jButtonSetCashInput.setBorderPainted(false);
        jButtonSetCashInput.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonSetCashInput.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonSetCashInput.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonSetCashInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetCashInputActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonMoveLayout = new javax.swing.GroupLayout(jPanelButtonMove);
        jPanelButtonMove.setLayout(jPanelButtonMoveLayout);
        jPanelButtonMoveLayout.setHorizontalGroup(
            jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonMoveLayout.createSequentialGroup()
                .addComponent(jButtonRecordAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRecordEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonRecordDel, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSetCashInput, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jPanelButtonMoveLayout.setVerticalGroup(
            jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonMoveLayout.createSequentialGroup()
                .addGroup(jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRecordAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRecordEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonRecordDel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSetCashInput, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanelNewCashMove.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Введите информацию о выдаче денежных средств:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel1.setText("Сумма выдачи:");

        jFormattedTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jFormattedTextField1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFormattedTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jFormattedTextField1FocusLost(evt);
            }
        });
        jFormattedTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Причина выдачи:");

        jTextField2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonRecordSave.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonRecordSave.setText("Сохранить");
        jButtonRecordSave.setToolTipText("Сохранить инф. о движ. денег");
        jButtonRecordSave.setBorderPainted(false);
        jButtonRecordSave.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonRecordSave.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonRecordSave.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonRecordSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecordSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelNewCashMoveLayout = new javax.swing.GroupLayout(jPanelNewCashMove);
        jPanelNewCashMove.setLayout(jPanelNewCashMoveLayout);
        jPanelNewCashMoveLayout.setHorizontalGroup(
            jPanelNewCashMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNewCashMoveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2)
                .addGap(18, 18, 18)
                .addComponent(jButtonRecordSave, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelNewCashMoveLayout.setVerticalGroup(
            jPanelNewCashMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelNewCashMoveLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelNewCashMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jButtonRecordSave, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jScrollPaneCashMove.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Движение средств по кассе:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableCashMove.setAutoCreateRowSorter(true);
        jTableCashMove.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableCashMove.setForeground(new java.awt.Color(0, 0, 102));
        jTableCashMove.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCashMove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCashMoveMouseClicked(evt);
            }
        });
        jScrollPaneCashMove.setViewportView(jTableCashMove);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPaneCashTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanelNewCashMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelButtonMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPaneCashMove)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneCashTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jPanelButtonMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanelNewCashMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPaneCashMove, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jButtonRecordSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecordSaveActionPerformed
        jButtonRecordSaveActionPerformed();
    }//GEN-LAST:event_jButtonRecordSaveActionPerformed
    private void jTableCashMoveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCashMoveMouseClicked
        if (evt.getClickCount() == 2) jTableCashMoveMouseClicked();
    }//GEN-LAST:event_jTableCashMoveMouseClicked
    private void jButtonRecordAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecordAddActionPerformed
        jButtonRecordAddActionPerformed();
    }//GEN-LAST:event_jButtonRecordAddActionPerformed
    private void jButtonRecordEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecordEditActionPerformed
        jButtonRecordEditActionPerformed();
    }//GEN-LAST:event_jButtonRecordEditActionPerformed
    private void jButtonRecordDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecordDelActionPerformed
        jButtonRecordDelActionPerformed();
    }//GEN-LAST:event_jButtonRecordDelActionPerformed
    private void jButtonSetCashInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetCashInputActionPerformed
        jButtonSetCashInputActionPerformed();
    }//GEN-LAST:event_jButtonSetCashInputActionPerformed
    private void jFormattedTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextField1ActionPerformed
        jFormattedTextField1ActionPerformed();
    }//GEN-LAST:event_jFormattedTextField1ActionPerformed
    private void jFormattedTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jFormattedTextField1FocusLost
		jFormattedTextField1ActionPerformed();
    }//GEN-LAST:event_jFormattedTextField1FocusLost
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonRecordAdd;
    private javax.swing.JButton jButtonRecordDel;
    private javax.swing.JButton jButtonRecordEdit;
    private javax.swing.JButton jButtonRecordSave;
    private javax.swing.JButton jButtonSetCashInput;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelButtonMove;
    private javax.swing.JPanel jPanelNewCashMove;
    private javax.swing.JScrollPane jScrollPaneCashMove;
    private javax.swing.JScrollPane jScrollPaneCashTotal;
    private javax.swing.JTable jTableCashMove;
    private javax.swing.JTable jTableCashTotal;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
