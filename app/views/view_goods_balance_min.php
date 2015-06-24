<?php
    $pointid = $_REQUEST['pointid'];
	if ($pointid == null) $pointid = 1000;
?>
<script type="text/javascript">
$(document).ready(function () {
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 400, //height: 300,
		buttons: [{text: "Закрыть", click: function () {
		$(this).dialog("close"); }}],
		show: {effect: "clip", duration: 500},
		hide: {effect: "clip", duration: 500}
	});
	$("#dialog_progress").dialog({
		autoOpen: false, modal: true, width: 400, height: 400,
		show: {effect: "explode",duration: 600},
		hide: {effect: "explode", duration: 600}
    });
	
//************************************//
	$("#treegrid").jqGrid({
	    treeGrid: true,
	    treeGridModel: 'nested',
	    treedatatype: 'json',
	    datatype: "json",
	    mtype: "POST",
	    width: 327,
	    height: 460,
	    ExpandColumn: 'name',
	    url: '../category/get_tree_NS',
	    colNames: ["id", "Категории"],
	    colModel: [
			{name: 'id', index: 'id', width: 1, hidden: true, key: true},
			{name: 'name', index: 'name', width: 190, resizable: false, editable: true, sorttype: "text", edittype: 'text', stype: "text", search: true}
	    ],
	    sortname: "Name",
	    sortorder: "asc",
	    pager: "#ptreegrid",
	    caption: "Категории",
	    toppager: true,
	    onSelectRow: function (cat_id) {
			if (cat_id == null) cat_id = 0;
			//url:"../engine/jqgrid3?action=goods_list_w_cost&f1=DT&f2=1C&f3=ClientID&f4=GoodID&f5=ID&f6=OPT_ID&f7=Article&f8=Name",
			var pointid = $("#select_point").select2("val");
			var newurl = "../engine/jqgrid3?action=good_list&sid=5&group=" + cat_id + "&point_balance_min="+pointid+
					"&f1=Article&f2=Name&f3=PeriodDelivery&f4=QtySale&f5=QtyPeriod&f6=Avg&f7=Std&f8=Rezerv&f9=CalcBalanceMinA&f10=BalanceMinA&f11=BalanceMinM";
			$("#grid1").jqGrid('setGridParam', {url: newurl, page: 1});
			$("#grid1").trigger('reloadGrid');
	    }
	});
	$("#treegrid").jqGrid('navGrid', '#ptreegrid', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#pg_ptreegrid").remove();
	$(".ui-jqgrid-hdiv").remove();
	$("#ptreegrid").removeClass('ui-jqgrid-pager');
	$("#ptreegrid").addClass('ui-jqgrid-pager-empty');
	$("#treegrid").gridResize();
	
//************************************//
	// Creating grid1
	var lastsel;
	$("#grid1").jqGrid({
	    sortable: true,
	    datatype: "json",
	    width: 1000-30,
	    height: 460-48,
	    colModel: [
			{label:'Артикул',		name:'Article',		index:'Article',	width: 100, sorttype: "text",	search:true},
			{label:'Название',		name:'Name',		index:'Name',		width: 220, sorttype: "text",	search:true},
			{label:'Срок пост.',name:'PeriodDelivery',index:'PeriodDelivery',width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Продано',		name:'QtySale',		index:'QtySale',	width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Периодов',		name:'QtyPeriod',	index:'QtyPeriod',	width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Среднее',		name:'Avg',			index:'Avg',		width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Ст.откл.',		name:'Std',			index:'Std',		width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Резерв',		name:'Rezerv',		index:'Rezerv',		width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Расч.М.О.А',	name:'CalcBalanceMinA',	index:'CalcBalanceMinA',width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Тек.М.О.А',		name:'BalanceMinA',	index:'BalanceMinA',width:80,	sorttype:"number",	search:true, align:"right"},
			{label:'Тек.М.О.Р',		name:'BalanceMinM',	index:'BalanceMinM',width:80,	sorttype:"number",	search:true, align:"right", editable:true},
	    ],
	    rowNum: 20,
	    rowList: [20, 30, 40, 50, 100, 200, 300],
	    sortname: "Name",
	    viewrecords: true,
	    gridview: true,
	    toppager: true,
		onSelectRow: function(id){
			if(id && id!==lastsel){
				$('#grid1').jqGrid('restoreRow',lastsel);
				lastsel = id;
			}
			$("#grid1").jqGrid('editRow',id, { 
				keys : true, 
				oneditfunc: function() {
					editurl = "../engine/balance_min_set?action=set_BalanceMinM&clientid=" + $("#select_point").select2("val");;
					$("#grid1").jqGrid('setGridParam', {editurl: editurl});
				},
				successfunc: function(json){
					var data=$.parseJSON(json.responseText);
					if (data.value_new === data.value_old) return true;
				}
			});
	    },
		caption: "Список товаров входящих в категорию:",
	    pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
    $("#pgrid1").addClass('ui-jqgrid-pager-empty');
	$("#grid1").gridResize();

	$.post('../Engine/select2?action=point', function (json) {
		$("#select_point").select2({multiple: false, placeholder: "Выберите торговую точку", data: {results: json, text: 'text'}});
		$("#select_point").select2("val", <?php echo $pointid; ?>);
    });
	$('#btn_set_auto').click(function (e) {
		var id = $("#treegrid").jqGrid('getGridParam','selrow');
		var node = $("#treegrid").jqGrid('getRowData',id);
		var cat_id = node.id;
		if( cat_id == null ) return;
		var pointid = $("#select_point").select2("val");
		var newurl = "../engine/balance_min_set_auto?action=set_BalanceMinA&sid=5&group=" + cat_id + "&point_balance_min=" + pointid;
//			alert(newurl);
		$("#dialog_progress").dialog( "option", "title", 'Ожидайте! Выполняется установка значений...');
		$("#dialog_progress").dialog("open");
			$.post(newurl, 
				function (data) {
					//console.log(data);
					$("#grid1").trigger('reloadGrid');
					$("#dialog_progress").dialog("close");
		});
    });
});
</script>
<div class="container-fluid center">
	<div class='p5 ui-corner-all frameL w330 ml0 border1 h45' style='display:table;'>
		<legend class="TAL">Управление минимальными остатками:</legend>
	</div>
	<div class='p5 ui-corner-all frameL w970 ml10 border1 h45' style='display:table;'>
		<div class="input-group input-group-sm frameL mt2 0border1">
			<span class="input-group-addon w130">Торг. точка:</span>
			<div class="w300" id="select_point"></div>
			<span class="input-group-addon w32"></span>
		</div>
		<button id="btn_set_auto" class="btn btn-sm btn-default frameL ml20 hidden-print font14">
			<span class="ui-button-text">Установить автоматически рассчитанные мин.остатки</span>
		</button>
	</div>
</div>
<div class="container-fluid center mt5">
	<div class='frameL'>
		<table id="treegrid"></table>
		<div id="ptreegrid"></div>
	</div>
	<div id='div1' class='frameL pl10'>
		<table id="grid1"></table>
		<div id="pgrid1"></div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
<div id="dialog_progress" title="Ожидайте!">
	<img class="ml30 mt20 border0 w300" src="../../img/progress_circle5.gif">
</div>
