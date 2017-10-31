package builder.plugins;

import javafx.util.Pair;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LtoPlugin implements StatisticsPlugin {

    private Graph graph;
    private int start;
    private int limit;
    private Map<TextonHeader, Double> statMap = new HashMap<>();

    //Build prompts.
    private Function<String, Boolean> requireFirstTexton = s -> {
        //First prompt: ask for first texton. Fail if not a valid texton number or not a number.
        try {
            int num = Integer.parseInt(s);
            if (graph.getTextonHeader(num) == null) return false;

            start = num;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    };
    private String requireFirstTextonString = "Le nom du premier texton (à utiliser comme point de départ pour l'analyse)";

    private Function<String, Boolean> requireLimit = s -> {

        try {
            int limit = Integer.parseInt(s);
            if (limit > 100) return false;
            this.limit = limit;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    };

    private String requireLimitString = "Le nombre de textons maximal à utiliser dans un parcours";

    private Pair<Function<String, Boolean>, String> pair1 = new Pair<>(requireFirstTexton, requireFirstTextonString);
    private Pair<Function<String, Boolean>, String> pair2 = new Pair<>(requireLimit, requireLimitString);
    private List<Pair<Function<String, Boolean>, String>> prompts = Stream.of(pair1, pair2).collect(Collectors.toList());

    //Adapted from Gephi plugin
    private void setLto(int limit, TextonHeader start, List<TextonHeader> list) {
        List<TextonHeader> path = new ArrayList<>(list);
        path.add(start);

        //If path length limit has been reached
        if (limit == 0 || graph.getChildren(start.getNumTexton()).size() == 0) {
            ListIterator<TextonHeader> it = path.listIterator();
            while (it.hasNext()) {
                TextonHeader textonHeader = it.next();
                TextonHeader neighbour = null;

                //Current interface does not allow computing of edge statistics, but it could be easily implemented.
                //(see old Gephi plugin)
                double freq = 0;
                statMap.compute(textonHeader, (textonHeader1, aDouble) -> aDouble == null ? 0 : aDouble + 1);
            }
            return;
        }

        for (TextonHeader child : graph.getTextonHeaderChildren(start.getNumTexton())) {
            //Test that edge has not already been visited.
            if (path.contains(child) && path.get((path.indexOf(child) - 1)).equals(start))
                continue;
            //Recursion
            setLto(limit - 1, child, path);
        }
    }

    @Override
    public Map<TextonHeader, Double> compute() {
        setLto(limit, graph.getTextonHeader(start), new ArrayList<>());
        return statMap;
    }

    @Override
    public String getName() {
        return "Fréqence d'occurence (limité)";
    }

    @Override
    public String getResultName() {
        return "Occurence (%)";
    }

    @Override
    public String getResultDescription() {
        return "Ce plugin calcule le pourcentage d'occurence d'un texton par rapport à un " +
                "chemin limité (en nombre de textons), en commençant par un texton spécifié.";
    }

    @Override
    public void init(Graph graph) {
        this.graph = graph;
    }

    @Override
    public List<Pair<Function<String, Boolean>, String>> getPrompts() {
        return prompts;
    }
}
