<?php
class Cnn {
	private $db = null;
	public function __construct() {
		try {
			$this->db = new PDO('mysql:host=localhost;dbname='.$_SESSION['dbname'], 'shop', '149521',array(1006));
			//$this->db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		} catch (PDOException $e) {
			Fn::errorToLog("PDO error!: ", $e->getMessage());
			die();
		}
	}
	
//user login and register
	public function login($login, $pass) {
		$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger'>ВНИМАНИЕ!<br><small>Неверно введен e-mail или пароль!</small></h4>";
		$stmt = $this->db->prepare("CALL pr_login_site('login', @id, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $login, PDO::PARAM_STR);
		$stmt->bindParam(2, $pass, PDO::PARAM_STR);
		$stmt->bindParam(3, $email, PDO::PARAM_STR);
		$stmt->bindParam(4, $fio, PDO::PARAM_STR);
		$stmt->bindParam(5, $phone, PDO::PARAM_STR);
		$stmt->bindParam(6, $company, PDO::PARAM_STR);
		$stmt->bindParam(7, $post, PDO::PARAM_STR);
		$stmt->bindParam(8, $codeauth, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt)) return false;
		$r = $stmt->fetch(PDO::FETCH_BOTH);
		if (!$r) return false;
		if ($stmt->rowCount()==0) return false;
//Fn::debugToLog("login", json_encode($r));
		if ($r[AccessLevel]==-1){
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger'>"
					. "ВНИМАНИЕ!<br><small>Вы не активировали Ваш аккаунт!<br>"
					. "Вход в систему возможен только после активации!</small></h4>";
			return false;
		}
		$_SESSION['UserID'] = $r[UserID];
		$_SESSION['UserName'] = $r[UserName];
		$_SESSION['UserEMail'] = $r[EMail];
		$_SESSION['UserPost'] = $r[Position];
		$_SESSION['ClientID'] = $r[ClientID];
		$_SESSION['ClientName'] = $r[ClientName];
		$_SESSION['CompanyName'] = $r[CompanyName];
		$_SESSION['AccessLevel'] = $r[AccessLevel];
		$_SESSION['access'] = true;
		$_SESSION['error_msg'] = "";
		return true;
	}
	public function registration($login, $email, $pass, $fio, $company, $phone, $post) {
		$codeauth = rand(1111111111, 9999999999);
		$company = "Сузирье™";
//проверяем валидность e-mail
		if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
				. "ВНИМАНИЕ!<br><small>Указан неверный e-mail!</small></h4>";
			return false;
		}
//вызов хранимой процедуры
		$stmt = $this->db->prepare("CALL pr_login_site('register', @id, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $login, PDO::PARAM_STR);
		$stmt->bindParam(2, $pass, PDO::PARAM_STR);
		$stmt->bindParam(3, $email, PDO::PARAM_STR);
		$stmt->bindParam(4, $fio, PDO::PARAM_STR);
		$stmt->bindParam(5, $phone, PDO::PARAM_STR);
		$stmt->bindParam(6, $company, PDO::PARAM_STR);
		$stmt->bindParam(7, $post, PDO::PARAM_STR);
		$stmt->bindParam(8, $codeauth, PDO::PARAM_STR);
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt)) {
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
					. "ВНИМАНИЕ!<br><small>Ошибка при регистрации пользователя!</small></h4>";
			return false;
		}
		$result = $stmt->fetch(PDO::FETCH_BOTH);
		if (!$result) return false;
		//echo 'result='.$result[0].'<br>';
		if ($result[0] < 0) {
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
				. "ВНИМАНИЕ!<br><small>Пользователь с указанным именем уже зарегистрирован!</small></h4>";
			return false;
		}
		if ($result[0] == 0) {
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
				. "ВНИМАНИЕ!<br><small>Не удалось зарегистрировать пользователя!</small></h4>";
			return false;
		}
		if ($result[0] > 0) {
//оправляем сообщение пользователю
			$subject = 'Регистрация аккаунта в инф. системе ';
			$message = "
Здравствуйте, ".$fio."!

Ваш email был зарегистрирован в информационной системе ".$_SESSION['company']."

Для полноценной работы в нашей системе Вам необходимо активировать 
Ваш аккаунт перейдя по ссылке: http://" . $_SERVER['HTTP_HOST'] . "/register_ok/activate?auth=" . $codeauth . "

После активации Вы сможете войти в информационную систему.

Если вы получили это сообщение по ошибке, не предпринимайте никаких действий. 

Если вы не нажмете эту ссылку, то адрес не будет добавлен в аккаунт.

Успехов!
------------------
admin@" . $_SERVER['HTTP_HOST'] . "
";
			$sended = Mail::smtpmail($email, $fio, $subject, $message);
			if (!$sended) {
				$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
						. "ВНИМАНИЕ!<br><small>При отправке сообщения по e-mail возникли проблемы!</small></h4>";
				return false;
			}
			$sended = Mail::smtpmail($_SESSION['adminEmail'], $fio, $subject, $message.'E-mail:'.$email);
			return true;
		}
	}
	public function registration_ok($codeauth) {
//вызов хранимой процедуры
		$stmt = $this->db->prepare("CALL pr_login_site('register_ok', @id, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $login, PDO::PARAM_STR);
		$stmt->bindParam(2, $pass, PDO::PARAM_STR);
		$stmt->bindParam(3, $email, PDO::PARAM_STR);
		$stmt->bindParam(4, $fio, PDO::PARAM_STR);
		$stmt->bindParam(5, $phone, PDO::PARAM_STR);
		$stmt->bindParam(6, $company, PDO::PARAM_STR);
		$stmt->bindParam(7, $post, PDO::PARAM_STR);
		$stmt->bindParam(8, $codeauth, PDO::PARAM_STR);
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt)) return false;
		$result = $stmt->fetch(PDO::FETCH_BOTH);
		if (!$result) return false;
		if ($result[0] == 0) {
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
				. "<br>ВНИМАНИЕ!<br><br><small>Возникла ошибка при активации аккаунта!<br><br>"
				. "Сообщите разработчику!<br><br></small></h4>";
			return false;
		}
		if ($result[0] > 0) {
//оправляем сообщение пользователю
			$email = $result['EMail'];
			$fio = $result[2];
			$subject = "Активация аккаунта успешно завершена!";
			$message = "
Добро пожаловать, " . $fio . "!

Ваш аккаунт был успешно активирован в информационной системе " . $_SESSION['company'] . "

Вы можете войти в систему по адресу http://" . $_SERVER['HTTP_HOST'] . "/logon

Если вы получили это сообщение по ошибке, не предпринимайте никаких действий. 

Успехов!
------------------
admin@" . $_SERVER['HTTP_HOST'] . "
";
			$sended = Mail::smtpmail($email, $fio, $subject, $message);
			if (!$sended) {
				$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
						. "ВНИМАНИЕ!<br><small>При отправке сообщения по e-mail возникли проблемы!</small></h4>";
				return false;
			}
			$sended = Mail::smtpmail($_SESSION['adminEmail'], $fio, $subject, $message.'E-mail:'. $email);
			return true;
		}
	}
	public function recovery($email) {
//вызов хранимой процедуры
		$stmt = $this->db->prepare("CALL pr_login_site('recovery', @id, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $login, PDO::PARAM_STR);
		$stmt->bindParam(2, $pass, PDO::PARAM_STR);
		$stmt->bindParam(3, $email, PDO::PARAM_STR);
		$stmt->bindParam(4, $fio, PDO::PARAM_STR);
		$stmt->bindParam(5, $phone, PDO::PARAM_STR);
		$stmt->bindParam(6, $company, PDO::PARAM_STR);
		$stmt->bindParam(7, $post, PDO::PARAM_STR);
		$stmt->bindParam(8, $codeauth, PDO::PARAM_STR);
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt)) return false;
		$result = $stmt->fetch(PDO::FETCH_BOTH);
		if (!$result) return false;
		if ($result[0] == 0) {
			$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
				. "<br>ВНИМАНИЕ!<br><br><small>E-mail ".$email." не найден!<br><br>"
				. "Возможно Вы неправильно указали e-mail!<br><br></small></h4>";
			return false;
		}
		if ($result[0] > 0) {
//оправляем сообщение пользователю
			$fio = $result[2];
			$subject = 'Восстановление пароля для доступа к информационной системе ' . $_SESSION['company'];
			$message = "
Здравствуйте, " . $fio . "!

Вы можете войти в систему по адресу http://" . $_SERVER['HTTP_HOST'] . "/logon

Ваш пароль: " . $result[1] . "

Если вы получили это сообщение по ошибке, не предпринимайте никаких действий. 

Успехов!
------------------
admin@" . $_SERVER['HTTP_HOST'] . "
";
			$sended = Mail::smtpmail($email, $fio, $subject, $message);
			if (!$sended) {
				$_SESSION['error_msg'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
						. "ВНИМАНИЕ!<br><small>При отправке сообщения по e-mail возникли проблемы!</small></h4>";
				return false;
			}
			$sended = Mail::smtpmail($_SESSION['adminEmail'], $fio, $subject, $message . 'E-mail:' . $email);
			return true;
		}
	}

