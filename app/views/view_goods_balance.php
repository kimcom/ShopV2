<?php
$cnni = new Cnni();
if (isset($_REQUEST['goodid'])) {
	$GoodID = $_REQUEST['goodid'];
	$result = Shop::GetGoodInfo($cnni->getDbi(), 'good_info', $GoodID);
	$row = $result->fetch_array(MYSQLI_BOTH);
	$disabled = " disabled";
	$disabledAlik = " disabled";
	if ($_SESSION['AccessLevel'] >= 1000) {
		$disabled = "";
	}
	if ($_SESSION['UserID'] == 1) {
		$disabledAlik = "";
	}
}
?>
<script type="text/javascript">
    $(document).ready(function () {
	$("#dialog").dialog({
	    autoOpen: false, modal: true, width: 400,
	    buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
	    }}]
	});
});
</script>
<div class="min570">
	<div class='p5 ui-corner-all frameL border1' style='display:table;'>
		<legend>Карта товара:</legend>
		<label class='w90' for="Article">Артикул:</label>
		<input class='w310' id="Article" name="Article" minlength="5" type="text" value='<?php echo $row['Article']; ?>' required/>
		<p></p>
		<label class='w90' for="Name">Название:</label>
		<input class='w310' id="Name" name="Name" minlength="5" type="text" value='<?php echo $row['Name']; ?>' required/>
		<p></p>
		<div class='p5 ui-corner-all frameL ml5 border1 w200' style=''>
			<h3 class='font12 TAC mt0' >Характеристики:</h3>
			<label class='w80' for="Division">Отдел:</label>
			<input class='w80 TAR' id="Division" name="Division" minlength="1" type="text" value='<?php echo $row['Division']; ?>' required/>
			<p></p>
			<label class='w80' for="DiscountMax">Макс.скидка:</label>
			<input class='w80 TAR' id="DiscountMax" name="DiscountMax" minlength="1" type="text" value='<?php echo $row['DiscountMax']; ?>' required/>
			<p></p>
			<label class='w80' for="Unit_in_pack">К-во в упак.:</label>
			<input class='w80 TAR' id="Unit_in_pack" name="Unit_in_pack" minlength="1" type="text" value='<?php echo $row['Unit_in_pack']; ?>' required/>
			<p></p>
			<label class='w80' for="Unit">Ед.изм.:</label>
			<input class='w80 TAL' id="Unit" name="Unit" minlength="1" type="text" value='<?php echo $row['Unit']; ?>' required/>
			<p></p>
			<label class='w80' for="Weight">Вес (кг):</label>
			<input class='w80 TAR' id="Weight" name="Weight" minlength="1" type="text" value='<?php echo $row['Weight']; ?>' required/>
		</div>
		<div class='p5 ui-corner-all frame1 border1 w200' style='margin-left:215px;'>
			<h3 class='font12 TAC mt0' >Коды товара:</h3>
			<label class='w80' for="GoodID">GoodID:</label>
			<input class='w80 TAR' id="GoodID" name="GoodID" minlength="1" type="text" value='<?php echo $row['GoodID'] . '\' ' . $disabledAlik; ?>'/>
			<p></p>
			<label class='w80' for="OPT_ID">OPT_ID:</label>
			<input class='w80 TAR' id="OPT_ID" name="OPT_ID" minlength="1" type="text" value='<?php echo $row['OPT_ID'] . '\' ' . $disabled; ?>'/>
			<p></p>
			<label class='w80' for="SHOP_ID">SHOP_ID:</label>
			<input class='w80 TAR' id="SHOP_ID" name="SHOP_ID" minlength="1" type="text" value='<?php echo $row['SHOP_ID'] . '\' ' . $disabled; ?>'/>
			<p></p>
			<label class='w80' for="KIEV_ID">KIEV_ID:</label>
			<input class='w80 TAR' id="KIEV_ID" name="KIEV_ID" minlength="1" type="text" value='<?php echo $row['KIEV_ID'] . '\' ' . $disabled; ?>'/>
			<p style='height:30px;'></p>
		</div>
	</div>
	<div class='p5 ui-corner-all frameL ml10 border1' style='float:left;'>
		<legend>Остатки товара:</legend>
		<label class='w50' for="1">№ маг.:</label>
		<label class='w200' for="1">Магазин:</label><!--
		<input class='w80 TAR' id="PriceBase" name="PriceBase" minlength="1" type="text" value='<?php echo $row['PriceBase']; ?>' disabled/>-->
		<label class='w50' for="1">Дата нач. ост.</label>
		<label class='w50' for="1">Ост.нач.</label>
		<label class='w50' for="1">Приход</label>
		<label class='w50' for="1">Расход</label>
		<label class='w50' for="1">Ост.кон.</label>
		<p></p> 
		<?php
		while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
		$res2 = Shop::GetGoodInfo($cnni->getDbi(),'good_balance', $GoodID);
		while ($row2 = $res2->fetch_array(MYSQLI_BOTH)) {
			$labelID = $row2['ClientID'];
			$label = $row2['NameShort'];
			$price = $row2['PriceShop'];
			$balanceStart	= $row2['Balance'];
			$receipt		= $row2['Receipt'];
			$sale			= $row2['Sale'];
			$dateAct		= $row2['DateAct'];
			$balanceStop	= $balanceStart + $receipt - $sale;
		?>
		<label class='w50' for="PriceShop<?php echo $labelID; ?>"><?php echo $labelID; ?>:</label>
			<label class='w200' for="PriceShop<?php echo $labelID; ?>"><?php echo $label; ?>:</label>
<!--			<input class='w80 TAR' id="PriceShop<?php echo $labelID; ?>" name="PriceShop<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $price; ?>' disabled>-->
			<input class='w70 TAC' id="dateAct<?php echo $labelID; ?>" name="dateAct<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $dateAct; ?>' disabled>
			<input class='w50 TAR' id="balanceStart<?php echo $labelID; ?>" name="balanceStart<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $balanceStart; ?>' disabled>
			<input class='w50 TAR' id="receipt<?php echo $labelID; ?>" name="receipt<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $receipt; ?>' disabled>
			<input class='w50 TAR' id="sale<?php echo $labelID; ?>" name="sale<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $sale; ?>' disabled>
			<input class='w50 TAR' id="balanceStop<?php echo $labelID; ?>" name="balanceStop<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $balanceStop; ?>' disabled>
			<p></p>
		<?php
	}
	?>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
