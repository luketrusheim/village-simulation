public interface AnimatedEntity extends ActioningEntity{
    double getAnimationPeriod();

    @Override
    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Animation(this, 0), getAnimationPeriod());
    }
}
