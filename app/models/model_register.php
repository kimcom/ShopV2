<?php
class Model_Register extends Model {
	public function get_data() {
		if (!empty($_REQUEST['captcha'])) {
			if (empty($_SESSION['captcha']) || trim(strtolower($_REQUEST['captcha'])) != $_SESSION['captcha']) {
				$_SESSION['error_msg2'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
					. "ВНИМАНИЕ!<br><small>Неверно введен проверочный код!</small></h4>";
				return false;
			}
		}
		if (!empty($_REQUEST['pass'])){
			if ($_REQUEST['pass'] != $_REQUEST['repass']) {
				$_SESSION['error_msg1'] = "<h4 class='center list-group-item list-group-item-danger m0'>"
					. "ВНИМАНИЕ!<br><small>Неверно введен пароль!</small></h4>";
			return false;
			}
		}
		$cnn = new Cnn();
		return $cnn->registration(	$_REQUEST['login'], 
									$_REQUEST['email'], 
									$_REQUEST['pass'], 
									$_REQUEST['fio'],
									'',
									$_REQUEST['phone'],
									$_REQUEST['post']
		);
	}
}
?>
