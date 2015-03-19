function filter_save(gridName){
    section = decodeURIComponent(window.location.pathname.split('/')[1]);
    if(window.location.pathname.split('/')[2]!==undefined){
	section += '_'+decodeURIComponent(window.location.pathname.split('/')[2]);
    }
    p = $(gridName).jqGrid('getGridParam','postData');
    filter = JSON.stringify(p);
    //$("#text2").html(window.location.pathname+' '+section);
    $.post('../Engine/filter_save',{ section: section, gridid: gridName,	filter: filter },
	    function(json){
		    //alert(json);
	    }
    );
}
function filter_restore(gridName){
    section = decodeURIComponent(window.location.pathname.split('/')[1]);
    if (window.location.pathname.split('/')[2] !== undefined) {
	section += '_' + decodeURIComponent(window.location.pathname.split('/')[2]);
    }
    $.post('../Engine/filter_restore',{ section: section, gridid: gridName },
	function(filter){
	    //$("#text2").html(filter);
	    var p = jQuery.parseJSON(filter);
	    if(p.success){
//		p.data.replace("rows","_r");
//		p.data.replace('page','_p');
		//alert(p.data);
		filter = jQuery.parseJSON(p.data);
		//alert(filter.rows+'\n'+filter.page);
		//filter.replace("rows", "_r");
		filter._search = true;
		$.each(filter,function(key,value) {
//			if (key == 'rows') return;
//			if (key == 'page') return;
//			if (key == 'nd') return;
			key = key.replace('.','_');
			$('#gs_'+key).val(value);
			//if(key=='rows') $(gridName).jqGrid('setGridParam',{'rowNum':value});
		});
		$(gridName).jqGrid('setGridParam',{'postData':filter}).trigger("reloadGrid");
	    }
	}
    );
}
