var brwinInit = false;
/* Controllers */
var vnfTopoInited = false;

// tesks 
var getVepgAlarmNumTask = null;
var perfDataTask = null;
var getAlarmTask = null;


var topoQueryUrl = baseUrl + "cm/topo";
var mmetopoQueryUrl = "./json/v1-VNFnetwork-SGSN-MME.json";

var queryBoardUrl = baseUrl + "pm/getboards/";

var alarmQueryVmUrl = baseUrl + "fm/vm/";
var alarmAckVmUrl = alarmQueryVmUrl + "ack";
var alarmClearVmUrl = alarmQueryVmUrl + "clear";
var alarmQueryVnfUrl = baseUrl + "fm/vnf/";
var alarmAckVnfUrl = alarmQueryVnfUrl + "ack";
var alarmClearVnfUrl = alarmQueryVnfUrl + "clear";

var perfQueryUrl = baseUrl + "pm/data/";

var brGetTaskUrl = baseUrl + "br/backup";
var brGetEventUrl = baseUrl + "br/backupevents";
var brRetoreUrl = baseUrl + "br/restore";
var brRetoreUrl = baseUrl + "br/restore";
var backfilesUrl = baseUrl + "br/backfiles";

var setUpTaskUrl = baseUrl + "up/setUpTask";
var getUpTaskUrl = baseUrl + "up/getUpTask";
var getUserNumUrl = baseUrl + "up/getUserNum";
var cancelUpTaskUrl = baseUrl + "up/cancelUpTask";

function clearAllTask() {
    clearInterval(getVepgAlarmNumTask);
    clearInterval(perfDataTask);
    clearInterval(getAlarmTask);
}

var alarmCountQueryUrl = baseUrl + "fm/vnf/" + vnfName + "/count/";

angular.module('myApp.controllers', []).controller('MyCtrlvnftopo',
function($scope) {
    clearAllTask();
    var hostNameIdMap = {};
    var vns = [],
    hosts = null;

    $.ajax({
        data: "get",
        url: topoQueryUrl,
        cache: false,
        async: false,
        success: function(result) {
            var vns_t = result.data.vns;
            for (var i = 0; /*i < 6 && */i < vns_t.length; i++) {
            	vns.push(vns_t[i]);
            }
            hosts = result.data.hosts;

            var vnf = {};
            vnf.hostname = vnfName;
            vnf.hostid = 0;
            hostNameIdMap[vnf.hostname] = vnf.hostid;
            var desCount = 1;
            for (var key = 0; key < hosts.length; key++) {
                var oHo = hosts[key];
                var msgVm = oHo.vms;
                for (var oneVMk = 0; oneVMk < msgVm.length; oneVMk++) {
                    var oneVM = msgVm[oneVMk];
                    var hostone = {};
                    hostone.hostname = oneVM.vmname;
                    hostone.hostid = desCount;
                    desCount++;
                    hostNameIdMap[hostone.hostname] = hostone.hostid;
                }
            }
        }
    });
    
//    var pageWid = document.body.clientWidth;
//    var gridWid = pageWid / 12 * 9 + 20;
    
//    $("#canvas").width(gridWid);
//    $("#canvas").height(gridWid/12*7);
    
    var canvas = document.getElementById('canvas');
    var stage = new JTopo.Stage(canvas);
    showJTopoToobar(stage);
    var scene = new JTopo.Scene();
    scene.alpha = 1;
    scene.backgroundColor = "210,234,255";
    var getContainerCount = 1;
    //scene.background = './images/bg.jpg';

    function node(x, y, img, name) {
        var node = new JTopo.Node(name);
        node.setImage('./images/' + img, true);
        node.setLocation(x, y);
        node.fontColor = "0,0,0";
        //node.font = "22px Consolas";
        scene.add(node);
        return node;
    }

    function linkNodeStra(nodeA, nodeZ) {
        var link = new JTopo.Link(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '150,150,150';
        scene.add(link);
        return link;
    }

    function linkNode(nodeA, nodeZ) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '150,0,0';
        scene.add(link);
        return link;
    }

    function linkNodeVmAndVn(nodeA, nodeZ) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '80,194,231';
        scene.add(link);
        return link;
    }
    
    function linkNodeDash(nodeA, nodeZ, name) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 4;
        link.strokeColor = '200,200,200';
        link.text = name;
        scene.add(link);
        return link;
    }

    function getContainer(name) {
        var container = new JTopo.Container(name);
        container.textPosition = 'Bottom_Center';
        container.fontColor = '0,0,0';
        container.fillColor = '161,230,192';
        container.font = '18pt Bold';
        container.borderColor = '255,0,0';
        container.borderRadius = 30; // 圆角
        scene.add(container);
        return container;
    }

    function linkVmAndVn(vm, vmX, vnY) {
        var linkN = node(vmX, vnY, 'lineNode.png');
        linkNodeVmAndVn(vm, linkN);
    }
    var elmOffset = $("#headtitleTP").offset();
    var fatherTop = elmOffset.top;
    var fatherLeft = elmOffset.left;
    var currentNode = null;

    var startVN_X = 50;
    var startVN_Y = 50;
    var endVN_X = 750;
    var endVN_Y = 50;
    var step_VN_Y = 30;
    var vnDownStairStep = 330;
    var vnNumUpstairs = 6;
    	
    var startVM_X = 50;
    var stepVM_X = 70;
    var stepVMhost_X = 20;

    var VM_Y_down = 400;
    var VM_Y_up = 230;

    var step_X_between_vmLink = 10;
    var firstLinkOffet = 10;

    function getVnY(vnName) {
        for (var i = 0; i < vns.length; i++) {
            var vn = vns[i];
            if (vn.vnname == vnName) {
            	if (i < vnNumUpstairs) {
            		return startVN_Y + step_VN_Y * i;
            	} else {
            		return startVN_Y + step_VN_Y * i + vnDownStairStep;
            	}
                
            }
        }
        return - 1;
    }

    var xMove = startVM_X;
    // drow Host
    for (var i = 0; i < hosts.length; i++) {
        var hostn = hosts[i];
        var vms = hostn.vms;
        var hostt;
        hostt = getContainer(hostn.hostname);
        // drow VM
        for (var j = 0; j < vms.length; j++) {
            var vmn = vms[j];
            var yy = -1;
            if (j % 2 == 0) {
                yy = VM_Y_down;
            } else {
                yy = VM_Y_up;
            }
            var vmnNameTmp = vmn.vmname;
            var vmt = node(xMove, yy, 'cscftoVepg.PNG', vmnNameTmp);
            vmt.addEventListener('mouseup',
            function(event) {
                currentNode = this;
                handler(event);
            });
            // link vm and vn
            var belVn = vmn.belongto;
            for (var k = 0; k < belVn.length; k++) {
                var linkVN = belVn[k];
                var linkVnName = linkVN.vnname;
                var vny = getVnY(linkVnName);
                if (vny == -1) {
                	continue;
                }
                linkVmAndVn(vmt, firstLinkOffet + xMove + step_X_between_vmLink * k, vny);
            }
            hostt.add(vmt);
            xMove += stepVM_X;
        }
        if (vms.length == 1) {
        	// drow a extra element
        	var vmtDown = node(xMove - 65, VM_Y_down + 90, 'extraNode.png', "");
        	var vmt = node(xMove + 30, VM_Y_up, 'extraNode.png', "");
        	xMove += stepVM_X;
        	hostt.add(vmtDown);
        	hostt.add(vmt);
        }
        xMove += stepVMhost_X;
    }
    
    // drow VN
    endVN_X = xMove; 
    for (var i = 0; i < vns.length; i++) {
        var vn = vns[i];
        if (i < vnNumUpstairs) {
        	var leftn = node(startVN_X, startVN_Y + step_VN_Y * i, 'lineNode.png');
            var rightn = node(endVN_X, endVN_Y + step_VN_Y * i, 'lineNode.png', vn.vnname);
            linkNode(leftn, rightn);
        } else {
        	var leftn = node(startVN_X, startVN_Y + step_VN_Y * i + vnDownStairStep, 'lineNode.png');
            var rightn = node(endVN_X, endVN_Y + step_VN_Y * i + vnDownStairStep, 'lineNode.png', vn.vnname);
            linkNode(leftn, rightn);
        }
        
    }
    
    function handler(event) {
        if (event.button == 2) { // 右键
            $("#contextmenu").css({
                top: event.pageY - fatherTop + 20,
                left: event.pageX - fatherLeft + 40
            }).show();
        }
    }
    $("#contextmenu a").click(function() {
        var text = $(this).text();
        var selId = hostNameIdMap[currentNode.text];
        if (text == 'View Alarms') {
            //alert(selId);
            window.location.href = "#/view2?vmname=" + currentNode.text;
        } else if (text == 'View Kpi') {
            window.location.href = "#/view3?vmname=" + currentNode.text;
        }
        $("#contextmenu").hide();
    });

    stage.click(function(event) {
        if (event.button == 0) { // 右键
            // 关闭弹出菜单（div）
            $("#contextmenu").hide();
        }
    });

    stage.add(scene);
    function centerAndZ() {
    	stage.centerAndZoom();
    	if (endVN_X > 870) { 
    		stage.zoomIn();
    	}
    }
    setTimeout(centerAndZ, 200);
});

