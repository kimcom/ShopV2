<script type="text/javascript">
$(document).ready(function(){
//************************************//
	$( "#dialog" ).dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function() {$( this ).dialog( "close" );}}]
	});
    fs = 0;
// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		url:"../engine/jqgrid3?action=check_list&grouping=cl.CheckId&f1=CheckID&f2=CheckStatus&f3=TypePayment&f4=TypeCheck&f5=CreateDateTime&f6=City&f7=ClientName&f8=SumBase&f9=SumDiscount&f10=Sum&f11=CardID",
		datatype: "json",
		height:'auto',
		colNames:['№ чека','Статус Чека','Тип оплаты','Тип чека','Дата, время','Город','Магазин','Сумма общая','Сумма скидки','Сумма к оплате','Номер Дисконта'],
		colModel:[
			{name:'cl_CheckID',        index:'cl.CheckID',         width: 100,  align:"center",  sorttype:"number",    search:true, sortable: true},
			{name:'CheckStatus',       index:'CheckStatus',        width: 90,   align:"left",    stype:"select",     editoptions: {value: ":любой;1:распечатан;0:не закрыт" }},
			{name:'TypePayment',       index:'TypePayment',        width: 80,   align:"center",  stype:"select",     editoptions: {value: ":любой;1:без нал;0:нал"}},
			{name:'FlagReturn',        index:'FlagReturn',         width: 80,   align: "center", stype:"select",     editoptions: {value: ":любой;1:продажа;-1:возврат"}},
			{name:'CreateDateTime',    index:'CreateDateTime',     width: 120,  align:"center",  sorttype:"date",    search:true},
			{name:'City',              index:'City',               width: 100,  align:"center",  sorttype:"text",    search:true, sortable:false},
			{name:'c_NameShort',       index:'c.NameShort',        width: 120,  align:"center",  sorttype:"text",    search:true},
			{name:'SumBase',           index:'SumBase',            width: 80,   align:"right",   sorttype:"number",  search:false, sortable:false},
			{name:'SumDiscount',       index:'SumDiscount',        width: 80,   align:"right",   sorttype:"number",  search:false, sortable:false},
			{name:'Sum',               index: 'Sum',               width: 100,  align: "right",  sorttype: "number", search: false, sortable: true},
            {name:'CardID',            index: 'CardID',            width: 100,  align: "center", sorttype: "number", search: true, sortable: true},
		],
		gridComplete: function() {if(!fs) {fs = 1; filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
//		loadonce: true,
//		rowNum:10000000,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "cl.ClientID, cl.CheckId",
                sortorder: "desc",
		viewrecords: true,
		gridview : true,
		toppager: true,
		caption: "Список чеков",
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
                        //console.log(subgrid_id, row_id);
                        var subgrid_table_id, pager_id;
                        subgrid_id = subgrid_id.replace('.', '_');
                        row_id = row_id.replace('_', '.');
                        subgrid_table_id = subgrid_id + "_t";
                        pager_id = "p_" + subgrid_table_id;
                        //console.log(subgrid_id, row_id, subgrid_table_id);
                        $("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class='scroll'></table><div id='" + pager_id + "' class='scroll'></div>");
                        $("#" + subgrid_table_id).jqGrid({
                            url: "../engine/jqgrid3?action=doc_check_info&CheckID="+row_id+"&f1=GoodID&f2=Article&f3=Name&f4=Quantity&f5=PriceBase&f6=PriceDiscount&f7=DiscountPercent&f8=Price&f9=Summa",
                            datatype: "json",
                            colNames: ['GoodID', 'Артикул', 'Название', 'Кол-во', 'Цена баз.', 'Скидка', '% ск.', 'Цена', 'Сумма'],
                            colModel: [
                                {name: "sc_GoodID", index: "sc.GoodID", width: 60, align: "center", sorttype: "number"},
                                {name: "g_Article", index: "g.Article", width: 100, align: "left", sorttype: "text"},
                                {name: "g_Name", index: "g.Name", width: 250, align: "left", sorttype: "text"},
                                {name: "sc_Quantity", index: "sc.Quantity", width: 60, align: "right", sorttype: "number"},
                                {name: "sc_PriceBase", index: "sc.PriceBase", width: 60, align: "right", sorttype: "number"},
                                {name: "sc_PriceDiscount", index: "sc.PriceDiscount", width: 60, align: "right", sorttype: "number"},
                                {name: "sc_DiscountPercent", index: "sc.DiscountPercent", width: 60, align: "right", sorttype: "number"},
                                {name: "sc_Price", index: "sc.Price", width: 60, align: "right", sorttype: "number"},
                                {name: "Summa", index: "Summa", width: 80, align: "right", sorttype: "number"},
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
	//$("#grid1").jqGrid('navGrid','#pgrid1', {edit: false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	//$("#grid1").navButtonAdd('#grid1_toppager', {
	//	title: 'Открыть информационную карту', buttonicon: "ui-icon-pencil", caption: 'Открыть информационную карту', position: "last",
	//	onClickButton: function () {
	//	    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
	//	    var node = $("#grid1").jqGrid('getRowData', id);
			//console.log(id,node,node.Name);
	//	    if (id != '')
	//			window.location = "../goods/map_discountcard_edit?cardid=" + id;
	//}
//    });
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function(){filter_save("#grid1");}});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );

	//$("#grid1").draggable();
	$("#grid1").gridResize();
});
</script>
<div class="container min570">
	<div style='display:table;'>
		<!--<legend>Список дисконтных карт:</legend>-->
		<div id='div1' class='frameL pt5'>
			<table id="grid1"></table>
			<div id="pgrid1"></div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
