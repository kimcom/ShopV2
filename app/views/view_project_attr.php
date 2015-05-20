<?php
$cnn = new Cnn();
if (isset($_REQUEST['projectid'])) {
	$projectid = $_REQUEST['projectid'];
	$row = $cnn->project_info();
	
	$taskid = $_REQUEST['taskid'];
	if($taskid!='')	$rowT = $cnn->task_info();
	
	$unitID = $row['UnitID'];
	if($unitID==null) $unitID = 0;
	if(!isset($row)) $unitID = 0;
	
	$userID_resp = $row['UserID_resp'];
	if($userID_resp==null)	$userID_resp = 0;
	if(!isset($row))		$userID_resp = 0;

	$status = $row['Status'];
	if($status ==null)		$status = 0;
	if(!isset($row))		$status = 0;

	$t_parentid = $rowT['ParentID'];
	if($t_parentid ==null)	$t_parentid = 0;
	if(!isset($rowT))		$t_parentid = 0;

	$t_unitID = $rowT['UnitID'];
	if ($t_unitID == null) $t_unitID = 0;
	if (!isset($rowT)) $t_unitID = 0;

	$t_status = $rowT['Status'];
	if ($t_status == null) $t_status = -1;
	if (!isset($rowT)) $t_status = -1;

	$t_userID_resp = $rowT['UserID_resp'];
	if ($t_userID_resp == null) $t_userID_resp = 0;
	if (!isset($rowT)) $t_userID_resp = 0;
} else {
	echo "не могу найти параметр projectid";
	return;
}
?>
<script type="text/javascript">
var tab_active = '';
$(document).ready(function () {
	$('#myTab a').click(function (e) {
		e.preventDefault();
		if(this.id=='a_tab_task')tab_active = 'task';
		if(this.id=='a_tab_project')tab_active = 'project';
		$(this).tab('show');
	});
	if($("#taskid").val()!=""){
		$("#a_tab_task").tab('show');
		tab_active = 'task';
	}else{
		$("#a_tab_project").tab('show');
		tab_active = 'project';
	}
	
	$("#dialog").dialog({
	    autoOpen: false, modal: true, width: 400,
	    buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
	    }}]
	});
	$("#button_save").click(function () {
		if($("#name").val()==''||$("#select_unitID").select2("val")==0) {
			$("#dialog>#text").html('Введите название проэкта и подразделение!');
			$("#dialog").dialog("open");
			return;
		};
		if(tab_active=='project'){
			$.post('../engine/project_save', {
				projectid: $("#projectid").val(),
				unitID: $("#select_unitID").select2("val"),
				name: $("#name").val(),
				description: $("#description").val(),
				status: $("#select_status").select2("val"),
				userID_resp: $("#select_userID_resp").select2("val"),
				DT_plan: $("#DT_plan").val(),
				DT_fact: $("#DT_fact").val()
				},
				function (data) {
					$("#dialog>#text").html(data.message);
					$("#dialog").dialog("open");
					if (data.new_id > 0) {
						$("#projectid").val(data.new_id);
						$("#projectid_span").html(data.new_id);
				}
			},"json");
		}else{
			$.post('../engine/task_save', {
				taskid: $("#taskid").val(),
				projectid: $("#projectid").val(),
				parentID: $("#select_parentID").select2("val"),
				status: $("#select_taskStatus").select2("val"),
				unitID: $("#select_taskUnitID").select2("val"),
				name: $("#taskName").val(),
				description: $("#taskDescription").val(),
				userID_resp: $("#select_task_userID_resp").select2("val"),
				DT_plan: $("#taskDT_plan").val(),
				DT_fact: $("#taskDT_fact").val()
				},
				function (data) {
					$("#dialog>#text").html(data.message);
					$("#dialog").dialog("open");
					if (data.new_id > 0) {
						$("#taskid").val(data.new_id);
						$("#taskid_span").html(data.new_id);
						$("#grid2").trigger('reloadGrid');
						task_open(data.new_id);
				}
			},"json");
		}
	});
	fs2 = 0;
