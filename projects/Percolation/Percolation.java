/* ***********************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               22/01/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php
 * 
 ********************************************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * the {@code Percolation} class represents a percolation system.
 * 
 * some key points as follows:
 * 
 * 1. there are two union-find data structures {@code uf} and {@code ufFull}.
 *    {@code uf} is used to determine whether the system percolates, and
 *    {@code ufFull} is used to determine whether a site is full.
 * 2. for {@code uf}, there are two virtual sites ( top and bottom ).
 *    the top virtual site is initialiezd connected to all sites in the top row, and
 *    the bottom virtual site is initialized connected to all sites in the bottom row.
 *    if the top virtual site and the bottom virtual site are connected,
 *    the system percolates.
 *    note that {@code isFull()} will fail if we use {@code uf} to determine.
 *    for example: ( 1 is open site, 0 is blocked site )
 *    | 0 0 1 0 |
 *    | 0 0 1 0 |
 *    | 0 0 1 0 |
 *    | 1 0 1 0 |
 *    the site ( 4, 1 ) should be not full, but it is in the same component with
 *    the other four sites, the top virtual site and the bottom virtual site.
 *    what's more, this is called backwash, more details can be found in the point 5.
 * 3. for {@code ufFull}, there is only one virtual site ( top ).
 *    the top virtual site is connected to every open site in the top row.
 *    for the same example above,
 *    the other four sites are in the same component with the top virtual site, while
 *    the site ( 4, 1 ) is not. so it is not full, as expected.
 * 4. virtual sites are clever tricks
 *    to check whether the system percolates or a site is full.
 *    otherwise, we have to check every site in the top row and the bottom row,
 *    to see if there is a path between them,
 *    which is very time-consuming.
 * 5. copied from cousera discussion forum:
 *     "Backwash is a like a false positive for isFull method
 *      and arises after a system percolates.
 *      Here a site is not connected to top, but isFull method returns true
 *      because its connected to any site bottom site and bottom in turn is connected to top
 *      via (using virtual bottom) some other path.
 *      Fact is water flows down only from top to bottom, but
 *      here a short circuit of connectivity happens.
 *      It comes as a result of using both virtual top and virtual bottom.
 *      Another way is to use 2 copies of UF data structure to keep calculations separate.
 *      More advanced way will be
 *      to maintain additional state with each site (using single UF)
 *      on whether its connected to bottom.
 *      This is quite tricky to figure out."
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/percolation/specification.php
 */

public class Percolation {
    
    private int size;                       // size of grid
    private boolean[][] grid;               // grid of sites
    private WeightedQuickUnionUF uf;        // union-find data structure
    private WeightedQuickUnionUF ufFull;    // union-find data structure for isFull()
    private int top;                        // virtual top site
    private int bottom;                     // virtual bottom site
    private int openSites;                  // number of open sites

    /**
     * creates n-by-n grid, with all sites initially blocked
     * 
     * @param n size of grid
     * @throws IllegalArgumentException if {@code n <= 0}
     */
    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("size of grid must be greater than 0");
        size = n;
        grid = new boolean[n][n];
        uf = new WeightedQuickUnionUF(n * n + 2);       // 2 virtual sites ( top and bottom )
        ufFull = new WeightedQuickUnionUF(n * n + 1);   // 1 virtual site ( top )
        top = n * n;
        bottom = n * n + 1;
        openSites = 0;

        // initialize all sites to be blocked
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                grid[i][j] = false;
        }

        // connect virtual top and bottom to top row and bottom row, respectively, only for uf
        for (int i = 0; i < n; i++)
            uf.union(top, i);
        for (int i = n * (n - 1); i < n * n; i++)
            uf.union(bottom, i);
    }

    /**
     * open site (row, col) if it is not open already
     * 
     * @param row the row of the site
     * @param col the column of the site
     * @throws IllegalArgumentException unless
     *         both {@code 1 <= row <= n} and {@code 1 <= col <= n}
     */
    public void open(int row, int col) {
        validate(row, col);

        // if already open, do nothing
        if (isOpen(row, col))
            return;

        // if not open, open it
        grid[row - 1][col - 1] = true;
        openSites++;

        // connect to open neighbors
        if (row > 1 && isOpen(row - 1, col)) {      // left neighbor
            uf.union((row - 1) * size + col - 1, (row - 2) * size + col - 1);
            ufFull.union((row - 1) * size + col - 1, (row - 2) * size + col - 1);
        }
        if (row < size && isOpen(row + 1, col)) {   // right neighbor
            uf.union((row - 1) * size + col - 1, row * size + col - 1);
            ufFull.union((row - 1) * size + col - 1, row * size + col - 1);
        }
        if (col > 1 && isOpen(row, col - 1)) {      // top neighbor
            uf.union((row - 1) * size + col - 1, (row - 1) * size + col - 2);
            ufFull.union((row - 1) * size + col - 1, (row - 1) * size + col - 2);
        }
        if (col < size && isOpen(row, col + 1)) {   // bottom neighbor
            uf.union((row - 1) * size + col - 1, (row - 1) * size + col);
            ufFull.union((row - 1) * size + col - 1, (row - 1) * size + col);
        }

        // if on top row, connect to virtual top, only for ufFull
        if (row == 1)
            ufFull.union((row - 1) * size + col - 1, top);

        // if percolates, connect top and bottom
        if (percolates())
            uf.union(top, bottom);

        return;
    }

    /**
     * is the site (row, col) open?
     * 
     * @param row the row of the site
     * @param col the column of the site
     * @return {@code true} if the site is open;
     *         {@code false} otherwise
     */
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return grid[row - 1][col - 1];
    }

    /**
     * if the site (row, col) is full?
     * 
     * a full site is an open site that
     * can be connected to an open site in the top row.
     * 
     * @param row the row of the site
     * @param col the column of the site
     * @return {@code true} if the site is full;
     *         {@code false} otherwise
     */
    public boolean isFull(int row, int col) {
        validate(row, col);
        return isOpen(row, col) && ufFull.find((row - 1) * size + col - 1) == ufFull.find(top);
    }

    /**
     * returns the number of open sites
     * 
     * @return the number of open sites
     */
    public int numberOfOpenSites() {
        return openSites;
    }

    /**
     * does the system percolate?
     * 
     * a system percolates if
     * {@code top} and {@code bottom} are connected, which means
     * there is a path from some site in the top row to some site in the bottom row.
     * 
     * @return {@code true} if the system percolates;
     *         {@code false} otherwise
     */
    public boolean percolates() {
        if (size == 1) return isOpen(1, 1); // corner case
        return uf.find(top) == uf.find(bottom);
    }

    // validate that row and col are valid indices
    private void validate(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new IllegalArgumentException("row and col must be between 1 and " + size);
    }

    /**
     * test client (optional)
     * 
     * reads an integer {@code size} and a sequence of pairs of integers
     * (between {@code 1} and {@code n}) from standard input, where
     * each integer in the pair represents some site.
     * each site will be opened (if not already)
     * and tested to see if the system percolates.
     * the program will not terminate until the system percolates.
     * 
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int size = StdIn.readInt();
        Percolation percolation = new Percolation(size);
        while (!StdIn.isEmpty()) {
            int row = StdIn.readInt();
            int col = StdIn.readInt();
            percolation.open(row, col);
            if (percolation.percolates()) {
                StdOut.println("percolates");
                break;
            }
        }
        StdOut.println(percolation.numberOfOpenSites() + " open sites");
    }
}
