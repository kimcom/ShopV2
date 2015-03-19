<?php
class Controller_Register_Ok extends Controller {
	function action_index() {
		$this->view->generate('view_register_ok.php', 'view_template.php');
	}
	function action_activate() {
		$this->model = new Model_Register_Ok();
		$data = $this->model->get_data();
		$this->view->generate('view_register_ok.php', 'view_template.php', $data);
	}
}
?>
