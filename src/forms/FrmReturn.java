package forms;

import db.ConnectionDb;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import tablemodel.TmCheckContent;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyTimerTask;
import main.MyUtil;
import main.Updater;
import reports.ReportCheck;

public class FrmReturn extends javax.swing.JDialog {
    private static FrmReturn instance = null;
    private ConfigReader conf;
    private ConnectionDb cnn;
	private boolean cnnState = true;
    private static String barCode = "";
	private boolean blDiscountCardFuture = false;
	private int countRows=0;

	public FrmReturn() {
		initComponents();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		dim.height = dim.height - 25;
		setSize(dim);
		conf = ConfigReader.getInstance();
		setTitle(conf.FORM_TITLE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
		setLocationRelativeTo(null);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		requery();
//временно
//setInvisible(jButtonPromo);
//setInvisible(jButtonSeller);
		//назначение MyKeyListener
		getAllComponents((Container) this.getContentPane());
		//setVisible(true);
		//уст. русской раскладки для клавиатуры
		this.getInputContext().selectInputMethod(new Locale("ru", "RU"));
//		toFront();
//		requestFocus();
//		setAutoRequestFocus(true);
//jButtonDiscountActionPerformed();
//jButtonDiscountCardActionPerformed();
//jButtonNewDiscountCardActionPerformed();
//jButtonAdminActionPerformed();
//FrmCashMove.getInstance(this, 0);
//ReportCash.getInstance(this, false);
//ReportSale.getInstance(this, false);
//ReportBarcodeShort.getInstance(this, false,2);
//FrmPriceOverList.getInstance(this, 0);
	}
   
    private ResultSet getCheckContentRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getCheckContent(cnn.currentCheckID);
        return rs;
    }
    private ResultSet getCheckContentLastModiRS() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return null;
        ResultSet rs = cnn.getCheckContentLastModi(cnn.currentCheckID);
        return rs;
    }
    private class MyRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if(column > 3 && column !=10){
                setHorizontalAlignment(SwingConstants.RIGHT);
            }else{
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            return this;
        }
    }
    private class HeaderRenderer extends DefaultTableCellRenderer {
        // метод возвращает компонент для прорисовки
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
				//if(row == -1) return new JLabel();
				JLabel label
						= (JLabel) super.getTableCellRendererComponent(
								table, value, isSelected, hasFocus,
								row, column);
				label.setBorder(BorderFactory.createLineBorder(java.awt.Color.gray));
				label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
				label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				return label;
			} catch (IndexOutOfBoundsException ex) {
//				MyUtil.errorToLog(this.getClass().getName(), ex);
//				System.out.println("errror row:" + Integer.toString(row) + "	col:" + Integer.toString(column));
//				DialogBoxs.viewError(ex);
				return new JLabel();
			}
        }
    }
    private class DecimalRenderer extends DefaultTableCellRenderer {
        DecimalFormat formatter = new DecimalFormat("###,###,###.00");
        DecimalRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }
        @Override
        public void setValue(Object val) {
            setText((val == null || ((Double) val).doubleValue() == 0) ? "" : formatter.format(((Double) val).doubleValue()));
        }
    }

	private void setFocusjTableCheck(FocusEvent e) {
		if (jTableCheck.getRowCount() == 0) {
			return;
		}
		jTableCheck.requestFocus();
		int selectedRow = jTableCheck.getSelectedRow();
		if (jTableCheck.getRowCount() > 0) {
			if (selectedRow > jTableCheck.getRowCount() - 1) {
				selectedRow = jTableCheck.getRowCount() - 1;
			}
			if (selectedRow == -1) {
				jTableCheck.setRowSelectionInterval(jTableCheck.getRowCount() - 1, jTableCheck.getRowCount() - 1);
			} else {
				jTableCheck.setRowSelectionInterval(selectedRow, selectedRow);
			}
		}
	}
	
	private List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
