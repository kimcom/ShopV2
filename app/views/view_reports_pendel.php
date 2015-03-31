<script type="text/javascript">
$(document).ready(function () {
	var reportID = 11; 
//Object Converter
	oconv	= function (a) {var o = {};for(var i=0;i<a.length;i++) {o[a[i]] = '';} return o;}
	strJoin = function (obj){ var ar = []; for (key in obj){ar[ar.length] = obj[key];}return ar;}
	keyJoin = function (obj){ var ar = []; for (key in obj){ar[ar.length] = key;}return ar;}
	clearObj= function (obj){ for(key in obj){for(k in obj[key]){delete obj[key][k];}}return obj;}
	var settings = new Object();
	var cat = new Object();
	settings['cat']=cat;
	var colnames = ['Кол-во','Себест.','Оборот','Доход','% наценки'];
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 400, //height: 300,
		buttons: [{text: "Закрыть", click: function () {
			    $(this).dialog("close");}}],
		show: {effect: "clip",duration: 500},
		hide: {effect: "clip",duration: 500}
    });
	$("#dialog_progress").dialog({
		autoOpen: false, modal: true, width: 400, height: 400,
		show: {effect: "explode",duration: 1000},
		hide: {effect: "explode",duration: 1000}
    });
	dt = new Date();
	dt.setMonth(dt.getMonth() - 1, 1);
	$("#DT_start").datepicker({
		//showOn: "both", 
		numberOfMonths: 1,
		showButtonPanel: true, 
		dateFormat: 'dd/mm/yy',
		closeText: "Закрыть",
		//showAnim: "fold"
	});
	$("#DT_start").datepicker("setDate", dt);
	dt = new Date();
	dt.setDate(0);
    $("#DT_stop").datepicker({
		//showOn: "both", 
		numberOfMonths: 1,
		showButtonPanel: true,
		dateFormat: 'dd/mm/yy'
	});
	$("#DT_stop").datepicker("setDate", dt);
	$(".ui-datepicker-trigger").addClass("hidden-print");
	
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
				if(k[0]=='DT_start') {$("#DT_start").val(k[1]);continue;}
				if(k[0]=='DT_stop') {$("#DT_stop").val(k[1]);continue;}
			}
//$('#button_report_run').click();
		});
	});
	
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
			$.post("../Engine/setting_set"+
					"?DT_start="+ $("#DT_start").val()+
					"&DT_stop="	+ $("#DT_stop").val(),
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
		if(operid=='DT_start')
			$("#DT_start").datepicker("show");
		if(operid=='DT_stop')
			$("#DT_stop").datepicker("show");
	});

//test pivot
	jQuery("#grid1").jqGrid(
			'jqPivot',
		    "../reports_fin/pendel_data?sid=11&DT_start=01/01/2015&DT_stop=28/02/2015",
//			{},
		    // pivot options
			    {
				xDimension: [
				    {dataName: 'field0', label: 'Валовый доход', width: 90}
//					,
//				    {dataName: 'field1', label: 'Product', width: 90},
//				    {dataName: 'field4', label: 'Product', width: 90},
//				    {dataName: 'field5', label: 'Product', width: 90}
				],
				yDimension: [
				    {
						dataName: 'field1',
						converter : function(value, xData, yData) {
								console.log(value, xData, yData);
							    return value;
						}
					}
				],
				aggregates: [
					{	member: 'field2', label: 'Себест.',					
						width: 50, align: 'right', sorttype: "number", formatter: 'number', aggregator: 'sum'},
				    {	member: 'field3', label: 'Оборот',					
						width: 50, align: 'right', sorttype: "number", formatter: 'number', aggregator: 'sum'},
				    {	member: 'field4', label: 'Доход',					
						width: 50, align: 'right', sorttype: "number", formatter: 'number', aggregator: 'sum'}
				],
//				rowTotals: true
//				,
				colTotals: true
			    },
		    // grid options
		    {
		    //url: "../reports_fin/pendel_data?sid=11&DT_start=01/01/2015&DT_stop=28/02/2015",
//			height: 'auto',
//			colnNames: ['Валовый доход', 'Период', 'Себест.', 'Оборот', 'Доход', '% наценки'],
//			colModel: [
//				{name: 'field0', index: 'field0', width: 200, align: "left", sorttype: "text", summaryType: 'count', summaryTpl: '<b class="ml10">Итого ({0} эл.):</b>'},
//				{name: 'field1', index: 'field1', width: 200, align: "left", sorttype: "text", summaryType: 'count', summaryTpl: '<b class="ml10">Итого ({0} эл.):</b>'},
//				{name: 'field2', index: 'field2', width: 90, align: "right", sorttype: "number", formatter: "number", summaryType: 'sum', summaryTpl: '<b>{0} </b>'},
//				{name: 'field3', index: 'field3', width: 90, align: "right", sorttype: "number", formatter: "number", summaryType: 'sum', summaryTpl: '<b>{0} грн.</b>'},
//				{name: 'field4', index: 'field4', width: 90, align: "right", sorttype: "number", formatter: "number", summaryType: 'sum', summaryTpl: '<b>{0} грн.</b>'},
//				{name: 'field5', index: 'field5', width: 90, align: "right", sorttype: "number", formatter: "number", summaryType: 'sum', summaryTpl: '<b>{0} грн.</b>'},
//			],
//			datatype: "json",
			width: 700,
//			rowNum: 10,
			pager: "#pager",
			caption: "Amounts and quantity by category and product"
	});

