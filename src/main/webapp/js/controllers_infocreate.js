angular.module('myApp').controller('MyCtrlInfoCreate',
		function($scope, $routeParams) {
	checkLogin();
	$("#headtitleTP").html("信息管理->新建信息");
	var infoid = $routeParams.infoid;
});
