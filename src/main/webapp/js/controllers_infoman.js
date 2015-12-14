angular.module('myApp.controllers', []).controller('MyCtrlInfoman',
		function($scope) {
	var alarmQueryVnfUrl = baseUrl + "fm/vnf/";
	var dataurl = alarmQueryVnfUrl + "1";
	var source = {
			datatype: "json",
			url: dataurl,
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

	$("#infoman_table").jqxGrid({
		theme: themeConstant,
		sortable: true,
		autoheight: true,
		width: gridWid,
		height:600,
		editable: false,
		source: dataAdapter,
		enabletooltips: true,
		selectionmode: 'checkbox',
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
		        	  var confirmButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-ok' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>确认</span></div>");
		        	  container.append(confirmButton);
		        	  statusbar.append(container);
		        	  confirmButton.jqxButton({
		        		  theme: btnTheme,
		        		  width: 100,
		        		  height: 20
		        	  });
		          }
	});
});
