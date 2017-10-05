/**
 * Created by Mikaël on 2017-10-04.
 */

/*var nodes;
 var edges;*/
var network;


//For testing only
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


var receiveJson = function (json) {
    var graph = JSON.parse(json);
    nodes = new vis.DataSet(graph.get('nodes'));
    var edges = new vis.DataSet(graph.get('edges'));
};
var initGraph = function () {
    //TODO Set node 1 as 'chosen'
    //Ex: {id: 1, label: 'one', chosen:true}
    var container = document.getElementById('network');
    var data = {'nodes': nodes, 'edges': edges};
    var options = {
            nodes: {
                borderWidth: 4,
                borderWidthSelected: 4,
                size: 20,
                shape: 'dot',
                color: {
                    border: '#222222',
                    background: '#f1ea2c',
                    highlight: {
                        border: '#f1ea2c',
                        background: '#666666'
                    }
                },
                shadow: {
                    enabled: true,
                    color: '#f1ea2c',
                    size: 20,
                    x: 0,
                    y: 0
                },
                font: {
                    color: '#eeeeee'
                }
            },
            edges: {
                color: 'lightgray',
                arrows: {
                    to: {
                        enabled: true
                    }
                },
                smooth: true,
                width: 2
            }
        }
    ;
    network = new vis.Network(container, data, options);
};

var fitGraph = function () {
    network.fit();
};

window.onload = function () {
    initGraph();
}
