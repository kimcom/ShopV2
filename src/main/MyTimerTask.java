package main;

import db.ConnectionDb;
import forms.FrmMain;
import java.sql.SQLException;
import java.util.Date;
import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
	private FrmMain parentJFrame;
	private String taskName;
	private ConnectionDb cnn=null;

	public MyTimerTask(FrmMain parentJFrame, String taskName) {
		this.parentJFrame = parentJFrame;
		this.taskName = taskName;
		this.cnn = ConnectionDb.getInstanceSilent();
	}

	@Override
	public void run() {
		if (taskName.equals("linkStatusTask")){
			linkStatusTask();
		} else if (taskName.equals("updateAppTask")) {
			updateAppTask();
		}
	}
	
	private void updateAppTask() {
		new Updater();
//		Date dt = new Date();
//		System.out.println("updateAppTask" + dt.toString());
	}

	private void linkStatusTask() {
//		Date dt = new Date();
//		System.out.println("linkStatusTask" + dt.toString());
		parentJFrame.setCnnStatus(1);
		//ConnectionDb cnn = ConnectionDb.getInstanceSilent();
		if (cnn != null){
//			System.out.println("cnn проверяем! statusValid()="+cnn.statusValid());
			parentJFrame.setCnnStatus(1);
			if (!cnn.statusValid()){
//				System.out.println("cnn is clossed!");
				parentJFrame.setCnnStatus(2);
				//cnn.destroy();
				//cnn = ConnectionDb.getInstanceSilent();
				cnn.close();
				cnn.startConnect();
				if (cnn != null) {
//					System.out.println("statusValid()=" + cnn.statusValid());
//					System.out.println("проверяем closed");
					if(!cnn.statusClosed()){
//						System.out.println("cnn is open!");
//						if (cnn.currentCheckID == null) {
//							final ConfigReader config = ConfigReader.getInstance();
//							String password = new StringBuffer(config.USER_NAME).reverse().toString();
//							cnn.login(config.USER_NAME, password);
//						}
						parentJFrame.setCnnStatus(0);
					}
				} else {
//					System.out.println("1. cnn не поднимается!");
				}
			} else {
//				if (cnn.currentCheckID == null) {
//					final ConfigReader config = ConfigReader.getInstance();
//					String password = new StringBuffer(config.USER_NAME).reverse().toString();
//					cnn.login(config.USER_NAME, password);
//				}
//				System.out.println(""+Integer.toString(cnn.clientID));
//				System.out.println(""+cnn.currentCheckID.toString());
//				System.out.println("	коннект в порядке");
				parentJFrame.setCnnStatus(0);
			}
		} else {
//			System.out.println("2. cnn не поднимается!");
			parentJFrame.setCnnStatus(2);
		}
	}
}