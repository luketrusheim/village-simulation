public interface ActioningEntity extends Entity{
    void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
}
