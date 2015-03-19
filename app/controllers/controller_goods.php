<?php
class Controller_Goods extends Controller {
	function action_index() {
		$this->view->generate('view_goods_list.php', 'view_template.php');
	}
	function action_good_edit() {
		$this->view->generate('view_goods_attr.php', 'view_template.php');
	}
	function action_good_balance() {
		$this->view->generate('view_goods_balance.php', 'view_template.php');
	}
	function action_barcodes() {
		$this->view->generate('view_goods_barcodes.php', 'view_template.php');
	}
	function action_without_barcodes() {
		$this->view->generate('view_goods_without_barcodes.php', 'view_template.php');
	}
	function action_barcode_verify() {
		$this->view->generate('view_goods_barcode_verify.php', 'view_template.php');
	}
	function action_list_w_cost() {
		$this->view->generate('view_goods_list_w_cost.php', 'view_template.php');
	}
	function action_list() {
		$this->model = new Model_Goods();
		echo $this->model->get_data();
		if (1 == 0) {
			ob_start();
			var_dump($_REQUEST);
			Fn::DebugToLog("test param\n" . $_SERVER['SCRIPT_FILENAME'] . "\n" . $_SERVER['REQUEST_URI'] . "\n", ob_get_clean());
			ob_end_clean();
		}
	}
	function action_good_info_save() {
		$this->model = new Model_Goods();
		echo $this->model->good_info_save();
	}
	function action_synchro_info_save() {
		$this->model = new Model_Goods();
		echo $this->model->synchro_info_save();
	}
	function action_good_delete() {
		$this->model = new Model_Goods();
		echo $this->model->good_delete();
	}
	function action_good_barcode() {
		$this->model = new Model_Goods();
		echo $this->model->get_good_barcode_list();
	}
	function action_barcode_edit() {
		$this->model = new Model_Goods();
		echo $this->model->get_barcode_edit();
	}
	function action_goods_add2_in_promo() {
		$this->model = new Model_Goods();
		echo $this->model->goods_add2_in_promo();
	}
	function action_edit_price() {
		$this->model = new Model_Goods();
		echo $this->model->edit_price();
	}
	function action_del_goods_from_promo() {
		$this->model = new Model_Goods();
		echo $this->model->del_goods_from_promo();
	}
	function action_del_goods_from_promo_action() {
		$this->model = new Model_Goods();
		echo $this->model->del_goods_from_promo_action();
	}
	function action_goods_add_in_promo() {
		$this->model = new Model_Goods();
		echo $this->model->goods_add_in_promo();
	}
	function action_goods_add_in_promo_action() {
		$this->model = new Model_Goods();
		echo $this->model->goods_add_in_promo_action();
	}
	function action_get_cats_list() {
		$this->model = new Model_Goods();
		echo $this->model->get_cats_list();
	}
	function action_del_cats_from_promo() {
		$this->model = new Model_Goods();
		echo $this->model->del_cats_from_promo();
	}
	function action_cats_add_in_promo() {
		$this->model = new Model_Goods();
		echo $this->model->cats_add_in_promo();
	}
	function action_cats_add_in_promo2() {
		$this->model = new Model_Goods();
		echo $this->model->cats_add_in_promo2();
	}
	function action_edit_discount() {
		$this->model = new Model_Goods();
		echo $this->model->edit_discount();
	}
	function action_edit_discount_cat() {
		$this->model = new Model_Goods();
		echo $this->model->edit_discount_cat();
	}
	function action_edit_discount_cat2() {
		$this->model = new Model_Goods();
		echo $this->model->edit_discount_cat2();
	}
	function action_del_cats_from_promo2() {
		$this->model = new Model_Goods();
		echo $this->model->del_cats_from_promo2();
	}
	function action_map_discountCard_edit() {
		$this->view->generate('view_discountCard_attr.php', 'view_template.php');
	}
}
?>
