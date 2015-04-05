<?php
class Cnni {
	private $dbi = null;
	public function getDbi() {
		return $this->dbi;
	}
	public function __construct() {
		$this->dbi = new mysqli("localhost", "shop", "149521", $_SESSION['dbname']);
		if($this->dbi->connect_errno){
			Fn::errorToLog("MySQLi error!: ", $this->dbi->connect_errno." ".$this->dbi->connect_error);
			die();
		}
	}
//goods
	public function get_goods_list() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
//Fn::debugToLog('get_goods_list', urldecode($_SERVER['QUERY_STRING']));
		//для отчета о продажах
		if(!isset($Article)) $Article = $field1;
		if(!isset($Name)) $Name = $field2;
		if (1 == 0) {
			ob_start();
			var_dump($_REQUEST);
			Fn::debugToLog("test param\n" . $_SERVER['SCRIPT_FILENAME'] . "\n", ob_get_clean());
			ob_end_clean();
		}
		// Номер запришиваемой страницы							$page = $_GET['page'];
		// Количество запрашиваемых записей						
		$limit = $rows;
		// Поле, по которому следует производить сортировку		$sidx = $_GET['sidx'];
		// Направление сортировки								$sord = $_GET['sord'];
		// Если не указано поле сортировки, то производить сортировку по первому полю
		if (!$sidx)
			$sidx = 1;
		$totalrows = isset($_REQUEST['totalrows']) ? $_REQUEST['totalrows'] : false;
		if ($totalrows) {
			$limit = $totalrows;
		}
//	$count = 1000;
		$where = " 	Article  like \'%" . adds($Article) . "\'";
		$where .= " and Name 	 like \'%" . adds($Name) . "\'";
		$where .= " and Division like \'%" . adds($Division) . "\'";
		$where .= " and g.GoodID like \'%" . adds($GoodID) . "\'";
		$where .= " and OPT_ID 	 like \'%" . adds($OPT_ID) . "\'";
		$where .= " and SHOP_ID  like \'%" . adds($SHOP_ID) . "\'";
		$where .= " and KIEV_ID	 like \'%" . adds($KIEV_ID) . "\'";
//Fn::debugToLog('start2', $where);
		if ($param == 'goods_list_where') {
			$result = Shop::GetGoodsListWhere($this->dbi,$param, $sidx, $sord, 1, 0, 0, $where, $cat_id);
//Fn::debugToLog('stop', '');
		} else {
			$result = Shop::GetGoodsList($this->dbi,$param, $sidx, $sord, 1, 0, 0, $GoodID, $Article, $Name, $EAN13, $cat_id);
		}
		while ($row =  $result->fetch_array(MYSQLI_BOTH)) {
			$count = $row[0];
		}
//Fn::debugToLog('test', $count);
		while ($this->dbi->next_result())
			$this->dbi->store_result();
//DebugToLog("test param\n".$_SERVER['SCRIPT_FILENAME']." count: ",$count);
		// Рассчитаем сколько всего страниц займут данные в БД
		if ($count > 0 && $limit > 0) {
			$total_pages = ceil($count / $limit);
		} else {
			$total_pages = 0;
		}
		// Если по каким-то причинам клиент запросил
		if ($page > $total_pages)
			$page = $total_pages;
		// Рассчитываем стартовое значение для LIMIT
		$start = $limit * $page - $limit;
		// Зашита от отрицательного значения
		if ($start < 0)
			$start = 0;

