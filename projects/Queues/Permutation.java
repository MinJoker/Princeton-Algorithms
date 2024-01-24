/* ***********************************************************************************************************
 *
 *  Name:               MinJoker
 *  Date:               23/01/2024
 *  Grade:              100/100
 *  Libraries:          algs4.jar
 *  Project source:     https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
 * 
 ********************************************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * the {@code Permutation} class is a client program for testing {@code RandomizedQueue} class.
 * 
 * bonus: use only one RandomizedQueue object of maximum size at most {@code k}
 * solution: powered by Knuth shuffle, the algorithm is as follows:
 * read the strings one by one from the standard input, and enqueue the first {@code k} items, then
 * for each item, accept it with probability {@code k / (cnt + 1)}, where{@code cnt} is the number of items read so far.
 * if accepted, dequeue a random item and enqueue the new item.
 * note that the probability of each item remaining in the queue is exactly {@code k / n}.
 * 
 * more details can be found in the project source:
 * https://coursera.cs.princeton.edu/algs4/assignments/queues/specification.php
 */

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);  // number of items to print
        RandomizedQueue<String> q = new RandomizedQueue<String>();
        int cnt = 0;  // number of items read so far

        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            if (q.size() < k) {
                // fill the queue with the first k items
                q.enqueue(s);
            } else if (StdRandom.uniformInt(cnt + 1) < k) {
                // accept the new item with probability k / (cnt + 1)
                q.dequeue();
                q.enqueue(s);
            }
            cnt++;
        }
        for (int i = 0; i < k; i++) {   // print the k items
            StdOut.println(q.dequeue());
        }
    }
}
