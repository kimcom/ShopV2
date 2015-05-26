<?php
$cnni = new Cnni();
if (isset($_REQUEST['goodid'])) {
    $GoodID = $_REQUEST['goodid'];
    $result = Shop::GetGoodInfo($cnni->getDbi(), 'good_info', $GoodID);
    $row = $result->fetch_array(MYSQLI_BOTH);
    $disabled = " disabled";
    $disabledAlik = " disabled";
    if ($_SESSION['AccessLevel'] >= 1000) {
        $disabled = "";
    }
    if ($_SESSION['UserID'] == 1) {
        $disabledAlik = "";
    }
}
?>
<script type="text/javascript">
    $(document).ready(function () {
        var source_id_cut = 0;
        var source_id_copy = 0;

        $("#dialog").dialog({
            autoOpen: false, modal: true, width: 400,
            buttons: [{text: "Закрыть", click: function () {
                        $(this).dialog("close");
                    }}]
        });

// Creating grid1
        $("#grid1").jqGrid({
	    sortable: true,
	   // url: "../goods/good_barcode?param=goods_barcode_short&GoodID=<?php echo $GoodID; ?>",
	    datatype: "json",
	    height: 'auto',
	    colNames: ['Штрих-код'],
	    colModel: [
		{name: 'EAN13', index: 'EAN13', width: 299, sorttype: "text", search: false, editable: true, edittype: "text"}
	    ],
	    width: 'auto',
	    shrinkToFit: false,
	    rowNum: 5,
	    rowList: [5, 10, 20],
	    sortname: "EAN13",
	    viewrecords: true,
            gridview: true,
            toppager: true,
            caption: "Штрих-коды",
            pager: '#pgrid1'
          });
        $("#grid1").jqGrid('navGrid', '#pgrid1', {edit: false, add: true, del: true, search: false, refresh: true, cloneToTop: true},
        {//edit
        }, {//add
            modal: true,
            closeOnEscape: true,
            closeAfterAdd: true,
            reloadAfterSubmit: true,
            beforeInitData: function () {
                if ($("#GoodID").val() == '')
                    return false;
                $("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + $("#GoodID").val()});
            },
            afterSubmit: function (json, postdata) {
                var result = $.parseJSON(json.responseText);
                return [result.success, result.message, result.new_id, result.name];
            },
            savekey: [true, 13]
        }, {//del
            modal: true,
            closeOnEscape: true,
            closeAfterAdd: true,
            reloadAfterSubmit: true,
            beforeInitData: function () {
                if ($("#GoodID").val() == '')
                    return false;
                $("#grid1").jqGrid('setGridParam', {editurl: "../goods/barcode_edit?goodid=" + $("#GoodID").val()});
            },
            afterSubmit: function (json, postdata) {
                var result = $.parseJSON(json.responseText);
                return [result.success, result.message, result.new_id, result.name];
            },
            savekey: [true, 13]
        }
        );

        $("#grid1").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});

        $("#pg_pgrid1").remove();
        $("#pgrid1").removeClass('ui-jqgrid-pager');
        $("#pgrid1").addClass('ui-jqgrid-pager-empty');

        $("#button_save").click(function () {
            if ($("#GoodID").val() == '')
                return;
            $.post('../goods/good_info_save', {
                goodid: $("#GoodID").html(),
                Article: $("#Article").val(),
                Name: $("#Name").val(),
                Division: $("#Division").val(),
                DiscountMax: $("#DiscountMax").val(),
                Unit_in_pack: $("#Unit_in_pack").val(),
                Unit: $("#Unit").val(),
                Weight: $("#Weight").val()
            },
            function (data) {
                if (data == false) {
                    $("#dialog>#text").html('Возникла ошибка при сохранении изменений.<br><br>Сообщите разработчику!');
                    $("#dialog").dialog("open");
                } else {
                    $("#dialog>#text").html('Данные успешно сохранены!');
                    $("#dialog").dialog("open");
                }
            }
            );
        });
        $("#button_save_synchro").click(function () {
            if ($("#GoodID").val() == '')
                return;
            $.post('../goods/synchro_info_save', {
                goodid: $("#GoodID").html(),
                OPT_ID: $("#OPT_ID").val(),
                SHOP_ID: $("#SHOP_ID").val(),
                KIEV_ID: $("#KIEV_ID").val()
            },
            function (data) {
                if (data == false) {
                    $("#dialog>#text").html('Возникла ошибка при сохранении кодов товаров.<br><br>Сообщите разработчику!');
                    $("#dialog").dialog("open");
                } else {
                    $("#dialog>#text").html('Коды товара успешно сохранены!');
                    $("#dialog").dialog("open");
                }
            }
            );
        });
        /////////
        $("#grid2").jqGrid({
		sortable: true,
		url:"../engine/jqgrid3?action=point_list_full&f1=ClientID&f2=NameShort&f3=City&f4=Label",
//		url:"../lists/get_points_list?param=list",
		datatype: "json",
		height:'auto',
		colNames:['Код','Торговая точка','Город','Цена'],
                            colModel: [
                                {name: 'ClientID', index: 'ClientID', width: 60, align: "center", sorttype: "text", search: true},                         
                                {name: 'NameShort',     index: 'NameShort',    width: 150, sorttype: "text", search: true},
                                {name: 'City',          index: 'City',         width: 80,  sorttype: "text", search: true},
                                {name: 'Price',     index: 'Price',    width: 70,  align: "center",  search: true},
                            ],
                    width: 'auto',
                    shrinkToFit: false,
//		loadonce: true,
//		rowNum:10000000,
                    rowNum: 20,
                    rowList: [20, 30, 40, 50, 100],
                    sortname: "ClientID",
                    viewrecords: true,
                    gridview: true,
                    toppager: true,
                    caption: "Цена по тогровым точкам",
                    pager: '#pgrid2',
//		grouping: true,
//		groupingView : { 
//			groupField : ['City','Version'],
//			groupColumnShow : [true,true],
//			groupText : ['<b>{0}</b>'],
//			groupCollapse : false,
//			groupOrder: ['asc','asc'],
//			//groupSummary : [true,true]
//	    }
                });
                $("#grid2").jqGrid('navGrid', '#pgrid2', {edit: false, add: false, del: false, search: false, refresh: true, cloneToTop: true});
                
                $("#grid2").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true});

                $("#pg_pgrid2").remove();
                $("#pgrid2").removeClass('ui-jqgrid-pager');
                $("#pgrid2").addClass('ui-jqgrid-pager-empty');

                //клавиатура
                $("#grid2").jqGrid('bindKeys', {"onEnter": function (rowid) {
                        alert("You enter a row with id:" + rowid)
                    }});

                //$("#grid1").draggable();
                $("#grid2").gridResize();
        //список проектов для выезжающей вкладки
        fsL = 0;
        
        
        // Creating gridL
        $("#gridL").jqGrid({
            sortable: true,
            url: "../goods/list?param=goods_list_where&col=goods_list&cat_id=0",
//		url: "../engine/jqgrid3?action=project_list&f1=ProjectID&f2=Name&pr.Status<>1000",
            datatype: "json",
            height: '500',
            colNames: ['GoodID', 'OPT_ID', 'SHOP_ID', 'KIEV_ID', 'Артикул', 'Название'],
            colModel: [
                {name: 'GoodID', index: 'GoodID', hidden: true},
                {name: 'OPT_ID', index: 'OPT_ID', hidden: true},
                {name: 'SHOP_ID', index: 'SHOP_ID', hidden: true},
                {name: 'KIEV_ID', index: 'KIEV_ID', hidden: true},
                {name: 'Article', index: 'Article', width: 60, sorttype: "text", search: true, editable: true, edittype: "text"},
                {name: 'Name', index: 'Name', width: 150, sorttype: "text", search: true},
//		    {name: 'pr_ProjectID', index: 'pr.ProjectID', width: 50, align: "center", sorttype: "text", search: true},
//		    {name: 'pr_Name', index: 'pr.Name', width: 120, align: "left", sorttype: "text", search: true},
            ],
            gridComplete: function () {
                if (!fsL) {
                    fsL = 1;
                    filter_restore("#gridL");
                }
            },
            onSelectRow: function (rowid, status, e) {
                var id = rowid;
                if (id != null) {
                    window.location = "../goods/good_info?goodid=" + id;
                } else {
                    $("#dialog>#text").html('Сначала выберите запись в таблице!');
                    $("#dialog").dialog("open");
                }
            },
            width: '190',
            shrinkToFit: true,
            rowNum: 100,
            sortname: "Article,Name",
            sortorder: "asc",
            editurl: '../project/operation',
            pager: '#pgridL'
        });
        $("#gridL").jqGrid('navGrid', '#pgridL', {edit: false, add: false, del: false, search: false, refresh: false, cloneToTop: false});
        $("#gridL").jqGrid('filterToolbar', {autosearch: true, searchOnEnter: true, beforeSearch: function () {
                filter_save("#gridL");
            }});
        $("#pgridL").remove();
        $("#rs_mgridL").remove();
        $("#gbox_gridL").removeClass('ui-corner-all');
        $("#gview_gridL .ui-jqgrid-titlebar").remove();
        
        ////
        $('#myTab a').click(function (e) {
		e.preventDefault();
		if (this.id == 'a_tab_barcode') {
			$("#grid1").jqGrid('setGridParam', { url: "../goods/good_barcode?param=goods_barcode_short&GoodID=<?php echo $GoodID; ?>", page: 1});
			$("#grid1").trigger('reloadGrid');
		}
	});
        
        
    });
    
