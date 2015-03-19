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
		url: "../goods/list?param=goods_list_where&col=goods_list&cat_id=0",
	    datatype: "json",
	    height: 'auto',
	    colNames: ['GoodID', 'OPT_ID', 'SHOP_ID', 'KIEV_ID', 'Артикул', 'Название', 'Отдел', 'Макс.ск.', 'В упак.', 'Ед.изм.', 'Вес'],
	    colModel: [
			{name: 'GoodID', index: 'GoodID', width: 80, align: "center", sorttype: "text", search: true},
			{name: 'OPT_ID', index: 'OPT_ID', width: 80, align: "center", sorttype: "text", search: true},
			{name: 'SHOP_ID', index: 'SHOP_ID', width: 80, align: "center", sorttype: "text", search: true},
			{name: 'KIEV_ID', index: 'KIEV_ID', width: 80, align: "center", sorttype: "text", search: true},
			{name: 'Article', index: 'Article', width: 100, sorttype: "text", search: true, editable: true, edittype: "text"},
			{name: 'Name', index: 'Name', width: 320, sorttype: "text", search: true},
			{name: 'Division', index: 'Division', width: 60, align: "right", search: true,
				formatter: 'number', formatoptions: {decimalSeparator: ".", decimalPlaces: 0}},
			{name: 'DiscountMax', index: 'DiscountMax', width: 60, align: "right", search: false,
				formatter: 'number', formatoptions: {decimalSeparator: ".", decimalPlaces: 0}},
			{name: 'Unit_in_pack', index: 'Unit_in_pack', width: 60, align: "right", search: false,
				formatter: 'number', formatoptions: {decimalSeparator: ".", decimalPlaces: 2}},
			{name: 'Unit', index: 'Unit', width: 60, align: "center", sorttype: "text", search: false},
			{name: 'Weight', index: 'Weight', width: 60, align: "right", search: false,
				formatter: 'number', formatoptions: {decimalSeparator: ".", decimalPlaces: 2}}
	    ],
	    gridComplete: function() {if(!fs) {fs = 1; filter_restore("#grid1");}},
	    width: 'auto',
	    shrinkToFit: false,
	    rowNum: 20,
	    rowList: [20, 30, 40, 50, 100],
	    sortname: "Article,Name",
	    viewrecords: true,
	    gridview: true,
	    toppager: true,
	    caption: "Список товаров",
	    pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: true, search: false, refresh: true, cloneToTop: true},
	{//edit
	}, {//add
	}, {//del
	    modal: true,
	    closeOnEscape: true,
	    closeAfterAdd: true,
	    reloadAfterSubmit: true,
	    beforeInitData: function () {
			var id = $("#grid1").jqGrid('getGridParam', 'selrow');
			if (id == null) return false;
			var node = $("#grid1").jqGrid('getRowData', id);
			$("#grid1").jqGrid('setGridParam', {editurl: "../goods/good_delete?goodid=" + node.GoodID});
	    },
	    afterSubmit: function (json, postdata) {
			var result = $.parseJSON(json.responseText);
			return [result.success, result.message, result.new_id, result.name];
		},
	    savekey: [true, 13]
	}
	);
	$("#grid1").navButtonAdd('#grid1_toppager', {
	    title: 'Открыть карту товара', buttonicon: "ui-icon-pencil", caption: '', position: "last",
	    onClickButton: function () {
		var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		var node = $("#grid1").jqGrid('getRowData', id);
		if (id != '')
		    window.location = "../goods/good_edit?goodid=" + id;
	    }
	});
	$("#grid1").navButtonAdd('#grid1_toppager', {
	    title: 'Движение товара', buttonicon: "", caption: 'Движение товара', position: "last",
	    onClickButton: function () {
		var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		var node = $("#grid1").jqGrid('getRowData', id);
		if (id != '')
		    window.location = "../goods/good_balance?goodid=" + id;
	    }
	});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter": function (rowid) {alert("You enter a row with id:" + rowid)}});

	//$("#grid1").draggable();
	$("#grid1").gridResize();
    });
</script>
<div class="container min570">
	<div id='div1' class='frameL pt5'>
		<table id="grid1"></table>
		<div id="pgrid1"></div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
