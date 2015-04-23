<!-- Fixed navbar -->
<div class="navbar navbar-default navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
		<a class="navbar-brand pt10" href="..">
			<img class="img-rounded border1 h30 m0" src="../../img/logo.png">
			<?php echo $_SESSION['company']; ?>
		</a>
        <div class="navbar-collapse collapse">
<?php 
if ($_SESSION['access'] and $_SESSION['AccessLevel'] > 0) {
	$name = pathinfo($_SERVER['REQUEST_URI'], PATHINFO_FILENAME);
	$controller = pathinfo($_SERVER['REQUEST_URI'], PATHINFO_DIRNAME);
//	Fn::debugToLog('menu', $name.'	'.  $controller);
	if ($name == "category1" ||
		$name == "category2" ||
		$name == "category3" ||
		$name == "cat_partner3" ||
		$controller == "/category") {
		$active_menu1 = 'active';
	}else if (
		$name == "goods" || 
		$name == "barcodes" || 
		$name == "without_barcodes" || 
		$name == "barcode_verify" ||
		$name == "promo_tree" ||
		$name == "points" ||
		$name == "sellers" ||
		$name == "discountCards" ||
		$name == "user_list" || 
		$name == "promo_list" ||
		$controller == "/goods" ||
		$controller == "/lists" ||
		substr($name,0,13) == "promo_control"){
		$active_menu2 = 'active';
	}else if (
		$controller == "/reports"){
		$active_menu5 = 'active';
	}else if (
		$controller == "/reports_fin"){
		$active_menu9 = 'active';
	}else if (
		$controller == "/documents"){
		$active_menu10 = 'active';
	}else if (
		$controller == "/project") {
		$active_menu6 = 'active';
	}else if (
		$controller == "/helper") {
		$active_menu8 = 'active';
	} else if (
		$controller == "/documents") {
		$active_menu10 = 'active';
	}else if (
		$controller == "/task") {
		$active_menu7 = 'active';
	}
?>
			<ul class="nav navbar-nav">
				<li class="menu-item dropdown <?php echo $active_menu1;?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Категории<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/category/var1">Категории товаров 1</a></li>
						<li><a href="/category/var2">Категории товаров 2</a></li>
						<li><a href="/category/var3">Категории товаров 3</a></li>
						<li class="divider"></li>
						<li><a href="/category/cat_partner1">Категории партнеров вар. 1</a></li>
						<li><a href="/category/cat_partner3">Категории партнеров вар. 3</a></li>
						<li class="divider"></li>
						<li><a href="/category/cat_spent1">Категории "Статьи затрат" вар. 1</a></li>
						<li><a href="/category/cat_spent3">Категории "Статьи затрат" вар. 3</a></li>
					</ul>
				</li>
				<li class="<?php echo $active_menu2;?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Списки<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/goods">Список товаров</a></li>
						<li><a href="/goods/list_w_cost">Список товаров без себестоимости</a></li>
						<li class="divider"></li>
						<li><a href="/goods/barcodes">Список штрих-кодов</a></li>
						<li><a href="/goods/barcode_verify">Список ШК для проверки</a></li>
						<li><a href="/goods/without_barcodes">Список товаров без ШК</a></li>
						<li class="divider"></li>
						<li><a href="/lists/promo_tree">Новый список</a></li>
						<li><a href="/lists/points">Список магазинов</a></li>
						<li><a href="/lists/sellers">Список сотрудников</a></li>
						<li><a href="/lists/discountCards">Список дисконтных карт</a></li>
						<li><a href="/lists/user_list">Список пользователей</a></li>
						<li><a href="/lists/promo_list">Список акций</a></li>
						</ul>
				</li>
<!--				<li class="<?php echo $active_menu3;?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Акции<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/promo_tree">Дерево акций</a></li>
						<li><a href="promo_control_price">Акции: спец.цена</a></li>
						<li><a href="promo_control_disc_complect">Акции: Скидка, если комплект</a></li>
						<li><a href="promo_control_disc">Акции: Скидка, если кол-во</a></li>
						<li><a href="promo_control_disc_dop">Акции: Скидка на доп.товар</a></li>
						<li><a href="promo_control_gift_complect">Акции: Подарок, если комплект</a></li>
						<li><a href="promo_control_gift">Акции: Подарок, если кол-во</a></li>
					</ul>
				</li>-->
<!--				<li class="<?php echo $active_menu4; ?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Списки<b class="caret"></b></a>
					<ul class="dropdown-menu">
					</ul>
				</li>-->
<?php
if($_SESSION['AccessLevel'] >= 1000){
?>
				<li class="<?php echo $active_menu10; ?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Документы<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/documents/sale?operID=1">Расходные накладные</a></li>
						<li><a href="/documents/sale?operID=-1">Возвраты от покупателей</a></li>
						<li><a href="/documents/check_list">Список чеков</a></li>
					</ul>
				</li>
<?php 
}
?>
				<li class="<?php echo $active_menu5; ?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Отчеты<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/reports/report1">Отчет "Средняя сумма чека и среднее кол-во чеков"</a></li>
						<li><a href="/reports/report4">Отчет "Продажи товаров в рознице"</a></li>
						<li><a href="/reports/report7">Отчет "Продажи товаров в опте"</a></li>
						<li><a href="/reports/report5">Отчет "Товарный ассортимент"</a></li>
						<!--<li><a href="/reports/jqgrid3">Отчет №4</a></li>-->
					</ul>
				</li>
<?php
if ($_SESSION['AccessLevel'] >= 2000) {
?>
				<li class="<?php echo $active_menu9; ?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">Фин. отчеты<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/reports_fin/pendel">Profit and loss (Отчет о прибылях и убытках/доходах и расходах)</a></li>
						<li><a href="/reports_fin/cashflow">Cash Flow (Отчет о движении денежных средств)</a></li>
					</ul>
				</li>
<?php
}
?>

<!--                <li><a href="#about">About</a></li>
                <li><a href="#contact">Contact</a></li>-->
<!--                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
                    <ul class="dropdown-menu">
                        <li><a href="#">Action</a></li>
                        <li><a href="#">Another action</a></li>
                        <li><a href="#">Something else here</a></li>
                        <li class="divider"></li>
                        <li class="dropdown-header">Nav header</li>
                        <li><a href="#">Separated link</a></li>
                        <li><a href="#">One more separated link</a></li>
                    </ul>
                </li>-->
            </ul>
<?php
}
?>
            <ul class="nav navbar-nav navbar-right">
<!--                <li><a href="../navbar/">Default</a></li>-->
<?php
if($_SESSION['AccessLevel'] >= 1000){
?>
				<li class="<?php echo $active_menu8; ?>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown">HELP<b class="caret"></b></a>
					<ul class="dropdown-menu">
						<li><a href="/helper/control">Элементы управления</a></li>
						<li><a href="/helper/help">Помощь</a></li>
					</ul>
				</li>
				<li class="<?php echo $active_menu6; ?>"><a href="/project/list">Проекты</a></li>
				<li class="<?php echo $active_menu7; ?>"><a href="/task/list">Задачи</a></li>
<?php
}
?>
				<li class="navbar-text mb5 font12"><?php echo $_SESSION['UserName'].'<br>'. $_SESSION['UserPost'];?></li>
				<li class="active"><a href="/login/logout">Выход</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</div>
