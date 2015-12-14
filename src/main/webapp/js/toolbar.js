function showJTopoToobar(stage) {

	var toobarDiv = $('<div class="jtopo_toolbar">');
	var centerButton = $("<div id='centerButton' style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-modal-window' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Center</span></div>");
	var fscreenButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-fullscreen' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>FullScreen</span></div>");
	var exportButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-arrow-down' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>ExportPNG</span></div>");
	toobarDiv.append(centerButton);
	//toobarDiv.append(fscreenButton);
	toobarDiv.append(exportButton);
	centerButton.jqxButton({
		theme: btnTheme,
		width: 100,
		height: 20
	});
	fscreenButton.jqxButton({
		theme: btnTheme,
		width: 100,
		height: 20
	});
	exportButton.jqxButton({
		theme: btnTheme,
		width: 100,
		height: 20
	});

	$('#content').prepend(toobarDiv);
	stage.wheelZoom = 0.85;

	fscreenButton.click(function() {
		runPrefixMethod(stage.canvas, "RequestFullScreen")
	});
	centerButton.click(function() {
		stage.centerAndZoom(); //缩放并居中显示
	});
	exportButton.click(function() {
		stage.saveImageInfo();
	});

	//$("#centerButton").jqxButton({theme: btnTheme, width: 80, height: 30});
	//$("#fullScreenButton").jqxButton({theme: btnTheme, width: 80, height: 30});
	//$("#zoomOutButton").jqxButton({theme: btnTheme, width: 100, height: 30});
	//$("#zoomInButton").jqxButton({theme: btnTheme, width: 100, height: 30});
	//$("#findButton").jqxButton({theme: btnTheme, width: 80, height: 30});
	//$("#exportButton").jqxButton({theme: btnTheme, width: 80, height: 30});
	// 工具栏按钮处理
	/*$("input[name='modeRadio']").click(function(){			
		stage.mode = $("input[name='modeRadio']:checked").val();
	});
	$('#centerButton').click(function(){
		stage.centerAndZoom(); //缩放并居中显示
	});
	$('#zoomOutButton').click(function(){
		stage.zoomOut();
	});
	$('#zoomInButton').click(function(){
		stage.zoomIn();
	});
	$('#exportButton').click(function(){
		stage.saveImageInfo();
	});
	$('#zoomCheckbox').click(function(){

		if($('#zoomCheckbox').get(0).checked){
			
			stage.wheelZoom = 0.85; // 设置鼠标缩放比例
		}else{
			stage.wheelZoom = null; // 取消鼠标缩放比例
		}
	});
	$('#fullScreenButton').click(function(){
		runPrefixMethod(stage.canvas, "RequestFullScreen")
	});

	// 查询
	$('#findButton').click(function(){
		var text = $('#findText').val().trim();
		var nodes = stage.find('node[text="'+text+'"]');
		if(nodes.length > 0){
			var node = nodes[0];
			node.selected = true;
			var location = node.getCenterLocation();
			// 查询到的节点居中显示
			stage.setCenter(location.x, location.y);
			
			function nodeFlash(node, n){
				if(n == 0) {
					node.selected = false;
					return;
				};
				node.selected = !node.selected;
				setTimeout(function(){
					nodeFlash(node, n-1);
				}, 300);
			}
			
			// 闪烁几下
			nodeFlash(node, 6);
		}
	});*/
};

var runPrefixMethod = function(element, method) {
	var usablePrefixMethod; ["webkit", "moz", "ms", "o", ""].forEach(function(prefix) {
		if (usablePrefixMethod) return;
		if (prefix === "") {
			// 无前缀，方法首字母小写
			method = method.slice(0, 1).toLowerCase() + method.slice(1);
		}
		var typePrefixMethod = typeof element[prefix + method];
		if (typePrefixMethod + "" !== "undefined") {
			if (typePrefixMethod === "function") {
				usablePrefixMethod = element[prefix + method]();
			} else {
				usablePrefixMethod = element[prefix + method];
			}
		}
	});

	return usablePrefixMethod;
};
/*
runPrefixMethod(this, "RequestFullScreen");
if (typeof window.screenX === "number") {
var eleFull = canvas;
eleFull.addEventListener("click", function() {
	if (runPrefixMethod(document, "FullScreen") || runPrefixMethod(document, "IsFullScreen")) {
		runPrefixMethod(document, "CancelFullScreen");
		this.title = this.title.replace("退出", "");
	} else if (runPrefixMethod(this, "RequestFullScreen")) {
		this.title = this.title.replace("点击", "点击退出");
	}
});
} else {
alert("浏览器不支持");
}*/
