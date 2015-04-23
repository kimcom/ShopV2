package main;

import com.jacob.activeX.*;
import com.jacob.com.*;

public class MyEKKA2 extends Dispatch{
	final ConfigReader conf = ConfigReader.getInstance();
	public MyEKKA2(int t){
		System.out.println("EKKA_TYPE: "+conf.EKKA_TYPE);
		System.out.println("EKKA_PORT: "+conf.EKKA_PORT);
		System.out.println("EKKA_BAUD: "+conf.EKKA_BAUD);
	}
	public MyEKKA2() {
		if(1==1){
		ActiveXComponent ecr = new ActiveXComponent("ecrmini.t400");
		boolean bl;
		String wstr;
		wstr = "open_port;"+conf.EKKA_PORT+";"+conf.EKKA_BAUD+"";
		if(ecr.invoke("t400me", wstr).getBoolean()){
			wstr = "get_last_event";
				System.out.println(wstr + ":" + ecr.invoke(wstr).toString());
			wstr = "get_last_result";
				System.out.println(wstr + ":" + ecr.invoke(wstr).toString());
			wstr = "get_last_error";
				System.out.println(wstr + ":" + ecr.invoke(wstr).toString());
			wstr = "get_error_info";
				System.out.println(wstr + ":" + ecr.invoke(wstr).toString());
			wstr = "close_port";
				System.out.println(wstr + ":" + ecr.invoke("t400me", wstr).getBoolean());
//			bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
//			System.out.println(wstr + ":" + bl);
		}else{
			System.out.println("Нет связи с фиск.регистратором");
		}
//		wstr = "open_port;22;115200;";
//		System.out.println(ecr.invoke("t400me",wstr).toString());
//		System.out.println(wstr);
//		String ostr = "";
		//wstr = "get_date_time";
		//Dispatch dp = Dispatch.call(ecr, "get_last_error", ostr).toDispatch();
		//System.out.println(ecr.getProperty(wstr).toString());
		//System.out.println(bl+"	"+wstr);
		//Dispatch dp = Dispatch.get(ecr, "get_last_error").toDispatch();
		//System.out.println(dp.toString());

//		System.out.println(bl+"	"+wstr);
//		Dispatch dp = Dispatch.get(ecr, "t400me").toDispatch();
//		Object oSelection = ecr.getProperty("Last_result").toDispatch();
//		System.out.println(oSelection.toString());
//		Object oFont = Dispatch.get(oSelection, "Font").toDispatch();
//		Dispatch.put(oFont, "Bold", "1");
		
//		wstr = "show_error_flags;1";
//		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
//		System.out.println(bl + "	" + wstr);
/*
		wstr = "add_plu;1;0;1;0;0;0;1;100.00;111111;ТОВАР 1;100000;";
		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
		System.out.println(bl + "	" + wstr);
		
		wstr = "cashier_registration;1;0;";
		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
		System.out.println(bl + "	" + wstr);
		
		wstr = "open_receipt;0";
		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
		System.out.println(bl + "	" + wstr);
		
		wstr = "sale_plu;0;0;0;2.000;1";
		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
		System.out.println(bl + "	" + wstr);
		
		wstr = "pay;0;0";
		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
		System.out.println(bl + "	" + wstr);
		
		wstr = "execute_report;x1;12321;";
		bl = Dispatch.call(ecr, "t400me", wstr).toBoolean();
		System.out.println(bl + "	" + wstr);
*/		
//Variant v = Dispatch.get(ecr, "Get_last_result");
		//System.out.println(wstr + v.toString());
		//System.out.println(ecr.getProperty("get_last_result").toString());
		//System.out.println(Dispatch.get(ecr, "Get_error_info").toBoolean());
		//System.out.println(bl+"	"+wstr);
		
		//System.out.println(Dispatch.call(ecr, "t400me", "get_soft_version").toString());
		//System.out.println(Dispatch.call(ecr, "open_port","22;115200").toString());
		//System.out.println("wstr:"+wstr);
		}else{
			String strDir = "d:\\java\\";
			String strInputDoc = strDir + "file_in.doc";
			String strOutputDoc = strDir + "file_out.doc";
			String strOldText = "[label:import:1]";
			String strNewText
					= "I am some horribly long sentence, so long that [insert anything]";
			boolean isVisible = true;
			boolean isSaveOnExit = true;
			ActiveXComponent oWord = new ActiveXComponent("Word.Application");
			oWord.setProperty("Visible", new Variant(isVisible));
			Dispatch oDocuments = oWord.getProperty("Documents").toDispatch();
			Dispatch oDocument = Dispatch.call(oDocuments, "Open", strInputDoc).
					toDispatch();
			Dispatch oSelection = oWord.getProperty("Selection").toDispatch();
			Dispatch oFind = oWord.call(oSelection, "Find").toDispatch();
			Dispatch.put(oFind, "Text", strOldText);
			Dispatch.call(oFind, "Execute");
			Dispatch.put(oSelection, "Text", strNewText);
			Dispatch.call(oSelection, "MoveDown");
			Dispatch.put(oSelection, "Text",
					"nSo we got the next line including BR.n");

			Dispatch oFont = Dispatch.get(oSelection, "Font").toDispatch();
			Dispatch.put(oFont, "Bold", "1");
			Dispatch.put(oFont, "Italic", "1");
			Dispatch.put(oFont, "Underline", "0");

			Dispatch oAlign = Dispatch.get(oSelection, "ParagraphFormat").
					toDispatch();
			Dispatch.put(oAlign, "Alignment", "3");
			Dispatch oWordBasic = (Dispatch) Dispatch.call(oWord, "WordBasic").
					getDispatch();
			Dispatch.call(oWordBasic, "FileSaveAs", strOutputDoc);
			Dispatch.call(oDocument, "Close", new Variant(isSaveOnExit));
			oWord.invoke("Quit", new Variant[0]);		}		
	}
}
