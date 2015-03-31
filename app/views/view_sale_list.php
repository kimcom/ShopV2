<script type="text/javascript">
    $(document).ready(function () {
//************************************//
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
			}}]
	});
	fs = 0;
// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		url: "../engine/jqgrid3?action=sale_0&operID=0&f1=SaleID&f2=Number1C&f3=DT_sale&f4=SaleStatus&f5=PartnerName",
		datatype: "json",
		height: 'auto',
		colNames: ['ID', 'Номер 1С', 'Дата', 'Статус', 'Партнер'],
		colModel: [
			{name: 'SaleID',	index: 'SaleID',	width: 100, align: "center",sorttype: "number", search: true},
			{name: 'Number1C',	index: 'Number1C',	width: 100, align: "left",	sorttype: "text",	search: true},
			{name: 'DT_sale',	index: 'DT_sale',	width: 120, align: "center",sorttype: "date",	search: true},
			{name: 'SaleStatus',index: 'SaleStatus',width: 100, align: "left", stype: 'select', editoptions: {value: ":любой;1:проведен;0:не проведен"}},
			{name: 'p_PartnerName',index: 'p.Name',	width: 220, align: "left",	sorttype: "text",	search: true},
		],
		gridComplete: function () {if (!fs) {fs = 1; filter_restore("#grid1");}},
		width: 'auto',
		shrinkToFit: false,
		rowNum: 20,
		rowList: [20, 30, 40, 50, 100],
		sortname: "DT_sale",
		viewrecords: true,
		gridview: true,
		toppager: true,
		caption: "Документы \"Расходные накладные\"",
		pager: '#pgrid1',
//		grouping: true,
//		groupingView : { 
//			groupField : ['City','Version'],
//			groupColumnShow : [true,true],
//			groupText : ['<b>{0}</b>'],
//			groupCollapse : false,
//			groupOrder: ['asc','asc'],
//			//groupSummary : [true,true]
//	    }
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть документ', buttonicon: "ui-icon-pencil", caption: 'Открыть документ', position: "last",
		onClickButton: function () {
		var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		var node = $("#grid1").jqGrid('getRowData', id);
		//console.log(id,node,node.Name);
		if (id != '')
			alert("Здесь откроем документ: "+id);
//			window.location = "../goods/map_discountcard_edit?cardid=" + id;
		}
	});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter": function (rowid) {
		alert("You enter a row with id:" + rowid)
		}});

	//$("#grid1").draggable();
	$("#grid1").gridResize();
    });
</script>
<div class="container min570">
	<div style='display:table;'>
		<div id='div1' class='frameL pt5'>
			<table id="grid1"></table>
			<div id="pgrid1"></div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
	