package forms;

import db.ConnectionDb;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import tablemodel.TmCheckContent;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import main.EKKA;
import main.Loader;
import main.MyTimerTask;
import main.MyUtil;
import main.Updater;
import reports.ReportCheck;

public class FrmMain extends javax.swing.JFrame {
    private static FrmMain instance = null;
    private final ConfigReader conf;
    private ConnectionDb cnn;
	private boolean cnnState = true;
    private static String barCode = "";
	private static String barCodeKlass = "";
	private static boolean blBarCodeKlass = false;
	private boolean blDiscountCardFuture = false;
	private int countRows=0;

    public static FrmMain getInstance() {
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch ( ClassCastException | IndexOutOfBoundsException | NullPointerException | IllegalArgumentException | ArithmeticException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//            //java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//			MyUtil.errorToLog(FrmMain.class.getName(), ex);
//        }
        if (instance == null) {
            try {
                instance = new FrmMain();
            } catch (Exception e) {
				MyUtil.errorToLog(FrmMain.class.getName(), e);
                DialogBoxs.viewError(e);
                instance = null;
            }
        }
        return instance;
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
//System.out.println("e.getKeyCode()="+e.getKeyCode()+"	checkCnnStatus()="+checkCnnStatus());
			if (!checkCnnStatus()) {
				if (e.getKeyCode() != KeyEvent.VK_F8 && e.getKeyCode() != KeyEvent.VK_F5) {
					e.consume();
					return;
				}
			}
//			System.out.println("KeyCode=" + e.getKeyCode());
            keyOverride(e);
            if (e.getKeyCode() == KeyEvent.VK_ENTER && e.getSource() == jTableCheck) e.consume();
            if (e.getKeyCode() == KeyEvent.VK_F10) e.consume();
			if (e.getKeyCode() == KeyEvent.VK_MULTIPLY) return;
            super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
        }
        private KeyEvent keyOverride(KeyEvent e) {
            int keyCode = e.getKeyCode();
			//char keyChar = e.getKeyChar();
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
					if (blBarCodeKlass) {
						blBarCodeKlass = false;
						barCodeKlass = "";
						barCode = "";
						break;
					}
					if (blDiscountCardFuture) break;
					//barCode = "3182550711142";
					if (barCode.equals("")) break;
					//System.out.println("cnnState: "+cnnState);
					if (!checkCnnStatus()) break;
					//System.out.println("cnnState 5: "+cnn.statusValid(5));
					//System.out.println("cnnState 0: "+cnn.statusValid(0));
					if (barCode.length()==6 && barCode.startsWith("2")){
						//проверяем есть ли такая анкета
						if (cnn.getDiscountCardInfo(barCode)) {
							//проверяем анкета уже аннулирована или нет
							if (!cnn.getDiscountCardInfo("DateOfCancellation", "DateTime").equals("")) {
								DialogBoxs.viewMessageError("Анкета с номером: " + barCode + " уже аннулирована!");
								barCode = "";
								break;
							}
							//если анкета рабочая тогда навесим скидку
							if (cnn.setCheckDiscountByCard(barCode)){
								requery();
								DialogBoxs.viewMessage("Сработала анкета с номером: " + barCode);
								//если анкета сработала тогда аннулируем ее
								if (!cnn.setDiscountCardCancellation(barCode)){
									DialogBoxs.viewMessageError("При аннулировании анкеты с номером: " + barCode + "\nВозникла ошибка!\n\nСообщите разработчику!");
								}
							}
						}else{
							DialogBoxs.viewMessageError("Анкета с номером: " + barCode + " не существует!");
						}
						barCode = "";
						break;
					}
					double res = cnn.addGoodInCheck(barCode);
					if (res == 1) {
						requery();
					} else if (res < 0 && cnn.checkFlagReturn > 0) {
						DialogBoxs.viewMessage("Не достаточно товара с ШК: "+barCode+ "\n\nПродажа запрещена!");
						barCode = "";
					} else if (res == 0 && cnn.checkFlagReturn < 0) {
						DialogBoxs.viewMessage("Этот товар не покупали в чеке:"+cnn.returnID.toPlainString()+" \n\nВернуть можно только то, что отпускали.");
					} else if (res < 0 && cnn.checkFlagReturn < 0) {
						DialogBoxs.viewMessage("Нельзя вернуть товара больше чем покупали: \n\nБыло куплено:" + (-res));
						requery();
					} else if(res == -99999)  {
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
                case KeyEvent.VK_F2: // админка
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonAdminActionPerformed();
                    break;
				case KeyEvent.VK_F3:        // поиск
					if (e.getModifiers() != 0) {
						break;
					}
					jButtonSearchActionPerformed("");
					break;
                case KeyEvent.VK_F4: // акции
					if (e.getModifiers() != 0) {
						break;
					}
					jButtonPromoActionPerformed();
                    break;
                case KeyEvent.VK_F5:        // список сотрудников
					if (e.getModifiers() != 0) break;
                    jButtonSellerActionPerformed(0);
                    break;
                case KeyEvent.VK_F6:        // список сотрудников
					if (e.getModifiers() != 0) break;
					jButtonReturnActionPerformed();
                    break;
                case KeyEvent.VK_F7:        // дисконтная карта
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonDiscountCardActionPerformed();
                    break;
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
                case KeyEvent.VK_F10:        // ручная скидка
					if (e.getModifiers() != 0) {
						break;
					}
                    jButtonDiscountActionPerformed();
                    break;
                case KeyEvent.VK_SPACE:        // список сотрудников на конкретный товар
					if (e.getModifiers() != 0) {
						break;
					}
					int selectedRow = jTableCheck.getSelectedRow();
					if (selectedRow == -1) break;
					int rowNum = jTableCheck.getRowSorter().convertRowIndexToModel(selectedRow);
					int goodID = Integer.parseInt(jTableCheck.getModel().getValueAt(rowNum, 0).toString());
                    jButtonSellerActionPerformed(goodID);
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
                    
					//System.out.println("keyChar:" + keyChar + "	keycode:" + Integer.toString(e.getKeyCode()));
					
					if ((!blBarCodeKlass)&&((keyCode > 47 && keyCode < 58) || (keyCode > 95 && keyCode < 106))) {
						barCode = barCode.concat(Character.toString(e.getKeyChar()));
						//System.out.println("barCode:" + barCode);
						break;
					//}else if(keyCode >= 65 && keyCode <= 90){
					} else {
						//System.out.println(barCode);
						if (blBarCodeKlass) {
							barCodeKlass = barCodeKlass.concat(Character.toString(e.getKeyChar()));
						}else{
							blBarCodeKlass = true;
							barCodeKlass = Character.toString(e.getKeyChar());
						}
						//System.out.println("blBarCodeKlass:" + blBarCodeKlass + "	barCodeKlass:" + barCodeKlass);
					}
	            }
            return e;
        }        
    }
    
