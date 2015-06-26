package forms;

import datepicker.DatePicker;
import datepicker.ObservingTextField;
import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
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
import main.MyUtil;
import tablemodel.TmStickerContent;
import tablemodel.TmStickerList;
        
public class FrmStickerList extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;

    public FrmStickerList() {
        initComponents();
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
        setTitle("Список документов. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        
        final Locale locale = new Locale("ru");
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.DATE, -(6+calendar.get(GregorianCalendar.DAY_OF_WEEK)));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        jTextFieldDT_Start.setText(dateFormat.format(calendar.getTime()));
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.DATE, +(7+calendar.get(GregorianCalendar.DAY_OF_WEEK)));
        jTextFieldDT_Stop.setText(dateFormat.format(calendar.getTime()));
        
		requery();
		setLocationRelativeTo(null);
		jTableDocList.requestFocus();
	}
    
	private void requery(){
        int selectedRow = jTableDocList.getSelectedRow();
        jTableDocList.setModel(new TmStickerList(getDocListRS()));
        jTableDocList.setDefaultRenderer(jTableDocList.getColumnClass(0), new MyRendererTotal());
        jTableDocList.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        //"Док №","Дата","Примечание","К-во стикеров","К-во планок"
        jTableDocList.setRowHeight(17);
        jTableDocList.getColumnModel().getColumn(0).setPreferredWidth(20);
        jTableDocList.getColumnModel().getColumn(1).setPreferredWidth(50);
        jTableDocList.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTableDocList.getColumnModel().getColumn(3).setPreferredWidth(30);
        jTableDocList.getColumnModel().getColumn(4).setPreferredWidth(30);
		
//		if (jTableDocList.getRowCount() > 0) {
//			jTableDocList.setRowSelectionInterval(0, 0);
//		}
        if (jTableDocList.getRowCount() > 0) {
            if (selectedRow > jTableDocList.getRowCount() - 1) {
                selectedRow = jTableDocList.getRowCount() - 1;
            }
            if (selectedRow == -1) {
                jTableDocList.setRowSelectionInterval(0, 0);
            } else {
                jTableDocList.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }

		requeryDocList();
		
		ListSelectionModel selModel = jTableDocList.getSelectionModel();
		selModel.addListSelectionListener(new MyListSelectionListener());
    }
	private void requeryDocContent(BigDecimal docID) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
        //"ID", "Производитель", "Артикул", "Название", "Тип ценника", "Кол-во", "Цена"
		jTableDocContent.setModel(new TmStickerContent(getDocContentRS(docID)));
		jTableDocContent.setDefaultRenderer(jTableDocContent.getColumnClass(0), new MyRenderer());
		jTableDocContent.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		jTableDocContent.setRowHeight(25);
		jTableDocContent.getColumnModel().getColumn(0).setMinWidth(0);
		jTableDocContent.getColumnModel().getColumn(0).setMaxWidth(0);
		jTableDocContent.getColumnModel().getColumn(0).setResizable(false);
		jTableDocContent.getColumnModel().getColumn(1).setPreferredWidth(100);
		jTableDocContent.getColumnModel().getColumn(2).setPreferredWidth(200);
		jTableDocContent.getColumnModel().getColumn(3).setPreferredWidth(60);
		jTableDocContent.getColumnModel().getColumn(4).setPreferredWidth(60);
		//jTableDocContent.getColumnModel().getColumn(5).setPreferredWidth(40);
		//jTableDocContent.getColumnModel().getColumn(6).setPreferredWidth(40);
	}
	private void requeryDocList(){
		int selectedRow = jTableDocList.getSelectedRow();
		if (selectedRow == -1) {
			requeryDocContent(BigDecimal.ZERO);
			return;
		}
        int rowNum = jTableDocList.getRowSorter().convertRowIndexToModel(selectedRow);
        BigDecimal currentDocID = (BigDecimal) jTableDocList.getModel().getValueAt(rowNum, 0);
		requeryDocContent(currentDocID);
	}
			
    private void jButtonDT_StartActionPerformed(){
        final Locale locale = new Locale("ru");
        DatePicker dp = new DatePicker((Observer) jTextFieldDT_Start, locale);
        // previously selected date
        Date selectedDate = dp.parseDate(jTextFieldDT_Start.getText());
        dp.setSelectedDate(selectedDate);
        dp.start(jTextFieldDT_Start);
        dp.getScreen().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                //здесь обработчик срабатывает 2 раза - не понятно почему так
                requery();
            }
        });
    }
    private void jButtonDT_StopActionPerformed(){
        final Locale locale = new Locale("ru");
        DatePicker dp = new DatePicker((Observer) jTextFieldDT_Stop, locale);
        // previously selected date
        Date selectedDate = dp.parseDate(jTextFieldDT_Stop.getText());
        dp.setSelectedDate(selectedDate);
        dp.start(jTextFieldDT_Start);
        dp.getScreen().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                //здесь обработчик срабатывает 2 раза - не понятно почему так
                requery();
            }
        });
    }
    private void jButtonDocAddActionPerformed() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.newSticker().compareTo(BigDecimal.ZERO)==0){
            JOptionPane.showMessageDialog(null, "Возникла ошибка!\n\nСообщите разработчику.", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
        }
        requery();
    }
    private void jButtonDocEditActionPerformed() {
        int selectedRow = jTableDocList.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableDocList.getRowSorter().convertRowIndexToModel(selectedRow);
        BigDecimal currentDocID = (BigDecimal) jTableDocList.getModel().getValueAt(rowNum, 0);
//        String statusDoc = jTableDocList.getModel().getValueAt(rowNum, 3).toString();
//        if (!statusDoc.equals("предварительный")) {
//            DialogBoxs.viewMessage("Изменять можно только предварительные документы!");
//            return;
//        }
        final FrmStickerEdit frmStickerEdit = new FrmStickerEdit(currentDocID);
        frmStickerEdit.setModal(true);
		frmStickerEdit.setVisible(true);
        requery();
    }
    private void jButtonDocDelActionPerformed() {
        int selectedRow = jTableDocList.getSelectedRow();
        if (selectedRow == -1) return;
        int rowNum = jTableDocList.getRowSorter().convertRowIndexToModel(selectedRow);
        BigDecimal currentDocID = (BigDecimal) jTableDocList.getModel().getValueAt(rowNum, 0);
        int i = JOptionPane.showConfirmDialog(new JFrame(), "Подтвердите удаление:\nНомер: "+currentDocID.toString()+"\nДата: " + jTableDocList.getModel().getValueAt(rowNum, 1).toString() + "\nСтикеров: " + jTableDocList.getModel().getValueAt(rowNum, 3).toString() + "\nПланок: " + jTableDocList.getModel().getValueAt(rowNum, 4).toString() + "\nОписание: " + jTableDocList.getModel().getValueAt(rowNum, 2).toString(), "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (i == 0) {
            cnn.delSticker(currentDocID);
            requery();
        }
        jButtonDocAdd.requestFocus();
    }
	private void jButtonDocCopyActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		int selectedRow = jTableDocList.getSelectedRow();
		if (selectedRow == -1) return;
        int rowNum = jTableDocList.getRowSorter().convertRowIndexToModel(selectedRow);
        BigDecimal currentDocID = (BigDecimal) jTableDocList.getModel().getValueAt(rowNum, 0);
		int i = JOptionPane.showConfirmDialog(null, "Скопировать документ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (i == 0) {
            cnn.copySticker(currentDocID);
		}
		requery();
		jTableDocList.requestFocus();
	}
	private void jButtonExitActionPerformed() {
        dispose();
    }

	private ResultSet getDocContentRS(BigDecimal docID) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return null;
		ResultSet rs = cnn.getStickerContent(docID,"_view");
		return rs;
	}
    private ResultSet getDocListRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
        java.sql.Date dt1 = null, dt2 = null;
        try {
            if (!jTextFieldDT_Start.getText().equals("")) {
                dt1 = new java.sql.Date(dateFormat.parse(jTextFieldDT_Start.getText()).getTime());
            }
            if (!jTextFieldDT_Stop.getText().equals("")) {
                dt2 = new java.sql.Date(dateFormat.parse(jTextFieldDT_Stop.getText()).getTime());
            }
        } catch (ParseException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
            DialogBoxs.viewError(ex);
        }

        ResultSet rs = cnn.getStickerList(dt1,dt2);
        return rs;
    }
    public class MyRendererTotal extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) {
                setHorizontalAlignment(SwingConstants.CENTER);
            }else if (column == 1 || column == 2) {
                    setHorizontalAlignment(SwingConstants.LEFT);
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
			if (column >= 0 && column <= 2) {
				setHorizontalAlignment(SwingConstants.LEFT);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelFilter = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldDT_Start = new ObservingTextField();
        jButtonDT_Start = new javax.swing.JButton();
        jTextFieldDT_Stop = new ObservingTextField();
        jButtonDT_Stop = new javax.swing.JButton();
        jScrollPaneDocList = new javax.swing.JScrollPane();
        jTableDocList = new javax.swing.JTable();
        jPanelButtonMove = new javax.swing.JPanel();
        jButtonDocAdd = new javax.swing.JButton();
        jButtonDocEdit = new javax.swing.JButton();
        jButtonDocDel = new javax.swing.JButton();
        jButtonDocCopy = new javax.swing.JButton();
        jScrollPaneDocContent = new javax.swing.JScrollPane();
        jTableDocContent = new javax.swing.JTable();
        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelFilter.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Период", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel1.setText("Показывать документы с");

        jLabel2.setText("по");

        jTextFieldDT_Start.setEditable(false);
        jTextFieldDT_Start.setToolTipText("начальная дата");
        jTextFieldDT_Start.setFocusable(false);

        jButtonDT_Start.setText("...");
        jButtonDT_Start.setToolTipText("выбор даты");
        jButtonDT_Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDT_StartActionPerformed(evt);
            }
        });

        jTextFieldDT_Stop.setEditable(false);
        jTextFieldDT_Stop.setToolTipText("начальная дата");
        jTextFieldDT_Stop.setFocusable(false);

        jButtonDT_Stop.setText("...");
        jButtonDT_Stop.setToolTipText("выбор даты");
        jButtonDT_Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDT_StopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFilterLayout = new javax.swing.GroupLayout(jPanelFilter);
        jPanelFilter.setLayout(jPanelFilterLayout);
        jPanelFilterLayout.setHorizontalGroup(
            jPanelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFilterLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButtonDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldDT_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButtonDT_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFilterLayout.setVerticalGroup(
            jPanelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFilterLayout.createSequentialGroup()
                .addGroup(jPanelFilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDT_Start)
                    .addComponent(jTextFieldDT_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDT_Stop))
                .addGap(0, 0, 0))
        );

        jScrollPaneDocList.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список документов:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jScrollPaneDocList.setFocusable(false);
        jScrollPaneDocList.setRequestFocusEnabled(false);

        jTableDocList.setAutoCreateRowSorter(true);
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

        jPanelButtonMove.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));

        jButtonDocAdd.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonDocAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-add-32.png"))); // NOI18N
        jButtonDocAdd.setText("Новый");
        jButtonDocAdd.setToolTipText("Новый");
        jButtonDocAdd.setBorderPainted(false);
        jButtonDocAdd.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDocAdd.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDocAdd.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDocAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDocAddActionPerformed(evt);
            }
        });

        jButtonDocEdit.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonDocEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-edit-32.png"))); // NOI18N
        jButtonDocEdit.setText("Изменить");
        jButtonDocEdit.setToolTipText("Изменить");
        jButtonDocEdit.setBorderPainted(false);
        jButtonDocEdit.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDocEdit.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDocEdit.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDocEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDocEditActionPerformed(evt);
            }
        });

        jButtonDocDel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonDocDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-delete-32.png"))); // NOI18N
        jButtonDocDel.setText("Удалить");
        jButtonDocDel.setToolTipText("Удалить");
        jButtonDocDel.setBorderPainted(false);
        jButtonDocDel.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDocDel.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDocDel.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDocDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDocDelActionPerformed(evt);
            }
        });

        jButtonDocCopy.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButtonDocCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-edit-32.png"))); // NOI18N
        jButtonDocCopy.setText("Копировать");
        jButtonDocCopy.setToolTipText("Копировать");
        jButtonDocCopy.setBorderPainted(false);
        jButtonDocCopy.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDocCopy.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDocCopy.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDocCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDocCopyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelButtonMoveLayout = new javax.swing.GroupLayout(jPanelButtonMove);
        jPanelButtonMove.setLayout(jPanelButtonMoveLayout);
        jPanelButtonMoveLayout.setHorizontalGroup(
            jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonMoveLayout.createSequentialGroup()
                .addComponent(jButtonDocAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDocEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDocDel, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDocCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelButtonMoveLayout.setVerticalGroup(
            jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonMoveLayout.createSequentialGroup()
                .addGroup(jPanelButtonMoveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDocAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDocEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDocDel, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDocCopy, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

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
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(190, 190, 190))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneDocList, javax.swing.GroupLayout.DEFAULT_SIZE, 768, Short.MAX_VALUE)
                    .addComponent(jPanelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanelButtonMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPaneDocContent)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneDocList, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButtonMove, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneDocContent, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jTableDocContentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDocContentMouseClicked
        //if (evt.getClickCount() == 2) jTableCheckContentMouseClicked();
    }//GEN-LAST:event_jTableDocContentMouseClicked
    private void jButtonDocAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDocAddActionPerformed
        jButtonDocAddActionPerformed();
    }//GEN-LAST:event_jButtonDocAddActionPerformed
    private void jButtonDocEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDocEditActionPerformed
        jButtonDocEditActionPerformed();
    }//GEN-LAST:event_jButtonDocEditActionPerformed
    private void jButtonDocDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDocDelActionPerformed
        jButtonDocDelActionPerformed();
    }//GEN-LAST:event_jButtonDocDelActionPerformed
    private void jButtonDocCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDocCopyActionPerformed
        jButtonDocCopyActionPerformed();
    }//GEN-LAST:event_jButtonDocCopyActionPerformed
    private void jButtonDT_StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDT_StartActionPerformed
        jButtonDT_StartActionPerformed();
    }//GEN-LAST:event_jButtonDT_StartActionPerformed
    private void jButtonDT_StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDT_StopActionPerformed
        jButtonDT_StopActionPerformed();
    }//GEN-LAST:event_jButtonDT_StopActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDT_Start;
    private javax.swing.JButton jButtonDT_Stop;
    private javax.swing.JButton jButtonDocAdd;
    private javax.swing.JButton jButtonDocCopy;
    private javax.swing.JButton jButtonDocDel;
    private javax.swing.JButton jButtonDocEdit;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelButtonMove;
    private javax.swing.JPanel jPanelFilter;
    private javax.swing.JScrollPane jScrollPaneDocContent;
    private javax.swing.JScrollPane jScrollPaneDocList;
    private javax.swing.JTable jTableDocContent;
    private javax.swing.JTable jTableDocList;
    private javax.swing.JTextField jTextFieldDT_Start;
    private javax.swing.JTextField jTextFieldDT_Stop;
    // End of variables declaration//GEN-END:variables
}
