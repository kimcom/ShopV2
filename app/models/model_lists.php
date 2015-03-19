<?php
class Model_Lists extends Model {
	public function get_promo_tree_NS() {
		$cnn = new Cnni();
		return $cnn->get_promo_tree_NS();
	}
	public function get_promo_tree_info() {
		$cnn = new Cnni();
		return $cnn->get_promo_tree_info();
	}
	public function get_promo_type() {
		$cnn = new Cnni();
		return $cnn->get_promo_type();
	}
	public function promo_tree_oper() {
		$cnn = new Cnni();
		return $cnn->promo_tree_oper();
	}
	public function get_user_list() {
		$cnn = new Cnni();
		return $cnn->get_user_list();
	}
	public function promo_save() {
		$cnn = new Cnni();
		return $cnn->promo_save();
	}
	public function get_promo_list() {
		$cnn = new Cnni();
		return $cnn->get_promo_list();
	}
}
?>
