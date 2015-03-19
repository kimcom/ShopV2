<?php
class Controller_Helper extends Controller {
	function action_control() {
		$this->view->generate('view_helper_controls.php', 'view_template.php');
	}
	function action_info() {
		$this->view->generate('view_project_attr.php', 'view_template.php');
	}
}
?>
