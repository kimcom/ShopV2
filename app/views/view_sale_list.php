<?php
$operID = 1;
$docName = 'Документы \"Расходные накладные\"';
if(isset($_REQUEST['operID'])) $operID = $_REQUEST['operID'];
if($operID == -1)	$docName = 'Документы \"Возвратные накладные\"';
?>
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
		url: "../engine/jqgrid3?action=sale_opt&operID=<?php echo $operID;?>&f1=SaleID&f2=Number1C&f3=DT_sale&f4=SaleStatus&f5=PartnerName&f6=SellerName",
		datatype: "json",
		height: 'auto',
		colNames: ['ID', 'Номер 1С', 'Дата', 'Статус', 'Партнер', 'Менеджер'],
		colModel: [
			{name: 'SaleID',	index: 'SaleID',	width: 100, align: "center",sorttype: "number", search: true},
			{name: 'Number1C',	index: 'Number1C',	width: 100, align: "left",	sorttype: "text",	search: true},
			{name: 'DT_sale',	index: 'DT_sale',	width: 120, align: "center",sorttype: "date",	search: true},
			{name: 'SaleStatus',index: 'SaleStatus',width: 100, align: "left",	stype: 'select', editoptions: {value: ":любой;1:проведен;0:не проведен;-1:удален"}},
			{name: 'p_PartnerName',index: 'p.Name',	width: 220, align: "left",	sorttype: "text",	search: true},
			{name: 's_Name',	index: 's.Name',	width: 220, align: "left",	sorttype: "text",	search: true},
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
		caption: '<?php echo $docName;?>',
		pager: '#pgrid1',
		subGrid: true,
		subGridOptions: {
			plusicon  : "ui-icon-triangle-1-e",
			minusicon : "ui-icon-triangle-1-s",
			openicon: "ui-icon-arrowreturn-1-e",
			// load the subgrid data only once
			// and the just show/hide
			reloadOnExpand: false,
			// select the row when the expand column is clicked
			selectOnExpand: true
		    },
	    subGridRowExpanded: function (subgrid_id, row_id) {
			var subgrid_table_id, pager_id;
			subgrid_id = subgrid_id.replace('.','_');
			row_id = row_id.replace('_','.');
			subgrid_table_id = subgrid_id + "_t";
			pager_id = "p_" + subgrid_table_id;
			//console.log(subgrid_id, row_id, subgrid_table_id);
			$("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class='scroll'></table><div id='" + pager_id + "' class='scroll'></div>");
			$("#" + subgrid_table_id).jqGrid({
				url: "../engine/jqgrid3?action=doc_sale_info&sc.SaleID="+row_id+"&f1=GoodID&f2=Article&f3=Name&f4=Quantity&f5=PriceBase&f6=PriceDiscount&f7=DiscountPercent&f8=Price&f9=Summa",
			    datatype: "json",
			    colNames: ['GoodID', 'Артикул', 'Название', 'Кол-во', 'Цена баз.', 'Скидка', '% ск.', 'Цена', 'Сумма'],
			    colModel: [
				{name: "sc_GoodID", index: "sc.GoodID",		width:  60,	align: "center",sorttype: "number"},
				{name: "g_Article", index: "g.Article",		width: 100, align: "left",	sorttype: "text"},
				{name: "g_Name",	index: "g.Name",		width: 200, align: "left",	sorttype: "text"},
				{name: "sc_Quantity", index: "sc.Quantity", width:  60, align: "right",	sorttype: "number"},
				{name: "sc_PriceBase", index: "sc.PriceBase", width: 60, align: "right",sorttype: "number"},
				{name: "sc_PriceDiscount", index: "sc.PriceDiscount", width: 60, align: "right",sorttype: "number"},
				{name: "sc_DiscountPercent", index: "sc.DiscountPercent", width: 60, align: "right",sorttype: "number"},
				{name: "sc_Price", index: "sc.Price", width: 60, align: "right",sorttype: "number"},
				{name: "Summa", index: "Summa", width: 80, align: "right",sorttype: "number"},
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
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть документ', buttonicon: "ui-icon-pencil", caption: 'Открыть документ', position: "last",
		onClickButton: function () {
		var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		var node = $("#grid1").jqGrid('getRowData', id);
		//console.log(id,node,node.Name);
		//if (id != '') alert("Здесь откроем документ: "+id);
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
	
