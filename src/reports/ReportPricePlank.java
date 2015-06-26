package reports;

import db.ConnectionDb;
import forms.FrmMain;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UnsupportedLookAndFeelException;
import main.ConfigReader;
import main.MyUtil;

public class ReportPricePlank extends JDialog{
    private ConfigReader conf;
    private ConnectionDb cnn;
	private final BigDecimal currentDocID;
	private final double PPI_W = 100;
	private final double PPI_H = 102;
	private int type;

	private class Report extends JPanel implements Printable{
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			int result = NO_SUCH_PAGE;
			if (pageIndex < 1) {
				Graphics2D g2d = (Graphics2D) graphics;
				g2d.translate(conf.PLANK_PADDING_LEFT, conf.PLANK_PADDING_TOP);
//System.out.println(""+conf.PLANK_PADDING_LEFT + " " + conf.PLANK_PADDING_TOP);
				//Get the relation between the label width and image width:
				double sx = 0.72;
				//Get the relation between the label height and image height:
				double sy = 0.72;
				//Use the relation to scale the image to the printer
				g2d.scale(sx, sy);
				print(graphics);
				result = PAGE_EXISTS;
			}
			return result;
		}
	}
	
    public boolean printReportPage(boolean blAllPages) {
		PrinterJob pj = PrinterJob.getPrinterJob();
		if (pj.printDialog()) {
			try {
				PageFormat pf = new PageFormat();
				Paper p = new Paper();
				p.setSize(conf.PAGE_WIDTH, conf.PAGE_HEIGHT); //A4 (borderless)
				p.setImageableArea(0, 0, p.getWidth(), p.getHeight());
				pf.setPaper(p);
				//Открываем диалоговое окно Параметры страницы
				//PageFormat pf = pj.pageDialog(pj.defaultPage());
				//pj.pageDialog(pf);
//System.out.println("формат   " + pf.getWidth() + "	" + pf.getHeight() + " " + blAllPages);
				pj.setCopies(1);
				if (blAllPages) {
					for (int i=0; i < jTabbedPane.getTabCount(); i++){
						pj.setJobName("ShopV2 - ценовые планки. Стр. " + (i+1));
						Report r = new Report();
						r.setBackground(new java.awt.Color(255, 255, 255));
						JScrollPane sp = (JScrollPane) jTabbedPane.getComponentAt(i);
						Component c = (Component) sp.getViewport().getView();
//System.out.println(""+i+" "+c.getWidth() + " " + c.getHeight());
						r.add(c,new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, c.getWidth(), c.getHeight()));
						r.setSize(new Dimension(c.getWidth(),c.getHeight()));
						r.setPreferredSize(new Dimension(c.getWidth(), c.getHeight()));
						pj.setPrintable(r, pf);
						pj.print();            // Обращается к print(g, pf, ind) 
						sp.setViewportView(c);
					}
				}else{
					int i = jTabbedPane.getSelectedIndex();
					pj.setJobName("ShopV2 - ценовые планки. Стр. " + (i+1));
					Report r = new Report();
					r.setBackground(new java.awt.Color(255, 255, 255));
					JScrollPane sp = (JScrollPane) jTabbedPane.getComponentAt(i);
					Component c = sp.getViewport().getView();
//System.out.println(""+i+" "+c.getWidth() + " " + c.getHeight());
					r.add(c, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, c.getWidth(), c.getHeight()));
					r.setSize(new Dimension(c.getWidth(), c.getHeight()));
					r.setPreferredSize(new Dimension(c.getWidth(), c.getHeight()));
					pj.setPrintable(r, pf);
					pj.print();            // Обращается к print(g, pf, ind) 
					sp.setViewportView(c);
				}
			} catch (PrinterException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
				return false;
			}
		} else {
			return false;
		}
		return true;
    }
    public ReportPricePlank(BigDecimal docID, int type) {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassCastException | IndexOutOfBoundsException | NullPointerException | IllegalArgumentException | ArithmeticException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			MyUtil.errorToLog(FrmMain.class.getName(), ex);
		}
		currentDocID = docID;
		this.type = type;
		if (currentDocID.equals("")) dispose();
        initComponents();
        clientInfo();
		getContentPane().setBackground(new java.awt.Color(255, 255, 255));
		fillCheck();
        setLocationRelativeTo(null);
	}
    private void jButtonPrintAllActionPerformed(){
		printReportPage(true);
    }
    private void jButtonPrint1PageActionPerformed(){
		printReportPage(false);
    }
    private void clientInfo() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        conf = ConfigReader.getInstance();
        setTitle("Ценники. Ценовые планки. ".concat(conf.FORM_TITLE));
	}
	private void addTabPane(JPanel panel){
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setBorder(null);
		jScrollPane.setViewportView(panel);
		jTabbedPane.addTab("Страница "+(jTabbedPane.getTabCount()+1), jScrollPane);
	}
	private void fillCheck(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		ResultSet res;
		if (type == 1) {
			res = cnn.getStickerReport(currentDocID,"5157");//планки
		} else {
			res = cnn.getStickerReport(currentDocID, "%");//планки
		}
		String str;
		
		JPanel jPanelMain = new JPanel(false);
		jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
		jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		jPanelMain.setBorder(null);

		int width_mm = 665, height_mm = 400;
		int width_out = (int) (width_mm / 10 / 25.4 * PPI_W);
		int height_out = (int) (height_mm / 10 / 25.4 * PPI_H);
		int h = height_out / 8;
		height_out--;
		int x_out = width_out * 0;
		int y_out = height_out * 0;
//		System.out.println("height_out: "+height_out);
//		System.out.println("width_out: "+width_out);
		int iQuantity = 0, col_number = 1;
		int count = 0;
		try {
			while (res.next()) {
				//iQuantity = res.getInt("Quantity");
				iQuantity = 1;//ценовые планки всегда по 1 штуке
				for (int q = 1; q <= iQuantity; q++) {
//System.out.println("bdQuantity=" + Integer.toString(iQuantity) + " " + Integer.toString(q) + " " + res.getString("Name") + "	y_out:" + Integer.toString(y_out)+ "	col:" + Integer.toString(col_number));
					int x = 0, y = 0, height_label = 0;
					int padding_left = 5, padding_right = padding_left + 5;
					x += padding_left;
					JPanel jPanel1 = new javax.swing.JPanel();
					jPanel1.setBackground(new java.awt.Color(255, 255, 255));
					jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
					jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
					JLabel jLabel11 = new javax.swing.JLabel();
					JLabel jLabel12 = new javax.swing.JLabel();
					JLabel jLabel13 = new javax.swing.JLabel();
					JLabel jLabel14 = new javax.swing.JLabel();
					JLabel jLabel15 = new javax.swing.JLabel();
					JLabel jLabel16 = new javax.swing.JLabel();
					JLabel jLabel17 = new javax.swing.JLabel();
					JLabel jLabel18 = new javax.swing.JLabel();
//					JSeparator jSeparator2 = new javax.swing.JSeparator();

					jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14)); // название
					jLabel12.setFont(new java.awt.Font("Tahoma", 1, 30)); // цена
					jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // артикул
					jLabel14.setFont(new java.awt.Font("Tahoma", 0, 10)); // дата время
					jLabel18.setFont(new java.awt.Font("Tahoma", 0, 10)); // кат.наценки
					jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // производитель
					jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14)); // текст "Цена: "
					jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // текст "грн."