		if ($param == 'goods_list_where') {
			$result = Shop::GetGoodsListWhere($this->dbi, $param, $sidx, $sord, 0, $start, $limit, $where, $cat_id);
		} else {
			$result = Shop::GetGoodsList($this->dbi, $param, $sidx, $sord, 0, $start, $limit, $GoodID, $Article, $Name, $EAN13, $cat_id);
		}
		// Создаем объект response
		$response->page = $page;
		$response->total = $total_pages;
		$response->records = $count;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$response->rows[$i]['id'] = $row['ID'];
			if ($col == 'price') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name'], $row['PriceBase'], $row['Price1'], $row['PricePromo'], $row['SortIndex']);
			} elseif ($col == 'discount') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name'], $row['PriceBase'], $row['DiscountMax'], $row['DiscountPromo']);
			} elseif ($col == 'disc123') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name'], $row['PriceBase'], $row['DiscountMax'], $row['DiscountPromo']);
			} elseif ($col == 'disc_dop') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name'], $row['PriceBase'], $row['DiscountMax']);
			} elseif ($col == 'gift') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name'], $row['PriceBase'], $row['Price1'], $row['PricePromo']);
			} elseif ($col == 'cat') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name']); //
			} elseif ($col == 'goods_list') {
				$response->rows[$i]['cell'] = array($row['GoodID'], $row['OPT_ID'], $row['SHOP_ID'], $row['KIEV_ID'], $row['Article'], $row['Name'], $row['Division'], $row['DiscountMax'], $row['Unit_in_pack'], $row['Unit'], $row['Weight']);
			} elseif ($col == 'goods_barcode') {
				$response->rows[$i]['id'] = $row['EAN13'];
				$response->rows[$i]['cell'] = array($row['ID'], $row['Article'], $row['Name'], $row['EAN13']);
			} elseif ($col == 'goods_barcode_verify') {
				$response->rows[$i]['id'] = $row['EAN13'];
				$response->rows[$i]['cell'] = array($row['EAN13'], $row['ID'], $row['Article'], $row['Name'], $row['UserID'], $row['UserName'], $row['NameShort'], $row['DT_create'], $row['Virified']);
			} elseif ($col == 'goods_without_barcode') {
				$response->rows[$i]['cell'] = array($row['ID'], $row['Article'], $row['Name']);
			} elseif ($col == 'goods_price') {
				$response->rows[$i]['cell'] = array($row['Article'], $row['Name'], $row['DiscountMax'], $row['PriceBase'], $row['Price1'], $row['Price2'], $row['Price3']);
			}
			// echo $row['GoodID'].$row['Article'].$row['Name'].$row['Division'].$row['DiscountMax'];
			$i++;
		}
		// Перед выводом не забывайте выставить header с указанием типа контента и кодировки
		header("Content-type: application/json;charset=cp1251");
		echo json_encode($response);
	}
	public function good_info_save() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$response->success = Shop::SetGoodInfo($this->dbi, $goodid, $Article, $Name, $Division, $Unit_in_pack, $Unit, $Weight, $DiscountMax);
		$response->message = 'Возникла ошибка при внесении информации!<br>Сообщите разработчику!';
		$response->new_id = 0;
		echo json_encode($response);
		return;
	}
	public function get_good_barcode_list() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		// Номер запришиваемой страницы							$page = $_GET['page'];
		// Количество запрашиваемых записей						
		$limit = $rows;
		// Поле, по которому следует производить сортировку		$sidx = $_GET['sidx'];
		// Направление сортировки								$sord = $_GET['sord'];
		// Если не указано поле сортировки, то производить сортировку по первому полю
		if (!$sidx)
			$sidx = 1;
		$totalrows = isset($_REQUEST['totalrows']) ? $_REQUEST['totalrows'] : false;
		if ($totalrows) {
			$limit = $totalrows;
		}
//	$count = 1000;

		$result = Shop::GetGoodBarcode($this->dbi, $param, $sidx, $sord, 1, 0, 0, $GoodID);
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$count = $row[0];
		}
		while ($this->dbi->next_result())
			$this->dbi->store_result();
