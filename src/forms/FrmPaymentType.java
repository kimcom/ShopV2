package forms;

import db.ConnectionDb;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import main.ConfigReader;

public class FrmPaymentType extends javax.swing.JDialog {
	private final ConfigReader conf;
	private final ConnectionDb cnn;
	public int typePayment = -1;

	public FrmPaymentType() {
		initComponents();
		cnn = ConnectionDb.getInstance();
		conf = ConfigReader.getInstance();
		if (cnn == null) {
			return;
		}
		setTitle("Тип оплаты. " + conf.FORM_TITLE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
		setLocationRelativeTo(null);
		
		jComboBoxQty.addItem("1 платеж");
		jComboBoxQty.addItem("2 платежа");
		jComboBoxQty.addItem("3 платежа");
		jComboBoxQty.addItem("4 платежа");
		jComboBoxQty.addItem("5 платежей");
		jComboBoxQty.addItem("6 платежей");
		
		if (cnn.checkTypePayment==0) {
			buttonGroup1.setSelected(jRadioButton1.getModel(), true);
			jRadioButton1.requestFocus();
		}
		if (cnn.checkTypePayment==1) {
			buttonGroup1.setSelected(jRadioButton2.getModel(), true);
			jRadioButton2.requestFocus();
		}
		if (Integer.toString(cnn.checkTypePayment).startsWith("2")) {
			buttonGroup1.setSelected(jRadioButton3.getModel(), true);
			System.out.println(Integer.toString(cnn.checkTypePayment).substring(1));
			if (Integer.toString(cnn.checkTypePayment).substring(1).equals("1")) jComboBoxQty.setSelectedIndex(0);
			if (Integer.toString(cnn.checkTypePayment).substring(1).equals("2")) jComboBoxQty.setSelectedIndex(1);
			if (Integer.toString(cnn.checkTypePayment).substring(1).equals("3")) jComboBoxQty.setSelectedIndex(2);
			if (Integer.toString(cnn.checkTypePayment).substring(1).equals("4")) jComboBoxQty.setSelectedIndex(3);
			if (Integer.toString(cnn.checkTypePayment).substring(1).equals("5")) jComboBoxQty.setSelectedIndex(4);
			if (Integer.toString(cnn.checkTypePayment).substring(1).equals("6")) jComboBoxQty.setSelectedIndex(5);
			jRadioButton3.requestFocus();
		}
		
		jComboBoxQtyVisible(false);
		//назначение MyKeyListener
		getAllComponents((Container) this.getContentPane());
		//jButtonOK.setEnabled(false);
	}
	private void jComboBoxQtyVisible(boolean visibleFlag){
		jLabelQtyPay.setVisible(visibleFlag);
		jComboBoxQty.setVisible(visibleFlag);
		pack();
	}
	private void jButtonOKActionPerformed() {
		//System.out.println(buttonGroup1.getSelection());
		if (jRadioButton1.isSelected()) {
			typePayment = 0;
		}else if (jRadioButton2.isSelected()) {
			typePayment = 1;
		}else if (jRadioButton3.isSelected()) {
			if (jComboBoxQty.getSelectedIndex()==0) typePayment = 21;
			if (jComboBoxQty.getSelectedIndex()==1) typePayment = 22;
			if (jComboBoxQty.getSelectedIndex()==2) typePayment = 23;
			if (jComboBoxQty.getSelectedIndex()==3) typePayment = 24;
			if (jComboBoxQty.getSelectedIndex()==4) typePayment = 25;
			if (jComboBoxQty.getSelectedIndex()==5) typePayment = 26;
		}
		//System.out.println(typePayment);
		dispose();
	}
	private void jButtonExitActionPerformed() {
		typePayment = -1;
		dispose();
	}
	private void jRadioButton1FocusGained(){
		buttonGroup1.setSelected(jRadioButton1.getModel(), true);
		jComboBoxQtyVisible(false);
	}
	private void jRadioButton2FocusGained(){
		buttonGroup1.setSelected(jRadioButton2.getModel(), true);
		jComboBoxQtyVisible(false);
	}
	private void jRadioButton3FocusGained(){
		buttonGroup1.setSelected(jRadioButton3.getModel(), true);
		jComboBoxQtyVisible(true);
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
			switch (keyCode) {
				case KeyEvent.VK_ENTER:    // штрих-код
					if (e.getModifiers() != 0) {
						break;
					}
					//System.out.println(objCanonicalName);
					if (objCanonicalName.startsWith("javax.swing.JRadioButton")) {
						jButtonOK.requestFocus();
					}
					if (objCanonicalName.startsWith("javax.swing.JComboBox")) {
						jButtonOK.requestFocus();
					}
					if (e.getSource() == jButtonOK) {
						jButtonOKActionPerformed();
					}
					if (e.getSource() == jButtonExit) {
						jButtonExitActionPerformed();
					}
					break;
				case KeyEvent.VK_UP:
					if (e.getModifiers() != 0) {
						break;
					}
					if (objCanonicalName.startsWith("javax.swing.JRadioButton")) {
						JRadioButton tf = (JRadioButton) e.getSource();
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
					if (objCanonicalName.startsWith("javax.swing.JRadioButton")) {
						JRadioButton tf = (JRadioButton) e.getSource();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanelGroupTypePayment = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabelQtyPay = new javax.swing.JLabel();
        jComboBoxQty = new javax.swing.JComboBox();
        jPanelButtons = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jButtonExit = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelGroupTypePayment.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Выберите тип оплаты:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 18), new java.awt.Color(0, 102, 0))); // NOI18N
        jPanelGroupTypePayment.setToolTipText("");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton1.setForeground(new java.awt.Color(0, 102, 102));
        jRadioButton1.setText("наличный расчет");
        jRadioButton1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton1FocusGained(evt);
            }
        });
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton2.setForeground(new java.awt.Color(0, 102, 102));
        jRadioButton2.setText("безналичный расчет");
        jRadioButton2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton2FocusGained(evt);
            }
        });
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jRadioButton3.setForeground(new java.awt.Color(0, 102, 102));
        jRadioButton3.setText("оплата частями");
        jRadioButton3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jRadioButton3FocusGained(evt);
            }
        });
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        jLabelQtyPay.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabelQtyPay.setForeground(new java.awt.Color(0, 0, 102));
        jLabelQtyPay.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelQtyPay.setText("Кол-во платежей:");
        jLabelQtyPay.setAutoscrolls(true);
        jLabelQtyPay.setFocusable(false);
        jLabelQtyPay.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabelQtyPay.setRequestFocusEnabled(false);
        jLabelQtyPay.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jComboBoxQty.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxQty.setBorder(null);
        jComboBoxQty.setMinimumSize(new java.awt.Dimension(240, 26));
        jComboBoxQty.setPreferredSize(new java.awt.Dimension(240, 26));
        jComboBoxQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxQtyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGroupTypePaymentLayout = new javax.swing.GroupLayout(jPanelGroupTypePayment);
        jPanelGroupTypePayment.setLayout(jPanelGroupTypePaymentLayout);
        jPanelGroupTypePaymentLayout.setHorizontalGroup(
            jPanelGroupTypePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGroupTypePaymentLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanelGroupTypePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanelGroupTypePaymentLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanelGroupTypePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabelQtyPay, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                            .addComponent(jComboBoxQty, 0, 0, Short.MAX_VALUE)))
                    .addComponent(jRadioButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(jRadioButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelGroupTypePaymentLayout.setVerticalGroup(
            jPanelGroupTypePaymentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGroupTypePaymentLayout.createSequentialGroup()
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelQtyPay, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jComboBoxQty, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jRadioButton1.getAccessibleContext().setAccessibleName("jRadioButton1");
        jRadioButton2.getAccessibleContext().setAccessibleName("jRadioButton2");
        jRadioButton3.getAccessibleContext().setAccessibleName("jRadioButton3");

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

        javax.swing.GroupLayout jPanelButtonsLayout = new javax.swing.GroupLayout(jPanelButtons);
        jPanelButtons.setLayout(jPanelButtonsLayout);
        jPanelButtonsLayout.setHorizontalGroup(
            jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelButtonsLayout.setVerticalGroup(
            jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelButtonsLayout.createSequentialGroup()
                .addGroup(jPanelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonExit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonOK, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 2, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelGroupTypePayment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelGroupTypePayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        //jRadioButtonActionPerformed();
    }//GEN-LAST:event_jRadioButton1ActionPerformed
    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        //jRadioButtonActionPerformed();
    }//GEN-LAST:event_jRadioButton2ActionPerformed
    private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        //jRadioButtonActionPerformed();
    }//GEN-LAST:event_jRadioButton3ActionPerformed
    private void jComboBoxQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxQtyActionPerformed
        if (evt.getModifiers() != 0) {
            //jComboBoxStickerTypeActionPerformed();
        }
    }//GEN-LAST:event_jComboBoxQtyActionPerformed
    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        jButtonOKActionPerformed();
    }//GEN-LAST:event_jButtonOKActionPerformed
    private void jButtonExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExitActionPerformed
        jButtonExitActionPerformed();
    }//GEN-LAST:event_jButtonExitActionPerformed
    private void jRadioButton1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRadioButton1FocusGained
        jRadioButton1FocusGained();
    }//GEN-LAST:event_jRadioButton1FocusGained
    private void jRadioButton2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRadioButton2FocusGained
		jRadioButton2FocusGained();
    }//GEN-LAST:event_jRadioButton2FocusGained
    private void jRadioButton3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jRadioButton3FocusGained
		jRadioButton3FocusGained();
    }//GEN-LAST:event_jRadioButton3FocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonExit;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox jComboBoxQty;
    public javax.swing.JLabel jLabelQtyPay;
    private javax.swing.JPanel jPanelButtons;
    private javax.swing.JPanel jPanelGroupTypePayment;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    // End of variables declaration//GEN-END:variables
}
