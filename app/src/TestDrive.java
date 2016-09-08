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
        String paramUid = "xx";
        String paramGid = null;
        vkDownloader.getTracks(paramUid, paramGid);
        vkDownloader.saveTracks("xx");
    }

    private static void testPhoto() {
        VKDownloader vkDownloader = new VKDownloader();
        vkDownloader.init();
        vkDownloader.setAccessToken("xx");
        String paramUid = "xx";
        String paramGid = null;
        String folderToSave = "xx/";
        System.out.println("getAlbums...");
        vkDownloader.getAlbums(paramUid, paramGid);
        System.out.println("getAlbumsPhotos...");
        vkDownloader.getAlbumsPhotos(paramUid, paramGid);
        System.out.println("saveAlbumsPhotos...");
        vkDownloader.saveAlbumsPhotos(folderToSave);
    }
}
