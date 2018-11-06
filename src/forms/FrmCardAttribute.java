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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;
import tablemodel.TmCardAnimals;

public class FrmCardAttribute extends javax.swing.JDialog {
    private final ConfigReader conf;
    private final ConnectionDb cnn;
    private static String barCode = "";
    private long timeBarCode = 0;
	private boolean blDiscountCardFuture = false;
            
    public boolean blDisposeStatus = false;
    private boolean blStatusBarCode = false;
	public String strBarCode;
	public String parentCardID = "";
    private int iStatus;
	private	ResultSet resScaleTable;
	private	ResultSet resInfoTable;

	private List<String> arrayAnimal = new ArrayList<String>();
	private List<String> arrayBreed = new ArrayList<String>();
	
    public FrmCardAttribute(int _iStatus, String _parentCardID) {
        conf = ConfigReader.getInstance();
		cnn = ConnectionDb.getInstance();
        initComponents();
        iStatus = _iStatus;
		parentCardID = _parentCardID;
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
        jTextFieldSumma.setEditable(false);
        jTextFieldSumma.setFocusable(false);

        jPanel2.setEnabled(false);
        jPanel2.setVisible(false);
        jPanel3.setEnabled(false);
        jPanel3.setVisible(false);
        jLabel40.setVisible(false);
        jButtonOK.setEnabled(false);
		jPanelAnimalAdd.setEnabled(false);
		jPanelAnimalAdd.setVisible(false);
		
		pack();
        setLocationRelativeTo(null);

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
		resInfoTable = cnn.getDiscountInfoTable();
		try {
			while (resInfoTable.next()) {
				jComboBox4.addItem(resInfoTable.getString("Info").toString());
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
		}
		//System.out.println("info:" + jComboBox4.getSelectedIndex() + jComboBox4.getSelectedItem().toString());
		//jComboBox1.setSelectedItem("0.00");

//jTextField1.setText("9800000501863");
//jTextField1.setText("9800000929285");
//jTextField1.setText("2200000450234");
//jTextField1.setText("2200000191847");
//jTextField1.setText("2200000363497");
//jTextField1.setText("200136");
//jTextField1.setText("9800000037621");
//jTextField1.setText("9800001963332");
//jTextField1.setText("9800002106004");
//barCode = jTextField1.getText();
//requery();

		getListAnimals();
		//jTextFieldFamily.requestFocus();
		
		final JTextField textfield = (JTextField) jComboBox3.getEditor().getEditorComponent();
		textfield.addKeyListener(new BreedKeyListener());
		
		if (iStatus == 2 && !parentCardID.equals("")) {
			//DialogBoxs.viewMessage("Требуется ввести питомцев!");
			JOptionPane.showMessageDialog(this, "Требуется ввести питомцев!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			jTextField1.setText(parentCardID);
			barCode = jTextField1.getText();
			strBarCode = barCode;
			parentCardID = "";
			_parentCardID = "";
			requery();
		}
    }

	private class BreedKeyListener extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			final JTextField textfield = (JTextField) jComboBox3.getEditor().getEditorComponent();
			int keyCode = e.getKeyCode();
			switch (keyCode) {
				case KeyEvent.VK_ENTER:    // штрих-код
					break;
				case KeyEvent.VK_UP:
					break;
				case KeyEvent.VK_DOWN:
					break;
				default:
					String enteredText = textfield.getText();
					List<String> filterArray = new ArrayList<String>();
					for (int i = 0; i < arrayBreed.size(); i++) {
						if (arrayBreed.get(i).toLowerCase().contains(enteredText.toLowerCase())) {
							filterArray.add(arrayBreed.get(i));
						}
					}
					if (filterArray.size() > 0) {
						jComboBox3.setModel(new DefaultComboBoxModel(filterArray.toArray()));
						jComboBox3.setSelectedItem(enteredText);
						jComboBox3.showPopup();
					} else {
						jComboBox3.hidePopup();
					}
			}
		}
	}
	
