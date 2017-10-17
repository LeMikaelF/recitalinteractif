/**
 * Created by Mikaël on 2017-10-05.
 */

var network;
var container;
var json;

//TODO Mettre en forme le graphe avec les mêmes options que la conclusion.
//Cette méthode est le seul point de communication Java -> javascript
var initGraphWithJson = function (json) {
    json = JSON.parse(json);
    nodes = new vis.DataSet(json.nodes);
    edges = new vis.DataSet(json.edges);
    nodes.on('*', dataSetChangeHandler);
    edges.on('*', dataSetChangeHandler);
    if (typeof network === 'undefined' || network === null)
        network = new vis.Network(container, {'nodes': nodes, 'edges': edges}, Object.assign(options, optionsNastasia));
    else network.setData({'nodes': nodes, 'edges': edges});
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


window.onload = function () {
    container = document.getElementById('network');
};

function addEdge(data, callback) {
    var children = network.getConnectedNodes(data.from, 'to');
    if (children.size <= 4) {
        alert('Un texton ne peut pas posséder plus de quatre liens sortants.')
        return;
    }
    if (children.some(function (p1, p2, p3) {
            return p1 === data.to
        })) {
        alert('Vous ne pouvez pas ajouter un lien en double.');
        return;
    }


    var r = confirm('Voulez-vous ajouter un lien du texton ' + data.from + ' au texton ' + data.to + '?');
    if (!r) {

        callback(null);
        return;
    }
    callback(data);
    pushChanges();
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
    pushChanges();
}

function pushChanges() {
    var edgeArray = edges.get();
    edgeArray.forEach(function (edge) {
        delete edge.id;
    });
    var push = JSON.stringify({'nodes': nodes.get(), 'edges': edgeArray});

    javaFX.receiveGraphJavaFX(push);
}

function dataSetChangeHandler(event, properties, senderId) {
    pushChanges();
}