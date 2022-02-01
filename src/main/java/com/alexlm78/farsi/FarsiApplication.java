package com.alexlm78.farsi;

import com.alexlm78.farsi.db.OracleConn;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@SpringBootApplication
@Slf4j
public class FarsiApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(FarsiApplication.class, args);
    }
    @Override
    public void run( String... args) {
        fLista = new ArrayList<>();

        if (args.length != 0)
            log.error("No hay archivo para jalar, se debe pedir al menos uno");
        else
        {
            //parseParams(args);
            //Farsi rep = new Farsi(fLista);
            OracleConn rep = new OracleConn();

            /*if (getXLSX().booleanValue())
                rep.ObtenerXLSX();
            else*/
                rep.ObtenerCSV();
        }
    }

    private static ArrayList<String> fLista;
    private static Boolean XLSX = Boolean.FALSE;

    /*public static void parseParams(String[] params) {
        for (String param : params) {
            if (param.equalsIgnoreCase("-x"))
                setXLSX(Boolean.TRUE);
            else if (param.indexOf('/') > 0) {
                int indice = param.indexOf('/');
                fLista.add(param.substring(0, indice).toUpperCase() + "." + param.substring(indice + 1, param.length()).toUpperCase());
            } else if (param.indexOf('.') == -1)
                fLista.add("PASO." + param.toUpperCase());
            else
                fLista.add(param.toUpperCase());
        }
    }*/

    public static void setXLSX(Boolean xlsx)
    {  XLSX = xlsx;	}

    public static Boolean getXLSX()
    {  return XLSX;   }
}
