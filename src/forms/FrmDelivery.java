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
import java.util.GregorianCalendar;
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
	public String responce = "";
	private BigDecimal checkSumWithoutDelivery = BigDecimal.ZERO; // сумма чека без учета доставки
	private BigDecimal checkSumFull = BigDecimal.ZERO; // сумма чека к оплате
	private BigDecimal maxSumCheckForDelivery = BigDecimal.ZERO; // если сумма чека больше или равно 1500 - тогда доставка 0 грн иначе 200 грн.
	private BigDecimal deliverySum = BigDecimal.ZERO; // сумма за доставку по умолчанию равна 0
			
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
		//варианты доставки
		jComboBoxDeliveryOption.addItem("выберите период дня удобный для клиента");
		jComboBoxDeliveryOption.addItem("1-ая половина дня - с 11:00 до 15:00");
		jComboBoxDeliveryOption.addItem("2-ая половина дня - с 15:00 до 21:00");
		//назначение MyKeyListener
		getAllComponents((Container) this.getContentPane());
		requery();
		
		if (jTextFieldAddress.getText().equals("")) jTextFieldAddress.requestFocus();
		if (jTextFieldRegion.getText().equals("")) jTextFieldRegion.requestFocus();
		if (jTextFieldCity.getText().equals("")) jTextFieldCity.requestFocus();
		if (jTextFieldMiddleName.getText().equals("")) jTextFieldMiddleName.requestFocus();
		if (jTextFieldName.getText().equals("")) jTextFieldName.requestFocus();
		if (jTextFieldFamily.getText().equals("")) jTextFieldFamily.requestFocus();
		pack();
		setLocationRelativeTo(null);
	}
	private void requery(){
		if (cnn == null) return;
		responce = "action=DeliveryGet"
				+ "&CheckID=" + jLabelCheckID.getText().trim()
				+ "&ShopID=" + cnn.clientID
				+ "&UserID=" + cnn.userID
				+ "&CardID=" + jTextDiscountCardID.getText().trim()
				+ "";
		//System.out.println(responce);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		calendar.setTime(new Date());
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
		jTextFieldDT_Delivery.setText(dateFormat.format(calendar.getTime()));
		if (cnn.deliveryInfo(responce)) {
			jTextFieldFamily.setText(cnn.getDeliveryInfo("Family", "String"));
			jTextFieldName.setText(cnn.getDeliveryInfo("Name", "String"));
			jTextFieldMiddleName.setText(cnn.getDeliveryInfo("MiddleName", "String"));
			jTextFieldCity.setText(cnn.getDeliveryInfo("City", "String"));
			jTextFieldRegion.setText(cnn.getDeliveryInfo("Region", "String"));
			jTextFieldAddress.setText(cnn.getDeliveryInfo("Address", "String"));
			jFormattedTextFieldPhone1.setText(cnn.getDeliveryInfo("Phone1", "String"));
			jFormattedTextFieldPhone2.setText(cnn.getDeliveryInfo("Phone2", "String"));
			jTextFieldNotes.setText(cnn.getDeliveryInfo("Notes", "String"));
			jComboBoxDeliveryOption.setSelectedIndex(Integer.parseInt(cnn.getDeliveryInfo("DeliveryOption", "String")));
			if (!cnn.getDeliveryInfo("DT_delivery", "String").equals(""))
				jTextFieldDT_Delivery.setText(cnn.getDeliveryInfo("DT_delivery", "String"));

			checkSumWithoutDelivery = cnn.checkSumBase.subtract(cnn.checkSumDiscount);
			deliverySum = new BigDecimal(cnn.getDeliveryInfo("SumDelivery", "String"));
			checkSumFull = checkSumWithoutDelivery.add(deliverySum);
		}
/*
//		System.out.println("cnn.checkSumBase="+cnn.checkSumBase);
//		System.out.println("cnn.checkSumDiscount="+cnn.checkSumDiscount);
		checkSumWithoutDelivery = cnn.checkSumBase.subtract(cnn.checkSumDiscount);
		if (checkSumWithoutDelivery.compareTo(maxSumCheckForDelivery) == -1) {
			deliverySum = new BigDecimal("120");
		}
		checkSumFull = checkSumWithoutDelivery.add(deliverySum);
*/
		jTextField31.setText(checkSumWithoutDelivery.setScale(2, RoundingMode.HALF_UP).toPlainString());
		jTextField32.setText(deliverySum.setScale(2, RoundingMode.HALF_UP).toPlainString());
		jTextField33.setText(checkSumFull.setScale(2, RoundingMode.HALF_UP).toPlainString());
		
	}
	private boolean checkSetParameter(){
		String msg = "";
		if (jTextFieldFamily.getText().equals("")) msg += "Обязательное поле: Фамилия\n";
		if (jTextFieldName.getText().equals("")) msg += "Обязательное поле: Имя\n";
		if (jTextFieldCity.getText().equals("")) msg += "Обязательное поле: Город\n";
		if (jTextFieldRegion.getText().equals("")) msg += "Обязательное поле: Район\n";
		if (jTextFieldAddress.getText().equals("")) msg += "Обязательное поле: Адрес\n";
		if (jFormattedTextFieldPhone1.getText().equals("")) msg += "Обязательное поле: Телефон\n";
		if (jTextFieldDT_Delivery.getText().equals("")) msg += "Обязательное поле: Дата доставки\n";
		if (jComboBoxDeliveryOption.getSelectedIndex()==0) msg += "Обязательное поле: период дня для доставки\n";
		if (msg.length() > 0){
			JOptionPane.showMessageDialog(null, "Не корректно заполнена анкета на доставку товара.\n\n" + msg, "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/png/exit.png")));
			return false;
		}
		return true;
	}
	private void jButtonOKActionPerformed() {
		if (!checkSetParameter()) return;
		blDisposeStatus = true;
		responce = "action=DeliverySet"
				+ "&CheckID="	+ jLabelCheckID.getText().trim()
				+ "&ShopID="	+ cnn.clientID
				+ "&UserID="	+ cnn.userID
				+ "&CardID="	+ jTextDiscountCardID.getText().trim()
				+ "&Family="	+ jTextFieldFamily.getText().trim()
				+ "&Name="		+ jTextFieldName.getText().trim()
				+ "&MiddleName="+ jTextFieldMiddleName.getText().trim()
				+ "&City="		+ jTextFieldCity.getText().trim()
				+ "&Region="	+ jTextFieldRegion.getText().trim()
				+ "&Address="	+ jTextFieldAddress.getText().trim()
				+ "&Phone1="	+ jFormattedTextFieldPhone1.getText().trim()
				+ "&Phone2="	+ jFormattedTextFieldPhone2.getText().trim()
				+ "&SumCheck="	+ checkSumWithoutDelivery.setScale(2, RoundingMode.HALF_UP).toPlainString().trim()
				+ "&SumDelivery=" + deliverySum.setScale(2, RoundingMode.HALF_UP).toPlainString().trim()
				+ "&DT_delivery=" + jTextFieldDT_Delivery.getText().trim()
				+ "&DeliveryOption=" + jComboBoxDeliveryOption.getSelectedIndex()
				+ "&Notes="		+ jTextFieldNotes.getText().trim()
				+ "";
		//System.out.println("responce:"+responce);
		dispose();
	}
	private void jButtonCancelDeliveryActionPerformed(){
		blDisposeStatus = true;
		responce = "action=DeliveryCancel"
				+ "&CheckID=" + jLabelCheckID.getText().trim()
				+ "&ShopID=" + cnn.clientID
				+ "&UserID=" + cnn.userID
				+ "";
		//System.out.println("responce:"+responce);
		dispose();
	}
	private void jButtonExitActionPerformed() {
		blDisposeStatus = false;
		dispose();
	}
	private void jButtonDT_StartActionPerformed() {
		final Locale locale = new Locale("ru");
		DatePicker dp = new DatePicker((Observer) jTextFieldDT_Delivery, locale);
		// previously selected date
		Date selectedDate = dp.parseDate(jTextFieldDT_Delivery.getText());
		dp.setSelectedDate(selectedDate);
		dp.start(jTextFieldDT_Delivery);
//		dp.getScreen().addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				//здесь обработчик срабатывает 2 раза - не понятно почему так
//				//requery();
//			}
//		});

//		final Locale locale = new Locale("ru");
//		DatePicker dp = new DatePicker((Observer) jTextFieldDT_Delivery, locale);
//		// previously selected date
//		Date selectedDate = dp.parseDate(jTextFieldDT_Delivery.getText());
//		dp.setSelectedDate(selectedDate);
//		dp.start(jTextFieldDT_Delivery);
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
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jTextFieldFamily = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldMiddleName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jTextField31 = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jTextField32 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();
        jButtonCancelDelivery = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jTextFieldRegion = new javax.swing.JTextField();
        jButtonDT_Start = new javax.swing.JButton();
        jTextFieldNotes = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jTextFieldAddress = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jComboBoxDeliveryOption = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jTextFieldCity = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jFormattedTextFieldPhone1 = new javax.swing.JFormattedTextField();
        jLabel36 = new javax.swing.JLabel();
        jFormattedTextFieldPhone2 = new javax.swing.JFormattedTextField();
        jTextFieldDT_Delivery = new ObservingTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Оформление заказа на доставку:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

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
        jTextDiscountCardID.setBackground(new java.awt.Color(244, 244, 244));
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
        jLabel12.setText("Дата и время оформления заявки:");
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelOrderDilivery, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelCheckID, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextDiscountCardID, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о клиенте:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel21.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Фамилия:");
        jLabel21.setAutoscrolls(true);
        jLabel21.setFocusable(false);
        jLabel21.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel21.setRequestFocusEnabled(false);

        jLabel34.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("Имя:");
        jLabel34.setAutoscrolls(true);
        jLabel34.setFocusable(false);
        jLabel34.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel34.setRequestFocusEnabled(false);

        jLabel35.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Отчество:");
        jLabel35.setAutoscrolls(true);
        jLabel35.setFocusable(false);
        jLabel35.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel35.setRequestFocusEnabled(false);

        jTextFieldFamily.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldFamily.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldFamily.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldFamily.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFamilyActionPerformed(evt);
            }
        });

        jTextFieldName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldName.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldMiddleName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldMiddleName.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldMiddleName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldMiddleName))
                    .addComponent(jTextFieldFamily))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFamily, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldMiddleName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о сумме заказа:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel31.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel31.setText("Сумма со скидкой:");
        jLabel31.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField31.setEditable(false);
        jTextField31.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField31.setForeground(new java.awt.Color(0, 0, 204));
        jTextField31.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField31.setAutoscrolls(false);
        jTextField31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField31.setFocusable(false);
        jTextField31.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel33.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel33.setText("Сумма к оплате:");
        jLabel33.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField33.setEditable(false);
        jTextField33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField33.setForeground(new java.awt.Color(0, 0, 204));
        jTextField33.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField33.setAutoscrolls(false);
        jTextField33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField33.setFocusable(false);
        jTextField33.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel32.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel32.setText("Сумма доставки:");
        jLabel32.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField32.setEditable(false);
        jTextField32.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField32.setForeground(new java.awt.Color(0, 0, 204));
        jTextField32.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField32.setAutoscrolls(false);
        jTextField32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField32.setFocusable(false);
        jTextField32.setPreferredSize(new java.awt.Dimension(78, 22));

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
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField32, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

        jButtonCancelDelivery.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButtonCancelDelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/report-delete-32.png"))); // NOI18N
        jButtonCancelDelivery.setText("Аннулировать заявку");
        jButtonCancelDelivery.setToolTipText("Аннулировать заявку?");
        jButtonCancelDelivery.setActionCommand("Поиск");
        jButtonCancelDelivery.setAlignmentX(0.5F);
        jButtonCancelDelivery.setBorderPainted(false);
        jButtonCancelDelivery.setMaximumSize(new java.awt.Dimension(70, 70));
        jButtonCancelDelivery.setMinimumSize(new java.awt.Dimension(70, 70));
        jButtonCancelDelivery.setPreferredSize(new java.awt.Dimension(70, 70));
        jButtonCancelDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelDeliveryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(92, 92, 92)
                .addComponent(jButtonCancelDelivery, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonCancelDelivery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о доставке:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jTextFieldRegion.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldRegion.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldRegion.setToolTipText("Введите административный район в городе (необязательно)");
        jTextFieldRegion.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonDT_Start.setText("...");
        jButtonDT_Start.setToolTipText("выбор даты");
        jButtonDT_Start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDT_StartActionPerformed(evt);
            }
        });

        jTextFieldNotes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNotes.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldNotes.setToolTipText("Введите примечание (необязательно)");
        jTextFieldNotes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel26.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Дата:");
        jLabel26.setAutoscrolls(true);
        jLabel26.setFocusable(false);
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jLabel26.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel26.setRequestFocusEnabled(false);
        jLabel26.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel24.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Район:");
        jLabel24.setFocusable(false);
        jLabel24.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel24.setRequestFocusEnabled(false);

        jLabel30.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Примечание");
        jLabel30.setFocusable(false);
        jLabel30.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel30.setRequestFocusEnabled(false);

        jTextFieldAddress.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldAddress.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldAddress.setToolTipText("Введите адрес доставки в формате: ул. Мира, 46 Б, кв. 1");
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

        jComboBoxDeliveryOption.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxDeliveryOption.setToolTipText("Выберите вариант доставки");
        jComboBoxDeliveryOption.setBorder(null);
        jComboBoxDeliveryOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDeliveryOptionActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Город:");
        jLabel25.setFocusable(false);
        jLabel25.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel25.setRequestFocusEnabled(false);

        jLabel37.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel37.setText("Период дня:");
        jLabel37.setFocusable(false);
        jLabel37.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel37.setRequestFocusEnabled(false);

        jTextFieldCity.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldCity.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldCity.setToolTipText("Введите город в формате: г. Харьков или м. Харкiв или пгт. Солоницевка");
        jTextFieldCity.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCityActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel23.setText("Телефон:");
        jLabel23.setFocusable(false);
        jLabel23.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel23.setRequestFocusEnabled(false);

        jFormattedTextFieldPhone1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        try {
            jFormattedTextFieldPhone1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("+380#########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldPhone1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N

        jLabel36.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel36.setText("Телефон 2:");
        jLabel36.setFocusable(false);
        jLabel36.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel36.setRequestFocusEnabled(false);

        jFormattedTextFieldPhone2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        try {
            jFormattedTextFieldPhone2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("+380#########")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        jFormattedTextFieldPhone2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jFormattedTextFieldPhone2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFormattedTextFieldPhone2ActionPerformed(evt);
            }
        });

        jTextFieldDT_Delivery.setEditable(false);
        jTextFieldDT_Delivery.setToolTipText("дата");
        jTextFieldDT_Delivery.setFocusable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jTextFieldCity, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jTextFieldAddress)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jFormattedTextFieldPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextFieldPhone2, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jTextFieldDT_Delivery, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jComboBoxDeliveryOption, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldNotes))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldCity, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextFieldPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jFormattedTextFieldPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonDT_Start, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldDT_Delivery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxDeliveryOption, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void jComboBoxDeliveryOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDeliveryOptionActionPerformed
