package pt.ipp.isep.dei.ESINF.dataStruc;

import pt.ipp.isep.dei.ESINF.domain.GeoCoordinate;

import java.util.*;

/**
 * Node used in a K-D Tree. Each node represents a splitting point in 2D space
 * (defined by a {@link GeoCoordinate}) and stores a list of elements (bucket) that
 * share the exact same coordinate.
 * <p>
 * The node alternates the splitting dimension (latitude/longitude) depending on
 * its depth in the tree. It extends {@link BST.Node} to reuse common binary-tree
 * functionality while adding spatial information.
 *
 * @param <E> the type of elements stored in the bucket (must be comparable)
 */
public class KDNode<E> extends BST.Node<E>{
    /** Geographic coordinate (latitude, longitude) that defines the splitting point */
    private GeoCoordinate coordinate;
    /** List of elements that lie exactly at this coordinate (bucket/leaf data) */
    private List<E> info;

    /**
     * Constructs a new KDNode.
     *
     * @param coordinate geographic coordinate that defines this node's position
     * @param info       list of elements associated with this coordinate (can be empty)
     * @param left       left child node (points with smaller value in current dimension)
     * @param right      right child node (points with greater or equal value in current dimension)
     */
    public KDNode(GeoCoordinate coordinate, List<E> info, KDNode<E> left, KDNode<E> right) {
        super(null, left, right);
        this.coordinate = coordinate;
        this.info = info;
    }

    /**
     * Returns the coordinate that defines this node's position in 2D space.
     *
     * @return the coordinate (never null)
     */
    public GeoCoordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Returns the list of elements stored at this exact coordinate (the bucket).
     * The returned list is mutable – modifications affect the tree.
     *
     * @return list of elements associated with this node
     */
    public List<E> getInfo() {
        return info;
    }

    /**
     * Replaces the list of elements stored at this node.
     *
     * @param info new list of elements (must not be null)
     */
    public void setInfo(List<E> info) {
        this.info = info;
    }

    /**
     * Returns the left child, safely cast to {@code KDNode<E>}.
     *
     * @return left child or {@code null} if there is no left child
     */
    @Override
    public KDNode<E> getLeft() {
        return (KDNode<E>) super.getLeft();
    }

    /**
     * Returns the right child, safely cast to {@code KDNode<E>}.
     *
     * @return right child or {@code null} if there is no right child
     */
    @Override
    public KDNode<E> getRight() {
        return (KDNode<E>) super.getRight();
    }
}
