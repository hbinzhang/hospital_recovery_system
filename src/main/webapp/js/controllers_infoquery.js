angular.module('myApp').controller('MyCtrlInfoQuery',
		function($scope, $routeParams) {
	checkLogin();
	$("#headtitleTP").html("信息管理->查看信息");
	var infoid = $routeParams.infoid;
	
	$("#patiantName").jqxInput({placeHolder: "请输入患者姓名", height: 25, width: 200, minLength: 1});
	$("#patiantGender").jqxInput({placeHolder: "请输入医师性别", height: 25, width: 200, minLength: 1});
	$("#patiantType").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#patiantCarrier").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#inssuranceType").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#inssuranceRange").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	
	$("#doctorNameInput").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#doctorCodeInput").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	
	$('#printButton').jqxButton({ theme: btnTheme, width: '100px', height: '30px',disabled: false });
	$('#sureButton').jqxButton({ theme: btnTheme, width: '100px', height: '30px',disabled: false });
	$('#cancelButton').jqxButton({ theme: btnTheme, width: '100px', height: '30px', disabled: false });
	
	$('#printButton').on('click', function () {
		window.open("printPreview.html?menuId=12321");
	}); 
	
	var source = {
			datatype: "json",
			url: "./json/healitem.json",
            pager: function (pagenum, pagesize, oldpagenum) {
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
				name: 'itemName',
				type: 'string'
			},
			{
				name: 'freqency',
				type: 'string'
			},
			{
				name: 'times',
				type: 'string'
			},
			{
				name: 'cost',
				type: 'string'
			}]
	};
	var dataAdapter = new $.jqx.dataAdapter(source, {
		loadComplete: function(data) {}
	});

	var pageWid = document.body.clientWidth;
	var gridWid = pageWid /12 * 7;

//	var pageHeight = document.body.scrollHeight;
//	var gridHei = pageHeight - 80;
	$("#healItemTable").jqxGrid({
		theme: themeConstant,
		sortable: true,
		autoheight: true,
		width: gridWid,
		editable: false,
		source: dataAdapter,
		enabletooltips: true,
//		selectionmode: 'singlerow',
		columnsresize: true,
		columns: [
		          {
		        	  text: '记录序号',
		        	  columntype: 'textbox',
		        	  datafield: 'sequenceNo',
		        	  width: 120
		          },
		          {
		        	  text: '治疗项目',
		        	  columntype: 'textbox',
		        	  datafield: 'itemName'
		          },
		          {
		        	  text: '频次',
		        	  columntype: 'textbox',
		        	  datafield: 'freqency',
		        	  width: 120
		          },
		          {
		        	  text: '次数',
		        	  columntype: 'textbox',
		        	  datafield: 'times',
		        	  width: 120
		          },
		          {
		        	  text: '费用',
		        	  columntype: 'textbox',
		        	  datafield: 'cost',
		        	  width: 120
		          }],
		          showtoolbar: true,
		          toolbarheight: 40,
		          rendertoolbar: function(statusbar) {
		        	  var container = $("<div style='overflow: hidden; position: relative; margin: 7px;'></div>");
		        	  var addButton = $("<div style='float: left; margin-left: 2px;'><span style='margin-left: 2px; position: relative;'>新建项目</span></div>");
		        	  container.append(addButton);
		        	  statusbar.append(container);
		        	  addButton.jqxButton({
		        		  theme: btnTheme,
		        		  width: 70,
		        		  height: 15
		        	  });
		        	  addButton.click(function(event) {
    	        	  });
		          }
	});
	
	var source_rec = {
			datatype: "json",
			url: "./json/healrecord.json",
            pager: function (pagenum, pagesize, oldpagenum) {
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
				name: 'phyFactor',
				type: 'string'
			},
			{
				name: 'doctor',
				type: 'string'
			},
			{
				name: 'times',
				type: 'string'
			},
			{
				name: 'description',
				type: 'string'
			},
			{
				name: 'datetime',
				type: 'string'
			}]
	};
	var dataAdapter_rec = new $.jqx.dataAdapter(source_rec, {
		loadComplete: function(data) {}
	});

	var pageWid_rec = document.body.clientWidth;
	var gridWid_rec = pageWid_rec /12 * 7;

//	var pageHeight = document.body.scrollHeight;
//	var gridHei = pageHeight - 80;
	$("#healRecordTable").jqxGrid({
		theme: themeConstant,
		sortable: true,
		autoheight: true,
		width: gridWid_rec,
		editable: false,
		source: dataAdapter_rec,
		enabletooltips: true,
//		selectionmode: 'singlerow',
		columnsresize: true,
		columns: [
		          {
		        	  text: '记录序号',
		        	  columntype: 'textbox',
		        	  datafield: 'sequenceNo',
		        	  width: 120
		          },
		          {
		        	  text: '物理因子',
		        	  columntype: 'textbox',
		        	  datafield: 'phyFactor'
		          },
		          {
		        	  text: '医生',
		        	  columntype: 'textbox',
		        	  datafield: 'doctor',
		        	  width: 120
		          },
		          {
		        	  text: '次数',
		        	  columntype: 'textbox',
		        	  datafield: 'times',
		        	  width: 120
		          },
		          {
		        	  text: '秒速',
		        	  columntype: 'textbox',
		        	  datafield: 'description',
		        	  width: 120
		          },
		          {
		        	  text: '日期',
		        	  columntype: 'textbox',
		        	  datafield: 'datetime',
		        	  width: 120
		          }],
		          showtoolbar: true,
		          toolbarheight: 40,
		          rendertoolbar: function(statusbar) {
		        	  var container = $("<div style='overflow: hidden; position: relative; margin: 7px;'></div>");
		        	  var addButton = $("<div style='float: left; margin-left: 2px;'><span style='margin-left: 2px; position: relative;'>新建记录</span></div>");
		        	  container.append(addButton);
		        	  statusbar.append(container);
		        	  addButton.jqxButton({
		        		  theme: btnTheme,
		        		  width: 70,
		        		  height: 15
		        	  });
		        	  addButton.click(function(event) {
    	        	  });
		          }
	});
});
