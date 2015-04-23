package main;

import forms.FrmMain;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class DialogBoxs {
    public static void viewError(Exception e) {
        JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
    }

    public static void viewMessage(String msg) {
        JOptionPane.showMessageDialog(new JFrame(), msg, "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void viewMessageWarning(String msg) {
        JOptionPane.showMessageDialog(new JFrame(), msg, "ВНИМАНИЕ!", JOptionPane.WARNING_MESSAGE);
    }
    
    public static void viewMessageError(String msg) {
        JOptionPane.showMessageDialog(new JFrame(), msg, "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showOptionDialog(String[] args) {
        Object[] options = {"Да", "Нет!"};
        JFrame jf = new JFrame();
        int n = JOptionPane
                .showOptionDialog(jf, "Закрыть окно?",
                        "Подтверждение", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0]);
        if (n == 0) {
            jf.setVisible(false);
        }
    }

	private class MyKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			String objCanonicalName = e.getSource().getClass().getCanonicalName();
			//System.out.println("objCanonicalName:"+objCanonicalName);
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (objCanonicalName.endsWith("Field")) {
					Component tf = (Component) e.getSource();
					tf.transferFocus();
				};
				if (objCanonicalName.endsWith("Button")) {
					JButton jb = (JButton) e.getSource();
					jb.doClick();
				}
			}
			super.keyPressed(e); //To change body of generated methods, choose Tools | Templates.
		}
	}
	private List<Component> getAllComponents(final Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
            String canonicalName = comp.getClass().getCanonicalName();
			if (comp.isDisplayable()) {
//				System.out.println("1. addKeyListener: " + comp.getName() + "  " + canonicalName + "    display=" + comp.isDisplayable());
				comp.addKeyListener(new MyKeyListener());
			}
			if (comp.isFocusable()) {
//				System.out.println("2. addKeyListener: " + comp.getName() + "  " + canonicalName + "    focus=" + comp.isFocusable());
				comp.addKeyListener(new MyKeyListener());
				//comp.addFocusListener(new MyFocusListener());
			}
			compList.add(comp);
			if (comp instanceof Container) {
				compList.addAll(getAllComponents((Container) comp));
			}
		}
		return compList;
	}
	
	public String showOptionDialogGetCheckID(String title, String textMessage, Icon icon) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel(textMessage);
		JFormattedTextField jFormattedTextField3 = new JFormattedTextField();
		jFormattedTextField3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
		jFormattedTextField3.setForeground(new java.awt.Color(0, 0, 204));
		jFormattedTextField3.setPreferredSize(new Dimension(96, 22));
		jFormattedTextField3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
		jFormattedTextField3.setAutoscrolls(false);
		jFormattedTextField3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
		jFormattedTextField3.addFocusListener(new MyUtil.MyFormatedTextFocusListener());

		jFormattedTextField3.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#0"))));
		if (jFormattedTextField3.getText().equals("")) jFormattedTextField3.setText("0");
		//jFormattedTextField3.setText(jFormattedTextField3.getText().replace(".", ","));
		try {
			jFormattedTextField3.commitEdit();
		} catch (ParseException ex) {
			DialogBoxs.viewError(ex);
		}
		panel.add(label);
		panel.add(jFormattedTextField3);
		String[] options = new String[]{"Ввод", "Отмена"};
		JOptionPane jop = new JOptionPane(panel, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, title);
        getAllComponents((Container) jop);
		JDialog dialog = jop.createDialog(null, title);
		dialog.setVisible(true);
		Object opt = jop.getValue();
		if (opt.toString() == "Ввод") // pressing OK button
		{
			return jFormattedTextField3.getValue().toString();
		} else {
			return "0";
		}
	}
}
