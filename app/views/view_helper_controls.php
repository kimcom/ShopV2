<script type="text/javascript">
    $(document).ready(function () {
	//превратим div dialog в "окно сообщений"
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
			}}]
	});
	//отработка нажатия на вкладку
	$('#myTab a').click(function (e) {
		e.preventDefault();
		$(this).tab('show');
	});
	//активируем после старта 1 вкладку
	$("#a_tab1").tab('show');
	//отработка нажатия на кнопку "сохранить"
	$("#button_save").click(function () {
		$("#dialog>#text").html('Тестовое сообщение!');
		$("#dialog").dialog("open");
	});
	//отработка нажатия на ссылку btn_name_edit
	$("#btn_name_edit").click(function () {
		if ($("#name2").prop("disabled")) {
		$("#name2").prop("disabled", false);
		$("#name2").val("Редактируемое значение (disabled = false)");
		} else {
		$("#name2").prop("disabled", true);
		$("#name2").val("Редактируемое значение (disabled = true)");
		}
	});
	//заполнение select2 через ajax - подразделения
	$.post('../Engine/select2?action=unit&type=0', function (json) {
		$("#select_unitID").select2({placeholder: "Выберите подразделение", data: {results: json, text: 'text'}});
		$("#select_unitID").select2("val", 2);//значение по умолчанию Сисадмины
	});
	//заполнение select2 - статусы
	var a_status = [{id: 0, text: 'можно начинать'}, {id: 20, text: 'постановка задач'}, {id: 30, text: 'в работе'}, {id: 50, text: 'пауза'}, {id: 60, text: 'на проверке'}, {id: 100, text: 'завершено'}];
	$("#select_status").select2({data: a_status, placeholder: "Выберите статус"});
	$("#select_status").select2("val", 20);
	//заполнение select2 - версия 1С
	var a_db1C = [{id: 'ShopV1', text: 'ShopV1'}, {id: 'ShopV2', text: 'ShopV2'}];
	$("#select_db1C").select2({data: a_db1C, placeholder: "Выберите версию 1С"});
	$("#select_db1C").select2("val", 'ShopV2');
	//заполнение select2 через ajax - сотрудники
	$.post('../Engine/select2?action=user', function (json) {
		$("#select_userID_resp").select2({placeholder: "Выберите сотрудника", data: {results: json, text: 'text'}});
		$("#select_userID_resp").select2("val", 5);
	});
	//заполнение select2 через ajax - магазины
	$.post('../Engine/select2?action=point', function (json) {
		$("#select_point_multi").select2({multiple: true, placeholder: "Выберите магазин", data: {results: json, text: 'text'}});
		$("#select_point_multi").select2("val", 2010);
	});
	$.post('../Engine/select2?action=point', function (json) {
		$("#select_point").select2({multiple: false, placeholder: "Выберите магазин", data: {results: json, text: 'text'}});
		$("#select_point").select2("val", 2004);
	});
	//заполнение select2 через ajax - настройки
	$.post('../Engine/setting_get?sid=' + 4, function (json) {
		$("#select_report_setting").select2({placeholder: "Выберите настройку отчета", data: {results: json, text: 'text'}});
		$("#select_report_setting").select2("val", "тест");
	});
	//обработаем сохранение настройки
	$("#setting a").click(function () {
		setting = $("#select_report_setting").select2("data");
		$("#dialog").css('background-color', '');
		$("#dialog>#text").html('Вы выбрали настройку: ' + setting.text);
		$("#dialog").dialog("open");
	});
	//datepickers
	$("#DT_plan").datepicker({numberOfMonths: 1, dateFormat: 'dd/mm/yy', showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#DT_fact").datepicker({numberOfMonths: 3, dateFormat: 'dd/mm/yy', showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#DT_start").datepicker({numberOfMonths: 1, dateFormat: 'dd/mm/yy', showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#DT_stop").datepicker({numberOfMonths: 1, dateFormat: 'dd/mm/yy', showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	dt = new Date();
	dt.setMonth(dt.getMonth() - 1, 1);
	$("#DT_start").datepicker("setDate", dt);
	dt = new Date();
	dt.setMonth(dt.getMonth() + 1, 1);
	$("#DT_start").datepicker("setDate", dt);
	//datepickers обработчик нажатий кнопок
	$("#datapickers a").click(function () {
		if ($(this).attr("type") != 'button')
		return;
		var command = this.parentNode.previousSibling.previousSibling;
		if (command.tagName == 'SPAN')
		command = command.previousSibling.previousSibling;
		if (command.tagName == "INPUT")
		operid = command.id;
		if ($(this).html() == 'X')
		$("#" + operid).val("");
		if ($(this).html() == '...')
		$("#" + operid).datepicker("show");
	});
	
	//список проектов для выезжающей вкладки
	fsL = 0;
	// Creating gridL
	$("#gridL").jqGrid({
		sortable: true,
		url: "../engine/jqgrid3?action=project_list&f1=ProjectID&f2=Name&pr.Status<>1000",
		datatype: "json",
		height: 'auto',
		colNames: ['№', 'Название'],
		colModel: [
		    {name: 'pr_ProjectID', index: 'pr.ProjectID', width: 80, align: "center", sorttype: "text", search: true},
		    {name: 'pr_Name', index: 'pr.Name', width: 350, align: "left", sorttype: "text", search: true},
		],
		gridComplete: function () {if (!fsL) {fsL = 1;filter_restore("#gridL");}},
		width: '190',
//		shrinkToFit: false,
		rowNum: 20,
//		rowList: [10, 20, 30, 40, 50, 100],
		sortname: "pr.ProjectID",
		sortorder: "desc",
		viewrecords: true,
		gridview: true,
//		toppager: true,
		caption: "Список проектов",
		editurl: '../project/operation',
		pager: '#pgridL'
	    });
	    $("#gridL").jqGrid('navGrid', '#pgridL', {edit: false, add: false, del: false, search: false, refresh: false, cloneToTop: false});
	    $("#gridL").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#gridL");}});
//	    $("#pg_pgridL").remove();
	    $("#pgridL").remove();
	    $("#rs_mgridL").remove();
//	    $("#pgridL").removeClass('ui-jqgrid-pager');
	    $("#gbox_gridL").removeClass('ui-corner-all');
	    $("#gview_gridL .ui-jqgrid-titlebar").remove();
//		console.log($("#gview_gridL .ui-jqgrid-titlebar"));
//	    $("#pgridL").addClass('ui-jqgrid-pager-empty');
	    //клавиатура
	    $("#gridL").jqGrid('bindKeys', {"onEnter": function (rowid) {
		    alert("You enter a row with id:" + rowid)
		}});
	    //$("#gridL").gridResize();
});
</script>
<style>
	#feedback { font-size: 12px; }
	.selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
	.selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
	<ul id="myTab" class="nav nav-tabs floatL hidden-print" role="tablist">
		<li><a id="a_tab1" href="#tab_1" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
				<legend class="h20">Информация о проекте</legend></a></li>
		<li><a id="a_tab2" href="#tab_2" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
				<legend class="h20">Информация о задаче</legend></a></li>
	</ul>
	<div class="floatL">
		<button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
			<span class="ui-button-text" style='width:120px;height:22px;'>Сохранить данные</span>
		</button>
	</div>
	<div id="tab-content" class="tab-content">
		<div class="tab-pane min0 m0 w100p ui-corner-all borderColor frameL border1 h60" id="tab_1">
			Tab-1
		</div>
		<div class="tab-pane min0 m0 w100p ui-corner-all borderColor frameL border1 h60" id="tab_2">
			Tab-2
		</div>
	</div>
