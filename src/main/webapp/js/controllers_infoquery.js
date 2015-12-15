angular.module('myApp').controller('MyCtrlInfoQuery',
		function($scope, $routeParams) {
	checkLogin();
	$("#headtitleTP").html("信息管理->查看信息");
	var infoid = $routeParams.infoid;
});
