import java.util.Objects;

public class Node implements Comparable<Node> {
    public Point position;
    public Node previousNode;

    public int distanceFromStart;

    public int distanceToEnd;

    public Node(Point position, int distanceFromStart, int distanceToEnd, Node previousNode) {
        this.position = position;
        this.previousNode = previousNode;
        this.distanceFromStart = distanceFromStart;
        this.distanceToEnd = distanceToEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return position.x == node.position.x && position.y == node.position.y;
    }

    @Override
    public String toString() {
        return "Node{" +
                "position=" + position +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, previousNode, distanceToEnd);
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distanceToEnd + this.distanceFromStart, other.distanceToEnd + other.distanceFromStart);
    }
}
