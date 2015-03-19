<?php
class Controller_Task extends Controller {
	function action_list() {
		$this->view->generate('view_task_list.php', 'view_template.php');
	}
	function action_info() {
		$this->view->generate('view_project_attr.php', 'view_template.php');
	}
}
?>
