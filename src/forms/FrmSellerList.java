package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import main.ConfigReader;
import main.DialogBoxs;
import tablemodel.TmSellerList;

public class FrmSellerList extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;
    public boolean blDisposeStatus = false;
    public int currentSellerID = 0;
    public int sellerID = 0;
	private long timeBarCode = 0;
	String searchSellerID = "";
	String searchSellerName = "";
			
    public FrmSellerList() {
        initComponents();
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
        setTitle("Список сотрудников. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        jLabelHelp.setText("<html>"
				+ "Backspace - поиск по ФИО" + "<br>"
				+ "Tab - перейти в след. поле" + "<br>"
				+ "Shift + Tab - перейти в пред. поле" + "<br>"
				+ "Enter - поиск / выбор" + "<br>"
				+ "</html>");
		requery();
		pack();
		setLocationRelativeTo(null);
		jTableSellerList.requestFocus();
	}
    
	private void requery(){
        jTableSellerList.setModel(new TmSellerList(getSellerListRS()));
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(jTableSellerList.getModel());
        jTableSellerList.setRowSorter(sorter);
        jTableSellerList.setDefaultRenderer(jTableSellerList.getColumnClass(0), new MyRendererTotal());
        jTableSellerList.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableSellerList.setRowHeight(25);
        jTableSellerList.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTableSellerList.getColumnModel().getColumn(1).setPreferredWidth(300);
        jTableSellerList.getColumnModel().getColumn(2).setPreferredWidth(200);
		if (jTableSellerList.getRowCount() > 0) {
			jTableSellerList.setRowSelectionInterval(0, 0);
		}

		requerySellerDesc();
		
		ListSelectionModel selModel = jTableSellerList.getSelectionModel();
		selModel.addListSelectionListener(new MyListSelectionListener());
    }
	private void requerySellerDesc(){
        if (cnn == null) return;
		int selectedRow = jTableSellerList.getSelectedRow();
		if (selectedRow == -1) return;
        int rowNum = jTableSellerList.getRowSorter().convertRowIndexToModel(selectedRow);
        currentSellerID = (int) jTableSellerList.getModel().getValueAt(rowNum, 0);
	}
			
	private void jButtonSearchActionPerformed(){
		requery();
		jTableSellerList.requestFocus();
	}
    private void jButtonOKActionPerformed(){
        sellerID = currentSellerID;
        blDisposeStatus = true;
		dispose();
    }
	private void jButtonExitActionPerformed() {
		sellerID = 0;
		dispose();
	}
    private ResultSet getSellerListRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getSellerList(jTextField2.getText(),jTextField3.getText());
        return rs;
    }
    public  class MyRendererTotal extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.RIGHT);
            }
            return this;
        }
    }
	private class MyRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 1) {
				setHorizontalAlignment(SwingConstants.RIGHT);
			} else {
				setHorizontalAlignment(SwingConstants.CENTER);
			}
			return this;
		}
	}
    public  class HeaderRenderer extends DefaultTableCellRenderer {
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
            if (e.getSource() == jTableSellerList && e.getKeyCode() == KeyEvent.VK_ENTER) {
                e.consume();
            }
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
//					else if (objCanonicalName.endsWith("JButton")) {
//						JButton bt = (JButton) e.getSource();
//						bt.transferFocus();
//					}
                    if (e.getSource() == jButtonSearch) jButtonSearchActionPerformed();
                    if (e.getSource() == jTableSellerList) jButtonOKActionPerformed();
                    if (e.getSource() == jButtonExit) jButtonExitActionPerformed();
                    break;
                case KeyEvent.VK_UP:
					if (e.getModifiers() != 0) {
						break;
					}
//                    if (objCanonicalName.endsWith("Field")) {
//                        JTextField tf = (JTextField) e.getSource();
//                        tf.transferFocusBackward();
//                    } else if (objCanonicalName.endsWith("JButton")) {
//                        JButton bt = (JButton) e.getSource();
//                        bt.transferFocusBackward();
//                    }
                    break;
                case KeyEvent.VK_DOWN:
					if (e.getModifiers() != 0) {
						break;
					}
//                    if (objCanonicalName.endsWith("Field")) {
//                        JTextField tf = (JTextField) e.getSource();
//                        tf.transferFocus();
//                    } else if (objCanonicalName.endsWith("JButton")) {
//                        JButton bt = (JButton) e.getSource();
//                        bt.transferFocus();
//                    }
                    break;
                case KeyEvent.VK_ESCAPE:
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonExitActionPerformed();
                    break;
				case KeyEvent.VK_BACK_SPACE:
					if (e.getModifiers() != 0) {
						break;
					}
					if (e.getSource() == jTableSellerList) jTextField3.requestFocus();
					if (e.getSource() == jButtonSearch) jTextField3.requestFocus();
					if (e.getSource() == jTextField3) {
						if (jTextField3.getText().equals(""))
							jTextField2.requestFocus();
					}
					break;
                default:
                    //String objCanonicalName = e.getSource().getClass().getCanonicalName();
                    //System.out.println("keyOverride: " + objCanonicalName + " keycode:" + Integer.toString(e.getKeyCode())+" "+String.valueOf(e.getKeyChar()));
					if (e.getSource() == jTableSellerList){
						try {
							long newtimeBarCode = new Date().getTime();
							if (timeBarCode + 3000 < newtimeBarCode) {
								searchSellerID = "";
								searchSellerName = "";
							}
							timeBarCode = new Date().getTime();
						} catch (NoSuchMethodError ex) {
						}
						if ((keyCode > 47 && keyCode < 58) || (keyCode > 95 && keyCode < 106)) {
							searchSellerID = searchSellerID.concat(Character.toString(e.getKeyChar()));
							searchSellerName = "";
						}else if (keyCode >= 70 && keyCode <= 90) {
							searchSellerName = searchSellerName.concat(Character.toString(e.getKeyChar()));
							searchSellerID = "";
						}else{
							break;
						}
						int selRow = jTableSellerList.getSelectedRow();
						if (selRow == -1) selRow = 0;
						if (searchSellerID.equals("") && searchSellerName.equals("")) {
							break;
						}
						if (jTableSellerList.getRowCount() < 1) {
							break;
						}
						int row = 0;
						for (row = 0; row <= jTableSellerList.getRowCount() - 1; row++) {
							String strSellerID = jTableSellerList.getValueAt(row, 0).toString();
							String strSellerName = jTableSellerList.getValueAt(row, 1).toString();
							if(searchSellerID.equals("")){
								if(strSellerName.toLowerCase().contains(searchSellerName.toLowerCase()))
									break;
							}
							if(searchSellerName.equals("")){
								if(strSellerID.toLowerCase().contains(searchSellerID.toLowerCase()))
									break;
							}
						}
						if (row == jTableSellerList.getRowCount()) {
							row = selRow;
						}
						Rectangle cellRect = jTableSellerList.getCellRect(row, 1, true);
						jTableSellerList.scrollRectToVisible(cellRect);
						jTableSellerList.setRowSelectionInterval(row, row);
						jTableSellerList.requestFocus();
					}
                    break;
            }
            return e;
        }
    }
	private class MyListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			requerySellerDesc();
		}
	}
    private void jTableSellerListMouseClicked(java.awt.event.MouseEvent evt, int i) {
        if (evt.getClickCount() == 1) {
            requerySellerDesc();
		}else if (evt.getClickCount() == 2) {
			requerySellerDesc();
			if (evt.getSource() == jTableSellerList) jButtonOKActionPerformed();
        } 
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneSellerList = new javax.swing.JScrollPane();
        jTableSellerList = new javax.swing.JTable();
        jPanelSearch = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButtonSearch = new javax.swing.JButton();
        jLabelHelp = new javax.swing.JLabel();
        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jScrollPaneSellerList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список сотрудников магазина:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneSellerList.setFocusable(false);
        jScrollPaneSellerList.setRequestFocusEnabled(false);

        jTableSellerList.setAutoCreateRowSorter(true);
        jTableSellerList.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jTableSellerList.setForeground(new java.awt.Color(0, 0, 102));
        jTableSellerList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableSellerList.setRowHeight(20);
        jTableSellerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableSellerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableSellerListMouseClicked(evt);
            }
        });
        jScrollPaneSellerList.setViewportView(jTableSellerList);

        jPanelSearch.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Поиск в списке сотрудников", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel2.setText("Номер:");
        jLabel2.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(0, 0, 204));
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField2.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel3.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel3.setText("ФИО:");
        jLabel3.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(0, 0, 204));
        jTextField3.setAutoscrolls(false);
        jTextField3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField3.setPreferredSize(new java.awt.Dimension(78, 22));

        jButtonSearch.setText("Поиск");
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jLabelHelp.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabelHelp.setForeground(new java.awt.Color(0, 153, 0));
        jLabelHelp.setText("Help");
        jLabelHelp.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabelHelp.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanelSearchLayout = new javax.swing.GroupLayout(jPanelSearch);
        jPanelSearch.setLayout(jPanelSearchLayout);
        jPanelSearchLayout.setHorizontalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelSearchLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSearch)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabelHelp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelSearchLayout.setVerticalGroup(
            jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSearchLayout.createSequentialGroup()
                .addGroup(jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonSearch, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelHelp)
                .addGap(6, 6, 6))
        );

        jPanelSearchLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButtonSearch, jLabel2, jLabel3, jTextField2, jTextField3});

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

        jButtonOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/ok-64.png"))); // NOI18N
        jButtonOK.setToolTipText("Выполнить");
        jButtonOK.setActionCommand("Поиск");
        jButtonOK.setAlignmentX(0.5F);
        jButtonOK.setBorderPainted(false);
        jButtonOK.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonOK.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonOK.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneSellerList, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneSellerList, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        jButtonOKActionPerformed();
    }//GEN-LAST:event_jButtonOKActionPerformed
    private void jTableSellerListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableSellerListMouseClicked
        jTableSellerListMouseClicked(evt,0);
    }//GEN-LAST:event_jTableSellerListMouseClicked
    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        jButtonSearchActionPerformed();
    }//GEN-LAST:event_jButtonSearchActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelHelp;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelSearch;
    private javax.swing.JScrollPane jScrollPaneSellerList;
    private javax.swing.JTable jTableSellerList;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
