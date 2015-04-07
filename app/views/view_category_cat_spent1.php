<script type="text/javascript">
$(document).ready(function(){
var source_id_cut	= 0;
var source_id_copy 	= 0;
//************************************//
	$("#treegrid").jqGrid({
		treeGrid: true,
		treeGridModel: 'nested',
		treedatatype: 'json',
		datatype: "json",
		mtype: "POST",
		width:234,
		height:486,
		ExpandColumn : 'name',
		url: '../category/get_tree_NS_cat_spent',
		colNames:["id","Категории"],
		colModel:[
			 {name:'id',index:'id', width:1, hidden:true, key:true},
			 {name:'name',index:'name', width:190, resizable:false, editable:true, sorttype:"text", edittype:'text', stype:"text", search:true}
		],
		sortname: "Name",
		sortorder: "asc",
		editurl: '../category/cat_spent_tree_oper',
		pager : "#ptreegrid",
		caption: "Категории",
		toppager: true,
		gridComplete: function() {
			//setTimeout(function(){$("#treegrid").jqGrid('sortGrid','name', false, 'asc');},1);
			//$("#gbox_treegrid").clone().prependTo($("#pgrid3"));
			//$("#div3").html($("#treegrid").clone(true));
		},
		onSelectRow: function(cat_id) {
			if(cat_id == null) cat_id=0;
		    $("#grid1").jqGrid('setGridParam', {url: "../engine/jqgrid3?action=spent_list&f1=SpentID&f2=Name&cat_spent_id=" + cat_id, page: 1});
			$("#grid1").trigger('reloadGrid');
		}
	});
	$("#treegrid").jqGrid('navGrid','#ptreegrid', {edit:true, add:true, del:true, search:false, refresh:true, cloneToTop: true},
		{//edit
			modal:true,
			closeOnEscape:true,
			closeAfterEdit: true,
			reloadAfterSubmit: false,
			viewPagerButtons: false,
			savekey : [ true, 13 ],
			navkeys : [ false, 38, 40 ],
			afterSubmit : function(json, postdata) {
				var result=$.parseJSON(json.responseText);
				return [result.success,result.message,result.new_id];
			}
		},{//add
			modal:true,
			closeOnEscape:true,
			closeAfterAdd: true,
			reloadAfterSubmit: false,
			beforeInitData : function() {
				var id = $("#treegrid").jqGrid('getGridParam','selrow');
				if(id==null){
					$("#dialog>#text").html('Добавлять можно только в существующие категории.<br><br>Сначала выберите категорию');
					$("#dialog").dialog( "open" );
					return false;
				}
			},
			afterSubmit : function(json, postdata) {
				var result=$.parseJSON(json.responseText);
				return [result.success,result.message,result.new_id];
			},
			savekey : [ true, 13 ]
		},{//delete
			modal:true,
			closeOnEscape:true,
			closeAfterDel:true,
			reloadAfterSubmit: true,
			beforeInitData : function(formid) {
				var id = $("#treegrid").jqGrid('getGridParam','selrow');
				var node = $("#treegrid").jqGrid('getRowData',id);
				if(node.level==0){
					$("#dialog>#text").html('Нельзя удалять категории 1-го уровня!');
					$("#dialog").dialog( "open" );
					return false;
				}else{
					$("td.delmsg",formid[0]).html("Переместить в корзину:<br/>'" + node.name + "' ?");
					return true;
				}
			},
			afterSubmit : function(json, postdata) {
				var result=$.parseJSON(json.responseText);
				if(result.success)$("#treegrid").trigger("reloadGrid");
				return [result.success,result.message,result.new_id];
			},
			savekey : [ true, 13 ]
		}
	);
	
	$("#treegrid").navSeparatorAdd('#treegrid_toppager');
	$("#treegrid").navButtonAdd('#treegrid_toppager',{
		title:'Копировать текущую запись', buttonicon:"ui-icon-copy", caption:'', position:"last",
		onClickButton: function(){ 
			var id = $("#treegrid").jqGrid('getGridParam','selrow');
			var node = $("#treegrid").jqGrid('getRowData',id);
			if(node.level==0){
				source_id_copy 	= 0;
				$("#dialog>#text").html('Нельзя копировать категории 1-го уровня!');
				$("#dialog").dialog( "open" );
			}else{
				source_id_copy = id;
			}
		}
	});
	$("#treegrid").navButtonAdd('#treegrid_toppager',{
		title:'Вырезать текущую запись', buttonicon:"ui-icon-scissors", caption:'', position:"last",
		onClickButton: function(){ 
			var id = $("#treegrid").jqGrid('getGridParam','selrow');
			var node = $("#treegrid").jqGrid('getRowData',id);
			if(node.level==0){
				source_id_cut 	= 0;
				$("#dialog>#text").html('Нельзя вырезать категории 1-го уровня!');
				$("#dialog").dialog( "open" );
			}else{
				source_id_cut = id;
			}
		}
	});
	$("#treegrid").navButtonAdd('#treegrid_toppager',{
		title:'Вставить скопированную запись', buttonicon:"ui-icon-clipboard", caption:'', position:"last",
		onClickButton: function(){ 
			var target_id = $("#treegrid").jqGrid('getGridParam','selrow');
			console.log(source_id_cut);
			console.log(target_id);
			if(source_id_cut){
				if(source_id_cut==target_id)return false;
				$.post('../category/cat_spent_tree_oper?oper=move&source='+source_id_cut+'&target='+target_id,function(data){
					if(data==0){
						$("#dialog>#text").html('Возникла ошибка.<br/>Сообщите разработчику!');
						$("#dialog").dialog( "open" );
					}else{
						$("#treegrid").trigger("reloadGrid");
					}
				});
			}
			if(source_id_copy){
				if(source_id_copy==target_id)return false;
				$.post('../category/cat_spent_tree_oper?oper=copy&source='+source_id_copy+'&target='+target_id,function(data){
					if(data==0){
						$("#dialog>#text").html('Возникла ошибка.<br/>Сообщите разработчику!');
						$("#dialog").dialog( "open" );
					}else{
						$("#treegrid").trigger("reloadGrid");
					}
				});
			}	
			source_id_cut = 0;
			source_id_copy= 0; 
		}
	});
	$("#pg_ptreegrid").remove();
	$(".ui-jqgrid-hdiv").remove();
	$("#ptreegrid").removeClass('ui-jqgrid-pager');
	$("#ptreegrid").addClass('ui-jqgrid-pager-empty');
//***** treegrid2 *********//
	$("#treegrid2").jqGrid({
		treeGrid: true,
		treeGridModel: 'nested',
		treedatatype: 'json',
		datatype: "json",
		mtype: "POST",
		width:254,
		height:486,
		ExpandColumn : 'name',
		url: '../category/get_tree_NS_cat_spent',
		colNames:["id","Категории"],
		colModel:[
			 {name:'id',index:'id', width:1, hidden:true, key:true},
			 {name:'name',index:'name', width:190, resizable:false, editable:true, sorttype:"text", edittype:'text', stype:"text", search:true}
		],
		sortname: "Name",
		//sortable: true,
		sortorder: "asc",
		editurl: '../category/cat_spent_tree_oper',
		pager : "#ptreegrid2",
		caption: "Категории",
		toppager: true,
		gridComplete: function() {
			//setTimeout(function(){$("#treegrid2").jqGrid('sortGrid','name', false, 'asc');},1);
			//$("#gbox_treegrid").clone().prependTo($("#pgrid3"));
			//$("#div3").html($("#treegrid").clone(true));
		},
		onSelectRow: function(cat_id) {
			if(cat_id == null) cat_id=0;
		    $("#grid4").jqGrid('setGridParam', {url: "../engine/jqgrid3?action=spent_list&f1=SpentID&f2=Name&cat_spent_id=" + cat_id, page: 1});
			$("#grid4").trigger('reloadGrid');
		}
	});
	$("#treegrid2").jqGrid('navGrid','#ptreegrid', {edit:false, add:false, del:false, search:false, refresh:true, cloneToTop: true});
	$("#pg_ptreegrid2").remove();
	$(".ui-jqgrid-hdiv").remove();
	$("#ptreegrid2").removeClass('ui-jqgrid-pager');
	$("#ptreegrid2").addClass('ui-jqgrid-pager-empty');
	
//************************************//
	// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		datatype: "json",
		width:'100%',
		height: '100%',
		colNames:['Код','Статья затрат'],
		colModel:[
			{name:'p_SpentID', index:'p.SpentID', width: 60, sorttype: "number", search: true},
			{name:'Name', index:'Name', width:220, sorttype:"text", search:true}
		],
		rowNum:20,
		rowList:[20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		toppager: true,
		caption: "Список ст. затрат входящих в категорию:",
		pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit:false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	$("#grid1").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true	});

	$("#grid1").navButtonAdd('#grid1_toppager',{
		title:'Удалить из категории', buttonicon:"ui-icon-minusthick", caption:'из катег.', position:"last",
		onClickButton: function(){ 
			var id = $("#treegrid").jqGrid('getGridParam','selrow');
			var node = $("#treegrid").jqGrid('getRowData',id);
			if(id==null){
				$("#dialog>#text").html('Вы не указали категорию!');
				$("#dialog").dialog( "open" );
				return;
			}
			var sel;
			sel = jQuery("#grid1").jqGrid('getGridParam','selarrrow');
			if(sel==''){
				$("#dialog>#text").html('Вы не выбрали ни одной записи!');
				$("#dialog").dialog( "open" );
				return;
			}
			$("#grid1").jqGrid('delGridRow', id, {
				modal:true,
				closeOnEscape:true,
				closeAfterDel:true,
				reloadAfterSubmit: true,
				msg: 'Удалить выбранные записи из категории<br/>'+node.name+'?',
				url: '../category/del_from_cat_spent?cat_id='+id+'&source='+sel,
				savekey : [ true, 13 ],
				afterSubmit : function(json, postdata) {
					var result=$.parseJSON(json.responseText);
					if(result.success)$("#grid4").trigger("reloadGrid");
					return [result.success,result.message,result.new_id];
				}
				} 
			);
		}
	});
	
	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

//************************************//
	// Creating grid4
	$("#grid4").jqGrid({
		sortable: true,
		datatype: "json",
		width:'100%',
		height: '100%',
		colNames:['Код','Статья затрат'],
		colModel:[
			{name:'p_SpentID', index:'p.SpentID', width: 60, sorttype: "number", search: true},
			{name:'Name', index:'Name', width:310, sorttype:"text", search:true}
		],
		rowNum:20,
		rowList:[20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		toppager: true,
		caption: "Список ст. затрат входящих в категорию:",
		pager: '#pgrid4'
	});
	$("#grid4").jqGrid('navGrid','#pgrid4', {edit:false, add:false, del:false, search:false, refresh: true,	cloneToTop: true});
	$("#grid4").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true	});

	$("#grid4").navButtonAdd('#grid4_toppager',{
		title:'Добавить в категорию', buttonicon:"ui-icon-plusthick", caption:'в катег.', position:"last",
		onClickButton: function(){ 
			var id = $("#treegrid").jqGrid('getGridParam','selrow');
			if(id==null){
				$("#dialog>#text").html('Вы не указали категорию!');
				$("#dialog").dialog( "open" );
				return;
			}
			var sel;
			sel = jQuery("#grid4").jqGrid('getGridParam','selarrrow');
			if(sel==''){
				$("#dialog>#text").html('Вы не выбрали ни одной записи!');
				$("#dialog").dialog( "open" );
				return;
			}
			$.post('../category/add_in_cat_spent?cat_id='+id+'&source='+sel,function(data){
				if(data==0){
					$("#dialog>#text").html('Возникла ошибка.<br/>Сообщите разработчику!');
					$("#dialog").dialog( "open" );
				}else{
					$("#grid1").trigger("reloadGrid");
					$("#grid4").trigger("reloadGrid");
				}
			});
		}
	});
	$("#pg_pgrid4").remove();
	$("#pgrid4").removeClass('ui-jqgrid-pager');
	$("#pgrid4").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#treegrid").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );
	$("#grid1").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );
	$("#grid4").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );

	$("#treegrid").gridResize();
	//$("#grid1").draggable();
	$("#grid1").gridResize();
	$("#grid4").gridResize();

	$( "#dialog" ).dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function() {$( this ).dialog( "close" );}}]
	});

});
</script>
<div class="min570">
	<div class='frameL'>
		<table id="treegrid"></table>
		<div id="ptreegrid"></div>
	</div>
	<div id='div1' class='frameL pl10'>
		<table id="grid1"></table>
		<div id="pgrid1"></div>
	</div>
	<div class='frameL pl10'>
		<table id="treegrid2"></table>
		<div id="ptreegrid2"></div>
	</div>
	<div id='div4' class='frameL pl10'>
		<table id="grid4"></table>
		<div id="pgrid4"></div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
