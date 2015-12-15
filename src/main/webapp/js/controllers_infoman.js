angular.module('myApp.controllers', []).controller('MyCtrlInfoman',
		function($scope) {
	checkLogin();
	var alarmQueryVnfUrl = baseUrl + "fm/vnf/";
	var dataurl = alarmQueryVnfUrl + "1";
	var source = {
			datatype: "json",
			url: dataurl,
            pager: function (pagenum, pagesize, oldpagenum) {
                // callback called when a page or page size is changed.
            	console.log('page changed, pagenum: ' + pagenum);
            	console.log('page changed, pagesize: ' + pagesize);
            	console.log('page changed, oldpagenum: ' + oldpagenum);
            },
			updaterow: function(rowid, rowdata, commit) {
				commit(true);
			},
			datafields: [{
				name: 'sequenceNo',
				type: 'string'
			},
			{
				name: 'component',
				type: 'string'
			},
			{
				name: 'alarmName',
				type: 'string'
			},
			{
				name: 'type',
				type: 'string'
			},
			{
				name: 'severity',
				type: 'string'
			},
			{
				name: 'probCause',
				type: 'string'
			},
			{
				name: 'timestamp',
				type: 'string'
			},
			{
				name: 'alarmBody',
				type: 'string'
			},
			{
				name: 'ackStatus',
				type: 'string'
			},
			{
				name: 'clearStatus',
				type: 'string'
			},
			{
				name: 'clearTime',
				type: 'string'
			}]
	};
	var dataAdapter = new $.jqx.dataAdapter(source, {
		loadComplete: function(data) {}
	});

	var pageWid = document.body.clientWidth;
	var gridWid = pageWid - 80;

//	var pageHeight = document.body.scrollHeight;
//	var gridHei = pageHeight - 80;
	$("#infoman_table").jqxGrid({
		theme: themeConstant,
		sortable: true,
		autoheight: true,
		pageable: true,
		pagesizeoptions: ['10', '20', '50'],
		width: gridWid,
//		height:gridHei,
		editable: false,
		source: dataAdapter,
		enabletooltips: true,
		selectionmode: 'singlerow',
		columnsresize: true,
		columns: [
		          {
		        	  text: '记录序号',
		        	  columntype: 'textbox',
		        	  datafield: 'sequenceNo',
		        	  width: 120
		          },
		          {
		        	  text: '姓名',
		        	  columntype: 'textbox',
		        	  datafield: 'component',
		        	  width: 120
		          },
		          {
		        	  text: '性别',
		        	  columntype: 'textbox',
		        	  datafield: 'alarmName',
		        	  width: 120
		          },
		          {
		        	  text: '年龄',
		        	  columntype: 'textbox',
		        	  datafield: 'type'
		          },
		          {
		        	  text: '类型',
		        	  columntype: 'textbox',
		        	  datafield: 'severity',
		        	  width: 120
		          },
		          {
		        	  text: '医保类型',
		        	  columntype: 'textbox',
		        	  datafield: 'probCause',
		        	  width: 120
		          },
		          {
		        	  text: '时间',
		        	  columntype: 'textbox',
		        	  datafield: 'timestamp',
		        	  width: 120
		          },
		          {
		        	  text: '科室',
		        	  columntype: 'textbox',
		        	  datafield: 'alarmBody',
		        	  width: 120
		          },
		          {
		        	  text: '医师',
		        	  columntype: 'textbox',
		        	  datafield: 'ackStatus',
		        	  width: 120
		          },
		          {
		        	  text: '项目',
		        	  columntype: 'textbox',
		        	  datafield: 'clearStatus',
		        	  width: 120
		          },
		          {
		        	  text: '记录',
		        	  columntype: 'textbox',
		        	  datafield: 'clearTime',
		        	  width: 120
		          }],
		          showtoolbar: true,
		          toolbarheight: 40,
		          rendertoolbar: function(statusbar) {
		        	  var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
		        	  var detailButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-ok' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>查看</span></div>");
		        	  var addButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-ok' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>新建</span></div>");
		        	  var queryButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-ok' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>查询</span></div>");
		        	  container.append(addButton);
		        	  container.append(detailButton);
		        	  container.append(queryButton);
		        	  statusbar.append(container);
		        	  addButton.jqxButton({
		        		  theme: btnTheme,
		        		  width: 100,
		        		  height: 20
		        	  });
		        	  detailButton.jqxButton({
		        		  theme: btnTheme,
		        		  width: 100,
		        		  height: 20
		        	  });
		        	  queryButton.jqxButton({
		        		  theme: btnTheme,
		        		  width: 100,
		        		  height: 20
		        	  });
		        	  detailButton.click(function(event) {
    	        		  window.location.href="#/info_query?infoid=1";
    	        	  });
		        	  addButton.click(function(event) {
		        		  window.location.href="#/info_create?infoid=1";
    	        	  });
		        	  queryButton.click(function(event) {
		        		  // 弹出条件对话框
		        		  $('#customWindow').jqxWindow("open");
		        		  $('#endtimeSearchTextInput').jqxDateTimeInput('val', new Date());
    	        	  });
		          }
	});
	
//	$("#infoman_table_btn_add").jqxButton({
//		  theme: btnTheme,
//		  width: 100,
//		  height: 20
//	  });
//	
//	$("#infoman_table_btn_detail").jqxButton({
//		  theme: btnTheme,
//		  width: 100,
//		  height: 20
//	  });
//	
//	$("#infoman_table_btn_query").jqxButton({
//		  theme: btnTheme,
//		  width: 100,
//		  height: 20
//	  });
//	
//	$("#infoman_table_btn_query").click(function(event) {
//		var showtoolbar = $('#infoman_table').jqxGrid('showtoolbar'); 
//		if (showtoolbar) {
//			$('#infoman_table').jqxGrid({ showtoolbar: false}); 
//		} else {
//			$('#infoman_table').jqxGrid({ showtoolbar: true}); 
//		}
//		
//	});
	
	$("#infoman_table").on("pagechanged", function (event) {
        var args = event.args;
        console.log("pagechanged Page:" + args.pagenum + ", Page Size: " + args.pagesize);
        // get page information.
        var paginginformation = $("#infoman_table").jqxGrid('getpaginginformation');
        console.log("pagechanged Page:" + paginginformation.pagenum + ", Page Size: " + paginginformation.pagesize + ", Pages Count: " + paginginformation.pagescount);
    });

    $("#infoman_table").on("pagesizechanged", function (event) {
        var args = event.args;
        console.log("pagesizechanged Page:" + args.pagenum + ", Page Size: " + args.pagesize);
        // get page information.          
        var paginginformation = $("#infoman_table").jqxGrid('getpaginginformation');
        console.log("pagesizechanged Page:" + paginginformation.pagenum + ", Page Size: " + paginginformation.pagesize + ", Pages Count: " + paginginformation.pagescount);
    });
	
    $("#starttimeSearchTextInput").jqxDateTimeInput({
        animationType: 'slide',
        width: '200px',
        showTimeButton: true,
        formatString: "yyyy/MM/dd HH:mm:ss"
    });
    
    $("#endtimeSearchTextInput").jqxDateTimeInput({
        animationType: 'slide',
        width: '200px',
        showTimeButton: true,
        formatString: "yyyy/MM/dd HH:mm:ss"
    });

    $('#customWindow').jqxWindow({  
    	theme: btnTheme,
    	isModal: true,
    	width: 340,
    	height: 260, 
    	autoOpen: false,
    	resizable: false,
    	cancelButton: $('#cancelButton'),
    	initContent: function () {
    		$("#nameSearchTextInput").jqxInput({placeHolder: "请输入患者姓名", height: 25, width: 200, minLength: 1});
    		$("#docnameSearchTextInput").jqxInput({placeHolder: "请输入医师姓名", height: 25, width: 200, minLength: 1});
    		
    		$('#searchButton').jqxButton({ theme: btnTheme, width: '80px', disabled: false });
    		$('#cancelButton').jqxButton({ theme: btnTheme, width: '80px', disabled: false });
    	}
    });
});
