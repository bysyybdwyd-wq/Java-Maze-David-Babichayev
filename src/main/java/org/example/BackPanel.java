package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BackPanel extends JPanel {

    private MazePanel mazePanel;
    private JButton checkSolutionButton = new JButton("Check Solution");


    public  BackPanel(int width, int height,BufferedImage rawMazeImage, BufferedImage mazeImage , Color wallColor,Color pathColor , int animationDilay , int cellSize) {
        this.setBounds(0,0,width,height);
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        //יצירת פאנל המבוך
        this.mazePanel = new MazePanel(rawMazeImage, mazeImage, wallColor, pathColor, animationDilay , cellSize);
        this.add(mazePanel,  BorderLayout.CENTER);
        this.mazePanel.setPreferredSize(new Dimension(width, height - 60));

        //יצירת הכפתור
        this.checkSolutionButton.setBackground(Color.GRAY);
        this.checkSolutionButton.setPreferredSize(new Dimension(width, 50));
        this.checkSolutionButton.setVisible(true);
        this.add(checkSolutionButton, BorderLayout.SOUTH);

        this.checkSolutionButton.addActionListener(e -> {
            if(!this.mazePanel.isSolving()) {
                Point start = new Point(0, 0);

                int rows = mazePanel.getMazeGridRows();
                int cols = mazePanel.getMazeGridCols();
                Point end = new Point(cols - 1, rows - 1);


                // 2. קריאה לאלגוריתם BFS
                List<Point> path = mazePanel.solveBFS(start, end);

                // 3. בדיקה אם נמצא פתרון והפעלת האנימציה
                if (path != null && !((java.util.List<?>) path).isEmpty()) {
                    System.out.println("Path found! Starting animation...");
                    mazePanel.startAnimation(path);
                } else {
                    JOptionPane.showMessageDialog(this, "No Solution");
                }
            }
            });


    }
}
