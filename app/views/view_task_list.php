<link rel="stylesheet" type="text/css" href="../../css/ui.jqgrid2.css">
<script type="text/javascript">
    $(document).ready(function () {
//************************************//
	$("#dialog").dialog({
	    autoOpen: false, modal: true, width: 400,
	    buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
		    }}]
	});

	fs = 0;
	// Creating grid1
	$("#grid1").jqGrid({
	    sortable: true,
		url:"../engine/jqgrid3?action=task_list&f1=TaskID&f2=Status&f3=UnitName&f4=ProjectName&f5=Name&f6=UserName_resp&f7=DT_create&f8=DT_plan&f9=DT_fact&f10=ProjectID",
	    datatype: "json",
	    height: 'auto',
	    colNames: ['№', 'Статус', 'Подразд.', 'Проект','Описание', 'Ответственный', 'Дата добавления', 'План. запуск', 'Факт. запуск','ddd'],
	    colModel: [
			{name: 'pc_TaskID',		index: 'pc.TaskID',		width:  40, align: "center",sorttype: "text", search: true},
	   		{name: 'pc_Status',		index: 'pc.Status',		width: 120, align: "left",stype:'select', searchoptions: {value: ":любой;-1:не завершенные;0:можно начинать;20:ожидаем зависимость;30:в работе;50:пауза;60:на проверке;100:завершено"}},
			{name: 'un_UnitName', 	index: 'un.UnitName', 	width:  80, align: "left",	sorttype: "text", search: true},
			{name: 'pl_ProjectName',index: 'pl.ProjectName',width: 200, align: "left",  sorttype: "text", search: true},
			{name: 'pc_Name',		index: 'pc.Name',		width: 200, align: "left",	sorttype: "text", search: true},
			{name: 'up_UserName',	index: 'up.UserName',	width: 150, align: "left",	sorttype: "text", search: true},
			{name: 'pc_DT_create',	index: 'pc.DT_create',	width: 130, align: "center",sorttype: "text", search: true},
			{name: 'pc_DT_plan',	index: 'pc.DT_plan',	width: 120, align: "center",sorttype: "text", search: true},
			{name: 'pc_DT_fact',	index: 'pc.DT_fact',	width: 120, align: "center",sorttype: "text", search: true},
			{name: 'pc_ProjectID',	index: 'pc.ProjectID',  hidden:true},
	    ],
	    gridComplete: function () {if (!fs) {fs = 1; filter_restore("#grid1"); }},
	    width: 'auto',
	    shrinkToFit: false,
	    rowNum: 10,
	    rowList: [10, 20, 30, 40, 50, 100],
	    sortname: "TaskID",
	    sortorder: "desc",
	    viewrecords: true,
	    gridview: true,
	    toppager: true,
	    caption: "Список заданий",
		editurl: '../task/operation',
	    pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Добавить задание', buttonicon: "ui-icon-pencil", caption: 'Добавить задачу', position: "last",
		onClickButton: function () {
			window.location = "../project/info?projectid=0";
		}
    });
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть информ. карту', buttonicon: "ui-icon-pencil", caption: 'Открыть информ. карту', position: "last",
		onClickButton: function () {
			var id = $("#grid1").jqGrid('getGridParam', 'selrow');
			var node = $("#grid1").jqGrid('getRowData', id);
		    if (id != '')
				window.location = "../project/info?projectid="+node.pc_ProjectID+"&taskid=" + id;
		}
	});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter": function (rowid) {
		var id = $("#grid1").jqGrid('getGridParam', 'selrow');
		var node = $("#grid1").jqGrid('getRowData', id);
		if (rowid != '')
			window.location = "../project/info?projectid="+node.pc_ProjectID+"&taskid=" + rowid;
    }});
	$("#grid1").gridResize();
});
</script>
<div class="container min570">
	<div id='div1' class='frameL pt5'>
		<table id="grid1"></table>
		<div id="pgrid1"></div>
	</div>
</div>
<div id='test' class='border1 frameL mt10 text-left'></div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
