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
	var source_id_cut = 0;
	var source_id_copy = 0;

	$("#dialog").dialog({
	    autoOpen: false, modal: true, width: 400,
	    buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
		    }}]
	});

// Creating grid1
	$("#grid1").jqGrid({
	    sortable: true,
	    url: "../goods/good_barcode?param=goods_barcode_short&GoodID=<?php echo $GoodID; ?>",
	    datatype: "json",
	    height: 'auto',
	    colNames: ['Штрих-код'],
	    colModel: [
		{name: 'EAN13', index: 'EAN13', width: 299, sorttype: "text", search: false, editable: true, edittype: "text"}
	    ],
	    width: 'auto',
	    shrinkToFit: false,
	    rowNum: 5,
	    rowList: [5, 10, 20],
	    sortname: "EAN13",
	    viewrecords: true,
	    gridview: true,
	    toppager: true,
	    caption: "Штрих-коды",
	    pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: true, del: true, search: false, refresh: true, cloneToTop: true},
	{//edit
	}, {//add
	    modal: true,
	    closeOnEscape: true,
	    closeAfterAdd: true,
	    reloadAfterSubmit: true,
	    beforeInitData: function () {
		if ($("#GoodID").val() == '')
		    return false;
		$("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + $("#GoodID").val()});
	    },
	    afterSubmit: function (json, postdata) {
		var result = $.parseJSON(json.responseText);
		return [result.success, result.message, result.new_id, result.name];
	    },
	    savekey: [true, 13]
	}, {//del
	    modal: true,
	    closeOnEscape: true,
	    closeAfterAdd: true,
	    reloadAfterSubmit: true,
	    beforeInitData: function () {
		if ($("#GoodID").val() == '')
		    return false;
		$("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + $("#GoodID").val()});
	    },
	    afterSubmit: function (json, postdata) {
		var result = $.parseJSON(json.responseText);
		return [result.success, result.message, result.new_id, result.name];
	    },
	    savekey: [true, 13]
	}
	);

	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	$("#button_save").click(function () {
	    if ($("#GoodID").val() == '') return;
	    $.post('../goods/good_info_save', {
		goodid: $("#GoodID").val(),
		Article: $("#Article").val(),
		Name: $("#Name").val(),
		Division: $("#Division").val(),
		DiscountMax: $("#DiscountMax").val(),
		Unit_in_pack: $("#Unit_in_pack").val(),
		Unit: $("#Unit").val(),
		Weight: $("#Weight").val()
	    },
	    function (data) {
		if (data == false) {
		    $("#dialog>#text").html('Возникла ошибка при сохранении изменений.<br><br>Сообщите разработчику!');
		    $("#dialog").dialog("open");
		} else {
		    $("#dialog>#text").html('Данные успешно сохранены!');
		    $("#dialog").dialog("open");
		}
	    }
	    );
	});
	$("#button_save_synchro").click(function () {
	    if ($("#GoodID").val() == '')
		return;
	    $.post('../goods/synchro_info_save', {
		goodid: $("#GoodID").val(),
		OPT_ID: $("#OPT_ID").val(),
		SHOP_ID: $("#SHOP_ID").val(),
		KIEV_ID: $("#KIEV_ID").val()
	    },
	    function (data) {
		if (data == false) {
		    $("#dialog>#text").html('Возникла ошибка при сохранении кодов товаров.<br><br>Сообщите разработчику!');
		    $("#dialog").dialog("open");
		} else {
		    $("#dialog>#text").html('Коды товара успешно сохранены!');
		    $("#dialog").dialog("open");
		}
	    }
	    );
	});
	//список проектов для выезжающей вкладки
	fsL = 0;
	// Creating gridL
	$("#gridL").jqGrid({
		sortable: true,
		url: "../goods/list?param=goods_list_where&col=goods_list&cat_id=0",
//		url: "../engine/jqgrid3?action=project_list&f1=ProjectID&f2=Name&pr.Status<>1000",
		datatype: "json",
		height: '500',
	    colNames: ['GoodID', 'OPT_ID', 'SHOP_ID', 'KIEV_ID', 'Артикул', 'Название'],
		colModel: [
			{name: 'GoodID', index: 'GoodID', hidden: true},
			{name: 'OPT_ID', index: 'OPT_ID', hidden: true},
			{name: 'SHOP_ID', index: 'SHOP_ID', hidden: true},
			{name: 'KIEV_ID', index: 'KIEV_ID', hidden: true},
			{name: 'Article', index: 'Article', width: 60, sorttype: "text", search: true, editable: true, edittype: "text"},
		    {name: 'Name', index: 'Name', width: 150, sorttype: "text", search: true},
//		    {name: 'pr_ProjectID', index: 'pr.ProjectID', width: 50, align: "center", sorttype: "text", search: true},
//		    {name: 'pr_Name', index: 'pr.Name', width: 120, align: "left", sorttype: "text", search: true},
		],
		gridComplete: function () {if (!fsL) {fsL = 1; filter_restore("#gridL");}},
		onSelectRow: function (rowid, status, e) {
			var id = rowid;
			if (id != null) {
			    window.location = "../goods/good_edit?goodid=" + id;
			} else {
				$("#dialog>#text").html('Сначала выберите запись в таблице!');
				$("#dialog").dialog("open");
			}
		},
		width: '190',
		shrinkToFit: true,
		rowNum: 100,
	    sortname: "Article,Name",
		sortorder: "asc",
		editurl: '../project/operation',
		pager: '#pgridL'
	    });
	    $("#gridL").jqGrid('navGrid', '#pgridL', {edit: false, add: false, del: false, search: false, refresh: false, cloneToTop: false});
	    $("#gridL").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#gridL");
	}});
	$("#pgridL").remove();
	$("#rs_mgridL").remove();
	$("#gbox_gridL").removeClass('ui-corner-all');
	$("#gview_gridL .ui-jqgrid-titlebar").remove();
});
</script>
<div class="container center min570">
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
		<div class='p5 ui-corner-all frameL ml5 mt5 border0 w180' style=''>
			<p style='text-align:center;'>
				<input id='section' type='hidden' value='<?php echo $_REQUEST['section'] ?>'/>
				<button id="button_save" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus" style='align:center;'>
					<span class="ui-button-text" style='width:130px;'>Сохранить</span>
				</button>
			</p>
		</div>
		<div class='p5 ui-corner-all frame1 ml5 mt5 border0 w200' style='margin-left:215px;'>
			<p style='text-align:center;'>
				<button id="button_save_synchro" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus" style='align:center;'>
					<span class="ui-button-text" style='width:130px;'>Сохранить коды</span>
				</button>
			</p>
		</div>
	</div>
	<div class='p5 ui-corner-all frameL ml10 border1' style='float:left;'>
		<h3 class='font12 TAC mt0' >Цены товара:</h3>
		<label class='w50' for="PriceBase">№ маг.:</label>
		<label class='w200' for="PriceBase">Цена опт.:</label>
		<input class='w80 TAR' id="PriceBase" name="PriceBase" minlength="1" type="text" value='<?php echo $row['PriceBase']; ?>' disabled/>
