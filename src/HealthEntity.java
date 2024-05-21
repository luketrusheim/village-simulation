public interface HealthEntity extends ActivityEntity{
    default boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Entity entity = new Stump(Stump.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(Stump.STUMP_KEY));

        world.removeEntity(scheduler, this);

        world.addEntity(entity);

        return true;
    };

    @Override
    default void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        }
    }

    int getHealth();

    void reduceHealth();
}