</div>
<br>
<div id="lpanel_button" class="border0">
	<div style="padding-left: 10px; padding-top: 10px; padding-bottom: 10px;width: 1ch; text-align: center; word-wrap: break-word;">Список&nbsp;Проектов</div>
<!--	<div class="vtext border1 pt50 w100" style="display: block;">Список проектов</div>-->
<!--	<div class="vtext border1 pt50 w100" style="display: block;">Список проектов</div>-->
<!--	<span class="ui-icon ui-icon-triangle-1-e"></span>-->
<!--	<img class="img-rounded pt10 m0" src="../../images/right-32.png">-->
	<div id="lpanel" class="border0">
		<h4>Список проектов</h4>
		<div id='div1' class='frameL pl5' >
			<table id="gridL"></table>
			<div id="pgridL"></div>
		</div>
	</div>
</div>
<br>
<div class="container center w500">
	<label>INPUT</label>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Проект:</span>
		<span id="id_span" class="input-group-addon form-control TAL">Не редактируемое значение</span>
		<span class="input-group-addon w32"></span>
	</div>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Название:</span>
		<input id="name1" type="text" class="form-control TAL" value="Редактируемое значение">
		<span class="input-group-addon w32"></span>
	</div>               
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Название упр.:</span>
		<input id="name2" type="text" class="form-control TAL" value="Редактируемое значение (disabled = true)" disabled>
		<span class="input-group-btn w32"><a id="btn_name_edit" class="btn btn-default w100p" type="button">...</a></span>
	</div>
	<label>SELECT</label>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Подразд.:</span>
		<div class="w100p" id="select_unitID"></div>
		<span class="input-group-addon w32"></span>
	</div>               
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Статус:</span>
		<div class="w100p" id="select_status"></div>
		<span class="input-group-addon w32"></span>
	</div>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Версия 1С:</span>
		<div class="w100p" id="select_db1C"></div>
		<span class="input-group-addon w32"></span>
	</div>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Ответствен.:</span>
		<div class="w100p" id="select_userID_resp"></div>
		<span class="input-group-addon w32"></span>
	</div>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Магазины (multi):</span>
		<div class="w100p" id="select_point_multi"></div>
		<span class="input-group-addon w32"></span>
	</div>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Магазины:</span>
		<div class="w100p" id="select_point"></div>
		<span class="input-group-addon w32"></span>
	</div>
	<div id="setting" class="input-group input-group-sm mt10 w100p">
		<span class="input-group-addon w130">Настройки:</span>
		<div class="w100p" id="select_report_setting" name="select_report_setting"></div>
		<span class="input-group-btn hide"><a class="btn btn-default w100p" type="button">X</a></span>
		<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button"><img class="img-rounded h20 m0" src="../../images/save-as.png"></a></span>
	</div>
	<label>textarea</label>
	<div class="input-group input-group-sm w100p">
		<span class="input-group-addon w20p TAL">Описание:</span>
		<textarea id="description" name="description" rows="6" type="text" style="height: auto;" class="form-control TAL p5">
многострочный
текст
		</textarea>
		<span class="input-group-addon w32"></span>
	</div>
	<label>datapicker</label>
	<div id="datapickers">
		<div class="input-group input-group-sm w100p">
			<span class="input-group-addon w25p TAL">Срок план.:</span>
			<input id="DT_plan" name="DT_plan" type="text" class="form-control TAL" value="15/03/2015">
			<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
			<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
		</div>
		<div class="input-group input-group-sm w100p">
			<span class="input-group-addon w25p TAL">Срок фактич.:</span>
			<input id="DT_fact" name="DT_fact" type="text" class="form-control TAL" value="">
			<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
			<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
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

<div id="dialog" title="ВНИМАНИЕ!"><p id='text'></p></div>
