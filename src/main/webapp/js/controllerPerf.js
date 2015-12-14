var testTimelineCount = 1;
var baseTimeLine = 0;
var loadTable = false;

var ITEMNAME_CONST = {
		host : {
			cpu_util : 'CPU_UTIL',
			mem_util : 'MEM_UTIL'
		},
		
		vm : {
			averageCpuUsage : 'averageCpuUsage',
			nOutByteRate : 'network.outgoing.bytes',
			nInByteRate : 'network.incoming.bytes'
		},
		
		vnf : {
			averageCpuUsage : 'averageCpuUsage',
			memoryUsed : 'memoryUsed',
			ggsnDownlinkBytes : 'ggsnDownlinkBytes',
			ggsnUplinkBytes : 'ggsnUplinkBytes'
		}
};

function getPerfHisData(objId, objType, meterItem, beginTime) {
	// http://localhost:8080/skt/rest/v1/pm/data/
	// 9e82d8fe-2d22-4a2a-958c-e0daa912f867?
	// objectType=host&meterItem=cpu_util&history=true&beginTime=2015-04-02
	var url = perfQueryUrl + objId + "/?objectType=" + objType + "&meterItem=" + meterItem
		+ "&history=true&beginTime=" + beginTime;
	var retDataMap;
	var retData = [];
	$.ajax({
		data: "get",
		url: url,
		cache: false,
		async: false,
		success: function(result) {
			retDataMap = result.data;
		}
	});
	var myDate = new Date();
	var offsetTime = myDate.getTimezoneOffset();
	// iterator the map
	var i = 0, len = retDataMap.length;
	for(i = 0; i < len; i++){
		var oneMap = retDataMap[i];
		for (var key in oneMap) {
			if (key == undefined || key == null) {
				continue;
			}
			if (key == "del") {
				continue;
			}
			console.log('get his data: ' + key + ' value: ' + oneMap[key]);
			retData.push([Number(key) - offsetTime * 60000, Number(oneMap[key])]);
		}
	}
	return retData;
}

Date.prototype.format = function(fmt) { 
	var o = {   
			"M+" : this.getMonth()+1,                 
			"d+" : this.getDate(),                   
			"h+" : this.getHours(),                  
			"m+" : this.getMinutes(),                   
			"s+" : this.getSeconds(),                 
			"q+" : Math.floor((this.getMonth()+3)/3), 
			"S"  : this.getMilliseconds()               
	};   
	if(/(y+)/.test(fmt))   
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
	for(var k in o)   
		if(new RegExp("("+ k +")").test(fmt))   
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
	return fmt;   
}  

// Get and format the date str: yyyy-mm-dd hh24:mi:ss
function getTimeStrBeforeMinute(minute) {
	var currentDate = new Date();
	currentDate.setTime(currentDate.getTime() - minute*1000*60);
	var time = currentDate.format("yyyy-MM-dd hh:mm:ss");
	return time;
}

