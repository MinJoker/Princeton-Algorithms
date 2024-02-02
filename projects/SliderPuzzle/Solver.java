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
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

/**
 * The {@code Solver} class represents a solution to the 8-puzzle problem.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/8puzzle/specification.php
 */

public class Solver {
    private List<Board> solution;   // solution to the puzzle
    private boolean solvable;       // is the puzzle solvable?
    private int moves;              // number of moves to solve the puzzle

    // the helper search node class
    private class SearchNode implements Comparable<SearchNode> {
        private Board board;            // the board
        private int moves;              // number of moves to reach this board
        private SearchNode previous;    // previous search node
        private int priority;           // the priority of the search node

        public SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
            priority = board.manhattan() + moves;
        }

        // compare this search node to that search node
        // based on the manhattan priority function
        public int compareTo(SearchNode that) {
            // optimized by caching the priority
            int thisPriority = priority;
            int thatPriority = that.priority;
            if (thisPriority < thatPriority) {
                return -1;
            } else if (thisPriority > thatPriority) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * find a solution to the initial board (using the A* algorithm)
     * 
     * @param initial the initial board
     * @throws IllegalArgumentException if initial is null
     */
    public Solver(Board initial) {

        if (initial == null) throw new IllegalArgumentException("initial is null");
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();     // the priority queue for the initial board
        MinPQ<SearchNode> pqTwin = new MinPQ<SearchNode>(); // the priority queue for the twin board
        
        // insert the initial search node into the priority queue
        pq.insert(new SearchNode(initial, 0, null));
        pqTwin.insert(new SearchNode(initial.twin(), 0, null));

        // delete the search node with the minimum priority from the priority queue
        // and insert onto the priority queue all neighboring search nodes
        // repeat this procedure until the search node dequeued corresponds to a goal board
        SearchNode node = pq.delMin();
        SearchNode nodeTwin = pqTwin.delMin();
        while (!node.board.isGoal() && !nodeTwin.board.isGoal()) {
            // optimized by not enqueuing the same board twice with the help of the previous search node
            for (Board neighbor : node.board.neighbors()) {
                if (node.previous == null || !neighbor.equals(node.previous.board)) {
                    pq.insert(new SearchNode(neighbor, node.moves + 1, node));
                }
            }
            for (Board neighbor : nodeTwin.board.neighbors()) {
                if (nodeTwin.previous == null || !neighbor.equals(nodeTwin.previous.board)) {
                    pqTwin.insert(new SearchNode(neighbor, nodeTwin.moves + 1, nodeTwin));
                }
            }
            node = pq.delMin();
            nodeTwin = pqTwin.delMin();
        }
        
        // check if the initial board is solvable
        if (node.board.isGoal()) {
            // the initial board is solvable
            solvable = true;
            moves = node.moves;
            solution = new ArrayList<Board>();
            // reconstruct the sequence of boards in a shortest solution
            // by following the link from each search node to its predecessor
            while (node != null) {
                solution.add(0, node.board);
                node = node.previous;
            }
        } else {
            // the initial board is unsolvable
            solvable = false;
            moves = -1;
            solution = null;
        }
    }

    /**
     * is the initial board solvable?
     * 
     * @return true if the initial board is solvable, false otherwise
     */
    public boolean isSolvable() {
        return solvable;
    }

    /**
     * min number of moves to solve initial board; -1 if unsolvable
     * 
     * @return min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves() {
        return moves;
    }

    /**
     * sequence of boards in a shortest solution; null if unsolvable
     * 
     * @return sequence of boards in a shortest solution; null if unsolvable
     */
    public Iterable<Board> solution() {
        return solution;
    }

    /**
     * test client
     * 
     * read the puzzle instance from a file and
     * print the solution to standard output
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
