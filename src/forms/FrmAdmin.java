package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import reports.ReportBarcodeShort;
import reports.ReportCash;
import reports.ReportCheck;
import reports.ReportMarkup;
import reports.ReportSale;

public class FrmAdmin extends javax.swing.JDialog {
    private final ConfigReader conf;
    private ConnectionDb cnn;

	public FrmAdmin() {
        initComponents();
        //setModal(true);
        conf = ConfigReader.getInstance();
        cnn = ConnectionDb.getInstance();
        setTitle("Дополнительные функции. " + conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        //назначение MyKeyListener
        getAllComponents((Container) this.getContentPane());
        
        setLocationRelativeTo(null);
    }
	private void jButtonCashMoveActionPerformed() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		dispose();
		FrmCashMove frmCashMove = new FrmCashMove();
		frmCashMove.setModal(true);
		frmCashMove.setVisible(true);
	}
	private void jButtonCheckListActionPerformed() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		dispose();
		FrmCheckList frmCheckList = new FrmCheckList();
		frmCheckList.setModal(true);
		frmCheckList.setVisible(true);
	}
	private void jButtonPriceOverActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		dispose();
		FrmPriceOverList frmPriceOverList = new FrmPriceOverList();
		frmPriceOverList.setModal(true);
		frmPriceOverList.setVisible(true);
	}
    private void jButtonOrderActionPerformed() {
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
		dispose();
		FrmOrderList frmOrderList = new FrmOrderList();
		frmOrderList.setModal(true);
		frmOrderList.setVisible(true);
    }
	private void jButtonReceiptActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) {
			return;
		}
		dispose();
		FrmReceiptList frmReceiptList = new FrmReceiptList();
		frmReceiptList.setModal(true);
		frmReceiptList.setVisible(true);
	}
	private void jButtonBarcodeShortReportActionPerformed() {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		String i = JOptionPane.showInputDialog(null, "Введите номер отдела?", "ВНИМАНИЕ!", JOptionPane.QUESTION_MESSAGE);
		if (!i.equals("")) {
			try {
				int division = Integer.parseInt(i);
				dispose();
				ReportBarcodeShort reportBarcodeShort = new ReportBarcodeShort(division);
				reportBarcodeShort.setModal(true);
				reportBarcodeShort.setVisible(true);
			} catch (NumberFormatException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
				DialogBoxs.viewError(ex);
				return;
			}
		}
	}
	private void jButtonReportCashActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		dispose();
		ReportCash reportCash = new ReportCash();
		reportCash.setModal(true);
		reportCash.setVisible(true);
	}
	private void jButtonReportSaleActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		String i = JOptionPane.showInputDialog(null, "Введите номер отдела?", "ВНИМАНИЕ!", JOptionPane.QUESTION_MESSAGE);
		if (i!=null) {
			try {
				int division = Integer.parseInt(i);
				dispose();
				ReportSale reportSale = new ReportSale(division);
				reportSale.setModal(true);
				reportSale.setVisible(true);
			} catch (NumberFormatException ex){
				MyUtil.errorToLog(this.getClass().getName(), ex);
				DialogBoxs.viewError(ex);
				return;
			}
		}
	}
	private void jButtonPrintCopyCheckActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		DialogBoxs db = new DialogBoxs();
		String returnID = db.showOptionDialogGetCheckID("Копия чека (на обычный принтер)", "<html>Введите № чека<br>(без дробной части):</html>", new javax.swing.ImageIcon(getClass().getResource("/png/return_on.png")));
