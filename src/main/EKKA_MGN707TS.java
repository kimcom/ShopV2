package main;

import db.ConnectionDb;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EKKA_MGN707TS {
	private final ConfigReader conf;
	private ConnectionDb cnn;
	private BigDecimal currentCheckID;
	private boolean online = false;
	private String hostname = "";
	private String json_response = "";
	private int fskMode = 0;
	private String err_code = "";
	private Map err = new HashMap< String, String>();

	public EKKA_MGN707TS() {
		this.conf = ConfigReader.getInstance();
		this.cnn = ConnectionDb.getInstance();
		hostname = conf.EKKA_HOST;
		fillErr();
		checkState();
	}

	private int sendPost(String cgi, String strEntity) {
		int result = 0;
		json_response = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost("http://" + hostname + cgi);
			StringEntity strent = new StringEntity(strEntity, "UTF-8");
			//httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setEntity(strent);
			CloseableHttpResponse response = httpclient.execute(httpPost);
			result = response.getStatusLine().getStatusCode();
			json_response = EntityUtils.toString(response.getEntity());
//System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			response.close();

		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		} finally {
			try {
				httpclient.close();
			} catch (IOException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
			}
		}
		return result;
	}
	private int sendGet(String cgi) {
		int result = 0;
		json_response = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpHost target = new HttpHost(hostname, 80, "http");
			HttpGet request = new HttpGet(cgi);
//System.out.println("Executing request " + request.getRequestLine() + " to " + target);
			CloseableHttpResponse response = httpclient.execute(target, request);
			result = response.getStatusLine().getStatusCode();
			json_response = EntityUtils.toString(response.getEntity());
//			System.out.println("send: " + cgi);
//			System.out.println("resp: " + response.getStatusLine());
//			System.out.println("mess: " + json_response);
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		} finally {
			try {
				httpclient.close();
			} catch (IOException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
			}
		}
		return result;
	}
	private boolean HttpDigestAuthenticationMGN707TS() {
		boolean relust = false;
		HttpHost target = new HttpHost(hostname, 80, "http");
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials("service", "751426"));
		CloseableHttpClient httpclient = HttpClients.custom()
				.setDefaultCredentialsProvider(credsProvider)
				.build();
		try {
			// Create AuthCache instance
			AuthCache authCache = new BasicAuthCache();
			// Generate DIGEST scheme object, initialize it and add it to the local
			// auth cache
			DigestScheme digestAuth = new DigestScheme();
			// Suppose we already know the realm name
			digestAuth.overrideParamter("realm", "HTROM");
			// Suppose we already know the expected nonce value
			digestAuth.overrideParamter("nonce", "whatever");
			authCache.put(target, digestAuth);

			// Add AuthCache to the execution context
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setAuthCache(authCache);

			HttpGet httpget = new HttpGet("http://" + hostname + "/cgi/proc/register");
//System.out.println("Executing request " + httpget.getRequestLine() + " to target " + target);
			CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
			if (response.getStatusLine().getStatusCode() == 200) {
				relust = true;
			}
//			System.out.println(response.getStatusLine());
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		} finally {
			try {
				httpclient.close();
			} catch (IOException ex) {
				MyUtil.errorToLog(this.getClass().getName(), ex);
			}
		}
		return relust;
	}
	private boolean checkState() {
		try {
			InetAddress inet = InetAddress.getByName(hostname);
			if (inet.isReachable(2000)) {
				if (sendGet("/cgi/state") == 401) {
					if (!HttpDigestAuthenticationMGN707TS()) {
						DialogBoxs.viewMessageError("Ошибка дайджест авторизации на регистраторе MGN707TS");
						return online;
					}
				}
//json_response = "{\"model\":\"MG N707TS\",\"name\":\"IC30800612\",\"serial\":\"IC30800612\",\"time\":1448453601,\"chkId\":13,\"JrnTime\":1448450374,\"currZ\":0,\"IsWrk\":1,\"FskMode\":1,\"CurrDI\":1,\"err\":[{\"e\":\"xEA\"}]}";
				JSONParser parser = new JSONParser();
				JSONObject jsonObj;
				jsonObj = (JSONObject) parser.parse(json_response);
				fskMode = Integer.parseInt(jsonObj.get("FskMode").toString());
//System.out.println(jsonObj.get("FskMode"));
				JSONArray ja = (JSONArray) jsonObj.get("err");
				if (ja.size() != 0) {
					JSONObject jo = (JSONObject) ja.get(0);
					err_code = jo.get("e").toString();
					if (err.get(err_code)==null) {
						online = true; // если на регистраторе ошибка: Document transfer error
					} else {
						//System.out.println(err.get(err_code));
						DialogBoxs.viewMessageError("Ошибка "+err_code+": "+err.get(err_code));
					}
				}
				if (fskMode == 1 && err_code.equals("")) {
					online = true;
				}
			} else {
				DialogBoxs.viewMessageError("Ошибка! Нет связи с регистратором MGN707TS");
				return online;
			}
		} catch (UnknownHostException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		} catch (IOException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		} catch (ParseException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
		}
		return online;
	}
	
	public void report(String reportType) {
		if (!online) return;
		String rep = "";
		if (reportType.equals("X1")) {
			rep = "10";
		} else if (reportType.equals("Z1")) {
			rep = "0";
		} else {
			return;
		}
		sendGet("/cgi/proc/printreport?"+rep);
	}
	public void in(String summa) {
		if (!online) return;
		JSONArray jar = new JSONArray();
		JSONObject joC1 = new JSONObject();
		JSONObject joC2 = new JSONObject();
		JSONObject joP1 = new JSONObject();
		JSONObject joP2 = new JSONObject();
		JSONObject joR = new JSONObject();
		joC2.put("cm", "Оператор № " + conf.TERMINAL_ID + "(" + conf.MARKET_ID + ")");
		joC1.put("C", joC2);
		jar.add(joC1);
		joP2.put("sum", summa);
		joP1.put("IO", joP2);
		jar.add(joP1);
		joR.put("IO", jar);
//System.out.println(joR.toJSONString());
		sendPost("/cgi/chk", joR.toJSONString());
	}
	public void out(String summa) {
		if (!online) return;
		in("-"+summa);
	}
	public void nullCheck() {
		if (!online) return;
		sendPost("/cgi/chk", "{}");
	}
	public void copyCheck() {
		if (!online) return;
		JSONArray jar = new JSONArray();
		JSONObject joR = new JSONObject();
		joR.put("L", jar);
		sendPost("/cgi/chk", joR.toJSONString());
	}

	public boolean printCheck(BigDecimal checkID, String typePay, BigDecimal returnIDFiscalNumber) {
		if (cnn == null) return false;
		this.currentCheckID = checkID;
		if (this.currentCheckID == null) {
			this.currentCheckID = cnn.currentCheckID;
		}
		if (!online){
			//DialogBoxs.viewMessageError("Ошибка! Нет связи с регистратором MGN707TS");
			return false;
		}
		ResultSet res = cnn.getCheckContent(currentCheckID);
		json_response = "";
		try {
			JSONArray jar = new JSONArray();
			JSONObject joC1 = new JSONObject();
			JSONObject joC2 = new JSONObject();
			JSONObject joID1 = new JSONObject();
			JSONObject joID2 = new JSONObject();
			JSONObject joP1 = new JSONObject();
			JSONObject joP2 = new JSONObject();
			JSONObject joR = new JSONObject();
			joC2.put("cm", "Оператор № "+conf.TERMINAL_ID+ "("+conf.MARKET_ID+")");
			joC1.put("C", joC2);
			jar.add(joC1);
			joID2.put("cm", "ID: "+currentCheckID.setScale(4, RoundingMode.HALF_UP).toPlainString());
			joID1.put("C", joID2);
			jar.add(joID1);
			while (res.next()) {
				String goodid = res.getString("GoodID").toString();
				//String weight = Integer.toString(res.getInt("weight"));
				String name = res.getString("Name").toString();
				name = name.replaceAll("  ", " ");
				String quantity = res.getBigDecimal("Quantity").setScale(3, RoundingMode.HALF_UP).abs().toString();
				String price = res.getBigDecimal("Price").setScale(2, RoundingMode.HALF_UP).abs().toString();
//String sum = res.getBigDecimal("Sum").setScale(2, RoundingMode.HALF_UP).abs().toString();
//System.out.println(""+goodid+"	"+name+"	"+quantity+" "+price+" "+sum);
				JSONObject joS1 = new JSONObject();
				JSONObject joS2 = new JSONObject();
				joS2.put("code", goodid);
				joS2.put("price", price);
				joS2.put("name", name);
				joS2.put("qty", quantity);
				joS1.put("S", joS2);
				jar.add(joS1);
			}
			if (typePay.equals("2")) joP2.put("no", "4");
			joP1.put("P", joP2);
			jar.add(joP1);

			if (returnIDFiscalNumber == null) {
				joR.put("F", jar);
			} else {
				joR.put("R", jar);
			}

//System.out.println(joR.toJSONString());
			if (sendPost("/cgi/chk", joR.toJSONString())!=0) {
				if (!json_response.equals("")) {
					JSONParser parser = new JSONParser();
					JSONObject jsonObj;
					jsonObj = (JSONObject) parser.parse(json_response);
					if (jsonObj.get("no") != null) {
						String numberCheck = jsonObj.get("no").toString();
						cnn.setCheckFiscalNumber(";"+numberCheck+";");
					}
				}
			}
		} catch (SQLException e) {
			MyUtil.errorToLog(this.getClass().getName(), e);
			DialogBoxs.viewError(e);
			return false;
		} catch (ParseException ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
			return false;
		}
		return true;
	}

	private void fillErr(){
		err.put("x01", "Цена не указана");
		err.put("x02", "Количество не указано");
		err.put("x03", "Отдел не указан");
		err.put("x04", "Группа не указана");
		err.put("x25", "Нет бумаги");
		err.put("x31", "Пользователь уже зарегистрирован");
		err.put("x32", "Неверный пароль");
		err.put("x33", "Неверный номер таблицы");
		err.put("x34", "Доступ к таблице запрещен");
		err.put("x35", "Умолчание не найдено");
		err.put("x36", "Неверный индекс");
		err.put("x37", "Неверное поле");
		err.put("x38", "Таблица переполнена");
		err.put("x39", "Неверная длина двоичных данных");
		err.put("x3A", "Попытка модификации поля только для чтения");
		err.put("x3B", "Неверное значение поля");
		err.put("x3C", "Товар уже существует");
		err.put("x3D", "По товару были продажи");
		err.put("x3E", "Запрос запрещен");
		err.put("x3F", "Неверная закладка");
		err.put("x40", "Ключ не найден");
		err.put("x41", "Процедура уже исполняется");
		err.put("x42", "Количество товара отрицательно");
		err.put("x8B", "Нет бумаги");
		err.put("xA3", "Операция прекращена устройством");
		err.put("xA5", "Дневной отчет не найден");
		err.put("xA7", "MMC запрещено");
		err.put("xA8", "Неверен номер фискальной памяти");
		err.put("xA9", "Фискальная память не пуста");
		err.put("xBB", "Лента не пуста");
		err.put("xBC", "Режим тренировки");
		err.put("xBD", "Текущая дата неверна");
		err.put("xBE", "Запрещено изменение времени");
		err.put("xBF", "Истек сервисный таймер");
		err.put("xC0", "Ошибка работы с терминалом НСМЕП");
		err.put("xC1", "Неверный номер налога");
		err.put("xC2", "Неверный параметр у процедуры");
		err.put("xC3", "Режим фискального принтера не активен");
		err.put("xC4", "Изменялось название товара или его налог");
		err.put("xD1", "Сейф не закрыт");
		err.put("xD2", "Печать ленты прервана");
		err.put("xD3", "Достигнут конец текущей смены, или изменилась дата");
		err.put("xD4", "Не указано значение процентной скидки по умолчанию");
		err.put("xD5", "Не указано значение скидки по умолчанию");
		err.put("xD6", "Дневной отчет не выведен");
		err.put("xD7", "Дневной отчет уже выведен (и пуст)");
		err.put("xD8", "Нельзя отменить товар на который сделана скидка без ее предварительной отмены");
		err.put("xD9", "Товар не продавался в этом чеке");
		err.put("xDA", "Нечего отменять");
		err.put("xDB", "Отрицательная сумма продажи товара");
		err.put("xDC", "Неверный процент");
		err.put("xDD", "Нет ни одной продажи");
		err.put("xDE", "Скидки запрещены");
		err.put("xDF", "Неверная сумма платежа");
		err.put("xE0", "Тип оплаты не предполагает введения кода клиента");
		err.put("xE1", "Неверная сумма платежа");
		err.put("xE2", "Идет оплата чека");
		err.put("xE3", "Товар закончился");
		err.put("xE4", "Номер группы не может меняться");
		err.put("xE5", "Неверная группа");
		err.put("xE6", "Номер отдела не может меняться");
		err.put("xE7", "Неверный отдел");
		err.put("xE8", "Нулевое произведение количества на цену");
		err.put("xE9", "Переполнение внутренних сумм");
		err.put("xEA", "Дробное количество запрещено");
		err.put("xEB", "Неверное количество");
		err.put("xEC", "Цена не может быть изменена");
		err.put("xED", "Неверная цена");
		err.put("xEE", "Товар не существует");
		err.put("xEF", "Начат чек внесения-изъятия денег");
		err.put("xF0", "Чек содержит продажи");
		err.put("xF1", "Не существующий или запрещенный тип оплаты");
		err.put("xF2", "Поле в строке переполнено");
		err.put("xF3", "Отрицательная сумма по дневному отчету");
		err.put("xF4", "Отрицательная сумма по чеку");
		err.put("xF5", "Чек переполнен");
		err.put("xF6", "Дневной отчет переполнен");
		err.put("xF7", "Чек для копии не найден");
		err.put("xF8", "Оплата чека не завершена");
		err.put("xF9", "Кассир не зарегистрирован");
		err.put("xFA", "У кассира нет прав на эту операцию");
		err.put("xFB", "Нефискальный чек не открыт");
		err.put("xFC", "Чек не открыт");
		err.put("xFD", "Нефискальный чек уже открыт");
		err.put("xFE", "Чек уже открыт");
		err.put("xFF", "Переполнение ленты");
	}
}
