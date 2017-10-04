package util;

import textonclasses.Graph;
import textonclasses.Texton;

/**
 * Created by Mikaël on 2016-12-04.
 */
interface TCWithTexton {
    Texton getTexton();

    void setGraph(Graph graph);

    void setTexton(Texton texton);
}
