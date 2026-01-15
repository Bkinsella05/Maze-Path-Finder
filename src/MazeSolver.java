import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Maze Solver object.
 *
 * Purdue University -- CS18000 -- Fall 2024 -- Challenge
 *
 * @author Bobby Kinsella
 * @version Decembe 2, 2024
 */

public class MazeSolver {

    private Maze maze;

    public MazeSolver() { }

    public void validateChar(Character c) throws InvalidMazeException {
        if (c != 'P' && c != 'W') throw new InvalidMazeException("Invalid Maze!(not a w or p)");
    }

    public void validatePoint(int value, int range) throws InvalidMazeException {
        if (value < 0 || value > range) throw new InvalidMazeException("Invalid Start/End!(Outside maze");
    }

    public void readMaze(String filename) throws InvalidMazeException, IOException {
        try {
            //If the Maze does not have a name then it is invalid.
            //If the Maze is not rectangular (squares are rectangles) then the Maze is invalid.
            //If the Maze does not have a start or an end it is invalid.
            //If the start or end values are non-integer the Maze is invalid.
            //If the start or end values are not within the grid then the Maze is invalid.
            //If the start or end square is not a P then the Maze is invalid.
            //If the Maze contains square that are not a W or P then it is invalid.
            //Initializes variables
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String name;
            int startX;
            int startY;
            int endX;
            int endY;
            char[][] grid = new char[][]{};
            int gridWidth = 0; //if whole grid has same width -> rectangle

            //Checks name
            name = br.readLine();
            if (name.contains("Start: ") || name.contains("End: ") || name.indexOf(",") > 0 || name.isEmpty()) {
                throw new Exception("Invalid Name");
            }
            //Gets start line, checks format
            String start = br.readLine();
            if (!start.contains("Start: ") || !start.contains("-")) {
                throw new InvalidMazeException("Invalid Start");
            }
            //Gets end line, checks format
            String end = br.readLine();
            if (!end.contains("End: ") || !end.contains("-")) {
                throw new InvalidMazeException("Invalid End");
            }
            //Checks for invalid integers in start and end
            try {
                startY = Integer.parseInt(start.substring(7, start.indexOf("-")));
                startX = Integer.parseInt(start.substring(start.indexOf("-") + 1));

                endY = Integer.parseInt(end.substring(5, end.indexOf("-")));
                endX = Integer.parseInt(end.substring(end.indexOf("-") + 1));
            } catch (Exception e) {
                throw new InvalidMazeException("Invalid Start/End(non-int)");
            }

            //Reads maze
            String row = "";
            int rowIndex = 0;
            while ((row = br.readLine()) != null) {
                if (gridWidth == 0) {
                    gridWidth = row.length() / 2 + 1;
                }
                if (gridWidth != row.length() / 2 + 1) {
                    throw new InvalidMazeException("Invalid maze!(Grid not rectangle)");
                }
                validateChar(row.charAt(0));
                //Creates the maze array row by row
                grid = Arrays.copyOf(grid, grid.length + 1);
                grid[rowIndex] = new char[gridWidth];
                grid[rowIndex][0] = row.charAt(0);

                for (int charAt = 2; charAt < row.length(); charAt += 2) {
                    validateChar(row.charAt(charAt));
                    grid[rowIndex][charAt / 2] = row.charAt(charAt);
                }
                rowIndex++;
            }

            //Validates x and y points
            validatePoint(startX, gridWidth);
            validatePoint(startY, grid.length);
            validatePoint(endX, gridWidth);
            validatePoint(endY, grid.length);

            validateChar(grid[startY][startX]);
            validateChar(grid[endY][endX]);

            maze = new Maze(name, grid, new int[]{startY, startX}, new int[]{endY, endX});
            br.close();
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw new InvalidMazeException(e.getMessage());
        }
    }

