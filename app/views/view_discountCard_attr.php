<?php
$cnn = new Cnn();
if (isset($_REQUEST['cardid'])) {
	$cardid = $_REQUEST['cardid'];
	$row = $cnn->discountcard_info();
} else {
	echo "не могу найти параметр cardid";
	return;
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
	$("#inputbox").dialog({
			autoOpen: false, modal: true, width: 285,
			buttons: [{
			text: "Сохранить",
			width: 80,
			click: function() {
				$.post('../engine/discoundcard_history', {
					oper: 'add',
					cardid: $("#cardid").val(),
					checkid: $("#checkid").val()
				},
				function (data) {
				    $("#dialog>#text").html(data.sql_message);
				    $("#dialog").dialog("open");
					if(data.success)$("#grid1").trigger('reloadGrid');
				}, "json");
			$( this ).dialog( "close" );
		}},{
			text: "Отмена", 
			width: 80,
			click: function () {$(this).dialog("close");}
		}]
	});
	$("#inputbox2").dialog({
			autoOpen: false, modal: true, width: 285,
			buttons: [{
			text: "Выполнить",
			width: 80,
			click: function() {
				$.post('../engine/discoundcard_history', {
					oper: 'del',
					cardid: $("#cardid").val(),
					checkid: $("#checkid").val()
			    },
			    function (data) {
				$("#dialog>#text").html(data.sql_message);
				$("#dialog").dialog("open");
				if (data.success)
				    $("#grid1").trigger('reloadGrid');
			    }, "json");
			    $(this).dialog("close");
			}}, {
			text: "Отмена",
			width: 80,
			click: function () {
			    $(this).dialog("close");
			}
		}]
	});
//	console.log($("#DT_cancellation").val());
	$("#button_save").click(function () {
		if ($("#cardid").val() == '') return;
		$.post('../engine/discoundcard_save', {
			cardid: $("#cardid").val(),
			name: $("#name").val(),
			dateOfIssue: $("#DT_issue").val(),
			dateOfCancellation: $("#DT_cancellation").val(),
			clientID: $("#select_point").val(),
			address: $("#address").val(),
			eMail: $("#eMail").val(),
			phone: $("#phone").val(),
			animal: $("#animal").val(),
			startPercent: $("#startPercent").val(),
			startSum: $("#startSum").val(),
			dopSum: $("#dopSum").val(),
			percentOfDiscount: $("#percentOfDiscount").val(),
			howWeLearn: $("#howWeLearn").val(),
			notes: $("#notes").val()
			},
			function (data) {
			$("#dialog>#text").html(data.message);
			$("#dialog").dialog("open");
		}, "json");
	});

	// выбор магазина 
	$.post('../Engine/select2?action=point', function (json) {
		$("#select_point").select2({multiple: false, placeholder: "Выберите магазин", data: {results: json, text: 'text'}});
		$("#select_point").select2("val", "<?php echo $row['ClientID']; ?>");
	});

	// выбор даты выдачи 
	$("#DT_issue").datepicker({numberOfMonths: 1, dateFormat: 'dd/mm/yy', showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#DT_cancellation").datepicker({numberOfMonths: 1, dateFormat: 'dd/mm/yy', showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#datapickers a").click(function () {
		if ($(this).attr("type") != 'button')
		return;
		var command = this.parentNode.previousSibling.previousSibling;
		if (command.tagName == 'SPAN')
		command = command.previousSibling.previousSibling;
		if (command.tagName == "INPUT")
		operid = command.id;
		if ($(this).html() == 'X')
		$("#" + operid).val("");
		if ($(this).html() == '...')
		$("#" + operid).datepicker("show");
	});
//************************************//
	fs = 0;
// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		datatype: "json",
		height: 'auto',
		colNames: ['Код чека', 'Дата', 'Магазин', 'Тип оплаты', 'Сумма без скидки', 'Скидка', 'К оплате'],
		colModel: [
			{name: 'cl_CheckID', index: 'cl.CheckID', width: 80, align: "center", sorttype: "number", search: true},
			{name: 'cl_CloseDateTime', index: 'cl.CloseDateTime', width: 120, align: "center", sorttype: "date", search: true},
			{name: 'c_NameShort', index: 'c.NameShort', width: 300, align: "left", sorttype: "text", search: true},
			{name: 'cl_TypePayment', index: 'cl.TypePayment', width: 80, align: "center", stype: 'select', searchoptions: {value: ":любой;1:без нал.;0:нал."}},
			{name: 'SumFull', index: 'SumFull', width: 100, align: "right", sorttype: "number", search: false},
			{name: 'SumDiscount', index: 'SumDiscount', width: 100, align: "right", sorttype: "number", search: false},
			{name: 'Sum', index: 'Sum', width: 100, align: "right", sorttype: "number", search: false},
		],
		gridComplete: function () {if (!fs) {fs = 1; filter_restore("#grid1");}},
		width: 'auto',
		shrinkToFit: false,
//		loadonce: true,
//		rowNum:10000000,
		rowNum: 20,
		rowList: [20, 30, 40, 50, 100],
		sortname: "cl.CloseDateTime",
		sortorder: "desc",
		viewrecords: true,
		gridview: true,
		toppager: true,
		caption: "Список чеков",
		pager: '#pgrid1',
		// subGrid
		subGrid: true,
		subGridOptions: {
		plusicon: "ui-icon-triangle-1-e",
		minusicon: "ui-icon-triangle-1-s",
		openicon: "ui-icon-arrowreturn-1-e",
		// load the subgrid data only once	
		// and the just show/hide
		reloadOnExpand: false,
		// select the row when the expand column is clicked
		selectOnExpand: true
		},
		subGridRowExpanded: function (subgrid_id, row_id) {
		var subgrid_table_id, pager_id;
		subgrid_id = subgrid_id.replace('.', '_');
		row_id = row_id.replace('_', '.');
		subgrid_table_id = subgrid_id + "_t";
		pager_id = "p_" + subgrid_table_id;
		$("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class='scroll'></table><div id='" + pager_id + "' class='scroll'></div>");
		$("#" + subgrid_table_id).jqGrid({
			url: "../engine/jqgrid3?action=doc_check_info&sc.CheckID=" + row_id + "&f1=GoodID&f2=Article&f3=Name&f4=Quantity&f5=PriceBase&f6=PriceDiscount&f7=DiscountPercent&f8=Price&f9=Summa",
			datatype: "json",
			colNames: ['GoodID', 'Артикул', 'Название', 'Кол-во', 'Цена баз.', 'Скидка', '% ск.', 'Цена', 'Сумма'],
			colModel: [
			{name: "sc_GoodID", index: "sc.GoodID", width: 60, align: "center", sorttype: "number"},
			{name: "g_Article", index: "g.Article", width: 100, align: "left", sorttype: "text"},
			{name: "g_Name", index: "g.Name", width: 200, align: "left", sorttype: "text"},
			{name: "sc_Quantity", index: "sc.Quantity", width: 80, align: "right", sorttype: "number"},
			{name: "sc_PriceBase", index: "sc.PriceBase", width: 80, align: "right", sorttype: "number"},
			{name: "sc_PriceDiscount", index: "sc.PriceDiscount", width: 80, align: "right", sorttype: "number"},
			{name: "sc_DiscountPercent", index: "sc.DiscountPercent", width: 80, align: "right", sorttype: "number"},
			{name: "sc_Price", index: "sc.Price", width: 80, align: "right", sorttype: "number"},
			{name: "Summa", index: "Summa", width: 100, align: "right", sorttype: "number"},
			],
			rowNum: 20,
			pager: pager_id,
			sortname: "sc.DT_modi",
			height: '100%',
		});
		$("#" + subgrid_table_id).jqGrid('navGrid', "#" + pager_id, {edit: false, add: false, del: false})
		$("#pg_" + pager_id).remove();
		$("#" + pager_id).removeClass('ui-jqgrid-pager');
		$("#" + pager_id).addClass('ui-jqgrid-pager-empty');
		}
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Добавить чек в историю', buttonicon: "ui-icon-pencil", caption: 'Добавить чек в историю', position: "last",
		onClickButton: function () {
			$("#inputbox").dialog("open");
		}
	});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Удалить чек из истории', buttonicon: "ui-icon-pencil", caption: 'Удалить чек из истории', position: "last",
		onClickButton: function () {
		    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		    var node = $("#grid1").jqGrid('getRowData', id);
			//console.log(id,node,node.Name);
		    if (node.cl_CheckID != ''){
				$("#checkid").val(node.cl_CheckID);
				$("#inputbox2>#text").html("Удалить из истории чек № "+node.cl_CheckID+"?");
				$("#inputbox2").dialog("open");
			}
		}
	});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	$("#grid1").gridResize();

	$('#myTab a').click(function (e) {
		e.preventDefault();
		if (this.id == 'a_tab_history') {
			$("#grid1").jqGrid('setGridParam', {url: "../engine/jqgrid3?action=discountcards_history&cl.CardID=" + $("#cardid").val() + "&grouping=cl.CheckID&f1=CheckID&f2=DT_check&f3=ClientName&f4=TypePaymentName&f5=SumFull&f6=SumDiscount&f7=Sum", page: 1});
			$("#grid1").trigger('reloadGrid');
		}
	});