//					jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
					//дата время
					height_label = (int) (h * 1);
					str = "";
					if (!res.getString("CatMargin").equals(""))
						str = "&nbsp;&nbsp;&nbsp;("+res.getString("CatMargin")+")";
					jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel18.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel18.setText("<html>" + str + "</html>");
					jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width_out - padding_right, height_label));
					jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
					jLabel14.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel14.setText("<html>" + MyUtil.getCurrentDate() + "</html>");
					jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width_out - padding_right, height_label));

					//производитель
					y = y + height_label;
					height_label = (int) (h * 1);
					str = res.getString("Producer");
					jLabel15.setSize(new Dimension(width_out - padding_right, height_label));
					jLabel15.setPreferredSize(new Dimension(width_out - padding_right, height_label));
					jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel15.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel15.setText("<html>Производитель:&nbsp;<strong>" + str + "</strong></html>");
					jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width_out - padding_right, height_label));

					//артикул
					y = y + height_label;
					height_label = h * 1;
					str = res.getString("Article");
					jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel13.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel13.setText("<html>Артикул:&nbsp;&nbsp;<strong>" + str + "</strong></html>");
					jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width_out - padding_right, height_label));

					//название
					y = y + height_label;
					height_label = (int) (h * 3);
					str = res.getString("Name");
					jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel11.setText("<html>Название:&nbsp;<strong>" + str + "</strong></html>");
					jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, width_out - padding_right, height_label));

					//текст "Цена:"
					int segment = 6;
					y = y + height_label;
					height_label = h * 2;
					jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel16.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
					jLabel16.setText("<html>Цена:&nbsp;</html>");
					jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, (width_out - padding_right) / segment, height_label - 5));

					//цена
					//y = y + height_label;
					//height_label = h * 2;
					str = res.getString("Price") + "&nbsp;";
					jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
					jLabel12.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
					jLabel12.setText("<html>" + str + "</html>");
					jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + (width_out - padding_right) / segment, y, (width_out - padding_right) / segment * (segment - 2), height_label));

					//текст "грн."
					//y = y + height_label;
					//height_label = h * 2;
					jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel17.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
					jLabel17.setText("<html>&nbsp;грн.</html>");
					jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + (width_out - padding_right) / segment * (segment - 1), y, (width_out - padding_right) / segment, height_label - 5));

					jPanelMain.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(x_out, y_out, width_out, height_out));
					count++;
					x_out = width_out * col_number;
					col_number++;
					if (col_number > 3) {
						col_number = 1;
						x_out = 0;
						y_out += height_out;
					}
					if (q >= iQuantity) {
						break;
					}
					if (count > 20) {
						addTabPane(jPanelMain);
						jPanelMain = new JPanel(false);
						jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
						jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
						jPanelMain.setBorder(null);
						count = 0;
						x_out = 0;
						y_out = 0;
						col_number = 1;
					}
				}
				if (count > 20) {
					addTabPane(jPanelMain);
					jPanelMain = new JPanel(false);
					jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
					jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
					jPanelMain.setBorder(null);
					count = 0;
					x_out = 0;
					y_out = 0;
					col_number = 1;
				}
			}
		} catch (SQLException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
		if (jPanelMain.getComponentCount() > 0) addTabPane(jPanelMain);
		pack();
		if (jTabbedPane.getComponentCount() > 0) {
			Component c = jTabbedPane.getComponent(0);
	//System.out.println("getY:"+c.getY()+" "+c.getClass().getCanonicalName());
			setSize(new Dimension(getWidth(), height_out * 3 + c.getY()+55));
			jPanelMenu.setPreferredSize(new Dimension(getWidth() - 8, jPanelMenu.getHeight()));
		}
	}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMenu = new javax.swing.JPanel();
        jButtonPrintAll = new javax.swing.JButton();
        jButtonPrint1Page = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelMenu.setBackground(new java.awt.Color(255, 255, 255));

        jButtonPrintAll.setText("Печать всех страниц");
        jButtonPrintAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintAllActionPerformed(evt);
            }
        });

        jButtonPrint1Page.setText("Печать выбранной страницы");
        jButtonPrint1Page.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrint1PageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMenuLayout = new javax.swing.GroupLayout(jPanelMenu);
        jPanelMenu.setLayout(jPanelMenuLayout);
        jPanelMenuLayout.setHorizontalGroup(
            jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMenuLayout.createSequentialGroup()
                .addComponent(jButtonPrintAll, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jButtonPrint1Page, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
        );
        jPanelMenuLayout.setVerticalGroup(
            jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButtonPrintAll)
            .addComponent(jButtonPrint1Page)
        );

        jTabbedPane.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButtonPrintAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintAllActionPerformed
        jButtonPrintAllActionPerformed();
    }//GEN-LAST:event_jButtonPrintAllActionPerformed
    private void jButtonPrint1PageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrint1PageActionPerformed
        jButtonPrint1PageActionPerformed();
    }//GEN-LAST:event_jButtonPrint1PageActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonPrint1Page;
    private javax.swing.JButton jButtonPrintAll;
    private javax.swing.JPanel jPanelMenu;
    private javax.swing.JTabbedPane jTabbedPane;
    // End of variables declaration//GEN-END:variables
}
