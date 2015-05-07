<?php
class Controller_Register extends Controller {
	function __construct() {
		$this->model = new Model_Register();
		$this->view = new View();
	}
	function action_index() {
		$this->view->generate('view_register.php', 'view_template.php');
	}
	function action_check() {
		$data = $this->model->get_data();
		if ($data==false) {
			$this->view->generate('view_register.php', 'view_template.php');
		} else {
			Fn::redirectToController('register_ok');
		}
	}
        function action_user_cabinet() {
            $this->view->generate('view_user_cabinet.php', 'view_template.php');
        }

//    function user_cabinet() {
//        $data = $this->model->get_data();
//        if ($data == false) {
//            $this->view->generate('view_user_cabinet.php', 'view_template.php');
//        } else {
//            Fn::redirectToController('register_ok');
//        }
//    }

}
?>
