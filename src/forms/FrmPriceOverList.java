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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import reports.ReportPriceOver;
import tablemodel.TmPriceOverContent;
import tablemodel.TmPriceOverList;

public class FrmPriceOverList extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;
    public boolean blDisposeStatus = false;
	private String currentDocID = "";

    public FrmPriceOverList() {
        initComponents();
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
        setTitle("Список переоценок. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        
		requery();
		setLocationRelativeTo(null);
		jTableDocList.requestFocus();
	}
    
	private void requery(){

        jTableDocList.setModel(new TmPriceOverList(getPriceOverListRS()));
        jTableDocList.setDefaultRenderer(jTableDocList.getColumnClass(0), new MyRendererTotal());
        jTableDocList.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableDocList.setRowHeight(17);
        jTableDocList.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTableDocList.getColumnModel().getColumn(1).setPreferredWidth(20);
        jTableDocList.getColumnModel().getColumn(2).setPreferredWidth(20);
        jTableDocList.getColumnModel().getColumn(3).setPreferredWidth(300);
		
		if (jTableDocList.getRowCount() > 0) {
			jTableDocList.setRowSelectionInterval(0, 0);
		}

		requeryDocList();
		
		ListSelectionModel selModel = jTableDocList.getSelectionModel();
		selModel.addListSelectionListener(new MyListSelectionListener());
    }
	private void requeryDocContent(String docID) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		jTableDocContent.setModel(new TmPriceOverContent(getPriceOverContentRS(docID)));
		jTableDocContent.setDefaultRenderer(jTableDocContent.getColumnClass(0), new MyRenderer());
		jTableDocContent.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		jTableDocContent.setRowHeight(25);
		jTableDocContent.getColumnModel().getColumn(0).setMinWidth(0);
		jTableDocContent.getColumnModel().getColumn(0).setMaxWidth(0);
		jTableDocContent.getColumnModel().getColumn(0).setResizable(false);
		jTableDocContent.getColumnModel().getColumn(1).setPreferredWidth(100);
		jTableDocContent.getColumnModel().getColumn(2).setPreferredWidth(200);
		jTableDocContent.getColumnModel().getColumn(3).setPreferredWidth(40);
		jTableDocContent.getColumnModel().getColumn(4).setPreferredWidth(40);
		jTableDocContent.getColumnModel().getColumn(5).setPreferredWidth(40);
		jTableDocContent.getColumnModel().getColumn(6).setPreferredWidth(40);
	}
	private void requeryDocList(){
		int selectedRow = jTableDocList.getSelectedRow();
		if (selectedRow == -1) {
			requeryDocContent("");
			return;
		}
		currentDocID = jTableDocList.getModel().getValueAt(selectedRow, 0).toString();
		requeryDocContent(currentDocID);
	}
			
	private void jButtonPrintCheckActionPerformed() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		int selectedRow = jTableDocList.getSelectedRow();
		if (selectedRow == -1) return;
		currentDocID = jTableDocList.getModel().getValueAt(selectedRow, 0).toString();
		ReportPriceOver reportPriceOver = new ReportPriceOver(currentDocID);
		reportPriceOver.setModal(true);
		reportPriceOver.setVisible(true);
	}
	private void jButtonSetStatusActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		int selectedRow = jTableDocList.getSelectedRow();
		if (selectedRow == -1) return;
		currentDocID = jTableDocList.getModel().getValueAt(selectedRow, 0).toString();
		int i = JOptionPane.showConfirmDialog(null, "Установить отметку \nо выполнении переценки товара?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (i == 0) {
			cnn.setPriceOverStatus(currentDocID);
		}
		requery();
		jTableDocList.requestFocus();
	}
	private void jButtonExitActionPerformed() {
        dispose();
    }

	private ResultSet getPriceOverContentRS(String docID) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return null;
		ResultSet rs = cnn.getPriceOverContent(docID);
		return rs;
	}
    private ResultSet getPriceOverListRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return null;
        }
        ResultSet rs = cnn.getPriceOverList();
        return rs;
    }
    public class MyRendererTotal extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column < 3) {
                setHorizontalAlignment(SwingConstants.CENTER);
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
			if (column == 3 || column == 4) {
				setHorizontalAlignment(SwingConstants.CENTER);
			} else if (column > 4) {
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
					jTableDocList.requestFocus();
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
			requeryDocList();
		}
	}

    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jButtonPrintCheck = new javax.swing.JButton();
        jButtonSetStatus = new javax.swing.JButton();
        jScrollPaneDocList = new javax.swing.JScrollPane();
        jTableDocList = new javax.swing.JTable();
        jScrollPaneDocContent = new javax.swing.JScrollPane();
        jTableDocContent = new javax.swing.JTable();

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
        jButtonSetStatus.setToolTipText("Уст. отметку о выполнении");
        jButtonSetStatus.setActionCommand("Печать");
        jButtonSetStatus.setBorderPainted(false);
        jButtonSetStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSetStatus.setIconTextGap(0);
        jButtonSetStatus.setLabel("<html><center>Уст. отметку о выполнении</html>");
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
                        .addGap(29, 29, 29)
                        .addGroup(jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonPrintCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelButtonLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonSetStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButtonPrintCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButtonSetStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPaneDocList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список документов:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneDocList.setFocusable(false);
        jScrollPaneDocList.setRequestFocusEnabled(false);

        jTableDocList.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableDocList.setForeground(new java.awt.Color(0, 0, 102));
        jTableDocList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDocList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPaneDocList.setViewportView(jTableDocList);

        jScrollPaneDocContent.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров в документе:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableDocContent.setAutoCreateRowSorter(true);
        jTableDocContent.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTableDocContent.setForeground(new java.awt.Color(0, 0, 102));
        jTableDocContent.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDocContent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableDocContentMouseClicked(evt);
            }
        });
        jScrollPaneDocContent.setViewportView(jTableDocContent);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPaneDocList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPaneDocContent, javax.swing.GroupLayout.PREFERRED_SIZE, 843, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPaneDocList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDocContent, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }//GEN-END:initComponents
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jTableDocContentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDocContentMouseClicked
        //if (evt.getClickCount() == 2) jTableCheckContentMouseClicked();
    }//GEN-LAST:event_jTableDocContentMouseClicked
    private void jButtonPrintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintCheckActionPerformed
        jButtonPrintCheckActionPerformed();
    }//GEN-LAST:event_jButtonPrintCheckActionPerformed
    private void jButtonSetStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetStatusActionPerformed
        jButtonSetStatusActionPerformed();
    }//GEN-LAST:event_jButtonSetStatusActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonPrintCheck;
    private javax.swing.JButton jButtonSetStatus;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JScrollPane jScrollPaneDocContent;
    private javax.swing.JScrollPane jScrollPaneDocList;
    private javax.swing.JTable jTableDocContent;
    private javax.swing.JTable jTableDocList;
    // End of variables declaration//GEN-END:variables
}
