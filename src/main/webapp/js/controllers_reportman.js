angular.module('myApp').controller('MyCtrlReportman',
		function($scope) {
	checkLogin();
	$("#headtitleTP").html("报表管理");
		var source = [ {
		icon : "./images/nsIcon.png",
		id : "tree_ns_ns1",
		label : "报表分类",
		expanded : true,
		items : [ {
			icon : "./images/vnfIcon.png",
			id : "tree_vnf_cpeg1",
			label : "科室所需报表",
			expanded : true,
			items : [ {
				icon : "./images/perfIcon.png",
				id : "keshi_liliaoke",
				label : "理疗科",
				expanded : true,
				items : [ {
					icon : "./images/vnfPerfIcon.png",
					id : "keshi_liliaoke_jibao",
					label : "季报"
				}, {
					icon : "./images/vmPerfIcon.png",
					id : "tree_cpeg1_perf_vm",
					label : "年报"
				} ]
			},{
				icon : "./images/perfIcon.png",
				id : "tree_cpeg1_p",
				label : "放射科",
				expanded : true,
				items : [ {
					icon : "./images/vnfPerfIcon.png",
					id : "tree_cpeg1_perf_vnf",
					label : "季报"
				}, {
					icon : "./images/vmPerfIcon.png",
					id : "tree_cpeg1_perf_vm",
					label : "年报"
				} ]
			}]
		}, {
			icon : "./images/mmeTopo.png",
			id : "tree_mme1",
			label : "医院所需报表",
			expanded : true,
			items : [ {
				icon : "./images/perfIcon.png",
				id : "tree_cpeg1_p",
				label : "住院部",
				expanded : true,
				items : [ {
					icon : "./images/vnfPerfIcon.png",
					id : "tree_cpeg1_perf_vnf",
					label : "季报"
				}, {
					icon : "./images/vmPerfIcon.png",
					id : "tree_cpeg1_perf_vm",
					label : "年报"
				} ]
			},{
				icon : "./images/perfIcon.png",
				id : "tree_cpeg1_p",
				label : "院领导",
				expanded : true,
				items : [ {
					icon : "./images/vnfPerfIcon.png",
					id : "tree_cpeg1_perf_vnf",
					label : "季报"
				}, {
					icon : "./images/vmPerfIcon.png",
					id : "tree_cpeg1_perf_vm",
					label : "年报"
				} ]
			},]
		} ]
	} ];

	// create jqxTree
	 $('#reportTypeTree').jqxTree({ source: source, width: '180px'});
	 $('#reportTypeTree').on('select', function (event) {
         var args = event.args;
         var item = $('#reportTypeTree').jqxTree('getItem', args.element);
         if (item.id == "keshi_liliaoke_jibao") {
         	// 弹出对话框
        	 $('#customWindow').jqxWindow("open");
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
		var gridWid_rec = pageWid_rec /12 * 9;

//		var pageHeight = document.body.scrollHeight;
//		var gridHei = pageHeight - 80;
		$("#reportTable").jqxGrid({
			theme: themeConstant,
			sortable: true,
			autoheight: true,
			width: gridWid_rec,
			editable: false,
			source: dataAdapter_rec,
			enabletooltips: true,
//			selectionmode: 'singlerow',
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
			        	  var addButton = $("<div style='float: left; margin-left: 2px;'><span style='margin-left: 2px; position: relative;'>导出报表</span></div>");
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
		
		// 初始化报表条件对话框
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
