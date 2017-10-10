package tests;

import builder.plugins.StatisticsPlugin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import javafx.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Mikaël on 2017-10-08.
 */
public class TestPlugins {

    private static Graph graph;

    @Inject
    StatisticsPlugin plugin;
    private static Injector guice;

    @BeforeClass
    public static void before() throws IOException {
        guice = Guice.createInjector(new TestGuiceModule());
        graph = new ObjectMapper().readValue(TestPlugins.class.getResource("/graph.json"), Graph.class);
    }

    @Test
    public void test1() throws JsonProcessingException {
        guice.injectMembers(this);
        plugin.init(graph);
        System.out.println(new ObjectMapper().writeValueAsString(graph));
        int[] promptValues = {1, 3};
        for (int i = 0; i < plugin.getPrompts().size(); i++) {
            plugin.getPrompts().get(i).getKey().apply(String.valueOf(promptValues[i]));
        }
        Map<TextonHeader, Double> result = plugin.apply(graph);
        System.out.println(result);
    }
}
