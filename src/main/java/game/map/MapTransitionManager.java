package game.map;

import game.entity.Player;
import main.menu.GamePanel;

import java.io.IOException;
import java.util.*;
import java.util.Map;

public class MapTransitionManager {
    private static final String BASE_MAP = "/maps/base.txt";
    private static final String[] MAP_FILES = {
            "/maps/dry.txt",
            "/maps/messed.txt",
            "/maps/lake.txt",
            "/maps/simple.txt"
    };

    // Spawn coordinates for base map
    private static final int BASE_SPAWN_X = 49;
    private static final int BASE_SPAWN_Y = 41;

    private static final int TRANSITION_BOUNDARY = 15;
    private final GamePanel gamePanel;
    private final Random random = new Random();
    private boolean isFirstMap = true;
    private Direction lastTransitionDirection;

    // Map of spawn points for each map and direction
    private final Map<String, DirectionalSpawns> spawnPoints;

    public MapTransitionManager(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.spawnPoints = initializeSpawnPoints();
    }

    private Map<String, DirectionalSpawns> initializeSpawnPoints() {
        Map<String, DirectionalSpawns> points = new HashMap<>();

        // Dry map spawn points
        points.put("/maps/dry.txt", new DirectionalSpawns()
                .addUp(new SpawnPoint(72, 22), new SpawnPoint(42, 21))
                .addRight(new SpawnPoint(75, 70), new SpawnPoint(75, 40))
                .addLeft(new SpawnPoint(21, 26), new SpawnPoint(21, 64))
                .addDown(new SpawnPoint(39, 77), new SpawnPoint(62, 78)));

        // Simple map spawn points
        points.put("/maps/simple.txt", new DirectionalSpawns()
                .addUp(new SpawnPoint(33, 21), new SpawnPoint(62, 22))
                .addRight(new SpawnPoint(75, 58))
                .addDown(new SpawnPoint(32, 78))
                .addLeft(new SpawnPoint(22, 69)));

        // Messed map spawn points
        points.put("/maps/messed.txt", new DirectionalSpawns()
                .addRight(new SpawnPoint(75, 46), new SpawnPoint(75, 31), new SpawnPoint(73, 74))
                .addUp(new SpawnPoint(70, 21), new SpawnPoint(48, 21))
                .addLeft(new SpawnPoint(21, 23), new SpawnPoint(21, 71))
                .addDown(new SpawnPoint(29, 78), new SpawnPoint(35, 78), new SpawnPoint(45, 78)));

        // Lake map spawn points
        points.put("/maps/lake.txt", new DirectionalSpawns()
                .addLeft(new SpawnPoint(21, 59), new SpawnPoint(21, 23), new SpawnPoint(21, 73))
                .addDown(new SpawnPoint(29, 78), new SpawnPoint(49, 78), new SpawnPoint(66, 78))
                .addRight(new SpawnPoint(75, 67), new SpawnPoint(75, 46), new SpawnPoint(75, 34))
                .addUp(new SpawnPoint(68, 22), new SpawnPoint(53, 21), new SpawnPoint(26, 21)));

        return points;
    }

    public String getInitialMapPath() {
        return BASE_MAP;
    }

