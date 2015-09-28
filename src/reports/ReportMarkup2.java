package reports;

import db.ConnectionDb;
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
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import main.ConfigReader;
import main.MyUtil;

public class ReportMarkup2 extends javax.swing.JFrame {

	private ConfigReader conf;
	private ConnectionDb cnn;
	private final double PPI_W = 100;
	//private final double PPI_H = 102;

	private class Report extends JPanel implements Printable {

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
		if (jTabbedPane.getTabCount() == 0) {
			JOptionPane.showMessageDialog(null, "Сначала надо сформировать отчет", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
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
					for (int i = 0; i < jTabbedPane.getTabCount(); i++) {
						pj.setJobName("ShopV2 - отчет по категориям наценки. Стр. " + (i + 1));
						Report r = new Report();
						r.setBackground(new java.awt.Color(255, 255, 255));
						JScrollPane sp = (JScrollPane) jTabbedPane.getComponentAt(i);
						JScrollBar verticalScrollBar = sp.getVerticalScrollBar();
						JScrollBar horizontalScrollBar = sp.getHorizontalScrollBar();
						verticalScrollBar.setValue(verticalScrollBar.getMinimum());
						horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
						Component c = (Component) sp.getViewport().getView();
//System.out.println(""+i+" "+c.getWidth() + " " + c.getHeight());
						r.add(c, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, c.getWidth(), c.getHeight()));
						r.setSize(new Dimension(c.getWidth(), c.getHeight()));
						r.setPreferredSize(new Dimension(c.getWidth(), c.getHeight()));
						pj.setPrintable(r, pf);
						pj.print();            // Обращается к print(g, pf, ind) 
						sp.setViewportView(c);
					}
				} else {
					int i = jTabbedPane.getSelectedIndex();
					pj.setJobName("ShopV2 - отчет по категориям наценки. Стр. " + (i + 1));
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

	public ReportMarkup2() {
		//setModalityType(JDialog.ModalityType.DOCUMENT_MODAL);
		setAlwaysOnTop(true);
		initComponents();
		clientInfo();
		getContentPane().setBackground(new java.awt.Color(255, 255, 255));
		//fillCheck();
		setLocationRelativeTo(null);
	}

	private void jButtonPrintAllActionPerformed() {
		printReportPage(true);
	}

	private void jButtonPrint1PageActionPerformed() {
		printReportPage(false);
	}

	private void jButtonGenerateReportActionPerformed() {
		fillCheck(jComboBoxReportType.getSelectedIndex());
	}

	private void clientInfo() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/png/logo.png")));
		conf = ConfigReader.getInstance();
		setTitle("Отчет по категориям наценки. ".concat(conf.FORM_TITLE));
	}

	private void addTabPane(JPanel panel) {
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setBorder(null);
		jScrollPane.setViewportView(panel);
		jTabbedPane.addTab("Страница " + (jTabbedPane.getTabCount() + 1), jScrollPane);
	}

	private int mm_to_ppi(int mm) {
		return (int) (mm / 25.4 * PPI_W);
	}

	private String getColumnName(int col, int type) {
		String res = "";
		if (col == 0 && type == 0) {
			res = "Группировка по категориям наценки";
		}
		if (col == 0 && type == 1) {
			res = "Группировка по категориям наценки и товарам";
		}
		if (col == 0 && type == 2) {
			res = "Группировка по категориям наценки и сотрудникам";
		}
		if (col == 0 && type == 3) {
			res = "Группировка по сотрудникам и категориям наценки";
		}
		if (col == 1 && type == 1) {
			res = "Артикул";
		}
		if (col == 1 && type == 2) {
			res = "Должность";
		}
		if (col == 1 && type == 3) {
			res = "";
		}
		if (col == 2 && type == 1) {
			res = "Название товара";
		}
		if (col == 2 && type == 2) {
			res = "Сотрудник";
		}
		if (col == 2 && type == 3) {
			res = "Категория";
		}
		return res;
	}

