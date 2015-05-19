<?php
class Controller_Reports_fin extends Controller {
	function action_pendel() {
		$this->view->generate('view_reports_pendel.php', 'view_template.php');
	}
	function action_pendel_dop() {
		include 'app/views/view_reports_pendel_dop.php';
	}
	function action_pendel2() {
		$this->view->generate('view_reports_pendel2.php', 'view_template.php');
	}

	function action_pendel_data_XXX() {
		$cnn = new Cnn();
		return $cnn->get_pendel_data();
	}

	function action_pendel_data2() {
		$cnn = new Cnn();
		return $cnn->get_pendel_data2();
	}

}
?>