    /**
     * A Path Finder object.
     *
     * Purdue University -- CS18000 -- Fall 2024 -- Challenge
     *
     * @author Bobby Kinsella
     * @version December 2, 2024
     */
    private class PathFinder {
        //Points to same spot in memory as others, essentially static
        public char[][] charGrid;
        private ArrayList<PathFinder> finders;
        private final int[] end;
        public final int[][] path;

        //For the first instance
        public PathFinder(int[][] path, int[] end, char[][] grid) {
            this.path = path;
            this.end = end;
            this.charGrid = grid;
            this.finders = new ArrayList<PathFinder>();
            finders.add(this);
        }

        public PathFinder(int[][] path, int[] end, char[][] grid, ArrayList<PathFinder> finders) {
            this.path = path;
            this.charGrid = grid;
            this.end = end;
            this.finders = finders;
            int currX = path[path.length - 1][1];
            int currY = path[path.length - 1][0];
            this.charGrid[currY][currX] = 'W';
        }


        public boolean createNewFinders() {
            int[] north = new int[]{-1, 0};
            int[] south = new int[]{1, 0};
            int[] east = new int[]{0, 1};
            int[] west = new int[]{0, -1};

            int[][] directionList = new int[][]{north, south, east, west};
            boolean wallHit = true;

            for (int i = 0; i < directionList.length; i++) {
                int yCoord = path[path.length - 1][0] + directionList[i][0];
                int xCoord = path[path.length - 1][1] + directionList[i][1];
                try {
                    if (charGrid[yCoord][xCoord] == 'P') { //checks for path
                        //Copies path and adds new position to end
                        int[][] newPath = Arrays.copyOf(path, path.length + 1);
                        newPath[newPath.length - 1] = new int[]{yCoord, xCoord};
                        //Adds the new finder to the list of PathFinders
                        PathFinder newFinder = new PathFinder(newPath, end, charGrid, finders);
                        finders.add(newFinder);
                        wallHit = false; //To save processing time
                    }
                } catch (IndexOutOfBoundsException indexOutOfBounds) {
                    continue;
                }
            }
            return wallHit;
        }

        //COULD DECIDE TO MAKE NEW PATHFINDERS ADD TO THE PATH THE FIRST TIME IF DELETING THIS OBJECT DOESN'T WORK
        public boolean move() {

            if (Arrays.equals(path[path.length - 1], end)) {
                return true;
            }
            boolean hitWall = createNewFinders();

            //KEY: by removing the possibility of referencing 'this', object gets deleted
            finders.remove(this);

            if (!hitWall) {
                for (int index = 0; index < finders.size(); index++) {
                    //Each pathfinder in the list is updated with the current list
                    PathFinder finder = finders.get(index);
                    finder.setFinders(finders);
                }
            }

            return false;
        }

        public ArrayList<PathFinder> getFinders() {
            return finders;
        }

        public int[][] getPath() {
            return path;
        }

        public char[][] getCharGrid() {
            return charGrid;
        }

        public void setFinders(ArrayList<PathFinder> finders) {
            this.finders = finders;
        }
    }

    public void solveMaze() {
        /*
        private maze runner class
         */
        PathFinder pathFinder = new PathFinder(new int[][]{maze.getStart()}, maze.getEnd(), maze.getPlaceholderGrid());
        ArrayList<PathFinder> finders = pathFinder.getFinders();
        boolean pathFound = false;
        while (!finders.isEmpty()) {
            for (int finderIndex = 0; finderIndex < finders.size(); finderIndex++) {
                PathFinder finder = finders.get(finderIndex);
                pathFound = finder.move();
                if (pathFound) {
                    maze.setPath(finder.getPath());
//                    for (char[] line: finder.getCharGrid()) {
//                        System.out.println(Arrays.toString(line));
//                    }
                    //Check final maze to see output

                    break;
                }
            }
            if (pathFound) {
                break;
            }
            //Before it becomes obsolete, the last PathFinder has access to the newest list of PathFinders
            finders = finders.get(finders.size() - 1).getFinders();
        }
    }

    public void writeSolution(String filename) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            bw.write(maze.pathString());
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
