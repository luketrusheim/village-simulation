import javax.management.InvalidAttributeValueException;

/**
 * A simple class representing a location in 2D space.
 */
public final class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point adjacentPoint(String direction){
        Point newPoint;
        switch (direction){
            case "north" -> newPoint = new Point(this.x, this.y - 1);
            case "east" -> newPoint = new Point(this.x + 1, this.y);
            case "south" -> newPoint = new Point(this.x, this.y + 1);
            case "west" -> newPoint = new Point(this.x - 1, this.y);
            default -> newPoint = this;
        }

        return newPoint;
    }

    public Point[] adjacentPoints(){
        return new Point[]{new Point(this.x, this.y - 1), new Point(this.x + 1, this.y), new Point(this.x, this.y + 1), new Point(this.x - 1, this.y)};
    }

    public int manhattanDistance(Point p2){
        return Math.abs(this.x - p2.x) + Math.abs(this.y - p2.y);
    }

    public int distanceSquared(Point p2) {
        int deltaX = this.x - p2.x;
        int deltaY = this.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public boolean adjacent(Point p2) {
        return (this.x == p2.x && Math.abs(this.y - p2.y) == 1) || (this.y == p2.y && Math.abs(this.x - p2.x) == 1);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }
}
