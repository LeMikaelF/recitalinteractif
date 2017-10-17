//Ceci sont les options appliquées au graphique avec Nastasia.
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
        "shadow": {
            "enabled": true,
            "size": 11,
            "x": 0,
            "y": 0
        },
        "smooth": false
    },
    "interaction": {
        "navigationButtons": true
    },
    "physics": {
        "enabled": true,
        "forceAtlas2Based": {
            "springLength": 295,
            "avoidOverlap": 0.56
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
        "width": 1.3,
        "shadow": {
            "color": "rgba(255,199,20,1)",
            "x": 0,
            "y": 0
        }
    }
};