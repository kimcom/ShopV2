<?php
class Fn {
	public static function redirectToMain() {
		$host = 'http://' . $_SERVER['HTTP_HOST'] . '/';
		header('Location:' . $host);
	}
	
	public static function redirectToController($ControllerName) {
		$host = 'http://' . $_SERVER['HTTP_HOST'] . '/';
		header('Location:' . $host . $ControllerName);
	}

	public static function checkErrorMySQLi($mysqli) {
		if ($mysqli->errno) {
//			echo 'mysqli error: ' . $mysqli->errno . ' ' . $mysqli->error.'<br>';
			Fn::ErrorToLog('mysqli error: ' . $mysqli->errno, $mysqli->error);
			return false;
		}
		return true;
	}

	public static function checkErrorMySQLcnn($mysqli) {
		if ($mysqli->connect_errno) {
			Fn::ErrorToLog('mysqli error: ' . $mysqli->connect_errno, $mysqli->connect_error);
			return false;
		}
		return true;
	}

	public static function checkErrorMySQLstmt($stmt) {
		if ($stmt->errorCode() > 0) {
			$ar = $stmt->errorInfo();
			Fn::ErrorToLog('stmt error '.$stmt->errorCode(), $ar[1].' '.$ar[2]);
			return false;
		}
		return true;
	}

	public static function writeToLog($ssql) {
		Fn::ErrorToLog('mysql', $ssql . "\n\t\t" . mysql_errno() . ": " . mysql_error());
	}

	public static function debugToLog($Source, $Message) {
		$dir_name = 'Logs';
		$file_name = $dir_name . '/log_' . date('Y-m-d') . '.txt';
		if (!file_exists($dir_name))
			mkdir($dir_name, 0777);
		$fp = fopen($file_name, 'a');
		if (!$fp) {
			echo "Не могу открыть файл ($filename)";
			return;
		}
		if (fwrite($fp, date('Y-m-d H:i:s ') . $Source . ": " . $Message . "\r\n") === FALSE) {
			echo "Не могу произвести запись в файл ($filename)";
			exit;
		}
		fclose($fp);
	}

	public static function errorToLog($Source, $Message) {
//		echo '' . $Source . ': ' . $Message . '<br>';
		$dir_name = 'Logs';
		$file_name = $dir_name . '/error_' . date('Y-m-d') . '.txt';
		if (!file_exists($dir_name))
			mkdir($dir_name, 0777);
		$fp = fopen($file_name, 'a');
		if (!$fp) {
			echo "Не могу открыть файл ($filename)";
			return;
		}
//		$Source = mb_convert_encoding($Source, 'UTF-8');
//		$Message = mb_convert_encoding($Message, 'UTF-8');
		if (fwrite($fp, date('Y-m-d H:i: ') . $Source . ": " . $Message . "\r\n") === FALSE) {
			echo "Не могу произвести запись в файл ($filename)";
			exit;
		}
		fclose($fp);
	}

	public static function paramToLog() {
		ob_start();
		var_dump($_REQUEST);
		Fn::DebugToLog("param\n" . $_SERVER['SCRIPT_FILENAME'] . "\n" . $_SERVER['REQUEST_URI'] . "\n", ob_get_clean());
		ob_end_clean();
	}
	
	public static function nf($num) {
		return number_format($num, 2, '.', '');
	}

	public static function nfx($num, $count) {
		return number_format($num, $count, '.', '');
	}

	public static function nfx0($num, $count) {
		if ($num == 0) return '';
		return number_format($num, $count, '.', '');
	}
	
	public static function nfPendel($num) {
		if ($num == 0) return '';
		return number_format($num, 2, '.', ' ');
	}

	public static function nfPendelP($num) {
		if ($num == 0) return '';
		return number_format($num, 2, '.', ' ').' %';
	}
}
?>