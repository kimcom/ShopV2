<?php
class Controller_Login extends Controller {
	function __construct() {
		$this->model = new Model_Login();
		$this->view = new View();
	}
	function action_index() {
		$data = $this->model->get_data();
		//if (!$data) return;
		//$this->view->generate('view_login.php', 'view_template.php', $data);
		//$this->view->generate('','view_login.php', $data);
		Fn::redirectToMain();
	}
	function action_logout() {
		$_SESSION['UserID'] = 0;
		$_SESSION['UserName'] = "";
		$_SESSION['UserEMail'] = "";
		$_SESSION['UserPost'] = "";
		$_SESSION['ClientID'] = null;
		$_SESSION['ClientName'] = "";
		$_SESSION['CompanyName'] = "";
		$_SESSION['AccessLevel'] = null;
		$_SESSION['access'] = false;
		Fn::redirectToMain();
	}
}
?>
