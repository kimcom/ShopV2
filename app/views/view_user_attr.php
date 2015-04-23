<?php
$cnn = new Cnn();
if (isset($_REQUEST['userID'])) {
	$row = $cnn->user_info();
	$clientid = $row['ClientID'];
	if ($clientid == null) $clientid = 0;
	$userid = $_REQUEST['userID'];
	if($userid != $_SESSION['UserID']) return;
}
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
		//if ($("userID").val() == '') return;
		$.post('../engine/user_save', {
		userid: $("#userID").html(),
		clientid: $("#select_point").val(),
		login: $("#login").val(),
		password: $("#password").val(),
		eMail: $("#eMail").val(),
		userName: $("#userName").val(),
		userPhone: $("#userPhone").val(),
		city: $("#city").val(),
		accessLevel: $("#accessLevel").val(),
		position: $("#position").val(),
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
		    }, "json");
		});
	
	$.post('../Engine/select2?action=point', function (json) {
		$("#select_point").select2({multiple: false, placeholder: "Выберите магазин", data: {results: json, text: 'text'}});
		$("#select_point").select2("val", <?php echo $clientid ?>);
	});
});
</script>
<style>
	#feedback { font-size: 12px; }
	.selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
	.selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
	<ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
		<li class="active"><a href="#tab_filter" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;"><legend class="h20">Информация о пользователе</legend></a></li>
	</ul>
	<div class="floatL">
		<button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
			<span class="ui-button-text" style='width:120px;height:22px;'>Сохранить данные</span>
		</button>
	</div>
<div class="tab-content">
	<div class="active tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_filter">
		<div class='p5 ui-corner-all frameL border0 w400' style='display1:table;'>

			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">ID пользователя:</span>
				<span id="userID" name="userID" type="text" class="input-group-addon form-control TAL"><?php echo $row['UserID']; ?></span>
				<span class="input-group-addon w20p"></span>
			</div>

			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">Логин:</span>
				<input id="login" name="login" type="text" class="form-control TAL" value="<?php echo $row['Login']; ?>">
				<span class="input-group-addon w20p"></span>
			</div>               

			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">ФИО</span>
				<input id="userName" name="userName" type="text" class="form-control TAL" value="<?php echo $row['UserName']; ?>">
				<span class="input-group-addon w20p"></span>
			</div>

			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">E-mail:</span>
				<input id="eMail" name="eMail" type="text" class="form-control TAL" value="<?php echo $row['EMail']; ?>">
				<span class="input-group-addon w20p"></span>
			</div>


			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">Должность:</span>
				<input id="position" name="position" type="text" class="form-control TAL" value="<?php echo $row['Position']; ?>">
				<span class="input-group-addon w20p"></span>
			</div>


			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">Телефон:</span>
				<input id="userPhone" name="userPhone" type="text" class="form-control TAL" value ="<?php echo $row['UserPhone']; ?>">
				<span class="input-group-addon w20p"></span>
			</div>

			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">Подразделение:</span>
				<div class="w100p" id="select_point"></div>
				<span class="input-group-addon w20p"></span>
			</div>
			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">Город: </span>
				<input id="city" name="city" type="text" class="form-control TAL" value="<?php echo $row['City']; ?>" disabled>
				<span class="input-group-addon w20p"></span>
			</div>

			<div class="input-group input-group-sm w100p">
				<span class="input-group-addon w25p TAL">Уровень доступа:</span>
				<input id="accessLevel" name="accessLevel" type="text" class="form-control TAL" value="<?php echo $row['AccessLevel']; ?>">
				<span class="input-group-addon w20p"></span>
			</div>
		</div>
			<!--*********************-->
			<!--
			<div class='p5 ui-corner-all frameL ml10 border0 w400' style='float:left;'>
			
			</div>
			-->
		</div>
	</div>
</div>
<div id="dialog" title="ВНИМАНИЕ!">
	<p id='text'></p>
</div>
