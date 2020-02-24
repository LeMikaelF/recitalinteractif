//Attention: j'ai utilisé la propriété «sélectionné» pour simuler les textons déjà parcourus.
//Dans l'implémentation réelle, les noeuds sélectionnés devront être pareils aux autres,
//mais ceux qui ont déjà été parcourus sont représentés dans ce jeu d'options par l'option sélectionné.
//Pour appliquer les options aux noeuds parcourus: utiliser Object.assign sur les noeuds individuels.

var optionsNastasia = {
    "nodes": {
        "borderWidthSelected": 1,
        "color": {
            "border": "rgba(127,105,16,1)",
            "background": "rgba(200,219,106,1)",
            "highlight": {
                "border": "rgba(127,105,16,1)",
                "background": "rgba(200,219,106,1)"
            }
        },
        "font": {
            "color": "rgba(15,1,5,1)",
            "size": 16,
            "strokeColor": "blue"
        },
        "labelHighlightBold": false,
        "scaling": {
            "min": 8,
            "max": 12
        },
        "shadow": {
            "enabled": true
        },
        "shape": "box",
        "size": 22
    },
    "edges": {
        "arrows": {
            "to": {
                "enabled": true,
                "scaleFactor": 1.4
            }
        },
        "color": {
            "color": "rgba(132,72,26,1)",
            "inherit": false
        },
        "scaling": {
            "label": {
                "min": 13
            }
        },
        "width": 1.5,
        "smooth": false
    },
    "interaction": {
        "navigationButtons": true
    },
    "physics": {
        "forceAtlas2Based": {
            "avoidOverlap": 1,
            "springConstant": 0.02,
            "springLength": 1000
        },
        "minVelocity": 0.75,
        "solver": "forceAtlas2Based"
    }
};

var pathOptions = {
    "nodes": {
        "color": {
            "border": "rgba(224,167,29,1)",
            "background": "rgba(255,253,97,1)"
        }
    },
    "edges": {
        "color": "rgba(255,199,20,1)",
        "width": 1.8,
        //"width": 1.3,
        "shadow": {
            "color": "rgba(255,199,20,1)",
            "x": 0,
            "y": 0
        }
    }
};

function setPhysics(bool) {
    if (bool) {
        //network.startSimulation();
        network.setOptions({
            "physics": {
                "enabled": true
            }
        });
    } else {
        //network.stopSimulation();
        network.setOptions({
            "physics": {
                "enabled": false
            }
        })
    }
}