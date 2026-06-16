package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.JSONObject;

public class StartPanel extends JPanel {
    private JTextField widthField = new JTextField();
    private JTextField heightField = new JTextField();
    private int width;
    private int mazeWidth=30;
    private int mazeHeight=30;
    private String wallColorString;
    private String pathColorString;
    private boolean drawGrid;
    private String gridColorString;
    private int animationDelay;
    private int cellSize;
    private JLabel wallColorLabel = new JLabel();
    private JLabel pathColorLabel = new JLabel();
    private JLabel drawGridLabel = new JLabel();
    private JLabel gridColorLabel = new JLabel();
    private JLabel animationDelayMsLabel = new JLabel();
    private JButton getMaze = new JButton("Get Maze");
    private JButton refresh = new JButton("Refresh");
    private BufferedImage tempMazeImage;
    private BufferedImage finalMazeImage;
    private BufferedImage rawMazeImage;


    public StartPanel(int x, int y, int width, int height) {
        this.width = width;
        this.setBounds(x, y, width, height);
        this.setLayout(null);


        //welcome text
        JLabel welcome = new JLabel("WELCOME");
        Font font = new Font("Times New Roman", Font.BOLD, 20);
        welcome.setFont(font);
        welcome.setBounds(width / 2 - 50, 10, width / 2, height / 10);
        this.add(welcome);

        //enter width/height
        JLabel enterWidth = new JLabel("Enter Width :");
        JLabel enterHeight = new JLabel("Enter Height :");
        enterWidth.setBounds(0, 200, width / 3, height / 10);
        enterHeight.setBounds(0, 300, width / 3, height / 10);
        this.widthField.setBounds(80, 220, width / 8, height / 20);
        this.heightField.setBounds(80, 320, width / 8, height / 20);
        this.add(enterWidth);
        this.add(this.widthField);
        this.add(enterHeight);
        this.add(this.heightField);

        //get maze button
        this.getMaze.setBounds(width / 2 - 100, height - 120, width / 8, height / 10);
        this.getMaze.setBackground(Color.GRAY);
        this.getMaze.setVisible(true);
        this.getMaze.addActionListener(e -> {
            getMaze(widthField, heightField); // מוריד ל-tempMazeImage

            if (this.tempMazeImage != null) {
                // מכינים את הכל כאן!
                BufferedImage raw = createRawImage(tempMazeImage, getWallColor(), drawGrid);
                BufferedImage display = (drawGrid) ? addGridToImage(raw, getGridColor()) : raw;
                this.cellSize= Math.max(1, tempMazeImage.getWidth() / this.mazeWidth);

                // מעדכנים את ה-Main עם התמונות שכבר מוכנות
                Main.changePanel(raw, display);
            }
        });

        //refresh button
        this.refresh.setBounds(width / 2 - 100, height - 120 - height / 9, width / 8, height / 10);
        this.refresh.setBackground(Color.GRAY);
        this.refresh.setVisible(true);
        this.refresh.addActionListener(e -> {
            getBackend();
        });
        getBackend();


        //adds
        this.add(wallColorLabel);
        this.add(pathColorLabel);
        this.add(drawGridLabel);
        this.add(gridColorLabel);
        this.add(animationDelayMsLabel);
        this.add(getMaze);
        this.add(refresh);
    }

    //פונקציית קבלת ההגדרות
    public void getBackend() {

        HttpResponse responseBackend = Unirest.get("https://backend-qcf9.onrender.com/fm1/get-render-config").asString();
        String body = responseBackend.getBody().toString();
        JSONObject jsonBackend = new JSONObject(body);

        //הגבדרת שדות ההגדרות
        this.gridColorString = jsonBackend.getString("gridColor");
        this.pathColorString = jsonBackend.getString("pathColor");
        this.wallColorString = jsonBackend.getString("wallCellColor");
        this.animationDelay = jsonBackend.getInt("animationDelayMs");
        this.drawGrid = jsonBackend.getBoolean("drawGrid");

        // הגדרת הlabels של ההגדרות
        this.wallColorLabel.setText("wall color ; " + this.wallColorString);
        this.pathColorLabel.setText("path color ; " + this.pathColorString);
        String drawGridString = "" + this.drawGrid;
        this.drawGridLabel.setText("draw grid ; " + drawGridString);
        this.gridColorLabel.setText("grid color ; " + this.gridColorString);
        String animationDelayMsString = "" + this.animationDelay;
        this.animationDelayMsLabel.setText("animation delay ; " + animationDelayMsString);

        //מיקומי הlabels
        this.wallColorLabel.setBounds(width - width / 3, 100, width / 3, 20);
        this.pathColorLabel.setBounds(width - width / 3, 130, width / 3, 20);
        this.drawGridLabel.setBounds(width - width / 3, 160, width / 3, 20);
        this.gridColorLabel.setBounds(width - width / 3, 190, width / 3, 20);
        this.animationDelayMsLabel.setBounds(width - width / 3, 220, width / 3, 20);
    }

