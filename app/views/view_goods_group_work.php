<script type="text/javascript">
$(document).ready(function () {
	var reportID = 6; 
//Object Converter
	oconv	= function (a) {var o = {};for(var i=0;i<a.length;i++) {o[a[i]] = '';} return o;}
	strJoin = function (obj){ var ar = []; for (key in obj){ar[ar.length] = obj[key];}return ar;}
	keyJoin = function (obj){ var ar = []; for (key in obj){ar[ar.length] = key;}return ar;}
	clearObj= function (obj){ for(key in obj){for(k in obj[key]){delete obj[key][k];}}return obj;}
	var settings = new Object();
	var grouping = new Object();
	var group = new Object();
	var good = new Object();
	var cat = new Object();
	var markup = new Object();
	var matrix = new Object();
	settings['grouping']=grouping;
	settings['group']=group;
	settings['good']=good;
	settings['cat']=cat;
	settings['markup']=markup;
	settings['matrix']=matrix;
	var colnames = ['Ед.','Вес','Отдел','Макс.%'];
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 400, //height: 300,
		buttons: [{text: "Закрыть", click: function () {
			    $(this).dialog("close");}}],
		show: {effect: "clip",duration: 500},
		hide: {effect: "clip",duration: 500}
    });
	$("#dialog_progress").dialog({
		autoOpen: false, modal: true, width: 400, height: 400,
		show: {effect: "explode",duration: 600},
		hide: {effect: "explode",duration: 600}
    });
	
	$.post('../Engine/setting_get?sid='+reportID, function (json) {
		$("#select_report_setting").select2({
		    createSearchChoice: function (term, data){
				if ($(data).filter(function(){return this.text.localeCompare(term) === 0;}).length === 0) {
				    return {id: term, text: term};
				}
			},
			//multiple: true,
			placeholder: "Выберите настройку отчета",
		    data: {results: json, text: 'text'}
		});
$("#select_report_setting").select2("val", "тест");
$("#select_report_setting").click();
    });

	$("#select_report_setting").click(function () { 
		var setting = $("#select_report_setting").select2("data");
		if (setting == null) return;
		clearObj(settings);
		$.post('../Engine/setting_get_byName?sid='+reportID+'&sname='+setting.text,
		function (json) {
			var set = json.Setting;
			var aset = set.split('&');
			for(key in aset){
				var k = aset[key].split('=');
				if(k[1]=='')continue;
				var l = k[1].split('|');
				var m = l[0].split(';');
				var n = l[1].split(';');
				for(i=0;i<m.length;i++){
					if(m[i]=='') continue;
					if(k[0]=='grouping'){
						settings[k[0]][i]=n[i];
					}else{
						settings[k[0]][m[i]]=n[i];
					}
				}
			}
//			console.log(settings['grouping']);
//			$("#grouping li").each(function( index ) {
//				var id = this.id;
//				$("#" + id).appendTo($('#grouping_add'));
//				$("#" + id + ">#a1").removeClass('hide').addClass('show');
//				$("#" + id + ">#a2").removeClass('show').addClass('hide');
//			});
//			for(id in grouping){
//				$("#divGridGrouping_add #" + grouping[id]).appendTo($('#grouping'));
//				$("#" + grouping[id] + ">#a2").removeClass('hide').addClass('show');
//				$("#" + grouping[id] + ">#a1").removeClass('show').addClass('hide');
//			}
//			if(Object.keys(grouping).length==0){
//				id = 'g_goodID';
//				$("#"+id).appendTo($('#grouping'));
//				$("#" + id + ">#a2").removeClass('hide').addClass('show');
//			    $("#" + id + ">#a1").removeClass('show').addClass('hide');
//			}
			$("#group").val(strJoin(group).join(';'));
			$("#group").attr("title", strJoin(group).join("\n"));
			$("#good").val(strJoin(good).join(';'));
			$("#good").attr("title", strJoin(good).join("\n"));
			$("#cat").val(strJoin(cat).join(';'));
			$("#cat").attr("title",strJoin(cat).join("\n"));
			$("#markup").val(strJoin(markup).join(';'));
			$("#markup").attr("title", strJoin(markup).join("\n"));
			$("#matrix").val(strJoin(matrix).join(';'));
			$("#matrix").attr("title", strJoin(matrix).join("\n"));
//$('#button_selection_run').click();
		});
	});
		
