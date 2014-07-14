package core;

/**
 * Created by Andrey on 7/14/2014.
 */
public class Track {
    private String title;
    private String artist;
    private String url;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return artist + " - " + title;
    }
}
