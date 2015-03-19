<script type="text/javascript">
$(document).ready(function(){
//************************************//
	$( "#dialog" ).dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function() {$( this ).dialog( "close" );}}]
	});
	fs=0;
// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		url:"../goods/list?param=goods_barcode&col=goods_barcode&cat_id=0",
		datatype: "json",
		height:'auto',
		colNames:['GoodID','Артикул','Название','EAN13'],
		colModel:[
			{name:'GoodID' , index:'b.GoodID' , width:80, align:"center", sorttype:"text", search:false},
			{name:'Article', index:'Article', width:100, sorttype:"text", search:true, editable: true, edittype:"text", },
			{name:'Name', index:'Name', width:320, sorttype:"text", search:true},
			{name:'EAN13', index:'EAN13', width:100, sorttype:"text", search:true}
		],
		gridComplete: function() {if(!fs){ fs = 1;	filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "Article,Name",
		viewrecords: true,
		gridview : true,
		toppager: true,
		caption: "Информация о штрих-кодах:",
		pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit:false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	$("#grid1").navButtonAdd('#grid1_toppager',{
		title:'Открыть карту товара', buttonicon:"ui-icon-pencil", caption:'', position:"last",
		onClickButton: function(){ 
			var id = $("#grid1").jqGrid('getGridParam','selrow');
			if(id==null) return false;
			var node = $("#grid1").jqGrid('getRowData',id);
			if(id!='') window.location = "../goods/good_edit?goodid="+node.GoodID;
		}
	});

    $("#grid1").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});
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
	<div id='div1' class='frameL pt5'>
		<table id="grid1"></table>
		<div id="pgrid1"></div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