</script>
<input id="cardid" name="cardid" type="hidden" value="<?php echo $row['GoodID']; ?>">
<style>
        #feedback { font-size: 12px; }
        .selectable { list-style-type: none; margin: 0; padding: 0; width: 100%; }
        .selectable li { margin: 3px; padding: 7px 0 0 5px; text-align: left;font-size: 14px; height: 34px; }
</style>
<div class="container center">
    <ul id="myTab" class="nav nav-tabs floatL active hidden-print" role="tablist">
        <li class="active">
            <a id="a_tab_filter" href="#tab_filter" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Информация о товаре</legend>
            </a>
        </li>
        
        <li>
            <a id="a_tab_barcode" href="#tab_barcode" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Штрих-коды</legend>
            </a>
        </li>
        <li>
            <a id="a_tab_price" href="#tab_price" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Цена</legend>
            </a>
        </li>
        <li>
            <a id="a_tab_category" href="#tab_category" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Категории товара</legend>
            </a>
        </li>
        <li>
            <a id="a_tab_balance" href="#tab_balance" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Остатки</legend>
            </a>
        </li>
        <li>
            <a id="a_tab_promo" href="#tab_promo" role="tab" data-toggle="tab" style="padding-top: 5px; padding-bottom: 5px;">
                <legend class="h20">Акции</legend>
            </a>
        </li>
    </ul>
    <div class="floatL">
        <button id="button_save" class="btn btn-sm btn-success frameL m0 h40 hidden-print font14">
            <span class="ui-button-text" style='width:120px;height:22px;'>Сохранить данные</span>
        </button>
    </div>
    <div class="tab-content">
        <div class="active tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_filter">
            <div class='p5 ui-corner-all frameL border0 w400' style='display:table;'>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">Код товара</span>
                    <span id="GoodID" class="input-group-addon form-control TAL"><?php echo $row['GoodID']; ?></span>
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">OPT_ID:</span>
                    <input id="OPT_ID" name="OPT_ID" type="text" class="form-control TAL" value="<?php echo $row['OPT_ID']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>               
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">SHOP_ID:</span>
                    <input id="SHOP_ID" name="SHOP_ID" type="text" class="form-control TAL" value="<?php echo $row['SHOP_ID']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>       
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">KIEV_ID:</span>
                    <input id="kiev_id" name="kiev_id" type="text" class="form-control TAL" value="<?php echo $row['KIEV_ID']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">Артикул:</span>
                    <input id="Article" name="Article" type="text" class="form-control TAL" value="<?php echo $row['Article']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">Наименование:</span>
                    <input id="name" name="name" type="text" class="form-control TAL" value="<?php echo $row['Name']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">Торговая марка:</span>
                    <input id="name" name="name" type="text" class="form-control TAL" value="<?php echo $row['Trademark']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span class="input-group-addon w25p TAL">Производитель:</span>
                    <input id="name" name="name" type="text" class="form-control TAL" value="<?php echo $row['Producer']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w25p TAL">Ед. измерения</span>
                    <input   type="text"  class="form-control TAL" value="<?php echo $row['Unit'];?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w25p TAL">Длина</span>
                    <input   type="text"  class="form-control TAL" value="<?php echo $row['Length']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w25p TAL">Ширина</span>
                    <input   type="text"  class="form-control TAL" value="<?php echo $row['Width']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w25p TAL">Высота</span>
                    <input   type="text"  class="form-control TAL" value="<?php echo $row['Height']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w25p TAL">В упаковке</span>
                    <input   type="text"  class="form-control TAL" value="<?php echo $row['Unit_in_pack']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
                <div class="input-group input-group-sm w100p">
                    <span  class="input-group-addon w25p TAL">Вес </span>
                    <input id="Weight" name="Weight"  type="text"  class="form-control TAL" value="<?php echo $row['Weight']; ?>">
                    <span class="input-group-addon w32"></span>
                </div>
            </div>
      </div>
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_barcode">
            
            <div  class='p5 ui-corner-all frameL border0 w400' style='display:table;'>
                <div class='ui-corner-all frame1 ml5 border0' style=''>
                    <table id="grid1"></table>
                    <div id="pgrid1"></div>
                </div>
            </div>
           
        </div> 
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_price">
         
            <div style='display:table;'>
                <div id='div1' class='frameL pt5'>
                    <table id="grid2"></table>
                    <div id="pgrid2"></div>
                </div>
            </div>
        </div>
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1" id="tab_balance">

            <div style='display:table;'>
                <div class='p5 ui-corner-all frameL ml10 border1' style='float:left;'>
                    <legend>Остатки товара:</legend>
                    <label class='w50' for="1">№ маг.:</label>
                    <label class='w200' for="1">Магазин:</label><!--
                    <input class='w80 TAR' id="PriceBase" name="PriceBase" minlength="1" type="text" value='<?php echo $row['PriceBase']; ?>' disabled/>-->
                    <label class='w50' for="1">Дата нач. ост.</label>
                    <label class='w50' for="1">Ост.нач.</label>
                    <label class='w50' for="1">Приход</label>
                    <label class='w50' for="1">Расход</label>
                    <label class='w50' for="1">Ост.кон.</label>
                    <p></p> 
                    <?php
                    while ($cnni->getDbi()->next_result())
                        $cnni->getDbi()->store_result();
                    $res2 = Shop::GetGoodInfo($cnni->getDbi(), 'good_balance', $GoodID);
                    while ($row2 = $res2->fetch_array(MYSQLI_BOTH)) {
                        $labelID = $row2['ClientID'];
                        $label = $row2['NameShort'];
                        $price = $row2['PriceShop'];
                        $balanceStart = $row2['Balance'];
                        $receipt = $row2['Receipt'];
                        $sale = $row2['Sale'];
                        $dateAct = $row2['DateAct'];
                        $balanceStop = $balanceStart + $receipt - $sale;
                        ?>
                        <label class='w50' for="PriceShop<?php echo $labelID; ?>"><?php echo $labelID; ?>:</label>
                        <label class='w200' for="PriceShop<?php echo $labelID; ?>"><?php echo $label; ?>:</label>
    <!--			<input class='w80 TAR' id="PriceShop<?php echo $labelID; ?>" name="PriceShop<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $price; ?>' disabled>-->
                        <input class='w70 TAC' id="dateAct<?php echo $labelID; ?>" name="dateAct<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $dateAct; ?>' disabled>
                        <input class='w50 TAR' id="balanceStart<?php echo $labelID; ?>" name="balanceStart<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $balanceStart; ?>' disabled>
                        <input class='w50 TAR' id="receipt<?php echo $labelID; ?>" name="receipt<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $receipt; ?>' disabled>
                        <input class='w50 TAR' id="sale<?php echo $labelID; ?>" name="sale<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $sale; ?>' disabled>
                        <input class='w50 TAR' id="balanceStop<?php echo $labelID; ?>" name="balanceStop<?php echo $labelID; ?>" minlength="1" type="text" value='<?php echo $balanceStop; ?>' disabled>
                        <p></p>
                        <?php
                    }
                    ?>
                </div>
            </div>
        </div>
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1 p10" id="tab_category">
            <table class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead><tr><th colspan="3"><h4 class='TAC mt10' >Категории товара:</h4></th></tr>
                    <tr><th>Код</th><th>Название</th><th>Полное имя</th></tr>
                </thead>
                <tbody>