// Creating gridRep
	var gridRep = function(){
	$("#gridRep").jqGrid(
//		{
//			xDimension : [{dataName: 'field0', width: 90}],
//			yDimension: [
//				{
//				dataName: 'field1',
//				converter: function (value, xData, yData) {
//					return 'Period';
//				}
//			}, {dataName: 'field1'}],
//			aggregates: [{
//				member: 'field3',
//				aggregator: 'sum',
//				width: 50,
//				formatter: 'number',
//				align: 'right',
//				summaryType: 'sum'
//			}]
//		},
		{
		sortable: true,
	    //datatype: "json",
		datatype: 'local',
		height: 'auto',
		colnNames: ['Валовый доход','Период','Себест.','Оборот', 'Доход', '% наценки'],
		colModel: [
			{name: 'field0', index: 'field0', width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field1', index: 'field1', width: 200, align: "left", sorttype: "text",summaryType:'count', summaryTpl:'<b class="ml10">Итого ({0} эл.):</b>'},
			{name: 'field2', index: 'field2', width: 90, align: "right", sorttype: "number", formatter:"number", summaryType:'sum', summaryTpl:'<b>{0} </b>'},
			{name: 'field3', index: 'field3', width: 90, align: "right", sorttype: "number", formatter:"number", summaryType:'sum', summaryTpl:'<b>{0} грн.</b>'},
			{name: 'field4', index: 'field4', width: 90, align: "right", sorttype: "number", formatter:"number", summaryType:'sum', summaryTpl:'<b>{0} грн.</b>'},
			{name: 'field5', index: 'field5', width: 90, align: "right", sorttype: "number", formatter:"number", summaryType:'sum', summaryTpl:'<b>{0} грн.</b>'},
	    ],
	    //width: 'auto',
	    shrinkToFit: true,
		loadonce: true,
		rowNum:10000000,
	    gridview: true,
//		footerrow:true,
		//userDataOnFooter: true,
	    toppager: true,
		loadComplete: function(data) {
			if(data['error']){
				setTimeout(function () {$("#dialog_progress").dialog("close");},10);
				setTimeout(function () {
					$("#dialog").css('background-color','linear-gradient(to bottom, #f7dcdb 0%, #c12e2a 100%)');
					$("#dialog>#text").html("При выполнении запроса возникла ошибка: <br><br>"
						//+ data.error[2] + "<br><br>"
						+ "Сообщите разработчику!");
					$("#dialog").dialog("open");
				},1200);
				return;
			}
			if(data['total']>0&&data['records']==0){
				setTimeout(function () {$("#dialog_progress").dialog("close");}, 10);
				setTimeout(function () {
					$("#dialog").css('background-color', 'linear-gradient(to bottom, #def0de 0%, #419641 100%)');
					$("#dialog>#text").html("По Вашему запросу: <br><br>"
						+ "Не найдено ни одной записи!");
					$("#dialog").dialog("open");
				},1200);
			    return;
			}
//			$("#grouping li").each(function( index ) {
//				var cl = this.className.substr(0,3);
//				$(".jqgroup.ui-row-ltr.gridRepghead_"+index).css("background-image","none");
//				$(".jqgroup.ui-row-ltr.gridRepghead_"+index).addClass(cl);
//			});
//			var ar = new Object();
//			for(i=10;i<15;i++){
//				var summary = $("#gridRep").jqGrid('getCol', "field"+i, false, 'sum');
//				ar["field"+i] = summary;
//			}
//			i = 14;
//			var summary = $("#gridRep").jqGrid('getCol', "field"+i, false, 'avg');
//			ar["field" + i] = summary;
//			$("#gridRep").jqGrid('footerData','set', ar);
//			setTimeout(function () {$("#dialog_progress").dialog("close");}, 10);
			$("#dialog_progress").dialog("close");
			if(data.records>2000){
				$("#dialog").css('background-color','');
				$("#dialog>#text").html("По Вашему запросу найдено: "+data.records+" записей.<br><br>"
						+ "В отчет выведены первые 2000 записей.<br><br>"
						+ "Итоговые суммы рассчитаны только по выведенным записям.<br><br>"
						+ "Вы можете исправить параметры отбора и группировки отчета "
						+ "для получения правильных итоговых расчетов."
				);
			    $("#dialog").dialog("open");
			}
//			var test = data.query;
//			test = test.replace(new RegExp("\t","g"),"&nbsp&nbsp&nbsp&nbsp&nbsp");
//			test = test.replace(new RegExp("\r\n","g"),"<br>");
//			$("#test").html(test);
		},
	    caption: 'Отчет "Profit and Lost"',
	    pager: '#pgridRep',
	});
	$("#gridRep").jqGrid('navGrid', '#pgridRep', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#gridRep").navButtonAdd("#gridRep_toppager",{
		caption: 'Экспорт в Excel', 
		title: 'to Excel', 
		icon: "ui-extlink",
		onClickButton: function () {
			$("#dialog_progress").dialog( "option", "title", 'Ожидайте! Готовим данные для XLS файла');
			$("#dialog_progress").dialog("open");
			setTimeout(function () {
				var gr = $("#gview_gridRep").clone();
				$(gr).find("#pg_gridRep_toppager").remove();
				$(gr).find("#gridRep_toppager").html($("#report_param_str").html());
				$(gr).find("th").filter(function () {if ($(this).css('display') == 'none')$(this).remove();});
				$(gr).find("td").filter(function () {if ($(this).css('display') == 'none')$(this).remove();});
				$(gr).find("table").filter(function () {if ($(this).attr('border') == '0')$(this).attr('border', '1');});
				$(gr).find("td").filter(function () {if ($(this).attr('colspan') > 1)$(this).attr('colspan', '6');});
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
				
				var file_name = 'P&L';
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
	//	$("#gridRep").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
	$("#pg_pgridRep").remove();
	$("#pgridRep").removeClass('ui-jqgrid-pager');
	$("#pgridRep").addClass('ui-jqgrid-pager-empty');
	$("#gridRep").gridResize();
		
	$('#myTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
	}

	$('#button_report_run').click(function (e) {
		$("#gridRep").jqGrid("GridUnload");
		gridRep();
		$("#dialog_progress").dialog( "option", "title", 'Ожидайте! Выполняется формирование отчета...');
		$("#dialog_progress").dialog("open");
		$("#a_tab_report").tab('show');
		prmRep = "<b>Отбор данных выполнен по критериям:</b> ";
		prmRep += "<br>" + "Период с " + $("#DT_start").val() + " по " + $("#DT_stop").val();
		$("#report_param_str").html(prmRep);
		$("#gridRep").jqGrid('setGridParam', {datatype: "json", url: "../reports_fin/pendel_data" +
			"?sid=" + reportID +
			"&DT_start=" + $("#DT_start").val() +
			"&DT_stop=" + $("#DT_stop").val() +
			""}).trigger('reloadGrid');
	});
	$("#a_tab_report").tab('show');
});
</script>
<style>
	#feedback { font-size: 12px; }
	.selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
	.selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
	<ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
		<li class="active"><a href="#tab_filter" role="tab" data-toggle="tab">Настройки отбора</a></li>
		<li><a id="a_tab_report" href="#tab_report" role="tab" data-toggle="tab">Отчет "Profit and Lost"</a></li>
	</ul>
	<div class="floatL">
		<button id="button_report_run" class="btn btn-sm btn-info frameL m0 h40 hidden-print font14">
			<span class="ui-button-text" style1='width:120px;height:22px;'>Сформировать отчет</span>
		</button>
	</div>
	<div id='test' class='frameL mt10 text-left'></div>
	<div class="tab-content">
		<div class="active tab-pane min530 m0 w100p ui-corner-all borderColor frameL border1" id="tab_filter">
			<div id="setting_filter" class='p5 ui-corner-all frameL w400 h400 ml0 border0' style='display:table;'>
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
				<div class="input-group input-group-sm mt5 w100p">
					<span class="input-group-addon w130">Период с:</span>
					<input id="DT_start" name="DT_start" type="text" class="form-control" placeholder="Дата нач." required>
					<span class="input-group-btn hide">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
					<span class="input-group-addon">по:</span>
					<input id="DT_stop"  name="DT_stop"  type="text" class="form-control" placeholder="Дата кон." required>
					<span class="input-group-btn hide">
						<a class="btn btn-default w100p" type="button">X</a>
					</span>
					<span class="input-group-btn w32">
						<a class="btn btn-default w100p" type="button">...</a>
					</span>
				</div>
			</div>
		</div>
		<div class="tab-pane m0 w100p min530 borderTop1 frameL center border0" id="tab_report">
			<div id='report_param_str' class="mt10 TAL font14">
			</div>
			<div id='div2' class='center frameL mt10 border1'>
				<table id="grid1"></table>
				<div id="pgrid1"></div>
			</div>
			<div id='div1' class='center frameL mt10'>
				<table id="gridRep"></table>
				<div id="pgridRep"></div>
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