//	$("#inputbox").dialog("open");
	
});
</script>
<input id="cardid" name="cardid" type="hidden" value="<?php echo $row['CardID']; ?>">
<style>
	#feedback { font-size: 12px; }
	.selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
	.selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
	<ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
		<li class="active"><a id="a_tab_filter" href="#tab_filter" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;"><legend class="h20">Информация о дисконтной карте</legend></a></li>
		<li><a id="a_tab_history" href="#tab_history" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;"><legend class="h20">История покупок</legend></a></li>
	</ul>
	<div class="floatL">
		<button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
			<span class="ui-button-text" style='width:120px;height:22px;'>Сохранить данные</span>
		</button>
	</div>
	<div class="tab-content">
		<div class="active tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_filter">
			<div class='p5 ui-corner-all frameL border0 w400' style='display:table;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Дисконтная карта:</span>
					<span class="input-group-addon form-control TAL"><?php echo $row['CardID']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Ф.И.О. клиента:</span>
					<input id="name" name="name" type="text" class="form-control TAL" value="<?php echo $row['Name']; ?>">
					<span class="input-group-addon w32"></span>
				</div>               
				<div id="datapickers">
					<div class="datapicker input-group input-group-sm w100p">
						<span class="input-group-addon w25p TAL">Дата выдачи:</span>
						<input id="DT_issue" name="DT_issue" type="text" class="form-control TAL" value="<?php echo $row['DateOfIssue']; ?>">
						<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
						<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
					</div>
					<div class="datapicker input-group input-group-sm w100p">
						<span class="input-group-addon w25p TAL">Дата анулирования:</span>
						<input id="DT_cancellation" name="DT_cancellation" type="text" class="form-control TAL" value="<?php echo $row['DateOfCancellation']; ?>">
						<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
						<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
					</div>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Магазин:</span>
					<div class="w100p" id="select_point"></div>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Адрес:</span>
					<input id="address" name="address" type="text" class="form-control TAL" value="<?php echo $row['Address']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">EMail:</span>
					<input id="eMail" name="eMail" type="text" class="form-control TAL" value="<?php echo $row['EMail']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Телефон:</span>
					<input id="phone" name="phone" type="text" class="form-control TAL" value="<?php echo $row['Phone']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Животное: </span>
					<input id="animal" name="animal" type="text" class="form-control TAL" value="<?php echo $row['Animal']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Причина выдачи:</span>
					<input id="howWeLearn" name="howWeLearn" type="text" class="form-control TAL" value="<?php echo $row['HowWeLearn']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Примечание:</span>
					<input id="notes" name="notes" type="text" class="form-control TAL" value="<?php echo $row['Notes']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
			</div>
			<!--*********************-->
			<div class='p5 ui-corner-all frameL ml10 border0 w300' style='float:left;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w130 TAL">Стартовый процент:</span>
					<input id="startPercent" name="startPercent" type="text" class="form-control TAR" value="<?php echo $row['StartPercent']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w130 TAL">Стартовая сумма:</span>
					<input id="startSum" name="startSum" type="text" class="form-control TAR" value="<?php echo $row['StartSum']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w130 TAL">Доп. сумма:</span>
					<input id="dopSum" name="dopSum" type="text" class="form-control TAR" value ="<?php echo $row['DopSum']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w130 TAL">Сумма покупок: </span>
					<span class="input-group-addon form-control TAR"><?php echo $row['SummaAmount']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w130 TAL">Сумма накопления: </span>
					<span class="input-group-addon form-control TAR"><?php echo $row['AmountOfBuying']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w130 TAL">% скидки: </span>
					<input id="percentOfDiscount" name="percentOfDiscount" type="text" class="form-control TAR" value="<?php echo $row['PercentOfDiscount']; ?>">
					<span class="input-group-addon w32"></span>
				</div>
			</div>
		</div>
		<div class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_history">
			<div class="container min570">
				<div style='display:table;'>
					<div id='div1' class='frameL pt5'>
						<table id="grid1"></table>
						<div id="pgrid1"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
<div id="inputbox2" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
<div id="inputbox" title="Ввод информации:">
	<div class='p5 ui-corner-all border1 w250' style='display:table;'>
		<div class="input-group input-group-sm w100p">
			<span class="input-group-addon w25p TAL">Введите № чека:</span>
			<input id="checkid" name="checkid" type="text" class="form-control TAR" value="">
			<span class="input-group-addon w32"></span>
		</div>               
	</div>
</div>
