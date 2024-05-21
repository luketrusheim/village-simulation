public interface ActivityEntity extends AnimatedEntity{
    double getActionPeriod();

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());
    }
}
