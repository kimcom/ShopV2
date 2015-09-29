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
		} else if (taskName.equals("linkStatusServer")){
			linkStatusServerTask();
		} else if (taskName.equals("updateAppTask")) {
			Updater updater = new Updater();
		} else if (taskName.equals("updaterShopV1")) {
			UpdaterShopV1 updaterShopV1 = new UpdaterShopV1();
		}
	}
	
	private void linkStatusTask() {
		parentJFrame.setCnnStatus(1);
		if (cnn != null){
//			System.out.println("cnn проверяем! statusValid()="+cnn.statusValid());
			parentJFrame.setCnnStatus(1);
			if (!cnn.statusValid()){
//				System.out.println("cnn is clossed!");
				parentJFrame.setCnnStatus(2);
				cnn.close();
				cnn.startConnect();
				if (cnn != null) {
					if(!cnn.statusClosed()){
						parentJFrame.setCnnStatus(0);
					}
				} else {
//					System.out.println("1. cnn не поднимается!");
				}
			} else {
//				System.out.println("	коннект в порядке");
				parentJFrame.setCnnStatus(0);
			}
		} else {
//			System.out.println("2. cnn не поднимается!");
			parentJFrame.setCnnStatus(2);
		}
	}
	private void linkStatusServerTask() {
		parentJFrame.setCnnStatus(1);
		if (cnn != null){
//			System.out.println("cnn проверяем! statusValid()="+cnn.statusValid());
			parentJFrame.setCnnStatus(1);
			
			if (!cnn.statusValid() || cnn.serverID == 2){
//				System.out.println("cnn is clossed!");
				parentJFrame.setCnnStatus(2);
				cnn.close();
				cnn.startConnect();
				if (cnn != null) {
					if(!cnn.statusClosed()){
						parentJFrame.setCnnStatus(0);
					}
				} else {
//					System.out.println("1. cnn не поднимается!");
				}
			} else {
//				System.out.println("	коннект в порядке");
				parentJFrame.setCnnStatus(0);
			}
		} else {
//			System.out.println("2. cnn не поднимается!");
			parentJFrame.setCnnStatus(2);
		}
	}
}