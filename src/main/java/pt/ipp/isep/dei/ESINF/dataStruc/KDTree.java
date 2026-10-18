package pt.ipp.isep.dei.ESINF.dataStruc;

import java.util.*;

/**
 * A K-D Tree implementation that extends a Binary Search Tree (BST) to organize
 * points in a two-dimensional space (latitude and longitude).
 * Alternates comparison between X (latitude) and Y (longitude) coordinates
 * depending on the depth level of the tree (even depths compare latitude,
 * odd depths compare longitude).
 *
 * @param <E> the type of elements stored in the tree, must extend Point or any class
 *            that implements a getCoordinate() method returning a Coordinate object
 */
public class KDTree<E extends Comparable<E>> extends BST<E> implements KDTreeInterface<E> {
    /**
     * Default constructor. Creates an empty KD-Tree.
     */
    public KDTree() {
        super();
    }

    /**
     * Comparator used at even depths (0, 2, 4, ...) to compare points by latitude (X coordinate)
     */
    private final Comparator<KDNode<E>> cmpX = new Comparator<KDNode<E>>() {
        @Override
        public int compare(KDNode<E> p1, KDNode<E> p2) {
            return Double.compare(p1.getCoordinate().getLatitude(), p2.getCoordinate().getLatitude());
        }
    };

    /**
     * Comparator used at odd depths (1, 3, 5, ...) to compare points by longitude (Y coordinate).
     */
    private final Comparator<KDNode<E>> cmpY = new Comparator<KDNode<E>>() {
        @Override
        public int compare(KDNode<E> p1, KDNode<E> p2) {
            return Double.compare(p1.getCoordinate().getLongitude(), p2.getCoordinate().getLongitude());
        }
    };

    /**
     * Returns the root node cast as a KDNode.
     * Protected to allow subclasses to access the root safely.
     *
     * @return the root node of the tree as KDNode, or null if the tree is empty
     */
    protected KDNode<E> kdRoot() {
        return (KDNode<E>) root;
    }

    /**
     * Public accessor for the root node of the KD-Tree.
     *
     * @return the root node, or null if the tree is empty
     */
    public KDNode<E> getRootNode() {
        return kdRoot();
    }

    /**
     * Checks if the KD-Tree is empty.
     *
     * @return true if the tree contains no elements, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Returns the number of elements (points) stored in the KD-Tree.
     *
     * @return the total number of elements in the tree
     */
    @Override
    public int size() {
        return size(root);
    }

    /**
     * Recursive helper method to calculate the size of the subtree rooted at the given node.
     *
     * @param node the root of the subtree
     * @return the number of nodes in the subtree
     */
    private int size(Node<E> node) {
        if (node == null) {
            return 0;
        }
        return 1 + size(node.getLeft()) + size(node.getRight());
    }

    /**
     * Returns the height of the KD-Tree.
     * The height of an empty tree is -1, a tree with only the root has height 0.
     *
     * @return the height of the tree
     */
    @Override
    public int height() {
        return height(root);
    }

    /**
     * Recursive helper to compute the height of a subtree.
     *
     * @param node the root of the subtree
     * @return the height of the subtree (-1 for null)
     */
    protected int height(Node<E> node) {
        if (node == null) {
            return -1;
        }
        int lHeight = height(node.getLeft());
        int rHeight = height(node.getRight());
        return Math.max(lHeight, rHeight) + 1;
    }

    /**
     * Returns a map where each key is a bucket size (number of points stored in a leaf)
     * and the value is the count of leaves with that exact size.
     *
     * @return a Map<Integer, Integer> with bucket size frequencies
     */
    @Override
    public Map<Integer, Integer> getDistinctBucketSizes() {
        Map<Integer, Integer> bucketSizes = new HashMap<>();
        collectBucketSizes(kdRoot(), bucketSizes);
        return bucketSizes;
    }

    /**
     * Traverses the tree and populates the map with the frequency of each bucket size.
     *
     * @param node the current node being visited
     * @param bucketSizes the map being filled with frequencies
     */
    private void collectBucketSizes(KDNode<E> node, Map<Integer, Integer> bucketSizes) {
        if (node == null) {
            return;
        }

        int size = node.getInfo().size();
        bucketSizes.put(size, bucketSizes.getOrDefault(size, 0) + 1);

        collectBucketSizes(node.getLeft(), bucketSizes);
        collectBucketSizes(node.getRight(), bucketSizes);
    }
}
