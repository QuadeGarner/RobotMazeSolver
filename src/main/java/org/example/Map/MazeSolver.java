package org.example.Map;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.PriorityQueue;

public class MazeSolver extends Application {
    int startRow = 1, startCol = 1;
    int endRow = -1, endCol = -1;
    private Rectangle[][] rectGrid = new Rectangle[10][13];
    private int[][] discoveredMap = new int[10][13];
    private final int[][] maze = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1}, // Start at (1,1)
            {1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 0, 1}, // Large open vertical gap at col 5 & 7
            {1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 3, 1}, // Exit at (8,11)
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    @Override
    public void start(Stage primaryStage){
        GridPane grid = new GridPane();

        for(int i = 0; i < maze.length; i++){
            for(int j = 0; j < maze[i].length; j++){
                Rectangle rectangle = new Rectangle(40,40);
                    rectangle.setFill(Color.LIGHTGRAY);
                grid.add(rectangle, j, i);
                rectGrid[i][j] = rectangle;
            }
        }
        Button btn = new Button("Solve Maze");
        Button aStarBtn = new Button("Solve Maze with A*");
        Button solveWithDisBtn = new Button("Solve With Discovery");
        Button resetBtn = new Button("Reset Maze");
        btn.setOnAction(event -> {
            solve(1,1);
            findEndOfMaze();
            if (endRow == -1) {
                System.out.println("Exit (3) not found in the maze!");
                return;
            }
            updateVisuals();

        });
        aStarBtn.setOnAction(event->{
            findEndOfMaze();
            if (endRow == -1) {
                System.out.println("Exit (3) not found in the maze!");
                return;
            }
            solveAStar(startRow, startCol, endRow, endCol);
            updateVisuals();
        });
        solveWithDisBtn.setOnAction(event -> {
            solveWithDiscovery();
        });
        resetBtn.setOnAction(event ->{
            resetMaze();
        });
        grid.add(btn, 0,10,5,1);
        grid.add(aStarBtn, 0, 13, 5, 2);
        grid.add(solveWithDisBtn, 0, 15, 5, 3);
        grid.add(resetBtn, 0, 17, 5, 4);

        Scene scene = new Scene(grid, 400,400);

        primaryStage.setTitle("Robot Maze Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void findEndOfMaze() {
        for(int i = 0; i < maze.length; i ++){
            for (int j = 0; j < maze[0].length; j++){
                if(maze[i][j] == 3){
                    endRow = i;
                    endCol = j;
                }
            }
        }
    }

    private void updateVisuals() {
        for(int i = 0; i < maze.length; i++){
            for(int j = 0; j < maze[i].length; j++){
                Rectangle rect = rectGrid[i][j];
                if(maze[i][j]== 2){
                    rect.setFill(Color.GOLD);
                }else if(maze[i][j] == 5){
                    rect.setFill(Color.BROWN);
                } else if (maze[i][j] == 4) {
                    rect.setFill(Color.BLUE);
                }else if(maze[i][j] == 0){
                    rect.setFill(Color.WHITE);
                }
            }
        }
    }
    public void resetMaze(){
        for(int i = 0; i < maze.length; i ++){
            for (int j = 0; j < maze[0].length; j++){
                if(maze[i][j] != 3 && maze[i][j] != 1){
                    maze[i][j] = 0;

                }
            }
        }
        updateVisuals();
    }


    public boolean solve(int row, int col){
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[row].length) {
            return false;
        }
        if ( maze[row][col] == 3){
            return true;
        }
        if (maze[row][col]== 1 || maze [row][col]==2 || maze[row][col]== 5){
            return false;
        }

        maze[row][col] = 5;


        return solve(row + 1, col) ||
                solve(row - 1, col) ||
                solve(row, col + 1) ||
                solve(row, col - 1);
    }


    public void solveAStar(int startRow, int startCol, int endRow, int endCol){
        PriorityQueue<Node> openList = new PriorityQueue<>();
        boolean[][]closedList = new boolean[maze.length][maze[0].length];

        int initialH = Math.abs(startRow - endRow) + Math.abs(startCol - endCol);
        openList.add(new Node(startRow, startCol, 0,initialH ,null));

        while( !openList.isEmpty()){
            Node current = openList.poll();

            if(closedList[current.row][current.col]) continue;

            closedList[current.row][current.col] = true;
            if(maze[current.row][current.col]==3) {
                retracePath(current);
                return;
            }
            int[][] directions ={{1,0},{-1,0},{0,1},{0,-1}};
            for (int[] dir : directions){
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];

                if (isValid(newRow, newCol)&& maze[newRow][newCol] != 1 && !closedList[newRow][newCol]){
                    int g = current.g + 1;
                    int h = Math.abs(newRow - endRow)+ Math.abs(newCol - endCol);
                    openList.add(new Node(newRow, newCol, g, h, current));
                    if (maze[newRow][newCol] == 0){
                        maze[newRow][newCol] = 4;
                    }
                }
            }
        }
    }
    private void retracePath(Node node){
        Node temp = node;
        while(temp != null){
            maze[temp.row][temp.col]= 2;
            temp= temp.parent;
        }
        updateVisuals();
    }
    private boolean isValid(int para1, int para2){

        return (para1 >=0 && para1 < maze.length) && (para2 >= 0 && maze[0].length > para2);
    }
    private void scanEnvironment(int row, int col) {
        int scannerRange = 2;
        for (int i = -scannerRange; i <= scannerRange; i++) {
            for (int j = -scannerRange; j <= scannerRange; j++) {
                int scanRow = row + i;
                int scanCol = col + j;
                if (isValid(scanRow, scanCol)) {
                    discoveredMap[scanRow][scanCol] = maze[scanRow][scanCol];

                    if (discoveredMap[scanRow][scanCol] == 1) {
                        rectGrid[scanRow][scanCol].setFill(Color.BLACK);
                    } else if (discoveredMap[scanRow][scanCol] == 0) {
                        rectGrid[scanRow][scanCol].setFill(Color.WHITE);
                    } else if (discoveredMap[scanRow][scanCol]== 3) {
                        rectGrid[scanRow][scanCol].setFill(Color.RED);
                    }
                }
            }
        }
    }
    public void solveWithDiscovery(){
       scanEnvironment(startRow, startCol);

       Node targetNode = planNextMove(startRow, startCol);

       if(targetNode != null){
           if(startRow == endRow && startCol == endCol){
               return;
           }
           Node step = targetNode;
           while(step.parent != null && step.parent.parent != null){
               step = step.parent;
           }
           startRow = step.row;
           startCol = step.col;

           rectGrid[startRow][startCol].setFill(Color.GREEN);

           javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(100));
           pause.setOnFinished( event -> solveWithDiscovery());
           pause.play();
       }
    }

    private Node planNextMove(int currentRow, int currentCol) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        boolean[][] closed = new boolean[10][13];

        findEndOfMaze();
        int startH = Math.abs(currentRow- endRow)+ Math.abs(currentCol- endCol);
        openList.add(new Node(currentRow, currentCol, 0, startH, null));

        while(!openList.isEmpty()){
            Node current = openList.poll();
            if(maze[current.row][current.col] == 3) return current;

            if(closed[current.row][current.col])continue;

            closed[current.row][current.col] = true;

            int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
            for(int[] dir : directions){
                int newRow = current.row + dir[0];
                int newCow = current.col + dir[1];

                // treat unknown cell as walkable
                if(isValid(newRow, newCow) && !closed[newRow][newCow] && discoveredMap[newRow][newCow] != 1){
                    int g = current.g + 1;
                    int h =Math.abs(newRow - endRow) + Math.abs(newCow - endCol);
                    openList.add(new Node(newRow, newCow, g, h, current));
                }
            }
        }
        return null;
    }

    public static void main(String[] args){
        launch(args);
    }
}