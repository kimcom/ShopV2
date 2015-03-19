<?php
class Controller_Project extends Controller {
	function action_list() {
		$this->view->generate('view_project_list.php', 'view_template.php');
	}
	function action_info() {
		$this->view->generate('view_project_attr.php', 'view_template.php');
	}
}
?>
