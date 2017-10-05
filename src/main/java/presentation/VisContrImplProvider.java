package presentation;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:01
 */

import com.google.inject.Inject;
import com.google.inject.Provider;

public class VisContrImplProvider implements Provider<VisContr> {

    @Inject
    private VisContrImpl visContrImpl;

    @Inject
    Provider<CommsManager> provider;

    public VisContr get() {
        System.out.println("Ceci est VisContrImplProvider");
        return visContrImpl;
    }
}