// Creating grid2
	$("#grid2").jqGrid({
		sortable: true,
		url:"../engine/jqgrid3?action=task_list&pc.ProjectID=<?php echo $projectid;?>&f1=TaskID&f2=ParentName&f3=UnitName&f4=Name&f5=Status&f6=UserName_resp&f7=DT_plan&f8=DT_fact&f9=DT_create&f10=UserName_create",
		datatype: "json",
		height: '150px',
	    colNames: ['№ задачи', 'Зависимость', 'Отдел', 'Название', 'Статус', 'Ответственный', 'План. запуск', 'Факт. запуск', 'Дата добавления', 'Автор'],
	    colModel: [
			{name: 'pc_TaskID',			index: 'pc.TaskID',			width:  60, align: "center",sorttype: "text", search: true},
			{name: 'pc_ParentID',		index: 'pc.ParentID',		width: 150, align: "center",sorttype: "text", search: true},
			{name: 'un_UnitName',		index: 'un.UnitName',		width:  60, align: "center",sorttype: "text", search: true},
			{name: 'pc_Name',			index: 'pc.Name',			width: 150, align: "left",	sorttype: "text", search: true},
			{name: 'pc_Status',			index: 'pc.Status',			width:  60, align: "center",sorttype: "text", search: true},
			{name: 'up_UserName_resp',	index: 'up.UserName_resp',	width: 120, align: "left",	sorttype: "text", search: true},
			{name: 'pc_DT_plan',		index: 'pc.DT_plan',		width: 120, align: "center",sorttype: "text", search: true},
			{name: 'pc_DT_fact',		index: 'pc.DT_fact',		width: 120, align: "center",sorttype: "text", search: true},
			{name: 'pc_DT_create',		index: 'pc.DT_create',		width: 130, align: "center",sorttype: "text", search: true},
			{name: 'u_UserName_create',	index:'u.UserName_create',	width: 150, align: "left",	sorttype: "text", search: true},
		    ],
	    gridComplete: function () {if (!fs2) { fs2 = 1; filter_restore("#grid2"); }},
		onSelectRow: function (rowid, status, e) {
			task_open(rowid);
		},
	    width: 'auto',
	    shrinkToFit: false,
	    rowNum: 6,
	    rowList: [6, 12, 20, 30, 40, 50, 100],
	    sortname: "OrderID",
	    viewrecords: true,
	    gridview: true,
	    toppager: true,
//		hiddengrid: true,
	    caption: "Список задач к проекту",
	    pager: '#pgrid2'
	});
	$("#grid2").jqGrid('navGrid', '#pgrid2', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid2").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid2");}});
	$("#grid2").navButtonAdd('#grid2_toppager', {
		title: 'Добавить задачу', buttonicon: "ui-icon-pencil", caption: 'Добавить задачу ', position: "last",
		onClickButton: function () {
				task_create(false);
		}
    });
	$("#grid2").navButtonAdd('#grid2_toppager', {
		title: 'Открыть инф. карту', buttonicon: "ui-icon-pencil", caption: 'Открыть инф. карту', position: "last",
		onClickButton: function () {
			var id = $("#grid2").jqGrid('getGridParam', 'selrow');
			var node = $("#grid2").jqGrid('getRowData', id);
		    if (id != null) {
				task_open(id);
		    } else {
				$("#dialog>#text").html('Сначала выберите запись в таблице!');
				$("#dialog").dialog("open");
		    }
		}
    });
	$("#grid2").navButtonAdd('#grid2_toppager', {
		title: 'Копировать', buttonicon: "ui-icon-pencil", caption: 'Копировать', position: "last",
		onClickButton: function () {
			var id = $("#grid2").jqGrid('getGridParam', 'selrow');
			var node = $("#grid2").jqGrid('getRowData', id);
		    if (id != null) {
				task_create(true);
		    } else {
				$("#dialog>#text").html('Сначала выберите запись в таблице!');
				$("#dialog").dialog("open");
		    }
		}
    });
	$("#grid2").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid2"); }});
	$("#pg_pgrid2").remove();
	$("#pgrid2").removeClass('ui-jqgrid-pager');
	$("#pgrid2").addClass('ui-jqgrid-pager-empty');
	//клавиатура
	$("#grid2").jqGrid('bindKeys', {"onEnter": function (rowid) {}});
	$("#grid2").gridResize();

