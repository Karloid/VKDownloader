/**
 * Created by Andrey on 7/10/2014.
 */
public class TestDrive {
    private static final String USE_SELF_UID = null;

    public static void main(String[] args) throws Exception{
        VKDownloader vkDownloader = new VKDownloader();
        vkDownloader.init();
      //  vkDownloader.simpleRequest(VKDownloader.IS_APP_USER);
      //  vkDownloader.simpleRequest(VKDownloader.GET_USER_SETTINGS);
        vkDownloader.getAlbums(USE_SELF_UID);
        vkDownloader.getAlbumsPhotos();
        vkDownloader.saveAlbumsPhotos();
    }
}
