<?php
$promoid = 0;
if(isset($_REQUEST['promoid'])) $promoid = $_REQUEST['promoid'];
?>
<script src="../../js/jquery.validate.min.js"></script>
<script type="text/javascript">
$(document).ready(function(){
//************************************//
	$( "#dialog" ).dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function() {$( this ).dialog( "close" );}}]
	});

	$.post('../lists/get_promo_list',{promo_type:'40'},function(json){
		$("#select_promo_list").select2({
			placeholder: "Выберите акцию",
			data: {results: json, text: 'text'}
		});
		promoid = <?php echo $promoid;?>;
		if(promoid!=0){
			$("#select_promo_list").select2("val", promoid);
			$("#select_promo_list").click();
		}
	});
	
	$("#select_promo_list").click(function () { 
		row_id=$("#select_promo_list").select2("val");
		if(row_id == null) row_id=0;
		$.post('../lists/get_promo_tree_info',{
			id:row_id
			},
			function(json){
				$("#Name").val(json.Name);
				$("#Description").val(json.Description);
				$("#TypeName").val(json.TypeName);
				$("#PromoID").val(json.PromoID);
			}
		);
		$("#grid1").jqGrid('setGridParam',{url:"../goods/list?col=disc123&param=in promo_goods_item&cat_id="+row_id,page:1});
		$("#grid1").trigger('reloadGrid');
		$("#grid2").jqGrid('setGridParam',{url:"../goods/list?col=disc123&param=no promo_goods_item&cat_id="+row_id,page:1});
		$("#grid2").trigger('reloadGrid');
		$("#grid3").jqGrid('setGridParam',{url:"../goods/get_cats_list?param=in promo_cats_item&cat_id="+row_id,page:1});
		$("#grid3").trigger('reloadGrid');
		$("#grid4").jqGrid('setGridParam',{url:"../goods/get_cats_list?param=no promo_cats_item&cat_id="+row_id,page:1});
		$("#grid4").trigger('reloadGrid');
	});

// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		//url:"post_goods.php?action=get_goods_list&param=in promo_goods_item&promo_id=-1",
		datatype: "json",
		height: '230px',
		colNames:['Артикул','Название','Опт.цена','Макс.скидка','Акц.скидка'],
		colModel:[
			{name:'Article', 		index:'Article', 		width:100, 	sorttype:"text", search:true},
			{name:'Name', 			index:'Name', 			width:320, 	sorttype:"text", search:true},
			{name:'PriceOpt',		index:'PriceOpt', 		width:60, 	align:"right", search:false},
			{name:'DiscountMax',	index:'DiscountMax', 	width:60, 	align:"right", search:false},
			{name:'DiscountPromo',	index:'DiscountPromo', 	width:60, 	align:"right", search:false, editable: true, edittype:"text", editoptions:{size:"10",maxlength:"10"}}
		],
		width:666,
		shrinkToFit:false,
		rowNum:10,
		rowList:[10,20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		toppager: true,
		hiddengrid: true,
		caption: "Список товаров на которые действует акция:",
		pager: '#pgrid1',
		beforeSaveCell: function(rowid,cellname,value,iRow,iCol) { 
			$("#grid1").jqGrid('setGridParam', {cellurl: "../goods/edit_discount?promoid=" + $("#PromoID").val()});
        },
		cellEdit: true,
		cellsubmit: 'remote'
	});
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit:false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	$("#grid1").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true	});

	$("#grid1").navButtonAdd('#grid1_toppager',{
		title:'Удалить из акции', buttonicon:"ui-icon-minusthick", caption:'из акции', position:"last",
		onClickButton: function(){ 
			id=$("#select_promo_list").select2("val");
			if(id==null){
				$("#dialog>#text").html('Вы не указали акцию!');
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
				msg: 'Удалить выбранные товары из акции<br/>'+$("#select_promo_list").select2("data").text+'?',
				url: '../goods/del_goods_from_promo?promo_id='+id+'&source='+sel,
				savekey : [ true, 13 ],
				afterSubmit : function(json, postdata) {
					var result=$.parseJSON(json.responseText);
					if(result.success)$("#grid2").trigger("reloadGrid");
					return [result.success,result.message,result.new_id];
				}
				} 
			);
		}
	});
	
	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

