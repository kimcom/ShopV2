package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {
    public static final String CURDIR  = Paths.get("").toAbsolutePath().toString();;
    private static final String CONF_FILE_NAME  = "res/shop.conf";
    private static final String MANIFEST_FILE_NAME  = "manifest.mf";
    private static ConfigReader instance   = null;
    public String       FORM_TITLE;
    public String       ICON_IMAGE;
    public String       SERVER_ADDRESS_1;
    public String       SERVER_ADDRESS_2;
    public String       SERVER_PORT;
    public String       SERVER_DB;
    public String       USER_NAME;
	public String		APP_VERSION;
    public int          RESET_CONFIG = 0;
    public int          MARKET_ID = 0;
    public int          TERMINAL_ID = 0;
    public int          EKKA_TYPE = 0;
    public String       EKKA_NAME = "";
    public String       EKKA_HOST = "";
    public int          EKKA_PORT = 0;
    public int          EKKA_BAUD = 0;
	public int			TIME_WAIT = 10;//in second
	public int			TIME_UPDATE = 1 * 60 * 60;//in second
	public int			TIME_UPDATE_START = 10 * 60;//in second
	public double		PAGE_WIDTH;
	public double		PAGE_HEIGHT;
	public double		STICKER_HEIGHT_CORRECT;
	public double		STICKER_PADDING_LEFT;
	public double		STICKER_PADDING_TOP;
	public double		PLANK_PADDING_LEFT;
	public double		PLANK_PADDING_TOP;
	public String		POS_ACTIVE;
	public String		POS_TYPE;
	public String		POS_COM_PORT;
	public String		POS_BAUD_RATE;
	public String		POS_MerchantIdx;

	private int getIntegerValue(Properties props, String paramName){
		int result = 0;
		try {
			String str = props.getProperty(paramName);
			if (str != null) {
				result = new Integer(str);
			}
		} catch (NumberFormatException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
		}
		return result;
	}
	private double getDoubleValue(Properties props, String paramName){
		double result = 0;
		try {
			String str = props.getProperty(paramName);
			if (str != null) {
				result = new Double(str);
			}
		} catch (NumberFormatException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
		}
		return result;
	}
	private void addInfoIntoConfig(String typeInfo){
		PrintWriter zzz = null;
		try {
			File fileConf = new File(CONF_FILE_NAME);
			zzz = new PrintWriter(new FileOutputStream(fileConf, true), true);
			if(typeInfo.equals("ценники")){
				//zzz.println("");
				zzz.println(";stickers");
				zzz.println("PAGE_WIDTH             = 595");
				zzz.println("PAGE_HEIGHT            = 842");
				zzz.println("STICKER_HEIGHT_CORRECT = 1"); //canon LBP-2900 харьков деревянко
				zzz.println("STICKER_PADDING_LEFT   = 30");//canon LBP-2900 харьков таврия
				zzz.println("STICKER_PADDING_TOP    = 32");
				zzz.println("PLANK_PADDING_LEFT     = 18");
				zzz.println("PLANK_PADDING_TOP      = 20");
			}
			if(typeInfo.equals("SERVERS")){
				//zzz.println("");
				zzz.println(";servers");
				zzz.println("SERVER_ADDRESS_1 = shopv2.priroda.pp.ua");
				zzz.println("SERVER_ADDRESS_2 = shopv2.priroda.pp.ua");
			}
			if(typeInfo.equals("POS")){
				//zzz.println("");
				zzz.println(";setting POS-terminal");
				zzz.println("POS_ACTIVE = 0");
				zzz.println("POS_TYPE = BPOS1");
				zzz.println("POS_COM_PORT = 0");
				zzz.println("POS_BAUD_RATE = 115200");
				zzz.println("POS_MerchantIdx = 0");
			}
			zzz.close();
		} catch (FileNotFoundException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
		}
	}
	private ConfigReader() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
		Properties props = new Properties();
		Package p = this.getClass().getPackage();
		APP_VERSION = p.getImplementationVersion();
		if (APP_VERSION == null) {
			//Properties propsMF = new Properties();
			File fileConfMF = new File(MANIFEST_FILE_NAME);
			if (!fileConfMF.exists()) {
				//throw new FileNotFoundException("Не найден файл манифеста:\n" + MANIFEST_FILE_NAME);
				DialogBoxs.viewMessage("Не найден файл манифеста:\n" + MANIFEST_FILE_NAME);
				return;
			}
			FileInputStream fileMF = new FileInputStream(fileConfMF);
			InputStreamReader inCharsMF = new InputStreamReader(fileMF, "UTF-8");
			props.load(inCharsMF);
			APP_VERSION = props.getProperty("Implementation-Version");
			RESET_CONFIG = Integer.parseInt(props.getProperty("Implementation-reset").toString());
		}

		reset_SERVER_ADDRESS();
		
        File fileConf = new File(CONF_FILE_NAME);
        if(!fileConf.exists()) {
			DialogBoxs.viewMessage("Не найден файл конфигурации:\n" + CONF_FILE_NAME);
			return;
		}
        FileInputStream file = new FileInputStream(fileConf);
        InputStreamReader inChars = new InputStreamReader(file,"UTF-8");
        props.load(inChars);
		
		String str = props.getProperty("PAGE_WIDTH");//проверим есть ли настройки для ценников
		if(str==null) {
			addInfoIntoConfig("ценники");
			file = new FileInputStream(fileConf);
			inChars = new InputStreamReader(file, "UTF-8");
			props.load(inChars);
		}
		str = props.getProperty("SERVER_ADDRESS_1");//проверим есть ли настройки для ценников
		if(str==null) {
			addInfoIntoConfig("SERVERS");
			file = new FileInputStream(fileConf);
			inChars = new InputStreamReader(file, "UTF-8");
			props.load(inChars);
		}
		str = props.getProperty("EKKA_NAME");//проверим есть ли настройки для ценников
		if(str==null) {
			addInfoIntoConfig("EKKA");
			file = new FileInputStream(fileConf);
			inChars = new InputStreamReader(file, "UTF-8");
			props.load(inChars);
		}
		str = props.getProperty("POS_TYPE");//проверим есть ли настройки для ценников
		if(str==null) {
			addInfoIntoConfig("POS");
			file = new FileInputStream(fileConf);
			inChars = new InputStreamReader(file, "UTF-8");
			props.load(inChars);
		}

		FORM_TITLE      = props.getProperty("FORM_TITLE");
        ICON_IMAGE      = props.getProperty("ICON_IMAGE");
        SERVER_ADDRESS_1= props.getProperty("SERVER_ADDRESS_1");
        SERVER_ADDRESS_2= props.getProperty("SERVER_ADDRESS_2");
        SERVER_PORT     = props.getProperty("SERVER_PORT");
        SERVER_DB       = props.getProperty("SERVER_DB");
        USER_NAME       = props.getProperty("USER_NAME");
		MARKET_ID		= getIntegerValue(props,"MARKET_ID");
		TERMINAL_ID		= getIntegerValue(props,"TERMINAL_ID");
		EKKA_TYPE		= getIntegerValue(props,"EKKA_TYPE");
		EKKA_PORT		= getIntegerValue(props,"EKKA_PORT");
		EKKA_BAUD		= getIntegerValue(props,"EKKA_BAUD");
		EKKA_NAME		= props.getProperty("EKKA_NAME");
		EKKA_HOST		= props.getProperty("EKKA_HOST");

		PAGE_WIDTH				= getDoubleValue(props, "PAGE_WIDTH");
		PAGE_HEIGHT				= getDoubleValue(props, "PAGE_HEIGHT");
		STICKER_HEIGHT_CORRECT	= getDoubleValue(props, "STICKER_HEIGHT_CORRECT");
		STICKER_PADDING_LEFT	= getDoubleValue(props, "STICKER_PADDING_LEFT");
		STICKER_PADDING_TOP		= getDoubleValue(props, "STICKER_PADDING_TOP");
		PLANK_PADDING_LEFT		= getDoubleValue(props, "PLANK_PADDING_LEFT");
		PLANK_PADDING_TOP		= getDoubleValue(props, "PLANK_PADDING_TOP");
		
		POS_ACTIVE				= props.getProperty("POS_ACTIVE");
		POS_TYPE				= props.getProperty("POS_TYPE");
		POS_COM_PORT			= props.getProperty("POS_COM_PORT");
		POS_BAUD_RATE			= props.getProperty("POS_BAUD_RATE");
		POS_MerchantIdx			= props.getProperty("POS_MerchantIdx");
		FORM_TITLE = FORM_TITLE + " v." + APP_VERSION;
	}

	private void reset_SERVER_ADDRESS() throws FileNotFoundException, UnsupportedEncodingException, IOException {
		if (RESET_CONFIG > 1000) return;
		StringBuilder builder = new StringBuilder();
		File fileConf = new File(CONF_FILE_NAME);
		if (!fileConf.exists()) {
			DialogBoxs.viewMessage("Не найден файл конфигурации:\n" + CONF_FILE_NAME);
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(fileConf));
		String line;
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("SERVER_ADDRESS") && !line.startsWith(";") && !line.equals(""))
				builder.append(line.concat("\r\n"));
			if (line.startsWith("SERVER_DB") || line.startsWith("TERMINAL_ID") || line.startsWith("PLANK_PADDING_TOP") || line.startsWith("EKKA_HOST") || line.startsWith("POS_MerchantIdx"))
				builder.append("\r\n");
		}
		FileWriter writer = new FileWriter(fileConf);
		writer.write(builder.toString());
		writer.close();
		reader.close();
	}
			
    public static ConfigReader getInstance() {
        if (instance == null) {
            try {
                instance = new ConfigReader();
            }catch (Exception e) {
				MyUtil.errorToLog(ConfigReader.class.getName(), e);
                instance = null;
                DialogBoxs.viewError(e);
            }
        }
        return instance;
    }
}
