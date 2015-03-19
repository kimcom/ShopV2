<?php
class Route {
	static function start() {
//Fn::debugToLog("REQUEST_URI", urldecode($_SERVER['REQUEST_URI']));
		if (1 == 0) {
			ob_start();
			var_dump($_REQUEST);
			//var_dump($_SERVER);
			//var_dump($_POST);
			Fn::DebugToLog("test param\n" . $_SERVER['SCRIPT_FILENAME'] . "\n".$_SERVER['REQUEST_URI']."\n", ob_get_clean());
			ob_end_clean();
		}
		$pos = strpos($_SERVER['REQUEST_URI'], 'favicon.ico');
		if ($pos !== FALSE) Route::toFavicon ();
		
//Fn::debugToLog("route REQUEST_URI ", urldecode($_SERVER['REQUEST_URI']));
//Fn::debugToLog("route QUERY_STRING", urldecode($_SERVER['QUERY_STRING']));
		// контроллер и действие по умолчанию
		$controller_name = 'main';
		$action_name = 'index';
		// получаем имя контроллера
		$routes = explode('/', $_SERVER['REQUEST_URI']);
		if (!empty($routes[1])) {
			$controller_name = $routes[1];
			$pos = strpos($controller_name, '?');
			if ($pos !== FALSE)
				$controller_name = substr($controller_name, 0, $pos);
		}
		// получаем имя экшена
		if (!empty($routes[2])) {
			$action_name = $routes[2];
			$pos = strpos($action_name, '?');
			if ($pos !== FALSE)
				$action_name = substr($action_name, 0, $pos);
		}
		//проверяем есть ли доступ к системе
//Fn::debugToLog("test", 'access='.$_SESSION['access'].' $controller_name='.$controller_name);
		if ($_SESSION['access'] == '' && 
			$controller_name != 'logon' && 
			$controller_name != 'login' &&
			$controller_name != 'register' &&
			$controller_name != 'register_ok'
			) {
			Fn::redirectToController('logon');
			return;
		}
//		if ($controller_name == 'css') return;
		if ($controller_name == 'favicon.ico') return;
		if (!empty($routes[2]))
			if ($routes[2] == 'favicon.ico') return;
		if ($controller_name == 'images') return;
		if ($action_name == 'favicon.ico') return;
		if ($action_name == 'captcha') return;
		// добавляем префиксы
		$model_name = 'Model_' . $controller_name;
		$controller_name = 'Controller_' . $controller_name;
		$action_name = 'action_' . $action_name;

//Fn::debugToLog("route", "/".$controller_name."/".$model_name."/".$action_name);
		// подцепляем файл с классом модели (файла модели может и не быть)
		$model_file = strtolower($model_name) . '.php';
		$model_path = "app/models/" . $model_file;
		if (file_exists($model_path)) {
			include "app/models/" . $model_file;
		}

		// подцепляем файл с классом контроллера
		$controller_file = strtolower($controller_name) . '.php';
		$controller_path = "app/controllers/" . $controller_file;
//echo $controller_path . " " . $controller_name . " " . $action_name . ' access=' . $_SESSION['access'];
//return;
		if (file_exists($controller_path)) {
			include "app/controllers/" . $controller_file;
		} else {
			Fn::debugToLog('route controller отсутствует:', $controller_name);
			/*
			  правильно было бы кинуть здесь исключение,
			  но для упрощения сразу сделаем редирект на страницу 404
			 */
			Route::ErrorPage404();
			return;
		}

		// создаем контроллер
		$controller = new $controller_name;
		$action = $action_name;

		if (method_exists($controller, $action)) {
			// вызываем действие контроллера
			$controller->$action();
		} else {
			//Fn::debugToLog('route controller:', $controller_name);
			Fn::debugToLog('route action отсутствует:', $controller_name.'/'.$action);
			// здесь также разумнее было бы кинуть исключение
			Route::ErrorPage404();
			return;
		}
	}
	static function ErrorPage404() {
		Fn::redirectToController('404');
//		$host = 'http://' . $_SERVER['HTTP_HOST'] . '/';
//		header('HTTP/1.1 404 Not Found');
//		header("Status: 404 Not Found");
//		header('Location:' . $host . '404');
	}
	static function toFavicon() {
		Fn::redirectToController('favicon.ico');
	}
}
?>