//DebugToLog("test param\n".$_SERVER['SCRIPT_FILENAME']." count: ",$count);
		// Рассчитаем сколько всего страниц займут данные в БД
		if ($count > 0 && $limit > 0) {
			$total_pages = ceil($count / $limit);
		} else {
			$total_pages = 0;
		}
		// Если по каким-то причинам клиент запросил
		if ($page > $total_pages)
			$page = $total_pages;
		// Рассчитываем стартовое значение для LIMIT
		$start = $limit * $page - $limit;
		// Зашита от отрицательного значения
		if ($start < 0)
			$start = 0;
		//GetCatsList

		$result = Shop::GetGoodBarcode($this->dbi, $param, $sidx, $sord, 0, $start, $limit, $GoodID);
		// Создаем объект response
		$response->page = $page;
		$response->total = $total_pages;
		$response->records = $count;
		$i = 0;
		while ($row = mysqli_fetch_assoc($result)) {
			$response->rows[$i]['id'] = $row['EAN13'];
			$response->rows[$i]['cell'] = array($row['EAN13']);
			$i++;
		}
		// Перед выводом не забывайте выставить header с указанием типа контента и кодировки
		header("Content-type: application/json;charset=cp1251");
		echo json_encode($response);
	}
	public function synchro_info_save() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$response->success = Shop::SetSynchroInfo($this->dbi, 'edit', $goodid, $OPT_ID, $SHOP_ID, $KIEV_ID);
		$response->message = 'Возникла ошибка при внесении информации!<br>Сообщите разработчику!';
		$response->new_id = 0;
		echo json_encode($response);
		return;
	}
	public function good_delete() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		if ($_SESSION['AccessLevel'] == 1000) {
			$response->success = Shop::GoodDelete($this->dbi, $goodid); //удалить товар
			$response->message = 'Этот товар удалить невозможно, т.к. он уже продавался в магазинах!';
			$response->new_id = 0;
			echo json_encode($response);
			return;
		} else {
			$response->success = false; //удалить товар
			$response->message = 'Недостаточный уровень доступа для удаления товара!';
			$response->new_id = 0;
			echo json_encode($response);
			return;
		}
	}
//barcode
	public function get_barcode_edit(){
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		if ($oper == 'add') {
			$response->success = Shop::BarcodeAddDelEdit($this->dbi, 'barcode_add', $goodid, $EAN13); //добавить ШК
			$response->message = 'Возникла ошибка при внесении информации!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
			return;
		}
		if ($oper == 'del') {
			$response->success = Shop::BarcodeAddDelEdit($this->dbi, 'barcode_del', $goodid, $id); //удалить ШК
			$response->message = 'Возникла ошибка при удалении информации!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
			return;
		}
		if ($oper == 'verified') {
			$response->success = Shop::BarcodeAddDelEdit($this->dbi, 'barcode_verified', $goodid, $barcode); //уст. отметку ШК-Проверен
			$response->message = 'Возникла ошибка при удалении информации!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
			return;
		}
	}
//category partner
	public function get_tree_NS_cat_partner() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$result = Shop::GetCategoryTreeNS_cat_partner($this->dbi, $nodeid, $n_level, $n_left, $n_right);
		if ($nodeid > 0) {
			$n_level = $n_level + 1;
		} else {
			$n_level = 0;
		}
		$response->page = 1;
		$response->total = 1;
		$response->records = 1;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			if ($row['rgt'] == $row['lft'] + 1)
				$leaf = 'true';
			else
				$leaf = 'false';
			if ($n_level == $row['level']) { // we output only the needed level
				$response->rows[$i]['id'] = $row['CatID'];
				$response->rows[$i]['cell'] = array($row['CatID'],
					//$row['name'].' ('.$row['CatID'].')',
					$row['name'],
					$row['level'],
					$row['lft'],
					$row['rgt'],
					$leaf,
					'false'
				);
			}
			$i++;
		}
		header("Content-type: text/html;charset=utf-8");
		echo json_encode($response);
	}
	public function cat_partner_tree_oper() {
		foreach ($_REQUEST as $arg => $val){
			${$arg} = $val;
//			Fn::debugToLog('arg', $arg." = ".  $val);
		}
		$response = new stdClass();
		if ($oper == 'add') {
			$id = Shop::CreateNewElementTreeNS($this->dbi, 'cat_partner', $id, $parent_id, $name);
			if ($id == false) {
				$response->success = false;
				$response->message = 'Возникла ошибка при добавлении!<br>Сообщите разработчику!';
				$response->new_id = 0;
			} else {
				$response->success = true;
				$response->message = '';
				$response->new_id = $id;
			}
			echo json_encode($response);
		}
		if ($oper == 'edit') {
			$response->success = Shop::SetNewNameforElementTreeNS($this->dbi, 'cat_partner', $id, $name);
			$response->message = 'Возникла ошибка сохранения изменений!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'del') {
			//$response->success = DeleteElementTreeNS('category',$id);
			$response->success = Shop::MoveElementTreeNS($this->dbi, 'cat_partner', $id, 90);
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'copy') {
			//echo CopyTreeByID('category',$source,$target);
			echo Shop::CopyTreeNS($this->dbi, 'cat_partner', $source, $target);
		}
		if ($oper == 'move') {
			//echo SetParentIDforTree('category','CatID',$source,$target);
			echo Shop::MoveElementTreeNS($this->dbi, 'cat_partner', $source, $target);
		}
	}
