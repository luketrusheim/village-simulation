import processing.core.PImage;

import java.util.List;

public class NaturalRiver extends Obstacle{
    public NaturalRiver(String id, Point position, List<PImage> images, double animationPeriod) {
        super(id, position, images, animationPeriod);
        this.kind = EntityKind.NATURAL_RIVER;
    }
}
