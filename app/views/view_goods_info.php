<?php
$cnni = new Cnni();
$cnn = new Cnn();
if (isset($_REQUEST['goodid'])) {
    $GoodID = $_REQUEST['goodid'];
//    $result = Shop::GetGoodInfo($cnni->getDbi(), 'good_info', $GoodID);
//    $row = $result->fetch_array(MYSQLI_BOTH);
	$row = $cnn->good_info();
	if (!$row) return;
	$TypeSticker = $row['TypeSticker'];
	if ($TypeSticker==null) $TypeSticker = -1;
	$FoldOrder	 = $row['FoldOrder'];
	if ($FoldOrder==null) $FoldOrder = -1;
	$Visible	 = $row['Visible'];
	if ($Visible==null) $Visible = -1;
	$VisibleInOrder = $row['VisibleInOrder'];
	if ($VisibleInOrder ==null) $VisibleInOrder = -1;
	$disabled    = " disabled";
    if ($_SESSION['AccessLevel'] >= 1000) {
        $disabled = "";
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
		datatype: "json",
		height:400,
		colNames: ['Штрих-код'],
		colModel: [
			{name: 'EAN13', index: 'EAN13', width: 500, sorttype: "text", search: false, editable: true, edittype: "text"}
		],
		width: 'auto',
		shrinkToFit: false,
		rowNum: 10,
		rowList: [10, 20, 100],
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
			if ($("#GoodID").html() == '')
				return false;
			$("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + $("#GoodID").html()});
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
			if ($("#GoodID").html() == '')
				return false;
			$("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + $("#GoodID").html()});
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
	$("#grid1").gridResize();

	$("#button_save").click(function () {
		if ($("#GoodID").html() == '')
			return;
		$.post('../goods/synchro_info_save', {
			goodid: $("#GoodID").html(),
			OPT_ID: $("#OPT_ID").val(),
			SHOP_ID: $("#SHOP_ID").val(),
			KIEV_ID: $("#KIEV_ID").val()
		},
		function (data) {
			if (data == false) {
				$("#dialog>#text").html('Возникла ошибка при сохранении кодов товаров.<br><br>Сообщите разработчику!');
				$("#dialog").dialog("open");
		    }
	    });
		$.post('../goods/good_info_save', {
			goodid: $("#GoodID").html(),
			article: $("#article").val(),
			name: $("#name").val(),
			namestickers : $("#namestickers ").val(),
			unit: $("#unit").val(),
			trademark: $("#trademark").val(),
			countryproducer: $("#countryproducer").val(),
			typesticker: $("#select_type_sticker").select2("val"),
			packtype: $("#packtype").val(),
			packmaterial : $("#packmaterial ").val(),
			foldorder: $("#select_fold_order").select2("val"),
			segment: $("#segment").val(),
			visible: $("#select_visible").select2("val"),
			visibleinorder: $("#select_visible_in_order").select2("val"),
			service: $("#service").val(),
			division: $("#division").val(),
			length: $("#length").val(),
			width: $("#width").val(),
			height: $("#height").val(),
			weight: $("#weight").val(),
			unit_in_pack: $("#unitinpack").val(),
			perioddelivery: $("#perioddelivery").val(),
			discountmax: $("#discountmax").val()
		},
		function (data) {
			$("#dialog>#text").html(data.message);
			$("#dialog").dialog("open");
			if (data.new_id > 0) {
				$("#projectid").val(data.new_id);
				$("#projectid_span").html(data.new_id);
			}
		},"json");
	});
	// цена
	$("#grid2").jqGrid({
		sortable: true,
		datatype: "json",
		height:400,
		colNames:['Код','Торговая точка','Город','Цена'],
		colModel: [
			{name: 'c_ClientID', index: 'c.ClientID',	width: 60,	align: "center", sorttype: "text", search: true},                         
			{name: 'c_NameShort',index: 'c.NameShort',  width: 250, sorttype: "text", search: true},
			{name: 'c_City',     index: 'c.City',       width: 150,  sorttype: "text", search: true},
			{name: 'p_Price',    index: 'p.Price',		width: 70,  align: "right",  search: false},
		],
		width: 546,
		shrinkToFit: false,
		rowNum: 20,
		rowList: [20, 30, 40, 50, 100],
		sortname: "ClientID",
		viewrecords: true,
		gridview: true,
		toppager: true,
		caption: "Цена по торговым точкам",
		pager: '#pgrid2',
	});
	$("#grid2").jqGrid('navGrid', '#pgrid2', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid2").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});

	$("#pg_pgrid2").remove();
	$("#pgrid2").removeClass('ui-jqgrid-pager');
	$("#pgrid2").addClass('ui-jqgrid-pager-empty');

	$("#grid2").gridResize();
    
	//остатки
//	$("#grid3").jqGrid({
//        sortable: true,
//		datatype: "json",
//		height:400,
//	    colNames:['Код','Торговая точка','Город','Дата. нач.ост','Ост. нач.','Приход','Расход','Ост.кон.'],
//		colModel: [
//			{name: 'ClientID',          index: 'ClientID',         width: 60,  align: "center",    sorttype: "text", search: true},
//			{name: 'NameShort',         index: 'NameShort',        width: 250, sorttype: "text",   search: true},
//			{name: 'c_City',			index: 'c.City',		   width: 150, sorttype: "text", search: true},
//			{name: 'DataBalanceStart',  index: 'DataBalanceStart', width: 80,  sorttype: "data",   search: true},
//			{name: 'BalanceStart',      index: 'BalanceStart',     width: 80,  sorttype: "number", search: true},
//			{name: 'Receipt',           index: 'Receipt',          width: 70,  align: "center",    sorttype: "number",  search: true},
//			{name: 'Sale',              index: 'Sale',             width: 70,  align: "center",    sorttype: "number",  search: true},
//			{name: 'BalanceStop',       index: 'BalanceStop',      width: 70,  align: "center",    sorttype: "number",  search: true},
//		],
//		width: 700,
//		shrinkToFit: false,
//		rowNum: 20,
//		rowList: [20, 30, 40, 50, 100],
//		sortname: "ClientID",
//		viewrecords: true,
//		gridview: true,
//		toppager: true,
//		caption: "Остатки товара",
//		pager: '#pgrid3',
//	});
//	$("#grid3").jqGrid('navGrid', '#pgrid3', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
//	$("#grid3").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
//	$("#pg_pgrid3").remove();
//	$("#pgrid3").removeClass('ui-jqgrid-pager');
//	$("#pgrid3").addClass('ui-jqgrid-pager-empty');
//	$("#grid3").gridResize();       

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
		],
		gridComplete: function () {if (!fsL) {fsL = 1; filter_restore("#gridL");}},
		onSelectRow: function (rowid, status, e) {
			var id = rowid;
			if (id != null) {
				window.location = "../goods/good_info?goodid=" + id;
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
	$("#gridL").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {
			filter_save("#gridL");
		}});
	$("#pgridL").remove();
	$("#rs_mgridL").remove();
	$("#gbox_gridL").removeClass('ui-corner-all');
	$("#gview_gridL .ui-jqgrid-titlebar").remove();
        
	$('#myTab a').click(function (e) {
		e.preventDefault();
		if (this.id == 'a_tab_barcode') {
			$("#grid1").jqGrid('setGridParam', { url: "../goods/good_barcode?param=goods_barcode_short&GoodID=<?php echo $GoodID; ?>", page: 1});
			$("#grid1").trigger('reloadGrid');
		}
		if (this.id == 'a_tab_price') {
			$("#grid2").jqGrid('setGridParam', {url:"../engine/jqgrid3?action=good_price&p.GoodID=<?php echo $GoodID; ?>&f1=ClientID&f2=NameShort&f3=City&f4=PriceShop", page: 1});
			$("#grid2").trigger('reloadGrid');
		}
        if (this.id == 'a_tab_balance') {
//			$("#grid3").jqGrid('setGridParam', {url:"../engine/jqgrid3?action=point_list_full&f1=ClientID&f2=NameShort&f3=City&f4=DataBalanceStart&f5=BalanceStart&f6=Receipt&f7=Sale&f8=BalanceStop", page: 1});
//			$("#grid3").trigger('reloadGrid');
		}
	});
 // select
	var a_status = [{id: 10, text: 'стикер'},  {id: 20, text: 'ценовая планка'}];
	$("#select_type_sticker").select2({data: a_status, placeholder: "Выберите тип ценника"});
	$("#select_type_sticker").select2("val", <?php echo $TypeSticker; ?>); 

	var a_status = [{id: 10, text: 'заказ только упаковкой'},  {id: 20, text: 'заказ по-штучно'}];
	$("#select_fold_order").select2({data: a_status, placeholder: "Выберите кратность для заказа"});
	$("#select_fold_order").select2("val", <?php echo $FoldOrder; ?>); 

	var a_status = [{id: 1, text: 'товар доступен'},  {id: 0, text: 'товар не доступен'}];
	$("#select_visible").select2({data: a_status, placeholder: "Выберите доступность товара через поиск"});
	$("#select_visible").select2("val", <?php echo $Visible; ?>); 
	
	var a_status = [{id: 1, text: 'товар доступен'},  {id: 0, text: 'товар не доступен'}];
	$("#select_visible_in_order").select2({data: a_status, placeholder: "Выберите доступность товара а заказе"});
	$("#select_visible_in_order").select2("val", <?php echo $VisibleInOrder; ?>); 
	
//	setTimeout(function(){
//		$("#grid3").jqGrid('setGridParam', {url:"../engine/jqgrid3?action=point_list_full&f1=ClientID&f2=NameShort&f3=DataBalanceStart&f4=BalanceStart&f5=Receipt&f6=Sale&f7=BalanceStop", page: 1});
//		$("#grid3").trigger('reloadGrid');
//		$("#a_tab_balance").click();
//	}, 100);
});
</script>
<style>
        #feedback { font-size: 12px; }
        .selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
        .selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
    <ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
        <li class="active">
            <a id="a_tab_filter" href="#tab_filter" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Информация о товаре<?php echo $VisibleInOrder; ?></legend>
            </a>
        </li>
        
        <li>
            <a id="a_tab_barcode" href="#tab_barcode" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Штрих-коды</legend>
            </a>
        </li>
        <li>
            <a id="a_tab_price" href="#tab_price" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Цены</legend>
            </a>
        </li>
        <li>
            <a id="a_tab_category" href="#tab_category" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Категории</legend>
            </a>
        </li>
<!--        <li>
            <a id="a_tab_balance" href="#tab_balance" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Остатки</legend>
            </a>
        </li>-->
        <li>
            <a id="a_tab_promo" href="#tab_promo" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Акции</legend>
            </a>
        </li>
    </ul>
    <div class="floatL">
        <button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
            <span class="ui-button-text" style='width:120px;height:22px;'>Сохранить данные</span>
        </button>
    </div>
    <div class="tab-content">
        <div class="active tab-pane min530 m0 w100p ui-corner-tab1 borderTop1 borderColor frameL border1" id="tab_filter">
            <div class='p5 ui-corner-all frameL border0 w600' style='display:table;'>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Артикул:</span>
                    <input id="article" type="text" class="form-control TAL" value='<?php echo $row['Article']; ?>'>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Наименование:</span>
                    <input id="name" type="text" class="form-control TAL" value='<?php echo ($row['Name']); ?>'>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Наим. для ценника:</span>
                    <input id="namestickers" type="text" class="form-control TAL" value='<?php echo $row['NameSticker']; ?>'>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Ед. измерения</span>
                    <input id="unit" type="text"  class="form-control TAL" value="<?php echo $row['Unit']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Торговая марка:</span>
                    <input id="trademark" type="text" class="form-control TAL" value='<?php echo $row['Trademark']; ?>'>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Страна производ.:</span>
                    <input id="countryproducer" type="text" class="form-control TAL" value="<?php echo $row['CountryProducer']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Тип ценника</span>
                    <div class="w100p" id="select_type_sticker"></div>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Вид осн. тары</span>
                    <input id="packtype" type="text"  class="form-control TAL" value="<?php echo $row['PackType']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Материал осн. тары</span>
                    <input id="packmaterial" type="text"  class="form-control TAL" value="<?php echo $row['PackMaterial']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Кратность заказа</span>
                    <div class="w100p" id="select_fold_order"></div>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Сегмент ассортим.</span>
                    <input id="segment" type="text"  class="form-control TAL" value="<?php echo $row['Segment']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Доступ в поиске</span>
                    <div class="w100p" id="select_visible"></div>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Доступ в заказе</span>
                    <div class="w100p" id="select_visible_in_order"></div>
                    <span class="input-group-addon w32"></span>
                </div>
            </div>
            <div class='p5 ui-corner-all frameL ml10 border0 w300' style='float:left;'>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Услуга</span>
					<span  class="input-group-addon w128 TAС" style="padding-top: 5px; padding-bottom: 0px;">
						<input id="service" type="checkbox" value="<?php echo $row['Service']; ?>">
					</span>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">Код товара</span>
                    <span id="GoodID" class="input-group-addon form-control TAR"><?php echo $row['GoodID']; ?></span>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">OPT_ID:</span>
                    <input id="OPT_ID" name="OPT_ID" type="text" class="form-control TAR" value="<?php echo $row['OPT_ID']  . '" ' . $disabled;?>>
                    <span class="input-group-addon w32"></span>
                </div>               
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">SHOP_ID:</span>
                    <input id="SHOP_ID" name="SHOP_ID" type="text" class="form-control TAR" value="<?php echo $row['SHOP_ID']  . '" ' . $disabled;?>>
                    <span class="input-group-addon w32"></span>
                </div>       
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w130 TAL">KIEV_ID:</span>
                    <input id="KIEV_ID" name="kiev_id" type="text" class="form-control TAR" value="<?php echo $row['KIEV_ID']  . '" ' . $disabled; ?>>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Отдел</span>
                    <input id="division" type="text"  class="form-control TAR" value="<?php echo $row['Division']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Длина</span>
                    <input id="length" type="text"  class="form-control TAR" value="<?php echo $row['Length']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Ширина</span>
                    <input id="width"  type="text"  class="form-control TAR" value="<?php echo $row['Width']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Высота</span>
                    <input id="height" type="text"  class="form-control TAR" value="<?php echo $row['Height']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Вес </span>
                    <input id="weight" name="weight"  type="text"  class="form-control TAR" value="<?php echo $row['Weight']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">В упаковке</span>
                    <input id="unitinpack" type="text"  class="form-control TAR" value="<?php echo $row['Unit_in_pack']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Период поставок(дн)</span>
                    <input id="perioddelivery"  type="text"  class="form-control TAR" value="<?php echo $row['PeriodDelivery']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w130 TAL">Макс. скидка</span>
                    <input id="discountmax"  type="text"  class="form-control TAR" value="<?php echo $row['DiscountMax']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
            </div>
		</div>
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_barcode">
			<div class='p5'>
				<table id="grid1"></table>
                <div id="pgrid1"></div>
            </div>
        </div> 
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_price">
			<div class='p5'>
				<table id="grid2"></table>
				<div id="pgrid2"></div>
			</div>
        </div>
<!--        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_balance">
			<div class='p5'>
				<table id="grid3"></table>
                <div id="pgrid3"></div>
            </div>
        </div>-->
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1 p5" id="tab_category">
            <table class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead><tr><th colspan="3"><h4 class='TAC mt10' >Категории товара:</h4></th></tr>
                    <tr><th>Код</th><th>Название</th><th>Полное имя</th></tr>
                </thead>
                <tbody>
<?php
    while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
    $res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_cats', $GoodID);
    while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
        $labelID = $row3['CatID'];
        $label = $row3['Name'];
        $label2 = $row3['FullName'];
        ?>
                    <tr>
                        <td class="TAC"><?php echo $labelID; ?></td>
                        <td class="TAL"><?php echo $label; ?></td>
                        <td class="TAL"><?php echo $label2; ?></td>
                    </tr>
<?php
    }
?>
                </tbody>
            </table>
        </div>
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1 p10" id="tab_promo">
            <table class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead><tr><th colspan="3"><h4 class='TAC mt10' >Акции на товар:</h4></th></tr>
                <tr><th>Код</th><th>Название</th><th>Полное имя</th></tr>
                </thead>
                <tbody>
<?php
    while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
    $res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_promo', $GoodID);
    while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
        $labelID = $row3['PromoID'];
        $label = $row3['Name'];
        $label2 = $row3['FullName'];
?>
                    <tr>
                        <td class="TAC"><?php echo $labelID; ?></td>
                        <td class="TAL"><?php echo $label; ?></td>
                        <td class="TAL"><?php echo $label2; ?></td>
                    </tr>
<?php
            }
?>
                </tbody>
            </table>
            <br>
            <table class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead><tr><th colspan="3"><h4 class='TAC mt10' >Подарок для акции:</h4></th></tr>
                <tr><th>Код</th><th>Название</th><th>Полное имя</th></tr>
                </thead>
                <tbody>
<?php
                    while ($cnni->getDbi()->next_result())
                        $cnni->getDbi()->store_result();
                    $res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_promo_action', $GoodID);
                    while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
                        $labelID = $row3['PromoID'];
                        $label = $row3['Name'];
                        $label2 = $row3['FullName'];
?>
                        <tr>
                            <td class="TAC"><?php echo $labelID; ?></td>
                            <td class="TAL"><?php echo $label; ?></td>
                            <td class="TAL"><?php echo $label2; ?></td>
                        </tr>
<?php
}
?>
                </tbody>
            </table>

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
<div id="inputbox2" title="ВНИМАНИЕ!">
    <p id='text'></p>
</div>

</div>
