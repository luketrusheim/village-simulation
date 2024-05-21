import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    @Override
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {

        PriorityQueue<Node> toVisit = new PriorityQueue<>();
        HashSet<Point> visitedPoints = new HashSet<>();
        List<Point> path = new LinkedList<>();

        // Add starting point to queue
        toVisit.add(new Node(start, 0, start.manhattanDistance(end), null));

        // Add nodes to queue until queue is empty or a node is found that is withinReach
        while (!toVisit.isEmpty()) {
            Node current = toVisit.poll();

            // If node is withinReach, assemble and return path
            if (withinReach.test(current.position, end)) {
                while (current.previousNode != null) {
                    path.addFirst(current.position);
                    current = current.previousNode;
                }
                return path;
            }

            // Add node to visitedPoints
            visitedPoints.add(current.position);

            // Create a list of neighbors to be added to queue
            List<Point> neighbors = potentialNeighbors.apply(current.position)
                    .filter(canPassThrough)
                    .filter(p -> !visitedPoints.contains(p))
                    .toList();

            // For each neighbor, convert it into a node and add to queue
            for (Point neighbor : neighbors) {
                Node neighborNode = new Node(neighbor,
                        current.distanceFromStart + 1,
                        neighbor.manhattanDistance(end),
                        current);

                if (!toVisit.contains(neighborNode)){
                    toVisit.add(neighborNode);
                } else if (toVisit.removeIf(p -> p.equals(neighborNode) && p.distanceFromStart > neighborNode.distanceFromStart)) {
                    toVisit.add(neighborNode);
                }
            }
        }

        return path;

    }
}
