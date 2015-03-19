<?php
class Mail {
	public static function smtpmail($mail_to, $fio, $subject, $message) {
		static $config = Array();
//настройки подключения к серверу
		$config['smtp_username'] = 'Администрация сайта ' . $_SESSION['company'];  //Смените на имя своего почтового ящика.
		$config['smtp_port'] = '25'; // Порт работы. Не меняйте, если не уверены.
		$config['smtp_host'] = '192.168.1.1';  //сервер для отправки почты(для наших клиентов менять не требуется)
		//$config['smtp_host'] = '1c.priroda.com.ua';  //сервер для отправки почты(для наших клиентов менять не требуется)
		$config['smtp_login'] = $_SESSION['siteEmail']; //Ваше имя - или имя Вашего сайта. Будет показывать при прочтении в поле "От кого"
		$config['smtp_password'] = 'x1234';  //Измените пароль
		$config['smtp_debug'] = true;  //Если Вы хотите видеть сообщения ошибок, укажите true вместо false
//		$config['smtp_charset'] = 'Windows-1251';   //кодировка сообщений. (или UTF-8, итд)
		$config['smtp_charset'] = 'UTF-8';   //кодировка сообщений. (или UTF-8, итд)
//формируем сообщение
		$SEND = "Date: " . date("D, d M Y H:i:s") . " UT\r\n";
		$SEND .= 'Subject: =?' . $config['smtp_charset'] . '?B?' . base64_encode($subject) . "=?=\r\n";
//		$headers =  "To: \"Administrator\" <$mail_to>\r\n".
//					"From: \"$replyto\" <$mail_from>\r\n".
//					"Reply-To: $replyto\r\n".
//					"Content-Type: text/$type; charset=\"$charset\"\r\n";
		$SEND .= "To: \"" . $fio . "\" <" . $mail_to . ">\r\n";
		$SEND .= "From: \"" . $config['smtp_username'] . "\" <" . $config['smtp_login'] . ">\r\n";
		$SEND .= "Reply-To: " . $config['smtp_login'] . "\r\n";
		$SEND .= "Sender: <" . $config['smtp_login'] . ">\r\n";
		$SEND .= "MIME-Version: 1.0\r\n";
		$SEND .= "Content-Type: text/plain; charset=\"" . $config['smtp_charset'] . "\"\r\n";
		$SEND .= "Content-Transfer-Encoding: 8bit\r\n";
		$SEND .= "X-Priority: 3";
		$SEND .= "X-Mailer: PHP/" . phpversion();
		$SEND .= $message . "\r\n";

		if (!$socket = fsockopen($config['smtp_host'], $config['smtp_port'], $errno, $errstr, 30)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", $errno . ":" . $errstr);
			return false;
		}

//$enc = mb_detect_encoding($errstr, mb_list_encodings(), true);
//echo $enc;
		if (!Mail::server_parse($socket, "220", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", $errno . ":" . $errstr);
			return false;
		}
		fputs($socket, "HELO " . $config['smtp_host'] . "\r\n");
		if (!Mail::server_parse($socket, "250", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Не могу отправить HELO!");
			fclose($socket);
			return false;
		}
		fputs($socket, "AUTH LOGIN\r\n");
		if (!Mail::server_parse($socket, "334", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Не могу найти ответ на запрос авторизаци.");
			fclose($socket);
			return false;
		}
		fputs($socket, base64_encode($config['smtp_login']) . "\r\n");
		if (!Mail::server_parse($socket, "334", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Логин авторизации не был принят сервером!");
			fclose($socket);
			return false;
		}
		fputs($socket, base64_encode($config['smtp_password']) . "\r\n");
		if (!Mail::server_parse($socket, "235", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Пароль не был принят сервером как верный! Ошибка авторизации!");
			fclose($socket);
			return false;
		}
		fputs($socket, "MAIL FROM: <" . $config['smtp_login'] . ">\r\n");
		if (!Mail::server_parse($socket, "250", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Не могу отправить комманду MAIL FROM:");
			fclose($socket);
			return false;
		}
		fputs($socket, "RCPT TO: <" . $mail_to . ">\r\n");
		if (!Mail::server_parse($socket, "250", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Не могу отправить комманду RCPT TO:");
			fclose($socket);
			return false;
		}
		fputs($socket, "DATA\r\n");
		if (!Mail::server_parse($socket, "354", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Не могу отправить комманду DATA");
			fclose($socket);
			return false;
		}
		fputs($socket, $SEND . "\r\n.\r\n");
		if (!Mail::server_parse($socket, "250", __LINE__)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", "Не смог отправить тело письма. Письмо не было отправленно!");
			fclose($socket);
			return false;
		}
		fputs($socket, "QUIT\r\n");
		fclose($socket);
		return TRUE;
	}
	private static function server_parse($socket, $response, $line = __LINE__) {
		while (substr($line, 3, 1) != ' ') {
			if (!($line = fgets($socket, 256))) {
				if ($config['smtp_debug'])
					Fn::errorToLog("email robot", base64_encode ($response) . "\r\n" . $line);
				return false;
			}
		}
		if (!(substr($line, 0, 3) == $response)) {
			if ($config['smtp_debug'])
				Fn::errorToLog("email robot", base64_encode ($response) . "\r\n" . $line);
			return false;
		}
		return true;
	}
}
?>