	private void fillCheck(int type) {
		cnn = ConnectionDb.getInstance();
		if (cnn == null) {
			return;
		}
		ResultSet res;
		res = cnn.getMarkupReport(type);
		try {
			if (res.isLast()) {
				JOptionPane.showMessageDialog(null, "Нет данных для вывода в отчет", "ВНИМАНИЕ!", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		} catch (SQLException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return;
		}
		jTabbedPane.removeAll();
		String str;

		JPanel jPanelMain = new JPanel(false);
		jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
		jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
		jPanelMain.setBorder(null);

		int x = 0, y = 0, h = 20, pl = 5, width_label = 0;
		BigDecimal total_cat_oborot = BigDecimal.ZERO;
		BigDecimal total_cat_prize = BigDecimal.ZERO;
		BigDecimal total_full_oborot = BigDecimal.ZERO;
		BigDecimal total_full_prize = BigDecimal.ZERO;

		JLabel jLabel1 = new javax.swing.JLabel();
		JLabel jLabel2 = new javax.swing.JLabel();
		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 16)); // название
		jLabel2.setFont(new java.awt.Font("Tahoma", 0, 16)); // цена
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		jLabel1.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("<html>&nbsp;&nbsp;<strong>Отчет по категориям наценки.<br>&nbsp;&nbsp;" + getColumnName(0, type) + "</strong></html>");
		jPanelMain.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(125), h * 2));
		jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel2.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel2.setText("<html>Сформирован: " + MyUtil.getCurrentDateTime(false) + "</html>");
		jPanelMain.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl + mm_to_ppi(125), y, mm_to_ppi(65), h * 2));
		y = y + h * 2 + 5;

		String cur_markup = "";
		try {
			while (res.next()) {
				JLabel jLabel11 = new javax.swing.JLabel();
				JLabel jLabel12 = new javax.swing.JLabel();
				JLabel jLabel13 = new javax.swing.JLabel();
				JLabel jLabel14 = new javax.swing.JLabel();
				JLabel jLabel15 = new javax.swing.JLabel();
				JLabel jLabel16 = new javax.swing.JLabel();
				JLabel jLabel21 = new javax.swing.JLabel();
				JLabel jLabel22 = new javax.swing.JLabel();
				JLabel jLabel23 = new javax.swing.JLabel();
				JLabel jLabel24 = new javax.swing.JLabel();
				JLabel jLabel25 = new javax.swing.JLabel();
				JLabel jLabel26 = new javax.swing.JLabel();
				JLabel jLabel31 = new javax.swing.JLabel();
				JLabel jLabel41 = new javax.swing.JLabel();
				JLabel jLabel44 = new javax.swing.JLabel();
				JLabel jLabel46 = new javax.swing.JLabel();

				jLabel11.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel16.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel21.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel22.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel23.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel24.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel31.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel41.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel44.setFont(new java.awt.Font("Tahoma", 0, 14));
				jLabel46.setFont(new java.awt.Font("Tahoma", 0, 14));

				jLabel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel21.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				jLabel46.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
				//дата время
				String new_markup = res.getString("MarkupName");
//System.out.println(cur_markup + "	" + new_markup);
				if (!new_markup.equals(cur_markup)) {
					if (cur_markup.length() > 0) {
						x = 0;
						jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
						jLabel41.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
						jLabel41.setText("<html>&nbsp;&nbsp;<strong>Итого:</strong></html>");
						jPanelMain.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(130) - 1, h + 10));
						x += mm_to_ppi(130) - 1;
						jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
						jLabel44.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
						jLabel44.setText("<html>&nbsp;&nbsp;<strong>" + total_cat_oborot.setScale(2, RoundingMode.HALF_UP).toPlainString() + "</strong></html>");
						jPanelMain.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(30), h + 10));
						x += mm_to_ppi(30);
						jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
						jLabel46.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
						jLabel46.setText("<html>&nbsp;&nbsp;<strong>" + total_cat_prize.setScale(2, RoundingMode.HALF_UP).toPlainString() + "</strong></html>");
						jPanelMain.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(30) - 1, h + 10));
						y = y + h + 10;
						total_cat_oborot = BigDecimal.ZERO;
						total_cat_prize = BigDecimal.ZERO;
					}
					cur_markup = new_markup;
					str = "";
					if (!res.getString("MarkupName").equals("")) {
						str = res.getString("MarkupName");
					}
					x = 0;
					jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
					jLabel31.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel31.setText("<html>&nbsp;&nbsp;<strong>" + str + "</strong></html>");
					jPanelMain.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(190) - 3, h + 10));
					y = y + h + 10;
					x = 0;
					width_label = mm_to_ppi(30);
					jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel21.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel21.setText("<html><strong>" + getColumnName(1, type) + "</strong></html>");
					jPanelMain.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(80);
					jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel22.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel22.setText("<html><strong>" + getColumnName(2, type) + "</strong></html>");
					jPanelMain.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(20);
					jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel23.setText("<html><strong>Кол-во</strong></html>");
					jPanelMain.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(30);
					jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel24.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel24.setText("<html><strong>Оборот</strong></html>");
					jPanelMain.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(10);
					jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel25.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel25.setText("<html><strong>%</strong></html>");
					jPanelMain.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(20);
					jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel26.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel26.setText("<html><strong>Премия</strong></html>");
					jPanelMain.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					y = y + h;
				}
				//подсчет итогов
				total_cat_oborot = total_cat_oborot.add(res.getBigDecimal("Oborot"));
				total_cat_prize = total_cat_prize.add(res.getBigDecimal("Markup"));
				total_full_oborot = total_full_oborot.add(res.getBigDecimal("Oborot"));
				total_full_prize = total_full_prize.add(res.getBigDecimal("Markup"));
				//артикул
				x = 0;
				width_label = mm_to_ppi(30);
				str = res.getString("Article");
