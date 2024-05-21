import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

public class PathingTest {

    public void isValid(Point start, Point end, int pathLength, List<Point> path){
        Assertions.assertEquals(pathLength, path.size());

        if (!path.isEmpty()){
            Assertions.assertTrue(path.getFirst().adjacent(start));
            Assertions.assertTrue(path.getLast().adjacent(end));
        }
    }

    @Test
    public void testSimpleSearch(){
        boolean[][] grid = {
            {true, true, true},
            {true, true, true},
            {true, true, true}
        };

        Point start = new Point(0, 0);
        Point end = new Point(2, 2);

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                start, end,
                p -> isWithinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        isValid(start, end, 3, path);
    }

    @Test
    public void testOutOfBoundsEnd(){
        boolean[][] grid = {
                {true, true, true},
                {true, true, true},
                {true, true, true}
        };

        Point start = new Point(0, 0);
        Point end = new Point(3, 2);

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                start, end,
                p -> isWithinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        isValid(start, end, 4, path);
    }

    @Test
    public void testObstacle1(){
        boolean[][] grid = {
                {true, true, true},
                {false, true, false},
                {true, true, true}
        };

        Point start = new Point(0, 0);
        Point end = new Point(2, 2);

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                start, end,
                p -> isWithinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        isValid(start, end, 3, path);
    }

    @Test
    public void testObstacle2(){
        boolean[][] grid = {
                {true, false, true},
                {true, true, true},
                {true, true, true}
        };

        Point start = new Point(0, 0);
        Point end = new Point(2, 0);

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                start, end,
                p -> isWithinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        isValid(start, end, 3, path);
    }

    @Test
    public void testObstacle3(){
        boolean[][] grid = {
                {true, false, true, true, true},
                {true, false, true, false, true},
                {true, true, true, false, true}
        };

        Point start = new Point(0, 0);
        Point end = new Point(4, 2);

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                start, end,
                p -> isWithinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        isValid(start, end, 9, path);
    }

    @Test
    public void testStuck(){
        boolean[][] grid = {
                {true, true, true},
                {false, false, false},
                {true, true, true}
        };

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(
                new Point(0,0),
                new Point(2,2),
                p -> isWithinBounds(p, grid) && grid[p.y][p.x],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        isValid(new Point(0,0), new Point(2,2), 0, path);
    }

    private boolean isWithinBounds(Point p, boolean[][] grid){
        return p.x >= 0 && p.x < grid[0].length &&
                p.y >=0 && p.y < grid.length;
    }
}
