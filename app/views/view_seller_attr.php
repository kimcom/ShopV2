<?php
$cnn = new Cnn();
if (isset($_REQUEST['sellerID'])) {
	$row = $cnn->seller_info();
	//if (!$row)	return;
	//Fn::debugToLog('point attr', $row[1].'	'.  $row['Version']);
} else {
	return;
}
$postid = $row['PostID'];
if ($postid == null)
	$postid = 0;
$fired = $row['Fired'];
if ($fired == null)
	$fired = 0;
$clientid = $row['ClientID'];
if ($clientid == null)
	$clientid = 0;
?>
<script type="text/javascript">
    $(document).ready(function () {
	$("#dialog").dialog({
		autoOpen: false, modal: true, width: 400,
		buttons: [{text: "Закрыть", click: function () {
			$(this).dialog("close");
			}}]
	});
	$("#button_save").click(function () {
		//if ($("#sellerID").val() == '') return;
		$.post('../engine/seller_save', {
		sellerID: $("#sellerID").val(),
		name: $("#name").val(),
		post: $("#select_post").select2("data").text,
		postID: $("#select_post").val(),
		fired: $("#select_fired").val(),
		clientID: $("#select_point").val()
		},
		function (data) {
		$("#dialog>#text").html(data.message);
		$("#dialog").dialog("open");
		$("#dialog").dialog({close: function (event, ui) {
			if (data.new_id > 0)
				window.location = "../lists/seller_info?sellerID=" + data.new_id;
			}});
		if (data.new_id > 0) {
			$("#sellerID").val(data.new_id);
			$("#sellerID_span").html(data.new_id);
		}
		}, "json"
			);
	});
	// выбор должности 
	var a_status = [{id: 1, text: 'администратор'}, {id: 2, text: 'старший продавец'}, {id: 3, text: 'продавец'}, {id: 4, text: 'экономист'}, {id: 5, text: 'грумер'}];
	$("#select_post").select2({data: a_status, placeholder: "Выберите должность"});
	$("#select_post").select2("val", <?php echo $postid; ?>);

	// выбор статуса 
	var a_status = [{id: 0, text: 'работает'}, {id: 1, text: 'уволен'}];
	$("#select_fired").select2({data: a_status, placeholder: "Статус"});
	$("#select_fired").select2("val", <?php echo $fired; ?>);

	// выбор магазина
	$.post('../Engine/select2?action=point', function (json) {
		$("#select_point").select2({multiple: false, placeholder: "Выберите магазин", data: {results: json, text: 'text'}});
		$("#select_point").select2("val", <?php echo $clientid; ?>);
	});
    });
</script>
<input id="sellerID" name="sellerID" type="hidden" value="<?php echo $row['SellerID']; ?>">
<style>
	#feedback { font-size: 12px; }
	.selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
	.selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
	<ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
		<li class="active"><a href="#tab_filter" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;"><legend class="h20">Информация о сотруднике</legend></a></li>
	</ul>
	<div class="floatL">
		<button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
			<span class="ui-button-text">Сохранить данные</span>
		</button>
	</div>
	<div class="tab-content">
		<div class="active tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_filter">
			<div class='p5 ui-corner-all frameL border0 w400' style='display1:table;'>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Код:</span>
					<span id="sellerID_span" class="input-group-addon form-control TAL"><?php echo $row['SellerID']; ?></span>
					<span class="input-group-addon w20p"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">ФИО:</span>
					<input id="name" name="name" type="text" class="form-control TAL" value="<?php echo $row['Name']; ?>">
					<span class="input-group-addon w20p"></span>
				</div>               
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Должность:</span>
					<div class="w100p" id="select_post" ></div>
					<span class="input-group-addon w20p"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Статус:</span>
					<div class="w100p" id="select_fired" ></div>
					<span class="input-group-addon w20p"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Магазин:</span>
					<div class="w100p" id="select_point"></div>
					<span class="input-group-addon w20p"></span>
				</div>
				<div class="input-group input-group-sm w100p">
					<span class="input-group-addon w20p TAL">Город:</span>
					<span class="input-group-addon form-control TAL"><?php echo $row['City']; ?></span>
					<span class="input-group-addon w20p"></span>
				</div>
			</div>

			<!--*********************-->
			<div class='p5 ui-corner-all frameL ml10 border0 w400' style='float:left;'>

			</div>
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