angular.module('myApp').controller('MyCtrl1',
function($scope) {
    clearAllTask();
    $scope.message = "Network Topology";
    //$("#vnfTopoBtn").bind("click", function(){
    //	window.location.href="#/vnftopo";
    //});
    //init();
//    var pageWid = document.body.clientWidth;
//    var gridWid = pageWid / 12  * 9 + 20;
//    $("#canvas").width(gridWid);
//    $("#canvas").height(gridWid/12*7);
    
    var elmOffset = $("#headtitleTP").offset();
    var fatherTop = elmOffset.top;
    var fatherLeft = elmOffset.left;

    var canvas = document.getElementById('canvas');
    var stage = new JTopo.Stage(canvas);
    //显示工具栏
    showJTopoToobar(stage);
    var scene = new JTopo.Scene();
    scene.alpha = 1;
    scene.backgroundColor = "210,234,255";
    //scene.background = './images/bg2.jpg';

    function node(x, y, img, name) {
        var node = new JTopo.Node(name);
        node.setImage('./images/' + img, true);
        node.setLocation(x, y);
        scene.add(node);
        return node;
    }

    function linkNode(nodeA, nodeZ) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '150,0,0';
        scene.add(link);
        return link;
    }

    function linkNodeDash(nodeA, nodeZ) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 4;
        link.strokeColor = '200,200,200';
        scene.add(link);
        return link;
    }
    var hss = node(200, 120, 'hss.png');
    var pcfr = node(290, 120, 'pcfr.png');
    var cloud_bg = node(150, 180, 'cloud_bg.png');
    var station = node(50, 290, 'station.png');
    var mme = node(200, 300, 'mme.png');
    var vepg = node(290, 300, 'vepg.png');

    var ocs = node(380, 120, 'oc.png');
    var ofcs = node(470, 120, 'opc.png');

    var cloud = node(670, 280, 'cloud.png');

    linkNode(station, mme);
    linkNode(mme, vepg);
    linkNode(vepg, cloud);
    vepg.alarm = 'NaN';

    linkNodeDash(station, mme);

    linkNodeDash(mme, hss);
    linkNodeDash(vepg, pcfr);

    // link vepg and oc
    var n1 = node(330, 250, 'lineNode.png');
    var n2 = node(410, 250, 'lineNode.png');
    linkNodeDash(vepg, n1);
    linkNodeDash(n1, n2);
    linkNodeDash(n2, ocs);

    var n3 = node(500, 250, 'lineNode.png');
    linkNodeDash(n2, n3);
    linkNodeDash(n3, ofcs);

    function handler(event) {
        if (event.button == 2) {
            //alert(event.pageY);
            $("#contextmenu").css({
                top: event.pageY - fatherTop + 20,
                left: event.pageX - fatherLeft + 40
            }).show();
        }
    }

    function openMmeMenu(event) {
        if (event.button == 2) {
            //alert(event.pageY);
            $("#contextmenu2").css({
                top: event.pageY - fatherTop + 20,
                left: event.pageX - fatherLeft + 40
            }).show();
        }
    }
    
    vepg.addEventListener('mouseup',
    function(event) {
        currentNode = this;
        handler(event);
    });

    mme.addEventListener('mouseup',
    function(event) {
    	currentNode = this;
    	openMmeMenu(event);
    });

    $("#contextmenu a").click(function() {
        var text = $(this).text();
        if (text == 'View Topology') {
            window.location.href = "#/vnftopo";
        } else if (text == 'View Alarms') {
            window.location.href = "#/view2";
        } else if (text == 'View Kpi') {
            window.location.href = "#/view3";
        }
        $("#contextmenu").hide();
    });

    $("#contextmenu2 a").click(function() {
        var text = $(this).text();
        if (text == 'View Topology') {
            window.location.href = "#/view6";
        }
        $("#contextmenu2").hide();
    });
    
    stage.click(function(event) {
        if (event.button == 0) { // 右键
            // 关闭弹出菜单（div）
            $("#contextmenu").hide();
            $("#contextmenu2").hide();
        }
    });

    stage.add(scene);

    function doGetAlarmListNum() {
        $.ajax({
            data: "get",
            url: alarmCountQueryUrl,
            cache: false,
            async: true,
            success: function(result) {
                var num = result.data;
                //console.log('get a list: ' + num);
                vepg.alarm = num;
            }
        });
    };
    doGetAlarmListNum();
    getVepgAlarmNumTask = setInterval(doGetAlarmListNum, 30000);

});