//reports
	public function get_report1_data() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		//echo $DT_start.'<br>';
		if(isset($DT_start)){
			$dt = DateTime::createFromFormat('d?m?Y', $DT_start);
		//echo $dt->format('Ymd');
			$date1 = $dt->format('Ymd');
		}
		if (isset($DT_stop)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_stop);
			$date2 = $dt->format('Ymd');
		}
		//call pr_reports('avg_sum', @_id, '20141001', '20141031', '');
		$stmt = $this->db->prepare("CALL pr_reports('avg_sum', @id, ?, ?, null)");
		$stmt->bindParam(1, $date1, PDO::PARAM_STR);
		$stmt->bindParam(2, $date2, PDO::PARAM_STR);
		//$stmt->bindParam(3, '', PDO::PARAM_STR);
		//$stmt->bindParam(3, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
//		$r = $stmt->fetch(PDO::FETCH_BOTH);
//		if (!$r)
//			return false;
		if ($stmt->rowCount() == 0)
			return false;
		$response = new stdClass();
//		$response->draw = 1;
//		$response->recordsTotal = 28;
//		$response->recordsFiltered = 28;
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				$i = 0;
				foreach ($rowset as $row) {
					$response->data[$i] = array(
												$row['ClientID'],
												$row['NameShort'],
												$row['City'],
												$row['Avg_Sum'],
												$row['Avg_Check'],
												$row['DT_start'],
												$row['DT_stop'],
												$row['DayWork']
					);
					$i++;
				}
			}
		} while ($stmt->nextRowset());
		//header("Content-type: application/json;charset=utf8");
		return json_encode($response);
	}
	public function get_report2_data() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		//echo $DT_start.' '.  $DT_stop . '<br>';
		if(isset($DT_start)){
			$dt = DateTime::createFromFormat('d?m?Y', $DT_start);
		//echo $dt->format('Ymd').'<br>';
			$date1 = $dt->format('Ymd');
		}else{return;}
		if (isset($DT_stop)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_stop);
		//echo $dt->format('Ymd');
			$date2 = $dt->format('Ymd');
		}else{return;}
		//call pr_reports('avg_sum', @_id, '20141001', '20141031', '');
		$stmt = $this->db->prepare("CALL pr_reports('sale_Trixie', @id, ?, ?, null)");
		$stmt->bindParam(1, $date1, PDO::PARAM_STR);
		$stmt->bindParam(2, $date2, PDO::PARAM_STR);
		//$stmt->bindParam(3, "nnn", PDO::PARAM_STR);
		//$stmt->bindParam(3, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