// Creating grid2
	$("#grid2").jqGrid({
		sortable: true,
		//url:"post_goods.php?action=get_goods_list&param=no promo_goods_item&cat_id=-1",
		datatype: "json",
		mtype: "POST",
		height: '230px',
		colNames:['Артикул','Название'],
		colModel:[
			//{name:'GoodID', index:'GoodID', width:80, sorttype:"text", search:true},
			{name:'Article', index:'Article', width:100, sorttype:"text", search:true},
			{name:'Name', index:'Name', width:320, sorttype:"text", search:true}
		],
		width:471,
		shrinkToFit:false,
		rowNum:10,
		rowList:[10,20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		toppager: true,
		hiddengrid: true,
		caption: "Список товаров для добавления в акцию:",
		pager: '#pgrid2'
	});
	$("#grid2").jqGrid('navGrid','#pgrid2', {edit:false, add:false, del:false, search:false, refresh: true,	cloneToTop: true});
	$("#grid2").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true	});

	$("#grid2").navButtonAdd('#grid2_toppager',{
		title:'Добавить в акцию', buttonicon:"ui-icon-plusthick", caption:'в акцию', position:"last",
		onClickButton: function(){ 
			id=$("#select_promo_list").select2("val");
			if(id==null){
				$("#dialog>#text").html('Вы не указали акцию!');
				$("#dialog").dialog( "open" );
				return;
			}
			var sel;
			sel = jQuery("#grid2").jqGrid('getGridParam','selarrrow');
			if(sel==''){
				$("#dialog>#text").html('Вы не выбрали ни одной записи!');
				$("#dialog").dialog( "open" );
				return;
			}
			$.post('../goods/goods_add_in_promo?promo_id='+id+'&source='+sel,function(data){
				if(data==0){
					$("#dialog>#text").html('Возникла ошибка.<br/>Сообщите разработчику!');
					$("#dialog").dialog( "open" );
				}else{
					$("#grid1").trigger("reloadGrid");
					$("#grid2").trigger("reloadGrid");
				}
			});
		}
	});
	$("#pg_pgrid2").remove();
	$("#pgrid2").removeClass('ui-jqgrid-pager');
	$("#pgrid2").addClass('ui-jqgrid-pager-empty');

