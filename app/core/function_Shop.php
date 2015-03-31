<?php
class Shop {
	public static function GetGoodsList($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $GoodID, $Article, $Name, $EAN13, $cat_id) {
		$GoodID = '%' . $GoodID . '%';
		$Article = '%' . $Article . '%';
		$Name = $Name . '%';
		$ssql = "CALL pr_goods_list('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'" . adds($GoodID) . "','" . adds($Article) . "','" . adds($Name) . "','" . adds($EAN13) . "', " . adds($cat_id) . " , 0)";
//Fn::debugToLog("GetGoodsList", $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function GetGoodsListWhere($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $where, $cat_id) {
		$GoodID = '%' . $GoodID . '%';
		$Article = '%' . $Article . '%';
		$Name = $Name . '%';
		$ssql = "CALL pr_goods_list('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'','','" . ($where) . "','', " . adds($cat_id) . " , 0)";
//Fn::debugToLog("sql", $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function GoodDelete($dbi, $GoodID) {
		if ($GoodID == '')
			return false;
		$ssql = "CALL pr_good_delete('" . $GoodID . "');";
//Fn::debugToLog('GoodDelete: ', $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$rows = $res->num_rows;
		if ($rows == 0)
			return true;
		return false;
	}
	public static function GetGoodInfo($dbi, $param, $ID) {
		$ssql = "CALL pr_goods('" . $param . "'," . $ID . ",'','','',0,0,'',0,0,0,0,0,0,0,0,@id);";
//Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function xxxGetDiscountCardsInfo($dbi, $param, $ID) {
		$ssql = "CALL pr_discountCard('info',@id,null,".$ID.");";
Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}

	public static function SetGoodInfo($dbi, $GoodID, $Article, $Name, $Division, $Unit_in_pack, $Unit, $Weight, $DiscountMax) {
		$Article = trim($Article);
		$Name = trim($Name);
		if ($Unit_in_pack == '')
			$Unit_in_pack = 0;
		if ($Weight == '')
			$Weight = 0;
		$Weight = str_replace(',', '.', $Weight);
		if ($DiscountMax == '')
			$DiscountMax = 0;
		$ssql = "CALL pr_goods('good_edit_site'," . $GoodID . ",'','" . adds($Article) . "','" . adds($Name) . "'," . $Division . "," . adds($Unit_in_pack) . ",'" . $Unit . "'," . $Weight . "," . $DiscountMax . ",0,0,0,0,0,0,@id);";
//DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$res = $dbi->query("SELECT @id;");
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$r = $res->fetch_array(MYSQLI_BOTH);
		$id = $r[0];
		if ($id <= 0)
			return false;
		if ($id > 0)
			return true;
		return true;
	}
	public static function SetSynchroInfo($dbi, $action, $GoodID, $OPT_ID, $SHOP_ID, $KIEV_ID) {
		$Article = trim($Article);
		$Name = trim($Name);
		if ($Unit_in_pack == '')
			$Unit_in_pack = 0;
		if ($Weight == '')
			$Weight = 0;
		$Weight = str_replace(',', '.', $Weight);
		if ($DiscountMax == '')
			$DiscountMax = 0;
		$ssql = "CALL pr_synchro('" . $action . "',@id," . $GoodID . ",'" . adds($OPT_ID) . "','" . adds($SHOP_ID) . "','" . adds($KIEV_ID) . "'," . $_SESSION['UserID'] . ");";
//Fn::debugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$res = $dbi->query("SELECT @id;");
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$r = $res->fetch_array(MYSQLI_BOTH);
		$id = $r[0];
		if ($id <= 0)
			return false;
		if ($id > 0)
			return true;
		return true;
	}

	public static function GetGoodBarcode($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $GoodID) {
		$ssql = "CALL pr_goods_list('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'" . adds($GoodID) . "','','','', 0, 0)";
//DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function BarcodeAddDelEdit($dbi, $param, $GoodID, $EAN13) {
		if ($GoodID == '') return false;
		if ($EAN13 == '') return false;
//$ssql = "CALL pr_promo_control('good_by_art_add_in_promo',".$promo_id.",'".$PricePromo."','".$Article."');";
		$ssql = "CALL pr_goods('" . $param . "'," . $GoodID . ",'','" . $EAN13 . "','',0,0,'',0,0,0,0,0,0,0,0,@id);";
//Fn::debugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$res = $dbi->query("SELECT @id;");
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$r = $res->fetch_array(MYSQLI_BOTH);
		$id = $r[0];
		if ($id <= 0)
			return false;
		if ($id > 0)
			return true;
		return true;
	}
	
