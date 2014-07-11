
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Andrey on 7/10/2014.
 */
public class VKDownloader {
    public static final String ACCESS_TOKEN = "access_token";
    private static final String APP_ID = "app_id";
    private static final String PROP_NAME = "config.prop";
    public static final String HOST = "api.vk.com";
    private static final String GET_ALBUMS_PATH = "/method/photos.getAlbums";
    public static final String IS_APP_USER = "/method/isAppUser";
    public static final String GET_USER_SETTINGS = "/method/getUserSettings";
    private static final String UID = "uid";
    public static final String RESPONSE = "response";
    public static final String AID = "aid";
    public static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    public static final String PHOTOS_GET = "/method/photos.get";
    public static final String PHOTO_SIZES = "photo_sizes";
    public static final String EXTENDED = "extended";
    public static final String TEXT = "text";
    public static final String LIKES = "likes";
    public static final String COUNT = "count";
    public static final String CREATED = "created";
    private Properties properties;
    private String accessToken;
    private String appId;
    private String uid;
    private ArrayList<Album> albums;

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
        uid = (String) properties.get(UID);
    }

    public void getAlbums(String paramUid) {
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https").setHost(HOST).setPath(GET_ALBUMS_PATH)
                    .setParameter("uid", (paramUid == null ? uid : paramUid))
                    .setParameter(ACCESS_TOKEN, accessToken);

            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            String result = makeRequest(httpGet);

            System.out.println("getAlbums" + result);

            parseAlbums(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseAlbums(String result) {
        albums = new ArrayList<Album>();
        Map root = new Gson().fromJson(result, Map.class);
        ArrayList<Map<String, Object>> albumsMaps = (ArrayList<Map<String, Object>>) root.get(RESPONSE);
        for (Map albumMap : albumsMaps) {
            Double aid = (Double) albumMap.get(AID);
            long aidLong = aid.longValue();

            Album album = new Album();
            album.setAid(aidLong);
            album.setTitle((String) albumMap.get(TITLE));
            album.setDescription((String) albumMap.get(DESCRIPTION));
            albums.add(album);
            System.out.println(album);
        }
    }

    public void simpleRequest(String path) {
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https").setHost(HOST).setPath(path)
                    .setParameter("uid", uid)
                    .setParameter(ACCESS_TOKEN, accessToken);

            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            System.out.println(path + " " + makeRequest(httpGet));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String makeRequest(HttpGet httpGet) {
        InputStream inputStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                inputStream = entity.getContent();
                String resposneAsString = null;
                resposneAsString = IOUtils.toString(inputStream);
                return resposneAsString;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "nothing";
    }

    public void getAlbumsPhotos() {
        for (Album album : albums) {
            try {
                URIBuilder builder = new URIBuilder();
                builder.setScheme("https").setHost(HOST).setPath(PHOTOS_GET)
                        .setParameter(UID, uid)
                        .setParameter(AID, album.getAid() + "")
                        .setParameter(EXTENDED, "1")
                        .setParameter(PHOTO_SIZES, "0")
                        .setParameter(ACCESS_TOKEN, accessToken);

                URI uri = builder.build();
                HttpGet httpGet = new HttpGet(uri);
                String result = makeRequest(httpGet);


                parseAndSaveToAlbum(result, album);
                //break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseAndSaveToAlbum(String result, Album album) {
        Map root = new Gson().fromJson(result, Map.class);
        ArrayList<Map<String, Object>> photosMaps = (ArrayList<Map<String, Object>>) root.get(RESPONSE);
        for (Map photoMap : photosMaps) {

            Photo photo = new Photo();
            photo.setText((String) photoMap.get(TEXT));
            String src = null;
            String[] keys = {"src_xxxbig", "src_xxbig", "src_xbig", "src_big", "src"};
            for (String key : keys) {
                src = (String) photoMap.get(key);
                if (src != null && src != "null") {
                    break;
                }
            }
            photo.setSrc(src);
            photo.setLikes(((Double) ((Map) (photoMap.get(LIKES))).get(COUNT)).intValue());
            photo.setCreated(new Date(((Double) photoMap.get(CREATED)).longValue()));
            photo.setAlbum(album);
            album.addPhoto(photo);
            System.out.println(photo);
        }

    }

    public void saveAlbumsPhotos() {
        for (Album album : albums) {
            File folder = new File("albums/" + album.getAid() + "_" + album.getTitle());
            boolean success = folder.mkdirs();
            System.out.println("created folder: " + folder.getAbsolutePath() + " " + success);

            for (Photo photo : album.getPhotos()) {
                File dest = new File(folder.getAbsolutePath() + "/" + photo.getFileName());
                if (!dest.exists()) {
                    try {
                        FileUtils.copyURLToFile(new URL(photo.getSrc()), dest);
                        System.out.println(photo.getFileName() + " done");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(photo.getFileName() + " already exists");
                }
            }
        }

    }
}
