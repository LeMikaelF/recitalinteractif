package textonIO;

import textonclasses.Texton;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Mikaël on 2017-01-10.
 */
public interface TextonReaderWriter {
    Texton readTexton(int numTexton) throws SQLException, IOException;
    void writeTexton(Texton texton, boolean setNextAvailNumTexton) throws SQLException, IOException;
    //Returned list wraps a source list and a dest list.
    List<List<Integer>> getHangingLinks() throws SQLException;
    String getTextonName(int numTexton) throws SQLException;
    Map<Integer,String> getTextonNumsToNamesMap(List<Integer> numTextons) throws SQLException;
}