	public static function GetPromoTreeNS($dbi, $nodeid, $n_level, $n_left, $n_right) {
		if ($nodeid == '')  $nodeid = 'null';
		if ($n_level == '') $n_level = 0;
		if ($n_left == '')  $n_left = 'null';
		if ($n_right == '') $n_right = 'null';
		$ssql = "CALL pr_tree_NS('promo', 'PromoID', " . $nodeid . ", " . $n_level . ", " . $n_left . ", " . $n_right . ");";
//DebugToLog('start GetPromoTree: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function GetPromoInfo($dbi, $ID) {
		$ssql = "CALL pr_promo_info('getById'," . $ID . ",'',null,'','','',null,null,null);";
//DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function GetPromoTypeList($dbi) {
		$ssql = "CALL pr_promo_info('getPromoTypeList',0,'',null,'','','',null,null,null);";
//DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function CreateNewElementTreeNS($dbi, $table, $id, $parent_id, $name) {
		if ($table == 'category')
			$ssql = "CALL pr_tree_NS_action_cat('add', @id, " . $parent_id . ", '" . $name . "', " . $_SESSION['UserID'] . ");";
		if ($table == 'cat_partner')
			$ssql = "CALL pr_tree_NS_action_cat_partner('add', @id, " . $parent_id . ", '" . $name . "', " . $_SESSION['UserID'] . ");";
		if ($table == 'promo')
			$ssql = "CALL pr_tree_NS_action_promo('add', @id, " . $parent_id . ", '" . $name . "', " . $_SESSION['UserID'] . ");";

Fn::DebugToLog('add: ',$ssql);

		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;

		$res = $dbi->query("SELECT @id;");
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		$r = $res->fetch_array(MYSQLI_BOTH);
		$id = $r[0];
		return $id;
	}
	public static function SetNewNameforElementTreeNS($dbi, $table, $id, $name) {
		if ($table == 'category')
			$ssql = "CALL pr_tree_NS_action_cat('edit', @id, 0, '" . $name . "', " . $_SESSION['UserID'] . ");";
		if ($table == 'cat_partner')
			$ssql = "CALL pr_tree_NS_action_cat_partner('edit', @id, 0, '" . $name . "', " . $_SESSION['UserID'] . ");";
		if ($table == 'promo')
			$ssql = "CALL pr_tree_NS_action_promo('edit', @id, 0, '" . $name . "', " . $_SESSION['UserID'] . ");";

Fn::DebugToLog('edit: ', $ssql);

		$res = $dbi->query("SET @id=" . $id . ";");
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function MoveElementTreeNS($dbi, $table, $id, $target) {
		if ($table == 'category')
			$ssql = "CALL pr_tree_NS_action_cat('move', @id, " . $target . ", '', " . $_SESSION['UserID'] . ");";
		if ($table == 'cat_partner')
			$ssql = "CALL pr_tree_NS_action_cat_partner('move', @id, " . $target . ", '', " . $_SESSION['UserID'] . ");";
		if ($table == 'promo')
			$ssql = "CALL pr_tree_NS_action_promo('move', @id, " . $target . ", '', " . $_SESSION['UserID'] . ");";

//		$res = $dbi->query("SET @id=" . $id . ";");
Fn::debugToLog('MoveElementTreeNS id='.$id, $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function CopyTreeNS($dbi, $table, $id, $target) {
		if ($table == 'category')
			$ssql = "CALL pr_tree_NS_action_cat('copy', @id, " . $target . ", '', " . $_SESSION['UserID'] . ");";
		if ($table == 'cat_partner')
			$ssql = "CALL pr_tree_NS_action_cat_partner('copy', @id, " . $target . ", '', " . $_SESSION['UserID'] . ");";
		if ($table == 'promo')
			$ssql = "CALL pr_tree_NS_action_cat('copy', @id, " . $target . ", '', " . $_SESSION['UserID'] . ");";
//Fn::debugToLog('CopyTreeNS id='.$id, $ssql);
		$res = $dbi->query("SET @id=" . $id . ";");
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function GetUserList($dbi) {
		$ssql = "CALL pr_user('getList',0,'','',null,null);";
//DebugToLog('start: ', $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function SetPromoInfo($dbi, $promoid, $name, $promo_type_id, $description, $DT_start, $DT_stop, $UserID_response, $QuantityPromo) {
		if ($QuantityPromo == '')
			$QuantityPromo = 0;
		$ssql = "CALL pr_promo_info('setInfo'," . $promoid . ",'" . $name . "'," . $promo_type_id . ",'" . $description . "','" . $DT_start . "','" . $DT_stop . "'," . $UserID_response . "," . $_SESSION['UserID'] . "," . $QuantityPromo . ");";
//DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function GetPromoList($dbi, $promo_type) {
		$ssql = "CALL pr_promo_info('getPromoList',0,'',0,'" . $promo_type . "','','',null,null,null);";
//DebugToLog('start: ', $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function AddGoodsAndPriceToPromo($dbi, $promo_id, $Article, $PricePromo) {
		$PricePromo = str_replace(',', '.', $PricePromo);
		if ($PricePromo == '')
			return false;
		$ssql = "CALL pr_promo_control('good_by_art_add_in_promo'," . $promo_id . ",'" . $PricePromo . "','" . $Article . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function PromoEditPrice($dbi, $promoid, $id, $PricePromo) {
		$PricePromo = str_replace(',', '.', $PricePromo);
		$ssql = "CALL pr_promo_control('set_price_in_promo'," . $promoid . ",'" . $PricePromo . "','" . $id . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function DelGoodsFromPromo($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('goods_del_from_promo'," . $promo_id . ",'','" . $source . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function DelGoodsFromPromoAction($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('goods_del_from_promo_action'," . $promo_id . ",'','" . $source . "');";
//Fn::debugToLog("del", $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function AddGoodsToPromo($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('goods_add_in_promo'," . $promo_id . ",'','" . $source . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function AddGoodsToPromoAction($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('goods_add_in_promo_action'," . $promo_id . ",'','" . $source . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function PromoEditDiscount($dbi, $promoid, $id, $PricePromo) {
		$PricePromo = str_replace(',', '.', $PricePromo);
		$ssql = "CALL pr_promo_control('set_discount_in_promo'," . $promoid . ",'" . $PricePromo . "','" . $id . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function PromoEditDiscountCat($dbi, $promoid, $id, $PricePromo) {
		$PricePromo = str_replace(',', '.', $PricePromo);
		$ssql = "CALL pr_promo_control('set_discount_cat_in_promo'," . $promoid . ",'" . $PricePromo . "','" . $id . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function PromoEditDiscountCat2($dbi, $promoid, $id, $PricePromo) {
		$PricePromo = str_replace(',', '.', $PricePromo);
		$ssql = "CALL pr_promo_control('set_discount_cat_in_promo2'," . $promoid . ",'" . $PricePromo . "','" . $id . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function GetCatsList($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $Name, $cat_id) {
		$GoodID = '%' . $GoodID . '%';
		$Article = '%' . $Article . '%';
		$Name = $Name . '%';
		$ssql = "CALL pr_cats_list('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'" . adds($Name) . "', " . adds($cat_id) . " , 0)";
//DebugToLog('start: ', $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function DelCatsFromPromo($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('cats_del_from_promo'," . $promo_id . ",'" . $source . "','');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function DelCatsFromPromo2($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('cats_del_from_promo2'," . $promo_id . ",'" . $source . "','');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function AddCatsToPromo($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('cats_add_in_promo'," . $promo_id . ",'" . $source . "','');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function AddCatsToPromo2($dbi, $promo_id, $source) {
		$ssql = "CALL pr_promo_control('cats_add_in_promo2'," . $promo_id . ",'" . $source . "','');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function xxx_GetPointsList($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $ClientID, $City, $Name) {
		$ClientID = '%' . $ClientID . '%';
		$City = '%' . $City . '%';
		$Name = $Name . '%';
		$ssql = "CALL pr_point('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'" . adds($ClientID) . "','" . adds($City) . "','" . adds($Name) . "', '','','')";
//Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function GetSellersList($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $ClientID, $City, $Name) {
		$ClientID = '%' . $ClientID . '%';
		$City = '%' . $City . '%';
		$Name = $Name . '%';
		//$ssql = "CALL pr_seller_info('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'" . adds($ClientID) . "','" . adds($City) . "','" . adds($Name) . "', '','','')";
			$ssql = "CALL pr_seller_info('listForSite',@id,null,null,null,null,null,null)";
//Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function xxxGetDiscountCardsList($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $ClientID, $City, $Name) {
		$ClientID = '%' . $ClientID . '%';
		$City = '%' . $City . '%';
		$Name = $Name . '%';
		//$ssql = "CALL pr_seller_info('" . adds($param) . "','" . adds($sidx) . "','" . adds($sord) . "'," . adds($request_count) . "," . adds($start) . "," . adds($limit) . ",'" . adds($ClientID) . "','" . adds($City) . "','" . adds($Name) . "', '','','')";
		$ssql = "call pr_discountCard('listForSite', @id, null);";
//Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}

	public static function GetCategoryTreeNS($dbi, $nodeid, $n_level, $n_left, $n_right) {
		if ($nodeid == '')
			$nodeid = 'null';
		if ($n_level == '')
			$n_level = 0;
		if ($n_left == '')
			$n_left = 'null';
		if ($n_right == '')
			$n_right = 'null';
		$ssql = "CALL pr_tree_NS('category', 'CatID', " . $nodeid . ", " . $n_level . ", " . $n_left . ", " . $n_right . ");";
//Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function AddToCategory($dbi, $cat_id, $source) {
		$ssql = "CALL pr_category_goods('add_in_cat'," . $cat_id . ", '" . $source . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}
	public static function DelFromCategory($dbi, $cat_id, $source) {
		$ssql = "CALL pr_category_goods('del_from_cat'," . $cat_id . ", '" . $source . "');";
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}

	public static function GetCategoryTreeNS_cat_partner($dbi, $nodeid, $n_level, $n_left, $n_right) {
		if ($nodeid == '')
			$nodeid = 'null';
		if ($n_level == '')
			$n_level = 0;
		if ($n_left == '')
			$n_left = 'null';
		if ($n_right == '')
			$n_right = 'null';
		$ssql = "CALL pr_tree_NS('cat_partner', 'CatID', " . $nodeid . ", " . $n_level . ", " . $n_left . ", " . $n_right . ");";
Fn::debugToLog('Shop::GetCategoryTreeNS_cat_partner: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}

//discount card
	public static function GetDiscountCardsList($dbi, $param, $sidx, $sord, $request_count, $start, $limit, $ClientID, $City, $Name) {
		$ClientID = '%' . $ClientID . '%';
		$City = '%' . $City . '%';
		$Name = $Name . '%';
		$ssql = "call pr_discountCard('listForSite', @id, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);";
//Fn::DebugToLog('start: ',$ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function GetDiscountCardsInfo($dbi, $param, $ID) {
		$ssql = "CALL pr_discountCard('info',@id," . $ID . ", null, null, null, null, null, null, null, null, null, null, null, null, null, null);";
		Fn::DebugToLog('start: ', $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return $res;
	}
	public static function xxxSetDiscountCardInfo($dbi, $action, $cardid, $name, $dateOfIssue, $dateOfCancellation, $clientID, $address, $eMail, $phone, $animal, $startPercent, $startSum, $dopSum, $percentOfDiscount, $howWeLearn, $notes) {
		if ($cardid == '') return false;
		$ssql = "CALL pr_discountCard('" . $action . "',"
				. "@id," . $cardid . ","
				. "'" . adds($name) . "',"
				. "'" . adds($dateOfIssue) . "',"
				. "'" . adds($dateOfCancellation) . "',"
				. "" . adds($clientID) . ","
				. "'" . adds($address) . "',"
				. "'" . adds($eMail) . "',"
				. "'" . adds($phone) . "',"
				. "'" . adds($animal) . "',"
				. "'" . adds($startPercent) . "',"
				. "'" . adds($startSum) . "',"
				. "'" . adds($dopSum) . "',"
				. "" . adds($percentOfDiscount) . ","
				. "'" . adds($howWeLearn) . "',"
				. "'" . adds($notes) . "');";
//		Fn::debugToLog('SetDiscountCardInfo: ', $ssql);
		$res = $dbi->query($ssql);
		if (!Fn::checkErrorMySQLi($dbi))
			return false;
		return true;
	}

	function ShowTestTable($res) {
		$rows = mysqli_num_rows($res);
		$fields = mysqli_num_fields($res);
		echo "<h3>___T_E_S_T___</h3>";
		echo "<h3>Всего записей: " . $rows . "___Всего полей: " . $fields . "</h3>";
		echo "<table width='98%' border=0 cellspacing=1 cellpadding=1 bgcolor=#000>";
		echo "<tr bgcolor=#EEE  style='font-weight:bolder'>";

		while ($finfo = $res->fetch_field()) {
			echo "<td>".$finfo->name."</td>";
		}
		echo "</tr>";
		while ($row = $res->fetch_row()) {
			echo "<tr bgcolor=#EEE>";
			for ($x = 0; $x < $fields; $x++) {
				echo "<td>".$row[$x]."</td>";
			}
			echo "</tr>";
		}
		echo "</table>";
		return true;
	}
}
