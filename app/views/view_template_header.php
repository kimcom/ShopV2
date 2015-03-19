<!DOCTYPE html>
<html lang="ru">
    <head>
        <title><?php echo $_SESSION['titlename'];?></title>
        <meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="<?php echo $_SESSION['titlename'];?>">
        <meta name="author" content="ALIK-UANIC">
        <meta http-equiv="Last-Modified" content="<?php echo gmdate("D, d M Y H:i:s"); ?> GMT">
        <link rel="shortcut icon" type="image/x-icon" href="favicon.ico?<?php echo rand(1111111111, 9999999999);?>">
        <link rel="stylesheet" type="text/css" href="../../css/bootstrap.css">
		<link rel="stylesheet" type="text/css" href="../../css/bootstrap-responsive.css">
        <link rel="stylesheet" type="text/css" href="../../css/bootstrap-theme.css">
		<link rel="stylesheet" type="text/css" href="../../css/signin.css">
		<link rel="stylesheet" type="text/css" href="../../css/ui.jqgrid.css">
		<link rel="stylesheet" type="text/css" href="../../css/alik-theme/jquery-ui-1.10.3.custom.css">
		<link rel="stylesheet" type="text/css" href="../../css/alik-theme/correct.css">
        <link rel="stylesheet" type="text/css" href="../../css/jquery.dataTables.min.css">
        <link rel="stylesheet" type="text/css" href="../../css/fs.css">
		<link rel="stylesheet" type="text/css" href="../../css/select2.css">

		<script src="../../js/jquery-1.11.2.min.js" type="text/javascript"></script>
		<script src="../../js/jquery-ui-1.10.3.custom.min.js" type="text/javascript"></script>
		<script src="../../js/i18n/grid.locale-ru.js" type="text/javascript"></script>
		<script src="../../js/jquery.jqGrid.min.js" type="text/javascript"></script>
		<script src="../../js/bootstrap.min.js" type="text/javascript"></script>
		<script src="../../js/jqgrid-filter.js" type="text/javascript"></script>
		<script src="../../js/jquery.dataTables.min.js" type="text/javascript"></script>
		<script src="../../js/dataTables.bootstrap.js" type="text/javascript"></script>
		<script src="../../js/select2.min.js"></script>
		
		<style type="text/css">
            body {
                padding-top: 60px;
                padding-bottom: 0px;
                min-height: 600px;
            }
        </style>
    </head>
	<body>
