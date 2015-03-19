<?php
class Model_Register_Ok extends Model {
	public function get_data() {
		$cnn = new Cnn();
		return $cnn->registration_ok($_REQUEST['auth']);
	}
}
?>