//select2 tab_project
	$.post('../Engine/select2?action=unit&type=0', function (json) {
		$("#select_unitID").select2({placeholder: "Выберите подразделение", data: {results: json, text: 'text'}});
		$("#select_unitID").select2("val", <?php echo $unitID;?>);
    });
	$.post('../Engine/select2?action=user', function (json) {
		$("#select_userID_resp").select2({placeholder: "Выберите сотрудника", data: {results: json, text: 'text'}});
		$("#select_userID_resp").select2("val", <?php echo $userID_resp;?>);
    });
	var a_status = [{id:0,text:'можно начинать'},{id:20,text:'постановка задач'},{id:30,text:'в работе'},{id:50,text:'пауза'},{id:60,text:'на проверке'},{id:100,text:'завершено'}];
	$("#select_status").select2({data: a_status, placeholder: "Выберите статус"});
	$("#select_status").select2("val", <?php echo $status; ?>);
//tab_task
	$.post('../Engine/select2?action=task_project&type=<?php echo $projectid; ?>', function (json) {
		$("#select_parentID").select2({placeholder: "Выберите зависимость", data: {results: json, text: 'text'}});
		$("#select_parentID").select2("val", <?php echo $t_parentid;?>);
    });
	$.post('../Engine/select2?action=unit&type=1', function (json) {
		$("#select_taskUnitID").select2({placeholder: "Выберите подразделение", data: {results: json, text: 'text'}});
		$("#select_taskUnitID").select2("val", <?php echo $t_unitID; ?>);
    });
	var a_status = [{id:0,text:'можно начинать'},{id:20,text:'ожидаем зависимость'},{id:30,text:'в работе'},{id:50,text:'пауза'},{id:60,text:'на проверке'},{id:100,text:'завершено'}];
	$("#select_taskStatus").select2({data: a_status, placeholder: "Выберите статус"});
	$("#select_taskStatus").select2("val", <?php echo $t_status; ?>);
	$.post('../Engine/select2?action=user', function (json) {
		$("#select_task_userID_resp").select2({placeholder: "Выберите сотрудника", data: {results: json, text: 'text'}});
		$("#select_task_userID_resp").select2("val", <?php echo $t_userID_resp; ?>);
    });

