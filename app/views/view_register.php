<div class="container min550">
	<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons" 
		 style="position: relative; height: auto; width: 510px; left:50%; top:0%; margin-left:-250px; margin-top:10px; display: block;"
		 tabindex="-1">
		<form class="form-signin p10" action="../register/check" metod="post" role="form">
			<h3 class="form-signin-heading center">Регистрация нового пользователя<br><small>в информационной системе компании <?php echo $_SESSION['company']; ?></small></h3>
			<div class="input-group w100p">
				<?php echo $_SESSION['error_msg']; unset($_SESSION['error_msg']); ?>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p">Пользователь:</span>
				<input name="login" type="text" class="form-control w50p" placeholder="Имя" required autofocus value="<?php echo $_REQUEST['login']; ?>">
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p">E-mail:</span>
				<input name="email" type="email" class="form-control w50p" placeholder="E-mail адрес" required value="<?php echo $_REQUEST['email'];?>">
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p">Пароль:</span>
				<input name="pass" type="password" class="form-control w50p" placeholder="Пароль" required>
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p p5">Снова пароль:</span>
				<input name="repass" type="password" class="form-control w50p" placeholder="Повторите пароль" required>
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p m0">
<?php
	echo $_SESSION['error_msg1'];unset($_SESSION['error_msg1']);
?>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p p5">Имя и фамилия:</span>
				<input name="fio" type="text" class="form-control w50p" placeholder="Введите Ваше Ф.И.О." required value="<?php echo $_REQUEST['fio']; ?>">
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p p5">Должность:</span>
				<input name="post" type="text" class="form-control w50p" placeholder="Ваша должность" required value="<?php echo $_REQUEST['post']; ?>">
				<span class="input-group-addon w25p"></span>
			</div>
<!--			<div class="input-group w100p">
				<span class="input-group-addon w25p p5">Ваша фирма:</span>
				<input name="company" type="text" class="form-control w50p" placeholder="Введите название Вашей фирмы" required value="<?php echo $_REQUEST['company']; ?>">
				<span class="input-group-addon w25p"></span>
			</div>-->
			<div class="input-group w100p">
				<span class="input-group-addon w25p p5">Ваш телефон:</span>
				<input name="phone" type="text" class="form-control w50p" placeholder="Введите телефон" required value="<?php echo $_REQUEST['phone']; ?>">
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p p5">Код:</span>
				<input name="captcha" type="text" class="form-control w50p" placeholder="Введите проверочный код" required>
				<span class="input-group-addon w25p"></span>
			</div>
<?php
if (!empty($_REQUEST['captcha'])) {
	if (empty($_SESSION['captcha']) || trim(strtolower($_REQUEST['captcha'])) != $_SESSION['captcha']) {
//		$captcha_message = "Вы ввели неправильный проверочный код!";
		$style = "background-color: #FF0000";
//echo <<<HTML
//        <div id="result" style="$style">
//        <h2>$captcha_message</h2>
//        </div>
//HTML;
	}/* else {
		$captcha_message = "Все в порядке!";
		$style = "background-color: #CCFF99";
	}*/
	unset($_SESSION['captcha']);
}
?>
			<div class="input-group w100p">
				<span class="input-group-addon w25p p5 h60">Проверочный<br>код:</span>
				<img class="form-control w50p h60" style="<?php echo $style;?>" src="../captcha/captcha.php" id="captcha"><br>
				<span class="input-group-btn w25p">
					<a class="btn btn-default w100p h91" type="button"
					   onclick="
							document.getElementById('captcha').src='../captcha/captcha.php?'+Math.random();
							document.getElementById('captcha-form').focus();"><br>Обновить код</a>
				</span>
			</div>
			<div class="input-group w100p m0">
<?php
echo $_SESSION['error_msg2'];unset($_SESSION['error_msg2']);
?>
			</div>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Регистрация</button>
		</form>
	</div>
</div>
