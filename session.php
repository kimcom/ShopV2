<?php
session_save_path("d:\\sites\\Session");
session_start();

if (!isset($_SESSION['DT_sebest']))
    $_SESSION['DT_sebest'] = '11/02/2015';
if (!isset($_SESSION['sitename']))
    $_SESSION['sitename'] = 'Система анализа статистики<br/>и управления продажами. (Сокращенно САУ)<br/>Сузирье'.'&trade;';
if (!isset($_SESSION['titlename']))
    $_SESSION['titlename'] = 'Система анализа статистики и управления продажами компании Сузирье'.'&trade;';
if (!isset($_SESSION['company']))
	$_SESSION['company'] = 'Сузирье™';
if (!isset($_SESSION['dbname']))
	$_SESSION['dbname'] = 'shop';
if (!isset($_SESSION['siteEmail']))
	$_SESSION['siteEmail'] = 'stat@tor.pp.ua';
if (!isset($_SESSION['adminEmail']))
	$_SESSION['adminEmail'] = 'kimcom@ukr.net';
if (!isset($_SESSION['UserID']))
    $_SESSION['UserID'] = 0;
if (!isset($_SESSION['UserName']))
    $_SESSION['UserName'] = "";
if (!isset($_SESSION['UserEMail']))
    $_SESSION['UserEMail'] = "";
if (!isset($_SESSION['UserPost']))
    $_SESSION['UserPost'] = "";
if (!isset($_SESSION['ClientID']))
    $_SESSION['ClientID'] = 0;
if (!isset($_SESSION['ClientName']))
    $_SESSION['ClientName'] = "";
if (!isset($_SESSION['CompanyName']))
    $_SESSION['CompanyName'] = "";
if (!isset($_SESSION['access']))
    $_SESSION['access'] = false;
if (!isset($_SESSION['AccessLevel']))
    $_SESSION['AccessLevel'] = 0;
if (!isset($_SESSION['error_msg']))
	$_SESSION['error_msg'] = "";
if (!isset($_SESSION['error_msg1']))
	$_SESSION['error_msg1'] = "";
if (!isset($_SESSION['error_msg2']))
	$_SESSION['error_msg2'] = "";
?>