import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Serpent implements MovingEntity, ActivityEntity {

    private final EntityKind kind;
    private String id;
    private Point position;
    private final Point spawnPoint;
    private List<PImage> images;
    private int imageIndex;
    private double actionPeriod;
    private double animationPeriod;
    private boolean hasEaten;
    private boolean isHome;
    public static final double HUNGRY_ACTION_PERIOD = 0.6;
    public static final double FULL_ACTION_PERIOD = 0.8;
    public static final double HUNGRY_ANIMATION_PERIOD = 0.1;
    public static final double FULL_ANIMATION_PERIOD = 0.2;



    public Serpent(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod){
        this.kind = EntityKind.SERPENT;
        this.id = id;
        this.position = position;
        this.spawnPoint = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.hasEaten = false;
        this.isHome = false;
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            return true;
        } else {
            return MovingEntity.super.moveTo(world, target, scheduler);
        }
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough;
        BiPredicate<Point, Point> withinReach = Point::adjacent;
        Function<Point, Stream<Point>> potentialNeighbors = PathingStrategy.CARDINAL_NEIGHBORS;

        if (hasEaten){
            canPassThrough = p -> world.withinBounds(p) && (world.getOccupant(p).isPresent() &&
                    (world.getOccupant(p).get().getKind() == EntityKind.SERPENT_RIVER || world.getOccupant(p).get().getKind() == EntityKind.NATURAL_RIVER));
        } else {
            canPassThrough = p -> world.withinBounds(p) && (!world.isOccupied(p) ||
                    world.getOccupancyCell(p).getKind() == EntityKind.SERPENT_RIVER || world.getOccupancyCell(p).getKind() == EntityKind.NATURAL_RIVER);
        }

        List<Point> newPoints = new AStarPathingStrategy().computePath(this.position, destPos, canPassThrough, withinReach, potentialNeighbors);

        if (newPoints.isEmpty()){
            return this.position;
        } else {
            return newPoints.get(0);
        }
    }


    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!hasEaten){
            seekDude(world, imageStore, scheduler);
        } else if (!isHome) {
            returnHome(world, imageStore, scheduler);
        } else {
            world.removeEntity(scheduler, this);
        }
    }

    private void returnHome(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.getOccupant(spawnPoint);
        if (target.isPresent() && this.moveTo(world, target.get(), scheduler)) {
            world.moveEntity(scheduler, this, target.get().getPosition());
            isHome = true;
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), Serpent.FULL_ACTION_PERIOD);
        } else {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), Serpent.FULL_ACTION_PERIOD);
        }
    }

    private void seekDude(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(List.of(EntityKind.DUDE_FULL, EntityKind.DUDE_NOT_FULL)));
        if (target.isPresent() && this.moveTo(world, target.get(), scheduler)) {
            Point targetLocation = target.get().getPosition();
            world.removeEntity(scheduler, target.get());
            world.moveEntity(scheduler, this, targetLocation);
            this.hasEaten = true;
            this.setAnimationPeriod(Serpent.FULL_ANIMATION_PERIOD);
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        } else {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        }

        // Generate river below serpent
        if (world.getSecondOccupant(position).isEmpty()) {
            SerpentRiver serpentRiver = new SerpentRiver("serpentObstacle", position, imageStore.getImageList(Obstacle.OBSTACLE_KEY), 1, 1, world, imageStore);
            world.addAnotherEntity(serpentRiver);
            scheduler.scheduleEvent(serpentRiver, new Activity(serpentRiver, world, imageStore), SerpentRiver.ACTION_PERIOD);
        }

        for (Point position: position.adjacentPoints()){
            generateSerpentRiver(world, imageStore, scheduler, position);
//            createTurbulence(world, imageStore, scheduler, northPoint, westPoint, eastPoint, southPoint);
        }

    }

    // this was an attempt at animating the waves as the serpent swam through them but I could never get it to work
    private void createTurbulence(WorldModel world, ImageStore imageStore, EventScheduler scheduler,
                                  Point northPoint, Point westPoint, Point eastPoint, Point southPoint) {
        Optional<Entity> north = world.getOccupant(northPoint);
        Optional<Entity> south = world.getOccupant(southPoint);
        Optional<Entity> east = world.getOccupant(eastPoint);
        Optional<Entity> west = world.getOccupant(westPoint);
        Optional<Entity> center = world.getSecondOccupant(position);

        if (north.isPresent() && north.get().getKind() == EntityKind.SERPENT_RIVER){
            SerpentRiver northSerpentRiver = (SerpentRiver) north.get();
            scheduler.scheduleEvent(northSerpentRiver,  new Activity(northSerpentRiver, world, imageStore), 1);
        }

        if (south.isPresent() && south.get().getKind() == EntityKind.SERPENT_RIVER){
            SerpentRiver southSerpentRiver = (SerpentRiver) south.get();
            scheduler.scheduleEvent(southSerpentRiver,  new Activity(southSerpentRiver, world, imageStore), 1);
        }
    }

    public void generateSerpentRiver(WorldModel world, ImageStore imageStore, EventScheduler scheduler, Point targetPoint) {
        if (world.getOccupant(targetPoint).isEmpty()){
            SerpentRiver serpentRiver = new SerpentRiver("serpentObstacle", targetPoint, imageStore.getImageList(Obstacle.OBSTACLE_KEY), 1, 1, world, imageStore);
            world.addEntity(serpentRiver);
            scheduler.scheduleEvent(serpentRiver, new Activity(serpentRiver, world, imageStore), this.actionPeriod);
        }
    }

    @Override
    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
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

    public void setAnimationPeriod(double animationPeriod) {
        this.animationPeriod = animationPeriod;
    }

    @Override
    public double getActionPeriod() {
        return actionPeriod;
    }
}
