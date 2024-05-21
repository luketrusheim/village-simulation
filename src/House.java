import java.util.*;

import processing.core.PImage;


public final class House implements Entity{
    // Static variables
    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_NUM_PROPERTIES = 0;

    // Instance variables
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;


    public House(String id, Point position, List<PImage> images) {
        this.kind = EntityKind.HOUSE;
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
