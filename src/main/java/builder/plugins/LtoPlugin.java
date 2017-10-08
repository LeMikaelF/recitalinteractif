package builder.plugins;

import javafx.util.Pair;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Mikaël on 2017-10-07.
 */
public class LtoPlugin implements StatisticsPlugin {

    final private String NAME = "Fréqence d'occurence (limité)";
    final private String RESULT_NAME = "Occurence (%)";
    final private String DESCRIPTION = "Ce plugin calcule le pourcentage d'occurence d'un texton par rapport à un chemin limité (en nombre de textons), en commençant par un texton spécifié.";

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


    private void setLTO(int limit, TextonHeader start, List<TextonHeader> path) {
        path = new ArrayList<TextonHeader>(path);
        if (limit == 0) {
            for (TextonHeader textonHeader : path) {
                statMap.put(textonHeader, statMap.get(textonHeader) + 1);
                //return;
            }
        }

        path.add(start);

        for (TextonHeader child : graph.getTextonHeaderChildren(start.getNumTexton())) {
            setLTO(limit--, child, path);
        }
    }

    @Override
    public Map<TextonHeader, Double> apply(Graph graph) {
        this.graph = graph;
        statMap = new HashMap<>();
        setLTO(limit, graph.getTextonHeader(start), new ArrayList<>());
        return statMap;
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
    public List<Pair<Function<String, Boolean>, String>> getPrompts() {
        return prompts;
    }
}