//		$r = $stmt->fetch(PDO::FETCH_BOTH);
//		if (!$r)
//			return false;
		if ($stmt->rowCount() == 0)
			return false;
		$response = new stdClass();
//		$response->draw = 1;
//		$response->recordsTotal = 28;
//		$response->recordsFiltered = 28;
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				$i = 0;
				foreach ($rowset as $row) {
					$response->data[$i] = array(
												$row['PointName'],
												$row['SellerName'],
												$row['GoodArticle'],
												$row['GoodName'],
												$row['Quantity'],
												$row['Sebest'],
												$row['Oborot'],
												$row['Dohod'],
												$row['Percent'],
					);
					$i++;
				}
			}
		} while ($stmt->nextRowset());
		header("Content-type: application/json;charset=utf8");
		return json_encode($response);
	}
	public function get_report4_data() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
//Fn::debugToLog('report4 user:' . $_SESSION['UserName'], urldecode($_SERVER['QUERY_STRING']));
		//echo $DT_start.' '.  $DT_stop . '<br>';
		if (isset($DT_start)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_start);
			//echo $dt->format('Ymd').'<br>';
			$date1 = $dt->format('Ymd');
		} else {
			return;
		}
		if (isset($DT_stop)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_stop);
			//echo $dt->format('Ymd');
			$date2 = $dt->format('Ymd');
		} else {
			return;
		}
		//$url = 'ddd=1&'.urldecode($_SERVER['QUERY_STRING']);
		//call pr_reports('avg_sum', @_id, '20141001', '20141031', '');
		$stmt = $this->db->prepare("CALL pr_reports('sale', @id, ?, ?, ?)");
		$stmt->bindParam(1, $date1, PDO::PARAM_STR);
		$stmt->bindParam(2, $date2, PDO::PARAM_STR);
		//$stmt->bindParam(3, $url, PDO::PARAM_STR);
		$stmt->bindParam(3, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		header("Content-type: application/json;charset=utf8");
		$response = new stdClass();
		$response->page = 1;
		$response->total = 1;
		$response->records = 0;
		$response->error = '';
		if (!Fn::checkErrorMySQLstmt($stmt)) $response->error = $stmt->errorInfo();
//	Fn::debugToLog("resp", json_encode($response));
		if ($stmt->rowCount() > 0){
			$t = 0;
			do {
				$rowset = $stmt->fetchAll();
				if ($rowset!=null) {
					if($t==1){
						foreach ($rowset as $row) {
	//			Fn::debugToLog($t, $row[0]);
							$response->query = $row[0];
							$response->records = $row[1];
						}
					}else if ($t == 0) {
				//Fn::debugToLog("columnCount 2", $stmt->columnCount());
						$columnCount = $stmt->columnCount();
						$i = 0;
						foreach ($rowset as $row) {
							$response->rows[$i]['id'] = $row[0];
							$ar = array();
							for($f=0;$f<$columnCount-5;$f++){
								$ar[] = $row[$f];
							}
							$ar = array_pad($ar,10,null);
							for ($f = $columnCount - 5; $f < $columnCount; $f++) {
								$ar[] = $row[$f];
							}
							$response->rows[$i]['cell'] = $ar;
							$i++;
						}
					}
				}
				$t++;
			} while ($stmt->nextRowset());
		}
		return json_encode($response);
	}
	public function get_report5_data() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
Fn::debugToLog('report5 user:'.  $_SESSION['UserName'], urldecode($_SERVER['QUERY_STRING']));
//Fn::debugToLog('REQUEST_URI', urldecode($_SERVER['REQUEST_URI']));
		$stmt = $this->db->prepare("CALL pr_reports('goods', @id, ?, ?, ?)");
		$stmt->bindParam(1, $date1, PDO::PARAM_STR);
		$stmt->bindParam(2, $date2, PDO::PARAM_STR);
		$stmt->bindParam(3, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		header("Content-type: application/json;charset=utf8");
		$response = new stdClass();
		$response->page = 1;
		$response->total = 1;
		$response->records = 0;
		$response->query = "";
		$response->error = '';
		if (!Fn::checkErrorMySQLstmt($stmt)) $response->error = $stmt->errorInfo();
		//	Fn::debugToLog("resp", json_encode($response));
		if ($stmt->rowCount() > 0){
			$t = 0;
			do {
				$rowset = $stmt->fetchAll();
				if ($rowset != null) {
					if ($t == 1) {
						foreach ($rowset as $row) {
	//			Fn::debugToLog($t, $row[0]);
							$response->query = $row[0];
							$response->records = $row[1];
						}
					} else if ($t == 0) {
						//Fn::debugToLog("columnCount 2", $stmt->columnCount());
						$columnCount = $stmt->columnCount();
						$i = 0;
						foreach ($rowset as $row) {
							$response->rows[$i]['id'] = $row[0];
							$ar = array();
							for ($f = 0; $f < $columnCount - 7; $f++) {
								$ar[] = $row[$f];
							}
							$ar = array_pad($ar, 8, null);
							for ($f = $columnCount - 7; $f < $columnCount; $f++) {
								$ar[] = $row[$f];
							}
							$response->rows[$i]['cell'] = $ar;
							$i++;
						}
					}
				}
				$t++;
			} while ($stmt->nextRowset());
		}
		echo json_encode($response);
	}
	public function get_report7_data() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
