package server;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;

/**
 * Created by Mikaël on 2017-10-09.
 */
public class WebjarDefaultServlet extends DefaultServlet {
    @Override
    public Resource getResource(String path) {
        Resource resource = Resource.newClassPathResource("META-INF/resources" + path);
        if(resource == null) resource = super.getResource(path);
        return resource;
    }
}