angular.module('myApp').controller('MyCtrl2',
function($scope, $routeParams) {
    $scope.message = "22222";
    clearAllTask();
    var vmnamePara = $routeParams.vmname;
    var selectedHostId = 0;
    var vns = null,
    hosts = [];
    var hostNameIdMap = {};
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
        htmlStr += '<div id="alarm_host_' + hostt.hostid + '" class="row placeholders">';
        htmlStr += '</div>';
    }
    $('#alarmTabs').html(htmlStr);
    var pageWid = document.body.clientWidth;
	var gridWid = pageWid / 12 * 9 + 30;
    $('#alarmTabs').jqxTabs({
        theme: themeConstant,
        width: gridWid,
        showCloseButtons: false
    });
    var selectedHostId = 0;
    if (vmnamePara === undefined) {
        selectedHostId = 0;
    } else {
        selectedHostId = hostNameIdMap[vmnamePara];
        $('#alarmTabs').jqxTabs('select', selectedHostId);
    }
    $('#alarmTabs').on('selected',
    function(event) {
        var selTitle = $('#alarmTabs').jqxTabs('getTitleAt', event.args.item);
        selectedHostId = hostNameIdMap[selTitle];
        var hostt = hosts[selectedHostId];
        hostt.hostid = selectedHostId;
        createTabFun(hostt);
    });

    var createTabFun = function(hostt) {
        if (hostt.hostid == 0) {
            var dataurl = alarmQueryVnfUrl + hosts[hostt.hostid].hostname;
            var source = {
                //localdata: data,
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
            var gridWid = pageWid / 12 * 9 + 20;

            $("#alarm_host_" + hostt.hostid).jqxGrid({
                theme: btnTheme,
                width: gridWid,
                sortable: true,
                autoheight: true,
                editable: false,
                source: dataAdapter,
                enabletooltips: true,
                selectionmode: 'checkbox',
                columnsresize: true,
                columns: [

                {
                    text: 'Sequence No.',
                    columntype: 'textbox',
                    datafield: 'sequenceNo',
                    width: 120
                },
                {
                    text: 'Component',
                    columntype: 'textbox',
                    datafield: 'component',
                    width: 120
                },
                {
                    text: 'AlarmName',
                    columntype: 'textbox',
                    datafield: 'alarmName',
                    width: 120
                },
                {
                    text: 'Type',
                    columntype: 'textbox',
                    datafield: 'type'
                },
                {
                    text: 'Sevevity',
                    columntype: 'textbox',
                    datafield: 'severity',
                    width: 120,
                    cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties) {
                        if (value == 'CRITICAL' || value == 'Critical') {
                            return "<div style='background-color:red;height:100%;line-height:23px;'>" + value + "</div>";
                        } else if (value == 'MAJOR' || value == 'Major') {
                            return "<div style='background-color:orange;height:100%;line-height:23px;'>" + value + "</div>";
                        } else if (value == 'MINOR' || value == 'Minor') {
                            return "<div style='background-color:blue;height:100%;line-height:23px;'>" + value + "</div>";
                        } else {
                            return '' + value + '';
                        }
                    }
                },
                {
                    text: 'ProbCause',
                    columntype: 'textbox',
                    datafield: 'probCause',
                    width: 120
                },
                {
                    text: 'Timestamp',
                    columntype: 'textbox',
                    datafield: 'timestamp',
                    width: 120
                },
                {
                    text: 'AlarmBody',
                    columntype: 'textbox',
                    datafield: 'alarmBody',
                    width: 120
                },
                {
                    text: 'AckStatus',
                    columntype: 'textbox',
                    datafield: 'ackStatus',
                    width: 120
                },
                {
                    text: 'ClearStatus',
                    columntype: 'textbox',
                    datafield: 'clearStatus',
                    width: 120
                },
                {
                    text: 'ClearTime',
                    columntype: 'textbox',
                    datafield: 'clearTime',
                    width: 120
                }],
                showtoolbar: true,
                toolbarheight: 40,
                rendertoolbar: function(statusbar) {
                    // appends buttons to the status bar.
                    var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
//                    var deleteButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-remove' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Clear</span></div>");
                    var confirmButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-ok' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Confirm</span></div>");

//                    container.append(deleteButton);
                    container.append(confirmButton);

                    statusbar.append(container);
//                    deleteButton.jqxButton({
//                        theme: btnTheme,
//                        width: 100,
//                        height: 20
//                    });
                    confirmButton.jqxButton({
                        theme: themeConstant,
                        width: 100,
                        height: 20
                    });

//                    deleteButton.click(function(event) {
//                        var para = "";
//                        var rowindexes = $("#alarm_host_" + hostt.hostid).jqxGrid('getselectedrowindexes');
//                        if (rowindexes === undefined || rowindexes == null || rowindexes.length == 0) {
//                            alert("Please select alarms");
//                            return;
//                        }
//                        for (var i = 0; i < rowindexes.length; i++) {
//                            var data = $("#alarm_host_" + hostt.hostid).jqxGrid('getrowdata', rowindexes[i]);
//                            data.sequenceNo;
//                            para += data.sequenceNo;
//                            if (i < rowindexes.length - 1) {
//                                para += ',';
//                            }
//                        }
//                        console.log("delete select alarm is: " + para);
//                        $.ajax({
//                            type: "post",
//                            url: alarmClearVnfUrl + "?sequenceNo=" + para,
//                            cache: false,
//                            async: false,
//                            success: function(result) {
//                                alert("清除告警成功！");
//                                $("#alarm_host_" + hostt.hostid).jqxGrid('updatebounddata');
//                            },
//                            error: function(result) {
//                                alert("清除告警失败！");
//                            }
//                        });
//                    });
                    confirmButton.click(function(event) {
                        var para = "";
                        var rowindexes = $("#alarm_host_" + hostt.hostid).jqxGrid('getselectedrowindexes');
                        if (rowindexes === undefined || rowindexes == null || rowindexes.length == 0) {
                            alert("Please select alarms");
                            return;
                        }
                        for (var i = 0; i < rowindexes.length; i++) {
                            var data = $("#alarm_host_" + hostt.hostid).jqxGrid('getrowdata', rowindexes[i]);
                            data.sequenceNo;
                            para += data.sequenceNo;
                            if (i < rowindexes.length - 1) {
                                para += ',';
                            }
                        }
                        console.log("confirmButton select alarm is: " + para);
                        $.ajax({
                            type: "post",
                            url: alarmAckVnfUrl + "?sequenceNo=" + para,
                            cache: false,
                            async: false,
                            success: function(result) {
                                alert("确认告警成功！");
                                $("#alarm_host_" + hostt.hostid).jqxGrid('updatebounddata');
                            },
                            error: function(result) {
                                alert("确认告警失败！");
                            }
                        });
                    });
                }
            });

            return;
        }
        var dataurl = alarmQueryVmUrl + hosts[hostt.hostid].hostname;
        var source = {
            //localdata: data,
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
                name: 'timestamp',
                type: 'string'
            },
            {
                name: 'type',
                type: 'string'
            },
            {
                name: 'major',
                type: 'string'
            },
            {
                name: 'minor',
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
                name: 'specificProblem',
                type: 'string'
            },
            {
                name: 'additionalText',
                type: 'string'
            },
            {
                name: 'additionalInfo',
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
        var gridWid = pageWid / 12 * 9 + 20;

        $("#alarm_host_" + hostt.hostid).jqxGrid({
            theme: themeConstant,
            width: gridWid,
            sortable: true,
            autoheight: true,
            editable: false,
            source: dataAdapter,
            enabletooltips: true,
            selectionmode: 'checkbox',
            columnsresize: true,
            columns: [

            {
                text: 'Sequence No.',
                columntype: 'textbox',
                datafield: 'sequenceNo',
                width: 120
            },
            {
                text: 'Component',
                columntype: 'textbox',
                datafield: 'component',
                width: 120
            },
            {
                text: 'Timestamp',
                columntype: 'textbox',
                datafield: 'timestamp',
                width: 120
            },
            {
                text: 'Type',
                columntype: 'textbox',
                datafield: 'type'
            },
            {
                text: 'Major',
                columntype: 'textbox',
                datafield: 'major',
                width: 120
            },
            {
                text: 'Minor',
                columntype: 'textbox',
                datafield: 'minor',
                width: 120
            },
            {
                text: 'Sevevity',
                columntype: 'textbox',
                datafield: 'severity',
                width: 120,
                cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties) {
                    if (value == 'CRITICAL' || value == 'Critical') {
                        return "<div style='background-color:red;height:100%;line-height:23px;'>" + value + "</div>";
                    } else if (value == 'MAJOR' || value == 'Major') {
                        return "<div style='background-color:orange;height:100%;line-height:23px;'>" + value + "</div>";
                    } else if (value == 'MINOR' || value == 'Minor') {
                        return "<div style='background-color:blue;height:100%;line-height:23px;'>" + value + "</div>";
                    } else {
                        return '' + value + '';
                    }
                }
            },
            {
                text: 'ProbCause',
                columntype: 'textbox',
                datafield: 'probCause',
                width: 120
            },
            {
                text: 'specificProblem',
                columntype: 'textbox',
                datafield: 'specificProblem',
                width: 120
            },
            {
                text: 'AdditionalText',
                columntype: 'textbox',
                datafield: 'additionalText',
                width: 120
            },
            {
                text: 'AdditionalInfo',
                columntype: 'textbox',
                datafield: 'additionalInfo',
                width: 120
            },
            {
                text: 'AckStatus',
                columntype: 'textbox',
                datafield: 'ackStatus',
                width: 120
            },
            {
                text: 'ClearStatus',
                columntype: 'textbox',
                datafield: 'clearStatus',
                width: 120
            },
            {
                text: 'ClearTime',
                columntype: 'textbox',
                datafield: 'clearTime',
                width: 120
            }],
            showtoolbar: true,
            toolbarheight: 40,
            rendertoolbar: function(statusbar) {
                // appends buttons to the status bar.
                var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");

                //var deleteButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-remove' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Clear</span></div>");
                var confirmButton = $("<div style='float: left; margin-left: 5px;'><span class='glyphicon glyphicon-ok' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Confirm</span></div>");

                //container.append(deleteButton);
                container.append(confirmButton);

                statusbar.append(container);
//                deleteButton.jqxButton({
//                    theme: btnTheme,
//                    width: 100,
//                    height: 20
//                });
                confirmButton.jqxButton({
                    theme: btnTheme,
                    width: 100,
                    height: 20
                });

//                deleteButton.click(function(event) {
//                    var para = "";
//                    var rowindexes = $("#alarm_host_" + hostt.hostid).jqxGrid('getselectedrowindexes');
//                    if (rowindexes === undefined || rowindexes == null || rowindexes.length == 0) {
//                        alert("Please select alarms");
//                        return;
//                    }
//                    for (var i = 0; i < rowindexes.length; i++) {
//                        var data = $("#alarm_host_" + hostt.hostid).jqxGrid('getrowdata', rowindexes[i]);
//                        data.sequenceNo;
//                        para += data.sequenceNo;
//                        if (i < rowindexes.length - 1) {
//                            para += ',';
//                        }
//                    }
//                    console.log("delete select alarm is: " + para);
//                    $.ajax({
//                        type: "post",
//                        url: alarmClearVmUrl + "?sequenceNo=" + para,
//                        cache: false,
//                        async: false,
//                        success: function(result) {
//                            alert("清除告警成功！");
//                            $("#alarm_host_" + hostt.hostid).jqxGrid('updatebounddata');
//                        },
//                        error: function(result) {
//                            alert("清除告警失败！");
//                        }
//                    });
//                });
                confirmButton.click(function(event) {
                    var para = "";
                    var rowindexes = $("#alarm_host_" + hostt.hostid).jqxGrid('getselectedrowindexes');
                    if (rowindexes === undefined || rowindexes == null || rowindexes.length == 0) {
                        alert("Please select alarms");
                        return;
                    }
                    for (var i = 0; i < rowindexes.length; i++) {
                        var data = $("#alarm_host_" + hostt.hostid).jqxGrid('getrowdata', rowindexes[i]);
                        data.sequenceNo;
                        para += data.sequenceNo;
                        if (i < rowindexes.length - 1) {
                            para += ',';
                        }
                    }
                    console.log("confirmButton select alarm is: " + para);
                    $.ajax({
                        type: "post",
                        url: alarmAckVmUrl + "?sequenceNo=" + para,
                        cache: false,
                        async: false,
                        success: function(result) {
                            alert("确认告警成功！");
                            $("#alarm_host_" + hostt.hostid).jqxGrid('updatebounddata');
                        },
                        error: function(result) {
                            alert("确认告警失败！");
                        }
                    });
                });

            }
        });
    };

    var initialHost = {};
    initialHost.hostid = selectedHostId;
    createTabFun(initialHost);

    getAlarmTask = setInterval(function() {
        for (var i = 0; i < hosts.length; i++) {
            var hostt = hosts[i];
            //console.log("selectedHostId: " + selectedHostId);
            if (hostt.hostid != selectedHostId) {
                continue;
            }
            $("#alarm_host_" + hostt.hostid).jqxGrid('updatebounddata');
        }

    },
    30000);

});

