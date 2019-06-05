package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mikaël on 2017-09-28.
 */
//TODO Add server port number to properties
//TODO Allow separate properties file after packaging (there is currently one, but it is not easily accessible)
public class PropLoader {
    private static Map<String, String> properties;

    static {
        try (InputStream is = PropLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(is);
            properties = (Map) prop;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getMap(){
        return properties;
    }
}