//category spent
	public function get_tree_NS_cat_spent() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		$result = Shop::GetCategoryTreeNS_cat_spent($this->dbi, $nodeid, $n_level, $n_left, $n_right);
		if ($nodeid > 0) {
			$n_level = $n_level + 1;
		} else {
			$n_level = 0;
		}
		$response->page = 1;
		$response->total = 1;
		$response->records = 1;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			if ($row['rgt'] == $row['lft'] + 1)
				$leaf = 'true';
			else
				$leaf = 'false';
			if ($n_level == $row['level']) { // we output only the needed level
				$response->rows[$i]['id'] = $row['CatID'];
				$response->rows[$i]['cell'] = array($row['CatID'],
					//$row['name'].' ('.$row['CatID'].')',
					$row['name'],
					$row['level'],
					$row['lft'],
					$row['rgt'],
					$leaf,
					'false'
				);
			}
			$i++;
		}
		header("Content-type: text/html;charset=utf-8");
		echo json_encode($response);
	}
	public function cat_spent_tree_oper() {
		foreach ($_REQUEST as $arg => $val) {
			${$arg} = $val;
//			Fn::debugToLog('arg', $arg." = ".  $val);
		}
		$response = new stdClass();
		if ($oper == 'add') {
			$id = Shop::CreateNewElementTreeNS($this->dbi, 'cat_spent', $id, $parent_id, $name);
			if ($id == false) {
				$response->success = false;
				$response->message = 'Возникла ошибка при добавлении!<br>Сообщите разработчику!';
				$response->new_id = 0;
			} else {
				$response->success = true;
				$response->message = '';
				$response->new_id = $id;
			}
			echo json_encode($response);
		}
		if ($oper == 'edit') {
			$response->success = Shop::SetNewNameforElementTreeNS($this->dbi, 'cat_spent', $id, $name);
			$response->message = 'Возникла ошибка сохранения изменений!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'del') {
			//$response->success = DeleteElementTreeNS('category',$id);
			$response->success = Shop::MoveElementTreeNS($this->dbi, 'cat_spent', $id, 90);
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'copy') {
			//echo CopyTreeByID('category',$source,$target);
			echo Shop::CopyTreeNS($this->dbi, 'cat_spent', $source, $target);
		}
		if ($oper == 'move') {
			//echo SetParentIDforTree('category','CatID',$source,$target);
			echo Shop::MoveElementTreeNS($this->dbi, 'cat_spent', $source, $target);
		}
	}

