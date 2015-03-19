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
		url:"../engine/jqgrid3?action=project_list&f1=ProjectID&f2=Status&f3=UnitName&f4=Name&f5=UserName&f6=DT_create&f7=DT_plan&f8=DT_fact",
	    datatype: "json",
	    height: 'auto',
	    colNames: ['№ проэкта', 'Статус', 'Подразделение', 'Описание', 'Ответственный', 'Дата добавления', 'План. запуск', 'Факт. запуск'],
	    colModel: [
			{name: 'pr_ProjectID',	index: 'pr.ProjectID',	width:  80, align: "center",sorttype: "text", search: true},
			{name: 'pr_Status',		index: 'pr.Status',		width:  80, align: "center",sorttype: "text", search: true},
			{name: 'un_UnitName', 	index: 'un.UnitName', 	width: 120, align: "left",	sorttype: "text", search: true},
			{name: 'pr_Name',		index: 'pr.Name',		width: 350, align: "left",	sorttype: "text", search: true},
			{name: 'u_UserName',	index: 'u.UserName',	width: 150, align: "left",	sorttype: "text", search: true},
			{name: 'pr_DT_create',	index: 'pr.DT_create',	width: 130, align: "center",sorttype: "text", search: true},
			{name: 'pr_DT_plan',	index: 'pr.DT_plan',	width: 120, align: "center",sorttype: "text", search: true},
			{name: 'pr_DT_fact',	index: 'pr.DT_fact',	width: 120, align: "center",sorttype: "text", search: true},
	    ],
	    gridComplete: function () {if (!fs) {fs = 1; filter_restore("#grid1"); }},
	    width: 'auto',
	    shrinkToFit: false,
	    rowNum: 10,
	    rowList: [10, 20, 30, 40, 50, 100],
	    sortname: "pr.ProjectID",
	    sortorder: "desc",
	    viewrecords: true,
	    gridview: true,
	    toppager: true,
	    caption: "Список проектов",
		editurl: '../project/operation',
	    pager: '#pgrid1'
	});
	$("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Добавить проект', buttonicon: "ui-icon-pencil", caption: 'Добавить проект ', position: "last",
		onClickButton: function () {
			window.location = "../project/info?projectid=0";
		}
	    });
	$("#grid1").navButtonAdd('#grid1_toppager', {
		title: 'Открыть информ. карту', buttonicon: "ui-icon-pencil", caption: 'Открыть информ. карту', position: "last",
		onClickButton: function () {
			var id = $("#grid1").jqGrid('getGridParam', 'selrow');
			var node = $("#grid1").jqGrid('getRowData', id);
		    if (id != null) {
				window.location = "../project/info?projectid=" + id;
			} else {
				$("#dialog>#text").html('Сначала выберите запись в таблице!');
			    $("#dialog").dialog("open");
			}
		}
	});
	$("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {filter_save("#grid1");}});

	$("#pg_pgrid1").remove();
	$("#pgrid1").removeClass('ui-jqgrid-pager');
	$("#pgrid1").addClass('ui-jqgrid-pager-empty');

	//клавиатура
	$("#grid1").jqGrid('bindKeys', {"onEnter": function (rowid) {
		alert("You enter a row with id:" + rowid)
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
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