angular.module('myApp').controller('MyCtrl3VMdata',
function($scope, $routeParams) {
	$scope.message = "Chart";
	var vnfid = $routeParams.vnfid;
	//alert($routeParams.vnfid);
	clearAllTask();
	var localscope = $scope;

	$scope.clickTestChartBtn = function() {
		$scope.message = "Test chart";
	};

	$scope.clickLineChartBtn = function() {
		$scope.message = "Line chart";
	};

	$scope.clickSpeedChartBtn = function() {
		$scope.message = "Speed Meter chart";
	};

	var pageWid = document.body.clientWidth;
	var gridWid = pageWid / 12 * 9 + 30;
	var vns = null,
	hosts = null;
	var hostNameIdMap = {};
	var hostIdNameMap = {};
	$.ajax({
		data: "get",
		url: topoQueryUrl,
		cache: false,
		async: false,
		success: function(result) {
			vns = result.data.vns;
			hosts = result.data.hosts;
		}
	});

	var htmlStr = '<ul style="margin-left: 30px;" id="unorderedList">';
	for (var i = 0; i < hosts.length; i++) {
		htmlStr += "<li>" + hosts[i].hostname + "</li>";
	}
	htmlStr += '</ul>';
	for (var i = 0; i < hosts.length; i++) {
		var hostt = hosts[i];
		hostt.hostid = i;

		hostNameIdMap[hostt.hostname] = hostt.hostid;
		hostIdNameMap[hostt.hostid] = hostt.hostname;
		htmlStr += '<div id="host_perf_data_' + hostt.hostid + '" class="row placeholders" >';
		htmlStr += '<div class="row placeholders" id="chartSpecifiedDivMe' + hostt.hostid + '">';
		htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_1"></div>';
		htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_2"></div>';
		htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_3"></div>';
		htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_4"></div>';
		htmlStr += '</div>';
		htmlStr += '<div class="row placeholders" style="width:600px;" id="chartSpecifiedDivLine' + hostt.hostid + '">';
		htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin: 0px; margin-left: 10px;" id="perfchart_line' + hostt.hostid + '">';
		htmlStr += '</div>';
		htmlStr += '</div>';
		htmlStr += '</div>';
	}
	$('#chartTabs').html(htmlStr);
	var selectedHostId = 0;

	$('#chartTabs').jqxTabs({
		theme: themeConstant,
		width: gridWid,
		showCloseButtons: false
	});
	var meterCharts = [];
	var lineCharts = [];
	$('#chartTabs').on('selected',
	function(event) {
		var selTitle = $('#chartTabs').jqxTabs('getTitleAt', event.args.item);
		selectedHostId = hostNameIdMap[selTitle];
		var hostt = hosts[selectedHostId];
		hostt.hostid = selectedHostId;
		createTabFun(hostt);
	});

	var createTabFun = function(hostt) {
		$("#chartSpecifiedDivLine" + hostt.hostid).width(pageWid / 12 * 9 + 20);
		$("#chartSpecifiedDivMe" + hostt.hostid).width(pageWid / 12 * 9 + 20);

		// drow the first miles table
		var meterChart1, meterChart2, meterChart3, meterChart4;
		$('#perfchart_meter' + hostt.hostid + '_1').highcharts({
			chart: {
				type: 'gauge',
				plotBackgroundColor: null,
				plotBackgroundImage: null,
				plotBorderWidth: 0,
				plotShadow: false
			},
			title: {
				style: {fontSize: "16px"},
				text: 'AverageCpuUsage'
			},
			credits: {
				enabled: false
			},
			credits: {
				enabled: false
			},
			pane: {
				startAngle: -150,
				endAngle: 150,
				background: [{
					backgroundColor: {
						linearGradient: {
							x1: 0,
							y1: 0,
							x2: 0,
							y2: 1
						},
						stops: [[0, '#FFF'], [1, '#333']]
					},
					borderWidth: 0,
					outerRadius: '109%'
				},
				{
					backgroundColor: {
						linearGradient: {
							x1: 0,
							y1: 0,
							x2: 0,
							y2: 1
						},
						stops: [[0, '#333'], [1, '#FFF']]
					},
					borderWidth: 1,
					outerRadius: '107%'
				},
				{
					// default background
				},
				{
					backgroundColor: '#DDD',
					borderWidth: 0,
					outerRadius: '105%',
					innerRadius: '103%'
				}]
			},

			// the value axis
			yAxis: {
				min: 0,
				max: 100,

				minorTickInterval: 'auto',
				minorTickWidth: 1,
				minorTickLength: 10,
				minorTickPosition: 'inside',
				minorTickColor: '#666',

				tickPixelInterval: 30,
				tickWidth: 2,
				tickPosition: 'inside',
				tickLength: 10,
				tickColor: '#666',
				labels: {
					step: 2,
					rotation: 'auto'
				},
				title: {
					text: '%'
				},
				plotBands: [{
					from: 0,
					to: 60,
					color: '#55BF3B' // green
				},
				{
					from: 60,
					to: 90,
					color: '#DDDF0D' // yellow
				},
				{
					from: 90,
					to: 100,
					color: '#DF5353' // red
				}]
			},

			series: [{
				name: 'Use',
				data: [0],
				tooltip: {
					valueSuffix: ' %'
				}
			}]

		},
		// Add some life
		function(chart) {
			meterChart1 = chart;
		});

		$('#perfchart_meter' + hostt.hostid + '_2').highcharts({
			chart: {
				type: 'gauge',
				plotBackgroundColor: null,
				plotBackgroundImage: null,
				plotBorderWidth: 0,
				plotShadow: false
			},
			title: {
				style: {fontSize: "16px"},
				text: 'MemoryUsage'
			},
			credits: {
				enabled: false
			},
			pane: {
				startAngle: -150,
				endAngle: 150,
				background: [{
					backgroundColor: {
						linearGradient: {
							x1: 0,
							y1: 0,
							x2: 0,
							y2: 1
						},
						stops: [[0, '#FFF'], [1, '#333']]
					},
					borderWidth: 0,
					outerRadius: '109%'
				},
				{
					backgroundColor: {
						linearGradient: {
							x1: 0,
							y1: 0,
							x2: 0,
							y2: 1
						},
						stops: [[0, '#333'], [1, '#FFF']]
					},
					borderWidth: 1,
					outerRadius: '107%'
				},
				{
					// default background
				},
				{
					backgroundColor: '#DDD',
					borderWidth: 0,
					outerRadius: '105%',
					innerRadius: '103%'
				}]
			},

			// the value axis
			yAxis: {
				min: 0,
				max: 100,

				minorTickInterval: 'auto',
				minorTickWidth: 1,
				minorTickLength: 10,
				minorTickPosition: 'inside',
				minorTickColor: '#666',

				tickPixelInterval: 30,
				tickWidth: 2,
				tickPosition: 'inside',
				tickLength: 10,
				tickColor: '#666',
				labels: {
					step: 2,
					rotation: 'auto'
				},
				title: {
					text: '%'
				},
				plotBands: [{
					from: 0,
					to: 60,
					color: '#55BF3B' // green
				},
				{
					from: 60,
					to: 90,
					color: '#DDDF0D' // yellow
				},
				{
					from: 90,
					to: 100,
					color: '#DF5353' // red
				}]
			},

			series: [{
				name: 'Use',
				data: [0],
				tooltip: {
					valueSuffix: ' %'
				}
			}]

		},
		// Add some life
		function(chart) {
			meterChart2 = chart;
		});
		/*
        $('#perfchart_meter'+ hostt.hostid + '_3').highcharts({
        chart: {
            type: 'gauge',
            plotBackgroundColor: null,
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false
        },
        title: {
            text: 'AverageCpuUsage'
        },
        credits: {
                    enabled: false
        },
        pane: {
            startAngle: -150,
            endAngle: 150,
            background: [{
                backgroundColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                    stops: [
                        [0, '#FFF'],
                        [1, '#333']
                    ]
                },
                borderWidth: 0,
                outerRadius: '109%'
            }, {
                backgroundColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                    stops: [
                        [0, '#333'],
                        [1, '#FFF']
                    ]
                },
                borderWidth: 1,
                outerRadius: '107%'
            }, {
                // default background
            }, {
                backgroundColor: '#DDD',
                borderWidth: 0,
                outerRadius: '105%',
                innerRadius: '103%'
            }]
        },

        // the value axis
        yAxis: {
            min: 0,
            max: 200,

            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',

            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 10,
            tickColor: '#666',
            labels: {
                step: 2,
                rotation: 'auto'
            },
            title: {
                text: 'km/h'
            },
            plotBands: [{
                from: 0,
                to: 120,
                color: '#55BF3B' // green
            }, {
                from: 120,
                to: 160,
                color: '#DDDF0D' // yellow
            }, {
                from: 160,
                to: 200,
                color: '#DF5353' // red
            }]
        },

        series: [{
            name: 'Speed',
            data: [80],
            tooltip: {
                valueSuffix: ' km/h'
            }
        }]

    },
        // Add some life
        function (chart) {
           meterChart3=chart;
        });
        
        
        
        $('#perfchart_meter'+ hostt.hostid + '_4').highcharts({
        chart: {
            type: 'gauge',
            plotBackgroundColor: null,
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false
        },
        title: {
            text: 'AverageCpuUsage'
        },
        credits: {
                    enabled: false
        },
        pane: {
            startAngle: -150,
            endAngle: 150,
            background: [{
                backgroundColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                    stops: [
                        [0, '#FFF'],
                        [1, '#333']
                    ]
                },
                borderWidth: 0,
                outerRadius: '109%'
            }, {
                backgroundColor: {
                    linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
                    stops: [
                        [0, '#333'],
                        [1, '#FFF']
                    ]
                },
                borderWidth: 1,
                outerRadius: '107%'
            }, {
                // default background
            }, {
                backgroundColor: '#DDD',
                borderWidth: 0,
                outerRadius: '105%',
                innerRadius: '103%'
            }]
        },

        // the value axis
        yAxis: {
            min: 0,
            max: 200,

            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',

            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 10,
            tickColor: '#666',
            labels: {
                step: 2,
                rotation: 'auto'
            },
            title: {
                text: 'km/h'
            },
            plotBands: [{
                from: 0,
                to: 120,
                color: '#55BF3B' // green
            }, {
                from: 120,
                to: 160,
                color: '#DDDF0D' // yellow
            }, {
                from: 160,
                to: 200,
                color: '#DF5353' // red
            }]
        },

        series: [{
            name: 'Speed',
            data: [80],
            tooltip: {
                valueSuffix: ' km/h'
            }
        }]

    },
        // Add some life
        function (chart) {
           meterChart4=chart;
        });  
        */
		meterCharts['perfchart_meter' + hostt.hostid + '_1'] = meterChart1;
		meterCharts['perfchart_meter' + hostt.hostid + '_2'] = meterChart2;
		meterCharts['perfchart_meter' + hostt.hostid + '_3'] = meterChart3;
		meterCharts['perfchart_meter' + hostt.hostid + '_4'] = meterChart4;
		var linechart = null;
		var seriesOptions = [],
		seriesCounter = 0,
		names = ['CPU', 'MEMORY'],
		// create the chart when all data is loaded
		createChart = function() {

			$('#perfchart_line' + hostt.hostid).highcharts('StockChart', {
				title: {
					text: 'Cpu & Memory Line Chart'
				},
				credits: {
					enabled: false
				},
				rangeSelector: {
					enabled: false
				},
				navigator : {
					enabled: true
				},
				yAxis: {
					min: 0,
					labels: {
						formatter: function() {
							//return (this.value > 0 ? ' + ' : '') + this.value + '%';
							return this.value;
						}
					},
					plotLines: [{
						value: 0,
						width: 2,
						color: 'silver'
					}]
				},

				tooltip: {
					pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
					valueDecimals: 2
				},

				series: seriesOptions
			},
			// Add some life
			function(chart) {
				linechart = chart;
				lineCharts["perfchart_line" + hostt.hostid] = linechart;
			});
		};
		var da1 = getPerfHisData(hostIdNameMap[hostt.hostid], "HOST", ITEMNAME_CONST.host.cpu_util, getTimeStrBeforeMinute(60));
//		var da1 = [];
		//  		da1.push([1214179200000, 24.74]);
		//  		da1.push([1214179300000, 13.74]);
		//  		da1.push([1214179400000, 10]);
		var da2 = getPerfHisData(hostIdNameMap[hostt.hostid], "HOST", ITEMNAME_CONST.host.mem_util, getTimeStrBeforeMinute(60));
//		var da2 = [];
		//  		da2.push([1214179200000, 14.74]);
		//  		da2.push([1214179300000, 2.74]);
		//  		da2.push([1214179400000, 56.74]);
		seriesOptions[0] = {
			name: 'CPU',
			color: '#18BDFB',
			data: da1
		};
		seriesOptions[1] = {
			name: 'MEMORY',
			color: '#FE7070',
			data: da2
		};
		createChart();
	};

	// initialise tabs
	var initialHost = {};
	initialHost.hostid = selectedHostId;
	createTabFun(initialHost);

	function getPerfDataFunc() {
		for (var i = 0; i < hosts.length; i++) {
			var hostt = hosts[i];
			hostt.hostid = i;
			//console.log("selectedHostId: " + selectedHostId);
			if (hostt.hostid != selectedHostId) {
				continue;
			}
			var hostName = hostt.hostname;
			var newVal_avgCpu = 0
			var newVal_memUsed = 0
			var inc = Math.round((Math.random() - 0.5) * 20);
			$.ajax({
				data: "get",
				url: perfQueryUrl + hostName + "?objectType=HOST",
				cache: false,
				async: false,
				success: function(result) {
					var len = result.data.length;
					for (var i = 0; i < len; i++) {
						var oneData = result.data[i];
						if (oneData.meterItem == ITEMNAME_CONST.host.cpu_util) {
							newVal_avgCpu = oneData.meterValue;
						} else if (oneData.meterItem == ITEMNAME_CONST.host.mem_util) {
							newVal_memUsed = oneData.meterValue;
						}
					}
					newVal_avgCpu = Number(newVal_avgCpu);
					newVal_memUsed = Number(newVal_memUsed);
					// get current time
					var myDate = new Date();
					var offsetTime = myDate.getTimezoneOffset();
					baseTimeLine = Date.parse(myDate) - offsetTime * 60 * 1000;

					var ct = meterCharts['perfchart_meter' + hostt.hostid + '_1'];
					var point = ct.series[0].points[0];
					point.update(newVal_avgCpu);

					var ct2 = meterCharts['perfchart_meter' + hostt.hostid + '_2'];
					var point2 = ct2.series[0].points[0];
					point2.update(newVal_memUsed);

					var linechart = lineCharts["perfchart_line" + hostt.hostid];
					var series1 = linechart.series[0];
					series1.addPoint([baseTimeLine, newVal_avgCpu]);
					var series2 = linechart.series[1];
					series2.addPoint([baseTimeLine, newVal_memUsed]);
				},
				error: function(result) {
					console.log('get perf data error');
				}
			});
		}
	};
	getPerfDataFunc();
	perfDataTask = setInterval(getPerfDataFunc, 60000);
	
	function getPerfDataFuncRadomData() {
		for (var i = 0; i < hosts.length; i++) {
			var hostt = hosts[i];
			hostt.hostid = i;
			//console.log("selectedHostId: " + selectedHostId);
			if (hostt.hostid != selectedHostId) {
				continue;
			}
			var hostName = hostt.hostname;
			var newVal_avgCpu = Number((Math.random() * 100).toFixed(2));
			var newVal_memUsed = Number((Math.random() * 100).toFixed(2));
			// get current time

			var ct = meterCharts['perfchart_meter' + hostt.hostid + '_1'];
			var point = ct.series[0].points[0];
			point.update(newVal_avgCpu);

			var ct2 = meterCharts['perfchart_meter' + hostt.hostid + '_2'];
			var point2 = ct2.series[0].points[0];
			point2.update(newVal_memUsed);

			var linechart = lineCharts["perfchart_line" + hostt.hostid];
			var series1 = linechart.series[0];
			series1.addPoint([1214179600000 + testTimelineCount * 3000, newVal_avgCpu]);
			var series2 = linechart.series[1];
			series2.addPoint([1214179600000 + testTimelineCount * 3000, newVal_memUsed]);
		}
		testTimelineCount++;
	};
//	getPerfDataFuncRadomData();
//	perfDataTask = setInterval(getPerfDataFuncRadomData, 6000);
});

