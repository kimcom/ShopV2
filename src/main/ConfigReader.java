package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
	public int			TIME_WAIT = 10;//in second
	public int			TIME_UPDATE = 1 * 60 * 60;//in second
	public int			TIME_UPDATE_START = 10 * 60;//in second

    private ConfigReader() throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        Properties props = new Properties();
        File fileConf = new File(CONF_FILE_NAME);
        if(!fileConf.exists()) {
			//throw new FileNotFoundException("Не найден файл конфигурации:\n"+CONF_FILE_NAME);
			DialogBoxs.viewMessage("Не найден файл конфигурации:\n" + CONF_FILE_NAME);
			return;
		}
        FileInputStream file = new FileInputStream(fileConf);
        InputStreamReader inChars = new InputStreamReader(file,"UTF-8");
        props.load(inChars);
        //props.load(new FileInputStream(new File(CONF_FILE_NAME)));
        FORM_TITLE      = props.getProperty("FORM_TITLE");
        ICON_IMAGE      = props.getProperty("ICON_IMAGE");
        SERVER_ADDRESS  = props.getProperty("SERVER_ADDRESS");
        SERVER_PORT     = props.getProperty("SERVER_PORT");
        SERVER_DB       = props.getProperty("SERVER_DB");
        USER_NAME       = props.getProperty("USER_NAME");
        try {
			String str = props.getProperty("MARKET_ID");
			if (str != null)
				MARKET_ID = new Integer(str);
        } catch (NumberFormatException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
            MARKET_ID = 0;
        }
        try {
			String str = props.getProperty("TERMINAL_ID");
			if (str != null)
	            TERMINAL_ID = new Integer(str);
        } catch (NumberFormatException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
            TERMINAL_ID = 0;
        }            
        try {
			String str = props.getProperty("EKKA_TYPE");
			if (str!=null)
				EKKA_TYPE = new Integer(str);
        } catch (NumberFormatException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
            EKKA_TYPE = 0;
        }            

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