//category
	public function get_tree_NS_category() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$result = Shop::GetCategoryTreeNS($this->dbi, $nodeid, $n_level, $n_left, $n_right);
		if ($nodeid > 0) {
			$n_level = $n_level + 1;
		} else {
			$n_level = 0;
		}
		$response->page = 1;
		$response->total = 1;
		$response->records = 1;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			if ($row['rgt'] == $row['lft'] + 1)
				$leaf = 'true';
			else
				$leaf = 'false';
			if ($n_level == $row['level']) { // we output only the needed level
				$response->rows[$i]['id'] = $row['CatID'];
				$response->rows[$i]['cell'] = array($row['CatID'],
					//$row['name'].' ('.$row['CatID'].')',
					$row['name'],
					$row['level'],
					$row['lft'],
					$row['rgt'],
					$leaf,
					'false'
				);
			}
			$i++;
		}
		header("Content-type: text/html;charset=utf-8");
		echo json_encode($response);
	}
	public function category_tree_oper() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		if ($oper == 'add') {
			$id = Shop::CreateNewElementTreeNS($this->dbi, 'category', $id, $parent_id, $name);
			if ($id == false) {
				$response->success = false;
				$response->message = 'Возникла ошибка при добавлении!<br>Сообщите разработчику!';
				$response->new_id = 0;
			} else {
				$response->success = true;
				$response->message = '';
				$response->new_id = $id;
			}
			echo json_encode($response);
		}
		if ($oper == 'edit') {
			$response->success = Shop::SetNewNameforElementTreeNS($this->dbi, 'category', $id, $name);
			$response->message = 'Возникла ошибка сохранения изменений!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'del') {
			//$response->success = DeleteElementTreeNS('category',$id);
			$response->success = Shop::MoveElementTreeNS($this->dbi, 'category', $id, 90);
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'copy') {
			//echo CopyTreeByID('category',$source,$target);
			echo Shop::CopyTreeNS($this->dbi, 'category', $source, $target);
		}
		if ($oper == 'move') {
			//echo SetParentIDforTree('category','CatID',$source,$target);
			echo Shop::MoveElementTreeNS($this->dbi, 'category', $source, $target);
		}
	}
	public function add_in_cat() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		echo Shop::AddToCategory($this->dbi, $cat_id, $source);
	}
	public function del_from_cat() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		echo Shop::DelFromCategory($this->dbi, $cat_id, $source);
	}
//promo_tree
	public function get_promo_tree_NS(){
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$result = Shop::GetPromoTreeNS($this->dbi, $nodeid, $n_level, $n_left, $n_right);
		if ($nodeid > 0) {
			$n_level = $n_level + 1;
		} else {
			$n_level = 0;
		}
		$response->page = 1;
		$response->total = 1;
		$response->records = 1;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			if ($row['rgt'] == $row['lft'] + 1)
				$leaf = 'true';
			else
				$leaf = 'false';
			//if($n_level == $row['level']) { // we output only the needed level
			$response->rows[$i]['id'] = $row['PromoID'];
			$response->rows[$i]['cell'] = array($row['PromoID'],
				//$row['name'].' ('.$row['PromoID'].')',
				$row['name'],
				$row['level'],
				$row['lft'],
				$row['rgt'],
				$leaf,
				'false'
			);
			//}
			$i++;
		}
		header("Content-type: text/html;charset=utf-8");
		echo json_encode($response);
	}
	public function get_promo_tree_info(){
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		$result = Shop::GetPromoInfo($this->dbi, $id);
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$response->PromoID = $row['PromoID'];
			$response->Name = $row['Name'];
			$response->Description = $row['Description'];
			$response->TypeID = $row['TypeID'];
			$response->TypeName = $row['TypeName'];
			$response->DT_start = dt_format($row['DT_start']);
			$response->DT_stop = dt_format($row['DT_stop']);
			$response->UserID_response = $row['UserID_response'];
			$response->DT_create = dt_format($row['DT_create']);
			$response->DT_modi = dt_format($row['DT_modi']);
			$response->UserID_create = $row['UserID_create'];
			$response->UserID_modi = $row['UserID_modi'];
			$response->UserName_create = $row['UserName_create'];
			$response->UserName_modi = $row['UserName_modi'];
			$response->UserID_response = $row['UserID_response'];
			$response->promo_quantity = $row['QuantityPromo'];
			$i++;
		}
		// Перед выводом не забывайте выставить header с указанием типа контента и кодировки
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
		//DebugToLog('',json_encode($response));
	}
	public function get_promo_type(){
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$result = Shop::GetPromoTypeList($this->dbi);
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$response[$i] = array('id' => $row['TypeID'], 'text' => $row['Name']);
			$i++;
		}
