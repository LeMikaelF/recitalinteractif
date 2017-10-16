/**
 * Created by Mikaël on 2017-10-04.
 */

var nodes;
var edges;
var network;
var container;
var path;
var nodeArray;
var options = {
  interaction:{
      keyboard:{
          enabled: true
      }
  },
  configure:{
      enabled: true,
      showButton: true
  }
};
function receiveJson(json) {
    var graph = JSON.parse(json);
    nodes = new vis.DataSet(graph.nodes);
    edges = new vis.DataSet(graph.edges);
    nodeArray = nodes.get({returnType: 'Object'});
    network = new vis.Network(container, {'nodes': nodes, 'edges': edges}, Object.assign(options, commonOptions));
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
function moveForwardInGraph() {
    if(currentIndex >= path.length) return;
    var id = path[currentIndex++].id;
    var node = nodeArray[id];
    //TODO Trouver un beau style pour le réseau. Ensuite, trouver un beau style contrastant à appliquer manuellement aux noeuds concernés.
    //Idée: les noeuds concernés pourraient avoir une image.
    node.color = 'blue';
    nodes.update(node);
}