import java.util.*;

import processing.core.PImage;


public final class DudeNotFull extends AbstractDude {

    // Instance variables

    public int resourceCount;

    public DudeNotFull(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        super(id, position, images, resourceLimit, actionPeriod, animationPeriod);
        this.kind = EntityKind.DUDE_NOT_FULL;
        this.resourceCount = 0;
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            this.resourceCount += 1;
            if (target instanceof HealthEntity){
                HealthEntity healthTarget = (HealthEntity) target;
                healthTarget.reduceHealth();
            } else {
                System.out.println("cannot take health from non-health entity");
            }
            return true;
        } else {
            return super.moveTo(world, target, scheduler);
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            DudeFull dude = new DudeFull(this.id, this.position, this.images, this.resourceLimit, this.actionPeriod, this.animationPeriod);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageStore);

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
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(EntityKind.TREE, EntityKind.SAPLING)));

        if (target.isEmpty() || !this.moveTo(world, target.get(), scheduler) || !this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        }
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
