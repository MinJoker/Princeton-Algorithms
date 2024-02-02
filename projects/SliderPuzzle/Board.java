/* ********************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               02/02/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 * 
 ******************************************************************************************************* */

import java.util.ArrayList;
import java.util.List;
import edu.princeton.cs.algs4.StdOut;

/**
 * The {@code Board} class represents an n-by-n board with sliding tiles.
 * 
 * note that this is an immutable data type.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 */

public class Board {
    private final int n;            // dimension of the board
    private final int[][] tiles;    // tiles of the board
    private int hamming;            // number of tiles out of place
    private int manhattan;          // sum of Manhattan distances between tiles and goal
    private int blankRow;           // row of the blank tile
    private int blankCol;           // column of the blank tile
    
    /**
     * create a board from an n-by-n array of tiles,
     * where tiles[row][col] = tile at (row, col)
     * 
     * @param tiles the n-by-n array of tiles
     * @throws IllegalArgumentException if tiles is null
     */
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException("tiles is null");
        n = tiles.length;
        this.tiles = new int[n][n];
        hamming = 0;
        manhattan = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];     // defensive copy
                if (tiles[i][j] != 0 && outOfPlace(i, j)) {
                    hamming++;
                    manhattan += abs((tiles[i][j] - 1) / n - i) + abs((tiles[i][j] - 1) % n - j);
                } else if (tiles[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
    }

    /**
     * string representation of this board
     * 
     * @return the string representation of this board
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n + "\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    /**
     * board dimension n
     * 
     * @return the dimension of the board
     */
    public int dimension() {
        return n;
    }

    /**
     * number of tiles out of place
     * 
     * @return the number of tiles out of place
     */
    public int hamming() {
        return hamming;
    }

    /**
     * sum of Manhattan distances between tiles and goal
     * 
     * @return the sum of Manhattan distances between tiles and goal
     */
    public int manhattan() {
        return manhattan;
    }

    /**
     * is this board the goal board?
     * 
     * @return true if this board is the goal board, false otherwise
     */
    public boolean isGoal() {
        return hamming == 0;
    }

    /**
     * does this board equal y?
     * 
     * note that this is a standard equals design pattern.
     * 
     * @param y the other board
     * @return true if this board equals y, false otherwise
     */
    public boolean equals(Object y) {
        // optimization for reference equality
        if (y == this) return true;
        // check against null
        // check that two objects are of the same type and cast
        if (y == null || y.getClass() != this.getClass()) return false;
        Board that = (Board) y;
        // compare each significant field
        if (n != that.n) return false;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != that.tiles[i][j]) return false;
            }
        }
        return true;
    }

    /**
     * all neighboring boards
     * 
     * @return all neighboring boards
     */
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<>();
        if (blankRow > 0) { // move the blank tile up
            int[][] neighborTiles = copy();
            neighborTiles[blankRow][blankCol] = neighborTiles[blankRow - 1][blankCol];
            neighborTiles[blankRow - 1][blankCol] = 0;
            Board neighbor = new Board(neighborTiles);
            neighbors.add(neighbor);
        }
        if (blankRow < n - 1) { // move the blank tile down
            int[][] neighborTiles = copy();
            neighborTiles[blankRow][blankCol] = neighborTiles[blankRow + 1][blankCol];
            neighborTiles[blankRow + 1][blankCol] = 0;
            Board neighbor = new Board(neighborTiles);
            neighbors.add(neighbor);
        }
        if (blankCol > 0) {     // move the blank tile left
            int[][] neighborTiles = copy();
            neighborTiles[blankRow][blankCol] = neighborTiles[blankRow][blankCol - 1];
            neighborTiles[blankRow][blankCol - 1] = 0;
            Board neighbor = new Board(neighborTiles);
            neighbors.add(neighbor);
        }
        if (blankCol < n - 1) { // move the blank tile right
            int[][] neighborTiles = copy();
            neighborTiles[blankRow][blankCol] = neighborTiles[blankRow][blankCol + 1];
            neighborTiles[blankRow][blankCol + 1] = 0;
            Board neighbor = new Board(neighborTiles);
            neighbors.add(neighbor);
        }
        return neighbors;
    }

    /**
     * a board that is obtained by exchanging any pair of tiles
     * 
     * note that this method is used for detecting unsolvable boards in the A* algorithm.
     * choose the first two tiles and swap them if blank tile is not in the first row.
     * otherwise, choose first two tiles in the second row and swap them.
     * 
     * @return a board that is obtained by exchanging any pair of tiles
     */
    public Board twin() {
        int[][] twinTiles = copy();
        // n is guaranteed to be at least 2
        if (blankRow != 0) {
            int temp = twinTiles[0][0];
            twinTiles[0][0] = twinTiles[0][1];
            twinTiles[0][1] = temp;
        } else {
            int temp = twinTiles[1][0];
            twinTiles[1][0] = twinTiles[1][1];
            twinTiles[1][1] = temp;
        }
        return new Board(twinTiles);
    }

    // is this tile out of place?
    private boolean outOfPlace(int i, int j) {
        return tiles[i][j] != i * n + j + 1;
    }

    // return the absolute value of x
    private int abs(int x) {
        return x < 0 ? -x : x;
    }

    // return a copy of the tiles
    private int[][] copy() {
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                copy[i][j] = tiles[i][j];
            }
        }
        return copy;
    }

    /**
     * unit testing (not graded)
     * 
     * print the board, dimension, hamming, manhattan, isGoal, twin, and neighbors.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int[][] tiles = {{1, 0, 3}, {4, 2, 5}, {7, 8, 6}};
        Board board = new Board(tiles);
        StdOut.println(board);
        StdOut.println("dimension: " + board.dimension());
        StdOut.println("hamming: " + board.hamming());
        StdOut.println("manhattan: " + board.manhattan());
        StdOut.println("isGoal: " + board.isGoal());
        StdOut.println("twin:");
        StdOut.println(board.twin());
        StdOut.println("neighbors:");
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
        }
    }
}