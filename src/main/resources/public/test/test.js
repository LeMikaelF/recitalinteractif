/**
 * Created by Mikaël on 2017-10-10.
 */

var network;

var nodes = new vis.DataSet([
    {id: 1, label: 'Node 1'},
    {id: 2, label: 'Node 2'},
    {id: 3, label: 'Node 3'},
    {id: 4, label: 'Node 4'},
    {id: 5, label: 'Node 5'}
]);

var edges = new vis.DataSet([
    {from: 1, to: 3},
    {from: 1, to: 2},
    {from: 2, to: 4},
    {from: 2, to: 5}
]);

var allNodes;

window.onload = function () {
    console.log('initialisation');
    network = new vis.Network(document.getElementById('network'), {'nodes': nodes, 'edges': edges}, {});
    allNodes = nodes.get({returnType: 'Object'});
    var timelineId = setInterval(timeLine, 1000);
};

var stop = false;
var currentIndex = 1;
function timeLine(){
    allNodes[currentIndex].color = 'red';
    console.log(JSON.stringify(allNodes[currentIndex]));
    nodes.update(allNodes[currentIndex++]);
}