'use strict';

var themeConstant = "bootstrap";
var btnTheme = "darkblue";

var baseUrl = "rest/v1/";
var vnfNameQueryUrl = baseUrl + "cm/config";
var vnfName = "vEPG";

var currentRole = "doctor";// healer doctor

function checkLogin() {
	var isLogin = true;
	if (!isLogin) {
		alert("登录失效，请重新登录！");
		window.location.href = "./login.html";
	}
}

// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers','ngRoute','ngSanitize']).
  config(['$routeProvider', function($routeProvider) {
  	$routeProvider.when('/infoman', {templateUrl: 'partials/infoman_healer.html', controller: 'MyCtrlInfoman'});
    $routeProvider.when('/info_create', {templateUrl: 'partials/infodetail_healer.html', controller: 'MyCtrlInfoCreate'});
    $routeProvider.when('/info_query', {templateUrl: 'partials/infodetail_healer.html', controller: 'MyCtrlInfoQuery'});
    $routeProvider.when('/reportman', {templateUrl: 'partials/reportman.html', controller: 'MyCtrlReportman'});
    
  }]);
  
  
function setAllNavLiInactive() {
	$("#nav_li1").removeClass("active");
	$("#nav_li2").removeClass("active");
	$("#nav_li3").removeClass("active");
	$("#nav_li4").removeClass("active");
}

$("#nav_li1").bind("click", function(){
			setAllNavLiInactive();
			$("#nav_li1").addClass("active");
		});
		
		$("#nav_li2").bind("click", function(){
			setAllNavLiInactive();
			$("#nav_li2").addClass("active");
		});
		
		$("#nav_li3").bind("click", function(){
			setAllNavLiInactive();
  		$("#nav_li3").addClass("active");
		});
		$("#nav_li4_1").bind("click", function(){
			setAllNavLiInactive();
  		$("#nav_li4").addClass("active");
  		$("#myModalLabel").text("Do backup");
  		$('#myModal').modal({
			  keyboard: false
			});
		});
		
		$("#nav_li4_2").bind("click", function(){
			setAllNavLiInactive();

  		$("#nav_li4").addClass("active");
  		$("#myModalLabel").text("Do restore");
			$('#myModal').modal({
			  keyboard: false
			});
		});

$(document).ready(function () {
// Create jqxTree
            var source = [
            	
            { icon: "./images/nsIcon.png", id: "tree_ns_ns1", label: "NS", expanded: true, items: [
                { icon: "./images/vnfIcon.png", id: "tree_vnf_cpeg1", label: vnfName, expanded: true, items: [
                    { icon: "./images/perfIcon.png", id: "tree_cpeg1_p", label: "Performance",expanded: true, items: [
                    	{ icon: "./images/vnfPerfIcon.png", id: "tree_cpeg1_perf_vnf", label: "VNF&VM Data"},
                    	{ icon: "./images/vmPerfIcon.png", id: "tree_cpeg1_perf_vm", label: "HOST Data"}
                    ] },
                    { icon: "./images/alarmIcon.png", id: "tree_cpeg1_a", label: "Alarm"},
                    { icon: "./images/brIcon.png", id: "tree_cpeg1_br", label: "Backup & Restore"},
                    { icon: "./images/upgrade.png", id: "tree_cpeg1_up", label: "UpGrade"}
                ]}
                ,
                { icon: "./images/mmeTopo.png", id: "tree_mme1", label: "MME"}
                /*,
                { icon: "./images/vnfIcon.png", label: "vEPG_2", expanded: true, items: [
                    { icon: "./images/perfIcon.png", label: "Performance" ,expanded: true, items: [
                    	{ icon: "./images/vnfPerfIcon.png", id: "tree_cpeg2_perf_vnf", label: "VNF&VM Data"},
                    	{ icon: "./images/vmPerfIcon.png", id: "tree_cpeg2_perf_vm", label: "HOST Data"}
                    ]},
                    { icon: "./images/alarmIcon.png", label: "Alarm"},
                    { icon: "./images/brIcon.png", label: "Backup & Restore"}
                ]},
                { icon: "./images/vnfIcon.png", label: "vEPG_3", expanded: true, items: [
                    { icon: "./images/perfIcon.png", label: "Performance",expanded: true, items: [
                    	{ icon: "./images/vnfPerfIcon.png", id: "tree_cpeg3_perf_vnf", label: "VNF&VM Data"},
                    	{ icon: "./images/vmPerfIcon.png", id: "tree_cpeg3_perf_vm", label: "HOST Data"}
                    ] },
                    { icon: "./images/alarmIcon.png", label: "Alarm"},
                    { icon: "./images/brIcon.png", label: "Backup & Restore"}
                ]}*/
            ]}
            
            
            
            ];

});

function showNotification(content) {
	var date = new Date();
                var minutes = date.getMinutes();
                if (minutes < 10) {
                    minutes = "0" + minutes;
                }
                var seconds = date.getSeconds();
                if (seconds < 10) {
                    seconds = "0" + seconds;
                }
                
	//$("#currentTime").text(date.getHours() + ":" + minutes + ":" + seconds + ". Label: " + content);
	$("#currentTime").text("Select: " + content);
	$("#timeNotification").jqxNotification("open");
}