//        if (evt.getModifiers()!=0)
//			jComboBoxDeliveryOptionActionPerformed();
    }//GEN-LAST:event_jComboBoxDeliveryOptionActionPerformed

    private void jButtonDT_StartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDT_StartActionPerformed
        jButtonDT_StartActionPerformed();
    }//GEN-LAST:event_jButtonDT_StartActionPerformed

    private void jTextFieldFamilyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFamilyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFamilyActionPerformed

    private void jFormattedTextFieldPhone2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldPhone2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jFormattedTextFieldPhone2ActionPerformed

    private void jTextFieldCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCityActionPerformed

    private void jButtonCancelDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelDeliveryActionPerformed
        jButtonCancelDeliveryActionPerformed();
    }//GEN-LAST:event_jButtonCancelDeliveryActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCancelDelivery;
    private javax.swing.JButton jButtonDT_Start;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox jComboBoxDeliveryOption;
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
    public javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    public javax.swing.JLabel jLabel34;
    public javax.swing.JLabel jLabel35;
    public javax.swing.JLabel jLabel36;
    public javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabelCheckID;
    private javax.swing.JLabel jLabelOrderDilivery;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    public javax.swing.JTextField jTextDiscountCardID;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldCity;
    private javax.swing.JTextField jTextFieldDT_Delivery;
    private javax.swing.JTextField jTextFieldFamily;
    private javax.swing.JTextField jTextFieldMiddleName;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNotes;
    private javax.swing.JTextField jTextFieldRegion;
    // End of variables declaration//GEN-END:variables
}