    private void jButtonOKActionPerformed(){
        if (!blStatusBarCode) return;
        if (cnn == null) return;
		strBarCode = jTextField1.getText();

		//System.out.println("info:" + jComboBox4.getSelectedIndex() +jComboBox4.getSelectedItem().toString());
		
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
		//по требованию Путятиной В.
		if (jTableAnimals.getRowCount()==0) {
			JOptionPane.showMessageDialog(this, "Вы не указали питомца!\n\nС 23.02.2018 года - согласно указанию руководства,\n\nнеобходимо обязательно указывать питомца!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (jComboBox4.getSelectedIndex()==0) {
			int i = JOptionPane.showConfirmDialog(this,
					"Вы не указали информацию\n\n"
					+ "О том как клиенты узнали о магазине!\n\n"
					+ "Желаете указать сейчас?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (i == 0)	return;
		}
		BigDecimal bdSumma = BigDecimal.ZERO;
		if (!jTextFieldSumma.getText().equals("")){
			bdSumma = new BigDecimal(jTextFieldSumma.getText());
		}
		String phone1 = jFormattedTextFieldPhone1.getText().trim().replaceAll(" ","");
		String phone2 = jFormattedTextFieldPhone2.getText().trim().replaceAll(" ","");
		if (phone1.length() != 13 || phone1.length() == 4){
			DialogBoxs.viewMessageWarning("Некорректно указан 1-ый телефон!",this);
			return;
		}
		if (phone2.length() != 13 && phone2.length() !=4){
			DialogBoxs.viewMessageWarning("Некорректно указан 2-ый телефон!",this);
			return;
		}
		phone1 = jFormattedTextFieldPhone1.getText().trim().replaceAll(" ", "%");
		phone2 = jFormattedTextFieldPhone2.getText().trim().replaceAll(" ", "%");

//if (0 == 0) return;
		String action = "card_attr_new";
		String cardID = strBarCode;
		if (iStatus==2) action = "card_attr_edit";
		if (iStatus==3) action = "card_attr_new_by_parent";
		if (cnn.setDiscountCardAttribute(action, cardID,
                jTextFieldFamily.getText(), jTextFieldName.getText(), jTextFieldMiddleName.getText(),
				jTextFieldAddress.getText(), phone1, phone2,
                jTextFieldEmail.getText(), "", "", jTextFieldNotes.getText(),
                dt1,
                bdPercent, bdSumma,
                dt2,Integer.toString(jComboBox4.getSelectedIndex()),parentCardID)) {
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
					jTextFieldSumma.setText(resScaleTable.getBigDecimal("SumFrom").setScale(2, RoundingMode.HALF_UP).toPlainString());
				}
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
		}
	}
    private void jButtonAnimalAddActionPerformed(){
		jButtonAnimalAdd.setVisible(false);
		jButtonAnimalDel.setVisible(false);
		jPanelAnimalAdd.setEnabled(true);
		jPanelAnimalAdd.setVisible(true);
		pack();
		setLocationRelativeTo(null);
		jComboBox2.requestFocus();
	}
	private void jButtonAnimalDelActionPerformed(){
		int selectedRow = jTableAnimals.getSelectedRow();
		if (selectedRow == -1) { 
			JOptionPane.showMessageDialog(this, "Выберите запись в таблице", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		int rowNum = jTableAnimals.getRowSorter().convertRowIndexToModel(selectedRow);
		int i = JOptionPane.showConfirmDialog(this, "Подтвердите удаление:\nВид животного:" + jTableAnimals.getModel().getValueAt(rowNum, 1).toString() + "\nПорода: " + jTableAnimals.getModel().getValueAt(rowNum, 2).toString(), "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (i == 0) {
			barCode = jTextField1.getText();
			if(cnn.delDiscountCardAnimal(barCode, Integer.decode(jTableAnimals.getModel().getValueAt(rowNum, 0).toString()))){
				requeryTableAnimals();
				jTextFieldNotes.requestFocus();
			}else{
				JOptionPane.showMessageDialog(this, "Возникла ошибка при удалении.\n\nСообщите разработчику.", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
	private void jButtonAnimalSaveActionPerformed(){
		if (jComboBox2.getSelectedItem().toString()=="" || jComboBox3.getSelectedItem().toString()=="") {
			JOptionPane.showMessageDialog(this, "Заполните вид и породу!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
//		if (jTextFieldPetName.getText().equals("") || jTextFieldPetDT.getText().equals("")) {
//			JOptionPane.showMessageDialog(this, "Заполните кличку и дату рождения!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
//			return;
//		}
		JTextField textfield = (JTextField) jComboBox2.getEditor().getEditorComponent();
		String enteredText = textfield.getText();
		List<String> filterArray = new ArrayList<String>();
		for (int i = 0; i < arrayAnimal.size(); i++) {
			if (arrayAnimal.get(i).toLowerCase().equals(enteredText.toLowerCase())) {
				filterArray.add(arrayAnimal.get(i));
			}
		}
		if (filterArray.size() == 0) {
			JOptionPane.showMessageDialog(this, "Вы должны выбрать вид питомца из списка!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		textfield = (JTextField) jComboBox3.getEditor().getEditorComponent();
		enteredText = textfield.getText();
		filterArray = new ArrayList<String>();
		for (int i = 0; i < arrayBreed.size(); i++) {
			if (arrayBreed.get(i).toLowerCase().equals(enteredText.toLowerCase())) {
				filterArray.add(arrayBreed.get(i));
			}
		}
		if (filterArray.size() == 0) {
			JOptionPane.showMessageDialog(this, "Вы должны выбрать породу из списка!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		barCode = jTextField1.getText();
		if (cnn.setDiscountCardAttribute("add_animal", barCode,	"", "", "", "", "", "",	"",  
				jComboBox2.getSelectedItem().toString(), jComboBox3.getSelectedItem().toString(), jTextFieldPetName.getText(), jTextFieldPetDT.getText(),
				BigDecimal.ZERO, BigDecimal.ZERO, "", "", "")) {
			JOptionPane.showMessageDialog(this, "Информация о питомце успешно записана!", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
			requeryTableAnimals();
			jComboBox2.setSelectedIndex(0);
			jComboBox3.setSelectedIndex(0);
			jPanelAnimalAdd.setEnabled(false);
			jPanelAnimalAdd.setVisible(false);
			pack();
			jButtonAnimalAdd.setVisible(true);
			jButtonAnimalDel.setVisible(true);
			jTextFieldNotes.requestFocus();
			blDisposeStatus = true;
		} else {
			JOptionPane.showMessageDialog(this, "Ошибка при записи информации о питомце!\n\nВозможно Вы уже добавили такого питомца.", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
		}
	}
	private void jButtonAnimalCancelActionPerformed(){
		jComboBox2.setSelectedIndex(0);
		jComboBox3.setSelectedIndex(0);
		jPanelAnimalAdd.setEnabled(false);
		jPanelAnimalAdd.setVisible(false);
		pack();
		jButtonAnimalAdd.setVisible(true);
		jButtonAnimalDel.setVisible(true);
	}
	private void jButtonPetDTActionPerformed(){
		final Locale locale = new Locale("ru");
		if (jTextFieldPetDT.getText().equals("")) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
			calendar.setTime(new Date());
//			calendar.add(GregorianCalendar.YEAR, - (calendar.get(GregorianCalendar.YEAR) - 2010));
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
			jTextFieldPetDT.setText(dateFormat.format(calendar.getTime()));
		}
		//final Locale locale = new Locale("ru");
		DatePicker dp = new DatePicker((Observer) jTextFieldPetDT, locale);
		// previously selected date
		Date selectedDate = dp.parseDate(jTextFieldPetDT.getText());
		dp.setSelectedDate(selectedDate);
		dp.start(jTextFieldPetDT);
//		dp.getScreen().addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {
//				//здесь обработчик срабатывает 2 раза - не понятно почему так
//				requeryTableAnimals();
//			}
//		});
	}
	private void requery(){
        if (cnn == null) return;

		if (!parentCardID.equals("")) barCode = parentCardID;
        if (cnn.getDiscountCardInfo(barCode)) {
            jTextFieldFamily.setText(cnn.getDiscountCardInfo("Family","String"));
            jTextFieldName.setText(cnn.getDiscountCardInfo("Name","String"));
            jTextFieldMiddleName.setText(cnn.getDiscountCardInfo("MiddleName","String"));
            jTextFieldAddress.setText(cnn.getDiscountCardInfo("Address", "String"));
            jFormattedTextFieldPhone1.setText(cnn.getDiscountCardInfo("Phone1", "String"));
            jFormattedTextFieldPhone2.setText(cnn.getDiscountCardInfo("Phone2", "String"));
            jTextFieldEmail.setText(cnn.getDiscountCardInfo("Email", "String"));
            jTextFieldNotes.setText(cnn.getDiscountCardInfo("Notes", "String"));
			if (iStatus==2) {
				jScrollPaneAnimals.setToolTipText(cnn.getDiscountCardInfo("AnimalOld", "String"));
				jButtonAnimalAdd.setToolTipText(cnn.getDiscountCardInfo("AnimalOld", "String"));
			}
			jTextFieldNotes.setToolTipText(cnn.getDiscountCardInfo("AnimalOld", "String"));
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
				jTextFieldSumma.setText(cnn.getDiscountCardInfo("AmountOfBuying", "BigDecimal"));
			} else if (iStatus == 1) {
				jLabel25.setText(dateFormatOut.format(new Date()));
				jComboBox1.setSelectedItem(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
			} else if (iStatus == 3) {
				jLabel25.setText(dateFormatOut.format(new Date()));
				jComboBox1.setSelectedItem(cnn.getDiscountCardInfo("PercentOfDiscount", "BigDecimal"));
				//jComboBox4.setSelectedItem(cnn.getDiscountCardInfo("HowWeLearn", "String"));
				jComboBox4.setSelectedIndex(Integer.parseInt(cnn.getDiscountCardInfo("SourceInfo", "String")));
				jTextFieldSumma.setText(cnn.getDiscountCardInfo("AmountOfBuying", "BigDecimal"));
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
                jTextFieldSumma.setText(cnn.getDiscountCardInfo("AmountOfBuying", "BigDecimal"));
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
                    jTextFieldFamily.requestFocus();
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
                    jTextFieldFamily.requestFocus();
                }
            }
    
			requeryTableAnimals();
			
            blStatusBarCode = true;
        } else {
            jTextField1.requestFocus();
			JOptionPane.showMessageDialog(this, "Не найдена карта с штрих-кодом: ".concat(barCode), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
        }
    }
	private void requeryTableAnimals(){
		jTableAnimals.setModel(new TmCardAnimals(getCardAnimals()));
		jTableAnimals.setDefaultRenderer(jTableAnimals.getColumnClass(0), new MyRenderer());
		jTableAnimals.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		jTableAnimals.setRowHeight(17);
		jTableAnimals.getColumnModel().getColumn(0).setMinWidth(0);
		jTableAnimals.getColumnModel().getColumn(0).setMaxWidth(0);
		jTableAnimals.getColumnModel().getColumn(0).setResizable(false);
//			jTableAnimals.getColumnModel().getColumn(0).setPreferredWidth(20);
		jTableAnimals.getColumnModel().getColumn(1).setPreferredWidth(40);
		jTableAnimals.getColumnModel().getColumn(2).setPreferredWidth(50);
		jTableAnimals.getColumnModel().getColumn(3).setPreferredWidth(40);
		jTableAnimals.getColumnModel().getColumn(4).setPreferredWidth(50);
	}

	private ResultSet getCardAnimals() {
		if (cnn == null) {
			return null;
		}
		ResultSet rs = cnn.getCardAnimals(barCode);
		return rs;
	}
	private void getListAnimals() {
		if (cnn == null) {
			return;
		}
		jComboBox2.addItem("");
		ResultSet rs = cnn.getListAnimals();
		try {
			while (rs.next()) {
				jComboBox2.addItem(rs.getString(1));
				arrayAnimal.add(rs.getString(1));
			}
		} catch (Exception e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
		}
	}
	private void getListBreeds() {
		if (cnn == null) {
			return;
		}
		jComboBox3.addItem("");
		if (jComboBox2.getSelectedIndex()>0){
			ResultSet rs = cnn.getListBreeds(jComboBox2.getSelectedItem().toString());
			try {
				while (rs.next()) {
					jComboBox3.addItem(rs.getString(1));
					arrayBreed.add(rs.getString(1));
				}
			} catch (Exception e) {
				MyUtil.errorToLog(this.getClass().getName(), e);
			}
		}
	}
	
	public class MyRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setHorizontalAlignment(SwingConstants.LEFT);
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
			label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 11));
			label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			return label;
		}
	}
    private List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        List<Component> compList = new ArrayList<Component>();
        for (Component comp : comps) {
//            String canonicalName = comp.getClass().getCanonicalName();
//            System.out.println("addKeyListener: " + comp.getName() + "  " + canonicalName + "    focus=" + comp.isDisplayable());
			if (comp == jComboBox2) continue;
			if (comp == jComboBox3) continue;
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
			//case KeyEvent.VK_NUMPAD0:    // штрих-код
			 //jTextField1.setText("9800000000823");
			 //jTextField1.setText("9800000000830");
			 //jTextField1.setText("9800000000151");
//jTextField1.setText("9800000436578");
//barCode = jTextField1.getText();
//requery();
			 //break;
			 /**/
//			System.out.println("keyCode: "+Integer.toString(keyCode));
//barCode = "9800000000151";
//barCode = "9800001906087";
            switch (keyCode) {
				case KeyEvent.VK_ENTER:    // штрих-код
					if (e.getModifiers() != 0) {
						break;
					}
                    if (objCanonicalName.endsWith("Field")) {
                            JTextField tf = (JTextField) e.getSource();
                            tf.transferFocus();
                    }
//jTextField1.setText("9800000000151");
//jTextField1.setText("9800002294008");
//barCode = jTextField1.getText();
//requery();
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
        jLabel36 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25t = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26t = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jTextFieldFamily = new javax.swing.JTextField();
        jTextFieldName = new javax.swing.JTextField();
        jTextFieldMiddleName = new javax.swing.JTextField();
        jTextFieldAddress = new javax.swing.JTextField();
        jTextFieldEmail = new javax.swing.JTextField();
        jTextFieldNotes = new javax.swing.JTextField();
        jTextFieldSumma = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jFormattedTextFieldPhone1 = new javax.swing.JFormattedTextField();
        jFormattedTextFieldPhone2 = new javax.swing.JFormattedTextField();
        jPanelAnimalAdd = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButtonAnimalSave = new javax.swing.JButton();
        jButtonAnimalCancel = new javax.swing.JButton();
        jLabel39 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jTextFieldPetName = new javax.swing.JTextField();
        jButtonPetDT = new javax.swing.JButton();
        jTextFieldPetDT = new ObservingTextField();
        jScrollPaneAnimals = new javax.swing.JScrollPane();
        jTableAnimals = new javax.swing.JTable();
        jButtonAnimalAdd = new javax.swing.JButton();
        jButtonAnimalDel = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
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
        jLabel21.setText("Фамилия:");
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
        jLabel29.setText("Питомцы:");
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

        jTextFieldAddress.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldAddress.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldEmail.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldEmail.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldEmail.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldNotes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldNotes.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldNotes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTextFieldSumma.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldSumma.setForeground(new java.awt.Color(102, 102, 102));
        jTextFieldSumma.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldSumma.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jComboBox1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBox1.setBorder(null);
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

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

        jPanelAnimalAdd.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Добавление питомца:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N

        jLabel37.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel37.setText("Вид:");
        jLabel37.setFocusable(false);
        jLabel37.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel37.setRequestFocusEnabled(false);

        jComboBox2.setEditable(true);
        jComboBox2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBox2.setToolTipText("");
        jComboBox2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel38.setText("Порода:");
        jLabel38.setFocusable(false);
        jLabel38.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel38.setRequestFocusEnabled(false);

        jComboBox3.setEditable(true);
        jComboBox3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBox3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonAnimalSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Save-icon.png"))); // NOI18N
        jButtonAnimalSave.setToolTipText("Сохранить");
        jButtonAnimalSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnimalSaveActionPerformed(evt);
            }
        });

        jButtonAnimalCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/exit16x16.png"))); // NOI18N
        jButtonAnimalCancel.setToolTipText("Сохранить");
        jButtonAnimalCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnimalCancelActionPerformed(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel39.setText("Кличка:");
        jLabel39.setFocusable(false);
        jLabel39.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel39.setRequestFocusEnabled(false);

        jLabel41.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel41.setText("День. рожд.:");
        jLabel41.setFocusable(false);
        jLabel41.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel41.setRequestFocusEnabled(false);

        jTextFieldPetName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldPetName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButtonPetDT.setText("...");
        jButtonPetDT.setToolTipText("выбор даты");
        jButtonPetDT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPetDTActionPerformed(evt);
            }
        });

        jTextFieldPetDT.setEditable(false);
        jTextFieldPetDT.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jTextFieldPetDT.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPetDT.setToolTipText("начальная дата");
        jTextFieldPetDT.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextFieldPetDT.setFocusable(false);

        javax.swing.GroupLayout jPanelAnimalAddLayout = new javax.swing.GroupLayout(jPanelAnimalAdd);
        jPanelAnimalAdd.setLayout(jPanelAnimalAddLayout);
        jPanelAnimalAddLayout.setHorizontalGroup(
            jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAnimalAddLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelAnimalAddLayout.createSequentialGroup()
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelAnimalAddLayout.createSequentialGroup()
                        .addComponent(jTextFieldPetName, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldPetDT, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addGroup(jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelAnimalAddLayout.createSequentialGroup()
                        .addComponent(jButtonAnimalSave, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAnimalCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonPetDT, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelAnimalAddLayout.setVerticalGroup(
            jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelAnimalAddLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAnimalCancel)
                    .addComponent(jButtonAnimalSave)
                    .addGroup(jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7)
                .addGroup(jPanelAnimalAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldPetName, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPetDT)
                    .addComponent(jTextFieldPetDT, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4))
        );

        jScrollPaneAnimals.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jTableAnimals.setAutoCreateRowSorter(true);
        jTableAnimals.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTableAnimals.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableAnimals.setToolTipText("");
        jScrollPaneAnimals.setViewportView(jTableAnimals);

        jButtonAnimalAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Add-icon.png"))); // NOI18N
        jButtonAnimalAdd.setText("Добавить");
        jButtonAnimalAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnimalAddActionPerformed(evt);
            }
        });

        jButtonAnimalDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/Close-icon.png"))); // NOI18N
        jButtonAnimalDel.setText("Удалить");
        jButtonAnimalDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAnimalDelActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("Узнали о нас:");
        jLabel42.setFocusable(false);
        jLabel42.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabel42.setRequestFocusEnabled(false);

        jComboBox4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBox4.setMaximumRowCount(10);
        jComboBox4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(452, 452, 452))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel25t, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26t, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelAnimalAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPaneAnimals, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButtonAnimalAdd)
                                    .addComponent(jButtonAnimalDel, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jTextFieldEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(72, 72, 72)
                                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldSumma, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jFormattedTextFieldPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jFormattedTextFieldPhone2))
                            .addComponent(jTextFieldNotes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                            .addComponent(jTextFieldFamily, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                            .addComponent(jTextFieldName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldMiddleName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldAddress, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextFieldAddress, jTextFieldEmail, jTextFieldFamily, jTextFieldNotes});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel21, jLabel22, jLabel23, jLabel24, jLabel29, jLabel30});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFamily, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldMiddleName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextFieldPhone1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFormattedTextFieldPhone2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButtonAnimalAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonAnimalDel))
                    .addComponent(jScrollPaneAnimals, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelAnimalAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26t, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25t, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldSumma, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel21, jLabel22, jLabel23, jLabel24, jLabel29});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jFormattedTextFieldPhone1, jFormattedTextFieldPhone2, jLabel36});

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
                .addGap(18, 18, 18)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(jButtonExit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
		jComboBox3.setEditable(false);
		jComboBox3.removeAllItems();
		getListBreeds();
		jComboBox3.setEditable(true);
		//new AutoCompletion(jComboBox3);
    }//GEN-LAST:event_jComboBox2ActionPerformed
    private void jButtonAnimalAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnimalAddActionPerformed
        jButtonAnimalAddActionPerformed();
    }//GEN-LAST:event_jButtonAnimalAddActionPerformed
    private void jButtonAnimalDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnimalDelActionPerformed
        jButtonAnimalDelActionPerformed();
    }//GEN-LAST:event_jButtonAnimalDelActionPerformed
    private void jButtonAnimalSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnimalSaveActionPerformed
        jButtonAnimalSaveActionPerformed();
    }//GEN-LAST:event_jButtonAnimalSaveActionPerformed
    private void jButtonAnimalCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAnimalCancelActionPerformed
        jButtonAnimalCancelActionPerformed();
    }//GEN-LAST:event_jButtonAnimalCancelActionPerformed

    private void jButtonPetDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPetDTActionPerformed
        jButtonPetDTActionPerformed();
    }//GEN-LAST:event_jButtonPetDTActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAnimalAdd;
    private javax.swing.JButton jButtonAnimalCancel;
    private javax.swing.JButton jButtonAnimalDel;
    private javax.swing.JButton jButtonAnimalSave;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPetDT;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JFormattedTextField jFormattedTextFieldPhone1;
    private javax.swing.JFormattedTextField jFormattedTextFieldPhone2;
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
    public javax.swing.JLabel jLabel34;
    public javax.swing.JLabel jLabel35;
    public javax.swing.JLabel jLabel36;
    public javax.swing.JLabel jLabel37;
    public javax.swing.JLabel jLabel38;
    public javax.swing.JLabel jLabel39;
    public javax.swing.JLabel jLabel40;
    public javax.swing.JLabel jLabel41;
    public javax.swing.JLabel jLabel42;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelAnimalAdd;
    private javax.swing.JScrollPane jScrollPaneAnimals;
    private javax.swing.JTable jTableAnimals;
    public javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField31;
    private javax.swing.JTextField jTextField32;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldFamily;
    private javax.swing.JTextField jTextFieldMiddleName;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldNotes;
    private javax.swing.JTextField jTextFieldPetDT;
    private javax.swing.JTextField jTextFieldPetName;
    private javax.swing.JTextField jTextFieldSumma;
    // End of variables declaration//GEN-END:variables
}