Fn::debugToLog('report7 user:' . $_SESSION['UserName'], urldecode($_SERVER['QUERY_STRING']));
		//echo $DT_start.' '.  $DT_stop . '<br>';
		if (isset($DT_start)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_start);
			//echo $dt->format('Ymd').'<br>';
			$date1 = $dt->format('Ymd');
		} else {
			return;
		}
		if (isset($DT_stop)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_stop);
			//echo $dt->format('Ymd');
			$date2 = $dt->format('Ymd');
		} else {
			return;
		}
Fn::debugToLog('report', $date1.'	'.$date2);
//Fn::paramToLog();
		//$url = 'ddd=1&'.urldecode($_SERVER['QUERY_STRING']);
		//call pr_reports('avg_sum', @_id, '20141001', '20141031', '');
		$stmt = $this->db->prepare("CALL pr_reports('sale_opt', @id, ?, ?, ?)");
		$stmt->bindParam(1, $date1, PDO::PARAM_STR);
		$stmt->bindParam(2, $date2, PDO::PARAM_STR);
		//$stmt->bindParam(3, $url, PDO::PARAM_STR);
		$stmt->bindParam(3, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		header("Content-type: application/json;charset=utf8");
		$response = new stdClass();
		$response->page = 1;
		$response->total = 1;
		$response->records = 0;
		$response->error = '';
		if (!Fn::checkErrorMySQLstmt($stmt))
			$response->error = $stmt->errorInfo();
//	Fn::debugToLog("resp", json_encode($response));
		if ($stmt->rowCount() > 0) {
			$t = 0;
			do {
				$rowset = $stmt->fetchAll();
				if ($rowset != null) {
					if ($t == 1) {
						foreach ($rowset as $row) {
							//			Fn::debugToLog($t, $row[0]);
							$response->query = $row[0];
							$response->records = $row[1];
						}
					} else if ($t == 0) {
						//Fn::debugToLog("columnCount 2", $stmt->columnCount());
						$columnCount = $stmt->columnCount();
						$i = 0;
						foreach ($rowset as $row) {
							$response->rows[$i]['id'] = $row[0];
							$ar = array();
							for ($f = 0; $f < $columnCount - 5; $f++) {
								$ar[] = $row[$f];
							}
							$ar = array_pad($ar, 10, null);
							for ($f = $columnCount - 5; $f < $columnCount; $f++) {
								$ar[] = $row[$f];
							}
							$response->rows[$i]['cell'] = $ar;
							$i++;
						}
					}
				}
				$t++;
			} while ($stmt->nextRowset());
		}
		return json_encode($response);
	}
	public function get_pendel_data() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
Fn::debugToLog('pendel user:' . $_SESSION['UserName'], urldecode($_SERVER['QUERY_STRING']));
		//echo $DT_start.' '.  $DT_stop . '<br>';
		if (isset($DT_start)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_start);
			//echo $dt->format('Ymd').'<br>';
			$date1 = $dt->format('Ymd');
		} else {
			return;
		}
		if (isset($DT_stop)) {
			$dt = DateTime::createFromFormat('d?m?Y', $DT_stop);
			//echo $dt->format('Ymd');
			$date2 = $dt->format('Ymd');
		} else {
			return;
		}
Fn::debugToLog('pendel', $date1 . '	' . $date2);
//Fn::paramToLog();
		//$url = 'ddd=1&'.urldecode($_SERVER['QUERY_STRING']);
		//call pr_reports('avg_sum', @_id, '20141001', '20141031', '');
		$stmt = $this->db->prepare("CALL pr_reports_7('pendel', @id, ?, ?)");
		$stmt->bindParam(1, $date1, PDO::PARAM_STR);
		$stmt->bindParam(2, $date2, PDO::PARAM_STR);
		//$stmt->bindParam(3, $url, PDO::PARAM_STR);
		//$stmt->bindParam(3, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		header("Content-type: application/json;charset=utf8");
		$response = new stdClass();
		$response->page = 1;
		$response->total = 1;
		$response->records = 0;
		$response->error = '';
		if (!Fn::checkErrorMySQLstmt($stmt))
			$response->error = $stmt->errorInfo();
//	Fn::debugToLog("resp", json_encode($response));
		if ($stmt->rowCount() > 0) {
			$t = 0;
			do {
				$rowset = $stmt->fetchAll();
				if ($rowset != null) {
					if ($t == 1) {
						foreach ($rowset as $row) {
							//			Fn::debugToLog($t, $row[0]);
							$response->query = $row[0];
							$response->records = $row[1];
						}
					} else if ($t == 0) {
//Fn::debugToLog('pendel',json_encode($rowset));
						//Fn::debugToLog("columnCount 2", $stmt->columnCount());
						$columnCount = $stmt->columnCount();
						$i = 0;
						foreach ($rowset as $row) {
//							$response->rows[$i]['id'] = $row[0];
							$response->rows[$i]['field0'] = $row[0];
							$response->rows[$i]['field1'] = $row[1];
							$response->rows[$i]['field2'] = $row[2];
							$response->rows[$i]['field3'] = $row[3];
							$response->rows[$i]['field4'] = $row[4];
//							$response->rows[$i]['field5'] = $row[5];

//							$ar = array();
//							for ($f = 0; $f < $columnCount - 3; $f++) {
//								$ar[] = $row[$f];
//							}
////							$ar = array_pad($ar, 10, null);
////							for ($f = $columnCount - 5; $f < $columnCount; $f++) {
////								$ar[] = $row[$f];
////							}
//							$response->rows[$i]['cell'] = $ar;
							$i++;
						}
					}
				}
				$t++;
			} while ($stmt->nextRowset());
		}
