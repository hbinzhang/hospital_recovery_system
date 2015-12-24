angular.module('myApp.controllers', []).controller('MyCtrlInfomanDoctor',
		function($scope) {
	checkLogin();
	$('#jqxTabs').jqxTabs({ width: '100%',height: 500, position: 'top'});

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
    		$('#cancelButton').jqxButton({ theme: btnTh8eme, width: '80px', disabled: false });
    	}
    });
    
    $("#jqxWidget").jqxPanel({ width: 180, height: 180});
    $("#jqxWidget2").jqxPanel({ width: 180, height: 180});
    $("#jqxWidget3").jqxPanel({ width: 180, height: 180});
    $("#jqxWidget4").jqxPanel({ width: 180, height: 180});
    $("#jqxWidget5").jqxPanel({ width: 180, height: 180});
    $("#jqxWidget6").jqxPanel({ width: 180, height: 180});
    
    $("#jqxWidget").dblclick(function() {
    	alert('click jqxwidgets');
    });
});