angular.module('myApp').controller('MyCtrl4',
function($scope) {
    clearAllTask();
    var dataurl = "./json/br.json";
    var source = {
        //localdata: data,
        datatype: "json",
        url: brGetEventUrl,
        updaterow: function(rowid, rowdata, commit) {
            commit(true);
        },
        datafields: [

        {
            name: 'id',
            type: 'string'
        },
        {
            name: 'vnfName',
            type: 'string'
        },
        {
            name: 'time',
            type: 'string'
        },
        {
            name: 'event',
            type: 'string'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'dump',
            type: 'string'
        }]
    };
    var dataAdapter = new $.jqx.dataAdapter(source, {
        loadComplete: function(data) {}
    });

    var pageWid = document.body.clientWidth;
    var gridWid = pageWid / 12 * 9 + 20;

    $("#radioDay").jqxRadioButton({
        theme: themeConstant,
        width: 120,
        height: 20
    });
    $("#radioWeek").jqxRadioButton({
        theme: themeConstant,
        width: 120,
        height: 20
    });
    $("#radioMonth").jqxRadioButton({
        theme: themeConstant,
        width: 120,
        height: 20
    });
    $("#triggerTime").jqxDateTimeInput({
        animationType: 'slide',
        showTimeButton: true,
        formatString: "yyyy/MM/dd HH:mm:ss"
    });
    $("#backSubmit").jqxButton({
        theme: themeConstant,
        width: '80'
    });
    $("#backCancel").jqxButton({
        theme: themeConstant,
        width: '80'
    });

    $("#backCancel").click(function(event) {
        $('#backupWin').jqxWindow('close');
    });
    

    $("#backSubmit").click(function(event) {
    	var pstData = {};
    	pstData.vnfName = vnfName;
    	pstData.period = "";
    	if ($("#radioDay").jqxRadioButton('val')) {
    		pstData.period = "Every Day";
    	}
    	if ($("#radioWeek").jqxRadioButton('val')) {
    		pstData.period = "Every Week";
    	}
    	if ($("#radioMonth").jqxRadioButton('val')) {
    		pstData.period = "Every Month";
    	}
    	pstData.triggerTime =  $('#triggerTime').jqxDateTimeInput('val');
    	$.ajax({
            type: "post",
            data: JSON.stringify(pstData),
            contentType: "application/json",
            url: brGetTaskUrl,
            cache: false,
            async: true,
            success: function(result) {
            	alert("修改备份任务成功！");
            	$('#backupWin').jqxWindow('close');
            }, 
            error: function(result) {
            	alert("修改备份任务失败！");
            	$('#backupWin').jqxWindow('close');
            }
        });
    });
    
    $('#backupWin').jqxWindow({
        theme: themeConstant,
        isModal: true,
        autoOpen: false,
        resizable: false,
        height: 260,
        width: 260,
        initContent: function() {}
    });

    $('#backupWin').jqxWindow('close');

    var sourceRes = [];
    
    $.ajax({
        type: "get",
        contentType: "application/json",
        //url: backfilesUrl,
        url: './json/backfiles.json',
        cache: false,
        async: false,
        success: function(result) {
        	var data = result.data;
        	console.log("get file data: " + data);
        	for (var i = 0; i < data.length; i++) {
        		sourceRes.push(data[i].message);
        	}
        }, 
        error: function(result) {
        	alert("Get backup file error！");
        }
    });
    
    // Create a jqxComboBox
    $("#dumpComboBox").jqxComboBox({
    	source: sourceRes,
    	width: '200px',
    	height: '25px'
    });

    $("#resSubmit").jqxButton({
    	theme: themeConstant,
    	width: '80'
    });
    $("#resCancel").jqxButton({
    	theme: themeConstant,
    	width: '80'
    });

    $("#resCancel").click(function(event) {
    	$('#restoreWin').jqxWindow('close');
    });
    $("#resSubmit").click(function(event) {
    	var pstData = {
    			filename : 'backup_filename1.cfg'
    	};
    	$.ajax({
    		type: "post",
    		data: JSON.stringify(pstData),
    		contentType: "application/json",
    		url: brRetoreUrl,
    		cache: false,
    		async: true,
    		success: function(result) {
    			alert("恢复成功！");
    			$('#restoreWin').jqxWindow('close');
    		}, 
    		error: function(result) {
    			alert("恢复失败！");
    			$('#restoreWin').jqxWindow('close');
    		}
    	});
    });
    $('#restoreWin').jqxWindow({
    	theme: themeConstant,
    	isModal: true,
    	autoOpen: false,
    	resizable: false,
    	height: 160,
    	width: 260,
    	initContent: function() {}
    });

    $('#restoreWin').jqxWindow('close');

    $("#br_panel").jqxGrid({
    	theme: themeConstant,
    	width: gridWid,
    	sortable: true,
    	autoheight: true,
    	editable: false,
    	source: dataAdapter,
    	enabletooltips: true,
    	selectionmode: 'checkbox',
    	columnsresize: true,
    	columns: [

    	          {
    	        	  text: 'Id',
    	        	  columntype: 'textbox',
    	        	  datafield: 'id',
    	        	  width: 120
    	          },
    	          {
    	        	  text: 'VnfName',
    	        	  columntype: 'textbox',
    	        	  datafield: 'vnfName',
    	        	  width: 120
    	          },
    	          {
    	        	  text: 'Time',
    	        	  columntype: 'textbox',
    	        	  datafield: 'time',
    	        	  width: 120
    	          },
    	          {
    	        	  text: 'Event',
    	        	  columntype: 'textbox',
    	        	  datafield: 'event'
    	          },
    	          {
    	        	  text: 'Status',
    	        	  columntype: 'textbox',
    	        	  datafield: 'status',
    	        	  width: 120
    	          },
    	          {
    	        	  text: 'Dump',
    	        	  columntype: 'textbox',
    	        	  datafield: 'dump',
    	        	  width: 120
    	          }],
    	          showtoolbar: true,
    	          toolbarheight: 40,
    	          rendertoolbar: function(statusbar) {
    	        	  // appends buttons to the status bar.
    	        	  var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");

    	        	  var bButton = $("<div style='float: left; margin-left: 5px;margin-bottom: 5px;'><span class='glyphicon glyphicon-cloud-upload' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Backup</span></div>");
    	        	  var rButton = $("<div style='float: left; margin-left: 5px;margin-bottom: 5px;'><span class='glyphicon glyphicon-cloud-download' aria-hidden='true'><span style='margin-left: 4px; position: relative;'>Restore</span></div>");

    	        	  container.append(bButton);
    	        	  container.append(rButton);

    	        	  statusbar.append(container);
    	        	  bButton.jqxButton({
    	        		  theme: btnTheme,
    	        		  width: 100,
    	        		  height: 20
    	        	  });
    	        	  rButton.jqxButton({
    	        		  theme: btnTheme,
    	        		  width: 100,
    	        		  height: 20
    	        	  });
    	        	  bButton.click(function(event) {
    	        		  // set window content
    	        		  $.ajax({
    	        			  type: "get",
    	        			  url: brGetTaskUrl,
    	        			  cache: false,
    	        			  async: true,
    	        			  success: function(result) {
    	        				  var obj = result.data;
    	        				  if (obj.length > 0) {
    	        					  var p = obj[0].period;
    	        					  var t = obj[0].triggerTime;
    	        					  console.log(t);
    	        					  $('#triggerTime').jqxDateTimeInput('val', new Date(t));
    	        					  if (p == "Every Day") {
    	        						  $('#radioDay').jqxRadioButton('check');
    	        					  } else if (p == "Every Week") {
    	        						  $('#radioWeek').jqxRadioButton('check');
    	        					  } else if (p == "Every Month") {
    	        						  $('#radioMonth').jqxRadioButton('check');
    	        					  } 
    	        				  }
    	        			  }, 
    	        			  error: function(result) {
    	        				  alert("查询备份任务失败！");
    	        			  }
    	        		  });
    	        		  $('#backupWin').jqxWindow('open');
    	        	  });
    	        	  rButton.click(function(event) {
    	        		  $('#restoreWin').jqxWindow('open');
    	        	  });
    	          }
    });
});
angular.module('myApp').controller('MyCtrl5',
function($scope) {
	$('#vnfUpgradeHeader').html(vnfName + ' upgrade page');
	var sourceRes = ["New sw 1", "New sw 2", "New sw 3", "New sw 4", "New sw 5"];
    // Create a jqxComboBox
    $("#newSwDropDown").jqxComboBox({
        source: sourceRes,
        width: '200px',
        height: '25px'
    });
    $("#upScheduleTime").jqxDateTimeInput({
        animationType: 'slide',
        showTimeButton: true,
        formatString: "yyyy/MM/dd HH:mm:ss"
    });
    
    $("#upradio1").jqxRadioButton({
        theme: themeConstant,
        width: 120,
        height: 20
    });
    $("#upradio2").jqxRadioButton({
        theme: themeConstant,
        width: 120,
        height: 20
    });
    $("#upgradeSubmit").jqxButton({
        theme: btnTheme,
        width: '80'
    });
    $("#upgradeCancel").jqxButton({
        theme: btnTheme,
        width: '80'
    });
    $("#upgradeRefresh").jqxButton({
        theme: btnTheme,
        width: '80'
    });
    
    $("#upgradeSubmit").click(function(event) {
    	var pstData = {};
    	var item = $("#newSwDropDown").jqxComboBox('getSelectedItem'); 
    	pstData.swName = item.label;
    	pstData.vnfName = vnfName;
    	pstData.scheduleTime =  $('#upScheduleTime').jqxDateTimeInput('val');
    	pstData.timeConsumed =  $('#scriptConsumedMin').val();
    	pstData.userRemain =  $('#userRemained').val();
    	if ($("#upradio1").jqxRadioButton('val')) {
    		pstData.thresholdType = "1";
    	}
    	if ($("#upradio2").jqxRadioButton('val')) {
    		pstData.thresholdType = "2";
    	}
    	$.ajax({
            type: "post",
            data: JSON.stringify(pstData),
            contentType: "application/json",
            url: setUpTaskUrl,
            cache: false,
            async: true,
            success: function(result) {
            	alert("Set upgrade task sucessful!");
            	$("#upgradeSubmit").jqxButton({ disabled: true});
            }, 
            error: function(result) {
            	alert("Set upgrade task failed!");
            }
        });
    });
    
    $("#upgradeCancel").click(function(event) {
    	$.ajax({
    		type: "post",
    		contentType: "application/json",
    		url: cancelUpTaskUrl,
    		cache: false,
    		async: true,
    		success: function(result) {
    			alert("Cancel task successful");
    			$("#upgradeSubmit").jqxButton({ disabled: false});
    		}, 
    		error: function(result) {
    			alert("Cancel task failed");
    		}
    	});
    });
    
    $("#upgradeRefresh").click(function(event) {
    	$.ajax({
            type: "get",
            contentType: "application/json",
            url: getUserNumUrl,
            cache: false,
            async: true,
            success: function(result) {
            	console.log('get user num: ' + result);
            	$("#currentUserLabel").html('Current there are still <font style="color:#F00">' + result + '</font> active users on the node');
            }, 
            error: function(result) {
            }
        });
    });
    
    $.ajax({
        type: "get",
        contentType: "application/json",
        url: getUpTaskUrl,
        cache: false,
        async: true,
        success: function(result) {
        	console.log('get up task: ' + result);
        	var total = result.total;
        	if (total == 0) {
        		// the thing it is
        		$("#upgradeSubmit").jqxButton({ disabled: false});
        		$("#upgradeCancel").jqxButton({ disabled: true});
        	} else {
        		// set information
        		var data = result.data;
        		var task = data[0];
        		$("#upgradeSubmit").jqxButton({ disabled: true});
        		//task.swName;
        		$("#newSwDropDown").jqxComboBox('selectItem', task.swName); 
        		$('#upScheduleTime').jqxDateTimeInput('val', new Date(task.scheduleTime));
                if (task.thresholdType == "1") {
                	$('#upradio1').jqxRadioButton('check');
                } else if (task.thresholdType == "2") {
                	$('#upradio2').jqxRadioButton('check');
                }
                $('#scriptConsumedMin').val(task.timeConsumed);
            	$('#userRemained').val(task.userRemain);
        		
        	}
        }, 
        error: function(result) {
        }
    });
    
    $.ajax({
        type: "get",
        contentType: "application/json",
        url: getUserNumUrl,
        cache: false,
        async: true,
        success: function(result) {
        	console.log('get up task: ' + result);
        	$("#currentUserLabel").html('Current there are still <font style="color:#F00">' + result + '</font> active users on the node');
        }, 
        error: function(result) {
        }
    });
    $("#jqxProgressBarUpgStatus").jqxProgressBar({ theme:'darkblue',width: 280, height: 12, value: 100});
    $("#jqxProgressBarUpgStatusSpan").html("100%");
});