//System.out.println("x=" + x + " y=" + y + " h=" + h + "	str=" + str);
				jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				jLabel11.setVerticalAlignment(javax.swing.SwingConstants.TOP);
				jLabel11.setText("<html>&nbsp;" + str + "</html>");
				jPanelMain.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));

				//название
				x += width_label;
				width_label = mm_to_ppi(80);
				str = res.getString("Name");
//System.out.println("x=" + x + " y=" + y + " h=" + h + "	str=" + str);
				jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
				jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);
				jLabel12.setText("<html>&nbsp;" + str + "</html>");
				jPanelMain.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));

				//Кол-во
				x += width_label;
				width_label = mm_to_ppi(20);
				str = res.getString("Quantity");
				jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				jLabel13.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
				jLabel13.setText("<html>" + str + "</html>");
				jPanelMain.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));

				//Оборот
				x += width_label;
				width_label = mm_to_ppi(30);
				str = res.getString("Oborot");
				jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
				jLabel14.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
				jLabel14.setText("<html>" + str + "</html>");
				jPanelMain.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));

				//Процент
				x += width_label;
				width_label = mm_to_ppi(10);
				str = res.getString("MarkupPercent");
				jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
				jLabel15.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
				jLabel15.setText("<html>" + str + "</html>");
				jPanelMain.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));

				//Сумма премии
				x += width_label;
				width_label = mm_to_ppi(20);
				str = res.getString("Markup");
				jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
				jLabel16.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
				jLabel16.setText("<html>" + str + "</html>");
				jPanelMain.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));

