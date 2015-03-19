<div class="container min550">
	<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons" 
		 style="position: relative; height: auto; width: 500px; left:50%; top:0%; margin-left:-250px; margin-top:100px; display: block;"
		 tabindex="-1">
		<h3 class="form-signin-heading center">Восстановление пароля<br><small> для входа в информационную систему компании <?php echo $_SESSION['company']; ?></small></h3>
		<div class="input-group w100p">
			<?php echo $_SESSION['error_msg'];
			unset($_SESSION['error_msg']); ?>
		</div>
<?php
if (isset($data) && $data) {
?>
			<div class = "input-group w100p">
				<h4 class = 'center list-group-item list-group-item-info m0'>
				ВНИМАНИЕ!<br><small>На Ваш e-mail отправлено сообщение
				с Вашим паролем!</small></h4>
			</div>
			<button class="btn btn-lg btn-primary btn-block mt10" type="button" onclick="window.location='../logon';">Страница входа</button>
<?php
}else{
?>
		<form class="form-signin p10" action="../recovery/send" metod="post" role="form">
			<div class="input-group w100p">
				<h4 class='center list-group-item list-group-item-info m0'>
					ВНИМАНИЕ!<br><small>Для восстановления пароля
					введите Ваш e-mail<br>и нажмите <НАПОМНИТЬ>!</small></h4>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p">E-mail:</span>
				<input name="email" type="email" class="form-control w50p" placeholder="E-mail адрес" required autofocus value="<?php echo $_REQUEST['email'];?>">
				<span class="input-group-addon w25p"></span>
			</div>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Напомнить</button>
		</form>
<?php
}
?>
	</div>
</div>
