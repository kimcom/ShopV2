package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import main.ConfigReader;
import main.DialogBoxs;
import reports.ReportCheck;
import tablemodel.TmCheckContent;
import tablemodel.TmCheckList;

public class FrmCheckList extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;
    public boolean blDisposeStatus = false;
	private BigDecimal currentCheckID = BigDecimal.ZERO;

    public FrmCheckList() {
        initComponents();
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
        setTitle("Список чеков. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        
		requery();
		setLocationRelativeTo(null);
		jTableCheckList.requestFocus();
	}
    
	private void requery(){

        jTableCheckList.setModel(new TmCheckList(getCheckListRS()));
        jTableCheckList.setDefaultRenderer(jTableCheckList.getColumnClass(0), new MyRendererTotal());
        jTableCheckList.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCheckList.setRowHeight(17);
        jTableCheckList.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTableCheckList.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableCheckList.getColumnModel().getColumn(2).setPreferredWidth(30);
        jTableCheckList.getColumnModel().getColumn(3).setPreferredWidth(30);
        jTableCheckList.getColumnModel().getColumn(4).setPreferredWidth(30);
        jTableCheckList.getColumnModel().getColumn(5).setPreferredWidth(30);
        jTableCheckList.getColumnModel().getColumn(6).setPreferredWidth(30);
		
		if (jTableCheckList.getRowCount() > 0) {
			//jTableChechList.setRowSelectionInterval(jTableChechList.getRowCount() - 1, jTableChechList.getRowCount() - 1);
			jTableCheckList.setRowSelectionInterval(0, 0);
		}

		requeryCheckList();
		
		ListSelectionModel selModel = jTableCheckList.getSelectionModel();
		selModel.addListSelectionListener(new MyListSelectionListener());
    }
	private void requeryCheckContent(BigDecimal checkID) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		jTableCheckContent.setModel(new TmCheckContent(getCheckContentRS(checkID)));
		jTableCheckContent.setDefaultRenderer(jTableCheckContent.getColumnClass(0), new MyRenderer());
		jTableCheckContent.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		jTableCheckContent.setRowHeight(25);
		jTableCheckContent.getColumnModel().getColumn(0).setMinWidth(0);
		jTableCheckContent.getColumnModel().getColumn(0).setMaxWidth(0);
		jTableCheckContent.getColumnModel().getColumn(0).setResizable(false);
		jTableCheckContent.getColumnModel().getColumn(1).setPreferredWidth(100);
		jTableCheckContent.getColumnModel().getColumn(2).setPreferredWidth(200);
		jTableCheckContent.getColumnModel().getColumn(3).setMinWidth(0);
		jTableCheckContent.getColumnModel().getColumn(3).setMaxWidth(0);
		jTableCheckContent.getColumnModel().getColumn(3).setResizable(false);
		jTableCheckContent.getColumnModel().getColumn(4).setPreferredWidth(40);
		jTableCheckContent.getColumnModel().getColumn(5).setPreferredWidth(40);
		jTableCheckContent.getColumnModel().getColumn(6).setPreferredWidth(40);
		jTableCheckContent.getColumnModel().getColumn(7).setPreferredWidth(40);
		jTableCheckContent.getColumnModel().getColumn(8).setPreferredWidth(40);
		jTableCheckContent.getColumnModel().getColumn(9).setPreferredWidth(40);
		jTableCheckContent.getColumnModel().getColumn(10).setMinWidth(0);
		jTableCheckContent.getColumnModel().getColumn(10).setMaxWidth(0);
		jTableCheckContent.getColumnModel().getColumn(10).setResizable(false);
	}
	private void requeryCheckList(){
		int selectedRow = jTableCheckList.getSelectedRow();
		if (selectedRow == -1) return;
		currentCheckID = new BigDecimal(jTableCheckList.getModel().getValueAt(selectedRow, 0).toString());
		requeryCheckContent(currentCheckID);
	}
			
	private void jTableCheckContentMouseClicked(){
	}
	private void jButtonPrintCheckActionPerformed(java.awt.event.ActionEvent evt, boolean blIconified) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		int selectedRow = jTableCheckList.getSelectedRow();
		if (selectedRow == -1) return;
		currentCheckID = new BigDecimal(jTableCheckList.getModel().getValueAt(selectedRow, 0).toString());
//currentCheckID = new BigDecimal(5458.1247);
		cnn.getCheckInfo(currentCheckID);
		if (cnn.checkIsBlank()) {
			DialogBoxs.viewMessage("Нулевые чеки не печатаем!");
			return;
		}
		if (cnn.checkStatus==0) {
			DialogBoxs.viewMessage("Копию чека можно делать\nтолько после распечатки\nиз главной формы!");
			return;
		}
		final ReportCheck rc = new ReportCheck(currentCheckID);
		rc.setModal(true);
		rc.setVisible(true);
		rc.dispose();
		cnn.getCheckInfo(cnn.currentCheckID);
