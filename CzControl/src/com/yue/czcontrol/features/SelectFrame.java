package com.yue.czcontrol.features;

import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.yue.czcontrol.MainFrame;
import com.yue.czcontrol.listener.TimerListener;
import com.yue.czcontrol.utils.SocketSetting;
import com.yue.czcontrol.utils.TimeProperty;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelectFrame extends JFrame implements TimeProperty, SocketSetting{

	private static final long serialVersionUID = 1L;
	
	private Connection conn = null;

	private JTable table;
	
	private final PrintWriter out;
	
	MainFrame mf;

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);
	
	@Override
	public void addData(String msg) {
		
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void message(String msg) {
		out.println(msg);
		out.flush();
	}
	
	/**
	 * get the data from DataBase
	 * and make a table
	 */
	private void createData() {
		try {
			//Get data(SQL Syntax)
			String select = "SELECT * FROM player";
			//String select to PreparedStatement
			PreparedStatement psmt = conn.prepareStatement(select);

			ResultSet rs = psmt.executeQuery();
			
			//Define the column name
			String[] columnName = {"\u7de8\u865f", "\u540d\u7a31", "\u8077\u4f4d" , "\u672c\u6708\u6d3b\u8e8d" , "\u8ca0\u8cac\u4eba"};
			
			DefaultTableModel tableModel = new DefaultTableModel(columnName, 0);
			
			//Detect data
			while(rs.next()) {
				String id = rs.getString("ID");
				String name = rs.getString("NAME");
				String rank = rs.getString("RANK");
				String active = rs.getString("ACTIVE");
				String handler = rs.getString("HANDLER");
				
			    String[] data = {id, name, rank, active, handler};
			    
				tableModel.addRow(data);
				
			}
			
			table = new JTable(tableModel);
			table.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 15));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create a Frame
	 * 
	 * @param name The user Name
	 * @param user user Account
	 * @param password user Password
	 * @param socket The user's socket
	 * @throws IOException IOException
	 */
	public SelectFrame(String name, String user, String password, Socket socket) throws IOException {
		out = new PrintWriter(socket.getOutputStream());
		
		mf = new MainFrame(socket);
		
		try {
			this.conn = com.yue.czcontrol.LoginFrame.initDB(this.conn);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//JFrame init
		setTitle("Cz\u7BA1\u7406\u7CFB\u7D71-\u6AA2\u8996\u6210\u54E1");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);		
		setBounds(100, 100, 787, 503);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Panel init
		JPanel panel = new JPanel();
		panel.setBackground(new Color(65,105,225));
		panel.setBounds(5, 5, 763, 140);
		contentPane.add(panel);
		panel.setLayout(null);
		
		//Title init
		JLabel title = new JLabel("TeamCz\u8ECA\u968A\u7BA1\u7406\u7CFB\u7D71-\u6210\u54E1\u6AA2\u8996\r\n");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 40));
		title.setBounds(10, 10, 743, 67);
		panel.add(title);
		
		//userLabel init
		JLabel userLabel = new JLabel("\u76ee\u524d\u4f7f\u7528\u5e33\u865f: " + name);
		userLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		
		
		userLabel.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 20));
		userLabel.setBounds(478, 87, 285, 43);
		panel.add(userLabel);
		
		//ScrollPane init
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 155, 768, 260);
		contentPane.add(scrollPane);
		
		createData();
		table.setPreferredScrollableViewportSize(new Dimension(500, 200));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setEnabled(false);
		scrollPane.setViewportView(table);
		
		//back init
		JLabel back = new JLabel("\u56DE\u4E0A\u4E00\u9801");
		back.setHorizontalAlignment(SwingConstants.TRAILING);
		back.setFont(new Font("\u5fae\u8edf\u6b63\u9ed1\u9ad4", Font.PLAIN, 25));
		back.setBounds(487, 425, 286, 40);
		back.addMouseListener(new MouseAdapter() {//If get clicked Event, run the override method
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				
				mf.setUser(user);
				mf.setPassword(password);
				mf.setSocket(socket);
				mf.setSocketName(socket);
				mf.init();
				mf.setVisible(true);
			}
		});
		contentPane.add(back);
		
		//TimeLabel init
		JLabel timeLabel = new JLabel("");
		timeLabel.setFont(new Font("Dialog", Font.PLAIN, 30));
		timeLabel.setBounds(10, 87, 484, 43);
		panel.add(timeLabel);
		new TimerListener(timeLabel);
		
		//Generate the PDF Btm init
		Button generatePDF = new Button("\u751F\u6210PDF");
		generatePDF.setBackground(new Color(0,191,255));
		generatePDF.setActionCommand("pdf");
		generatePDF.setBounds(200, 421, 94, 44);
		//If get clicked Event, run the override method
		generatePDF.addActionListener(e -> {
			try {
				//Set the Font
				BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\kaiu.ttf", "Identity-H", BaseFont.NOT_EMBEDDED);
				com.itextpdf.text.Font FontChinese = new com.itextpdf.text.Font(bfChinese, 12);
				PdfPCell cell;
				Document doc = new Document();

				//Set the output path
				PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("./TeamCz\u6210\u54e1\u6aa2\u8996.pdf"));
				TableHeader event = new TableHeader();

				writer.setPageEvent(event);//load the header event
				event.setHeader("TeamCz\u6210\u54e1\u6aa2\u8996");

				doc.open();
				PdfPTable pdfTable = new PdfPTable(table.getColumnCount());

				//Write the column
				for (int i = 0; i < table.getColumnCount(); i++) {
					cell = new PdfPCell();
					cell.addElement(new Paragraph(table.getColumnName(i), FontChinese));
					pdfTable.addCell(cell);
				}

				//Write the data to column and row
				for (int rows = 0; rows < table.getRowCount(); rows++) {
					for (int cols = 0; cols < table.getColumnCount(); cols++) {
						cell = new PdfPCell();
						cell.addElement(new Paragraph(table.getModel().getValueAt(rows, cols).toString(), FontChinese));
						pdfTable.addCell(cell);
					}
				}

				doc.add(pdfTable);
				doc.close();
				System.out.println("Column:" + table.getColumnCount());
				System.out.println("Rows:" + table.getRowCount());
				System.out.println("Done");
				JOptionPane.showMessageDialog(null, "pdf\u6a94\u6848\u5df2\u8f38\u51fa\u5b8c\u7562");
				message(dateFormatter.format(new Date())+ "\t" + name + " \u5df2\u751f\u6210\u6210\u54e1PDF\u6a94 ~[console]");
			} catch (DocumentException | IOException ex) {
				ex.printStackTrace();
			}
		});
		
		contentPane.add(generatePDF);
	}
}

