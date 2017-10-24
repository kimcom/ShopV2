package reports;

import db.ConnectionDb;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import main.ConfigReader;
import main.MyUtil;

public class ReportPricePlankA4 extends JDialog{
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
				//g2d.translate(conf.PLANK_PADDING_LEFT, conf.PLANK_PADDING_TOP);
				g2d.translate(0, 0);
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
	private String getMyJobName(){
		String res = "";
		if (type == 7){
			res = "ShopV2 - ценник А4 стандарт. Стр. ";
		}else if (type == 8) {
			res = "ShopV2 - ценник А4 клубная цена. Стр. ";
		}else if (type == 9) {
			res = "ShopV2 - ценник А4 старая цена. Стр. ";
		}else{
			res = "ShopV2 - ценник А4. Стр. ";
		}
		return res;
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
						pj.setJobName(getMyJobName() + (i+1));
						Report r = new Report();
						r.setBackground(new java.awt.Color(255, 255, 255));
						JScrollPane sp = (JScrollPane) jTabbedPane.getComponentAt(i);
						JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
						JScrollBar horizontalScrollBar = sp.getHorizontalScrollBar();
						verticalScrollBar.setValue(verticalScrollBar.getMinimum());
						horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
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
					pj.setJobName(getMyJobName() + (i+1));
					Report r = new Report();
					r.setBackground(new java.awt.Color(255, 255, 255));
					JScrollPane sp = (JScrollPane) jTabbedPane.getComponentAt(i);
					JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
					JScrollBar horizontalScrollBar = sp.getHorizontalScrollBar();
					verticalScrollBar.setValue(verticalScrollBar.getMinimum());
					horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
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
    public ReportPricePlankA4(BigDecimal docID, int type) {
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
		if (type==7){
			setTitle("Ценники. Ценники А4 стандарт. ".concat(conf.FORM_TITLE));
		}else if (type == 8) {
			setTitle("Ценники. Ценники А4 клубная цена. ".concat(conf.FORM_TITLE));
		}else if (type == 9) {
			setTitle("Ценники. Ценники А4 старая цена. ".concat(conf.FORM_TITLE));
		}
	}
	private void addTabPane(JPanel panel){
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setBorder(null);
		jScrollPane.setViewportView(panel);
		jTabbedPane.addTab("Страница "+(jTabbedPane.getTabCount()+1), jScrollPane);
	}
	class MyJLabel extends JLabel{
		public void paint(Graphics g){
			//System.out.println("paint");
			super.paint(g);
			if (type == 9){
				int[] arrayX = {  0,  0,  3,  6,388,390,387,384};
				int[] arrayY = {114,117,120,119,  6,  3,  0,  0};
				Polygon poly = new Polygon(arrayX, arrayY, 8);
				g.setColor(Color.GRAY);
				g.drawPolygon(poly);		
				g.fillPolygon(poly);
				g.setColor(Color.WHITE);
			}		
		}		
	}
	private void fillCheck(){
		cnn = ConnectionDb.getInstance();
		if (cnn == null) return;
		ResultSet res;
		res = cnn.getStickerReport(currentDocID, "A4");// A4
		String str;
		
		JPanel jPanelMain = new JPanel(false);
		jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
		jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		jPanelMain.setBorder(null);

		int width_mm = 2070, height_mm = 2770;
		int width_out = (int) (width_mm / 10 / 25.4 * PPI_W);
		int height_out = (int) (height_mm / 10 / 25.4 * PPI_H);
		height_out--;
		int x_out = width_out * 0;
		int y_out = height_out * 0;
//	System.out.println("height_out: "+height_out);
//	System.out.println("width_out: "+width_out);
		int iQuantity = 0, col_number = 1;
		int count = 0;
		try {
			while (res.next()) {
				//iQuantity = res.getInt("Quantity");
				iQuantity = 1;//ценники А4 всегда по 1 штуке
				for (int q = 1; q <= iQuantity; q++) {
					JPanel jPanel1 = new JPanel();
					jPanel1.setBackground(new java.awt.Color(255, 255, 255));
					jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
					//jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
					JLabel jLabel11 = new javax.swing.JLabel();
					JLabel jLabel12 = new javax.swing.JLabel();
					JLabel jLabel13 = new javax.swing.JLabel();
					JLabel jLabel14 = new javax.swing.JLabel();
					JLabel jLabel15 = new javax.swing.JLabel();
					JLabel jLabel16 = new javax.swing.JLabel();
					JLabel jLabel17 = new javax.swing.JLabel();
					JLabel jLabel18 = new javax.swing.JLabel();
					JLabel jLabel19 = new MyJLabel();
					JLabel jLabel20 = new javax.swing.JLabel();
					JLabel jLabel21 = new javax.swing.JLabel();
					JLabel jLabel22 = new javax.swing.JLabel();
					JLabel jLabel30 = new javax.swing.JLabel();
					//JSeparator jSeparator2 = new javax.swing.JSeparator();

					//jLabel30.setFont(new java.awt.Font("Tahoma", 0, 12)); // логотип
					
					jLabel20.setFont(new java.awt.Font("Tahoma", 0, 16)); // артикул текст
					jLabel21.setFont(new java.awt.Font("Tahoma", 0, 16)); // артикул
					jLabel14.setFont(new java.awt.Font("Tahoma", 0, 16)); // дата время
					jLabel18.setFont(new java.awt.Font("Tahoma", 0, 16)); // кат.наценки
					jLabel11.setFont(new java.awt.Font("Tahoma", 1, 80)); // название
					jLabel12.setFont(new java.awt.Font("Tahoma", 1, 140));// цена
					jLabel17.setFont(new java.awt.Font("Tahoma", 1, 20)); // текст клуб.цена
					jLabel22.setFont(new java.awt.Font("Tahoma", 0, 20)); // вiд 3-5 одиниць
					jLabel19.setFont(new java.awt.Font("Tahoma", 0, 90)); // цена клуб
					
					jLabel16.setFont(new java.awt.Font("Tahoma", 1, 120)); // текст за один.
					jLabel13.setFont(new java.awt.Font("Tahoma", 0, 11)); // 
					jLabel15.setFont(new java.awt.Font("Tahoma", 0, 11)); // 
					
					jLabel22.setBackground(new java.awt.Color(128, 128, 128));
					jLabel22.setForeground(new Color(255,255,255));
					jLabel22.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(128, 128, 128), 1, true));
					jLabel22.setOpaque(true);

					jLabel17.setBackground(new java.awt.Color(128, 128, 128));
					jLabel17.setForeground(new Color(255,255,255));
					jLabel17.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(128, 128, 128), 1, true));
					jLabel17.setOpaque(true);

//					jLabel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
//					jLabel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));

//					jLabel30.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 0, 0), 1, true));
//					jLabel20.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 128)));
//					jLabel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 128, 0)));
//					jLabel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 255, 0)));
//					jLabel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 0, 128)));
//					jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 0, 0)));
//					jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
//					jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 0, 0)));
//					jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 255)));
//					jLabel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 128, 0)));
//					jLabel19.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));

					//logo
					jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/png/logo_gray.png")));
					jPanel1.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 80, 200, 129));

					//текст "артикул"
					jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel20.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel20.setText("<html>Артикул:</html>");
					jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 320, 70, 24));
					//артикул
					str = res.getString("Article");
					jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel21.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel21.setText("<html>"+str+"</html>");
					jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(142, 320, 200, 24));
					
					//дата время
					jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel14.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel14.setText("<html>" + MyUtil.getCurrentDate2() + "</html>");
					jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 320, 100, 24));

					//категория наценки
					str = "";
					if (!res.getString("CatMargin").equals(""))
						str = "("+res.getString("CatMargin")+")";
					jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel18.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel18.setText("<html>" + str + "</html>");
					jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 320, 20, 24));

					//название
					str = res.getString("Name");
					jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel11.setText("<html><strong>" + str + "</strong></html>");
					jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 350, 680, 310));

					//цена
					str = res.getString("Price");
					jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel12.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel12.setText("<html>" + str + "</html>");
					jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 680, 750, 130));
					
					if (type == 8) { // клубная цена
						if (!res.getString("Price").toString().equals("") && !res.getString("DiscountClub").toString().equals("") && !res.getString("QtyClub").equals("0")) {
							//текст "Клубна ціна" 
							jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
							jLabel17.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
							jLabel17.setText("<html><center>Клубна ціна:</html>");
							jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 950, 240, 70));
							//від 5-ти одиниць
							jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
							jLabel22.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
							jLabel22.setText("<html>&nbsp;&nbsp;від " + res.getString("QtyClub") + " одиниць</html>");
							jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 950, 160, 70));
							//рассчитываем цену
							BigDecimal discountClub = new BigDecimal(100).subtract(new BigDecimal(res.getString("DiscountClub"))).divide(new BigDecimal(100));
							BigDecimal priceClub = new BigDecimal(res.getString("Price")).multiply(discountClub).setScale(2, RoundingMode.HALF_UP);
							str = priceClub.toPlainString() + "&nbsp;";
							jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
							jLabel19.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
							jLabel19.setText("<html>" + str + "</html>");
							jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 920, 390, 120));
						}
					}else if (type == 9){ // старая цена
						if (res.getString("PriceOld") != null) {
							//текст "Стара ціна" 
							jLabel17.setFont(new java.awt.Font("Tahoma", 1, 32)); // текст старая цена
							jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
							jLabel17.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
							jLabel17.setText("<html><center>Стара ціна:</html>");
							jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 950, 400, 70));
							//выводим старую цену
							str = res.getString("PriceOld") + "&nbsp;";
							jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
							jLabel19.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
							jLabel19.setText("<html>" + str + "</html>");
							jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 920, 390, 120));
						}
					}
					
					jPanelMain.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(x_out, y_out, width_out, height_out));
					count++;
					x_out = width_out * col_number;
					col_number++;
					if (col_number > 1) {
						col_number = 1;
						x_out = 0;
						y_out += height_out;
					}
					if (q >= iQuantity) {
						break;
					}
					if (count > 0) {
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
				if (count > 0) {
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
			//System.out.println("getY:"+c.getY()+" height_out:"+height_out+"	getHeight()="+getHeight()+"	getWidth()="+getWidth());
			//setSize(new Dimension(getWidth(), height_out * 3 + c.getY()+55));
			setSize(new Dimension(getWidth(), height_out-500));
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