// Creating grid3
	$("#grid3").jqGrid({
		sortable: true,
		//url:"post_goods.php?action=get_cats_list&param=in promo_cats_item&promo_id=-1",
		datatype: "json",
		height: '230px',
		colNames:['Название','Акц.скидка'],
		colModel:[
			//{name:'GoodID', index:'GoodID', width:80, sorttype:"text", search:true},
			{name:'Name', index:'Name', width:425, sorttype:"text", search:true},
			{name:'DiscountPromo',index:'DiscountPromo', width:60, align:"right", search:false, editable: true, edittype:"text", editoptions:{size:"10",maxlength:"10"}}
		],
		width:666,
		shrinkToFit:false,
		rowNum:10,
		rowList:[10,20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		toppager: true,
		hiddengrid: true,
		caption: "Список категорий на которые действует акция:",
		pager: '#pgrid3',
		beforeSaveCell: function(rowid,cellname,value,iRow,iCol) { 
			$("#grid3").jqGrid('setGridParam', {cellurl: "../goods/edit_discount_cat?promoid=" + $("#PromoID").val()});
        },
		cellEdit: true,
		cellsubmit: 'remote'
	});
	$("#grid3").jqGrid('navGrid','#pgrid3', {edit:false, add:false, del:false, search:false, refresh: true, cloneToTop: true});
	$("#grid3").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true	});

	$("#grid3").navButtonAdd('#grid3_toppager',{
		title:'Удалить из акции', buttonicon:"ui-icon-minusthick", caption:'из акции', position:"last",
		onClickButton: function(){ 
			id=$("#select_promo_list").select2("val");
			if(id==null){
				$("#dialog>#text").html('Вы не указали акцию!');
				$("#dialog").dialog( "open" );
				return;
			}
			var sel;
			sel = jQuery("#grid3").jqGrid('getGridParam','selarrrow');
			if(sel==''){
				$("#dialog>#text").html('Вы не выбрали ни одной записи!');
				$("#dialog").dialog( "open" );
				return;
			}

			$("#grid3").jqGrid('delGridRow', id, {
				modal:true,
				closeOnEscape:true,
				closeAfterDel:true,
				reloadAfterSubmit: true,
				msg: 'Удалить выбранные товары из акции<br/>'+$("#select_promo_list").select2("data").text+'?',
				url: '../goods/del_cats_from_promo?promo_id='+id+'&source='+sel,
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
	
	$("#pg_pgrid3").remove();
	$("#pgrid3").removeClass('ui-jqgrid-pager');
	$("#pgrid3").addClass('ui-jqgrid-pager-empty');

// Creating grid4
	$("#grid4").jqGrid({
		sortable: true,
		//url:"post_goods.php?action=get_cats_list&param=no promo_cats_item&cat_id=-1",
		datatype: "json",
		mtype: "POST",
		height: '230px',
		colNames:['Название'],
		colModel:[
			{name:'Name', index:'Name', width:425, sorttype:"text", search:true}
		],
		width:471,
		shrinkToFit:false,
		rowNum:10,
		rowList:[10,20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		toppager: true,
		hiddengrid: true,
		caption: "Список категорий для добавления в акцию:",
		pager: '#pgrid4'
	});
	$("#grid4").jqGrid('navGrid','#pgrid4', {edit:false, add:false, del:false, search:false, refresh: true,	cloneToTop: true});
	$("#grid4").jqGrid('filterToolbar', { autosearch: true,	searchOnEnter: true	});

	$("#grid4").navButtonAdd('#grid4_toppager',{
		title:'Добавить в акцию', buttonicon:"ui-icon-plusthick", caption:'в акцию', position:"last",
		onClickButton: function(){ 
			id=$("#select_promo_list").select2("val");
			if(id==null){
				$("#dialog>#text").html('Вы не указали акцию!');
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
			$.post('../goods/cats_add_in_promo?promo_id='+id+'&source='+sel,function(data){
				if(data==0){
					$("#dialog>#text").html('Возникла ошибка.<br/>Сообщите разработчику!');
					$("#dialog").dialog( "open" );
				}else{
					$("#grid3").trigger("reloadGrid");
					$("#grid4").trigger("reloadGrid");
				}
			});
		}
	});
	$("#pg_pgrid4").remove();
	$("#pgrid4").removeClass('ui-jqgrid-pager');
	$("#pgrid4").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );
	$("#grid2").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );
	$("#grid3").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );
	$("#grid4").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );

	//$("#grid1").draggable();
	$("#grid1").gridResize();
	$("#grid2").gridResize();
	$("#grid3").gridResize();
	$("#grid4").gridResize();
});
</script>
<div class="container-fluid min570">
	<div class='p5 ui-corner-all frameL border1' style='display:block;'>
		<div class='frameL ml10' style='display:table;'>
			<label for="select_promo_list">Акция:</label>
			<div class='w300' id="select_promo_list" name="select_promo_list"></div>
			<p></p>
			<label for="TypeName">Тип акции:</label>
			<input class='w300' id="TypeName" name="TypeName" minlength="5" type="text" required/>
		</div>
		<div class='frameL ml10' style='display:table;'>
			<label for="Name">Название:</label>
			<input class='w500' id="Name" name="Name" minlength="5" type="text" required/>
			<p></p>
			<label for="Description">Описание:</label>
			<textarea class='w500' rows=2 id="Description" name="Description" ></textarea>
		</div>
		<input id='PromoID' name='PromoID' type='hidden' />
		<input id='section' type='hidden' value="<?php echo $_REQUEST['section']?>"/>
	</div>
	<div style='display:table;'>
		<legend>Информация об акционных товарах:</legend>
		<div id='div1' class='frameL pt5'>
			<table id="grid1"></table>
			<div id="pgrid1"></div>
		</div>
		<div id='div2' class='frameL pt5 pl10'>
			<table id="grid2"></table>
			<div id="pgrid2"></div>
		</div>
	</div>
	<p></p>
	<div style='display:table;'>
		<div id='div3' class='frameL pt5'>
			<table id="grid3"></table>
			<div id="pgrid3"></div>
		</div>
		<div id='div4' class='frameL pt5 pl10'>
			<table id="grid4"></table>
			<div id="pgrid4"></div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
