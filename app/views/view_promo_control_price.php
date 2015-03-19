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

	$.post('../lists/get_promo_list',{promo_type:'10'},function(json){
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
//$("#grid1").jqGrid('setCell','16277','PricePromo','12345',"");
			}
		);
		$("#grid1").jqGrid('setGridParam',{url:"../goods/list?col=price&param=in promo_goods_item&cat_id="+row_id,page:1});
		$("#grid1").trigger('reloadGrid');
		$("#grid2").jqGrid('setGridParam',{url:"../goods/list?col=price&param=no promo_goods_item&cat_id="+row_id,page:1});
		$("#grid2").trigger('reloadGrid');
	});
	
	var last_edit_row = '';
// Creating grid1
	$("#grid1").jqGrid({
		sortable: true,
		//url:"post_goods.php?action=get_goods_list0&param=in promo_goods_item&cat_id=-1",
		datatype: "json",
		height: 300,
		colNames:['Артикул','Название','Опт.цена','Розн.цена','Акц.Цена','SortIndex'],
		colModel:[
			{name:'Article', index:'Article', width:100, sorttype:"text", search:true, editable: true, edittype:"text", },
			{name:'Name', index:'Name', width:320, sorttype:"text", search:true},
			{name:'PriceOpt',index:'PriceOpt', width:60, align:"right", search:false},
			{name:'PriceR',index:'PriceR', width:60, align:"right", search:false},
			{name:'PricePromo',index:'PricePromo', width:60, align:"right", search:false, 
				formatter:'number',formatoptions:{decimalSeparator:".",decimalPlaces: 2},
				editable: true, edittype:"text", editoptions:{size:"10",maxlength:"10",
				dataInit:function (elem) { if(elem.value=='0'||elem.value=='0.00'||elem.value=='0,00')elem.value='' }}
			},
			{name:'SortIndex', index:'SortIndex', width:75, sorttype:"int", align:'right', hidden:true, search:false}
		],
		width:666,
		shrinkToFit:false,
		rowNum:20,
		rowList:[10,20,30,40,50,100,200,300],
		sortname: 'SortIndex',
		sortorder: 'desc',
		viewrecords: true,
		multiselect: true,
		gridview : true,
		hiddengrid: true,
		toppager: true,
		caption: "Список товаров на которые действует акция:",
		pager: '#pgrid1',
		beforeSaveCell: function(rowid,cellname,value,iRow,iCol) { 
			$("#grid1").jqGrid('setGridParam', {cellurl: "../goods/edit_price?promoid=" + $("#PromoID").val()});
        },
		cellEdit: true,
		cellsubmit: 'remote'
});
	$("#grid1").jqGrid('navGrid','#pgrid1', {edit:false, add:true, del:false, search:false, refresh: true, cloneToTop: true},
		{//edit
		},{//add
			modal:true,
			closeOnEscape:true,
			closeAfterAdd: true,
			reloadAfterSubmit: true,
			beforeInitData : function() {
				if($("#PromoID").val()=='')return false;
				$("#select_promo_list").select2("enable", false);
				$("#Article").attr('tabindex','1');
				$("#PricePromo").attr('tabindex','2');
				$("#sData").attr('tabindex','3');
				$("#grid1").jqGrid('setGridParam', {editurl: "../goods/goods_add2_in_promo?promo_id=" + $("#PromoID").val()});
			},
			afterSubmit : function(json, postdata) {
				var result=$.parseJSON(json.responseText);
				return [result.success,result.message,result.new_id,result.name];
			},
			onClose : function() {
				$("#select_promo_list").select2("enable", true);
			},
			savekey : [ true, 13 ]
	});
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
		//url:"post_goods.php?action=get_goods_list0&param=no promo_goods_item&cat_id=-1",
		datatype: "json",
		mtype: "POST",
		width:460,
		shrinkToFit:false,
		height: 300,
		colNames:['Артикул','Название'],
		colModel:[
			{name:'Article', index:'Article', width:100, sorttype:"text", search:true},
			{name:'Name', index:'Name', width:320, sorttype:"text", search:true}
		],
		rowNum:20,
		rowList:[10,20,30,40,50,100,200,300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		gridview : true,
		hiddengrid: true,
		toppager: true,
		//hiddengrid: true,
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

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );
	$("#grid2").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );

	//$("#grid1").draggable();
	$("#grid1").gridResize();
	$("#grid2").gridResize();

});
</script>
<div class="container min570">
	<div class='p5 ui-corner-all frameL border1' style='display:block;'>
		<div class='frameL ml10' style='display:table;'>
			<label for="select_promo_list">Акция:</label>
			<div class='w300' id="select_promo_list" name="select_promo_list"></div>
			<p></p>
			<label for="TypeName">Тип акции:</label>
			<input class='w300' id="TypeName" name="TypeName" minlength="5" type="text" disabled required/>
		</div>
		<div class='frameL ml10' style='display:table;'>
			<label for="Name">Название:</label>
			<input class='w500' id="Name" name="Name" minlength="5" type="text" disabled required/>
			<p></p>
			<label for="Description">Описание:</label>
			<textarea class='w500' rows=2 id="Description" name="Description" disabled ></textarea>
		</div>
		<input id='PromoID' name='PromoID' type='hidden' disabled/>
		<input id='section' type='hidden' value="<?php echo $_REQUEST['section']?>" disabled/>
	</div>
	<div style='display:table;'>
		<legend>Информация об акционных товарах:</legend>
		<div id='div1' class='frameL pt5'>
			<table id="grid1"></table>
			<div id="pgrid1"></div>
		</div>
		<div id='div2' class='frameL pt5 ml10'>
			<table id="grid2"></table>
			<div id="pgrid2"></div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
