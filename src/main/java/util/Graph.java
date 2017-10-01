package util;

import java.util.Map;

/**
 * Created by Mikaël on 2017-09-28.
 */
public class Graph {
    private String xmlGraph;

    public Graph(String xmlGraph) {
        this.xmlGraph = xmlGraph;
    }

    public String getJsonGraph(){
        //TODO Convertir le graphe XML au bon format Json pour la librairie désirée.
        return null;
    }

    public Map<Integer, Integer> getHangingLinks(){
        //TODO Compléter Graph.getHangingLinks();
        return null;
    }

    public String getName(int numTexton){
        //TODO Retourner le nom du texton à partir de son numéro.
        return null;
    }

    public int[] getChildren(int numTexton) {
        //TODO retourner les enfants de ce noeud.
        return null;
    }

}
