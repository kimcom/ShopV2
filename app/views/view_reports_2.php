<style>
	tr.group,td.group,tr.group:hover,
	tr.group:hover {
		background-color: #EFF !important;
	}
	tr.group2,td.group2,tr.group2:hover,
	tr.group2:hover {
		background-color: #FEF !important;
	}
</style>
<script type="text/javascript">
var table;
var numberVal = function ( i ) {
	return typeof i === 'string' ?
		i.replace(/[\$,]/g, '')*1 :
		typeof i === 'number' ?
	i : 0;
};
function sum(column, api, digit){
	if(digit == null) digit = 0;
	data = api.column(column).data();
	total = data.length ? data.reduce(function (a, b) { return numberVal(a) + numberVal(b);}) : 0;
	$(api.column(column).footer()).html(total.toFixed(digit));
}
$(document).ready(function () {
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 600, height: 300,
		buttons: [{text: "Закрыть", click: function () {
			    $(this).dialog("close");
			}}]
	    });
	dt = new Date();
//	dt.setMonth(dt.getMonth() - 1, 1);
	dt.setDate(0);
	//alert(dt.toLocaleDateString()+' '+dt.toLocaleTimeString());
	$("#DT_start").datepicker({
		showOn: "both", 
		numberOfMonths: 3,
		showButtonPanel: true, 
		dateFormat: 'dd/mm/yy'
	});
	$("#DT_start").datepicker("setDate", dt);
	dt = new Date();
	dt.setDate(0);
    $("#DT_stop").datepicker({
		showOn: "both", 
		numberOfMonths: 3,
		showButtonPanel: true,
		dateFormat: 'dd/mm/yy'
	});
	$("#DT_stop").datepicker("setDate", dt);
	$(".ui-datepicker-trigger").addClass("hidden-print");
	
	$('#example2')
		.removeClass('display')
		.addClass('table table-striped table-bordered');
	cntVisCol = 9;
	table = $('#example2').DataTable({
		columnDefs: [
			{ title: "Магазин",				width: 100, visible: false, className: "TAL", targets: [ 0 ] },
			{ title: "Продавец",			width: 100, visible: false, className: "TAL", targets: [ 1 ] },
			{ title: "Артикул",				width: 50, className: "TAL", targets: [ 2 ] },
			{ title: "Товар",				width: 150,className: "TAL", targets: [ 3 ] },
			{ title: "Кол-во",				width: 50, className: "TAR", targets: [ 4 ] },
			{ title: "Себест.",				width: 60, className: "TAR", targets: [ 5 ] },
			{ title: "Оборот",				width: 60, className: "TAR", targets: [ 6 ] },
			{ title: "Доход",				width: 50, className: "TAR", targets: [ 7 ] },
			{ title: "% наценки",			width: 50, className: "TAR", targets: [ 8 ] },
	    ],
		searching: false,
		processing: true,
		paging: false,
		info: false,
		autoWidth: false,
		ordering: false,
		stateSave: true,
		//jQueryUI:true,
		//displayLength: 05,
		//order: [[0, 'asc'], [1, 'asc'], [3, 'asc']],
		language: {
			url: "../../css/dataTables.russian.lang"
		},
//		footerCallback: function ( row, data, start, end, display ) {
//            var api = this.api();
//			sum(4,api);
//			sum(5,api,2);
//			sum(6,api,2);
//			
//			sum(7,api,2);
//		},
		drawCallback: function (settings) {
			var api = this.api();
			var rows = api.rows().nodes();
			var last = null;
			api.column(0).data().each(
				function (group, i) {
					//console.log(group);
					if (last !== group) {
						$(rows).eq(i).before('<tr class="group"><td class="group TAL fontb" colspan='+cntVisCol+'>'+group+'</td></tr>');
					    last = group;
					}
				    });
			var last = null;
			var current = 0;
			var sumCol3 = 0, sumCol4 = 0, sumCol5 = 0, sumCol6 = 0;
			api.column(1).data().each(
				function (group, i) {
					if (last !== group) {
						if(current!=0){
							$(rows).eq(current).before(
								'<tr class="group2">\n\
									<td class="group2 fontb"></td>\n\
									<td class="group2 TAL fontb" colspan=1>'+last+'</td>\n\
									<td class="group2 TAR fontb">'+sumCol3+'</td>\n\
									<td class="group2 TAR fontb">'+sumCol4.toFixed(2)+'</td>\n\
									<td class="group2 TAR fontb">'+sumCol5.toFixed(2)+'</td>\n\
									<td class="group2 TAR fontb">'+sumCol6.toFixed(2)+'</td>\n\
									<td class="group2" colspan='+(cntVisCol-8)+'></td>\n\
								</tr>');
						}else if(last!=null){
							$(rows).eq(0).before(
								'<tr class="group2">\n\
									<td class="group2 fontb"></td>\n\
									<td class="group2 TAL fontb" colspan=1>'+last+'</td>\n\
									<td class="group2 TAR fontb">' + sumCol3 + '</td>\n\
									<td class="group2 TAR fontb">' + sumCol4.toFixed(2) + '</td>\n\
									<td class="group2 TAR fontb">' + sumCol5.toFixed(2) + '</td>\n\
									<td class="group2 TAR fontb">' + sumCol6.toFixed(2) + '</td>\n\
									<td class="group2" colspan=' + (cntVisCol - 8) + '></td>\n\
								</tr>');
						}
						current = i;
						last = group;
						sumCol3  = numberVal(api.cell(i,4).data());
						sumCol4  = numberVal(api.cell(i,5).data());
						sumCol5  = numberVal(api.cell(i,6).data());
						sumCol6  = numberVal(api.cell(i,7).data());
					}else{
						sumCol3 += numberVal(api.cell(i,4).data());
						sumCol4 += numberVal(api.cell(i,5).data());
						sumCol5 += numberVal(api.cell(i,6).data());
						sumCol6 += numberVal(api.cell(i,7).data());
					}
				});
				
			sum(4,api);
			sum(5,api,2);
			sum(6,api,2);
			sum(7,api,2);
		}
	});
	
	
