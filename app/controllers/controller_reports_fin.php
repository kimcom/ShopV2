<?php
class Controller_Reports_fin extends Controller {
	function action_pendel() {
		$this->view->generate('view_reports_pendel.php', 'view_template.php');
	}

	function action_pendel_data() {
		$cnn = new Cnn();
		return $cnn->get_pendel_data();
	}

}
?>
