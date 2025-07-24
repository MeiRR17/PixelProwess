package game.object.mob;

import main.menu.GamePanel;
import game.map.Map;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PathFinder {
    private final GamePanel gamePanel;
    private final Map map;
    private Node[][] grid;
    private final int tileSize;

    private static class Node implements Comparable<Node> {
        int x, y;
        double g = Double.MAX_VALUE; // Cost from start to this node
        double h = 0; // Heuristic (estimated cost to goal)
        Node parent = null;
        boolean isWall = false;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        double f() {
            return g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f(), other.f());
        }
    }

    public PathFinder(GamePanel gamePanel, Map map) {
        this.gamePanel = gamePanel;
        this.map = map;
        this.tileSize = gamePanel.tileSize;
        initializeGrid();
    }

    private void initializeGrid() {
        grid = new Node[gamePanel.worldColumn][gamePanel.worldRow];

        // Initialize nodes
        for (int x = 0; x < gamePanel.worldColumn; x++) {
            for (int y = 0; y < gamePanel.worldRow; y++) {
                grid[x][y] = new Node(x, y);
                int tileNum = map.mapNumber[x][y];
                grid[x][y].isWall = map.tiles[tileNum].collision ||
                        map.tiles[tileNum].customCollision;
            }
        }
    }

    public List<Point> findPath(int startX, int startY, int goalX, int goalY) {
        // Convert world coordinates to grid coordinates
        int startGridX = startX / tileSize;
        int startGridY = startY / tileSize;
        int goalGridX = goalX / tileSize;
        int goalGridY = goalY / tileSize;

        // Ensure coordinates are within bounds
        if (!isValidCoordinate(startGridX, startGridY) ||
                !isValidCoordinate(goalGridX, goalGridY)) {
            return new ArrayList<>();
        }

        // Reset nodes for new path calculation
        for (Node[] row : grid) {
            for (Node node : row) {
                node.g = Double.MAX_VALUE;
                node.h = 0;
                node.parent = null;

                // Improve custom collision handling
                int tileNum = Map.mapNumber[node.x][node.y];
                if (Map.tiles[tileNum].customCollision) {
                    // Check if the point is actually inside the custom collision polygon
                    Polygon collisionPoly = map.customCollisionBounds[node.x][node.y];
                    if (collisionPoly != null) {
                        // Mark as wall only if the center point is inside the polygon
                        int centerX = node.x * tileSize + tileSize / 2;
                        int centerY = node.y * tileSize + tileSize / 2;
                        node.isWall = collisionPoly.contains(centerX, centerY);
                    }
                }
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Node> closedSet = new HashSet<>();

        Node startNode = grid[startGridX][startGridY];
        Node goalNode = grid[goalGridX][goalGridY];

        startNode.g = 0;
        startNode.h = calculateHeuristic(startNode, goalNode);
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current == goalNode) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            // Check all 8 neighbors
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    int newX = current.x + dx;
                    int newY = current.y + dy;

                    if (!isValidCoordinate(newX, newY)) continue;

                    Node neighbor = grid[newX][newY];
                    if (neighbor.isWall || closedSet.contains(neighbor)) continue;

                    // Calculate new cost (diagonal movement costs more)
                    double moveCost = (dx == 0 || dy == 0) ? 1.0 : 1.414;
                    double tentativeG = current.g + moveCost;

                    if (tentativeG < neighbor.g) {
                        neighbor.parent = current;
                        neighbor.g = tentativeG;
                        neighbor.h = calculateHeuristic(neighbor, goalNode);

                        if (!openSet.contains(neighbor)) {
                            openSet.add(neighbor);
                        }
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < gamePanel.worldColumn &&
                y >= 0 && y < gamePanel.worldRow;
    }

    private double calculateHeuristic(Node from, Node to) {
        // Using Euclidean distance as heuristic
        double dx = from.x - to.x;
        double dy = from.y - to.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private List<Point> reconstructPath(Node goal) {
        List<Point> path = new ArrayList<>();
        Node current = goal;

        while (current != null) {
            // Convert grid coordinates back to world coordinates
            path.add(0, new Point(
                    current.x * tileSize + tileSize / 2,
                    current.y * tileSize + tileSize / 2
            ));
            current = current.parent;
        }

        return path;
    }
}