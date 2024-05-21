public interface MovingEntity extends AnimatedEntity{
    Point nextPosition(WorldModel world, Point destPos);

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler){
        Point nextPos = this.nextPosition(world, target.getPosition());

        if (!this.getPosition().equals(nextPos)) {
            world.moveEntity(scheduler, this, nextPos);
        }
        return false;
    };
}
