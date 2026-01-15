/**
 * A Maze object.
 *
 * Purdue University -- CS18000 -- Fall 2024 -- Challenge
 *
 * @author Bobby Kinsella
 * @version December 2, 2024
 */

public class Maze {

    private final int[] end;
    private final char[][] grid;
    private final String name;
    private int[][] path;
    private final int[] start;

    public Maze(String name, char[][] grid, int[] start, int[] end) {
        this.end = end;
        this.grid = grid;
        this.start = start;
        this.name = name;
    }

    public char[][] getPlaceholderGrid() {
        char[][] holder = new char[grid.length][grid[0].length];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                holder[r][c] = grid[r][c];
            }
        }
        return holder;
    }

    public char[][] getGrid() {
        return grid;
    }

    public int[] getEnd() {
        return end;
    }

    public int[] getStart() {
        return start;
    }

    public String getName() {
        return name;
    }

    public int[][] getPath() {
        return path;
    }

    public void setPath(int[][] path) {
        this.path = path;
    }

    public String pathString() {
        int numMoves = path.length;
        String moves = "";
        for (int move = 0; move < numMoves; move++) {
            moves = String.format("%s\n%d-%d", moves, path[move][0], path[move][1]);
        }
        return String.format("%s\nMoves: %d\nStart%s\nEnd", name, numMoves, moves);
    }

    @Override
    public String toString() {
        String mazeAsString = "";
        for (int row = 0; row < grid.length; row++) {
            String rowAsString = Character.toString(grid[row][0]);
            for (int col = 1; col < grid[0].length; col++) {
                rowAsString = String.format("%s,%c", rowAsString, grid[row][col]);
            }
            mazeAsString = String.format("%s\n%s", mazeAsString, rowAsString);
        }
        return String.format("%s\nStart: %d-%d\nEnd: %d-%d%s", name, start[0], start[1], end[0], end[1], mazeAsString);
    }
}
