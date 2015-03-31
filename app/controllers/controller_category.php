<?php
class Controller_Category extends Controller {
	function action_var1() {
		$this->view->generate('view_category_var1.php', 'view_template.php');
	}
	function action_var2() {
		$this->view->generate('view_category_var2.php', 'view_template.php');
	}
	function action_var3() {
		$this->view->generate('view_category_var3.php', 'view_template.php');
	}
	
	function action_cat_partner3() {
		$this->view->generate('view_category_cat_partner3.php', 'view_template.php');
	}
	function action_get_tree_NS_cat_partner() {
		$this->model = new Model_Category();
		echo $this->model->get_tree_NS_cat_partner();
	}
	function action_cat_partner_tree_oper() {
		$this->model = new Model_Category();
		echo $this->model->cat_partner_tree_oper();
	}
	function action_partner_add_in_cat() {
		$cnn = new Cnni();
		return $cnn->add_in_cat();
	}

	function action_get_tree_NS() {
		$this->model = new Model_Category();
		echo $this->model->get_tree_NS();
	}
	function action_category_tree_oper() {
		$this->model = new Model_Category();
		echo $this->model->category_tree_oper();
	}
	function action_add_in_cat() {
		$this->model = new Model_Category();
		echo $this->model->add_in_cat();
	}
	function action_del_from_cat() {
		$this->model = new Model_Category();
		echo $this->model->del_from_cat();
	}

}
?>