Fn::debugToLog('pendel',json_encode($response));
		echo json_encode($response);
	}

//report setting
	public function set_report_setting() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_report_setting('set', @id, ?, ?, ?, ?)");
		$stmt->bindParam(1, $_SESSION['UserID'], PDO::PARAM_STR);
		$stmt->bindParam(2, $sid, PDO::PARAM_STR);
		$stmt->bindParam(3, $sname, PDO::PARAM_STR);
		$stmt->bindParam(4, urldecode($_SERVER['QUERY_STRING']), PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$result = false;
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				foreach ($rowset as $row) {
					$result = $row[0];
				}
			}
		} while ($stmt->nextRowset());
		echo $result;
	}
	public function get_report_setting_list() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_report_setting('get list', @id, ?, ?, ?, ?)");
		$stmt->bindParam(1, $_SESSION['UserID'], PDO::PARAM_STR);
		$stmt->bindParam(2, $sid, PDO::PARAM_STR);
		$stmt->bindParam(3, $sname, PDO::PARAM_STR);
		$stmt->bindParam(4, $url, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$result = false;
		//$response = new stdClass();
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				$i = 0;
				foreach ($rowset as $row) {
					$response[$i] = array('id' => $row['SettingName'], 
										  'text' => $row['SettingName']
					);
					$i++;
				}
			}
		} while ($stmt->nextRowset());
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
		return;
	}
	public function get_report_setting_byName() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
//		$url = urldecode($_SERVER['QUERY_STRING']);
		$stmt = $this->db->prepare("CALL pr_report_setting('get by name', @id, ?, ?, ?, ?)");
		$stmt->bindParam(1, $_SESSION['UserID'], PDO::PARAM_STR);
		$stmt->bindParam(2, $sid, PDO::PARAM_STR);
		$stmt->bindParam(3, $sname, PDO::PARAM_STR);
		$stmt->bindParam(4, $url, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$result = false;
		//$response = new stdClass();
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				foreach ($rowset as $row) {
					//Fn::debugToLog("get by name", explode("&", $row['Setting']));
					$response->Setting = $row['Setting'];
				}
			}
		} while ($stmt->nextRowset());
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
	}

//project
	public function project_info() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_project('info', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $projectid, PDO::PARAM_STR);
		$stmt->bindParam(2, $departmentID, PDO::PARAM_STR);
		$stmt->bindParam(3, $name, PDO::PARAM_STR);
		$stmt->bindParam(4, $description, PDO::PARAM_STR);
		$stmt->bindParam(5, $status, PDO::PARAM_STR);
		$stmt->bindParam(6, $userID_resp, PDO::PARAM_STR);
		$stmt->bindParam(7, $userID_create, PDO::PARAM_STR);
		$stmt->bindParam(8, $DT_plan, PDO::PARAM_STR);
		$stmt->bindParam(9, $DT_fact, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
		foreach ($rowset as $row) {
			break; //берем первую запись из результата
		}
		return $row;
	}
	public function project_save() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::paramToLog();
		if ($projectid == '')	$projectid = null;
		if ($unitID == '')		$unitID = null;
		if ($status == '')		$status = 0;
		if ($userID_resp == '')	$userID_resp = null;
		if(isset($DT_plan) && $DT_plan!=''){
			$DT_plan = DateTime::createFromFormat('d?m?Y', $DT_plan);
			$DT_plan = $DT_plan->format('Ymd');
		}
		if(isset($DT_fact) && $DT_fact!=''){
			$DT_fact = DateTime::createFromFormat('d?m?Y', $DT_fact);
			$DT_fact = $DT_fact->format('Ymd');
		}