//группы товара
	$("#treeGrid").jqGrid({
		treeGrid: true,
		treeGridModel: 'nested',
		treedatatype: 'json',
		datatype: "json",
		mtype: "POST",
		width: 250,
		height: 380,
		ExpandColumn: 'name',
//		url: '../category/get_tree_NS?nodeid=20',
		colNames: ["id", "Категории"],
		colModel: [
		    {name: 'id', index: 'id', width: 1, hidden: true, key: true},
		    {name: 'name', index: 'name', width: 190, resizable: false, editable: true, sorttype: "text", edittype: 'text', stype: "text", search: true}
		],
		sortname: "Name",
		//sortable: true,
		sortorder: "asc",
		pager: "#ptreeGrid",
		//caption: "Группы товаров",
		toppager: true,
		onSelectRow: function (cat_id) {
		    if (cat_id == null)
			cat_id = 0;
		    $("#grid1").jqGrid('setGridParam', {datatype: "json", url: "../goods/list?col=cat&param=in category&cat_id=" + cat_id, page: 1});
		    $("#grid1").trigger('reloadGrid');
		}
    });
	$("#treeGrid").jqGrid('navGrid','#ptreeGrid', {edit:false, add:false, del:false, search: false, refresh: true, cloneToTop: true});
	$("#treeGrid").navButtonAdd('#treeGrid_toppager',{
		buttonicon: "ui-icon-plusthick", caption: 'Выбрать', position: "last",
		onClickButton: function () {
		    var id = $("#treeGrid").jqGrid('getGridParam', 'selrow');
		    var node = $("#treeGrid").jqGrid('getRowData', id);
			datastr = $("#treeGrid").getGridParam('datastr');
			if (datastr=='group'){
				group[id] = node.name;
				$("#group").val(strJoin(group).join(';'));
				$("#group").attr("title",strJoin(group).join("\n"));
			}
			if (datastr=='cat'){
				cat[id] = node.name;
				$("#cat").val(strJoin(cat).join(';'));
				$("#cat").attr("title",strJoin(cat).join("\n"));
		    }
			if (datastr=='markup'){
				markup[id] = node.name;
				$("#markup").val(strJoin(markup).join(';'));
				$("#markup").attr("title",strJoin(markup).join("\n"));
		    }
			if (datastr=='matrix'){
				matrix[id] = node.name;
				$("#matrix").val(strJoin(matrix).join(';'));
				$("#matrix").attr("title",strJoin(matrix).join("\n"));
		    }
		}
    });
	$("#pg_ptreeGrid").remove();
	$(".ui-jqgrid-hdiv").remove();
	$("#ptreeGrid").removeClass('ui-jqgrid-pager');
    $("#ptreeGrid").addClass('ui-jqgrid-pager-empty');

