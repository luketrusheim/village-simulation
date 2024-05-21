import java.util.*;

import processing.core.PImage;


public final class Stump implements Entity{
    // Static variables
    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;


    // Instance variables
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;

    public Stump(String id, Point position, List<PImage> images) {
        this.kind = EntityKind.STUMP;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public EntityKind getKind() {
        return kind;
    }

    @Override
    public List<PImage> getImages() {
        return images;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }
}
