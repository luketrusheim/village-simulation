import java.util.*;

import processing.core.PImage;


public final class Sapling implements HealthEntity {
    // Static variables
    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_NUM_PROPERTIES = 1;



    // Instance variables
    public EntityKind kind;
    public String id;
    public Point position;
    public List<PImage> images;
    public int imageIndex;
    public double actionPeriod;
    public double animationPeriod;
    public int health;
    public int healthLimit;

    public Sapling(String id, Point position, List<PImage> images) {
        this.kind = EntityKind.SAPLING;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = SAPLING_ACTION_ANIMATION_PERIOD;
        this.animationPeriod = SAPLING_ACTION_ANIMATION_PERIOD;
        this.health = 0;
        this.healthLimit = SAPLING_HEALTH_LIMIT;
    }

    @Override
    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            return HealthEntity.super.transform(world, scheduler, imageStore);
        } else if (this.health >= this.healthLimit) {
            Entity entity = new Tree(Tree.TREE_KEY + "_" + this.id, this.position, imageStore.getImageList(Tree.TREE_KEY), Functions.getNumFromRange(Tree.TREE_ACTION_MAX, Tree.TREE_ACTION_MIN), Functions.getNumFromRange(Tree.TREE_ANIMATION_MAX, Tree.TREE_ANIMATION_MIN), Functions.getIntFromRange(Tree.TREE_HEALTH_MAX, Tree.TREE_HEALTH_MIN));

            world.removeEntity(scheduler, this);

            world.addEntity(entity);

            ActioningEntity actioningEntity = (ActioningEntity) entity;
            actioningEntity.scheduleActions(scheduler, world, imageStore);


            return true;
        }

        return false;
    }

    @Override
    public double getActionPeriod() {
        return actionPeriod;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        HealthEntity.super.executeActivity(world, imageStore, scheduler);
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
    public int getHealth(){
        return health;
    }

    @Override
    public void reduceHealth(){
        health--;
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
