angular.module('myApp').controller('MyCtrlInfoCreate',
		function($scope, $routeParams) {
	checkLogin();
	$("#headtitleTP").html("信息管理->新建信息");
	var infoid = $routeParams.infoid;
	
	$("#patiantName").jqxInput({placeHolder: "请输入患者姓名", height: 25, width: 200, minLength: 1});
	$("#patiantGender").jqxInput({placeHolder: "请输入医师性别", height: 25, width: 200, minLength: 1});
	$("#patiantType").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#patiantCarrier").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#inssuranceType").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#inssuranceRange").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	
	$("#doctorNameInput").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	$("#doctorCodeInput").jqxInput({placeHolder: "", height: 25, width: 200, minLength: 1});
	
	$("#printButton").hide();
	$('#sureButton').jqxButton({ theme: btnTheme, width: '100px', height: '30px',disabled: false });
	$('#cancelButton').jqxButton({ theme: btnTheme, width: '100px', height: '30px', disabled: false });
	
	var source = {
			datatype: "json",
			url: "./json/healitem2.json",
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
			url: "./json/healrecord2.json",
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
	
	// init event
	// draw body pics
    var canvas = document.getElementById('canvas');
    var stage = new JTopo.Stage(canvas);
    var scene = new JTopo.Scene();
    scene.alpha = 1;
    scene.backgroundColor = "210,234,255";
    
    var selectedNode = null;
	$("#picButtons").jqxButtonGroup({ theme: btnTheme, mode: 'default' });
	$("#picButtons").on('buttonclick', function (event) {
        var clickedButton = event.args.button;
//        console.log("click " + clickedButton[0].id);
        if (clickedButton[0].id == 'deleteArea') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		scene.remove(selectedNode);
        	}
        } else if (clickedButton[0].id == 'horizonScaleOut') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		selectedNode.scaleX = selectedNode.scaleX * 1.2;
        	}
        } else if (clickedButton[0].id == 'horizonScaleIn') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		selectedNode.scaleX = selectedNode.scaleX * 0.8;
        	}
        } else if (clickedButton[0].id == 'verticalScaleOut') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		selectedNode.scaleY = selectedNode.scaleY * 1.2;
        	}
        } else if (clickedButton[0].id == 'verticalScaleIn') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		selectedNode.scaleY = selectedNode.scaleY * 0.8;
        	}
        } else if (clickedButton[0].id == 'rotateClockWise') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		selectedNode.rotate = selectedNode.rotate + 0.15;
        	}
        } else if (clickedButton[0].id == 'rotateUcClockWise') {
        	if (selectedNode == null) {
        		alert('请选择区域！');
        	} else {
        		selectedNode.rotate = selectedNode.rotate - 0.15;
        	}
        }
        
    });
	
    function node(x, y, img, name, move) {
        var node = new JTopo.Node(name);
        node.setImage('./images/' + img, true);
        node.setLocation(x, y);
        node.fontColor = "0,0,0";
        node.dragable = move;
        scene.add(node);
        return node;
    }
    node(0, 0, 'muscle.png', "", false);
    
    stage.dbclick(function(event) {
        if (event.button == 0) {
        	var elmOffset = $("#canvas").offset();
            var fatherTop = elmOffset.top;
            var fatherLeft = elmOffset.left;
            
        	var y = event.pageY - fatherTop;
            var x = event.pageX - fatherLeft;
        	var n = node(x, y, 'paint.png', "", true);
            n.alpha = 0.8;
            
            n.addEventListener('mouseup', function(event){
            	console.log('mup');
            	selectedNode = this;
            });
        }
    });
    
//    stage.click(function(event) {
//        if (event.button == 0) {
//        	console.log('clear state');
//        	selectedNode = null;
//        }
//    });
    
    stage.add(scene);
});
