/**
 * Created by Mikaël on 2017-10-06.
 */
var commonOptions = {
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
};

var options = {
    nodes: {
        borderWidthSelected: 1,
        font: {
            color: 'blue',
            size: 16,
            strokeColor: 'blue'
        },
        scaling: {
            min: 8,
            max: 12
        },
        shape: 'box',
        size: 22
    },
    edges: {
        arrows: {
            to: {
                enabled: true,
                scaleFactor: 1.4
            }
        },
        scaling: {
            min: 13,
            label: false
        },
        smooth: false
    },
    interaction: {
        navigationButtons: true
    },
    physics: {
        enabled: false,
        forceAtlas2Based: {
            springLength: 295,
            avoidOverlap: 0.56
        },
        minVelocity: 0.75,
        solver: 'forceAtlas2Based'
    }
};
