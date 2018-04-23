package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JTextField;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;

public class FrmCardKlass extends javax.swing.JDialog {
	private final ConfigReader conf;
	private final ConnectionDb cnn;
	private static String barCode = "";
	private long timeBarCode = 0;
	private boolean blDiscountCardFuture = false;
	public boolean blDisposeStatus = false;
	public String strBarCode;
	private int iStatus;

	public FrmCardKlass(String _checkCardKlassID) {
		//initComponents(_checkCardKlassID);
		initComponents();
		conf = ConfigReader.getInstance();
		cnn = ConnectionDb.getInstance();
		setTitle("Карта супермаркета 'Класс'. " + conf.FORM_TITLE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
		strBarCode = _checkCardKlassID;
		//jTextField1.setText(strBarCode);
		jTextField1.addFocusListener(new MyUtil.MyTextFocusListener());
		jTextField1.setText("Просканируйте карту супермаркета");
		jTextField33.setText(cnn.currentCheckID.setScale(4, RoundingMode.HALF_UP).toPlainString());
		jTextField34.setText(cnn.checkSum.setScale(2, RoundingMode.HALF_UP).toPlainString());
		jButtonOK.setEnabled(false);
		pack();
		setLocationRelativeTo(null);
		//назначение MyKeyListener
		getAllComponents((Container) this.getContentPane());
//jTextField1.setText("ВФ475461");
//barCode = jTextField1.getText();
//requery();
	}
	private void jButtonOKActionPerformed() {
		blDisposeStatus = true;
		strBarCode = jTextField1.getText();
		dispose();
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
//					if (iStatus == 2) {
//						barCode = jTextField1.getText();
//					}
					if (objCanonicalName.endsWith("Field")) {
						if (!barCode.equals("")) {
							JTextField tf = (JTextField) e.getSource();
							tf.transferFocus();
						}
					}
					if (e.getSource() == jTextField1) {
						//barCode = "200001";
						if (!barCode.equals("") && blDiscountCardFuture == false) {
							blDiscountCardFuture = true;
							//DialogBoxs.viewMessage("Карта КЛАССА: "+barCode);
							jTextField1.setText(barCode);
							jButtonOK.setEnabled(true);
							jButtonOK.requestFocus();
							barCode = "";
							jButtonOKActionPerformed();
							break;
						}
					}
					if (e.getSource() == jButtonOK) {
						jButtonOKActionPerformed();
						break;
					}
					if (e.getSource() == jButtonExit) {
						jButtonExitActionPerformed();
						break;
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
					if (blDiscountCardFuture) {
						break;
					}
//					if ((keyCode > 47 && keyCode < 58) || (keyCode > 95 && keyCode < 106)) {
//						if (iStatus != 2) {
//							try {
//								long newtimeBarCode = new Date().getTime();
//								if (timeBarCode + 100 < newtimeBarCode) {
//									barCode = "";
//								}
//								timeBarCode = new Date().getTime();
//							} catch (NoSuchMethodError ex) {
//								DialogBoxs.viewMessage(ex.getMessage());
//							}
//						}
//						barCode = barCode.concat(Character.toString(e.getKeyChar()));
//					}
					if ((keyCode > 47 && keyCode < 58) || (keyCode > 95 && keyCode < 106) || (keyCode >= 65 && keyCode <= 90)) {
						barCode = barCode.concat(Character.toString(e.getKeyChar()));
						//System.out.println("barCode:"+barCode);
						jTextField1.setText(barCode);
						break;
					}
			}
			return e;
		}
	}
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jTextField33 = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jTextField34 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Идентификатор карты:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(316, 51));

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 0, 204));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setAutoscrolls(false);
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField1.setPreferredSize(new java.awt.Dimension(78, 22));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Информация о чеке и сумме:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 12))); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(316, 110));

        jLabel33.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel33.setText("Номер чека:");
        jLabel33.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField33.setEditable(false);
        jTextField33.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField33.setForeground(new java.awt.Color(0, 0, 204));
        jTextField33.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField33.setAutoscrolls(false);
        jTextField33.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField33.setFocusable(false);
        jTextField33.setPreferredSize(new java.awt.Dimension(78, 22));

        jLabel34.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel34.setText("Итого сумма:");
        jLabel34.setPreferredSize(new java.awt.Dimension(41, 17));

        jTextField34.setEditable(false);
        jTextField34.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextField34.setForeground(new java.awt.Color(0, 0, 204));
        jTextField34.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField34.setAutoscrolls(false);
        jTextField34.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTextField34.setFocusable(false);
        jTextField34.setPreferredSize(new java.awt.Dimension(78, 22));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
		jButtonOKActionPerformed();
    }//GEN-LAST:event_jButtonOKActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField33;
    private javax.swing.JTextField jTextField34;
    // End of variables declaration//GEN-END:variables
}
