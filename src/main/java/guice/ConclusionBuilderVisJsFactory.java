package guice;

import com.google.inject.Inject;
import presentation.ConclusionBuilder;
import textonclasses.Graph;
import textonclasses.TextonHeader;

import java.util.List;

/**
 * Created by Mikaël on 2017-10-21.
 */
public interface ConclusionBuilderVisJsFactory {
    @Inject
    ConclusionBuilder create(List<TextonHeader> path, Graph graph);
}