//table.row.add(['1','','','','','','','','']).draw();
//	$('#example2').on( 'processing.dt', function ( e, settings, processing ) {
//		example2_processing
//		$('#processingIndicator').css( 'display', processing ? 'block' : 'none' );
//		$("#testtext").html('test=' + $('#processingIndicator').css('display'));
//		//$('#processingIndicator').css( 'top', processing ? '200px' : '100px' );
//		$('#processingIndicator').css( 'color', processing ? '#FFA' : '#FFD' );
//    });
//	Order by the grouping
//	 $('#example2 tbody').on('click', 'tr.group', function () {
//	     var currentOrder = table.order()[0];
//	     if (currentOrder[0] === 2 && currentOrder[1] === 'asc') {
//		 table.order([2, 'desc']).draw();
//	     }
//	     else {
//		 table.order([2, 'asc']).draw();
//	     }
//	 });
	 
	$('#example2').on('preXhr.dt', function (e, settings, data) {
		$('#timestamp').html('');
		table.clear().draw();
	});
	$('#example2').on('xhr.dt', function (e, settings, json) {
		dt = new Date();
		$('#timestamp').html('Отчет сформирован: ' + dt.toLocaleDateString() + ' ' + dt.toLocaleTimeString());
	});
	$("#button_submit").click(function() {
		table.ajax.url('../reports/report2_data?DT_start=' + $("#DT_start").val() + '&DT_stop=' + $("#DT_stop").val()).load();
	});
});
</script>
<div class="container center">
	<legend>Отчет "Продажи товаров в рознице".</legend>
	<p></p>
	<label class='w70' for='DT_start'>Период с:</label>
	<input class='w80' type="text" id="DT_start" name="DT_start">
	<label class='w20' for='DT_stop'>по:</label>
	<input class='w80' type="text" id="DT_stop" name="DT_stop">
	<button id="button_submit" class="btn btn-xs btn-success hidden-print" class1="hidden-print ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only ui-state-focus">
		<span class="ui-button-text" style1='width:120px;height:22px;'>Сформировать</span>
	</button>
	<p></p>
	<table id="example2" class="display">
		<tfoot>
            <tr>
                <th colspan="4" style="text-align:right">Итого:</th>
                <th class="TAR"></th>
                <th></th>
                <th></th>
                <th></th>
                <th></th>
            </tr>
        </tfoot>
	</table>
	<p id="timestamp" class="small"></p>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
