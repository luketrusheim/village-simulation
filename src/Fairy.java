import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import processing.core.PImage;


public final class Fairy implements MovingEntity, ActivityEntity {

    // Static variables
    public static final String FAIRY_KEY = "fairy";
    public static final String BLUE_FAIRY_KEY = "blue_fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;


    // Instance variables
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public double actionPeriod;
    public double animationPeriod;
    public boolean isBlueFairy;

    public Fairy(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.kind = EntityKind.FAIRY;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.isBlueFairy = false;
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {

        Predicate<Point> canPassThrough = p -> world.withinBounds(p) && !world.isOccupied(p);
        BiPredicate<Point, Point> withinReach = (position, target) -> position.adjacent(target);
        Function<Point, Stream<Point>> potentialNeighbors = SingleStepPathingStrategy.CARDINAL_NEIGHBORS;

        List<Point> newPoints = new AStarPathingStrategy().computePath(this.position, destPos, canPassThrough, withinReach, potentialNeighbors);

        if (newPoints.isEmpty()){
            return this.position;
        } else {
            return newPoints.get(0);
        }
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            return MovingEntity.super.moveTo(world, target, scheduler);
        }
    }

    @Override
    public double getActionPeriod() {
        return actionPeriod;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!isBlueFairy){
            regenerateTrees(world, imageStore, scheduler);
        } else {
            deleteObstacles(world, imageStore, scheduler);
        }
    }

    private void deleteObstacles(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> obstacleTarget = world.findNearest(this.position, new ArrayList<>(List.of(EntityKind.SERPENT_RIVER, EntityKind.NATURAL_RIVER)));

        if (obstacleTarget.isPresent()) {
            if (this.moveTo(world, obstacleTarget.get(), scheduler)) {

                world.removeEntity(scheduler, obstacleTarget.get());
            }
        }

        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), 2);
    }


    public void regenerateTrees(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(this.position, new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (this.moveTo(world, fairyTarget.get(), scheduler)) {

                Sapling sapling = new Sapling(Sapling.SAPLING_KEY + "_" + fairyTarget.get().getId(), tgtPos, imageStore.getImageList(Sapling.SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
    }

    public void transform(ImageStore imageStore){
        this.images = imageStore.getImageList(Fairy.BLUE_FAIRY_KEY);
        this.animationPeriod = 0.01;
        this.isBlueFairy = true;
    }

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }

    @Override
    public String getId() {
        return id;
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
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

}
