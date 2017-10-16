package builder.plugins;

import javafx.util.Pair;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-10-07.
 */

//TODO This plugin is broken.
    //Use code at: C:\Programmation\Plugin Gephi LTO\modules\LtoPlugin\src\main\java
public class LtoPlugin implements StatisticsPlugin {

    private final String NAME = "Fréqence d'occurence (limité)";
    private final String RESULT_NAME = "Occurence (%)";
    private final String DESCRIPTION = "Ce plugin calcule le pourcentage d'occurence d'un texton par rapport à un chemin limité (en nombre de textons), en commençant par un texton spécifié.";

    private int start;
    private int limit;

    private Graph graph;
    private Map<TextonHeader, Double> statMap;

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

    //For testing only
    private int numPaths;
    private List<TextonHeader> resultList = new ArrayList<>();

    private List<TextonHeader> setLTO2(int limit, TextonHeader start, List<TextonHeader> path) {
        path = new ArrayList<>(path);

        long numberOfChildrensChildren = graph.getTextonHeaderChildren(start.getNumTexton())
                .stream().flatMap(textonHeader -> graph.getTextonHeaderChildren(textonHeader.getNumTexton()).stream()).count();

        if (limit == 0 || numberOfChildrensChildren == 0) {
            numPaths++;
            return path;
        } else path.add(start);

        for (TextonHeader textonHeader : graph.getTextonHeaderChildren(start.getNumTexton())) {
            path.addAll(setLTO2(limit - 1, textonHeader, path));
        }
        return path;
    }

    @Override
    public Map<TextonHeader, Double> compute() {
        List<TextonHeader> occurences = setLTO2(limit, graph.getTextonHeader(start), new ArrayList<>());
        System.out.println(numPaths);
        System.out.println(occurences);
        Map<TextonHeader, Double> map = occurences.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingDouble(value -> 1)));
        return map;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getResultName() {
        return null;
    }

    @Override
    public String getResultDescription() {
        return null;
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
