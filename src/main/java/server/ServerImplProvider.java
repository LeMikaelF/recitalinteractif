package server;/*
 * Created by IntelliJ IDEA.
 * User: Mikaël
 * Date: 2017-10-02
 * Time: 00:12
 */
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ServerImplProvider implements Provider<ServerImpl> {
    @Inject
    private Provider<ServerRunnable> provider;

    public ServerImpl get() {
return new ServerImpl(provider);
    }
}