//System.out.println("returnID:"+returnID);
		int rrr = new Integer(returnID);
		BigDecimal currentCheckID = new BigDecimal(""+returnID+"."+cnn.clientID);
		cnn.getCheckInfo(currentCheckID);
		if (cnn.checkIsBlank()) {
			DialogBoxs.viewMessage("Нулевые чеки не печатаем!");
			return;
		}
		if (cnn.checkStatus == 0) {
			DialogBoxs.viewMessage("Копию чека можно делать\nтолько после распечатки\nиз главной формы!");
			return;
		}
		final ReportCheck rc = new ReportCheck(currentCheckID);
		rc.setModal(true);
		rc.setVisible(true);
		rc.dispose();
		cnn.getCheckInfo(cnn.currentCheckID);
	}
	private void jButtonReportMarkupActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		dispose();
		ReportMarkup reportMarkup = new ReportMarkup();
		reportMarkup.setModal(true);
		reportMarkup.setVisible(true);
	}
	private void jButtonNewDiscountCardActionPerformed() {
		dispose();
		final FrmCardAttribute frmCardAttribute = new FrmCardAttribute(1,""); //выдача новой
		frmCardAttribute.setModal(true);
		frmCardAttribute.setVisible(true);
	}
	private void jButtonDiscountCardActionPerformed() {
		dispose();
		final FrmCardAttribute frmCardAttribute =new FrmCardAttribute(2,""); //ввод анкеты
		frmCardAttribute.setModal(true);
		frmCardAttribute.setVisible(true);
	}
	private void jButtonStickerActionPerformed(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		dispose();
		FrmStickerList frmStickerList = new FrmStickerList();
		frmStickerList.setModal(true);
		frmStickerList.setVisible(true);
	}
    private void jButtonExitActionPerformed() {
        dispose();
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
                    //System.out.println(objCanonicalName);
                    if (objCanonicalName.endsWith("Field")) {
                        JTextField tf = (JTextField) e.getSource();
                        tf.transferFocus();
                    } else if (objCanonicalName.endsWith("RadioButton")) {
                        JRadioButton rb = (JRadioButton) e.getSource();
                        rb.transferFocus();
                    }
                    if (e.getSource() == jButtonExit) {
                        jButtonExitActionPerformed();
                    }
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
                    jButtonExitActionPerformed();
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

        jPanelForms1 = new javax.swing.JPanel();
        jButtonCheckList = new javax.swing.JButton();
        jButtonPriceOver = new javax.swing.JButton();
        jButtonOrder = new javax.swing.JButton();
        jButtonReceipt = new javax.swing.JButton();
        jButtonSticker = new javax.swing.JButton();
        jPanelReport = new javax.swing.JPanel();
        jButtonBarcodeShortReport = new javax.swing.JButton();
        jButtonReportCash = new javax.swing.JButton();
        jButtonReportSale = new javax.swing.JButton();
        jButtonPrintCopyCheck = new javax.swing.JButton();
        jButtonReportMarkup = new javax.swing.JButton();
        jPanelButton = new javax.swing.JPanel();
        jButtonExit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanelForms2 = new javax.swing.JPanel();
        jButtonCashMove = new javax.swing.JButton();
        jButtonDiscountCard = new javax.swing.JButton();
        jButtonNewDiscountCard = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanelForms1.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));

        jButtonCheckList.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonCheckList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check-32.png"))); // NOI18N
        jButtonCheckList.setText("Список чеков");
        jButtonCheckList.setToolTipText("Список чеков");
        jButtonCheckList.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonCheckList.setIconTextGap(10);
        jButtonCheckList.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonCheckList.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonCheckList.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonCheckList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckListActionPerformed(evt);
            }
        });

        jButtonPriceOver.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonPriceOver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check-32.png"))); // NOI18N
        jButtonPriceOver.setText("Список переоценок");
        jButtonPriceOver.setToolTipText("Список переоценок");
        jButtonPriceOver.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonPriceOver.setIconTextGap(10);
        jButtonPriceOver.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonPriceOver.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonPriceOver.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonPriceOver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPriceOverActionPerformed(evt);
            }
        });

        jButtonOrder.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check-32.png"))); // NOI18N
        jButtonOrder.setText("Заказы товаров");
        jButtonOrder.setToolTipText("Заказы товаров для магазина");
        jButtonOrder.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonOrder.setIconTextGap(10);
        jButtonOrder.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonOrder.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonOrder.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOrderActionPerformed(evt);
            }
        });

        jButtonReceipt.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonReceipt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check-32.png"))); // NOI18N
        jButtonReceipt.setText("Приходы товаров");
        jButtonReceipt.setToolTipText("Приходы товаров для магазина");
        jButtonReceipt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonReceipt.setIconTextGap(10);
        jButtonReceipt.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonReceipt.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonReceipt.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReceiptActionPerformed(evt);
            }
        });

        jButtonSticker.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonSticker.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/check-32.png"))); // NOI18N
        jButtonSticker.setText("Ценники");
        jButtonSticker.setToolTipText("Ценники");
        jButtonSticker.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonSticker.setIconTextGap(10);
        jButtonSticker.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonSticker.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonSticker.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonSticker.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStickerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelForms1Layout = new javax.swing.GroupLayout(jPanelForms1);
        jPanelForms1.setLayout(jPanelForms1Layout);
        jPanelForms1Layout.setHorizontalGroup(
            jPanelForms1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForms1Layout.createSequentialGroup()
                .addGroup(jPanelForms1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonCheckList, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPriceOver, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReceipt, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSticker, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelForms1Layout.setVerticalGroup(
            jPanelForms1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForms1Layout.createSequentialGroup()
                .addComponent(jButtonCheckList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPriceOver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReceipt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSticker, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanelReport.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Отчеты", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N

        jButtonBarcodeShortReport.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonBarcodeShortReport.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/reports-64.png"))); // NOI18N
        jButtonBarcodeShortReport.setText("Короткие штрих-коды");
        jButtonBarcodeShortReport.setToolTipText("Отчет о коротких штрих-кодах");
        jButtonBarcodeShortReport.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonBarcodeShortReport.setIconTextGap(10);
        jButtonBarcodeShortReport.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonBarcodeShortReport.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonBarcodeShortReport.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonBarcodeShortReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBarcodeShortReportActionPerformed(evt);
            }
        });

        jButtonReportCash.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonReportCash.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/reports-64.png"))); // NOI18N
        jButtonReportCash.setText("Отчет по кассе");
        jButtonReportCash.setToolTipText("Печать отчета по кассе");
        jButtonReportCash.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonReportCash.setIconTextGap(10);
        jButtonReportCash.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonReportCash.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonReportCash.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonReportCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReportCashActionPerformed(evt);
            }
        });

        jButtonReportSale.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonReportSale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/reports-64.png"))); // NOI18N
        jButtonReportSale.setText("Отчет о продажах");
        jButtonReportSale.setToolTipText("Отчет о продажах");
        jButtonReportSale.setActionCommand("Ввод дисконтной карты");
        jButtonReportSale.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonReportSale.setIconTextGap(10);
        jButtonReportSale.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonReportSale.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonReportSale.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonReportSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReportSaleActionPerformed(evt);
            }
        });

        jButtonPrintCopyCheck.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonPrintCopyCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/reports-64.png"))); // NOI18N
        jButtonPrintCopyCheck.setText("Копия чека");
        jButtonPrintCopyCheck.setToolTipText("Копия чека по номеру");
        jButtonPrintCopyCheck.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonPrintCopyCheck.setIconTextGap(10);
        jButtonPrintCopyCheck.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonPrintCopyCheck.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonPrintCopyCheck.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonPrintCopyCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintCopyCheckActionPerformed(evt);
            }
        });

        jButtonReportMarkup.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonReportMarkup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/reports-64.png"))); // NOI18N
        jButtonReportMarkup.setText("Отчет по кат. наценки");
        jButtonReportMarkup.setToolTipText("Отчет по кат. наценки");
        jButtonReportMarkup.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonReportMarkup.setIconTextGap(10);
        jButtonReportMarkup.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonReportMarkup.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonReportMarkup.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonReportMarkup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReportMarkupActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelReportLayout = new javax.swing.GroupLayout(jPanelReport);
        jPanelReport.setLayout(jPanelReportLayout);
        jPanelReportLayout.setHorizontalGroup(
            jPanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelReportLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonBarcodeShortReport, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReportCash, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReportSale, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPrintCopyCheck, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReportMarkup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        jPanelReportLayout.setVerticalGroup(
            jPanelReportLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelReportLayout.createSequentialGroup()
                .addComponent(jButtonBarcodeShortReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReportCash, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReportSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPrintCopyCheck, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReportMarkup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

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

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Дополнительные функции");

        javax.swing.GroupLayout jPanelButtonLayout = new javax.swing.GroupLayout(jPanelButton);
        jPanelButton.setLayout(jPanelButtonLayout);
        jPanelButtonLayout.setHorizontalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanelButtonLayout.setVerticalGroup(
            jPanelButtonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanelButtonLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelForms2.setBorder(javax.swing.BorderFactory.createTitledBorder(" "));

        jButtonCashMove.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonCashMove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Cash-64.png"))); // NOI18N
        jButtonCashMove.setText("Движение денег в кассе");
        jButtonCashMove.setToolTipText("Ввод инф. о движ. денег в кассе");
        jButtonCashMove.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonCashMove.setIconTextGap(10);
        jButtonCashMove.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonCashMove.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonCashMove.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonCashMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCashMoveActionPerformed(evt);
            }
        });

        jButtonDiscountCard.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonDiscountCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/card-client 64.png"))); // NOI18N
        jButtonDiscountCard.setText("<html>Заполнить анкету<br/>      для диск.карты</html>");
        jButtonDiscountCard.setToolTipText("<html>Заполнить анкету<br/>      для диск.карты</html>");
        jButtonDiscountCard.setActionCommand("Заполнить анкету для диск.карты");
        jButtonDiscountCard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonDiscountCard.setIconTextGap(10);
        jButtonDiscountCard.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonDiscountCard.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonDiscountCard.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonDiscountCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDiscountCardActionPerformed(evt);
            }
        });

        jButtonNewDiscountCard.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButtonNewDiscountCard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/new-card 64.png"))); // NOI18N
        jButtonNewDiscountCard.setText("<html>Выдать новую<br/>дисконтную карту</html>");
        jButtonNewDiscountCard.setToolTipText("Выдать новую дисконтную карту к чеку");
        jButtonNewDiscountCard.setActionCommand("Выдача новой карты");
        jButtonNewDiscountCard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButtonNewDiscountCard.setIconTextGap(10);
        jButtonNewDiscountCard.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonNewDiscountCard.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonNewDiscountCard.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonNewDiscountCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewDiscountCardActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButton1.setToolTipText("");
        jButton1.setEnabled(false);
        jButton1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton1.setIconTextGap(10);
        jButton1.setMaximumSize(new java.awt.Dimension(70, 70));
        jButton1.setMinimumSize(new java.awt.Dimension(70, 70));
        jButton1.setPreferredSize(new java.awt.Dimension(70, 70));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jButton2.setToolTipText("");
        jButton2.setEnabled(false);
        jButton2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton2.setIconTextGap(10);
        jButton2.setMaximumSize(new java.awt.Dimension(70, 70));
        jButton2.setMinimumSize(new java.awt.Dimension(70, 70));
        jButton2.setPreferredSize(new java.awt.Dimension(70, 70));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelForms2Layout = new javax.swing.GroupLayout(jPanelForms2);
        jPanelForms2.setLayout(jPanelForms2Layout);
        jPanelForms2Layout.setHorizontalGroup(
            jPanelForms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForms2Layout.createSequentialGroup()
                .addGroup(jPanelForms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonCashMove, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jButtonDiscountCard, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jButtonNewDiscountCard, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelForms2Layout.setVerticalGroup(
            jPanelForms2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelForms2Layout.createSequentialGroup()
                .addComponent(jButtonCashMove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDiscountCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonNewDiscountCard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanelForms2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanelForms1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanelReport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelReport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelForms1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelForms2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonNewDiscountCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewDiscountCardActionPerformed
        jButtonNewDiscountCardActionPerformed();
    }//GEN-LAST:event_jButtonNewDiscountCardActionPerformed
    private void jButtonDiscountCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDiscountCardActionPerformed
        jButtonDiscountCardActionPerformed();
    }//GEN-LAST:event_jButtonDiscountCardActionPerformed
    private void jButtonCashMoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCashMoveActionPerformed
        jButtonCashMoveActionPerformed();
    }//GEN-LAST:event_jButtonCashMoveActionPerformed
    private void jButtonReportSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReportSaleActionPerformed
		jButtonReportSaleActionPerformed();
    }//GEN-LAST:event_jButtonReportSaleActionPerformed
    private void jButtonReportCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReportCashActionPerformed
		jButtonReportCashActionPerformed();
    }//GEN-LAST:event_jButtonReportCashActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jButtonCheckListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckListActionPerformed
        jButtonCheckListActionPerformed();
    }//GEN-LAST:event_jButtonCheckListActionPerformed
    private void jButtonPriceOverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPriceOverActionPerformed
        jButtonPriceOverActionPerformed();
    }//GEN-LAST:event_jButtonPriceOverActionPerformed
    private void jButtonBarcodeShortReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBarcodeShortReportActionPerformed
        jButtonBarcodeShortReportActionPerformed();
    }//GEN-LAST:event_jButtonBarcodeShortReportActionPerformed
    private void jButtonOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOrderActionPerformed
        jButtonOrderActionPerformed();
    }//GEN-LAST:event_jButtonOrderActionPerformed
    private void jButtonStickerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStickerActionPerformed
		jButtonStickerActionPerformed();
    }//GEN-LAST:event_jButtonStickerActionPerformed
    private void jButtonReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReceiptActionPerformed
        jButtonReceiptActionPerformed();
    }//GEN-LAST:event_jButtonReceiptActionPerformed
    private void jButtonPrintCopyCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintCopyCheckActionPerformed
		jButtonPrintCopyCheckActionPerformed();
    }//GEN-LAST:event_jButtonPrintCopyCheckActionPerformed

    private void jButtonReportMarkupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReportMarkupActionPerformed
        jButtonReportMarkupActionPerformed();
    }//GEN-LAST:event_jButtonReportMarkupActionPerformed
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonBarcodeShortReport;
    private javax.swing.JButton jButtonCashMove;
    private javax.swing.JButton jButtonCheckList;
    private javax.swing.JButton jButtonDiscountCard;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonNewDiscountCard;
    private javax.swing.JButton jButtonOrder;
    private javax.swing.JButton jButtonPriceOver;
    private javax.swing.JButton jButtonPrintCopyCheck;
    private javax.swing.JButton jButtonReceipt;
    private javax.swing.JButton jButtonReportCash;
    private javax.swing.JButton jButtonReportMarkup;
    private javax.swing.JButton jButtonReportSale;
    private javax.swing.JButton jButtonSticker;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanelButton;
    private javax.swing.JPanel jPanelForms1;
    private javax.swing.JPanel jPanelForms2;
    private javax.swing.JPanel jPanelReport;
    // End of variables declaration//GEN-END:variables
}