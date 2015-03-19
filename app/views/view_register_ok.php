<div class="container min550">
	<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons" 
		 style="position: relative; height: auto; width: 500px; left:50%; top:0%; margin-left:-250px; margin-top:100px; display: block;"
		 tabindex="-1">
<?php 
if (isset($data) && !$data){
echo '<div class = "input-group w100p">';
echo $_SESSION['error_msg'];unset($_SESSION['error_msg']);
echo '</div>';
}else if (isset($data) && $data) {
?>
		<h3 class="form-signin-heading center">
			Поздравляем!<br><br>
			<small>
				Активация Вашего аккаунта<br>
				в информационной системе компании <?php echo $_SESSION['company']; ?><br>
				успешно завершена!<br><br>
				Теперь Вы можете войти в систему<br><br>
			</small>
		</h3>
		<button class="btn btn-lg btn-primary btn-block mt10" type="button" onclick="window.location='../logon';">Страница входа</button>
<?php
}else{
?>
		<h3 class="form-signin-heading center">
			Поздравляем!<br>
			<small>
				Вы успешно зарегистрированы<br>
				в информационной системе компании <?php echo $_SESSION['company']; ?>
			</small>
		</h3>
		<div class="input-group w100p">
			<h4 class='center list-group-item list-group-item-info m0'>
				<br>ВНИМАНИЕ!<br><br>
				На Ваш e-mail выслано письмо<br><br>
				с кодом активации Вашего аккаунта<br><br>
				<small>Для активации нажмите на ссылку в письме.<br><br>
					После активации Вы сможете войти в нашу систему.<br><br>
				</small>
			</h4>
		</div>
		<button class="btn btn-lg btn-primary btn-block mt10" type="button" onclick="window.location='../logon';">Страница входа</button>
<?php 
}
?>
	</div>
</div>
