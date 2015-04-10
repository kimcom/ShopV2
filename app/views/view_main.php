<div class="container min570">
	<div class="panel panel-default mb5">
		<div class="panel-heading">
			<h3><?php echo $_SESSION['titlename']; ?></h3>
		</div>
		<div class="panel-body min500">
			<p class="text-info lead">
				Данная система создана для накопления, управления и анализа информации<br>
				о проданных товарах в розничной и оптовой сетях компании <?php echo $_SESSION['company']; ?>
			</p>
			<div class="list-group">
				<a class="list-group-item disabled">
					<h4>Основные возможности системы:</h4>
				</a>
				<div class="list-group">
					<ul class="list-group-item">
						<li class="list-group-item">
							Управление товарным ассортиментом, штрих-кодами, ценами.
						</li>
						<li class="list-group-item">
							Управление категориями, группами и матрицами товарного ассортимента.
						</li>
						<li class="list-group-item">
							Управление списками торговых точек и продавцов.
						</li>
						<li class="list-group-item">
							Управление акциями для оптовой и розничной сети.
						</li>
						<li class="list-group-item">
							Получение аналитических отчетов.
						</li>
					</ul>
				</div>
			</div>
<?php
if ($_SESSION['AccessLevel'] == 0) {
				echo "<h4 class='center list-group-item list-group-item-danger'>"
					. "ВНИМАНИЕ!<br><small>Ваш аккаунт имеет ограниченный доступ!<br>"
					. "Для получения доступа к функциям системы,<br>"
					. "обратитесь к администратору системы: <a href='mailto:".$_SESSION['adminEmail']."'>".  $_SESSION['adminEmail'] ."</a></small></h4>";
}
?>
		</div>
	</div>
</div>