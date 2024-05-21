import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SerpentRiver extends Obstacle implements ActivityEntity{
    public double actionPeriod;
    public boolean serpentNearby;
    public static final double ACTION_PERIOD = 0.5;

    public SerpentRiver(String id, Point position, List<PImage> images, double animationPeriod, double actionPeriod, WorldModel world, ImageStore imageStore) {
        super(id, position, images, animationPeriod);
        this.actionPeriod = actionPeriod;
        this.kind = EntityKind.SERPENT_RIVER;
        this.serpentNearby = true;

        changeSurroundingBackground(position, world, imageStore);
    }

    @Override
    public double getActionPeriod() {
        return actionPeriod;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> serpentTarget = world.findNearest(this.position, new ArrayList<>(List.of(EntityKind.SERPENT)));

        if (serpentTarget.isPresent()) {
            Point tgtPos = serpentTarget.get().getPosition();

            if (position.distanceSquared(tgtPos) < 3) {
                serpentNearby = true;
            } else{
                serpentNearby = false;
            }
        }

        if (serpentNearby){
            becomeTurbulent(world, imageStore, scheduler);
        } else{
            becomeCalm(world, imageStore, scheduler);
        }
    }

    public void changeSurroundingBackground(Point target, WorldModel world, ImageStore imageStore) {
        for (Point point: target.adjacentPoints()){

            if (!world.withinBounds(point)){
                continue;
            }

            String currentBackground = world.getBackgroundCell(point).id;


            switch (currentBackground){
                case "grass" -> world.setBackgroundCell(point, new Background("flowers", imageStore.getImageList("flowers")));
                case "dirt_bot_right_up" -> world.setBackgroundCell(point, new Background("flower_dirt_bot_right_up", imageStore.getImageList("flower_dirt_bot_right_up")));
                case "dirt_vert_left" -> world.setBackgroundCell(point, new Background("flower_dirt_vert_left", imageStore.getImageList("flower_dirt_vert_left")));
                case "dirt_vert_left_bot" -> world.setBackgroundCell(point, new Background("flower_dirt_vert_left_bot", imageStore.getImageList("flower_dirt_vert_left_bot")));
                case "dirt_horiz" -> world.setBackgroundCell(point, new Background("flower_dirt_horiz", imageStore.getImageList("flower_dirt_horiz")));
                case "dirt_vert_right" -> world.setBackgroundCell(point, new Background("flower_dirt_vert_right", imageStore.getImageList("flower_dirt_vert_right")));
                case "dirt_bot_left_corner" -> world.setBackgroundCell(point, new Background("flower_dirt_bot_left_corner", imageStore.getImageList("flower_dirt_bot_left_corner")));
                default -> {}
            }
        }

    }

    // These three functions were all attempts at getting the waves to animate as the serpent passed by, but
    // I could never get it to work
    private void becomeCalm(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
//        this.setAnimationPeriod(10);
//        this.setImageIndex(0);
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this, 1), this.animationPeriod);
    }

    public void becomeTurbulent(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        this.getCurrentImage();
        this.setAnimationPeriod(1);
        scheduler.scheduleEvent(this, new Animation(this, 0), this.animationPeriod);
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
    }

    @Override
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.getActionPeriod());
        scheduler.scheduleEvent(this, new Animation(this, 1), this.getAnimationPeriod());
    }
}
