package builder.plugins;

import javafx.util.Pair;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Mikaël on 2017-10-07.
 */
public interface StatisticsPlugin {

    Map<TextonHeader, Double> compute();
    String getName();
    String getResultName();
    String getResultDescription();
    void init(Graph graph);
    //Function returns false on failure.
    List<Pair<Function<String, Boolean>, String>> getPrompts();

}