	private void setInvisible(Component comp) {
		comp.setEnabled(false);
		comp.setFocusable(false);
		comp.setVisible(false);
	}
	private void setVisible(Component comp) {
		comp.setEnabled(true);
		comp.setFocusable(true);
		comp.setVisible(true);
	}
	private void setVisible(Component comp, boolean value) {
		comp.setFocusable(value);
		comp.setVisible(value);
		comp.setEnabled(value);
	}
	private void setEnabled(Component comp, boolean value) {
		comp.setFocusable(value);
		comp.setEnabled(value);
	}
    public FrmMain() {
        initComponents();
        conf = ConfigReader.getInstance();
        setTitle(conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
		cnn = ConnectionDb.getInstance();
		if (cnn != null) cnn.setAppVersion();
		if (cnn.checkCardID!=null && cnn.returnID==null && cnn.returnIDFiscalNumber==null) blDiscountCardFuture = true;
        requery();
		
		setVisible(jButtonCheckCopy, false);
		if (conf.EKKA_TYPE!=0)setVisible(jButtonCheckCopy,true);
		
//временно
//setInvisible(jButtonReturn);
//setInvisible(jButtonPromo);
//setInvisible(jButtonSeller);
//setInvisible(jButtonReturn);

		//назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        setVisible(true);
		//уст. русской раскладки для клавиатуры
		this.getInputContext().selectInputMethod(new Locale("ru", "RU"));

//		jButtonDelivery.setVisible(false);
//		FrmStickerList frmStickerList = new FrmStickerList();
//		frmStickerList.setModal(true);
//		frmStickerList.setVisible(true);
//		ReportPriceSticker reportPrice = new ReportPriceSticker(new BigDecimal("1.2009"));
//		reportPrice.setModal(true);
//		reportPrice.setVisible(true);
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
        jButtonNewDiscountCard = new javax.swing.JButton();
        jButtonPromo = new javax.swing.JButton();
        jButtonDelivery = new javax.swing.JButton();
        jLabelReturn = new javax.swing.JLabel();
        jButtonSeller = new javax.swing.JButton();
        jPanelRight = new javax.swing.JPanel();
        jButtonAdmin = new javax.swing.JButton();
        jButtonReturn = new javax.swing.JButton();
        jButtonCheckCopy = new javax.swing.JButton();
        jButtonUpdate = new javax.swing.JButton();
        jButtonLink = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
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

        jButtonNewDiscountCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/new-card 64.png"))); // NOI18N
        jButtonNewDiscountCard.setToolTipText("Выдать новую дисконтную карту к чеку");
        jButtonNewDiscountCard.setActionCommand("Выдача новой карты");
        jButtonNewDiscountCard.setBorderPainted(false);
        jButtonNewDiscountCard.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonNewDiscountCard.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonNewDiscountCard.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonNewDiscountCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewDiscountCardActionPerformed(evt);
            }
        });

        jButtonPromo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Akcia.png"))); // NOI18N
        jButtonPromo.setToolTipText("F4 - Список акций");
        jButtonPromo.setActionCommand("Список акций");
        jButtonPromo.setBorderPainted(false);
        jButtonPromo.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonPromo.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonPromo.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPromoActionPerformed(evt);
            }
        });

        jButtonDelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Food_delivery_truck-64.png"))); // NOI18N
        jButtonDelivery.setToolTipText("Заявка на доставку товара");
        jButtonDelivery.setActionCommand("Список акций");
        jButtonDelivery.setBorderPainted(false);
        jButtonDelivery.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDelivery.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDelivery.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeliveryActionPerformed(evt);
            }
        });

        jLabelReturn.setFont(new java.awt.Font("Tahoma", 3, 30)); // NOI18N
        jLabelReturn.setForeground(java.awt.SystemColor.activeCaption);
        jLabelReturn.setText(" ");
        jLabelReturn.setMaximumSize(new java.awt.Dimension(100, 70));
        jLabelReturn.setPreferredSize(new java.awt.Dimension(100, 70));

        jButtonSeller.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/seller.png"))); // NOI18N
        jButtonSeller.setToolTipText("F5 - Список сотрудников");
        jButtonSeller.setActionCommand("Список акций");
        jButtonSeller.setBorderPainted(false);
        jButtonSeller.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonSeller.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonSeller.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonSeller.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSellerActionPerformed(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNewDiscountCard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPromo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSeller, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(jButtonNewDiscountCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPromo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelivery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelReturn, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSeller, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jButtonAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/admin.png"))); // NOI18N
        jButtonAdmin.setToolTipText("Доп. функции");
        jButtonAdmin.setActionCommand("Админ");
        jButtonAdmin.setBorderPainted(false);
        jButtonAdmin.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonAdmin.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonAdmin.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdminActionPerformed(evt);
            }
        });

        jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/return.png"))); // NOI18N
        jButtonReturn.setToolTipText("Возврат товара");
        jButtonReturn.setActionCommand("Возврат товара");
        jButtonReturn.setBorderPainted(false);
        jButtonReturn.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonReturn.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonReturn.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReturnActionPerformed(evt);
            }
        });

        jButtonCheckCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/copy-icon2.png"))); // NOI18N
        jButtonCheckCopy.setToolTipText("Печать копии последнего фиск. чека");
        jButtonCheckCopy.setActionCommand("Печать копии последнего фиск. чека");
        jButtonCheckCopy.setBorderPainted(false);
        jButtonCheckCopy.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonCheckCopy.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonCheckCopy.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonCheckCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckCopyActionPerformed(evt);
            }
        });

        jButtonUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/update.png"))); // NOI18N
        jButtonUpdate.setToolTipText("Обновление программы");
        jButtonUpdate.setActionCommand("Обновление программы");
        jButtonUpdate.setBorderPainted(false);
        jButtonUpdate.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonUpdate.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonUpdate.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
            }
        });

        jButtonLink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/connect on.png"))); // NOI18N
        jButtonLink.setToolTipText("Соединение с сервером");
        jButtonLink.setActionCommand("Соединение с сервером");
        jButtonLink.setBorderPainted(false);
        jButtonLink.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonLink.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonLink.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLinkActionPerformed(evt);
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

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButtonAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReturn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCheckCopy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRightLayout.createSequentialGroup()
                .addGroup(jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReturn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLink, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCheckCopy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelMiddle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelLastModi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneCheck, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private boolean checkPayPartStatus() {
		if (!checkCnnStatus()) return false;
		ResultSet rs = cnn.getCheckNotPayPart(cnn.currentCheckID);
		boolean bl = false;
		String str = "<html><table border=1 cellspacing=0><thead><tr>"
				+ "<th>Код</th>"
				+ "<th>Артикул</th>"
				+ "<th>Название</th>"
				+ "<th>Кол-во</th>"
				+ "</tr></thead>"
				+ "<tbody>";
		try {
			while (rs.next()) {
				bl = true;
				//str += rs.getString("GoodID").toString() + "\t" +rs.getString("Article").toString()+"\t"+rs.getString("Name").toString()+"\n";
				str += "<tr><td>"+rs.getString("GoodID").toString() + "</td><td>" +rs.getString("Article").toString()+"</td><td>"+rs.getString("Name").toString()+"</td><td>" + rs.getString("Quantity").toString() + "</td></tr>";
			}
			str += "</tbody></table></html>";
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
		}
		if (bl){
			JOptionPane.showMessageDialog(new JFrame(), "В чеке есть товары для которых оплата частями НЕ РАЗРЕШЕНА!\n"
					+ "Удалите эти товары из чека или измените тип оплаты!\n\n"
					+ "Список товаров:\n"
					+ str, "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	private boolean checkCnnStatus() {
		cnnState = cnn.statusValid(0);
		if(!cnnState){
			URL url = getClass().getResource("/wav/error.wav");
			AudioClip clip = Applet.newAudioClip(url);
			clip.play();
			JOptionPane.showMessageDialog(null, "Нет связи с сервером!\n\nПовторите операцию позже.", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE,new javax.swing.ImageIcon(getClass().getResource("/png/connect lost.png")));
		}	
		return cnnState;
	}
	private boolean checkPromo(String msg, boolean manual){
		if (cnn.checkSumDiscount.compareTo(BigDecimal.ZERO) == 0 || manual == true) {
			cnn.assignPromoAll();
			if (cnn.checkSumDiscount.compareTo(BigDecimal.ZERO) != 0) {
				requery();
				JOptionPane.showMessageDialog(null, "Для этого чека сработали акции!\n\nСумма скидки изменилась!\n\n"+msg, "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/Akcia.png")));
				return false;
			}
		}
		return true;
	}
	public void setCnnStatus(int status){
		if (status==0) {
			jButtonLink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/connect on.png")));
			cnnState = true;
			if (cnn.serverID == 1) {
				jButtonLink.setToolTipText("Установлена связь с сервером: " + conf.SERVER_ADDRESS_1);
			} else {
				jButtonLink.setToolTipText("Установлена связь с сервером: " + conf.SERVER_ADDRESS_2);
			}
		}
		if (status==1) {
			jButtonLink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/connect search.png")));
			cnnState = false;
			jButtonLink.setToolTipText("Связь с сервером восстанавливается...");
		}
		if (status==2) {
			jButtonLink.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/connect lost.png")));
			cnnState = false;
			jButtonLink.setToolTipText("Связь с сервером невозможна.");
		}
	}
    private void requery() {
		if(!checkCnnStatus())return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.currentCheckID == null) return;
        if (!cnn.statusValid()) return;
        int selectedRow = jTableCheck.getSelectedRow();
//System.out.println("cnn.currentCheckID:"+cnn.currentCheckID.toString());
        jLabel2.setText(cnn.currentCheckID.setScale(4, RoundingMode.HALF_UP).toPlainString());
        //jLabel4.setText(cnn.checkTypePayment == 1 ? "безналичный расчет" : "наличный расчет");
        if (cnn.checkTypePayment == 0) jLabel4.setText("наличный расчет");
        if (cnn.checkTypePayment == 1) jLabel4.setText("безналичный расчет");
        if (Integer.toString(cnn.checkTypePayment).startsWith("2")) jLabel4.setText("оплата частями ("+Integer.toString(cnn.checkTypePayment).substring(1)+" плат.)");
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
		
		setEnabled(jButtonSearch,!blDiscountCardFuture);
		setEnabled(jButtonDiscount, !blDiscountCardFuture);
		setEnabled(jButtonDiscountCard, !blDiscountCardFuture);
		setEnabled(jButtonNewDiscountCard, !blDiscountCardFuture);
		setEnabled(jButtonPromo, !blDiscountCardFuture);
		setEnabled(jButtonReturn, !blDiscountCardFuture);

		
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
		if (cnn.checkFlagReturn == 1) {
			setVisible(jButtonAdmin);
			setVisible(jButtonNewDiscountCard);
			setVisible(jButtonPromo);
			setInvisible(jLabelReturn);
			jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/return.png"))); // NOI18N
			jLabel1.setText("№ чека");
			jLabel1.setForeground(java.awt.SystemColor.desktop);
			jLabel2.setForeground(java.awt.SystemColor.desktop);
			jLabel3.setForeground(java.awt.SystemColor.desktop);
			jLabel4.setForeground(java.awt.SystemColor.desktop);
			jLabel5.setForeground(java.awt.SystemColor.desktop);
			jLabel6.setForeground(java.awt.SystemColor.desktop);
			jLabel7.setForeground(java.awt.SystemColor.desktop);
			jLabel8.setForeground(java.awt.SystemColor.desktop);
			jLabel9.setForeground(java.awt.SystemColor.desktop);
			jLabel10.setForeground(java.awt.SystemColor.desktop);
			jLabel9.setText("К оплате:");
		} else {
			setInvisible(jButtonAdmin);
			setInvisible(jButtonNewDiscountCard);
			setInvisible(jButtonPromo);
			setVisible(jLabelReturn);
			jLabelReturn.setFont(new java.awt.Font("Tahoma", 3, 24)); // NOI18N
			jLabelReturn.setForeground(java.awt.SystemColor.RED);
			jLabelReturn.setText("<html><center>ОФОРМЛЕНИЕ&nbsp;&nbsp;<br>ВОЗВРАТА</html>");
			jLabel1.setForeground(java.awt.SystemColor.RED);
			jLabel2.setForeground(java.awt.SystemColor.RED);
			jLabel3.setForeground(java.awt.SystemColor.RED);
			jLabel4.setForeground(java.awt.SystemColor.RED);
			jLabel5.setForeground(java.awt.SystemColor.RED);
			jLabel6.setForeground(java.awt.SystemColor.RED);
			jLabel7.setForeground(java.awt.SystemColor.RED);
			jLabel8.setForeground(java.awt.SystemColor.RED);
			jLabel9.setForeground(java.awt.SystemColor.RED);
			jLabel10.setForeground(java.awt.SystemColor.RED);
			jLabel1.setText("№ возврата");
			jLabel9.setText("К выдаче:");
			jButtonReturn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/return_on.png"))); // NOI18N
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
        double res;
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
		} else if (res < 0 && cnn.checkFlagReturn > 0) {
			DialogBoxs.viewMessage("Не достаточно товара: \n\n" + jTableCheck.getModel().getValueAt(rowNum, 2).toString() + "\n\nПродажа запрещена!");
		} else if (res < 0 && cnn.checkFlagReturn < 0) {
			DialogBoxs.viewMessage("Нельзя вернуть товара больше чем покупали: \n\n" + jTableCheck.getModel().getValueAt(rowNum, 2).toString() + "\n\nБыло куплено:" + (-res));
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
		frmQuantity.checkFlagReturn = cnn.checkFlagReturn;
		frmQuantity.jFormattedTextField1.setValue(new BigDecimal(jTableCheck.getModel().getValueAt(rowNum, 4).toString()));
        //frmQuantity.jFormattedTextField1.selectAll();
		frmQuantity.jFormattedTextField1.setSelectionStart(1);
		frmQuantity.jFormattedTextField1.setSelectionEnd(2);
		frmQuantity.jFormattedTextField1.setSelectionColor(Color.BLUE);
		frmQuantity.setModal(true);
		frmQuantity.setVisible(true);
		if (!checkCnnStatus()) return;
		BigDecimal bdQuantity = new BigDecimal(frmQuantity.jFormattedTextField1.getValue().toString());
		if (frmQuantity.blDisposeStatus) {
			double res = cnn.editGoodQuantityInCheck(frmQuantity.goodID,bdQuantity);
			if (res == 1) {
				requery();
			} else if (res < 0 && cnn.checkFlagReturn > 0) {
				DialogBoxs.viewMessage("Не достаточно товара: \n\n" + jTableCheck.getModel().getValueAt(rowNum, 2).toString() + "\n\nПродажа запрещена!");
			} else if (res < 0 && cnn.checkFlagReturn < 0) {
				DialogBoxs.viewMessage("Нельзя вернуть товара больше чем покупали: \n\n" + jTableCheck.getModel().getValueAt(rowNum, 2).toString() + "\n\nБыло куплено:"+(-res));
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

		if (!checkPromo("Проверте скидки и снова нажмите печать!",false)) return;

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
		if (conf.MARKET_ID == 6958) {//только для Харьков 23 августа
			//if (cnn.checkCardKlassID.equals("")){
				final FrmCardKlass frmCardKlass = new FrmCardKlass(cnn.checkCardKlassID);
				frmCardKlass.setModal(true);
				frmCardKlass.setVisible(true);
				if (frmCardKlass.blDisposeStatus) {
					cnn = ConnectionDb.getInstance();
					//cnn.checkCardKlassID = frmCardKlass.strBarCode;
					if (cnn == null) return;
					if (!cnn.setCheckKlassCard(frmCardKlass.strBarCode)) {
						DialogBoxs.viewMessage("Ошибка при регистрации карты супермаркета!");
					}
					if (cnn.checkCardKlassID.length() == 0) {
						DialogBoxs.viewMessage("Карта супермаркета не зарегистрирована!!!");
					}
 				} else {
					DialogBoxs.viewMessage("Карта супермаркета не зарегистрирована!");
				}
			//}
			//System.out.println("checkCardKlassID:"+cnn.checkCardKlassID);
			//return;
		}
		
		
		if(conf.EKKA_TYPE==2){//если и нал и безнал проводим через РРО
			if (cnn.returnID == null && cnn.returnIDFiscalNumber == null) {
//				System.out.println("обычный чек");
				String typePay = "0";
				String typePayMsg = "НАЛИЧНЫЙ расчет";
				if (cnn.checkTypePayment == 1) {
					typePay = "2";
					typePayMsg = "БЕЗНАЛИЧНЫЙ расчет";
				}else if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
					typePay = "2";
					typePayMsg = "Оплата частями ("+Integer.toString(cnn.checkTypePayment).substring(1)+" плат.)";
					if (!checkPayPartStatus()) {
						return;
					}
				}
				int i = JOptionPane.showOptionDialog(null, "Указан тип оплаты: " + typePayMsg + "\n\nРаспечатать чек на регистраторе?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
				if (i != 0) {
					jButtonPayTypeActionPerformed();
					return;
				}
				try {
					EKKA me = new EKKA(this);
					if (me.printCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber)) {
						if (cnn.setCheckStatus(1)) {
							jButtonNewCheckActionPerformed();//чек распечатан успешно
						}
					}
				} catch (Exception e) {
					MyUtil.errorToLog(this.getClass().getName(), e);
					return;
				}
			} else if (cnn.returnID != null && cnn.returnIDFiscalNumber == null) {
//				System.out.println("возврат не фискальный");
				final ReportCheck rc = new ReportCheck(cnn.currentCheckID);
				if (!blIconified) {
					rc.setModal(true);
					rc.setVisible(true);
					if (!checkCnnStatus()) {
						return;
					}
					if (rc.blStatusPrintButton) {
						if (rc.blStatusPrinted) {
							if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
							if (cnn.setCheckStatus(1)) {
								jButtonNewCheckActionPerformed();//чек распечатан успешно
							}
						} else {
							if (cnn.setCheckStatus(2)) {
								jButtonNewCheckActionPerformed();//без распечатки
							}
						}
					}
				} else {
//					rc.setModal(true);
					//одобрено караваном !!!
					rc.setVisible(true);
					if (rc.silentPrint()) {
						if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
						if (cnn.setCheckStatus(1)) {
							jButtonNewCheckActionPerformed();//чек распечатан успешно
						}
					} else {
						if (cnn.setCheckStatus(2)) {
							jButtonNewCheckActionPerformed();//без распечатки
						}
					}
					rc.dispose();
				}
			} else if (cnn.returnID != null && cnn.returnIDFiscalNumber != null) {
//				System.out.println("возврат фискальный");
				String typePay = "0";
				String typePayMsg = "НАЛИЧНЫЙ расчет";
				if (cnn.checkTypePayment == 1) {
					typePay = "2";
					typePayMsg = "БЕЗНАЛИЧНЫЙ расчет";
				} else if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
					typePay = "2";
					typePayMsg = "Оплата частями (" + Integer.toString(cnn.checkTypePayment).substring(1) + " плат.)";
					if (!checkPayPartStatus()) {
						return;
					}
				}
				int i = JOptionPane.showOptionDialog(null, "Указан тип оплаты: " + typePayMsg + "\n\nРаспечатать чек на регистраторе?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
				if (i != 0) {
					jButtonPayTypeActionPerformed();
					return;
				}
				try {
					EKKA me = new EKKA(this);
					if (me.printCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber)) {
						if (cnn.setCheckStatus(1)) {
							jButtonNewCheckActionPerformed();//чек распечатан успешно
						}
//					} else {
//						if (cnn.setCheckStatus(2)) {
//							jButtonNewCheckActionPerformed();//без распечатки
//						}
					}
				} catch (Exception e) {
					MyUtil.errorToLog(this.getClass().getName(), e);
					return;
				}
			} else {
				MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("jButtonPrintCheckActionPerformed: ошибка определения типа чека при печати!"));
				DialogBoxs.viewMessage("ОШИБКА определения типа чека при печати!");
			}
		}else if(conf.EKKA_TYPE!=0){
			if (cnn.returnID == null && cnn.returnIDFiscalNumber == null){
//				System.out.println("обычный чек");
				String typePay = "0";
				String typePayMsg = "НАЛИЧНЫЙ расчет";
				String typePrinter = "ПРИНТЕРЕ";
				if (cnn.checkTypePayment == 1) {
					typePay = "2";
					typePayMsg = "БЕЗНАЛИЧНЫЙ расчет";
				} else if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
					typePay = "2";
					typePayMsg = "Оплата частями (" + Integer.toString(cnn.checkTypePayment).substring(1) + " плат.)";
					if (!checkPayPartStatus()) {
						return;
					}
				}
				if (cnn.checkTypePayment != 0) {
					typePrinter = "РЕГИСТРАТОРЕ";
					int i = JOptionPane.showOptionDialog(null, "Указан тип оплаты: " + typePayMsg + "\n\nРаспечатать чек на "+typePrinter+" ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
					if (i != 0) return;
					try {
						EKKA me = new EKKA(this);
						if (me.printCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber)) {
							if (cnn.setCheckStatus(1)) {
								jButtonNewCheckActionPerformed();//чек распечатан успешно
							}
						}
					} catch (Exception e) {
						MyUtil.errorToLog(this.getClass().getName(), e);
						return;
					}
				} else {
					int i = JOptionPane.showOptionDialog(null, "Указан тип оплаты: " + typePayMsg + "\n\nРаспечатать чек на " + typePrinter + " ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
					if (i != 0) return;
					final ReportCheck rc = new ReportCheck(cnn.currentCheckID);
					if (!blIconified) {
						rc.setModal(true);
						rc.setVisible(true);
						if (!checkCnnStatus()) {
							return;
						}
						if (rc.blStatusPrintButton) {
							if (rc.blStatusPrinted) {
								if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
								if (cnn.setCheckStatus(1)) {
									jButtonNewCheckActionPerformed();//чек распечатан успешно
								}
							} else {
								if (cnn.setCheckStatus(2)) {
									jButtonNewCheckActionPerformed();//без распечатки
								}
							}
						}
					} else {
	//			rc.setModal(true);
						//одобрено караваном !!!
						rc.setVisible(true);
						if (rc.silentPrint()) {
							if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
							if (cnn.setCheckStatus(1)) {
								jButtonNewCheckActionPerformed();//чек распечатан успешно
							}
						} else {
							if (cnn.setCheckStatus(2)) {
								jButtonNewCheckActionPerformed();//без распечатки
							}
						}
						rc.dispose();
					}
				}
			} else if (cnn.returnID != null && cnn.returnIDFiscalNumber == null) {
//				System.out.println("возврат не фискальный");
				final ReportCheck rc = new ReportCheck(cnn.currentCheckID);
				if (!blIconified) {
					rc.setModal(true);
					rc.setVisible(true);
					if (!checkCnnStatus()) {
						return;
					}
					if (rc.blStatusPrintButton) {
						if (rc.blStatusPrinted) {
							if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
							if (cnn.setCheckStatus(1)) {
								jButtonNewCheckActionPerformed();//чек распечатан успешно
							}
						} else {
							if (cnn.setCheckStatus(2)) {
								jButtonNewCheckActionPerformed();//без распечатки
							}
						}
					}
				} else {
//					rc.setModal(true);
					//одобрено караваном !!!
					rc.setVisible(true);
					if (rc.silentPrint()) {
						if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
						if (cnn.setCheckStatus(1)) {
							jButtonNewCheckActionPerformed();//чек распечатан успешно
						}
					} else {
						if (cnn.setCheckStatus(2)) {
							jButtonNewCheckActionPerformed();//без распечатки
						}
					}
					rc.dispose();
				}
			} else if (cnn.returnID != null && cnn.returnIDFiscalNumber != null) {
//				System.out.println("возврат фискальный");
				String typePay = "0";
				String typePayMsg = "НАЛИЧНЫЙ расчет";
				String typePrinter = "ПРИНТЕРЕ";
				if (cnn.checkTypePayment == 1) {
					typePay = "2";
					typePayMsg = "БЕЗНАЛИЧНЫЙ расчет";
				} else if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
					typePay = "2";
					typePayMsg = "Оплата частями (" + Integer.toString(cnn.checkTypePayment).substring(1) + " плат.)";
					if (!checkPayPartStatus()) {
						return;
					}
				}
				if (cnn.checkTypePayment != 0) {
					typePrinter = "РЕГИСТРАТОРЕ";
				} else {
					JOptionPane.showMessageDialog(null, "Возврат для фискального чека\n\nвозможно делать только по безналу!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/exit.png")));
					return;
				}
				int i = JOptionPane.showOptionDialog(null, "Указан тип оплаты: " + typePayMsg + "\n\nРаспечатать возвратный чек на " + typePrinter + " ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
				if (i != 0) return;
				try {
					EKKA me = new EKKA(this);
					if (me.printCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber)) {
						if (cnn.setCheckStatus(1)) {
							jButtonNewCheckActionPerformed();//чек распечатан успешно
						}
//					} else {
//						if (cnn.setCheckStatus(2)) {
//							jButtonNewCheckActionPerformed();//без распечатки
//						}
					}
				} catch (Exception e) {
					MyUtil.errorToLog(this.getClass().getName(), e);
					return;
				}
			} else {
				MyUtil.errorToLog(this.getClass().getName(), new IllegalArgumentException("jButtonPrintCheckActionPerformed: ошибка определения типа чека при печати!"));
				DialogBoxs.viewMessage("ОШИБКА определения типа чека при печати!");
			}
		}else{
			String typePay = "0";
			String typePayMsg = "НАЛИЧНЫЙ расчет";
			String typePrinter = "ПРИНТЕРЕ";
			if (cnn.checkTypePayment == 1) {
				typePay = "2";
				typePayMsg = "БЕЗНАЛИЧНЫЙ расчет";
			} else if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
				typePay = "2";
				typePayMsg = "Оплата частями (" + Integer.toString(cnn.checkTypePayment).substring(1) + " плат.)";
				if (!checkPayPartStatus()) {
					return;
				}
			}
			if (!typePay.equals("0") && cnn.returnID==null && cnn.returnIDFiscalNumber==null){
//				int i = JOptionPane.showOptionDialog(null, "Указан тип оплаты: " + typePayMsg + "\n\nРаспечатать чек на " + typePrinter + " ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Да", "Нет"}, "Нет");
//				if (i != 0) {
//					return;
//				}
				try {
					EKKA me = new EKKA(this);
					if (!me.terminalCheck(cnn.currentCheckID, typePay, cnn.returnIDFiscalNumber)) {
//						if (cnn.setCheckStatus(1)) {
//							jButtonNewCheckActionPerformed();//чек распечатан успешно
//						}
						if (!conf.POS_ACTIVE.equals("0")) return;
					}
				} catch (Exception e) {
					MyUtil.errorToLog(this.getClass().getName(), e);
					return;
				}
			}

			final ReportCheck rc = new ReportCheck(cnn.currentCheckID);
			if (!blIconified) {
				rc.setModal(true);
				rc.setVisible(true);
				if (!checkCnnStatus()) return;
				if (rc.blStatusPrintButton) {
					if (rc.blStatusPrinted) {
						if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
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
					if (blDiscountCardFuture) rc.silentPrint(); // если выдана новая карта печатаем 2 чека
					if (cnn.setCheckStatus(1)) jButtonNewCheckActionPerformed();//чек распечатан успешно
				} else {
					if (cnn.setCheckStatus(2)) jButtonNewCheckActionPerformed();//без распечатки
				}
				rc.dispose();
			}
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
					double res = cnn.addGoodInCheck(frmSearch.goodID, barCodeNew);
					if (res == 1) {
						requery();
						TmCheckContent tm = (TmCheckContent) jTableCheck.getModel();
						int row = tm.getRowByID(frmSearch.goodID);
						if (row > 0) {
							jTableCheck.setRowSelectionInterval(row, row);
						}
					} else if (res < 0 && cnn.checkFlagReturn > 0) {
						DialogBoxs.viewMessage("Не достаточно товара с кодом: " + frmSearch.goodID + "\n\nПродажа запрещена!");
					} else if (res < 0 && cnn.checkFlagReturn < 0) {
						DialogBoxs.viewMessage("Нельзя вернуть товара больше чем покупали: \n\nБыло куплено:" + (-res));
						requery();
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
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		if (!checkCnnStatus()) return;
		if (blDiscountCardFuture) return;
        if (cnn.checkIsBlank()) {
            DialogBoxs.viewMessage("Для ввода скидки необходимо ввести товары!");
            return;
        }

		if (!checkPromo("Проверте скидки, после этого можете ввести дисконт!", false)) return;
		
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
			cnn.setCheckDiscount(frmDiscount.iTypeDiscount, frmDiscount.bdDiscount, frmDiscount.goodID, "", frmDiscount.iTypeReason);
			requery();
		} else {
			DialogBoxs.viewMessage("Скидка не назначена!");
		}
    }
    private void jButtonDiscountCardActionPerformed(){
		if (blDiscountCardFuture) return;
		cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (!checkCnnStatus())return;
        if (cnn.checkIsBlank()) {
            DialogBoxs.viewMessage("Для ввода скидки необходимо ввести товары!");
            return;
        }
		
		if (!checkPromo("Проверте скидки, после этого можете ввести дисконтную карту!", false)) return;
		
		final FrmCardDiscount frmCardDiscount = new FrmCardDiscount(0);//2 - временно, исправить на 0
		frmCardDiscount.setModal(true);
		frmCardDiscount.setVisible(true);
		if (frmCardDiscount.blDiscountCardReplace) {
			if (!checkCnnStatus()) return;
			cnn = ConnectionDb.getInstance();
			if (cnn == null) return;
			jButtonNewDiscountCardActionPerformed(frmCardDiscount.strBarCode);
		} else if (frmCardDiscount.blDisposeStatus) {
			if (!checkCnnStatus()) return;
			cnn = ConnectionDb.getInstance();
			if (cnn == null) return;
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
    }
    private void jButtonCalcActionPerformed(){
        if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        if (cnn.checkIsBlank()) {
            DialogBoxs.viewMessage("Сначала необходимо ввести товары!");
            return;
        }
		if (!checkPromo("Проверте скидки и снова нажмите расчет сдачи!", false)) return;
		FrmCalcCash frmCalcCash = new FrmCalcCash();
		frmCalcCash.jTextField2.setText(cnn.checkSum.setScale(2, RoundingMode.HALF_UP).toPlainString());
		frmCalcCash.setModal(true);
		frmCalcCash.setVisible(true);
    }
    private void jButtonPayTypeActionPerformed(){
		if (!checkCnnStatus()) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		
		FrmPaymentType frmPaymentType = new FrmPaymentType();
		frmPaymentType.setModal(true);
		frmPaymentType.setVisible(true);
		if (frmPaymentType.typePayment==-1) return;
		cnn.setCheckPaymentType(frmPaymentType.typePayment, cnn.currentCheckID); // уст. тип оплаты
		requery();
		
/*		if (cnn.checkTypePayment == 0){
			int i = JOptionPane.showConfirmDialog(null, "Оплата безналичным расчетом?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i==0){
				cnn.setCheckPaymentType(1, cnn.currentCheckID); // уст. тип оплаты БЕЗНАЛ
				requery();
			}
		}else{
			int i = JOptionPane.showConfirmDialog(null, "Оплата НАЛИЧНЫМИ?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i == 0) {
				cnn.setCheckPaymentType(0, cnn.currentCheckID); // уст. тип оплаты НАЛ
				requery();
			}
		}
*/
    }
    private void jButtonNewDiscountCardActionPerformed(String parentCardID){
		if (blDiscountCardFuture) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		if (cnn.checkFlagReturn == -1) return;
		if (!checkCnnStatus()) return;
		if (!checkPromo("Проверте скидки, после этого можете выдать дисконтную карту!", false)) return;
		BigDecimal bdDiscountSumStart = cnn.getDiscountScaleStart();
		final FrmCardAttribute frmCardAttribute;
		if (parentCardID.equals("")) {
			if (cnn.checkIsBlank()) {
				DialogBoxs.viewMessage("Для ввода скидки необходимо ввести товары!");
				return;
			}
			if (cnn.checkSum.compareTo(bdDiscountSumStart) < 0 ){
				DialogBoxs.viewMessage("Сумма товаров недостаточна \nдля выдачи новой карты!\nМинимальная сумма: "+bdDiscountSumStart.setScale(2,RoundingMode.HALF_UP).toPlainString());
				return;
			}
			frmCardAttribute = new FrmCardAttribute(0,"");
		} else {
			frmCardAttribute = new FrmCardAttribute(3,parentCardID);
		}
		frmCardAttribute.setModal(true);
		frmCardAttribute.setVisible(true);
		if (frmCardAttribute.blDisposeStatus && parentCardID.equals("")) {
			if (!checkCnnStatus()) return;
			cnn = ConnectionDb.getInstance();
			if (cnn == null) return;
			if (!cnn.setCheckNewCard(frmCardAttribute.strBarCode)) {
				requery();
				DialogBoxs.viewMessage("Ошибка при назначении скидки!");
			} else {
				blDiscountCardFuture = true;
				requery();
			}
		} else if (frmCardAttribute.blDisposeStatus && !parentCardID.equals("")) {
			if (!checkCnnStatus()) return;
			cnn = ConnectionDb.getInstance();
			if (cnn == null) return;
			if (!cnn.setCheckDiscountByCard(frmCardAttribute.strBarCode)) {
				requery();
				DialogBoxs.viewMessage("Ошибка при назначении скидки!");
			} else {
				//blDiscountCardFuture = true;
				requery();
			}
		} else {
			DialogBoxs.viewMessage("Скидка не назначена!");
		}
    }
    private void jButtonPromoActionPerformed(){
        if (!checkCnnStatus()) return;
        if (blDiscountCardFuture) return;
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		if (cnn.checkFlagReturn == -1) return;

		//if (!checkPromo("Проверте скидки, после этого можете выбрать акцию!",false)) return;

		final FrmPromoList frmPromoList = new FrmPromoList();
		frmPromoList.setModal(true);
		frmPromoList.setVisible(true);
		if (!checkCnnStatus()) return;
		if (frmPromoList.blPromoAll) if (!checkPromo("Проверте скидки!", true)) return;
		if (frmPromoList.promoID > 0) {
			if (!cnn.assignPromoByID(frmPromoList.promoID))
				JOptionPane.showMessageDialog(null, "Возникла ошибка при назначении акции.\nСообщите разработчику.", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/exit.png")));
			requery();
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
	private void jButtonDeliveryActionPerformed() {
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		final FrmDelivery frmDelivery = new FrmDelivery(cnn.checkCardID);
		frmDelivery.setModal(true);
		frmDelivery.setVisible(true);
		if (!checkCnnStatus()) {
			return;
		}
		if (frmDelivery.blDisposeStatus == true) {
			//if (!cnn.assignSellerByID(0, 0)) {
				JOptionPane.showMessageDialog(null, "Возникла ошибка при оформлении доставки товара.\nСообщите разработчику.", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/exit.png")));
			//}
			requery();
		}
	}
	private void jButtonAdminActionPerformed(){
		if (!checkCnnStatus()) return;
		if (cnn.checkFlagReturn==-1) return;
//		cnn.getGoodInfoByBarcode("BarCode=0123");
//		if (1==1) return;
		try {
			if (!Loader.checkFrmAdmin()){
				Runtime.getRuntime().exec("javaw -jar shopv2.jar admin");
				Thread.sleep(2000);
				Loader.clientSocketListener(Loader.PORTadmin,Loader.focusProgram);
			}
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			DialogBoxs.viewError(ex);
		} catch (InterruptedException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
//		FrmAdmin frmAdmin = new FrmAdmin();
//		frmAdmin.setModal(true);
//		frmAdmin.setVisible(true);
	}
	private void jButtonReturnActionPerformed() {
		if (!checkCnnStatus()) return;
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		if (!cnn.checkIsBlank()) {
			DialogBoxs.viewMessage("Сначала закройте текущий чек!");
			return;
		}
		DialogBoxs db = new DialogBoxs();
		String returnID = db.showOptionDialogGetFiscalCheckID("Оформление возврата","<html>Введите № чека <br>(без дробной части):</html>","<html>или № фискального чека:</html>",new javax.swing.ImageIcon(getClass().getResource("/png/return_on.png")));
		boolean fiscal = false;
//System.out.println("returnID:"+returnID);
		if (returnID.equals("0")) return;
		if (returnID.startsWith("f")) {
			fiscal = true;
			returnID = returnID.replace("f", "");
		}
		int rrr = new Integer(returnID);
		if(cnn.checkFlagReturn!=1){
			cnn.setCheckFlagReturn(1, cnn.currentCheckID, rrr, fiscal); // уст. флаг возврата
		}else{
			cnn.setCheckFlagReturn(-1, cnn.currentCheckID, rrr, fiscal); // уст. флаг возврата
		}
		requery();
	}
	private void jButtonCheckCopyActionPerformed(){
		if (conf.EKKA_TYPE == 0) return;
		try {
			EKKA me = new EKKA(this);
			me.copyCheck();
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			return;
		}
	}
	private void jButtonUpdateActionPerformed(){
		Updater u = new Updater();
		synchronized (u) {
			try {
				u.wait();
			} catch (InterruptedException ex) {
				//ex.printStackTrace();
				MyUtil.errorToLog(this.getClass().getName(), ex);
			}		
		}
		if (!u.statusUpgrade)
			DialogBoxs.viewMessage("Вы используете последнюю версию программы!");
	}
	private void jButtonLinkActionPerformed(){
		TimerTask timerTask = new MyTimerTask(this, "linkStatusTask");
		timerTask.run();
	}
	private void jButtonExitActionPerformed () {
        cnn.destroy();
        System.exit(0);
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
    private void jButtonNewDiscountCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewDiscountCardActionPerformed
        jButtonNewDiscountCardActionPerformed("");
    }//GEN-LAST:event_jButtonNewDiscountCardActionPerformed
    private void jButtonAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdminActionPerformed
        jButtonAdminActionPerformed();
    }//GEN-LAST:event_jButtonAdminActionPerformed
    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
        jButtonUpdateActionPerformed();
    }//GEN-LAST:event_jButtonUpdateActionPerformed
    private void jButtonLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLinkActionPerformed
        jButtonLinkActionPerformed();
    }//GEN-LAST:event_jButtonLinkActionPerformed
    private void jButtonPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPromoActionPerformed
        jButtonPromoActionPerformed();
    }//GEN-LAST:event_jButtonPromoActionPerformed
    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        formWindowActivated();
    }//GEN-LAST:event_formWindowActivated
    private void jButtonDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeliveryActionPerformed
        jButtonDeliveryActionPerformed();
    }//GEN-LAST:event_jButtonDeliveryActionPerformed
    private void jButtonReturnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReturnActionPerformed
        jButtonReturnActionPerformed();
    }//GEN-LAST:event_jButtonReturnActionPerformed
    private void jButtonCheckCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckCopyActionPerformed
        jButtonCheckCopyActionPerformed();
    }//GEN-LAST:event_jButtonCheckCopyActionPerformed
    private void jButtonSellerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSellerActionPerformed
        jButtonSellerActionPerformed(0);
    }//GEN-LAST:event_jButtonSellerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdmin;
    private javax.swing.JButton jButtonCalc;
    private javax.swing.JButton jButtonCheckCopy;
    private javax.swing.JButton jButtonDelivery;
    private javax.swing.JButton jButtonDiscount;
    private javax.swing.JButton jButtonDiscountCard;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonLink;
    private javax.swing.JButton jButtonNewDiscountCard;
    private javax.swing.JButton jButtonPayType;
    private javax.swing.JButton jButtonPrintCheck;
    private javax.swing.JButton jButtonPromo;
    private javax.swing.JButton jButtonReturn;
    private javax.swing.JButton jButtonSearch;
    private javax.swing.JButton jButtonSeller;
    private javax.swing.JButton jButtonUpdate;
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