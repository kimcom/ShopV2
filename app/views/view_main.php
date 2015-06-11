<script type="text/javascript">
var tab_active = '';
$(document).ready(function () {
	$('#myCarousel').carousel({interval: 5000,})
});
</script>
<div class="container-fluid p0 min570">
    <header id="myCarousel" class="carousel slide h570 mt-9">
		<!-- Индикаторы на каруселе -->
		<div class="bgig">
			<ol class="carousel-indicators">
            <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
            <li data-target="#myCarousel" data-slide-to="1"></li>
            <li data-target="#myCarousel" data-slide-to="2"></li>
            <li data-target="#myCarousel" data-slide-to="3"></li>
            <li data-target="#myCarousel" data-slide-to="4"></li>
            <li data-target="#myCarousel" data-slide-to="5"></li>
            <li data-target="#myCarousel" data-slide-to="6"></li>
        </ol>
		</div>
        <!-- Карусель -->
        <div class="carousel-inner">
            <div class="item active h550">
                <div class="fill center"><img src="../../users/upload/discount history.png" class=""></div>
                <div class="carousel-caption">
                    <h3 class="TAR">Дисконт.карты "История покупок"</h3>
                    <h4 class="TAR">На сайте появилась возможность просмотра истории покупок<br>для выбранной дисконтной карты.</h4>
                </div>
            </div>
            <div class="item h550">
                <div class="fill center"><img src="../../users/upload/p&l.png" class=""></div>
                <div class="carousel-caption">
                    <h3 class="TAR">Отчет "Profit and loss"</h3>
                    <h4 class="TAR">Доступен новый отчет о прибылях и убытках</h4>
                </div>
            </div>
            <div class="item h550">
                <div class="fill center"><img src="../../users/upload/sale opt.png" class=""></div>
                <div class="carousel-caption">
                    <h3 class="TAR">Отчет "Продажи товаров в опте"</h3>
                    <h4 class="TAR">Создан новый отчет, отражающий информацию<br>о продажах оптового отдела компании</h4>
                </div>
            </div>
            <div class="item h550">
                <div class="fill center"><img src="../../users/upload/discount_history.png" class=""></div>
                <div class="carousel-caption">
                    <h3 class="TAR">Корректировка истории диск.карты</h3>
                    <h4 class="TAR">Реализована функция добавления и удаления чеков<br>из истории продаж для конкретной 
						дисконтной карты</h4>
                </div>
            </div>
            <div class="item h550">
                <div class="fill center"><img src="../../users/upload/user_list.png" class=""></div>
                <div class="carousel-caption">
                    <h3 class="TAR">Список пользователей</h3>
                    <h4 class="TAR">Сделан список пользователей САУ	с возможностью сортировки и фильтрации.<br>
						Также реализована возможность изменения информации о пользователе.</h4>
                </div>
            </div>
            <div class="item h550">
                <div class="fill center"><img src="../../users/upload/check_list.png" class=""></div>
                <div class="carousel-caption">
                    <h3 class="TAR">Список чеков</h3>
                    <h4 class="TAR">Сделан список чеков с отражением содержимого чека.<br>
						Есть возможность фильтрации и сортировки.</h4>
                </div>
            </div>
        </div>

        <!-- Стрелки на каруселе -->
        <a class="left carousel-control" href="#myCarousel" data-slide="prev">
            <span class="icon-prev"></span>
        </a>
        <a class="right carousel-control" href="#myCarousel" data-slide="next">
            <span class="icon-next"></span>
        </a>
    </header>
</div>
