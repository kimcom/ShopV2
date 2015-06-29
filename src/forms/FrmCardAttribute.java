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
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;

public class FrmCardAttribute extends javax.swing.JDialog {
    private final ConfigReader conf;
    private final ConnectionDb cnn;
    private static String barCode = "";
    private long timeBarCode = 0;
	private boolean blDiscountCardFuture = false;
            
    public boolean blDisposeStatus = false;
    private boolean blStatusBarCode = false;
	public String strBarCode;
    private int iStatus;
	private	ResultSet resScaleTable;

    public FrmCardAttribute(int _iStatus) {
        initComponents();
        iStatus = _iStatus;
        conf = ConfigReader.getInstance();
        setTitle("Дисконтная карта. "+conf.FORM_TITLE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
		//назначение MyKeyListener
		getAllComponents((Container) this.getContentPane());
        jComboBox1.setEnabled(iStatus == 1);
		jComboBox1.setEditable(iStatus == 1);
        jComboBox1.setFocusable(iStatus == 1);
//временно для ручного ввода
//		jComboBox1.setEnabled(iStatus != 0);
//		jComboBox1.setEditable(iStatus != 0);
//		jComboBox1.setFocusable(iStatus != 0);
		
		jLabel32.setVisible(false);
		jTextField32.setVisible(false);
		jLabel33.setVisible(false);
		jTextField33.setVisible(false);

		jTextField1.setEditable(iStatus==2);
		//jComboBox1.addFocusListener(new MyUtil.MyFormatedTextFocusListener());
        jTextField28.setEditable(false);
        jTextField28.setFocusable(false);

        jPanel2.setEnabled(false);
        jPanel2.setVisible(false);
        jPanel3.setEnabled(false);
        jPanel3.setVisible(false);
        jLabel40.setVisible(false);
        jButtonOK.setEnabled(false);
        pack();
        setLocationRelativeTo(null);

        cnn = ConnectionDb.getInstance();
        jTextField31.setText(cnn.checkSumBase.setScale(2, RoundingMode.HALF_UP).toPlainString());
        jTextField32.setText(cnn.checkSumDiscount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        jTextField33.setText(cnn.checkSum.setScale(2, RoundingMode.HALF_UP).toPlainString());

		jTextField1.setText("Просканируйте дисконтную карту");
		jTextField1.addFocusListener(new MyUtil.MyTextFocusListener());
		
		resScaleTable = cnn.getDiscountScaleTable();
		try {
			while (resScaleTable.next()) {
				jComboBox1.addItem(resScaleTable.getBigDecimal("Percent").setScale(2,RoundingMode.HALF_UP).toPlainString());
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
		}
		//jComboBox1.setSelectedItem("0.00");

//jTextField1.setText("9800000501863");
//jTextField1.setText("9800000929285");
//barCode = jTextField1.getText();
//requery();
    }

    private void jButtonOKActionPerformed(){
        if (!blStatusBarCode) return;
        if (cnn == null) return;
		strBarCode = jTextField1.getText();
        
		SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dt1 = null, dt2 = null;
        try {
            if (!jLabel25.getText().equals(""))
                dt1 = dateFormatIn.format(dateFormatOut.parse(jLabel25.getText()));
            if (!jLabel26.getText().equals("")) 
				dt2 = dateFormatIn.format(dateFormatOut.parse(jLabel26.getText()));
        } catch (ParseException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			JOptionPane.showMessageDialog(this, ex.getMessage(), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
        }
		BigDecimal bdPercent = new BigDecimal(jComboBox1.getSelectedItem().toString());
		if (bdPercent.compareTo(BigDecimal.ZERO)==0){
			JOptionPane.showMessageDialog(this, "Вы не указали процент скидки!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		BigDecimal bdSumma = new BigDecimal(jTextField28.getText());
        if (cnn.setDiscountCardAttribute((iStatus==2)?"card_attr_edit":"card_attr_new", strBarCode,
                jTextField21.getText(), jTextField22.getText(), jTextField23.getText(),
                jTextField24.getText(), jTextField25.getText(), jTextField26.getText(),
                dt1,
                bdPercent,bdSumma,
                dt2,"СМС")) {
			JOptionPane.showMessageDialog(this, "Информация о дисконтной карте успешно записана!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
            blDisposeStatus = true;
            dispose();
        } else {
			JOptionPane.showMessageDialog(this, "Ошибка при записи реквизитов карты!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void jButtonExitActionPerformed() {
        dispose();
    }
	private void jComboBox1ItemStateChanged(){
		if (iStatus != 1 || barCode.equals("")) return;
		BigDecimal bgPercentCard = new BigDecimal(jComboBox1.getSelectedItem().toString());
		try {
			resScaleTable.beforeFirst();
			while (resScaleTable.next()) {
				if (bgPercentCard.compareTo(resScaleTable.getBigDecimal("Percent")) == 0) {
					jTextField28.setText(resScaleTable.getBigDecimal("SumFrom").setScale(2, RoundingMode.HALF_UP).toPlainString());
				}
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
		}
	}
    private void requery(){
        if (cnn == null) return;

        if (cnn.getDiscountCardInfo(barCode)) {
            jTextField21.setText(cnn.getDiscountCardInfo("Name","String"));
            jTextField22.setText(cnn.getDiscountCardInfo("Address", "String"));
            jTextField23.setText(cnn.getDiscountCardInfo("Phone", "String"));
            jTextField24.setText(cnn.getDiscountCardInfo("Email", "String"));
            jTextField25.setText(cnn.getDiscountCardInfo("Animal", "String"));
            jTextField26.setText(cnn.getDiscountCardInfo("Notes", "String"));
            jLabel25t.setText("Дата выдачи карты:");
            jLabel26t.setText("Дата аннулирования:");
            jLabel26.setText(cnn.getDiscountCardInfo("DateOfCancellation", "DateTime"));

            BigDecimal bgPercentCard = new BigDecimal(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
            
			SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormatOut = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            if(iStatus==0){
                jLabel25.setText(dateFormatOut.format(new Date()));
                if (bgPercentCard.compareTo(BigDecimal.ZERO)==0){
					jComboBox1.setSelectedItem(cnn.getDiscountScalePercent(cnn.checkSum).setScale(2, RoundingMode.HALF_UP).toPlainString());
                } else {
					jComboBox1.setSelectedItem(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
                }
				jTextField28.setText(cnn.getDiscountCardInfo("AmountOfBuying", "BigDecimal"));
			} else if (iStatus == 1) {
				jLabel25.setText(dateFormatOut.format(new Date()));
				jComboBox1.setSelectedItem(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
            } else if (iStatus == 2) {
				if (bgPercentCard.compareTo(BigDecimal.ZERO) == 0) {
					JOptionPane.showMessageDialog(this, "Данную карту еще не выдавали!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					dispose();
				}
				String d = cnn.getDiscountCardInfo("DateOfIssue", "DateTime").toString();
				try {
					if(d.equals("")){
						jLabel25.setText(dateFormatOut.format(new Date()));
				} else {
						jLabel25.setText(dateFormatOut.format(dateFormatIn.parse(d)));
					}
				} catch (ParseException ex) {
					MyUtil.errorToLog(this.getClass().getName(), ex);
					DialogBoxs.viewError(ex);
				}
				jComboBox1.setSelectedItem(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
                jTextField28.setText(cnn.getDiscountCardInfo("AmountOfBuying", "BigDecimal"));
            }
            
            jTextField31.setText(cnn.checkSumBase.setScale(2, RoundingMode.HALF_UP).toPlainString());

            BigDecimal bdSumBase = cnn.checkSumBase;
			BigDecimal bdSumDiscount = BigDecimal.ZERO;
            jTextField32.setText(bdSumDiscount.setScale(2, RoundingMode.HALF_UP).toPlainString());
            jTextField33.setText(bdSumBase.subtract(bdSumDiscount).setScale(2, RoundingMode.HALF_UP).toPlainString());
            
            jPanel2.setEnabled(true);
            jPanel2.setVisible(true);
            pack();
            setLocationRelativeTo(null);
			blDiscountCardFuture = true;
            if (iStatus == 0){
				int status = 0;
				String dateOfCancellation = cnn.getDiscountCardInfo("DateOfCancellation", "DateTime");
				if (!dateOfCancellation.equals("")) {
					Date curdate = new Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date cardDateCancel;
					try {
						cardDateCancel = format.parse(dateOfCancellation);
					} catch (ParseException ex) {
						cardDateCancel = new Date();
					}
					if (curdate.compareTo(cardDateCancel) > 0) {
						status = 1;
					}
				}
                if (bgPercentCard.compareTo(BigDecimal.ZERO) != 0) {
                    status = 2;
				}
				if (status == 1){
					JOptionPane.showMessageDialog(this, "Дисконтная карта АННУЛИРОВАНА!\nСкидка рассчитана не будет", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					jButtonExit.requestFocus();
                } else if (status == 2) {
					JOptionPane.showMessageDialog(this, "Дисконтная карта уже выдана!\nСкидка рассчитана не будет", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
					jButtonExit.requestFocus();
                } else if (status == 0) {
                    jPanel3.setEnabled(iStatus == 0);
                    jPanel3.setVisible(iStatus == 0);
                    jLabel40.setVisible(iStatus == 0);
                    jButtonOK.setEnabled(true);
                    pack();
                    setLocationRelativeTo(null);
                    jTextField21.requestFocus();
                }
            } else {
                jPanel3.setEnabled(iStatus == 0);
                jPanel3.setVisible(iStatus == 0);
                jLabel40.setVisible(iStatus == 0);
				String dateOfCancellation = cnn.getDiscountCardInfo("DateOfCancellation", "DateTime");
				if (!dateOfCancellation.equals("")) {
					Date curdate = new Date();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date cardDateCancel;
					try {
						cardDateCancel = format.parse(dateOfCancellation);
					} catch (ParseException ex) {
						cardDateCancel = new Date();
					}
					if (curdate.compareTo(cardDateCancel) > 0) {
						JOptionPane.showMessageDialog(this, "Дисконтная карта аннулирована!\nСкидка рассчитана не будет", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
						jButtonExit.requestFocus();
					}
                }else{
                    jButtonOK.setEnabled(true);
                    pack();
                    setLocationRelativeTo(null);
                    jTextField21.requestFocus();
                }
            }
            blStatusBarCode = true;
        } else {
            jTextField1.requestFocus();
			JOptionPane.showMessageDialog(this, "Не найдена карта с штрих-кодом: ".concat(barCode), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
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
			/*
			 case KeyEvent.VK_NUMPAD0:    // штрих-код
			 //jTextField1.setText("9800000000823");
			 //jTextField1.setText("9800000000830");
			 //jTextField1.setText("9800000000151");
			 jTextField1.setText("9800000436639");
			 barCode = jTextField1.getText();
			 requery();
			 //barCode = "";
			 break;
			 /**/
//			System.out.println("keyCode: "+Integer.toString(keyCode));
            switch (keyCode) {
				case KeyEvent.VK_ENTER:    // штрих-код
					if (e.getModifiers() != 0) {
						break;
					}
                    if (objCanonicalName.endsWith("Field")) {
                            JTextField tf = (JTextField) e.getSource();
                            tf.transferFocus();
                    }
                    if (e.getSource() == jButtonOK) {
                        jButtonOKActionPerformed();
                        break;
                    }
                    if (e.getSource() == jButtonExit) {
                        jButtonExitActionPerformed();
                        break;
                    }
                    if (!blStatusBarCode && blDiscountCardFuture == false) {
                        if (!barCode.equals("")) {
                            jTextField1.setText(barCode);
                            requery();
                            barCode = "";
                            break;
                        }
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
					if (blDiscountCardFuture) break;
                    if (!blStatusBarCode && (keyCode > 47 && keyCode < 58) || (keyCode > 95 && keyCode < 106)) {
                        if (iStatus!=2) {
							try{
								long newtimeBarCode = new Date().getTime();
								if (timeBarCode + 100 < newtimeBarCode) barCode = "";
								timeBarCode = new Date().getTime();
							} catch (NoSuchMethodError ex) {
								DialogBoxs.viewMessage(ex.getMessage());
							}
						}
						barCode = barCode.concat(Character.toString(e.getKeyChar()));
                    }
                    break;
            }
            return e;
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25t = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26t = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
        jTextField22 = new javax.swing.JTextField();
        jTextField23 = new javax.swing.JTextField();
        jTextField24 = new javax.swing.JTextField();
        jTextField25 = new javax.swing.JTextField();
        jTextField26 = new javax.swing.JTextField();
        jTextField28 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Идентификатор карты:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 0, 204));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setAutoscrolls(false);
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField1.setPreferredSize(new java.awt.Dimension(78, 22));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(140, 140, 140)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(131, 131, 131))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о владельце карты:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("ФИО:");
        jLabel21.setAutoscrolls(true);
        jLabel21.setFocusable(false);
        jLabel21.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel21.setRequestFocusEnabled(false);
        jLabel21.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel22.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Адрес:");
        jLabel22.setAutoscrolls(true);
        jLabel22.setFocusable(false);
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel22.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel22.setRequestFocusEnabled(false);
        jLabel22.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel23.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel23.setText("Телефон:");
        jLabel23.setFocusable(false);
        jLabel23.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel23.setRequestFocusEnabled(false);

        jLabel24.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("E-mail:");
        jLabel24.setFocusable(false);
        jLabel24.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel24.setRequestFocusEnabled(false);

        jLabel25t.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel25t.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25t.setText("25t");
        jLabel25t.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel25t.setFocusable(false);
        jLabel25t.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel25t.setRequestFocusEnabled(false);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("25");
        jLabel25.setFocusable(false);
        jLabel25.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel25.setRequestFocusEnabled(false);

        jLabel26t.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        jLabel26t.setForeground(new java.awt.Color(255, 0, 0));
        jLabel26t.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26t.setText("26t");
        jLabel26t.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel26t.setFocusable(false);
        jLabel26t.setOpaque(true);
        jLabel26t.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel26t.setRequestFocusEnabled(false);

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("26");
        jLabel26.setFocusable(false);
        jLabel26.setOpaque(true);
        jLabel26.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel26.setRequestFocusEnabled(false);

        jLabel29.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("Животные:");
        jLabel29.setFocusable(false);
        jLabel29.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel29.setRequestFocusEnabled(false);

        jLabel30.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Примечание:");
        jLabel30.setFocusable(false);
        jLabel30.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel30.setRequestFocusEnabled(false);

        jLabel27.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("% скидки:");
        jLabel27.setFocusable(false);
        jLabel27.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel27.setRequestFocusEnabled(false);

        jLabel28.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Сумма накоп.:");
        jLabel28.setFocusable(false);
        jLabel28.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel28.setRequestFocusEnabled(false);

        jTextField21.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField21.setForeground(new java.awt.Color(102, 102, 102));
        jTextField21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField22.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField22.setForeground(new java.awt.Color(102, 102, 102));
        jTextField22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField23.setForeground(new java.awt.Color(102, 102, 102));
        jTextField23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField24.setForeground(new java.awt.Color(102, 102, 102));
        jTextField24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField25.setForeground(new java.awt.Color(102, 102, 102));
        jTextField25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField26.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField26.setForeground(new java.awt.Color(102, 102, 102));
        jTextField26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextField28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextField28.setForeground(new java.awt.Color(102, 102, 102));
        jTextField28.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jComboBox1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBox1.setBorder(null);
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(452, 452, 452))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel25t, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel26t, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField21, jTextField22, jTextField23, jTextField24, jTextField25, jTextField26});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel21, jLabel22, jLabel23, jLabel24, jLabel29, jLabel30});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField25, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField26, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField28, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(172, 172, 172)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26t, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(173, 173, 173)
                        .addComponent(jLabel25t, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel21, jLabel22, jLabel23, jLabel24, jLabel29, jTextField25});

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о сумме и скидке:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel31.setText("Сумма без скидки:");
        jLabel31.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField31.setEditable(false);
        jTextField31.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField31.setForeground(new java.awt.Color(0, 0, 204));
        jTextField31.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField31.setAutoscrolls(false);
        jTextField31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField31.setFocusable(false);
        jTextField31.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel32.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel32.setText("Сумма скидки:");
        jLabel32.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField32.setEditable(false);
        jTextField32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField32.setForeground(new java.awt.Color(0, 0, 204));
        jTextField32.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField32.setAutoscrolls(false);
        jTextField32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField32.setFocusable(false);
        jTextField32.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel33.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel33.setText("Итого сумма:");
        jLabel33.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField33.setEditable(false);
        jTextField33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField33.setForeground(new java.awt.Color(0, 0, 204));
        jTextField33.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField33.setAutoscrolls(false);
        jTextField33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField33.setFocusable(false);
        jTextField33.setPreferredSize(new java.awt.Dimension(78, 22));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

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

        jLabel40.setFont(new java.awt.Font("Tahoma", 3, 16)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 0, 0));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("<html><center>ВНИМАНИЕ!<br/>После выдачи карты чек будет ЗАКРЫТ! <br/>Изменения в чеке  будут невозможны!</html>");
        jLabel40.setFocusable(false);
        jLabel40.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel40.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        jButtonOKActionPerformed();
    }//GEN-LAST:event_jButtonOKActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        jComboBox1ItemStateChanged();
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox jComboBox1;
    public javax.swing.JLabel jLabel21;
    public javax.swing.JLabel jLabel22;
    public javax.swing.JLabel jLabel23;
    public javax.swing.JLabel jLabel24;
    public javax.swing.JLabel jLabel25;
    public javax.swing.JLabel jLabel25t;
    public javax.swing.JLabel jLabel26;
    public javax.swing.JLabel jLabel26t;
    public javax.swing.JLabel jLabel27;
    public javax.swing.JLabel jLabel28;
    public javax.swing.JLabel jLabel29;
    public javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    public javax.swing.JLabel jLabel40;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField21;
    private javax.swing.JTextField jTextField22;
    private javax.swing.JTextField jTextField23;
    private javax.swing.JTextField jTextField24;
    private javax.swing.JTextField jTextField25;
    private javax.swing.JTextField jTextField26;
    private javax.swing.JTextField jTextField28;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    // End of variables declaration//GEN-END:variables
}