//список товаров
	$("#grid1").jqGrid({
		sortable: true,
		datatype: "json",
		width: 370,
		height: 330,
		colNames: ['Артикул', 'Название','field3'],
		colModel: [
		    {name: 'field1', index: 'field1', width: 80, sorttype: "text", search: true},
		    {name: 'field2', index: 'field2', sorttype: "text", search: true},
		    {name: 'field3', index: 'field3', sorttype: "text", search: true, hidden: true}
		],
		rowNum: 15,
		rowList: [15, 30, 40, 50, 100, 200, 300],
		sortname: "Name",
		viewrecords: true,
		multiselect: true,
		//loadonce: true,
		gridview: true,
		toppager: true,
		caption: "",
		pager: '#pgrid1'
	    });
	    $("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: false, search: false, refresh: false, cloneToTop: true});
	    $("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});

	    $("#grid1").navButtonAdd('#grid1_toppager', {
		buttonicon: 'ui-icon-plusthick', caption: 'Выбрать', position: "last",
		onClickButton: function () {
		    var sel;
		    sel = jQuery("#grid1").jqGrid('getGridParam', 'selarrrow');
		    if (sel == '') {
				$("#dialog").css('background-color','');
				$("#dialog>#text").html('Вы не выбрали ни одной записи!');
				$("#dialog").dialog("open");
				return;
		    }
			datastr = $("#grid1").getGridParam('datastr');
			for(key in sel){
				var node = $("#grid1").jqGrid('getRowData', sel[key]);
				//alert('key='+key+'\nsel[key]='+sel[key]+'\nnode.field2='+node.field2);
				if (datastr=='good') good[sel[key]] = node.field2;
			}
			if (datastr=='good'){
				$("#good").val(strJoin(good).join(';'));
				$("#good").attr("title", strJoin(good).join("\n"));
			}
		}
	});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	$("#treeGrid").gridResize();
	$("#grid1").gridResize();
	
	$("#divGrid").hide();

	$("#setting_filter a").click(function() {
		operid = '';
		var command = this.parentNode.previousSibling.previousSibling.previousSibling.previousSibling;
		if(command.tagName=='SPAN'){
			command = this.parentNode.previousSibling.previousSibling;
		}
//		console.log(command,$(this).html(),this.parentNode.previousSibling.previousSibling.previousSibling.previousSibling.id);
		if(command.tagName=="INPUT"){
			operid = command.id;
		}else if(command.tagName=="DIV"){
			operid = this.parentNode.previousSibling.previousSibling.previousSibling.previousSibling.id;
	    } else{
			alert('Ошибка определения действия!');
			return;
		}
		if($(this).html()=='X'){
			for(k in settings[operid]){
				delete settings[operid][k];
			}
			$("#"+operid).val(strJoin(settings[operid]).join(';'));
			$("#"+operid).attr("title", strJoin(settings[operid]).join("\n"));
			return;
		}
		if(operid=='select_report_setting'){
			setting = $("#select_report_setting").select2("data");
			if(setting==null){
				$("#dialog").css('background-color','');
				$("#dialog>#text").html('Введите название для сохранения настройки!');
				$("#dialog").dialog("open");
				return;
			}
//			grouping = [];
//			$("#grouping li").each(function( index ) {grouping[index] = this.id;});
			setID = setting.id;
			if(setting.id==setting.text) setID='';
			$.post("../Engine/setting_set"+
					"?group="	+ keyJoin(group).join(';')	+"|"+strJoin(group).join(';')+
					"&good="	+ keyJoin(good).join(';')	+"|"+strJoin(good).join(';')+
					"&cat="		+ keyJoin(cat).join(';')	+"|"+strJoin(cat).join(';')+
					"&markup="	+ keyJoin(markup).join(';') +"|"+strJoin(markup).join(';')+
					"&matrix="	+ keyJoin(matrix).join(';')	+"|"+strJoin(matrix).join(';'),
				{	sid:	reportID,
					sname:	setting.text,
				}, 
				function (data) {
					if (data == 0) {
						$("#dialog").css('background-color','linear-gradient(to bottom, #f7dcdb 0%, #c12e2a 100%)');
						$("#dialog>#text").html('Возникла ошибка.<br/>Сообщите разработчику!');
						$("#dialog").dialog("open");
					} else {
						$("#dialog").css('background-color','');
						$("#dialog>#text").html('Настройки успешно сохранены!');
						$("#dialog").dialog("open");
					}
			});
		}
		if(operid=='group'){
			$("#legendGrid").html('Выбор товара или группы:');
			$("#treeGrid").jqGrid('setGridParam',{datastr:"group"});
			$("#treeGrid").jqGrid('setCaption','Группы товаров');
		    $("#treeGrid").jqGrid('setGridParam', {url: "../category/get_tree_NS?nodeid=10", page: 1}).trigger('reloadGrid');
			$("#divTable").hide();
			$("#divTree").show();
			$("#divGrid").show();
		}
		if(operid=='good'){
			$("#grid1").jqGrid('setLabel', "field1","Артикул");
			$("#grid1").jqGrid('setLabel', "field2", "Название");
			$("#legendGrid").html('Выбор товара:');
//			$("#treeGrid").jqGrid('setGridParam',{datastr:"good"});
//			$("#treeGrid").jqGrid('setCaption','Группы товаров');
//		    $("#treeGrid").jqGrid('setGridParam', {url: "../category/get_tree_NS?nodeid=10", page: 1}).trigger('reloadGrid');
			$("#grid1").jqGrid('setGridParam',{datastr:"good"});
		    $("#grid1").hideCol("field3");
		    $(".ui-search-input>input").val("");
				//$("#grid1").jqGrid("clearGridData", true).trigger("reloadGrid");
		    $("#grid1").jqGrid('setGridParam', {datatype: "json", url: "../goods/list?param=goods_list_select&col=cat&cat_id=0", page: 1}).trigger('reloadGrid');
			//$("#divTable").addClass('ml10');
			$("#divTable").show();
			//$("#divTree").show();
			$("#divTree").hide();
			$("#divGrid").show();
		}
		if(operid=='cat'){
			$("#legendGrid").html('Выбор категории товара:');
			$("#treeGrid").jqGrid('setGridParam',{datastr:"cat"});
			$("#treeGrid").jqGrid('setCaption', 'Категории товаров');
		    $("#treeGrid").jqGrid('setGridParam', {datatype: "json", url: "../category/get_tree_NS?nodeid=50", page: 1}).trigger('reloadGrid');
			$("#divTable").hide();
			$("#divTree").show();
			$("#divGrid").show();
	    }
		if(operid=='markup'){
			$("#legendGrid").html('Выбор категории наценки:');
			$("#treeGrid").jqGrid('setGridParam',{datastr:"markup"});
			$("#treeGrid").jqGrid('setCaption', 'Категории наценки');
		    $("#treeGrid").jqGrid('setGridParam', {datatype: "json", url: "../category/get_tree_NS?nodeid=60", page: 1}).trigger('reloadGrid');
			$("#divTable").hide();
			$("#divTree").show();
			$("#divGrid").show();
	    }
		if(operid=='matrix'){
			$("#legendGrid").html('Выбор товарной матрицы:');
			$("#treeGrid").jqGrid('setGridParam', {datastr: "matrix"});
			$("#treeGrid").jqGrid('setCaption', 'Товарная матрица');
			$("#treeGrid").jqGrid('setGridParam', {datatype: "json", url: "../category/get_tree_NS?nodeid=30", page: 1}).trigger('reloadGrid');
			$("#divTable").hide();
			$("#divTree").show();
			$("#divGrid").show();
	    }
	});
//	$('#grouping_add').selectable({
//		selected: function(event, ui){
//			if(ui.selected.tagName!='LI') return;
//			$(ui.selected).appendTo($('#grouping'));
//			$("#"+ui.selected.id+">#a2").removeClass('hide').addClass('show');
//			$("#"+ui.selected.id+">#a1").removeClass('show').addClass('hide');
//		}
//	});
//	$('#grouping').selectable({
//		selected: function(event, ui){
//			if(ui.selected.tagName!='LI') return;
//			$(ui.selected).appendTo($('#grouping_add'));
//			$("#"+ui.selected.id+">#a1").removeClass('hide').addClass('show');
//			$("#"+ui.selected.id+">#a2").removeClass('show').addClass('hide');
//		}
//	});

// Creating gridRep
	var gridRep = function(){
	$("#gridRep").jqGrid({
		sortable: true,
	    //datatype: "json",
		datatype: 'local',
	    height: 350,
	    colModel: [
			{name: 'field0' , index: 'field0' , width: 60, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field1' , index: 'field1' , width: 100, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field2' , index: 'field2' , width: 300, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field3' , index: 'field3' , width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field4' , index: 'field4' , width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field5' , index: 'field5' , width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field6' , index: 'field6' , width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field7' , index: 'field7' , width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field8' , index: 'field8' , width: 90, align: "center",sorttype: "text"},
			{name: 'field9' , index: 'field9' , width: 90, align: "right", sorttype: "number"},
			{name: 'field10', index: 'field11', width: 90, align: "right", sorttype: "number"},
			{name: 'field11', index: 'field10', width: 90, align: "center",sorttype: "text"},
			{name: 'field12', index: 'field12', width: 90, align: "right", sorttype: "number", formatter:"number"},
			{name: 'field13', index: 'field13', width: 90, align: "right", sorttype: "number", formatter:"number"},
			{name: 'field14', index: 'field14', width: 60, align: "right", sorttype: "number", summaryType:'count', summaryTpl:'<b>{0} эл.</b>'},
	    ],
	    //width: 'auto',
	    shrinkToFit: true,
		loadonce: true,
		rowNum:1000000,
	    //rowList: [20, 30, 40, 50, 100],
		sortname: "name",
	    gridview: true,
	    viewrecords: true,
	    toppager: true,
		//footerrow:true,
		multiselect: true,
		//userDataOnFooter: true,
		loadComplete: function(data) {
			//console.log(data);
			if(data['error']){
				$("#dialog_progress").dialog("close");
				setTimeout(function () {
					$("#dialog").css('background-color', 'linear-gradient(to bottom, #f7dcdb 0%, #c12e2a 100%)');
					$("#dialog>#text").html("При выполнении запроса возникла ошибка: <br><br>"
						+ data.error[2] + "<br><br>"
						+ "Сообщите разработчику!");
					$("#dialog").dialog("open");
				}, 1200);
				return;
			}
			if (data['total'] > 0 && data['records'] == 0) {
				setTimeout(function () {
					$("#dialog").css('background-color', 'linear-gradient(to bottom, #def0de 0%, #419641 100%)');
					$("#dialog>#text").html("По Вашему запросу: <br><br>"
						+ "Не найдено ни одной записи!");
					$("#dialog").dialog("open");
				}, 1200);
				return;
			}
//			$("#grouping li").each(function( index ) {
//				var cl = this.className.substr(0,3);
//				$(".jqgroup.ui-row-ltr.gridRepghead_"+index).css("background-image","none");
//				$(".jqgroup.ui-row-ltr.gridRepghead_"+index).addClass(cl);
//			});
//			var ar = new Object();
//			i = 14;
//			var summary = $("#gridRep").jqGrid('getCol', "field"+i, false, 'sum');
//			ar["field" + i] = summary;
//			$("#gridRep").jqGrid('footerData','set', ar);
			$("#dialog_progress").dialog("close");
		},
	    caption: 'Групповая обработка товаров',
	    pager: '#pgridRep',
	});
	$("#gridRep").jqGrid('navGrid', '#pgridRep', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#gridRep").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1")}});
	$("#gridRep").navButtonAdd("#gridRep_toppager",{
		caption: 'Экспорт в Excel', 
		title: 'to Excel', 
		icon: "ui-extlink",
		onClickButton: function () {
			$("#dialog_progress").dialog( "option", "title", 'Ожидайте! Готовим данные для XLS файла');
			$("#dialog_progress").dialog("open");
			setTimeout(function(){
				var gr = $("#gview_gridRep").clone();
				$(gr).find("#pg_gridRep_toppager").remove();
				$(gr).find("#gridRep_toppager").html($("#report_param_str").html());
				$(gr).find("th").filter(function () {if ($(this).css('display') == 'none') $(this).remove();});
				$(gr).find("td").filter(function () {if ($(this).css('display') == 'none') $(this).remove();});
				$(gr).find("table").filter(function () {if ($(this).attr('border') == '0') $(this).attr('border', '1');});
				$(gr).find("td").filter(function () {if ($(this).attr('colspan') > 1) $(this).attr('colspan', '6');});

		$(gr).find("a").remove();
				$(gr).find("div").removeAttr("id");
				$(gr).find("div").removeAttr("style");
				$(gr).find("div").removeAttr("class");
				$(gr).find("div").removeAttr("role");
				$(gr).find("div").removeAttr("dir");
				$(gr).find("span").removeAttr("class");
				$(gr).find("span").removeAttr("style");
				$(gr).find("span").removeAttr("sort");
				$(gr).find("table").removeAttr("id");
				$(gr).find("table").removeAttr("class");
				$(gr).find("table").removeAttr("role");
				$(gr).find("table").removeAttr("tabindex");
				$(gr).find("table").removeAttr("aria-labelledby");
				$(gr).find("table").removeAttr("aria-multiselectable");
				$(gr).find("th").removeAttr("id");
				$(gr).find("th").removeAttr("class");
				$(gr).find("th").removeAttr("role");
				$(gr).find("tr").removeAttr("id");
				$(gr).find("tr").removeAttr("class");
				$(gr).find("tr").removeAttr("role");
				$(gr).find("tr").removeAttr("tabindex");
				$(gr).find("td").removeAttr("id");
				$(gr).find("td").removeAttr("role");
				$(gr).find("td").removeAttr("title");
				$(gr).find("td").removeAttr("aria-describedby");
				$(gr).find("table").removeAttr("style");
				$(gr).find("th").removeAttr("style");
				$(gr).find("tr").removeAttr("style");
				$(gr).find("td").removeAttr("style");

				var html = $(gr).html();
				html = html.split(" грн.").join("");
				html = html.split("<table ").join("<table border='1' ");
				var file_name = 'Товарный ассортимент';
				var report_name = 'report'+reportID;
				$.ajax({
					type: "POST",
					data: ({report_name: report_name, file_name: file_name, html: html}),
					url: '../Engine/set_file',
					dataType: "html",
					success: function (data) {
						$("#dialog_progress").dialog("close");
						var $frame = $('<iframe src="../Engine/get_file?report_name='+report_name+'&file_name='+file_name+'" style="display:none;"></iframe>');
						$('html').append($frame);
					}
				});
			}, 1000);
		}
	});
	$("#gridRep").navButtonAdd('#gridRep_toppager', {
		title: 'Карта товара', buttonicon: "ui-icon-pencil", caption: 'Карта товара', position: "last",
		onClickButton: function () {
		var id = $("#gridRep").jqGrid('getGridParam', 'selrow');
		var node = $("#gridRep").jqGrid('getRowData', id);
		//console.log(id,node,node.Name);
		if (id != '')
			window.location = "../goods/good_info?goodid=" + id;
		}
	});

	//	$("#gridRep").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
	$("#pg_pgridRep").remove();
	$("#pgridRep").removeClass('ui-jqgrid-pager');
	$("#pgridRep").addClass('ui-jqgrid-pager-empty');
	$("#gridRep").gridResize();
		
//	$('#myTab a').click(function (e) {
//		e.preventDefault();
//		$(this).tab('show');
//	});
	}

	$('#btn_selection_run').click(function (e) {
		if($("#gridRep").jqGrid('getRowData').length > 0) $.jgrid.gridUnload("#gridRep");
		gridRep();
		$("#dialog_progress").dialog( "option", "title", 'Ожидайте! Выполняется формирование отчета...');
		$("#dialog_progress").dialog("open");
		//$("#btn_action_run").removeClass('disabled');
		$("#a_tab_action").removeClass('disabledTab');
		$("#a_tab_selection").removeClass('disabledTab');
		$("#a_tab_selection").tab('show');
//		$("#button_selection_run").hide();
//		$("#button_change_run").show();
//return;
		grouping = [];
		//$("#grouping li").each(function( index ) {grouping[index] = this.id;});
		grouping[0] = "g_goodID";
		var start_col = 3;
		var max_col = 15;
		for(i=start_col; i < max_col; i++) {
			$("#gridRep").jqGrid('hideCol', "field" + i);
//console.log('hideCol ' + i);
	    }
		for(i=(max_col - colnames.length); i<max_col; i++){
			$("#gridRep").jqGrid('showCol',"field"+i);
//console.log('showCol '+i);
		}
		var grlen = Object.keys(grouping).length;
		var ar = [];
		for(var id=0; id<grlen; id++){
			if(id==grlen-1) break;
			ar[id] = 'field'+id;
			$("#gridRep").jqGrid('setLabel', "field"+id, grouping[id]);
		}
		if(grouping[id]=='groupName')$("#gridRep").jqGrid('setLabel', "field"+id, "Группа товара");
		if(grouping[id]=='catName')$("#gridRep").jqGrid('setLabel', "field"+id, "Категория товара");
		if(grouping[id]=='markupName')$("#gridRep").jqGrid('setLabel', "field"+id, "Категория наценки");
		if(grouping[id]=='matrixName')$("#gridRep").jqGrid('setLabel', "field"+id, "Товарная матрица");
		if(grouping[id]=='g_goodID'){
//console.log('setlabel '+id);
			$("#gridRep").jqGrid('setLabel', "field"+id, "GoodID");
			id++;
//console.log('setlabel '+id);
			$("#gridRep").jqGrid('setLabel', "field"+id, "Артикул");
			id++;
//console.log('setlabel '+id);
			$("#gridRep").jqGrid('setLabel', "field"+id, "Название");
		}
		id++;
		for(var fi=(max_col - colnames.length); fi < max_col; fi++){
			$("#gridRep").jqGrid('setLabel', "field"+(fi), colnames[fi-(max_col - colnames.length)]);
//console.log('setlabel '+(fi),colnames[fi-(max_col - colnames.length)]);
		}
		if(grlen<=1){
			$("#gridRep").jqGrid('setGridParam', {
				grouping: true,
				groupingView : {
					groupField: ar,
					//groupColumnShow: [false, false, false, false, false, false, false, false, false, false],
					groupColumnShow: [true, true, true, true, true, true, true, true, true, true],
					groupText: ['<b>{0}</b>'],
					//groupCollapse: false,
					groupDataSorted: true,
					//groupOrder: ['asc', 'asc'],
					groupSummary : [true, true, true, true, true, true, true, true, true, true],
					showSummaryOnHide: true,
				}
			});
		}else{
			$("#gridRep").jqGrid('setGridParam', {
				grouping: true,
				groupingView : {
					groupField: ar,
					groupColumnShow: [false, false, false, false, false, false, false, false, false, false],
					//groupColumnShow: [true, true, true, true, true, true, true, true, true, true],
					groupText: ['<b>{0}</b>'],
					//groupCollapse: false,
					groupDataSorted: true,
					//groupOrder: ['asc', 'asc'],
					groupSummary : [true,true,true,true,true,true,true,true,true,true],
					showSummaryOnHide: true,
				}
			});
		}
//		var grouping_str = '';
//		$("#grouping li span").each(function( index ) { grouping_str += ((grouping_str.length==0) ? '' : ', ') + $(this).html();});
		prmRep = "<b>Отбор данных выполнен по критериям:</b> ";
		prmRep += (Object.keys(group).length == 0) ? "" : "<br>" + "Группа товара: " + strJoin(group).join(', ');
		prmRep += (Object.keys(good).length == 0) ? "" : "<br>" + "Товары: " + strJoin(good).join(', ');
		prmRep += (Object.keys(cat).length == 0) ? "" : "<br>" + "Категории товаров: " + strJoin(cat).join(', ');
		prmRep += (Object.keys(markup).length == 0) ? "" : "<br>" + "Категории наценок: " + strJoin(markup).join(', ');
		prmRep += (Object.keys(matrix).length == 0) ? "" : "<br>" + "Товарная матрица: " + strJoin(matrix).join(', ');
//		prmRep += (grouping_str.length == 0) ? "" : "<br>" + "Группировки отчета: " + grouping_str;
		$("#report_param_str").html(prmRep);
//return;
		$("#gridRep").jqGrid('setGridParam', {datatype: "json", url: "../reports/report"+reportID+"_data" +
			"?sid=" + reportID +
			"&grouping=" + strJoin(grouping).join(';') +
			"&group=" + keyJoin(group).join(';') +
			"&goodID=" + keyJoin(good).join(';') +
			"&cat=" + keyJoin(cat).join(';') +
			"&markup=" + keyJoin(markup).join(';') +
			"&matrix=" + keyJoin(matrix).join(';') +
			""}).trigger('reloadGrid');
	});
	$('#myTab a').click(function (e) {
		e.preventDefault();
		if (this.id == 'a_tab_setting') {
//			$("#button_selection_run").show();
//			$("#button_change_run").hide();
		}
		if (this.id == 'a_tab_selection') {
//			$("#button_selection_run").hide();
//			$("#button_change_run").show();
		}
    });

	$('#btn_action_run, #btn_view_param').click(function (e) {
		var id = e.currentTarget.id;
		var set = $("#select_action").select2("val");
		if (set == '0') {
			$("#dialog>#text").html('Вы не выбрали действие!');
			$("#dialog").dialog("open");
			return;
	    }
		var sel = jQuery("#gridRep").jqGrid('getGridParam', 'selarrrow');
		if (sel == '') {
			$("#dialog>#text").html('Вы не выбрали ни одной записи!');
			$("#dialog").dialog("open");
			return;
		}
		var val = '';
	    if		(set == 120) { val = $("#select_type_sticker").select2("val");		} 
		else if (set == 140) { val = $("#select_fold_order").select2("val");	}
		else if (set == 180) { val = $("#select_visible").select2("val");		}
		else				 { val = $("#newvalue").val();					}
		if (val == '' && id != 'btn_view_param') {
			$("#dialog>#text").html('Вы не указали значение!');
			$("#dialog").dialog("open");
			return;
		}
		if (id == 'btn_view_param') {
			set = 'get'+set;
			//$('#btn_action_run').show();
			$("#btn_action_run").removeClass('disabled');		
		}
		var tbl = document.getElementById("res");
		while(tbl.rows.length > 3){tbl.deleteRow(2);}	// очищаем результаты
		for(var i=0;i<sel.length;i++){					// обрабатываем товары
			$.post("../Engine/good_set_param", { goodid: sel[i], action: set, value: val, },
				function (data) {
					var row = tbl.insertRow(2);
					row.insertCell(0).innerHTML = tbl.rows.length-3;
					row.insertCell(1).innerHTML = data.goodid;
					var cell = row.insertCell(2); $(cell).addClass('TAL'); cell.innerHTML = data.article;
					var cell = row.insertCell(3); $(cell).addClass('TAL'); cell.innerHTML = data.name;
					var cell = row.insertCell(4); $(cell).addClass('TAL'); cell.innerHTML = data.value_old;
					var cell = row.insertCell(5); $(cell).addClass('TAL'); cell.innerHTML = data.value_new;
					//row.insertCell(4).innerHTML = data.success;
		    });
		}
	});

	//заполнение select2 - статусы
	var a_action = [
		{id: 0, text: 'действие не выбрано'}, 
		{id: 100, text: 'установить страну производителя'}, 
		{id: 110, text: 'установить торговую марку'}, 
		{id: 120, text: 'установить тип ценника'}, 
		{id: 130, text: 'установить процент вознагр.'}, 
		{id: 140, text: 'установить кратность в заказе'}, 
		{id: 150, text: 'установить вид осн. тары'}, 
		{id: 160, text: 'установить материал осн. тары'}, 
		{id: 170, text: 'установить сегмент ассортимента'}, 
		{id: 180, text: 'установить доступность товара'}, 
		{id: 190, text: 'установить ед. измерения'}, 
		{id: 200, text: 'установить отдел'}, 
		{id: 210, text: 'установить кол-во в упаковке'}, 
		{id: 220, text: 'установить макс. скидку'}, 
	];
	$("#select_action").select2({data: a_action, placeholder: "Выберите действие"});
	$("#select_action").select2("val", 0);
	var select_set = function () {
		$("#set_all").hide();
		$("#set_fold_order").hide();
		$("#set_stickers").hide();
		$("#set_visible").hide();
		set = $("#select_action").select2("val");
		if (set == 120) {
		    $("#set_stickers").show();
		} else if (set == 140) {
		    $("#set_fold_order").show();
		} else if (set == 180) {
		    $("#set_visible").show();
		} else {
		    $("#set_all").show();
		}
	};
	$("#select_action").on("change", function(){select_set();});
	$("#set_all").hide();
	$("#set_fold_order").hide();
	$("#set_stickers").hide();
	$("#set_visible").hide();
		
	var a_status = [{id: 10, text: 'стикер'}, {id: 20, text: 'ценовая планка'}];
	$("#select_type_sticker").select2({data: a_status, placeholder: "Выберите тип ценника"});
	$("#select_type_sticker").select2("val", 0);
	var a_status = [{id: 10, text: 'заказ только упаковкой'}, {id: 20, text: 'заказ по-штучно'}];
	$("#select_fold_order").select2({data: a_status, placeholder: "Выберите кратность для заказа"});
	$("#select_fold_order").select2("val", 0);
	var a_status = [{id: 1, text: 'товар доступен'}, {id: 0, text: 'товар не доступен'}];
	$("#select_visible").select2({data: a_status, placeholder: "Выберите доступность товара"});
	$("#select_visible").select2("val", -1);
	
//	setTimeout(function(){
//		$('#btn_selection_run').click();
//		//$("#a_tab_action").click();
//		$("#select_action").select2("val", 110);
//		//$("#select_type_sticker").select2("val", 100);
//		select_set();
//		//console.log($("#res"));
//	}, 100);
});
</script>
<style>
 #feedback { font-size: 12px; }
 .selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
 .selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
	<ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
		<li class="active"><a id="a_tab_setting" href="#tab_filter" role="tab" data-toggle="tab">Настройки отбора</a></li>
		<li>
			<button id="btn_selection_run" class="btn btn-sm btn-default frameL m0 mr2 h40 hidden-print font14">
				<span class="ui-button-text" style1='width:120px;height:22px;'>Выполнить отбор</span>
			</button>
		</li>
		<li><a id="a_tab_selection" class="disabledTab" href="#tab_selection" role="tab" data-toggle="tab">Отобранные товары</a></li>
		<li><a id="a_tab_action" class="disabledTab" href="#tab_action" role="tab" data-toggle="tab">Действия</a></li>
	</ul>
	<div class="tab-content">
		<div class="active tab-pane min530 m0 w100p ui-corner-tab1 borderColor frameL border1" id="tab_filter">
			<div id="setting_filter" class='p5 frameL w400 h400 ml0 border0' style='display:table;'>
				<legend>Параметры отбора данных:</legend>
				<div class="input-group input-group-sm mt10 w100p">
					<span class="input-group-addon w130">Настройки:</span>
					<div class="w100p" id="select_report_setting" name="select_report_setting"></div>
					<span class="input-group-btn hide">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button"><img class="img-rounded h20 m0" src="../../images/save-as.png">
						</a>
					</span>
				</div>
				<div class="input-group input-group-sm mt20 w100p">
					<span class="input-group-addon w130">Группа товара:</span>
					<input id="group" name="group" type="text" class="form-control">
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
				</div>
				<div class="input-group input-group-sm mt5 w100p">
					<span class="input-group-addon w130">Товар:</span>
					<input id="good" name="good" type="text" class="form-control">
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
				</div>
				<div class="input-group input-group-sm mt5 w100p">
					<span class="input-group-addon w130">Категория товара:</span>
					<input id="cat" name="cat" type="text" class="form-control" >
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
				</div>
				<div class="input-group input-group-sm mt20 w100p">
					<span class="input-group-addon w130">Категория наценки:</span>
					<input id="markup" name="markup" type="text" class="form-control" >
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
				</div>
				<div class="input-group input-group-sm mt20 w100p">
					<span class="input-group-addon w130">Товарная матрица:</span>
					<input id="matrix" name="matrix" type="text" class="form-control" >
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
				</div>
			</div>
			<div id="divGrid" class='p5 ui-corner-all frameL ml5 border0'>
				<legend id="legendGrid"></legend>
				<div id="divTree" class='frameL'>
					<table id="treeGrid"></table>
					<div id="ptreeGrid"></div>
				</div>
				<div id="divTable" class='frameL ml10'>
					<table id="grid1"></table>
					<div id="pgrid1"></div>
				</div>
			</div>
		</div>
		<div class="tab-pane min530 m0 w100p borderColor borderTop1 frameL center border0" id="tab_selection">
			<div id='report_param_str' class="mt10 TAL font14">
			</div>
			<div id='div1' class='center frameL mt10'>
				<table id="gridRep"></table>
				<div id="pgridRep"></div>
			</div>
		</div>
		<div class="tab-pane min530 m0 w100p ui-corner-all borderColor frameL border1" id="tab_action">
			<div class="row">
				<div class="col-md-5">
					<div class='ui-corner-all borderColor border1 p5 m10 w100p'>
						<div class="input-group input-group-sm w100p">
							<span class="input-group-addon w100 TAL">Действие:</span>
							<div class="w100p" id="select_action"></div>
							<span class="input-group-addon w32"></span>
						</div>
						<div id="set_all" class="input-group input-group-sm mt10 w100p">
							<span class="input-group-addon w100 TAL">Значение:</span>
							<input id="newvalue" name="group" type="text" class="form-control" placeholder="введите новое значение">
							<span class="input-group-addon w32"></span>
						</div>
						<div id="set_stickers" class="input-group input-group-sm mt10 w100p">
							<span class="input-group-addon w100 TAL">Значение:</span>
							<div class="w100p" id="select_type_sticker"></div>
							<span class="input-group-addon w32"></span>
						</div>
						<div id="set_visible" class="input-group input-group-sm mt10 w100p">
							<span class="input-group-addon w100 TAL">Значение:</span>
							<div class="w100p" id="select_visible"></div>
							<span class="input-group-addon w32"></span>
						</div>
						<div id="set_fold_order" class="input-group input-group-sm mt10 w100p">
							<span class="input-group-addon w100 TAL">Значение:</span>
							<div class="w100p" id="select_fold_order"></div>
							<span class="input-group-addon w32"></span>
						</div>
					</div>
					<div class='m10 w100p'>
						<button id="btn_view_param" class="btn btn-sm btn-default frameL m0 mr2 w100p h40 hidden-print font14">
							<span class="ui-button-text" style1='width:120px;height:22px;'>Просмотр тек. значений</span>
						</button>
					</div>
					<div class='m10 w100p'>
						<button id="btn_action_run" class="btn btn-sm btn-default frameL m0 mr2 w100p h40 disabled hidden-print font14">
							<span class="ui-button-text" style1='width:120px;height:22px;'>Выполнить действие</span>
						</button>
					</div>
				</div>
				<div class="col-md-7 p0">
					<div class='ui-corner-all borderColor border1 table-responsive p5 m10 w95p h500 scroll-y'>
						<table id="res" class="table table-striped table-bordered table-hover w100p" cellspacing="0">
							<thead><tr><th colspan="6"><h4 class='TAC mt5 mb5' >Результаты выполнения:</h4></th></tr>
								<tr><th>№ п-п</th><th>GoodID</th><th>Артикул</th><th>Название</th><th>Старое знач.</th><th>Новое знач.</th></tr>
							</thead>
							<tbody>
								<tr class="hide">
									<td class="TAC">1</td>
									<td class="TAC">2</td>
									<td class="TAC">3</td>
									<td class="TAC">4</td>
									<td class="TAC">5</td>
									<td class="TAC">6</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
<div id="dialog_progress" title="Ожидайте!">
	<img class="ml30 mt20 border0 w300" src="../../img/progress_circle5.gif">
</div>