//		if (!blIconified){
//			return;
//		}
		//rc.silentPrint();
	}
	private void jButtonSetStatusActionPerformed() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		int selectedRow = jTableCheckList.getSelectedRow();
		if (selectedRow == -1) return;
		currentCheckID = new BigDecimal(jTableCheckList.getModel().getValueAt(selectedRow, 0).toString());
		String typePay = jTableCheckList.getModel().getValueAt(selectedRow, 3).toString();
		String typePayNew = (typePay.equals("нал")) ? "безнал" : "нал";
		int i = JOptionPane.showConfirmDialog(null, "Чек №" + currentCheckID.toString() + "\nустановлен тип оплаты: "+typePay+"\n\nИзменить на "+typePayNew+"?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (i == 0) {
			if (typePayNew.equals("нал")){
				cnn.setCheckPaymentType(0,currentCheckID);
			} else {
				cnn.setCheckPaymentType(1, currentCheckID);
			}
		}
		requery();
		jTableCheckList.requestFocus();
	}
	private void jButtonExitActionPerformed() {
		dispose();
    }
	private void formWindowClosed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		cnn.lastCheck();
	}
	
	private ResultSet getCheckContentRS(BigDecimal checkID) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return null;
		ResultSet rs = cnn.getCheckContent(checkID);
		return rs;
	}
    private ResultSet getCheckListRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return null;
        }
        ResultSet rs = cnn.getCheckList();
        return rs;
    }
    public class MyRendererTotal extends DefaultTableCellRenderer {
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
					jTableCheckList.requestFocus();
                    break;
                default:
                    //String objCanonicalName = e.getSource().getClass().getCanonicalName();
                    //System.out.println("keyOverride: " + objCanonicalName + " keycode:" + Integer.toString(e.getKeyCode()));
                    break;
            }
            return e;
        }
    }
	private class MyListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			requeryCheckList();
		}
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jButtonPrintCheck = new javax.swing.JButton();
        jButtonSetStatus = new javax.swing.JButton();
        jScrollPaneCheckList = new javax.swing.JScrollPane();
        jTableCheckList = new javax.swing.JTable();
        jScrollPaneCheckContent = new javax.swing.JScrollPane();
        jTableCheckContent = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

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

        jButtonPrintCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check_close.png"))); // NOI18N
        jButtonPrintCheck.setToolTipText("Распечатать копию чека");
        jButtonPrintCheck.setActionCommand("Печать");
        jButtonPrintCheck.setBorderPainted(false);
        jButtonPrintCheck.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonPrintCheck.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonPrintCheck.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonPrintCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintCheckActionPerformed(evt);
            }
        });

        jButtonSetStatus.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jButtonSetStatus.setText("<html><center>Изм. нал/безнал</html>");
        jButtonSetStatus.setToolTipText("Изм. нал/безнал");
        jButtonSetStatus.setActionCommand("Печать");
        jButtonSetStatus.setBorderPainted(false);
        jButtonSetStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSetStatus.setIconTextGap(0);
        jButtonSetStatus.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonSetStatus.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonSetStatus.setPreferredSize(new java.awt.Dimension(100, 70));
        jButtonSetStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelButtonLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonSetStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelButtonLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonPrintCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonPrintCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonSetStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jScrollPaneCheckList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список чеков:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneCheckList.setFocusable(false);
        jScrollPaneCheckList.setRequestFocusEnabled(false);

        jTableCheckList.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableCheckList.setForeground(new java.awt.Color(0, 0, 102));
        jTableCheckList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCheckList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneCheckList.setViewportView(jTableCheckList);

        jScrollPaneCheckContent.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров в чеке:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableCheckContent.setAutoCreateRowSorter(true);
        jTableCheckContent.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableCheckContent.setForeground(new java.awt.Color(0, 0, 102));
        jTableCheckContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCheckContent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCheckContentMouseClicked(evt);
            }
        });
        jScrollPaneCheckContent.setViewportView(jTableCheckContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPaneCheckList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPaneCheckContent, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneCheckList, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneCheckContent, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jTableCheckContentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCheckContentMouseClicked
        if (evt.getClickCount() == 2) jTableCheckContentMouseClicked();
    }//GEN-LAST:event_jTableCheckContentMouseClicked
    private void jButtonPrintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintCheckActionPerformed
        jButtonPrintCheckActionPerformed( evt, true);
    }//GEN-LAST:event_jButtonPrintCheckActionPerformed
    private void jButtonSetStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetStatusActionPerformed
        jButtonSetStatusActionPerformed();
    }//GEN-LAST:event_jButtonSetStatusActionPerformed
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        formWindowClosed();
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonPrintCheck;
    private javax.swing.JButton jButtonSetStatus;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JScrollPane jScrollPaneCheckContent;
    private javax.swing.JScrollPane jScrollPaneCheckList;
    private javax.swing.JTable jTableCheckContent;
    private javax.swing.JTable jTableCheckList;
    // End of variables declaration//GEN-END:variables
}
