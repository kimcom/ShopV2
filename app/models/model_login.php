<?php
class Model_Login extends Model {
	public function get_data() {
		$cnn = new Cnn();
		return $cnn->login($_REQUEST['login'], $_REQUEST['pass']);
	}
}
?>