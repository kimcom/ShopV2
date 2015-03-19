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
		url:"../engine/jqgrid3?action=goods_list_w_cost&f1=DT&f2=1C&f3=ClientID&f4=GoodID&f5=ID&f6=OPT_ID&f7=Article&f8=Name",	
		datatype: "json",
		height:'auto',
		colNames:['Дата','1С','ClientID','GoodID','ID','OPT_ID','Артикул','Имя'],
		colModel:[
			{name:'c_CreateDateTime', index:'c.CreateDateTime', width: 70,	 align:"center",  sorttype:"date",	  search:true},
			{name:'1C',				  index:'1C',				width: 50,	 align:"center",  sorttype:"text",	  search:true},
			{name:'ClientID',		  index:'ClientID',			width: 50,	 align:"center",  sorttype:"number",  search:true},
			{name:'cc_GoodID',		  index:'cc.GoodID',			width: 50,	 align:"center",  sorttype:"number",  search:true},
			{name:'s_ID',			  index:'s.ID',			width: 100,  align:"center",  sorttype:"number",  search:true},
			{name:'s_OPT_ID',		  index:'s.OPT_ID',			width: 100,  align:"center",  sorttype:"number",  search:true},
			{name:'g_Article',		  index:'g.Article',			width: 100,	 align:"left",    sorttype:"text",	  search:true},
			{name:'g_Name ',		  index:'g.Name',				width: 350,  align:"left",    sorttype:"text",	  search:true},		
		],
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
		caption: "Список товаров без себестоимости",
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
