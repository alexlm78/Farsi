package com.alexlm78.farsi.db;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
public class OracleConn {

    public void ObtenerCSV()
    {
        Connection dbConn = null;
        Statement stStmt = null;
        ResultSet rsRess = null;
        FileWriter fArch = null;

        StringBuilder qry = new StringBuilder();
        qry.append("Select  X.*,");
        qry.append("        CASE WHEN X.TIPOCLIENTE in (1007, 3003) THEN 1 ELSE 0 END ExcepcionTipo,");
        qry.append("        CASE WHEN X.TIPOCLIENTE between 3001 AND 3007 THEN 1 ELSE 0 END ClienteXT2,");
        qry.append("        CASE WHEN X.BILL_CYCLE_ID in (8002, 8005) THEN 1 ELSE 0 END ClientesPlanesSinCompromisoXT,");
        qry.append("        Y.CODE Ciclo, Z.DESTINATION_STATE_ID, W.Name EstadoCuenta");
        qry.append(" FROM   (       ");
        qry.append("        Select  a.customer_id, a.customer_number, a.CUSTOMER_TYPE_ID TIPOCLIENTE, A.PAST_DUE_DEBT DEUDAVENCIDA, A.COLLECTIBLE_DEBT DEUDAGESTIONABLE, A.DUE_DEBT TOTALDEUDA,");
        qry.append("                CASE WHEN a.OLDEST_PAST_DUE_DEBT IS NULL THEN -999 ELSE ROUND(TRUNC(SYSDATE) - a.OLDEST_PAST_DUE_DEBT) END DIASMORA,");
        qry.append("                C.SGM_SEGMENT_ID, D.NAME, E.ACCOUNT_ID, E.BILL_CYCLE_ID, G.SERVICE_LINE_ID, H.CODE,");
        qry.append("                count(1) Cantidad,");
        qry.append("                count(CASE WHEN SERVICE_SUB_PLAN_ID in (2851098, 2851328, 2850948, 2851965, 2851436, 2881422, 2884423, 2885442, 2885445, 2885444, 2946423,");
        qry.append("                                                        2946426, 2946427, 2946428, 2946429, 2946430, 3002420, 3002413, 3002411) THEN 1 END ) ClientesPlanesSinCompromiso2,");
        qry.append("                count(CASE WHEN SERVICE_SUB_PLAN_ID in (3140411, 3146411, 3137412) THEN 1 END ) ClientesPlanesSinCompromiso3");
        qry.append("        From    TBL_RM_Customer A, TBL_RM_CUSTOMER_SEGMENT C, TBL_SGM_SEGMENTS D,");
        qry.append("                TBL_RM_ACCOUNT E, TBL_RM_SUBSCRIPTION F, TBL_RM_SERVICE_SUBSCRIPTION G, TBL_RM_SERVICE_LINE H");
        qry.append("        where   C.Customer_ID = A.Customer_ID");
        qry.append("                AND C.SGM_SEGMENT_ID = D.SGM_SEGMENT_ID");
        qry.append("                AND E.ACCOUNT_CODE = A.customer_number");
        qry.append("                AND F.BILLING_ACCOUNT_ID = E.ACCOUNT_ID");
        qry.append("                AND G.SUBSCRIPTION_ID = F.SUBSCRIPTION_ID");
        qry.append("                AND G.Service_Line_ID = 1056");
        qry.append("                AND H.Service_Line_ID = G.Service_Line_ID");
        qry.append("                AND G.SERVICE_SUB_STATUS_ID in (1010, 1008, 1012)");
        qry.append("        Group By a.customer_id, a.customer_number, a.CUSTOMER_TYPE_ID, A.PAST_DUE_DEBT, A.COLLECTIBLE_DEBT, A.DUE_DEBT,");
        qry.append("                CASE WHEN A.OLDEST_PAST_DUE_DEBT IS NULL THEN -999 ELSE ROUND(TRUNC(SYSDATE) - A.OLDEST_PAST_DUE_DEBT) END,");
        qry.append("                C.SGM_SEGMENT_ID, D.NAME, E.ACCOUNT_ID, E.BILL_CYCLE_ID, G.SERVICE_LINE_ID, H.CODE ) X, TBL_RM_BILL_CYCLE Y,");
        qry.append("                TBL_RM_TRANSITION_RECORD Z, TBL_RM_PROCESS_REGISTRY_STATE W");
        qry.append(" Where  Y.BILL_CYCLE_ID = X.BILL_CYCLE_ID");
        qry.append("        AND Z.INSTANCE_ID = X.Account_ID");
        qry.append("        AND W.STATE_ID = Z.DESTINATION_STATE_ID");

        try {
            dbConn = getConnection();
            stStmt = dbConn.createStatement();


                String sArch = "GT" +new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime()) + ".csv";
                if (new File(sArch).exists()) {
                    File aArch = new File(sArch);
                    aArch.delete();
                    aArch = null;
                    fArch = new FileWriter(sArch);
                } else {
                    fArch = new FileWriter(sArch);
                }

                rsRess = stStmt.executeQuery(qry.toString());

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


            return;
        }catch (ClassNotFoundException|SQLException| IOException ex) {
            ex.printStackTrace();
        }finally {
            try{ if (fArch != null) { fArch.close();fArch = null; } } catch (Exception e) {}
            try{ if (rsRess != null) { rsRess.close();rsRess = null; } } catch (Exception e) {}
            try{ if (stStmt != null) { stStmt.close();stStmt = null; } } catch (Exception e) {}
            try{ if (dbConn != null) { dbConn.close();dbConn = null; } } catch (Exception e) {}
        }
    }



    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection Conn = null;
        StringBuilder tns = new StringBuilder();
        tns.append("(DESCRIPTION=");
        tns.append("  (ADDRESS_LIST=");
        tns.append("    (ADDRESS=(PROTOCOL=TCP)(HOST=cen-reg-dbpc-04)(PORT=3875))");
        tns.append("    (ADDRESS=(PROTOCOL=TCP)(HOST=cen-reg-dbpc-05)(PORT=3875))");
        tns.append("    (ADDRESS=(PROTOCOL=TCP)(HOST=cen-reg-dbpc-06)(PORT=3875))");
        tns.append("  )");
        tns.append("  (CONNECT_DATA=");
        tns.append("    (SERVER=dedicated)");
        tns.append("    (SERVICE_NAME=SICCGTS)");
        tns.append("  )");
        tns.append(")");

        if (Conn == null){
            Class.forName("oracle.jdbc.OracleDriver");
            Conn = DriverManager.getConnection("jdbc:oracle:thin:@"+tns.toString(),"COLLECTIONS_PRD_GT", "B3G79z_x$");
        }

        return Conn;
    }
}
/*


 */