//Fn::paramToLog();
		$stmt = $this->db->prepare("CALL pr_project('save', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $projectid, PDO::PARAM_STR);
		$stmt->bindParam(2, $unitID, PDO::PARAM_STR);
		$stmt->bindParam(3, $name, PDO::PARAM_STR);
		$stmt->bindParam(4, $description, PDO::PARAM_STR);
		$stmt->bindParam(5, $status, PDO::PARAM_STR);
		$stmt->bindParam(6, $userID_resp, PDO::PARAM_STR);
		$stmt->bindParam(7, $_SESSION['UserID'], PDO::PARAM_STR);
		$stmt->bindParam(8, $DT_plan, PDO::PARAM_STR);
		$stmt->bindParam(9, $DT_fact, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		$this->echo_response($stmt);
	}

//point
	public function point_info() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_point('info', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $clientID, PDO::PARAM_STR);
		$stmt->bindParam(2, $matrixID, PDO::PARAM_STR);
		$stmt->bindParam(3, $nameShort, PDO::PARAM_STR);
		$stmt->bindParam(4, $nameValid, PDO::PARAM_STR);
		$stmt->bindParam(5, $city, PDO::PARAM_STR);
		$stmt->bindParam(6, $address, PDO::PARAM_STR);
		$stmt->bindParam(7, $telephone, PDO::PARAM_STR);
		$stmt->bindParam(8, $countTerminal, PDO::PARAM_STR);
		$stmt->bindParam(9, $rD, PDO::PARAM_STR);
		$stmt->bindParam(10, $priceType, PDO::PARAM_STR);
		$stmt->bindParam(11, $appVersion, PDO::PARAM_STR);
		$stmt->bindParam(12, $statusID, PDO::PARAM_STR);
		$stmt->bindParam(13, $balanceActivity, PDO::PARAM_STR);
		$stmt->bindParam(14, $x1C, PDO::PARAM_STR);
		$stmt->bindParam(15, $label, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
		foreach ($rowset as $row) {
			break; //берем первую запись из результата
		}
		return $row;
	}
	public function point_save() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		if ($matrixID == '') $matrixID = null;
		if ($version == '')	$version = null;
		if ($priceType == '') $priceType = null;
		if ($balanceActivity == '') $balanceActivity = null;
		Fn::paramToLog();
		$stmt = $this->db->prepare("CALL pr_point('save', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $clientID, PDO::PARAM_STR);
		$stmt->bindParam(2, $matrixID, PDO::PARAM_STR);
		$stmt->bindParam(3, $nameShort, PDO::PARAM_STR);
		$stmt->bindParam(4, $nameValid, PDO::PARAM_STR);
		$stmt->bindParam(5, $city, PDO::PARAM_STR);
		$stmt->bindParam(6, $address, PDO::PARAM_STR);
		$stmt->bindParam(7, $telephone, PDO::PARAM_STR);
		$stmt->bindParam(8, $countTerminal, PDO::PARAM_STR);
		$stmt->bindParam(9, $rD, PDO::PARAM_STR);
		$stmt->bindParam(10, $priceType, PDO::PARAM_STR);
		$stmt->bindParam(11, $appVersion, PDO::PARAM_STR);
		$stmt->bindParam(12, $version, PDO::PARAM_STR);
		$stmt->bindParam(13, $balanceActivity, PDO::PARAM_STR);
		$stmt->bindParam(14, $x1C, PDO::PARAM_STR);
		$stmt->bindParam(15, $label, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		$this->echo_response($stmt);
	}

//task
	public function task_info() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_project_content('info', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $taskid, PDO::PARAM_STR);
		$stmt->bindParam(2, $projectid, PDO::PARAM_STR);
		$stmt->bindParam(3, $parentID, PDO::PARAM_STR);
		$stmt->bindParam(4, $status, PDO::PARAM_STR);
		$stmt->bindParam(5, $unitID, PDO::PARAM_STR);
		$stmt->bindParam(6, $name, PDO::PARAM_STR);
		$stmt->bindParam(7, $description, PDO::PARAM_STR);
		$stmt->bindParam(8, $userID_resp, PDO::PARAM_STR);
		$stmt->bindParam(9, $DT_plan, PDO::PARAM_STR);
		$stmt->bindParam(10, $DT_fact, PDO::PARAM_STR);
		$stmt->bindParam(11, $userID_create, PDO::PARAM_STR);
		$stmt->bindParam(12, $DT_create, PDO::PARAM_STR);
		$stmt->bindParam(13, $orderID, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
		foreach ($rowset as $row) {
			break; //берем первую запись из результата
		}
		return $row;
	}
	public function task_save() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		if ($taskid == '') $taskid = null;
		if ($projectid == '') $projectid = null;
		if ($parentID == '') $parentID = null;
		if ($status == '') $status = 0;
		if ($unitID == '') $unitID = null;
		if ($userID_resp == '')	$userID_resp = null;
		if ($orderID == '')	$orderID = null;
		if (isset($DT_plan) && $DT_plan != '') {
			$DT_plan = DateTime::createFromFormat('d?m?Y', $DT_plan);
			$DT_plan = $DT_plan->format('Ymd');
		}
		if (isset($DT_fact) && $DT_fact != '') {
			$DT_fact = DateTime::createFromFormat('d?m?Y', $DT_fact);
			$DT_fact = $DT_fact->format('Ymd');
		}
//Fn::paramToLog();
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_project_content('save', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $taskid, PDO::PARAM_STR);
		$stmt->bindParam(2, $projectid, PDO::PARAM_STR);
		$stmt->bindParam(3, $parentID, PDO::PARAM_STR);
		$stmt->bindParam(4, $status, PDO::PARAM_STR);
		$stmt->bindParam(5, $unitID, PDO::PARAM_STR);
		$stmt->bindParam(6, $name, PDO::PARAM_STR);
		$stmt->bindParam(7, $description, PDO::PARAM_STR);
		$stmt->bindParam(8, $userID_resp, PDO::PARAM_STR);
		$stmt->bindParam(9, $DT_plan, PDO::PARAM_STR);
		$stmt->bindParam(10, $DT_fact, PDO::PARAM_STR);
		$stmt->bindParam(11, $_SESSION['UserID'], PDO::PARAM_STR);
		$stmt->bindParam(12, $DT_create, PDO::PARAM_STR);
		$stmt->bindParam(13, $orderID, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		$this->echo_response($stmt);
	}

//discount cards 
	public function discountcard_info() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::paramToLog();
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_discountCard('info', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $cardid, PDO::PARAM_STR);
		$stmt->bindParam(2, $name, PDO::PARAM_STR);
		$stmt->bindParam(3, $dateOfIssue, PDO::PARAM_STR);
		$stmt->bindParam(4, $dateOfCancellation, PDO::PARAM_STR);
		$stmt->bindParam(5, $clientID, PDO::PARAM_STR);
		$stmt->bindParam(6, $address, PDO::PARAM_STR);
		$stmt->bindParam(7, $eMail, PDO::PARAM_STR);
		$stmt->bindParam(8, $phone, PDO::PARAM_STR);
		$stmt->bindParam(9, $animal, PDO::PARAM_STR);
		$stmt->bindParam(10, $startPercent, PDO::PARAM_STR);
		$stmt->bindParam(11, $startSum, PDO::PARAM_STR);
		$stmt->bindParam(12, $dopSum, PDO::PARAM_STR);
		$stmt->bindParam(13, $percentOfDiscount, PDO::PARAM_STR);
		$stmt->bindParam(14, $howWeLearn, PDO::PARAM_STR);
		$stmt->bindParam(15, $notes, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
		foreach ($rowset as $row) {
			break; //берем первую запись из результата
		}
		return $row;
	}
	public function discoundcard_save() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
//Fn::paramToLog();
		if ($clientID == '') $clientID = null;
		if ($startPercent == '') $startPercent = 0;
		if ($startSum == '') $startSum = 0;
		if ($dopSum == '') $dopSum = 0;
		if ($percentOfDiscount == '') $percentOfDiscount = 0;
		if (isset($dateOfIssue) && $dateOfIssue != '') {
			$dateOfIssue = DateTime::createFromFormat('d?m?Y', $dateOfIssue);
			$dateOfIssue = $dateOfIssue->format('Ymd');
		}
		if (isset($dateOfCancellation) && $dateOfCancellation != '') {
			$dateOfCancellation = DateTime::createFromFormat('d?m?Y', $dateOfCancellation);
			$dateOfCancellation = $dateOfCancellation->format('Ymd');
		}
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_discountCard('save', @id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $cardid, PDO::PARAM_STR);
		$stmt->bindParam(2, $name, PDO::PARAM_STR);
		$stmt->bindParam(3, $dateOfIssue, PDO::PARAM_STR);
		$stmt->bindParam(4, $dateOfCancellation, PDO::PARAM_STR);
		$stmt->bindParam(5, $clientID, PDO::PARAM_STR);
		$stmt->bindParam(6, $address, PDO::PARAM_STR);
		$stmt->bindParam(7, $eMail, PDO::PARAM_STR);
		$stmt->bindParam(8, $phone, PDO::PARAM_STR);
		$stmt->bindParam(9, $animal, PDO::PARAM_STR);
		$stmt->bindParam(10, $startPercent, PDO::PARAM_STR);
		$stmt->bindParam(11, $startSum, PDO::PARAM_STR);
		$stmt->bindParam(12, $dopSum, PDO::PARAM_STR);
		$stmt->bindParam(13, $percentOfDiscount, PDO::PARAM_STR);
		$stmt->bindParam(14, $howWeLearn, PDO::PARAM_STR);
		$stmt->bindParam(15, $notes, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		$this->echo_response($stmt);
	}

//sellers
	public function seller_info() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_seller('info', @id, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $sellerID, PDO::PARAM_STR);
		$stmt->bindParam(2, $clientID, PDO::PARAM_STR);
		$stmt->bindParam(3, $name, PDO::PARAM_STR);
		$stmt->bindParam(4, $post, PDO::PARAM_STR);
		$stmt->bindParam(5, $postID, PDO::PARAM_STR);
		$stmt->bindParam(6, $fired, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
		foreach ($rowset as $row) {
			break; //берем первую запись из результата
		}
		return $row;
	}
	public function seller_save() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::paramToLog();
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		if ($sellerID == '') $sellerID = null;
		if ($postID == '')	$postID = null;
		if ($clientID == '') $clientID = 0;
		$stmt = $this->db->prepare("CALL pr_seller('save', @id, ?, ?, ?, ?, ?, ?)");
		$stmt->bindParam(1, $sellerID, PDO::PARAM_STR);
		$stmt->bindParam(2, $clientID, PDO::PARAM_STR);
		$stmt->bindParam(3, $name, PDO::PARAM_STR);
		$stmt->bindParam(4, $post, PDO::PARAM_STR);
		$stmt->bindParam(5, $postID, PDO::PARAM_STR);
		$stmt->bindParam(6, $fired, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		$this->echo_response($stmt);
	}

//jqgrid
	public function get_jqgrid3() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$url = urldecode($_SERVER['QUERY_STRING']);
		$url = str_replace("field1", $f1, $url);
		$url = str_replace("field2", $f2, $url);
		$url = str_replace("field3", $f3, $url);
		$url = str_replace("field4", $f4, $url);
		$url = str_replace("field5", $f5, $url);
		$url = str_replace("field6", $f6, $url);
		$url = str_replace("field7", $f7, $url);
		$url = str_replace("field8", $f8, $url);
		$url = str_replace("field9", $f9, $url);
		$url = str_replace("field10", $f10, $url);
		$url = str_replace("field11", $f11, $url);
		$url = str_replace("field12", $f12, $url);
		$url = str_replace("field13", $f13, $url);
		$url = str_replace("field14", $f14, $url);
		$url = str_replace("field15", $f15, $url);

Fn::debugToLog('jqgrid3 action', $action);
Fn::debugToLog('jqgrid3 url', $url);
//Fn::paramToLog();

		$stmt = $this->db->prepare("CALL pr_jqgrid(?, @id, ?)");
		$stmt->bindParam(1, $action, PDO::PARAM_STR);
		$stmt->bindParam(2, $url, PDO::PARAM_STR);
	// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		$response = new stdClass();
		$response->page = 0;
		$response->total = 0;
		$response->records = 0;
		$r = 0;
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				if ($r == 1) {
					$i = 0;
					foreach ($rowset as $row) {
						$response->page = $row['_page'];
						$response->total = $row['_total_pages'];
						$response->records = $row['_rows_count'];
						$i++;
					}
				} else {
					$i = 0;
					$colCount = $stmt->columnCount();
					foreach ($rowset as $row) {
						$response->rows[$i]['id'] = $row[0];
						$response->rows[$i]['cell'] = array($row[$f1],
							$row[$f2],
							$row[$f3],
							$row[$f4],
							$row[$f5],
							$row[$f6],
							$row[$f7],
							$row[$f8],
							$row[$f9],
							$row[$f10],
							$row[$f11],
							$row[$f12],
							$row[$f13],
							$row[$f14],
							$row[$f15],
						);
//						$response->rows[$i]['cell'] = array_values($row);
						//$a1 = array_fill_keys(array_keys($row),$row);
						//$a1 = array_fill(0,$colCount-1,$row);
						//$a1 = array_fill(0,1,array_values($row));
//						if (1 == 0) {
//							ob_start();
//							var_dump($response->rows[$i]['cell']);
//							var_dump(array_values($row));
//							//Fn::DebugToLog("test param\n" . $_SERVER['SCRIPT_FILENAME'] . "\n" . $_SERVER['REQUEST_URI'] . "\n", ob_get_clean());
//							Fn::DebugToLog("тест а1", ob_get_clean());
//							ob_end_clean();
//						}
						$i++;
					}
				}
			}
			$r++;
		} while ($stmt->nextRowset());
		
