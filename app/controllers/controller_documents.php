<?php
class Controller_Documents extends Controller {
	function action_sale() {
		$this->view->generate('view_sale_list.php', 'view_template.php');
	}
}
?>