    public void getMaze(JTextField widthField, JTextField heightField) {

//בדיקת ערכי הרוחב והאורך
        try {
            this.mazeWidth = Integer.parseInt(widthField.getText().trim());
        } catch (Exception e) {
            this.mazeWidth = 30;
        }
        try {
            this.mazeHeight = Integer.parseInt(heightField.getText().trim());
        } catch (Exception e) {
            this.mazeHeight = 30;
        }
        if (this.mazeWidth > 100 || this.mazeHeight < 5 || mazeHeight > 100 || mazeHeight < 5) {
            this.mazeWidth = 30;
            this.mazeHeight = 30;
        }

        //בקשת התמונה של המבוך
        String url="https://backend-qcf9.onrender.com/fm1/get-maze-image?width="+this.mazeWidth+"&height="+this.mazeHeight;
        HttpResponse<byte[]> responseMaze = Unirest.get(url).asBytes();
        try {
            this.tempMazeImage = ImageIO.read(new ByteArrayInputStream(responseMaze.getBody()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //יצירת התמונה של המבוך
    public BufferedImage createRawImage(BufferedImage originalImage, Color wallColor , boolean drawGrid) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage mazeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Color color = new Color(originalImage.getRGB(j, i));

                if (color.getRed() == 255 && color.getGreen() == 255 && color.getBlue() == 255) {
                    mazeImage.setRGB(j, i, Color.WHITE.getRGB());
                } else {
                    mazeImage.setRGB(j, i, wallColor.getRGB());
                }
            }
        }
        this.rawMazeImage = mazeImage;
        this.finalMazeImage = mazeImage;
        if(rawMazeImage==null){
            System.out.println("null");
        }

        if(drawGrid){
            return addGridToImage(this.finalMazeImage , this.getPathColor());
        }
        return this.finalMazeImage;
    }

    //הוספת הרשת
    public BufferedImage addGridToImage(BufferedImage rawMazeImage, Color gridColor) {

        Graphics2D g2 = rawMazeImage.createGraphics();
            g2.setColor(gridColor);
            this.cellSize = rawMazeImage.getWidth()/this.mazeWidth;
            for (int x = cellSize; x < rawMazeImage.getWidth(); x += cellSize) g2.drawLine(x, 0, x, rawMazeImage.getHeight());
            for (int y = cellSize; y < rawMazeImage.getHeight(); y += cellSize) g2.drawLine(0, y, rawMazeImage.getWidth(), y);
        g2.dispose();
        return rawMazeImage;
    }

    //geters של שדות ההגדרות
    public Color getWallColor() {
        Color wallColor = Color.decode(this.wallColorString);
        return wallColor;
    }

    public Color getPathColor() {
        Color pathColor = Color.decode(this.pathColorString);
        return pathColor;
    }

    public Color getGridColor() {
        Color gridColor = Color.decode(this.gridColorString);
        return gridColor;
    }

    public int getAnimationDelay() {
        return this.animationDelay;
    }

    public boolean isDrawGrid() {
        return this.drawGrid;
    }

    public BufferedImage getTempMazeImage() {
        return this.tempMazeImage;
    }

    public BufferedImage getRawMazeImage() {
        return this.rawMazeImage;
    }

    public BufferedImage getFinalMazeImage() {
        return this.finalMazeImage;
    }

    public int getCellSize() {
        return this.cellSize;
    }

}