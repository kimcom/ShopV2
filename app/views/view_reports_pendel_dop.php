<?php
foreach ($_REQUEST as $arg => $val)
	${$arg} = $val;
//Fn::paramToLog();
if(isset($partner_period)){
	$url = '../engine/jqgrid3?action=partner_total&cat_partner_id='.$catid;
	if ($catid == '10000')
	$url = '../engine/jqgrid3?action=partner_total&cat_partner_noid=' . $catid;
	$url .= '&partner_period=' . $partner_period . '&grouping=ca.CatID,dl.PartnerID&f1=PartnerID&f2=CatName&f3=PartnerName&f4=Oborot&f5=Sebest&f6=Dohod';
?>
<script type="text/javascript">
function start() {
// Creating gridH
	$("#gridH").jqGrid({
		sortable: true,
		datatype: "json",
		url: '<?php echo $url; ?>',
		width: '100%',
		height: '365',
		colNames: ['Код статьи', 'Категория', 'Статья затрат', 'Оборот', 'Себест.', 'Доход'],
		colModel: [
			{name:'dl_PartnerID',index: 'dl.PartnerID',width:  80, sorttype: "number", align:"center", search: true},
			{name: 'cs_Name',	index: 'cs.Name',	width: 150, sorttype: "text", align:"left", search: true},
			{name: 'p_Name',	index: 'p.Name',	width: 150, sorttype: "text", align:"left", search: true},
			{name: 'Oborot',	index: 'Oborot',	width: 100, sorttype: "number", align:"right", search: true},
			{name: 'Sebest',	index: 'Sebest',	width: 100, sorttype: "number", align:"right", search: true},
			{name: 'Dohod',		index: 'Dohod',		width: 100, sorttype: "number", align:"right", search: true},
		],
		shrinkToFit: true,
		rowNum: 999999999,
		sortname: "cs.Name,p.Name",
		viewrecords: true,
		gridview: true,
		pager: '#pgridH'
	});
	$("#gridH").jqGrid('navGrid', '#pgridH', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#gridH").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
	}
</script>
<?php
}
if(isset($spent_period)){
	$url = '../engine/jqgrid3?action=spent_total&cat_spent_id='.$catid;
	if($catid=='10000')
		$url = '../engine/jqgrid3?action=spent_total&cat_spent_noid='.$catid;
	$url .= '&spent_period='.$spent_period.'&grouping=ca.CatID,dc.SpentID&f1=SpentID&f2=CatName&f3=SpentName&f4=Summa';
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
		colNames: ['Код статьи', 'Категория', 'Статья затрат', 'Сумма'],
		colModel: [
			{name: 'dc_SpentID', index: 'dc.SpentID', width: 80, sorttype: "number", align:"center", search: true},
			{name: 'cs_Name', index: 'cs.Name', width: 200, sorttype: "text", align:"left", search: true},
			{name: 'p_Name', index: 'p.Name', width: 300, sorttype: "text", align:"left", search: true},
			{name: 'dc_Sum', index: 'dc.Sum', width: 100, sorttype: "number", align:"right", search: true},
		],
		shrinkToFit: true,
		rowNum: 999999999,
		sortname: "cs.Name,p.Name",
		viewrecords: true,
		gridview: true,
		pager: '#pgridH'
	});
	$("#gridH").jqGrid('navGrid', '#pgridH', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
	$("#gridH").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});
}
</script>
<?php
}
?>
	<table id="gridH"></table>
	<div id="pgridH"></div>
