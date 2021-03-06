/**
 * Created by Mikaël on 2017-10-04.
 */

var nodes;
var edges;
var network;
var container;
var path;
var nodeArray;
var edgeArray;
var options = {
    interaction: {
        keyboard: {
            enabled: true
        }
    },
    configure: {
        enabled: true,
        showButton: true
    }
};

function receiveJson(json) {
    var graph = JSON.parse(json);
    nodes = new vis.DataSet(graph.nodes);
    edges = new vis.DataSet(graph.edges);
    nodeArray = nodes.get({returnType: 'Object'});
    edgeArray = edges.get({returnType: 'Object'});
    network = new vis.Network(container, {'nodes': nodes, 'edges': edges}, Object.assign(optionsNastasia, options));
}


var fitGraph = function () {
    network.fit();
};

window.onload = function () {
    container = document.getElementById('network');
};

function receiveTextonPath(str) {
    //path is a TextonHeader array.
    path = JSON.parse(str);
}

var currentIndex = 0;
var lastNode;

function moveForwardInGraph() {
    if (currentIndex === path.length + 1)
        return;
    //Fit graph on last node
    if (currentIndex === path.length) {
        fitGraph();
        return;
    }
    var node = nodeArray[path[currentIndex].id];
    //Set edge looking back
    if (currentIndex > 0 && currentIndex < path.length) {
        Object.keys(edgeArray).forEach(function (key, index) {
            var edge = edgeArray[key];
            if (edge.from === lastNode.id && edge.to === node.id) {
                edge.color = pathOptions.edges.color;
                edges.update(edge);
            }
        });
    }

    if (currentIndex < path.length) {
        node.color = pathOptions.nodes.color;
        nodes.update(node);
    }

    //Prezi-stye animation, focus on each node
    var options = {scale: 1, animation: true};
    network.focus(node.id, options);



    currentIndex++;
    lastNode = node;

}
