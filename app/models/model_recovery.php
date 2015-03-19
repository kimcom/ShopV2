<?php
class Model_Recovery extends Model {
	public function get_data() {
		$cnn = new Cnn();
		return $cnn->recovery($_REQUEST['email']);
	}
}
?>
