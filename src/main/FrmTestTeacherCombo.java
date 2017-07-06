package main;

import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FrmTestTeacherCombo extends JFrame{

	public FrmTestTeacherCombo() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.setTitle("teacher combobox with ID");
		this.getContentPane().setLayout(new FlowLayout());
		try {
			Connection conn = null;
			conn = ConnectionManager.getConnection();
			//conn = Connector.getConnection();

			ArrayList<TeacherItem> teacherItems = new ArrayList<TeacherItem>();
			JComboBox<TeacherItem> teacherCombo = new JComboBox<TeacherItem>();
			PreparedStatement stmt9 = conn.prepareStatement("SELECT * FROM Teachers");
			ResultSet rs6 = stmt9.executeQuery();
			teacherItems.clear();
			while (rs6.next()) {
				teacherItems.add(new TeacherItem(rs6.getString(TeacherItem.TEACHER_ID), rs6.getString(TeacherItem.TEACHER_FIRST_NAME), rs6.getString(TeacherItem.TEACHER_LAST_NAME)));
			}
			teacherCombo.setModel(new DefaultComboBoxModel<TeacherItem>(teacherItems.toArray(new TeacherItem[0])));

			getContentPane().add(teacherCombo);

			teacherCombo.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() != ItemEvent.SELECTED) {
						return;
					}
					TeacherItem ti = (TeacherItem) e.getItem();
					System.out.println(ti.id);
					JOptionPane.showMessageDialog(null, "Teacher ID: " + ti.id + "\nFirst name: " + ti.first_name + "\nLast name: " + ti.last_name, "RESULT!", JOptionPane.INFORMATION_MESSAGE);
				}
			});

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
		} catch (SQLException ex) {
			Logger.getLogger(this.getName()).log(Level.SEVERE, null, ex);
		}

		this.setSize(200, 200);
		this.setLocationRelativeTo(null);
		//pack();
		this.setVisible(true);
	}

	private class TeacherItem {

		public static final String TEACHER_ID = "ID";
		public static final String TEACHER_FIRST_NAME = "First_Name";
		public static final String TEACHER_LAST_NAME = "Last_Name";
		private String id;
		private String first_name;
		private String last_name;
		// Конструктор с параметрами

		public TeacherItem(String id, String first_name, String last_name) {
			this.id = id;
			this.first_name = first_name;
			this.last_name = last_name;
		}

		@Override
		public String toString() {
			return first_name + " " + last_name;
		}
	}
	
	public static class ConnectionManager {

		public static Connection getConnection() throws ClassNotFoundException, SQLException {
			Class.forName("com.mysql.jdbc.Driver");
//			String DB_URL = "jdbc:mysql://173.212.192.223:3306/studentdb?useUnicode=true&characterEncoding=utf-8";
//			String USER = "studentdb";
//			String PASS = "3W7b5N9d";
        String DB_URL = "jdbc:mysql://localhost:3306/studentdb?useUnicode=true&characterEncoding=utf-8";
		String USER = "root";
		String PASS = "sasasa";
			return DriverManager.getConnection(DB_URL, USER, PASS);
		}
	}
}
