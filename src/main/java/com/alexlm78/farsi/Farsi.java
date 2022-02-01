package com.alexlm78.farsi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@Slf4j
public class Farsi {
	private ArrayList<String> fLista;
	private String ipConnection;
	private String usrConn;
	private String pasConn;
	
	public Farsi(ArrayList<String> lista)
	{
		this.fLista = lista;
		this.ipConnection = "172.20.5.100";
		this.usrConn = "PISAFTP";
		this.pasConn = "PISAFTP1";
	}
	
	public void ObtenerCSV()
	{
		Connection dbConn = null;
		Statement stStmt = null;
		ResultSet rsRess = null;
		FileWriter fArch = null;

		try {
			dbConn = getConnection();
			stStmt = dbConn.createStatement();

			for (int w = 0; w < this.fLista.size(); w++) {
				String sArch = (String)this.fLista.get(w) + ".csv";
				if (new File(sArch).exists()) {
					File aArch = new File(sArch);
					aArch.delete();
					aArch = null;
					fArch = new FileWriter(sArch);
				} else {
					fArch = new FileWriter(sArch);
				}

				rsRess = stStmt.executeQuery("SELECT * FROM " + (String)this.fLista.get(w));
				
				String cadena = "";
				int cols = rsRess.getMetaData().getColumnCount();
				int fila = 0;
				for (int i = 1; i <= cols; i++) {
					cadena = cadena + rsRess.getMetaData().getColumnName(i).trim().toUpperCase() + ",";
				}
				cadena = cadena.substring(0, cadena.length() - 1);
				fArch.write(cadena);
				fArch.write(System.getProperty("line.separator"));
				
				cadena = "";
				while (rsRess.next()) {
					for (int i = 1; i <= cols; i++) {
						cadena = cadena + (rsRess.getObject(i) != null ? "\"" + rsRess.getString(i).trim().replaceAll("\"", "") + "\"," : "\"\",");
					}
					cadena = cadena.substring(0, cadena.length() - 1);
					fArch.write(cadena);
					fArch.write(System.getProperty("line.separator"));
					cadena = "";
				}
				fArch.close();
			}

			return;
		}catch (ClassNotFoundException|SQLException|IOException ex) {
			ex.printStackTrace();
		}finally {
			try{ if (fArch != null) { fArch.close();fArch = null; } } catch (Exception e) {}
			try{ if (rsRess != null) { rsRess.close();rsRess = null; } } catch (Exception e) {}
			try{ if (stStmt != null) { stStmt.close();stStmt = null; } } catch (Exception e) {}
			try{ if (dbConn != null) { dbConn.close();dbConn = null; } } catch (Exception e) {}
		}
	}
	
	protected void ObtenerXLSX() {
		Connection dbConn = null;
		Statement stStmt = null;
		ResultSet rsRess = null;
		FileOutputStream foss = null;

		try {
			dbConn = getConnection();
			stStmt = dbConn.createStatement();

			for (int w = 0; w < this.fLista.size(); w++) {
				String sArch = (String)this.fLista.get(w) + ".xlsx";
				
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet();
				
				rsRess = stStmt.executeQuery("SELECT * FROM " + (String)this.fLista.get(w));
				
				int cols = rsRess.getMetaData().getColumnCount();
				int fila = 0;
				
				HSSFRow headerRow = sheet.createRow((short)fila);
				for (int i = 1; i <= cols; i++) {
					headerRow.createCell((short)i - 1).setCellValue(rsRess.getMetaData().getColumnName(i));
				}
				fila++;
				while (rsRess.next()) {
					HSSFRow F = sheet.createRow((short)fila);
					for (int i = 1; i <= cols; i++) {
						F.createCell((short)i - 1).setCellValue(rsRess.getObject(i) == null ? "" : rsRess.getObject(i).toString());
					}
					fila++;
				}
				for (int i = 1; i <= cols; i++) {
					sheet.autoSizeColumn(i);
				}
				File ff = new File(sArch);
				foss = new FileOutputStream(ff);
				wb.write(foss);
				wb.close();
				foss.close();
			}
			return;
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			try{ if (foss != null) { foss.close();foss = null; } } catch (Exception e) {}
			try{ if (rsRess != null) { rsRess.close();rsRess = null; } } catch (Exception e) {}
			try{ if (stStmt != null) { stStmt.close();stStmt = null; } } catch (Exception e) {}
			try{ if (dbConn != null) { dbConn.close();dbConn = null; } } catch (Exception e) {}
		}
	}
	
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName("com.ibm.as400.access.AS400JDBCDriver");
		conn = DriverManager.getConnection("jdbc:as400://" + this.ipConnection + "/;libraries=GUAV1,GUARDBV1,TFSOBMX1,PASO,QTEMP;prompt=false;naming=sql;errors=full", this.usrConn, this.pasConn);
		return conn;
	}
}
