package main;

import javax.swing.*;

public class DialogBoxs {
    public static void viewError(Exception e) {
        JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
    }

    public static void viewMessage(String msg) {
        JOptionPane.showMessageDialog(new JFrame(), msg, "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
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

}