//DebugToLog('',json_encode($response));
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
	}
	public function promo_tree_oper() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if ($oper == 'add') {
			$id = Shop::CreateNewElementTreeNS($this->dbi, 'promo', $id, $parent_id, $name);
			if ($id == false) {
				$response->success = false;
				$response->message = 'Возникла ошибка при добавлении!<br>Сообщите разработчику!';
				$response->new_id = 0;
			} else {
				$response->success = true;
				$response->message = '';
				$response->new_id = $id;
			}
			echo json_encode($response);
		}
		if ($oper == 'edit') {
			$response->success = Shop::SetNewNameforElementTreeNS($this->dbi, 'promo', $id, $name);
			$response->message = 'Возникла ошибка сохранения изменений!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'del') {
			//$response->success = DeleteElementTreeNS('promo',$id);
			$response->success = Shop::MoveElementTreeNS($this->dbi, 'promo', $id, 90);
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
		if ($oper == 'copy') {
			echo Shop::CopyTreeNS($this->dbi, 'promo', $source, $target);
		}
		if ($oper == 'move') {
			echo Shop::MoveElementTreeNS($this->dbi, 'promo', $source, $target);
		}
	}
	public function get_user_list() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		$result = Shop::GetUserList($this->dbi);
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$response[$i] = array('id' => $row['UserID'], 'text' => $row['UserName']);
			$i++;
		}
		//DebugToLog('',json_encode($response));
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
	}
	public function promo_save() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if ($promo_type_id == "")
			$promo_type_id = 0;
		if (!isset($UserID_response))
			$UserID_response = 0;
		$result = Shop::SetPromoInfo($this->dbi, $promoid, $Name, $promo_type_id, $Description, $DT_start, $DT_stop, $UserID_response, $QuantityPromo);
		echo $result;
	}
	public function get_promo_list() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$result = Shop::GetPromoList($this->dbi, $promo_type);
		$i = 0;
		$x = 0;
		$id_group = 0;
		if($colNames == "ID,NameShort"){
			while ($row = $result->fetch_array(MYSQLI_BOTH)) {
				if ($row['isgroup'] == 1) continue;
				$response->rows[$i]['id'] = $row['PromoID'];
				$response->rows[$i]['cell'] = array( $row['PromoID'],
				 $row['Name']); //
				$i++;
			}
		}else{
			while ($row = $result->fetch_array(MYSQLI_BOTH)) {
//$response[$i] = array('id'=>$row['PromoID'],'text'=>$row['Name']);
				if ($row['isgroup'] == 1) {
					if ($id_group != 0 && $id_group != $row['PromoID']) {
						$response[$i - 1]['children'] = $children;
						$children = null;
						$x = 0;
					}
					$id_group = $row['PromoID'];
					$response[$i]['text'] = $row['Name'];
					$x = 0;
					$i++;
				} else {
					$children[$x]['id'] = $row['PromoID'];
					$children[$x]['text'] = $row['Name'];
					$x++;
				}
			}
			if ($x > 0)
				$response[$i - 1]['children'] = $children;
		}
//DebugToLog('',json_encode($response));
		header("Content-type: application/json;charset=utf-8");
		echo json_encode($response);
	}

	public function xxx_get_points_list() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
//WriteToLog($arg);
		// Номер запришиваемой страницы							$page = $_GET['page'];
		// Количество запрашиваемых записей						
		$limit = $rows;
		// Поле, по которому следует производить сортировку		$sidx = $_GET['sidx'];
		// Направление сортировки								$sord = $_GET['sord'];
		// Если не указано поле сортировки, то производить сортировку по первому полю
		if (!$sidx)
			$sidx = 1;
		$totalrows = isset($_REQUEST['totalrows']) ? $_REQUEST['totalrows'] : false;
		if ($totalrows) {
			$limit = $totalrows;
		}
