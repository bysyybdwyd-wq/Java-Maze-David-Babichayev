package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.util.*;
import java.util.List;

public class MazePanel extends JPanel {
    private BufferedImage rawMazeImage;
    private BufferedImage displayMazeImage;
    private Color wallColor, pathColor;
    private int animationDelay;
    private  int cellSize;
    private List<Point> solutionPath;
    private int currentStep = 0;
    private boolean[][] mazeGrid;
    private Timer timer;
    private boolean isSolving = false;


    public MazePanel(BufferedImage rawMazeImage, BufferedImage mazeImage, Color wallColor, Color pathColor, int animationDelay,int cellSize) {
        this.rawMazeImage = rawMazeImage;
        this.displayMazeImage = mazeImage;
        this.wallColor = wallColor;
        this.pathColor = pathColor;
        this.animationDelay = animationDelay;
        this.cellSize =cellSize;
        //יצירת המפה הלוגית פעם אחת
        this.mazeGrid = createGridFromImage(this.rawMazeImage);

        this.setSize(this.displayMazeImage.getWidth(), this.displayMazeImage.getHeight());
        this.setLayout(null);
    }

    // 1. הפיכת תמונה למטריצה של True/False
    private boolean[][] createGridFromImage(BufferedImage img) {
        int rows = img.getHeight() / this.cellSize;
        int cols = img.getWidth() / this.cellSize;
        boolean[][] grid = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Color pixelColor = new Color(img.getRGB(j * cellSize + cellSize/2, i * cellSize + cellSize/2));


                int brightness = (pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue()) / 3;
                grid[i][j] = (brightness > 200); // כל מה שבהיר מ-200 הוא דרך
            }
        }
        return grid;
    }


    // 2. BFS - החזרת מסלול
    public List<Point> solveBFS(Point start, Point end) {
        int rows = mazeGrid.length;
        int cols = mazeGrid[0].length;


        Point[][] parent = new Point[rows][cols];
        boolean[][] visited = new boolean[rows][cols];
        Queue<Point> queue = new LinkedList<>();

        queue.add(start);
        visited[start.y][start.x] = true;

        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        while (!queue.isEmpty()) {
            Point curr = queue.poll();
            if (curr.distance(end) < 2.0) {
                return reconstructPath(parent, start, curr);
            }

            for (int i = 0; i < 4; i++) {
                int nx = curr.x + dx[i], ny = curr.y + dy[i];
                if (ny >= 0 && ny < rows && nx >= 0 && nx < cols && mazeGrid[ny][nx] && !visited[ny][nx]) {
                    visited[ny][nx] = true;
                    parent[ny][nx] = curr;
                    queue.add(new Point(nx, ny));
                }
            }
        }
        return new ArrayList<>();
    }

    private List<Point> reconstructPath(Point[][] parent, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        Point curr = end;
        while (curr != null) {
            path.add(curr);
            curr = parent[curr.y][curr.x];
        }
        Collections.reverse(path);
        return path;
    }

    // 3. אנימציה
    public void startAnimation(List<Point> fullPath) {
        this.solutionPath = fullPath;
        this.currentStep = 0;
        this.isSolving = true;

        if (this.timer != null && this.timer.isRunning()) {
            this.timer.stop();
        }

        this.timer = new Timer(this.animationDelay, e -> {
            // שינוי: הוספת סימן שווה כדי לאפשר ל-currentStep להגיע ל-size
            if (this.currentStep <= this.solutionPath.size()) {
//                repaint(); // קודם מציירים
                currentStep++; // אחר כך מגדילים
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
                this.isSolving = false;
            }
        });
        this.timer.start();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        if (displayMazeImage != null) g2.drawImage(displayMazeImage, 0, 0, null);

        if (solutionPath != null) {
            g2.setColor(pathColor);
            for (int i = 0; i < currentStep && i< solutionPath.size(); i++) {
                Point p = solutionPath.get(i);
                g2.fillRect(p.x * cellSize, p.y * cellSize, cellSize, cellSize);
            }
        }
    }
    public int getCellSize() {
        return cellSize;
    }
    public int getMazeGridRows() { return mazeGrid.length; }
    public int getMazeGridCols() { return mazeGrid[0].length; }
    public boolean isSolving() {
        return isSolving;
    }
}
