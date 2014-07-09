import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Andrey on 7/10/2014.
 */
public class VKDownloader {
    public static final String ACCESS_TOKEN = "access_token";
    private static final String APP_ID = "app_id";
    private static final String PROP_NAME = "config.prop";
    private Properties properties;
    private String accessToken;
    private String appId;

    public void init() {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();
        try {
            properties.load(new FileReader(PROP_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
        accessToken = (String) properties.get(ACCESS_TOKEN);
        appId = (String) properties.get(APP_ID);
    }
}
