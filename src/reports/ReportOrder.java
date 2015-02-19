package reports;

import db.ConnectionDb;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import main.ConfigReader;
import main.DialogBoxs;
import main.MyUtil;

public class ReportOrder extends JDialog implements Printable{
    private ConfigReader conf;
    private ConnectionDb cnn;
	private BigDecimal currentDocID;

    private void setjPanel0(boolean blVisible){
        jPanel0.setEnabled(blVisible);
        jPanel0.setVisible(blVisible);
        pack();
    }
	
	@Override
	public int print(Graphics g, PageFormat pf, int pageNumber) throws PrinterException {
//		System.out.println("page: "+Integer.toString(pageNumber));
		if (pageNumber > 0) { /* We have only one page, and 'page' is zero-based */

			return NO_SUCH_PAGE;
		}
		/* User (0,0) is typically outside the imageable area, so we must
		 * translate by the X and Y values in the PageFormat to avoid clipping
		 */
		Graphics2D g2d = (Graphics2D) g;
		//g2d.translate(pf.getImageableX(), pf.getImageableY());
		g2d.translate(0f, 0f);
		/* Now print the window and its visible contents */
		print(g);
		/* tell the caller that this page is part of the printed document */
		return PAGE_EXISTS;
	}
    public boolean print(boolean blViewDialogPrint) {
		setjPanel0(false);
		Paper p = new Paper();
		p.setImageableArea(0, 0, 226, 9286);
		p.setSize(226, 9286);
		PageFormat pf = new PageFormat();
		pf.setPaper(p);

		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setJobName("ShopV2 report Sale");
		pj.setCopies(1);
		pj.setPrintable(this, pf);

		if (blViewDialogPrint) {
			if (pj.printDialog()) {
				try {
					pj.print();            // Обращается к print(g, pf, ind) 
				} catch (PrinterException ex) {
					//Logger.getLogger(ReportOrder.class.getName()).log(Level.SEVERE, null, ex);
					MyUtil.errorToLog(this.getClass().getName(), ex);
					setjPanel0(true);
					return false;
				}
			} else {
				setjPanel0(true);
				return false;
			}
		} else {
			try {
				pj.print();
			} catch (PrinterException ex) {
				//Logger.getLogger(ReportOrder.class.getName()).log(Level.SEVERE, null, ex);
				MyUtil.errorToLog(this.getClass().getName(), ex);
				setjPanel0(true);
				return false;
			}
		}
		setjPanel0(true);
		return true;
    }
    public ReportOrder(BigDecimal docID) {
		currentDocID = docID;
		if (currentDocID.equals("")) dispose();
        initComponents();
        clientInfo();
        setLocationRelativeTo(null);
	}
    private void jButton1ActionPerformed(){
		print(true);
    }
    private void clientInfo() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
        conf = ConfigReader.getInstance();
        setTitle("Заказ. ".concat(conf.FORM_TITLE));
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        jLabel1.setText(cnn.getClientInfo("Company"));
        jLabel2.setText(cnn.getClientInfo("NameValid"));
        jLabel3.setText(cnn.getClientInfo("Telephone"));
        jLabel4.setText(cnn.getClientInfo("City"));
        jLabel5.setText(cnn.getClientInfo("Address"));
        jLabel6.setText("Заказ № "+currentDocID+"   ("+MyUtil.getClientID()+")");
        jLabel30.setText(MyUtil.getCurrentDateTime());
    }
    private void fillCheck(){
        cnn = ConnectionDb.getInstance();
        if (cnn == null) return;
        ResultSet res = cnn.getOrderReport(currentDocID);
        int y = 0, h = 15;
		try {
			while (res.next()) {
				//for (int i = 0; i < 5; i++) {
				jLabel11 = new javax.swing.JLabel();
				jLabel12 = new javax.swing.JLabel();
				jLabel13 = new javax.swing.JLabel();
				jSeparator2 = new javax.swing.JSeparator();
				
				jLabel11.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
				jLabel12.setFont(new java.awt.Font("Tahoma", 0, 9)); // NOI18N
				jLabel13.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
				
//				jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
//				jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
//				jLabel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

				//артикул
				y = y + 2; h = 13;
				String str = res.getString("Article").toString() + " " + res.getString("Name").toString();
				if (str.length() > 30 && str.length() <= 60) h = h * 2;
				if (str.length() > 60 && str.length() <= 90) h = h * 3;
				if (str.length() > 90) h = h * 4;
				jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				jLabel11.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
				//jLabel11.setText("<html>" + Integer.toString(str.length()) + " "+Integer.toString(h)+ " " + "</html>");
				jLabel11.setText("<html>" + str + "</html>");
				jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, y, 150, h));
				
				//цена
//System.out.println(res.getString("GoodID").toString()+"	"+str);
				BigDecimal bdQuantity = res.getBigDecimal("Quantity");
				String strQuantity = "";
				if (bdQuantity != null) 
					strQuantity = bdQuantity.setScale(0, RoundingMode.HALF_UP).toPlainString();
				jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
				jLabel13.setText(strQuantity);
				jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(165, y, 35, h));
				
				y = y + h;
				jPanel2.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, y, 210, 2));
			}
		} catch (SQLException ex) {
			//Logger.getLogger(ReportOrder.class.getName()).log(Level.SEVERE, null, ex);
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel0 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jButton1.setText("Печать");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel0Layout = new javax.swing.GroupLayout(jPanel0);
        jPanel0.setLayout(jPanel0Layout);
        jPanel0Layout.setHorizontalGroup(
            jPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel0Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel0Layout.setVerticalGroup(
            jPanel0Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1)
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ЧП Пупкин П.П.");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Магазин \"МАСТЕР-ЗОО\"");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("тел. 057 1234567");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("г.Харьков");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("ТЦ \"Караван\"");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("Переоценка");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jSeparator1.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jLabel1.getAccessibleContext().setAccessibleDescription("");
        jLabel2.getAccessibleContext().setAccessibleDescription("");
        jLabel3.getAccessibleContext().setAccessibleDescription("");
        jLabel4.getAccessibleContext().setAccessibleDescription("");
        jLabel5.getAccessibleContext().setAccessibleDescription("");
        jLabel6.getAccessibleContext().setAccessibleDescription("");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(0, 40));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        fillCheck();

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(0, 0));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Дата: 15/04/14 Время: 16:37:22");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel0, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jButton1ActionPerformed();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel0;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JSeparator jSeparator2;
}