// mme topo
angular.module('myApp').controller('MyCtrl6',
function($scope) {

    clearAllTask();
    var vns = [],hostMap=[],
    hosts = [];

    $.ajax({
        data: "get",
        url: mmetopoQueryUrl,
        cache: false,
        async: false,
        success: function(result) {
            var vns_t = result.vnlist;
            for (var i = 0; i < vns_t.length; i++) {
            	var oneVn = vns_t[i];
            	var vn = {vnname:oneVn.name};
            	vns.push(vn);
            }
            var vmlist = result.vmlist;
            for (var i = 0; i < vmlist.length; i++) {
            	var oneVm = vmlist[i];
            	var hostName = oneVm.hostName;
            	var vmName = oneVm.name;
            	var vmVnicsLst = oneVm.vmVnics;
            	var oneHost = hostMap[hostName];
            	if (oneHost == undefined || oneHost == null) {
            		oneHost = {
            				hostname:hostName,
            				vms:[]
            		};
            		hostMap[hostName] = oneHost;
            	}
            	var vm = {
            			vmname:vmName,
            			belongto:[]
            	};
            	for (var j = 0; j < vmVnicsLst.length; j++) {
            		var vnName = vmVnicsLst[j].vn.name;
            		var oneBlto = {vnname:vnName};
            		vm.belongto.push(oneBlto);
            	}
            	oneHost.vms.push(vm);
            }
            // change host map to list
            for (var key in hostMap) {
            	if (key == "del") {
            		continue;
            	}
            	hosts.push({
            		hostname: key,
                    vms: hostMap[key].vms
            	});
            }
//            console.log("vnvnvnvnvnv: " + vns);
//            console.log("hostshostshostshosts: " + hosts);
        }
    });
    
    var canvas = document.getElementById('canvas');
    var stage = new JTopo.Stage(canvas);
    showJTopoToobar(stage);
    var scene = new JTopo.Scene();
    scene.alpha = 1;
    scene.backgroundColor = "210,234,255";
    var getContainerCount = 1;
    //scene.background = './images/bg.jpg';

    function node(x, y, img, name) {
        var node = new JTopo.Node(name);
        node.setImage('./images/' + img, true);
        node.setLocation(x, y);
        node.fontColor = "0,0,0";
        //node.font = "22px Consolas";
        scene.add(node);
        return node;
    }

    function linkNodeStra(nodeA, nodeZ) {
        var link = new JTopo.Link(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '150,150,150';
        scene.add(link);
        return link;
    }

    function linkNode(nodeA, nodeZ) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '150,0,0';
        scene.add(link);
        return link;
    }

    function linkNodeVmAndVn(nodeA, nodeZ) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 5;
        link.strokeColor = '80,194,231';
        scene.add(link);
        return link;
    }
    
    function linkNodeDash(nodeA, nodeZ, name) {
        var link = new JTopo.FoldLink(nodeA, nodeZ);
        link.lineWidth = 4;
        link.strokeColor = '200,200,200';
        link.text = name;
        scene.add(link);
        return link;
    }

    function getContainer(name) {
        var container = new JTopo.Container(name);
        container.textPosition = 'Bottom_Center';
        container.fontColor = '0,0,0';
        container.fillColor = '161,230,192';
        container.font = '18pt Bold';
        container.borderColor = '255,0,0';
        container.borderRadius = 30; // 圆角
        scene.add(container);
        return container;
    }

    function linkVmAndVn(vm, vmX, vnY) {
        var linkN = node(vmX, vnY, 'lineNode.png');
        linkNodeVmAndVn(vm, linkN);
    }
    var elmOffset = $("#headtitleTP").offset();
    var fatherTop = elmOffset.top;
    var fatherLeft = elmOffset.left;
    var currentNode = null;

    var startVN_X = 50;
    var startVN_Y = 50;
    var endVN_X = 750;
    var endVN_Y = 50;
    var step_VN_Y = 30;

    var startVM_X = 50;
    var stepVM_X = 70;
    var stepVMhost_X = 20;

    var VM_Y_down = 400;
    var VM_Y_up = 230;

    var step_X_between_vmLink = 10;
    var firstLinkOffet = 10;

    function getVnY(vnName) {
        for (var i = 0; i < vns.length; i++) {
            var vn = vns[i];
            if (vn.vnname == vnName) {
                return startVN_Y + step_VN_Y * i
            }
        }
        return - 1;
    }

    var xMove = startVM_X;
    // drow Host
    for (var i = 0; i < hosts.length; i++) {
        var hostn = hosts[i];
        var vms = hostn.vms;
        var hostt;
        hostt = getContainer(hostn.hostname);
        // drow VM
        for (var j = 0; j < vms.length; j++) {
            var vmn = vms[j];
            var yy = -1;
            if (j % 2 == 0) {
                yy = VM_Y_down;
            } else {
                yy = VM_Y_up;
            }
            var vmnNameTmp = vmn.vmname;
            var vmt = node(xMove, yy, 'cscftoMme.PNG', vmnNameTmp);
            vmt.addEventListener('mouseup',
            function(event) {
                currentNode = this;
            });
            // link vm and vn
            var belVn = vmn.belongto;
            for (var k = 0; k < belVn.length; k++) {
                var linkVN = belVn[k];
                var linkVnName = linkVN.vnname;
                var vny = getVnY(linkVnName);
                if (vny == -1) {
                	continue;
                }
                linkVmAndVn(vmt, firstLinkOffet + xMove + step_X_between_vmLink * k, vny);
            }
            hostt.add(vmt);
            xMove += stepVM_X;
        }
        if (vms.length == 1) {
        	// drow a extra element
        	var vmtDown = node(xMove - 65, VM_Y_down + 90, 'extraNode.png', "");
        	var vmt = node(xMove + 30, VM_Y_up, 'extraNode.png', "");
        	xMove += stepVM_X;
        	hostt.add(vmtDown);
        	hostt.add(vmt);
        }
        xMove += stepVMhost_X;
    }
    
    // drow VN
    endVN_X = xMove; 
    for (var i = 0; i < vns.length; i++) {
        var vn = vns[i];
        var leftn = node(startVN_X, startVN_Y + step_VN_Y * i, 'lineNode.png');
        var rightn = node(endVN_X, endVN_Y + step_VN_Y * i, 'lineNode.png', vn.vnname);
        linkNode(leftn, rightn);
    }
    
    stage.add(scene);
    function centerAndZ() {
    	stage.centerAndZoom();
    	if (endVN_X > 870) { 
    		stage.zoomIn();
    	}
    }
    setTimeout(centerAndZ, 200);

});