//datepicker
	$("#DT_plan").datepicker({numberOfMonths: 2, dateFormat: 'dd/mm/yy',	showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#DT_fact").datepicker({numberOfMonths: 2, dateFormat: 'dd/mm/yy',	showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#taskDT_plan").datepicker({numberOfMonths: 2, dateFormat: 'dd/mm/yy',showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});
	$("#taskDT_fact").datepicker({numberOfMonths: 2, dateFormat: 'dd/mm/yy',showButtonPanel: true, closeText: "Закрыть", showAnim: "fold"});

	$("#div_content a").click(function() {
		if($(this).attr("type")!='button') return;
		var operid = "";
		var command = this.parentNode.previousSibling.previousSibling;
		if(command.tagName=='SPAN') command = command.previousSibling.previousSibling;
		if(command.tagName=="INPUT") operid = command.id;
		if($(this).html()=='X') $("#"+operid).val("");
		if($(this).html()=='...') $("#"+operid).datepicker("show");
	});
	
	//список проектов для выезжающей вкладки
	fsL = 0;
	// Creating gridL
	$("#gridL").jqGrid({
		sortable: true,
		url: "../engine/jqgrid3?action=project_list&f1=ProjectID&f2=Name&pr.Status<>100",
		datatype: "json",
		height: '500',
		colNames: ['№', 'Название'],
		colModel: [
		    {name: 'pr_ProjectID', index: 'pr.ProjectID', width: 50, align: "center", sorttype: "text", search: true},
		    {name: 'pr_Name', index: 'pr.Name', width: 120, align: "left", sorttype: "text", search: true},
		],
		gridComplete: function () {if (!fsL) {fsL = 1;filter_restore("#gridL");}},
		onSelectRow: function (rowid, status, e) {
			project_open(rowid);
		},
		width: '190',
		shrinkToFit: true,
		rowNum: 999999999,
		sortname: "pr.ProjectID",
		sortorder: "desc",
		editurl: '../project/operation',
		pager: '#pgridL'
	});
	$("#gridL").jqGrid('navGrid', '#pgridL', {edit: false, add: false, del: false, search: false, refresh: false, cloneToTop: false});
	$("#gridL").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#gridL");}});
	$("#pgridL").remove();
	$("#rs_mgridL").remove();
	$("#gbox_gridL").removeClass('ui-corner-all');
	$("#gview_gridL .ui-jqgrid-titlebar").remove();
	
//$("#a_tab_task").tab('show');
//task_create();
});
function project_open(id){
	if (id != null) {
		window.location = "../project/info?projectid=" + id;
	} else {
		$("#dialog>#text").html('Сначала выберите запись в таблице!');
		$("#dialog").dialog("open");
	}
}
function task_create(copy){
	if($("#projectid").val()==0) {
		$("#dialog>#text").html('Сначала надо сохранить новый проект!');
		$("#dialog").dialog("open");
		return;
	};
	$("#taskid").val(0);
	$("#taskID_span").html(0);
	$("#taskProjectName_span").html($("#name").val());
	if(!copy){
		$("#select_parentID").select2("val", 0);
		$("#select_taskUnitID").select2("val", 0);
		$("#taskUserName_create_span").html('');
		$("#taskDT_create_span").html('');
		$("#taskDT_plan").val('');
		$("#taskDT_fact").val('');
		$("#taskName").val($("#name").val());
		$("#taskDescription").val($("#description").val());
		$("#select_taskStatus").select2("val", 0);
		$("#select_task_userID_resp").select2("val", 0);
		$("#taskDT_status_span").html('');
	}
	tab_active = 'task';
	$("#a_tab_task").tab('show');
}
function task_open(rowid){
	if($("#projectid").val()==0) {
		$("#dialog>#text").html('Сначала надо сохранить новый проект!');
		$("#dialog").dialog("open");
		return;
	};
	$("#taskid").val(rowid);
	$("#taskID_span").html(rowid);
	$.post('../engine/task_info', {	taskid: $("#taskid").val() },
		function (data) {
			var t = data.success;
			if (data.success == false) {
				$("#dialog>#text").html('Возникла ошибка при получении информации.<br><br>Сообщите разработчику!');
				$("#dialog").dialog("open");
			} else {
				$("#taskProjectName_span").html(t.ProjectName);
				$("#select_parentID").select2("val", t.ParentID);
				$("#select_taskUnitID").select2("val", t.UnitID);
				$("#taskUserName_create_span").html(t.UserName_create);
				$("#taskDT_create_span").html(t.DT_create);
				$("#taskDT_plan").val(t.DT_plan);
				$("#taskDT_fact").val(t.DT_fact);
				$("#taskName").val(t.Name);
				$("#taskDescription").val(t.Description);
				$("#select_taskStatus").select2("val", t.Status);
				$("#select_task_userID_resp").select2("val", t.UserID_resp);
				$("#taskDT_status_span").html(t.DT_status);
			}
			tab_active = 'task';
			$("#a_tab_task").tab('show');
		}, "json"
	);
}
</script>
<input id="projectid" name="projectid" type="hidden" value="<?php echo $row['ProjectID']; ?>">
<input id="taskid" name="taskid" type="hidden" value="<?php echo $rowT['TaskID']; ?>">
<style>
 #feedback { font-size: 12px; }
 .selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
 .selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div id="div_content" class="container center">
	<ul id="myTab" class="nav nav-tabs floatL hidden-print" role="tablist">
		<li><a id="a_tab_project" href="#tab_project" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
		<legend class="h20">Информация о проекте</legend></a></li>
		<li><a id="a_tab_task" href="#tab_task" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
		<legend class="h20">Информация о задаче</legend></a></li>
	</ul>
	<div class="floatL">
		<button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
			<span class="ui-button-text" style='width:120px;height:22px;'>Сохранить данные</span>
		</button>
	</div>
	<div id="tab-content" class="tab-content">
		<div class="tab-pane min250 m0 w100p ui-corner-bottom0 borderBottom0 borderColor frameL border1" id="tab_project">
			<div class='p5 ui-corner-all frameL border0 w400' style='display:table;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Проект:</span>
					<span id="projectid_span" class="input-group-addon form-control TAL"><?php echo $row['ProjectID']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Название:</span>
					<input id="name" name="name" type="text" class="form-control TAL" value="<?php echo $row['Name']; ?>">
					<span class="input-group-addon w32"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Подразд.:</span>
<!--					<div class="w100p" id="select_report_setting" name="select_report_setting"></div>-->
					<div class="w100p" id="select_unitID"></div>
