import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public interface Entity {

    static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    default PImage getCurrentImage(){
        return this.getImages().get(this.getImageIndex() % this.getImages().size());
    }


    default void nextImage() {
        this.setImageIndex(this.getImageIndex() + 1);
    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    default String log(){
        return this.getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().x, this.getPosition().y, this.getImageIndex());
    }

    Point getPosition();

    EntityKind getKind();

    List<PImage> getImages();

    int getImageIndex();

    String getId();

    void setImageIndex(int imageIndex);

    void setPosition(Point position);
}
