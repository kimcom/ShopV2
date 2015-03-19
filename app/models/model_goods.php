<?php
class Model_Goods extends Model {
	public function get_data() {
		$cnn = new Cnni();
		return $cnn->get_goods_list();
	}
	public function get_good_barcode_list() {
		$cnn = new Cnni();
		return $cnn->get_good_barcode_list();
	}
	public function get_barcode_edit() {
		$cnn = new Cnni();
		return $cnn->get_barcode_edit();
	}
	public function good_info_save() {
		$cnn = new Cnni();
		return $cnn->good_info_save();
	}
	public function synchro_info_save() {
		$cnn = new Cnni();
		return $cnn->synchro_info_save();
	}
	public function good_delete() {
		$cnn = new Cnni();
		return $cnn->good_delete();
	}
	public function goods_add2_in_promo() {
		$cnn = new Cnni();
		return $cnn->goods_add2_in_promo();
	}
	public function edit_price() {
		$cnn = new Cnni();
		return $cnn->edit_price();
	}
	public function del_goods_from_promo() {
		$cnn = new Cnni();
		return $cnn->del_goods_from_promo();
	}
	public function del_goods_from_promo_action() {
		$cnn = new Cnni();
		return $cnn->del_goods_from_promo_action();
	}
	public function goods_add_in_promo() {
		$cnn = new Cnni();
		return $cnn->goods_add_in_promo();
	}
	public function goods_add_in_promo_action() {
		$cnn = new Cnni();
		return $cnn->goods_add_in_promo_action();
	}
	public function get_cats_list() {
		$cnn = new Cnni();
		return $cnn->get_cats_list();
	}
	public function edit_discount() {
		$cnn = new Cnni();
		return $cnn->edit_discount();
	}
	public function edit_discount_cat() {
		$cnn = new Cnni();
		return $cnn->edit_discount_cat();
	}
	public function edit_discount_cat2() {
		$cnn = new Cnni();
		return $cnn->edit_discount_cat2();
	}
	public function del_cats_from_promo() {
		$cnn = new Cnni();
		return $cnn->del_cats_from_promo();
	}
	public function del_cats_from_promo2() {
		$cnn = new Cnni();
		return $cnn->del_cats_from_promo2();
	}
	public function cats_add_in_promo() {
		$cnn = new Cnni();
		return $cnn->cats_add_in_promo();
	}
	public function cats_add_in_promo2() {
		$cnn = new Cnni();
		return $cnn->cats_add_in_promo2();
	}
}
?>
