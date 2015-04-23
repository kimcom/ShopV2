package main;

import db.ConnectionDb;
import forms.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

public class ShopMain {

	public static void main(final String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final ConfigReader config = ConfigReader.getInstance();
//				MyEKKA me = new MyEKKA();
//				//me.report("z1");
//				me.printCheck(new BigDecimal(12961.2009));
//				if (1==1) {
//					System.exit(0);
//				}
                if (config == null) System.exit(0);
                ConnectionDb cnn = ConnectionDb.getInstance();
                if (cnn == null) System.exit(0);
                
                if(!config.USER_NAME.equals("")){
                    String password = new StringBuffer(config.USER_NAME).reverse().toString();
                    boolean loginStatus = cnn.login(config.USER_NAME, password);
//                    if(loginStatus){
//                        //final FrmOrderList frmOrderList = FrmOrderList.getInstance(new JFrame(),0);
//                        final FrmOrderEdit frmOrderEdit = FrmOrderEdit.getInstance(new JFrame(),new BigDecimal(2.1453));
//                    }else 
                    if(loginStatus) {
                        final FrmMain frmMain = FrmMain.getInstance();

						TimerTask timerTask = new MyTimerTask(frmMain,"linkStatusTask");
						//running timer task as daemon thread
						Timer timer = new Timer(true);
						timer.scheduleAtFixedRate(timerTask, 0, config.TIME_WAIT * 1000);

						TimerTask timerTask2 = new MyTimerTask(frmMain, "updateAppTask");
						//running timer task as daemon thread
						Timer timer2 = new Timer(true);
						timer2.scheduleAtFixedRate(timerTask2, config.TIME_UPDATE_START*1000, config.TIME_UPDATE * 1000);

//						final FrmSearch frmSearch = FrmSearch.getInstance();
//                      final FrmDiscount frmDiscount = FrmDiscount.getInstance();
                    }else{
                        System.exit(0);
                    }
                }else{
                    //final FrmLoginTest frmLogin = FrmLoginTest.getInstance();
                    final FrmLogin frmLogin = FrmLogin.getInstance();
                    frmLogin.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            //System.out.println("login status= " + frmLogin.loginStatus);
                            if (frmLogin.loginStatus) {
                                final FrmMain frmMain = FrmMain.getInstance();
								TimerTask timerTask = new MyTimerTask(frmMain,"linkStatusTask");
								//running timer task as daemon thread
								Timer timer = new Timer(true);
								timer.scheduleAtFixedRate(timerTask, 0, config.TIME_WAIT * 1000);

								TimerTask timerTask2 = new MyTimerTask(frmMain, "updateAppTask");
								//running timer task as daemon thread
								Timer timer2 = new Timer(true);
								timer2.scheduleAtFixedRate(timerTask2, config.TIME_UPDATE_START * 1000, config.TIME_UPDATE * 1000);
							}else{
                                ConnectionDb cnn = ConnectionDb.getInstance();
                                cnn.destroy();
                                System.exit(0);
                            }
                        }
                    });
                }
            }
        });
    }
}
