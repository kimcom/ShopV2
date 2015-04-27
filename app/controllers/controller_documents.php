<?php
class Controller_Documents extends Controller {
	function action_sale() {
		$this->view->generate('view_sale_list.php', 'view_template.php');
	}
    function action_check_list() {
		$this->view->generate('view_check_list.php', 'view_template.php');
	}
	function action_checkContent_list() {
		$this->view->generate('view_check_list.php', 'view_template.php');
	}
}

