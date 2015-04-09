<script type="text/javascript">
$(document).ready(function () {
	var reportID = 12; 
//Object Converter
	oconv	= function (a) {var o = {};for(var i=0;i<a.length;i++) {o[a[i]] = '';} return o;}
	strJoin = function (obj){ var ar = []; for (key in obj){ar[ar.length] = obj[key];}return ar;}
	keyJoin = function (obj){ var ar = []; for (key in obj){ar[ar.length] = key;}return ar;}
	clearObj= function (obj){ for(key in obj){for(k in obj[key]){delete obj[key][k];}}return obj;}
	var settings = new Object();
	var cat = new Object();
	settings['cat']=cat;
	var colnames = ['Кол-во','Себест.','Оборот','Доход','% наценки'];
	$("#history").dialog({
		autoOpen: false, modal: true, width: 720, height: 550,
		buttons: [{text: "Закрыть", click: function () {
			    $(this).dialog("close");}}],
		show: {effect: "clip",duration: 300},
		hide: {effect: "clip",duration: 300}
    });
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

	$('#myTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
	
	$('#button_report_run').click(function (e) {
		$("#dialog_progress").dialog( "option", "title", 'Ожидайте! Выполняется формирование отчета...');
		$("#dialog_progress").dialog("open");
		$.post("../reports_fin/pendel_data2?action=3&DT_start=" + $("#DT_start").val() + "&DT_stop=" + $("#DT_stop").val(), function (json) {
			$('#div_report').html(json.table1);
			//if($('#example1').hasClass('dataTable'))$('#example1').dataTable().fnDestroy();
			//$('#example1').html(json.table1);
			//$('#example1').dataTable({"bDestroy": true,"searching": false, "paging": false, "info": false, "ordering": false});
//			if($('#example2').hasClass('dataTable'))$('#example2').dataTable().fnDestroy();
//			$('#example2').html(json.table2);
//			$('#example2').dataTable({"bDestroy": true,"searching": false, "paging": false, "info": false, "ordering": false});
			setTimeout(function () {$("#dialog_progress").dialog("close");}, 300);
		});
		
		$("#a_tab_report").tab('show');
		prmRep  = "<b>Отчет \"Profit and Lost\"</b> ";
		prmRep += "<br>" + "Период с " + $("#DT_start").val() + " по " + $("#DT_stop").val();
		$("#report_param_str").html(prmRep);
	});
//	$("#a_tab_report").tab('show');
//	$("#history").dialog("open");
});
function history(url){
	$("#history").dialog("open");
	$.post(url, function (html) {
		$('#history').html(html);
		start();
	});
}
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
			<div id='div_report' class='center frame0 mt10'></div>
<!--			<div id='div1' class='center frame0 mt10'>
				<table id="example1" class="table table-striped table-bordered" cellspacing="0"  width="100%">
				</table>
				<table id="example2" class="table table-striped table-bordered" cellspacing="0"  width="100%">
				</table>
			</div>-->
		</div>
	</div>
</div>
<div id="history" title="Расшифровка движений">
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
<div id="dialog_progress" title="Ожидайте!">
	<img class="ml30 mt20 border0 w300" src="../../img/progress_circle5.gif">
</div>
