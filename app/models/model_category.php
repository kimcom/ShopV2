<?php
class Model_Category extends Model {
//категории партнеров	
	public function get_tree_NS_cat_partner() {
		$cnn = new Cnni();
		echo $cnn->get_tree_NS_cat_partner();
	}
	public function cat_partner_tree_oper() {
		$cnn = new Cnni();
		echo $cnn->cat_partner_tree_oper();
	}
//категории статьи затрат
	public function get_tree_NS_cat_spent() {
		$cnn = new Cnni();
		echo $cnn->get_tree_NS_cat_spent();
	}
	public function cat_spent_tree_oper() {
		$cnn = new Cnni();
		echo $cnn->cat_spent_tree_oper();
	}
	public function add_in_cat_spent() {
		$cnn = new Cnni();
		return $cnn->add_in_cat_spent();
	}
	public function del_from_cat_spent() {
		$cnn = new Cnni();
		return $cnn->del_from_cat_spent();
	}

	public function get_tree_NS() {
		$cnn = new Cnni();
		return $cnn->get_tree_NS_category();
	}
	public function category_tree_oper() {
		$cnn = new Cnni();
		return $cnn->category_tree_oper();
	}
	public function add_in_cat() {
		$cnn = new Cnni();
		return $cnn->add_in_cat();
	}
	public function del_from_cat() {
		$cnn = new Cnni();
		return $cnn->del_from_cat();
	}
}
?>
