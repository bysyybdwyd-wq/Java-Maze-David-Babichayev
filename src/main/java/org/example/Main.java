package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
    //המחשב שלי קטן ולכן לא יכולתי לעשות את החלון גדול מידי
    //למרות שאני יודע שבערכים המקסימליים של המבוך זה יכול לא להספיק
    public static final int width = 1000;
    public static final int height = 700;
    private static CardLayout cardLayout = new CardLayout();
    private static StartPanel startPanel=new StartPanel(0,0,width,height);
    private static JPanel mainConteimer =new JPanel(cardLayout);
    private static BackPanel backPanel;

  public static void main() {

      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setSize(width,height);
      frame.setResizable(false);
      frame.setLocationRelativeTo(null);
      frame.setLayout(cardLayout);
      frame.add(mainConteimer);
      mainConteimer.add(startPanel,"StartPanel");
      cardLayout.show(mainConteimer,"StartPanel");
      frame.setVisible(true);
    }
    public static void changePanel(BufferedImage raw, BufferedImage display) {
        backPanel = new BackPanel(width, height, raw, display, startPanel.getWallColor() , startPanel.getPathColor(),startPanel.getAnimationDelay(), startPanel.getCellSize());
        mainConteimer.add(backPanel, "BackPanel");
        cardLayout.show(mainConteimer, "BackPanel");
    }
}
