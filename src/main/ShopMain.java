package main;

import db.ConnectionDb;
import forms.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.UnsupportedLookAndFeelException;
import reports.ReportMarkup;
import reports.ReportPricePlank;
import reports.ReportPriceSticker;

public class ShopMain {

	public static void main(final String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
				try {
					for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							javax.swing.UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (ClassCastException | IndexOutOfBoundsException | NullPointerException | IllegalArgumentException | ArithmeticException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
					//java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
					MyUtil.errorToLog(FrmMain.class.getName(), ex);
				}
                final ConfigReader config = ConfigReader.getInstance();
//				MyEKKA me = new MyEKKA();
//				//me.report("z1");
//				me.printCheck(new BigDecimal(12961.2009));

//				ConnectionDb cnn1 = ConnectionDb.getInstance();
//				String password1 = new StringBuffer(config.USER_NAME).reverse().toString();
//				cnn1.login(config.USER_NAME, password1);
//
////				FrmAdmin frmAdmin = new FrmAdmin();
////				frmAdmin.setModal(true);
////				frmAdmin.setVisible(true);
//				ReportMarkup reportMarkup = new ReportMarkup();
//				reportMarkup.setModal(true);
//				reportMarkup.setVisible(true);
//				if (1 == 1) System.exit(0);
				
//				ReportPriceSticker reportPrice = new ReportPriceSticker(new BigDecimal("6.1453"));
//				reportPrice.setModal(true);
//				reportPrice.setVisible(true);

				//ReportPricePlank reportPrice = new ReportPricePlank(new BigDecimal("17.5411"),3);
				//Алексей администратор акв. отдел
//				ReportPricePlank reportPrice = new ReportPricePlank(new BigDecimal("39.1453"),3);
//				reportPrice.setModal(true);
//				reportPrice.setVisible(true);

//				final FrmStickerEdit frmStickerEdit = new FrmStickerEdit(new BigDecimal(40.1453));
//				//final FrmStickerEdit frmStickerEdit = new FrmStickerEdit(new BigDecimal(17.5411));
//				frmStickerEdit.setModal(true);
//				frmStickerEdit.setVisible(true);

//				final FrmCardAttribute frmCardAttribute = new FrmCardAttribute(1); //выдача новой
//				frmCardAttribute.setModal(true);
//				frmCardAttribute.setVisible(true);

//				final FrmCardAttribute frmCardAttribute = new FrmCardAttribute(2); //ввод анкеты
//				frmCardAttribute.setModal(true);
//				frmCardAttribute.setVisible(true);
				
				//if (1==1) System.exit(0);
				
                if (config == null) System.exit(0);
                ConnectionDb cnn = ConnectionDb.getInstance();
                if (cnn == null) System.exit(0);
                
				if(!config.USER_NAME.equals("")){
                    String password = new StringBuffer(config.USER_NAME).reverse().toString();
                    boolean loginStatus = cnn.login(config.USER_NAME, password);
//                    if(loginStatus){
//                        //final FrmOrderList frmOrderList = FrmOrderList.getInstance(new JFrame(),0);
//                        //final FrmOrderEdit frmOrderEdit = FrmOrderEdit.getInstance(new JFrame(),new BigDecimal(2.1453));
//						FrmCashMove frmCashMove = new FrmCashMove();
//						frmCashMove.setModal(true);
//						frmCashMove.setVisible(true);
//						cnn.destroy();
//						System.exit(0);
//                    }else 
                    if(loginStatus) {
//						UpdaterShopV1 updaterShopV1 = new UpdaterShopV1();
//						if (1 == 1) {
//							return;
//						}
                        final FrmMain frmMain = FrmMain.getInstance();

						TimerTask timerTask = new MyTimerTask(frmMain,"linkStatusTask");
						//running timer task as daemon thread
						Timer timer = new Timer(true);
						timer.scheduleAtFixedRate(timerTask, 0, config.TIME_WAIT * 1000);

						TimerTask timerTask2 = new MyTimerTask(frmMain, "updateAppTask");
						//running timer task as daemon thread
						Timer timer2 = new Timer(true);
						timer2.scheduleAtFixedRate(timerTask2, config.TIME_UPDATE_START * 1000, config.TIME_UPDATE * 1000);

						TimerTask timerTask3 = new MyTimerTask(frmMain, "updaterShopV1");
						//running timer task as daemon thread
						Timer timer3 = new Timer(true);
						timer3.schedule(timerTask3, config.TIME_WAIT * 1000); //выполняем один раз

						TimerTask timerTask4 = new MyTimerTask(frmMain, "linkStatusServer");
						//running timer task as daemon thread
						Timer timer4 = new Timer(true);
						timer4.scheduleAtFixedRate(timerTask4, 0, config.TIME_WAIT * 1000 * 60);
						
//						final FrmSearch frmSearch = FrmSearch.getInstance();
//                      final FrmDiscount frmDiscount = FrmDiscount.getInstance();
                    }else{
						cnn.destroy();
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
