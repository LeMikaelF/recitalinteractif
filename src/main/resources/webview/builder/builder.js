/**
 * Created by Mikaël on 2017-10-05.
 */


// create an array with nodes
var nodes = [
    {id: 1, label: 'Node 1'},
    {id: 2, label: 'Node 2'},
    {id: 3, label: 'Node 3'},
    {id: 4, label: 'Node 4'},
    {id: 5, label: 'Node 5'}
];

// create an array with edges
var edges = [
    {from: 1, to: 3},
    {from: 1, to: 2},
    {from: 2, to: 4},
    {from: 2, to: 5}
];

var network;
var container;

var initGraphWithJson = function (json) {
    json = JSON.parse(json);
    console.log('json reçu de javaFX: ' + JSON.stringify(json));
    network = new vis.Network(container, json, options);
};

var fitGraph = function () {
};

var options = {
    interaction: {
        navigationButtons: true,
        keyboard: true
    },
    manipulation: {
        initiallyActive: true,
        addNode: false,
        addEdge: function (data, callback) {
            addEdge(data, callback)
        },
        deleteNode: false,
        deleteEdge: function (data, callback) {
            deleteEdge(data, callback);
        }
    }
};


//TODO le graphe doit être chargé seulement à la réception du json. Présentement, c'est un json de test, à ne pas garder.
window.onload = function () {
    container = document.getElementById('network');
    initGraphWithJson({"nodes": nodes, "edges": edges});

    console.log('initialized!')
};

//TODO Activer les touches de navigation
//http://visjs.org/examples/network/other/navigation.html
//TODO Sauvegarder le graphe en json
//http://visjs.org/examples/network/other/saveAndLoad.html

function addEdge(data, callback) {
    var r = confirm('Voulez-vous ajouter un lien du texton ' + data.from + ' au texton ' + data.to + '?');
    if (!r) {
        callback(null);
        return;
    }
    callback(data);
    updateGraph();
}

function deleteEdge(data, callback) {
    var from = network.getConnectedNodes(data.edges)[0];
    var to = network.getConnectedNodes(data.edges)[1];
    var r = confirm('Voulez-vous supprimer le lien du texton ' + from + ' vers le texton ' + to + '?');
    if (!r) {
        callback(null);
        return;
    }
    callback(data);
    updateGraph();
}

function pushChanges(json) {
    javaFX.receiveGraphJavaFX(json);
}

function updateGraph() {
    exportGraph();
}

//TODO Ajouter citation
//Ces trois fonctions sont tirées de https://github.com/almende/vis/blob/master/examples/network/other/saveAndLoad.html


function exportGraph() {
    var nodes = objectToArray(network.getPositions());
    nodes.forEach(addConnections);
    var exportValue = JSON.stringify(nodes, undefined, 4);
    //Cette ligne ne provient pas de la citation.
    pushChanges(exportValue);
}
function objectToArray(obj) {
    return Object.keys(obj).map(function (key) {
        obj[key].id = key;
        return obj[key];
    });
}
function addConnections(elem, index) {
    elem.connections = network.getConnectedNodes(index);
}