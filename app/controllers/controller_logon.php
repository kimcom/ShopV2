<?php
class Controller_Logon extends Controller {
	function action_index() {
		$this->view->generate('view_logon.php', 'view_template.php');
	}
}
?>
