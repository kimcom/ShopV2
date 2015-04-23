<?php
class Controller_Engine extends Controller {
	function action_index() {
	}
//получение настроек пользователя
	function action_filter_save() {
//Fn::debugToLog('filter_save', ($_POST['filter']));
		$filename = "Users\\Setting\\".$_SESSION['UserID'].'_'.$_REQUEST['section'].'_'.$_REQUEST['gridid'].".txt";
		$bl = file_put_contents($filename, $_REQUEST['filter']);
		if ($bl==false) Fn::debugToLog ("Engine", 'ошибка при записи фильтра в файл: '.$filename );
	}
	function action_filter_restore() {
		$filename = "Users\\Setting\\".$_SESSION['UserID'].'_'.$_REQUEST['section'].'_'.$_REQUEST['gridid'].".txt";
		$handle = @fopen($filename, "r");
		$response = new stdClass();
		if ($handle != null) {
			$response->success = true;
			$response->message = 'ok';
			$response->data = fread($handle, filesize($filename));
			echo json_encode($response);
		} else {
			//Fn::debugToLog("Engine", 'ошибка при чтении фильтра из файла: ' . $filename);
			$response->success = false;
			$response->message = 'Возникла ошибка получении настроек!<br>Сообщите разработчику!';
			$response->data = 0;
			echo json_encode($response);
		}
	}
	function action_setting_set() {
		$cnn = new Cnn();
		return $cnn->set_report_setting();
	}
	function action_setting_get() {
		$cnn = new Cnn();
		return $cnn->get_report_setting_list();
	}
	function action_setting_get_byName() {
		$cnn = new Cnn();
		return $cnn->get_report_setting_byName();
	}

	function action_get_file() {
		foreach ($_REQUEST as $arg => $val) ${$arg} = $val;
		$userFileName = $file_name;
		$report_name = "Users\\Files\\" . $_SESSION['UserID'] . '_' . $report_name . ".xls";
//		Fn::debugToLog('get file', $report_name.' '.  $file_name);
		$path = 'php://output';
		$userFileName = $userFileName.' '.date('Y-m-d H:i:s').'.xls';
		$handle = @fopen($report_name, "r");
		if ($handle != null) {
//			Fn::debugToLog('get file', filesize($report_name));
			$content = fread($handle, filesize($report_name));
	        header("Pragma: public");
			header("Expires: 0");
			header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
			header("Content-type: application/vnd.ms-excel");
			header("Content-Disposition: attachment; filename='$userFileName';");
			file_put_contents($path, $content);
		}
}
	function action_set_file(){
		foreach ($_REQUEST as $arg => $val)	${$arg} = $val;
		$report_name = "Users\\Files\\" . $_SESSION['UserID'] . '_' . $report_name . ".xls";
//		Fn::debugToLog('set file', $report_name);
        $content = <<<EOF
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf8">
</head>
<body>
	{$html}
</body>
</html>
EOF;
		$bl = file_put_contents($report_name, $content);
		//Fn::debugToLog('set file',$bl);
	}

	function action_jqgrid3() {
		$cnn = new Cnn();
		return $cnn->get_jqgrid3();
	}

	function action_select2() {//for select2
		$cnn = new Cnn();
		return $cnn->select2();
	}
	function action_user_list() {//for select2
		$cnn = new Cnn();
		return $cnn->user_list();
	}
	
	function action_discoundcard_save(){
		$cnn = new Cnn();
		return $cnn->discoundcard_save();
	}
	function action_point_save() {
		$cnn = new Cnn();
		return $cnn->point_save();
	}
	function action_project_save() {
		$cnn = new Cnn();
		return $cnn->project_save();
	}
	function action_seller_save() {
		$cnn = new Cnn();
		return $cnn->seller_save();
	}
	function action_task_save() {
		$cnn = new Cnn();
		return $cnn->task_save();
	}

	function action_task_info(){
		$taskid = $_REQUEST['taskid'];
		if ($taskid != ''){
//			Fn::debugToLog("task", "info ".$taskid);
			$cnn = new Cnn();
			$response = new stdClass();
			$response->success = $cnn->task_info();
//			$response->message = 'Возникла ошибка при получении информации!<br>Сообщите разработчику!';
//			$response->new_id = 0;
			echo json_encode($response);
			return;
//			return $cnn->task_info();
		}
		//return $cnn->discoundcard_save();
	} 
	
	function action_user_save() {
		$cnn = new Cnn();
		return $cnn->user_save();
	}
}
?>