class TableHeader extends PdfPageEventHelper {

    String header;

    PdfTemplate total;
    /**
     * set the PDF Header
     * @param header The PDF Header
     */
    public void setHeader(String header) {
        this.header = header;
    }
    /**
     * load page
     * 
     * @param writer PdfWriter
     * @param document Doc
     */
    public void onEndPage(PdfWriter writer, Document document) {
    	BaseFont bfChinese = null;
    	PdfPCell cell = new PdfPCell();
		try {
			bfChinese = BaseFont.createFont("c:\\windows\\fonts\\kaiu.ttf", "Identity-H", BaseFont.NOT_EMBEDDED);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
		com.itextpdf.text.Font FontChinese = new com.itextpdf.text.Font(bfChinese, 12);
        PdfPTable table = new PdfPTable(3);
        try {
            table.setWidths(new int[]{24, 24, 2});
            table.setTotalWidth(527);
            table.setLockedWidth(true);
            table.getDefaultCell().setFixedHeight(20);
            cell.addElement(new Paragraph(header, FontChinese));
            table.addCell(cell);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(String.format("Page %d of", writer.getPageNumber()));
            cell = new PdfPCell(Image.getInstance(total));
            table.addCell(cell);
            table.writeSelectedRows(0, -1, 34, 830, writer.getDirectContent());
        } catch(DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
    /**
     * Set the total count
     * @param writer PdfWriter
     * @param document Doc
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }
    /**
     * Show text aligned
     * @param writer PdfWriter
     * @param document Doc
     */
    public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber())),
                2, 2, 0);
    }
}