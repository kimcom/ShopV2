<?php
foreach ($_REQUEST as $arg => $val)
	${$arg} = $val;
//Fn::paramToLog();
//Fn::debugToLog('pendel_dop:', urldecode($_SERVER['QUERY_STRING']));
//echo urldecode($_SERVER['QUERY_STRING']).'<br>';
	
$url = '../engine/jqgrid3?action=spent_total&cat_spent_id='.$catid;
if($catid=='10000')
	$url = '../engine/jqgrid3?action=spent_total&cat_spent_noid='.$catid;
$url .= '&spent_period='.$period.'&grouping=ca.CatID,dc.SpentID&f1=SpentID&f2=CatName&f3=SpentName&f4=Summa';
//	echo $url.'<br>';
?>
<script type="text/javascript">
function start() {
// Creating gridH
	$("#gridH").jqGrid({
		sortable: true,
		datatype: "json",
		url: '<?php echo $url;?>',
		width: '100%',
		height: '365',
		//&f1=SpentID&f2=CatName&f3=SpentName&f4=Summa
		colNames: ['Код статьи', 'Категория', 'Статья затрат', 'Сумма'],
		colModel: [
			{name: 'dc_SpentID', index: 'dc.SpentID', width: 80, sorttype: "number", align:"center", search: true},
			{name: 'cs_Name', index: 'cs.Name', width: 200, sorttype: "text", align:"left", search: true},
			{name: 'p_Name', index: 'p.Name', width: 300, sorttype: "text", align:"left", search: true},
			{name: 'dc_Sum', index: 'dc.Sum', width: 100, sorttype: "number", align:"right", search: true},
		],
		shrinkToFit: true,
		rowNum: 999999999,
//		rowNum: 20,
//		rowList: [20, 30, 40, 50, 100, 200, 300],
		sortname: "cs.Name,p.Name",
		viewrecords: true,
		gridview: true,
//		toppager: true,
		pager: '#pgridH'
	});
	$("#gridH").jqGrid('navGrid', '#pgridH', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#gridH").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
//	$("#pg_pgridH").remove();
//	$("#pgridH").removeClass('ui-jqgrid-pager');
//	$("#pgridH").addClass('ui-jqgrid-pager-empty');
}
</script>
	<table id="gridH"></table>
	<div id="pgridH"></div>
