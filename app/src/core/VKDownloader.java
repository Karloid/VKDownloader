package core;

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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


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
    private static final String DESCRIPTION = "description";
    public static final String PHOTOS_GET = "/method/photos.get";
    public static final String AUDIO_GET = "/method/audio.get";
    public static final String PHOTO_SIZES = "photo_sizes";
    public static final String EXTENDED = "extended";
    public static final String TEXT = "text";
    public static final String LIKES = "likes";
    public static final String COUNT = "count";
    public static final String CREATED = "created";
    public static final String GID = "gid";
    private static final String OWNER_ID = "owner_id";
    public static final String TITLE = "title";
    private static final String ARTIST = "artist";
    public static final String ITEMS = "items";
    public static final String URL = "url";
    public static final int MINIMUM_TRACK_SIZE = 2000;
    private static final String PHOTOS_GET_PROFILE = "/method/photos.getAll";
    private static final String OFFSET = "offset";
    private static final long DELAY_BETWEEN_REQUESTS = 300;
    public static final int ERROR_CODE_TOO_MANY_REQUESTS = 6;
    private Properties properties;
    private String accessToken;
    private String appId;
    private String uid;
    private ArrayList<Album> albums;
    private List<Track> tracks;
    private int nThreads = 10;

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

    public void getAlbums(String paramUid, String paramGid) {
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https")
                    .setHost(HOST)
                    .setPath(GET_ALBUMS_PATH)
                    .setParameter(ACCESS_TOKEN, accessToken);
            if (paramUid != null) {
                builder.setParameter(UID, paramUid);
            } else {
                builder.setParameter(GID, paramGid);
            }
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            Map result = makeRequest(httpGet);
            System.out.println("getAlbums: " + uri.toString());
            System.out.println("getAlbums" + result);

            parseAlbums(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseAlbums(Map root) {
        albums = new ArrayList<>();
        ArrayList<Map<String, Object>> albumsMaps = (ArrayList<Map<String, Object>>) root.get(RESPONSE);
        if (albumsMaps == null) {
            System.out.println("WARN parseAlbums: empty albums");
            return;
        }
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

    private Map makeRequest(HttpGet httpGet) {
        InputStream inputStream = null;
        for (int i = 0; i < 15; i++) { // try some times
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    inputStream = entity.getContent();
                    String resposneAsString = null;
                    resposneAsString = IOUtils.toString(inputStream);
                    Map result = new Gson().fromJson(resposneAsString, Map.class);
                    Map error = (Map) result.get("error");
                    if (error == null || (Double) error.get("error_code") != ERROR_CODE_TOO_MANY_REQUESTS) {
                        return result;
                    } else {
                        System.out.println("* got too many requests error try sleep two seconds");
                        TimeUnit.SECONDS.sleep(2);
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    assert inputStream != null;
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null; //so bad :(
    }

    public void downloadAllPhotos(String paramUid, String paramGid, String folderToSave) {
        log("getAlbums...");
        getAlbums(paramUid, paramGid);
        log("getAlbumsPhotos...");
        getAlbumsPhotos(paramUid, paramGid);
        log("getOtherPhotos");
        getOtherPhotos(paramUid, paramGid);
        log("saveAlbumsPhotos...");
        saveAlbumsPhotos(folderToSave);
    }

    private void getOtherPhotos(String paramUid, String paramGid) {
        int currentIndex = 0;
        int maxIndex = 1000;
        int count = 99;
        Album profileAlbum = new Album();
        profileAlbum.setAid(0);
        profileAlbum.setTitle("Other");
        albums.add(profileAlbum);
        while (currentIndex < maxIndex) {
            try {
                URIBuilder builder = new URIBuilder();
                builder.setScheme("https").setHost(HOST).setPath(PHOTOS_GET_PROFILE)
                        .setParameter(OFFSET, currentIndex + "")
                        .setParameter(EXTENDED, "1")
                        .setParameter(COUNT, count + "")
                        //       .setParameter(PHOTO_SIZES, "0")
                        .setParameter(ACCESS_TOKEN, accessToken);
                if (paramUid != null) {
                    builder.setParameter(OWNER_ID, paramUid);
                } else {
                    builder.setParameter(OWNER_ID, "-" + paramGid);
                }
                URI uri = builder.build();
                HttpGet httpGet = new HttpGet(uri);
                Map result = makeRequest(httpGet);
                System.out.println(uri.toString());
                System.out.println(result);

                maxIndex = parseAndSaveToAlbum(result, profileAlbum, true);

                currentIndex += count;
                Thread.sleep(DELAY_BETWEEN_REQUESTS);
                //break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    public void getAlbumsPhotos(String paramUid, String paramGid) {
        for (Album album : albums) {
            try {
                URIBuilder builder = new URIBuilder();
                builder.setScheme("https")
                        .setHost(HOST)
                        .setPath(PHOTOS_GET)
                        .setParameter(AID, album.getAid() + "")
                        .setParameter(EXTENDED, "1")
                        .setParameter(PHOTO_SIZES, "0")
                        .setParameter(ACCESS_TOKEN, accessToken);
                if (paramUid != null) {
                    builder.setParameter(UID, paramUid);
                } else {
                    builder.setParameter(GID, paramGid);
                }
                URI uri = builder.build();
                HttpGet httpGet = new HttpGet(uri);
                Map result = makeRequest(httpGet);

                System.out.println(result);
                parseAndSaveToAlbum(result, album, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int parseAndSaveToAlbum(Map root, Album album, boolean firstElementCount) {
        int count = 0;
        ArrayList<Object> photosMapsTmp = (ArrayList<Object>) root.get(RESPONSE);
        if (firstElementCount) {
            count = ((Double) photosMapsTmp.get(0)).intValue();
            photosMapsTmp.remove(0);
        }

        ArrayList<Map<String, Object>> photosMaps = new ArrayList<>();
        for (Object obj : photosMapsTmp) {
            System.out.println("obj : " + obj);
            photosMaps.add((Map<String, Object>) obj);
        }

        // ArrayList<Map<String, Object>> photosMaps = (ArrayList<Map<String, Object>>) root.get(RESPONSE);
        for (Map photoMap : photosMaps) {

            Photo photo = new Photo();
            photo.setText((String) photoMap.get(TEXT));
            String src = null;
            String[] keys = {"src_xxxbig", "src_xxbig", "src_xbig", "src_big", "src"};
            for (String key : keys) {
                src = (String) photoMap.get(key);
                if (src != null && !src.equals("null")) {
                    break;
                }
            }
            photo.setSrc(src);
            photo.setLikes(((Double) ((Map) (photoMap.get(LIKES))).get(COUNT)).intValue());
            photo.setCreated(new Date(((Double) photoMap.get(CREATED)).longValue() * 1000));
            photo.setAlbum(album);
            if (!photoContainsInAlbums(photo)) {
                album.addPhoto(photo);
                System.out.println(photo);
            }
        }
        return count;

    }

    private boolean photoContainsInAlbums(Photo photo) {
        for (Album album : albums) {
            for (Photo photoForEach : album.getPhotos()) {
                if (photo.equals(photoForEach)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveAlbumsPhotos(String folderToSave) {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        for (Album album : albums) {
            File folder = new File(folderToSave + album.getAid() + "_" + fixWindowsFileName(album.getTitle()));
            boolean success = folder.mkdirs();
            System.out.println("created folder: " + folder.getAbsolutePath() + " " + success);
            for (Photo photo : album.getPhotos()) {
                File dest = new File(folder.getAbsolutePath() + "/" + photo.getFileName());

                if (!dest.exists() || dest.length() < 100) {
                    executor.execute(new Downloader(photo.getSrc(), dest));

                } else {
                    System.out.println(dest + " already exists");
                }
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All photos try to download.");

    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void getTracks(String paramUid, String paramGid) {
        try {
            URIBuilder builder = new URIBuilder();
            builder.setScheme("https").setHost(HOST).setPath(AUDIO_GET)
                    .setParameter(ACCESS_TOKEN, accessToken)
                    .setParameter("v", "5.23");
            if (paramUid != null) {
                builder.setParameter(OWNER_ID, paramUid);
            } else {
                builder.setParameter(OWNER_ID, "-" + paramGid);
            }
            URI uri = builder.build();
            HttpGet httpGet = new HttpGet(uri);
            Map result = makeRequest(httpGet);

            System.out.println("getAudio : " + result);

            parseTracks(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseTracks(Map root) {
        tracks = new ArrayList<>();
        //noinspection unchecked
        ArrayList<Map<String, Object>> audioMaps = (ArrayList<Map<String, Object>>)
                ((Map<String, Object>) root.get(RESPONSE)).get(ITEMS);
        for (Map audioMap : audioMaps) {

            Track track = new Track();
            track.setArtist((String) audioMap.get(ARTIST));
            track.setTitle((String) audioMap.get(TITLE));
            track.setUrl((String) audioMap.get(URL));
            tracks.add(track);
        }
    }

    public void saveTracksMultithreading(String folderToSave) {
        File folder = new File(folderToSave);
        boolean success = folder.mkdirs();
        System.out.println("created folder: " + folder.getAbsolutePath() + " " + (success ? "created" : ""));
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);


        for (Track track : tracks) {
            String fileName = fixWindowsFileName(track.getFileName()) + ".mp3";
            File dest = new File(folder.getAbsolutePath() + "/" + fileName);
            if (!dest.exists() || dest.length() < MINIMUM_TRACK_SIZE) {
                executor.execute(new Downloader(track.getUrl(), dest));
            } else {
                System.out.println(dest + " already exists");
            }
        }
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All tracks try to download.");


    }


    private static String fixWindowsFileName(String pathname) {
        /*String[] forbiddenSymbols = new String[]{"<", ">", ":", "\"", "/", "\\", "|", "?", "*"}; // для windows
        String result = pathname;
        for (String forbiddenSymbol : forbiddenSymbols) {
            result = result.replace(forbiddenSymbol, "");
        }
        // амперсанд в названиях передаётся как '& amp', приводим его к читаемому виду
        return result;*/
        return Util.sanitizeFilename(pathname);
    }

    public int getDownloadedPhotosCount() {
        int count = 0;
        for (Album album : albums) {
            count += album.getPhotos().size();
        }
        return count;
    }

    public int getDownloadedTracksCount() {
        return tracks.size();
    }

    private class Downloader implements Runnable {
        private final File dest;
        private final String url;

        public Downloader(String url, File dest) {
            this.url = url;
            this.dest = dest;
        }


        @Override
        public void run() {
            System.out.println(dest + " try download ");
            try {
                FileUtils.copyURLToFile(new URL(url), dest);
                System.out.println(dest + " done");
            } catch (IOException e) {
                System.out.println(dest + " error: ");
                e.printStackTrace();
                System.out.println(dest + " Deleting! " + dest.delete());
            }
        }
    }
}
