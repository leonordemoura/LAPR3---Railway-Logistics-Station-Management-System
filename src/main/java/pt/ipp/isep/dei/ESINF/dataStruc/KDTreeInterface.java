package pt.ipp.isep.dei.ESINF.dataStruc;

import java.util.Map;

/**
 * Interface defining the basic operations and queries supported by a K-D Tree
 * data structure that stores spatial points (typically with latitude and longitude).
 * <p>
 * The tree organizes points in 2D space, alternating the comparison dimension
 * (X = latitude, Y = longitude) at each level of depth.
 * Multiple elements sharing the exact same coordinate are stored together in
 * "buckets" at leaf/internal nodes.
 *
 * @param <E> the type of elements stored in the tree (usually objects containing
 *            or associated with a geographic coordinate)
 */
public interface KDTreeInterface<E> {

    /**
     * Returns the total number of elements (points) stored in the K-D Tree.
     *
     * @return the number of elements in the tree
     */
    int size();

    /**
     * Returns the height of the tree.
     * <ul>
     *   <li>Height of an empty tree is -1</li>
     *   <li>Height of a tree with only the root node is 0</li>
     * </ul>
     *
     * @return the height of the tree
     */
    int height();

    /**
     * Checks whether the K-D Tree contains any elements.
     *
     * @return {@code true} if the tree is empty, {@code false} otherwise
     */
    boolean isEmpty();

    /**
     * Analyzes the distribution of bucket sizes across all nodes.
     * A bucket is the list of elements that share the exact same coordinate in a node.
     * <p>
     * Returns a map where:
     * <ul>
     *   <li>Key   - bucket size (number of elements stored in a node)</li>
     *   <li>Value - how many nodes have a bucket of that exact size</li>
     * </ul>
     * <p>
     * Example: {@code {1=450, 2=80, 5=3}} means there are 450 nodes with 1 element,
     * 80 nodes with 2 elements, and 3 nodes with 5 elements.
     *
     * @return a map with the frequency of each distinct bucket size
     */
    Map<Integer, Integer> getDistinctBucketSizes();
}
