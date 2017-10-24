package main;

import db.ConnectionDb;
import forms.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.UnsupportedLookAndFeelException;
import reports.ReportPricePlankA4;

public class ShopMain {

	public static FrmMain frmMain = null;
	public static FrmAdmin frmAdmin = null;
	public static boolean startAdmin = false;
	
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
				if(args.length > 0){
					if (args[0].equals("admin")) startAdmin = true;
				}
				//JOptionPane.showMessageDialog(null, args.length);
				
				//AppActivate aa = new AppActivate();
				//AppActivate.getCurrentPid(args);
				//AppActivate.WinActivate("7284");
				//AppActivate.WinActivate("Калькулятор");
				//AppActivate.WinActivate("MasterZoo");
				//Date dt = new DateTime();
				//AppActivate.getProcessList();
				//AppActivate.getProcessList2();
				//if (1 == 1) System.exit(0);
				
                final ConfigReader config = ConfigReader.getInstance();
				if (config == null) System.exit(0);
				ConnectionDb cnn = ConnectionDb.getInstance();
				if (cnn == null) System.exit(0);

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
//System.out.println(config.EKKA_NAME);
//System.out.println(config.EKKA_HOST);
//				DialogBoxs db = new DialogBoxs();
//				String returnSum = db.showOptionDialogGetSum("Выдача денег из кассы регистратора", "<html>Введите сумму<br>выданных денег:</html>", new javax.swing.ImageIcon(getClass().getResource("/png/Cash-register-32.png")), new Color(255, 152, 0));
////				JFrameHelp j = new JFrameHelp();
//				j.setVisible(true);
/*
				String str = "TE7E121 S1HA0TEP00CT71631274/S1HA0TEP/I1HA0TEP/I1HA0TEP/IGHA0TEP/S1HA0TEP/IPHA0TEP/X1HA1TEP/X1HA2TEP/X1HAXTE/G1HA0TEP";
				String mer = "";
				int mid = 0;
				String[] s = str.split("/");
				mer = s[1].replace("S1", "X1").replace("0", "1");
				int i = 0;
				while (i<s.length){
					if (s[i].equals(mer)) mid = i;
					//System.out.println(s[i]);
					i++;
				}
				System.out.println(mid);
*/						
//				ReportPriceStickerClub reportPrice = new ReportPriceStickerClub(new BigDecimal("943.1453"),2,65);
//				ReportPricePlankClub reportPrice = new ReportPricePlankClub(new BigDecimal("943.1453"),2);
//				reportPrice.setModal(true);
//				reportPrice.setVisible(true);
//				if (1 == 1) System.exit(0);

//				ReportPriceSticker reportPrice = new ReportPriceSticker(new BigDecimal("6.1453"));
//				reportPrice.setModal(true);
//				reportPrice.setVisible(true);

				//ReportPricePlank reportPrice = new ReportPricePlank(new BigDecimal("17.5411"),3);
				//Алексей администратор акв. отдел
//				ReportPricePlank reportPrice = new ReportPricePlank(new BigDecimal("39.1453"),3);
//				reportPrice.setModal(true);
//				reportPrice.setVisible(true);

//				final FrmStickerEdit frmStickerEdit = new FrmStickerEdit(new BigDecimal(2482.1453));
//				//final FrmStickerEdit frmStickerEdit = new FrmStickerEdit(new BigDecimal(17.5411));
//				frmStickerEdit.setModal(true);
//				frmStickerEdit.setVisible(true);
//				System.exit(0);

//				final FrmCardAttribute frmCardAttribute = new FrmCardAttribute(1); //выдача новой
//				frmCardAttribute.setModal(true);
//				frmCardAttribute.setVisible(true);

//				String[] portNames = SerialPortList.getPortNames();
//				for (int i = 0; i < portNames.length; i++) {
//					System.out.println(portNames[i]);
//					//MyUtil.messageToLog("ports", portNames[i]);
//				}
//				if (1==1) return;
				
