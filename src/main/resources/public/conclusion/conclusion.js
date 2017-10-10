/**
 * Created by Mikaël on 2017-10-04.
 */

/*var nodes;
 var edges;*/
var network;

//TODO recevoir le trajet parcouru par le récital et permettre de surligner les noeuds, un par un, en déroulant le parcours.
//Changer le label du bouton «terminé» du tableau de bord et l'utiliser pour passer au prochain noeud surligné.


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
