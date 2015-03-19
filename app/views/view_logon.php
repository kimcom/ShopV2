<div class="container min550">
	<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-dialog-buttons" 
		 style="position: relative; height: auto; width: 510px; left:50%; top:0%; margin-left:-250px; margin-top:100px; display: block;"
		 tabindex="-1">
		<form class="form-signin p10" action="login/index/" metod="post" role="form">
			<a class="btn btn-default btn-sm btn-block disabled">
				<h4 class="form-signin-heading center">Cистема анализа статистики<br>и управления продажами компании<br><?php echo $_SESSION['company']; ?></h4>
			</a>
			<div class="input-group w100p">
			<?php echo $_SESSION['error_msg']; unset($_SESSION['error_msg']); ?>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p">Пользователь:</span>
				<input name="login" type="text" class="form-control w50p" placeholder="Ваше имя" required autofocus>
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<span class="input-group-addon w25p">Пароль:</span>
				<input name="pass" type="password" class="form-control w50p" placeholder="Пароль" required>
				<span class="input-group-addon w25p"></span>
			</div>
			<div class="input-group w100p">
				<button class="btn btn-default btn-sm btn-block" type="button" onclick="window.location='../../recovery';">Я непомню пароль. Что делать?</button>
			</div>
			<div class="input-group w100p">
				<button class="btn btn-default btn-sm btn-block" type="button" onclick="window.location='../../register';">Зарегистрироваться в системе</button>
			</div>
			<button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
		</form>
	</div>
</div> <!-- /container -->
