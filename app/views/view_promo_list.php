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
	    url:"../engine/jqgrid3?action=promo_list_full&f1=PromoID&f2=PromoType&f3=Name&f4=Description&f5=TypeID",
		datatype: "json",
		height:'auto',
		colNames:['ID','Тип','Название','Описание','TypeID'],
		colModel:[
			{name:'PromoID',	 index:'PromoID',	  width: 80, align:"center", sorttype:"number", search:true},
			{name:'PromoType',	 index:'PromoType',	  width: 200, align:"left",  sorttype:"text",   search:true},
			{name:'Name',		 index:'Name',		  width: 250, align:"left",  sorttype:"text",   search:true},
			{name:'Description', index:'Description', width: 645, align:"left",  sorttype:"text",   search:true},
			{name:'TypeID',		 index:'TypeID',	  width: 250, align:"left",  sorttype:"text",   search:true, hidden:true}
		],
		//gridComplete: function() {if (!fs) {fs = 1;	filter_restore("#grid1");}},
		width:'auto',
		shrinkToFit:false,
		rowNum:20,
		rowList:[20,30,40,50,100],
		sortname: "PromoID",
		viewrecords: true,
		toppager: true,
		caption: "Список акций",
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
		title: 'Открыть информационную карту', buttonicon: "ui-icon-pencil", caption: 'Открыть инф. карту', position: "last",
		onClickButton: function () {
		    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
			var node = $("#grid1").jqGrid('getRowData', id);
			if (id != '')
				window.location = "../lists/promo_tree?promoid=" + id;
		}
	    });
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть дополнительную информационную карту', buttonicon: "ui-icon-pencil", caption: 'Открыть доп. инф. карту', position: "last",
		onClickButton: function () {
		    var id = $("#grid1").jqGrid('getGridParam', 'selrow');
			var node = $("#grid1").jqGrid('getRowData', id);
		    console.log(id,node,node.Name,node.TypeID);
			if (id != '' && node.TypeID != ''){
				section = "";
				promo_type_id = node.TypeID;
			    if (promo_type_id == '10')		 {//Акционная цена
				section = "promo_control_price";
				} else if (promo_type_id == '30'){//Скидка, если комплект
				section = "promo_control_disc_complect";
				} else if (promo_type_id == '40'){//Скидка, если кол-во
				section = "promo_control_disc";
				} else if (promo_type_id == '50'){//Скидка на дополн.товар
				section = "promo_control_disc_dop";
				} else if (promo_type_id == '80'){//Подарок, если комплект
				section = "promo_control_gift_complect";
				} else if (promo_type_id == '90'){//Подарок, если кол-во
				section = "promo_control_gift";
				} else if (promo_type_id == '70'){//1+1=3
				section = "promo_control_1plus1";
				} else if (promo_type_id == '60'){//фикс.сумма
				section = "promo_control_fixsumma";
				} else{return; }
				window.location = "../lists/"+section+ "?promoid=" + id;
			}
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