    public void loadInitialMap() throws IOException {
        if (gamePanel.debug) {
            // In debug mode, randomly select a non-base map and spawn point
            String[] mapFiles = {"/maps/dry.txt", "/maps/messed.txt", "/maps/lake.txt", "/maps/simple.txt"};
            String randomMap = mapFiles[new Random().nextInt(mapFiles.length)];
            gamePanel.map.loadFromFile(randomMap);

            // Get spawn points for the selected map
            DirectionalSpawns spawns = spawnPoints.get(randomMap);

            // Get all spawn points for the map
            List<SpawnPoint> allSpawnPoints = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                SpawnPoint[] points = spawns.spawns.get(dir);
                if (points != null) {
                    allSpawnPoints.addAll(Arrays.asList(points));
                }
            }

            // Randomly select a spawn point
            if (!allSpawnPoints.isEmpty()) {
                SpawnPoint randomSpawn = allSpawnPoints.get(new Random().nextInt(allSpawnPoints.size()));
                gamePanel.player.playerX = randomSpawn.x * gamePanel.tileSize;
                gamePanel.player.playerY = randomSpawn.y * gamePanel.tileSize;
            }
        } else {
            // Normal mode - load base map
            gamePanel.map.loadFromFile(getInitialMapPath());
            setInitialPlayerPosition();
        }
    }

    public void setInitialPlayerPosition() {
        gamePanel.player.playerX = BASE_SPAWN_X * gamePanel.tileSize;
        gamePanel.player.playerY = BASE_SPAWN_Y * gamePanel.tileSize;
    }

    public void checkForTransition() throws IOException {
        Player player = gamePanel.player;
        Direction direction = null;

        if (player.playerY < TRANSITION_BOUNDARY * gamePanel.tileSize) {
            direction = Direction.NORTH;
        } else if (player.playerY > (100 - TRANSITION_BOUNDARY) * gamePanel.tileSize) {
            direction = Direction.SOUTH;
        } else if (player.playerX < TRANSITION_BOUNDARY * gamePanel.tileSize) {
            direction = Direction.WEST;
        } else if (player.playerX > (100 - TRANSITION_BOUNDARY) * gamePanel.tileSize) {
            direction = Direction.EAST;
        }

        if (direction != null) {
            lastTransitionDirection = direction;
            transitionToNewMap(direction);
        }
    }

    private void transitionToNewMap(Direction exitDirection) throws IOException {
        String newMapPath;
        if (isFirstMap) {
            newMapPath = MAP_FILES[random.nextInt(MAP_FILES.length)];
            isFirstMap = false;
        } else {
            String currentMapPath = gamePanel.map.getCurrentMapPath();
            do {
                newMapPath = MAP_FILES[random.nextInt(MAP_FILES.length)];
            } while (newMapPath.equals(currentMapPath));
        }

        // Load new map
        gamePanel.map.loadFromFile(newMapPath);

        // Get spawn points for the new map
        DirectionalSpawns spawns = spawnPoints.get(newMapPath);
        SpawnPoint spawnPoint = spawns.getSpawnPoint(getOppositeDirection(exitDirection));

        // Set player position using the selected spawn point
        gamePanel.player.playerX = spawnPoint.x * gamePanel.tileSize;
        gamePanel.player.playerY = spawnPoint.y * gamePanel.tileSize;

        // Update player's facing direction based on the transition
        updatePlayerDirection(exitDirection);

        System.out.println("Transitioning to new map: " + newMapPath + " from direction: " + exitDirection);
    }

    private void updatePlayerDirection(Direction exitDirection) {
        // Set the player's direction based on where they came from
        switch (exitDirection) {
            case NORTH -> gamePanel.player.direction = "up";
            case SOUTH -> gamePanel.player.direction = "down";
            case EAST -> gamePanel.player.direction = "right";
            case WEST -> gamePanel.player.direction = "left";
        }
    }

    private Direction getOppositeDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    private static class SpawnPoint {
        final int x;
        final int y;

        SpawnPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class DirectionalSpawns {
        private final Map<Direction, SpawnPoint[]> spawns = new HashMap<>();

        DirectionalSpawns addUp(SpawnPoint... points) {
            spawns.put(Direction.NORTH, points);
            return this;
        }

        DirectionalSpawns addDown(SpawnPoint... points) {
            spawns.put(Direction.SOUTH, points);
            return this;
        }

        DirectionalSpawns addLeft(SpawnPoint... points) {
            spawns.put(Direction.WEST, points);
            return this;
        }

        DirectionalSpawns addRight(SpawnPoint... points) {
            spawns.put(Direction.EAST, points);
            return this;
        }

        SpawnPoint getSpawnPoint(Direction direction) {
            SpawnPoint[] points = spawns.get(direction);
            if (points == null || points.length == 0) {
                // Fallback spawn point if none defined for this direction
                return new SpawnPoint(50, 50);
            }
            return points[new Random().nextInt(points.length)];
        }
    }
    public boolean isInBaseMap() {
        return getCurrentMapPath().equals(BASE_MAP);
    }

    public String getCurrentMapPath() {
        return gamePanel.map.getCurrentMapPath();
    }
}