<?php
    while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
    $res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_cats', $GoodID);
    while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
        $labelID = $row3['CatID'];
        $label = $row3['Name'];
        $label2 = $row3['FullName'];
        ?>
                    <tr>
                        <td class="TAC"><?php echo $labelID; ?></td>
                        <td class="TAL"><?php echo $label; ?></td>
                        <td class="TAL"><?php echo $label2; ?></td>
                    </tr>
<?php
    }
?>
                </tbody>
            </table>
        </div>
        <div  class="tab-pane min530 m0 w100p ui-corner-all borderTop1 borderColor frameL border1 p10" id="tab_promo">
            <table class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead><tr><th colspan="3"><h4 class='TAC mt10' >Акции на товар:</h4></th></tr>
                <tr><th>Код</th><th>Название</th><th>Полное имя</th></tr>
                </thead>
                <tbody>
<?php
    while ($cnni->getDbi()->next_result()) $cnni->getDbi()->store_result();
    $res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_promo', $GoodID);
    while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
        $labelID = $row3['PromoID'];
        $label = $row3['Name'];
        $label2 = $row3['FullName'];
?>
                    <tr>
                        <td class="TAC"><?php echo $labelID; ?></td>
                        <td class="TAL"><?php echo $label; ?></td>
                        <td class="TAL"><?php echo $label2; ?></td>
                    </tr>
