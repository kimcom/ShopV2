
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
	    url:"../engine/jqgrid3?action=sellers_list&f1=SellerID&f2=Name&f3=Post&f4=Fired&f5=ClientID&f6=City&f7=NameShort&f8=Kod1C",
		datatype: "json",
		height:'auto',
		colNames:['Код','ФИО','Должность','Статус','Код маг.','Город','Магазин', 'Код 1С'],
		colModel:[
			{name:'SellerID',	index:'SellerID', width: 60, align:"center", sorttype:"text", search:true},
			{name:'Name',		index:'Name', 	  width:160, align:"left",   sorttype:"text", search:true},
			{name:'Post',		index:'Post',     width:100, align:"left",   sorttype:"text", search:true},
			{name:'Fired',		index:'Fired',	  width: 60, align:"center", sorttype:"text", search: true},
			{name:'c_ClientID',	index:'c.ClientID', width: 60, align:"center", sorttype:"text", search:true},
			{name:'c_City',		index:'c.City',	  width:100, sorttype:"text", search:true},
			{name:'c_NameShort',index:'c.NameShort', width:160, sorttype:"text", search:true},
			{name:'Kod1C',		index:'Kod1C',	  width: 60, align:"center", sorttype:"text", search:true}
		],
		gridComplete: function() {if (!fs) {fs = 1;	filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "Name",
		viewrecords: true,
		toppager: true,
		caption: "Список сотрудников",
		pager: '#pgrid1',
		grouping: true
//,
//		groupingView : { 
//			groupField : ['c_NameShort','Post'],
//			groupColumnShow : [true,true],
//			groupText : ['<b>{0}</b>'],
//			groupCollapse : true,
//			groupOrder: ['asc','asc']
//			//,
//			//groupSummary : [true,true]
//	    }
	});
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit:false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Добавить сотрудника', buttonicon: "ui-icon-pencil", caption: 'Добавить', position: "last",
		onClickButton: function () {
		    window.location = "../lists/seller_info?sellerID=-1";
		}
	});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть информационную карту', buttonicon: "ui-icon-pencil", caption: 'Открыть карту', position: "last",
		onClickButton: function () {
		    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		    var node = $("#grid1").jqGrid('getRowData', id);
		    //console.log(id,node,node.Name);
		    if (id != '')
				window.location = "../lists/seller_info?sellerID=" + id;
		}
	});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});
	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');
	$("#grid1").gridResize();
	$("#gs_Fired").attr('title','1-уволен, 0-нет');
});
</script>
<div class="container min570">
	<div style='display:table;'>
<!--		<legend>Информация о сотрудниках:</legend>-->
		<div id='div1' class='frameL pt5'>
			<table id="grid1"></table>
			<table id="pgrid1"></table>
<!--			<div id="pgrid1"></div>-->
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
