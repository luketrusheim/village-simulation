
public final class Animation implements Action{
    public ActionKind kind;
    public Entity entity;
    public int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        this.kind = ActionKind.ANIMATION;
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    @Override
    public void execute(EventScheduler scheduler) {
        this.entity.nextImage();

        AnimatedEntity animatedEntity = (AnimatedEntity) this.entity;

        if (this.repeatCount != 1) {
            scheduler.scheduleEvent(this.entity, new Animation(this.entity, Math.max(this.repeatCount - 1, 0)), animatedEntity.getAnimationPeriod());
        }
    }
}