<?php
            }
?>
                </tbody>
            </table>
            <br>
            <table class="table table-striped table-bordered" cellspacing="0" width="100%">
                <thead><tr><th colspan="3"><h4 class='TAC mt10' >Подарок для акции:</h4></th></tr>
                <tr><th>Код</th><th>Название</th><th>Полное имя</th></tr>
                </thead>
                <tbody>
<?php
                    while ($cnni->getDbi()->next_result())
                        $cnni->getDbi()->store_result();
                    $res3 = Shop::GetGoodInfo($cnni->getDbi(), 'good_promo_action', $GoodID);
                    while ($row3 = $res3->fetch_array(MYSQLI_BOTH)) {
                        $labelID = $row3['PromoID'];
                        $label = $row3['Name'];
                        $label2 = $row3['FullName'];
?>
                        <tr>
                            <td class="TAC"><?php echo $labelID; ?></td>
                            <td class="TAL"><?php echo $label; ?></td>
                            <td class="TAL"><?php echo $label2; ?></td>
                        </tr>
<?php
}
?>
                </tbody>
            </table>

            </div> 
          
</div>
    <div id="lpanel_button" class="border0">
        <div style="padding-left: 10px; padding-top: 10px; padding-bottom: 10px;width: 1ch; text-align: center; word-wrap: break-word;">Список&nbsp;товаров</div>
        <div id="lpanel" class="border0">
            <h4>Список товаров</h4>
            <div id='div1' class='frameL pl5' >
                <table id="gridL"></table>
                <div id="pgridL"></div>
            </div>
        </div>
    </div>
<div id="dialog" title="ВНИМАНИЕ!">
    <p id='text'></p>
</div>
<div id="inputbox2" title="ВНИМАНИЕ!">
    <p id='text'></p>
</div>

</div>
