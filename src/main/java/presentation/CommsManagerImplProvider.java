package presentation;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:03
 */

;import com.google.inject.Inject;
import com.google.inject.Provider;
import server.Server;

public class CommsManagerImplProvider implements Provider<CommsManagerImpl> {
    @Inject
    Provider<Server> provider;

    public CommsManagerImpl get() {
        return new CommsManagerImpl(provider);
    }
}