//	$count = 1000;

		$result = Shop::GetPointsList($this->dbi, $param, $sidx, $sord, 1, 0, 0, $ClientID, $City, $Name);
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$count = $row[0];
		}
		while ($this->dbi->next_result())
			$this->dbi->store_result();
//DebugToLog("test param\n".$_SERVER['SCRIPT_FILENAME']." count: ",$count);
		// Рассчитаем сколько всего страниц займут данные в БД
		if ($count > 0 && $limit > 0) {
			$total_pages = ceil($count / $limit);
		} else {
			$total_pages = 0;
		}
		// Если по каким-то причинам клиент запросил
		if ($page > $total_pages)
			$page = $total_pages;
		// Рассчитываем стартовое значение для LIMIT
		$start = $limit * $page - $limit;
		// Зашита от отрицательного значения
		if ($start < 0)
			$start = 0;

		$result = Shop::GetPointsList($this->dbi, $param, $sidx, $sord, 0, $start, $limit, $ClientID, $City, $Name);
		// Создаем объект response
		$response->page = $page;
		$response->total = $total_pages;
		$response->records = $count;
		$i = 0;
		if($colNames == "ID,NameShort"){
			while ($row = $result->fetch_array(MYSQLI_BOTH)) {
				$response->rows[$i]['id'] = $row['ID'];
				$response->rows[$i]['cell'] = 
						array(	$row['ID'], 
								$row['NameShort']); //
				$i++;
			}
		}else{
			while ($row = $result->fetch_array(MYSQLI_BOTH)) {
				$response->rows[$i]['id'] = $row['ID'];
				$response->rows[$i]['cell'] = 
						array(	$row['ID'], 
								$row['Version'], 
								$row['AppVersion'], 
								$row['BalanceActivity'], 
								$row['1C'], 
								$row['NameShort'], 
								$row['NameValid'], 
								$row['City'], 
								$row['Address'], 
								$row['Telephone'], 
								$row['Label'], 
								$row['CountTerminal'], 
								$row['PriceType'], 
								$row['Matrix']); //
				$i++;
			}
		}
		// Перед выводом не забывайте выставить header с указанием типа контента и кодировки
		header("Content-type: application/json;charset=cp1251");
		echo json_encode($response);
	}

	public function goods_add_in_promo_action() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		echo Shop::AddGoodsToPromoAction($this->dbi, $promo_id, $source);
	}
	public function goods_add_in_promo() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		echo Shop::AddGoodsToPromo($this->dbi, $promo_id, $source);
	}
	public function goods_add2_in_promo() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if($oper == 'add'){
			$response->success = Shop::AddGoodsAndPriceToPromo($this->dbi, $promo_id, $Article, $PricePromo); //добавить товар в акцию и уст.цену
			$response->message = 'Возникла ошибка при внесении информации!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
	}
	public function edit_price() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		echo Shop::PromoEditPrice($this->dbi, $promoid, $id, $PricePromo);
	}
	public function del_goods_from_promo() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if ($oper == 'del') {
			$response->success = Shop::DelGoodsFromPromo($this->dbi, $promo_id, $source); //удаление товаров из категории
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
	}
	public function del_goods_from_promo_action() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if ($oper == 'del') {
			$response->success = Shop::DelGoodsFromPromoAction($this->dbi, $promo_id, $source); //удаление товаров из категории
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
	}
	public function del_cats_from_promo() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if ($oper == 'del') {
			$response->success = Shop::DelCatsFromPromo($this->dbi, $promo_id, $source); //удаление товаров из категории
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
	}
	public function del_cats_from_promo2() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		if ($oper == 'del') {
			$response->success = Shop::DelCatsFromPromo2($this->dbi, $promo_id, $source); //удаление товаров из категории
			$response->message = 'Возникла ошибка при удалении!<br>Сообщите разработчику!';
			$response->new_id = 0;
			echo json_encode($response);
		}
	}
	public function cats_add_in_promo() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		echo Shop::AddCatsToPromo($this->dbi, $promo_id, $source);
	}
	public function cats_add_in_promo2() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		echo Shop::AddCatsToPromo2($this->dbi, $promo_id, $source);
	}
	public function edit_discount() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		echo Shop::PromoEditDiscount($this->dbi, $promoid, $id, $DiscountPromo);
	}
	public function edit_discount_cat() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		echo Shop::PromoEditDiscountCat($this->dbi, $promoid, $id, $DiscountPromo);
	}
	public function edit_discount_cat2() {
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		echo Shop::PromoEditDiscountCat2($this->dbi, $promoid, $id, $DiscountPromo);
	}
	public function get_cats_list() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
