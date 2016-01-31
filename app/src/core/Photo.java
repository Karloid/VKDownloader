package core;

import java.text.SimpleDateFormat;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Photo photo = (Photo) o;

        if (src != null ? !src.equals(photo.src) : photo.src != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return src != null ? src.hashCode() : 0;
    }

    private static final SimpleDateFormat sdf;

    static {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "core.Photo{" +
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
        return sdf.format(created) + " " + split[split.length - 1];
        //return split[split.length - 1];
    }
}
