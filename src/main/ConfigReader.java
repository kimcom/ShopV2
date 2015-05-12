package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class ConfigReader {
    private static final String CONF_FILE_NAME  = "res/shop.conf";
    private static final String MANIFEST_FILE_NAME  = "manifest.mf";
    private static ConfigReader instance   = null;
    public String       FORM_TITLE;
    public String       ICON_IMAGE;
    public String       SERVER_ADDRESS;
    public String       SERVER_PORT;
    public String       SERVER_DB;
    public String       USER_NAME;
	public String		APP_VERSION;
    public int          MARKET_ID = 0;
    public int          TERMINAL_ID = 0;
    public int          EKKA_TYPE = 0;
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
				zzz.println("");
				zzz.println(";ценники");
				zzz.println("PAGE_WIDTH             = 595");
				zzz.println("PAGE_HEIGHT            = 842");
				zzz.println("STICKER_HEIGHT_CORRECT = 1"); //canon LBP-2900 харьков деревянко
				zzz.println("STICKER_PADDING_LEFT   = 30");//canon LBP-2900 харьков таврия
				zzz.println("STICKER_PADDING_TOP    = 32");
				zzz.println("PLANK_PADDING_LEFT     = 18");
				zzz.println("PLANK_PADDING_TOP      = 20");
			}
			zzz.close();
		} catch (FileNotFoundException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
		}
	}
	private ConfigReader() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        Properties props = new Properties();
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

		FORM_TITLE      = props.getProperty("FORM_TITLE");
        ICON_IMAGE      = props.getProperty("ICON_IMAGE");
        SERVER_ADDRESS  = props.getProperty("SERVER_ADDRESS");
        SERVER_PORT     = props.getProperty("SERVER_PORT");
        SERVER_DB       = props.getProperty("SERVER_DB");
        USER_NAME       = props.getProperty("USER_NAME");
		MARKET_ID		= getIntegerValue(props,"MARKET_ID");
		TERMINAL_ID		= getIntegerValue(props,"TERMINAL_ID");
		EKKA_TYPE		= getIntegerValue(props,"EKKA_TYPE");
		EKKA_PORT		= getIntegerValue(props,"EKKA_PORT");
		EKKA_BAUD		= getIntegerValue(props,"EKKA_BAUD");

		PAGE_WIDTH				= getDoubleValue(props, "PAGE_WIDTH");
		PAGE_HEIGHT				= getDoubleValue(props, "PAGE_HEIGHT");
		STICKER_HEIGHT_CORRECT	= getDoubleValue(props, "STICKER_HEIGHT_CORRECT");
		STICKER_PADDING_LEFT	= getDoubleValue(props, "STICKER_PADDING_LEFT");
		STICKER_PADDING_TOP		= getDoubleValue(props, "STICKER_PADDING_TOP");
		PLANK_PADDING_LEFT		= getDoubleValue(props, "PLANK_PADDING_LEFT");
		PLANK_PADDING_TOP		= getDoubleValue(props, "PLANK_PADDING_TOP");

		Package p = this.getClass().getPackage();
		APP_VERSION = p.getImplementationVersion();
		if(APP_VERSION == null) {
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
		}
		FORM_TITLE = FORM_TITLE + " v." + APP_VERSION;
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