<!--		<label class='w50' for="1">Дата ост.</label>
		<label class='w50' for="1">Ост.нач.</label>
		<label class='w50' for="1">Продано</label>
		<label class='w50' for="1">Ост.кон.</label>-->
		<p></p> 
		<?php
		while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
		$res2 = Shop::GetGoodInfo($cnni->getDbi(),'good_price', $GoodID);
		while ($row2 = $res2->fetch_array(MYSQLI_BOTH)) {
			$labelID = $row2['ClientID'];
			$label = $row2['NameShort'];
			$price = $row2['PriceShop'];
//			$balanceStart = $row2['Balance'];
//			$sale	 = $row2['Sale'];
//			$dateAct = $row2['DateAct'];
//			$balanceStop = $balanceStart - $sale;
		?>
		<label class='w50' for="PriceShop<?php echo $labelID; ?>"><?php echo $labelID; ?>:</label>
			<label class='w200' for="PriceShop<?php echo $labelID; ?>"><?php echo $label; ?>:</label>
			<input class='w80 TAR' id="PriceShop<?php echo $labelID; ?>" name="PriceShop<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $price; ?>' disabled/>
<!--			<input class='w70 TAC' id="dateAct<?php echo $labelID; ?>" name="dateAct<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $dateAct; ?>' disabled/>
			<input class='w50 TAR' id="balanceStart<?php echo $labelID; ?>" name="balanceStart<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $balanceStart; ?>' disabled/>
			<input class='w50 TAR' id="sale<?php echo $labelID; ?>" name="sale<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $sale; ?>' disabled/>
			<input class='w50 TAR' id="balanceStop<?php echo $labelID; ?>" name="balanceStop<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $balanceStop; ?>' disabled/>-->
			<p></p>
		<?php
	}
	?>
	</div>
	<div class='ui-corner-all frameL ml5 border0' style=''>
		<div class='ui-corner-all frame1 ml5 border0' style=''>
			<table id="grid1"></table>
			<div id="pgrid1"></div>
		</div>
		<div class='p5 ui-corner-all frame1 ml5 mt10 border1 w300' style=''>
			<h3 class='font12 TAC mt0' >Акции на товар:</h3>
			<?php
			while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
			$res3 = Shop::GetGoodInfo($cnni->getDbi(),'good_promo', $GoodID);
			while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
				$labelID = $row3['PromoID'];
				$label = $row3['Name'];
				//$price = $row3['PriceShop'];
				?>
				<label class='w30' for="cat_<?php echo $labelID; ?>"><?php echo $labelID; ?>:</label>
				<input class='w240 TAL border1' id="cat_<?php echo $labelID; ?>" name="cat_<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $label; ?>' disabled/>
				<p></p>
				<?php
			}
			?>
		</div>
		<div class='p5 ui-corner-all frame1 ml5 mt10 border1 w300' style=''>
			<h3 class='font12 TAC mt0' >Подарок для акции:</h3>
			<?php
			while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
			$res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_promo_action', $GoodID);
			while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
				$labelID = $row3['PromoID'];
				$label = $row3['Name'];
				//$price = $row3['PriceShop'];
				?>
				<label class='w30' for="cat_<?php echo $labelID; ?>"><?php echo $labelID; ?>:</label>
				<input class='w240 TAL' id="cat_<?php echo $labelID; ?>" name="cat_<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $label; ?>' disabled/>
				<p></p>
				<?php
			}
			?>
		</div>
		<div class='p5 ui-corner-all frame1 ml5 mt10 border1 w300' style=''>
			<h3 class='font12 TAC mt0' >Категории товара:</h3>
			<?php
			while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
			$res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_cats', $GoodID);
			while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
				$labelID = $row3['CatID'];
				$label = $row3['Name'];
				//$price = $row3['PriceShop'];
				?>
				<label class='w30' for="cat_<?php echo $labelID; ?>"><?php echo $labelID; ?>:</label>
				<input class='w240 TAL' id="cat_<?php echo $labelID; ?>" name="cat_<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $label; ?>' disabled/>
				<p></p>
		<?php
	}
	?>
		</div>
	</div>
</div>
<div id="lpanel_button" class="border0">
	<div style="padding-left: 10px; padding-top: 10px; padding-bottom: 10px;width: 1ch; text-align: center; word-wrap: break-word;">Список&nbsp;товаров</div>
	<div id="lpanel" class="border0">
		<h4>Список товаров</h4>
		<div id='div1' class='frameL pl5' >
			<table id="gridL"></table>
			<div id="pgridL"></div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
