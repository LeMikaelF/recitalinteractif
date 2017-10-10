package presentation;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:02
 */

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.io.IOException;
import java.net.URISyntaxException;

public class TabBordContrImplProvider implements Provider<TabBordContrImpl> {

    @Inject
    private TabBordContrImpl tabBordContrImpl;

    private static TabBordContrImpl instance;

    public TabBordContrImpl get() {
        System.out.println("Ceci est TabBordContrImplProvider");
        return tabBordContrImpl;
    }
}
