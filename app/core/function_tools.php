<?php
/*
// echo 'Пользователей онлайн: ' . getOnlineUsers() . '<br />';
define("MAX_IDLE_TIME", 3);
function getOnlineUsers(){
	 if ( $directory_handle = opendir( session_save_path() ) ) {
		$count = 0;
		while ( false !== ( $file = readdir( $directory_handle ) ) ) {
			 if($file != '.' && $file != '..'){
				 if(time()- fileatime(session_save_path() . '\\' . $file) < MAX_IDLE_TIME * 60) {
				 $count++;
				 }
			 } 
		}
		closedir($directory_handle);
		return $count;
	 } 
	 else {
		return false;
	 }
}
*/

function nf($num){
	return number_format($num,2, '.', '');
}
function nfx($num,$count){
	return number_format($num,$count, '.', '');
}
function nfx0($num,$count){
	if($num==0)return '';
	return number_format($num,$count, '.', '');
}
function adds($str){
	//return addslashes($str);
	return mysql_escape_string($str);
}
function nfstr($num,$count){
	$str = '000000000000000000000000000000'.number_format($num,0, '.', '');
	return substr($str,strlen($str)-$count,$count);
}
function dt_format($date_str,$date_format_str = 'd/m/Y H:i:s'){
	if($date_str==null)return null;
	return date_format(date_create($date_str),$date_format_str);
}
function microtime_float(){
    list($usec, $sec) = explode(" ", microtime());
    return ((float)$usec + (float)$sec);
}
if (!function_exists('mb_ucfirst') && extension_loaded('mbstring')){
    /**
    * mb_ucfirst - преобразует первый символ в верхний регистр
     * @param string $str - строка
     * @param string $encoding - кодировка, по-умолчанию UTF-8
     * @return string
     */
    function mb_ucfirst($str, $encoding='windows-1251')
    {
        $str = mb_ereg_replace('^[\ ]+', '', $str);
        $str = mb_strtoupper(mb_substr($str, 0, 1, $encoding), $encoding).
               mb_substr($str, 1, mb_strlen($str), $encoding);
        return $str;
    }

}/**
 * Возвращает сумму прописью
 * @ author runcore
 * @ uses morph(...)
 */
function num2str($num) {
    $nul='ноль';
    $ten=array(
        array('','один','два','три','четыре','пять','шесть','семь', 'восемь','девять'),
        array('','одна','две','три','четыре','пять','шесть','семь', 'восемь','девять'),
    );
    $a20=array('десять','одиннадцать','двенадцать','тринадцать','четырнадцать' ,'пятнадцать','шестнадцать','семнадцать','восемнадцать','девятнадцать');
    $tens=array(2=>'двадцать','тридцать','сорок','пятьдесят','шестьдесят','семьдесят' ,'восемьдесят','девяносто');
    $hundred=array('','сто','двести','триста','четыреста','пятьсот','шестьсот', 'семьсот','восемьсот','девятьсот');
    $unit=array( // Units
        array('копейка' ,'копейки' ,'копеек',	 1),
        array('гривня'  ,'гривни'  ,'гривен'    ,0),
        array('тысяча'  ,'тысячи'  ,'тысяч'     ,1),
        array('миллион' ,'миллиона','миллионов' ,0),
        array('миллиард','милиарда','миллиардов',0),
    );
    //
    list($rub,$kop) = explode('.',sprintf("%015.2f", floatval($num)));
    $out = array();
    if (intval($rub)>0) {
        foreach(str_split($rub,3) as $uk=>$v) { // by 3 symbols
            if (!intval($v)) continue;
            $uk = sizeof($unit)-$uk-1; // unit key
            $gender = $unit[$uk][3];
            list($i1,$i2,$i3) = array_map('intval',str_split($v,1));
            // mega-logic
            $out[] = $hundred[$i1]; # 1xx-9xx
            if ($i2>1) $out[]= $tens[$i2].' '.$ten[$gender][$i3]; # 20-99
            else $out[]= $i2>0 ? $a20[$i3] : $ten[$gender][$i3]; # 10-19 | 1-9
            // units without rub & kop
            if ($uk>1) $out[]= morph($v,$unit[$uk][0],$unit[$uk][1],$unit[$uk][2]);
        }
    }
    else $out[] = $nul;
    $out[] = morph(intval($rub), $unit[1][0],$unit[1][1],$unit[1][2]); // rub
    $out[] = $kop.' '.morph($kop,$unit[0][0],$unit[0][1],$unit[0][2]); // kop
    return trim(preg_replace('/ {2,}/', ' ', join(' ',$out)));
}
/**
 * Склоняем словоформу
 * @ author runcore
 */
function morph($n, $f1, $f2, $f5) {
    $n = abs(intval($n)) % 100;
    if ($n>10 && $n<20) return $f5;
    $n = $n % 10;
    if ($n>1 && $n<5) return $f2;
    if ($n==1) return $f1;
    return $f5;
}

function csv_to_array($file_name){
  //echo $file_name;
  $values = false;
  $csv_lines  = file($file_name);
  if(is_array($csv_lines))
  {
    //разбор csv
    $cnt = count($csv_lines);
	//echo $cnt."<br/>";
    for($i = 0; $i < $cnt; $i++)
    {
      $line = $csv_lines[$i];
      $line = trim($line);
      //указатель на то, что через цикл проходит первый символ столбца
      $first_char = true;
      //номер столбца
      $col_num = 0;
      $length = strlen($line);
      for($b = 0; $b < $length; $b++)
      {
        //переменная $skip_char определяет обрабатывать ли данный символ
        if($skip_char != true)
        {
          //определяет обрабатывать/не обрабатывать строку
          ///print $line[$b];
          $process = true;
          //определяем маркер окончания столбца по первому символу
          if($first_char == true)
          {
            if($line[$b] == '"')
            {
              $terminator = '";';
              $process = false;
            }
            else
              $terminator = ';';
            $first_char = false;
          }

          //просматриваем парные кавычки, опредляем их природу
          if($line[$b] == '"')
          {
            $next_char = $line[$b + 1];
            //удвоенные кавычки
            if($next_char == '"')
              $skip_char = true;
            //маркер конца столбца
            elseif($next_char == ';')
            {
              if($terminator == '";')
              {
                $first_char = true;
                $process = false;
                $skip_char = true;
              }
            }
          }

          //определяем природу точки с запятой
          if($process == true)
          {
            if($line[$b] == ';')
            {
               if($terminator == ';')
               {

                  $first_char = true;
                  $process = false;
               }
            }
          }

          if($process == true)
            $column .= $line[$b];

          if($b == ($length - 1))
          {
            $first_char = true;
          }

          if($first_char == true)
          {

            $values[$i][$col_num] = $column;
            $column = '';
            $col_num++;
          }
        }
        else
          $skip_char = false;
      }
    }
  }
   //var_dump($values);
   //echo $values[2][0];
   return $values;
}
?>
