import java.util.Date;

/**
 * Created by Andrey on 7/10/2014.
 */
public class Photo {
    private String text;
    private String src;
    private int likes;
    private Date created;
    private Album album;

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "text='" + text + '\'' +
                ", src='" + src + '\'' +
                ", likes=" + likes +
                ", created=" + created +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSrc() {
        return src;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getLikes() {
        return likes;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return album;
    }

    public String getFileName() {
        String[] split = src.split("/");

        return split[split.length - 1];
    }
}