				if(!config.USER_NAME.equals("")){
                    String password = new StringBuffer(config.USER_NAME).reverse().toString();
                    boolean loginStatus = cnn.login(config.USER_NAME, password);
                    if(loginStatus) {

//						final FrmStickerList frm = new FrmStickerList();
//						frm.setModal(true);
//						frm.setVisible(true);
//						System.exit(0);
						
//						final FrmStickerEdit frmStickerEdit = new FrmStickerEdit(new BigDecimal("46.1000"));
//						frmStickerEdit.setModal(true);
//						frmStickerEdit.setVisible(true);
//						if (1 == 1) System.exit(0);

//						ReportPricePlankA4 reportPrice = new ReportPricePlankA4(new BigDecimal("46.1000"), 9);
//						reportPrice.setModal(true);
//						reportPrice.setVisible(true);
//						if (1 == 1) System.exit(0);


						Loader.checkFrmStart();
						if(startAdmin) {
							frmAdmin = FrmAdmin.getInstance();
//							FrmOffline frmOffline = new FrmOffline();
//							frmOffline.setModal(true);
//							frmOffline.setVisible(true);
						}else{
							frmMain = FrmMain.getInstance();
							//if (config.MARKET_ID != 5390){
								TimerTask timerTask1 = new MyTimerTask(frmMain, "closeAplication");
								//running timer task as daemon thread
								Timer timer1 = new Timer(true);
								final Locale locale = new Locale("ru");
								GregorianCalendar calendar = new GregorianCalendar();
								calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
								calendar.setTime(new Date());
								calendar.set(Calendar.HOUR_OF_DAY, 22);
								calendar.set(Calendar.MINUTE, 30);
								calendar.set(Calendar.SECOND, 0);
								timer1.scheduleAtFixedRate(timerTask1, calendar.getTime(), 600 * 1000);//каждые 10 минут начиная с времени 22:30
							//}
							
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
							timer3.schedule(timerTask3, config.TIME_WAIT * 1000); //выполняем один раз при каждом старте проги

							TimerTask timerTask4 = new MyTimerTask(frmMain, "linkStatusServer");
							//running timer task as daemon thread
							Timer timer4 = new Timer(true);
							timer4.scheduleAtFixedRate(timerTask4, 0, config.TIME_WAIT * 1000 * 60);
						}
                    }else{
						cnn.destroy();
                        System.exit(0);
                    }
                }else{
                    final FrmLogin frmLogin = FrmLogin.getInstance();
                    frmLogin.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            //System.out.println("login status= " + frmLogin.loginStatus);
                            if (frmLogin.loginStatus) {
								Loader.checkFrmStart();
								if (startAdmin) {
									frmAdmin = FrmAdmin.getInstance();
								}else{
									frmMain = FrmMain.getInstance();
									TimerTask timerTask1 = new MyTimerTask(frmMain, "closeAplication");
									//running timer task as daemon thread
									Timer timer1 = new Timer(true);
									final Locale locale = new Locale("ru");
									GregorianCalendar calendar = new GregorianCalendar();
									calendar.setFirstDayOfWeek(GregorianCalendar.MONDAY);
									calendar.setTime(new Date());
									calendar.set(Calendar.HOUR, 22);
									calendar.set(Calendar.MINUTE, 10);
									calendar.set(Calendar.SECOND, 0);
									timer1.scheduleAtFixedRate(timerTask1, calendar.getTime(), 600 * 1000);//каждые 10 минут начиная с времени 22:10

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
									timer3.schedule(timerTask3, config.TIME_WAIT * 1000); //выполняем один раз при каждом старте проги

									TimerTask timerTask4 = new MyTimerTask(frmMain, "linkStatusServer");
									//running timer task as daemon thread
									Timer timer4 = new Timer(true);
									timer4.scheduleAtFixedRate(timerTask4, 0, config.TIME_WAIT * 1000 * 60);
								}
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
