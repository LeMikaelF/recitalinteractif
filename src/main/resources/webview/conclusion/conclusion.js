/**
 * Created by Mikaël on 2017-10-04.
 */

/*var nodes;
 var edges;*/
var network;




var receiveJson = function (json) {
    var graph = JSON.parse(json);
    nodes = new vis.DataSet(graph.get('nodes'));
    var edges = new vis.DataSet(graph.get('edges'));
};



var fitGraph = function () {
    network.fit();
};

window.onload = function () {
    network = getAndInitGraph(document.getElementById('network'), {'nodes': nodes, 'edges': edges});
};
