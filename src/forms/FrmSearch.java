package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import tablemodel.TmSearchContent;

public class FrmSearch extends javax.swing.JDialog {
    public int goodID = 0;
    private final ConfigReader conf;
    private ConnectionDb cnn;

	private void selectGood(){
        int selectedRow = jTableFind.getSelectedRow();
        if (selectedRow == -1) return;
		int rowNum = jTableFind.getRowSorter().convertRowIndexToModel(selectedRow);
        goodID = (int) jTableFind.getModel().getValueAt(rowNum, 0);
        dispose();
    }
    private void requery() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return;
        }
        jTableFind.setModel(new TmSearchContent(getSearchContentRS()));
//		RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jTableFind.getModel());
//		jTableFind.setRowSorter(sorter);
        jTableFind.setDefaultRenderer(jTableFind.getColumnClass(1), new FrmSearch.MyRenderer());
        jTableFind.setDefaultRenderer(jTableFind.getColumnClass(4), new FrmSearch.MyRenderer());
        jTableFind.setDefaultRenderer(jTableFind.getColumnClass(6), new FrmSearch.MyRenderer());
        jTableFind.getTableHeader().setDefaultRenderer(new FrmSearch.HeaderRenderer());

        jTableFind.setRowHeight(25);
        jTableFind.getColumnModel().getColumn(0).setMinWidth(0);
        jTableFind.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableFind.getColumnModel().getColumn(0).setResizable(false);
        jTableFind.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableFind.getColumnModel().getColumn(2).setPreferredWidth(300);
        jTableFind.getColumnModel().getColumn(3).setPreferredWidth(30);
        jTableFind.getColumnModel().getColumn(4).setPreferredWidth(30);
        jTableFind.getColumnModel().getColumn(5).setPreferredWidth(30);
        jTableFind.getColumnModel().getColumn(6).setPreferredWidth(30);
//        jTableFind.getColumnModel().getColumn(7).setPreferredWidth(30);
    }
    public FrmSearch() {
        initComponents();
        conf = ConfigReader.getInstance();
        setTitle("Поиск. "+conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        setLocationRelativeTo(null);

		requery();
        
        jTextField2.addKeyListener(new MyKeyListener());
        jTextField3.addKeyListener(new MyKeyListener());
        jButtonSearch.addKeyListener(new MyKeyListener());
        jButtonExit.addKeyListener(new MyKeyListener());
        jTableFind.addKeyListener(new MyKeyListener());
    }
    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                keyOverride(e);
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                jButtonExitActionPerformed();
            }
            super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
        }
        private KeyEvent keyOverride(KeyEvent e) {
			if (e.getModifiers() != 0) {
				return e;
			}
            String objCanonicalName = e.getSource().getClass().getCanonicalName();
            //System.out.println("keyOverride: "+ objCanonicalName + " keycode:" + Integer.toString(e.getKeyCode()));
            if (objCanonicalName.endsWith("JButton")) {
                JButton bt = (JButton) e.getSource();
                if (bt.getActionCommand().equals("Поиск")) {
                    jButtonSearchActionPerformed();
                }
                if (bt.getActionCommand().equals("Выход")) {
                    jButtonExitActionPerformed();
                }
            } else if (objCanonicalName.endsWith("Field")) {
                JTextField tf = (JTextField) e.getSource();
                tf.transferFocus();
            } else if (objCanonicalName.endsWith("JTable")) {
                JTable table = (JTable) e.getSource();
                if(table.getName().equals("jTableFind"))selectGood();
            }
            return e;
        }
    }

    private void jButtonSearchActionPerformed() {
        requery();
        jTableFind.requestFocus();
		if (jTableFind.getRowCount() > 0) {
			jTableFind.setRowSelectionInterval(0, 0);
		}
    }
    private void jButtonExitActionPerformed() {
        goodID = 0;
        dispose();
    }
    private void jTableFindMouseClicked(java.awt.event.MouseEvent e, int i){
        if (e.getClickCount() == 2) selectGood();
    }
    
    private ResultSet getSearchContentRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getSearchContent("",jTextField2.getText(),jTextField3.getText());
        return rs;
    }
    public class MyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			//System.out.println("row:"+Integer.toString(row)+"	column:" + Integer.toString(column)+ "	value="+value.toString());
            if (column == 1 || column == 2) {
				setHorizontalAlignment(SwingConstants.LEFT);
			} else if (column == 3 || column == 5) {
				setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.RIGHT);
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
//    class DecimalRenderer extends DefaultTableCellRenderer {
//        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
//        DecimalRenderer() {
//            setHorizontalAlignment(JLabel.RIGHT);
//        }
//        @Override
//        public void setValue(Object val) {
//            setText((val == null || ((Double) val).doubleValue() == 0) ? "" : formatter.format(((Double) val).doubleValue()));
//        }
//    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableFind = new javax.swing.JTable();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация для поиска:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(123, 85));

        jLabel2.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel2.setText("Артикул");
        jLabel2.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(0, 0, 204));
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField2.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel3.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel3.setText("Название");
        jLabel3.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(0, 0, 204));
        jTextField3.setAutoscrolls(false);
        jTextField3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField3.setPreferredSize(new java.awt.Dimension(78, 22));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/search.png"))); // NOI18N
        jButtonSearch.setToolTipText("Выполнить поиск товаров");
        jButtonSearch.setActionCommand("Поиск");
        jButtonSearch.setAlignmentX(0.5F);
        jButtonSearch.setBorderPainted(false);
        jButtonSearch.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonSearch.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonSearch.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список найденных товаров:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableFind.setAutoCreateRowSorter(true);
        jTableFind.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTableFind.setForeground(java.awt.SystemColor.textHighlight);
        jTableFind.setName("jTableFind"); // NOI18N
        jTableFind.setRowHeight(25);
        jTableFind.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableFind.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableFindMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableFind);

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
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(260, 260, 260)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jScrollPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        jButtonSearchActionPerformed();
    }//GEN-LAST:event_jButtonSearchActionPerformed
    private void jTableFindMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFindMouseClicked
        jTableFindMouseClicked(evt,0);
    }//GEN-LAST:event_jTableFindMouseClicked
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableFind;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
