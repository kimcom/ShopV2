package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class Updater implements Runnable {
	public boolean statusUpdate;
	public boolean statusUpgrade = true;
	private ConfigReader conf;
	private File file;
	private Download d;
	private String fileName;
	private final String UPDATE_PATH = "update/";
	private final String SITE_URL = "http://stat.priroda.pp.ua/shopv2/";
	
	public Updater() {
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
		//System.out.println("check update version");
        conf = ConfigReader.getInstance();
		statusUpdate = true;
		file = new File(UPDATE_PATH);
		//удаляем старые обновления
		deleteFolder(file);
		//создаем директорию "update"
		file = new File(UPDATE_PATH);
		if (!file.mkdirs()) {
			DialogBoxs.viewMessage("error create dir: " + file.getPath());
		}

		//качаем файл с инфой о программе
		fileName = "ver.txt";
		d = new Download(SITE_URL + fileName, UPDATE_PATH + fileName);
		if (d.getStatus()!=2) {
			statusUpdate = false;
			notifyTread();
			return;
		}
//		System.out.println("load file: " + fileName + " status: " + Download.STATUSES[d.getStatus()]);
		
		//перебираем файл с 2-ой строки и качаем указанные там файлы
		List<String> strings = readFile(UPDATE_PATH + fileName);
//		System.out.println("version app:" + conf.APP_VERSION + "|");
//		System.out.println("version new:" + strings.get(0)+"|");
		if (strings.get(0).equals(conf.APP_VERSION)) {
			statusUpgrade = false;
			file = new File(UPDATE_PATH);
			//удаляем закачаный файл
			deleteFolder(file);
			notifyTread();
			return;
		}
			
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
			//качаем указанный файл
			fileName = strings.get(i);
			d = new Download(SITE_URL + fileName, UPDATE_PATH + fileName);
			if (d.getStatus() != 2)	{
				statusUpdate = false;
				continue;
			}
//			System.out.println("load file: " + fileName + " status: " + Download.STATUSES[d.getStatus()]);
		}
		//копируем директорию update
		//copyFolder(new File(UPDATE_PATH),new File(targetPath));
		if (!statusUpdate) {
			JOptionPane.showMessageDialog(null, "Возникла ошибка при обновлении программы!\n\nСообщите программисту!", "ВНИМАНИЕ!", JOptionPane.ERROR_MESSAGE);
			notifyTread();
			return;
		}
		notifyTread();
		int i = JOptionPane.showConfirmDialog(null, 
				"Получено обновление программы\n\n"
			  + "Для завершения обновления\n"
			  + "нужно перезапустить программу.\n\n"
			  + "Обновить программу сейчас?", "ВНИМАНИЕ!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (i == 0) {
			try {
				Loader.clientSocketListener(Loader.PORTadmin, Loader.exitProgram);
				Runtime.getRuntime().exec("java -jar update/update.jar");
				System.exit(0);
			} catch (IOException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
				DialogBoxs.viewError(ex);
			}
		}
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
			MyUtil.errorToLog(Updater.class.getName(), ex);
		} catch (IOException ex) {
			//ex.printStackTrace();
			MyUtil.errorToLog(Updater.class.getName(), ex);
		} finally {
			try {
				reader.close();
			} catch (IOException ex) {
				//ex.printStackTrace();
				MyUtil.errorToLog(Updater.class.getName(), ex);
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
				//System.out.println("Directory copied from "	+ src + "  to " + dest);
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
				MyUtil.errorToLog(Updater.class.getName(), ex);
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
		}
	}
}