<!--					<input id="unitName" name="unitName" type="text" class="form-control TAL" value="<?php echo $row['UnitName']; ?>">-->
					<span class="input-group-addon w32"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Описание:</span>
					<textarea id="description" name="description" rows="7" type="text" style="height: 150px;" class="form-control TAL p5"><?php echo $row['Description']; ?></textarea>
					<span class="input-group-addon w32"></span>
				</div>
			</div>
			<!--*********************-->
			<div class='p5 ui-corner-all frameL ml10 border0 w400' style='float:left;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Автор:</span>
					<span class="input-group-addon form-control TAL"><?php echo $row['UserName_create']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Создано:</span>
					<span class="input-group-addon form-control TAL"><?php echo $row['DT_create']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Ответствен.:</span>
					<div class="w100p" id="select_userID_resp"></div>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Срок план.:</span>
					<input id="DT_plan" name="DT_plan" type="text" class="form-control TAL" value="<?php echo $row['DT_plan']; ?>">
					<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
					<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Срок фактич.:</span>
					<input id="DT_fact" name="DT_fact" type="text" class="form-control TAL" value="<?php echo $row['DT_fact']; ?>">
					<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
					<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w25p TAL">Статус:</span>
					<div class="w100p" id="select_status"></div>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p h60">
					<span class="input-group-addon w25p"></span>
					<span class="input-group-addon form-control" style="height:60px"></span>
					<span class="input-group-addon w32"></span>
				</div>
			</div>
		</div>

		<div class="tab-pane min250 m0 w100p ui-corner-bottom0 borderBottom0 borderColor frameL border1" id="tab_task">
			<div class='p5 ui-corner-all frameL border0 w400' style='display:table;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Задача:</span>
					<span id="taskID_span" class="input-group-addon form-control TAL"><?php echo $rowT['TaskID']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Назв. проекта:</span>
					<span id="taskProjectName_span" class="input-group-addon form-control TAL"><?php echo $rowT['ProjectName']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Зависимость:</span>
					<div class="w100p" id="select_parentID"></div>
					<span class="input-group-addon w32"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Подразд.:</span>
					<div class="w100p" id="select_taskUnitID"></div>
					<span class="input-group-addon w32"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Автор:</span>
					<span id="taskUserName_create_span" class="input-group-addon form-control TAL"><?php echo $rowT['UserName_create']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Создано:</span>
					<span id="taskDT_create_span" class="input-group-addon form-control TAL"><?php echo $rowT['DT_create']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Срок план.:</span>
					<input id="taskDT_plan" name="taskDT_plan" type="text" class="form-control TAL" value="<?php echo $rowT['DT_plan']; ?>">
					<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
					<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Срок фактич.:</span>
					<input id="taskDT_fact" name="taskDT_fact" type="text" class="form-control TAL" value="<?php echo $rowT['DT_fact']; ?>">
					<span class="input-group-btn"><a class="btn btn-default w100p" type="button">X</a></span>
					<span class="input-group-btn w32"><a class="btn btn-default w100p" type="button">...</a></span>
				</div>
			</div>
			<!--*********************-->
			<div class='p5 ui-corner-all frameL ml10 border0 w400' style='float:left;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Назв. задачи:</span>
					<input id="taskName" name="taskName" type="text" class="form-control TAL" value="<?php echo $rowT['Name']; ?>">
					<span class="input-group-addon w32"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Описание:</span>
					<textarea id="taskDescription" name="taskDescription" rows="6" type="text" style="height: auto;" class="form-control TAL p5"><?php echo $rowT['Description']; ?></textarea>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Ответствен.:</span>
					<div class="w100p" id="select_task_userID_resp"></div>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Статус:</span>
					<div class="w100p" id="select_taskStatus"></div>
					<span class="input-group-addon w32"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Изм. статуса:</span>
					<span id="taskDT_status_span" class="input-group-addon form-control TAL"><?php echo $rowT['DT_status']; ?></span>
					<span class="input-group-addon w32"></span>
				</div>
			</div>
		</div>
		
		<div class="min260 p5 pt0 w100p ui-corner-top0 borderTop0 borderColor frameL border1" id="tab_project">
			<div id='div2' class='frameL'>
				<table id="grid2"></table>
				<div id="pgrid2"></div>
			</div>
		</div>
	</div>
</div>
<div id="lpanel_button" class="border0">
	<div style="padding-left: 10px; padding-top: 10px; padding-bottom: 10px;width: 1ch; text-align: center; word-wrap: break-word;">Список&nbsp;Проектов</div>
	<div id="lpanel" class="border0">
		<h4>Список проектов</h4>
		<div id='div1' class='frameL pl5' >
			<table id="gridL"></table>
			<div id="pgridL"></div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