//System.out.println("x=" + x + " y=" + y + " h=" + h);
				y = y + h;
				//count++;
				//if (count > 1) break;
				if (y > 1100) {
//System.out.println(count + "	y = "+y);
					addTabPane(jPanelMain);
					jPanelMain = new JPanel(false);
					jPanelMain.setBackground(new java.awt.Color(255, 255, 255));
					jPanelMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
					jPanelMain.setBorder(null);
					y = 0;
					//count = 0; 
					x = 0;
					width_label = mm_to_ppi(30);
					jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel21.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel21.setText("<html><strong>Артикул</strong></html>");
					jPanelMain.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(80);
					jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel22.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel22.setText("<html><strong>Название товара</strong></html>");
					jPanelMain.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(20);
					jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel23.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel23.setText("<html><strong>Кол-во</strong></html>");
					jPanelMain.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(30);
					jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel24.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel24.setText("<html><strong>Оборот</strong></html>");
					jPanelMain.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(10);
					jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel25.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel25.setText("<html><strong>%</strong></html>");
					jPanelMain.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					x += width_label;
					width_label = mm_to_ppi(20);
					jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
					jLabel26.setVerticalAlignment(javax.swing.SwingConstants.TOP);
					jLabel26.setText("<html><strong>Премия</strong></html>");
					jPanelMain.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, width_label, h));
					y = y + h;
					//break;
				}
			}
			JLabel jLabel41 = new javax.swing.JLabel();
			JLabel jLabel44 = new javax.swing.JLabel();
			JLabel jLabel46 = new javax.swing.JLabel();
			jLabel41.setFont(new java.awt.Font("Tahoma", 0, 14));
			jLabel44.setFont(new java.awt.Font("Tahoma", 0, 14));
			jLabel46.setFont(new java.awt.Font("Tahoma", 0, 14));
			jLabel41.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
			jLabel44.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
			jLabel46.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
			x = 0;
			jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel41.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel41.setText("<html>&nbsp;&nbsp;<strong>Итого:</strong></html>");
			jPanelMain.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(130) - 1, h + 10));
			x += mm_to_ppi(130) - 1;
			jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel44.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel44.setText("<html>&nbsp;&nbsp;<strong>" + total_cat_oborot.setScale(2, RoundingMode.HALF_UP).toPlainString() + "</strong></html>");
			jPanelMain.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(30), h + 10));
			x += mm_to_ppi(30);
			jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel46.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel46.setText("<html>&nbsp;&nbsp;<strong>" + total_cat_prize.setScale(2, RoundingMode.HALF_UP).toPlainString() + "</strong></html>");
			jPanelMain.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(30) - 1, h + 10));
			y = y + h + 20;

			JLabel jLabel51 = new javax.swing.JLabel();
			JLabel jLabel54 = new javax.swing.JLabel();
			JLabel jLabel56 = new javax.swing.JLabel();
			jLabel51.setFont(new java.awt.Font("Tahoma", 0, 14));
			jLabel54.setFont(new java.awt.Font("Tahoma", 0, 14));
			jLabel56.setFont(new java.awt.Font("Tahoma", 0, 14));
			jLabel51.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
			jLabel54.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
			jLabel56.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(128, 128, 128)));
			x = 0;
			jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel51.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel51.setText("<html>&nbsp;&nbsp;<strong>Всего по отчету:</strong></html>");
			jPanelMain.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(130) - 1, h + 10));
			x += mm_to_ppi(130) - 1;
			jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel54.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel54.setText("<html>&nbsp;&nbsp;<strong>" + total_full_oborot.setScale(2, RoundingMode.HALF_UP).toPlainString() + "</strong></html>");
			jPanelMain.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(30), h + 10));
			x += mm_to_ppi(30);
			jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			jLabel56.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel56.setText("<html>&nbsp;&nbsp;<strong>" + total_full_prize.setScale(2, RoundingMode.HALF_UP).toPlainString() + "</strong></html>");
			jPanelMain.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(x + pl, y, mm_to_ppi(30) - 1, h + 10));
			y = y + h + 10;

		} catch (SQLException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
		if (jPanelMain.getComponentCount() > 0) {
			addTabPane(jPanelMain);
		}
		//pack();
		//setLocationRelativeTo(null);
		if (jTabbedPane.getComponentCount() > 0) {
			Component c = jTabbedPane.getComponent(0);
			//System.out.println("getY:"+c.getY()+" "+c.getClass().getCanonicalName());
//			System.out.println(getWidth() + 10);
//			System.out.println(mm_to_ppi(100) + c.getY() + 55);
			setSize(new Dimension(getWidth() + 10, mm_to_ppi(100) + c.getY() + 55));
			//jPanelMenu.setPreferredSize(new Dimension(getWidth() - 8, jPanelMenu.getHeight()));
		}
	}

	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelMenu = new javax.swing.JPanel();
        jButtonPrintAll = new javax.swing.JButton();
        jButtonPrint1Page = new javax.swing.JButton();
        jLabelReportType = new javax.swing.JLabel();
        jComboBoxReportType = new javax.swing.JComboBox();
        jButtonGenerateReport = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

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

        jLabelReportType.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        jLabelReportType.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelReportType.setText("Вид отчета:");
        jLabelReportType.setAutoscrolls(true);
        jLabelReportType.setFocusable(false);
        jLabelReportType.setPreferredSize(new java.awt.Dimension(41, 17));
        jLabelReportType.setRequestFocusEnabled(false);
        jLabelReportType.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jComboBoxReportType.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jComboBoxReportType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "по категориям наценки", "по кат. нац. и товарам", "по кат. нац. и сотрудникам", "по сотрудникам и кат. нац." }));
        jComboBoxReportType.setBorder(null);
        jComboBoxReportType.setMinimumSize(new java.awt.Dimension(240, 26));
        jComboBoxReportType.setPreferredSize(new java.awt.Dimension(240, 26));

        jButtonGenerateReport.setText("Сформировать");
        jButtonGenerateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerateReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMenuLayout = new javax.swing.GroupLayout(jPanelMenu);
        jPanelMenu.setLayout(jPanelMenuLayout);
        jPanelMenuLayout.setHorizontalGroup(
            jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonGenerateReport)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPrint1Page)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPrintAll)
                .addGap(0, 36, Short.MAX_VALUE))
        );
        jPanelMenuLayout.setVerticalGroup(
            jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMenuLayout.createSequentialGroup()
                .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxReportType, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonGenerateReport)
                        .addComponent(jButtonPrint1Page)
                        .addComponent(jButtonPrintAll)))
                .addGap(6, 6, 6))
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
                .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonPrintAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintAllActionPerformed
        jButtonPrintAllActionPerformed();
    }//GEN-LAST:event_jButtonPrintAllActionPerformed

    private void jButtonPrint1PageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrint1PageActionPerformed
        jButtonPrint1PageActionPerformed();
    }//GEN-LAST:event_jButtonPrint1PageActionPerformed

    private void jButtonGenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerateReportActionPerformed
        jButtonGenerateReportActionPerformed();
    }//GEN-LAST:event_jButtonGenerateReportActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(ReportMarkup2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ReportMarkup2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ReportMarkup2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ReportMarkup2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ReportMarkup2().setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonGenerateReport;
    private javax.swing.JButton jButtonPrint1Page;
    private javax.swing.JButton jButtonPrintAll;
    private javax.swing.JComboBox jComboBoxReportType;
    public javax.swing.JLabel jLabelReportType;
    private javax.swing.JPanel jPanelMenu;
    private javax.swing.JTabbedPane jTabbedPane;
    // End of variables declaration//GEN-END:variables
}
