package main;

import db.ConnectionDb;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class UpdaterShopV1 implements Runnable {
	public boolean statusUpdate;
	public boolean statusUpgrade = true;
	private ConfigReader conf;
	private ConnectionDb cnn;
	private File file;
	private Download d;
	private String fileName;
	private String fileShopK = "SHOP_MasterZoo.ini";
	//private final String UPDATE_PATH = "d:/shop-k-T/";
	private final String UPDATE_PATH = "../shop-k/";
	private final String SITE_URL = "http://stat.priroda.pp.ua/shopv2/shop-k/";
	private final String DEL_URL = "http://stat.priroda.pp.ua/engine/deletefile?clientID=";
//	private final String SITE_URL = "http://stat-new.localhost/shopv2/shop-k/";
//	private final String DEL_URL = "http://stat-new.localhost/engine/deletefile?clientID=";
	
	public UpdaterShopV1() {
		Thread thread = new Thread(this);
		thread.start();
	}
	private void notifyTread(){
		synchronized (this){
			this.notifyAll();
		}
	}
	@Override
	public void run() {
        conf = ConfigReader.getInstance();
		cnn = ConnectionDb.getInstance();
	
		d = new Download(DEL_URL + cnn.clientID, UPDATE_PATH + cnn.clientID + ".html");
//		System.out.println(DEL_URL + cnn.clientID);
//		System.out.println(UPDATE_PATH + cnn.clientID+" d.getSize()="+d.getSize());
		
		if(!cnn.createFileForShopV1()){
			MyUtil.messageToLog("update_ShopV1", "ошибка генерации CSV файла");
			JOptionPane.showMessageDialog(null, "Возникла ошибка при генерации файла\nдля автономной программы!\n\nСообщите программисту!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			notifyTread();
			return;
		}
		statusUpdate = true;
		//file = new File(UPDATE_PATH);
		//удаляем старые обновления
		//deleteFolder(file);
		//notifyTread();
		//создаем директорию "update"
//		file = new File(UPDATE_PATH);
//		if (!file.mkdirs()) {
//			DialogBoxs.viewMessage("error create dir: " + file.getPath());
//		}
		//изменение настроек в файле конфигурации автономной программы ShopV1 на MSACCESS
//		MyUtil.replaceInFile(UPDATE_PATH + fileShopK,"POP3=tor.pp.ua", "POP3=ns.tor.pp.ua");

		//получим версию автономного клиента
		Properties props = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream(UPDATE_PATH + fileShopK);
			InputStreamReader inChars = new InputStreamReader(fis, "UTF-8");
			props.load(inChars);
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
		String verShopV1 = props.getProperty("Version");//версия авт.клиента
		
		//качаем файл с инфой о программе
		fileName = "ver.txt";
		d = new Download(SITE_URL + fileName, UPDATE_PATH + fileName);
		if (d.getStatus()!=2) {
			statusUpdate = false;
			notifyTread();
			return;
		}
		
		//перебираем файл и качаем указанные там файлы
		List<String> strings = readFile(UPDATE_PATH + fileName);
		MyUtil.messageToLog("update_ShopV1",verShopV1);
		MyUtil.messageToLog("update_ShopV1",strings.get(0));
//System.out.println("version app:" + verShopV1 + "|");
//System.out.println("version new:" + strings.get(0)+"|");
			
		for (int i = 1; i<strings.size(); i++) {
			//пустые строки пропускаем
			if (strings.get(i).length()==0) continue;
			//если начинается с ";" то пропускаем
			if (strings.get(i).startsWith(";")) continue;
			//если начинается с "dir:" - создаем каталог
			if (strings.get(i).startsWith("dir:")) {
				file = new File(strings.get(i).replaceFirst("dir:", ""));
				if (!file.mkdirs()) 
					DialogBoxs.viewMessage("error create dir: "+file.getPath());
				continue;
			}
			fileName = strings.get(i);
			if (fileName.equals("shop client.mdb")) {
				if (strings.get(0).equals(verShopV1)) continue; //если версия правильная - то не скачиваем
			}
			//качаем указанный файл
			d = new Download(SITE_URL + fileName, UPDATE_PATH + fileName);
//System.out.println(fileName + "	"+	d.getStatus());
			if (d.getStatus() != 2)	{
				MyUtil.messageToLog("update_ShopV1", "ошибка при скачивании файла: " + SITE_URL + fileName);
				statusUpdate = false;
				continue;
			}else if(d.getStatus()==2 && fileName.equals("shop client.mdb")){
				MyUtil.messageToLog("update_ShopV1", "файл скачан успешно: " + SITE_URL + fileName);
				//изменение настроек в файле конфигурации автономной программы ShopV1 на MSACCESS
				MyUtil.replaceInFile(UPDATE_PATH + fileShopK, verShopV1, strings.get(0));
			}
//			System.out.println("load file: " + fileName + " status: " + Download.STATUSES[d.getStatus()]);
		}
		//качаем файл c товарами и ценами нужной торговой точки
		fileName = "goods_"+cnn.clientID+".csv";
		d = new Download(SITE_URL + fileName, UPDATE_PATH + fileName);
		if (d.getStatus() != 2 || d.getSize() == 6228) {
			file = new File(UPDATE_PATH + fileName); 
			deleteFolder(file);
			MyUtil.messageToLog("update_ShopV1", "ошибка при скачивании файла: " + SITE_URL + fileName);
			JOptionPane.showMessageDialog(null, "Возникла ошибка при получении данных\nо товарах и ценах для автономной программы!\n\nСообщите программисту!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			notifyTread();
			return;
		}else{
			MyUtil.messageToLog("update_ShopV1", "файл скачан успешно: " + SITE_URL + fileName);
		}
		//качаем файл c дисконтными картами, если дата локального файла устарела
		fileName = "cards.csv";
		File file = new File(UPDATE_PATH + fileName);
		boolean bl = true;
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		Date dt1 = new Date(file.lastModified());
		Date dt2 = new Date();
		if (file.exists()){
			if (sdf.format(dt1).equals(sdf.format(dt2))) bl = false; //если дата файла равна текущей
		}
		if (bl){
			d = new Download(SITE_URL + fileName, UPDATE_PATH + fileName);
			if (d.getStatus() != 2 || d.getSize() == 6228) {
				file = new File(UPDATE_PATH + fileName);
				deleteFolder(file);
				MyUtil.messageToLog("update_ShopV1", "ошибка при скачивании файла: " + SITE_URL + fileName);
				JOptionPane.showMessageDialog(null, "Возникла ошибка при получении данных\nо дисконтных картах для автономной программы!\n\nСообщите программисту!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
				notifyTread();
				return;
			} else {
				MyUtil.messageToLog("update_ShopV1", "файл скачан успешно: " + SITE_URL + fileName);
			}
		}
		
		//копируем директорию update
		//copyFolder(new File(UPDATE_PATH),new File(targetPath));
		if (!statusUpdate) {
			JOptionPane.showMessageDialog(null, "Возникла ошибка при обновлении автономной программы!\n\nСообщите программисту!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			notifyTread();
			return;
		}
		notifyTread();
//		int i = JOptionPane.showConfirmDialog(null, 
//				"Получено обновление данных для автономной программы.\n\n"
//				, "ВНИМАНИЕ!", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
	}

	private static List readFile(String fileName){
		BufferedReader reader = null;
		List<String> strings = new ArrayList<String>();
		String line;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			while ((line = reader.readLine()) != null) {
				strings.add(line);
			}
		} catch (FileNotFoundException ex) {		
			//ex.printStackTrace();
			MyUtil.errorToLog(UpdaterShopV1.class.getName(), ex);
		} catch (IOException ex) {
			//ex.printStackTrace();
			MyUtil.errorToLog(UpdaterShopV1.class.getName(), ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				//ex.printStackTrace();
				MyUtil.errorToLog(UpdaterShopV1.class.getName(), ex);
			}
		}
		return strings;
	}
	private static void deleteFolder(File file) {
		if (file.isDirectory()) {
			//directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				//System.out.println("Directory is deleted : " + file.getAbsolutePath());
			} else {
				//list all the directory contents
				String files[] = file.list();
				for (String temp : files) {
					//construct the file structure
					File fileDelete = new File(file, temp);
					//recursive delete
					deleteFolder(fileDelete);
				}
				//check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					//System.out.println("Directory is deleted : " + file.getAbsolutePath());
				}
			}
		} else {
			//if file, then delete it
			file.delete();
			//System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}
	private static void copyFolder(File src, File dest) {
		if (src.isDirectory()) {
			//if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				System.out.println("Directory copied from "	+ src + "  to " + dest);
			}
			//list all the directory contents
			String files[] = src.list();
			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile, destFile);
			}
		} else {
			//if file, then copy it
			//Use bytes stream to support all file types
			try {
				Files.copy(src.toPath(), dest.toPath(), REPLACE_EXISTING);
			} catch (IOException ex) {
				//ex.printStackTrace();
				MyUtil.errorToLog(UpdaterShopV1.class.getName(), ex);
			}
			
/*
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			//copy the file content in bytes 
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
*/
			//System.out.println("File copied from " + src + " to " + dest);
			
/*
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			//copy the file content in bytes 
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
*/
			//System.out.println("File copied from " + src + " to " + dest);
		}
	}
}
