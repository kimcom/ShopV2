<?php
echo $_REQUEST['id'];
?>
<script src="../../js/jquery.validate.min.js"></script>
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
		width:284,
		height:486,
		ExpandColumn : 'name',
		url: '../lists/get_promo_tree_NS',
		colNames:["id","Акции"],
		colModel:[
			 {name:'id',index:'id', width:1, hidden:true, key:true},
			 {name:'name',index:'name', width:240, resizable:false, editable:true, sorttype:"text", edittype:'text', stype:"text", search:true}
		],
		sortname: "Name",
		sortorder: "asc",
		editurl: '../lists/promo_tree_oper',
		pager : "#ptreegrid",
		caption: "Дерево акций",
		toppager: true,
		onSelectRow: function(row_id) {
			if(row_id == null) row_id=0;
			$.post('../lists/get_promo_tree_info',{
				id:row_id
				},
				function(json){
					var node = $("#treegrid").jqGrid('getRowData',row_id);
					if(node.level!=0){
						$("#PromoID").val(json.PromoID);
						$("#Name").removeAttr("disabled");
						$("#Description").removeAttr("disabled");
						$("#select_promo_type").removeAttr("disabled");
						$("#select_user_list").removeAttr("disabled");
						$("#DT_start").removeAttr("disabled");
						$("#DT_stop").removeAttr("disabled");
						$("#button_save").removeAttr("disabled");
						$("#button_open").removeAttr("disabled");

					}else{
						$("#Name").attr("disabled","disabled");
						$("#Description").attr("disabled","disabled");
						$("#select_promo_type").attr("disabled","disabled");
						$("#select_user_list").attr("disabled","disabled");
						$("#DT_start").attr("disabled","disabled");
						$("#DT_stop").attr("disabled","disabled");
						$("#button_save").attr("disabled","disabled");
						$("#button_open").attr("disabled","disabled");
					}
					$("#Name").val(json.Name);
					$("#Description").val(json.Description);
					$("#DT_start").datepicker("setDate",json.DT_start);
					$("#DT_stop").datepicker("setDate",json.DT_stop);
					$("#DT_create").val(json.DT_create);
					$("#UserID_create").val(json.UserName_create);
					$("#DT_modi").val(json.DT_modi);
					$("#UserID_modi").val(json.UserName_modi);
					$("#select_promo_type").select2("val", json.TypeID);
					$("#select_user_list").select2("val", json.UserID_response);
					$("#promo_quantity").val(json.promo_quantity);
				}
			);
		}
		// ,
 // gridComplete: function(){
	    // setTimeout(function(){	   
			// autoClicked = [<?php echo $_REQUEST['nodeid'];?>];
			// jQuery.each(autoClicked, function(index, value) {
                // jQuery('#' + value + ' td div').click();  
            // });
	    // }, 10);
	// }
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
					$("#dialog>#text").html('Добавлять можно только в существующие группы.<br><br>Сначала выберите группу');
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
					$("#dialog>#text").html('Нельзя удалять 1-ый уровень!');
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
				$("#dialog>#text").html('Нельзя копировать 1-ый уровень!');
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
				$("#dialog>#text").html('Нельзя вырезать 1-ый уровень!');
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
			if(source_id_cut){
				if(source_id_cut==target_id)return false;
				$.post('../lists/promo_tree_oper?oper=move&source='+source_id_cut+'&target='+target_id,function(data){
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
				$.post('../lists/promo_tree_oper?oper=copy&source='+source_id_copy+'&target='+target_id,function(data){
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

	$("#treegrid").jqGrid('bindKeys', {"onEnter":function( rowid ) { alert("You enter a row with id:"+rowid)} } );

	$( "#dialog" ).dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function() {$( this ).dialog( "close" );}}]
	});

	$("#DT_start").datepicker({dateFormat:'dd/mm/yy'});
	$("#DT_stop" ).datepicker({dateFormat:'dd/mm/yy'});
	//$("#div1").resizable();

	$.post('../lists/get_promo_type',function(json){
		$("#select_promo_type").select2({
			placeholder: "Выберите тип акции",
			data: {results: json, text: 'text'}
		});
	});
	$.post('../lists/get_user_list',function(json){
		$("#select_user_list").select2({
			placeholder: "Выберите ответственного",
			data: {results: json, text: 'text'}
		});
	});
	$("#button_save").click(function(){
		if($("#PromoID").val()=='') return;
		$.post('../lists/promo_save',{
			promoid:$("#PromoID").val(),
			Name:$("#Name").val(),
			promo_type_id:$("#select_promo_type").select2("val"),
			Description:$("#Description").val(),
			DT_start:$("#DT_start").val(),
			DT_stop:$("#DT_stop").val(),
			UserID_response:$("#select_user_list").select2("val"),
			QuantityPromo:$("#promo_quantity").val()
			},
			function(data){
				if(data==false){
					$("#dialog>#text").html('Возникла ошибка при сохранении изменений.<br><br>Сообщите разработчику!');
					$("#dialog").dialog( "open" );
				}else{
					$("#dialog>#text").html('Данные успешно сохранены!');
					$("#dialog").dialog( "open" );
				}
			}
		);
	});
	$("#button_open").click(function(){
		if($("#PromoID").val()=='') return;
		section = "";
		promo_type_id = $("#select_promo_type").select2("val");
		if(promo_type_id=='10')		 {//Акционная цена
			section = "promo_control_price";
		}else if(promo_type_id=='30'){//Скидка, если комплект
			section = "promo_control_disc_complect";
		}else if(promo_type_id=='40'){//Скидка, если кол-во
			section = "promo_control_disc";
		}else if(promo_type_id=='50'){//Скидка на дополн.товар
			section = "promo_control_disc_dop";
		}else if(promo_type_id=='80'){//Подарок, если комплект
			section = "promo_control_gift_complect";
		}else if(promo_type_id=='90'){//Подарок, если кол-во
			section = "promo_control_gift";
		}else if(promo_type_id=='70'){//1+1=3
			section = "promo_control_1plus1";
		}else if(promo_type_id=='60'){//фикс.сумма
			section = "promo_control_fixsumma";
		}else{return;}
		window.location = "../lists/"+section+"?promoid="+$("#PromoID").val();
	});
});
</script>
<div class="container min570">
	<div class='frameL'>
		<table id="treegrid"></table>
		<div id="ptreegrid"></div>
	</div>
	<div id='div1' class='p5 ui-corner-all frameL ml10 border1' style='display:table;'>
		<legend>Информация об акции:</legend>
			<label for="Name">Название:</label>
			<input class='w300' id="Name" name="Name" minlength="5" type="text" required/>
		<p></p>
			<label for="select_promo_type">Тип акции:</label>
			<div class='w300' id="select_promo_type" name="select_promo_type"></div>
		<p></p>
			<label for="promo_quantity">Кол.товара (опция):</label>
			<input class='w300' id="promo_quantity" name="promo_quantity" minlength="5" type="text" required/>
		<p></p>
			<label for="Description">Описание:</label>
			<textarea class='w300' rows=5 id="Description" name="Description" ></textarea>
		<p></p>
			<label for='DT_start'>Дата начала:</label>
			<input class='w120' type="text" id="DT_start" name="DT_start">
		<p></p>
			<label for='DT_stop'>Дата завершения:</label>
			<input class='w120' type="text" id="DT_stop" name="DT_stop">
		<p></p>
			<label for="select_user_list">Ответственный:</label>
			<div class='w300' id="select_user_list" name="select_user_list"></div>
		<p></p>
			<label for="DT_create">Время созд.:</label>
			<input class='w120' id="DT_create" name="DT_create" type="text" disabled/>
			<input class='w300' id="UserID_create" name="UserID_create" type="text" disabled/>
		<p></p>
			<label for="DT_modi">Время изм.:</label>
			<input class='w120' id="DT_modi" name="DT_modi" type="text" disabled/>
			<input class='w300' id="UserID_modi" name="UserID_modi" type="text" disabled/>
		<p></p>
			<input id='PromoID' type='hidden' />
			<input id='section' type='hidden' value="<?php echo $_REQUEST['section']?>"/>
			<button id="button_open" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus" style='float:left;'>
			  <span class="ui-button-text" style='width:240px;'>Открыть карту товаров для акции</span>
			</button>
			<button id="button_save" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus" style='float:right;'>
			  <span class="ui-button-text" style='width:100px;'>Сохранить</span>
			</button>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
