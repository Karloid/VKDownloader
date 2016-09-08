package core;

import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 7/10/2014.
 */
public class Album {
    private List<Photo> photos;

    public Album() {
        photos = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "core.Album{" +
                "aid=" + aid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    private long aid;
    private String title;
    private String description;

    public void setAid(long aid) {
        this.aid = aid;
    }

    public long getAid() {
        return aid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public List<Photo> getPhotos() {
        return photos;
    }
}
