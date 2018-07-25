package com.alexlm78.farsi;

import java.util.ArrayList;

public class Main
{
	private static ArrayList<String> fLista;
	private static Boolean XLSX = Boolean.valueOf(false);
	
	public static void parseParams(String[] params) {
		for (String param : params) {
			if (param.equalsIgnoreCase("-x"))
				setXLSX(Boolean.valueOf(true));
			else if (param.indexOf('/') > 0) {
				int indice = param.indexOf('/');
				fLista.add(param.substring(0, indice).toUpperCase() + "." + param.substring(indice + 1, param.length()).toUpperCase());
			} else if (param.indexOf('.') == -1)
				fLista.add("PASO." + param.toUpperCase());
			else
				fLista.add(param.toUpperCase());
		}
	}
	
	public static void setXLSX(Boolean xlsx)
	{  XLSX = xlsx;	}
	
	public static Boolean getXLSX()
	{  return XLSX;   }
	
	public static void main(String[] args)
	{
		fLista = new ArrayList();
		
		if (args.length == 0)
			System.err.println("No hay archivo para jalar, se debe pedir al menos uno");
		else
		{
			parseParams(args);
			Farsi rep = new Farsi(fLista);
			
			if (getXLSX().booleanValue())
				rep.ObtenerXLSX();
			else
				rep.ObtenerCSV();
		}
	}
}
