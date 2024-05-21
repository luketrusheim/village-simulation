import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    public static final int VIEW_WIDTH = 800;
    public static final int VIEW_HEIGHT = 800;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public String loadFile = "world.sav";
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    public ImageStore imageStore;
    public WorldModel world;
    public WorldView view;
    public EventScheduler scheduler;

    public static final int eventCooldownMillis = 0;
    public long lastEventTriggerTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.currentTime)/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();
        System.out.println("CLICK! " + pressed.x + ", " + pressed.y);

        Optional<Entity> entityOptional = world.getOccupant(pressed);
        printClickInfo(entityOptional);

        Optional<Entity> secondEntityOptional = world.getSecondOccupant(pressed);
        printClickInfo(secondEntityOptional);

        tryLakeEvent(pressed);
    }

    public void printClickInfo(Optional<Entity> entityOptional) {
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            System.out.println(entity.getId() + ": " + entity.getKind());
            if (entity instanceof HealthEntity){
                HealthEntity healthEntity = (HealthEntity) entity;
                System.out.println("Health: " + healthEntity.getHealth());
            }
        }
    }

    public void tryLakeEvent(Point pressed){
        boolean validLakeEvent = checkValidLakeEvent(pressed);
        if (validLakeEvent){
            spawnLake(pressed);
            generateSerpentEvent(pressed);
            transformNearestFairy(pressed);
            // HAVE WAVES ONLY ANIMATE AS SERPENT PASSES THROUGH.
            // MAKE THIS A PROPERTY OF OBSTACLES, PERHAPS HAVE A NEW SUBTYPE OF OBSTACLE. IF SERPENT ADJACENT THEN ANIMATE
            //HAVE IT GO UNDERWATER AND ONLY SHOW WAVES MOVING
            // HOUSE BECOMES HOUSE BOAT AND DUDE BUILDS BRIDGE TO GET HOME
            lastEventTriggerTime = System.currentTimeMillis();
        } else {
//            System.out.println("Must wait " + (15000 - (System.currentTimeMillis() - lastEventTriggerTime)) / 1000 + " more seconds");
        }
    }

    private void transformNearestFairy(Point pressed) {
        Optional<Entity> target = world.findNearest(pressed, new ArrayList<>(List.of(EntityKind.FAIRY)));

        if (target.isPresent()){
            Fairy fairyTarget = (Fairy) target.get();
            fairyTarget.transform(imageStore);
        }
    }

    public void generateSerpentEvent(Point pressed) {
        Random rn = new Random();
        int serpentDelayMillis = rn.nextInt(5000) + 5000;
        Timer timer = new Timer();

        // Epicenter wave animates
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                createWave(pressed);
            }
        }, serpentDelayMillis/2);

        // Surrounding waves animate
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                createWave(pressed.adjacentPoint("north"));
                createWave(pressed.adjacentPoint("south"));
                createWave(pressed.adjacentPoint("east"));
                createWave(pressed.adjacentPoint("west"));
            }
        }, (serpentDelayMillis * 2)/3);

        // Serpent emerges
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                createSerpent(pressed);
                timer.cancel();
            }
        }, serpentDelayMillis);
    }

    private void createWave(Point pressed) {
        Optional<Entity> target = world.getOccupant(pressed);
        if (target.isPresent() && target.get().getKind() == EntityKind.SERPENT_RIVER){
            Obstacle obstacleTarget = (Obstacle) target.get();
            obstacleTarget.setAnimationPeriod(0.6);
            obstacleTarget.scheduleActions(scheduler, world, imageStore);
        }
    }

    public void spawnLake(Point pressed){

        List<Point> points = generateLakeSpawns(pressed);

        for (Point point : points){
            if (world.getOccupant(point).isPresent()){
                world.removeEntity(scheduler, world.getOccupant(point).get());
            }

            Obstacle newObstacle = new SerpentRiver("eventObstacle", point, imageStore.getImageList("obstacle"), 1, 0.3, world, imageStore);
            world.addEntity(newObstacle);
        }



    }

    public List<Point> generateLakeSpawns(Point pressed){
        Random rn = new Random();
        int lakeArrangement = rn.nextInt(4);

        Point northPoint = pressed.adjacentPoint("north");
        Point southPoint = pressed.adjacentPoint("south");
        Point westPoint = pressed.adjacentPoint("west");
        Point eastPoint = pressed.adjacentPoint("east");
        Point northNorthPoint = northPoint.adjacentPoint("north");
        Point southSouthPoint = southPoint.adjacentPoint("south");
        Point westWestPoint = westPoint.adjacentPoint("west");
        Point eastEastPoint = eastPoint.adjacentPoint("east");
        Point northEastPoint = northPoint.adjacentPoint("east");
        Point northWestPoint = northPoint.adjacentPoint("west");
        Point southEastPoint = southPoint.adjacentPoint("east");
        Point southWestPoint = southPoint.adjacentPoint("west");


        List<Point> points;

        switch (lakeArrangement){
            case 0 -> points = new ArrayList<>(Arrays.asList(northPoint, southPoint, westPoint, eastPoint, pressed, northEastPoint, southWestPoint));
            case 1 -> points = new ArrayList<>(Arrays.asList(northPoint, southPoint, westPoint, eastPoint, pressed, northWestPoint, southEastPoint));
            case 2 -> points = new ArrayList<>(Arrays.asList(pressed, southEastPoint, northPoint, westPoint, southPoint, eastPoint, eastEastPoint));
            default -> points = new ArrayList<>(Arrays.asList(pressed, southWestPoint, northPoint, westPoint, southPoint, eastPoint, eastEastPoint, southEastPoint, southSouthPoint));
        }

        List<Point> validPoints = points.stream().filter(p -> world.withinBounds(p)).filter(p -> world.getOccupant(p).isEmpty() || world.getOccupant(p).get().getKind() != EntityKind.NATURAL_RIVER).toList();

        return validPoints;
    }

    public void createSerpent(Point pressed){
        Serpent serpent = new Serpent("serpent", pressed, this.imageStore.getImageList("serpent"), Serpent.HUNGRY_ACTION_PERIOD, Serpent.HUNGRY_ANIMATION_PERIOD);
        this.world.addEntity(serpent);
        serpent.scheduleActions(this.scheduler, this.world, this.imageStore);
//            world.setBackgroundCell(pressed, new Background("obstacle", imageStore.getImageList("obstacle")));
    }

    public boolean checkValidLakeEvent(Point pressed){
        boolean eventCooldownOver = System.currentTimeMillis() - lastEventTriggerTime > eventCooldownMillis;
        boolean eventSpaceOkay = world.getOccupant(pressed).isEmpty();
        return eventCooldownOver && eventSpaceOkay;
    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.entities) {
            if (entity instanceof ActioningEntity){
                ActioningEntity actioningEntity = (ActioningEntity) entity;
                actioningEntity.scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private Point mouseToPoint() {
        return view.viewport.viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 2;
                case DOWN -> dy += 2;
                case LEFT -> dx -= 2;
                case RIGHT -> dx += 2;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            WorldModel.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
