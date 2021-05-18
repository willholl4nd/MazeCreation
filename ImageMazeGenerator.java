import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Description:
 * Date Last Modified: 03/19/2020
 *
 * @author Will Holland
 * CS1131, Fall 2019
 * Section 1
 */

public class ImageMazeGenerator {
    WritableImage w;
    Position start;
    Position[][] positions;
    int screenWidth = 8, screenHeight = 8;
    int width = screenWidth-1, height = screenHeight-1;
    Color initialColor = Color.SKYBLUE;
    Color borderColor = Color.BLACK;


    public ImageMazeGenerator() throws IOException {
        Timer t = new Timer();
        System.out.println("Start");
        t.start();
        setupWritableImage();
        t.end();
        System.out.println("Took " + t.getTimeFromStart());

        System.out.println("\nInitializing positions in memory");
        t.clear();
        t.start();
        positions = new Position[width][height];
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                boolean specialCase = row == 0 && col == 1 || row == 1 && col == 1 || row == width-1 && col == height-2;
                positions[row][col] = new Position(row, col, specialCase ? borderColor : initialColor);
            }
        }
        System.out.println("Done initializing positions");
        t.end();
        System.out.println("Took " + t.getTimeFromStart());

        System.out.println("\nArranging neighbors and walls");
        t.clear();
        t.start();
        for (int row = 1; row < positions.length-1; row++) {
            for (int col = 1; col < positions[0].length-1; col++) {
                positions[row][col].findNeighbors();
                positions[row][col].findWalls();
            }
        }
        t.end();
        System.out.println("Done arranging neighbors and walls");
        System.out.println("Took " + t.getTimeFromStart());

        System.out.println("\nSaving initial file");
        t.clear();
        t.start();
        saveImage(true);
        t.end();
        System.out.println("Done saving");
        System.out.println("Took " + t.getTimeFromStart());

        start = positions[1][1];
        Position current = start;
        start.visited = true;
        Stack<Position> backTrack = new Stack<>();
        backTrack.push(start);
        long iter = 0;
        System.out.println("\nStarting backtracking algorithm");
        t.clear();
        t.start();

        while (!backTrack.isEmpty()) {
            iter++;
            if (current.hasNeighbors()) {
                Position neighbor = current.getRandomNeighbor();
                neighbor.color = Color.BLACK;
                current.removeWall(neighbor);
                backTrack.push(neighbor);
                neighbor.visited = true;
                current = neighbor;
            } else {
                current = backTrack.pop();
            }
        }
        t.end();
        System.out.println("Backtracking done after " + iter + " iterations");
        System.out.println("Took " + t.getTimeFromStart());

        System.out.println("\nSaving final file");
        t.clear();
        t.start();
        saveImage(false);
        t.end();
        System.out.println("Done saving");
        System.out.println("Took " + t.getTimeFromStart());
    }

    //TODO Make it so that it only iterates through positions if it isn't the initial save
    public void saveImage(boolean isInitial) throws IOException {
        File f = new File("new2.png");
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                w.getPixelWriter().setColor(row, col, positions[row][col].color);
            }
        }

        BufferedImage image = SwingFXUtils.fromFXImage(w, null);
        ImageIO.write(image, "png", f);
    }

    public void setupWritableImage() {
        System.out.println("\nSetting up writable image");
        w = new WritableImage(width, height);
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                w.getPixelWriter().setColor(row, col, initialColor);
            }
        }
        w.getPixelWriter().setColor(0, 1, borderColor);
        w.getPixelWriter().setColor(width-1, height-2, borderColor);
        System.out.println("Done setting up writable image");
    }


//    @Override
//    public void start(Stage primaryStage) {
//
//        Pane p = new Pane();
//        p.getChildren().addAll(new ImageView(w));
//        primaryStage = new Stage();
//        primaryStage.setScene(new Scene(p, screenWidth, screenHeight));
//        primaryStage.show();
//    }

    public static void main(String[] args) {
        Timer t = new Timer();
        t.start();
        try {
            new ImageMazeGenerator();
        } catch (IOException e) {
            e.printStackTrace();
        }
        t.end();
        System.out.println("Overall took " + t.getTimeFromStart());
    }

    public class Position {
        int row, col;
        Color color;
        Position[] neighbors;
        Position[] walls;
        boolean visited;

        public Position(int row, int col, Color color) {
            this.row = row;
            this.col = col;
            this.color = color;
            neighbors = new Position[4];
            walls = new Position[4];
            visited = false;
        }

        public void findNeighbors() {
            if (isValidRowIndex(row - 2)) {
                neighbors[0] = positions[row - 2][col];
            }
            if (isValidColIndex(col + 2)) {
                neighbors[1] = positions[row][col + 2];
            }
            if (isValidRowIndex(row + 2)) {
                neighbors[2] = positions[row + 2][col];
            }
            if (isValidColIndex(col - 2)) {
                neighbors[3] = positions[row][col - 2];
            }
        }

        public void findWalls() {
            if (isValidRowIndex(row - 1)) {
                walls[0] = positions[row - 1][col];
            }
            if (isValidColIndex(col + 1)) {
                walls[1] = positions[row][col + 1];
            }
            if (isValidRowIndex(row + 1)) {
                walls[2] = positions[row + 1][col];
            }
            if (isValidColIndex(col - 1)) {
                walls[3] = positions[row][col - 1];
            }
        }

        public Position getRandomNeighbor() {
            ArrayList<Position> pos = new ArrayList<>();
            for (Position p : neighbors) {
                if (p != null){
                    if(!p.visited) pos.add(p);
                }
            }
            return pos.get((int) (pos.size() * Math.random()));
        }

        public void removeWall(Position neighbor) {
            int rowDif = neighbor.row-this.row;
            int colDif = neighbor.col-this.col;
            if(Math.abs(rowDif) == 2) positions[row+rowDif/2][col].color = Color.BLACK;
            if(Math.abs(colDif) == 2) positions[row][col+colDif/2].color = Color.BLACK;
        }

        public boolean hasNeighbors() {
            for (int i = 0; i < 4; i++) {
                if (neighbors[i] != null) {
                    if (!neighbors[i].visited) return true;
                }
            }
            return false;
        }

        public int getCount() {
            int out = 0;
            for (int i = 0; i < 4; i++) {
                if (neighbors[i] != null) {
                    out++;
                }
            }
            return out;
        }

        public boolean isValidRowIndex(int i) {
            return i > 0 && i < positions.length;
        }

        public boolean isValidColIndex(int j) {
            return j > 0 && j < positions[0].length-1;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + row +
                    ", y=" + col +
                    ", color=" + color.toString() +
                    ", neighborCount=" + getCount() +
                    '}';
        }
    }
}

