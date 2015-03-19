<script type="text/javascript">
$(document).ready(function(){
//************************************//
	$( "#dialog" ).dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function() {$( this ).dialog( "close" );}}]
	});

	var lastsel2;
	fs=0;
// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		url:"../goods/list?param=goods_barcode_verify&col=goods_barcode_verify&cat_id=0",
		datatype: "json",
		height:'auto',
		colNames:['EAN13','GoodID','Артикул','Название','UserID','Пользователь','Магазин','Время создания'],
		colModel:[
			{name:'EAN13', index:'EAN13', width:100, sorttype:"text", search:true},
			{name:'GoodID' , index:'b.GoodID' , width:80, align:"center", sorttype:"text", search:false},
			{name:'Article', index:'Article', width:100, sorttype:"text", search:true, editable: true, edittype:"text", },
			{name:'Name', index:'Name', width:320, sorttype:"text", search:true},
			{name:'UserID', index:'b.UserID', width:80, sorttype:"text", search:false},
			{name:'UserName', index:'UserName', width:120, sorttype:"text", search:false},
			{name:'NameValid', index:'NameValid', width:120, sorttype:"text", search:false},
			{name:'DT_create', index:'b.DT_create', width:120, sorttype:"text", search:false},
/*			{name:'Virified', index: 'Virified', width: 75, align: 'center', formatter: 'checkbox',
				edittype: 'checkbox', editoptions: {value: 'Yes:No', defaultValue: 'Yes'}, search:false},	
			{name:'stock',index:'stock', width:60, align: 'center', search:false, 
				formatter: 'checkbox', editable: true,edittype:"checkbox",editoptions: {value:"Yes:No"}},
*/		],
/*		onSelectRow: function(id){
				if(id && id!==lastsel2){
					jQuery('#grid1').jqGrid('restoreRow',lastsel2);
					jQuery('#grid1').jqGrid('editRow',id,true);
					lastsel2=id;
				}
			},
*/		
		//gridComplete: function() { if(!fs){ fs = 1;	filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "Article,Name",
		viewrecords: true,
		gridview : true,
		toppager: true,
		caption: "Список неподтвержденных штрих-кодов:",
		pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit:false, add:false, del:true, search:false, refresh: true, cloneToTop: true},
		{//edit
		},{//add
		},{//del
			modal:true,
			closeOnEscape:true,
			closeAfterAdd: true,
			reloadAfterSubmit: true,
			beforeInitData : function() {
				var id = $("#grid1").jqGrid('getGridParam','selrow');
				if(id==null) return false;
				var node = $("#grid1").jqGrid('getRowData',id);
				$("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + node.GoodID});
			},
			afterSubmit : function(json, postdata) {
				var result=$.parseJSON(json.responseText);
				return [result.success,result.message,result.new_id,result.name];
			},
			savekey : [ true, 13 ]
		}
	);
	$("#grid1").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true});
	$("#grid1").navButtonAdd('#grid1_toppager',{
		title:'Открыть карту товара', buttonicon:"ui-icon-pencil", caption:'Открыть карту товара ', position:"last",
		onClickButton: function(){ 
			var id = $("#grid1").jqGrid('getGridParam','selrow');
			if(id==null) return false;
			var node = $("#grid1").jqGrid('getRowData',id);
			if(id!='') window.location = "../goods/good_edit?goodid="+node.GoodID;
		}
	});
	$("#grid1").navButtonAdd('#grid1_toppager',{
		title:'Уст. отметку ШК-Проверен', buttonicon:"ui-icon-check", caption:'Уст. отметку ШК-Проверен', position:"last",
		onClickButton: function(){ 
			var id = $("#grid1").jqGrid('getGridParam','selrow');
			if(id==null) return false;
			var node = $("#grid1").jqGrid('getRowData',id);
			$.post('../goods/barcode_edit?goodid='+node.GoodID+'&barcode='+id+'&oper=verified',function(data){
				if(data==0)alert('Возникла ошибка.\nСообщите разработчику!');
				window.location.reload();
			});
		}
	});

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
