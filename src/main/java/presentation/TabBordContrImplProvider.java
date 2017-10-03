package presentation;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:02
 */

import java.io.IOException;
import com.google.inject.Provider;

import java.io.IOException;
import java.net.URISyntaxException;

public class TabBordContrImplProvider implements Provider<TabBordContrImpl> {
    public TabBordContrImpl get() {
        try {
            return new TabBordContrImpl();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
