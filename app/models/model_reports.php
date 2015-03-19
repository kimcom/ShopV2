<?php
class Model_Reports extends Model {
	public function get_report1_data() {
		$cnn = new Cnn();
		return $cnn->get_report1_data();
	}
	public function get_report2_data() {
		$cnn = new Cnn();
		return $cnn->get_report2_data();
	}
	public function get_report3_data() {
		$cnn = new Cnn();
		return $cnn->get_report2_data();
	}
	public function get_report4_data() {
		$cnn = new Cnn();
		return $cnn->get_report4_data();
	}
	public function get_report5_data() {
		$cnn = new Cnn();
		return $cnn->get_report5_data();
	}
	public function get_jqgrid3_data() {
		$cnn = new Cnn();
		return $cnn->get_jqgrid3();
	}
}
?>