//Fn::DebugToLog("тест jqgrid3", json_encode($response));
		header("Content-type: application/json;charset=utf8");
		echo json_encode($response);
}

//for select2
	public function select2() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
//Fn::paramToLog();
//Fn::debugToLog('QUERY_STRING', urldecode($_SERVER['QUERY_STRING']));
		$stmt = $this->db->prepare("CALL pr_select2(?, @id, ?, ?)");
		$stmt->bindParam(1, $action, PDO::PARAM_STR);
		$stmt->bindParam(2, $name, PDO::PARAM_STR);
		$stmt->bindParam(3, $type, PDO::PARAM_STR);
// вызов хранимой процедуры
		$stmt->execute();
		if (!Fn::checkErrorMySQLstmt($stmt))
			return false;
		//$response = new stdClass();
		do {
			$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
			if ($rowset) {
				$i = 0;
				foreach ($rowset as $row) {
					$response[$i] = array('id' => $row[0], 'text' => $row[1]);
					$i++;
				}
			}
		} while ($stmt->nextRowset());
//Fn::debugToLog("", json_encode($response));
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
		return;
	}

//category
	public function get_tree_NS_category() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		$result = Shop::GetCategoryTreeNS($this->dbi, $nodeid, $n_level, $n_left, $n_right);
		if ($nodeid > 0) {
			$n_level = $n_level + 1;
		} else {
			$n_level = 0;
		}
		$response->page = 1;
		$response->total = 1;
		$response->records = 1;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			if ($row['rgt'] == $row['lft'] + 1)
				$leaf = 'true';
			else
				$leaf = 'false';
			if ($n_level == $row['level']) { // we output only the needed level
				$response->rows[$i]['id'] = $row['CatID'];
				$response->rows[$i]['cell'] = array($row['CatID'],
					//$row['name'].' ('.$row['CatID'].')',
					$row['name'],
					$row['level'],
					$row['lft'],
					$row['rgt'],
					$leaf,
					'false'
				);
			}
			$i++;
		}
		header("Content-type: text/html;charset=utf-8");
		echo json_encode($response);
	}
	public function category_tree_oper() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		if ($oper == 'add') {
			$id = Shop::CreateNewElementTreeNS($this->dbi, 'category', $id, $parent_id, $name);
			if ($id == false) {
				$response->success = false;
				$response->message = 'Возникла ошибка при добавлении!<br>Сообщите разработчику!';
				$response->new_id = 0;
			} else {
				$response->success = true;
				$response->message = '';
				$response->new_id = $id;
			}
			echo json_encode($response);
		}
		if ($oper == 'edit') {
			$response->success = Shop::SetNewNameforElementTreeNS($this->dbi, 'category', $id, $name);
			$response->message = 'Возникла ошибка сохранения изменений!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'del') {
			//$response->success = DeleteElementTreeNS('category',$id);
			$response->success = Shop::MoveElementTreeNS($this->dbi, 'category', $id, 90);
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'copy') {
			//echo CopyTreeByID('category',$source,$target);
			echo Shop::CopyTreeNS($this->dbi, 'category', $source, $target);
		}
		if ($oper == 'move') {
			//echo SetParentIDforTree('category','CatID',$source,$target);
			echo Shop::MoveElementTreeNS($this->dbi, 'category', $source, $target);
		}
	}
	public function add_in_cat() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		echo Shop::AddToCategory($this->dbi, $cat_id, $source);
	}
	public function del_from_cat() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		echo Shop::DelFromCategory($this->dbi, $cat_id, $source);
	}

//test grid for print result
	function printResultSet(&$rowset, $i) {
		echo "Result set $i:<br>";
		foreach ($rowset as $row) {
			foreach ($row as $col) {
				echo $col . " | ";
			}
			echo '<br>';
		}
		echo '<br>';
	}
//
	private function echo_response($stmt) {
		$response = new stdClass();
		$response->new_id = 0;
		$response->success = Fn::checkErrorMySQLstmt($stmt);
		if ($response->success != true) {
			$response->message = 'Возникла ошибка при внесении информации!<br><br>Сообщите разработчику!';
			echo json_encode($response);
			return;
		}
		$rowset = $stmt->fetchAll(PDO::FETCH_BOTH);
//Fn::debugToLog("s", "1");
		foreach ($rowset as $row) {
//Fn::debugToLog("s", "2");
			if ($response->success)
//Fn::debugToLog("s", json_encode($row));
				$response->success = $row[0];
				$response->new_id = $row[1];
			break;
		}
		if ($response->success != true) {
			$response->message = 'Вы ничего не изменили!';
			echo json_encode($response);
		} else {
			$response->message = 'Информация успешно сохранена!';
			echo json_encode($response);
		}
		return;
	}
}
?>