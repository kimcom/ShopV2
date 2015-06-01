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
		url:"../engine/jqgrid3?action=discountCards_list&f1=CardID&f2=Name&f3=Phone&f4=DateOfIssue&f5=DateOfCancellation&f6=ClientID&f7=PercentOfDiscount&f8=AmountOfBuying&f9=SummaAmount",	
		datatype: "json",
		height:'auto',
		colNames:['Карта','ФИО','Телефон','Выдана','Аннулир.','Магазин','% скидки','Сумма накопл.','Сумма'],
		colModel:[
			{name:'CardID',            index:'CardID',             width: 100, align:"center",sorttype:"text",  search:true},
			{name:'Name',              index:'Name',               width: 220, align:"left",sorttype:"text",  search:true},
                        {name:'Phone',             index:'Phone',              width: 100, align: "left", sorttype: "number", search: true},
			{name:'DateOfIssue',       index:'DateOfIssue',        width: 120, align:"center", sorttype:"date",   search:true},
			{name:'DateOfCancellation',index:'DateOfCancellation', width: 120, align:"center", sorttype:"date",   search:true},
			{name:'с_ClientID',        index:'c.ClientID',         width: 60, align:"center", sorttype:"number", search:true, sortable:false},
			{name:'PercentOfDiscount', index:'PercentOfDiscount',  width: 52, align:"center", sorttype:"text",   search:true},
			{name:'AmountOfBuying',    index:'AmounyOfBuying',     width:100, align:"right",  sorttype:"number", search:false, sortable:false},
			{name:'SummaAmount',       index:'SummaAmount',        width:80,  align:"right",  sorttype:"number", search:false, sortable:false},		
		],
		beforeRequest: function() {
                    var date = new Date();
                    formated_date = date.getFullYear() + '-' + ('0' + (date.getMonth() + 1)).slice(-2);
                    var postData = $("#grid1").jqGrid('getGridParam', 'postData');
                    if (postData.DateOfIssue == null)
                        postData.DateOfIssue = formated_date;
                    $("#gs_DateOfIssue").val(postData.DateOfIssue);
                },
                gridComplete: function() {if(!fs) {fs = 1; filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
//		loadonce: true,
//		rowNum:10000000,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "ClientID",
		viewrecords: true,
		gridview : true,
		toppager: true,
		caption: "Список дисконтных карт",
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
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit: false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть информационную карту', buttonicon: "ui-icon-pencil", caption: 'Открыть информационную карту', position: "last",
		onClickButton: function () {
		    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		    var node = $("#grid1").jqGrid('getRowData', id);
			//console.log(id,node,node.Name);
		    if (id != '')
				window.location = "../goods/map_discountcard_edit?cardid=" + id;
		}
    });
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
