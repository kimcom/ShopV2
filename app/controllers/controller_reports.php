<?php
class Controller_Reports extends Controller {
	function action_report1() {
		$this->view->generate('view_reports_1.php', 'view_template.php');
	}
	function action_report2() {
		$this->view->generate('view_reports_2.php', 'view_template.php');
	}
	function action_report3() {
		$this->view->generate('view_reports_3.php', 'view_template.php');
	}
	function action_report4() {
		$this->view->generate('view_reports_4.php', 'view_template.php');
	}
	function action_report5() {
		$this->view->generate('view_reports_5.php', 'view_template.php');
	}

	function action_report1_data() {
		$this->model = new Model_Reports();
		echo $this->model->get_report1_data();
	}
	function action_report2_data() {
		$this->model = new Model_Reports();
		echo $this->model->get_report2_data();
	}
	function action_report3_data() {
		$this->model = new Model_Reports();
		echo $this->model->get_report2_data();
	}

	function action_report4_data() {
		$this->model = new Model_Reports();
		echo $this->model->get_report4_data();
	}
	function action_report5_data() {
		$this->model = new Model_Reports();
		echo $this->model->get_report5_data();
	}
	function action_jqgrid3() {
		$this->model = new Model_Reports();
		echo $this->model->get_jqgrid3_data();
	}
}
?>
