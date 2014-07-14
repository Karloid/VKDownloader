import core.VKDownloader;

/**
 * Created by Andrey on 7/10/2014.
 */
public class TestDrive {

    public static void main(String[] args) throws Exception {
      //  testPhoto();
        testAudio();
    }

    private static void testAudio() {
        VKDownloader vkDownloader = new VKDownloader();
        vkDownloader.setAccessToken("xx");
        String paramUid = String.valueOf(1);
        String paramGid = null;
        vkDownloader.getTracks(paramUid, paramGid);
        vkDownloader.saveTracksMultithreading("audioFolder");
    }

    private static void testPhoto() {
        VKDownloader vkDownloader = new VKDownloader();
        vkDownloader.init();
        vkDownloader.setAccessToken("xx");
        String paramUid = null;
        String paramGid = String.valueOf(1);
        vkDownloader.getAlbums(paramUid, paramGid);
        vkDownloader.getAlbumsPhotos(paramUid, paramGid);
        vkDownloader.saveAlbumsPhotos("albums/");
    }
}
