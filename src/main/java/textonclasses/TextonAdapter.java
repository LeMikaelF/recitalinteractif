package textonclasses;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by Mikaël on 2017-10-02.
 */
public class TextonAdapter extends XmlAdapter<AdaptedTexton, Texton> {

    @Override
    public Texton unmarshal(AdaptedTexton v) throws Exception {
        return new Texton(v.getNumTexton(), v.getSource(), v.getName(), v.getDescription(),
                v.getComment(), v.getImage());
    }

    @Override
    public AdaptedTexton marshal(Texton v) throws Exception {
        return new AdaptedTexton(v.getNumTexton(), v.getSource(), v.getName(), v.getDescription(),
                v.getComment(), v.getImage());
    }
}