//            String canonicalName = comp.getClass().getCanonicalName();
//            System.out.println("addKeyListener: " + comp.getName() + "  " + canonicalName + "    focus=" + comp.isDisplayable());
            if(comp.isDisplayable())comp.addKeyListener(new MyKeyListener());
            if(comp.isFocusable()) comp.addFocusListener(new MyFocusListener());
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }
    private class MyFocusListener implements FocusListener{
        @Override
        public void focusGained(FocusEvent e) {
//            System.out.println("gain: " + e.getSource().getClass().getCanonicalName());
            if (e.getSource() != jTableCheck) setFocusjTableCheck(e);
        }
        @Override
        public void focusLost(FocusEvent e) {
            //System.out.println("lost: " + e.getSource().getClass().getCanonicalName());
//            if (e.getSource() == jTableCheck) setFocusjTableCheck(e);
        }
    }
    private class MyKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
			if (!checkCnnStatus()) {
				e.consume();
				return;
			}
            keyOverride(e);
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getSource() == jTableCheck) 
                e.consume();
            if (e.getKeyCode() == KeyEvent.VK_F10) 
                e.consume();
            super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
        }
        private KeyEvent keyOverride(KeyEvent e) {
            int keyCode = e.getKeyCode();
            switch (keyCode) {
                case KeyEvent.VK_DIVIDE:   // тест
                    if (e.getModifiers() == InputEvent.SHIFT_MASK) {
						//PaginationExample pe = new PaginationExample(0);
					} else {
						if (e.getModifiers() != 0) {
							break;
						}
						//ReportSaleTest.getInstance(instance, false);
						//Print2DPrinterJob.main();
					}
					break;
                case KeyEvent.VK_ENTER:    // штрих-код
					if (e.getModifiers() != 0) {
						break;
					}
					if (blDiscountCardFuture) break;
					//barCode = "3182550711142";
					if (barCode.equals("")) break;
					int res = cnn.addGoodInCheck(barCode);
					if (res == 1) {
						requery();
					}else if(res == -99999)  {
						DialogBoxs.viewMessage("Чек №"+cnn.currentCheckID.toString()+" уже закрыт!\n\nБудет создан новый чек!\n\nТовар нужно будет ввести снова!");
						jButtonNewCheckActionPerformed();
					} else {
						DialogBoxs.viewMessage("Не найден товар с штрих-кодом: "+barCode+"\n\nНайдите товар с помощью поиска");
						if (barCode.length() < 8) barCode = "";
						jButtonSearchActionPerformed(barCode);
					}
					barCode = "";
					break;
                case KeyEvent.VK_MINUS:       // удалить кол-во по тек.товару
					if (e.getModifiers() != 0) {
						break;
					}
					goodOperationQuantity("del");
                    break;
                case KeyEvent.VK_SUBTRACT:       // удалить кол-во по тек.товару
					if (e.getModifiers() != 0) {
						break;
					}
                    goodOperationQuantity("del");
                    break;
                case KeyEvent.VK_ADD:       // добавить кол-во по тек.товару
					if (e.getModifiers() != 0) {
						break;
					}
                    goodOperationQuantity("add");
                    break;
                case KeyEvent.VK_MULTIPLY:       // изменить кол-во по тек.товару
					if (e.getModifiers() != 0) {
						break;
					}
                    editQuantity();
                    break;
//                case KeyEvent.VK_F5:        // список сотрудников
//					if (e.getModifiers() != 0) {
//						break;
//					}
//                    jButtonSellerActionPerformed(0);
//                    break;
//                case KeyEvent.VK_SPACE:        // список сотрудников на конкретный товар
//					if (e.getModifiers() != 0) {
//						break;
//					}
//					int selectedRow = jTableCheck.getSelectedRow();
//					if (selectedRow == -1) break;
//					int rowNum = jTableCheck.getRowSorter().convertRowIndexToModel(selectedRow);
//					int goodID = Integer.parseInt(jTableCheck.getModel().getValueAt(rowNum, 0).toString());
//                    jButtonSellerActionPerformed(goodID);
//                    break;
//                case KeyEvent.VK_F7:        // дисконтная карта
//					if (e.getModifiers() != 0) {
//						break;
//					}
//                    jButtonDiscountCardActionPerformed();
//                    break;
                case KeyEvent.VK_F8:        // печать
                    if(e.getModifiers() == InputEvent.SHIFT_MASK){
						jButtonPrintCheckActionPerformed(null,false);
                    } else {
						if (e.getModifiers() != 0) {
							break;
						}
                        jButtonPrintCheckActionPerformed(null,true);
                    }
                    break;
                case KeyEvent.VK_F9:        // тесты разные
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonCalcActionPerformed();
                    break;
//                case KeyEvent.VK_F10:        // ручная скидка
//					if (e.getModifiers() != 0) {
//						break;
//					}
//                    jButtonDiscountActionPerformed();
//                    break;
                case KeyEvent.VK_F3:        // поиск
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonSearchActionPerformed("");
                    break;
                case KeyEvent.VK_INSERT:    // поиск
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonSearchActionPerformed("");
                    break;
                default:
                    //String objCanonicalName = e.getSource().getClass().getCanonicalName();
                    //System.out.println("keyOverride: " + objCanonicalName + " keycode:" + Integer.toString(e.getKeyCode()));
                    //System.out.println("keycode:" + Integer.toString(e.getKeyCode()));
                    if((keyCode > 47 && keyCode < 58)||(keyCode > 95 && keyCode < 106))
                        barCode = barCode.concat(Character.toString(e.getKeyChar()));
                    //System.out.println(barCode);
                    break;
            }
            return e;
        }        
    }
    
	private void setInvisible(Component comp) {
		comp.setEnabled(false);
		comp.setFocusable(false);
		comp.setVisible(false);
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPaneCheck = new javax.swing.JScrollPane();
        jTableCheck = new javax.swing.JTable();
        jPanelLeft = new javax.swing.JPanel();
        jButtonPrintCheck = new javax.swing.JButton();
        jButtonSearch = new javax.swing.JButton();
        jButtonDiscount = new javax.swing.JButton();
        jButtonDiscountCard = new javax.swing.JButton();
        jButtonCalc = new javax.swing.JButton();
        jButtonPayType = new javax.swing.JButton();
        jLabelReturn = new javax.swing.JLabel();
        jPanelRight = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jPanelMiddle = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanelLastModi = new javax.swing.JPanel();
        jTableCheckLastModi = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jScrollPaneCheck.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Список товаров в чеке:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableCheck.setAutoCreateRowSorter(true);
        jTableCheck.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTableCheck.setForeground(java.awt.SystemColor.textHighlight);
        jTableCheck.setRowHeight(25);
        jScrollPaneCheck.setViewportView(jTableCheck);

        jButtonPrintCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check_close.png"))); // NOI18N
        jButtonPrintCheck.setToolTipText("F8 и Shift+F8 - Распечатать и закрыть чек");
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

        jButtonSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/search.png"))); // NOI18N
        jButtonSearch.setToolTipText("Insert - Поиск товара");
        jButtonSearch.setActionCommand("Поиск");
        jButtonSearch.setBorderPainted(false);
        jButtonSearch.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonSearch.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonSearch.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchActionPerformed(evt);
            }
        });

        jButtonDiscount.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Percent-64.png"))); // NOI18N
        jButtonDiscount.setToolTipText("F10 - Ввести скидку ");
        jButtonDiscount.setActionCommand("Ввод скидки");
        jButtonDiscount.setBorderPainted(false);
        jButtonDiscount.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDiscount.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDiscount.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDiscount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscountActionPerformed(evt);
            }
        });

        jButtonDiscountCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/card-client 64.png"))); // NOI18N
        jButtonDiscountCard.setToolTipText("F7 - Ввод дисконтной карты");
        jButtonDiscountCard.setActionCommand("Ввод дисконтной карты");
        jButtonDiscountCard.setBorderPainted(false);
        jButtonDiscountCard.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDiscountCard.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDiscountCard.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDiscountCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscountCardActionPerformed(evt);
            }
        });

        jButtonCalc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/calculator-64.png"))); // NOI18N
        jButtonCalc.setToolTipText("F9 - Расчет сдачи");
        jButtonCalc.setActionCommand("Калькулятор");
        jButtonCalc.setBorderPainted(false);
        jButtonCalc.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonCalc.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonCalc.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonCalc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCalcActionPerformed(evt);
            }
        });

        jButtonPayType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/credit-cards-64.png"))); // NOI18N
        jButtonPayType.setToolTipText("Безналичный расчет");
        jButtonPayType.setActionCommand("Нал/Безнал");
        jButtonPayType.setBorderPainted(false);
        jButtonPayType.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonPayType.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonPayType.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonPayType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPayTypeActionPerformed(evt);
            }
        });

        jLabelReturn.setFont(new java.awt.Font("Tahoma", 3, 30)); // NOI18N
        jLabelReturn.setForeground(java.awt.SystemColor.activeCaption);
        jLabelReturn.setText("<html><center>Оформление<br>возврата товара</html>");

        javax.swing.GroupLayout jPanelLeftLayout = new javax.swing.GroupLayout(jPanelLeft);
        jPanelLeft.setLayout(jPanelLeftLayout);
        jPanelLeftLayout.setHorizontalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addComponent(jButtonPrintCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDiscountCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCalc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPayType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jLabelReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelLeftLayout.setVerticalGroup(
            jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLeftLayout.createSequentialGroup()
                .addGroup(jPanelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonPrintCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDiscount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPayType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCalc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDiscountCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelReturn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addGap(0, 304, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanelMiddle.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о текущем чеке:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(java.awt.SystemColor.desktop);
        jLabel1.setText("№ чека");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(java.awt.SystemColor.desktop);
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(java.awt.SystemColor.desktop);
        jLabel3.setText("Вид оплаты:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(java.awt.SystemColor.desktop);
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setForeground(java.awt.SystemColor.desktop);
        jLabel5.setText("Сумма без скидки:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setForeground(java.awt.SystemColor.desktop);
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(java.awt.SystemColor.desktop);
        jLabel7.setText("Сумма скидки:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel8.setForeground(java.awt.SystemColor.desktop);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(java.awt.SystemColor.textHighlight);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("К оплате: ");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel10.setForeground(java.awt.SystemColor.textHighlight);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("90.00");

        javax.swing.GroupLayout jPanelMiddleLayout = new javax.swing.GroupLayout(jPanelMiddle);
        jPanelMiddle.setLayout(jPanelMiddleLayout);
        jPanelMiddleLayout.setHorizontalGroup(
            jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMiddleLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(115, 115, 115)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelMiddleLayout.setVerticalGroup(
            jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMiddleLayout.createSequentialGroup()
                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMiddleLayout.createSequentialGroup()
                        .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMiddleLayout.createSequentialGroup()
                                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanelMiddleLayout.createSequentialGroup()
                                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMiddleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel1.getAccessibleContext().setAccessibleName("№ чека:");
        jLabel3.getAccessibleContext().setAccessibleName("");

        jPanelLastModi.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Последний добавленный товар:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTableCheckLastModi.setAutoCreateRowSorter(true);
        jTableCheckLastModi.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTableCheckLastModi.setForeground(java.awt.SystemColor.desktop);
        jTableCheckLastModi.setFocusable(false);
        jTableCheckLastModi.setOpaque(false);
        jTableCheckLastModi.setRowHeight(25);

        javax.swing.GroupLayout jPanelLastModiLayout = new javax.swing.GroupLayout(jPanelLastModi);
        jPanelLastModi.setLayout(jPanelLastModiLayout);
        jPanelLastModiLayout.setHorizontalGroup(
            jPanelLastModiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanelLastModiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTableCheckLastModi, javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE))
        );
        jPanelLastModiLayout.setVerticalGroup(
            jPanelLastModiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanelLastModiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTableCheckLastModi, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPaneCheck)
            .addComponent(jPanelLastModi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanelMiddle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanelLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelLastModi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private boolean checkCnnStatus() {
		if(!cnnState)
			JOptionPane.showMessageDialog(null, "Нет связи с сервером!\n\nПовторите операцию позже.", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE,new javax.swing.ImageIcon(getClass().getResource("/png/connect lost.png")));
		return cnnState;
	}
	public void setCnnStatus(int status){
		if (status==0) {
			cnnState = true;
		}
		if (status==1) {
			cnnState = false;
		}
		if (status==2) {
			cnnState = false;
		}
	}
    private void requery() {
		if(!checkCnnStatus())return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		//cnn.setFlagReturn(true);
		cnn.lastCheck();
        if (cnn.currentCheckID == null) return;
        if (!cnn.statusValid()) return;
        int selectedRow = jTableCheck.getSelectedRow();
//System.out.println("cnn.currentCheckID:"+cnn.currentCheckID.toString());
        jLabel2.setText(cnn.currentCheckID.setScale(4, RoundingMode.HALF_UP).toPlainString());
        jLabel4.setText(cnn.checkTypePayment == 1 ? "безналичный расчет" : "наличный расчет");
        jLabel6.setText(cnn.checkSumBase.toString());
        jLabel8.setText(cnn.checkSumDiscount.toString());
        jLabel10.setText(cnn.checkSum.toString());

        jTableCheck.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTableCheck.setModel(new TmCheckContent(getCheckContentRS()));
        jTableCheck.setDefaultRenderer(jTableCheck.getColumnClass(0), new MyRenderer());
        jTableCheck.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCheckLastModi.setModel(new TmCheckContent(getCheckContentLastModiRS()));
        jTableCheckLastModi.setDefaultRenderer(jTableCheckLastModi.getColumnClass(0), new MyRenderer());
        //jTableCheckLastModi.getTableHeader().setDefaultRenderer(new HeaderRenderer());

        jTableCheck.setRowHeight(25);
        jTableCheck.getColumnModel().getColumn(0).setMinWidth(0);
        jTableCheck.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableCheck.getColumnModel().getColumn(0).setResizable(false);
        jTableCheck.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableCheck.getColumnModel().getColumn(2).setPreferredWidth(300);
        jTableCheck.getColumnModel().getColumn(3).setPreferredWidth(70);
        jTableCheck.getColumnModel().getColumn(4).setPreferredWidth(40);
        jTableCheck.getColumnModel().getColumn(5).setPreferredWidth(40);
        jTableCheck.getColumnModel().getColumn(6).setPreferredWidth(40);
        jTableCheck.getColumnModel().getColumn(7).setPreferredWidth(40);
        jTableCheck.getColumnModel().getColumn(8).setPreferredWidth(40);
        jTableCheck.getColumnModel().getColumn(9).setPreferredWidth(40);
        jTableCheck.getColumnModel().getColumn(10).setPreferredWidth(80);

        jTableCheckLastModi.setRowHeight(25);
        jTableCheckLastModi.getColumnModel().getColumn(0).setMinWidth(0);
        jTableCheckLastModi.getColumnModel().getColumn(0).setMaxWidth(0);
        jTableCheckLastModi.getColumnModel().getColumn(0).setResizable(false);
        jTableCheckLastModi.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTableCheckLastModi.getColumnModel().getColumn(2).setPreferredWidth(300);
        jTableCheckLastModi.getColumnModel().getColumn(3).setPreferredWidth(70);
        jTableCheckLastModi.getColumnModel().getColumn(4).setPreferredWidth(40);
        jTableCheckLastModi.getColumnModel().getColumn(5).setPreferredWidth(40);
        jTableCheckLastModi.getColumnModel().getColumn(6).setPreferredWidth(40);
        jTableCheckLastModi.getColumnModel().getColumn(7).setPreferredWidth(40);
        jTableCheckLastModi.getColumnModel().getColumn(8).setPreferredWidth(40);
        jTableCheckLastModi.getColumnModel().getColumn(9).setPreferredWidth(40);
        jTableCheckLastModi.getColumnModel().getColumn(10).setPreferredWidth(80);
		
		jButtonSearch.setEnabled(!blDiscountCardFuture);
		jButtonDiscount.setEnabled(!blDiscountCardFuture);
		jButtonDiscountCard.setEnabled(!blDiscountCardFuture);
		
		if (countRows != jTableCheck.getRowCount()) selectedRow = -1;
		countRows = jTableCheck.getRowCount();
		
        if(countRows > 0){
            if (selectedRow > countRows-1) selectedRow = countRows - 1; 
            if (selectedRow == -1) {
                jTableCheck.setRowSelectionInterval(countRows - 1, countRows - 1);
            }else{
                jTableCheck.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
	}
    private void goodOperationQuantity(String typeOperation) {
		if (!checkCnnStatus()) return;
		if (blDiscountCardFuture) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        int selectedRow = jTableCheck.getSelectedRow();
        if (selectedRow == -1) return;
		int rowNum = jTableCheck.getRowSorter().convertRowIndexToModel(selectedRow);
        int goodID = Integer.parseInt(jTableCheck.getModel().getValueAt(rowNum, 0).toString());
        int res;
        if (typeOperation.equals("add")) {
            res = cnn.addGoodInCheckQuantity(goodID);
        } else if (typeOperation.equals("del")) {
            res = cnn.deleteGoodFromCheck(goodID);
        } else {
            DialogBoxs.viewMessage("Неверная операция с количеством товара: " + typeOperation);
            return;
        }
		if (res == 1) {
			requery();
		} else if (res == -99999) {
			DialogBoxs.viewMessage("Чек №" + cnn.currentCheckID.toString() + " уже закрыт!\n\nБудет создан новый чек!");
			jButtonNewCheckActionPerformed();
        } else {
            DialogBoxs.viewMessage("Не найден товар с GoodID: " + Integer.toString(goodID));
        }
    }
	private void editQuantity() {
		if (!checkCnnStatus())return;
		if (blDiscountCardFuture) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		if (cnn.checkIsBlank()) {
			DialogBoxs.viewMessage("Сначала необходимо ввести товары!");
			return;
		}
		int selectedRow = jTableCheck.getSelectedRow();
		if (selectedRow == -1) return;
		int rowNum = jTableCheck.getRowSorter().convertRowIndexToModel(selectedRow);
		
		final FrmQuantity frmQuantity = new FrmQuantity();
		frmQuantity.jLabel1.setText("<html>"+jTableCheck.getModel().getValueAt(rowNum, 2).toString()+"</html>");
		frmQuantity.goodID = Integer.parseInt(jTableCheck.getModel().getValueAt(rowNum, 0).toString());
		frmQuantity.jFormattedTextField1.setValue(new BigDecimal(jTableCheck.getModel().getValueAt(rowNum, 4).toString()));
        //frmQuantity.jFormattedTextField1.selectAll();
		frmQuantity.jFormattedTextField1.setSelectionStart(1);
		frmQuantity.jFormattedTextField1.setSelectionEnd(2);
		frmQuantity.jFormattedTextField1.setSelectionColor(Color.BLUE);
		frmQuantity.setModal(true);
		frmQuantity.setVisible(true);
		if (!checkCnnStatus()) return;
		BigDecimal bdQuantity = new BigDecimal(frmQuantity.jFormattedTextField1.getValue().toString());
		if (bdQuantity.compareTo(BigDecimal.ZERO) > 0) {
			int res = cnn.editGoodQuantityInCheck(frmQuantity.goodID,bdQuantity);
			if (res == 1) {
				requery();
			} else if (res == -99999) {
				DialogBoxs.viewMessage("Чек №" + cnn.currentCheckID.toString() + " уже закрыт!\n\nБудет создан новый чек!");
				jButtonNewCheckActionPerformed();
			} else {
				DialogBoxs.viewMessage("ОШИБКА при изменении кол-ва товара!");
			}
		}
	}
    private void jButtonNewCheckActionPerformed() {
        if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.checkIsBlank()) return;
        if (cnn.checkStatus == 0) {
            DialogBoxs.viewMessage("Чек необходимо распечатать!");
            return;
        }
        cnn.newCheck();
		blDiscountCardFuture = false;
        requery();
    }
    private void jButtonPrintCheckActionPerformed(java.awt.event.ActionEvent evt, boolean blIconified) {
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.checkIsBlank()) return;
		int countGoods = 0;
		int goodID = 0;
		for (int row = 0; row <= jTableCheck.getRowCount() - 1; row++) {
			if (jTableCheck.getValueAt(row, 10).toString().equals("")) {
				int selectedRow = jTableCheck.getSelectedRow();
				if (selectedRow == -1) break;
				int rowNum = jTableCheck.getRowSorter().convertRowIndexToModel(selectedRow);
				goodID = Integer.parseInt(jTableCheck.getModel().getValueAt(rowNum, 0).toString());
				countGoods++;
			}
		}
//временно
		if (countGoods == 1) {
			jButtonSellerActionPerformed(goodID);
			return;
		}else if (countGoods > 1) {
			jButtonSellerActionPerformed(-1);
			return;
		}
		final ReportCheck rc = new ReportCheck(cnn.currentCheckID);
		if (!blIconified) {
			rc.setModal(true);
			rc.setVisible(true);
			if (!checkCnnStatus()) return;
			if (rc.blStatusPrintButton) {
				if (rc.blStatusPrinted) {
					if (cnn.setCheckStatus(1)) 
						jButtonNewCheckActionPerformed();//чек распечатан успешно
				} else {
					if (cnn.setCheckStatus(2)) 
						jButtonNewCheckActionPerformed();//без распечатки
				}
			}
		} else {
//			rc.setModal(true);
//одобрено караваном !!!
			rc.setVisible(true);
			if (rc.silentPrint()) {
				if (cnn.setCheckStatus(1)) jButtonNewCheckActionPerformed();//чек распечатан успешно
			} else {
				if (cnn.setCheckStatus(2)) jButtonNewCheckActionPerformed();//без распечатки
			}
			rc.dispose();
		}
    }
    private void jButtonSearchActionPerformed(final String barCodeNew) {
		if (!checkCnnStatus()) return;
		if (blDiscountCardFuture) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			public void run() {
				final FrmSearch frmSearch = new FrmSearch();
				frmSearch.setModal(true);
				frmSearch.setVisible(true);
				this.getInputContext().selectInputMethod(frmSearch.getInputContext().getLocale());
				if (frmSearch.goodID > 0) {
					if (!checkCnnStatus()) return;
					int res = cnn.addGoodInCheck(frmSearch.goodID, barCodeNew);
					if (res == 1) {
						requery();
						TmCheckContent tm = (TmCheckContent) jTableCheck.getModel();
						int row = tm.getRowByID(frmSearch.goodID);
						if (row > 0) {
							jTableCheck.setRowSelectionInterval(row, row);
						}
					} else if (res == -99999) {
						DialogBoxs.viewMessage("Чек №" + cnn.currentCheckID.toString() + " уже закрыт!\n\nБудет создан новый чек!");
						jButtonNewCheckActionPerformed();
					} else {
						DialogBoxs.viewMessage("Не найден товар с GoodID: " + Integer.toString(frmSearch.goodID));
					}
				}
//			}
//		});
    }
    private void jButtonDiscountActionPerformed() {
		if (!checkCnnStatus()) return;
		if (blDiscountCardFuture) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.checkIsBlank()) {
            DialogBoxs.viewMessage("Для ввода скидки необходимо ввести товары!");
            return;
        }
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			public void run() {
				final FrmDiscount frmDiscount = new FrmDiscount();
				frmDiscount.setModal(false);
				int selectedRow = jTableCheck.getSelectedRow();
				if (selectedRow == -1) {
					frmDiscount.jLabel5.setText("");
					frmDiscount.jLabel55.setText("");
				}else{
					int rowNum = jTableCheck.getRowSorter().convertRowIndexToModel(selectedRow);
					String obj = "";
					obj = jTableCheck.getModel().getValueAt(rowNum, 4).toString(); if (obj.equals("")) obj = "0";
					BigDecimal bdQ = new BigDecimal(obj);
					obj = jTableCheck.getModel().getValueAt(rowNum, 5).toString(); if (obj.equals("")) obj = "0";
					BigDecimal bdP = new BigDecimal(obj);
					obj = jTableCheck.getModel().getValueAt(rowNum, 6).toString(); if (obj.equals("")) obj = "0";
					BigDecimal bdD = new BigDecimal(obj);
					obj = jTableCheck.getModel().getValueAt(rowNum, 9).toString(); if (obj.equals("")) obj = "0";
					BigDecimal bdS = new BigDecimal(obj);

					frmDiscount.goodID = Integer.parseInt(jTableCheck.getModel().getValueAt(rowNum, 0).toString());

					frmDiscount.rowSumBase = bdQ.multiply(bdP);
					frmDiscount.rowSumDiscount = bdQ.multiply(bdD);
					frmDiscount.rowSum = bdS;

					frmDiscount.jLabel5.setText(jTableCheck.getModel().getValueAt(rowNum, 2).toString());
					frmDiscount.jLabel51.setText(bdQ.toString());
					frmDiscount.jLabel52.setText(bdP.toString());
					frmDiscount.jLabel53.setText(bdD.toString());
					frmDiscount.jLabel55.setText(bdS.toString());
				}
				frmDiscount.setModal(true);
				frmDiscount.setVisible(true);
				if (frmDiscount.blDisposeStatus) {
					if (!checkCnnStatus()) {
						return;
					}
					cnn = ConnectionDb.getInstance();
					if (cnn == null) {
						return;
					}
					cnn.setCheckDiscount(frmDiscount.iTypeDiscount, frmDiscount.bdDiscount, frmDiscount.goodID, "");
					requery();
				} else {
					DialogBoxs.viewMessage("Скидка не назначена!");
				}
//			}
//		});
    }
    private void jButtonDiscountCardActionPerformed(){
		if (blDiscountCardFuture) return;
        if (!checkCnnStatus())return;
		cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.checkIsBlank()) {
            DialogBoxs.viewMessage("Для ввода скидки необходимо ввести товары!");
            return;
        }
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			public void run() {
				final FrmCardDiscount frmCardDiscount = new FrmCardDiscount(0);//2 - временно, исправить на 0
				frmCardDiscount.setModal(true);
				frmCardDiscount.setVisible(true);
				if (frmCardDiscount.blDisposeStatus) {
					if (!checkCnnStatus()) {
						return;
					}
					cnn = ConnectionDb.getInstance();
					if (cnn == null) {
						return;
					}
					if (!cnn.setCheckDiscountByCard(frmCardDiscount.strBarCode)) {
						requery();
						DialogBoxs.viewMessage("Ошибка при назначении скидки!");
					} else {
						//blDiscountCardFuture = true;
						requery();
					}
				} else {
					DialogBoxs.viewMessage("Скидка не назначена!");
				}
//			}
//		});
    }
    private void jButtonCalcActionPerformed(){
        if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.checkIsBlank()) {
            DialogBoxs.viewMessage("Сначала необходимо ввести товары!");
            return;
        }
//		java.awt.EventQueue.invokeLater(new Runnable() {
//			public void run() {
				FrmCalcCash frmCalcCash = new FrmCalcCash();
				frmCalcCash.jTextField2.setText(cnn.checkSum.setScale(2, RoundingMode.HALF_UP).toPlainString());
				frmCalcCash.setModal(true);
				frmCalcCash.setVisible(true);
//			}
//		});
    }
    private void jButtonPayTypeActionPerformed(){
		if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) {
            return;
        }
		if (cnn.checkTypePayment == 0){
			int i = JOptionPane.showConfirmDialog(null, "Оплата безналичным расчетом?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i==0){
				cnn.setCheckPaymentType(1, cnn.currentCheckID); // уст. тип оплаты БЕЗНАЛ
				requery();
			}
		}else{
			int i = JOptionPane.showConfirmDialog(null, "Оплата НАЛИЧНЫМИ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i == 0) {
				cnn.setCheckPaymentType(0, cnn.currentCheckID); // уст. тип оплаты БЕЗНАЛ
				requery();
			}
		}
    }
	private void jButtonSellerActionPerformed(final int goodID){
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		final FrmSellerList frmSellerList = new FrmSellerList();
		frmSellerList.setModal(true);
		frmSellerList.setVisible(true);
		if (!checkCnnStatus()) return;
		if (frmSellerList.blDisposeStatus == true) {
			if (!cnn.assignSellerByID(frmSellerList.sellerID, goodID)) {
				JOptionPane.showMessageDialog(null, "Возникла ошибка при выборе сотрудника.\nСообщите разработчику.", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/exit.png")));
			}
			requery();
		}
	}
	private void jButtonExitActionPerformed () {
        //cnn.setFlagReturn(false);
		cnn.lastCheck();
		dispose();
    }
	private void formWindowActivated(){
//		cnn.getCheckInfo(cnn.currentCheckID);
//		requery();
		jTableCheck.requestFocus();
	}
	
    private void jButtonPrintCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintCheckActionPerformed
        jButtonPrintCheckActionPerformed( evt, true);
    }//GEN-LAST:event_jButtonPrintCheckActionPerformed
    private void jButtonSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchActionPerformed
        jButtonSearchActionPerformed("");
    }//GEN-LAST:event_jButtonSearchActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jButtonDiscountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscountActionPerformed
        jButtonDiscountActionPerformed();
    }//GEN-LAST:event_jButtonDiscountActionPerformed
    private void jButtonCalcActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCalcActionPerformed
        jButtonCalcActionPerformed();
    }//GEN-LAST:event_jButtonCalcActionPerformed
    private void jButtonDiscountCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscountCardActionPerformed
        jButtonDiscountCardActionPerformed();
    }//GEN-LAST:event_jButtonDiscountCardActionPerformed
    private void jButtonPayTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPayTypeActionPerformed
        jButtonPayTypeActionPerformed();
    }//GEN-LAST:event_jButtonPayTypeActionPerformed
    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        formWindowActivated();
    }//GEN-LAST:event_formWindowActivated
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCalc;
    private javax.swing.JButton jButtonDiscount;
    private javax.swing.JButton jButtonDiscountCard;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonPayType;
    private javax.swing.JButton jButtonPrintCheck;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelReturn;
    private javax.swing.JPanel jPanelLastModi;
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelMiddle;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JScrollPane jScrollPaneCheck;
    private javax.swing.JTable jTableCheck;
    private javax.swing.JTable jTableCheckLastModi;
    // End of variables declaration//GEN-END:variables
}