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
	    url:"../engine/jqgrid3?action=users_list&f1=UserID&f2=Login&f3=EMail&f4=UserName&f5=Position&f6=NameShort&f7=City&f8=AccessLevel&f9=DT_create&f11=NameShort",
		datatype: "json",
		height:'auto',
		colNames:['ID','Логин','E-mail','ФИО','Должность','Подразделение','Город','Доступ','Дата создания'],
		colModel:[
			{name:'UserID',		index:'UserID',		width: 60, align:"center", sorttype:"number", search:true},
			{name:'Login',		index:'Login',		width: 130, align:"left",   sorttype:"text", search:true},
			{name:'EMail',		index:'EMail',		width: 170, align:"left", sorttype:"text", search: true},
			{name:'UserName',	index:'UserName',	width: 185, align:"left", sorttype:"text", search:true},
			{name:'Position',	index:'Position',	width: 185, align:"left", sorttype:"text", search:true},
			{name:'NameShort',	index:'NameShort', width: 120, align:"left", sorttype:"text", search:true},
			{name:'City',		index:'City',		width: 80, align:"center", sorttype:"text", search:true},
			{name:'AccessLevel',index:'AccessLevel',width: 40, align:"center", sorttype:"number", search:true},
			{name:'DT_create',	index:'DT_create',	width: 120, align:"center", sorttype:"date", search:true}
		],// 40
		//gridComplete: function() {if (!fs) {fs = 1;	filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "UserID",
		viewrecords: true,
		toppager: true,
		caption: "Список пользователей",
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
		title: 'Добавить пользователя', buttonicon: "ui-icon-pencil", caption: 'Добавить', position: "last",
		onClickButton: function () {
		    window.location = "../lists/user_info?userID=0";
		}
	    });
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть информационную карту пользователя', buttonicon: "ui-icon-pencil", caption: 'Открыть карту', position: "last",
		onClickButton: function () {
		    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		    var node = $("#grid1").jqGrid('getRowData', id);
		    //console.log(id,node,node.Name);
		    if (id != '')
				window.location = "../lists/user_info?userID=" + id;
		}
	});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});
	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');
	$("#grid1").gridResize();
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
