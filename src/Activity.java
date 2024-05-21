
public final class Activity implements Action{
    public ActionKind kind;
    public Entity entity;
    public WorldModel world;
    public ImageStore imageStore;
    public int repeatCount;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore) {
        this.kind = ActionKind.ACTIVITY;
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = 0;
    }

    @Override
    public void execute(EventScheduler scheduler) {
        ActivityEntity activityEntity = (ActivityEntity) this.entity;
        activityEntity.executeActivity(this.world, this.imageStore, scheduler);
    }
}
