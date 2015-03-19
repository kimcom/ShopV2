<?php
class Controller_Recovery extends Controller {
	function action_index() {
		$this->view->generate('view_recovery.php', 'view_template.php');
	}
	function action_send() {
		$this->model = new Model_Recovery();
		$data = $this->model->get_data();
		$this->view->generate('view_recovery.php', 'view_template.php', $data);
	}
}
?>
