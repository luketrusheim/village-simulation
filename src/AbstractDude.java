import processing.core.PImage;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractDude implements ActivityEntity, MovingEntity{
    // Static variables
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    //instance variables
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public int resourceLimit;
    public double actionPeriod;
    public double animationPeriod;

    public AbstractDude(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod){
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p) ||
                world.getOccupancyCell(p).getKind() == EntityKind.STUMP);
        BiPredicate<Point, Point> withinReach = (position, target) -> position.adjacent(target);
        Function<Point, Stream<Point>> potentialNeighbors = SingleStepPathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> newPoints = new AStarPathingStrategy().computePath(this.position, destPos, canPassThrough, withinReach, potentialNeighbors);

        if (newPoints.isEmpty()){
            return this.position;
        } else {
            return newPoints.get(0);
        }
    }


}
