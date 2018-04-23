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
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import main.ConfigReader;
import main.MyUtil;

public class FrmDelivery extends javax.swing.JDialog {
	private final ConfigReader conf;
	private final ConnectionDb cnn;
	public boolean blDisposeStatus = false;
	public String discountCardID = "";
	public String barCode = "";

	public FrmDelivery(String IDCard) {
		discountCardID = IDCard;
		conf = ConfigReader.getInstance();
		cnn = ConnectionDb.getInstance();
		initComponents();
		setTitle("Заявка на доставку. " + conf.FORM_TITLE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
		jLabelCheckID.setText(cnn.currentCheckID.setScale(4, RoundingMode.HALF_UP).toPlainString());
		jLabelOrderDilivery.setText(MyUtil.getCurrentDateTime(1));
		jTextDiscountCardID.setText(cnn.checkCardID);
		//назначение MyKeyListener
		getAllComponents((Container) this.getContentPane());
		requery();
		pack();
		setLocationRelativeTo(null);
	}
	public void requery(){
		if (cnn == null) return;
		if (cnn.getDiscountCardInfo(discountCardID)){
			jTextFieldFamily.setText(cnn.getDiscountCardInfo("Family", "String"));
			jTextFieldName.setText(cnn.getDiscountCardInfo("Name", "String"));
			jTextFieldMiddleName.setText(cnn.getDiscountCardInfo("MiddleName", "String"));
			jTextFieldAddress.setText(cnn.getDiscountCardInfo("Address", "String"));
			jFormattedTextFieldPhone1.setText(cnn.getDiscountCardInfo("Phone1", "String"));
			jFormattedTextFieldPhone2.setText(cnn.getDiscountCardInfo("Phone2", "String"));
			jTextFieldRegion.setText(cnn.getDiscountCardInfo("Email", "String"));
			jTextFieldNotes.setText(cnn.getDiscountCardInfo("Notes", "String"));
			jTextDiscountPercent.setText(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
			jTextFieldSumma.setText(cnn.getDiscountCardInfo("AmountOfBuying", "BigDecimal"));
			jTextField31.setText(cnn.checkSumBase.setScale(2, RoundingMode.HALF_UP).toPlainString());
			jTextField32.setText(cnn.checkSumDiscount.setScale(2, RoundingMode.HALF_UP).toPlainString());
			jTextField33.setText(cnn.checkSum.setScale(2, RoundingMode.HALF_UP).toPlainString());
		}
	}
	private void jButtonOKActionPerformed() {
		blDisposeStatus = true;
		dispose();
	}
	private void jButtonExitActionPerformed() {
		dispose();
	}
	private void jButtonDT_StartActionPerformed() {
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
	private void jButtonDT_StopActionPerformed() {
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
//			System.out.println(e);
/*			if(e.getSource()==jComboBox2) {
				if (e.getKeyCode()==KeyEvent.VK_DOWN) {
					jComboBox2.setPopupVisible(true);
					return;
				}
				if (e.getKeyCode()==KeyEvent.VK_UP) {
					jComboBox2.setPopupVisible(false);
					return;
				}
			}
			 */
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
/*					if (e.getSource() == jButtonOK) {
						jButtonOKActionPerformed();
						break;
					}
					if (e.getSource() == jButtonExit) {
						jButtonExitActionPerformed();
						break;
					}
*/					break;
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
					//jButtonExitActionPerformed();
					break;
				default:
					break;
			}
			return e;
		}
	}
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabelCheckID = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextDiscountCardID = new javax.swing.JTextField();
        jLabelOrderDilivery = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jTextFieldFamily = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldMiddleName = new javax.swing.JTextField();
        jTextFieldRegion = new javax.swing.JTextField();
        jTextFieldNotes = new javax.swing.JTextField();
        jTextFieldSumma = new javax.swing.JTextField();
        jFormattedTextFieldPhone1 = new javax.swing.JFormattedTextField();
        jFormattedTextFieldPhone2 = new javax.swing.JFormattedTextField();
        jTextDiscountPercent = new javax.swing.JTextField();
        jTextFieldAddress = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jTextFieldCity = new javax.swing.JTextField();
        jTextFieldDT_Start = new ObservingTextField();
        jButtonDT_Start = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jTextFieldDT_Stop = new ObservingTextField();
        jButtonDT_Stop = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Идентификатор заказа на доставку:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Чек №:");
        jLabel11.setAutoscrolls(true);
        jLabel11.setFocusable(false);
        jLabel11.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel11.setRequestFocusEnabled(false);
        jLabel11.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabelCheckID.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelCheckID.setForeground(java.awt.SystemColor.desktop);
        jLabelCheckID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel13.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Дисконт. №:");
        jLabel13.setAutoscrolls(true);
        jLabel13.setFocusable(false);
        jLabel13.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel13.setRequestFocusEnabled(false);
        jLabel13.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jTextDiscountCardID.setEditable(false);
        jTextDiscountCardID.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextDiscountCardID.setForeground(new java.awt.Color(0, 0, 204));
        jTextDiscountCardID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextDiscountCardID.setAutoscrolls(false);
        jTextDiscountCardID.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextDiscountCardID.setPreferredSize(new java.awt.Dimension(78, 22));
        jTextDiscountCardID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextDiscountCardIDActionPerformed(evt);
            }
        });

        jLabelOrderDilivery.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabelOrderDilivery.setForeground(java.awt.SystemColor.desktop);
        jLabelOrderDilivery.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel12.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("Дата и время заявки:");
        jLabel12.setAutoscrolls(true);
        jLabel12.setFocusable(false);
        jLabel12.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel12.setRequestFocusEnabled(false);
        jLabel12.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelOrderDilivery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelCheckID, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextDiscountCardID, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelOrderDilivery, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextDiscountCardID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelCheckID, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о доставке:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Фамилия:");
        jLabel21.setAutoscrolls(true);
        jLabel21.setFocusable(false);
        jLabel21.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel21.setRequestFocusEnabled(false);
        jLabel21.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel23.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel23.setText("Телефон 1:");
        jLabel23.setFocusable(false);
        jLabel23.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel23.setRequestFocusEnabled(false);

        jLabel36.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel36.setText("Телефон 2:");
        jLabel36.setFocusable(false);
        jLabel36.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel36.setRequestFocusEnabled(false);

        jLabel24.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Район:");
        jLabel24.setFocusable(false);
        jLabel24.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel24.setRequestFocusEnabled(false);

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

        jLabel34.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("Имя:");
        jLabel34.setAutoscrolls(true);
        jLabel34.setFocusable(false);
        jLabel34.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel34.setRequestFocusEnabled(false);
        jLabel34.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel35.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Отчество:");
        jLabel35.setAutoscrolls(true);
        jLabel35.setFocusable(false);
        jLabel35.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel35.setRequestFocusEnabled(false);
        jLabel35.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jTextFieldFamily.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldFamily.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldFamily.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldName.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldMiddleName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldMiddleName.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldMiddleName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldRegion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldRegion.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldRegion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldNotes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNotes.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldNotes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldSumma.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldSumma.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldSumma.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldSumma.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jFormattedTextFieldPhone1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        try {
            jFormattedTextFieldPhone1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("+380#########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldPhone1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jFormattedTextFieldPhone2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        try {
            jFormattedTextFieldPhone2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("+380#########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldPhone2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jTextDiscountPercent.setEditable(false);
        jTextDiscountPercent.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextDiscountPercent.setForeground(new java.awt.Color(102, 102, 102));
        jTextDiscountPercent.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextDiscountPercent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldAddress.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldAddress.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel22.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Адрес:");
        jLabel22.setAutoscrolls(true);
        jLabel22.setFocusable(false);
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel22.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel22.setRequestFocusEnabled(false);
        jLabel22.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel25.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Город:");
        jLabel25.setFocusable(false);
        jLabel25.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel25.setRequestFocusEnabled(false);

        jTextFieldCity.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldCity.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldDT_Start.setEditable(false);
        jTextFieldDT_Start.setToolTipText("доставка с ");
        jTextFieldDT_Start.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldDT_Start.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextFieldDT_Start.setFocusable(false);
        jTextFieldDT_Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldDT_StartActionPerformed(evt);
            }
        });

        jButtonDT_Start.setText("...");
        jButtonDT_Start.setToolTipText("выбор даты");
        jButtonDT_Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDT_StartActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Время доставки с :");
        jLabel26.setAutoscrolls(true);
        jLabel26.setFocusable(false);
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel26.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel26.setRequestFocusEnabled(false);
        jLabel26.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jTextFieldDT_Stop.setEditable(false);
        jTextFieldDT_Stop.setToolTipText("до");
        jTextFieldDT_Stop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldDT_Stop.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextFieldDT_Stop.setFocusable(false);

        jButtonDT_Stop.setText("...");
        jButtonDT_Stop.setToolTipText("выбор даты");
        jButtonDT_Stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDT_StopActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("по :");
        jLabel29.setFocusable(false);
        jLabel29.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel29.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(11, 11, 11)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldFamily, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jTextFieldCity, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jFormattedTextFieldPhone1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(6, 6, 6)
                                                .addComponent(jFormattedTextFieldPhone2))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldRegion))))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextFieldMiddleName))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(74, 74, 74)
                                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSumma, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldDT_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonDT_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldAddress)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFamily, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldMiddleName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextFieldPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextFieldPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonDT_Start))
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextFieldDT_Stop, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonDT_Stop)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSumma, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextDiscountPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jPanel1.getAccessibleContext().setAccessibleName("Идентификатор заказа:");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        jButtonOKActionPerformed();
    }//GEN-LAST:event_jButtonOKActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jTextDiscountCardIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextDiscountCardIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextDiscountCardIDActionPerformed
    private void jButtonDT_StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDT_StartActionPerformed
        jButtonDT_StartActionPerformed();
    }//GEN-LAST:event_jButtonDT_StartActionPerformed
    private void jButtonDT_StopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDT_StopActionPerformed
        jButtonDT_StopActionPerformed();
    }//GEN-LAST:event_jButtonDT_StopActionPerformed

    private void jTextFieldDT_StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDT_StartActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldDT_StartActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDT_Start;
    private javax.swing.JButton jButtonDT_Stop;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JFormattedTextField jFormattedTextFieldPhone1;
    private javax.swing.JFormattedTextField jFormattedTextFieldPhone2;
    public javax.swing.JLabel jLabel11;
    public javax.swing.JLabel jLabel12;
    public javax.swing.JLabel jLabel13;
    public javax.swing.JLabel jLabel21;
    public javax.swing.JLabel jLabel22;
    public javax.swing.JLabel jLabel23;
    public javax.swing.JLabel jLabel24;
    public javax.swing.JLabel jLabel25;
    public javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    public javax.swing.JLabel jLabel28;
    public javax.swing.JLabel jLabel29;
    public javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    public javax.swing.JLabel jLabel34;
    public javax.swing.JLabel jLabel35;
    public javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabelCheckID;
    private javax.swing.JLabel jLabelOrderDilivery;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JTextField jTextDiscountCardID;
    private javax.swing.JTextField jTextDiscountPercent;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldCity;
    private javax.swing.JTextField jTextFieldDT_Start;
    private javax.swing.JTextField jTextFieldDT_Stop;
    private javax.swing.JTextField jTextFieldFamily;
    private javax.swing.JTextField jTextFieldMiddleName;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNotes;
    private javax.swing.JTextField jTextFieldRegion;
    private javax.swing.JTextField jTextFieldSumma;
    // End of variables declaration//GEN-END:variables
}