angular.module('myApp').controller('MyCtrl3',
function($scope, $routeParams) {
	$scope.message = "Chart";
	var vmnamePara = $routeParams.vmname;
	//alert(vmnamePara);
	clearAllTask();
	var localscope = $scope;

	$scope.clickTestChartBtn = function() {
		$scope.message = "Test chart";
	};

	$scope.clickLineChartBtn = function() {
		$scope.message = "Line chart";
	};

	$scope.clickSpeedChartBtn = function() {
		$scope.message = "Speed Meter chart";
	};

	var pageWid = document.body.clientWidth;
	var gridWid = pageWid / 12 * 9 + 30;
	var vns = null,
	hosts = [];
	var hostNameIdMap = {};
	var hostIdNameMap = {};
	$.ajax({
		data: "get",
		url: topoQueryUrl,
		cache: false,
		async: false,
		success: function(result) {
			vns = result.data.vns;
			desCount = 1;
			// construct hosts
			var msghs = result.data.hosts;
			var vnf = {};
			vnf.hostname = vnfName;
			vnf.hostid = 0;
			hosts.push(vnf);

			for (var key = 0; key < msghs.length; key++) {
				var oHo = msghs[key];
				var msgVm = oHo.vms;
				for (var oneVMk = 0; oneVMk < msgVm.length; oneVMk++) {
					var oneVM = msgVm[oneVMk];
					var hostone = {};
					hostone.hostname = oneVM.vmname;
					hostone.hostid = desCount;
					desCount++;
					hosts.push(hostone);
				}
			}
		}
	});

	var htmlStr = '<ul style="margin-left: 30px;" id="unorderedList">';
	for (var i = 0; i < hosts.length; i++) {
		htmlStr += "<li>" + hosts[i].hostname + "</li>";
	}
	htmlStr += '</ul>';
	for (var i = 0; i < hosts.length; i++) {
		var hostt = hosts[i];
		hostNameIdMap[hostt.hostname] = hostt.hostid;
		hostIdNameMap[hostt.hostid] = hostt.hostname;
		if (hostt.hostid == 0) {
			htmlStr += '<div id="host_perf_data_' + hostt.hostid + '" class="row placeholders" >';
			htmlStr += '<div class="row placeholders" id="chartSpecifiedDivMe' + hostt.hostid + '">';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_1"></div>';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_2"></div>';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_3"></div>';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_4"></div>';
			htmlStr += '</div>';
			htmlStr += '<div class="row placeholders" style="width:600px;" id="chartSpecifiedDivLine' + hostt.hostid + '">';
			htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin-top: 0px; margin-left: 10px;" id="perfchart_line' + hostt.hostid + '">';
			htmlStr += '</div>';
			htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin-top: 50px; margin-left: 10px;" id="perfchart_column' + hostt.hostid + '">';
			htmlStr += '</div>';
			htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin-top: 50px;" id="perf_grid_title_' + hostt.hostid + '">';
			htmlStr += '<h5>Board Cpu & Memory Usage</h5></div>';
			htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin: 0px;" id="perf_grid_' + hostt.hostid + '">';
			htmlStr += '</div>';
			htmlStr += '</div>';
			htmlStr += '</div>';
		} else {
			htmlStr += '<div id="host_perf_data_' + hostt.hostid + '" class="row placeholders" >';
			htmlStr += '<div class="row placeholders" id="chartSpecifiedDivMe' + hostt.hostid + '">';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_1"></div>';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_2"></div>';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_3"></div>';
			htmlStr += '<div class="col-xs-3 col-sm-3 placeholder" style="margin: 0px;" id="perfchart_meter' + hostt.hostid + '_4"></div>';
			htmlStr += '</div>';
			htmlStr += '<div class="row placeholders" style="width:600px;" id="chartSpecifiedDivLine' + hostt.hostid + '">';
			htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin-top: 0px; margin-left: 10px;" id="perfchart_line' + hostt.hostid + '">';
			htmlStr += '</div>';
			htmlStr += '<div class="col-xs-12 col-sm-12 placeholder" style="margin-top: 50px; margin-left: 10px;" id="perfchart_column' + hostt.hostid + '">';
			htmlStr += '</div>';
			htmlStr += '</div>';
			htmlStr += '</div>';
		}
	}
	$('#chartTabs').html(htmlStr);

	$('#chartTabs').jqxTabs({
		theme: themeConstant,
		width: gridWid,
		showCloseButtons: false
	});
	var selectedHostId = 0;
	if (vmnamePara === undefined) {
		selectedHostId = 0;
	} else {
		selectedHostId = hostNameIdMap[vmnamePara];
		$('#chartTabs').jqxTabs('select', selectedHostId);
	}
	var meterCharts = [];
	var lineCharts = [];
	$('#chartTabs').on('selected',
	function(event) {
		var selTitle = $('#chartTabs').jqxTabs('getTitleAt', event.args.item);
		selectedHostId = hostNameIdMap[selTitle];
		var hostt = hosts[selectedHostId];
		hostt.hostid = selectedHostId;
		createTabFun(hostt);
	});

	var createTabFun = function(hostt) {
		if (hostt.hostid == 0) {
			$("#chartSpecifiedDivLine" + hostt.hostid).width(pageWid / 12 * 9 + 20);
			$("#chartSpecifiedDivMe" + hostt.hostid).width(pageWid / 12 * 9 + 20);

			// drow the first miles table
			var meterChart1, meterChart2, meterChart3, meterChart4;
			$('#perfchart_meter' + hostt.hostid + '_1').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'High CpuUsage'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 100,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: '%'
					},
					plotBands: [{
						id: "1",
						from: 0,
						to: 50,
						color: '#55BF3B' // green
					},
					{
						id: "2",
						from: 50,
						to: 80,
						color: '#DDDF0D' // yellow
					},
					{
						id: "3",
						from: 80,
						to: 100,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Use',
					data: [0],
					tooltip: {
						valueSuffix: ' %'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart1 = chart;
				//console.log(chart.yAxis[0]);
				/*chart.yAxis[0].update({
            max: 1000
        	});
	        chart.yAxis[0].removePlotBand("1");
	        chart.yAxis[0].removePlotBand("2");
	        chart.yAxis[0].removePlotBand("3");
	        	chart.yAxis[0].addPlotBand({
	                from: 800,
	                to: 1000,
	                color: '#DF5353' // red
	            });*/
				//chart.redraw();
			});

			$('#perfchart_meter' + hostt.hostid + '_2').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'High MemUsage'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 100,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: '%'
					},
					plotBands: [{
						from: 0,
						to: 50,
						color: '#55BF3B' // green
					},
					{
						from: 50,
						to: 80,
						color: '#DDDF0D' // yellow
					},
					{
						from: 80,
						to: 100,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Use',
					data: [0],
					tooltip: {
						valueSuffix: ' km/h'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart2 = chart;
			});

			$('#perfchart_meter' + hostt.hostid + '_3').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'GgsnDownlinkBytes'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 200,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: 'GBytes'
					},
					plotBands: [{
						from: 0,
						to: 120,
						color: '#55BF3B' // green
					},
					{
						from: 120,
						to: 160,
						color: '#DDDF0D' // yellow
					},
					{
						from: 160,
						to: 200,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Current',
					data: [0],
					tooltip: {
						valueSuffix: ' GBytes'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart3 = chart;
			});

			$('#perfchart_meter' + hostt.hostid + '_4').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'GgsnUplinkBytes'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 200,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: 'GBytes'
					},
					plotBands: [{
						from: 0,
						to: 120,
						color: '#55BF3B' // green
					},
					{
						from: 120,
						to: 160,
						color: '#DDDF0D' // yellow
					},
					{
						from: 160,
						to: 200,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Current',
					data: [0],
					tooltip: {
						valueSuffix: ' GBytes'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart4 = chart;
			});

			meterCharts['perfchart_meter' + hostt.hostid + '_1'] = meterChart1;
			meterCharts['perfchart_meter' + hostt.hostid + '_2'] = meterChart2;
			meterCharts['perfchart_meter' + hostt.hostid + '_3'] = meterChart3;
			meterCharts['perfchart_meter' + hostt.hostid + '_4'] = meterChart4;
			var linechart = null;
			var seriesOptions = [],
			seriesCounter = 0,
			names = ['CPU', 'MEMORY'],
			// create the chart when all data is loaded
			createChart = function() {

				$('#perfchart_line' + hostt.hostid).highcharts('StockChart', {
					title: {
						text: 'Cpu & Memory Line Chart'
					},
					credits: {
						enabled: false
					},
					rangeSelector: {
						enabled: false
					},

					yAxis: {
						min: 0,
						labels: {
							formatter: function() {
								//return (this.value > 0 ? ' + ' : '') + this.value + '%';
								return this.value;
							}
						},
						plotLines: [{
							value: 0,
							width: 2,
							color: 'silver'
						}]
					},

					tooltip: {
						pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
						valueDecimals: 2
					},

					series: seriesOptions
				},
				// Add some life
				function(chart) {
					linechart = chart;
					lineCharts["perfchart_line" + hostt.hostid] = linechart;
				});
			};

			var myDate = new Date();
			var offsetTime = myDate.getTimezoneOffset();
			baseTimeLine = Date.parse(myDate) - offsetTime * 60 * 1000;
			var da1 = [];//getPerfHisData(hostIdNameMap[hostt.hostid], "VNF", ITEMNAME_CONST.vnf.averageCpuUsage, getTimeStrBeforeMinute(1440));
//			da1.push([baseTimeLine, 0]);
			//  			da1.push([1214179300000, 13.74]);
			//  			da1.push([1214179400000, 10]);
			var da2 = [];//getPerfHisData(hostIdNameMap[hostt.hostid], "VNF", ITEMNAME_CONST.vnf.memoryUsed, getTimeStrBeforeMinute(1440));
//			da2.push([baseTimeLine, 0]);
			//  			da2.push([1214179300000, 2.74]);
			//  			da2.push([1214179400000, 56.74]);
			var da3 = [];
			//  			da3.push([1214179200000, 8.74]);
			//  			da3.push([1214179300000, 12.74]);
			//  			da3.push([1214179400000, 54.74]);
			seriesOptions[0] = {
				name: 'CPU',
				color: '#18BDFB',
				data: da1
			};
			seriesOptions[1] = {
				name: 'MEMORY',
				color: '#FE7070',
				data: da2
			};

			createChart();

			var da21 = getPerfHisData(hostIdNameMap[hostt.hostid], "VNF", ITEMNAME_CONST.vnf.ggsnDownlinkBytes, getTimeStrBeforeMinute(1440));
//			da21.push(['2015-10-01 00:09:08', 24.74]);
//			da21.push(['2015-10-01 00:10:08', 13.74]);
//			da21.push(['2015-10-01 00:11:08', 10]);
			var da22 = getPerfHisData(hostIdNameMap[hostt.hostid], "VNF", ITEMNAME_CONST.vnf.ggsnUplinkBytes, getTimeStrBeforeMinute(1440));
//			da22.push(['2015-10-01 00:09:08', 14.74]);
//			da22.push(['2015-10-01 00:10:08', 2.74]);
//			da22.push(['2015-10-01 00:11:08', 56.74]);
			$('#perfchart_column' + hostt.hostid).highcharts('StockChart', {
				chart: {
					type: 'column'
				},
				title: {
					text: 'Gssn link bytes'
				},
				credits: {
					enabled: false
				},
				subtitle: {
					text: 'downlink and uplink'
				},
				rangeSelector: {
					enabled: false
				},
				xAxis: {
					type: 'datetime',
					title: {
						text: 'Time'
					}
				},

				yAxis: {
					min: 0,
					title: {
						text: 'Bytes (GB)'
					}
				},
				tooltip: {
					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
					footerFormat: '</table>',
					shared: true,
					useHTML: true
				},
				plotOptions: {
					column: {
						pointPadding: 0.2,
						borderWidth: 0
					}
				},
				series: [{
					name: 'Downlink',
					color: '#B3DA2F',
					data: da21

				},
				{
					name: 'Uplink',
					color: '#78930D',
					data: da22

				}]
			},
			// Add some life
			function(chart) {
				var colchart = chart;
				lineCharts["perfchart_column0"] = colchart;
			});
			
			var dataurl = queryBoardUrl + hosts[hostt.hostid].hostname + "?objectType=VNF";//"./json/queryboard.json";
            var source = {
                //localdata: data,
                datatype: "json",
                url: dataurl,
                updaterow: function(rowid, rowdata, commit) {
                    commit(true);
                },
                datafields: [{
                    name: 'boardId',
                    type: 'string'
                },
                {
                    name: 'cpu',
                    type: 'string'
                },
                {
                    name: 'memory',
                    type: 'string'
                }]
            };
            var dataAdapter = new $.jqx.dataAdapter(source, {
                loadComplete: function(data) {
//                	var rows = $("#perf_grid_" + 0).jqxGrid('getboundrows');
//					var rowIDs = new Array();
//					for (var i = 0; i < rows.length; i++) {
//						rowIDs.push(rows[i].uid);
//					}
//					var commit = $("#perf_grid_" + 0).jqxGrid('deleterow', rowIDs);
                	
//					for (var i = 0; i < 9; i++) {
//						var datarow = {
//								boardId : 'Board Object ID ' + (i + 1),
//								cpu : '',
//								memory:''
//						};
//						$("#perf_grid_" + hostt.hostid).jqxGrid('addrow', null, datarow);
//					}
                }
            });
            
            $("#perf_grid_" + hostt.hostid).jqxGrid({
                theme: themeConstant,
                width: gridWid,
                sortable: false,
                autoheight: true,
                editable: false,
                source: dataAdapter,
                enabletooltips: true,
                selectionmode: 'checkbox',
                columnsresize: false,
                columns: [

                {
                    text: 'Board Object',
                    columntype: 'textbox',
                    datafield: 'boardId',
                    width: 320
                },
                {
                    text: 'CPU Usage',
                    columntype: 'textbox',
                    datafield: 'severity',
                    width: 320,
                    cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties) {
                    	var htmlt = "<div style='height:100%;line-height:23px;margin-top:5px;margin-left:2px;float:left;' id='jqxProgressBarCpu" + row + "'></div><span style='display:block; margin-top:3px;' id='pbSpan" + row + "'>0%</span>";
                    	return htmlt;
                    }
                },
                {
                    text: 'Memory Usage',
                    columntype: 'textbox',
                    datafield: 'ackStatus',
                    width: 320,
                    cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties) {
                    	return "<div style='height:100%;line-height:23px;margin-top:5px;margin-left:2px;float:left;' id='jqxProgressBarMem" + row + "'></div><span style='display:block; margin-top:3px;' id='pbSpanM" + row + "'>0%</span>";
                    }
                }]
            });
            
		} else {
			$("#chartSpecifiedDivLine" + hostt.hostid).width(pageWid / 12 * 9 + 20);
			$("#chartSpecifiedDivMe" + hostt.hostid).width(pageWid / 12 * 9 + 20);
			// drow the first miles table
			var meterChart1, meterChart2, meterChart3, meterChart4;
			$('#perfchart_meter' + hostt.hostid + '_1').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'CPU Usage'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 100,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: '%'
					},
					plotBands: [{
						id: "1",
						from: 0,
						to: 50,
						color: '#55BF3B' // green
					},
					{
						id: "2",
						from: 50,
						to: 80,
						color: '#DDDF0D' // yellow
					},
					{
						id: "3",
						from: 80,
						to: 100,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Use',
					data: [0],
					tooltip: {
						valueSuffix: ' %'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart1 = chart;

			});

			$('#perfchart_meter' + hostt.hostid + '_2').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'Outgoing Rate'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 1000,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: 'GB'
					},
					plotBands: [{
						from: 0,
						to: 600,
						color: '#55BF3B' // green
					},
					{
						from: 600,
						to: 900,
						color: '#DDDF0D' // yellow
					},
					{
						from: 900,
						to: 1000,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Current',
					data: [0],
					tooltip: {
						valueSuffix: ' GB'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart2 = chart;
			});

			$('#perfchart_meter' + hostt.hostid + '_3').highcharts({
				chart: {
					type: 'gauge',
					plotBackgroundColor: null,
					plotBackgroundImage: null,
					plotBorderWidth: 0,
					plotShadow: false
				},
				title: {
					style: {fontSize: "16px"},
					text: 'Incoming Rate'
				},
				credits: {
					enabled: false
				},
				pane: {
					startAngle: -150,
					endAngle: 150,
					background: [{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#FFF'], [1, '#333']]
						},
						borderWidth: 0,
						outerRadius: '109%'
					},
					{
						backgroundColor: {
							linearGradient: {
								x1: 0,
								y1: 0,
								x2: 0,
								y2: 1
							},
							stops: [[0, '#333'], [1, '#FFF']]
						},
						borderWidth: 1,
						outerRadius: '107%'
					},
					{
						// default background
					},
					{
						backgroundColor: '#DDD',
						borderWidth: 0,
						outerRadius: '105%',
						innerRadius: '103%'
					}]
				},

				// the value axis
				yAxis: {
					min: 0,
					max: 200,

					minorTickInterval: 'auto',
					minorTickWidth: 1,
					minorTickLength: 10,
					minorTickPosition: 'inside',
					minorTickColor: '#666',

					tickPixelInterval: 30,
					tickWidth: 2,
					tickPosition: 'inside',
					tickLength: 10,
					tickColor: '#666',
					labels: {
						step: 2,
						rotation: 'auto'
					},
					title: {
						text: 'GBytes'
					},
					plotBands: [{
						from: 0,
						to: 120,
						color: '#55BF3B' // green
					},
					{
						from: 120,
						to: 160,
						color: '#DDDF0D' // yellow
					},
					{
						from: 160,
						to: 200,
						color: '#DF5353' // red
					}]
				},

				series: [{
					name: 'Current',
					data: [0],
					tooltip: {
						valueSuffix: ' GBytes'
					}
				}]

			},
			// Add some life
			function(chart) {
				meterChart3 = chart;
			});

			meterCharts['perfchart_meter' + hostt.hostid + '_1'] = meterChart1;
			meterCharts['perfchart_meter' + hostt.hostid + '_2'] = meterChart2;
			meterCharts['perfchart_meter' + hostt.hostid + '_3'] = meterChart3;
			meterCharts['perfchart_meter' + hostt.hostid + '_4'] = meterChart4;

			var linechart = null;
			var seriesOptions = [],
			seriesCounter = 0,
			names = ['MSFT', 'AAPL', 'GOOG'],
			// create the chart when all data is loaded
			createChart = function() {

				$('#perfchart_line' + hostt.hostid).highcharts('StockChart', {
					title: {
						text: 'Cpu Line Chart'
					},
					credits: {
						enabled: false
					},
					rangeSelector: {
						enabled: false,
						selected: 4
					},

					yAxis: {
						labels: {
							formatter: function() {
								//return (this.value > 0 ? ' + ' : '') + this.value + '%';
								return this.value;
							}
						},
						plotLines: [{
							value: 0,
							width: 2,
							color: 'silver'
						}]
					},

					tooltip: {
						pointFormat: '<span style="color:{series.color}">{series.name}</span>: <b>{point.y}</b><br/>',
						valueDecimals: 2
					},

					series: seriesOptions
				},
				// Add some life
				function(chart) {
					linechart = chart;
					lineCharts["perfchart_line" + hostt.hostid] = linechart;
				});
			};

			var da1 = getPerfHisData(hostIdNameMap[hostt.hostid], "VM", ITEMNAME_CONST.vm.averageCpuUsage, getTimeStrBeforeMinute(1440));
			//  			da1.push([1214179200000, 24.74]);
			//  			da1.push([1214179300000, 13.74]);
			//  			da1.push([1214179400000, 10]);
			var da2 = [];
			//  			da2.push([1214179200000, 14.74]);
			//  			da2.push([1214179300000, 2.74]);
			//  			da2.push([1214179400000, 56.74]);
			var da3 = [];
			//  			da3.push([1214179200000, 8.74]);
			//  			da3.push([1214179300000, 12.74]);
			//  			da3.push([1214179400000, 54.74]);
			seriesOptions[0] = {
				name: 'CPU',
				color: '#18BDFB',
				data: da1
			};

			createChart();

			var da21 = getPerfHisData(hostIdNameMap[hostt.hostid], "VM", ITEMNAME_CONST.vm.nOutByteRate, getTimeStrBeforeMinute(1440));
			//  			da21.push(['2015-10-01 00:09:08', 24.74]);
			//  			da21.push(['2015-10-01 00:10:08', 13.74]);
			//  			da21.push(['2015-10-01 00:11:08', 10]);
			var da22 = getPerfHisData(hostIdNameMap[hostt.hostid], "VM", ITEMNAME_CONST.vm.nInByteRate, getTimeStrBeforeMinute(1440));
			//  			da22.push(['2015-10-01 00:09:08', 14.74]);
			//  			da22.push(['2015-10-01 00:10:08', 2.74]);
			//  			da22.push(['2015-10-01 00:11:08', 56.74]);
			$('#perfchart_column' + hostt.hostid).highcharts('StockChart', {
				chart: {
					type: 'column'
				},
				title: {
					text: 'Networking Byte Rate'
				},
				credits: {
					enabled: false
				},
				subtitle: {
					text: 'outgoing and incoming'
				},
				rangeSelector: {
					enabled: false,
					selected: 4
				},
				xAxis: {
					type: 'datetime',
					title: {
						text: 'Time'
					}
				},

				yAxis: {
					min: 0,
					title: {
						text: 'Bytes (GB)'
					}
				},
				tooltip: {
					headerFormat: '<span style="font-size:10px">{point.key}</span><table>',
					pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' + '<td style="padding:0"><b>{point.y:.1f} mm</b></td></tr>',
					footerFormat: '</table>',
					shared: true,
					useHTML: true
				},
				plotOptions: {
					column: {
						pointPadding: 0.2,
						borderWidth: 0
					}
				},
				series: [{
					name: 'Outgoing',
					color: '#B3DA2F',
					data: da21

				},
				{
					name: 'Incoming',
					color: '#78930D',
					data: da22

				}]
			},
			// Add some life
			function(chart) {
				var colchart = chart;
				lineCharts["perfchart_column" + hostt.hostid] = colchart;
			});
		}
	};

	// initialise tabs
	var initialHost = {};
	initialHost.hostid = selectedHostId;
	createTabFun(initialHost);

	function getPerfDataFunc() {
		for (var i = 0; i < hosts.length; i++) {
			var hostt = hosts[i];
			hostt.hostid = i;
			//console.log("selectedHostId: " + selectedHostId);
			if (hostt.hostid != selectedHostId) {
				continue;
			}
			var hostName = hostt.hostname;
			var newVal_avgCpu = 0;
			var newVal_memUsed = 0;
			var gssnDownByte = 0;
			var gssnUpByte = 0;

			var outByteRate = 0;
			var incomeByteRate = 0;

			var queryURL = perfQueryUrl + hostName + "?objectType=VM";
			if (hostt.hostid == 0) {
				queryURL = perfQueryUrl + hostName + "?objectType=VNF";
			}
			$.ajax({
				data: "get",
				url: queryURL,
				cache: false,
				async: false,
				success: function(result) {
					var len = result.data.length;
					if (hostt.hostid == 0) {
//						 KPIReport=[{
//						objectId=ManagedElement=76vCSCF,boardid=0, 
//						KPI=[{meterItem=averageCpuUsage, meterValue=15, meterUnit=}, 
//						     {meterItem=memoryUsed, meterValue=1218234343, meterUnit=}, 
//						     {meterItem=memory, meterValue=1212233333333, meterUnit=}]}, 
//					    {objectId=ManagedElement=76vCSCF, 
//					    KPI=[{meterItem=, meterValue=12121212, meterUnit=null}, 
//					         {meterItem=, meterValue=12177777, meterUnit=null}]}]}
						var boardDataCpuMap = {};
						var boardDataMemMap = {};
						var maxBoardCpu = 0, maxBoardMem = 0;
						for (var i = 0; i < len; i++) {
							var oneData = result.data[i];
							if (oneData.objectId.indexOf("boardid") >= 0) {
								// board type
								if (oneData.meterItem == ITEMNAME_CONST.vnf.averageCpuUsage) {
									boardDataCpuMap[oneData.objectId] = oneData.meterValue;
									if (Number(oneData.meterValue) > maxBoardCpu) {
										maxBoardCpu = Number(oneData.meterValue);
									}
								} else if (oneData.meterItem == ITEMNAME_CONST.vnf.memoryUsed) {
									boardDataMemMap[oneData.objectId] = oneData.meterValue;
									if (Number(oneData.meterValue) > maxBoardMem) {
										maxBoardMem = Number(oneData.meterValue);
									}
								} 
							} else {
							if (oneData.meterItem == ITEMNAME_CONST.vnf.averageCpuUsage) {
								newVal_avgCpu = oneData.meterValue;
							} else if (oneData.meterItem == ITEMNAME_CONST.vnf.memoryUsed) {
								newVal_memUsed = oneData.meterValue;
							} else if (oneData.meterItem == ITEMNAME_CONST.vnf.ggsnDownlinkBytes) {
								gssnDownByte = oneData.meterValue;
							} else if (oneData.meterItem == ITEMNAME_CONST.vnf.ggsnUplinkBytes) {
								gssnUpByte = oneData.meterValue;
							}
							}
						}
						
						var rows = $("#perf_grid_" + 0).jqxGrid('getboundrows');
						for (var i = 0; i < rows.length; i++) {
							var oneLineData = $("#perf_grid_" + 0).jqxGrid('getrowdata', i);
							var boardIdInGrid = oneLineData.boardId;
							var di = boardDataCpuMap[boardIdInGrid];
							var dimem = boardDataMemMap[boardIdInGrid];
							if (di == undefined || di == null) {
								di = 0;
							}
							if (dimem == undefined || dimem == null) {
								dimem = 0;
							}
							$("#jqxProgressBarCpu" + i).jqxProgressBar({ theme:'darkblue',width: 280, height: 12, value: di});
				            $("#jqxProgressBarMem" + i).jqxProgressBar({ theme:'darkblue',width: 280, height: 12, value: dimem});
				            $("#pbSpan" + i).html("&nbsp;" + di + "%");
				            $("#pbSpanM" + i).html("&nbsp;" + dimem + "%");
						}
						gssnDownByte = Number(gssnDownByte);
						gssnUpByte = Number(gssnUpByte);
						// get current time
						var myDate = new Date();
						var offsetTime = myDate.getTimezoneOffset();
						baseTimeLine = Date.parse(myDate) - offsetTime * 60 * 1000;
						var ct = meterCharts['perfchart_meter' + hostt.hostid + '_1'];
						var point = ct.series[0].points[0];
						point.update(maxBoardCpu);

						var ct2 = meterCharts['perfchart_meter' + hostt.hostid + '_2'];
						var point2 = ct2.series[0].points[0];
						point2.update(maxBoardMem);

						var ct3 = meterCharts['perfchart_meter' + hostt.hostid + '_3'];
						var point3 = ct3.series[0].points[0];
						point3.update(gssnDownByte);

						var ct4 = meterCharts['perfchart_meter' + hostt.hostid + '_4'];
						var point4 = ct4.series[0].points[0];
						point4.update(gssnUpByte);

						var linechart = lineCharts["perfchart_line" + hostt.hostid];
						var series1 = linechart.series[0];
						series1.addPoint([baseTimeLine, maxBoardCpu]);
						var series2 = linechart.series[1];
						series2.addPoint([baseTimeLine, maxBoardMem]);

						var colchart = lineCharts["perfchart_column" + hostt.hostid];
						var seriesc1 = colchart.series[0];
						seriesc1.addPoint([baseTimeLine, gssnDownByte]);
						var seriesc2 = colchart.series[1];
						seriesc2.addPoint([baseTimeLine, gssnUpByte]);
					} else {
						for (var i = 0; i < len; i++) {
							var oneData = result.data[i];
							if (oneData.meterItem == ITEMNAME_CONST.vm.averageCpuUsage) {
								newVal_avgCpu = oneData.meterValue;
							} else if (oneData.meterItem == ITEMNAME_CONST.vm.nOutByteRate) {
								outByteRate = oneData.meterValue;
							} else if (oneData.meterItem == ITEMNAME_CONST.vm.nInByteRate) {
								incomeByteRate = oneData.meterValue;
							}
						}
						newVal_avgCpu = Number(newVal_avgCpu);
						outByteRate = Number(outByteRate);
						incomeByteRate = Number(incomeByteRate);
						// get current time
						var myDate = new Date();
						var offsetTime = myDate.getTimezoneOffset();
						baseTimeLine = Date.parse(myDate) - offsetTime * 60 * 1000;

						var ct = meterCharts['perfchart_meter' + hostt.hostid + '_1'];
						var point = ct.series[0].points[0];
						point.update(newVal_avgCpu);

						var ct2 = meterCharts['perfchart_meter' + hostt.hostid + '_2'];
						var point2 = ct2.series[0].points[0];
						point2.update(outByteRate);

						var ct3 = meterCharts['perfchart_meter' + hostt.hostid + '_3'];
						var point3 = ct3.series[0].points[0];
						point3.update(incomeByteRate);

						var linechart = lineCharts["perfchart_line" + hostt.hostid];
						var series1 = linechart.series[0];
						series1.addPoint([baseTimeLine, newVal_avgCpu]);

						var colchart = lineCharts["perfchart_column" + hostt.hostid];
						var seriesc1 = colchart.series[0];
						seriesc1.addPoint([baseTimeLine, outByteRate]);
						var seriesc2 = colchart.series[1];
						seriesc2.addPoint([baseTimeLine, incomeByteRate]);
					}
				},
				error: function(result) {
					console.log('get perf data error');
				}
			});
		}
	};
	getPerfDataFunc();
	perfDataTask = setInterval(getPerfDataFunc, 60000);
	
	function getPerfDataRadomData () {
		for (var i = 0; i < hosts.length; i++) {
			var hostt = hosts[i];
			if (hostt.hostid != selectedHostId) {
				continue;
			}
			var newVal, newVal2, inc = Math.round((Math.random() - 0.5) * 20);

			var ct = meterCharts['perfchart_meter' + hostt.hostid + '_1'];
			var point = ct.series[0].points[0];
			newVal = point.y + inc;
			newVal2 = point.y - inc;
			if (newVal < 0 || newVal > 100) {
				newVal = point.y - inc;
			}
			if (newVal2 < 0 || newVal2 > 100) {
				newVal2 = point.y + inc;
			}
			point.update(newVal);

			var ct2 = meterCharts['perfchart_meter' + hostt.hostid + '_2'];
			var point2 = ct2.series[0].points[0];
			point2.update(newVal);

			var ct3 = meterCharts['perfchart_meter' + hostt.hostid + '_3'];
			var point3 = ct3.series[0].points[0];
			point3.update(newVal);

			var ct4 = meterCharts['perfchart_meter' + hostt.hostid + '_4'];
			if (ct4 !== undefined) {
				var point4 = ct4.series[0].points[0];
				point4.update(newVal);
			}
			var linechart = lineCharts["perfchart_line" + hostt.hostid];
			var series1 = linechart.series[0];
			series1.addPoint([1214179600000 + testTimelineCount * 3000, newVal]);
			if (selectedHostId == 0) {
				var series2 = linechart.series[1];
				series2.addPoint([1214179600000 + testTimelineCount * 3000, newVal2]);
			}

			var linechart = lineCharts["perfchart_column" + hostt.hostid];
			var series1 = linechart.series[0];
			series1.addPoint([1214179600000 + testTimelineCount * 3000, newVal]);
			var series2 = linechart.series[1];
			series2.addPoint([1214179600000 + testTimelineCount * 3000, newVal2]);

			if (selectedHostId == 0) {
				var rows = $("#perf_grid_" + 0).jqxGrid('getboundrows');
				for (var i = 0; i < rows.length; i++) {
					var cpuVal = Number((Math.random() * 100).toFixed(2));
					var memVal = Number((Math.random() * 100).toFixed(2));
					$("#jqxProgressBarCpu" + i).jqxProgressBar({ theme:'orange',width: 260, height: 12, value: cpuVal});
					$("#jqxProgressBarMem" + i).jqxProgressBar({ theme:'orange',width: 260, height: 12, value: memVal});
					$("#pbSpan" + i).html(cpuVal + "%");
					$("#pbSpanM" + i).html(memVal + "%");
				}
			}
			testTimelineCount++;
		}
	}
	perfDataTask = setInterval(getPerfDataRadomData, 3000);
});