//WriteToLog($arg);
		// Номер запришиваемой страницы							$page = $_GET['page'];
		// Количество запрашиваемых записей						
		$limit = $rows;
		// Поле, по которому следует производить сортировку		$sidx = $_GET['sidx'];
		// Направление сортировки								$sord = $_GET['sord'];
		// Если не указано поле сортировки, то производить сортировку по первому полю
		if (!$sidx)	$sidx = 1;
		$totalrows = isset($_REQUEST['totalrows']) ? $_REQUEST['totalrows'] : false;
		if ($totalrows) {
			$limit = $totalrows;
		}
//	$count = 1000;

		$result = Shop::GetCatsList($this->dbi, $param, $sidx, $sord, 1, 0, 0, $Name, $cat_id);
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$count = $row[0];
		}
		while ($this->dbi->next_result())
			$this->dbi->store_result();
//DebugToLog("test param\n".$_SERVER['SCRIPT_FILENAME']." count: ",$count);
		// Рассчитаем сколько всего страниц займут данные в БД
		if ($count > 0 && $limit > 0) {
			$total_pages = ceil($count / $limit);
		} else {
			$total_pages = 0;
		}
		// Если по каким-то причинам клиент запросил
		if ($page > $total_pages)
			$page = $total_pages;
		// Рассчитываем стартовое значение для LIMIT
		$start = $limit * $page - $limit;
		// Зашита от отрицательного значения
		if ($start < 0)
			$start = 0;

		$result = Shop::GetCatsList($this->dbi, $param, $sidx, $sord, 0, $start, $limit, $Name, $cat_id);
		// Создаем объект response
		$response->page = $page;
		$response->total = $total_pages;
		$response->records = $count;
		$i = 0;
		while ($row = $result->fetch_array(MYSQLI_BOTH)) {
			$response->rows[$i]['id'] = $row['ID'];
			$response->rows[$i]['cell'] = array($row['Name'], $row['DiscountPromo']); //
			//$response->rows[$i]['cell']=array($row['ID'],$row['Name']);//
			$i++;
		}
		// Перед выводом не забывайте выставить header с указанием типа контента и кодировки
		header("Content-type: application/json;charset=cp1251");
		echo json_encode($response);
	}
//discount card
	public function xxxdiscoundcard_save() {
		foreach ($_REQUEST as $arg => $val)
			${$arg} = $val;
		//Fn::debugToLog('test', 'cardid:' . $cardid . '	name:' . $name . '	dateOfIssue:' . $dateOfIssue.'	dateOfCancellation:'.  $dateOfCancellation);
		$response->success = Shop::SetDiscountCardInfo($this->dbi, 'info_edit', $cardid, $name, $dateOfIssue, $dateOfCancellation, $clientID, $address, $eMail, $phone, $animal, $startPercent, $startSum, $dopSum, $percentOfDiscount, $howWeLearn, $notes);
		$response->message = 'Возникла ошибка при внесении информации!<br>Сообщите разработчику!';
		$response->new_id = 0;
		echo json_encode($response);
		return;
	}

}
?>