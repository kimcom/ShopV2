<?php
class Controller_Lists extends Controller {
	function action_promo_tree() {
		$this->view->generate('view_list_promo_tree.php', 'view_template.php');
	}
	function action_promo_control_disc_complect() {
		$this->view->generate('view_promo_control_disc_complect.php', 'view_template.php');
	}
	function action_promo_control_price() {
		$this->view->generate('view_promo_control_price.php', 'view_template.php');
	}
	function action_promo_control_disc() {
		$this->view->generate('view_promo_control_disc.php', 'view_template.php');
	}
	function action_promo_control_disc_dop() {
		$this->view->generate('view_promo_control_disc_dop.php', 'view_template.php');
	}
	function action_promo_control_gift_complect() {
		$this->view->generate('view_promo_control_gift_complect.php', 'view_template.php');
	}
	function action_promo_control_gift() {
		$this->view->generate('view_promo_control_gift.php', 'view_template.php');
	}
	function action_promo_control_1plus1() {
		$this->view->generate('view_promo_control_1plus1.php', 'view_template.php');
	}
	function action_promo_control_fixsumma() {
		$this->view->generate('view_promo_control_fixsumma.php', 'view_template.php');
	}
	function action_points() {
		$this->view->generate('view_point_list.php', 'view_template.php');
	}
	function action_sellers() {
		$this->view->generate('view_seller_list.php', 'view_template.php');
	}
	function action_discountCards() {
		$this->view->generate('view_discountCard_list.php', 'view_template.php');
	}
	function action_get_promo_tree_NS() {
		$this->model = new Model_Lists();
		echo $this->model->get_promo_tree_NS();
	}
	function action_get_promo_tree_info() {
		$this->model = new Model_Lists();
		echo $this->model->get_promo_tree_info();
	}
	function action_get_promo_type() {
		$this->model = new Model_Lists();
		echo $this->model->get_promo_type();
	}
	function action_promo_tree_oper() {
		$this->model = new Model_Lists();
		echo $this->model->promo_tree_oper();
	}
	function action_get_user_list() {
		$this->model = new Model_Lists();
		echo $this->model->get_user_list();
	}
	function action_promo_save() {
		$this->model = new Model_Lists();
		echo $this->model->promo_save();
	}
	function action_get_promo_list() {
		$this->model = new Model_Lists();
		echo $this->model->get_promo_list();
	}
	function action_point_info() {
		$this->view->generate('view_point_attr.php', 'view_template.php');
	}
	function action_seller_info() {
		$this->view->generate('view_seller_attr.php', 'view_template.php');
	}
	function action_user_list() {
		$this->view->generate('view_user_list.php', 'view_template.php');
	}
	function action_user_info() {
		$this->view->generate('view_user_attr.php', 'view_template.php');
	}
	function action_promo_list() {
		$this->view->generate('view_promo_list.php', 'view_template.php');
	}
}
?>
